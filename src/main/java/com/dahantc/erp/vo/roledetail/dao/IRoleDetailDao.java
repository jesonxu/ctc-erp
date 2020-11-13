package com.dahantc.erp.vo.roledetail.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;

public interface IRoleDetailDao {
	RoleDetail read(Serializable id) throws DaoException;

	boolean save(RoleDetail entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(RoleDetail enterprise) throws DaoException;

	int getCount(SearchFilter filter) throws DaoException;

	PageResult<RoleDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<RoleDetail> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	List<RoleDetail> readRoleDetailByRoleAndMenuId(String roleId, String menuId) throws DaoException;

	List<RoleDetail> readRoleDetailByRole(String roleId) throws DaoException;

	boolean updateByBatch(List<RoleDetail> objs) throws DaoException;

	boolean saveByBatch(List<RoleDetail> objs) throws DaoException;

	boolean deleteByBatch(List<RoleDetail> objs) throws DaoException;
}
