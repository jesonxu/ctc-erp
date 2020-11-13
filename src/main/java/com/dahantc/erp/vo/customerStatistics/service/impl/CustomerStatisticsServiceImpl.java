package com.dahantc.erp.vo.customerStatistics.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.customerStatistics.dao.ICustomerStatisticsDao;
import com.dahantc.erp.vo.customerStatistics.entity.CustomerStatistics;
import com.dahantc.erp.vo.customerStatistics.service.ICustomerStatisticsService;

@Service("customerStatisticsService")
public class CustomerStatisticsServiceImpl implements ICustomerStatisticsService {
	private static Logger logger = LogManager.getLogger(CustomerStatisticsServiceImpl.class);

	@Autowired
	private ICustomerStatisticsDao customerStatisticsDao;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public CustomerStatistics read(Serializable id) throws ServiceException {
		try {
			return customerStatisticsDao.read(id);
		} catch (Exception e) {
			logger.error("读取客户统计详情表失败", e);
			throw new ServiceException("读取客户统计详情表失败", e);
		}
	}

	@Override
	public boolean save(CustomerStatistics entity) throws ServiceException {
		try {
			return customerStatisticsDao.save(entity);
		} catch (Exception e) {
			logger.error("保存客户统计详情表失败", e);
			throw new ServiceException("保存客户统计详情表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<CustomerStatistics> objs) throws ServiceException {
		try {
			return customerStatisticsDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return customerStatisticsDao.delete(id);
		} catch (Exception e) {
			logger.error("删除客户统计详情表失败", e);
			throw new ServiceException("删除客户统计详情表失败", e);
		}
	}

	@Override
	public boolean update(CustomerStatistics enterprise) throws ServiceException {
		try {
			return customerStatisticsDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新客户统计详情表失败", e);
			throw new ServiceException("更新客户统计详情表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return customerStatisticsDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询客户统计详情表数量失败", e);
			throw new ServiceException("查询客户统计详情表数量失败", e);
		}
	}

	@Override
	public PageResult<CustomerStatistics> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerStatisticsDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询客户统计详情表分页信息失败", e);
			throw new ServiceException("查询客户统计详情表分页信息失败", e);
		}
	}

	@Override
	public List<CustomerStatistics> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return customerStatisticsDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询客户统计详情表失败", e);
			throw new ServiceException("查询客户统计详情表失败", e);
		}
	}

	@Override
	public List<CustomerStatistics> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return customerStatisticsDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户统计详情表失败", e);
			throw new ServiceException("查询客户统计详情表失败", e);
		}
	}

	@Override
	public List<CustomerStatistics> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return customerStatisticsDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询客户统计详情表失败", e);
			throw new ServiceException("查询客户统计详情表失败", e);
		}
	}

	/**
	 * 查询客户多少月多条天内的消耗量（不分种类）
	 *
	 * @param customerIds
	 *            客户Id
	 * @param month
	 *            月数
	 * @param days
	 *            缇娜书
	 * @return 客户消耗统计信息
	 */
	@Override
	public Map<String, Long> findCustomerCost(List<String> customerIds , Integer month, Integer days) {
		Map<String, Long> customerCostInfo = new HashMap<>();
		if (customerIds == null || customerIds.isEmpty() || (month == null && days == null)) {
			return customerCostInfo;
		}
		try {
			Timestamp timeInfo = null;
			if (month != null) {
				timeInfo = new Timestamp(DateUtil.getMonthBefore(month));
			}
			if (days != null) {
				Date dayPoint = new Date();
				if (timeInfo != null) {
					dayPoint = new Date(timeInfo.getTime());
				}
				timeInfo = new Timestamp(DateUtil.getDayBefore(dayPoint, days));
			}
			// 判断是否有消耗（不用管是什么有任何内容都是）
			String statisticHql = " SELECT s.customerId ,SUM(s.totalCount) FROM CustomerStatistics s WHERE s.customerId IN (:cIds) and s.statsDate >= :timePoint GROUP BY s.customerId";
			Map<String, Object> statisticParams = new HashMap<>(1);
			statisticParams.put("cIds", customerIds);
			statisticParams.put("timePoint", timeInfo);
			List<Object> customerCostList = baseDao.findByhql(statisticHql, statisticParams, Integer.MAX_VALUE);
			if (customerCostList != null && !customerCostList.isEmpty()) {
				for (Object row : customerCostList) {
					if (row.getClass().isArray()) {
						Object[] rowInfo = (Object[]) row;
						if (rowInfo.length >= 2) {
							String cId = String.valueOf(rowInfo[0]);
							long count = 0L;
							if (rowInfo[1] instanceof Number) {
								count = ((Number) rowInfo[1]).longValue();
							}
							customerCostInfo.put(cId, count);
						}
					}
				}
			}
		} catch (BaseException e) {
			logger.error("通过HQL查询客户消耗情况异常", e);
		}
		return customerCostInfo;
	}
}
