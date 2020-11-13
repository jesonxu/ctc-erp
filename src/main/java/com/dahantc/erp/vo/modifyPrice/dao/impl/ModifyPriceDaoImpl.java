package com.dahantc.erp.vo.modifyPrice.dao.impl;

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
import com.dahantc.erp.vo.modifyPrice.dao.IModifyPriceDao;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.util.DetachedCriteriaUtil;

@Repository("modifyPriceDao")
public class ModifyPriceDaoImpl implements IModifyPriceDao {
	private static final Logger logger = LogManager.getLogger(ModifyPriceDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public ModifyPrice read(Serializable id) throws DaoException {
		try {
			ModifyPrice modifyPrice = (ModifyPrice) baseDao.get(ModifyPrice.class, id);
			return modifyPrice;
		} catch (Exception e) {
			logger.error("读取产品调价表失败", e);
		}
		return null;
	}

	@Override
	public boolean save(ModifyPrice entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存产品调价表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		ModifyPrice modifyPrice = read(id);
		try {
			return baseDao.delete(modifyPrice);
		} catch (Exception e) {
			logger.error("删除产品调价表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(ModifyPrice enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新产品调价表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(ModifyPrice.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<ModifyPrice> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ModifyPrice.class);
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
	public List<ModifyPrice> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ModifyPrice.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<ModifyPrice>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<ModifyPrice> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(ModifyPrice.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<ModifyPrice> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<ModifyPrice> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<ModifyPrice> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<ModifyPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
