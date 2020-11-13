package com.dahantc.erp.vo.modifyPrice.service.impl;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.PriceType;
import com.dahantc.erp.flowtask.service.InterAdjustPriceService;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.deductionPrice.dao.IDeductionPriceDao;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.modifyPrice.dao.IModifyPriceDao;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

@Service("modifyPriceService")
public class ModifyPriceServiceImpl implements IModifyPriceService {
	private static Logger logger = LogManager.getLogger(ModifyPriceServiceImpl.class);

	@Autowired
	private IModifyPriceDao modifyPriceDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IDeductionPriceDao deductionPriceDao;

	@Autowired
	private InterAdjustPriceService interAdjustPriceService;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public ModifyPrice read(Serializable id) throws ServiceException {
		try {
			return modifyPriceDao.read(id);
		} catch (Exception e) {
			logger.error("读取产品调价表失败", e);
			throw new ServiceException("读取产品调价表失败", e);
		}
	}

	@Override
	public boolean save(ModifyPrice entity) throws ServiceException {
		try {
			return modifyPriceDao.save(entity);
		} catch (Exception e) {
			logger.error("保存产品调价表失败", e);
			throw new ServiceException("保存产品调价表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return modifyPriceDao.delete(id);
		} catch (Exception e) {
			logger.error("删除产品调价表失败", e);
			throw new ServiceException("删除产品调价表失败", e);
		}
	}

	@Override
	public boolean update(ModifyPrice enterprise) throws ServiceException {
		try {
			return modifyPriceDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新产品调价表失败", e);
			throw new ServiceException("更新产品调价表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return modifyPriceDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询产品调价表数量失败", e);
			throw new ServiceException("查询产品调价表数量失败", e);
		}
	}

	@Override
	public PageResult<ModifyPrice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return modifyPriceDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询产品调价表分页信息失败", e);
			throw new ServiceException("查询产品调价表分页信息失败", e);
		}
	}

	@Override
	public List<ModifyPrice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return modifyPriceDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询产品调价表失败", e);
			throw new ServiceException("查询产品调价表失败", e);
		}
	}

	@Override
	public List<ModifyPrice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return modifyPriceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询产品调价表失败", e);
			throw new ServiceException("查询产品调价表失败", e);
		}
	}

	@Override
	public List<ModifyPrice> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return modifyPriceDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询产品调价表失败", e);
			throw new ServiceException("查询产品调价表失败", e);
		}
	}

	@Override
	public List<Map<String, Object>> findProductPriceInfo(String productId, Date start, Date end) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<>();
		hql.append("SELECT mp.modifyPriceId,mp.modifyPriceDetail,mp.priceType,dp.minSend,dp.maxSend,dp.price,"
				+ " mp.remark ,mp.validityDateStart,mp.validityDateEnd FROM ModifyPrice mp "
				+ " LEFT JOIN DeductionPrice dp ON mp.modifyPriceId = dp.modifyPriceId WHERE mp.productId =:productId ");
		params.put("productId", productId);
		if (start != null && end != null) {
			// 根据时间段进行查询 只要是在有效期内的都查询出来
			hql.append(" AND (mp.validityDateStart >= :startTime OR mp.validityDateEnd <= :endTime) ");
			params.put("startTime", start);
			params.put("endTime", end);
		} else {
			// 查询大于等于当前时间的价格信息
			hql.append(" AND (mp.validityDateStart >= :startTime) ");
			params.put("startTime", new Date());
		}
		hql.append(" order  by mp.priceType, dp.minSend ");
		try {
			List<Object> objects = baseDao.findByhql(hql.toString(), params, 0);
			if (!ListUtils.isEmpty(objects)) {
				return objects.stream().map(object -> {
					Object[] oArr = (Object[]) object;
					Map<String, Object> item = new HashMap<>();
					item.put("modifyPriceId", oArr[0]);
					item.put("modifyPriceDetail", oArr[1]);
					item.put("priceType", oArr[2]);
					item.put("minSend", oArr[3]);
					item.put("maxSend", oArr[4]);
					item.put("price", oArr[5]);
					// 国际调价的时候存储的是报价单的Excel路径
					item.put("remark", oArr[6]);
					String startTime = DateUtil.convert((Date) oArr[7], DateUtil.format1);
					String endTime = DateUtil.convert((Date) oArr[8], DateUtil.format1);
					item.put("startTime", startTime);
					item.put("endTime", endTime);
					return item;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("查询产品的价格信息异常,HQL=" + hql, e);
		}
		return null;
	}

	@Override
	public Map<TimeQuantum, BigDecimal> findCurrentProductPriceInfo(String productId, Date startDate, Date endDate, int productType) {

		Map<TimeQuantum, ModifyPrice> modifyPriceMap = getModifyPrice(productId, startDate, endDate);

		ModifyPrice modifyPrice = null;
		if (!CollectionUtils.isEmpty(modifyPriceMap)) {
			modifyPrice = modifyPriceMap.values().iterator().next();
		} else {
			if (CollectionUtils.isEmpty(modifyPriceMap)) {

				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
				searchFilter.getOrders().add(new SearchOrder("validityDateEnd", Constants.ROP_DESC));
				searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));

				try {
					List<ModifyPrice> list = findByFilter(1, 0, searchFilter);
					if (!CollectionUtils.isEmpty(list)) {
						modifyPrice = list.get(0);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}

		if (modifyPrice == null) {
			return null;
		}

		BigDecimal price = BigDecimal.ZERO;
		// 国际短信
		if (productType == productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS)) {

			Map<String, Double> priceMap = getInterSectionPrice(modifyPrice);
			if (!CollectionUtils.isEmpty(priceMap)) {
				List<Double> priceList = new ArrayList<>(priceMap.values());
				priceList.sort(Double::compareTo);
				if (priceList.size() % 2 == 0) {
					price = BigDecimal.valueOf(priceList.get(priceList.size() / 2 - 1) + priceList.get(priceList.size() / 2)).divide(new BigDecimal(2), 6, BigDecimal.ROUND_HALF_UP);
				} else {
					price = BigDecimal.valueOf(priceList.get(priceList.size() / 2)).setScale(6, BigDecimal.ROUND_HALF_UP);
				}
			} else {
				return null;
			}

		} else {
			try {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("modifyPriceId", Constants.ROP_EQ, modifyPrice.getModifyPriceId()));
				List<DeductionPrice> list = deductionPriceDao.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(list)) {
					for (DeductionPrice deductionPrice : list) {
						if (deductionPrice.getIsDefault() == 1) {
							price = deductionPrice.getPrice();
						}
					}
				}
			} catch (DaoException e) {
				logger.error("", e);
			}

		}

		HashMap<TimeQuantum, BigDecimal> result = new HashMap<>();
		TimeQuantum timeQuantum = new TimeQuantum();
		timeQuantum.setStartDate(modifyPrice.getValidityDateStart());
		timeQuantum.setEndDate(modifyPrice.getValidityDateEnd());
		result.put(timeQuantum, price);

		return result;
	}

	/**
	 * 获取调价信息
	 *
	 * @param productId
	 *            产品id
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return ModifyPrice
	 */
	@Override
	public Map<TimeQuantum, ModifyPrice> getModifyPrice(String productId, Date startDate, Date endDate) {
		logger.info("查询区间内有效的调价记录开始，开始日期：" + DateUtil.convert(startDate, DateUtil.format1) + "，结束时间：" + DateUtil.convert(endDate, DateUtil.format1)
				+ "，productId：" + productId);
		// 查在开始和结束日期之间有效的调价记录
		String hql = "FROM ModifyPrice WHERE NOT (validityDateEnd < :startDate OR validityDateStart > :endDate) AND productId = :productId ORDER BY wtime DESC";
		Map<String, Object> param = new HashMap<>();
		param.put("productId", productId);
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		// 调价信息
		List<ModifyPrice> modifylist = null;
		try {
			List<Object> result = baseDao.findByhql(hql, param, 0);
			if (CollectionUtils.isEmpty(result)) {
				logger.info("产品productId：" + productId + "，未找到调价记录");
				return null;
			}
			modifylist = result.stream().map(obj -> (ModifyPrice) obj).collect(Collectors.toList());
			return getSectionModifyPrice(modifylist, startDate, endDate);
		} catch (Exception e) {
			logger.error("查询区间内有效的调价记录异常", e);
		}
		return null;
	}

	private List<DeductionPrice> getDeductionPrice(String modifyPriceId) {
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("modifyPriceId", Constants.ROP_EQ, modifyPriceId));
		filter.getOrders().add(new SearchOrder("gradient", Constants.ROP_ASC));
		try {
			return deductionPriceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("获取梯度价格时出现异常，", e);
		}
		return null;
	}

	/**
	 * 获取当日国际价格
	 * 
	 * @param productId
	 *            产品
	 * @param startDate
	 *            统计当天
	 * @param endDate
	 *            下一天
	 * @return
	 */
	@Override
	public Map<String, Double> getInterCountryPrice(String productId, Date startDate, Date endDate) {
		Map<TimeQuantum, ModifyPrice> resultMap = getModifyPrice(productId, startDate, endDate);
		if (!CollectionUtils.isEmpty(resultMap)) {
			ModifyPrice modifyPrice = resultMap.values().iterator().next();
			if (StringUtil.isNotBlank(modifyPrice.getRemark()) && modifyPrice.getRemark().endsWith("xls") || modifyPrice.getRemark().endsWith("xlsx")) {
				Map<String, Double> interSectionPrice = getInterSectionPrice(modifyPrice);
				if (!CollectionUtils.isEmpty(interSectionPrice)) {
					return interSectionPrice;
				}
			}
		}
		return null;
	}

	/**
	 * 综合整月的发送量，获取统计当天的单价
	 * 
	 * @param successCountMap
	 *            每天发送量
	 * @param productId
	 *            客户产品
	 * @param startDate
	 *            统计月初
	 * @param tjDate
	 *            统计当天
	 * @return 统计当天的单价
	 */
	@Override
	public BigDecimal getDatePrice(Map<Date, Long> successCountMap, String productId, Date startDate, Date tjDate) {
		BigDecimal datePrice = null;
		// 获取当月有效调价
		Map<TimeQuantum, ModifyPrice> mdPrice = getModifyPrice(productId, startDate, DateUtil.getThisMonthFinalTime(tjDate));
		if (!CollectionUtils.isEmpty(mdPrice)) {
			// 每个价格区间按调价分组，列表中的所有调价区间 对应 同一个调价记录
			Map<String, List<Entry<TimeQuantum, ModifyPrice>>> groupMap = mdPrice.entrySet().stream()
					.collect(Collectors.groupingBy(entry -> entry.getValue().getModifyPriceId()));
			// 找出统计当天所在区间对应的调价id
			String modifyPriceId = null;
			for (Entry<TimeQuantum, ModifyPrice> entry : mdPrice.entrySet()) {
				TimeQuantum tq = entry.getKey();
				if (!tq.startDate.after(tjDate) && !tq.endDate.before(tjDate)) {
					modifyPriceId = entry.getValue().getModifyPriceId();
				}
			}
			// 统计当天没有价格
			if (null == modifyPriceId) {
				return null;
			}
			// 统计当天对应的调价记录，该调价记录在统计当月的有效时间段
			List<Entry<TimeQuantum, ModifyPrice>> mpList = groupMap.get(modifyPriceId);

			for (Entry<TimeQuantum, ModifyPrice> mpEntry : mpList) {
				ModifyPrice modifyPrice = mpEntry.getValue();
				// 调价记录对应的价格梯度
				List<DeductionPrice> deductionList = getDeductionPrice(modifyPrice.getModifyPriceId());
				if (deductionList != null && deductionList.size() > 0) {
					DeductionPrice deductionPriceInfo = deductionList.get(0);
					if (modifyPrice.getPriceType() == PriceType.UNIFORM_PRICE.getCode()) {
						// 统一价
						datePrice = deductionPriceInfo.getPrice();
						break;
					} else if (modifyPrice.getPriceType() == PriceType.STAGE_PRICE.getCode()) {
						// 阶段价，看 统计当天所在调价区间 与 此前的调价区间 的总发送量 落在哪个阶段 就用哪个阶段的价格
						List<TimeQuantum> timeList = mpList.stream().filter(entry -> {
							TimeQuantum tq = entry.getKey();
							return !tq.startDate.after(tjDate);
						}).map(Entry::getKey).collect(Collectors.toList());
						// 累计至统计当天的发送量
						long successCount = timeList.stream().mapToLong(timeQuantum -> getSuccessCountByDate(successCountMap, timeQuantum.getStartDate(),
								timeQuantum.getEndDate().after(tjDate) ? tjDate : timeQuantum.getEndDate())).sum();

						datePrice = deductionPriceInfo.getPrice();
						for (DeductionPrice deductionPrice : deductionList) {
							if (successCount >= deductionPrice.getMaxSend() && deductionPrice.getMaxSend() > deductionPrice.getMinSend()) {
								continue;
							} else if (successCount >= deductionPrice.getMinSend()) {
								datePrice = deductionPrice.getPrice();
							}
						}

					} else if (modifyPrice.getPriceType() == PriceType.STEPPED_PRICE.getCode()) {
						// 阶梯价， 看当月 该调价记录的所有调价区间 的总发送量 落在哪个梯度 就用哪个梯度的价格
						List<TimeQuantum> timeList = mpList.stream().map(Entry::getKey).collect(Collectors.toList());
						long successCount = timeList.stream()
								.mapToLong(timeQuantum -> getSuccessCountByDate(successCountMap, timeQuantum.getStartDate(), timeQuantum.getEndDate())).sum();
						datePrice = deductionPriceInfo.getPrice();
						for (DeductionPrice deductionPrice : deductionList) {
							if (successCount >= deductionPrice.getMaxSend() && deductionPrice.getMaxSend() > deductionPrice.getMinSend()) {
								continue;
							} else if (successCount >= deductionPrice.getMinSend()) {
								datePrice = deductionPrice.getPrice();
							}
						}
					}
				}
			}
		}
		return datePrice;
	}

	private long getSuccessCountByDate(Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate) {
		long successCount = 0L;
		for (; !startDate.after(endDate); startDate = DateUtil.getNextDayStart(startDate)) {
			for (Map.Entry<Date, Long> entry : platformSuccessCountMap.entrySet()) {
				if (StringUtils.equals(DateUtil.convert(entry.getKey(), DateUtil.format1), DateUtil.convert(startDate.getTime(), DateUtil.format1))
						&& entry.getValue() != null) {
					successCount += entry.getValue();
					break;
				}
			}
		}
		return successCount;
	}

	private <T> Map<TimeQuantum, T> getTreeMap() {
		return new TreeMap<>((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
	}

	private void executeGrouping(List<ModifyPrice> modifylist, Date startDate, Date endDate, Command command) {
		// 用位表示开始到结束日期之间每一天是否有调价，哪一天有调价，位置1
		int dayOfMonthFlag = 0;
		Calendar calender = Calendar.getInstance();
		// 遍历每个生效时间在区间内的调价
		for (ModifyPrice modifyPrice : modifylist) {
			// 本次循环占用的位，哪一天有调价，位置1
			int thisFlag = 0;
			Date validityDateStart = modifyPrice.getValidityDateStart();
			Date validityDateEnd = modifyPrice.getValidityDateEnd();
			// 调价的 生效开始日期 在 统计开始日期 之前
			if (!validityDateStart.after(startDate)) {
				validityDateStart = startDate;
			}
			// 调价的 生效结束日期 在 统计结束日期 之后
			if (!validityDateEnd.before(endDate)) {
				validityDateEnd = endDate;
			}
			int _start = DateUtil.getDiffDays(validityDateStart, startDate);
			int _end = DateUtil.getDiffDays(validityDateEnd, startDate);
			boolean inSection = false;
			for (; _start <= _end; _start++) {
				if ((dayOfMonthFlag >> _start & 1) == 0) {
					if (!inSection) {
						calender.setTime(startDate);
						calender.add(Calendar.DATE, _start);
						validityDateStart = calender.getTime();
						inSection = true;
					}
					dayOfMonthFlag += 1 << _start;
					thisFlag += 1 << _start;
					if (_start != _end) {
						continue;
					}
				}
				if (((dayOfMonthFlag >> _start & 1) != 0 || _start == _end) && inSection) {
					calender.setTime(startDate);
					if ((thisFlag >> _start & 1) == 0 || _start != _end) {
						// 循环到目前天数的位是1，但并非本次循环置1的，说明这天有更新的调价，本条调价只能到昨天
						calender.add(Calendar.DATE, (_start - 1));
					} else {
						calender.add(Calendar.DATE, _start);
					}
					validityDateEnd = calender.getTime();
					command.execute(validityDateStart, DateUtil.getDateEndDateTime(validityDateEnd, true), modifyPrice);
					inSection = false;
				}
			}
		}
	}

	/**
	 * 分日期区间
	 * 
	 * @param modifylist
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private Map<TimeQuantum, ModifyPrice> getSectionModifyPrice(List<ModifyPrice> modifylist, Date startDate, Date endDate) {
		Map<TimeQuantum, ModifyPrice> modifyPriceMap = getTreeMap();
		executeGrouping(modifylist, startDate, endDate,
				(validityDateStart, validityDateEnd, modifyPrice) -> modifyPriceMap.put(new TimeQuantum(validityDateStart, validityDateEnd), modifyPrice));
		return modifyPriceMap;
	}

	/**
	 * 国际调价
	 * 
	 * @param modifyList
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Override
	public Map<TimeQuantum, Map<String, Double>> getInterPrices(List<ModifyPrice> modifyList, Date startDate, Date endDate) {
		Map<TimeQuantum, Map<String, Double>> modifyPriceMap = getTreeMap();
		try {
			executeGrouping(modifyList, startDate, endDate, (validityDateStart, validityDateEnd, modifyPrice) -> {
				if (StringUtil.isNotBlank(modifyPrice.getRemark()) && modifyPrice.getRemark().endsWith("xls") || modifyPrice.getRemark().endsWith("xlsx")) {
					Map<String, Double> interSectionPrice = getInterSectionPrice(modifyPrice);
					if (!CollectionUtils.isEmpty(interSectionPrice)) {
						modifyPriceMap.put(new TimeQuantum(validityDateStart, validityDateEnd), interSectionPrice);
					}
				}
			});
		} catch (Exception e) {
			logger.error("查询国际单价异常", e);
		}
		return modifyPriceMap;
	}

	/**
	 * 获取国际价格
	 * 
	 * @param modifyPrice
	 *            调价记录
	 * @return { 国别号 -> 价格 }
	 */
	private Map<String, Double> getInterSectionPrice(ModifyPrice modifyPrice) {
		if (modifyPrice == null || StringUtils.isBlank(modifyPrice.getRemark())) {
			return null;
		}
		List<String[]> priceList = null;
		File file = new File(modifyPrice.getRemark());
		if (file.exists()) {
			String ext = modifyPrice.getRemark().substring(modifyPrice.getRemark().lastIndexOf(".") + 1);
			if ("xls".equals(ext)) {
				priceList = ParseFile.parseExcel2003(new File(modifyPrice.getRemark()));
			} else if ("xlsx".equals(ext)) {
				priceList = ParseFile.parseExcel2007(new File(modifyPrice.getRemark()));
			}
		} else {
			logger.info("国际调价文件路径不存在：" + modifyPrice.getRemark());
		}
		priceList = interAdjustPriceService.convertToPriceList(priceList);
		if (!CollectionUtils.isEmpty(priceList)) {
			return priceList.stream().filter(datas -> datas.length >= 3 && StringUtil.isNotBlank(datas[0]) && StringUtil.isNotBlank(datas[2]))
					.collect(Collectors.toMap(datas -> (StringUtils.isNotBlank(datas[0]) && datas[0].startsWith("+")) ? datas[0] : ("+" + datas[0]),
							datas -> Double.valueOf(datas[2]), (key1, key2) -> key2));
		}
		return null;
	}

	@FunctionalInterface
	public interface Command {
		void execute(Date validityDateStart, Date validityDateEnd, ModifyPrice modifyPrice);
	}

	public class TimeQuantum {

		private Date startDate;
		private Date endDate;

		public TimeQuantum() {

		}

		public TimeQuantum(Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
	}

}
