package com.dahantc.erp.vo.supplierStatistics.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.supplierStatistics.dao.ISupplierStatisticsDao;
import com.dahantc.erp.vo.supplierStatistics.entity.SupplierStatistics;
import com.dahantc.erp.util.DetachedCriteriaUtil;

@Repository("supplierStatisticsDao")
public class SupplierStatisticsDaoImpl implements ISupplierStatisticsDao {
	private static final Logger logger = LogManager.getLogger(SupplierStatisticsDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public SupplierStatistics read(Serializable id) throws DaoException {
		try {
			SupplierStatistics supplierStatistics = (SupplierStatistics) baseDao.get(SupplierStatistics.class, id);
			return supplierStatistics;
		} catch (Exception e) {
			logger.error("读取供应商统计详情表失败", e);
		}
		return null;
	}

	@Override
	public boolean save(SupplierStatistics entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商统计详情表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		SupplierStatistics supplierStatistics = read(id);
		try {
			return baseDao.delete(supplierStatistics);
		} catch (Exception e) {
			logger.error("删除供应商统计详情表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(SupplierStatistics enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商统计详情表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(SupplierStatistics.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	
	@Override
	public PageResult<SupplierStatistics> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SupplierStatistics.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierStatistics> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SupplierStatistics.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<SupplierStatistics>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<SupplierStatistics> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(SupplierStatistics.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<SupplierStatistics> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<SupplierStatistics> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<SupplierStatistics> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<SupplierStatistics> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
