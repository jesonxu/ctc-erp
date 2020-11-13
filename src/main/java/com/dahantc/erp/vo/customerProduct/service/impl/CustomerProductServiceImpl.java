package com.dahantc.erp.vo.customerProduct.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.YysType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;
import org.thymeleaf.util.SetUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customerProduct.SaveCustomerProductDto;
import com.dahantc.erp.enums.CostPriceType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customerProduct.dao.ICustomerProductDao;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.productType.entity.ProductType;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

@Service("customerProductService")
public class CustomerProductServiceImpl implements ICustomerProductService {
	private static Logger logger = LogManager.getLogger(CustomerProductServiceImpl.class);

	@Autowired
	private ICustomerProductDao customerProductDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public CustomerProduct read(Serializable id) throws ServiceException {
		try {
			return customerProductDao.read(id);
		} catch (Exception e) {
			logger.error("读取产品失败", e);
			throw new ServiceException("读取产品失败", e);
		}
	}

	@Override
	public boolean save(CustomerProduct entity) throws ServiceException {
		try {
			return customerProductDao.save(entity);
		} catch (Exception e) {
			logger.error("保存产品失败", e);
			throw new ServiceException("保存产品失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return customerProductDao.delete(id);
		} catch (Exception e) {
			logger.error("删除产品失败", e);
			throw new ServiceException("删除产品失败", e);
		}
	}

	@Override
	public boolean update(CustomerProduct entity) throws ServiceException {
		try {
			return customerProductDao.update(entity);
		} catch (Exception e) {
			logger.error("更新产品失败", e);
			throw new ServiceException("更新产品失败", e);
		}
	}

	@Override
	public BaseResponse<String> saveProduct(SaveCustomerProductDto dto) throws ServiceException {
		long start = System.currentTimeMillis();
		// 判断是新增还是修改，后台提交数据里没带productId说明是新增产品，带了说明是修改产品
		boolean needCreate = StringUtils.isBlank(dto.getProductId());
		try {
			CustomerProduct product = needCreate ? new CustomerProduct() : read(dto.getProductId());
			if (product == null) {
				logger.info("要修改的产品不存在，productId：" + dto.getProductId());
				return BaseResponse.error("要修改的产品不存在");
			}
			if (needCreate) {
				// 新增产品，校验产品名称
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("productName", Constants.ROP_EQ, dto.getProductName()));
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, dto.getCustomerId()));
				List<CustomerProduct> list = queryAllByFilter(filter);
				if (list != null && !list.isEmpty()) {
					logger.info("产品名称已存在：" + dto.getProductName());
					return BaseResponse.error("该客户已存在本产品：" + dto.getProductName());
				}
			} else {
				// 修改产品，如果改了产品名称，不能跟已有的产品重名
				if (!StringUtils.equals(product.getProductName(), dto.getProductName())) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("productName", Constants.ROP_EQ, dto.getProductName()));
					filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, dto.getCustomerId()));
					List<CustomerProduct> list = queryAllByFilter(filter);
					if (list != null && !list.isEmpty()) {
						logger.info("产品名称已存在：" + dto.getProductName());
						return BaseResponse.error("该客户已存在本产品：" + dto.getProductName());
					}
				}
			}
			// 从页面展示的运营商类型 转换成 真实的运营商类型（语音123，其他产品类型012）
			/*if (dto.getProductType() == 6) {
				dto.setYysType(YysType.toVoiceYysType(dto.getYysType()));
			}*/
			/* 校验账号，账号不能同时在 产品类型+运营商类型 相同的产品中存在，可以同时在 不同产品类型，或者 相同产品类型 但 运营商类型不同 的产品中存在 */
			String repeatResult = null;
			if (needCreate) {
				product.setCustomerId(dto.getCustomerId());
				repeatResult = checkProductAccount("", dto.getProductType(), dto.getAccount(), dto.getYysType());
			} else {
				product.setProductId(dto.getProductId());
				repeatResult = checkProductAccount(dto.getProductId(), dto.getProductType(), dto.getAccount(), dto.getYysType());
			}
			if (StringUtil.isNotBlank(repeatResult)) {
				return BaseResponse.error(repeatResult);
			}
			product.setYysType(dto.getYysType());
			product.setProductName(dto.getProductName());
			product.setProductType(dto.getProductType());
			product.setDirectConnect(dto.getDirectConnect());
			product.setAccount(dto.getAccount());
			product.setBillType(dto.getBillType());
			product.setBillCycle(dto.getBillCycle());
			product.setSettleType(dto.getSettleType());
			product.setBillTaskDay(dto.getBillTaskDay());
			product.setVoiceUnit(dto.getVoiceUnit());
			product.setSendDemo(dto.getSendDemo());
			if (dto.getFirstGenerateBillTime() != null) {
				product.setFirstGenerateBillTime(dto.getFirstGenerateBillTime());
			}
			boolean result = false;
			if (needCreate) {
				product.setOssUserId(dto.getOssUserId());
				result = save(product);
			} else {
				result = update(product);
			}
			if (!result) {
				return BaseResponse.error("保存产品失败");
			}
			logger.info("保存产品信息成功，耗时：" + (System.currentTimeMillis() - start) + "详情：" + product.toString());
			return BaseResponse.success("保存产品成功");
		} catch (Exception e) {
			logger.error("保存产品失败", e);
			return BaseResponse.error("保存产品失败");
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws ServiceException {
		try {
			return customerProductDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询产品数量失败", e);
			throw new ServiceException("查询产品数量失败", e);
		}
	}

	@Override
	public PageResult<CustomerProduct> findByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerProductDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询产品分页信息失败", e);
			throw new ServiceException("查询产品分页信息失败", e);
		}
	}

	@Override
	public List<CustomerProduct> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return customerProductDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询产品失败", e);
			throw new ServiceException("查询产品失败", e);
		}
	}

	@Override
	public List<CustomerProduct> queryAllByFilter(SearchFilter filter) throws ServiceException {
		try {
			return customerProductDao.queryAllByFilter(filter);
		} catch (Exception e) {
			logger.error("查询产品失败", e);
			throw new ServiceException("查询产品失败", e);
		}
	}

	@Override
	public List<CustomerProduct> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return customerProductDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询产品失败", e);
			throw new ServiceException("查询产品失败", e);
		}
	}

	/**
	 * 检测账号是否在其他同类型产品中存在
	 *
	 * @param productId
	 *            产品id
	 * @param productType
	 *            产品类型
	 * @param accountStr
	 *            以“|”分隔的账号
	 * @return 结果
	 */
	public String checkProductAccount(String productId, short productType, String accountStr, String yysType) {
		String result = null;
		if (StringUtils.isBlank(accountStr)) {
			return null;
		}
		logger.info("检测重复账号开始");
		long _start = System.currentTimeMillis();
		String repeatAccount = null;
		String repeatYysType = null;
		String productName = null;
		boolean repeat = false;
		try {
			String[] accounts = accountStr.split("\\|");
			String[] yysTypes = yysType.split(",");
			// 遍历每个账号
			for (String account : accounts) {
				if (repeat) {
					break;
				}
				// 查找同类型产品中疑似包含该账号的产品
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("productType", Constants.ROP_EQ, (int) productType));
				filter.getRules().add(new SearchRule("account", Constants.ROP_CN, account));
				// 排除产品本身
				if (StringUtils.isNotBlank(productId)) {
					filter.getRules().add(new SearchRule("productId", Constants.ROP_NE, productId));
				}
				List<CustomerProduct> products = queryAllByFilter(filter);
				if (ListUtils.isEmpty(products)) {
					continue;
				}
				// 遍历查到的每个产品，确认是否真的包含该账号
				for (CustomerProduct product : products) {
					String oldAccountStr = product.getAccount();
					List<String> oldAccounts = Arrays.asList(oldAccountStr.split("\\|"));
					// 产品类型相同，且有重复账号
					if (oldAccounts.contains(account)) {
						String oldYysType = product.getYysType();
						// 比较运营商类型
						if (yysType.equals(YysType.ALL.getValue() + "") || oldYysType.equals(YysType.ALL.getValue() + "") ) {
							// 如果 已存在的产品/新修改的产品 支持所有运营商类型，则会有运营商类型重复
							repeat = true;
							repeatYysType = yysType;
						} else {
							List<String> oldYysTypeList = new ArrayList<String>(Arrays.asList(oldYysType.split(",")));
							// 不能有重复的运营商
							for (String type : yysTypes) {
								if (oldYysTypeList.contains(type)) {
									repeat = true;
									repeatYysType = type;
								}
							}
						}
						if (repeat) {
							repeatAccount = account;
							productName = product.getProductName();
							break;
						}
					}
				}
			}
		} catch (ServiceException e) {
			logger.info("查询客户产品异常", e);
		}
		if (StringUtil.isNotBlank(repeatAccount)) {
			result = "检测到账号：" + repeatAccount + "，已存在于 相同产品类型 且 支持的运营商重复 的产品中：" + productName;
			logger.info(result);
		}
		logger.info("检测重复账号结束，耗时：" + (System.currentTimeMillis() - _start));
		return result;
	}

	@Override
	public boolean saveByBatch(List<CustomerProduct> objs) throws ServiceException {
		try {
			return customerProductDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean updateByBatch(List<CustomerProduct> objs) throws ServiceException {
		try {
			return customerProductDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取产品的账号在一段时间内的总成本
	 *
	 * @param startDate
	 *            开始时间（包含）
	 * @param endDate
	 *            结束时间（包含）
	 * @param loginNameList
	 *            产品下的账号
	 * @param productType
	 *            产品类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal queryCustomerProductCost(Timestamp startDate, Timestamp endDate, List<String> loginNameList, int productType, String yysType) {
		BigDecimal totalCost = new BigDecimal(0);
		ProductType productTypeO = null;
		String startTime = DateUtil.convert(startDate.getTime(), DateUtil.format2);
		String endTime = DateUtil.convert(endDate.getTime(), DateUtil.format2);
		if (ListUtils.isEmpty(loginNameList)) {
			logger.info("账号列表为空");
			return totalCost;
		}
		String loginNames = loginNameList.stream().map(loginName -> "'" + loginName + "'").collect(Collectors.joining(","));
		try {
			productTypeO = productTypeService.readOneByProperty("productTypeValue", productType);
			if (null == productTypeO) {
				logger.info("未查询到产品类型：" + productType);
				// return totalCost;
			}
		} catch (ServiceException e) {
			logger.error("查询产品类型异常", e);
		}
		try {
			// 成本是手动配置，用产品类型中的成本单价
			if (productTypeO != null && productTypeO.getCostPriceType() == CostPriceType.MANUAL.ordinal()) {
				String countSql = "select sum(successCount) from erp_customerproducttj where loginName in (" + loginNames + ") and statsDate >= '" + startTime
						+ "' and statsDate <= '" + endTime + "' and productType = " + productType;
				if (StringUtil.isNotBlank(yysType) && !(YysType.ALL.getValue() + "").equals(yysType)) {
					countSql += " and yysType in (" + yysType + ")";
				}
				logger.info("查产品账单成本sql：" + countSql);
				List<Object> result = (List<Object>) baseDao.selectSQL(countSql);
				if (!ListUtils.isEmpty(result) && result.get(0) != null) {
					totalCost = productTypeO.getCostPrice().multiply((BigDecimal) result.get(0));
				}

			} else {
				// 成本是平台同步
				String costSql = "select sum(successCount*costPrice) from erp_customerproducttj where loginName in (" + loginNames + ") and statsDate >= '"
						+ startTime + "' and statsDate <= '" + endTime + "' and productType = " + productType;
				if (StringUtil.isNotBlank(yysType) && !(YysType.ALL.getValue() + "").equals(yysType)) {
					costSql += " and yysType in (" + yysType + ")";
				}
				logger.info("查产品账单成本sql：" + costSql);
				List<Object> result = (List<Object>) baseDao.selectSQL(costSql);
				if (!ListUtils.isEmpty(result) && result.get(0) != null) {
					totalCost = (BigDecimal) result.get(0);
				}

			}
		} catch (Exception e) {
			logger.error("获取产品的总成本异常");
		}
		return totalCost;
	}

	@Override
	public PageResult<CustomerProduct> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerProductDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询产品信息分页信息失败", e);
			throw new ServiceException("查询产品信息分页信息失败", e);
		}
	}

	/**
	 * 根据客户列表获取所有客户产品的有效账号
	 *
	 * @param customerIdList
	 *            客户列表
	 * @return
	 */
	@Override
	public Set<String> queryLoginNameByCustomer(List<String> customerIdList) {
		Set<String> loginNameSet = new HashSet<>();
		if (CollectionUtils.isEmpty(customerIdList)) {
			return null;
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
		try {
			List<CustomerProduct> productList = queryAllByFilter(filter);
			if (ListUtils.isEmpty(productList)) {
				return null;
			}
			List<String> loginNameList = productList.stream().map(CustomerProduct::getAccount).filter(StringUtils::isNotBlank).collect(Collectors.toList());
			loginNameList.forEach(loginNames -> {
				loginNameSet.addAll(Arrays.asList(loginNames.split("\\|")));
			});
		} catch (Exception e) {
			logger.error("根据部门id列表获取客户产品账号异常", e);
			return null;
		}
		return loginNameSet;
	}

	@Override
	public Set<String> queryLoginNameByProduct(List<CustomerProduct> productList) {
		Set<String> loginNameSet = new HashSet<>();
		if (CollectionUtils.isEmpty(productList)) {
			return null;
		}
		List<String> loginNameList = productList.stream().map(CustomerProduct::getAccount).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		loginNameList.forEach(loginNames -> {
			loginNameSet.addAll(Arrays.asList(loginNames.split("\\|")));
		});
		return loginNameSet;
	}

	/**
	 * 客户的产品中 是否含有账号（多个产品，只要其中一个有账号就算）
	 *
	 * @param customerIds
	 *            客户id
	 * @return 客户产品还有账号的情况
	 */
	@Override
	public Map<String, Boolean> customerProductHasProduct(List<String> customerIds) {
		Map<String, Boolean> productAccountInfo = new HashMap<>();
		if (customerIds == null || customerIds.isEmpty()) {
			return productAccountInfo;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIds));
		List<CustomerProduct> customerProductList = null;
		try {
			customerProductList = queryAllByFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("批量查询客户的产品信息异常", e);
		}
		if (customerProductList != null && !customerProductList.isEmpty()) {
			Map<String, List<CustomerProduct>> productInfos = customerProductList.stream().collect(Collectors.groupingBy(CustomerProduct::getCustomerId));
			for (String customerId : productInfos.keySet()) {
				List<CustomerProduct> productList = productInfos.get(customerId);
				boolean hasAccount = false;
				if (productList != null && !productList.isEmpty()) {
					hasAccount = productList.stream().anyMatch(product -> StringUtil.isNotBlank(product.getAccount()));
				}
				productAccountInfo.put(customerId, hasAccount);
			}
		}
		return productAccountInfo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Map<String, Object>> getProductAndCustomerInfo() {
		logger.info("获取产品和客户信息开始");
		Map<String, Map<String, Object>> infoMap = new HashMap<>();
		String sql = "select p.productid, p.productname, c.customerid, c.companyname, c.ossuserid, p.productType, c.deptid, p.account from erp_customer_product p left join erp_customer c on p.customerid = c.customerid";
		try {
			List<Object[]> productLsit = (List<Object[]>) baseDao.selectSQL(sql);
			if (!CollectionUtils.isEmpty(productLsit)) {
				productLsit.forEach(product -> {
					Map<String, Object> info = new HashMap<>();
					info.put("productName", product[1]);
					info.put("customerId", product[2]);
					info.put("companyName", product[3]);
					info.put("ossUserId", product[4]);
					info.put("productType", product[5]);
					info.put("deptId", product[6]);
					info.put("loginName", product[7]);
					infoMap.put((String) product[0], info);
				});
			}
		} catch (BaseException e) {
			logger.error("", e);
		}
		return infoMap;
	}

	@Override
	public Map<String, String> findProductName(List<String> ids) {
		Map<String, String> productNames = new HashMap<>();
		if (ids == null || ids.isEmpty()) {
			return productNames;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, ids));
		List<CustomerProduct> productList = null;
		try {
			productList = queryAllByFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询异常", e);
		}
		if (productList != null && !productList.isEmpty()) {
			productNames.putAll(productList.stream().collect(Collectors.toMap(CustomerProduct::getProductId, CustomerProduct::getProductName, (o, n) -> n)));
		}
		return productNames;
	}

	@Override
	public List<String> getProductYysType(CustomerProduct product, boolean toNormal) {
		List<String> yysValueList = new ArrayList<>();
		String yysValue = product.getYysType();
		/*if (toNormal && product.getProductType() == 6) {
			yysValue = YysType.toNormalYysType(yysValue);
		}*/
		if (StringUtil.isNotBlank(yysValue)) {
			yysValueList = new ArrayList<>(Arrays.asList(yysValue.split(",")));
		}
		return yysValueList;
	}
}
