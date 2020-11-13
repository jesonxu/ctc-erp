package com.dahantc.erp.vo.menuItem.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;

public interface IMenuItemDao {
	MenuItem read(Serializable id);

	boolean save(MenuItem entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(MenuItem enterprise) throws DaoException;

	int getCount(SearchFilter filter) throws DaoException;

	PageResult<MenuItem> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<MenuItem> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
}
