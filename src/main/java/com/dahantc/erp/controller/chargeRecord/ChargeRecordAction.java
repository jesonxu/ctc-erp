package com.dahantc.erp.controller.chargeRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.chargeRecord.ChargeRecordDto;
import com.dahantc.erp.dto.chargeRecord.IncomeCheckOutDto;
import com.dahantc.erp.enums.CheckOutStatus;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 收支记录
 */
@Controller
@RequestMapping("/chargeRecord")
public class ChargeRecordAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ChargeRecordAction.class);

	@Autowired
	private IChargeRecordService chargeRecordService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	/**
	 * 跳转充值记录页面
	 * 
	 * @return
	 */
	@RequestMapping("/toChargeRecordSheet")
	public String toChargeRecordSheet() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return "";
		}
		try {
			request.setAttribute("pagePermission", roleService.getPagePermission(onlineUser.getRoleId()));
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "/views/chargeRecord/chargeRecordSheet";
	}

	/**
	 * 跳转充值核销页面
	 * 
	 * @param id
	 *            充值记录id
	 * @return
	 */
	@RequestMapping("/toChargeRecordCheckOut")
	public String toChargeRecordCheckOut(@RequestParam String id) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return "";
		}
		try {
			ChargeRecord chargeRecord = chargeRecordService.read(id);
			if (chargeRecord == null) {
				return "";
			}
			List<ChargeRecordDto> dto = buildDto(Arrays.asList(chargeRecord));
			if (CollectionUtils.isEmpty(dto)) {
				return "";
			}
			request.setAttribute("dto", dto.get(0));
		} catch (ServiceException e) {
			logger.error("", e);
			return "";
		}
		return "/views/chargeRecord/chargeRecordCheckOut";
	}

	/**
	 * 分页查询充值记录
	 * 
	 * @param limit
	 *            页大小
	 * @param page
	 *            当前页
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param companyName
	 *            公司名称
	 * @param checkOut
	 *            核销状态
	 * @param realName
	 *            发起人
	 * @return 分页
	 */
	@RequestMapping("/readPages")
	@ResponseBody
	public BaseResponse<PageResult<ChargeRecordDto>> readPages(@RequestParam int limit, @RequestParam int page,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) String companyName, @RequestParam(required = false) String checkOut,
			@RequestParam(required = false) String realName) {
		logger.info("分页查询充值记录开始，page：" + page + "，pageSize：" + limit);

		PageResult<ChargeRecordDto> result = new PageResult<>();
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}

		SearchFilter filter = new SearchFilter();
		// 数据权限查用户
		List<User> userList = userService.readUsers(onlineUser, null, realName, null);
		if (CollectionUtils.isEmpty(userList)) {
			logger.info("数据权限下的用户为空");
			return BaseResponse.success(result);
		} else {
			filter.getRules().add(new SearchRule("createrId", Constants.ROP_IN, userList.stream().map(User::getOssUserId).collect(Collectors.toList())));
		}

		// 按数据权限和公司名称查客户
		if (StringUtil.isNotBlank(companyName)) {
			List<Customer> customerList = customerService.readCustomers(onlineUser, null, null, null, null, companyName);
			if (CollectionUtils.isEmpty(customerList)) {
				logger.info("按公司名称搜索的结果为空");
				return BaseResponse.success(result);
			} else {
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList())));
			}
		}

		// 只查收支类型是 预购、预付 的记录
		List<Integer> typeList = Arrays.asList(IncomeExpenditureType.PREPURCHASE.getCode(), IncomeExpenditureType.ADVANCE.getCode());
		filter.getRules().add(new SearchRule("chargeType", Constants.ROP_IN, typeList));

		// 默认查本月，从月初到今天
		Date startTime = null;
		Date endTime = null;
		if (StringUtil.isNotBlank(startDate)) {
			startTime = DateUtil.convert1(startDate);
			if (StringUtil.isNotBlank(endDate)) {
				endTime = DateUtil.getTodayEndTime(endDate);
			}
		}
		if (null != startTime) {
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
		} else {
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getThisMonthFirst()));
		}
		if (null != endTime) {
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
		} else {
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getCurrentEndDateTime()));
		}
		// 核销状态
		if (StringUtil.isNotBlank(checkOut)) {
			List<String> checkOutList = Arrays.asList(checkOut.split(","));
			filter.getRules().add(new SearchRule("checkOut", Constants.ROP_IN, checkOutList.stream().map(Integer::parseInt).collect(Collectors.toList())));
		}

		try {
			PageResult<ChargeRecord> pageResult = chargeRecordService.queryByPages(limit, page, filter);
			if (pageResult != null) {
				result = new PageResult<>(buildDto(pageResult.getData()), pageResult.getCurrentPage(), pageResult.getTotalPages(), pageResult.getCount());
			}
		} catch (ServiceException e) {
			logger.error("分页查询充值记录异常", e);
			result.setData(new ArrayList<>());
		}
		return BaseResponse.success(result);
	}

	/**
	 * 封装对象
	 * 
	 * @param recordList
	 *            原始
	 * @return 封装后的
	 */
	private List<ChargeRecordDto> buildDto(List<ChargeRecord> recordList) {
		List<ChargeRecordDto> result = new ArrayList<>();
		if (CollectionUtils.isEmpty(recordList)) {
			return result;
		}
		// 发起人id -> 姓名
		Map<String, String> userNameMap = new HashMap<>();
		// 发起人部门id -> 部门名
		Map<String, String> deptNameMap = new HashMap<>();
		// 客户id -> 公司名
		Map<String, String> companyNameMap = new HashMap<>();
		for (ChargeRecord record : recordList) {
			ChargeRecordDto dto = new ChargeRecordDto(record);
			// 发起人
			if (StringUtil.isNotBlank(dto.getOssUserId())) {
				if (userNameMap.containsKey(dto.getOssUserId())) {
					dto.setRealName(userNameMap.get(dto.getOssUserId()));
				} else {
					String name = "未知";
					try {
						User user = userService.read(dto.getOssUserId());
						if (null != user) {
							name = user.getRealName();
						}
					} catch (ServiceException e) {
						logger.error("", e);
					}
					userNameMap.put(dto.getOssUserId(), name);
					dto.setRealName(name);
				}
			}
			// 部门
			if (StringUtil.isNotBlank(dto.getDeptId())) {
				if (deptNameMap.containsKey(dto.getDeptId())) {
					dto.setDeptName(deptNameMap.get(dto.getDeptId()));
				} else {
					String name = "未知";
					try {
						Department dept = departmentService.read(dto.getDeptId());
						if (null != dept) {
							name = dept.getDeptname();
						}
					} catch (ServiceException e) {
						logger.error("", e);
					}
					deptNameMap.put(dto.getDeptId(), name);
					dto.setDeptName(name);
				}
			}
			// 公司
			if (StringUtil.isNotBlank(dto.getEntityId())) {
				if (companyNameMap.containsKey(dto.getEntityId())) {
					dto.setCompanyName(companyNameMap.get(dto.getEntityId()));
				} else {
					String name = "未知";
					try {
						Customer customer = customerService.read(dto.getEntityId());
						if (null != customer) {
							name = customer.getCompanyName();
						}
					} catch (ServiceException e) {
						logger.error("", e);
					}
					companyNameMap.put(dto.getEntityId(), name);
					dto.setCompanyName(name);
				}
			}
			result.add(dto);
		}
		return result;
	}

	/**
	 * 充值记录与到款核销
	 * 
	 * @param incomeId
	 *            到款id
	 * @param chargeRecordId
	 *            充值记录id
	 * @return
	 */
	@RequestMapping("/checkOut")
	@ResponseBody
	public BaseResponse<String> checkOut(@RequestParam String incomeId, @RequestParam String chargeRecordId) {
		String result = "";
		FsExpenseIncome income = null;
		ChargeRecord charge = null;
		try {
			income = fsExpenseIncomeService.read(incomeId);
			charge = chargeRecordService.read(chargeRecordId);
			if (income == null) {
				logger.info("到款不存在，fsExpenseIncomeId：" + incomeId);
				return BaseResponse.error("到款不存在");
			}
			if (charge == null) {
				logger.info("充值记录不存在，chargeRecordId：" + chargeRecordId);
				return BaseResponse.error("充值记录不存在");
			}
			// 到款中关联的充值记录
			String incomeCheckOutInfo = income.getCheckOutInfo();
			JSONArray incomeCheckOutArray = StringUtil.isBlank(incomeCheckOutInfo) ? new JSONArray() : JSON.parseArray(incomeCheckOutInfo);
			BigDecimal incomeRemainCheckOut = income.getRemainCheckOut();

			// 充值记录中关联的到款
			String chargeCheckOutInfo = charge.getCheckOutInfo();
			JSONArray chargeCheckOutArray = StringUtil.isBlank(chargeCheckOutInfo) ? new JSONArray() : JSON.parseArray(chargeCheckOutInfo);
			BigDecimal chargeRemainCheckOut = charge.getRemainCheckOut();

			if (incomeRemainCheckOut.compareTo(chargeRemainCheckOut) == 0) {
				// 到款剩余可核销 == 充值记录剩余未核销，一起核销完
				income.setRemainCheckOut(BigDecimal.ZERO);
				income.setCheckOut(CheckOutStatus.CHECKED_OUT.ordinal());
				charge.setRemainCheckOut(BigDecimal.ZERO);
				charge.setCheckOut(CheckOutStatus.CHECKED_OUT.ordinal());

				// 到款关联充值记录
				JSONObject chargeInfo = new JSONObject();
				chargeInfo.put("chargeRecordId", charge.getChargerecordId());
				chargeInfo.put("thisCheckOut", incomeRemainCheckOut.toPlainString());
				incomeCheckOutArray.add(chargeInfo);
				income.setCheckOutInfo(incomeCheckOutArray.toJSONString());

				// 充值记录关联到款
				JSONObject incomeInfo = new JSONObject();
				incomeInfo.put("fsExpenseIncomeId", income.getId());
				incomeInfo.put("thisCheckOut", chargeRemainCheckOut.toPlainString());
				chargeCheckOutArray.add(incomeInfo);
				charge.setCheckOutInfo(chargeCheckOutArray.toJSONString());
			} else if (incomeRemainCheckOut.compareTo(chargeRemainCheckOut) > 0) {
				// 到款剩余可核销 > 充值记录剩余未核销，充值记录核销完，到款有剩余
				charge.setRemainCheckOut(BigDecimal.ZERO);
				charge.setCheckOut(CheckOutStatus.CHECKED_OUT.ordinal());
				income.setRemainCheckOut(income.getRemainCheckOut().subtract(chargeRemainCheckOut));
				income.setCheckOut(CheckOutStatus.PARTIAL_CHECKED.ordinal());

				// 到款关联充值记录，金额=充值记录剩余
				JSONObject chargeInfo = new JSONObject();
				chargeInfo.put("chargeRecordId", charge.getChargerecordId());
				chargeInfo.put("thisCheckOut", chargeRemainCheckOut.toPlainString());
				incomeCheckOutArray.add(chargeInfo);
				income.setCheckOutInfo(incomeCheckOutArray.toJSONString());

				// 充值记录关联到款，金额=充值记录剩余
				JSONObject incomeInfo = new JSONObject();
				incomeInfo.put("fsExpenseIncomeId", income.getId());
				incomeInfo.put("thisCheckOut", chargeRemainCheckOut.toPlainString());
				chargeCheckOutArray.add(incomeInfo);
				charge.setCheckOutInfo(chargeCheckOutArray.toJSONString());
			} else {
				// 到款剩余可核销 < 充值记录剩余未核销，到款核销完，充值记录有剩余
				income.setRemainCheckOut(BigDecimal.ZERO);
				income.setCheckOut(CheckOutStatus.CHECKED_OUT.ordinal());
				charge.setRemainCheckOut(charge.getRemainCheckOut().subtract(incomeRemainCheckOut));
				charge.setCheckOut(CheckOutStatus.PARTIAL_CHECKED.ordinal());

				// 到款关联充值记录，金额=到款剩余
				JSONObject chargeInfo = new JSONObject();
				chargeInfo.put("chargeRecordId", charge.getChargerecordId());
				chargeInfo.put("thisCheckOut", incomeRemainCheckOut.toPlainString());
				incomeCheckOutArray.add(chargeInfo);
				income.setCheckOutInfo(incomeCheckOutArray.toJSONString());

				// 充值记录关联到款，金额=到款剩余
				JSONObject incomeInfo = new JSONObject();
				incomeInfo.put("fsExpenseIncomeId", income.getId());
				incomeInfo.put("thisCheckOut", incomeRemainCheckOut.toPlainString());
				chargeCheckOutArray.add(incomeInfo);
				charge.setCheckOutInfo(chargeCheckOutArray.toJSONString());
			}
			result += "到款核销" + (fsExpenseIncomeService.update(income) ? "成功" : "失败");
			result += "，充值记录核销" + (chargeRecordService.update(charge) ? "成功" : "失败");
			return BaseResponse.success(result);
		} catch (ServiceException e) {
			logger.error("核销异常", e);
			return BaseResponse.error("核销异常，请联系管理员");
		}
	}

	/**
	 * 不管到款，强制核销充值记录
	 * 
	 * @param chargeRecordId
	 *            充值记录id
	 * @return
	 */
	@RequestMapping("/forceCheckOut")
	@ResponseBody
	public BaseResponse<String> forceCheckOut(@RequestParam String chargeRecordId) {
		String result = "";
		ChargeRecord charge = null;
		try {
			charge = chargeRecordService.read(chargeRecordId);
			if (charge == null) {
				logger.info("充值记录不存在，chargeRecordId：" + chargeRecordId);
				return BaseResponse.error("充值记录不存在");
			}

			// 充值记录中关联的到款
			String chargeCheckOutInfo = charge.getCheckOutInfo();
			JSONArray chargeCheckOutArray = StringUtil.isBlank(chargeCheckOutInfo) ? new JSONArray() : JSON.parseArray(chargeCheckOutInfo);
			BigDecimal chargeRemainCheckOut = charge.getRemainCheckOut();

			// 充值记录关联到款
			JSONObject incomeInfo = new JSONObject();
			incomeInfo.put("fsExpenseIncomeId", "");
			incomeInfo.put("thisCheckOut", chargeRemainCheckOut.toPlainString());
			User user = getOnlineUser();
			incomeInfo.put("remark", user.getRealName() + "强制核销");
			chargeCheckOutArray.add(incomeInfo);
			charge.setCheckOutInfo(chargeCheckOutArray.toJSONString());

			charge.setRemainCheckOut(BigDecimal.ZERO);
			charge.setCheckOut(CheckOutStatus.CHECKED_OUT.ordinal());

			result += "充值记录强制核销" + (chargeRecordService.update(charge) ? "成功" : "失败");
			return BaseResponse.success(result);
		} catch (ServiceException e) {
			logger.error("强制核销异常", e);
			return BaseResponse.error("强制核销异常，请联系管理员");
		}
	}

	/**
	 * 还原充值记录到未核销状态，还原到款到未核销状态
	 * 
	 * @param chargeRecordId
	 *            充值记录id
	 * @return
	 */
	@RequestMapping("/revertCheckOut")
	@ResponseBody
	public BaseResponse<String> revertCheckOut(@RequestParam String chargeRecordId) {
		String result = "";
		ChargeRecord charge = null;
		try {
			charge = chargeRecordService.read(chargeRecordId);
			if (charge == null) {
				logger.info("充值记录不存在，chargeRecordId：" + chargeRecordId);
				return BaseResponse.error("充值记录不存在");
			}

			// 充值记录中关联的到款
			String chargeCheckOutInfo = charge.getCheckOutInfo();
			JSONArray chargeCheckOutArray = StringUtil.isBlank(chargeCheckOutInfo) ? new JSONArray() : JSON.parseArray(chargeCheckOutInfo);

			Map<String, String> incomeCheckOutMap = new HashMap<>();
			for (Object obj : chargeCheckOutArray) {
				JSONObject incomeInfo = (JSONObject) obj;
				String incomeId = incomeInfo.getString("fsExpenseIncomeId");
				String thisCheckOutStr = incomeInfo.getString("thisCheckOut");
				// 强制核销的fsExpenseIncomeId为空
				if (StringUtil.isBlank(incomeId)) {
					continue;
				}
				incomeCheckOutMap.put(incomeId, thisCheckOutStr);
			}
			// 充值记录关联的到款
			if (!incomeCheckOutMap.isEmpty()) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<String>(incomeCheckOutMap.keySet())));
				List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(incomeList)) {
					// 遍历每笔到款，检查核销的每条充值记录
					for (FsExpenseIncome income : incomeList) {
						String incomeCheckOutInfo = income.getCheckOutInfo();
						JSONArray incomeCheckOutArray = StringUtil.isBlank(incomeCheckOutInfo) ? new JSONArray() : JSON.parseArray(incomeCheckOutInfo);
						Iterator<Object> iterator = incomeCheckOutArray.iterator();
						while (iterator.hasNext()) {
							JSONObject chargeInfo = (JSONObject) iterator.next();
							String chargeId = chargeInfo.getString("chargeRecordId");
							String thisCheckOutStr = chargeInfo.getString("thisCheckOut");
							// 如果是当前要还原的这条充值记录，还原用于核销的金额，并把这条关联信息移除
							if (chargeId.equals(chargeRecordId)) {
								income.setRemainCheckOut(new BigDecimal(thisCheckOutStr).add(income.getRemainCheckOut()));
								iterator.remove();
							}
						}
						if (income.getCost().compareTo(income.getRemainCheckOut()) == 0) {
							income.setCheckOut(CheckOutStatus.NO_CHECKED.ordinal());
						} else {
							income.setCheckOut(CheckOutStatus.PARTIAL_CHECKED.ordinal());
						}
						income.setCheckOutInfo(incomeCheckOutArray.toJSONString());
					}
					fsExpenseIncomeService.updateByBatch(incomeList);
				}
			} else {
				logger.info("充值记录无核销信息");
			}

			charge.setRemainCheckOut(charge.getChargePrice());
			charge.setCheckOut(CheckOutStatus.NO_CHECKED.ordinal());
			charge.setCheckOutInfo(null);

			result += "充值记录还原核销信息" + (chargeRecordService.update(charge) ? "成功" : "失败");
			return BaseResponse.success(result);
		} catch (ServiceException e) {
			logger.error("还原核销异常", e);
			return BaseResponse.error("还原核销异常，请联系管理员");
		}
	}

	/**
	 * 跳转核销详情
	 * @param id	充值记录id
	 * @return
	 */
	@RequestMapping("/toCheckOutDetail")
	public String toCheckOutDetail(@RequestParam String id) {
		request.setAttribute("id", id);
		return "/views/chargeRecord/checkOutDetail";
	}

	/**
	 * 获取核销用的到款信息
	 *
	 * @param id
	 *            充值记录id
	 * @return
	 */
	@RequestMapping("/getCheckOutDetail")
	@ResponseBody
	public BaseResponse<List<IncomeCheckOutDto>> getCheckOutDetail(@RequestParam String id) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		// 每一次核销记录封装
		List<IncomeCheckOutDto> incomeDtoList = new ArrayList<>();
		if (null == onlineUser) {
			logger.info("未登录");
			return BaseResponse.noLogin("请先登录");
		}
		try {
			ChargeRecord chargeRecord = chargeRecordService.read(id);
			if (chargeRecord == null) {
				logger.info("充值记录不存在：" + id);
				return BaseResponse.error("充值记录不存在");
			}

			// 发起人id -> 姓名
			Map<String, String> userNameMap = new HashMap<>();
			// 发起人部门id -> 部门名
			Map<String, String> deptNameMap = new HashMap<>();
			// 客户id -> 公司名
			Map<String, String> companyNameMap = new HashMap<>();

			Map<String, String> incomeInfoMap = new HashMap<>();
			// 核销记录
			String incomeInfoStr = chargeRecord.getCheckOutInfo();
			if (StringUtil.isNotBlank(incomeInfoStr)) {
				JSONArray incomeInfos = JSON.parseArray(incomeInfoStr);
				for (Object obj : incomeInfos) {
					JSONObject incomeInfo = (JSONObject) obj;
					String incomeId = incomeInfo.getString("fsExpenseIncomeId");
					String thisCheckOut = incomeInfo.getString("thisCheckOut");
					if (StringUtil.isNotBlank(incomeId)) {
						incomeInfoMap.put(incomeId, thisCheckOut);
						continue;
					}
					// 强制核销对应不上到款的
					String remark = incomeInfo.getString("remark");
					IncomeCheckOutDto noIncomeDto = new IncomeCheckOutDto();
					noIncomeDto.setThisCheckOut(thisCheckOut);
					noIncomeDto.setRemark(remark);
					noIncomeDto.setCheckOut(CheckOutStatus.CHECKED_OUT.ordinal());
					incomeDtoList.add(noIncomeDto);
				}
				// 找得到对应到款
				if (!incomeInfoMap.isEmpty()) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(incomeInfoMap.keySet())));
					List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
					if (!CollectionUtils.isEmpty(incomeList)) {
						Map<String, FsExpenseIncome> incomeMap = incomeList.stream().collect(Collectors.toMap(FsExpenseIncome::getId, v -> v));
						for (Map.Entry<String, String> incomeInfo : incomeInfoMap.entrySet()) {
							if (incomeMap.containsKey(incomeInfo.getKey())) {
								IncomeCheckOutDto incomeDto = new IncomeCheckOutDto(incomeMap.get(incomeInfo.getKey()));
								incomeDto.setThisCheckOut(incomeInfo.getValue());

								// 销售
								if (StringUtil.isNotBlank(incomeDto.getOssUserId())) {
									if (userNameMap.containsKey(incomeDto.getOssUserId())) {
										incomeDto.setRealName(userNameMap.get(incomeDto.getOssUserId()));
									} else {
										String name = "未知";
										try {
											User user = userService.read(incomeDto.getOssUserId());
											if (null != user) {
												name = user.getRealName();
											}
										} catch (ServiceException e) {
											logger.error("", e);
										}
										userNameMap.put(incomeDto.getOssUserId(), name);
										incomeDto.setRealName(name);
									}
								}
								// 公司
								if (StringUtil.isNotBlank(incomeDto.getCustomerId())) {
									if (companyNameMap.containsKey(incomeDto.getCustomerId())) {
										incomeDto.setCustomerName(companyNameMap.get(incomeDto.getCustomerId()));
									} else {
										String name = "未知";
										try {
											Customer customer = customerService.read(incomeDto.getCustomerId());
											if (null != customer) {
												name = customer.getCompanyName();
											}
										} catch (ServiceException e) {
											logger.error("", e);
										}
										companyNameMap.put(incomeDto.getCustomerId(), name);
										incomeDto.setCustomerName(name);
									}
								}

								incomeDtoList.add(incomeDto);
							}
						}
					}
				}
			}
			return BaseResponse.success(incomeDtoList);
		} catch (ServiceException e) {
			logger.error("", e);
			return BaseResponse.error("查询核销详情异常");
		}
	}
}
