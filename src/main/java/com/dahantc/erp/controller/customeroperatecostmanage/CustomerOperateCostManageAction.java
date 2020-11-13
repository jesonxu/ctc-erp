package com.dahantc.erp.controller.customeroperatecostmanage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.productoperatecost.ProductOperateCostDto;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.productType.entity.ProductType;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 运营成本管理
 *
 */
@Controller
@RequestMapping("/customerOperateCostManage")
public class CustomerOperateCostManageAction extends BaseAction {

	private static final String operareCostKeyPrefix = "unified_operate_single_cost_";

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IProductTypeService productTypeService;

	private static Logger logger = LogManager.getLogger(CustomerOperateCostManageAction.class);

	@RequestMapping("/toCustomerOperateCostManage")
	public String toCustomerOperateCostManage() {
		try {

			List<ProductType> productTypes = productTypeService.queryAllBySearchFilter(null);

			// 统一运营成本系统参数key
			List<String> paramkeyList = productTypes.stream().map(productType -> operareCostKeyPrefix + productType.getProductTypeValue()).collect(Collectors.toList());

			// key -> 产品类型名
			Map<String, String> productTypeMap = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
			productTypes.forEach(productType -> {
				productTypeMap.put(operareCostKeyPrefix + productType.getProductTypeValue(), productType.getProductTypeName());
			});

			// 按key查出所有统一运营成本
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("paramkey", Constants.ROP_IN, paramkeyList));
			filter.getOrders().add(new SearchOrder("entityid", Constants.ROP_DESC));
			List<Parameter> params = parameterService.findAllByCriteria(filter);

			if (params == null) {
				params = new ArrayList<>();
			}

			// key -> 产品类型的统一运营成本
			Map<String, BigDecimal> unifiedCostMap = params.stream()
					.filter(param -> StringUtils.isNotBlank(param.getParamvalue()) && NumberUtils.isParsable(param.getParamvalue()))
					.collect(Collectors.toMap(Parameter::getParamkey, param -> new BigDecimal(param.getParamvalue()), (k1, k2) -> k2));

			// 没有统一运营成本的产品类型补0
			productTypes.forEach(productType -> {
				unifiedCostMap.putIfAbsent(operareCostKeyPrefix + productType.getProductTypeValue(), BigDecimal.ZERO);
			});

			request.setAttribute("unifiedCostMap", unifiedCostMap);
			request.setAttribute("productTypeMap", productTypeMap);

		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "/views/manageConsole/customerOperateCostManage";
	}

