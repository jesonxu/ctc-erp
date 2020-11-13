package com.dahantc.erp.controller.user;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.dto.department.DeptOrUserInfo;
import com.dahantc.erp.dto.user.UserReqDto;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.enums.MaritalStatus;
import com.dahantc.erp.enums.RoleType;
import com.dahantc.erp.vo.role.entity.Role;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.fsExpenseIncome.UploadFileInfoResp;
import com.dahantc.erp.dto.personalcenter.PersonalInfoRespDto;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.FileMd5Utl;
import com.dahantc.erp.util.FileUploadUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/user")
public class UserAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(UserAction.class);

	private static final String userDataUploadPath = "userData";

	@Autowired
	private IUserService userService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private IBaseDao baseDao;

	/**
	 * 跳转账号管理页面
	 */
	@RequestMapping(value = "/toUserManagement")
	public String toUserManagement() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		try {
			Map<String, Boolean> pagePermission = roleService.getPagePermission(onlineUser.getRoleId());
			request.setAttribute("pagePermission", pagePermission);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "/views/user/user";
	}

	/**
	 * 跳转至 员工数据导入页面(Excel上传页面)
	 */
	@RequestMapping("/toUploadPage")
	public String toUploadPage() {
		return "/views/user/importUserData";
	}

	/** 判断是否是 领导 */
	@RequestMapping("/isLeader")
	@ResponseBody
	public BaseResponse<Integer> isLeader() {
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.success(-1);
		}
		return BaseResponse.success(user.getIdentityType());
	}

	/**
	 * 跳转修改密码页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/toEditPwd")
	public String toEditPwd() {
		User user = getOnlineUser();
		if (null == user) {
			return "";
		}
		request.setAttribute("verifyNeedPwd", StringUtils.isNotBlank(user.getWebPwd()));
		return "/views/user/editPwd";
	}

	/**
	 * 执行修改密码
	 * 
	 */
	@RequestMapping("/editPwd")
	@ResponseBody
	public BaseResponse<String> editPwd() {
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.error("用户未登录");
		}
		logger.info("员工[" + getOnlineUser().getLoginName() + "]修改密码，开始");

		String oldPwd = request.getParameter("oldPwd");
		String newPwd = request.getParameter("newPwd");
		String checkPwd = request.getParameter("checkPwd");

		// 如果密码为空不校验
		if (StringUtils.isBlank(user.getWebPwd()) || user.getWebPwd().equals(oldPwd)) {
			if (StringUtils.isNotBlank(newPwd) && StringUtils.isNotBlank(checkPwd)) {
				if (newPwd.equals(checkPwd)) {
					user.setWebPwd(newPwd);
					try {
						if (userService.update(user)) {
							return BaseResponse.success("修改密码成功");
						}
						return BaseResponse.error("修改密码失败");
					} catch (ServiceException e) {
						logger.info("修改密码异常", e);
						return BaseResponse.error("修改密码失败");
					}
				} else {
					return BaseResponse.error("两次密码不一致");
				}
			} else {
				return BaseResponse.error("新密码或验证密码为空");
			}
		} else {
			return BaseResponse.error("旧密码不正确");
		}
	}

	/**
	 * 自动补全查询账号
	 *
	 */
	@RequestMapping("/queryUserByAuto")
	@ResponseBody
	public String queryUserByAuto() {
		String keywords = request.getParameter("keywords");
		SearchFilter filter = new SearchFilter();
		SearchRule[] orRule = { new SearchRule("loginName", Constants.ROP_CN, keywords), new SearchRule("realName", Constants.ROP_CN, keywords) };
		List<User> userlist = null;
		Map<String, String> deptNames = new HashMap<String, String>();
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
				if (deptNames.containsKey(user.getDeptId())) {
					obj.put("deptName", deptNames.get(user.getDeptId()));
				} else {
					try {
						Department department = departmentService.read(user.getDeptId());
						if (department != null) {
							obj.put("deptName", department.getDeptname());
							deptNames.put(department.getDeptid(), department.getDeptname());
						}
					} catch (ServiceException e) {
						logger.info("根据部门id查询部门异常", e);
					}
				}
				jsonList.add(obj);
			}
		} else {
			req.put("code", 1);
			req.put("type", "error");
		}
		return req.toString();
	}

	/**
	 * 获取系统所有可用用户
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryAllUser")
	@ResponseBody
	public BaseResponse<List<User>> queryAllUser() {
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.error("未登录");
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
		filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
		try {
			List<User> users = userService.queryAllBySearchFilter(filter);
			return BaseResponse.success(users);
		} catch (ServiceException e) {
			logger.error("查询用户信息异常：", e);
			return BaseResponse.error("查询用户信息异常");
		}
	}

	/**
	 * 获取员工信息
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	@RequestMapping("/getUserInfo")
	@ResponseBody
	public BaseResponse<PersonalInfoRespDto> getUserInfo(@RequestParam String id) {
		try {
			User user = userService.read(id);
			if (null == user) {
				logger.info("员工不存在：" + id);
				return BaseResponse.error("员工不存在");
			}
			PersonalInfoRespDto dto = userService.queryPersonInfo(user);
			return BaseResponse.success(dto);
		} catch (ServiceException e) {
			logger.error("", e);
			return BaseResponse.error("获取员工信息异常");
		}
	}

	/** 上传文件 */
	@PostMapping("/upLoadFile")
	@ResponseBody
	public BaseResponse<List<UploadFileInfoResp>> uploadFile(@RequestParam("file") MultipartFile[] files) {
		return FileUploadUtil.uploadFile(files, userDataUploadPath);
	}

	/** 关闭弹出框的时候 删除上传文件信息 */
	@ResponseBody
	@RequestMapping("/delUploadFile")
	public BaseResponse<Boolean> delUploadFile(@RequestParam String md5s) {
		return FileUploadUtil.delUploadFile(md5s, userDataUploadPath);
	}

	/**
	 * 解析上传的员工数据文件
	 */
	@ResponseBody
	@RequestMapping("/loadUserData")
	public BaseResponse<Boolean> loadUserData(String fileInfos) {
		logger.info("解析上传文件获取员工数据开始");
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			return BaseResponse.noLogin("请先登录");
		}
		if (StringUtil.isBlank(fileInfos)) {
			return BaseResponse.error("请选择上传文件");
		}
		List<UploadFileInfoResp> uploadFileInfos = null;
		try {
			uploadFileInfos = JSON.parseArray(fileInfos, UploadFileInfoResp.class);
		} catch (Exception e) {
			logger.error("请求参数解析异常");
		}
		if (uploadFileInfos == null || uploadFileInfos.isEmpty()) {
			return BaseResponse.error("请选择上传文件");
		}
		StringBuilder resultMsg = new StringBuilder();
		try {
			// 解析出Excel的每一行
			List<String[]> userDataList = new ArrayList<>();
			// 失败文件名称
			List<String> failFiles = new ArrayList<>();
			// 之前已经解析过不用再解析的文件
			List<String> parsedFiles = new ArrayList<>();
			for (UploadFileInfoResp uploadFileInfo : uploadFileInfos) {
				String fileName = uploadFileInfo.getFileName();
				String path = uploadFileInfo.getFilePath();
				boolean canParse = FileMd5Utl.readAndUpdate(uploadFileInfo.getMd5());
				if (!canParse) {
					parsedFiles.add(fileName);
					logger.info("文件已解析过或无法更新状态，跳过文件：" + fileName);
					continue;
				}
				// 可以解析
				if (path.lastIndexOf(".") >= 0) {
					String fileType = path.substring(path.lastIndexOf(".") + 1);
					if ("xls".equals(fileType)) {
						// 解析excel2003文件
						userDataList.addAll(ParseFile.parseExcel2003(new File(path), null));
					} else if ("xlsx".equals(fileType)) {
						// 解析excel2007文件
						userDataList.addAll(ParseFile.parseExcel2007(new File(path), null, DateUtil.format1));
					} else {
						failFiles.add(fileName);
						logger.info("文件解析失败：" + fileName);
					}
				} else {
					failFiles.add(fileName);
					logger.info("无法解析不带后缀名的文件：" + fileName);
				}
			}
			if (!userDataList.isEmpty()) {
				resultMsg.append("解析文件成功");

				Map<String, User> userNameMap = new HashMap<>();
				List<User> userList = userService.queryAllBySearchFilter(null);
				if (!CollectionUtils.isEmpty(userList)) {
					userNameMap = userList.stream().collect(Collectors.toMap(User::getRealName, v -> v, (v1, v2) -> v2));
				}
				// 用导入数据更新过的用户
				List<User> updateUserList = new ArrayList<>();
				List<String> existRealName = new ArrayList<>();
				for (String[] userData : userDataList) {
					// 姓名，毕业日期，入职日期
					if (userData.length < 3) {
						logger.info("本条数据不完整，跳过数据：" + JSON.toJSONString(userData));
						continue;
					}
					// 姓名
					String realName = userData[0];
					if (StringUtil.isBlank(realName) || realName.equals("姓名")) {
						logger.info("本条数据的不正确，跳过数据：" + JSON.toJSONString(userData));
						continue;
					}
					// 姓名去重
					if (existRealName.contains(realName)) {
						logger.info("本条数据的姓名重复，跳过数据：" + JSON.toJSONString(userData));
						continue;
					}
					existRealName.add(realName);
					User user = userNameMap.get(realName);
					if (null == user) {
						logger.info("未找到用户：" + realName);
						continue;
					}
					// 毕业时间
					String graduationDate = userData[1];
					if (StringUtil.isNotBlank(graduationDate)) {
						Date time;
						if (graduationDate.contains("-")) {
							time = DateUtil.convert(graduationDate, DateUtil.format1);
						} else {
							time = DateUtil.convert(graduationDate, DateUtil.format12);
						}
						if (time != null) {
							user.setGraduationDate(time);
						}
					}
					// 入职时间
					String entryTime = userData[2];
					if (StringUtil.isNotBlank(entryTime)) {
						Date time;
						if (entryTime.contains("-")) {
							time = DateUtil.convert(entryTime, DateUtil.format1);
						} else {
							time = DateUtil.convert(entryTime, DateUtil.format12);
						}
						if (time != null) {
							user.setEntryTime(new Timestamp(time.getTime()));
						}
					}

					updateUserList.add(user);
				}
				if (!updateUserList.isEmpty()) {
					boolean updateResult = userService.updateByBatch(updateUserList);
					if (!updateResult) {
						logger.info("更新员工数据失败");
						return BaseResponse.error("更新员工数据失败");
					}
					logger.info("更新员工数据成功，更新成功总数：" + updateUserList.size());
					resultMsg.append(" 解析成功条数：").append(userDataList.size()).append("，更新成功总数：").append(updateUserList.size());
				}
			} else {
				resultMsg.append(" 解析上传文件失败 ");
				if (!parsedFiles.isEmpty()) {
					resultMsg.append(String.join(",", parsedFiles)).append("，已经上传过，请勿再次上传");
				}
				logger.info(resultMsg.toString());
			}
			if (!failFiles.isEmpty()) {
				resultMsg.append("文件[").append(String.join(",", failFiles)).append("]格式错误");
			}
		} catch (Exception e) {
			logger.error("解析上传文件获取员工数据异常", e);
			resultMsg.append("解析上传文件异常");
		}
		return BaseResponse.success(resultMsg.toString());
	}

	/**
	 * 修改账号信息，只能改角色和岗位类型
	 */
	@RequestMapping(value = "/editUser")
	@ResponseBody
	public BaseResponse<String> editUser(PersonalInfoRespDto dto) {
		long _start = System.currentTimeMillis();
		logger.info("更新员工信息开始");
		String msg = "";
		try {
			String ossUserId = dto.getOssUserId();
			String roleIds = dto.getRoleIds();
			String jobTypes = dto.getJobTypes();
			if (StringUtil.isNotBlank(ossUserId)) {
				User user = userService.read(ossUserId);
				if (user != null) {
					String[] roles = roleIds.split(",");
					// 找出该员工现有的角色
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
					List<RoleRelation> existRoleList = roleRelationService.queryAllBySearchFilter(searchFilter);

					// 删掉更新后去除的
					List<String> deleteRoleIdList = new ArrayList<>();
					Map<String, RoleRelation> existRoleIdMap = new HashMap<>();
					if (!CollectionUtils.isEmpty(existRoleList)) {
						existRoleIdMap = existRoleList.stream().collect(Collectors.toMap(RoleRelation::getRoleId, v -> v));
						deleteRoleIdList = existRoleList.stream().filter(role -> !roleIds.contains(role.getRoleId())).map(RoleRelation::getRoleRelationId)
								.collect(Collectors.toList());
					}

					// 增加新的
					List<RoleRelation> addRoleList = new ArrayList<>();
					for (String roleId : roles) {
						if (existRoleIdMap.containsKey(roleId)) {
							continue;
						}
						RoleRelation relation = new RoleRelation();
						relation.setOssUserId(ossUserId);
						relation.setRoleId(roleId);
						addRoleList.add(relation);
					}

					if (!CollectionUtils.isEmpty(deleteRoleIdList)) {
						deleteRoleIdList = deleteRoleIdList.stream().map(id -> "'" + id + "'").collect(Collectors.toList());
						String sql = "delete from erp_role_relation where rolerelationid in (" + String.join(",", deleteRoleIdList) + ")";
						baseDao.executeUpdateSQL(sql);
					}
					if (!CollectionUtils.isEmpty(addRoleList)) {
						roleRelationService.saveByBatch(addRoleList);
					}

					user.setJobType(jobTypes);
					user.setMaritalStatus(dto.getMaritalStatus());
					user.setSex(dto.getSex());
					boolean result = userService.update(user);

					msg += "修改员工信息" + (result ? "成功" : "失败");
					logger.info(msg + "，耗时：" + (System.currentTimeMillis() - _start));
					return BaseResponse.success(msg);
				}
			}
			msg = "员工id为空";
			logger.info(msg);
			return BaseResponse.error(msg);
		} catch (Exception e) {
			msg = "修改员工信息异常";
			logger.error(msg, e);
			return BaseResponse.error(msg);
		}
	}

	/**
	 * 获取岗位类型下拉框
	 */
	@RequestMapping("/getJobTypeSelect")
	@ResponseBody
	public String getJobTypeSelect() {
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

	/**
	 * 获取角色下拉框
	 */
	@RequestMapping("/getRoleSelect")
	@ResponseBody
	public String getRoleSelect() {
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
	 * 获取婚姻状况下拉框
	 */
	@RequestMapping("/getMaritalSelect")
	@ResponseBody
	public String getMaritalSelect() {
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		MaritalStatus[] statuses = MaritalStatus.values();
		for (MaritalStatus status : statuses) {
			JSONObject json = new JSONObject();
			json.put("value", status.getCode());
			json.put("name", status.getDesc());
			result.add(json);
		}
		logger.info("获取婚姻状况下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return result.toJSONString();
	}

	/**
	 * 员工中心按条件查客户
	 *
	 * @param reqDto	查询条件
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryUser")
	public BaseResponse<List<DeptOrUserInfo>> queryUser(@Valid UserReqDto reqDto) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		List<DeptOrUserInfo> result = new ArrayList<>();
		// 先按数据权限查出用户
		List<User> userList = userService.readUsers(onlineUser, null, null, reqDto.getDeptId());
		// 按角色过滤
		if (StringUtil.isNotBlank(reqDto.getRoleId())) {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, reqDto.getRoleId()));
			try {
				List<RoleRelation> rrList = roleRelationService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(rrList)) {
					List<String> roleUserList = rrList.stream().map(RoleRelation::getOssUserId).collect(Collectors.toList());
					// 过滤出拥有此角色的用户
					userList = userList.stream().filter(user -> roleUserList.contains(user.getOssUserId())).collect(Collectors.toList());
				} else {
					logger.info("无拥有角色id：" + reqDto.getRoleId() + "的用户");
					return BaseResponse.success(result);
				}
			} catch (ServiceException e) {
				logger.error("", e);
			}
		}
		// 按 姓名，登录名，id，手机号 过滤
		if (StringUtils.isNotBlank(reqDto.getKeyword())) {
			userList = userList.stream().filter(user -> {
				boolean match = false;
				if (user.getRealName() != null) {
					match = user.getRealName().contains(reqDto.getKeyword());
				}
				if (!match && user.getLoginName() != null) {
					match = user.getLoginName().contains(reqDto.getKeyword());
				}
				if (!match && user.getOssUserId() != null) {
					match = user.getOssUserId().contains(reqDto.getKeyword());
				}
				if (!match && user.getContactMobile() != null) {
					match = user.getContactMobile().contains(reqDto.getKeyword());
				}
				return match;
			}).collect(Collectors.toList());
		}
		// 按状态过滤
		String status = reqDto.getStatus();
		String[] statuses = status.split(",");
		if (statuses.length == 1) {
			int userStatus = Integer.parseInt(statuses[0]);
			userList = userList.stream().filter(user -> user.getUstate() == userStatus).collect(Collectors.toList());
		}
		// 封装
		if (!CollectionUtils.isEmpty(userList)) {
			userList.forEach(u -> {
				result.add(new DeptOrUserInfo(u.getOssUserId(), u.getRealName(), 1, u.getUstate()));
			});
		}
		return BaseResponse.success(result);
	}
}
