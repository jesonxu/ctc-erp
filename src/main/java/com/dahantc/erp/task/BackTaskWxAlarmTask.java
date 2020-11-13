package com.dahantc.erp.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.FlowTaskManager;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.dto.flow.UserFlowInfoDto;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 企业微信告警任务
 * 
 * @author 8501
 *
 */
@Component
public class BackTaskWxAlarmTask implements SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(BackTaskWxAlarmTask.class);

	private static String CRON = "0 0 09,14,17 ? * *";

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private IDepartmentService departmentService;

	@Resource
	private IBaseDao baseDao;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private Environment ev;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTask-WxAlarm-Task is running...");
				try {
					initwxParam();
					doFlowAlarm();
					syncWxUserAndOrg();
				} catch (Exception e) {
					logger.error("BackTask-WxAlarm-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-WxAlarm-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	public void initwxParam() {
		WeixinMessage.initwxParam(ev);
	}

	/**
	 * 待处理流程告警
	 */
	public void doFlowAlarm() {
		try {
			List<RoleRelation> roles = roleRelationService.queryAllBySearchFilter(new SearchFilter());
			Map<String, User> allUser = getAllUserListFromDB();
			for (User user : allUser.values()) {
				List<UserAlarmInfo> userAlarmInfos = getAlarmInfo(roles, user);
				if (!userAlarmInfos.isEmpty()) {
					doWxAlarm(user, userAlarmInfos);
				}
			}
		} catch (Exception e) {
			logger.error("用户微信预警异常", e);
		}
	}

	/**
	 * 同步企业微信通讯录信息及组织机构信息
	 */
	public void syncWxUserAndOrg() {
		syncWxOrganization();
		syncWxUser();
	}

	/**
	 * 获取用户在当前角色下当前等待处理的流程数量
	 * 
	 * @param user
	 *            用户
	 * @param roles
	 *            所有角色
	 * @return 等待预警信息
	 */
	private List<UserAlarmInfo> getAlarmInfo(List<RoleRelation> roles, User user) {
		List<UserAlarmInfo> alarmInfos = new ArrayList<>();
		if (roles != null && !roles.isEmpty()) {
			List<RoleRelation> uRoles = roles.stream().filter(roleRelation -> roleRelation.getOssUserId().equals(user.getOssUserId())).collect(Collectors.toList());
			if (uRoles.isEmpty()) {
				return alarmInfos;
			}
			SearchFilter roleFilter = new SearchFilter();
			List<String> roleIds = uRoles.stream().map(RoleRelation::getRoleId).collect(Collectors.toList());
			roleFilter.getRules().add(new SearchRule("roleid", Constants.ROP_IN, roleIds));
			try {
				List<Role> roleList = roleService.queryAllBySearchFilter(roleFilter);
				if (roleList != null && !roleList.isEmpty()) {
					for (Role role : roleList) {
						List<UserFlowInfoDto> userFlowInfos = flowEntService.queryUserFlowInfo(user, role.getRoleid());
						if (userFlowInfos != null) {
							Map<String, List<UserFlowInfoDto>> flowInfo = userFlowInfos.stream()
									.collect(Collectors.groupingBy(UserFlowInfoDto::getFlowClass));
							for (String flowClass : flowInfo.keySet()) {
								BaseFlowTask baseFlowTask = flowTaskManager.getFlowTasks(flowClass);
								String taskName = "普通流程";
								if (baseFlowTask != null) {
									taskName = baseFlowTask.getFlowName();
								}
								UserAlarmInfo userAlarmInfo = new UserAlarmInfo();
								userAlarmInfo.setRoleName(role.getRolename());
								userAlarmInfo.setItemName(taskName);
								userAlarmInfo.setUnDisposedCount(flowInfo.get(flowClass).size());
								alarmInfos.add(userAlarmInfo);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("未处理流程数据查询异常", e);
			}
		}
		return alarmInfos;
	}

	/**
	 * 同步企业微信通讯录信息
	 */
	private void syncWxUser() {
		try {
			Map<String, JSONObject> mobile_userInfo = WeixinMessage.getUserList();
			List<User> userArray = new ArrayList<User>();
			Map<String, User> allUserFromDb = getAllUserListFromDB();
			if (null != mobile_userInfo && !mobile_userInfo.isEmpty()) {
				for (JSONObject userInfoJsonObject : mobile_userInfo.values()) {
					String userid = userInfoJsonObject.getString("userid");
					String name = userInfoJsonObject.getString("name");
					String mobile = userInfoJsonObject.getString("mobile");
					String email = userInfoJsonObject.getString("email");
					String isleader = userInfoJsonObject.getString("isleader");
					String main_department = userInfoJsonObject.getString("main_department");
					
//					JSONArray departmentJsonArray = userInfoJsonObject.getJSONArray("department");
					User user = allUserFromDb.remove(userid);
					if (StringUtils.isNotBlank(main_department)) {
						if (null == user) {
							user = new User();
							user.setOssUserId(userid);
						}
						// 部门发生变化
						user.setRealName(name);
						user.setLoginName(mobile);
						user.setContactMobile(mobile);
						user.setContacteMail(email);
						user.setStatus(1);
						user.setUstate(1);
						try {
							user.setIdentityType(NumberUtils.toInt(isleader));
						} catch (Exception e) {
							// TODO: handle exception
						}
						user.setDeptId(main_department);
						userArray.add(user);
					}
				}
				// 新增or修改
				if (!userArray.isEmpty()) {
					baseDao.saveOrUpdateByBatch(userArray);
					logger.info("本次同步账户,新增or编辑数量:" + userArray.size());
					for (User user : userArray) {
						// TODO 保存客户历史
						String updateCustomer = "update erp_customer set deptid='" + user.getDeptId() + "' where ossuserid='" + user.getOssUserId() + "'";
						baseDao.executeUpdateSQL(updateCustomer);
						logger.info("本次同步账户,更新" + user.getOssUserId() + "的客户的所在部门为其当前部门");
					}
				}
				// 非测试环境，禁用未从企业微信同步的账户
				if (!allUserFromDb.isEmpty() && !StringUtils.equals("true", WeixinMessage.getWeixinParam().getTest())) {
					for (User user : allUserFromDb.values()) {
						if ("admin".equals(user.getLoginName())) {
							continue;
						}
						if (user.getUstate() != 0 || user.getStatus() != 0) {
							user.setUstate(0);
							user.setStatus(0);
							baseDao.update(user);
							logger.info("本次同步账户,禁用:" + user.getOssUserId() + "-" + user.getLoginName());
						}
					}
				}
			} else {
				logger.error("get getUserList empty");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
		}
	}

	/**
	 * 同步企业微信组织机构信息
	 */
	private void syncWxOrganization() {
		String departmentListStr = WeixinMessage.getDepartmentList("");
		if (StringUtils.isNotBlank(departmentListStr)) {
			try {
				JSONObject departmentListJson = JSONObject.parseObject(departmentListStr);
				JSONArray departmentJsonArray = departmentListJson.getJSONArray("department");
				List<Department> departmentArray = new ArrayList<Department>();
				Map<String, Department> allDepFromDb = getAllDepartmentListFromDB();
				for (Object object : departmentJsonArray) {
					String id = ((JSONObject) object).getString("id");
					Department department = allDepFromDb.remove(id);
					if (null == department) {
						department = new Department();
						department.setDeptid(id);
					}
					department.setDeptname(((JSONObject) object).getString("name"));
					department.setParentid(((JSONObject) object).getString("parentid"));
					department.setSequence(((JSONObject) object).getString("order"));
					department.setFlag(0);
					departmentArray.add(department);
				}
				// 新增
				if (!departmentArray.isEmpty()) {
					baseDao.saveOrUpdateByBatch(departmentArray);
					logger.info("本次同步部门,新增or编辑数量:" + departmentArray.size());
				}
				// 禁用
				if (!allDepFromDb.isEmpty()) {
					for (Department department : allDepFromDb.values()) {
						if (department.getFlag() != 1) {
							department.setFlag(1);
							baseDao.update(department);
							logger.info("本次同步部门,禁用:" + department.getDeptid() + "-" + department.getDeptname());
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.error("", e);
			}
		}
	}

	/**
	 * 查询库中所有部门信息
	 * 
	 * @return
	 */
	private Map<String, Department> getAllDepartmentListFromDB() {
		Map<String, Department> allDep = new HashMap<String, Department>();
		try {
			SearchFilter filter = new SearchFilter();
			// 查询当前用户自己的部门信息
			List<Department> departments = departmentService.queryAllBySearchFilter(filter);
			for (Department department : departments) {
				allDep.put(department.getDeptid(), department);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return allDep;
	}

	/**
	 * 查询库中所有用戶信息
	 * 
	 * @return
	 */
	private Map<String, User> getAllUserListFromDB() {
		Map<String, User> allUser = new HashMap<String, User>();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
			filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
			// 查询账号信息
			PageResult<User> users = userService.queryByPages(200000, 1, filter);
			for (User user : users.getData()) {
				allUser.put(user.getOssUserId(), user);
			}
		} catch (Exception e) {
			logger.error("用户数据查询异常", e);
		}
		return allUser;
	}

	/**
	 * 执行告警操作
	 *
	 * @param user           用户
	 * @param userAlarmInfos 用户预警信息
	 */
	private void doWxAlarm(User user, List<UserAlarmInfo> userAlarmInfos) {
		if (userAlarmInfos == null || userAlarmInfos.isEmpty() || "true".equals(WeixinMessage.getWeixinParam().getTest().toLowerCase())){
			logger.info("用户：" + user.getRealName() + " 的微信通知信息，无信息内容，取消通知");
			return;
		}
		int total = userAlarmInfos.stream().mapToInt(UserAlarmInfo::getUnDisposedCount).sum();
		StringBuilder content = new StringBuilder("温馨提醒：亲，你有"+total+"个流程未处理，加油！\r\n");
		Map<String,List<UserAlarmInfo>> roleAlarmInfo = userAlarmInfos.stream().collect(Collectors.groupingBy(UserAlarmInfo::getRoleName));
		roleAlarmInfo.keySet().forEach(roleName->{
			content.append("【").append(roleName).append("】：\r\n");
			List<UserAlarmInfo> roleInfo = roleAlarmInfo.get(roleName);
			roleInfo.forEach(info -> content.append(info.getItemName()).append("：").append(info.getUnDisposedCount()).append("个\r\n"));
			content.append("\r\n");
		});
		content.append("请<a href=\""+WeixinMessage.getWeixinParam().getRedirect_uri().replace("wxLogin.action","entry.action")+"\"> 登录ERP </a>查看详情");
		String sendRes = WeixinMessage.sendMessageByMobile(user.getContactMobile(), content.toString());
		if (StringUtils.isBlank(sendRes)) {
			WeixinMessage.sendMessage("", user.getOssUserId(), content.toString());
		}
	}

	/**
	 * 组装用户信息
	 * 
	 * @param roleRelation
	 * @param user
	 * @return
	 */
	private OnlineUser getOnlineUser(RoleRelation roleRelation, User user) {
		OnlineUser onlineUser = null;
		if (user != null && null != roleRelation && StringUtils.isNotBlank(roleRelation.getRoleId())) {
			onlineUser = new OnlineUser();
			onlineUser.setUser(user);
			onlineUser.setRoleId(roleRelation.getRoleId());
		}
		return onlineUser;
	}

	/**
	 * 用户的预警信息
	 */
	static class UserAlarmInfo{

		/**
		 * 角色名称
		 */
		private String roleName;
		/**
		 * 名称
		 */
		private String itemName;
		/**
		 * 未处理数量
		 */
		private Integer unDisposedCount = 0;

		public String getRoleName() {
			return roleName;
		}

		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public Integer getUnDisposedCount() {
			return unDisposedCount;
		}

		public void setUnDisposedCount(Integer unDisposedCount) {
			this.unDisposedCount = unDisposedCount;
		}

		public synchronized void addCount(int count) {
			unDisposedCount += count;
		}

		@Override
		public String toString() {
			return "UserAlarmInfo{" +
					"roleName='" + roleName + '\'' +
					", itemName='" + itemName + '\'' +
					", unDisposedCount=" + unDisposedCount +
					'}';
		}
	}
}