	@ResponseBody
	@RequestMapping(value = "/readPages")
	public BaseResponse<PageResult<ProductOperateCostDto>> readPages(@RequestParam(required = false) String customerName, @RequestParam int limit,
			@RequestParam int page) {
		try {
			// 查数据权限下的部门
			List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());

			SearchFilter searchFilter = new SearchFilter();
			List<User> userList = null;

			if (!CollectionUtils.isEmpty(deptIdList)) {
				// 数据权限下有部门，查部门下的销售
				SearchFilter userFilter = new SearchFilter();
				userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
				userList = userService.queryAllBySearchFilter(userFilter);

				// 客户名称模糊搜索
				if (StringUtils.isNotBlank(customerName)) {
					searchFilter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, customerName));
					List<Customer> customerList = customerService.queryAllBySearchFilter(searchFilter);
					if (CollectionUtils.isEmpty(customerList)) {
						return BaseResponse.success(new PageResult<>());
					}
					List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
					searchFilter.getRules().clear();
					searchFilter.getOrders().clear();
					searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
					searchFilter.getRules()
							.add(new SearchRule("ossUserId", Constants.ROP_IN, userList.stream().map(User::getOssUserId).collect(Collectors.toList())));
				} else {
					searchFilter.getRules()
							.add(new SearchRule("ossUserId", Constants.ROP_IN, userList.stream().map(User::getOssUserId).collect(Collectors.toList())));
				}
			} else {
				// 只能看自己
				if (StringUtils.isNotBlank(customerName)) {
					// 客户名称模糊搜索
					searchFilter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, customerName));
					List<Customer> customerList = customerService.queryAllBySearchFilter(searchFilter);
					if (CollectionUtils.isEmpty(customerList)) {
						return BaseResponse.success(new PageResult<>());
					}
					List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
					searchFilter.getRules().clear();
					searchFilter.getOrders().clear();
					searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
					searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, getOnlineUser().getOssUserId()));
				} else {
					searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, getOnlineUser().getOssUserId()));
				}
			}

			searchFilter.getOrders().add(new SearchOrder("ossUserId", Constants.ROP_DESC));
			searchFilter.getOrders().add(new SearchOrder("customerId", Constants.ROP_DESC));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			// 按条件查出所有产品
			PageResult<CustomerProduct> pages = customerProductService.queryByPages(limit, page, searchFilter);
			if (CollectionUtils.isEmpty(pages.getData())) {
				return BaseResponse.success(new PageResult<>());
			}
			// 产品对应的销售
			Set<String> ossUserIdSet = pages.getData().stream().map(CustomerProduct::getOssUserId).collect(Collectors.toSet());
			if (CollectionUtils.isEmpty(userList)) {
				searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, new ArrayList<>(ossUserIdSet)));
				userList = userService.queryAllBySearchFilter(searchFilter);
			}
			Map<String, User> cacheUserMap = userList.stream().collect(Collectors.toMap(User::getOssUserId, user -> user, (key1, key2) -> key1));

			// 每个销售的部门，查部门名称
			Set<String> deptIdSet = userList.stream().map(User::getDeptId).collect(Collectors.toSet());
			searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, new ArrayList<>(deptIdSet)));
			Map<String, String> cacheDeptNameMap = departmentService.queryAllBySearchFilter(searchFilter).stream()
					.collect(Collectors.toMap(Department::getDeptid, Department::getDeptname, (key1, key2) -> key1));

			// 每个产品的客户，查客户名称
			Set<String> CustomerIdSet = pages.getData().stream().map(CustomerProduct::getCustomerId).collect(Collectors.toSet());
			searchFilter.getRules().clear();
			searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, new ArrayList<>(CustomerIdSet)));
			Map<String, String> customerNameMap = customerService.queryAllBySearchFilter(searchFilter).stream()
					.collect(Collectors.toMap(Customer::getCustomerId, Customer::getCompanyName, (key1, key2) -> key1));

			// 封装
			List<ProductOperateCostDto> dtoList = pages.getData().stream().map(product -> buildDto(product, cacheUserMap, cacheDeptNameMap, customerNameMap))
					.collect(Collectors.toList());
			PageResult<ProductOperateCostDto> pageResult = new PageResult<>();
			pageResult.setCode(pages.getCode());
			pageResult.setCount(pages.getCount());
			pageResult.setData(dtoList);
			pageResult.setMsg(pages.getMsg());
			pageResult.setTotalPages(pages.getTotalPages());
			return BaseResponse.success(pageResult);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("系统产品运营成本异常");
	}

	/**
	 * 封装产品运营成本dto
	 * 
	 * @param product
	 *            产品
	 * @param cacheUserMap
	 *            销售map {ossUserId -> User}
	 * @param cacheDeptNameMap
	 *            部门名称map {deptId -> 部门名称}
	 * @param customerNameMap
	 *            公司名称map {customerId -> 公司名称}
	 * @return
	 */
	public ProductOperateCostDto buildDto(CustomerProduct product, Map<String, User> cacheUserMap, Map<String, String> cacheDeptNameMap,
			Map<String, String> customerNameMap) {
		ProductOperateCostDto dto = new ProductOperateCostDto();
		dto.setRemark(product.getCostRemark());
		dto.setCustomerName(customerNameMap.get(product.getCustomerId()));
		dto.setDeptName(cacheDeptNameMap.get(cacheUserMap.get(product.getOssUserId()).getDeptId()));
		dto.setSettleType(SettleType.values()[product.getSettleType()].getDesc());
		dto.setProductOperateSingleCost(product.getProductOperateSingleCost());
		dto.setProductOperateFixedCost(product.getProductOperateFixedCost());
		dto.setBillAmountRatio(product.getBillMoneyRatio());
		dto.setBillGrossProfitRatio(product.getBillGrossProfitRatio());
		dto.setProductId(product.getProductId());
		dto.setProductName(product.getProductName());
		dto.setSaleName(cacheUserMap.get(product.getOssUserId()).getRealName());
		dto.setProductType(productTypeService.getProductTypeNameByValue(product.getProductType()));
		return dto;
	}

	@ResponseBody
	@RequestMapping(value = "/saveProductOperateCost")
	public BaseResponse<String> saveProductOperateCost(
			@RequestParam String productId,
			@RequestParam double productOperateFixedCost,
			@RequestParam double productOperateSingleCost,
			@RequestParam double billAmountRatio,
			@RequestParam double billGrossProfitRatio,
			@RequestParam(required = false) String costRemark) {
		long _startTime = System.currentTimeMillis();
		String msg = productId + "保存运营成本信息";
		try {
			CustomerProduct product = customerProductService.read(productId);
			if (product == null) {
				return BaseResponse.error("客户产品不存在");
			}
			BigDecimal pOperateFixedCost = product.getProductOperateFixedCost();
			if (pOperateFixedCost == null) {
				msg += "，添加产品每月固定费用【" + productOperateFixedCost + "】";
				product.setProductOperateFixedCost(new BigDecimal(productOperateFixedCost));
			} else if (pOperateFixedCost.doubleValue() != productOperateFixedCost) {
				msg += "，产品每月固定费用【" + pOperateFixedCost + "】修改为【" + productOperateFixedCost + "】";
				product.setProductOperateFixedCost(new BigDecimal(productOperateFixedCost));
			}
			BigDecimal pOperateSingleCost = product.getProductOperateSingleCost();
			if (pOperateFixedCost == null) {
				msg += "，添加产品每条产品运营费用【" + productOperateSingleCost + "】";
				product.setProductOperateSingleCost(new BigDecimal(productOperateSingleCost));
			} else if (pOperateSingleCost.doubleValue() != productOperateSingleCost) {
				msg += "，产品每条产品运营费用【" + pOperateSingleCost + "】修改为【" + productOperateSingleCost + "】";
				product.setProductOperateSingleCost(new BigDecimal(productOperateSingleCost));
			}
			BigDecimal pBillAmountRatio = product.getBillMoneyRatio();
			if (pBillAmountRatio == null) {
				msg += "，添加产品账单金额运营费用比例【" + billAmountRatio + "】";
				product.setBillMoneyRatio(new BigDecimal(billAmountRatio));
			} else if (pBillAmountRatio.doubleValue() != billAmountRatio) {
				msg += "，产品账单金额运营费用比例【" + pBillAmountRatio.toPlainString() + "】修改为【" + billAmountRatio + "】";
				product.setBillMoneyRatio(new BigDecimal(billAmountRatio));
			}
			BigDecimal pBillGrossProfitRatio = product.getBillGrossProfitRatio();
			if (pBillGrossProfitRatio == null) {
				msg += "，添加产品账单毛利润运营费用比例【" + billGrossProfitRatio + "】";
				product.setBillGrossProfitRatio(new BigDecimal(billGrossProfitRatio));
			} else if (pBillGrossProfitRatio.doubleValue() != billGrossProfitRatio) {
				msg += "，产品账单毛利润运营费用比例【" + pBillGrossProfitRatio.toPlainString() + "】修改为【" + billGrossProfitRatio + "】";
				product.setBillGrossProfitRatio(new BigDecimal(billGrossProfitRatio));
			}
			if (StringUtils.isNotBlank(costRemark)) {
				String pCostRemark = product.getCostRemark();
				if (StringUtils.isBlank(pCostRemark)) {
					msg += "，添加产品运营费用备注【" + costRemark + "】";
					product.setCostRemark(costRemark);
				} else if (StringUtils.equals(pCostRemark, costRemark)) {
					msg += "，产品运营费用备注【" + pCostRemark + "】修改为【" + costRemark + "】";
					product.setProductOperateSingleCost(new BigDecimal(productOperateSingleCost));
				}
			}
			boolean result = customerProductService.update(product);
			logger.info(msg + "，耗时：【" + (System.currentTimeMillis() - _startTime) + "】毫秒，" + (result ? "成功" : "失败"));
			if (result) {
				return BaseResponse.success("保存产品运营成本成功");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return BaseResponse.error("保存产品运营成本失败");
	}

	@ResponseBody
	@RequestMapping(value = "/saveUnifiedOperateSingleCost")
	public BaseResponse<String> saveUnifiedOperateSingleCost(@RequestParam String unifiedCosts) {
		long _startTime = System.currentTimeMillis();
		try {

			String[] arr = unifiedCosts.split(",");

			if (arr != null && arr.length > 0) {

				List<ProductType> productTypes = productTypeService.queryAllBySearchFilter(null);

				Map<String, BigDecimal> newValueMap = new HashMap<>();
				List<String> paramkeyList = new ArrayList<>();

				for (int i = 0; i < productTypes.size(); i++) {
					String pKey = operareCostKeyPrefix + productTypes.get(i).getProductTypeValue();
					if (StringUtils.isNotBlank(arr[i]) && NumberUtils.isParsable(arr[i])) {
						newValueMap.put(pKey, new BigDecimal(arr[i]));
					}
					paramkeyList.add(pKey);
				}

				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("paramkey", Constants.ROP_IN, paramkeyList));
				filter.getOrders().add(new SearchOrder("entityid", Constants.ROP_DESC));
				List<Parameter> params = parameterService.findAllByCriteria(filter);

				boolean result = false;
				StringBuffer msg = new StringBuffer();

				Map<String, Parameter> paramMap = new HashMap<>();
				if (params != null) {
					params.forEach(param -> paramMap.put(param.getParamkey(), param));
				}

				List<Parameter> saveData = new ArrayList<>();
				List<Parameter> updateData = new ArrayList<>();
				productTypes.forEach(productType -> {
					String pKey = operareCostKeyPrefix + productType.getProductTypeValue();
					if (paramMap.get(pKey) != null && newValueMap.get(pKey) != null
							&& new BigDecimal(paramMap.get(pKey).getParamvalue()).subtract(newValueMap.get(pKey)).signum() != 0) {

						msg.append(productType.getProductTypeName() + "统一运营成本【" + paramMap.get(pKey).getParamvalue() + "】修改为【" + newValueMap.get(pKey) + "】，");

						paramMap.get(pKey).setParamvalue(newValueMap.get(pKey) + "");
						updateData.add(paramMap.get(pKey));
					} else if (newValueMap.get(pKey) != null) {

						msg.append(productType.getProductTypeName() + "添加统一运营成本【" + newValueMap.get(pKey) + "】，");

						if (paramMap.get(pKey) != null) {
							paramMap.get(pKey).setParamvalue(newValueMap.get(pKey) + "");
							updateData.add(paramMap.get(pKey));
						} else {
							Parameter param = new Parameter();
							param.setParamkey(pKey);
							param.setParamvalue(newValueMap.get(pKey) + "");
							saveData.add(param);
						}
					}
				});

				result = baseDao.updateByBatch(updateData);
				if (result) {
					baseDao.saveByBatch(saveData);
				}
				logger.info(msg + (result ? "成功" : "失败") + "，耗时：【" + (System.currentTimeMillis() - _startTime) + "】毫秒");

				if (result) {
					return BaseResponse.success("保存产品运营成本成功");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("保存统一运营成本失败");
	}

	@ResponseBody
	@RequestMapping(value = "/getUnifiedOperateSingleCost")
	public BaseResponse<String> getUnifiedOperateSingleCost() {
		long _startTime = System.currentTimeMillis();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("paramkey", Constants.ROP_EQ, "unified_operate_single_cost"));
			filter.getOrders().add(new SearchOrder("entityid", Constants.ROP_DESC));
			List<Parameter> params = parameterService.findAllByCriteria(filter);
			if (!CollectionUtils.isEmpty(params)) {
				return BaseResponse.success(StringUtils.isBlank(params.get(0).getParamvalue()) ? "0.0000" : params.get(0).getParamvalue());
			}
			logger.info("获取统一运营成本，耗时：【" + (System.currentTimeMillis() - _startTime) + "】毫秒");
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return BaseResponse.error("");
	}

}
