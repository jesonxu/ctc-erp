package com.dahantc.erp.controller.messageCenter;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.messageCenter.MsgCenterDto;
import com.dahantc.erp.dto.messageCenter.MsgCountRespDto;
import com.dahantc.erp.enums.MessageType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/messageCenter")
public class MessageCenterAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(MessageCenterAction.class);

	@Autowired
	private IMsgCenterService msgCenterService;

	@Autowired
	private IMsgDetailService msgDetailService;

	@Autowired
	private IBaseDao baseDao;

	// 每页显示条数
	private int pageSize = 10;

	// 当前页
	private int currentPage = 1;

	@RequestMapping("toMsgCenter")
	public String toMsgCenter() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return null;
		}
		return "/views/messageInfo/messageInfo";
	}

	@RequestMapping("toMsgCenterDetail")
	public String toMsgCenterDetail() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		return "/views/messageInfo/messageInfoDetail";
	}

	/**
	 * 获取消息中心列表
	 *
	 * @return 消息分类信息
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getMsgList")
	public BaseResponse<PageResult<MsgCenterDto>> getMsgList() {
		int totalPage = 0;
		int totalRecord = 0;
		List<MsgCenterDto> msgInfos = new ArrayList<>();
		try {
			String infotype = request.getParameter("infotype");
			String dateLinetype = request.getParameter("dateLinetype");
			pageSize = Integer.parseInt(request.getParameter("limit"));
			currentPage = Integer.parseInt(request.getParameter("page"));
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			String sql = "select mc.messageid,mc.infotype,mc.messagesourceid,mc.messagedetail,mc.wtime,md.state"
					+ " from erp_message_detail as md, erp_message_center as mc" + " where md.userid =" + "\"" + user.getOssUserId() + "\""
					+ " and mc.messageid = md.messageid";
			String count = "select count(md.state) as total, mc.messageid,mc.infotype,mc.messagesourceid,mc.messagedetail,mc.wtime,md.state"
					+ " from erp_message_detail as md, erp_message_center as mc" + "  where md.userid =" + "\"" + user.getOssUserId() + "\""
					+ " and mc.messageid = md.messageid";
			if (StringUtil.isNotBlank(infotype)) {
				if (Integer.parseInt(infotype) == 0) {
					Date today = DateUtil.getCurrentStartDateTime();
					sql = sql + " and (md.state = 1 or md.readtime >= " + "\"" + sf.format(today) + "\"" + ")";
					count = count + " and (md.state = 1 or md.readtime >= " + "\"" + sf.format(today) + "\"" + ")";
				}
				if (Integer.parseInt(infotype) != 0) {
					sql = sql + " and mc.infotype =  " + Integer.parseInt(infotype);
					count = count + " and mc.infotype =  " + Integer.parseInt(infotype);
				}
			}
			if (StringUtil.isNotBlank(dateLinetype)) {
				int dateType = Integer.parseInt(dateLinetype);
				if (dateType == 1) {
					Date date = new Date();
					date = DateUtil.getThisWeekMonday(date);
					sql = sql + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
					count = count + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
				} else if (dateType == 2) {
					Date date = DateUtil.getThisMonthFirst();
					sql = sql + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
					count = count + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
				} else if (dateType == 3) {
					Date date = DateUtil.getCurrentQuarterStartTime();
					sql = sql + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
					count = count + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
				} else if (dateType == 4) {
					Date date = DateUtil.getThisYearFirst();
					sql = sql + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
					count = count + " and md.wtime >= " + "\"" + sf.format(date) + "\"";
				} else if (dateType == 5) {
					Date date = DateUtil.getThisYearFirst();
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + (-730));
					Date date2 = calnextday.getTime();
					sql = sql + " and md.wtime >= " + "\"" + sf.format(date2) + "\"";
					count = count + " and md.wtime >= " + "\"" + sf.format(date2) + "\"";
				}
			}
			int number = (currentPage - 1) * pageSize;
			sql = sql + " ORDER BY wtime DESC" + " LIMIT " + number + "," + pageSize;

			List<Object[]> objects = (List<Object[]>) baseDao.selectSQL(sql);
			for (Object[] object : objects) {
				MsgCenterDto msgCenterDto = new MsgCenterDto();
				msgCenterDto.setMessageid((object[0]).toString());
				msgCenterDto.setInfotype(Integer.parseInt((object[1].toString())));
				msgCenterDto.setMessagesourceid((object[2]).toString());
				msgCenterDto.setMessagedetail(object[3].toString());
				msgCenterDto.setWtime((Date) object[4]);
				msgCenterDto.setState(Integer.parseInt((object[5].toString())));
				msgInfos.add(msgCenterDto);
			}
			List<Object[]> productList = (List<Object[]>) baseDao.selectSQL(count);

			if (productList != null && productList.size() > 0) {
				Object[] product = productList.get(0);
				totalRecord = ((BigInteger) product[0]).intValue();
				totalPage = ((totalRecord + pageSize - 1) / pageSize);
				return BaseResponse.success("查询成功", new PageResult<>(msgInfos, currentPage, totalPage, totalRecord));
			}
		} catch (Exception e) {
			logger.error("查询客户类型时错误", e);
			return BaseResponse.error("查询数据异常");
		}
		return BaseResponse.success("查询成功", new PageResult<>(msgInfos, currentPage, totalPage, totalRecord));
	}

	/**
	 * 获取消息中心列表
	 *
	 * @return 消息分类信息
	 */
	@ResponseBody
	@RequestMapping("/getMsgCount")
	public BaseResponse<List<MsgCountRespDto>> getMsgCount() {
		int dateLinetype = Integer.parseInt(request.getParameter("dateLinetype"));
		List<MsgCountRespDto> msgCountRespDtos = new ArrayList<>();
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.noLogin("请先登录");
		}

		MsgCountRespDto msgCountRespDto = new MsgCountRespDto();
		msgCountRespDto.setInfoType(1);
		msgCountRespDto.setCount(findMsgList(dateLinetype, 1, user.getOssUserId()).size());
		msgCountRespDtos.add(msgCountRespDto);

		MsgCountRespDto msgCountRespDto2 = new MsgCountRespDto();
		msgCountRespDto2.setInfoType(3);
		msgCountRespDto2.setCount(findMsgList(dateLinetype, 3, user.getOssUserId()).size());
		msgCountRespDtos.add(msgCountRespDto2);

		MsgCountRespDto msgCountRespDto4 = new MsgCountRespDto();
		msgCountRespDto4.setInfoType(2);
		msgCountRespDto4.setCount(findMsgList(dateLinetype, 2, user.getOssUserId()).size());
		msgCountRespDtos.add(msgCountRespDto4);

		MsgCountRespDto msgCountRespDto3 = new MsgCountRespDto();
		msgCountRespDto3.setInfoType(0);

		SearchFilter userfilter = new SearchFilter();
		userfilter.getRules().add(new SearchRule("userid", Constants.ROP_EQ, user.getOssUserId()));
		userfilter.getRules().add(new SearchRule("state", Constants.ROP_EQ, 1));
		List<MsgDetail> msgDetails;
		try {
			msgDetails = msgDetailService.queryAllBySearchFilter(userfilter);
			List<String> msgIds = new ArrayList<>();
			for (MsgDetail msgDetail : msgDetails) {
				msgIds.add(msgDetail.getMessageid());
			}
			SearchFilter filter = new SearchFilter();
			if (!CollectionUtils.isEmpty(msgIds)) {
				filter.getRules().add(new SearchRule("messageid", Constants.ROP_IN, msgIds));
				List<MsgCenter> msgCenters = msgCenterService.queryAllBySearchFilter(filter);
				msgCountRespDto3.setCount(msgCenters.size());
				msgCountRespDtos.add(msgCountRespDto3);
			} else {
				msgCountRespDto3.setCount(0);
				msgCountRespDtos.add(msgCountRespDto3);
			}
		} catch (ServiceException e) {
			logger.error("查询未读消息时错误", e);
			return BaseResponse.error("未读消息");
		}
		return BaseResponse.success(msgCountRespDtos);
	}

	public List<MsgDetail> findMsgList(int dateLinetype, int infotype, String userid) {
		List<MsgDetail> realMsgDetails = new ArrayList<>();
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("userid", Constants.ROP_EQ, userid));
		if (dateLinetype == 1) {
			Date date = new Date();
			date = DateUtil.getThisWeekMonday(date);
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, date));
		} else if (dateLinetype == 2) {
			Date date = DateUtil.getThisMonthFirst();
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, date));
		} else if (dateLinetype == 3) {
			Date date = DateUtil.getCurrentQuarterStartTime();
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, date));
		} else if (dateLinetype == 4) {
			Date date = DateUtil.getThisYearFirst();
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, date));
		} else if (dateLinetype == 5) {
			Date date = DateUtil.getThisYearFirst();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + (-730));
			Date date2 = calnextday.getTime();
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, date2));
		}
		try {
			List<MsgDetail> msgdetails = msgDetailService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(msgdetails)) {
				List<MsgCenter> msgCenters = new ArrayList<>();
				for (MsgDetail msgDetail : msgdetails) {
					SearchFilter msgFilter = new SearchFilter();
					msgFilter.getRules().add(new SearchRule("infotype", Constants.ROP_EQ, infotype));
					msgFilter.getRules().add(new SearchRule("messageid", Constants.ROP_EQ, msgDetail.getMessageid()));
					msgCenters = msgCenterService.queryAllBySearchFilter(msgFilter);
					if (!CollectionUtils.isEmpty(msgCenters)) {
						realMsgDetails.add(msgDetail);
					}
				}
			}
			return realMsgDetails;
		} catch (ServiceException e) {
			logger.error("查询消息时出现异常：", e);
		}
		return realMsgDetails;
	}

	/**
	 * 消息置为已读
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateMsgDetail")
	public BaseResponse<Boolean> updateMsgDetail() {
		String messageids = request.getParameter("messageid");
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.noLogin("请先登录");
		}
		List<MsgDetail> msgDetails;
		try {
			boolean result = false;
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("messageid", Constants.ROP_IN, messageids.split(",")));
			filter.getRules().add(new SearchRule("userid", Constants.ROP_EQ, user.getOssUserId()));
			msgDetails = msgDetailService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(msgDetails)) {
				result = false;
				return BaseResponse.error(result);
			}
			for (MsgDetail msgDetail : msgDetails) {
				if (msgDetail != null) {
					msgDetail.setState(0);
					msgDetail.setReadtime(new Date());
					result = msgDetailService.update(msgDetail);
				} else {
					result = false;
					return BaseResponse.error(result);
				}
			}
			return BaseResponse.success(result);
		} catch (ServiceException e) {
			logger.error("消息中心，将未读消息置为已读异常", e);
		}
		return BaseResponse.error("消息中心，将未读消息置为已读异常");
	}

	/**
	 * 获取消息类型
	 */
	@ResponseBody
	@RequestMapping("/messageType")
	public Map<Integer, String> messageType() {
		Map<Integer, String> result = new HashMap<>(MessageType.values().length);
		for (MessageType messageType : MessageType.values()) {
			result.put(messageType.getCode(), messageType.getDesc());
		}
		return result;
	}

	/**
	 * 分页查询 消息中心的消息
	 *
	 * @return 消息分类信息
	 */
	@ResponseBody
	@RequestMapping("/getMessageByPage")
	public BaseResponse<PageResult<MsgCenterDto>> getMessageByPage(Integer limit, Integer page, String time, String userId, Integer msgType) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null) {
			return BaseResponse.error("请先登录");
		}
		if (limit == null || page == null) {
			return BaseResponse.error("请求参数错误");
		}
		PageResult<MsgCenterDto> msgPageInfo = msgCenterService.queryMsgInfoByPage(onlineUser, limit, page, time, userId, msgType);
		return BaseResponse.success(msgPageInfo);
	}
}
