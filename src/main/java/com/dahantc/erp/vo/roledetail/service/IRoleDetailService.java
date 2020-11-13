package com.dahantc.erp.vo.roledetail.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;

public interface IRoleDetailService {
	RoleDetail read(Serializable id) throws ServiceException;

	boolean save(RoleDetail entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(RoleDetail enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<RoleDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<RoleDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<RoleDetail> readRoleDetailByRoleAndMenuId(String roleId, String menuId) throws ServiceException;

	List<RoleDetail> readRoleDetailByRole(String roleId) throws ServiceException;

	boolean updateByBatch(List<RoleDetail> objs) throws ServiceException;

	boolean saveByBatch(List<RoleDetail> objs) throws ServiceException;

	boolean deleteByBatch(List<RoleDetail> objs) throws ServiceException;
}
