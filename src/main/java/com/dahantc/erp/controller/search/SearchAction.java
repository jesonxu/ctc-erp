package com.dahantc.erp.controller.search;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.search.service.SearchHandler;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/search")
public class SearchAction extends BaseAction {
	private static Logger logger = LogManager.getLogger(SearchAction.class);

	@Autowired
	private IErpFlowService erpFlowService;

	@RequestMapping("toSearchContent")
	public String toMsgCenter(@RequestParam("searchType") int searchType, @RequestParam("searchContent") String searchContent) {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		try {
			Map<String, String> flowMap = erpFlowService.queryAllBySearchFilter(null).stream()
					.collect(Collectors.toMap(ErpFlow::getFlowId, ErpFlow::getFlowName));
			request.setAttribute("flowMap", flowMap);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		request.setAttribute("searchType", searchType);
		request.setAttribute("searchContent", searchContent);
		return "/views/search/searchContent";
	}

	@GetMapping("/searchContent")
	@ResponseBody
	public BaseResponse<PageResult<Object>> searchContent(@RequestParam("searchType") int searchType, @RequestParam("searchContent") String searchContent,
			@RequestParam(value = "searchStartDate", required = false) String searchStartDate,
			@RequestParam(value = "searchDate", required = false) String searchDate, @RequestParam("limit") int pageSize, @RequestParam("page") int nowPage) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			logger.error("未登录，获取数据失败");
			return BaseResponse.error("未登录，获取数据失败");
		}
		if (0 == pageSize) {
			pageSize = 15;
		}
		if (0 == nowPage) {
			nowPage = 1;
		}
		return SearchHandler.getInstance().doSearch(searchType, onlineUser, searchContent, searchStartDate, searchDate, pageSize, nowPage);
	}

	@ResponseBody
	@GetMapping("/exportFsExpenseIncome")
	public BaseResponse<UploadFileRespDto> exportFsExpenseIncome(@RequestParam("searchType") int searchType,
			@RequestParam("searchContent") String searchContent, @RequestParam(value = "searchDate", required = false) String searchDate,
			@RequestParam(value = "searchStartDate", required = false) String searchStartDate, @RequestParam(value = "flowId", required = false) String flowId) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			logger.error("未登录，获取数据失败");
			return BaseResponse.error("未登录，获取数据失败");
		}
		return SearchHandler.getInstance().doExport(searchType, onlineUser, searchContent, searchStartDate, searchDate, flowId);
	}
}
