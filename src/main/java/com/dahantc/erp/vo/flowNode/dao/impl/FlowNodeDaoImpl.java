package com.dahantc.erp.vo.flowNode.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dahantc.erp.commom.dao.BaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.flowNode.dao.IFlowNodeDao;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.util.DetachedCriteriaUtil;

@Repository("flowNodeDao")
public class FlowNodeDaoImpl implements IFlowNodeDao {
	private static final Logger logger = LogManager.getLogger(FlowNodeDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public FlowNode read(Serializable id) throws DaoException {
		try {
			FlowNode flowNode = (FlowNode) baseDao.get(FlowNode.class, id);
			return flowNode;
		} catch (Exception e) {
			logger.error("读取流程节点表失败", e);
		}
		return null;
	}

	@Override
	public boolean save(FlowNode entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存流程节点表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		FlowNode flowNode = read(id);
		try {
			return baseDao.delete(flowNode);
		} catch (Exception e) {
			logger.error("删除流程节点表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(FlowNode enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新流程节点表失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(FlowNode.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	
	@Override
	public PageResult<FlowNode> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(FlowNode.class);
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
	public List<FlowNode> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(FlowNode.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<FlowNode>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<FlowNode> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(FlowNode.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<FlowNode> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<FlowNode> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<FlowNode> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<FlowNode> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<Object> findBySql(String sql, Object[] params) throws DaoException {
		try {
			return baseDao.selectSQL(sql,params);
		} catch (BaseException e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
