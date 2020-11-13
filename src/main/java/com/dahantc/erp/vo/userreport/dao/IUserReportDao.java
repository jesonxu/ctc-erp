package com.dahantc.erp.vo.userreport.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.userreport.entity.UserReport;

public interface IUserReportDao {
	UserReport read(Serializable id) throws DaoException;

	boolean save(UserReport entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(UserReport enterprise) throws DaoException;

	int getCount(SearchFilter filter) throws DaoException;

	PageResult<UserReport> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<UserReport> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	List<UserReport> readUserReportByRoleAndMenuId(String roleId, String menuId) throws DaoException;

	List<UserReport> readUserReportByRole(String roleId) throws DaoException;

	boolean updateByBatch(List<UserReport> objs) throws DaoException;

	boolean saveByBatch(List<UserReport> objs) throws DaoException;

	boolean deleteByBatch(List<UserReport> objs) throws DaoException;
}
