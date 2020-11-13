package com.dahantc.erp.vo.dept.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.department.DeptOrUserInfo;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.role.entity.Role;

public interface IDepartmentService {

	Department read(Serializable id) throws ServiceException;

	List<Department> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	/**
	 * 查找所有下属部门
	 *
	 * @param deptId
	 * @return
	 * @throws ServiceException
	 */
	Set<Department> getSubDept(String deptId) throws ServiceException;

	/**
	 * 查询直属子部门
	 *
	 * @param pid
	 *            父部门id
	 * @param deptIdFilter
	 *            查询条件的 部门Id
	 * @return 部门信息
	 */
	List<Department> getDeptByFatherId(String pid, List<String> deptIdFilter);

	/**
	 * 查询部门的子部门Id集合
	 *
	 * @param deptId
	 *            部门id
	 * @return List<String>
	 */
	List<String> getSubDeptIds(String deptId);

	/**
	 * 查询部门的根节点
	 *
	 * @return List<Department>
	 */
	List<Department> getRootDept();

	/**
	 * 查询所有有效的部门信息
	 * 
	 * @return 部门信息
	 */
	List<Department> queryAll();

	/**
	 * 获取指定部门信息的中的子部门信息
	 *
	 * @param allDepartment
	 *            需要查询的部门信息
	 * @param fatherDeptId
	 *            需要查询的父节点id
	 * @return 子部门
	 */
	List<Department> getChildDepts(List<Department> allDepartment, String fatherDeptId);

	/**
	 * 按数据权限获取用户的部门id列表（部门+子部门）
	 * 
	 * @param user
	 *            当前用户
	 * @return 部门id列表
	 */
	List<String> getDeptIdsByPermission(OnlineUser user);

	/**
	 * 查询部门的上级部门直到根部门的Id集合
	 *
	 * @param deptId
	 *            部门id
	 * @return List<String>
	 */
	List<String> getSuperDeptIds(String deptId);

	/**
	 * 查询部门名称
	 *
	 * @param ids id
	 * @return 部门 id -> 名称
	 */
	Map<String, String> queryDeptName(List<String> ids);

	List<String> getDeptIdsByPermission(OnlineUser user, Role role);

	List<DeptOrUserInfo> getDirectChildAndUser(OnlineUser user, String deptId);

	List<DeptOrUserInfo> getDeptAndUser(OnlineUser onlineUser, String deptId, Boolean showAll);
}
