package com.dahantc.erp.vo.roledetail.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.roledetail.dao.IRoleDetailDao;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;

@Repository("roleDetailDao")
public class RoleDetailDaoImpl implements IRoleDetailDao {
	private static final Logger logger = LogManager.getLogger(RoleDetailDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public RoleDetail read(Serializable id) {
		try {
			RoleDetail RoleDetail = (RoleDetail) baseDao.get(RoleDetail.class, id);
			return RoleDetail;
		} catch (Exception e) {
			logger.error("读取角色详情信息失败", e);
		}
		return null;
	}

	@Override
	public boolean save(RoleDetail entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存角色详情信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		RoleDetail RoleDetail = read(id);
		try {
			return baseDao.delete(RoleDetail);
		} catch (Exception e) {
			logger.error("删除角色详情信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(RoleDetail RoleDetail) throws DaoException {
		try {
			return baseDao.update(RoleDetail);
		} catch (Exception e) {
			logger.error("更新角色详情信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(RoleDetail.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<RoleDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(RoleDetail.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<RoleDetail> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(RoleDetail.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<RoleDetail> readRoleDetailByRoleAndMenuId(String roleId, String menuId) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(RoleDetail.class);
			Object[] ids = roleId.split(",");
			dc.add(Restrictions.in("roleid", ids));
			dc.add(Restrictions.eq("menuid", menuId));
			List<RoleDetail> list = baseDao.findAllByCriteria(dc);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<RoleDetail> readRoleDetailByRole(String roleId) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(RoleDetail.class);
			dc.add(Restrictions.eq("roleid", roleId));
			List<RoleDetail> list = baseDao.findAllByCriteria(dc);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean updateByBatch(List<RoleDetail> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<RoleDetail> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<RoleDetail> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
