package com.dahantc.erp.vo.role.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.role.AddRoleReqDto;
import com.dahantc.erp.dto.role.EditRoleReqDto;
import com.dahantc.erp.vo.role.entity.Role;

public interface IRoleService {
	Role read(Serializable id) throws ServiceException;

	boolean save(Role entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Role enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Role> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Role> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	Role readOneByProperty(String property, Object value) throws ServiceException;

	BaseResponse<String> addRole(AddRoleReqDto roleReqDto, String creatorid) throws ServiceException;

	BaseResponse<String> updateRole(EditRoleReqDto roleReqDto) throws ServiceException;

	Map<String, Boolean> getPagePermission(Serializable id) throws ServiceException;

	/**
	 * 校验 角色 访问菜单路径是否正确
	 * 
	 * @param roleId
	 *            角色id
	 * @param url
	 *            访问路径
	 * @return 校验结果
	 */
	boolean checkRoleMenuUrl(String roleId, String url);

	/**
	 * 根据ID 批量查询 角色信息
	 * 
	 * @param roleIds
	 *            角色Id
	 * @return 角色信息
	 */
	List<Role> queryRoleByIds(List<String> roleIds);
	
	List<Role> findByHql(String hql, Map<String, Object> params, Integer max);

	/**
	 * 查找用户的所有角色信息
	 * 
	 * @param userId
	 *            用户
	 * @return 角色信息
	 */
	List<Role> findUserAllRole(String userId);
}
