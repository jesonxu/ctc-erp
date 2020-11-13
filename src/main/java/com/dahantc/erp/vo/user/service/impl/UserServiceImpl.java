package com.dahantc.erp.vo.user.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.vo.dept.entity.Department;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.AuthenticationException;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.UserInfo;
import com.dahantc.erp.commom.VerifyUtil;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.personalcenter.PersonalInfoRespDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.dao.IUserDao;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
@Service("userService")
public class UserServiceImpl implements IUserService {
	private static Logger logger = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	private IUserDao userDao;

	@Resource
	private IBaseDao baseDao;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Override
	public User read(Serializable id) throws ServiceException {
		try {
			return userDao.read(id);
		} catch (Exception e) {
			logger.error("读取用户信息失败", e);
			throw new ServiceException("读取用户信息失败", e);
		}
	}

	@Override
	public boolean save(User entity) throws ServiceException {
		try {
			return userDao.save(entity);
		} catch (Exception e) {
			logger.error("保存用户信息失败", e);
			throw new ServiceException("保存用户信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return userDao.delete(id);
		} catch (Exception e) {
			logger.error("删除用户信息失败", e);
			throw new ServiceException("删除用户信息失败", e);
		}
	}

	@Override
	public boolean update(User enterprise) throws ServiceException {
		try {
			return userDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新用户信息失败", e);
			throw new ServiceException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return userDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询用户信息数量失败", e);
			throw new ServiceException("查询用户信息数量失败", e);
		}
	}

	@Override
	public PageResult<User> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return userDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询用户信息分页信息失败", e);
			throw new ServiceException("查询用户信息分页信息失败", e);
		}
	}

	@Override
	public List<User> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return userDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("条件查询用户信息失败", e);
			throw new ServiceException("条件查询用户信息失败", e);
		}
	}

	/** 根据用户名和密码验证用户 */
	@Override
	public User login(String loginName, String passWord) throws AuthenticationException {
		User user = null;
		String msg = null;
		UserInfo userInfo = VerifyUtil.userMap.get(loginName);
		if (userInfo != null) {
			if (userInfo.getNextReuestTime() > System.currentTimeMillis()) {
				userInfo.setFailSize(0);
				msg = "密码已连续错误" + VerifyUtil.MAX_FAIL_SIZE + "次，请" + ((userInfo.getNextReuestTime() - System.currentTimeMillis()) / 60000 + 1) + "分钟后再试";
				throw new AuthenticationException(msg);
			} else {
				VerifyUtil.userMap.remove(loginName);
			}
		}
		try {
			user = userDao.readOneByProperty("loginName", loginName);
		} catch (BaseException e) {
			msg = "登录异常";
			logger.error(msg, e);
			throw new AuthenticationException(msg, e);
		}
		if (user == null) {
			msg = "该账号无效";
			throw new AuthenticationException(msg);
		} else {
			if (user.getStatus() == EntityStatus.DELETED.ordinal()) {
				msg = "登录过期";
				throw new AuthenticationException(msg);
			}
			if (user.getUstate() != UserStatus.DISABLED.ordinal()) {
				if (user.getWebPwd().equals(passWord)) {
					VerifyUtil.userMap.remove(loginName);
					return user;
				} else {
					if (userInfo == null) {
						userInfo = new UserInfo();
						userInfo.setLoginName(loginName);
					}
					int failSize = userInfo.addFailSize();
					if (failSize >= VerifyUtil.MAX_FAIL_SIZE) {
						msg = "密码已连续错误" + VerifyUtil.MAX_FAIL_SIZE + "次，请" + VerifyUtil.DISABLED_TIME + "分钟后再试";
						userInfo.setNextReuestTime(System.currentTimeMillis() + VerifyUtil.DISABLED_TIME * 60 * 1000);
					} else {
						msg = "密码错误，连续错误" + VerifyUtil.MAX_FAIL_SIZE + "次将禁用" + VerifyUtil.DISABLED_TIME + "分钟，剩余" + (VerifyUtil.MAX_FAIL_SIZE - failSize)
								+ "次";
					}
					VerifyUtil.userMap.put(loginName, userInfo);
					throw new AuthenticationException(msg);
				}
			} else {
				msg = "该账号已禁用";
				throw new AuthenticationException(msg);
			}
		}
	}

	/** 根据用户名和密码验证用户 */
	@Override
	public User loginWithRandomCode(String loginName, String passWord, String randomCode, boolean testModel) throws AuthenticationException {
		User user = null;
		String msg = null;
		UserInfo userInfo = VerifyUtil.userMap.get(loginName);
		if (userInfo != null) {
			if (userInfo.getNextReuestTime() > System.currentTimeMillis()) {
				userInfo.setFailSize(0);
				msg = "密码或手机验证码已连续错误" + VerifyUtil.MAX_FAIL_SIZE + "次，请" + ((userInfo.getNextReuestTime() - System.currentTimeMillis()) / 60000 + 1) + "分钟后再试";
				throw new AuthenticationException(msg);
			} else {
				VerifyUtil.userMap.remove(loginName);
			}
		}
		try {
			user = userDao.readOneByProperty("loginName", loginName);
		} catch (BaseException e) {
			msg = "登录异常";
			logger.error(msg, e);
			throw new AuthenticationException(msg, e);
		}
		if (user == null) {
			msg = "该账号无效";
			throw new AuthenticationException(msg);
		} else {
			if (user.getStatus() == EntityStatus.DELETED.ordinal()) {
				msg = "登录过期";
				throw new AuthenticationException(msg);
			}
			if (user.getUstate() != UserStatus.DISABLED.ordinal()) {
				if (user.getWebPwd().equals(passWord) && (testModel || randomCode.equals(VerifyUtil.randomCodeMap.get(loginName)))) {
					VerifyUtil.randomCodeMap.remove(loginName);
					Long lastTime = VerifyUtil.timeMap.remove(loginName);
					if (testModel || (null != lastTime && System.currentTimeMillis() - lastTime < 3 * 60 * 1000)) {
						VerifyUtil.userMap.remove(loginName);
						return user;
					} else {
						msg = "验证码已过有效期，请重新获取";
						throw new AuthenticationException(msg);
					}

				} else {
					if (userInfo == null) {
						userInfo = new UserInfo();
						userInfo.setLoginName(loginName);
					}
					int failSize = userInfo.addFailSize();
					if (failSize >= VerifyUtil.MAX_FAIL_SIZE) {
						msg = "密码或手机验证码已连续错误" + VerifyUtil.MAX_FAIL_SIZE + "次，请" + VerifyUtil.DISABLED_TIME + "分钟后再试";
						userInfo.setNextReuestTime(System.currentTimeMillis() + VerifyUtil.DISABLED_TIME * 60 * 1000);
					} else {
						msg = "密码或手机验证码错误，连续错误" + VerifyUtil.MAX_FAIL_SIZE + "次将禁用" + VerifyUtil.DISABLED_TIME + "分钟，剩余" + (VerifyUtil.MAX_FAIL_SIZE - failSize)
								+ "次";
					}
					VerifyUtil.userMap.put(loginName, userInfo);
					throw new AuthenticationException(msg);
				}
			} else {
				msg = "该账号已禁用";
				throw new AuthenticationException(msg);
			}
		}
	}

	@Override
	public User findAdmin() {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("loginName", Constants.ROP_EQ, "admin"));
		try {
			List<User> users = queryAllBySearchFilter(searchFilter);
			if (users != null && !users.isEmpty()) {
				return users.get(0);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询当前登录用户的所有下属，包括自己（按数据权限，可带过滤条件）
	 * 
	 * @param onlineUser
	 *            登录用户
	 * @param ossUserId
	 *            过滤条件 用户id
	 * @param realName
	 *            过滤条件 用户姓名
	 * @param deptIds
	 *            过滤条件 部门id
	 * @return
	 */
	@Override
	public List<User> readUsers(OnlineUser onlineUser, String ossUserId, String realName, String deptIds) {
		String roleId = onlineUser.getRoleId();
		return readUsersByRole(onlineUser, roleId, ossUserId, realName, deptIds);
	}

	/**
	 * 查询当前登录用户指定角色的所有下属（按数据权限，可带过滤条件）
	 *
	 * @param onlineUser
	 *            登录用户
	 * @param roleId
	 *            指定角色
	 * @param searchUserId
	 *            过滤条件 用户id
	 * @param realName
	 *            过滤条件 用户姓名
	 * @param searchDeptIds
	 *            过滤条件 部门id
	 * @return
	 */
	@Override
	public List<User> readUsersByRole(OnlineUser onlineUser, String roleId, String searchUserId, String realName, String searchDeptIds) {
		// 查询结果
		List<User> userList = null;
		Role role = null;
		try {
			role = roleService.read(roleId);
		} catch (ServiceException e) {
			logger.error("查询角色信息异常", e);
		}
		if (role == null) {
			return null;
		}
		// 部门id过滤条件
		List<String> searchDeptIdList = new ArrayList<>();
		if (StringUtil.isNotBlank(searchDeptIds)) {
			String[] deptIdsArr = searchDeptIds.split(",");
			searchDeptIdList.addAll(Arrays.asList(deptIdsArr));
		}
		// 角色数据权限
		int dataPermission = role.getDataPermission();
		if (DataPermission.Self.ordinal() == dataPermission) {
			userList = readUsersBySelf(onlineUser, searchUserId, realName, searchDeptIdList);
		} else if (DataPermission.Dept.ordinal() == dataPermission) {
			userList = readUsersByDept(onlineUser, searchUserId, realName, searchDeptIdList);
		} else if (DataPermission.All.ordinal() == dataPermission) {
			userList = readUsersByAll(onlineUser, searchUserId, realName, searchDeptIdList);
		} else if (DataPermission.Customize.ordinal() == dataPermission) {
			userList = readUsersByCustomize(onlineUser, searchUserId, realName, searchDeptIdList);
		} else if (DataPermission.Flow.ordinal() == dataPermission) {
			userList = readUsersByFlow(onlineUser, searchUserId, realName, searchDeptIdList);
		}
		return userList;
	}

	/**
	 * 按自己权限查询用户
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param searchUserId
	 *            过滤条件 用户id
	 * @param reanName
	 *            过滤条件 用户姓名
	 * @param searchDeptIdList
	 *            过滤条件 部门id
	 * @return
	 */
	private List<User> readUsersBySelf(OnlineUser onlineUser, String searchUserId, String reanName, List<String> searchDeptIdList) {
		logger.info("按自己权限查用户开始");
		List<User> userList = null;
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		if (!ListUtils.isEmpty(searchDeptIdList)) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchDeptIdList));
		}
		if (StringUtil.isNotBlank(searchUserId)) {
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, searchUserId));
		}
		if (StringUtil.isNotBlank(reanName)) {
			filter.getRules().add(new SearchRule("realName", Constants.ROP_CN, reanName));
		}
		try {
			userList = queryAllBySearchFilter(filter);
			logger.info("按自己权限查用户结束，查询到用户数：" + (ListUtils.isEmpty(userList) ? 0 : userList.size()));
		} catch (Exception e) {
			logger.error("按自己权限查用户异常，当前用户：" + onlineUser.getUser().getRealName());
		}
		return userList;
	}

	/**
	 * 按部门权限查询用户
	 * 
	 * @param onlineUser
	 *            当前用户
	 * @param searchUserId
	 *            过滤条件 用户id
	 * @param reanName
	 *            过滤条件 用户姓名
	 * @param searchDeptIdList
	 *            过滤条件 部门id
	 * @return
	 */
	private List<User> readUsersByDept(OnlineUser onlineUser, String searchUserId, String reanName, List<String> searchDeptIdList) {
		logger.info("按部门权限查用户开始");
		List<User> userList = null;
		// 找用户当前角色数据权限下的部门
		List<String> userDeptIdList = departmentService.getDeptIdsByPermission(onlineUser);
		if (!ListUtils.isEmpty(userDeptIdList)) {
			// 用户部门 与 搜索部门 取交集
			if (!ListUtils.isEmpty(searchDeptIdList)) {
				userDeptIdList.retainAll(searchDeptIdList);
			}
			if (!ListUtils.isEmpty(userDeptIdList)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, userDeptIdList));
				if (StringUtil.isNotBlank(searchUserId)) {
					filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, searchUserId));
				}
				if (StringUtil.isNotBlank(reanName)) {
					filter.getRules().add(new SearchRule("realName", Constants.ROP_CN, reanName));
				}
				try {
					userList = queryAllBySearchFilter(filter);
					logger.info("按部门权限查用户结束，查询到用户数：" + (ListUtils.isEmpty(userList) ? 0 : userList.size()));
				} catch (Exception e) {
					logger.error("按部门权限查用户异常，当前用户：" + onlineUser.getUser().getRealName());
				}
			}
		}
		return userList;
	}

	/**
	 * 按全部权限查询用户
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param searchUserId
	 *            过滤条件 用户id
	 * @param reanName
	 *            过滤条件 用户姓名
	 * @param searchDeptIdList
	 *            过滤条件 部门id
	 * @return
	 */
	private List<User> readUsersByAll(OnlineUser onlineUser, String searchUserId, String reanName, List<String> searchDeptIdList) {
		logger.info("按全部权限查用户开始");
		List<User> userList = null;
		SearchFilter filter = new SearchFilter();
		if (!ListUtils.isEmpty(searchDeptIdList)) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchDeptIdList));
		}
		if (StringUtil.isNotBlank(searchUserId)) {
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, searchUserId));
		}
		if (StringUtil.isNotBlank(reanName)) {
			filter.getRules().add(new SearchRule("realName", Constants.ROP_CN, reanName));
		}
		try {
			userList = queryAllBySearchFilter(filter);
			logger.info("按全部权限查用户结束，查询到用户数：" + (ListUtils.isEmpty(userList) ? 0 : userList.size()));
		} catch (Exception e) {
			logger.error("按全部权限查用户异常，当前用户：" + onlineUser.getUser().getRealName());
		}
		return userList;
	}

	/**
	 * 按自定义权限查询用户
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param searchUserId
	 *            过滤条件 用户id
	 * @param reanName
	 *            过滤条件 用户姓名
	 * @param searchDeptIdList
	 *            过滤条件 部门id
	 * @return
	 */
	private List<User> readUsersByCustomize(OnlineUser onlineUser, String searchUserId, String reanName, List<String> searchDeptIdList) {
		logger.info("按自定义权限查用户开始");
		List<User> userList = null;
		// 找用户当前角色数据权限下的部门
		List<String> userDeptIdList = departmentService.getDeptIdsByPermission(onlineUser);
		if (!ListUtils.isEmpty(userDeptIdList)) {
			// 用户部门 与 搜索部门 取交集
			if (!ListUtils.isEmpty(searchDeptIdList)) {
				userDeptIdList.retainAll(searchDeptIdList);
			}
			if (!ListUtils.isEmpty(userDeptIdList)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, userDeptIdList));
				if (StringUtil.isNotBlank(searchUserId)) {
					filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, searchUserId));
				}
				if (StringUtil.isNotBlank(reanName)) {
					filter.getRules().add(new SearchRule("realName", Constants.ROP_CN, reanName));
				}
				try {
					userList = queryAllBySearchFilter(filter);
					logger.info("按自定义权限查用户结束，查询到用户数：" + (ListUtils.isEmpty(userList) ? 0 : userList.size()));
				} catch (Exception e) {
					logger.error("按自定义权限查用户异常，当前用户：" + onlineUser.getUser().getRealName());
				}
			}
		}
		return userList;
	}

	/**
	 * 按流程权限查询用户
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param searchUserId
	 *            过滤条件 用户id
	 * @param reanName
	 *            过滤条件 用户姓名
	 * @param searchDeptIdList
	 *            过滤条件 部门id
	 * @return
	 */
	private List<User> readUsersByFlow(OnlineUser onlineUser, String searchUserId, String reanName, List<String> searchDeptIdList) {
		logger.info("按流程权限查用户开始");
		List<User> userList = null;
		SearchFilter filter = new SearchFilter();
		// TODO 按流程权限查客户
		if (!ListUtils.isEmpty(searchDeptIdList)) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchDeptIdList));
		}
		if (StringUtil.isNotBlank(searchUserId)) {
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, searchUserId));
		}
		if (StringUtil.isNotBlank(reanName)) {
			filter.getRules().add(new SearchRule("realName", Constants.ROP_CN, reanName));
		}
		try {
			userList = queryAllBySearchFilter(filter);
			logger.info("按流程权限查用户结束，查询到用户数：" + (ListUtils.isEmpty(userList) ? 0 : userList.size()));
		} catch (Exception e) {
			logger.error("按流程权限查用户异常，当前用户：" + onlineUser.getUser().getRealName());
		}
		return userList;
	}

	/**
	 * 根据用户的ids 查找对应的名称
	 *
	 * @param ids
	 *            用户id
	 * @return 用户 id -名称
	 */
	@Override
	public Map<String, String> findUserNameByIds(List<String> ids) {
		Map<String, String> userNames = new HashMap<>();
		if (ids == null || ids.isEmpty()) {
			return userNames;
		}
		String hql = "select ossUserId ,realName from User where ossUserId in (:ids)";
		Map<String, Object> param = new HashMap<>();
		param.put("ids", new ArrayList<>(new HashSet<>(ids)));
		List<Object> userInfos = null;
		try {
			userInfos = baseDao.findByhql(hql, param, Integer.MAX_VALUE);
		} catch (BaseException e) {
			logger.error("根据用户id查询名称异常", e);
		}
		if (userInfos != null && !userInfos.isEmpty()) {
			userInfos.forEach(userInfo -> {
				if (userInfo.getClass().isArray()) {
					Object[] row = (Object[]) userInfo;
					if (row.length >= 2) {
						userNames.put(String.valueOf(row[0]), String.valueOf(row[1]));
					}
				}
			});
		}
		return userNames;
	}

	@Override
	public Map<String, User> findUserByIds(List<String> ids) {
		Map<String, User> users = new HashMap<>();
		if (ids == null || ids.isEmpty()) {
			return users;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, new ArrayList<>(new HashSet<>(ids))));
		List<User> userInfos = null;
		try {
			userInfos = queryAllBySearchFilter(searchFilter);
			if (userInfos != null && !userInfos.isEmpty()) {
				userInfos.forEach(user -> users.put(user.getOssUserId(), user));
			}
		} catch (BaseException e) {
			logger.error("根据用户id查询名称异常", e);
		}
		return users;
	}

	/**
	 * 联表查询，获取每个用户的部门和上级部门，不包括顶级部门，如果上级部门是顶级部门，则上级部门也显示为用户的部门
	 *
	 * @return ossUserId -> {realName, deptName, parentDeptName}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, HashMap<String, String>> getUserAndDeptName() {
		logger.info("获取用户和部门名称对应关系开始");
		long _start = System.currentTimeMillis();
		HashMap<String, HashMap<String, String>> nameMap = new HashMap<>();
		String sql = "select u.ossuserid,u.realname,d.deptname,p.deptname parentDept" + " from erp_user u left join erp_department d on u.deptid = d.deptid"
				+ " left join erp_department p on (d.parentid = p.deptid and p.deptid != '1')";
		try {
			List<Object[]> nameList = (List<Object[]>) baseDao.selectSQL(sql);
			if (nameList != null && nameList.size() > 0) {
				for (Object[] data : nameList) {
					HashMap<String, String> info = new HashMap<>();
					info.put("realName", (String) data[1]);
					String deptName = (data[2] == null) ? "-" : (String) data[2];
					info.put("deptName", deptName);
					String parentDeptName = (data[3] == null) ? deptName : (String) data[3];
					info.put("parentDeptName", parentDeptName);
					String regionName = "-";
					// 部门表没有区域字段，这里用事业部名的括号里的名称
					if (parentDeptName.contains("(") && parentDeptName.contains(")")) {
						regionName = parentDeptName.substring(parentDeptName.indexOf("(") + 1, parentDeptName.indexOf(")"));
					}
					info.put("regionName", regionName);
					nameMap.put((String) data[0], info);
				}
			}
			logger.info("获取用户和部门名称对应关系结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.info("获取用户和部门名称对应关系异常", e);
		}
		return nameMap;
	}

	/**
	 * 联表查询，获取每个用户的部门
	 *
	 * @return ossUserId -> deptId
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getUserAndDept(boolean all) {
		logger.info("获取用户和部门对应关系开始");
		long _start = System.currentTimeMillis();
		Map<String, String> userDeptMap = new HashMap<>();
		String sql = "select u.ossuserid,d.deptid from erp_user u left join erp_department d on u.deptid = d.deptid";
		if (!all) {
			sql += " where u.status = 1 and u.uState = 1";
		}
		try {
			List<Object[]> dataList = (List<Object[]>) baseDao.selectSQL(sql);
			if (dataList != null && dataList.size() > 0) {
				dataList.forEach(data -> {
					userDeptMap.put((String) data[0], (String) data[1]);
				});
			}
			logger.info("获取用户和部门对应关系结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.info("获取用户和部门对应关系异常", e);
		}
		return userDeptMap;
	}

	@Override
	public List<User> findByHql(String hql, Map<String, Object> params, Integer max) {
		try {
			return userDao.queryByHql(hql, params, max);
		} catch (DaoException e) {
			logger.error("Hql查询用户角色信息错误", e);
		}
		return null;
	}

	@Override
	public PersonalInfoRespDto queryPersonInfo(User user) {
		PersonalInfoRespDto dto = new PersonalInfoRespDto();
		try {
			dto.setOssUserId(user.getOssUserId());
			dto.setLoginName(user.getLoginName());
			dto.setUserName(user.getRealName());
			dto.setSex(user.getSex());
			String deptName = "未知";
			if (StringUtil.isNotBlank(user.getDeptId())) {
				Department dept = departmentService.read(user.getDeptId());
				if (null != dept) {
					deptName = dept.getDeptname();
				}
			}
			dto.setDeptName(deptName);
			dto.setIdentityType(user.getIdentityType());
			dto.setIdentityTypeName(IdentityType.getIdentityType(user.getIdentityType()));
			dto.setEntryTime(user.getEntryTime() == null ? "" : DateUtil.convert(user.getEntryTime(), DateUtil.format1));
			dto.setJob(JobType.getJobTypes(user.getJobType()));
			dto.setJobTypes(user.getJobType());
			dto.setOfficeAddress(user.getOfficeAddress());
			dto.setMail(user.getContacteMail());
			dto.setMobilePhone(user.getContactMobile());
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, user.getDeptId()));
			searchFilter.getRules().add(new SearchRule("identityType", Constants.ROP_EQ, IdentityType.LEADER_IN_DEPT.ordinal()));
			List<User> userList = queryAllBySearchFilter(searchFilter);
			if (!CollectionUtils.isEmpty(userList)) {
				dto.setSuperior(userList.get(0).getRealName());
			}
			dto.setTelephone(user.getContactPhone());
			dto.setBirthday(user.getBirthday() == null ? "" : DateUtil.convert(user.getBirthday(), DateUtil.format1));
			// 查用户角色
			List<Role> roleList = roleService.findUserAllRole(user.getOssUserId());
			if (!roleList.isEmpty()) {
				dto.setRoleIds(roleList.stream().map(Role::getRoleid).collect(Collectors.joining(",")));
				dto.setRoles(roleList.stream().map(Role::getRolename).collect(Collectors.joining("，")));
			}
			dto.setGraduationDate(user.getGraduationDate() == null ? "" : DateUtil.convert(user.getGraduationDate(), DateUtil.format1));
			dto.setMaritalStatus(user.getMaritalStatus());
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return dto;
	}

	/**
	 * 根据节点设计中的角色，找到真实能处理节点的角色和用户
	 * 
	 * @param user
	 *            当前用户
	 * @param roleId
	 *            节点设计中选择的角色，其中有多个角色Id以,分隔
	 * @return 真实能处理的角色和有该角色的用户 {roleId -> [ossUserId]}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> getDealRoleAndUser(User user, String roleId) {
		logger.info("获取真实能处理流程节点的角色和用户开始");
		Map<String, List<String>> dealRoleAndUser = new HashMap<>();
		if (null == user || StringUtil.isBlank(roleId)) {
			return dealRoleAndUser;
		}
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("roleid", Constants.ROP_IN, Arrays.asList(roleId.split(","))));
			List<Role> roles = roleService.queryAllBySearchFilter(filter);
			List<String> dealUsers = null;
			// 遍历每个可处理此流程的角色，找到真实能处理这个流程的角色，和有这些角色的人
			for (Role dealRole : roles) {
				int dataPermission = dealRole.getDataPermission();
				if (DataPermission.Self.ordinal() == dataPermission) {
					// 自己权限，找到发起人
					String sql = "select distinct u.realname from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid" + " where r.roleid='"
							+ dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = " + EntityStatus.NORMAL.ordinal()
							+ " and u.ossuserid = '" + user.getOssUserId() + "'";
					List<Object> nameList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(nameList)) {
						List<String> names = nameList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
						dealRoleAndUser.put(dealRole.getRoleid(), names);
					}
				} else if (DataPermission.Dept.ordinal() == dataPermission) {
					// 部门权限，找到发起人所在部门，以及其所有上级部门，在这些部门中有部门权限的人
					List<String> superDeptIds = departmentService.getSuperDeptIds(user.getDeptId());
					superDeptIds.add(user.getDeptId());
					superDeptIds = superDeptIds.stream().map(dept -> "'" + dept + "'").collect(Collectors.toList());
					String sql = "select distinct u.realname" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
							+ " where r.roleid='" + dealRole.getRoleid() + "' and u.deptid in (" + String.join(",", superDeptIds) + ") and u.ustate = "
							+ UserStatus.ACTIVE.ordinal() + " and u.status = " + EntityStatus.NORMAL.ordinal();
					List<Object> nameList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(nameList)) {
						List<String> names = nameList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
						dealRoleAndUser.put(dealRole.getRoleid(), names);
					}
				} else if (DataPermission.All.ordinal() == dataPermission) {
					// 全部权限，找到所有有全部权限的人
					String sql = "select distinct u.realname" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
							+ " where r.roleid='" + dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = "
							+ EntityStatus.NORMAL.ordinal();
					List<Object> nameList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(nameList)) {
						List<String> names = nameList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
						dealRoleAndUser.put(dealRole.getRoleid(), names);
					}
				} else if (DataPermission.Customize.ordinal() == dataPermission) {
					// 自定义权限，首先判断自定义部门中是否包含发起人所在部门，然后找到有这个角色的人
					if (dealRole.getDeptIds().contains(user.getDeptId())) {
						String sql = "select distinct u.realname" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
								+ " where r.roleid='" + dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = "
								+ EntityStatus.NORMAL.ordinal();
						List<Object> nameList = (List<Object>) baseDao.selectSQL(sql);
						if (!ListUtils.isEmpty(nameList)) {
							List<String> names = nameList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
							dealRoleAndUser.put(dealRole.getRoleid(), names);
						}
					}
				} else if (DataPermission.Flow.ordinal() == dataPermission) {
					// 流程权限，找到所有有流程权限的人
					String sql = "select distinct u.realname" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
							+ " where r.roleid='" + dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = "
							+ EntityStatus.NORMAL.ordinal();
					List<Object> nameList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(nameList)) {
						List<String> names = nameList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
						dealRoleAndUser.put(dealRole.getRoleid(), names);
					}
				}
			}
			logger.info("获取真实能处理流程节点的角色和用户结束");
		} catch (Exception e) {
			logger.error("获取真实能处理流程节点的角色和用户异常", e);
		}
		return dealRoleAndUser;
	}

	/**
	 * 获取每个部门的领导
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<User>> findAllDeptLeader(List<String> deptIdList) {
		Map<String, List<User>> leaderMap = new HashMap<>();
		String sql = "select deptid, ossUserId, realName, identitytype from erp_user where identitytype = 1 and uState = 1 and status = 1";
		if (!CollectionUtils.isEmpty(deptIdList)) {
			sql += " and deptid in (" + deptIdList.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")) + ")";
		}
		sql += " group by deptid, ossUserId order by deptid";
		try {
			List<Object[]> dataList = (List<Object[]>) baseDao.selectSQL(sql);
			if (!CollectionUtils.isEmpty(dataList)) {
				for (Object[] data : dataList) {
					String deptId = (String) data[0];
					List<User> leaders = leaderMap.getOrDefault(deptId, new ArrayList<>());
					User u = new User();
					u.setOssUserId((String) data[1]);
					u.setRealName((String) data[2]);
					u.setIdentityType(IdentityType.LEADER_IN_DEPT.ordinal());
					leaders.add(u);
					leaderMap.put(deptId, leaders);
				}
			}
		} catch (BaseException e) {
			logger.error("", e);
		}
		return leaderMap;
	}

	/**
	 * 获取流程处理人
	 * 
	 * @param user
	 *            流程发起人
	 * @param roleId
	 *            流程节点可处理角色
	 * @return
	 */
	@Override
	public List<String> getDealUserId(User user, String roleId) {
		logger.info("获取真实能处理流程节点的用户开始");
		Set<String> dealUser = new HashSet<>();
		if (null == user || StringUtil.isBlank(roleId)) {
			return new ArrayList<>(dealUser);
		}
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("roleid", Constants.ROP_IN, Arrays.asList(roleId.split(","))));
			List<Role> roles = roleService.queryAllBySearchFilter(filter);
			// 遍历每个可处理此流程的角色，找到真实能处理这个流程的角色，和有这些角色的人
			for (Role dealRole : roles) {
				int dataPermission = dealRole.getDataPermission();
				if (DataPermission.Self.ordinal() == dataPermission) {
					// 自己权限，找到发起人
					String sql = "select distinct u.ossuserid from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid" + " where r.roleid='"
							+ dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = " + EntityStatus.NORMAL.ordinal()
							+ " and u.ossuserid = '" + user.getOssUserId() + "'";
					List<Object> dataList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(dataList)) {
						dealUser.addAll(dataList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList()));
					}
				} else if (DataPermission.Dept.ordinal() == dataPermission) {
					// 部门权限，找到发起人所在部门，以及其所有上级部门，在这些部门中有部门权限的人
					List<String> superDeptIds = departmentService.getSuperDeptIds(user.getDeptId());
					superDeptIds.add(user.getDeptId());
					superDeptIds = superDeptIds.stream().map(dept -> "'" + dept + "'").collect(Collectors.toList());
					String sql = "select distinct u.ossuserid" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
							+ " where r.roleid='" + dealRole.getRoleid() + "' and u.deptid in (" + String.join(",", superDeptIds) + ") and u.ustate = "
							+ UserStatus.ACTIVE.ordinal() + " and u.status = " + EntityStatus.NORMAL.ordinal();
					List<Object> dataList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(dataList)) {
						dealUser.addAll(dataList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList()));
					}
				} else if (DataPermission.All.ordinal() == dataPermission) {
					// 全部权限，找到所有有全部权限的人
					String sql = "select distinct u.ossuserid" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
							+ " where r.roleid='" + dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = "
							+ EntityStatus.NORMAL.ordinal();
					List<Object> dataList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(dataList)) {
						dealUser.addAll(dataList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList()));
					}
				} else if (DataPermission.Customize.ordinal() == dataPermission) {
					// 自定义权限，首先判断自定义部门中是否包含发起人所在部门，然后找到有这个角色的人
					if (dealRole.getDeptIds().contains(user.getDeptId())) {
						String sql = "select distinct u.ossuserid" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
								+ " where r.roleid='" + dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = "
								+ EntityStatus.NORMAL.ordinal();
						List<Object> dataList = (List<Object>) baseDao.selectSQL(sql);
						if (!ListUtils.isEmpty(dataList)) {
							dealUser.addAll(dataList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList()));
						}
					}
				} else if (DataPermission.Flow.ordinal() == dataPermission) {
					// 流程权限，找到所有有流程权限的人
					String sql = "select distinct u.ossuserid" + " from erp_role_relation r left join erp_user u on r.ossuserid = u.ossuserid"
							+ " where r.roleid='" + dealRole.getRoleid() + "' and u.ustate = " + UserStatus.ACTIVE.ordinal() + " and u.status = "
							+ EntityStatus.NORMAL.ordinal();
					List<Object> dataList = (List<Object>) baseDao.selectSQL(sql);
					if (!ListUtils.isEmpty(dataList)) {
						dealUser.addAll(dataList.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList()));
					}
				}
			}
			logger.info("获取真实能处理流程节点的用户结束");
		} catch (Exception e) {
			logger.error("获取真实能处理流程节点的用户异常", e);
		}
		return new ArrayList<>(dealUser);
	}

	/**
	 * 获取 ossUserId -> User
	 * 
	 * @param all
	 *            是否包含被禁用的
	 * @return
	 */
	@Override
	public Map<String, User> getUserMap(boolean all) {
		Map<String, User> userMap = null;
		try {
			SearchFilter filter = new SearchFilter();
			if (!all) {
				filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.getValue()));
				filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
			}
			List<User> userList = queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(userList)) {
				userMap = userList.stream().collect(Collectors.toMap(User::getOssUserId, o -> o));
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return userMap;
	}


	/**
	 * 获取用户的所有领导
	 *
	 * @return
	 */
	@Override
	public List<User> findUserAllLeader(String userId) {
		try {
			User user = read(userId);
			return findUserAllLeader(user);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 获取用户的所有领导
	 *
	 * @return
	 */
	@Override
	public List<User> findUserAllLeader(User user) {
		if (null == user) {
			return null;
		}
		List<String> deptList = new ArrayList<>();
		deptList.add(user.getDeptId());
		deptList.addAll(departmentService.getSuperDeptIds(user.getDeptId()));
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptList));
		filter.getRules().add(new SearchRule("identityType", Constants.ROP_EQ, IdentityType.LEADER_IN_DEPT.ordinal()));
		filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
		filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.getValue()));
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public List<User> findUserByDept(String deptId) {
		if (StringUtil.isBlank(deptId)) {
			return null;
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
		filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
		filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.getValue()));
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<User> objs) throws ServiceException {
		try {
			return userDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<User> objs) throws ServiceException {
		try {
			return userDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<User> objs) throws ServiceException {
		try {
			return userDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
