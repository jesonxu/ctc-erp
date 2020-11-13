package com.dahantc.erp.vo.menuItem.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.menuItem.dao.IMenuItemDao;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;
import com.dahantc.erp.vo.menuItem.service.IMenuItemService;

@Service("menuItemService")
public class MenuItemServiceImpl implements IMenuItemService {
	private static Logger logger = LogManager.getLogger(MenuItemServiceImpl.class);

	@Autowired
	private IMenuItemDao menuItemDao;

	@Override
	public MenuItem read(Serializable id) throws ServiceException {
		try {
			return menuItemDao.read(id);
		} catch (Exception e) {
			logger.error("读取菜单信息失败", e);
			throw new ServiceException("读取菜单信息失败", e);
		}
	}

	@Override
	public boolean save(MenuItem menuItem) throws ServiceException {
		try {
			return menuItemDao.save(menuItem);
		} catch (Exception e) {
			logger.error("保存菜单信息失败", e);
			throw new ServiceException("保存菜单信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return menuItemDao.delete(id);
		} catch (Exception e) {
			logger.error("删除菜单信息失败", e);
			throw new ServiceException("删除菜单信息失败", e);
		}
	}

	@Override
	public boolean update(MenuItem enterprise) throws ServiceException {
		try {
			return menuItemDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新菜单信息失败", e);
			throw new ServiceException("更新菜单信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return menuItemDao.getCount(filter);
		} catch (Exception e) {
			logger.error("查询菜单信息数量失败", e);
			throw new ServiceException("查询菜单信息数量失败", e);
		}
	}

	@Override
	public PageResult<MenuItem> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return menuItemDao.queryByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询菜单分页信息失败", e);
			throw new ServiceException("查询菜单分页信息失败", e);
		}
	}

	@Override
	public List<MenuItem> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return menuItemDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询菜单信息数量失败", e);
			throw new ServiceException("查询菜单信息数量失败", e);
		}
	}
}
