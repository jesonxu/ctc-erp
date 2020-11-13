package com.dahantc.erp.controller.goal;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.royalty.service.IRealRoyaltyService;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.goal.GoalDto;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.enums.GoalType;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.enums.PagePermission;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.goal.entity.Goal;
import com.dahantc.erp.vo.goal.service.IGoalService;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("goal")
public class GoalAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(GoalAction.class);

	private static final String downLoadFile = "exportFile/goalDetail";

	private static final String[] salesmanTitle = new String[] { "区域", "事业部", "部门", "销售", "年总销售额(万)", "年总毛利(万)", "1月销售额(万)", "1月毛利(万)", "2月销售额(万)", "2月毛利(万)",
			"3月销售额(万)", "3月毛利(万)", "4月销售额(万)", "4月毛利(万)", "5月销售额(万)", "5月毛利(万)", "6月销售额(万)", "6月毛利(万)", "7月销售额(万)", "7月毛利(万)", "8月销售额(万)", "8月毛利(万)",
			"9月销售额(万)", "9月毛利(万)", "10月销售额(万)", "10月毛利(万)", "11月销售额(万)", "11月毛利(万)", "12月销售额(万)", "12月毛利(万)" };

	private static final String[] companyTitle = new String[] { "区域", "事业部", "年总销售额(万)", "年总毛利(万)", "1月销售额(万)", "1月毛利(万)", "2月销售额(万)", "2月毛利(万)", "3月销售额(万)",
			"3月毛利(万)", "4月销售额(万)", "4月毛利(万)", "5月销售额(万)", "5月毛利(万)", "6月销售额(万)", "6月毛利(万)", "7月销售额(万)", "7月毛利(万)", "8月销售额(万)", "8月毛利(万)", "9月销售额(万)", "9月毛利(万)",
			"10月销售额(万)", "10月毛利(万)", "11月销售额(万)", "11月毛利(万)", "12月销售额(万)", "12月毛利(万)" };

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IGoalService goalService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IRealRoyaltyService realRoyaltyService;

	/**
	 * 跳转到销售/公司年度目标表
	 */
	@RequestMapping("/toGoalSheet")
	public String toGoalSheet() {
		try {
			Map<String, Boolean> pagePermission = roleService.getPagePermission(getOnlineUserAndOnther().getRoleId());
			request.setAttribute("salesmanGoalTable", pagePermission.getOrDefault(PagePermission.salesmanGoalTable.getDesc(), false));
			request.setAttribute("companyGoalTable", pagePermission.getOrDefault(PagePermission.companyGoalTable.getDesc(), false));
		} catch (Exception e) {
			logger.error("查询角色页面权限异常", e);
			return "";
		}
		return "/views/manageConsole/goalSheet";
	}

	/**
	 * 查询销售年度目标明细
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/readSalesmanGoalDetail")
	public BaseResponse<JSONArray> readSalesmanGoalDetail(@RequestParam String year) {
		logger.info("查询销售年度目标明细开始，年份：" + year);
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		List<GoalDto> detailUIList = getSalesmanGoalDetail(year);
		if (!CollectionUtils.isEmpty(detailUIList)) {
			result = JSONArray.parseArray(JSON.toJSONString(detailUIList));
		}
		return BaseResponse.success(result);
	}

	/**
	 * 获取业绩目标数据并封装UI对象
	 *
	 * @param year
	 *            年份
	 * @return
	 */
	private List<GoalDto> getSalesmanGoalDetail(String year) {
		logger.info("获取销售业绩目标数据开始，year：" + year);
		long _start = System.currentTimeMillis();
		List<GoalDto> detailUIList = null;
		String yearstart = year + "-01-01 00:00:00";
		String yearend = year + "-12-31 23:59:59";

		// 按权限找用户
		List<User> userList = userService.readUsers(getOnlineUserAndOnther(), "", "", "");
		if (CollectionUtils.isEmpty(userList)) {
			return null;
		}
		// 从中过滤出未禁用的销售
		userList = userList.stream().filter(user -> user.getUstate() == UserStatus.ACTIVE.getValue() && StringUtils.contains(user.getJobType(), JobType.Sales.name())).collect(Collectors.toList());
		List<String> userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(userIdList)) {
			logger.info("用户数据权限下没有销售");
			return detailUIList;
		}

		// 每个销售的业绩目标
		Map<String, GoalDto> goalDetailMap = new HashMap<>();

		// 用户id -> {真实姓名、部门名、事业部名}
		Map<String, HashMap<String, String>> nameMap = userService.getUserAndDeptName();

		try {
			Date yearStart = DateUtil.convert2(yearstart);
			Date yearEnd = DateUtil.convert2(yearend);
			// 查询业绩目标表获取已设定的目标
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
			filter.getRules().add(new SearchRule("goalType", Constants.ROP_EQ, GoalType.SelfMonth.ordinal()));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, yearStart));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, yearEnd));
			List<Goal> goalList = goalService.queryAllBySearchFilter(filter);
			logger.info("查询到" + (goalList == null ? 0 : goalList.size()) + "条数据，耗时：" + (System.currentTimeMillis() - _start));
			// 将业绩目标按销售id分组
			Map<String, List<Goal>> goalMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(goalList)) {
				goalMap = goalList.stream().collect(Collectors.groupingBy(Goal::getOssUserId));
			}

			// 遍历所有销售，封装业绩目标UI对象
			for (User user : userList) {
				// 当前销售的名字、部门、事业部名
				HashMap<String, String> info = nameMap.getOrDefault(user.getOssUserId(), new HashMap<>());

				// 当前销售的已设定的目标
				List<Goal> userGoalList = goalMap.getOrDefault(user.getOssUserId(), new ArrayList<>());

				// 当前销售的业绩目标UI，没有就新建
				GoalDto goalDetail = goalDetailMap.getOrDefault(user.getOssUserId(), new GoalDto());
				goalDetail.setRegionName(info.getOrDefault("regionName", "-"));
				// 没有名字则显示登录名
				goalDetail.setRealName(info.getOrDefault("realName", user.getLoginName()));
				goalDetail.setDeptName(info.getOrDefault("deptName", "-"));
				goalDetail.setOssUserId(user.getOssUserId());
				goalDetail.setDeptId(user.getDeptId());
				String parentDeptName = info.getOrDefault("parentDeptName", "-");
				goalDetail.setParentDeptName(parentDeptName);

				// 遍历当前销售已设定的目标
				for (Goal goal : userGoalList) {
					String month = DateUtil.convert(goal.getWtime(), DateUtil.format6);
					BigDecimal receivables = goal.getReceivables();
					BigDecimal grossProfit = goal.getGrossProfit();
					// 累计到年数据
					goalDetail.addSumGrossProfit(grossProfit);
					goalDetail.addSumReceivables(receivables);
					// 设置到销售UI月数据
					BigDecimal[] saleMonthData = goalDetail.getMonthData(month);
					saleMonthData[0] = receivables;
					saleMonthData[1] = grossProfit;
					goalDetail.setMonthData(month, saleMonthData);
				}
				goalDetailMap.put(user.getOssUserId(), goalDetail);
			}
			detailUIList = new ArrayList<>(goalDetailMap.values());
			// 按区域、事业部、部门排序
			detailUIList.sort((o1, o2) -> {
				if (o1.getRegionName().equals(o2.getRegionName())) {
					if (o1.getParentDeptName().equals(o2.getParentDeptName())) {
						return o1.getDeptName().compareTo(o2.getDeptName());
					} else {
						return o1.getParentDeptName().compareTo(o2.getParentDeptName());
					}
				} else {
					return o1.getRegionName().compareTo(o2.getRegionName());
				}
			});
			logger.info("获取销售业绩目标数据结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("获取销售业绩目标数据异常", e);
		} finally {
			if (nameMap != null) {
				nameMap.clear();
				nameMap = null;
			}
			if (goalDetailMap != null) {
				goalDetailMap.clear();
				goalDetailMap = null;
			}
			if (userList != null) {
				userList.clear();
				userList = null;
			}
		}
		return detailUIList;
	}

	/**
	 * 查询公司年度目标明细
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/readCompanyGoalDetail")
	public BaseResponse<JSONArray> readCompanyGoalDetail(@RequestParam String year) {
		logger.info("查询公司年度目标明细开始，年份：" + year);
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		List<GoalDto> detailUIList = getCompanyGoalDetail(year);
		if (!CollectionUtils.isEmpty(detailUIList)) {
			result = JSONArray.parseArray(JSON.toJSONString(detailUIList));
		}
		return BaseResponse.success(result);
	}

	/**
	 * 获取公司业绩目标数据并封装UI对象
	 *
	 * @param year
	 *            年份
	 * @return
	 */
	private List<GoalDto> getCompanyGoalDetail(String year) {
		logger.info("获取公司业绩目标数据开始，year：" + year);
		long _start = System.currentTimeMillis();
		List<GoalDto> detailUIList = null;
		String yearstart = year + "-01-01 00:00:00";
		String yearend = year + "-12-31 23:59:59";

		// 每个部门的业绩目标
		Map<String, GoalDto> goalDetailMap = new HashMap<>();

		// 获取区域和部门 {区域1 -> [事业部1,……]}
		Map<String, List<Department>> regionMap = getRegionAndDept();

		try {
			Date yearStart = DateUtil.convert2(yearstart);
			Date yearEnd = DateUtil.convert2(yearend);
			// 查询业绩目标表获取已设定的目标
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("goalType", Constants.ROP_EQ, GoalType.DeptMonth.ordinal()));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, yearStart));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, yearEnd));
			List<Goal> goalList = goalService.queryAllBySearchFilter(filter);
			logger.info("查询到" + (goalList == null ? 0 : goalList.size()) + "条数据，耗时：" + (System.currentTimeMillis() - _start));
			// 将业绩目标按部门id分组
			Map<String, List<Goal>> goalMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(goalList)) {
				goalMap = goalList.stream().collect(Collectors.groupingBy(Goal::getDeptId));
			}

			// 遍历所有区域
			for (Map.Entry<String, List<Department>> regionEntry : regionMap.entrySet()) {
				// 遍历区域内每个事业部
				List<Department> regionDeptList = regionEntry.getValue();
				for (Department dept : regionDeptList) {
					// 当前部门的已设定的目标
					List<Goal> deptGoalList = goalMap.getOrDefault(dept.getDeptid(), new ArrayList<>());

					// 当前事业部的业绩目标UI，没有就新建
					GoalDto goalDetail = goalDetailMap.getOrDefault(dept.getDeptid(), new GoalDto());
					goalDetail.setRegionName(regionEntry.getKey());
					// 不显示销售名
					goalDetail.setRealName("");
					goalDetail.setDeptName(dept.getDeptname());
					goalDetail.setParentDeptName(dept.getDeptname());
					goalDetail.setDeptId(dept.getDeptid());

					// 遍历已设定的目标
					for (Goal goal : deptGoalList) {
						String month = DateUtil.convert(goal.getWtime(), DateUtil.format6);
						BigDecimal receivables = goal.getReceivables();
						BigDecimal grossProfit = goal.getGrossProfit();
						// 累计到年数据
						goalDetail.addSumGrossProfit(grossProfit);
						goalDetail.addSumReceivables(receivables);
						// 设置到部门UI月数据
						BigDecimal[] saleMonthData = goalDetail.getMonthData(month);
						saleMonthData[0] = receivables;
						saleMonthData[1] = grossProfit;
						goalDetail.setMonthData(month, saleMonthData);
					}
					goalDetailMap.put(dept.getDeptid(), goalDetail);
				}
			}
			detailUIList = new ArrayList<>(goalDetailMap.values());
			// 按区域、事业部、部门排序
			detailUIList.sort((o1, o2) -> {
				if (o1.getRegionName().equals(o2.getRegionName())) {
					if (o1.getParentDeptName().equals(o2.getParentDeptName())) {
						return o1.getDeptName().compareTo(o2.getDeptName());
					} else {
						return o1.getParentDeptName().compareTo(o2.getParentDeptName());
					}
				} else {
					return o1.getRegionName().compareTo(o2.getRegionName());
				}
			});
			logger.info("获取公司业绩目标数据结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("获取公司业绩目标数据异常", e);
		} finally {
			if (regionMap != null) {
				regionMap.clear();
				regionMap = null;
			}
			if (goalDetailMap != null) {
				goalDetailMap.clear();
				goalDetailMap = null;
			}
		}
		return detailUIList;
	}

	/**
	 * 即时保存前台表格的单元格的修改后的数据
	 *
	 * @param field
	 *            修改的单元格
	 * @param value
	 *            修改后的值
	 * @param deptId
	 *            该条记录的部门id
	 * @param year
	 *            年份
	 * @param ossUserId
	 *            该条记录的销售id（销售目标表有，部门目标表没有）
	 * @return
	 */
	@PostMapping("/saveGoal")
	@ResponseBody
	public BaseResponse<String> saveGoal(@RequestParam String field, @RequestParam String value, @RequestParam String deptId, @RequestParam String year,
			@RequestParam(required = false) String ossUserId) {
		logger.info("更新目标记录开始，field：" + field + "，value：" + value + "，deptId：" + deptId + "，year：" + year + "，ossUserId：" + ossUserId);
		int goalType;
		boolean isGrossProfit = false;
		Date date;
		String dataStr = "";
		String msg = "";
		try {
			// grossprofit毛利(万)，receivables销售额(万)
			field = field.toLowerCase();
			if (field.contains("grossprofit")) {
				isGrossProfit = true;
				dataStr = year + "-" + field.substring("grossprofit".length()) + "-01";
			} else if (field.contains("receivables")) {
				dataStr = year + "-" + field.substring("receivables".length()) + "-01";
			}
			// 销售个人目标会提交ossUserId，部门目标没有
			goalType = StringUtil.isBlank(ossUserId) ? GoalType.DeptMonth.ordinal() : GoalType.SelfMonth.ordinal();
			date = DateUtil.convert(dataStr, DateUtil.format1);
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("goalType", Constants.ROP_EQ, goalType));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_EQ, date));
			if (StringUtil.isNotBlank(deptId)) {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
			}
			if (StringUtil.isNotBlank(ossUserId)) {
				filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
			}
			boolean isUpdate = false;
			Goal goal = new Goal();
			List<Goal> goalList = goalService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(goalList)) {
				isUpdate = true;
				goal = goalList.get(0);
			}
			goal.setDeptId(deptId);
			goal.setOssUserId(ossUserId);
			goal.setGoalType(goalType);
			BigDecimal realValue = new BigDecimal(value.replaceAll(",", ""));
			if (isGrossProfit) {
				goal.setGrossProfit(realValue);
			} else {
				goal.setReceivables(realValue);
			}
			goal.setWtime(new Timestamp(date.getTime()));
			boolean result = isUpdate ? goalService.update(goal) : goalService.save(goal);
			msg = result ? "更新成功" : "更新失败";
		} catch (Exception e) {
			logger.info("更新业绩目标异常，value：" + value);
			msg = "更新异常，恢复上次有效值";
			return BaseResponse.error(msg);
		}
		logger.info(msg + "，value：" + value);
		return BaseResponse.success(msg);
	}

	/**
	 * 获取每个区域和该区域内的事业部
	 *
	 * @return {区域1 -> [事业部1,……]}
	 */
	private Map<String, List<Department>> getRegionAndDept() {
		logger.info("获取区域和区域内的事业部开始");
		Map<String, List<Department>> regionMap = new HashMap<>();
		try {
			// 获取所有二级部门，一级部门是公司，事业部和其他各部门是二级部门
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("parentid", Constants.ROP_EQ, "1"));
			List<Department> secondDepts = departmentService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(secondDepts)) {
				return regionMap;
			}
			// 事业部特征：部门名后有小括号“()”，小括号内即区域名
			secondDepts = secondDepts.stream()
					.filter(dept -> StringUtils.isNotBlank(dept.getDeptname()) && dept.getDeptname().contains("(") && dept.getDeptname().contains(")"))
					.collect(Collectors.toList());
			for (Department sDept : secondDepts) {
				String deptName = sDept.getDeptname();
				// 区域名
				String regionName = deptName.substring(deptName.indexOf("(") + 1, deptName.indexOf(")"));
				List<Department> regionDeptList = regionMap.getOrDefault(regionName, new ArrayList<>());
				regionDeptList.add(sDept);
				regionMap.put(regionName, regionDeptList);
			}
			logger.info("获取区域和区域内的事业部结束，获取到区域数：" + regionMap.size());
		} catch (Exception e) {
			logger.error("获取区域和区域内的事业部异常", e);
		}
		return regionMap;
	}

	/**
	 * 导出业绩目标到Excel
	 *
	 * @param year
	 *            年份
	 * @param type
	 *            类型，0部门，1销售
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportGoalDetail")
	public BaseResponse<UploadFileRespDto> exportGoalDetail(@RequestParam String year, @RequestParam int type) {
		logger.info("导出业绩目标开始，year：" + year + "，type：" + type);
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			logger.error("未登录，获取数据失败");
			return BaseResponse.error("未登录，获取数据失败");
		}
		List<String[]> dataList = null;
		UploadFileRespDto result = new UploadFileRespDto();
		String resourceDir = Constants.RESOURCE + File.separator + downLoadFile + File.separator + year;
		String fileName = "";
		String filePath = "";
		try {
			// 获取导出数据
			dataList = getExportData(year, type);
			// 导出文件路径
			fileName = year + "年" + (GoalType.SelfMonth.ordinal() == type ? "销售" : "公司") + "业绩目标";
			File dir = new File(resourceDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			filePath = resourceDir + File.separator + fileName + ".xls";
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			String[] title = getExportTitle(type);
			ParseFile.exportDataToExcel(dataList, file, title);
		} catch (Exception e) {
			logger.error("导出业绩目标异常", e);
			return BaseResponse.error("导出业绩目标异常");
		} finally {
			if (dataList != null) {
				dataList.clear();
				dataList = null;
			}
			result.setFilePath(filePath);
			result.setFileName(fileName + ".xls");
		}
		logger.info("导出业绩目标结束" + result.toString());
		return BaseResponse.success(result);
	}

	/**
	 * 获取导出excel的行数据
	 * 
	 * @param year
	 *            年份
	 * @param type
	 *            类型，0部门，1销售
	 * @return
	 */
	private List<String[]> getExportData(String year, int type) {
		logger.info("获取导出数据开始，year：" + year + "，type：" + type);
		List<String[]> dataList = new ArrayList<>();
		List<GoalDto> detailUIList = null;
		if (GoalType.SelfMonth.ordinal() == type) {
			detailUIList = getSalesmanGoalDetail(year);
		} else if (GoalType.DeptMonth.ordinal() == type) {
			detailUIList = getCompanyGoalDetail(year);
		}
		if (!CollectionUtils.isEmpty(detailUIList)) {
			String[] title = getExportTitle(type);
			detailUIList.forEach(detailUI -> {
				dataList.add(detailUI.toExportData(type, title.length));
			});
		}
		logger.info("获取导出数据结束，数据条数：" + dataList.size());
		return dataList;
	}

	/**
	 * 获取导出excel的表头
	 * 
	 * @param type
	 *            类型，0部门，1销售
	 * @return
	 */
	private String[] getExportTitle(int type) {
		if (GoalType.SelfMonth.ordinal() == type) {
			return salesmanTitle;
		} else if (GoalType.DeptMonth.ordinal() == type) {
			return companyTitle;
		}
		return new String[] {};
	}

	/**
	 * 查询销售年度目标明细
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/readSaleGoalCompletion")
	public BaseResponse<JSONObject> readSaleGoalCompletion(@RequestParam String startTime, @RequestParam(required = false) String timeType) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		logger.info("查询销售目标完成情况开始，月份：" + startTime);
		long _start = System.currentTimeMillis();
		JSONObject result = new JSONObject();
		// yyyy年MM月
		Date startDate = DateUtil.convert(startTime, "yyyy年MM月");
		startDate = DateUtil.getThisMonthFirst(startDate);
		Date endDate = DateUtil.getNextMonthFirst(startDate);

		// 查目标
		BigDecimal totalGoalReceivable = new BigDecimal(0);
		BigDecimal totalGoalGrossProfit = new BigDecimal(0);
		JSONObject goalInfo = goalService.querySaleGoal(startDate, endDate, onlineUser);
		if (goalInfo != null) {
			String totalGoalReceivableStr = goalInfo.getString("totalGoalReceivable");
			totalGoalReceivable = new BigDecimal(totalGoalReceivableStr).multiply(BigDecimal.valueOf(10000));
			result.put("totalGoalReceivable", totalGoalReceivable.toPlainString());

			String totalGoalGrossProfitStr = goalInfo.getString("totalGoalGrossProfit");
			totalGoalGrossProfit = new BigDecimal(totalGoalGrossProfitStr).multiply(BigDecimal.valueOf(10000));
			result.put("totalGoalGrossProfit", totalGoalGrossProfit.toPlainString());
		}

		// 查到款
		BigDecimal totalIncome = new BigDecimal(0);
		JSONObject incomeInfo = fsExpenseIncomeService.querySaleIncome(startDate, endDate, timeType, onlineUser);
		if (incomeInfo != null) {
			String totalIncomeStr = incomeInfo.getString("totalIncome");
			totalIncome = new BigDecimal(totalIncomeStr);
			result.put("totalActualReceivable", totalIncomeStr);
		}

		// 查销账账单毛利
		BigDecimal totalGrossProfit = new BigDecimal(0);
		JSONObject grossProfitInfo = realRoyaltyService.queryBillGrossProfit(startDate, endDate, onlineUser);
		if (grossProfitInfo != null) {
			String totalGrossProfitStr = grossProfitInfo.getString("totalGrossProfit");
			totalGrossProfit = new BigDecimal(totalGrossProfitStr);
			result.put("totalGrossProfit", totalGrossProfit);
		}

		DecimalFormat format = new DecimalFormat("##.##%");
		if (totalGoalReceivable.compareTo(BigDecimal.ZERO) > 0) {
			result.put("receivableRatio", format.format(totalIncome.divide(totalGoalReceivable, 4, BigDecimal.ROUND_HALF_UP)));
		} else {
			result.put("receivableRatio", format.format(0));
		}
		if (totalGoalGrossProfit.compareTo(BigDecimal.ZERO) > 0) {
			result.put("grossProfitRatio", format.format(totalGrossProfit.divide(totalGoalGrossProfit, 4, BigDecimal.ROUND_HALF_UP)));
		} else {
			result.put("grossProfitRatio", format.format(0));
		}
		return BaseResponse.success(result);
	}
}
