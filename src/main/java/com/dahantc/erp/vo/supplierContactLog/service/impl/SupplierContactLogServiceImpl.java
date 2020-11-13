package com.dahantc.erp.vo.supplierContactLog.service.impl;

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
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.supplierContactLog.dao.ISupplierContactLogDao;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;
import com.dahantc.erp.vo.supplierContactLog.service.ISupplierContactLogService;

@Service("supplierContactLogService")
public class SupplierContactLogServiceImpl implements ISupplierContactLogService {
	private static Logger logger = LogManager.getLogger(SupplierContactLogServiceImpl.class);

	@Autowired
	private ISupplierContactLogDao supplierContactLogDao;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public SupplierContactLog read(Serializable id) throws ServiceException {
		try {
			return supplierContactLogDao.read(id);
		} catch (Exception e) {
			logger.error("读取联系日志表失败", e);
			throw new ServiceException("读取联系日志表失败", e);
		}
	}

	@Override
	public boolean save(SupplierContactLog entity) throws ServiceException {
		try {
			return supplierContactLogDao.save(entity);
		} catch (Exception e) {
			logger.error("保存联系日志表失败", e);
			throw new ServiceException("保存联系日志表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return supplierContactLogDao.delete(id);
		} catch (Exception e) {
			logger.error("删除联系日志表失败", e);
			throw new ServiceException("删除联系日志表失败", e);
		}
	}

	@Override
	public boolean update(SupplierContactLog enterprise) throws ServiceException {
		try {
			return supplierContactLogDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新联系日志表失败", e);
			throw new ServiceException("更新联系日志表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return supplierContactLogDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询联系日志表数量失败", e);
			throw new ServiceException("查询联系日志表数量失败", e);
		}
	}

	@Override
	public PageResult<SupplierContactLog> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return supplierContactLogDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询联系日志表分页信息失败", e);
			throw new ServiceException("查询联系日志表分页信息失败", e);
		}
	}

	@Override
	public List<SupplierContactLog> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return supplierContactLogDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询联系日志表失败", e);
			throw new ServiceException("查询联系日志表失败", e);
		}
	}

	@Override
	public List<SupplierContactLog> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return supplierContactLogDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询联系日志表失败", e);
			throw new ServiceException("查询联系日志表失败", e);
		}
	}

	@Override
	public List<SupplierContactLog> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return supplierContactLogDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询联系日志表失败", e);
			throw new ServiceException("查询联系日志表失败", e);
		}
	}

	/**
	 * 查询客户一个月内的联系日志
	 *
	 * @param customerIds
	 *            客户Id
	 * @param month
	 *            几个月内
	 * @return 客户的联系日志情况
	 */
	@Override
	public Map<String, Integer> queryCustomerContactLog(List<String> customerIds, Integer month, Integer day) {
		Map<String, Integer> customerContactInfo = new HashMap<>();
		if (customerIds == null || customerIds.isEmpty() || (month == null && day == null)) {
			return customerContactInfo;
		}
		Timestamp time = null;
		if (month != null) {
			time = new Timestamp(DateUtil.getMonthBefore(month));
		}
		if (day != null) {
			Date timePoint = new Date();
			if (time != null) {
				timePoint = new Date(time.getTime());
			}
			time = new Timestamp(DateUtil.getDayBefore(timePoint, day));
		}
		String hql = " select s.supplierId ,count(1) from SupplierContactLog s where s.supplierId in(:ids) and s.wtime >= :time group by s.supplierId ";
		Map<String, Object> params = new HashMap<>();
		params.put("ids", customerIds);
		params.put("time", time);
		List<Object> contactLogs = null;
		try {
			contactLogs = baseDao.findByhql(hql, params, Integer.MAX_VALUE);
		} catch (BaseException e) {
			logger.error("通过HQL查询联系日志信息异常", e);
		}
		if (contactLogs != null && !contactLogs.isEmpty()) {
			for (Object row : contactLogs) {
				if (row.getClass().isArray()) {
					Object[] rowInfo = (Object[]) row;
					if (rowInfo.length >= 2) {
						customerContactInfo.put(String.valueOf(rowInfo[0]), ((Number) rowInfo[1]).intValue());
					}
				}
			}
		}
		return customerContactInfo;
	}
}
