package com.dahantc.erp.vo.operationlog.dao.impl;

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
import com.dahantc.erp.vo.operationlog.dao.IOperationLogDao;
import com.dahantc.erp.vo.operationlog.entity.OperationLog;


@Repository("operationLogDao")
public class OperationLogDaoImpl implements IOperationLogDao {
	private static final Logger logger = LogManager.getLogger(OperationLogDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public OperationLog read(Serializable id) throws DaoException {
		try {
			OperationLog operationLog = (OperationLog) baseDao.get(OperationLog.class, id);
			return operationLog;
		} catch (Exception e) {
			logger.error("读取系统日志信息失败", e);
		}
		return null;
	}

	@Override
	public boolean save(OperationLog entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存系统日志信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		OperationLog operationLog = read(id);
		try {
			return baseDao.delete(operationLog);
		} catch (Exception e) {
			logger.error("删除系统日志信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(OperationLog enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新系统日志信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(OperationLog.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<OperationLog> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(OperationLog.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<OperationLog> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(OperationLog.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<OperationLog> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<OperationLog> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<OperationLog> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
