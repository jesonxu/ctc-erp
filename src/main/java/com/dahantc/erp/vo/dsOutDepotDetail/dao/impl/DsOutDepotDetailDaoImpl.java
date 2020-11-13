package com.dahantc.erp.vo.dsOutDepotDetail.dao.impl;

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
import com.dahantc.erp.vo.dsOutDepotDetail.dao.IDsOutDepotDetailDao;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;
import com.dahantc.erp.util.DetachedCriteriaUtil;

@Repository("dsOutDepotDetailDao")
public class DsOutDepotDetailDaoImpl implements IDsOutDepotDetailDao {
	private static final Logger logger = LogManager.getLogger(DsOutDepotDetailDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public DsOutDepotDetail read(Serializable id) throws DaoException {
		try {
			DsOutDepotDetail dsOutDepotDetail = (DsOutDepotDetail) baseDao.get(DsOutDepotDetail.class, id);
			return dsOutDepotDetail;
		} catch (Exception e) {
			logger.error("读取电商出库详情表失败", e);
		}
		return null;
	}

	@Override
	public boolean save(DsOutDepotDetail entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商出库详情表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		DsOutDepotDetail dsOutDepotDetail = read(id);
		try {
			return baseDao.delete(dsOutDepotDetail);
		} catch (Exception e) {
			logger.error("删除电商出库详情表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(DsOutDepotDetail enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商出库详情表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(DsOutDepotDetail.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	
	@Override
	public PageResult<DsOutDepotDetail> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DsOutDepotDetail.class);
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
	public List<DsOutDepotDetail> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DsOutDepotDetail.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<DsOutDepotDetail>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<DsOutDepotDetail> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(DsOutDepotDetail.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<DsOutDepotDetail> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsOutDepotDetail> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<DsOutDepotDetail> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<DsOutDepotDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
