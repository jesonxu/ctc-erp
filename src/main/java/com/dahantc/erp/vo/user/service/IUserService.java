package com.dahantc.erp.vo.user.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.AuthenticationException;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.personalcenter.PersonalInfoRespDto;
import com.dahantc.erp.vo.user.entity.User;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
public interface IUserService {
	User read(Serializable id) throws ServiceException;

	boolean save(User entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(User enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<User> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<User> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	User login(String loginName, String passWord) throws AuthenticationException;

	User loginWithRandomCode(String loginName, String passWord, String randomCode, boolean testModel) throws AuthenticationException;

	User findAdmin();

	List<User> readUsers(OnlineUser onlineUser, String searchUserId, String realName, String searchDeptIds);

	List<User> readUsersByRole(OnlineUser onlineUser, String roleId, String searchUserId, String realName, String searchDeptIds);

	/**
	 * 根据用户的ids 查找对应的名称
	 *
	 * @param ids
	 *            用户id
	 * @return 用户 id -名称
	 */
	Map<String, String> findUserNameByIds(List<String> ids);

	Map<String, User> findUserByIds(List<String> ids);

	/**
	 * 联表查询，获取每个用户的部门和上级部门，不包括顶级部门，如果上级部门是顶级部门，则上级部门也显示为用户的部门
	 *
	 * @return ossUserId -> {realName, deptName, parentDeptName}
	 */
	HashMap<String, HashMap<String, String>> getUserAndDeptName();

	List<User> findByHql(String hql, Map<String, Object> params, Integer max);

	/** 查询个人信息 */
	PersonalInfoRespDto queryPersonInfo(User onlineUser);

	Map<String, List<String>> getDealRoleAndUser(User user, String roleId);

	List<String> getDealUserId(User user, String roleId);

	Map<String, List<User>> findAllDeptLeader(List<String> deptIdList);

	/**
	 * 联表查询，获取每个用户的部门
	 *
	 * @return ossUserId -> deptId
	 */
	Map<String, String> getUserAndDept(boolean all);

	Map<String, User> getUserMap(boolean all);

	List<User> findUserAllLeader(User user);

	List<User> findUserAllLeader(String userId);

	List<User> findUserByDept(String deptId);

	boolean updateByBatch(List<User> objs) throws ServiceException;

	boolean saveByBatch(List<User> objs) throws ServiceException;

	boolean deleteByBatch(List<User> objs) throws ServiceException;
}
