package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.enums.BusinessType;
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
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.DeepCloneUtil;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.supplierStatistics.entity.SupplierStatistics;
import com.dahantc.erp.vo.supplierStatistics.service.ISupplierStatisticsService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;

/**
 * 后台定时任务，取出供应商统计表erp_supplierproducttj的数据，计算好成本，放入供应商统计详情表erp_supplier_statistics中
 * 统计表erp_supplierproducttj的数据是由短信云统计完之后，同步到erp的
 */
@Component
public class BackTaskSupplierStatisticsTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskSupplierStatisticsTask.class);

	private static String CRON = "0 40 4 * * ?";

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IUnitPriceService unitPriceService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ISupplierStatisticsService supplierStatisticsService;

	// 供应商x产品 = {供应商id，供应商类型id，所属部门id，所属销售id，产品id, 产品类型，产品下的账号List}
	private ArrayList<HashMap<String, Object>> productInfoList = new ArrayList<>();

	// 单价map 产品标识+年月+产品类型 -> 单价
	private HashMap<String, BigDecimal> priceMap = new HashMap<>();

	// 统计开始时间
	public static Date startDate;

	// 统计结束时间
	public static Date endDate;

	// 查询一条通道在某天的统计
	String hql = "select countryCode,sum(successCount),sum(failCount),sum(totalCount) from SupplierProductTj"
			+ " where businessType = " + BusinessType.YTX.ordinal() + " and channelId = :channelId and productType = :productType"
			+ " and statsDate >= :startDate and statsDate < :nextDate group by countryCode";

	/**
	 * 执行统计转换操作的线程类 产品太多时，由每个线程转换一部分产品的统计
	 */
	class SupplierStatisticsTask implements Runnable {
		private int id;
		// 本线程处理的部分产品
		private List<HashMap<String, Object>> subProductInfoList;
		private Date tjDate;

		public SupplierStatisticsTask(int id, ArrayList<HashMap<String, Object>> productInfoList) {
			this.id = id;
			this.subProductInfoList = DeepCloneUtil.clone(productInfoList);
			this.tjDate = new Date(startDate.getTime());
		}

		@Override
		public void run() {
			List<SupplierStatistics> subStatisticsList = new ArrayList<>();
			// 遍历统计每一天
			while (tjDate.getTime() < endDate.getTime()) {
				long start = System.currentTimeMillis();
				logger.info("线程" + id + "转换 " + DateUtil.convert(tjDate, DateUtil.format1) + " 的统计数据开始，产品数：" + subProductInfoList.size());
				// 明天
				Date nextDate = DateUtil.getDaysOfDistance(tjDate, 1);
				HashMap<String, Object> params;
				// 遍历所有产品
				for (HashMap<String, Object> productInfo : subProductInfoList) {
					String productId = (String) productInfo.get("productId");
					// 该产品当天的统计数据
					SupplierStatistics statistics = new SupplierStatistics();
					statistics.setProductId(productId);
					statistics.setStatsDate(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format1)));
					statistics.setStatsYearMonth(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format4) + "-01"));
					statistics.setStatsYear(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format11) + "-01-01"));
					statistics.setSupplierId((String) productInfo.get("supplierId"));
					statistics.setSupplierTypeId((String) productInfo.get("supplierTypeId"));
					statistics.setDeptId((String) productInfo.get("deptId"));
					statistics.setOssUserId((String) productInfo.get("ossUserId"));
					statistics.setBusinessType(BusinessType.YTX.ordinal());
					int productType = (Integer) productInfo.get("productType");
					statistics.setProductType(productType);
					// 产品标识即是通道Id
					String channelId = (String) productInfo.get("productMark");
					statistics.setProductMark(channelId);
					if (StringUtil.isNotBlank(channelId)) {
						params = new HashMap<>();
						params.put("channelId", channelId);
						params.put("productType", productType);
						params.put("startDate", tjDate);
						params.put("nextDate", nextDate);
						// 查询一条通道当天的统计记录，按通道id、国别号分组
						List<Object[]> productTj = null;
						try {
							productTj = baseDao.findByhql(hql, params, 0);
						} catch (Exception e) {
							logger.error("线程" + id + "查询产品id：" + productId + " 的账号的统计数据异常", e);
						}
						if (productTj != null && productTj.size() > 0) {
							// 一条通道在某个国别号的统计 tj -> [国别号，成功数，失败数，总数]
							for (Object[] tj : productTj) {
								// 供应商通道单价 = 通道id + 年月 + 国别号（通道id即erp的产品标识）
								String key = channelId + "," + DateUtil.convert(tjDate, DateUtil.format4) + "," + (String) tj[0];
								long successCount = (Long) tj[1];
								long failCount = (Long) tj[2];
								long totalCount = (Long) tj[3];
								BigDecimal price = priceMap.getOrDefault(key, new BigDecimal(0));
								BigDecimal payables = new BigDecimal(successCount).multiply(price);
								statistics.addPayables(payables);
								statistics.addSuccessCount(successCount);
								statistics.addFailCount(failCount);
								statistics.addTotalCount(totalCount);
							}
						}
					}
					statistics.setScale2(); // 保留2位精度
					subStatisticsList.add(statistics);
				}
				// 保存当天的数据
				if (subStatisticsList.size() > 0) {
					try {
						if (supplierStatisticsService.saveByBatch(subStatisticsList)) {
							logger.info("线程" + id + "批量保存转换完成的统计详情数据成功，本批次" + subStatisticsList.size() + "条");
						} else {
							logger.info("线程" + id + "批量保存转换完成的统计详情数据失败，本批次" + subStatisticsList.size() + "条");
						}
					} catch (Exception e) {
						logger.error("线程" + id + "批量保存转换完成的统计详情数据异常，清空本批次的数据继续执行任务", e);
					} finally {
						subStatisticsList.clear();
					}
				}
				logger.info("线程" + id + "转换 " + DateUtil.convert(tjDate, DateUtil.format1) + " 的统计数据结束，产品数：" + subProductInfoList.size() + "，耗时："
						+ (System.currentTimeMillis() - start));
				// 转换下一天
				tjDate = nextDate;
			}
		}
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				try {
					// 执行任务
					logger.info("BackTask-SupplierStatistics-Task is running...");
					supplierStatisticsTask();
				} catch (Exception e) {
					logger.error("BackTask-SupplierStatistics-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-SupplierStatistics-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	/**
	 * 获取统计表数据，计算好之后保存到详情表
	 */
	public void supplierStatisticsTask() {
		// 删除结束日期 = 统计详情（新）表最后一天
		endDate = getLastStatisticsDate();
		if (endDate == null) {
			// 新表为空，是第一次转换，转换全部，删除结束日期 = 昨天23:59:59
			endDate = DateUtil.getPreviousEndDateTime();
			// 删除/转换开始日期 = 统计（旧）表最早那天的00:00:00
			startDate = DateUtil.getDateStartDateTime(getFirstTjDate());
		} else {
			// 删除结束日期 = 最后一天的23:59:59
			endDate = DateUtil.getDateEndDateTime(endDate);
			// 删除/转换开始日期 = 删除结束日期 - 延迟天数的00:00:00
			startDate = DateUtil.getDateStartDateTime(DateUtil.getDaysOfDistance(endDate, Constants.STATISTICS_DELAY_DAYS * -1));
		}
		// 删除新表历史数据
		delOldData(startDate, endDate);
		// 转换结束日期 = 昨天23:59:59
		endDate = DateUtil.getPreviousEndDateTime();
		logger.info("转换供应商统计数据开始：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1));
		// 获取供应商x产品的信息
		getSupplierAndProductInfo(productInfoList);
		// 获取供应商通道单价（成本）
		getSupplierPrice(priceMap, startDate, endDate);

		// 多线程处理，每个线程处理30个产品，最大32线程，超出则重新计算每个线程的产品数
		if (productInfoList.size() > 0) {
			int maxPage = 32;
			int page = 1;
			int pageSize = 30;
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
					new Thread(new SupplierStatisticsTask(page, subList)).start();
				}
				page++;
			}
		}
		productInfoList.clear();
	}

	/**
	 * 获取所有产品的关键信息，包括所属供应商的关键信息
	 * {供应商id，供应商类型id，所属部门id，所属商务id，产品id, 产品类型，产品标识}
	 * 
	 * @param supAndProInfoList
	 *            要填充的map
	 */
	@SuppressWarnings("unchecked")
	private void getSupplierAndProductInfo(ArrayList<HashMap<String, Object>> supAndProInfoList) {
		supAndProInfoList.clear();
		try {
			logger.info("查询供应商x产品信息开始");
			long _start = System.currentTimeMillis();
			String sql = "select s.supplierid, s.suppliertypeid, s.deptid, s.ossuserid, p.productid, p.producttype, p.productmark" +
					" from erp_supplier s right join erp_product p on s.supplierid = p.supplierid";
			List<Object[]> productList = (List<Object[]>) baseDao.selectSQL(sql);
			if (productList != null && productList.size() > 0) {
				for (Object[] product : productList) {
					HashMap<String, Object> productInfo = new HashMap<>();
					productInfo.put("supplierId", product[0]);
					productInfo.put("supplierTypeId", product[1]);
					productInfo.put("deptId", product[2]);
					productInfo.put("ossUserId", product[3]);
					productInfo.put("productId", product[4]);
					productInfo.put("productType", product[5]);
					productInfo.put("productMark", product[6]);
					supAndProInfoList.add(productInfo);
				}
				productList.clear();
				productList = null;
			}
			logger.info("查询供应商x产品信息结束，查询到" + supAndProInfoList.size() + "条记录，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询供应商x产品信息异常", e);
		}
	}

	/**
	 * 获取供应商通道单价（成本）
	 * 
	 * @param priceMap
	 *            要填充的map
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 */
	private void getSupplierPrice(HashMap<String, BigDecimal> priceMap, Date startTime, Date endTime) {
		// 获取时间范围内的所有供应商通道单价
		logger.info("查询单价表获取供应商通道单价开始");
		long _start = System.currentTimeMillis();
		priceMap.clear();
		// 供应商产品id -> 产品标识
		Map<String, String> supplierChanMap = new HashMap<>();
		// 单价表目前只保存月单价
		startTime = DateUtil.getThisMonthFirst(startTime); // 月初
		endTime = DateUtil.getMonthFinal(endTime); // 月末
		try {
			// 获取 供应商产品id 和 产品标识 的对应，产品标识即通道Id
			logger.info("获取 供应商产品id 和 产品标识 的对应开始");
			long __start = System.currentTimeMillis();
			List<Product> productList = productService.queryAllBySearchFilter(null);
			if (!ListUtils.isEmpty(productList)) {
				for (Product product : productList) {
					if (StringUtils.isNotBlank(product.getProductMark())) {
						supplierChanMap.put(product.getProductId(), product.getProductMark());
					}
				}
			}
			logger.info("获取 供应商产品id 和 产品标识 的对应结束，耗时：" + (System.currentTimeMillis() - __start));

			logger.info("获取 供应商通道单价 开始");
			__start = System.currentTimeMillis();
			SearchFilter priceFilter = new SearchFilter();
			priceFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.SUPPLIER.ordinal())); // 只查供应商
			priceFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
			priceFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
			List<UnitPrice> unitPriceList = unitPriceService.queryAllBySearchFilter(priceFilter);
			if (!ListUtils.isEmpty(unitPriceList)) {
				for (UnitPrice unitPrice : unitPriceList) {
					// 只保存有产品标识
					if (supplierChanMap.containsKey(unitPrice.getBasicsId())) {
						// 产品标识,月份,国别号 - 单价（以通道标识为key，是因为统计记录里只有通道Id，即erp的产品标识）
						priceMap.put(supplierChanMap.get(unitPrice.getBasicsId()) + "," + DateUtil.convert(unitPrice.getWtime(), DateUtil.format4) + ","
								+ unitPrice.getCountryCode(), unitPrice.getUnitPrice());
					}
				}
			}
			logger.info("获取 供应商通道单价 结束，耗时：" + (System.currentTimeMillis() - __start));
		} catch (Exception e) {
			logger.error("查询单价表获取供应商通道单价异常", e);
		} finally {
			if (supplierChanMap != null) {
				supplierChanMap.clear();
				supplierChanMap = null;
			}
		}
		logger.info("查询单价表获取供应商通道单价结束，耗时：" + (System.currentTimeMillis() - _start));
	}

	/**
	 * 删除供应商统计详情表的历史数据，以实现覆盖统计
	 * 
	 * @param startDate
	 *            开始时间 yyyy-MM-dd 00:00:00
	 * @param endDate
	 *            结束时间 yyyy-MM-dd 23:59:59
	 */
	private void delOldData(Date startDate, Date endDate) {
		String sql = "delete from erp_supplier_statistics where statsdate >= '" + DateUtil.convert(startDate, DateUtil.format2) + "' and statsdate <= '"
				+ DateUtil.convert(endDate, DateUtil.format2) + "'";
		try {
			baseDao.executeUpdateSQL(sql);
			logger.info("删除供应商统计详情表历史数据结束：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1));
		} catch (Exception e) {
			logger.error("删除供应商统计详情表历史数据异常：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1), e);
		}
	}

	/**
	 * 获取供应商统计详情表最近的统计日期
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Date getLastStatisticsDate() {
		// 默认昨天
		Date date = null;
		String sql = "select max(statsdate) from erp_supplier_statistics";
		try {
			List<Object> result = (List<Object>) baseDao.selectSQL(sql);
			if (result != null && result.get(0) != null) {
				date = new Date(((java.sql.Date) result.get(0)).getTime());
				logger.info("获取到供应商统计详情表最近的统计日期：" + DateUtil.convert(date, DateUtil.format1));
			} else {
				logger.info("第一次转换供应商统计表记录，转换表中全部记录");
			}
		} catch (Exception e) {
			logger.error("获取供应商统计详情表最近的统计日期异常", e);
		}
		return date;
	}

	/**
	 * 获取供应商统计表最早的统计日期
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Date getFirstTjDate() {
		// 默认昨天
		Date date = DateUtil.getPreviousStartDateTime();
		String sql = "select min(statsdate) from erp_supplierproducttj";
		try {
			List<Object> result = (List<Object>) baseDao.selectSQL(sql);
			if (result != null && result.get(0) != null) {
				date = new Date(((java.sql.Timestamp) result.get(0)).getTime());
			}
		} catch (Exception e) {
			logger.error("获取供应商统计表最早的统计日期异常", e);
		}
		logger.info("获取到供应商统计表最早的统计日期：" + DateUtil.convert(date, DateUtil.format1));
		return date;
	}
}
