package com.dahantc.erp.vo.userreport.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.userreport.entity.UserReport;

public interface IUserReportService {
	UserReport read(Serializable id) throws ServiceException;

	boolean save(UserReport entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(UserReport enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<UserReport> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<UserReport> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<UserReport> readUserReportByRoleAndMenuId(String roleId, String menuId) throws ServiceException;

	List<UserReport> readUserReportByRole(String roleId) throws ServiceException;

	boolean updateByBatch(List<UserReport> objs) throws ServiceException;

	boolean saveByBatch(List<UserReport> objs) throws ServiceException;

	boolean deleteByBatch(List<UserReport> objs) throws ServiceException;

	List<String> queryHasReport(Integer type, String userId, List<String> deptIdList, Date startDate, Date endDate);
}
