package com.dahantc.erp.vo.parameter.dao.impl;

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
import com.dahantc.erp.vo.parameter.dao.IParameterDao;
import com.dahantc.erp.vo.parameter.entity.Parameter;

@Repository("parameterDao")
public class ParameterDaoImpl implements IParameterDao {
	private static final Logger logger = LogManager.getLogger(ParameterDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public Parameter read(Serializable id) throws DaoException {
		try {
			Parameter Parameter = (Parameter) baseDao.get(Parameter.class, id);
			return Parameter;
		} catch (Exception e) {
			logger.error("读取系统参数失败", e);
		}
		return null;
	}

	@Override
	public Parameter readOneByProperty(String property, Object value) throws DaoException {
		try {
			return (Parameter) baseDao.getEntityByProperty(property, value, Parameter.class);
		} catch (Exception e) {
			logger.error("查询系统参数异常", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean save(Parameter entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存系统参数失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		Parameter parameter = read(id);
		try {
			return baseDao.delete(parameter);
		} catch (Exception e) {
			logger.error("删除系统参数失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(Parameter enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新系统参数失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Parameter.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<Parameter> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Parameter.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<Parameter> findAllByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Parameter.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
