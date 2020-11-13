package com.dahantc.erp.controller.department;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.department.DeptInfo;
import com.dahantc.erp.dto.department.DeptOrUserInfo;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/department")
public class DepartmentAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(DepartmentAction.class);

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

	/**
	 * 获取组织架构页面
	 */
	@RequestMapping("/toDeptTree")
	public String toDeptTree(@RequestParam(required = false) String deptIds, @RequestParam(required = false) String check) {
		request.setAttribute("deptIds", StringUtil.isBlank(deptIds) ? "" : deptIds);
		request.setAttribute("check", check);
		return "/views/department/department";
	}

	/**
	 * 查询用户数据权限的所有部门及其下级部门
	 */
	@ResponseBody
	@PostMapping("/obtainUserDept")
	public BaseResponse<List<DeptInfo>> obtainUserDept() {
		logger.info("查询当前用户部门信息开始");
		long _start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			Role role = roleService.read(onlineUser.getRoleId());
			List<Department> departments = new ArrayList<>();
			if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：部门");
				// 部门权限
				String deptId = onlineUser.getUser().getDeptId();
				// 查询当前用户自己的部门信息
				Department dept = departmentService.read(deptId);
				if (dept != null) {
					departments.add(dept);
					// 查询子部门
					departments.addAll(departmentService.getSubDept(deptId));
				}
			} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：全部");
				// 所有权限
				SearchFilter filter = new SearchFilter();
				filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
				filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
				// 查询所有部门
				departments = departmentService.queryAllBySearchFilter(filter);
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：自定义");
				// 自定义权限
				String deptIds = role.getDeptIds();
				// 查询自定义的部门
				if (StringUtils.isNotBlank(deptIds)) {
					List<String> depts = Arrays.asList(deptIds.split(","));
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
					filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
					filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
					departments = departmentService.queryAllBySearchFilter(filter);
				}
			}
			if (!departments.isEmpty()) {
				List<DeptInfo> deptInfos = new ArrayList<>(departments.size());
				for (Department dept : departments) {
					DeptInfo deptInfo = new DeptInfo();
					deptInfo.setId(dept.getDeptid());
					deptInfo.setName(dept.getDeptname());
					deptInfo.setpId(dept.getParentid() == null ? "" : dept.getParentid());
					deptInfo.setSequence(dept.getSequence());
					deptInfos.add(deptInfo);
				}
				deptInfos.sort(Comparator.comparing(DeptInfo::getSequence));
				logger.info("查询当前用户部门信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return BaseResponse.success(deptInfos);
			}
		} catch (ServiceException e) {
			logger.error("查询当前用户部门信息异常", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	/**
	 * 查询用户数据权限的所有部门及其下级部门
	 */
	@ResponseBody
	@PostMapping("/searchDepartment")
	public BaseResponse<List<DeptInfo>> searchDepartment() {
		logger.info("查询当前用户部门信息开始");
		long _start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			Role role = roleService.read(onlineUser.getRoleId());
			List<Department> departments = new ArrayList<>();
			if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：部门");
				// 部门权限
				String deptId = onlineUser.getUser().getDeptId();
				// 查询当前用户自己的部门信息
				Department dept = departmentService.read(deptId);
				if (dept != null) {
					departments.add(dept);
					// 查询子部门
					departments.addAll(departmentService.getSubDept(deptId));
				}
			} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：全部");
				// 所有权限
				SearchFilter filter = new SearchFilter();
				filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
				filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
				// 查询所有部门
				departments = departmentService.queryAllBySearchFilter(filter);
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				logger.info("用户数据权限：自定义");
				// 自定义权限
				String deptIds = role.getDeptIds();
				// 查询自定义的部门
				if (StringUtils.isNotBlank(deptIds)) {
					List<String> depts = Arrays.asList(deptIds.split(","));
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
					filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
					filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
					departments = departmentService.queryAllBySearchFilter(filter);
				}
			}
			if (!departments.isEmpty()) {
				List<DeptInfo> deptInfos = new ArrayList<>(departments.size());
				for (Department dept : departments) {
					DeptInfo deptInfo = new DeptInfo();
					deptInfo.setId(dept.getDeptid());
					deptInfo.setName(dept.getDeptname());
					deptInfo.setpId(dept.getParentid() == null ? "" : dept.getParentid());
					deptInfo.setSequence(dept.getSequence());
					deptInfo.setNodeType("dept");
					deptInfos.add(deptInfo);
				}
				deptInfos.sort(Comparator.comparing(DeptInfo::getSequence));
				for (Department dept : departments) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, dept.getDeptid()));
					filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 1));
					filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, 1));
					List<User> users = userService.queryAllBySearchFilter(filter);
					for (User user : users) {
						DeptInfo deptInfo = new DeptInfo();
						deptInfo.setId(user.getOssUserId());
						deptInfo.setNodeType("user");
						deptInfo.setName(user.getRealName());
						deptInfo.setpId(dept.getDeptid() == null ? "" : dept.getDeptid());
						deptInfos.add(deptInfo);
					}
				}
				logger.info("查询当前用户部门信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return BaseResponse.success(deptInfos);
			}
		} catch (ServiceException e) {
			logger.error("查询当前用户部门信息异常", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	/**
	 * 工作汇报查询的部门信息
	 */
	@ResponseBody
	@RequestMapping("/queryDept4Report")
	public BaseResponse<List<DeptOrUserInfo>> queryDept4Report(@RequestParam(required = false) String deptId) {
		logger.info("查询当前用户部门信息开始");
		long _start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			List<DeptOrUserInfo> list = new ArrayList<>();
			// 自己权限（1.角色自己权限 2.不是领导）
			Role role = roleService.read(onlineUser.getRoleId());
			// 角色权限是自己但是是领导的按部门权限处理
			if (role.getDataPermission() == DataPermission.Self.ordinal() && onlineUser.getUser().getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal()) {
				role.setDataPermission(DataPermission.Dept.ordinal());
			}
			if (role.getDataPermission() == DataPermission.Self.ordinal() || role.getDataPermission() == DataPermission.Flow.ordinal()
					|| (role.getDataPermission() == DataPermission.All.ordinal()
							&& onlineUser.getUser().getIdentityType() == IdentityType.ORDINARY_MEMBER.ordinal())) {
				list.add(new DeptOrUserInfo(onlineUser.getUser().getOssUserId(), "我的汇报", 1));
				return BaseResponse.success(list);
			}

			// 每个人都可以看自己日报
			if (StringUtils.isBlank(deptId)) {
				list.add(new DeptOrUserInfo(onlineUser.getUser().getOssUserId(), "我的汇报", 1));
			}

			List<Department> departments = new ArrayList<>();
			if (DataPermission.Dept.ordinal() == role.getDataPermission() || StringUtils.isNotBlank(deptId)) {
				// 部门权限查自己部门 部门权限以上查询子部门
				logger.info("用户数据权限：部门");
				if (StringUtils.isBlank(deptId)) {
					deptId = onlineUser.getUser().getDeptId();
				}
				List<String> deptIds = departmentService.getDeptIdsByPermission(onlineUser, role);
				List<Department> departmentList = departmentService.getDeptByFatherId(deptId, null);
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
				List<Department> deptList = departmentService.getRootDept();
				if (!CollectionUtils.isEmpty(deptList)) {
					deptList.forEach(dept -> {
						departments.addAll(departmentService.getDeptByFatherId(dept.getDeptid(), null));
					});
				}
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				// 自定义权限
				logger.info("用户数据权限：自定义");
				String deptIds = role.getDeptIds();
				if (StringUtils.isNotBlank(deptIds)) {
					List<String> depts = Arrays.asList(deptIds.split(","));
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
					filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
					filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
					List<Department> deptList = departmentService.queryAllBySearchFilter(filter);
					// 保留最顶级的部门信息
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
					leaderList.stream().forEach(leader -> {
						if (!leaderMap.containsKey(leader.getDeptId())) {
							leaderMap.put(leader.getDeptId(), leader.getRealName());
						} else {
							leaderMap.put(leader.getDeptId(), leaderMap.get(leader.getDeptId()) + "、" + leader.getRealName());
						}
					});
				}

				departments.forEach(dept -> {
					list.add(new DeptOrUserInfo(dept.getDeptid(),
							dept.getDeptname() + (leaderMap.containsKey(dept.getDeptid()) ? "：" + leaderMap.get(dept.getDeptid()) : ""), 0));
				});
			}

			if (StringUtils.isNotBlank(deptId)) {
				// 查询这个部门的用户
				userFilter = new SearchFilter();
				userFilter.getRules().add(new SearchRule("identityType", Constants.ROP_EQ, IdentityType.ORDINARY_MEMBER.ordinal()));
				userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
				userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
				userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
				List<User> userList = userService.queryAllBySearchFilter(userFilter);
				if (!CollectionUtils.isEmpty(userList)) {
					userList.forEach(user -> {
						list.add(new DeptOrUserInfo(user.getOssUserId(), user.getRealName(), 1));
					});
				}
			}

			logger.info("查询当前用户部门信息结束，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(list);
		} catch (ServiceException e) {
			logger.error("查询当前用户部门信息异常", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	/**
	 * 查询部门的直属子部门，不再查询下级部门
	 */
	@ResponseBody
	@PostMapping("/queryDirectSubDept")
	public BaseResponse<List<DeptInfo>> queryDirectSubDept(String parentId) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null) {
			return BaseResponse.error("未登录");
		}
		if (StringUtil.isBlank(parentId)) {
			return BaseResponse.error("请求参数错误");
		}
		List<Department> departments = departmentService.getDeptByFatherId(parentId, null);
		if (departments != null && !departments.isEmpty()) {
			List<DeptInfo> deptInfos = departments.stream().map(dept -> {
				DeptInfo deptInfo = new DeptInfo();
				deptInfo.setId(dept.getDeptid());
				deptInfo.setName(dept.getDeptname());
				deptInfo.setpId(dept.getParentid() == null ? "" : dept.getParentid());
				deptInfo.setSequence(dept.getSequence());
				return deptInfo;
			}).sorted(Comparator.comparing(DeptInfo::getSequence)).collect(Collectors.toList());
			return BaseResponse.success(deptInfos);
		}
		return BaseResponse.success("暂无数据");
	}

	/**
	 * 查询全部部门
	 */
	@ResponseBody
	@PostMapping("/queryAllDept")
	public BaseResponse<List<DeptInfo>> queryAllDept() {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			List<Department> departments = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
			// 查询所有部门
			departments = departmentService.queryAllBySearchFilter(filter);

			if (!departments.isEmpty()) {
				List<DeptInfo> deptInfos = new ArrayList<>(departments.size());
				for (Department dept : departments) {
					DeptInfo deptInfo = new DeptInfo();
					deptInfo.setId(dept.getDeptid());
					deptInfo.setName(dept.getDeptname());
					deptInfo.setpId(dept.getParentid() == null ? "" : dept.getParentid());
					deptInfo.setSequence(dept.getSequence());
					deptInfos.add(deptInfo);
				}
				deptInfos.sort(Comparator.comparing(DeptInfo::getSequence));
				return BaseResponse.success(deptInfos);
			}
		} catch (ServiceException e) {
			logger.error("查询部门信息错误", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	/**
	 * 按数据权限查询部门的子部门，包括下级部门
	 */
	@ResponseBody
	@PostMapping("/querySubDeptId")
	public BaseResponse<List<String>> querySubDeptId(String parentId) throws ServiceException {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null) {
			return BaseResponse.error("未登录");
		}
		if (StringUtil.isBlank(parentId)) {
			return BaseResponse.error("请求参数错误");
		}
		Role role = roleService.read(onlineUser.getRoleId());
		List<Department> userDeptList = new ArrayList<>();
		List<String> userDeptIdList = new ArrayList<>();
		if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
			logger.info("用户数据权限：部门");
			// 部门权限
			String deptId = onlineUser.getUser().getDeptId();
			// 查询当前用户自己的部门信息
			Department dept = departmentService.read(deptId);
			if (dept != null) {
				userDeptList.add(dept);
				// 查询子部门
				userDeptList.addAll(departmentService.getSubDept(deptId));
			}
		} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
			logger.info("用户数据权限：全部");
			// 所有权限
			SearchFilter filter = new SearchFilter();
			filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
			filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
			// 查询所有部门
			userDeptList = departmentService.queryAllBySearchFilter(filter);
		} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
			logger.info("用户数据权限：自定义");
			// 自定义权限
			String deptIds = role.getDeptIds();
			// 查询自定义的部门
			if (StringUtils.isNotBlank(deptIds)) {
				List<String> depts = Arrays.asList(deptIds.split(","));
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
				filter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
				filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
				userDeptList = departmentService.queryAllBySearchFilter(filter);
			}
		}
		userDeptIdList = userDeptList.stream().map(Department::getDeptid).collect(Collectors.toList());

		List<Department> searchDeptList = null;
		List<String> searchDeptIdList = new ArrayList<>();
		try {
			searchDeptList = new ArrayList<>(departmentService.getSubDept(parentId));
			if (!searchDeptList.isEmpty()) {
				searchDeptIdList = searchDeptList.stream().map(Department::getDeptid).collect(Collectors.toList());
				// 取交集
				searchDeptIdList.retainAll(userDeptIdList);
			}
		} catch (Exception e) {
			logger.info("查询子部门异常：" + parentId, e);
		}
		return BaseResponse.success(searchDeptIdList);
	}

	@ResponseBody
	@RequestMapping("/queryDeptAndUser")
	public BaseResponse<List<DeptOrUserInfo>> queryDeptAndUser(@RequestParam(required = false) String deptId) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		List<DeptOrUserInfo> list = departmentService.getDeptAndUser(onlineUser, deptId, true);
		if (StringUtils.isBlank(deptId)) {
			list.add(0, new DeptOrUserInfo(onlineUser.getUser().getOssUserId(), "我的", 1));
		}
		return BaseResponse.success(list);
	}

}
