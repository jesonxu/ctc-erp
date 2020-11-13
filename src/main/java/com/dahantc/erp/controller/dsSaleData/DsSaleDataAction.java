package com.dahantc.erp.controller.dsSaleData;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import com.dahantc.erp.controller.saleGrossProfit.SaleGrossProfitAction;
import com.dahantc.erp.dto.department.DeptInfo;
import com.dahantc.erp.dto.dsSaleData.CustomerReturnDto;
import com.dahantc.erp.dto.dsSaleData.DsSaleDataDto;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.MessageType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
//import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
//import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
import com.dahantc.erp.vo.dsSaleData.entity.DsSaleData;
import com.dahantc.erp.vo.dsSaleData.service.IDsSaleDataService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/dsSaleData")
public class DsSaleDataAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(SaleGrossProfitAction.class);

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

//	@Autowired
//	private IDsOrderService dsOrderService;
	
	@Autowired
	private IDsSaleDataService dsSaleDataService;

	@Autowired
	private IBaseDao baseDao;

	@ResponseBody
	@PostMapping("/getDsSaleData")
	public BaseResponse<Object> getDsSaleData(@RequestParam(required = false) String queryDate) {
		List<DsSaleDataDto> dsSaleDataDtos = new ArrayList<>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();
			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			dsSaleDataDtos = querySaleData(monthDate, onlineUser);
			logger.info("查询销售数据统计耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(dsSaleDataDtos);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	@ResponseBody
	@PostMapping("/getDsSaleReturn")
	public BaseResponse<Object> getDsSaleReturn(@RequestParam(required = false) String queryDate) {
		List<CustomerReturnDto> CustomerReturnDtos = new ArrayList<>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();

			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			CustomerReturnDtos = querySaleReturn(monthDate, onlineUser);
			logger.info("查询销售数据统计耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(CustomerReturnDtos);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}
	
	@ResponseBody
	@PostMapping("/getDsSignStatistics")
	public BaseResponse<Object> getDsSignStatistics(@RequestParam(required = false) String queryDate) {
		List<DsSaleDataDto> dsSaleDataDtos = new ArrayList<>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();
			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			dsSaleDataDtos = querySignStatistics(monthDate, onlineUser);
			logger.info("查询销售数据统计耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(dsSaleDataDtos);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}
	
	
	@ResponseBody
	@PostMapping("/getDsIntentionCustomer")
	public BaseResponse<Object> getDsIntentionCustomer(@RequestParam(required = false) String queryDate) {
		List<DsSaleDataDto> dsSaleDataDtos = new ArrayList<>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();
			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			dsSaleDataDtos = DsIntentionCustomer(monthDate, onlineUser);
			logger.info("查询销售数据统计耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(dsSaleDataDtos);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}                                                                                                           
	

	/** 客户统计数据查询 */
	private List<DsSaleDataDto> querySaleData(Date monthDate, OnlineUser onlineUser) throws BaseException {
		List<DsSaleDataDto> dsSaleDataDtos = new ArrayList<>();
		List<DeptInfo> deptInfos = searchDepartment();
		String searchDeptIds = "";
		if (CollectionUtils.isEmpty(deptInfos)) {
			return dsSaleDataDtos;
		}
		for (DeptInfo deptInfo : deptInfos) {
			searchDeptIds = searchDeptIds + deptInfo.getId() + ",";
		}
		List<User> users = userService.readUsers(onlineUser, null, null, searchDeptIds);
		users.sort((o1, o2) -> o2.getDeptId().compareTo(o1.getDeptId()));
		if (CollectionUtils.isEmpty(users)) {
			return dsSaleDataDtos;
		}
		for (User user : users) {
			DsSaleDataDto dsSaleDataDto = new DsSaleDataDto();
			List<DsSaleData> dsSaleDatas = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserName", Constants.ROP_EQ, user.getRealName()));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getThisMonthFirst(monthDate)));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getNextMonthFirst(monthDate)));
			dsSaleDatas = dsSaleDataService.queryAllBySearchFilter(filter);
			Department department = departmentService.read(user.getDeptId());
			dsSaleDataDto.setDepartment(department.getDeptname());
			int customerAddCount = 0;
			int logAddCount = 0;
			int orderCount = 0;
			BigDecimal totalPrice = new BigDecimal(0);
			BigDecimal grossProfit = new BigDecimal(0);
			dsSaleDataDto.setSaleName(user.getRealName());
			//查询新增客户数&新增日志数
			for (DsSaleData dsSaleData : dsSaleDatas) {
				customerAddCount = customerAddCount + dsSaleData.getAddCustomerCount();
				logAddCount = logAddCount + dsSaleData.getAddLogCount();
				orderCount = orderCount + dsSaleData.getOrderCount();
				totalPrice = totalPrice.add(dsSaleData.getOrderTotalPrice());
				grossProfit = grossProfit.add(dsSaleData.getGrossProfit());
				
			}
			dsSaleDataDto.setNewCustomerCount(customerAddCount);
			dsSaleDataDto.setNewLogCount(logAddCount);
			//签单数
			dsSaleDataDto.setOrderNo(orderCount);
			//签单金额
			dsSaleDataDto.setOrderTotalPrice(totalPrice);
			//累计客户毛利
			dsSaleDataDto.setGrossProfit(grossProfit);
			//毛利率
			BigDecimal grossProfitRate = new BigDecimal(0);
			if (grossProfit.compareTo(BigDecimal.ZERO)!=0) {
				grossProfitRate = grossProfit.divide(totalPrice, 2, BigDecimal.ROUND_HALF_UP);
			}
			dsSaleDataDto.setGrossProfitRate(grossProfitRate);
			//客户回款
			
			//业绩目标
			
			//利润目标
			
			//业绩完成率
			
			//毛利完成率
			
			dsSaleDataDtos.add(dsSaleDataDto);
		}
		return dsSaleDataDtos;
	}

	/** 客户回款数据查询 */
	@SuppressWarnings("unchecked")
	private List<CustomerReturnDto> querySaleReturn(Date monthDate, OnlineUser onlineUser) throws BaseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.format2);
		String monthFirst = sdf.format(DateUtil.getThisMonthFirst(monthDate));
		String monthLast = sdf.format(DateUtil.getNextMonthFirst(monthDate));
		List<CustomerReturnDto> customerReturnDtos = new ArrayList<>();
		List<DeptInfo> deptInfos = searchDepartment();
		String searchDeptIds = "";
		if (CollectionUtils.isEmpty(deptInfos)) {
			return customerReturnDtos;
		}
		for (DeptInfo deptInfo : deptInfos) {
			searchDeptIds = searchDeptIds + deptInfo.getId() + ",";
		}
		String roleId = onlineUser.getRoleId();
		List<User> users = userService.readUsersByRole(onlineUser, roleId, null, null, searchDeptIds);
		if (CollectionUtils.isEmpty(users)) {
			return customerReturnDtos;
		}
		for (User user : users) {
			String sql = "select customername,deptname,sum(ordertotalprice) as ordertotalprice,ossusername,returnmoney"
					+ " from erp_ds_customer_receive" + " where ossusername =" + "\"" + user.getRealName() + "\"" 
					+ " and wtime >= " + "\"" + monthFirst + "\"" + " and wtime <= " + "\"" + monthLast + "\"" + " group by customername";
			List<Object[]> dsOrders = (List<Object[]>) baseDao.selectSQL(sql);
			for (Object[] object : dsOrders) {
				CustomerReturnDto customerReturnDto = new CustomerReturnDto();
				Department department = departmentService.read(user.getDeptId());
				customerReturnDto.setDepartment(department.getDeptname());
				customerReturnDto.setSaleName(user.getRealName());
				customerReturnDto.setOrderTotalPrice(new BigDecimal((object[2]).toString()));
				customerReturnDto.setCustomerName(object[0].toString());
				customerReturnDtos.add(customerReturnDto);
			}
		}
		return customerReturnDtos;
	}

	/** 销售签单统计数据查询 */
	private List<DsSaleDataDto> querySignStatistics(Date monthDate, OnlineUser onlineUser) throws BaseException {
		List<DsSaleDataDto> dsSaleDataDtos = new ArrayList<>();
		List<DeptInfo> deptInfos = searchDepartment();
		String searchDeptIds = "";
		Date date = new Date();
		if (CollectionUtils.isEmpty(deptInfos)) {
			return dsSaleDataDtos;
		}
		for (DeptInfo deptInfo : deptInfos) {
			searchDeptIds = searchDeptIds + deptInfo.getId() + ",";
		}
		String roleId = onlineUser.getRoleId();
		List<User> users = userService.readUsersByRole(onlineUser, roleId, null, null, searchDeptIds);
		users.sort((o1, o2) -> o2.getDeptId().compareTo(o1.getDeptId()));
		if (CollectionUtils.isEmpty(users)) {
			return dsSaleDataDtos;
		}
		for (User user : users) {
			DsSaleDataDto dsSaleDataDto = new DsSaleDataDto();
			List<DsSaleData> dsSaleDatas = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserName", Constants.ROP_EQ, user.getRealName()));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getThisMonthFirst(monthDate)));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getNextMonthFirst(monthDate)));
			dsSaleDatas = dsSaleDataService.queryAllBySearchFilter(filter);
			Department department = departmentService.read(user.getDeptId());
			dsSaleDataDto.setDepartment(department.getDeptname());
//			SearchFilter customerFilter = new SearchFilter();
//			customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, user.getOssUserId()));
//			List<Customer> customers = customerService.queryAllBySearchFilter(customerFilter);
//			int customerCount = 0;
//			int newCustomerCount = 0;
//			int oldCustomerCount = 0;
			int orderCount = 0;
			BigDecimal totalPrice = new BigDecimal(0);
			BigDecimal grossProfit = new BigDecimal(0);
//			if (!CollectionUtils.isEmpty(customers)) {
//				customerCount = customers.size();
//				for (Customer customer : customers) {
//					SearchFilter orderFilter = new SearchFilter();
//					orderFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customer.getCustomerId()));
//					orderFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getNextMonthFirst(monthDate)));
//					List<DsOrder> dsOrders = dsOrderService.queryAllBySearchFilter(orderFilter);
//					if (!CollectionUtils.isEmpty(dsOrders)) {
//						if (dsOrders.size()==1) {
//							newCustomerCount = newCustomerCount + 1;
//						}else {
//							oldCustomerCount = oldCustomerCount + 1;
//						}
//					}
//				}
//			}
			if (date.before(DateUtil.getNextMonthFirst(monthDate))) {
				List<DsSaleData> customerCountDatas = new ArrayList<>();
				SearchFilter customerCountFilter = new SearchFilter();
				customerCountFilter.getRules().add(new SearchRule("ossUserName", Constants.ROP_EQ, user.getRealName()));
				customerCountFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getMonthFinal(monthDate)));
				customerCountFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getDateStartDateTime(DateUtil.getMonthFinal(monthDate))));
				customerCountDatas = dsSaleDataService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(customerCountDatas)) {
					dsSaleDataDto.setCustomerCount(customerCountDatas.get(0).getCustomerCount());
					//新增客户数
					dsSaleDataDto.setNewCustomerCount(customerCountDatas.get(0).getNewCustomerCount());
					//老客户数
					dsSaleDataDto.setOldCustomerCount(customerCountDatas.get(0).getOldCustomerCount());
				}
			}else {
				List<DsSaleData> customerCountDatas = new ArrayList<>();
				SearchFilter customerCountFilter = new SearchFilter();
				customerCountFilter.getRules().add(new SearchRule("ossUserName", Constants.ROP_EQ, user.getRealName()));
				customerCountFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getPreviousEndDateTime()));
				customerCountDatas = dsSaleDataService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(customerCountDatas)) {
					dsSaleDataDto.setCustomerCount(customerCountDatas.get(0).getCustomerCount());
					//新增客户数
					dsSaleDataDto.setNewCustomerCount(customerCountDatas.get(0).getNewCustomerCount());
					//老客户数
					dsSaleDataDto.setOldCustomerCount(customerCountDatas.get(0).getOldCustomerCount());
				}
			}
