package com.dahantc.erp.vo.menuItem.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;


public interface IMenuItemService {
	MenuItem read(Serializable id) throws ServiceException;

	boolean save(MenuItem entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(MenuItem enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<MenuItem> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<MenuItem> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
}
