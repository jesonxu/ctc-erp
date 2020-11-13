/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.dahantc.erp.controller.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.enums.RoleType;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.dto.user.UserDto;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping(value = "/account")
public class AccountAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(AccountAction.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private IBaseDao baseDao;

	// 账号信息分页
	private PageResult<User> users;

	// 封装好的账号信息分页
	private PageResult<UserDto> userUIs;

	// 每页显示条数
	private int pageSize = 10;

	// 当前页
	private int currentPage = 1;

	// 操作描述
	private String msg;

	/**
	 * 跳转账号管理页面
	 */
	@RequestMapping(value = "/accountQuery")
	public String accountQuery() {
		return "/views/account/account";
	}

	/**
	 * 获取组织架构页面
	 */
	@RequestMapping("toDepts")
	public String toDepts() {
		Boolean isEditAble = new Boolean(request.getParameter("isEditAble"));
		request.setAttribute("isEditAble", isEditAble);
		return "/views/account/department/department";
	}

	/**
	 * 获取部门以及子部门选项
	 */
	@RequestMapping("/readDepts")
	@ResponseBody
	public String readDepts() {
		JSONObject result = new JSONObject();
		JSONArray data = null;
		long _start = System.currentTimeMillis();
		try {
			data = new JSONArray();
			SearchFilter filter = new SearchFilter();
			filter.getOrders().add(new SearchOrder("sequence", "asc", true));
			List<Department> departments = departmentService.queryAllBySearchFilter(filter);
			if (departments != null && !departments.isEmpty()) {
				for (Department dept : departments) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", dept.getDeptid());
					jsonObject.put("name", dept.getDeptname());
					jsonObject.put("pId", dept.getParentid() == null ? "" : dept.getParentid());
					jsonObject.put("sequence", dept.getSequence());
					data.add(jsonObject);
				}
			}
			result.put("data", data);
			logger.info("查询部门子部门选项成功，耗时：" + (System.currentTimeMillis() - _start));
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			result.put("data", "");
		}
		return result.toString();
	}

	/**
	 * 获取角色下拉框
	 */
	@RequestMapping("/getSelectRole")
	@ResponseBody
	public String getSelectRole() {
		long _start = System.currentTimeMillis();
		JSONArray roleList = new JSONArray();
		try {
			SearchFilter filter = new SearchFilter();
			// 去除“超级管理员”角色
			filter.getRules().add(new SearchRule("rolename", Constants.ROP_NE, RoleType.ADMIN.getDesc()));
			List<Role> dataList = roleService.queryAllBySearchFilter(filter);
			for (Role role : dataList) {
				JSONObject json = new JSONObject();
				json.put("value", role.getRoleid());
				json.put("name", role.getRolename());
				json.put("selected", "");
				json.put("disabled", "");
				roleList.add(json);
			}
			logger.info("获取角色下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return roleList.toJSONString();
	}

	/**
	 * 分页读取账号信息
	 */
	@RequestMapping(value = "/readPages")
	@ResponseBody
	public BaseResponse<PageResult<UserDto>> readPages() {
		try {
			long _start = System.currentTimeMillis();
			String realName = request.getParameter("realName");
			String deptId = request.getParameter("deptId");
			String accountNumber = request.getParameter("accountNumber");
			String roleId = request.getParameter("roleId");
			String ustate = request.getParameter("ustate");
			pageSize = Integer.parseInt(request.getParameter("limit"));
			currentPage = Integer.parseInt(request.getParameter("page"));

			PageResult<UserDto> result = new PageResult<>(null, 0);

			StringBuffer selectSql = new StringBuffer("select eu.ossUserId, eu.realName, eu.loginName, eu.deptId, eu.uState, eu.jobType");
			StringBuffer fromSql = new StringBuffer(" from erp_user eu ");
			StringBuffer whereSql = new StringBuffer(" where 1 = 1");
			// 判断账号查询条件
			if (StringUtil.isNotBlank(accountNumber)) {
				whereSql.append(" and eu.loginName like '%").append(accountNumber).append("%'");
			}
			// 判断姓名查询条件，支持模糊查询
			if (StringUtil.isNotBlank(realName)) {
				whereSql.append(" and eu.realName like '%").append(realName).append("%'");
			}
			// 判断部门Id查询条件
			if (StringUtil.isNotBlank(deptId)) {
				whereSql.append(" and eu.deptId = '").append(deptId).append("'");
			}
			// 角色Id查询条件
			if (StringUtil.isNotBlank(roleId)) {
				fromSql.append(" left join erp_role_relation err on eu.ossuserid = err.ossuserid");
				whereSql.append(" and err.roleId = '").append(roleId).append("'");
			}
			//用户状态
			if (StringUtils.isNotBlank(ustate)) {
				whereSql.append(" and eu.uState in (").append(ustate).append(")");
			} else {
				whereSql.append(" and eu.uState = ").append(UserStatus.ACTIVE.getValue());
			}
			String orderSql = " order by eu.realName asc";
			// 查询账号信息

			String countSql = "select count(1) " + fromSql.toString() + whereSql.toString() + orderSql;
			logger.info("查询用户数量的sql：" + countSql);

			List<Object> countResult = (List<Object>) baseDao.selectSQL(countSql);
			int count = 0;
			if (countResult != null) {
				count = ((Number) countResult.get(0)).intValue();
				logger.info("count：" + count);
			}
			result.setCount(count);
			result.setTotalPages((count % pageSize == 0) ? (count / pageSize) : (count / pageSize + 1));

			if (count == 0 || currentPage > result.getTotalPages()) {
				return BaseResponse.success(result);
			}

			String querySql = selectSql.toString() + fromSql.toString() + whereSql.toString() + orderSql;
			logger.info("查询用户的sql：" + querySql);
			List<Object[]> dataResult = (List<Object[]>) baseDao.selectSQL(querySql);
			if (!CollectionUtils.isEmpty(dataResult)) {

				List<UserDto> uiList = new ArrayList<>();

				List<Department> departmentList = departmentService.queryAllBySearchFilter(null);
				Map<String, String> deptNameMap = departmentList.stream().collect(Collectors.toMap(Department::getDeptid, Department::getDeptname));

				List<Role> roleList = roleService.queryAllBySearchFilter(null);
				Map<String, String> roleNameMap = roleList.stream().collect(Collectors.toMap(Role::getRoleid, Role::getRolename));

				List<RoleRelation> roleRelationList = roleRelationService.queryAllBySearchFilter(new SearchFilter());
				Map<String, List<RoleRelation>> roleRelationMap = roleRelationList.stream().collect(Collectors.groupingBy(RoleRelation::getOssUserId));

				for (Object[] data : dataResult) {
					// eu.ossUserId, eu.realName, eu.loginName, eu.deptId, eu.uState, eu.jobType
					UserDto userDto = new UserDto();
					userDto.setOssUserId(String.valueOf(data[0]));
					userDto.setRealName(String.valueOf(data[1]));
					userDto.setLoginName(String.valueOf(data[2]));
					userDto.setDeptId(String.valueOf(data[3]));
					userDto.setDeptName(deptNameMap.getOrDefault(userDto.getDeptId(), "-"));
					userDto.setuState(UserStatus.getDesc(((Number) data[4]).intValue()));
					userDto.setJobType(String.valueOf(data[5]));
					StringBuffer roleName = new StringBuffer("");
					StringBuffer roleIds = new StringBuffer("");
					List<RoleRelation> userRoleList = roleRelationMap.getOrDefault(userDto.getOssUserId(), null);
					if (!CollectionUtils.isEmpty(userRoleList)) {
						for (RoleRelation roleRelation : userRoleList) {
							if (roleNameMap.containsKey(roleRelation.getRoleId())) {
								roleName.append(",").append(roleNameMap.get(roleRelation.getRoleId()));
								roleIds.append(",").append(roleRelation.getRoleId());
							}
						}
					}
					if (roleName.length() > 0) {
						roleName.deleteCharAt(0);
						roleIds.deleteCharAt(0);
					}
					userDto.setRoleName(roleName.toString());
					userDto.setRoleId(roleIds.toString());
					uiList.add(userDto);
				}

				result.setData(uiList);
			}
			logger.info("返回账号信息分页成功，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(result);
		} catch (Exception e) {
			msg = e.getMessage();
			logger.error(e.getMessage(), e);
			return BaseResponse.error(msg);
		}
	}

	/**
	 * 修改账号信息，只能改角色和岗位类型
	 */
	@RequestMapping(value = "/editAccount")
	@ResponseBody
	public BaseResponse<String> editAccount() throws Exception {
		long _start = System.currentTimeMillis();
		try {
			String ossUserId = request.getParameter("ossUserId");
			String roleId = request.getParameter("roleId");
			String jobType = request.getParameter("jobType");
			if (StringUtil.isNotBlank(ossUserId)) {
				User user = userService.read(ossUserId);
				if (user != null) {
					String[] roles = roleId.split(",");

					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
					List<RoleRelation> exitRoles = roleRelationService.queryAllBySearchFilter(searchFilter);

					for (RoleRelation relation : exitRoles) { // 删掉去除的
						if (roleId.indexOf(relation.getRoleId()) < 0) {
							roleRelationService.delete(relation.getRoleRelationId());
						}
					}

					for (String role : roles) { // 增加新的
						SearchFilter filter = new SearchFilter();
						filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
						filter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, role));

						int exit = roleRelationService.getCount(filter);
						if (exit == 0) {
							RoleRelation relation = new RoleRelation();
							relation.setOssUserId(ossUserId);
							relation.setRoleId(role);
							roleRelationService.save(relation);
						}
					}

					user.setJobType(jobType);

					userService.update(user);
					msg = "修改成功";
					logger.info("修改账号信息成功，耗时：" + (System.currentTimeMillis() - _start));
					return BaseResponse.success(msg);
				}
			}
			msg = "账号为空";
			logger.info("修改账号信息失败，" + msg);
			return BaseResponse.error(msg);
		} catch (Exception e) {
			msg = e.getMessage();
			logger.error(e.getMessage(), e);
			return BaseResponse.error(msg);
		}
	}
	
	/**
	 * 自动补全查询账号
	 * 
	 */
	@RequestMapping("/queryByAuto")
	@ResponseBody
	public String queryByAuto() {
		String keywords = request.getParameter("keywords");
		SearchFilter filter = new SearchFilter();
		SearchRule[] orRule = { new SearchRule("loginName", Constants.ROP_CN, keywords), new SearchRule("realName", Constants.ROP_CN, keywords) };
		List<User> userlist = null;
		try {
			filter.getOrRules().add(orRule);
			userlist = userService.queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("自动补全查询用户异常", e);
		}
		JSONObject req = new JSONObject();
		JSONArray jsonList = new JSONArray();
		if (null != userlist && userlist.size() > 0) {
			req.put("code", 0);
			req.put("type", "success");
			req.put("content", jsonList);
			for (User user : userlist) {
				JSONObject obj = new JSONObject();
				obj.put("ossUserId", user.getOssUserId());
				obj.put("loginName", user.getLoginName());
				obj.put("realName", user.getRealName());
				jsonList.add(obj);
			}
		} else {
			req.put("code", 1);
			req.put("type", "error");
		}
		return req.toString();
	}

	/**
	 * 获取岗位类型下拉框
	 */
	@RequestMapping("/getJobType")
	@ResponseBody
	public String getJobType() {
		long _start = System.currentTimeMillis();
		JSONArray types = new JSONArray();
		HashMap<String, String> nameMap = JobType.getNameMap();
		for (Map.Entry<String, String> entry : nameMap.entrySet()) {
			JSONObject json = new JSONObject();
			json.put("value", entry.getKey());
			json.put("name", entry.getValue());
			types.add(json);
		}
		logger.info("获取岗位类型下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return types.toJSONString();
	}

	public PageResult<User> getUsers() {
		return users;
	}

	public void setUsers(PageResult<User> users) {
		this.users = users;
	}

	public PageResult<UserDto> getUserUIs() {
		return userUIs;
	}

	public void setUserUIs(PageResult<UserDto> userUIs) {
		this.userUIs = userUIs;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}