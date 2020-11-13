package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.YysType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.jodconverter.office.utils.Lo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.ParamType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.royalty.entity.Royalty;
import com.dahantc.erp.vo.royalty.service.IRoyaltyService;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;
import org.springframework.util.CollectionUtils;

/**
 * 权益提成表定时任务
 * 根据昨天的成功数，计算出销售额、成本、毛利润，毛利润x提成比例 得到 权益提成
 */
@Component
public class BackTaskRoyaltyTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskRoyaltyTask.class);

	private static String CRON = "0 20 4 * * ?";

	private static String DELETE_THREE_DAY_BEFORE_ROYALTY_SQL = "DELETE FROM erp_royalty WHERE wtime >= ? AND wtime < ?";

	private int statistics_delay_days = 3;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IRoyaltyService royaltyService;

	@Autowired
	private IUnitPriceService unitPriceService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				try {
					// 执行任务
					logger.info("BackTask-Royalty-Task is running...");
					updateDelayDays();
					createRoyaltyTask();
				} catch (Exception e) {
					logger.error("BackTask-Royalty-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-Royalty-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	/**
	 * 更新统计延迟天数，用于覆盖统计
	 */
	private void updateDelayDays() {
		try {
			SearchFilter filterParameter = new SearchFilter();
			filterParameter.getRules().add(new SearchRule("paramkey", Constants.ROP_EQ, "statistics_delay_days"));
			filterParameter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, ParamType.SYSTEM_PARAMETER.ordinal()));
			List<Parameter> list = parameterService.findAllByCriteria(filterParameter);
			if (list != null && list.size() == 1) {
				int value = Integer.parseInt(list.get(0).getParamvalue());
				if (value > 0) {
					statistics_delay_days = value;
				}
			} else {
				logger.info("未查询到统计延迟天数取默认值3");
			}
		} catch (Exception e) {
			logger.error("获取查询到统计延迟天数异常：", e);
		}
	}

	public void createRoyaltyTask() {
		// 通道 { loginName -> { key -> success } }
		Map<String, Map<String, Long>> chanLoginNameMap = new HashMap<>();
		// 客户 { loginName -> { key -> [success, total] } }
		Map<String, Map<String, Long[]>> custLoginNameMap = new HashMap<>();
		Map<String, BigDecimal> customerUnitPriceMap = new HashMap<>();
		Map<String, BigDecimal> channelUnitPriceMap = new HashMap<>();
		Map<String, String> chanProductMap = new HashMap<>();
		Map<String, String> custProductMap = new HashMap<>();
		Map<String, String> chanProductTypeMap = new HashMap<>();
		Map<String, String> custProductTypeMap = new HashMap<>();
		Map<String, String> custProductYysTypeMap = new HashMap<>();
		Map<String, Royalty> royaltyMap = new HashMap<>();
		Map<String, String> customerDeptMap = new HashMap<>();
		Map<String, BigDecimal> royaltyRatioMap = new HashMap<>();
		// 从短信云同步的成本单价
		Map<String, Map<String, BigDecimal>> costPriceMap = new HashMap<>();
		try {
			// 查询所有非公共池客户及其产品
			List<CustomerProduct> customerProductList = customerProductService.queryAllByFilter(new SearchFilter());
			SearchFilter searchFilter = new SearchFilter();
			String customerTypeId = customerTypeService.getCustomerTypeIdByValue(CustomerTypeValue.PUBLIC.getCode());
			if (StringUtils.isNotBlank(customerTypeId)) {
				searchFilter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_NE, customerTypeId));
			}
			// 客户id -> 部门id map
			List<Customer> customerList = customerService.queryAllBySearchFilter(searchFilter);
			if (customerList != null && customerList.size() > 0) {
				customerDeptMap = customerList.stream().collect(Collectors.toMap(Customer::getCustomerId, Customer::getDeptId));
			}
			// 获取产品类型提成比例
			royaltyRatioMap = getRoyaltyRatio();
			// 遍历产品
			if (customerProductList != null && customerProductList.size() > 0) {
				Calendar calender = Calendar.getInstance();
				calender.setTime(DateUtil.getDayStart(new Date()));
				// 假设今天15日
				Date currentDateTime = calender.getTime();
				// 统计延迟天数，默认3天
				calender.add(Calendar.DATE, -statistics_delay_days);
				// 预先删除之前三天的数据，删除12,13,14，其中12,13有数据，14也就是昨天的数据还未统计
				try {
					logger.info("删除" + statistics_delay_days + "天之前的提成数据重新计算：" + DateUtil.convert(calender.getTime(), DateUtil.format2) + "~"
							+ DateUtil.convert(currentDateTime, DateUtil.format2));
					baseDao.executeSqlUpdte(DELETE_THREE_DAY_BEFORE_ROYALTY_SQL,
							new Object[] { new Timestamp(calender.getTime().getTime()), new Timestamp(currentDateTime.getTime()) },
							new Type[] { StandardBasicTypes.TIMESTAMP, StandardBasicTypes.TIMESTAMP });
				} catch (Exception e) {
					logger.error("删除" + statistics_delay_days + "天之前的提成数据异常：", e);
					return;
				}
				// 覆盖统计前三天的数据，统计12,13,14，相当于重新统计12,13的数据，第一次统计14的数据
				// 12,13,14的数据由13,14,15统计，calender当前是12日，加一天，
				calender.add(Calendar.DATE, 1);
				// <16日
				Date endTime = DateUtil.getNextDayStart(currentDateTime);
				for (Date royaltyDate = calender.getTime(); endTime.after(royaltyDate); royaltyDate = calender.getTime()) {
					Date yesterday = DateUtil.getYesterdayDate(royaltyDate);
					logger.info("提成数据重新统计：" + DateUtil.convert(yesterday, DateUtil.format2));
					// 创建昨天提成的基本信息 royaltyMap {productId -> Royalty}
					for (CustomerProduct customerProduct : customerProductList) {
						this.createRoyalty(customerProduct, royaltyMap, customerDeptMap, yesterday);
					}
					// 创建通道/客户产品信息
					this.createProductMap(chanProductMap, chanProductTypeMap, custProductMap, custProductTypeMap, customerProductList, custProductYysTypeMap);
					// 创建客户/通道单价信息
					if (!chanProductMap.isEmpty() && !custProductMap.isEmpty()) {
						this.createUnitPriceMap(custProductMap, customerUnitPriceMap, chanProductMap, channelUnitPriceMap, chanProductTypeMap,
								custProductTypeMap, DateUtil.getThisMonthFirst(yesterday), custProductYysTypeMap);
					}
					// 获取昨日统计明细表的记录，计算利润
					if (this.queryTj(chanLoginNameMap, custLoginNameMap, costPriceMap, yesterday, royaltyDate)) {
						// 保存产品的通道成本，然后更新统计明细表中非短信产品的平均成本单价
						Map<String, BigDecimal> updateCostPriceMap = new HashMap<>();
						for (CustomerProduct customerProduct : customerProductList) {
							// 计算利润和提成
							this.saleRoyalty(customerProduct, chanLoginNameMap, custLoginNameMap, royaltyMap, customerUnitPriceMap, channelUnitPriceMap,
									updateCostPriceMap, costPriceMap, royaltyRatioMap);
						}
						// 更新一些产品类型在 统计明细表 的综合成本单价
						this.updateCustCost(updateCostPriceMap, yesterday);
					}

					logger.info("销售提成信息批量更新：" + DateUtil.convert(royaltyDate, DateUtil.format2));
					List<Royalty> royaltyList = new ArrayList<>(royaltyMap.values());
					royaltyList = royaltyList.stream().filter(r -> r.getTotalCount() > 0).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(royaltyList)) {
						royaltyService.saveByBatch(royaltyList);
					}
					// 计算下一天
					calender.add(Calendar.DATE, 1);
					costPriceMap = new HashMap<>();
					chanLoginNameMap = new HashMap<>();
					custLoginNameMap = new HashMap<>();
					customerUnitPriceMap = new HashMap<>();
					channelUnitPriceMap = new HashMap<>();
					chanProductMap = new HashMap<>();
					custProductMap = new HashMap<>();
					chanProductTypeMap = new HashMap<>();
					custProductTypeMap = new HashMap<>();
					royaltyMap = new HashMap<>();
				}
			}
			logger.info("销售提成信息批量更新成功");
		} catch (Exception e) {
			logger.error("销售提成信息更新失败", e);
		} finally {
			if (!chanLoginNameMap.isEmpty()) {
				chanLoginNameMap.clear();
			}
			if (!custLoginNameMap.isEmpty()) {
				custLoginNameMap.clear();
			}
			if (!customerUnitPriceMap.isEmpty()) {
				customerUnitPriceMap.clear();
			}
			if (!channelUnitPriceMap.isEmpty()) {
				channelUnitPriceMap.clear();
			}
			if (!chanProductMap.isEmpty()) {
				chanProductMap.clear();
			}
			if (!custProductMap.isEmpty()) {
				custProductMap.clear();
			}
			if (!chanProductTypeMap.isEmpty()) {
				chanProductTypeMap.clear();
			}
			if (!custProductTypeMap.isEmpty()) {
				custProductTypeMap.clear();
			}
			if (!royaltyMap.isEmpty()) {
				royaltyMap.clear();
			}
			if (!customerDeptMap.isEmpty()) {
				customerDeptMap.clear();
			}
			if (!royaltyRatioMap.isEmpty()) {
				royaltyRatioMap.clear();
			}
		}
	}

	/**
	 * 获取客户/供应商产品的一些信息
	 *
	 * @param chanProductMap
	 *            存放 {供应商产品id -> 产品标识（通道Id）}
	 * @param chanProductTypeMap
	 *            存放 {供应商产品id -> 产品类型}
	 * @param custProductMap
	 *            存放 {客户产品id -> 账号}
	 * @param custProducTypetMap
	 *            存放 {客户产品id -> 产品类型}
	 * @param customerProductList
	 *            客户产品
	 * @param custProductYysTypeMap
	 *            存放 {客户产品id -> 运营商类型}
	 */
	private void createProductMap(Map<String, String> chanProductMap, Map<String, String> chanProductTypeMap, Map<String, String> custProductMap,
			Map<String, String> custProducTypetMap, List<CustomerProduct> customerProductList, Map<String, String> custProductYysTypeMap) {
		try {
			for (CustomerProduct custProduct : customerProductList) {
				if (StringUtils.isNotBlank(custProduct.getAccount())) {
					// 生成 客户产品id -> 产品的所有账号 map
					custProductMap.put(custProduct.getProductId(), custProduct.getAccount());
					// 生成 客户产品id -> 产品类型 map
					custProducTypetMap.put(custProduct.getProductId(), custProduct.getProductType() + "");
					// 生成 客户产品id -> 运营商类型 map
					custProductYysTypeMap.put(custProduct.getProductId(), custProduct.getYysType());
				}
			}
			List<Product> chanProductList = productService.queryAllBySearchFilter(new SearchFilter());
			if (chanProductList != null && chanProductList.size() > 0) {
				for (Product chanProduct : chanProductList) {
					if (StringUtils.isNotBlank(chanProduct.getProductMark())) {
						// 生成 供应商产品id -> 产品标识（通道Id） map
						chanProductMap.put(chanProduct.getProductId(), chanProduct.getProductMark());
						// 生成 供应商产品id -> 产品类型 map
						chanProductTypeMap.put(chanProduct.getProductId(), chanProduct.getProductType() + "");
					}
				}
			}
		} catch (Exception e) {
			logger.error("创建客户/供应商产品map信息失败", e);
		}
	}

	/**
	 * 计算提成
	 *
	 * @param customerProduct
	 *            要计算的产品
	 * @param chanLoginNameMap
	 *            {账号 -> {通道-国别号-产品类型 -> 成功数}}（通道侧）
	 * @param custLoginNameMap
	 *            {账号 -> {国别号-产品类型-运营商类型 -> [成功数, 总数]}}（客户侧）
	 * @param royaltyMap
	 *            存放昨日提成 {productId -> Royalty}
	 * @param customerUnitPriceMap
	 *            客户调价
	 * @param channelUnitPriceMap
	 *            通道调价（无综合成本单价的产品使用）
	 * @param updateCostPriceMap
	 *            综合成本单价，后面会用来更新一些产品类型的综合成本单价
	 * @param costPriceMap
	 *            综合成本单价
	 */
	private void saleRoyalty(
			CustomerProduct customerProduct,
			Map<String, Map<String, Long>> chanLoginNameMap,
			Map<String, Map<String, Long[]>> custLoginNameMap,
			Map<String, Royalty> royaltyMap,
			Map<String, BigDecimal> customerUnitPriceMap,
			Map<String, BigDecimal> channelUnitPriceMap,
			Map<String, BigDecimal> updateCostPriceMap,
			Map<String, Map<String, BigDecimal>> costPriceMap,
			Map<String, BigDecimal> royaltyRatioMap) {
		// 销售额
		BigDecimal customerAmount = new BigDecimal(0);
		// 成本（一些产品类型用综合成本单价算，剩下的产品类型用通道成本单价）
		BigDecimal channelCost = new BigDecimal(0);
		String productid = customerProduct.getProductId();
		String yysType = customerProduct.getYysType();
		List<String> yysTypes = null;
		if (!(YysType.ALL.getValue() + "").equals(yysType)) {
			yysTypes = Arrays.asList(yysType.split(","));
		}
		try {
			Royalty royalty = royaltyMap.get(productid);
			long totalCountSum = 0;
			long successCountSum = 0;
			// 遍历该产品下所有账号
			for (String loginName : customerProduct.getAccount().split("\\|")) {
				// 根据成功数计算销售额
				if (custLoginNameMap.containsKey(loginName)) {
					// 遍历该账号在 国别号-产品类型-运营商类型 的发成功数
					for (Entry<String, Long[]> entry : custLoginNameMap.get(loginName).entrySet()) {
						// entry.key 账号,国别号,产品类型,运营商类型
						// entry.value 成功数
						String newKey = entry.getKey();
						String[] keywords = entry.getKey().split(",");
						int productType = Integer.parseInt(keywords[2]);
						if (customerProduct.getProductType() != productType) {
							// 账号可以在多个不同类型的产品中存在，如果这条记录不是本产品的，则不用算
							continue;
						}
						// 检查是否本产品支持的运营商
						if (yysTypes == null){
							// 运营商类型为空是支持所有运营商，取单价不用加运营商类型
							// newKey = loginName + countryCode + productType
							newKey = keywords[0] + "," + keywords[1] + "," + keywords[2];
						} else if (!yysTypes.contains(keywords[3])) {
							// 不为空但不包含这条记录的运营商，则说明这条记录不是本产品的
							continue;
						}
						Long[] value = entry.getValue(); // 成功数, 总数
						successCountSum += value[0];
						totalCountSum += value[1];
						// 有账号在某个国别号某产品类型的单价
						if (customerUnitPriceMap.containsKey(newKey)) {
							// 客户产品单价
							BigDecimal custPrice = customerUnitPriceMap.get(newKey);
							customerAmount = customerAmount.add(custPrice.multiply(BigDecimal.valueOf(value[0])));
						} else {
							logger.info("账号：" + loginName + "，所在产品没有单价，产品id：" + customerProduct.getProductId());
						}
					}
				}
				// 该账号的综合成本单价
				Map<String, BigDecimal> loginNameCostPrice = costPriceMap.getOrDefault(loginName, new HashMap<String, BigDecimal>());
				// 根据在不同通道的成功数计算成本
				if (chanLoginNameMap.containsKey(loginName)) {
					// 遍历该账号在每条通道，各个国别号，不同产品类型的成功数
					for (Entry<String, Long> entry : chanLoginNameMap.get(loginName).entrySet()) {
						// entry.key 通道,国别号,产品类型
						// entry.value 成功数
						int productType = Integer.parseInt(entry.getKey().split(",")[2]);
						if (customerProduct.getProductType() != productType) {
							// 账号可以在多个不同类型的产品中存在，如果这条记录不是本产品的，则不用算
							continue;
						}
						if (loginNameCostPrice.containsKey(entry.getKey())) {
							// 用综合成本单价计算成本
							BigDecimal costPrice = loginNameCostPrice.get(entry.getKey());
							channelCost = channelCost.add(costPrice.multiply(BigDecimal.valueOf(entry.getValue())));
						} else if (channelUnitPriceMap.containsKey(entry.getKey())) {
							// 用调价记录的单价计算成本
							BigDecimal channelPrice = channelUnitPriceMap.get(entry.getKey());
							channelCost = channelCost.add(channelPrice.multiply(BigDecimal.valueOf(entry.getValue())));
							// 用于更新统计明细表中的综合成本单价
							String key = loginName + "," + entry.getKey();
							if (!updateCostPriceMap.containsKey(key)) {
								updateCostPriceMap.put(key, channelPrice);
							}
						} else {
							logger.info("账号：" + loginName + "，有发送量的通道没有单价，通道Id-国别号-产品类型：" + entry.getKey());
						}
					}
				}
			}
			if (totalCountSum == 0) {
				return;
			}
			royalty.setSuccessCount(royalty.getSuccessCount() + successCountSum);
			royalty.setTotalCount(royalty.getTotalCount() + totalCountSum);
			royalty.setProfit(royalty.getProfit().add(customerAmount.subtract(channelCost)));
			// 根据产品类型获取提成比例
			if (royaltyRatioMap.containsKey(customerProduct.getProductType() + "")) {
				BigDecimal royaltyRatio = royaltyRatioMap.get(customerProduct.getProductType() + "");
				royalty.setRoyalty(royalty.getProfit().multiply(royaltyRatio));
			}
		} catch (Exception e) {
			logger.error("计算权益提成异常，产品id：" + productid, e);
		}
	}

	/**
	 * 查询销售统计明细表，获取账号的发送量
	 *
	 * @param chanLoginNameMap
	 *            {账号 -> 该账号在 通道-国别号-产品类型 的成功数}（通道侧）
	 * @param custLoginNameMap
	 *            {账号 -> {国别号-产品类型-运营商类型 -> [发成功数,总数]}}（客户侧）
	 * @param costPriceMap
	 *            {账号 -> 该账号在 通道-国别号-产品类型 的成本单价}
	 * @param strarTime
	 *            统计开始时间 >=
	 * @param endTime
	 *            统计结束时间 <
	 * @return
	 */
	private boolean queryTj(
			Map<String, Map<String, Long>> chanLoginNameMap,
			Map<String, Map<String, Long[]>> custLoginNameMap,
			Map<String, Map<String, BigDecimal>> costPriceMap,
			Date strarTime, Date endTime) {
		try {
			long _start = System.currentTimeMillis();
			Map<String, Object> selMap = new HashMap<>();
			String hql = "select loginName,channelId,countryCode,productType,yysType,sum(successCount),sum(totalCount),costPrice from CustomerProductTj where"
					+ " statsDate >= :startDate and statsDate < :endDate group by loginName,channelId,countryCode,productType,yysType";
			selMap.put("startDate", strarTime);
			selMap.put("endDate", endTime);
			List<Object[]> sendCountList = baseDao.findByhql(hql, selMap, 0);
			logger.info("查询销售统计明细表，获取成功数完成，耗时：" + (System.currentTimeMillis() - _start));
			if (sendCountList != null && sendCountList.size() > 0) {
				for (Object[] objects : sendCountList) {
					// objects: 0账号, 1通道id, 2国别号, 3产品类型, 4运营商类型, 5成功数, 6总数, 7综合成本单价
					String loginName = (String) objects[0];
					String channelId = (String) objects[1];
					String countryCode = (String) objects[2];
					String productType = objects[3].toString();
					String yysType = objects[4].toString();
					long successCount = new BigInteger(objects[5].toString()).longValue();
					long totalCount = new BigInteger(objects[6].toString()).longValue();

					// 综合成本单价
					if (objects[7] != null && ((BigDecimal) objects[7]).compareTo(BigDecimal.ZERO) > 0) {
						// 该账号的成本单价map
						Map<String, BigDecimal> loginNameCostPriceMap = costPriceMap.getOrDefault(loginName, new HashMap<String, BigDecimal>());
						BigDecimal costPrice = (BigDecimal) objects[7];
						// 保存账号在 通道-国别号-产品类型 的 成本单价
						loginNameCostPriceMap.put(channelId + "," + countryCode + "," + productType, costPrice);
						// 保存此账号的成本单价
						costPriceMap.put(loginName, loginNameCostPriceMap);
					}

					Map<String, Long> chanCountMap = chanLoginNameMap.getOrDefault(loginName, new HashMap<String, Long>());
					// （通道侧）保存账号在 通道-国别号-产品类型 的 成功数
					chanCountMap.put(channelId + "," + countryCode + "," + productType, totalCount);
					// （通道侧）保存此账号的成功数
					chanLoginNameMap.put(loginName, chanCountMap);

					Map<String, Long[]> custCountMap = custLoginNameMap.getOrDefault(loginName, new HashMap<>());
					String key = loginName + "," + countryCode + "," + productType + "," + yysType;
					Long[] value = custCountMap.getOrDefault(key, new Long[]{0L, 0L});
					// （客户侧）该账号在 国别号-产品类型-运营商类型 的 成功数
					value[0] = value[0] + successCount;
					value[1] = value[1] + totalCount;
					custCountMap.put(key, value);
					// （客户侧）保存此账号的成功数
					custLoginNameMap.put(loginName, custCountMap);
				}
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("查询销售统计明细表异常", e);
			return false;
		}
	}

	/**
	 * 获取 客户产品/供应商通道 的单价
	 *
	 * @param custProductMap
	 *            存放 {客户产品id -> 账号}
	 * @param customerUnitPriceMap
	 *            存放 每个账号在各个国别号不同产品类型的单价
	 * @param chanProductMap
	 *            存放 {供应商产品id -> 产品标识（通道Id）}
	 * @param channelUnitPriceMap
	 *            存放 每条通道在各个国别号不同产品类型的单价
	 * @param chanProductTypeMap
	 *            存放 {供应商产品id -> 产品类型}
	 * @param custProductTypeMap
	 *            存放 {客户产品id -> 产品类型}
	 * @param custProductYysTypeMap
	 *            {客户产品id -> 运营商类型}
	 */
	private void createUnitPriceMap(Map<String, String> custProductMap, Map<String, BigDecimal> customerUnitPriceMap, Map<String, String> chanProductMap,
			Map<String, BigDecimal> channelUnitPriceMap, Map<String, String> chanProductTypeMap, Map<String, String> custProductTypeMap,
			Date currentMonthFirst, Map<String, String> custProductYysTypeMap) {
		try {
			// TODO 改为从调价记录查单价
			SearchFilter filterUnitPrice = new SearchFilter();
			filterUnitPrice.getRules().add(new SearchRule("wtime", Constants.ROP_EQ, new Timestamp(currentMonthFirst.getTime())));
			List<UnitPrice> unitPricelist = unitPriceService.queryAllBySearchFilter(filterUnitPrice);
			if (unitPricelist != null && unitPricelist.size() > 0) {
				for (UnitPrice unitPrice : unitPricelist) {
					if (unitPrice.getEntityType() == EntityType.CUSTOMER.ordinal()) {
						// 客户产品单价
						if (custProductMap.containsKey(unitPrice.getBasicsId())) {
							String[] loginNames = custProductMap.get(unitPrice.getBasicsId()).split("\\|");
							String productType = custProductTypeMap.get(unitPrice.getBasicsId());
							String yysType = custProductYysTypeMap.get(unitPrice.getBasicsId());
							String[] yysTypes = null;

							if (!(YysType.ALL.getValue() + "").equals(yysType)) {
								yysTypes = yysType.split(",");
							}
							// 产品的 账号，国别号，产品类型，运营商类型 的单价
							for (String loginName : loginNames) {
								if (null != yysTypes) {
									for (String type : yysTypes) {
										customerUnitPriceMap.put(loginName + "," + unitPrice.getCountryCode() + "," + productType + "," + type, unitPrice.getUnitPrice());
									}
								} else {
									customerUnitPriceMap.put(loginName + "," + unitPrice.getCountryCode() + "," + productType, unitPrice.getUnitPrice());
								}
							}
						}
					} else if (unitPrice.getEntityType() == EntityType.SUPPLIER.ordinal()) {
						// 供应商产品单价
						if (chanProductMap.containsKey(unitPrice.getBasicsId())) {
							String channelId = chanProductMap.get(unitPrice.getBasicsId());
							String productType = chanProductTypeMap.get(unitPrice.getBasicsId());
							// 该通道在各个国家不同产品类型的单价
							channelUnitPriceMap.put(channelId + "," + unitPrice.getCountryCode() + "," + productType, unitPrice.getUnitPrice());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("创建客户/通道单价map信息失败", e);
		}
	}

	/**
	 * 从系统参数表获取提成比例
	 *
	 * @return 提成比例map {productType -> 0.x}
	 */
	private Map<String, BigDecimal> getRoyaltyRatio() {
		Map<String, BigDecimal> royaltyMap = new HashMap<>();
		try {
			SearchFilter filterParameter = new SearchFilter();
			filterParameter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, ParamType.COMMISSION_RATION.ordinal()));
			List<Parameter> list = parameterService.findAllByCriteria(filterParameter);
			if (list != null) {
				for (Parameter parameter : list) {
					royaltyMap.put(parameter.getParamkey(), new BigDecimal(parameter.getParamvalue()));
				}
			}
		} catch (Exception e) {
			logger.error("获取提成信息失败", e);
		}
		return royaltyMap;
	}

	/**
	 * 创建昨天的提成map
	 *
	 * @param customerProduct
	 *            当前计算提成的产品
	 * @param royaltyMap
	 *            昨天提成的基本信息 {productId -> Royalty}
	 * @param customerDeptMap
	 *            客户部门map
	 */
	public void createRoyalty(CustomerProduct customerProduct, Map<String, Royalty> royaltyMap, Map<String, String> customerDeptMap, Date royaltyDateTime) {
		Royalty erpRoyalty = new Royalty();
		try {
			// 设置昨天提成map信息
			erpRoyalty.setOssuserid(customerProduct.getOssUserId());
			erpRoyalty.setEntityid(customerProduct.getCustomerId());
			erpRoyalty.setProductid(customerProduct.getProductId());
			erpRoyalty.setProductType(customerProduct.getProductType());
			erpRoyalty.setDeptId(customerDeptMap.getOrDefault(customerProduct.getCustomerId(), null));
			erpRoyalty.setWtime(new Timestamp(royaltyDateTime.getTime()));
			royaltyMap.put(customerProduct.getProductId(), erpRoyalty);
		} catch (Exception e) {
			logger.error("创建提成信息map异常", e);
		}
	}

	/**
	 * 更新统计明细表中部分产品类型的综合成本单价，剩下的产品类型的综合成本单价由短信云同步
	 *
	 * @param updateCostPrice
	 *            账号在每条通道上的单价
	 * @param tjDateTime
	 *            统计日期
	 * @throws BaseException
	 */
	private void updateCustCost(Map<String, BigDecimal> updateCostPrice, Date tjDateTime) throws BaseException {
		String upHql = " update erp_customerproducttj set costprice=? where loginname=? and channelid=? and countrycode=? and producttype=? and statsDate='"
				+ DateUtil.convert(tjDateTime, DateUtil.format2) + "'";
		for (Entry<String, BigDecimal> entry : updateCostPrice.entrySet()) {
			String[] params = entry.getKey().split(",");
			String productType = params[3];
			// 只更新部分产品
			String productTypeKey = productTypeService.getProductTypeKeyByValue(Integer.parseInt(productType));
			if (Constants.PRODUCT_TYPE_KEY_INTER_SMS.equals(productTypeKey) || Constants.PRODUCT_TYPE_KEY_MOBILE_AUTH.equals(productTypeKey)) {
				String loginName = params[0];
				String channelId = params[1];
				String countryCode = params[2];
				BigDecimal costPrice = entry.getValue().setScale(6, BigDecimal.ROUND_HALF_UP);
				baseDao.executeSqlUpdte(upHql, new Object[] { costPrice, loginName, channelId, countryCode, Integer.valueOf(productType) },
						new Type[] { StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.STRING, StandardBasicTypes.STRING, StandardBasicTypes.STRING,
								StandardBasicTypes.INTEGER });
			}
		}
	}
}
