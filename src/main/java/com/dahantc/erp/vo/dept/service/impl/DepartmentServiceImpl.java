package com.dahantc.erp.vo.dept.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.department.DeptOrUserInfo;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.dao.IDepartmentDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("departmentService")
public class DepartmentServiceImpl implements IDepartmentService {

	private static Logger logger = LogManager.getLogger(DepartmentServiceImpl.class);

	@Autowired
	private IBaseDao baseDao;

	@Resource
	private IDepartmentDao departmentDao;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

	@Override
	public List<Department> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return departmentDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			throw new ServiceException("根据过滤条件查询部门异常", e);
		}
	}

	/**
	 * 查询子部门，包括下级部门
	 * 
	 * @param deptId
	 *            父部门id
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public Set<Department> getSubDept(String deptId) throws ServiceException {
		Set<Department> depts = new HashSet<Department>();
		List<Department> deptChild = new ArrayList<Department>();
		SearchFilter deptFilter = new SearchFilter();
		deptFilter.getRules().add(new SearchRule("parentid", "eq", deptId));
		deptFilter.getRules().add(new SearchRule("flag", "eq", 0));
		try {
			deptChild = this.queryAllBySearchFilter(deptFilter);
			if (deptChild != null && deptChild.size() > 0) {
				for (Department department : deptChild) {
					Set<Department> list = getSubDept(department.getDeptid());
					depts.addAll(list);
				}
				depts.addAll(deptChild);
			}
		} catch (Exception e) {
			throw new ServiceException("查询下属部门异常", e);
		} finally {
			if (null != deptChild) {
				deptChild.clear();
				deptChild = null;
			}
		}
		return depts;
	}

	/**
	 * 查询直属子部门
	 *
	 * @param pid
	 *            父部门id
	 * @param deptIdFilter
	 *            查询条件的 部门Id
	 * @return 部门信息
	 */
	@Override
	public List<Department> getDeptByFatherId(String pid, List<String> deptIdFilter) {
		SearchFilter deptFilter = new SearchFilter();
		deptFilter.getRules().add(new SearchRule("parentid", Constants.ROP_EQ, pid));
		deptFilter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
		if (deptIdFilter != null && !deptIdFilter.isEmpty()) {
			deptFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, deptIdFilter));
		}
		List<Department> departments = null;
		try {
			departments = queryAllBySearchFilter(deptFilter);
		} catch (ServiceException e) {
			logger.error("查询子部门信息错误", e);
		}
		return departments;
	}

	@Override
	public Department read(Serializable id) throws ServiceException {
		try {
			return departmentDao.read(id);
		} catch (Exception e) {
			logger.error("读取角色信息失败", e);
			throw new ServiceException("读取角色信息失败", e);
		}
	}

	/**
	 * 查询部门的子部门Id集合
	 *
	 * @param deptId
	 *            部门id
	 * @return List<String>
	 */
	@Override
	public List<String> getSubDeptIds(String deptId) {
		Set<Department> departments = null;
		try {
			departments = getSubDept(deptId);
		} catch (ServiceException e) {
			logger.error("根据部门id-{}-查询子部门信息错误", deptId, e);
		}
		if (departments != null && !departments.isEmpty()) {
			return departments.stream().map(Department::getDeptid).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	/**
	 * 查询部门的根节点
	 *
	 * @return List<String>
	 */
	@Override
	public List<Department> getRootDept() {
		String sql = "select dc.deptid, dc.deptname, dc.parentid, dc.sequence from erp_department dc left join erp_department df on dc.parentid = df.deptid where dc.flag = 0 and df.deptid is null";
		try {
			logger.info("查部门的根节点Hql：" + sql);
			List<Object[]> list = (List<Object[]>) baseDao.selectSQL(sql);
			if (!CollectionUtils.isEmpty(list)) {
				return list.stream().map(obj -> {
					Department department = new Department();
					department.setDeptid(String.valueOf(obj[0]));
					department.setDeptname(String.valueOf(obj[1]));
					department.setParentid(String.valueOf(obj[2]));
					department.setSequence(String.valueOf(obj[3]));
					return department;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("查询部门根节点出现异常", e);
		}
		return null;
	}

	@Override
	public List<Department> queryAll() {
		SearchFilter deptFilter = new SearchFilter();
		deptFilter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
		List<Department> departments = null;
		try {
			departments = queryAllBySearchFilter(deptFilter);
		} catch (ServiceException e) {
			logger.error("查询子部门信息错误", e);
		}
		return departments;
	}

	@Override
	public List<Department> getChildDepts(List<Department> allDepartment, String fatherDeptId) {
		if (allDepartment != null && allDepartment.size() > 0) {
			List<Department> childDepts = allDepartment.stream().filter(department -> department.getParentid().equals(fatherDeptId))
					.collect(Collectors.toList());
			if (!childDepts.isEmpty()) {
				List<Department> result = new ArrayList<>(childDepts);
				for (Department department : childDepts) {
					List<Department> cDepts = getChildDepts(allDepartment, department.getDeptid());
					if (cDepts != null && !cDepts.isEmpty()) {
						result.addAll(cDepts);
					}
				}
				return result;
			}
		}
		return null;
	}

	@Override
	public List<String> getDeptIdsByPermission(OnlineUser user) {
		if (user == null) {
			return null;
		}
		Role role = null;
		try {
			role = roleService.read(user.getRoleId());
		} catch (ServiceException e) {
			logger.error("查询角色信息错误：", e);
			return null;
		}
		return getDeptIdsByPermission(user, role);
	}

	@Override
	public List<String> getDeptIdsByPermission(OnlineUser user, Role role) {
		List<String> deptIdList = null;
		List<Department> deptList = null;
		// 角色数据权限
		int dataPermission = role.getDataPermission();
		try {
			if (DataPermission.All.ordinal() == dataPermission) {
				// 全部权限，所有有效部门
				deptList = queryAll();
				if (!ListUtils.isEmpty(deptList)) {
					deptIdList = deptList.stream().map(Department::getDeptid).collect(Collectors.toList());
				}
			} else if (DataPermission.Dept.ordinal() == dataPermission) {
				// 部门权限，自己部门+子部门
				String userDeptId = user.getUser().getDeptId();
				deptList = new ArrayList<>(getSubDept(userDeptId));

				if (!ListUtils.isEmpty(deptList)) {
					deptIdList = deptList.stream().map(Department::getDeptid).collect(Collectors.toList());
				} else {
					deptIdList = new ArrayList<>();
				}
				deptIdList.add(userDeptId);
			} else if (DataPermission.Flow.ordinal() == dataPermission) {
				// 流程权限
				// TODO
			} else if (DataPermission.Customize.ordinal() == dataPermission) {
				// 自定义，用角色中勾选的部门
				String deptids = role.getDeptIds();
				deptIdList = StringUtil.isNotBlank(deptids) ? Arrays.asList(deptids.split(",")) : null;
			} // else 自己权限，部门为空
		} catch (Exception e) {
			logger.error("按数据权限获取用户的部门id列表异常", e);
		}
		return deptIdList;
	}

	/**
	 * 查询部门的上级部门直到根部门的Id集合
	 *
	 * @param deptId
	 *            部门id
	 * @return List<String>
	 */
	@Override
	public List<String> getSuperDeptIds(String deptId) {
		List<String> deptIds = new ArrayList<>();
		logger.info("查询所有上级部门开始，部门id：" + deptId);
		try {
			Department nowDept = read(deptId);
			if (nowDept == null) {
				logger.info("查询的部门不存在，部门id：" + deptId);
			} else {
				Department parentDept = read(nowDept.getParentid());
				while (parentDept != null) {
					deptIds.add(parentDept.getDeptid());
					parentDept = read(parentDept.getParentid());
				}
			}
			logger.info("查询所有上级部门结束，查询到上级部门个数：" + deptIds.size());
		} catch (Exception e) {
			logger.error("查询所有上级部门异常，部门id：" + deptId, e);
		}
		return deptIds;
	}

	@Override
	public Map<String, String> queryDeptName(List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return new HashMap<>();
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, new ArrayList<>(new HashSet<>(ids))));
		// 部门信息
		List<Department> departments = null;
		try {
			departments = this.queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询部门信息错误", e);
		}
		if (departments == null || departments.isEmpty()) {
			return new HashMap<>();
		}
		return departments.stream().collect(Collectors.toMap(Department::getDeptid, Department::getDeptname, (o, n) -> n));
	}

	@Override
	public List<DeptOrUserInfo> getDirectChildAndUser(OnlineUser onlineUser, String deptId) {
		List<DeptOrUserInfo> list = new ArrayList<>();
		if (null == onlineUser) {
			return list;
		}
		User user = onlineUser.getUser();
		long _start = System.currentTimeMillis();
		try {
			Role role = roleService.read(onlineUser.getRoleId());
			// 角色权限是自己，但身份是领导，按部门权限处理
			if (role.getDataPermission() == DataPermission.Self.ordinal() && user.getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal()) {
				role.setDataPermission(DataPermission.Dept.ordinal());
			}
			// 自己权限（1.角色自己权限 2.不是领导）
			if (role.getDataPermission() == DataPermission.Self.ordinal() || role.getDataPermission() == DataPermission.Flow.ordinal()
					|| (role.getDataPermission() == DataPermission.All.ordinal() && user.getIdentityType() == IdentityType.ORDINARY_MEMBER.ordinal())) {
				return list;
			}

			List<Department> departments = new ArrayList<>();
			if (DataPermission.Dept.ordinal() == role.getDataPermission() || StringUtils.isNotBlank(deptId)) {
				// 部门权限查自己部门 部门权限以上查询子部门
				logger.info("用户数据权限：部门");
				if (StringUtils.isBlank(deptId)) {
					deptId = user.getDeptId();
				}
				List<String> deptIds = this.getDeptIdsByPermission(onlineUser, role);
				// 查指定的deptId的直属下级部门
				List<Department> departmentList = this.getDeptByFatherId(deptId, null);
				if (!CollectionUtils.isEmpty(deptIds) && !CollectionUtils.isEmpty(departmentList)) {
					departmentList.forEach(dept -> {
						if (deptIds.contains(dept.getDeptid())) {
							departments.add(dept);
						}
					});
				}
			} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
				// 所有权限
				logger.info("用户数据权限：全部");
				// 查根部门的直属下级部门
				List<Department> deptList = this.getRootDept();
				if (!CollectionUtils.isEmpty(deptList)) {
					deptList.forEach(dept -> {
						departments.addAll(this.getDeptByFatherId(dept.getDeptid(), null));
					});
				}
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				// 自定义权限
				logger.info("用户数据权限：自定义");
				String deptIds = role.getDeptIds();
				if (StringUtils.isNotBlank(deptIds)) {
					// 查自定义部门的所有根部门
					List<String> depts = Arrays.asList(deptIds.split(","));
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
					filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
					filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
					List<Department> deptList = this.queryAllBySearchFilter(filter);
					// 这些自定义的部门中，父部门不在自定义范围内的，即是根部门
					if (!CollectionUtils.isEmpty(deptList)) {
						Set<String> set = deptList.stream().map(Department::getDeptid).collect(Collectors.toSet());
						deptList.forEach(dept -> {
							if (!set.contains(dept.getParentid())) {
								departments.add(dept);
							}
						});
					}
				}
			}

			SearchFilter userFilter = new SearchFilter();

			if (!CollectionUtils.isEmpty(departments)) {
				// 查询每个部门的领导
				userFilter.getRules().add(new SearchRule("identityType", Constants.ROP_EQ, IdentityType.LEADER_IN_DEPT.ordinal()));
				userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
				userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
				userFilter.getRules()
						.add(new SearchRule("deptId", Constants.ROP_IN, departments.stream().map(Department::getDeptid).collect(Collectors.toList())));
				userFilter.getOrders().add(new SearchOrder("ossUserId", Constants.ROP_ASC));
				List<User> leaderList = userService.queryAllBySearchFilter(userFilter);
				Map<String, String> leaderMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(leaderList)) {
					leaderList.forEach(leader -> {
						if (!leaderMap.containsKey(leader.getDeptId())) {
							leaderMap.put(leader.getDeptId(), leader.getRealName());
						} else {
							leaderMap.put(leader.getDeptId(), leaderMap.get(leader.getDeptId()) + "、" + leader.getRealName());
						}
					});
				}
				// 部门：[{部门id，部门名称+领导姓名}]
				departments.forEach(dept -> {
					list.add(new DeptOrUserInfo(dept.getDeptid(),
							dept.getDeptname() + (leaderMap.containsKey(dept.getDeptid()) ? "：" + leaderMap.get(dept.getDeptid()) : ""), 0));
				});
			}

			// 查询指定部门直接的用户
			if (StringUtils.isNotBlank(deptId)) {
				userFilter = new SearchFilter();
				userFilter.getRules().add(new SearchRule("identityType", Constants.ROP_EQ, IdentityType.ORDINARY_MEMBER.ordinal()));
				userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
				userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
				userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
				List<User> userList = userService.queryAllBySearchFilter(userFilter);
				if (!CollectionUtils.isEmpty(userList)) {
					// 部门用户：[{用户id，用户姓名}]
					userList.forEach(u -> {
						list.add(new DeptOrUserInfo(u.getOssUserId(), u.getRealName(), 1));
					});
				}
			}
			logger.info("查询当前用户部门信息结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询当前用户部门信息异常", e);
		}
		return list;
	}

	/**
	 * 查询部门或客户
	 * 
	 * @param onlineUser
	 *            当前用户
	 * @param deptId
	 *            部门id条件
	 * @param showAll
	 *            是否查有，否则只查未禁用的用户
	 * @return
	 */
	@Override
	public List<DeptOrUserInfo> getDeptAndUser(OnlineUser onlineUser, String deptId, Boolean showAll) {
		List<DeptOrUserInfo> list = new ArrayList<>();
		if (null == onlineUser) {
			return list;
		}
		User user = onlineUser.getUser();
		long _start = System.currentTimeMillis();
		try {
			Role role = roleService.read(onlineUser.getRoleId());
			// 角色权限是自己
			if (role.getDataPermission() == DataPermission.Self.ordinal()) {
				return list;
			}

			List<Department> departments = new ArrayList<>();
			if (DataPermission.Dept.ordinal() == role.getDataPermission() || StringUtils.isNotBlank(deptId)) {
				// 部门权限查自己部门 部门权限以上查询子部门
				logger.info("用户数据权限：部门");
				if (StringUtils.isBlank(deptId)) {
					deptId = user.getDeptId();
				}
				List<String> deptIds = this.getDeptIdsByPermission(onlineUser, role);
				// 查指定的deptId的直属下级部门
				List<Department> departmentList = this.getDeptByFatherId(deptId, null);
				if (!CollectionUtils.isEmpty(deptIds) && !CollectionUtils.isEmpty(departmentList)) {
					departmentList.forEach(dept -> {
						if (deptIds.contains(dept.getDeptid())) {
							departments.add(dept);
						}
					});
				}
			} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
				// 所有权限
				logger.info("用户数据权限：全部");
				// 查根部门的直属下级部门
				List<Department> deptList = this.getRootDept();
				if (!CollectionUtils.isEmpty(deptList)) {
					deptList.forEach(dept -> {
						departments.addAll(this.getDeptByFatherId(dept.getDeptid(), null));
					});
				}
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				// 自定义权限
				logger.info("用户数据权限：自定义");
				String deptIds = role.getDeptIds();
				if (StringUtils.isNotBlank(deptIds)) {
					// 查自定义部门的所有根部门
					List<String> depts = Arrays.asList(deptIds.split(","));
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
					filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
					List<Department> deptList = this.queryAllBySearchFilter(filter);
					// 这些自定义的部门中，父部门不在自定义范围内的，即是根部门
					if (!CollectionUtils.isEmpty(deptList)) {
						Set<String> set = deptList.stream().map(Department::getDeptid).collect(Collectors.toSet());
						deptList.forEach(dept -> {
							if (!set.contains(dept.getParentid())) {
								departments.add(dept);
							}
						});
					}
				}
			}

			SearchFilter userFilter = new SearchFilter();

			if (!CollectionUtils.isEmpty(departments)) {
				// 部门：[{部门id，部门名称+领导姓名}]
				departments.forEach(dept -> {
					list.add(new DeptOrUserInfo(dept.getDeptid(), dept.getDeptname(), 0, dept.getFlag() == 0 ? 1 : 0));
				});
			}

			// 查询指定部门直接的用户
			if (StringUtils.isNotBlank(deptId)) {
				userFilter = new SearchFilter();
				userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
				List<User> userList = userService.queryAllBySearchFilter(userFilter);
				if (!CollectionUtils.isEmpty(userList)) {
					// 部门用户：[{用户id，用户姓名}]
					userList.forEach(u -> {
						list.add(new DeptOrUserInfo(u.getOssUserId(), u.getRealName(), 1, u.getUstate()));
					});
				}
			}
			logger.info("查询当前用户部门信息结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询当前用户部门信息异常", e);
		}
		return list;
	}

}
