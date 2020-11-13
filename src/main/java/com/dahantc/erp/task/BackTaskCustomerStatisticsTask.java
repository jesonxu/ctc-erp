package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.CostPriceType;
import com.dahantc.erp.enums.YysType;
import com.dahantc.erp.vo.productType.entity.ProductType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.DeepCloneUtil;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customerStatistics.entity.CustomerStatistics;
import com.dahantc.erp.vo.customerStatistics.service.ICustomerStatisticsService;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

/**
 * 后台定时任务，取出客户统计表erp_customerproducttj的数据，计算好成本、收入、毛利，放入客户统计表erp_customer_statistics中
 * 统计明细表erp_customerproducttj的数据是由短信云统计完之后，同步到erp的，短信云统计是在3点做的
 * 统计明细表中短信产品的成本单价是同步的，而非短信产品的成本单价是由计算权益提成的定时任务更新到统计表erp_customerproducttj中
 * 计算权益提成的定时任务在4点运行，因此，客户统计要在4点之后。
 */
@Component
public class BackTaskCustomerStatisticsTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskCustomerStatisticsTask.class);

	private static String CRON = "0 0 5 * * ?";

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerStatisticsService customerStatisticsService;

	@Autowired
	private IModifyPriceService modifyPriceService;

	@Autowired
	private IProductTypeService productTypeService;

	// 客户x产品 = {客户id，客户类型id，所属部门id，所属销售id，产品id, 产品类型，产品下的账号List}
	private ArrayList<HashMap<String, Object>> productInfoList = new ArrayList<>();

	// 统计开始时间
	public static Date startDate;

	// 统计结束时间
	public static Date endDate;

	// 产品类型为手动配置的成本单价
	public HashMap<Integer, BigDecimal> productTypeCostPrice = new HashMap<>();

	/**
	 * 执行统计转换操作的线程类 产品太多时，由每个线程转换一部分产品的统计
	 */
	class CustomerStatisticsTask implements Runnable {
		// 客户单价map
		private Map<String, BigDecimal> custPriceMap = new HashMap<>();
		private int id;
		// 本线程处理的部分产品
		private List<HashMap<String, Object>> subProductInfoList;
		// 查询统计明细表一天的统计数据，当天00:00:00 <= statsDate < 下一天00:00:00
		private Date tjDate;
		private Date nextDate;
		// 统计开始和结束时间
		private Date startDate;
		private Date endDate;

		public CustomerStatisticsTask(int id, ArrayList<HashMap<String, Object>> productInfoList, Date startDate, Date endDate) {
			this.id = id;
			this.subProductInfoList = DeepCloneUtil.clone(productInfoList);
			productInfoList.clear();
			this.startDate = new Date(startDate.getTime());
			this.endDate = new Date(endDate.getTime());
			this.tjDate = new Date(startDate.getTime());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			List<CustomerStatistics> subStatisticsList = new ArrayList<>();
			// 遍历统计每一天
			boolean isUpdate = false;
			int interProductType = productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS);
			while (tjDate.getTime() < endDate.getTime()) {
				String tjDateStr = DateUtil.convert(tjDate, DateUtil.format1);
				try {
					long start = System.currentTimeMillis();
					logger.info("线程" + id + "转换 " + DateUtil.convert(tjDate, DateUtil.format1) + " 的统计数据开始，产品数：" + subProductInfoList.size());
					// 当天0点到下一天0点
					nextDate = DateUtil.getDaysOfDistance(tjDate, 1);
					if (nextDate.getTime() >= endDate.getTime() || (DateUtil.getThisMonthFirst(tjDate).getTime() != tjDate.getTime()
							&& nextDate.getTime() == DateUtil.getNextMonthFirst(tjDate).getTime())) {
						isUpdate = true;
					}
					logger.info("线程" + id + "转换起止时间：" + DateUtil.convert(startDate, DateUtil.format2) + " ~ " + DateUtil.convert(endDate, DateUtil.format2));
					logger.info("线程" + id + "当前转换时间：" + DateUtil.convert(tjDate, DateUtil.format2) + " ~ " + DateUtil.convert(nextDate, DateUtil.format2));
					HashMap<String, Object> params;
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(tjDate);

					// 遍历所有产品，将产品在每条通道的统计数据累计，放到当天该产品的统计对象中
					// productInfo：{客户id，客户类型id，所属部门id，所属销售id，产品id,
					// 产品类型，产品下的账号List}
					final String staticWhereHql = " where businessType = " + BusinessType.YTX.ordinal()
							+ " and loginName in :loginNameList and productType = :productType" + " and statsDate >= :startDate and statsDate < :nextDate";
					for (HashMap<String, Object> productInfo : subProductInfoList) {
						String productId = (String) productInfo.get("productId");
						logger.info("线程" + id + "当前转换产品id：" + productId);
						// 该产品当天的统计数据对象
						CustomerStatistics statistics = new CustomerStatistics();
						statistics.setProductId(productId);
						statistics.setStatsDate(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format1)));
						statistics.setStatsYearMonth(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format4) + "-01"));
						statistics.setStatsYear(java.sql.Date.valueOf(calendar.get(Calendar.YEAR) + "-01-01"));
						statistics.setCustomerId((String) productInfo.get("customerId"));
						statistics.setCustomerTypeId((String) productInfo.get("customerTypeId"));
						statistics.setDeptId((String) productInfo.get("deptId"));
						statistics.setSaleUserId((String) productInfo.get("saleUserId"));
						statistics.setBusinessType(BusinessType.YTX.ordinal());
						int productType = (Integer) productInfo.get("productType");
						statistics.setProductType(productType);
						// 产品下的所有账号
						List<String> loginNameList = (ArrayList<String>) productInfo.get("loginNameList");
						logger.info("产品id：" + productId + " 的账号数：" + loginNameList.size());
						if (loginNameList.size() > 0) {
							statistics.setLoginName(String.join(",", loginNameList));
							params = new HashMap<>();
							params.put("loginNameList", loginNameList);
							params.put("productType", productType);
							params.put("startDate", tjDate);
							params.put("nextDate", nextDate);

							// 不支持所有运营商的产品，统计时才需要指定运营商类型
							String newWhereHql = staticWhereHql;
							if (productInfo.containsKey("yysTypeList")) {
								newWhereHql += " and yysType in (:yysTypeList)";
								params.put("yysTypeList", productInfo.get("yysTypeList"));
							}

							// 查询产品下所有账号当天在每条通道上的统计记录，按通道id、国别号分组
							List<Object[]> productTj = null;
							try {
								String hql = "select loginName,channelId,countryCode,sum(successCount),sum(failCount),sum(totalCount),costPrice"
										+ " from CustomerProductTj " + newWhereHql + " group by loginName,channelId,countryCode";
								productTj = baseDao.findByhql(hql, params, 0);
							} catch (Exception e) {
								logger.info("线程" + id + "查询产品id：" + productId + " 的账号的统计数据异常", e);
							}
							if (productType == interProductType) {
								getCustomerInterPrice(productId, tjDate, nextDate, custPriceMap);
							} else {
								params.put("startDate", DateUtil.getThisMonthFirst(tjDate));
								params.put("nextDate", DateUtil.getNextMonthFirst(tjDate));
								getCustomerPrice(params, productId, DateUtil.getThisMonthFirst(tjDate), tjDate, custPriceMap);
							}
							if (productTj != null && productTj.size() > 0) {
								logger.info("产品id：" + productId + " 在" + tjDateStr + " 在" + productTj.size() + " 条通道上有发送量");
								// 在一条通道上某个国别号的统计 tj ->
								// [账号，通道Id，国别号，成功数，失败数，总数，成本]
								for (Object[] tj : productTj) {
									// 客户产品单价key = 产品id + 年月 + 国别号
									String loginName = (String) tj[0];
									String channelId = (String) tj[1];
									String countryCode = (String) tj[2];
									long successCount = (Long) tj[3];
									long failCount = (Long) tj[4];
									long totalCount = (Long) tj[5];
									BigDecimal costPrice = null;
									if (productTypeCostPrice.containsKey(productType)) {
										costPrice = productTypeCostPrice.get(productType);
									} else {
										costPrice = (BigDecimal) tj[6];
									}
									statistics.addTotalCount(totalCount);
									// 成功数计入总成功数
									statistics.addTotalSuccessCount(successCount);
									statistics.addFailCount(failCount);
									if (costPrice.compareTo(BigDecimal.ZERO) == 0) {
										logger.info("成本单价缺失，账号-通道id-日期-国别号-产品类型：" + loginName + "-" + channelId + "-"
												+ DateUtil.convert(tjDate, DateUtil.format1) + "-" + countryCode + "-" + productType + "，成功数：" + successCount);
										// 累计无成本单价的成功数
										statistics.addNoCostPriceCount(successCount);
									} else {
										BigDecimal cost = new BigDecimal(successCount).multiply(costPrice);
										statistics.addCost(cost);
									}
									// 获取客户产品单价，计算销售额
									String cusKey = productId + "," + DateUtil.convert(tjDate, DateUtil.format1) + "," + countryCode;
									BigDecimal cusPrice = custPriceMap.get(cusKey);
									if (cusPrice == null) {
										logger.info("客户产品单价缺失，产品id-日期-国别号-产品类型：" + cusKey + "-" + productType + "，成功数：" + successCount);
										// 累计无客户单价的成功数
										statistics.addNoCustPriceCount(successCount);
									} else {
										BigDecimal receivables = new BigDecimal(successCount).multiply(cusPrice);
										statistics.addReceivables(receivables);
										// 成功数计入实际成功数
										statistics.addSuccessCount(successCount);
										statistics.setCustPrice(cusPrice);
									}
								}
								productTj.clear();
								productTj = null;
							} else {
								logger.info("产品id：" + productId + " 在" + tjDateStr + "的统计数据为空");
							}
						}
						// 成功数为0则不写这条记录到表中
						if (statistics.getTotalSuccessCount() == 0) {
							logger.info("产品id：" + productId + " 在" + tjDateStr + "的总成功数为0，不保存统计结果");
							continue;
						}
						// 计算毛利润
						statistics.setGrossProfit(statistics.getReceivables().subtract(statistics.getCost()));
						// 计算平均销售单价 = 销售额 / 有客户单价的发送量
						if (interProductType == productType) {
							if (statistics.getSuccessCount() > 0) {
								statistics.setCustPrice(statistics.getReceivables().divide(BigDecimal.valueOf(statistics.getSuccessCount()), 6, BigDecimal.ROUND_HALF_UP));
							} else {
								statistics.setCustPrice(BigDecimal.ZERO);
							}
						}
						// 计算平均成本单价 = 成本 / 有成本单价的发送量
						if (statistics.getTotalSuccessCount() - statistics.getNoCostPriceCount() > 0) {
							statistics.setCostPrice(statistics.getCost().divide(BigDecimal.valueOf(statistics.getTotalSuccessCount() - statistics.getNoCostPriceCount()), 6, BigDecimal.ROUND_HALF_UP));
						}
						// 金额保留2位小数
						statistics.setScale2();
						// 更新老数据
						if (isUpdate && productType != interProductType) {
							updateOldCustomerStatistics(subStatisticsList, productId, statistics);
							isUpdate = false;
						}
						subStatisticsList.add(statistics);
					}
					// 保存当天的数据
					if (subStatisticsList.size() > 0) {
						try {
							if (customerStatisticsService.saveByBatch(subStatisticsList)) {
								logger.info("线程" + id + "批量保存" + tjDateStr + "的统计数据成功，本批次" + subStatisticsList.size() + "条");
							} else {
								logger.info("线程" + id + "批量保存" + tjDateStr + "的统计数据失败，本批次" + subStatisticsList.size() + "条");
							}
						} catch (Exception e) {
							logger.info("线程" + id + "批量保存" + tjDateStr + "的统计数据异常，清空本批次的数据继续执行任务", e);
						} finally {
							subStatisticsList.clear();
						}
					} else {
						logger.info("线程" + id + "在" + tjDateStr + "转换的统计数据为空");
					}
					logger.info("线程" + id + "转换 " + tjDateStr + " 的统计数据结束，产品数：" + subProductInfoList.size() + "，耗时：" + (System.currentTimeMillis() - start));
				} catch (Exception e) {
					logger.info("线程" + id + "转换 " + tjDateStr + " 的统计数据异常", e);
				} finally {
					// 转换下一天
					tjDate = nextDate;
					logger.info("线程" + id + "更新统计日期为：" + DateUtil.convert(tjDate, DateUtil.format1));
					custPriceMap.clear();
				}
			}
			if (subProductInfoList != null) {
				subProductInfoList.clear();
				subProductInfoList = null;
			}
			if (subStatisticsList != null) {
				subStatisticsList.clear();
				subStatisticsList = null;
			}
		}

		private void updateOldCustomerStatistics(List<CustomerStatistics> subStatisticsList, String productId, CustomerStatistics statistics)
				throws ServiceException, BaseException {
			SearchFilter sFilter = new SearchFilter();
			sFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			sFilter.getRules().add(new SearchRule("statsDate", Constants.ROP_GE, DateUtil.getThisMonthFirst(tjDate)));
			sFilter.getRules().add(new SearchRule("statsDate", Constants.ROP_LT, DateUtil.getNextMonthFirst(tjDate)));
			List<CustomerStatistics> list = customerStatisticsService.queryAllBySearchFilter(sFilter);
			String cusKey = productId + "," + DateUtil.convert(tjDate, DateUtil.format4) + "," + Constants.CHINA_COUNTRY_CODE;
			BigDecimal avgPrice = custPriceMap.get(cusKey);
			if (!CollectionUtils.isEmpty(list) && avgPrice != null && avgPrice.signum() > 0) {
				for (CustomerStatistics customerStatistics : subStatisticsList) {
					customerStatistics.setReceivables(new BigDecimal(customerStatistics.getSuccessCount()).multiply(avgPrice));
					customerStatistics.setGrossProfit(statistics.getReceivables().subtract(statistics.getCost()));
				}
				baseDao.updateByBatch(list);
			}
		}
	}

	/**
	 * 根据当月发送量确定统计当天的单价
	 * 
	 * @param params
	 *            查询参数
	 * @param productId
	 *            客户产品
	 * @param startDate
	 *            统计月初
	 * @param tjDate
	 *            统计当天
	 * @param custPriceMap
	 *            客户单价map
	 * @throws Exception
	 */
	private void getCustomerPrice(Map<String, Object> params, String productId, Date startDate, Date tjDate, Map<String, BigDecimal> custPriceMap)
			throws Exception {
		String whereHql = "where businessType=" + BusinessType.YTX.ordinal()
				+ " and loginName in :loginNameList and productType = :productType and countryCode = '+86'"
				+ " and statsDate >= :startDate and statsDate < :nextDate";
		if (params.containsKey("yysTypeList")) {
			whereHql += " and yysType in (:yysTypeList)";
		}
		String hql = "select sum(successCount), statsDate from CustomerProductTj " + whereHql + " group by statsDate";
		Map<String, Object> paramMap = new HashMap<>(params);
		List<Object[]> resultList = baseDao.findByhql(hql, paramMap, 0);
		if (!CollectionUtils.isEmpty(resultList)) {
			// 每天发送量 { date -> successCount }
			Map<Date, Long> successCountMap = resultList.stream().collect(Collectors.toMap(obj -> (Date) obj[1], obj -> ((Number) obj[0]).longValue()));
			custPriceMap.put(productId + "," + DateUtil.convert(tjDate, DateUtil.format1) + "," + Constants.CHINA_COUNTRY_CODE,
					modifyPriceService.getDatePrice(successCountMap, productId, startDate, tjDate));
		}
	}

	/**
	 * 获取统计当天的国际短信价格
	 * 
	 * @param productId
	 *            产品
	 * @param startDate
	 *            统计当天
	 * @param endDate
	 *            下一天
	 * @param custPriceMap
	 *            填充的价格map
	 * @throws Exception
	 */
	private void getCustomerInterPrice(String productId, Date startDate, Date endDate, Map<String, BigDecimal> custPriceMap) throws Exception {
		Map<String, Double> interCountryPrice = modifyPriceService.getInterCountryPrice(productId, startDate, endDate);
		if (!CollectionUtils.isEmpty(interCountryPrice)) {
			interCountryPrice.entrySet().forEach(entry -> custPriceMap
					.put(productId + "," + DateUtil.convert(startDate, DateUtil.format1) + "," + entry.getKey(), BigDecimal.valueOf(entry.getValue())));
		}
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				try {
					// 执行任务
					logger.info("BackTask-CustomerStatistics-Task is running...");
					productInfoList.clear();
					customerStatisticsTask();
				} catch (Exception e) {
					logger.error("BackTask-CustomerStatistics-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-CustomerStatistics-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	/**
	 * 获取统计明细表数据，计算好之后保存到新统计表
	 */
	public void customerStatisticsTask() {
		// 删除结束日期 = 新统计表最后一天
		endDate = getLastStatisticsDate();
		if (endDate == null) {
			// 新表为空，是第一次转换，转换全部，删除结束日期 = 昨天23:59:59
			endDate = DateUtil.getPreviousEndDateTime();
			// 删除/转换开始日期 = 统计明细表最早那天的00:00:00
			startDate = DateUtil.getDateStartDateTime(getFirstTjDate());
		} else {
			// 删除结束日期 = 最后一天的23:59:59
			endDate = DateUtil.getDateEndDateTime(endDate);
			// 删除/转换开始日期 = 删除结束日期 - 延迟天数的00:00:00
			startDate = DateUtil.getDateStartDateTime(DateUtil.getDaysOfDistance(endDate, Constants.STATISTICS_DELAY_DAYS * -1));
			// 开始日期是在当月，则改为1号，即把从1号到昨天的数据都重新统计；
			// 开始日期是上月的话保持不变
			Calendar cal = Calendar.getInstance();
			int nowMonth = cal.get(Calendar.MONTH);
			cal.setTime(startDate);
			int startMonth = cal.get(Calendar.MONTH);
			startDate = nowMonth == startMonth ? DateUtil.getThisMonthFirst(startDate) : startDate;
		}
		// 删除新表历史数据
		delOldData(startDate, endDate);
		// 转换结束日期 = 昨天23:59:59
		endDate = DateUtil.getPreviousEndDateTime();
		logger.info("转换客户统计数据开始：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1));
		// 获取客户x产品的信息
		getCustomerAndProductInfo(productInfoList);
		// 获取成本单价
		getProductTypeCostPrice();
		// 多线程处理，每个线程处理50个产品，最大16线程，超出则重新计算每个线程的产品数
		if (productInfoList.size() > 0) {
			int maxPage = 16;
			int page = 1;
			int pageSize = 50;
			int pages = (int) (Math.ceil(1.0 * productInfoList.size() / pageSize)); // 向上
			if (pages > maxPage) {
				pageSize = (int) (Math.ceil(1.0 * productInfoList.size() / maxPage)); // 重新计算每个线程处理的产品数
				pages = (int) (Math.ceil(1.0 * productInfoList.size() / pageSize)); // 重新计算线程数
			}
			int pageStart = 0;
			int pageEnd = 0;
			while (page <= pages) {
				pageStart = (page - 1) * pageSize;
				pageEnd = Math.min(page * pageSize, productInfoList.size());
				ArrayList<HashMap<String, Object>> subList = new ArrayList<>(productInfoList.subList(pageStart, pageEnd));
				if (subList.size() > 0) {
					new Thread(new CustomerStatisticsTask(page, subList, startDate, endDate)).start();
				}
				page++;
			}
		}
		productInfoList.clear();
	}

	/**
	 * 获取所有产品的关键信息，包括所属客户的关键信息 {客户id，客户类型id，所属部门id，所属销售id，产品id, 产品类型，产品下的账号List}
	 *
	 * @param cusAndProInfoList
	 *            要填充的map
	 */
	@SuppressWarnings("unchecked")
	private void getCustomerAndProductInfo(ArrayList<HashMap<String, Object>> cusAndProInfoList) {
		cusAndProInfoList.clear();
		try {
			logger.info("查询客户x产品信息开始");
			long _start = System.currentTimeMillis();
			String sql = "select c.customerid, c.customertypeid, c.deptid, c.ossuserid, p.productid, p.producttype, p.account, p.yystype"
					+ " from erp_customer c right join erp_customer_product p on c.customerid = p.customerid";
			List<Object[]> productList = (List<Object[]>) baseDao.selectSQL(sql);
			if (productList != null && productList.size() > 0) {
				for (Object[] product : productList) {
					HashMap<String, Object> productInfo = new HashMap<>();
					productInfo.put("customerId", product[0]);
					productInfo.put("customerTypeId", product[1]);
					productInfo.put("deptId", product[2]);
					productInfo.put("saleUserId", product[3]);
					productInfo.put("productId", product[4]);
					productInfo.put("productType", product[5]);
					// 账号列表
					String account = String.valueOf(product[6]);
					List<String> loginNameList = StringUtil.isBlank(account) ? new ArrayList<String>()
							: new ArrayList<String>(Arrays.asList(account.split("\\|")));
					loginNameList = loginNameList.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toList());
					productInfo.put("loginNameList", loginNameList);

					// 支持的运营商类型
					String yysType = String.valueOf(product[7]);
					// 不是支持运营商所有运营商，那么只查指定运营商的统计
					if (StringUtil.isNotBlank(yysType) && !yysType.equals(YysType.ALL.getValue() + "")) {
						List<String> yysTypeList = new ArrayList<>(Arrays.asList(yysType.split(",")));
						productInfo.put("yysTypeList", yysTypeList.stream().map(Integer::parseInt).collect(Collectors.toList()));
					}

					cusAndProInfoList.add(productInfo);
				}
				productList.clear();
				productList = null;
			}
			logger.info("查询客户x产品信息结束，查询到" + cusAndProInfoList.size() + "条记录，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询客户x产品信息异常", e);
		}
	}

	/**
	 * 删除客户统计表（新表）的历史数据，以实现覆盖统计
	 *
	 * @param startDate
	 *            开始时间 yyyy-MM-dd 00:00:00
	 * @param endDate
	 *            结束时间 yyyy-MM-dd 23:59:59
	 */
	private void delOldData(Date startDate, Date endDate) {
		String sql = "delete from erp_customer_statistics where statsdate >= '" + DateUtil.convert(startDate, DateUtil.format2) + "' and statsdate <= '"
				+ DateUtil.convert(endDate, DateUtil.format2) + "'";
		try {
			baseDao.executeUpdateSQL(sql);
			logger.info("删除客户统计表历史数据结束：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1));
		} catch (Exception e) {
			logger.error("删除客户统计表历史数据异常：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1), e);
		}
	}

	/**
	 * 获取客户统计表（新表）最近的统计日期
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Date getLastStatisticsDate() {
		// 默认昨天
		Date date = null;
		String sql = "select max(statsdate) from erp_customer_statistics";
		try {
			List<Object> result = (List<Object>) baseDao.selectSQL(sql);
			if (result != null && result.get(0) != null) {
				date = new Date(((java.sql.Date) result.get(0)).getTime());
				logger.info("获取到客户统计表最近的统计日期：" + DateUtil.convert(date, DateUtil.format1));
			} else {
				logger.info("第一次转换客户统计明细表记录，转换表中全部记录");
			}
		} catch (Exception e) {
			logger.error("获取客户统计表最近的统计日期异常", e);
		}
		return date;
	}

	/**
	 * 获取客户统计明细表（旧表）最早的统计日期
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Date getFirstTjDate() {
		// 默认昨天
		Date date = DateUtil.getPreviousStartDateTime();
		String sql = "select min(statsdate) from erp_customerproducttj";
		try {
			List<Object> result = (List<Object>) baseDao.selectSQL(sql);
			if (result != null && result.get(0) != null) {
				date = new Date(((java.sql.Timestamp) result.get(0)).getTime());
			}
		} catch (Exception e) {
			logger.error("获取客户统计明细表最早的统计日期异常", e);
		}
		logger.info("获取到客户统计明细表最早的统计日期：" + DateUtil.convert(date, DateUtil.format1));
		return date;
	}

	/**
	 * 获取手动配置成本单价的产品类型的成本单价
	 */
	private void getProductTypeCostPrice() {
		productTypeCostPrice.clear();
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("costPriceType", Constants.ROP_EQ, CostPriceType.MANUAL.ordinal()));
		try {
			List<ProductType> productTypeList = productTypeService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(productTypeList)) {
				for (ProductType productType : productTypeList) {
					productTypeCostPrice.put(productType.getProductTypeValue(), productType.getCostPrice());
				}
			}
		} catch (ServiceException e) {
			logger.error("获取手动配置成本单价的产品类型的成本单价异常", e);
		}
	}
}
