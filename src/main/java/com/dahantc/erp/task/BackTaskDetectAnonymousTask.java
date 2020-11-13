package com.dahantc.erp.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 检测异常账单、流程，并发送企业微信告警
 *
 * @author 8523
 *
 */
@Component
public class BackTaskDetectAnonymousTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskDetectAnonymousTask.class);

	private static String CRON = "0 55 8,16 ? * *";

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IMsgCenterService msgCenterService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private Environment ev;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTaskDetectAnonymousTask is running...");
				try {
					initwxParam();
					detectFlow();
				} catch (Exception e) {
					logger.error("BackTaskDetectAnonymousTask is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTaskDetectAnonymousTask：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	public void initwxParam() {
		WeixinMessage.initwxParam(ev);
	}

	private void detectFlow() {
		List<FlowEnt> flowEntList = flowEntService.queryTimeOutFlow();
		if (CollectionUtils.isEmpty(flowEntList)) {
			logger.info("无异常流程");
			return;
		}
		// 用户id -> 用户
		Map<String, User> userMap = userService.getUserMap(false);
		// 部门 -> 部门领导
		Map<String, List<User>> deptLeaderMap = userService.findAllDeptLeader(null);
		// 用户流程异常数
		Map<String, Integer> userAlarmFlowMap = new HashMap<>();
		// 流程节点id -> 节点
		Map<String, FlowNode> nodeMap = new HashMap<>();
		// 流程节点处理人
		List<String> dealUserIdList = null;
		List<String> dealUserNameList = null;
		for (FlowEnt flowEnt : flowEntList) {
			User user = userMap.get(flowEnt.getOssUserId());
			FlowNode node = null;
			String nodeId = flowEnt.getNodeId();
			String roleIds = null;
			if (nodeMap.containsKey(nodeId)) {
				node = nodeMap.get(nodeId);
				if (null != node) {
					roleIds = node.getRoleId();
				}
			} else {
				try {
					node = flowNodeService.read(nodeId);
					if (node != null) {
						roleIds = node.getRoleId();
					} else {
						logger.info("流程节点不存在，nodeId：" + nodeId);
					}
				} catch (ServiceException e) {
					logger.error("查询流程节点异常，nodeId：" + nodeId, e);
				}
				nodeMap.put(nodeId, node);
			}
			try {
				dealUserIdList = userService.getDealUserId(user, roleIds);
			} catch (Exception e) {
				logger.info("获取流程处理人异常");
			}
			StringBuilder msgDetail = new StringBuilder();
			dealUserNameList = new ArrayList<>();
			if (CollectionUtils.isEmpty(dealUserIdList)) {
				// 无处理人，通知发起人
				logger.info("节点无可处理人，flowEntId：" + flowEnt.getId() + "，nodeId：" + flowEnt.getNodeId());
				int count = userAlarmFlowMap.getOrDefault(flowEnt.getOssUserId(), 0);
				count++;
				userAlarmFlowMap.put(flowEnt.getOssUserId(), count);
				msgDetail.append(user.getRealName()).append(" 的流程：").append(flowEnt.getFlowTitle()).append(" 在节点：").append(null == node ? "未知节点" : node.getNodeName()).append(" 无可处理人");
			} else if (dealUserIdList.size() == 1) {
				// 单个处理人
				User dealUser = userMap.getOrDefault(dealUserIdList.get(0), null);
				Set<String> alarmUserIdSet = new HashSet<>(dealUserIdList);
				if (null != dealUser) {
					dealUserNameList.add(dealUser.getRealName());
					// 处理人所在部门、上级部门……
					List<String> superDeptIdList = departmentService.getSuperDeptIds(dealUser.getDeptId());
					superDeptIdList.add(dealUser.getDeptId());
					// 每个部门的领导
					for (String deptId : superDeptIdList) {
						List<User> leaders = deptLeaderMap.getOrDefault(deptId, new ArrayList<>());
						alarmUserIdSet.addAll(leaders.stream().map(User::getOssUserId).collect(Collectors.toList()));
					}
				}
				dealUserIdList = new ArrayList<>(alarmUserIdSet);
				// 这些人的异常流程数+1
				dealUserIdList.forEach(id -> {
					int count = userAlarmFlowMap.getOrDefault(id, 0);
					count++;
					userAlarmFlowMap.put(id, count);
				});
				msgDetail.append(user.getRealName()).append(" 的流程：").append(flowEnt.getFlowTitle()).append(" 在节点：").append(null == node ? "未知节点" : node.getNodeName()).append(" 处理超时，请联系以下可处理人：").append(String.join("，", dealUserNameList));
			} else {
				// 多个处理人，只通知处理人，这些人的异常流程数+1
				for (String id : dealUserIdList) {
					int count = userAlarmFlowMap.getOrDefault(id, 0);
					count++;
					userAlarmFlowMap.put(id, count);
					User dealUser = userMap.get(id);
					if (null != dealUser) {
						dealUserNameList.add(dealUser.getRealName());
					}
				}
				msgDetail.append(user.getRealName()).append(" 的流程：").append(flowEnt.getFlowTitle()).append(" 在节点：").append(null == node ? "未知节点" : node.getNodeName()).append(" 处理超时，请联系以下可处理人：").append(String.join("，", dealUserNameList));
			}
			// 保存消息
			msgCenterService.buildMessage(user, dealUserIdList, msgDetail.toString(), flowEnt.getId());
		}
		// 企业微信告警
		if (userAlarmFlowMap.size() > 0) {
			doWxAlarm(userMap, userAlarmFlowMap);
		}
	}

	/**
	 * 企业微信告警
	 *
	 * @param userMap
	 *            用户
	 * @param userAlarmFlowMap
	 *            用户异常流程数
	 */
	private void doWxAlarm(Map<String, User> userMap, Map<String, Integer> userAlarmFlowMap) {
		if ("true".equals(WeixinMessage.getWeixinParam().getTest().toLowerCase())) {
			logger.info("测试环境，取消通知");
			return;
		}
		if (userMap == null || userMap.isEmpty()) {
			logger.info("无用户，取消通知");
			return;
		}
		if (userAlarmFlowMap == null || userAlarmFlowMap.isEmpty()) {
			logger.info("无异常，取消通知");
			return;
		}
		for (Map.Entry<String, Integer> userAlarm : userAlarmFlowMap.entrySet()) {
			User user = userMap.get(userAlarm.getKey());
//			User user = userMap.get("luozhu");
			if (null == user) {
				logger.info("用户不存在或已禁用，ossUserId：" + userAlarm.getKey());
				continue;
			}
			int count = userAlarm.getValue();
			StringBuilder content = new StringBuilder("异常提醒：你有" + count + "个流程已超过处理期限，请尽快处理！\r\n");
			content.append("请<a href=\"").append(WeixinMessage.getWeixinParam().getRedirect_uri().replace("wxLogin.action", "entry.action")).append("\"> 登录ERP </a>在 “员工中心 - 我的消息” 查看详情");

			String sendRes = WeixinMessage.sendMessageByMobile(user.getContactMobile(), content.toString());
			if (StringUtils.isBlank(sendRes)) {
				WeixinMessage.sendMessage("", user.getOssUserId(), content.toString());
			}
		}
	}
}
