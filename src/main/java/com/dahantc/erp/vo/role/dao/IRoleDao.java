package com.dahantc.erp.vo.role.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.role.entity.Role;

public interface IRoleDao {
	Role read(Serializable id) throws DaoException;

	boolean save(Role entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Role enterprise) throws DaoException;

	int getCount(SearchFilter filter) throws DaoException;

	PageResult<Role> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Role> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	Role readOneByProperty(String property, Object value) throws BaseException;

	List<Role> queryByHql(String hql, Map<String,Object> params ,Integer max) throws DaoException;
}
