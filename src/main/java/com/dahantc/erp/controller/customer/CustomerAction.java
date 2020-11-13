package com.dahantc.erp.controller.customer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dahantc.erp.dto.customer.CustomerAllDto;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.dahantc.erp.dto.customer.AddCustomerReqDto;
import com.dahantc.erp.dto.customer.CustomerDeptResp;
import com.dahantc.erp.dto.customer.CustomerDetailInfo;
import com.dahantc.erp.dto.customer.CustomerDetailRspDto;
import com.dahantc.erp.dto.customer.CustomerInfoDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerHistory.entity.CustomerHistory;
import com.dahantc.erp.vo.customerHistory.service.ICustomerHistoryService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.region.entity.Region;
import com.dahantc.erp.vo.region.service.IRegionService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/customer")
public class CustomerAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(CustomerAction.class);

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerHistoryService customerHistoryService;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IMsgCenterService msgCenterService;

	@Autowired
	private IMsgDetailService msgDetailService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRegionService regionService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private ICustomerProductService customerProductService;

	/**
	 * 加载客户添加页面
	 *
	 * @return 页面
	 */
	@RequestMapping("/toAddCustomer")
	public String toAddCustomer() {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, InvoiceType.SelfBank.ordinal()));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<BankAccount> list = bankAccountService.queryAllBySearchFilter(searchFilter);
			if (!CollectionUtils.isEmpty(list)) {
				JSONArray ownBankAccountInfo = new JSONArray();
				list.forEach(bankAccount -> {
					JSONObject bankAccountInfo = new JSONObject();
					bankAccountInfo.put("key", bankAccount.getAccountName() + "：" + bankAccount.getAccountBank());
					bankAccountInfo.put("value", bankAccount.getBankAccountId());
					ownBankAccountInfo.add(bankAccountInfo);
				});
				request.setAttribute("ownBankAccountInfo", ownBankAccountInfo);
			} else {
				logger.info("没有查询到我司银行信息请添加");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "/views/customer/addCustomer";
	}

	/**
	 * 获取客户类型下拉框的选项
	 * 
	 * @return 客户类型列表
	 */
	@ResponseBody
	@PostMapping("/getCustomerTypeSelect")
	public BaseResponse<List<CustomerType>> getCustomerTypeSelect() {
		try {
			// 查询客户类型
			List<CustomerType> customerTypes = new ArrayList<>();
			// 添加客户时，只能是意向客户，不可选择
			String type = request.getParameter("type");
			if (StringUtils.isBlank(type)) {
				CustomerType intentCustomerType = customerTypeService.getCustomerTypeByValue(CustomerTypeValue.INTENTION.getCode());
				if (null != intentCustomerType) {
					customerTypes.add(intentCustomerType);
				}
			} else {
				// 修改客户时，只能往下漏，合同value=1，测试2，……，公共池5
				CustomerType nowCustomerType = customerTypeService.read(type);
				if (null != nowCustomerType) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("customerTypeValue", Constants.ROP_GE, nowCustomerType.getCustomerTypeValue()));
					customerTypes = customerTypeService.queryAllBySearchFilter(filter);
					if (!CollectionUtils.isEmpty(customerTypes)) {
						customerTypes.sort((t1, t2) -> t1.getCustomerTypeValue() - t2.getCustomerTypeValue());
					}
				}
			}
			return BaseResponse.success(customerTypes);
		} catch (Exception e) {
			logger.error("查询客户类型时错误", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	/**
	 * 按照权限查找客户
	 * 
	 * @param customerName
	 * @param limit
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCustomerPage")
	public PageResult<CustomerInfoDto> queryCustomerPage(@RequestParam(required = false) String customerName, @RequestParam(required = false) int limit,
			@RequestParam(required = false) int page) {
		try {
			long _start = System.currentTimeMillis();
			SearchFilter searchFilter = new SearchFilter();
			List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
			if (CollectionUtils.isEmpty(deptIdList)) { // 查询自己的
				searchFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, getOnlineUser().getOssUserId()));
			} else {
				searchFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
			}
			if (StringUtils.isNotBlank(customerName)) {
				searchFilter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, customerName));
			}
			searchFilter.getOrders().add(new SearchOrder("customerTypeId", Constants.ROP_DESC));
			searchFilter.getOrders().add(new SearchOrder("ossuserId", Constants.ROP_DESC));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			PageResult<Customer> pResult = customerService.queryByPages(limit, page, searchFilter);
			// 获取所有客户类别
			Map<String, String> cacheCustomerTypeMap = customerTypeService.queryAllBySearchFilter(null).stream()
					.collect(Collectors.toMap(CustomerType::getCustomerTypeId, CustomerType::getCustomerTypeName, (v1, v2) -> v2));
			Map<String, String> cacheUserMap = userService.queryAllBySearchFilter(null).stream()
					.collect(Collectors.toMap(User::getOssUserId, User::getRealName, (v1, v2) -> v2));
			if (pResult != null && !CollectionUtils.isEmpty(pResult.getData())) {
				List<CustomerInfoDto> dataList = pResult.getData().stream().map(customer -> new CustomerInfoDto(customer,
						cacheCustomerTypeMap.get(customer.getCustomerTypeId()), cacheUserMap.get(customer.getOssuserId()))).collect(Collectors.toList());
				PageResult<CustomerInfoDto> result = new PageResult<>();
				result.setCode(pResult.getCode());
				result.setCount(pResult.getCount());
				result.setCurrentPage(pResult.getCurrentPage());
				result.setData(dataList);
				result.setMsg(pResult.getMsg());
				result.setTotalPages(pResult.getTotalPages());
				return result;
			}
			logger.info("分页查询客户，耗时【" + (System.currentTimeMillis() - _start) + "】毫秒");
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return new PageResult<>();
	}

	/**
	 * 获取客户分类信息（仅仅用于加载最外层的分类信息）
	 *
	 * @param deptIds
	 *            查询的部门id（查询条件 影响流程数量）
	 * @param customerId
	 *            公司Id
	 * @return 客户分类信息
	 */
	@ResponseBody
	@PostMapping("/getCustomerType")
	public BaseResponse<List<CustomerTypeRespDto>> getCustomerType(@RequestParam(value = "deptIds", required = false) String deptIds,
			@RequestParam(value = "userIds", required = false) String userIds, @RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "customerKeyWord", required = false) String customerKeyWord) {
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			// 查询客户类型
			List<CustomerTypeRespDto> customerTypeResps = customerService.getCustomerType(getOnlineUserAndOnther(), deptIds, customerId, customerKeyWord,
					userIds);
			return BaseResponse.success(customerTypeResps);
		} catch (Exception e) {
			logger.error("查询客户类型时错误", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	/**
	 * 获取客户和未处理流程统计信息
	 *
	 * @param customerTypeId
	 *            客户类型id(展开条件)
	 * @param deptId
	 *            部门名称（展开条件）
	 * @param searchDeptIds
	 *            部门id（查询条件）
	 * @param customerId
	 *            客户id（查询条件）
	 * @return 客户|部门信息
	 */
	@ResponseBody
	@PostMapping("/getCustomers")
	public BaseResponse<List<CustomerDeptResp>> getDeptInfoAndCount(
			@RequestParam(value = "customerTypeId", required = false) String customerTypeId,
			@RequestParam(value = "searchDeptIds", required = false) String searchDeptIds,
			@RequestParam(value = "searchUserIds", required = false) String searchUserIds,
			@RequestParam(value = "deptId", required = false) String deptId,
			@RequestParam(value = "ossUserId", required = false) String ossUserId,
			@RequestParam(value = "searchCustomerId", required = false) String customerId,
			@RequestParam(value = "customerKeyWord", required = false) String customerKeyWord) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("未登录，查询失败");
			}
			String roleId = onlineUser.getRoleId();
			Role role = roleService.read(roleId);
			if (role == null) {
				return BaseResponse.error("未知用户角色权限");
			}
			User user = onlineUser.getUser();
			List<CustomerDeptResp> customerDeptDtos = customerService.queryCustomerAndDept(role, user.getOssUserId(), user.getDeptId(), customerTypeId, deptId,
					ossUserId, searchDeptIds, customerId, customerKeyWord, searchUserIds);
			boolean nolyShowBasic = isSale() && customerTypeService.validatePublicType(customerTypeId);
			if (customerDeptDtos != null && !customerDeptDtos.isEmpty()) {
				customerDeptDtos = customerDeptDtos.stream().map(dto -> {
					dto.setOnlyShowBasic(nolyShowBasic);
					return dto;
				}).sorted(Comparator.comparing(CustomerDeptResp::getFlowCount).reversed()).collect(Collectors.toList());
			}
			return BaseResponse.success(customerDeptDtos);
		} catch (Exception e) {
			logger.error("查询客户类型时错误", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	/**
	 * 添加客户信息
	 *
	 * @param addCustomerReqDto
	 *            客户信息参数
	 * @return 添加结果
	 */
	@PostMapping("/addCustomerInfo")
	@ResponseBody
	public BaseResponse<String> addCustomerInfo(@Valid AddCustomerReqDto addCustomerReqDto) {
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			// 按公司名称判断客户是否已存在
			if (existsCustomer(addCustomerReqDto.getCompanyName(), addCustomerReqDto.getCustomerId())) {
				return BaseResponse.error("该客户名称已存在");
			}
			Customer customer = new Customer();
			addCustomerReqDto.toCustomer(customer);
			customer.setOssuserId(user.getOssUserId());
			customer.setDeptId(user.getDeptId());
			List<InvoiceInformation> invoiceInfos = null;
			if (StringUtil.isNotBlank(addCustomerReqDto.getInvoiceInfos())) {
				invoiceInfos = JSONArray.parseArray(StringEscapeUtils.unescapeHtml4(addCustomerReqDto.getInvoiceInfos()), InvoiceInformation.class);
			}
			List<BankAccount> bankInfos = null;
			if (StringUtil.isNotBlank(addCustomerReqDto.getBankInfos())) {
				bankInfos = JSON.parseArray(StringEscapeUtils.unescapeHtml4(addCustomerReqDto.getBankInfos()), BankAccount.class);
			}
			customerService.save(customer, invoiceInfos, bankInfos);
			buildMessageCenterEnt(user, customer);
			return BaseResponse.success("增加客户成功");
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("增加客户异常");
		}
	}

	/**
	 * 创建一条消息中心记录，并提醒给相关的人
	 *
	 * @param user
	 *            当前用户
	 * @param customer
	 *            创建的客户
	 * @throws ServiceException
	 */
	private void buildMessageCenterEnt(User user, Customer customer) throws ServiceException {
		String msgDetail = user.getRealName() + " 新增客户：" + customer.getCompanyName();
		if (StringUtil.isNotBlank(customer.getLegalPerson())) {
			msgDetail = msgDetail + ", " + "法人：" + customer.getLegalPerson();
		}
		if (StringUtil.isNotBlank(customer.getRegistrationNumber())) {
			msgDetail = msgDetail + ", " + " 营业执照号：" + customer.getRegistrationNumber();
		}
		if (StringUtil.isNotBlank(customer.getRegistrationAddress())) {
			msgDetail = msgDetail + ", " + "注册地：" + customer.getRegistrationAddress();
		}
		if (StringUtil.isNotBlank(customer.getPostalAddress())) {
			msgDetail = msgDetail + ", " + "地址：" + customer.getPostalAddress();
		}
		if (StringUtil.isNotBlank(customer.getTelePhoneNumber())) {
			msgDetail = msgDetail + ", " + "电话：" + customer.getTelePhoneNumber();
		}
		if (StringUtil.isNotBlank(customer.getEmail())) {
			msgDetail = msgDetail + ", " + "邮件：" + customer.getEmail();
		}
		if (StringUtil.isNotBlank(customer.getWebsite())) {
			msgDetail = msgDetail + ", " + "网站：" + customer.getWebsite();
		}
		if (StringUtil.isNotBlank(customer.getContactName())) {
			msgDetail = msgDetail + ", " + "联系人：" + customer.getContactName();
		}
		if (StringUtil.isNotBlank(customer.getContactPhone())) {
			msgDetail = msgDetail + ", " + "手机：" + customer.getContactPhone();
		}
		if (customer.getCreationDate() != null) {
			msgDetail = msgDetail + ", " + "创立日期：" + customer.getCreationDate();
		}
		if (StringUtil.isNotBlank(customer.getRegisteredCapital())) {
			msgDetail = msgDetail + ", " + "注册资本：" + customer.getRegisteredCapital();
		}
		if (StringUtil.isNotBlank(customer.getCorporateNature())) {
			msgDetail = msgDetail + ", " + "性质：" + customer.getCorporateNature();
		}
		if (StringUtil.isNotBlank(customer.getContactPhone())) {
			msgDetail = msgDetail + ", " + "税务号：" + customer.getContactPhone();
		}
		if (customer.getWtime() != null) {
			msgDetail = msgDetail + ", " + "记录创建时间：" + customer.getWtime();
		}
		if (customer.getUseDate() != null) {
			msgDetail = msgDetail + ", " + "开始使用日期：" + customer.getUseDate();
		}
		if (StringUtil.isNotBlank(customer.getCompanyIntroduction())) {
			msgDetail = msgDetail + ", " + "公司介绍：" + customer.getCompanyIntroduction();
		}
		if (StringUtil.isNotBlank(customer.getBusinessMode())) {
			msgDetail = msgDetail + ", " + "业务应用模式：" + customer.getBusinessMode();
		}
		MsgCenter msgCenter = new MsgCenter();
		msgCenter.setInfotype(MsgCenter.ADD_CUSTOMER);
		msgCenter.setOssUserId(user.getOssUserId());
		msgCenter.setWtime(new Date());
		msgCenter.setMessagesourceid(customer.getCustomerId());
		msgCenter.setMessagedetail(msgDetail);
		CustomerType customerType = customerTypeService.read(customer.getCustomerTypeId());
		if (customerType != null) {
			msgCenter.setCustomerType(customerType.getCustomerTypeValue());
		}
		msgCenterService.save(msgCenter);

		String deptId = user.getDeptId();
		SearchFilter roleFilter = new SearchFilter();
		List<Role> roles = roleService.queryAllBySearchFilter(roleFilter);
		List<String> userIds = new ArrayList<>();
		Department dept = departmentService.read(deptId);
		for (Role role : roles) {
			if (role.getDataPermission() == DataPermission.Dept.ordinal()) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, role.getRoleid()));
				List<RoleRelation> roleRelations = roleRelationService.queryAllBySearchFilter(filter);
				for (RoleRelation roleRelation : roleRelations) {
					SearchFilter userFilter = new SearchFilter();
					userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, roleRelation.getOssUserId()));
					userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 1));
					userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, 1));
					List<User> userList = userService.queryAllBySearchFilter(userFilter);
					for (User user2 : userList) {
						if (user2.getDeptId().equals(dept.getDeptid()) || user2.getDeptId().equals(dept.getParentid())) {
							userIds.add(user2.getOssUserId());
						}
					}
				}
			}
			if (role.getDataPermission() == DataPermission.All.ordinal()) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, role.getRoleid()));
				List<RoleRelation> roleRelations = roleRelationService.queryAllBySearchFilter(filter);
				for (RoleRelation roleRelation : roleRelations) {
					SearchFilter userFilter = new SearchFilter();
					userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, roleRelation.getOssUserId()));
					userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 1));
					userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, 1));
					List<User> userList = userService.queryAllBySearchFilter(userFilter);
					for (User user2 : userList) {
						userIds.add(user2.getOssUserId());
					}
				}
			}
			if (role.getDataPermission() == DataPermission.Customize.ordinal()) {
				if (StringUtil.isContains(role.getDeptIds(), dept.getDeptid())) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, role.getRoleid()));
					List<RoleRelation> roleRelations = roleRelationService.queryAllBySearchFilter(filter);
					for (RoleRelation roleRelation : roleRelations) {
						SearchFilter userFilter = new SearchFilter();
						userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, roleRelation.getOssUserId()));
						userFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 1));
						userFilter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, 1));
						List<User> userList = userService.queryAllBySearchFilter(userFilter);
						for (User user2 : userList) {
							userIds.add(user2.getOssUserId());
						}
					}
				}
			}
		}
		if (!userIds.contains(user.getOssUserId())) {
			userIds.add(user.getOssUserId());
		}
		Set<String> set = new HashSet<String>();
		List<String> listNew = new ArrayList<String>();
		set.addAll(userIds);
		listNew.addAll(set);

		List<MsgDetail> msgDetails = new ArrayList<MsgDetail>();
		for (String userId : listNew) {
			MsgDetail msgDetailEntity = new MsgDetail();
			msgDetailEntity.setUserid(userId);
			msgDetailEntity.setState(MsgDetail.NOT_READ);
			msgDetailEntity.setWtime(new Date());
			msgDetailEntity.setMessageid(msgCenter.getMessageid());
			msgDetails.add(msgDetailEntity);
		}
		msgDetailService.saveByBatch(msgDetails);
	}

	/**
	 * 跳转修改客户信息页面
	 *
	 * @param customerId
	 *            客户id
	 * @return 修改页面HTML
	 */
	@RequestMapping("/toEditCustomer/{customerId}")
	public String toEditCustomer(@PathVariable String customerId) {
		Customer customer = null;
		if (StringUtils.isNotBlank(customerId)) {
			try {
				customer = customerService.read(customerId);
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, customerId));
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
				filter.getOrders().add(new SearchOrder("companyName", Constants.ROP_ASC));
				List<InvoiceInformation> invoiceInfos = invoiceInformationService.queryAllBySearchFilter(filter);
				filter.getOrders().clear();
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
				filter.getOrders().add(new SearchOrder("bankAccount", Constants.ROP_ASC));
				List<BankAccount> bankInfos = bankAccountService.queryAllBySearchFilter(filter);
				if (invoiceInfos.isEmpty()) {
					invoiceInfos.add(new InvoiceInformation());
				}
				if (bankInfos.isEmpty()) {
					bankInfos.add(new BankAccount());
				}
				request.setAttribute("invoiceInfos", invoiceInfos);
				request.setAttribute("bankInfos", bankInfos);

				// 我方银行信息
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, InvoiceType.SelfBank.ordinal()));
				searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
				List<BankAccount> list = bankAccountService.queryAllBySearchFilter(searchFilter);
				if (!CollectionUtils.isEmpty(list)) {
					JSONArray ownBankAccountInfo = new JSONArray();
					list.forEach(bankAccount -> {
						JSONObject bankAccountInfo = new JSONObject();
						bankAccountInfo.put("key", bankAccount.getAccountName() + "：" + bankAccount.getAccountBank());
						bankAccountInfo.put("value", bankAccount.getBankAccountId());
						ownBankAccountInfo.add(bankAccountInfo);
					});
					request.setAttribute("ownBankAccountInfo", ownBankAccountInfo);
				} else {
					logger.info("没有查询到我司银行信息请添加");
				}
			} catch (ServiceException e) {
				logger.info("跳转修改客户信息异常", e);
			}
		}
		request.setAttribute("customer", customer);
		request.setAttribute("operationType", request.getParameter("operationType"));
		return "/views/customer/editCustomer";
	}

	/**
	 * 修改客户信息
	 *
	 * @param customerReqDto
	 *            修改参数
	 * @return 修改消息
	 */
	@PostMapping("/editCustomer")
	@ResponseBody
	public BaseResponse<String> editCustomer(@Valid AddCustomerReqDto customerReqDto) {
		try {
			long start = System.currentTimeMillis();
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			if (StringUtil.isBlank(customerReqDto.getCustomerId())) {
				return BaseResponse.error("客户id为空");
			}
			// 按公司名称判断客户是否已存在
			if (existsCustomer(customerReqDto.getCompanyName(), customerReqDto.getCustomerId())) {
				return BaseResponse.error("该客户名称已存在");
			}
			logger.info("修改客户信息信息开始");
			CustomerHistory customerHistory = new CustomerHistory();
			Customer customer = customerService.read(customerReqDto.getCustomerId());
			// 修改前的客户类型
			CustomerType nowCustomerType = customerTypeService.read(customer.getCustomerTypeId());
			BeanUtils.copyProperties(customer, customerHistory);
			customerHistory.setWtime(DateUtil.convert(new Date(), DateUtil.format2));
			customerHistoryService.save(customerHistory);
			logger.info("保存客户信息到历史表成功，耗时：" + (System.currentTimeMillis() - start));
			customerReqDto.toCustomer(customer);
			// 修改后的客户类型
			CustomerType newCustomerType = customerTypeService.read(customer.getCustomerTypeId());
			List<InvoiceInformation> invoiceInfos = null;
			if (StringUtil.isNotBlank(customerReqDto.getInvoiceInfos())) {
				invoiceInfos = JSONArray.parseArray(StringEscapeUtils.unescapeHtml4(customerReqDto.getInvoiceInfos()), InvoiceInformation.class);
			}
			List<BankAccount> bankInfos = null;
			if (StringUtil.isNotBlank(customerReqDto.getBankInfos())) {
				bankInfos = JSON.parseArray(StringEscapeUtils.unescapeHtml4(customerReqDto.getBankInfos()), BankAccount.class);
			}
			boolean result = customerService.update(customer, invoiceInfos, bankInfos, customerReqDto.getDelInvoiceIds(), customerReqDto.getDelBankIds());
			logger.info("修改客户信息成功，耗时：" + (System.currentTimeMillis() - start));
			if (result && !StringUtils.equals(nowCustomerType.getCustomerTypeId(), newCustomerType.getCustomerTypeId())) {
				String depict = "客户：" + customer.getCompanyName() + "，被：" + user.getRealName() + " 由" + nowCustomerType.getCustomerTypeName() + "变更为"
						+ newCustomerType.getCustomerTypeName();
				// 变更为公共池客户要清掉客户的销售id和部门id
				customerService.changeGrade(customer, user, nowCustomerType, newCustomerType, depict,
						newCustomerType.getCustomerTypeValue() == CustomerTypeValue.PUBLIC.getCode());
			}
			return BaseResponse.success("修改客户信息成功");
		} catch (Exception e) {
			logger.error("修改客户信息失败", e);
			return BaseResponse.error("修改客户信息失败");
		}
	}

	/**
	 * 判断是否存在客户
	 *
	 * @param companyName
	 *            公司名称
	 * @param customerId
	 *            客户id
	 * @return Boolean
	 */
	private boolean existsCustomer(String companyName, String customerId) {
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("companyName", Constants.ROP_EQ, companyName));
		if (StringUtil.isNotBlank(customerId)) {
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_NE, customerId));
		}
		try {
			int count = customerService.getCount(filter);
			if (count == 0) {
				return false;
			}
		} catch (ServiceException e) {
			logger.error("判断是否已经存在客户时出现异常：", e);
		}
		return true;
	}

	/**
	 * 获取客户详情信息
	 *
	 * @param customerId
	 *            客户id
	 * @return 详情页面
	 */
	@GetMapping("/readCustomerInfoById/{customerId}")
	public String readCustomerInfoById(@PathVariable String customerId) {
		CustomerDetailRspDto resp = null;
		Customer customer = null;
		try {
			customer = customerService.read(customerId);
			resp = new CustomerDetailRspDto(customer);
			User user1 = userService.read(customer.getOssuserId());
			if (user1 != null) {
				resp.setCreatUser(user1.getRealName());
			}
		} catch (ServiceException e) {
			// 防止模板页面空指针异常
			resp = new CustomerDetailRspDto();
			logger.error("根据客户id[" + customerId + "]获取客户详情时出现异常", e);
		}

		request.setAttribute("customer", resp);
		return "/views/customer/customerInfoDetail";
	}

	/**
	 * 根据公司名模糊查询公司信息
	 *
	 * @param companyName
	 *            公司名
	 * @param pageSize
	 *            分页大小
	 * @param currentPage
	 *            当前页
	 * @return 客户基本信息
	 */
	@ResponseBody
	@PostMapping("/queryCustomerByName")
	public BaseResponse<PageResult<CustomerInfoDto>> queryCustomerByName(@RequestParam(value = "companyName") String companyName,
			@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "currentPage") Integer currentPage) {
		// 校验用户
		User user = getOnlineUser();
		if (user == null) {
			return BaseResponse.error("请先登录");
		}
		if (StringUtil.isBlank(companyName)) {
			return BaseResponse.error("公司名为空");
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		try {
			PageResult<Customer> customerPage = customerService.queryByPages(pageSize, currentPage, filter);
			if (customerPage != null && customerPage.getCode() == 0 && customerPage.getData() != null) {
				List<CustomerInfoDto> customerInfoDtos = customerPage.getData().stream().map(customer -> {
					try {
						// 获取对应用户类型
						CustomerType customerType = customerTypeService.read(customer.getCustomerTypeId());
						String customerTypeName = "未知";
						if (customerType != null) {
							customerTypeName = customerType.getCustomerTypeName();
						}
						// 获取创建人姓名
						User userInfo = userService.read(customer.getOssuserId());
						String userName = "未知";
						if (userInfo != null) {
							userName = userInfo.getRealName();
						}
						return new CustomerInfoDto(customer, customerTypeName, userName);
					} catch (ServiceException e) {
						logger.error("查询客户对应信息出现异常", e);
					}
					return null;
				}).filter(Objects::nonNull).collect(Collectors.toList());
				currentPage = customerPage.getCurrentPage();
				Integer totalPage = customerPage.getTotalPages();
				Long totalRecord = customerPage.getCount();
				return BaseResponse.success("查询成功", new PageResult<>(customerInfoDtos, currentPage, totalPage, totalRecord));
			}
		} catch (ServiceException e) {
			logger.error("查询客户信息异常", e);
		}
		return BaseResponse.success("暂无数据");
	}

	@RequestMapping("/toMatchCustomer/{companyName}")
	public String toMatchCustomer(@PathVariable(value = "companyName") String companyName) {
		request.setAttribute("companyName", companyName);
		return "/views/customer/matchCustomer";
	}

	/**
	 * 获取流程类别下拉框
	 */
	@RequestMapping("/getRegion")
	@ResponseBody
	public String getRegion() {
		long _start = System.currentTimeMillis();
		JSONArray classes = new JSONArray();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getOrRules().add(new SearchRule[] { new SearchRule("ilevel", Constants.ROP_EQ, 2), new SearchRule("ilevel", Constants.ROP_EQ, 1) });
			List<Region> list = regionService.queryAllBySearchFilter(filter);
			for (Region region : list) {
				JSONObject json = new JSONObject();
				json.put("value", region.getId());
				json.put("name", region.getRegionName());
				classes.add(json);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("获取归属地下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return classes.toJSONString();
	}

	// 跳转转移客户页面
	@RequestMapping("/toTransferCustomer")
	public String toTransferCustomer() {
		return "/views/transferCustomer/transferCustomer";
	}

	// 根据销售和公司名查询客户
	@RequestMapping("/queryCustomer")
	@ResponseBody
	public String queryCustomer() {
		JSONObject res = new JSONObject();
		JSONArray list = new JSONArray();
		String userId = request.getParameter("userId");
		String companyName = request.getParameter("companyName");
		if (StringUtils.isAllBlank(userId, companyName)) {
			res.put("result", "error");
			return res.toJSONString();
		}
		try {
			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(userId)) {
				filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, userId));
			}
			if (StringUtils.isNotBlank(companyName)) {
				filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
			}
			List<Customer> customers = customerService.queryAllBySearchFilter(filter);
			Map<String, String> userNames = new HashMap<String, String>();
			Map<String, String> deptNames = new HashMap<String, String>();
			if (!ListUtils.isEmpty(customers)) {
				for (Customer customer : customers) {
					JSONObject obj = new JSONObject();
					obj.put("id", customer.getCustomerId());
					obj.put("name", customer.getCompanyName());
					if (userNames.containsKey(customer.getOssuserId())) {
						obj.put("userName", userNames.get(customer.getOssuserId()));
					} else {
						User user = userService.read(customer.getOssuserId());
						if (user != null) {
							obj.put("userName", user.getRealName());
							userNames.put(user.getOssUserId(), user.getRealName());
						}
					}
					if (deptNames.containsKey(customer.getDeptId())) {
						obj.put("deptName", deptNames.get(customer.getDeptId()));
					} else {
						Department department = departmentService.read(customer.getDeptId());
						if (department != null) {
							obj.put("deptName", department.getDeptname());
							deptNames.put(department.getDeptid(), department.getDeptname());
						}
					}
					list.add(obj);
				}
				res.put("result", "success");
				res.put("data", list);
			} else {
				res.put("result", "error");
			}
		} catch (ServiceException e) {
			res.put("result", "error");
			logger.info("查询客户异常：", e);
		}
		return res.toJSONString();
	}

	// 根据销售和公司名查询客户
	@RequestMapping("/queryCustomerInfos")
	@ResponseBody
	public BaseResponse<List<CustomerDetailInfo>> queryCustomerInfos(String userId, String companyName) {
		if (StringUtils.isAllBlank(userId, companyName)) {
			return BaseResponse.error("销售和公司不能都为空");
		}
		try {
			List<Customer> customerList = customerService.readCustomers(getOnlineUserAndOnther(), null, null, null, null, companyName);
			if (customerList == null || customerList.isEmpty()) {
				return BaseResponse.success();
			}
			// 用户信息（销售ID）
			Set<String> userIds = customerList.stream().map(Customer::getOssuserId).collect(Collectors.toSet());
			Map<String, String> userNames = new HashMap<>();
			if (!userIds.isEmpty()) {
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, new ArrayList<>(userIds)));
				List<User> userList = userService.queryAllBySearchFilter(searchFilter);
				if (userList != null && !userList.isEmpty()) {
					userNames.putAll(userList.stream().collect(Collectors.toMap(User::getOssUserId, User::getRealName)));
				}
			}
			// 部门
			Set<String> deptIds = customerList.stream().map(Customer::getDeptId).collect(Collectors.toSet());
			Map<String, String> deptNames = new HashMap<>();
			if (!deptIds.isEmpty()) {
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, new ArrayList<>(deptIds)));
				List<Department> departmentList = departmentService.queryAllBySearchFilter(searchFilter);
				if (departmentList != null && !departmentList.isEmpty()) {
					deptNames.putAll(departmentList.stream().collect(Collectors.toMap(Department::getDeptid, Department::getDeptname)));
				}
			}
			List<CustomerDetailInfo> detailInfoList = new ArrayList<>();
			customerList.forEach(customer -> {
				CustomerDetailInfo customerDetailInfo = new CustomerDetailInfo();
				customerDetailInfo.setId(customer.getCustomerId());
				customerDetailInfo.setName(customer.getCompanyName());
				customerDetailInfo.setUserName(userNames.get(customer.getOssuserId()));
				customerDetailInfo.setDeptName(deptNames.get(customer.getDeptId()));
				if (StringUtil.isBlank(userId) || userId.equals(customer.getOssuserId())) {
					detailInfoList.add(customerDetailInfo);
				}
			});
			return BaseResponse.success(detailInfoList);
		} catch (ServiceException e) {
			logger.info("查询客户异常：", e);
		}
		return BaseResponse.error("查询异常");
	}

	// 转移客户到其他销售
	@RequestMapping("/transferCustomer")
	@ResponseBody
	public BaseResponse<String> transferCustomer() {
		long _start = System.currentTimeMillis();
		User user = getOnlineUser();
		if (user == null) {
			return BaseResponse.error("请先登录");
		}
		String targetUserId = request.getParameter("targetUserId");
		String customerIds = request.getParameter("customerIds");
		logger.info(user.getLoginName() + "[" + user.getRealName() + "]" + "转移客户" + customerIds + "到销售id:" + targetUserId + "开始");
		try {
			if (StringUtils.isBlank(targetUserId)) {
				return BaseResponse.error("请选择目标销售");
			}
			User targetUser = userService.read(targetUserId);
			if (targetUser == null) {
				return BaseResponse.error("目标销售不存在");
			}
			logger.info("更新客户的销售开始");
			_start = System.currentTimeMillis();
			List<String> customerIdList = Arrays.asList(customerIds.split(","));
			// 查找选择的客户
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
			List<Customer> customers = customerService.queryAllBySearchFilter(filter);
			if (ListUtils.isEmpty(customers)) {
				return BaseResponse.error("目标客户不存在");
			}
			// 更新客户的销售
			logger.info("查询到待更新客户数：" + customers.size());
			for (Customer customer : customers) {
				CustomerHistory customerHistory = new CustomerHistory();
				BeanUtils.copyProperties(customer, customerHistory);
				customerHistoryService.save(customerHistory);
				customer.setOssuserId(targetUserId);
				customer.setDeptId(targetUser.getDeptId());
				customerService.update(customer, null, null, null, null);
			}
			logger.info("更新客户的销售结束，耗时：" + (System.currentTimeMillis() - _start));
			// 更新客户的产品的销售
			logger.info("更新客户产品的销售开始");
			_start = System.currentTimeMillis();
			filter = new SearchFilter();
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
			List<CustomerProduct> products = customerProductService.queryAllByFilter(filter);
			if (!ListUtils.isEmpty(products)) {
				logger.info("查询到待更新客户产品数：" + products.size());
				for (CustomerProduct product : products) {
					product.setOssUserId(targetUserId);
				}
				customerProductService.updateByBatch(products);
				logger.info("更新客户产品的销售结束，耗时：" + (System.currentTimeMillis() - _start));
			} else {
				logger.info("要转移的客户没有产品");
			}
			// 更新客户的流程
			logger.info("更新客户的流程开始");
			_start = System.currentTimeMillis();
			filter = new SearchFilter();
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, customerIdList));
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
			List<FlowEnt> flowEnts = flowEntService.queryAllBySearchFilter(filter);
			if (!ListUtils.isEmpty(flowEnts)) {
				logger.info("查询到" + flowEnts.size() + "条流程信息");
				String UPDATE_FLOWENT_SQL = "update erp_flow_ent set ossuserid = ? where id = ?";
				for (FlowEnt flowEnt : flowEnts) {
					// 生成流程处理日志记录
					FlowLog flowLog = new FlowLog();
					flowLog.setFlowId(flowEnt.getFlowId());
					flowLog.setFlowEntId(flowEnt.getId());
					flowLog.setAuditResult(AuditResult.SAVE.getCode());
					flowLog.setOssUserId(user.getOssUserId());
					flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
					flowLog.setRemark("客户从原销售员转移");
					flowLog.setNodeId(flowEnt.getNodeId());
					flowLog.setFlowMsg(flowEnt.getFlowMsg());
					flowLogService.save(flowLog);

					baseDao.executeSqlUpdte(UPDATE_FLOWENT_SQL, new Object[] { targetUserId, flowEnt.getId() },
							new Type[] { StandardBasicTypes.STRING, StandardBasicTypes.STRING });
				}
			}
			logger.info("更新客户的流程结束，耗时：" + (System.currentTimeMillis() - _start));
			logger.info(user.getLoginName() + "[" + user.getRealName() + "]" + "转移客户" + customerIdList + "到销售:" + targetUser.getLoginName() + "["
					+ targetUser.getRealName() + "]操作成功");
		} catch (Exception e) {
			logger.info(user.getLoginName() + "[" + user.getRealName() + "]" + "转移客户异常", e);
			return BaseResponse.error("转移客户异常");
		}
		return BaseResponse.success("转移客户成功");
	}

	// 跳转关键词搜索客户页面
	@RequestMapping("/toCustomerFilter")
	public String toCustomerFilter() {
		try {
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("deptIds", request.getParameter("deptIds"));
			request.setAttribute("userIds", request.getParameter("userIds"));
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
		} catch (Exception e) {
			logger.info("获取跳转客户过滤页面异常");
		}
		return "/views/customer/customerFilter";
	}

	// 获取系统所有客户
	/**
	 * 获取系统所有可用用户
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryAllCustomer")
	@ResponseBody
	public BaseResponse<List<Customer>> queryAllCustomer() {
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.error("未登录");
		}
		SearchFilter filter = new SearchFilter();
		try {
			List<Customer> customers = customerService.queryAllBySearchFilter(filter);
			return BaseResponse.success(customers);
		} catch (ServiceException e) {
			logger.error("查询用户信息异常：", e);
			return BaseResponse.error("查询用户信息异常");
		}
	}


	/**
	 * 分页加载用户的客户信息
	 *
	 * @param companyName 公司名
	 * @param pageSize    页大小
	 * @param currentPage 当前页
	 * @param onlyPublic  只查询公共池客户
	 * @param noPublic    不展示公共客户
	 * @return 分页的客户信息
	 */
	@ResponseBody
	@RequestMapping(path = "/queryUserCustomerInfo")
	public PageResult<CustomerRespDto> queryUserCustomerInfo(String companyName, Integer onlyPublic,Integer noPublic,
															 Integer pageSize, Integer currentPage){
		OnlineUser user = getOnlineUserAndOnther();
		if (user == null) {
			return PageResult.empty("请先登录");
		}
		if (pageSize == null || pageSize <= 0) {
			return PageResult.empty("请求参数错误");
		}
		if (currentPage == null || currentPage < 0) {
			return PageResult.empty("请求参数错误");
		}
		boolean forPublic = false;
		if (onlyPublic != null && onlyPublic > 0) {
			// 只查询公共池客户
			forPublic = true;
		}
		//禁止展示公共客户
		boolean banPublic = false;
		if (noPublic != null && noPublic > 0) {
			// 只查询公共池客户
			banPublic = true;
		}
		if (forPublic && banPublic){
			return PageResult.empty("条件冲突");
		}
		return customerService.queryUserCustomer(user, companyName, forPublic, banPublic, pageSize, currentPage);
	}

	/**
	 * 获取客户详情信息
	 *
	 * @return 详情页面
	 */
	@ResponseBody
	@PostMapping(value = "/queryCustomerById")
	public BaseResponse<CustomerAllDto> queryCustomerById(String customerId) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null) {
			return BaseResponse.error("请先登录");
		}
		// 获取 客户所有信息
		CustomerAllDto customerDetailInfo = customerService.queryCustomerDetailById(customerId);
		if (customerDetailInfo == null) {
			return BaseResponse.error("未能获取到相关数据");
		}
		return BaseResponse.success(customerDetailInfo);
	}
}