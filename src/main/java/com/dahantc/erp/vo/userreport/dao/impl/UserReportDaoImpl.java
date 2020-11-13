package com.dahantc.erp.vo.userreport.dao.impl;

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
import com.dahantc.erp.vo.userreport.dao.IUserReportDao;
import com.dahantc.erp.vo.userreport.entity.UserReport;

@Repository("userReportDao")
public class UserReportDaoImpl implements IUserReportDao {
	private static final Logger logger = LogManager.getLogger(UserReportDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public UserReport read(Serializable id) {
		try {
			UserReport UserReport = (UserReport) baseDao.get(UserReport.class, id);
			return UserReport;
		} catch (Exception e) {
			logger.error("读取用户报告信息失败", e);
		}
		return null;
	}

	@Override
	public boolean save(UserReport entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存用户报告信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		UserReport UserReport = read(id);
		try {
			return baseDao.delete(UserReport);
		} catch (Exception e) {
			logger.error("删除用户报告信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(UserReport UserReport) throws DaoException {
		try {
			return baseDao.update(UserReport);
		} catch (Exception e) {
			logger.error("更新用户报告信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(UserReport.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<UserReport> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UserReport.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<UserReport> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(UserReport.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<UserReport> readUserReportByRoleAndMenuId(String roleId, String menuId) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(UserReport.class);
			Object[] ids = roleId.split(",");
			dc.add(Restrictions.in("roleid", ids));
			dc.add(Restrictions.eq("menuid", menuId));
			List<UserReport> list = baseDao.findAllByCriteria(dc);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<UserReport> readUserReportByRole(String roleId) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(UserReport.class);
			dc.add(Restrictions.eq("roleid", roleId));
			List<UserReport> list = baseDao.findAllByCriteria(dc);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean updateByBatch(List<UserReport> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<UserReport> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<UserReport> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
