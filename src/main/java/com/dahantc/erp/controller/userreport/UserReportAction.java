package com.dahantc.erp.controller.userreport;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.department.DepartmentAction;
import com.dahantc.erp.dto.department.DeptInfo;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.dto.userreport.ReportCommentDto;
import com.dahantc.erp.dto.userreport.SaveUserReportDto;
import com.dahantc.erp.dto.userreport.UserReportDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.enums.ReportType;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.reportcomment.entity.ReportComment;
import com.dahantc.erp.vo.reportcomment.service.IReportCommentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userreport.entity.EnclosureFile;
import com.dahantc.erp.vo.userreport.entity.UserReport;
import com.dahantc.erp.vo.userreport.service.IUserReportService;

/**
 * 用户报告以及评论查询、创建Action
 *
 */
@Controller
@RequestMapping("/userReport")
public class UserReportAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(UserReportAction.class);

	@Autowired
	private DepartmentAction departmentAction;

	@Autowired
	private IUserReportService userReportService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IReportCommentService reportCommentService;

	private final static String uploadFile = "upFile/userreport";

	private final static String uploadTextImg = "upFile/textimg";

	/**
	 * 跳转到用户报告页面
	 */
	@RequestMapping("/toUserReport")
	public String toUserReport() {
		return "/views/messageInfo/userReport";
	}

	// 上传文件
	@PostMapping("/uploadFile")
	@ResponseBody
	public BaseResponse<List<UploadFileRespDto>> uploadFile(@RequestParam("file") MultipartFile[] files) {
		List<UploadFileRespDto> dtos = new ArrayList<UploadFileRespDto>();
		try {
			if (files != null && files.length > 0) {
				for (MultipartFile multipartFile : files) {
					String docFileName = multipartFile.getOriginalFilename();
					if (StringUtils.isNotBlank(docFileName)) {
						String ext = docFileName.substring(docFileName.lastIndexOf(".") + 1, docFileName.length());
						String reName = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "") + "." + ext;
						String resource = Constants.RESOURCE;
						String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
						String resourceDir = resource + File.separator + uploadFile + File.separator + datePath;
						File dir = new File(resourceDir);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						String disPath = resourceDir + File.separator + reName;
						File disFile = new File(disPath);
						multipartFile.transferTo(disFile);
						UploadFileRespDto dto = new UploadFileRespDto();
						dto.setFileName(docFileName);
						dto.setFilePath(disPath);
						dtos.add(dto);
					}
				}
			}
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
			return BaseResponse.error("上传异常");
		}
		return BaseResponse.success(dtos);
	}

	// 保存图片
	@RequestMapping("/uploadTextImg")
	@ResponseBody
	public JSONObject uploadTextImg(@RequestParam MultipartFile file) {
		JSONObject json = new JSONObject();
		JSONObject fileJson = new JSONObject();
		try {
			if (file != null) {
				String docFileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(docFileName)) {
					String ext = docFileName.substring(docFileName.lastIndexOf(".") + 1, docFileName.length());
					String reName = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "") + "." + ext;
					String resource = Constants.RESOURCE;
					String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
					String resourceDir = resource + File.separator + uploadTextImg + File.separator + datePath;
					File dir = new File(resourceDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					String disPath = resourceDir + File.separator + reName;
					File disFile = new File(disPath);
					file.transferTo(disFile);

					json.put("code", 0);
					json.put("msg", "上传成功");
					fileJson.put("src", "/operate/downloadFile?filePath=" + URLEncoder.encode(disPath, "utf-8") + "&fileName=" + docFileName);
					fileJson.put("title", docFileName);
					json.put("data", fileJson);
					return json;
				}
			}
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
		}
		json.put("code", 2);
		json.put("msg", "上传出错");
		fileJson.put("src", "");
		json.put("data", fileJson);
		return json;
	}

	// 保存汇报内容
	@PostMapping("/saveUserReport")
	@ResponseBody
	public BaseResponse<String> saveUserReport(@Valid SaveUserReportDto dto) {
		try {
			logger.info("保存汇报内容开始。。。。。。");
			long _start = System.currentTimeMillis();
			boolean isCreate = true;
			UserReport userReport = null;
			if (StringUtils.isNotBlank(dto.getReportId())) {
				isCreate = false;
				userReport = userReportService.read(dto.getReportId());
			} else {
				userReport = new UserReport();
				userReport.setDeptId(getOnlineUser().getDeptId());
				userReport.setOssUserId(getOnlineUser().getOssUserId());
				userReport.setReportType(dto.getReportType());
				if (dto.getReportType() != ReportType.DAYLY.ordinal() && dto.getReportType() != ReportType.ANNUAL_REPORT.ordinal()) {
					userReport.setReportCyle(DateUtil.getCycleNum(dto.getReportType()));
				}
			}
			userReport.setContent(dto.getContent());
			if (StringUtils.isBlank(dto.getFiles())) {
				userReport.setEnclosure("");
			} else {
				List<EnclosureFile> list = new ArrayList<>();
				String[] strArr = dto.getFiles().split(";");
				for (String str : strArr) {
					if (StringUtils.isNotBlank(str)) {
						String[] arr = str.split(",");
						if (arr != null && arr.length >= 2) {
							list.add(new EnclosureFile(arr[0], arr[1]));
						}
					}
				}
				if (!CollectionUtils.isEmpty(list)) {
					userReport.setEnclosure(JSON.toJSONString(list));
				}
			}
			if (isCreate) {
				userReportService.save(userReport);
			} else {
				userReportService.update(userReport);
			}
			logger.info("保存汇报内容结束，耗时：【" + (System.currentTimeMillis() - _start) + "】毫秒");
			return BaseResponse.success("保存成功!", userReport.getId());
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
			return BaseResponse.error("保存异常!");
		}
	}

	// 查询汇报最早时间,作为回报的最早时间节点
	@RequestMapping("/queryReportLastTime")
	@ResponseBody
	public BaseResponse<String> queryReportLastTime() {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			PageResult<UserReport> result = userReportService.queryByPages(1, 1, searchFilter);

			if (result != null && !CollectionUtils.isEmpty(result.getData())) {
				Date lastTime = result.getData().get(0).getWtime();
				return BaseResponse.success(DateUtil.convert(lastTime, DateUtil.format2));
			} else {
				return BaseResponse.success(DateUtil.convert(new Date(), DateUtil.format2));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("无数据");
	}

	// 最新的汇报时间,默认展开的时间节点
	@RequestMapping("/queryReportNewestTime")
	@ResponseBody
	public BaseResponse<String> queryReportNewestTime(@RequestParam Integer type) {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("reportType", Constants.ROP_EQ, type));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));

			PageResult<UserReport> result = userReportService.queryByPages(1, 1, searchFilter);

			if (result != null && !CollectionUtils.isEmpty(result.getData())) {
				Date lastTime = result.getData().get(0).getWtime();
				return BaseResponse.success(DateUtil.convert(lastTime, DateUtil.format2));
			} else {
				return BaseResponse.success(DateUtil.convert(new Date(), DateUtil.format2));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("无数据");
	}

	/** 移动端分页查询汇报 */
	@RequestMapping("/queryUserReportByPage4Mobile")
	@ResponseBody
	public BaseResponse<PageResult<UserReportDto>> queryUserReportByPage4Mobile(@RequestParam(required = false) String deptId,
			@RequestParam(required = false) String userId, Integer reportType, String queryDate, Integer pageSize, Integer currentPage) {

		try {
			List<String> userIdList = new ArrayList<>();
			List<String> deptIdList = new ArrayList<>();
			if (StringUtils.isAllBlank(deptId, userId)) {
				BaseResponse<List<DeptInfo>> deptInforesult = departmentAction.searchDepartment();
				if (!CollectionUtils.isEmpty(deptInforesult.getData())) {
					deptInforesult.getData().forEach(info -> {
						if (StringUtils.equals(info.getNodeType(), "user")) {
							userIdList.add(info.getId());
						} else if (StringUtils.equals(info.getNodeType(), "dept")) {
							deptIdList.add(info.getId());
						}
					});
				}
			} else if (StringUtils.isNotBlank(userId)) {
				userIdList.add(userId);
			} else {
				deptIdList.add(deptId);
			}

			if (CollectionUtils.isEmpty(deptIdList) && CollectionUtils.isEmpty(userIdList)) {
				return BaseResponse.success(new PageResult<>());
			}

			SearchFilter searchFilter = new SearchFilter();
			if (!CollectionUtils.isEmpty(userIdList) && !CollectionUtils.isEmpty(deptIdList)) {
				searchFilter.getOrRules().add(
						new SearchRule[] { new SearchRule("ossUserId", Constants.ROP_IN, userIdList), new SearchRule("deptId", Constants.ROP_IN, deptIdList) });
			} else if (!CollectionUtils.isEmpty(userIdList)) {
				searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
			} else {
				searchFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
			}
			searchFilter.getRules().add(new SearchRule("reportType", Constants.ROP_EQ, reportType));

			String[] arr = queryDate.split(" - ");
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert1(arr[0])));
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getDateEndDateTime(DateUtil.convert1(arr[1]))));

			searchFilter.getOrders().add(new SearchOrder("deptId", Constants.ROP_DESC));
			searchFilter.getOrders().add(new SearchOrder("ossUserId", Constants.ROP_DESC));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));

			PageResult<UserReport> pageResult = userReportService.queryByPages(pageSize, currentPage, searchFilter);

			List<UserReport> data = pageResult.getData();

			if (!CollectionUtils.isEmpty(data)) {

				Map<String, User> allUserMap = new HashMap<>();
				List<User> allUserList = userService.queryAllBySearchFilter(null);
				if (!CollectionUtils.isEmpty(allUserList)) {
					allUserList.forEach(user -> {
						allUserMap.put(user.getOssUserId(), user);
					});
				}

				Map<String, String> cacheDeptMap = new HashMap<>();
				List<Department> departments = departmentService.queryAllBySearchFilter(null);
				departments.forEach(dept -> {
					cacheDeptMap.put(dept.getDeptid(), dept.getDeptname());
				});

				User onlineUser = getOnlineUser();

				List<UserReportDto> dtoList = new ArrayList<>();
				for (UserReport report : data) {
					userIdList.remove(report.getOssUserId());
					UserReportDto dto = buildDto(report, cacheDeptMap, allUserMap, onlineUser.getOssUserId(), false);
					if (dto != null) {
						dtoList.add(dto);
					}
				}

				PageResult<UserReportDto> result = new PageResult<>();
				result.setCode(pageResult.getCode());
				result.setCount(pageResult.getCount());
				result.setCurrentPage(pageResult.getCurrentPage());
				result.setData(dtoList);
				result.setMsg(pageResult.getMsg());
				result.setTotalPages(
						(int) (pageResult.getCount() % pageSize == 0 ? (pageResult.getCount() / pageSize) : (pageResult.getCount() / pageSize + 1)));

				return BaseResponse.success(result);
			} else {
				return BaseResponse.success(new PageResult<>());
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return BaseResponse.error("查询异常");
	}

	@RequestMapping("/queryUserReports")
	@ResponseBody
	public BaseResponse<List<UserReportDto>> queryUserReports(@RequestParam(required = false) String deptId, @RequestParam(required = false) String userId,
			Integer reportType, String queryDate, Boolean ordinary) {
		List<UserReportDto> dtoList = new ArrayList<>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();

			Role role = roleService.read(onlineUser.getRoleId());

			final List<User> userList = new ArrayList<>(); // 查询哪些人的

			final List<Department> departments = new ArrayList<>();

			boolean querySelf = false;

			// 角色权限是自己但是身份是领导的按部门权限处理
			if (role.getDataPermission() == DataPermission.Self.ordinal() && onlineUser.getUser().getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal()) {
				role.setDataPermission(DataPermission.Dept.ordinal());
			}

			// 角色权限是自己/流程，或全部权限但身份不是领导的，只查我的汇报
			if (role.getDataPermission() == DataPermission.Self.ordinal() || role.getDataPermission() == DataPermission.Flow.ordinal()
					|| (role.getDataPermission() == DataPermission.All.ordinal()
							&& onlineUser.getUser().getIdentityType() == IdentityType.ORDINARY_MEMBER.ordinal())) {
				querySelf = true;
			}

			// 部门和用户id都为空按权限查，否则按照已选择的部门用户id查询
			if (StringUtils.isAllBlank(deptId, userId)) {
				if (querySelf) {
					// 查我的汇报
					departments.add(departmentService.read(onlineUser.getUser().getDeptId()));
					userList.add(onlineUser.getUser());
				} else {
					if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
						// 部门权限，查直属子部门
						List<Department> deptList = departmentService.getDeptByFatherId(onlineUser.getUser().getDeptId(), null);
						List<String> searchUserDeptIdList = new ArrayList<>();
						SearchFilter userFilter = new SearchFilter();
						// 存放当前用户的部门，以及直属子部门
						departments.add(departmentService.read(onlineUser.getUser().getDeptId()));
						searchUserDeptIdList.add(onlineUser.getUser().getDeptId());

						if (!CollectionUtils.isEmpty(deptList)) {
							departments.addAll(deptList);
							deptList.forEach(dept -> searchUserDeptIdList.add(dept.getDeptid()));
						}

						if (!CollectionUtils.isEmpty(searchUserDeptIdList)) {
							userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchUserDeptIdList));
							queryUserList(onlineUser, userList, searchUserDeptIdList, userFilter, onlineUser.getUser().getDeptId());
						}
					} else if (DataPermission.All.ordinal() == role.getDataPermission()) {
						// 全部权限，查部门根节点，生产环境只有一个根节点
						List<Department> deptList = departmentService.getRootDept();
						// 查根节点的直属子部门
						if (!CollectionUtils.isEmpty(deptList)) {
							deptList.forEach(dept -> {
								departments.addAll(departmentService.getDeptByFatherId(dept.getDeptid(), null));
							});
						}
						List<String> searchUserDeptIdList = new ArrayList<>();
						SearchFilter userFilter = new SearchFilter();
						departments.forEach(dept -> searchUserDeptIdList.add(dept.getDeptid()));

						if (!CollectionUtils.isEmpty(searchUserDeptIdList)) {
							userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchUserDeptIdList));
							queryUserList(onlineUser, userList, searchUserDeptIdList, userFilter, null);
						}
					} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
						// 自定义权限，查自定义的部门
						List<String> searchUserDeptIdList = new ArrayList<>();
						SearchFilter userFilter = new SearchFilter();

						String deptIds = role.getDeptIds();
						if (StringUtils.isNotBlank(deptIds)) {
							List<String> depts = Arrays.asList(deptIds.split(","));
							SearchFilter deptFilter = new SearchFilter();
							deptFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, depts));
							deptFilter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
							deptFilter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
							List<Department> deptList = departmentService.queryAllBySearchFilter(deptFilter);
							if (!CollectionUtils.isEmpty(deptList)) {
								Set<String> set = deptList.stream().map(Department::getDeptid).collect(Collectors.toSet());
								deptList.forEach(dept -> {
									if (!set.contains(dept.getParentid())) {
										searchUserDeptIdList.add(dept.getDeptid());
										departments.add(dept);
									}
								});
							}

							if (!CollectionUtils.isEmpty(searchUserDeptIdList)) {
								userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchUserDeptIdList));
								queryUserList(onlineUser, userList, searchUserDeptIdList, userFilter, null);
							}
						}
					}
				}
			} else if (StringUtils.isNotBlank(deptId)) {
				// 查询点击部门
				if (!ordinary) {
					// 点击组织架构中的部门
					List<String> searchUserDeptIdList = new ArrayList<>();
					SearchFilter userFilter = new SearchFilter();
					searchUserDeptIdList.add(deptId);
					departments.add(departmentService.read(deptId));

					if (!CollectionUtils.isEmpty(searchUserDeptIdList)) {
						userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchUserDeptIdList));
						queryUserList(onlineUser, userList, searchUserDeptIdList, userFilter, null);
					}
				} else {
					// 在部门领导的汇报上，点查看部门人员
					List<Department> subDepartmentList = departmentService.getDeptByFatherId(deptId, null);
					List<String> searchUserDeptIdList = new ArrayList<>();
					SearchFilter userFilter = new SearchFilter();

					searchUserDeptIdList.add(deptId);
					departments.add(departmentService.read(deptId));

					if (!CollectionUtils.isEmpty(subDepartmentList)) {
						departments.addAll(subDepartmentList);
						subDepartmentList.forEach(dept -> {
							searchUserDeptIdList.add(dept.getDeptid());
						});
					}

					if (!CollectionUtils.isEmpty(searchUserDeptIdList)) {
						userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchUserDeptIdList));
						queryUserList(onlineUser, userList, searchUserDeptIdList, userFilter, deptId);
					}
				}
			} else {
				// 点击员工查询
				User user = userService.read(userId);
				userList.add(user);
				departments.add(departmentService.read(user.getDeptId()));
			}

			if (CollectionUtils.isEmpty(userList)) {
				return BaseResponse.success(dtoList);
			}

			Map<String, String> cacheDeptMap = new HashMap<>();
			List<String> userIdList = new ArrayList<>();

			departments.forEach(dept -> {
				cacheDeptMap.put(dept.getDeptid(), dept.getDeptname());
			});

			userList.forEach(user -> {
				userIdList.add(user.getOssUserId());
			});

			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
			Date startTime = null;
			Date endTime = null;
			String[] arr = queryDate.split("~");
			if (arr != null && arr.length == 2) {
				startTime = DateUtil.convert2(arr[0]);
				endTime = DateUtil.convert2(arr[1]);
			}
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endTime));
			filter.getRules().add(new SearchRule("reportType", Constants.ROP_EQ, reportType));
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			filter.getOrders().add(new SearchOrder("deptId", Constants.ROP_DESC));
			filter.getOrders().add(new SearchOrder("ossUserId", Constants.ROP_DESC));
			List<UserReport> list = userReportService.queryAllBySearchFilter(filter);

			Map<String, User> allUserMap = new HashMap<>();
			List<User> allUserList = userService.queryAllBySearchFilter(null);
			if (!CollectionUtils.isEmpty(allUserList)) {
				allUserList.forEach(user -> {
					allUserMap.put(user.getOssUserId(), user);
				});
			}
			// 补充虚拟节点的员工
			if (!CollectionUtils.isEmpty(userList)) {
				userList.forEach(user -> {
					allUserMap.putIfAbsent(user.getOssUserId(), user);
				});
			}

			for (UserReport report : list) {
				userIdList.remove(report.getOssUserId());
				UserReportDto dto = buildDto(report, cacheDeptMap, allUserMap, onlineUser.getUser().getOssUserId(), querySelf);
				if (dto != null) {
					dtoList.add(dto);
				}
			}

			if (!CollectionUtils.isEmpty(userIdList)) { // 补充无汇报的用户
				for (String uId : userIdList) {
					User noReportUser = allUserMap.get(uId);
					UserReportDto dto = new UserReportDto();
					dto.setBlankReport(true);
					dto.setDeptId(noReportUser.getDeptId());
					dto.setUserName(StringUtils.isBlank(noReportUser.getRealName()) ? cacheDeptMap.get(noReportUser.getDeptId())
							: (cacheDeptMap.get(noReportUser.getDeptId()) + " - " + noReportUser.getRealName()));
					dto.setLeader(!querySelf && noReportUser.getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal());
					dtoList.add(dto);
				}
			}

			dtoList.sort((dto1, dto2) -> {
				if (dto1.isLeader() && !dto2.isLeader()) {
					return -1;
				}
				if (dto2.isLeader() && !dto1.isLeader()) {
					return 1;
				}
				if (dto1.isBlankReport() && !dto2.isBlankReport()) {
					return 1;
				}
				if (dto2.isBlankReport() && !dto1.isBlankReport()) {
					return -1;
				}
				return dto1.getUserName().compareTo(dto2.getUserName());
			});
			BaseResponse.success(dtoList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.success(dtoList);
	}

	/**
	 *
	 * @param onlineUser
	 * @param userList					查询员工的结果
	 * @param searchUserDeptIdList		部门id范围
	 * @param userFilter				过滤条件
	 * @param filterDeptId
	 * @throws ServiceException
	 */
	private void queryUserList(OnlineUser onlineUser, final List<User> userList, List<String> searchUserDeptIdList, SearchFilter userFilter,
			String filterDeptId) throws ServiceException {
		userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
		userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
		List<User> uList = userService.queryAllBySearchFilter(userFilter);
		Map<String, List<User>> deptUserMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(uList)) {
			uList.forEach(u -> {
				// 员工是哪个部门的
				List<User> deptUserList = deptUserMap.getOrDefault(u.getDeptId(), new ArrayList<>());
				deptUserList.add(u);
				deptUserMap.put(u.getDeptId(), deptUserList);

				// 只展示 子部门的领导 和 当前部门的员工
				if ((u.getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal() && !StringUtils.equals(filterDeptId, u.getDeptId()))
						|| (StringUtils.isNotBlank(filterDeptId) && u.getIdentityType() == IdentityType.ORDINARY_MEMBER.ordinal() && StringUtils.equals(filterDeptId, u.getDeptId()))) {
					userList.add(u);
					searchUserDeptIdList.remove(u.getDeptId());
				}
			});
		}
		if (StringUtils.isNotBlank(filterDeptId)) {
			searchUserDeptIdList.remove(filterDeptId);
		}
		// 剩余没有领导的子部门
		if (!CollectionUtils.isEmpty(searchUserDeptIdList)) {
			searchUserDeptIdList.forEach(dId -> {
				List<User> deptUserList = deptUserMap.getOrDefault(dId, new ArrayList<>());
				if (deptUserList.size() == 1 && CollectionUtils.isEmpty(departmentService.getDeptByFatherId(dId, null))) {
					// 部门没领导，但有1个员工，并且没有直属子部门，则展示员工
					userList.add(deptUserList.get(0));
				} else {
					// 部门没领导，员工数为0或者大于1个，或者有直属子部门，创建虚拟节点
					User user = new User();
					user.setOssUserId(UUID.randomUUID().toString().replace("-", ""));
					user.setDeptId(dId);
					user.setIdentityType(IdentityType.LEADER_IN_DEPT.ordinal());
					userList.add(user);
				}
			});
		}
	}

	private UserReportDto buildDto(UserReport report, Map<String, String> cacheDeptMap, Map<String, User> allUserMap, String ossUserId, boolean querySelf) {
		UserReportDto dto = new UserReportDto();
		dto.setBlankReport(false);
		if (StringUtils.equals(report.getOssUserId(), ossUserId)) {
			dto.setModify(true);
		} else {
			dto.setModify(false);
		}
		dto.setId(report.getId());
		dto.setContent(report.getContent());
		dto.setLeader(!querySelf && allUserMap.get(report.getOssUserId()).getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal());
		dto.setDeptId(report.getDeptId());
		dto.setUserName(cacheDeptMap.get(report.getDeptId()) + " - " + allUserMap.get(report.getOssUserId()).getRealName());
		if (report.getReportType() == ReportType.WEEKLY.ordinal()) {
			dto.setCycle("第" + report.getReportCyle() + "周");
		} else if (report.getReportType() == ReportType.QUARTERLY_REPORT.ordinal()) {
			dto.setCycle("第" + report.getReportCyle() + "季");
		} else if (report.getReportType() == ReportType.SEMIANNUAL_REPORT.ordinal()) {
			if (report.getReportCyle() == 1) {
				dto.setCycle("上半年");
			} else {
				dto.setCycle("下半年");
			}
		}
		dto.setEnclosure(report.getEnclosureFile());
		dto.setComments(queryReportComments(report.getId(), allUserMap, ossUserId));
		dto.setWtime(report.getWtime().getTime());
		return dto;
	}

	private List<ReportCommentDto> queryReportComments(String reportId, Map<String, User> allUserMap, String ossUserId) {
		List<ReportCommentDto> dtos = new ArrayList<>();
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("reportId", Constants.ROP_EQ, reportId));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<ReportComment> list = reportCommentService.queryAllBySearchFilter(searchFilter);
			if (!CollectionUtils.isEmpty(list)) {
				list.forEach(comment -> {
					ReportCommentDto dto = new ReportCommentDto();
					dto.setId(comment.getId());
					dto.setComment(comment.getComment());
					dto.setReportId(reportId);
					dto.setUserName(allUserMap.get(comment.getOssUserId()).getRealName());
					dto.setWtime(comment.getWtime().getTime());
					dtos.add(dto);
				});
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return dtos;
	}

	/** 查询评论 */
	@RequestMapping("/queryReportCommentsByReportId")
	@ResponseBody
	public BaseResponse<Object> queryReportCommentsByReportId(@RequestParam(required = true) String reportId) {
		try {
			String ossUserId = getOnlineUser().getOssUserId();

			Map<String, User> allUserMap = new HashMap<>();
			List<User> userList = userService.queryAllBySearchFilter(null);
			if (!CollectionUtils.isEmpty(userList)) {
				userList.forEach(user -> {
					allUserMap.put(user.getOssUserId(), user);
				});
			}

			return BaseResponse.success(queryReportComments(reportId, allUserMap, ossUserId));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.success("暂无数据");
	}

	/** 查询评论 */
	@RequestMapping("/queryHasReport")
	@ResponseBody
	public BaseResponse<Object> queryHasReport(@RequestParam Integer type, @RequestParam String deptId, @RequestParam String userId,
			@RequestParam(required = false) String queryDate) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			List<String> deptIdList = new ArrayList<>();
			if (StringUtils.isNotBlank(deptId)) {
				deptIdList.add(deptId);
			} else if (StringUtils.isBlank(userId)) {
				Role role = roleService.read(onlineUser.getRoleId());
				if (role.getDataPermission() == DataPermission.Self.ordinal() || role.getDataPermission() == DataPermission.Flow.ordinal()
						|| (role.getDataPermission() == DataPermission.All.ordinal()
								&& onlineUser.getUser().getIdentityType() == IdentityType.ORDINARY_MEMBER.ordinal())) {
					userId = onlineUser.getUser().getOssUserId();
				} else {
					List<String> list = departmentService.getDeptIdsByPermission(onlineUser);
					if (!CollectionUtils.isEmpty(list)) {
						deptIdList.addAll(list);
					}
				}
			}
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.isNotBlank(queryDate)) {
				String[] strArr = queryDate.split("~");
				startDate = DateUtil.convert2(strArr[0]);
				endDate = DateUtil.convert2(strArr[1]);
			}

			List<String> dateList = userReportService.queryHasReport(type, userId, deptIdList, startDate, endDate);
			return BaseResponse.success(dateList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.success("暂无数据");
	}

	/** 保存评论内容 */
	@RequestMapping("/commentReport")
	@ResponseBody
	public BaseResponse<ReportComment> commentReport(String reportId, String reportComment) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			UserReport userReport = userReportService.read(reportId);
			if (userReport == null) {
				return BaseResponse.error("汇报不存在!");
			}
			ReportComment comment = new ReportComment();
			comment.setReportId(reportId);
			comment.setComment(reportComment);
			comment.setDeptId(onlineUser.getUser().getDeptId());
			comment.setOssUserId(onlineUser.getUser().getOssUserId());
			reportCommentService.save(comment);
			return BaseResponse.success("保存评论成功!", comment);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("保存评论失败!");
	}

}
