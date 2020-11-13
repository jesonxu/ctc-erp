package com.dahantc.erp.vo.role.dao.impl;

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
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.role.dao.IRoleDao;
import com.dahantc.erp.vo.role.entity.Role;

@Repository("roleDao")
public class RoleDaoImpl implements IRoleDao {
	private static final Logger logger = LogManager.getLogger(RoleDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public Role read(Serializable id) {
		if (id != null) {
			try {
				Role role = (Role) baseDao.get(Role.class, id);
				return role;
			} catch (Exception e) {
				logger.error("读取角色信息失败", e);
			}
		} else {
			logger.info("id为空");
		}
		return null;
	}

	@Override
	public boolean save(Role entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存角色信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		Role role = read(id);
		try {
			return baseDao.delete(role);
		} catch (Exception e) {
			logger.error("删除角色信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(Role role) throws DaoException {
		try {
			return baseDao.update(role);
		} catch (Exception e) {
			logger.error("更新角色信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Role.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<Role> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Role.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<Role> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Role.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	/** 根据属性和值查询数据 */
	@Override
	public Role readOneByProperty(String property, Object value) throws BaseException {
		try {
			Object object = baseDao.getEntityByProperty(property, value, Role.class);
			return object != null ? (Role) object : null;
		} catch (Exception e) {
			logger.error("读取角色信息异常", e);
			throw new BaseException("读取角色信息异常", e);
		}
	}

	@Override
	public List<Role> queryByHql(String hql, Map<String, Object> params, Integer max) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, max);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
