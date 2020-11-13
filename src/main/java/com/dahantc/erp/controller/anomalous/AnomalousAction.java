package com.dahantc.erp.controller.anomalous;

import java.util.List;

import com.dahantc.erp.dto.messageCenter.MsgDetailDto;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.department.DeptOrUserInfo;
import com.dahantc.erp.vo.dept.service.IDepartmentService;

/**
 * 异常事件（流程）
 */
@Controller
@RequestMapping("/anomalous")
public class AnomalousAction extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(AnomalousAction.class);

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IMsgCenterService msgCenterService;


	@RequestMapping("/toAnomalousPage")
	public String toAnomalousPage() {
		return "/views/personalcenter/anomalous";
	}

	@ResponseBody
	@RequestMapping("/queryDept")
	public BaseResponse<List<DeptOrUserInfo>> queryDept(@RequestParam(required = false) String deptId) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		List<DeptOrUserInfo> list = departmentService.getDirectChildAndUser(onlineUser, deptId);
		if (StringUtils.isBlank(deptId)) {
			list.add(0, new DeptOrUserInfo(onlineUser.getUser().getOssUserId(), "我的异常", 1));
		}
		return BaseResponse.success(list);
	}

	@ResponseBody
	@RequestMapping("/queryAnomalousMsg")
	public BaseResponse<List<MsgDetailDto>> queryAnomalousMsg(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) String deptId, @RequestParam(required = false) String userId) {
		List<MsgDetailDto> result = msgCenterService.queryMsgAndDetail(startDate, endDate, deptId, userId);
		return BaseResponse.success(result);
	}
}
