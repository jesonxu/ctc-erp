package com.dahantc.erp.vo.billpenaltyinterest.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.billpenaltyinterest.dao.IBillPenaltyInterestDao;
import com.dahantc.erp.vo.billpenaltyinterest.entity.BillPenaltyInterest;

@Repository("billPenaltyInterestDao")
public class BillPenaltyInterestDaoImpl implements IBillPenaltyInterestDao {

	private static final Logger logger = LogManager.getLogger(BillPenaltyInterestDaoImpl.class);

	@Resource
	private IBaseDao baseDao;

	@Override
	public BillPenaltyInterest read(Serializable id) throws DaoException {
		try {
			BillPenaltyInterest flowLog = (BillPenaltyInterest) baseDao.get(BillPenaltyInterest.class, id);
			return flowLog;
		} catch (Exception e) {
			logger.error("读取账单罚息处理结果表失败", e);
		}
		return null;
	}

	@Override
	public boolean save(BillPenaltyInterest entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存账单罚息处理结果表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		BillPenaltyInterest flowLog = read(id);
		try {
			return baseDao.delete(flowLog);
		} catch (Exception e) {
			logger.error("删除账单罚息处理结果表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(BillPenaltyInterest enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新账单罚息处理结果表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(BillPenaltyInterest.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<BillPenaltyInterest> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(BillPenaltyInterest.class);
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
	public List<BillPenaltyInterest> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(BillPenaltyInterest.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<BillPenaltyInterest>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<BillPenaltyInterest> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(BillPenaltyInterest.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<BillPenaltyInterest> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<BillPenaltyInterest> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<BillPenaltyInterest> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
