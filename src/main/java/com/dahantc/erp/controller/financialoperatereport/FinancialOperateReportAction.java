package com.dahantc.erp.controller.financialoperatereport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.productType.ProductTypeAction;
import com.dahantc.erp.enums.PagePermission;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.financialoperatereport.IFinancialOperateReportService;
import com.dahantc.erp.vo.financialoperatereport.entity.GrossProfitRateOverview;
import com.dahantc.erp.vo.financialoperatereport.entity.GrossProfitRateOverview.GrossProfitRateSection;
import com.dahantc.erp.vo.financialoperatereport.entity.MainCustResult;
import com.dahantc.erp.vo.financialoperatereport.entity.NegativeGrossProfitCust;
import com.dahantc.erp.vo.financialoperatereport.entity.SectionMainCust;
import com.dahantc.erp.vo.role.entity.Role;

@Controller
@RequestMapping("financialOperateReport")
public class FinancialOperateReportAction extends BaseAction {

	@Autowired
	private IFinancialOperateReportService financialOperateReportService;

	@Autowired
	private ProductTypeAction productTypeAction;

	private static final Logger logger = LogManager.getLogger(FinancialOperateReportAction.class);

	// 跳转财务经营权责表
	@RequestMapping("/toFinancialOperateReportPage")
	public String toFinancialOperateReportPage() {
		String roleId = getOnlineUserAndOnther().getRoleId();
		try {
			JSONArray productTypeSelect = productTypeAction.getProductTypeSelect();
			JSONObject json = new JSONObject();
			json.put("value", "-1");
			json.put("name", "全部类型");
			productTypeSelect.add(0, json);
			request.setAttribute("productTypeSelect", productTypeSelect);
			Role role = roleService.read(roleId);
			Map<String, Boolean> map = role.getPagePermissionMap();
			if (map.containsKey(PagePermission.financialOperateReportTable.getDesc()) && map.get(PagePermission.financialOperateReportTable.getDesc())) {
				return "/views/manageConsole/financialOperatereport";
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "";
	}

	// 各个毛利率段总览
	@RequestMapping("/getEverySectionRateOverview")
	@ResponseBody
	public BaseResponse<List<GrossProfitRateOverview>> getEverySectionRateOverview(@RequestParam String date, @RequestParam(required = false) String productType) {
		try {
			if (date.length() == 7) {
				date = date + "-01";
			}
			Date startDate = DateUtil.convert1(date);
			Date endDate = DateUtil.getNextMonthFirst(startDate);
			return BaseResponse.success(financialOperateReportService.getEverySectionRateOverview(startDate, endDate, productType));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(new ArrayList<>());
	}

	// 负毛利率的主要客户
	@RequestMapping("/getNegativeGrossProfitCusts")
	@ResponseBody
	public BaseResponse<MainCustResult<List<NegativeGrossProfitCust>>> getNegativeGrossProfitCusts(@RequestParam String date, @RequestParam(required = false) String productType) {
		try {
			if (date.length() == 7) {
				date = date + "-01";
			}
			Date startDate = DateUtil.convert1(date);
			Date endDate = DateUtil.getNextMonthFirst(startDate);

			return BaseResponse.success(financialOperateReportService.getNegativeGrossProfitCusts(startDate, endDate, productType));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(new MainCustResult<>(new ArrayList<>(), 0));
	}

	// 各个毛利率段的主要客户
	@RequestMapping("/getSectionMainCusts")
	@ResponseBody
	public BaseResponse<MainCustResult<List<SectionMainCust>>> getSectionMainCusts(@RequestParam String date, @RequestParam Integer index, @RequestParam(required = false) String productType) {
		try {
			if (date.length() == 7) {
				date = date + "-01";
			}
			Date startDate = DateUtil.convert1(date);
			Date endDate = DateUtil.getNextDayStart(startDate);

			return BaseResponse.success(financialOperateReportService.getSectionMainCusts(startDate, endDate, GrossProfitRateSection.values()[index], productType));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(new MainCustResult<>(new ArrayList<>(), 0));
	}

	// 零星客户总数
	@RequestMapping("/getSmallCustCount")
	@ResponseBody
	public BaseResponse<Integer> getSmallCustCount(@RequestParam String date, @RequestParam(required = false) String productType) {
		try {
			if (date.length() == 7) {
				date = date + "-01";
			}
			Date startDate = DateUtil.convert1(date);
			Date endDate = DateUtil.getNextDayStart(startDate);

			return financialOperateReportService.getSmallCustCount(startDate, endDate, productType);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(-1);
	}

}