//			dsSaleDataDto.setCustomerCount(dsSaleData);
//			//新增客户数
//			dsSaleDataDto.setNewCustomerCount(newCustomerCount);
//			//老客户数
//			dsSaleDataDto.setOldCustomerCount(oldCustomerCount);
			dsSaleDataDto.setSaleName(user.getRealName());
			if (!CollectionUtils.isEmpty(dsSaleDatas)) {
				for (DsSaleData dsSaleData : dsSaleDatas) {
					orderCount = orderCount + dsSaleData.getOrderCount();
					totalPrice = totalPrice.add(dsSaleData.getOrderTotalPrice());
					grossProfit = grossProfit.add(dsSaleData.getGrossProfit());
				}
			}
			//签单金额
			dsSaleDataDto.setOrderTotalPrice(totalPrice);
			//累计客户毛利
			dsSaleDataDto.setGrossProfit(grossProfit);
			//成本
			dsSaleDataDto.setTotalCost(totalPrice.subtract(grossProfit));
			//客户回款
			
			dsSaleDataDtos.add(dsSaleDataDto);
		}
		return dsSaleDataDtos;
	}
	
	
	/** 销售意向客户数据查询 */
	@SuppressWarnings("unchecked")
	private List<DsSaleDataDto> DsIntentionCustomer(Date monthDate, OnlineUser onlineUser) throws BaseException {
		List<DsSaleDataDto> dsSaleDataDtos = new ArrayList<>();
		List<DeptInfo> deptInfos = searchDepartment();
		String searchDeptIds = "";
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.format2);
		String dayFirst = sdf.format(DateUtil.getThisMonthFirst(monthDate));
		String dayLast = sdf.format(DateUtil.getThisMonthFinal(monthDate));
		if (CollectionUtils.isEmpty(deptInfos)) {
			return dsSaleDataDtos;
		}
		for (DeptInfo deptInfo : deptInfos) {
			searchDeptIds = searchDeptIds + deptInfo.getId() + ",";
		}
		String roleId = onlineUser.getRoleId();
		List<User> users = userService.readUsersByRole(onlineUser, roleId, null, null, searchDeptIds);
		users.sort((o1, o2) -> o2.getDeptId().compareTo(o1.getDeptId()));
		if (CollectionUtils.isEmpty(users)) {
			return dsSaleDataDtos;
		}
		for (User user : users) {
			DsSaleDataDto dsSaleDataDto = new DsSaleDataDto();
			int customerAddCount = 0;
			int logAddCount = 0;
			dsSaleDataDto.setSaleName(user.getRealName());
			Department department = departmentService.read(user.getDeptId());
			dsSaleDataDto.setDepartment(department.getDeptname());
			String sql = "select *" + " from erp_message_center as mc" 
					 + " where mc.ossuserid =" + "\"" + user.getOssUserId() + "\""
					 + " and mc.wtime >= " + "\"" + dayFirst + "\"" + " and mc.wtime <= " + "\"" + dayLast + "\""
					 + " and mc.customertype = " + "\"" + CustomerTypeValue.INTENTION.getCode() + "\"";
			//查询新增客户数
			String addCustomerCount = sql + " and mc.infotype = " + "\"" + MessageType.ADD_CUSTOMER.ordinal() + "\"";
			List<Object[]> customerAddList = (List<Object[]>) baseDao.selectSQL(addCustomerCount);
			if (!CollectionUtils.isEmpty(customerAddList)) {
				customerAddCount = customerAddList.size();
			}
			dsSaleDataDto.setNewCustomerCount(customerAddCount);
			//查询新增日志数
			String logCount = sql + "and mc.infotype = " + "\"" + MessageType.ADD_LOG.ordinal() + "\"";
			List<Object[]> logAddList = (List<Object[]>) baseDao.selectSQL(logCount);
			if (!CollectionUtils.isEmpty(customerAddList)) {
				logAddCount = logAddList.size();
			}
			dsSaleDataDto.setNewLogCount(logAddCount);
			
			dsSaleDataDtos.add(dsSaleDataDto);
		}
		return dsSaleDataDtos;
	}
	
	@RequestMapping("/toDsSaleDataPage")
	public String toDsSaleDataPage() {
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
		return "/views/manageConsole/dsSaleDataStatistics.html";
	}

	@RequestMapping("/toDsReturnMoneyPage")
	public String toDsReturnMoneyPage() {
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
		return "/views/manageConsole/dsCustomerReceivables.html";
	}

	@RequestMapping("/toDsSignStatisticsPage")
	public String toDsSignStatistics() {
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
		return "/views/manageConsole/dsSignStatistics.html";
	}
	
	@RequestMapping("/toDsIntentionCustomerPage")
	public String toDsIntentionCustomer() {
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
		return "/views/manageConsole/dsIntentionCustomer.html";
	}
	
	
	/**
	 * 查询用户数据权限的所有部门及其下级部门
	 */
	public List<DeptInfo> searchDepartment() {
		logger.info("查询当前用户部门信息开始");
		long _start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return null;
			}
			Role role = roleService.read(onlineUser.getRoleId());
			List<Department> departments = new ArrayList<>();
			if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：部门");
				// 部门权限
				String deptId = onlineUser.getUser().getDeptId();
				// 查询当前用户自己的部门信息
				Department dept = departmentService.read(deptId);
				if (dept != null) {
					departments.add(dept);
					// 查询子部门
					departments.addAll(departmentService.getSubDept(deptId));
				}
			} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：全部");
				// 所有权限
				SearchFilter filter = new SearchFilter();
				filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
				filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
				// 查询所有部门
				departments = departmentService.queryAllBySearchFilter(filter);
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：自定义");
				// 自定义权限
				String deptIds = role.getDeptIds();
				// 查询自定义的部门
				if (StringUtils.isNotBlank(deptIds)) {
					List<String> depts = Arrays.asList(deptIds.split(","));
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
					filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
					filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
					departments = departmentService.queryAllBySearchFilter(filter);
				}
			}
			if (!departments.isEmpty()) {
				List<DeptInfo> deptInfos = new ArrayList<>(departments.size());
				for (Department dept : departments) {
					DeptInfo deptInfo = new DeptInfo();
					deptInfo.setId(dept.getDeptid());
					deptInfo.setName(dept.getDeptname());
					deptInfo.setpId(dept.getParentid() == null ? "" : dept.getParentid());
					deptInfo.setSequence(dept.getSequence());
					deptInfos.add(deptInfo);
				}
				deptInfos.sort(Comparator.comparing(DeptInfo::getSequence));
				for (Department dept : departments) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, dept.getDeptid()));
					filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 1));
					filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, 1));
					List<User> users = userService.queryAllBySearchFilter(filter);
					for (User user : users) {
						DeptInfo deptInfo = new DeptInfo();
						deptInfo.setId(user.getOssUserId());
						deptInfo.setNodeType(user.getOssUserId());
						deptInfo.setName(user.getRealName());
						deptInfo.setpId(dept.getDeptid() == null ? "" : dept.getDeptid());
						deptInfos.add(deptInfo);
					}
				}
				logger.info("查询当前用户部门信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return deptInfos;
			}
		} catch (ServiceException e) {
			logger.error("查询当前用户部门信息异常", e);
		}
		return null;
	}

}
