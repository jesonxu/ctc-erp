package com.dahantc.erp.controller.royalty;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.royalty.RoyaltyDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.royalty.entity.Royalty;
import com.dahantc.erp.vo.royalty.service.IRoyaltyService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/royalty")
public class RoyaltyAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(RoyaltyAction.class);

	@Autowired
	private IRoyaltyService royaltyService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private ICustomerService customerService;

	/**
	 * 查询权益提成
	 * 
	 * @param deptIds
	 *            部门id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerId
	 *            客户id
	 * @param productId
	 *            产品id
	 * @param dateType
	 *            时间类型
	 * @param customerKeyWord
	 *            关键词
	 * @return
	 */
	@ResponseBody
	@PostMapping("/getRoyalty")
	public BaseResponse<JSONArray> getRoyalty(@RequestParam String deptIds, @RequestParam String customerTypeId, @RequestParam String customerId,
			@RequestParam String productId, @RequestParam int dateType, @RequestParam String customerKeyWord) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();
			Map<String, String> datemap = getDate(dateType);
			String startTime = datemap.remove("startTime");
			String endTime = datemap.remove("endTime");

			List<String> customerIdList = new ArrayList<>();

			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) { // 点击产品
				filter.getRules().add(new SearchRule("productid", Constants.ROP_EQ, productId));
			} else if (StringUtils.isNotBlank(customerId)) { // 点击客户
				customerIdList.add(customerId);
			} else { // 未点击产品或者客户，按数据权限查询客户
				Role role = roleService.read(onlineUser.getRoleId());
				User user = onlineUser.getUser();

				List<CustomerRespDto> customers = null;
				if (StringUtils.isNotBlank(customerTypeId)) {
					List<String> depts = StringUtils.isNotBlank(deptIds) ? Arrays.asList(deptIds.split(",")) : null;
					customers = customerService.queryCustomerCount(user.getDeptId(), user.getOssUserId(), role, customerTypeId, depts, customerId,
							customerKeyWord, null);
				} else {
					customers = customerService.queryCustomers(onlineUser, deptIds, customerId, customerTypeId, customerKeyWord);
				}
				if (customers == null) {
					return BaseResponse.error("无客户");
				}
				logger.info("按权限和条件查询到" + customers.size() + "条客户记录，耗时：" + (System.currentTimeMillis() - _start));
				customerIdList = customers.stream().map(CustomerRespDto::getCustomerId).collect(Collectors.toList());
			}

			_start = System.currentTimeMillis();
			// 查询提成记录
			if (!ListUtils.isEmpty(customerIdList)) {
				// 客户条件，有产品的时候不需要
				filter.getRules().add(new SearchRule("entityid", Constants.ROP_IN, customerIdList));
			}
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert(startTime, DateUtil.format2)));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.convert(endTime, DateUtil.format2)));
			List<Royalty> list = royaltyService.queryAllBySearchFilter(filter);
			logger.info("查询到" + (list == null ? 0 : list.size()) + "条提成记录，耗时：" + (System.currentTimeMillis() - _start));

			_start = System.currentTimeMillis();
			Map<String, Royalty> royaltyMap = this.getMonthOrDay(dateType, startTime, endTime);
			if (!ListUtils.isEmpty(list)) {
				for (Royalty obj : list) { // 遍历查询出的权益提成
					String date = "";
					if (dateType == 0 || dateType == 4) {
						// 按周、天查询的需要精确到天的维度 yyyy-MM-dd
						date = DateUtil.convert(obj.getWtime(), DateUtil.format1);
					} else {
						// 按月、季、年精确到月 yyyy-MM
						date = DateUtil.convert(obj.getWtime(), DateUtil.format4);
					}
					// 将每条记录的数据加到对应日期/月份中
					Royalty temp = royaltyMap.getOrDefault(date, new Royalty());
					temp.setProfit(temp.getProfit().add(obj.getProfit()));
					temp.setRoyalty(temp.getRoyalty().add(obj.getRoyalty()));
					royaltyMap.put(date, temp);
				}
			}
			List<RoyaltyDto> proResultList = new ArrayList<>();

			for (Map.Entry<String, Royalty> entry : royaltyMap.entrySet()) {
				RoyaltyDto sp = null;
				if (null != entry.getValue()) {
					sp = new RoyaltyDto(entry.getValue());
				} else {
					sp = new RoyaltyDto();
				}
				sp.setDate(entry.getKey());
				proResultList.add(sp);
			}

			// 按天、周、月、季、年合并记录
			List<RoyaltyDto> resultList = new ArrayList<>();
			String lastDate = "";
			for (RoyaltyDto rsp : proResultList) {
				if (!resultList.isEmpty()) {
					lastDate = resultList.get(resultList.size() - 1).getDate();
				}
				// 设置日期
				String thisDate = datemap.get(rsp.getDate());
				if (lastDate.equals(thisDate)) {
					RoyaltyDto temp = resultList.get(resultList.size() - 1);
					temp.addCurrentbalance(rsp.getCurrentbalance());
					temp.addProfit(rsp.getProfit());
					temp.addRoyalty(rsp.getRoyalty());
				} else {
					rsp.setDate(thisDate);
					resultList.add(rsp);
				}
			}
			JSONArray json = JSONArray.parseArray(JSONObject.toJSONString(resultList));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");

	}

	private Map<String, String> getDate(int dateType) {
		// 根据查询日期类型，获取查询的开始日期和结束日期，且封装好日期到前台展示日期（ 第一周。。。 ）的具体对应关系
		Map<String, String> map = new HashMap<>();
		String startTime = "";
		String endTime = "";
		LocalDate local = LocalDate.now();
		if (dateType == 0) {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
			DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format1);
			local = local.plusDays(7 - local.getDayOfWeek().getValue());
			String[] temp = new String[4];
			temp[0] = "第一周（" + local.plusDays(-6 - 7 * 3).format(fmt) + "到" + local.plusDays(-7 * 3).format(fmt) + "）";
			temp[1] = "第二周（" + local.plusDays(-6 - 7 * 2).format(fmt) + "到" + local.plusDays(-7 * 2).format(fmt) + "）";
			temp[2] = "第三周（" + local.plusDays(-6 - 7 * 1).format(fmt) + "到" + local.plusDays(-7 * 1).format(fmt) + "）";
			temp[3] = "第四周（" + local.plusDays(-6 - 7 * 0).format(fmt) + "到" + local.plusDays(-7 * 0).format(fmt) + "）";

			for (int i = 0; i < 28; i++) {
				map.put(local.plusDays(-i).format(ymdfmt), temp[(27 - i) / 7]);
			}
			endTime = local.format(ymdfmt) + " 23:59:59";
			startTime = local.plusDays(-27).format(ymdfmt) + " 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 1) {
			DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
			for (int i = 0; i < 12; i++) {
				LocalDate templocal = local.plusMonths(-i);
				map.put(templocal.format(ymfmt), templocal.format(ymfmt));
			}
			endTime = local.format(ymfmt) + "-" + local.lengthOfMonth() + " 23:59:59";
			startTime = local.plusMonths(-11).format(ymfmt) + "-01 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 2) {
			DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
			String[] temp = new String[4];
			temp[0] = "yyyy-01-01到yyyy-03-31";
			temp[1] = "yyyy-04-01到yyyy-06-31";
			temp[2] = "yyyy-07-01到yyyy-09-31";
			temp[3] = "yyyy-10-01到yyyy-12-31";
			int thisMonth = local.getMonthValue();
			for (int i = 0; i < 12; i++) {
				LocalDate templocal = local.plusMonths(-i);
				map.put(templocal.format(ymfmt), temp[((thisMonth - i + 11) % 12) / 3].replace("yyyy", templocal.getYear() + ""));
			}
			endTime = local.format(ymfmt) + "-" + local.lengthOfMonth() + " 23:59:59";
			startTime = local.plusMonths(-11).format(ymfmt) + "-01 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 3) {
			DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
			int thisYear = local.getYear();
			LocalDate templocal = local;
			while (templocal.getYear() != thisYear - 3) {
				map.put(templocal.format(ymfmt), templocal.getYear() + "年");
				templocal = templocal.plusMonths(-1);
			}
			endTime = local.format(ymfmt) + "-" + local.lengthOfMonth() + " 23:59:59";
			startTime = (thisYear - 2) + "-01-01 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 4) {
			SimpleDateFormat fmt = new SimpleDateFormat("MM-dd");
			Date monthFirst = DateUtil.getThisMonthFirst();
			Date nextMonthFirst = DateUtil.getNextMonthFirst();
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(monthFirst);
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(nextMonthFirst);
			while (startCal.before(endCal)) {
				map.put(DateUtil.convert(startCal.getTime(), DateUtil.format1), fmt.format(startCal.getTime()));
				startCal.add(Calendar.DAY_OF_YEAR, 1);
			}
			map.put("startTime", DateUtil.convert(monthFirst, DateUtil.format2));
			map.put("endTime", DateUtil.convert(DateUtil.getMonthFinal(monthFirst), DateUtil.format2));
		}
		return map;
	}

	private <T> Map<String, T> getMonthOrDay(int dateType, String startTime, String endTime) {
		Map<String, T> map = new LinkedHashMap<>();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateUtil.format2);
		DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
		DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format1);
		LocalDate start = LocalDate.parse(startTime, fmt);
		LocalDate end = LocalDate.parse(endTime, fmt);
		while (!start.isAfter(end)) {
			if (dateType == 0 || dateType == 4) {
				map.put(start.format(ymdfmt), null);
				start = start.plusDays(1);
			} else {
				map.put(start.format(ymfmt), null);
				start = start.plusMonths(1);
			}
		}
		return map;
	}

	@RequestMapping("/toRoyaltySheet")
	public String toRoyaltySheet() {
		JSONObject params = new JSONObject();
		params.put("deptIds", request.getParameter("deptIds"));
		params.put("sale_open_customer_type_id", request.getParameter("sale_open_customer_type_id"));
		params.put("sale_customer_id", request.getParameter("sale_customer_id"));
		params.put("sale_customer_opts", request.getParameter("sale_customer_opts"));
		params.put("sale_product_id", request.getParameter("sale_product_id"));
		params.put("sale_product_settle_type", request.getParameter("sale_product_settle_type"));
		params.put("sale_operate_year", request.getParameter("sale_operate_year"));
		params.put("sale_operate_month", request.getParameter("sale_operate_month"));
		params.put("sale_settlement_year", request.getParameter("sale_settlement_year"));
		params.put("sale_settlement_month", request.getParameter("sale_settlement_month"));
		params.put("sale_statistic_year", request.getParameter("sale_statistic_year"));
		params.put("sale_open_dept_id", request.getParameter("sale_open_dept_id"));
		params.put("sale_open_sub_dept_id", request.getParameter("sale_open_sub_dept_id"));
		params.put("sale_open_customer_type_name", request.getParameter("sale_open_customer_type_name"));
		params.put("customerKeyWord", request.getParameter("customerKeyWord"));
		request.setAttribute("params", params);
		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("title", request.getParameter("title"));
		return "/views/sheet/royaltySheet";
	}
}
