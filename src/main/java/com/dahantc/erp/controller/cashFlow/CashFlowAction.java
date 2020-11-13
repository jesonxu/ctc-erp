package com.dahantc.erp.controller.cashFlow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.finance.CashFlowRspDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;
import com.dahantc.erp.vo.cashflow.service.ICashFlowService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/cashFlow")
public class CashFlowAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(CashFlowAction.class);

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private ICashFlowService cashFlowService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IFlowEntService flowEntService;

	/**
	 * 查询客户的现金流数据
	 * 
	 * @param deptIds
	 *            部门id（条件，可复数）
	 * @param customerTypeId
	 *            客户类型（条件）
	 * @param customerId
	 *            客户id（条件）
	 * @param productId
	 *            产品id（条件）
	 * @param reqYear
	 *            年份
	 * @param customerKeyWord
	 *            客户关键词（条件）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getCustomerCashFlow")
	public BaseResponse<JSONObject> getCustomerCashFlow(@RequestParam String deptIds, @RequestParam String customerTypeId, @RequestParam String customerId,
			@RequestParam String productId, @RequestParam int reqYear, @RequestParam String customerKeyWord) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();
			List<String> customerIdList = new ArrayList<>();

			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) { // 点击产品
				filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			} else if (StringUtils.isNotBlank(customerId)) { // 点击客户
				customerIdList.add(customerId);
			} else { // 未点击产品或者客户，按数据权限查询客户
				SearchFilter customerFilter = new SearchFilter();
				Role role = roleService.read(getOnlineUserAndOnther().getRoleId());
				User user = onlineUser.getUser();

				if (role.getDataPermission() == DataPermission.Dept.ordinal() && StringUtils.isBlank(deptIds)) {
					// 部门 权限
					List<String> depts = departmentService.getSubDept(user.getDeptId()).stream().map(Department::getDeptid).collect(Collectors.toList());
					depts.add(user.getDeptId()); // 自己部门+子部门
					customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, depts));
				} else if (role.getDataPermission() == DataPermission.Self.ordinal()) {
					// 自己 权限
					customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, getOnlineUser().getOssUserId()));
				} else if (role.getDataPermission() == DataPermission.Flow.ordinal()) {
					// 流程 权限
					// 查询待处理流程的相关客户
					List<String> customerIds = flowEntService.queryFlowEntityId(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId(),
							EntityType.CUSTOMER);
					if (ListUtils.isEmpty(customerIds)) {
						return BaseResponse.error("无客户");
					}
					customerFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIds));
				} else if (role.getDataPermission() == DataPermission.Customize.ordinal()) {
					// 自定义 权限
					// 选择的部门
					String roleDeptIds = role.getDeptIds();
					if (StringUtils.isNotBlank(roleDeptIds)) {
						List<String> depts = Arrays.asList(roleDeptIds.split(","));
						customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, depts));
					} else {
						return BaseResponse.error("无部门");
					}
				} // else 全部 权限

				if (StringUtils.isNotBlank(deptIds)) { // 部门过滤条件
					customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds.split(",")));
				}
				if (StringUtils.isNotBlank(customerTypeId)) { // 点击客户类型
					customerFilter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
				}
				if (StringUtils.isNotBlank(customerKeyWord)) {
					customerFilter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
				}
				List<Customer> customers = customerService.queryAllBySearchFilter(customerFilter);
				if (ListUtils.isEmpty(customers)) {
					return BaseResponse.error("无客户");
				}
				logger.info("按权限和条件查询到" + customers.size() + "条客户记录，耗时：" + (System.currentTimeMillis() - _start));
				customerIdList = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
			}

			_start = System.currentTimeMillis();
			// 查询现金流记录
			if (!ListUtils.isEmpty(customerIdList)) {
				// 客户条件，有产品的时候不需要
				filter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, customerIdList));
			}
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert4(reqYear + "-01")));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.convert4((reqYear + 1) + "-01")));
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
			List<CashFlow> list = cashFlowService.queryAllBySearchFilter(filter);
			logger.info("查询到" + (list == null ? 0 : list.size()) + "条现金流记录，耗时：" + (System.currentTimeMillis() - _start));

			_start = System.currentTimeMillis();
			Map<String, CashFlow> map = DateUtil.getYearMonth(reqYear); // 年的月份
			if (!ListUtils.isEmpty(list)) {
				for (CashFlow obj : list) { // 遍历查询出的现金流结果
					String date = DateUtil.convert(obj.getWtime(), DateUtil.format4);
					// 将每条记录的数据加到对应月份中
					if (map.get(date) == null) {
						map.put(date, new CashFlow());
					}
					CashFlow temp = map.get(date);
					temp.setPayables(temp.getPayables().add(obj.getPayables()));
					temp.setActualPayables(temp.getActualPayables().add(obj.getActualPayables()));
					temp.setActualReceivables(temp.getActualReceivables().add(obj.getActualReceivables()));
					temp.setReceivables(temp.getReceivables().add(obj.getReceivables()));
				}
			}

			Map<String, List<CashFlowRspDto>> resultMap = new HashMap<>();
			for (Map.Entry<String, CashFlow> entry : map.entrySet()) {
				String year = entry.getKey().substring(0, 4);
				if (!resultMap.containsKey(year)) {
					resultMap.put(year, new ArrayList<>());
				}

				CashFlowRspDto sp = null;
				if (null != entry.getValue()) {
					sp = new CashFlowRspDto(entry.getValue());
				} else {
					sp = new CashFlowRspDto();
				}
				sp.setYear(year);
				sp.setMonth(entry.getKey().substring(5));
				resultMap.get(year).add(sp);
			}

			JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(resultMap));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");

	}

	/**
	 * 查询供应商的现金流数据
	 * 
	 * @param keyWord
	 *            关键词（条件）
	 * @param supplierTypeId
	 *            供应商类型（条件）
	 * @param supplierId
	 *            供应商id（条件）
	 * @param productId
	 *            产品id（条件）
	 * @param reqYear
	 *            年份
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getResourceCashFlow")
	public BaseResponse<JSONObject> getResourceCashFlow(@RequestParam String keyWord, @RequestParam String supplierTypeId, @RequestParam String supplierId,
			@RequestParam String productId, @RequestParam int reqYear) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();

			List<String> supplierIdList = new ArrayList<>();

			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) { // 点击产品
				filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			} else if (StringUtils.isNotBlank(supplierId)) { // 点击供应商
				supplierIdList.add(supplierId);
			} else { // 未点击产品或者供应商，按数据权限查询
				SearchFilter supplierFilter = new SearchFilter();
				Role role = roleService.read(onlineUser.getRoleId());
				User user = onlineUser.getUser();

				if (role.getDataPermission() == DataPermission.Self.ordinal()) {
					// 自己 权限
					supplierFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
				} else if (role.getDataPermission() == DataPermission.Dept.ordinal()) {
					// 部门 权限
					List<String> depts = departmentService.getSubDept(user.getDeptId()).stream().map(Department::getDeptid).collect(Collectors.toList());
					depts.add(user.getDeptId()); // 自己部门+子部门
					supplierFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, depts));
				} else if (role.getDataPermission() == DataPermission.Flow.ordinal()) {
					// 流程 权限
					// 查询待处理流程的相关供应商
					List<String> supplierIds = flowEntService.queryFlowEntityId(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId(),
							EntityType.SUPPLIER);
					if (ListUtils.isEmpty(supplierIds)) {
						return BaseResponse.error("无供应商");
					}
					supplierFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, supplierIds));
				} else if (role.getDataPermission() == DataPermission.Customize.ordinal()) {
					// 自定义 权限
					// 选择的部门
					String roleDeptIds = role.getDeptIds();
					if (StringUtils.isNotBlank(roleDeptIds)) {
						List<String> depts = Arrays.asList(roleDeptIds.split(","));
						supplierFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, depts));
					} else {
						return BaseResponse.error("无部门");
					}
				} // else 全部 权限

				if (StringUtils.isNotBlank(keyWord)) { // 公司名搜索条件
					supplierFilter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, keyWord));
				}
				if (StringUtils.isNotBlank(supplierTypeId)) { // 点击供应商类型
					supplierFilter.getRules().add(new SearchRule("supplierTypeId", Constants.ROP_EQ, supplierTypeId));
				}

				List<Supplier> suppliers = supplierService.queryAllBySearchFilter(supplierFilter);
				if (ListUtils.isEmpty(suppliers)) {
					return BaseResponse.error("无供应商");
				}
				logger.info("按权限和条件查询到" + suppliers.size() + "条供应商记录，耗时：" + (System.currentTimeMillis() - _start));
				supplierIdList = suppliers.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
			}

			_start = System.currentTimeMillis();
			// 查询现金流记录
			if (!ListUtils.isEmpty(supplierIdList)) {
				// 供应商条件，有产品的时候不需要
				filter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, supplierIdList));
			}
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert4(reqYear + "-01")));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.convert4((reqYear + 1) + "-01")));
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.SUPPLIER.ordinal()));
			List<CashFlow> list = cashFlowService.queryAllBySearchFilter(filter);
			logger.info("查询到" + (list == null ? 0 : list.size()) + "条现金流记录，耗时：" + (System.currentTimeMillis() - _start));

			_start = System.currentTimeMillis();
			Map<String, CashFlow> map = DateUtil.getYearMonth(reqYear); // 年的月份
			if (!ListUtils.isEmpty(list)) {
				for (CashFlow obj : list) { // 遍历查询出的现金流结果
					String date = DateUtil.convert(obj.getWtime(), DateUtil.format4);
					// 将每条记录的数据加到对应月份中
					if (map.get(date) == null) {
						map.put(date, new CashFlow());
					}
					CashFlow temp = map.get(date);
					temp.setPayables(temp.getPayables().add(obj.getPayables()));
					temp.setActualPayables(temp.getActualPayables().add(obj.getActualPayables()));
					temp.setActualReceivables(temp.getActualReceivables().add(obj.getActualReceivables()));
					temp.setReceivables(temp.getReceivables().add(obj.getReceivables()));
				}
			}

			Map<String, List<CashFlowRspDto>> resultMap = new HashMap<>();
			for (Map.Entry<String, CashFlow> entry : map.entrySet()) {
				String year = entry.getKey().substring(0, 4);
				if (!resultMap.containsKey(year)) {
					resultMap.put(year, new ArrayList<>());
				}

				CashFlowRspDto sp = null;
				if (null != entry.getValue()) {
					sp = new CashFlowRspDto(entry.getValue());
				} else {
					sp = new CashFlowRspDto();
				}
				sp.setYear(year);
				sp.setMonth(entry.getKey().substring(5));
				resultMap.get(year).add(sp);
			}

			JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(resultMap));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	@RequestMapping("/toCashFlowSheet")
	public String toCashFlowSheet() {
		JSONObject params = new JSONObject();
		String from = request.getParameter("from");
		if (from.equals("supplier")) {
			params.put("productId", request.getParameter("productId"));
			params.put("productName", request.getParameter("productName"));
			params.put("supplierId", request.getParameter("supplierId"));
			params.put("supplierName", request.getParameter("supplierName"));
			params.put("customerId", request.getParameter("customerId"));
			params.put("customerName", request.getParameter("customerName"));
			params.put("supplierTypeId", request.getParameter("supplierTypeId"));
			params.put("supplierTypeName", request.getParameter("supplierTypeName"));
			params.put("customerTypeId", request.getParameter("customerTypeId"));
			params.put("customerTypeName", request.getParameter("customerTypeName"));
			params.put("companyName", request.getParameter("companyName"));
			params.put("deptIds", request.getParameter("deptIds"));
			params.put("searchCustomerId", request.getParameter("searchCustomerId"));
			params.put("keyWord", request.getParameter("keyWord"));
		} else if (from.equals("customer")) {
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
		}
		request.setAttribute("params", params);
		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("from", from);
		request.setAttribute("title", request.getParameter("title"));
		return "/views/sheet/cashflowSheet";
	}
}
