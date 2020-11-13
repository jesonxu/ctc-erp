package com.dahantc.erp.controller.customerConsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.dto.customer.CustomerDeptResp;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;

@Controller
@RequestMapping("/customerConsole")
public class CustomerConsoleAction extends BaseAction {

	public static final Logger logger = LoggerFactory.getLogger(CustomerConsoleAction.class);

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IDepartmentService departmentService;

	/**
	 * 跳转客户工作台
	 * 
	 * @return
	 */
	@RequestMapping("/toCustomerConsole")
	public String toCustomerConsole() {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 客户类型封装（类型名称，此类型客户数，此类型未处理流程数）
			List<CustomerTypeRespDto> customerTypeRespDtos = customerService.getCustomerType(onlineUser, null, null, null, null);
			request.setAttribute("customerTypes", customerTypeRespDtos);
			request.setAttribute("pagePermission", roleService.getPagePermission(onlineUser.getRoleId()));
		} catch (Exception e) {
			logger.error("查询客户信息异常", e);
		}
		return "/views/console/customerConsole";
	}

	/**
	 * 统计不同类型的客户的未处理流程的数量
	 *
	 * @param customerTypes
	 *            客户类型列表
	 * @param customers
	 *            客户信息列表
	 * @return 客户类型封装
	 */
	public List<CustomerTypeRespDto> countCustomerType(List<CustomerType> customerTypes, List<CustomerRespDto> customers) {
		// 统计结果
		List<CustomerTypeRespDto> countResults = new ArrayList<>();
		if (customerTypes != null && customerTypes.size() > 0) {
			// 遍历客户类型列表
			for (CustomerType customerType : customerTypes) {
				// 此类型id
				String customerTypeId = customerType.getCustomerTypeId();
				// 此类型客户数
				int customerCount = 0;
				// 此类型未处理的流程数
				long untakeFlowCount = 0L;
				if (customers != null && !customers.isEmpty()) {
					// 遍历客户信息列表，统计此类型的客户数和未处理流程数
					for (CustomerRespDto customerRespDto : customers) {
						if (customerTypeId.equals(customerRespDto.getCustomerTypeId())) {
							customerCount++;
							Long customerUntakeFlowCount = customerRespDto.getFlowEntCount();
							untakeFlowCount += (customerUntakeFlowCount == null ? 0 : customerUntakeFlowCount);
						}
					}
				}
				countResults.add(new CustomerTypeRespDto(customerType, untakeFlowCount, customerCount));
			}
		}
		return countResults;
	}

	/**
	 * 统计运营模块的待处理数
	 * 
	 * @param supplierId
	 * @param productId
	 * @param settleYearCount
	 * @param settleCount
	 * @param countList
	 */
	private void sumFlowCountByFlowType(String supplierId, String productId, Map<String, Long> yearCountMap, Map<String, Map<String, Long>> countMap,
			List<FlowEntDealCount> countList, int flowType) {
		Stream<FlowEntDealCount> stream = null;
		if (StringUtils.isNotBlank(productId)) {
			stream = countList.stream().filter(count -> StringUtils.equals(count.getProductId(), productId) && count.getFlowType() == flowType);
		} else if (StringUtils.isNotBlank(supplierId)) {
			stream = countList.stream().filter(count -> StringUtils.equals(count.getSupplierId(), supplierId) && count.getFlowType() == flowType);
		} else {
			stream = countList.stream().filter(count -> count.getEntityType() == EntityType.CUSTOMER.ordinal() && count.getFlowType() == flowType);
		}
		stream.forEach(count -> {
			Long lastCount = yearCountMap.get(count.getYear() + "");
			lastCount = lastCount == null ? 0L : lastCount;
			yearCountMap.put(count.getYear() + "", lastCount + count.getFlowEntCount());
			Map<String, Long> monthCount = countMap.get(count.getYear() + "");
			if (monthCount == null) {
				countMap.put(count.getYear() + "", new HashMap<>());
			}
			Long lastMCount = countMap.get(count.getYear() + "").get(count.getMonth() + "");
			lastMCount = lastMCount == null ? 0L : lastMCount;
			countMap.get(count.getYear() + "").put(count.getMonth() + "", lastMCount + count.getFlowEntCount());
		});
	}

	@RequestMapping("/reloadCustomer")
	public String reloadCustomer(@RequestParam(required = false) String deptIds, @RequestParam(required = false) String customerKeyWord) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 客户信息封装（客户名称，未处理流程数）
			List<CustomerRespDto> customerInfos = customerService.queryCustomers(onlineUser, deptIds, "", "", customerKeyWord);
			// 客户类型封装（类型名称，此类型客户数，此类型未处理流程数）
			List<CustomerTypeRespDto> customerTypeRespDtos = customerTypeService.countCustomerType(customerInfos);
			request.setAttribute("customers", customerInfos);
			request.setAttribute("customerTypes", customerTypeRespDtos);
			request.setAttribute("pagePermission", roleService.getPagePermission(onlineUser.getRoleId()));
		} catch (Exception e) {
			logger.error("查询客户信息异常", e);
		}
		return "/views/customer/customerInfo";
	}

	/**
	 * 刷新待审核数
	 *
	 * @param customerTypeId
	 *            客户类型id
	 * @param productId
	 *            产品id
	 * @param customerId
	 *            客户id
	 * @param searchDeptIds
	 *            查询的部门id
	 * @param openDeptId
	 *            点击展开的部门id
	 * @return 统计数据
	 */
	@ResponseBody
	@RequestMapping("/queryCustomerFlowEntCount")
	public JSONObject queryCustomerFlowEntCount(String customerTypeId, String productId, String customerId, String searchDeptIds, String openDeptId,
			String customerKeyWord) {
		JSONObject countResult = new JSONObject();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			Role role = roleService.read(onlineUser.getRoleId());
			String ossuserId = onlineUser.getUser().getOssUserId();
			String uDeptId = onlineUser.getUser().getDeptId();
			// 客户类型的统计数据
			List<CustomerTypeRespDto> customerTypeResp = customerService.getCustomerType(onlineUser, searchDeptIds, "", customerKeyWord, "");
			// 运营(年)
			Map<String, Long> operateYearCount = new HashMap<>();
			// 运营(月)
			Map<String, Map<String, Long>> operateCount = new HashMap<>();

			Map<String, Long> billYearCount = new HashMap<>();
			Map<String, Long> invoiceYearCount = new HashMap<>();
			Map<String, Long> writeOffYearCount = new HashMap<>();
			Map<String, Map<String, Long>> billCount = new HashMap<>();
			Map<String, Map<String, Long>> invoiceCount = new HashMap<>();
			Map<String, Map<String, Long>> writeOffCount = new HashMap<>();

			// 客户类别
			Map<String, Long> customerTypeCount = new HashMap<>();
			customerTypeResp.forEach(customerTypeRespDto -> {
				String cuid = customerTypeRespDto.getCustomerTypeId();
				Long count = customerTypeRespDto.getFlowEntCount();
				customerTypeCount.put(cuid, count);
			});
			// 查询部门或者客户的统计信息
			List<CustomerDeptResp> customerDeptResp = queryCustomerFlowCount(role, ossuserId, uDeptId, customerTypeId, openDeptId, searchDeptIds, customerId,
					customerKeyWord);
			// 客户
			Map<String, Integer> customerCount = new HashMap<>();
			// 部门
			Map<String, Integer> deptCount = new HashMap<>();
			if (!customerDeptResp.isEmpty()) {
				customerDeptResp.forEach(customerDept -> {
					Integer flowCount = customerDept.getFlowCount();
					if (customerDept.getIsDept() == 1) {
						// 部门
						deptCount.put(customerDept.getDeptId(), flowCount);
					} else {
						// 客户
						customerCount.put(customerDept.getCustomerId(), flowCount);
					}
				});
			}
			// 客户的统计
			List<FlowEntDealCount> countList = flowEntService.queryFlowEntDealCount(role.getRoleid(), ossuserId);
			// 获取当前用户的所有客户
			List<Customer> customers = customerService.readCustomers(onlineUser, "", "", "", customerKeyWord);
			List<String> customerIds = new ArrayList<String>();
			if (customers != null && !customers.isEmpty()) {
				customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
			}
			// 过滤出当前用户客户的流程
			List<String> finalCustomerIds = customerIds;
			countList = countList.stream().filter(flow -> finalCustomerIds.contains(flow.getSupplierId())).collect(Collectors.toList());
			// 产品
			Map<String, Long> productCount = queryProductFlowCount(customerId, countList);
			// 运营
			sumFlowCountByFlowType(customerId, productId, operateYearCount, operateCount, countList, FlowType.OPERATE.ordinal());
			// 账单、发票、销账
			sumFlowCountByFlowType(customerId, productId, billYearCount, billCount, countList, FlowType.BILL.ordinal());
			sumFlowCountByFlowType(customerId, productId, invoiceYearCount, invoiceCount, countList, FlowType.INVOICE.ordinal());
			sumFlowCountByFlowType(customerId, productId, writeOffYearCount, writeOffCount, countList, FlowType.WRITE_OFF.ordinal());

			countResult.put("customerCount", customerCount);
			countResult.put("customerTypeCount", customerTypeCount);
			countResult.put("productCount", productCount);
			countResult.put("operateYearCount", operateYearCount);
			countResult.put("operateCount", operateCount);
			countResult.put("billYearCount", billYearCount);
			countResult.put("billCount", billCount);
			countResult.put("invoiceYearCount", invoiceYearCount);
			countResult.put("invoiceCount", invoiceCount);
			countResult.put("writeOffYearCount", writeOffYearCount);
			countResult.put("writeOffCount", writeOffCount);
			countResult.put("deptCount", deptCount);
		} catch (ServiceException e) {
			logger.error("查询客户的流程统计数据异常", e);
		}
		return countResult;
	}

	/**
	 * 刷新控制台待审核数
	 *
	 * @param deptIds
	 *            查询的部门id
	 * @param keyWord
	 *            搜索的关键词
	 * @return 统计数据
	 */
	@ResponseBody
	@RequestMapping("/queryConsoleFlowEntCount")
	public JSONObject queryConsoleFlowEntCount(String deptIds, String keyWord) {
		JSONObject countResult = new JSONObject();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 客户类型的统计数据
			List<CustomerTypeRespDto> customerTypeResp = customerService.getCustomerType(onlineUser, deptIds, "", keyWord, "");
			// 客户类别
			Map<String, Long> customerTypeCount = new HashMap<>();
			customerTypeResp.forEach(customerTypeRespDto -> {
				String cuid = customerTypeRespDto.getCustomerTypeId();
				Long count = customerTypeRespDto.getFlowEntCount();
				customerTypeCount.put(cuid, count);
			});
			countResult.put("customerFlowCount", customerTypeCount.values().stream().mapToLong(Long::longValue).sum());
		} catch (Exception e) {
			logger.error("查询客户工作台的流程统计数据异常", e);
		}
		return countResult;
	}

	/**
	 * 统计 客户产品的待处理数
	 */
	private Map<String, Long> queryProductFlowCount(String customerId, List<FlowEntDealCount> countList) {
		Map<String, Long> productCount = new HashMap<>();
		List<CustomerProduct> productList = null;
		if (StringUtils.isNotBlank(customerId)) {
			SearchFilter productFilter = new SearchFilter();
			productFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
			try {
				productList = customerProductService.queryAllByFilter(productFilter);
			} catch (ServiceException e) {
				logger.error("根据客户id查询产品出现异常", e);
			}
		}
		if (!ListUtils.isEmpty(productList)) {
			// 统计数据
			Map<String, IntSummaryStatistics> countMap = null;
			if (countList != null && !countList.isEmpty()) {
				// 根据客户id过滤，再按产品id分类统计
				countMap = countList.stream().filter(c -> StringUtils.equals(c.getSupplierId(), customerId))
						.collect(Collectors.groupingBy(FlowEntDealCount::getProductId, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
			}
			for (CustomerProduct product : productList) {
				long flowCount = 0L;
				if (countMap != null) {
					IntSummaryStatistics statistics = countMap.get(product.getProductId());
					if (statistics != null) {
						flowCount = statistics.getSum();
					}
				}
				productCount.put(product.getProductId(), flowCount);
			}
		}
		return productCount;
	}

	/**
	 * 统计 客户产品的待处理数
	 */
	private List<CustomerDeptResp> queryCustomerFlowCount(Role role, String ossuserId, String uDeptId, String customerTypeId, String openDeptId, String deptIds,
			String openCustomerId, String customerKeyWord) {
		List<CustomerDeptResp> customerDeptResps = new ArrayList<>();
		try {
			Department department = null;
			if (StringUtil.isNotBlank(openCustomerId)) {
				// 最后点击的是客户id
				// 客户信息
				Customer customer = customerService.read(openCustomerId);
				// 客户对应的部门
				department = departmentService.read(customer.getDeptId());
			} else if (StringUtil.isNotBlank(openDeptId)) {
				// 最后点击的是部门
				department = departmentService.read(openDeptId);
			}
			while (department != null) {
				// 客户所在部门
				String tempOpenDeptId = department.getDeptid();
				List<CustomerDeptResp> customerDeptResp = customerService.queryCustomerAndDept(role, ossuserId, uDeptId, customerTypeId, tempOpenDeptId,
						deptIds, null, "", customerKeyWord, null);
				if (customerDeptResp != null && !customerDeptResp.isEmpty()) {
					customerDeptResps.addAll(customerDeptResp);
				}
				// 每一级循环查(只能查到)
				department = departmentService.read(department.getParentid());
				if (department == null) {
					// 查询最顶级的流程信息
					List<CustomerDeptResp> customerTopDept = customerService.queryCustomerAndDept(role, ossuserId, uDeptId, customerTypeId, null, "", deptIds,
							"", customerKeyWord, null);
					if (customerTopDept != null && !customerTopDept.isEmpty()) {
						customerDeptResps.addAll(customerTopDept);
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("获取流程统计数据异常", e);
		}
		return customerDeptResps;
	}
}
