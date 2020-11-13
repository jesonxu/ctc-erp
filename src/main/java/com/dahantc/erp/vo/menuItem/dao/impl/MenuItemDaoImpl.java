package com.dahantc.erp.vo.menuItem.dao.impl;

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
import com.dahantc.erp.vo.menuItem.dao.IMenuItemDao;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;

@Repository("menuItemDao")
public class MenuItemDaoImpl implements IMenuItemDao {
	private static final Logger logger = LogManager.getLogger(MenuItemDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public MenuItem read(Serializable id) {
		try {
			MenuItem menuItem = (MenuItem) baseDao.get(MenuItem.class, id);
			return menuItem;
		} catch (Exception e) {
			logger.error("读取菜单信息失败", e);
		}
		return null;
	}

	@Override
	public boolean save(MenuItem menuItem) throws DaoException {
		try {
			return baseDao.save(menuItem);
		} catch (Exception e) {
			logger.error("保存菜单信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		MenuItem menuItem = read(id);
		try {
			return baseDao.delete(menuItem);
		} catch (Exception e) {
			logger.error("删除菜单信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(MenuItem menuItem) throws DaoException {
		try {
			return baseDao.update(menuItem);
		} catch (Exception e) {
			logger.error("更新菜单信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(MenuItem.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<MenuItem> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MenuItem.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<MenuItem> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(MenuItem.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
