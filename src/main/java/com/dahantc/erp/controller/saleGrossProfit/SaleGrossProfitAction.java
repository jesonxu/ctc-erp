package com.dahantc.erp.controller.saleGrossProfit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.saleGrossProfit.SaleGrossProfitDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerStatistics.entity.CustomerStatistics;
import com.dahantc.erp.vo.customerStatistics.service.ICustomerStatisticsService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/saleGrossProfit")
public class SaleGrossProfitAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(SaleGrossProfitAction.class);

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ICustomerStatisticsService customerStatisticsService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IProductTypeService productTypeService;

	@ResponseBody
	@PostMapping("/getSaleGrossProdift")
	public BaseResponse<Object> getSaleGrossProdift(@RequestParam(required = false) String customerId, @RequestParam(required = false) String productId,
			@RequestParam(required = false) String queryDate) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}

			long _start = System.currentTimeMillis();

			List<CustomerProduct> productList = new ArrayList<>();

			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}

			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) {
				CustomerProduct product = customerProductService.read(productId);
				if (product != null) {
					productList.add(product);
				}
			} else {
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				productList = customerProductService.queryAllByFilter(filter);
			}

			if (CollectionUtils.isEmpty(productList)) {
				return BaseResponse.success(new ArrayList<SaleGrossProfitDto>());
			}

			List<CustomerStatistics> tjList = queryTjData(productList, monthDate, DateUtil.getNextMonthFirst(monthDate), null, null, null, -1);
			if (CollectionUtils.isEmpty(tjList)) {
				return BaseResponse.success(new ArrayList<SaleGrossProfitDto>());
			}

			Map<String, String> cacheProductMap = productList.stream()
					.collect(Collectors.toMap(CustomerProduct::getProductId, CustomerProduct::getProductName));

			List<SaleGrossProfitDto> resultList = tjList.stream().map(statistics -> buildSaleGrossProfitDto(statistics, cacheProductMap, null, null, null))
					.collect(Collectors.toList());

			logger.info("查询权益毛利耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	@ResponseBody
	@PostMapping("/getSaleGrossProdift2Manager")
	public BaseResponse<Object> getSaleGrossProdift2Manager(@RequestParam(required = false) String userId, @RequestParam(required = false) String deptId,
			@RequestParam(required = false) String queryDate, @RequestParam(required = false) String queryDate2) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}

			long _start = System.currentTimeMillis();

			List<CustomerProduct> productList = new ArrayList<>();
			Date startDateTime = DateUtil.getCurrentMonthFirstDay();
			Date endDateTime = DateUtil.getNextMonthFirst();
			// queryDate2 起始，queryDate结束
			if (StringUtils.isNoneBlank(queryDate, queryDate2)) {
				startDateTime = DateUtil.convert1(queryDate2);
				endDateTime = DateUtil.getNextDayStart(DateUtil.convert1(queryDate));
			} else if (StringUtils.isNotBlank(queryDate)) {
				startDateTime = DateUtil.convert1(queryDate);
				endDateTime = DateUtil.getNextDayStart(DateUtil.convert1(queryDate));
			}

			List<CustomerStatistics> tjList = null;
			if (!StringUtils.isAllBlank(userId, deptId)) {
				tjList = queryTjData(productList, startDateTime, endDateTime,
						StringUtils.isNotBlank(deptId) ? Arrays.asList(deptId.split(",")).stream().map(id -> "'" + id + "'").collect(Collectors.toSet()) : null,
						StringUtils.isNotBlank(userId) ? Arrays.asList(userId.split(",")).stream().map(id -> "'" + id + "'").collect(Collectors.toSet()) : null,
						null, 0);
			} else {
				List<String> deptIdList = departmentService.getDeptIdsByPermission(onlineUser);
				if (CollectionUtils.isEmpty(deptIdList)) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
					productList = customerProductService.queryAllByFilter(filter);
					if (CollectionUtils.isEmpty(productList)) {
						return BaseResponse.success(new ArrayList<SaleGrossProfitDto>());
					}
				}
				if (!CollectionUtils.isEmpty(deptIdList)) {
					tjList = queryTjData(productList, startDateTime, endDateTime, deptIdList.stream().map(id -> "'" + id + "'").collect(Collectors.toSet()),
							null, null, 0);
				} else {
					tjList = queryTjData(productList, startDateTime, endDateTime, null, null,
							productList.stream().map(product -> "'" + product.getProductId() + "'").collect(Collectors.toSet()), 0);
				}
			}

			if (CollectionUtils.isEmpty(tjList)) {
				return BaseResponse.success(new ArrayList<SaleGrossProfitDto>());
			}

			Set<String> deptIds = tjList.stream().map(CustomerStatistics::getDeptId).collect(Collectors.toSet());
			SearchFilter cacheFilter = new SearchFilter();
			cacheFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, new ArrayList<>(deptIds)));
			Map<String, String> cacheDeptNameMap = departmentService.queryAllBySearchFilter(cacheFilter).stream()
					.collect(Collectors.toMap(Department::getDeptid, Department::getDeptname));

			Set<String> customerIds = tjList.stream().map(CustomerStatistics::getCustomerId).collect(Collectors.toSet());
			cacheFilter.getRules().clear();
			if (customerIds.size() <= 100) {
				cacheFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, new ArrayList<>(customerIds)));
			}
			Map<String, String> cacheCustomerMap = customerService.queryAllBySearchFilter(cacheFilter).stream()
					.collect(Collectors.toMap(Customer::getCustomerId, Customer::getCompanyName));

			Set<String> productIds = tjList.stream().map(CustomerStatistics::getProductId).collect(Collectors.toSet());
			cacheFilter.getRules().clear();
			if (productIds.size() <= 100) {
				cacheFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, new ArrayList<>(productIds)));
			}
			Map<String, String> cacheProductMap = customerProductService.queryAllByFilter(cacheFilter).stream()
					.collect(Collectors.toMap(CustomerProduct::getProductId, CustomerProduct::getProductName));

			Set<String> saleUserIds = tjList.stream().map(CustomerStatistics::getSaleUserId).collect(Collectors.toSet());
			cacheFilter.getRules().clear();
			cacheFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, new ArrayList<>(saleUserIds)));
			Map<String, String> cacheSaleNameMap = userService.queryAllBySearchFilter(cacheFilter).stream()
					.collect(Collectors.toMap(User::getOssUserId, User::getRealName));

			List<SaleGrossProfitDto> resultList = new ArrayList<>();
			SaleGrossProfitDto totalDto = new SaleGrossProfitDto();
			totalDto.setDeptName("合计");
			totalDto.setSaleName("-");
			totalDto.setCustomerName("-");
			totalDto.setProductName("-");
			totalDto.setLoginName("-");
			totalDto.setProductType("-");
			totalDto.setUnitPrice(new BigDecimal("-1"));
			totalDto.setCustomerPrice(new BigDecimal("-1"));
			totalDto.setSendCount(0L);
			totalDto.setSalesVolume(BigDecimal.ZERO);
			totalDto.setGrossProfit(BigDecimal.ZERO);
			String lastDeptSaleName = "";
			SaleGrossProfitDto dto = null;

			for (CustomerStatistics statistics : tjList) {
				SaleGrossProfitDto currDto = buildSaleGrossProfitDto(statistics, cacheProductMap, cacheDeptNameMap, cacheSaleNameMap, cacheCustomerMap);

				if (StringUtils.isBlank(lastDeptSaleName) || !StringUtils.equals(lastDeptSaleName, currDto.getDeptName() + currDto.getSaleName())) {
					if (StringUtils.isNotBlank(lastDeptSaleName) && dto != null) { // 保存上一条记录
						resultList.add(dto);
						resultList.add(currDto);
					} else {
						resultList.add(currDto);
					}
					lastDeptSaleName = currDto.getDeptName() + currDto.getSaleName();
					dto = new SaleGrossProfitDto();
					dto.setDeptName(currDto.getDeptName());
					dto.setSaleName("合计（" + currDto.getSaleName() + "）");
					dto.setCustomerName("-");
					dto.setProductName("-");
					dto.setLoginName("-");
					dto.setProductType("-");
					dto.setUnitPrice(new BigDecimal("-1"));
					dto.setCustomerPrice(new BigDecimal("-1"));
					dto.setSendCount(currDto.getSendCount());
					dto.setSalesVolume(currDto.getSalesVolume());
					dto.setGrossProfit(currDto.getGrossProfit());
				} else {
					resultList.add(currDto);
					dto.setSendCount(dto.getSendCount() + currDto.getSendCount());
					dto.setSalesVolume(dto.getSalesVolume().add(currDto.getSalesVolume()));
					dto.setGrossProfit(dto.getGrossProfit().add(currDto.getGrossProfit()));
				}
				totalDto.setSendCount(totalDto.getSendCount() + currDto.getSendCount());
				totalDto.setSalesVolume(totalDto.getSalesVolume().add(currDto.getSalesVolume()));
				totalDto.setGrossProfit(totalDto.getGrossProfit().add(currDto.getGrossProfit()));
			}
			if (dto != null) {
				resultList.add(dto);
				resultList.add(totalDto);
			}

			logger.info("查询权益毛利耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	/** 客户统计数据查询 */
	private List<CustomerStatistics> queryTjData(List<CustomerProduct> productList, Date startDateTime, Date endDateTime, Set<String> deptId,
			Set<String> userId, Set<String> productId, int sumBy) throws BaseException {

		if (CollectionUtils.isEmpty(productList) && sumBy < 0) {
			return null;
		}

		if (sumBy < 0) {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("statsDate", Constants.ROP_GE, startDateTime));
			filter.getRules().add(new SearchRule("statsDate", Constants.ROP_LT, endDateTime));
			filter.getRules()
					.add(new SearchRule("productId", Constants.ROP_IN, productList.stream().map(CustomerProduct::getProductId).collect(Collectors.toList())));
			filter.getOrders().add(new SearchOrder("statsDate", Constants.ROP_ASC));
			filter.getOrders().add(new SearchOrder("productId", Constants.ROP_DESC));
			filter.getOrders().add(new SearchOrder("productType", Constants.ROP_DESC));
			return customerStatisticsService.queryAllBySearchFilter(filter);
		} else if (sumBy == 0) { // 按产品聚合
			String hql = "SELECT deptid, saleuserid, customerid, productid, SUM(successcount), SUM(nocostpricecount), SUM(receivables), SUM(cost), SUM(grossprofit), SUM(totalsuccessCount), producttype, loginname "
					+ "FROM erp_customer_statistics WHERE statsdate >= ? AND statsdate < ? AND totalsuccessCount <> 0 ";
			if (CollectionUtils.isEmpty(productId)) {
				if (CollectionUtils.isEmpty(deptId)) {
					hql += "AND saleuserid IN (" + StringUtils.join(userId.iterator(), ",") + ") ";
				} else if (CollectionUtils.isEmpty(userId)) {
					hql += "AND deptid IN (" + StringUtils.join(deptId.iterator(), ",") + ") ";
				} else {
					hql += "AND (deptid IN (" + StringUtils.join(deptId.iterator(), ",") + ") OR saleuserid IN (" + StringUtils.join(userId.iterator(), ",")
							+ ")) ";
				}
			} else {
				hql += "AND productid IN (" + StringUtils.join(productId.iterator(), ",") + ") ";
			}
			hql += "GROUP BY deptid, saleuserid, customerid, productid ORDER BY deptid DESC, saleuserid DESC, customerid DESC, productid DESC";

			List<Object> resultList = baseDao.selectSQL(hql, new Object[] { startDateTime, endDateTime });
			if (CollectionUtils.isEmpty(resultList)) {
				return null;
			}

			List<CustomerStatistics> list = new ArrayList<>();
			resultList.forEach(obj -> {
				Object[] objs = (Object[]) obj;
				CustomerStatistics customerStatistics = new CustomerStatistics();
				customerStatistics.setDeptId((String) objs[0]);
				customerStatistics.setSaleUserId((String) objs[1]);
				customerStatistics.setCustomerId((String) objs[2]);
				customerStatistics.setProductId((String) objs[3]);
				customerStatistics.setSuccessCount(((Number) objs[4]).longValue());
				customerStatistics.setNoCostPriceCount(((Number) objs[5]).longValue());
				customerStatistics.setReceivables((new BigDecimal(((Number) objs[6]).doubleValue())));
				customerStatistics.setCost((new BigDecimal(((Number) objs[7]).doubleValue())));
				customerStatistics.setGrossProfit((new BigDecimal(((Number) objs[8]).doubleValue())));
				customerStatistics.setTotalSuccessCount(((Number) objs[9]).longValue());
				customerStatistics.setProductType(((Number) objs[10]).intValue());
				customerStatistics.setLoginName((String) objs[11]);
				list.add(customerStatistics);
			});
			return list;
		}
		return null;
	}

	/** 转dto */
	@SuppressWarnings("deprecation")
	private SaleGrossProfitDto buildSaleGrossProfitDto(CustomerStatistics statistics, Map<String, String> cacheProductMap, Map<String, String> cacheDeptNameMap,
			Map<String, String> cacheSaleNameMap, Map<String, String> cacheCustomerMap) {
		SaleGrossProfitDto dto = new SaleGrossProfitDto();
		Date statsDate = statistics.getStatsDate();
		if (statsDate != null) {
			dto.setDay(statsDate.getDate());
			dto.setWeek(statsDate.getDay());
		}
		if (!CollectionUtils.isEmpty(cacheSaleNameMap)) {
			dto.setSaleName(cacheSaleNameMap.get(statistics.getSaleUserId()));
		}
		dto.setLoginName(StringUtils.isBlank(statistics.getLoginName()) ? statistics.getLoginName() : statistics.getLoginName().replace(",", "、"));
		dto.setProductName(cacheProductMap.get(statistics.getProductId()));
		dto.setProductType(productTypeService.getProductTypeNameByValue(statistics.getProductType()));
		dto.setSendCount(statistics.getSuccessCount());
		dto.setSalesVolume(statistics.getReceivables());
		// 计算平均销售单价
		if (statistics.getSuccessCount() > 0 && statistics.getCustPrice().compareTo(BigDecimal.ZERO) == 0) {
			dto.setCustomerPrice(statistics.getReceivables().divide(BigDecimal.valueOf(statistics.getSuccessCount()), 6, BigDecimal.ROUND_HALF_UP));
		} else {
			dto.setCustomerPrice(statistics.getCustPrice());
		}
		// 计算平均成本单价 = 成本 / 有成本单价的发送量
		if (statistics.getTotalSuccessCount() - statistics.getNoCostPriceCount() > 0 && statistics.getCostPrice().compareTo(BigDecimal.ZERO) == 0) {
			dto.setUnitPrice(statistics.getCost().divide(BigDecimal.valueOf(statistics.getTotalSuccessCount() - statistics.getNoCostPriceCount()), 6, BigDecimal.ROUND_HALF_UP));
		} else {
			dto.setUnitPrice(statistics.getCostPrice());
		}
		dto.setGrossProfit(statistics.getGrossProfit());
		if (!CollectionUtils.isEmpty(cacheDeptNameMap)) {
			dto.setProductId(statistics.getProductId());
			dto.setDeptName(cacheDeptNameMap.get(statistics.getDeptId()));
		}
		if (!CollectionUtils.isEmpty(cacheCustomerMap)) {
			dto.setCustomerName(cacheCustomerMap.get(statistics.getCustomerId()));
		}
		return dto;
	}

	@RequestMapping("/toSaleGrossProdift")
	public String toSaleGrossProdift() {
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
		if (StringUtils.isNotBlank(request.getParameter("sale_product_id"))) {
			try {
				CustomerProduct product = customerProductService.read(request.getParameter("sale_product_id"));
				Customer customer = customerService.read(product.getCustomerId());
				request.setAttribute("title", customer.getCompanyName() + ":" + product.getProductName());
			} catch (ServiceException e) {
				logger.error("", e);
			}
		} else {
			request.setAttribute("title", request.getParameter("title"));
		}
		return "/views/salesheet/saleGrossProfitSheet";
	}

	@RequestMapping("/toSaleGrossProdift2Manager")
	public String toSaleGrossProdift2Manager() {
		List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
		boolean isManager = CollectionUtils.isEmpty(deptIdList) ? false : true;
		request.setAttribute("isManager", isManager);
		return "/views/manageConsole/saleGrossProfitSheet";
	}

}
