package com.dahantc.erp.controller.supplier;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.flowtask.service.DsSupplierFlowService;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.commom.interceptor.XssHttpServletRequestWrapper;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.supplier.AddSupContactLogReqDto;
import com.dahantc.erp.dto.supplier.AddSupplierReqDto;
import com.dahantc.erp.dto.supplier.SupplierContactLogPageReqDto;
import com.dahantc.erp.dto.supplier.SupplierContactLogPageRspDto;
import com.dahantc.erp.dto.supplier.SupplierDetailRspDto;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;
import com.dahantc.erp.vo.supplierContactLog.service.ISupplierContactLogService;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;
import com.dahantc.erp.vo.supplierContacts.service.ISupplierContactsService;
import com.dahantc.erp.vo.supplierHistory.entity.SupplierHistory;
import com.dahantc.erp.vo.supplierHistory.service.ISupplierHistoryService;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 供应商接口
 *
 * @author 8520
 */
@Controller
@RequestMapping("/supplier")
public class SupplierAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(SupplierAction.class);

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ISupplierTypeService supplierTypeService;

	@Autowired
	private ISupplierHistoryService supplierHistoryService;

	@Autowired
	private ISupplierContactLogService supplierContactLogService;

	@Autowired
	private ISupplierContactsService supplierContactsService;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IBankAccountService bankAccountService;
	
	@Autowired
	private DsSupplierFlowService dsSupplierFlowService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private IMsgCenterService msgCenterService;

	@Autowired
	private IMsgDetailService msgDetailService;

	@Autowired
	private IDepartmentService departmentService;
	
	@Autowired
	private ICustomerTypeService customerTypeService;

	/**
	 * 跳转去供应商档案页面
	 *
	 * @return String 页面路径
	 */
	@RequestMapping("/toSupperPage")
	public String toSupperPage() {
		return "/views/supplier/supplier";
	}

	/**
	 * 跳转去供应商档案页面
	 *
	 * @return String 页面路径
	 */
	@RequestMapping("/toAddSupperPage")
	public String toAddSupperPage() {
		return "/views/supplier/addSupplier";
	}

	@PostMapping("/readPages")
	@ResponseBody
	public BaseResponse<List<SupplierRspDto>> readPages() {
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
			List<Supplier> page = supplierService.queryAllBySearchFilter(filter);
			if (page == null || page.isEmpty()) {
				return BaseResponse.error("未查询到数据");
			}
			List<SupplierRspDto> list = new ArrayList<>();
			for (Supplier sp : page) {
				list.add(new SupplierRspDto(sp.getSupplierId(), sp.getCompanyName()));
			}
			return BaseResponse.success(list);
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	@PostMapping("/getSupplierType")
	@ResponseBody
	public BaseResponse<List<SupplierType>> getSupplierType() {
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			SearchFilter filter = new SearchFilter();
			filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
			List<SupplierType> list = supplierTypeService.queryAllBySearchFilter(filter);
			if (list == null || list.isEmpty()) {
				return BaseResponse.error("未查询到数据");
			}
			return BaseResponse.success(list);
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	@RequestMapping("/toEditSupperlierBaseinfo/{supplierId}")
	public String toEditSupperlierBaseinfo(@PathVariable String supplierId) {
		BaseResponse<SupplierDetailRspDto> supplierDetailDto = readSupplier(supplierId);
		String entitytype = request.getParameter("entityType");
		String operationType = request.getParameter("operationType");
		int entityType = EntityType.SUPPLIER.ordinal();
		if (StringUtil.isNotBlank(entitytype) && StringUtils.isNumeric(entitytype)) {
			Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entitytype));
			if (entityTypeOpt.isPresent()) {
				entityType = entityTypeOpt.get().ordinal();
			}
		}
		if (StringUtils.isNotBlank(supplierId)) {
			try {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, supplierId));
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
			} catch (ServiceException e) {
				logger.error("", e);
			}
		}
		request.setAttribute("supplierInfo", supplierDetailDto.getData());
		request.setAttribute("operationType", operationType);
		if (entityType == EntityType.SUPPLIER_DS.ordinal()) {
			return "/views/supplierDs/editSupplier";
		}
		return "/views/supplier/editSupplier";
	}

	@RequestMapping("/readSupplierInfoById/{supplierId}")
	public String readSupplierInfoById(@PathVariable String supplierId) {
		BaseResponse<SupplierDetailRspDto> supplierDetailDto = readSupplier(supplierId);
		String entitytype = request.getParameter("entityType");
		int entityType = EntityType.SUPPLIER.ordinal();
		if (StringUtil.isNotBlank(entitytype) && StringUtils.isNumeric(entitytype)) {
			Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entitytype));
			if (entityTypeOpt.isPresent()) {
				entityType = entityTypeOpt.get().ordinal();
			}
		}
		request.setAttribute("supplierInfo", supplierDetailDto.getData());
		if (entityType == EntityType.SUPPLIER_DS.ordinal()) {
			return "/views/supplierDs/supplierBaseInfo";
		}
		return "/views/supplier/supplierBaseInfo";
	}

	@PostMapping("/readSupplierById/{supplierId}")
	@ResponseBody
	public BaseResponse<SupplierDetailRspDto> readSupplier(@PathVariable String supplierId) {
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			if (StringUtils.isBlank(supplierId)) {
				return BaseResponse.error("请求参数异常");
			}
			Supplier supplier = supplierService.read(supplierId);
			SupplierDetailRspDto rspDto = new SupplierDetailRspDto(supplier);
			User user1 = userService.read(supplier.getOssUserId());
			if (user1 != null) {
				rspDto.setCreatUser(user1.getRealName());
			}
			return BaseResponse.success(rspDto);
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	@PostMapping("/readSupplierDeptById/{supplierId}")
	@ResponseBody
	public BaseResponse<Map<String, List<SupplierContacts>>> readSupplierDept(@PathVariable String supplierId) {
		try {
			if (StringUtils.isBlank(supplierId)) {
				return BaseResponse.error("请求参数异常");
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
			filter.getOrders().add(new SearchOrder("deptName", Constants.ROP_ASC));
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<SupplierContacts> contacts = supplierContactsService.queryAllBySearchFilter(filter);
			Map<String, List<SupplierContacts>> result = contacts.stream().collect(Collectors.groupingBy(SupplierContacts::getDeptName));
			return BaseResponse.success(result);
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("查询数据异常");
		}
	}

	@RequestMapping("/toAddOrEditSupplierDept/{supplierId}")
	public String toAddOrEditSupplierDept(@PathVariable String supplierId) {
		try {
			BaseResponse<Map<String, List<SupplierContacts>>> result = readSupplierDept(supplierId);
			request.setAttribute("supplierDepts", result.getData());
			request.setAttribute("supplierId", supplierId);
			return "/views/supplier/addOrEditSupplierDept";
		} catch (Exception e) {
			logger.error("跳转添加/修改部门页面异常", e);
		}
		return "";
	}

	@RequestMapping("/editSupplierDept")
	@ResponseBody
	public BaseResponse<String> editSupplierDept() {
		try {
			String depts = request.getParameter("depts");
			depts = XssHttpServletRequestWrapper.xssDecode(depts);
			if (StringUtils.isNotBlank(depts)) {
				List<SupplierContacts> contacts = JSON.parseArray(depts, SupplierContacts.class);
				if (contacts != null && !contacts.isEmpty()) {
					supplierContactsService.editSupplierDept(contacts);
					return BaseResponse.success("保存成功");
				} else {
					return BaseResponse.error("没有联系人");
				}
			} else {
				return BaseResponse.error("参数不合法");
			}
		} catch (Exception e) {
			logger.error("跳转添加/修改部门页面异常", e);
			return BaseResponse.error("添加联系人发生内部错误");
		}
	}

	@RequestMapping("/readSupplierDeptPageById/{supplierId}")
	public String readSupplierDeptPageById(@PathVariable String supplierId) {
		BaseResponse<Map<String, List<SupplierContacts>>> result = readSupplierDept(supplierId);
		request.setAttribute("supplierDepts", result.getData());
		request.setAttribute("supplierId", supplierId);
		return "/views/supplier/supplierDept";
	}

	/**
	 * 根据供应商/客户id 查询当前用户的联系日志时间
	 *
	 * @param supplierId
	 *            供应商/客户id
	 * @return 联系日志时间页面
	 */
	@RequestMapping("/readSupContactLogTimeHtmlById/{supplierId}")
	public String readSupContactLogTimeHtmlById(@PathVariable String supplierId) {
		// 联系的时间
		Map<Integer, List<Integer>> contactTime = new HashMap<Integer, List<Integer>>();
		// 标记最新的一个年份
		int lastYear = 0;
		// 最新年份最新月数
		int lastMonth = 0;
		User user = getOnlineUser();
		if (null != user) {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
			filter.getOrders().add(new SearchOrder("recordTime", Constants.ROP_ASC));
			List<SupplierContactLog> contactLogs = null;
			try {
				contactLogs = supplierContactLogService.queryAllBySearchFilter(filter);
				// 处理时间
				if (contactLogs != null && contactLogs.size() > 0) {
					for (SupplierContactLog contactLog : contactLogs) {
						Timestamp contactLogRecordTime = contactLog.getRecordTime();
						if (contactLogRecordTime != null) {
							// 获取当前联系时间
							LocalDateTime localDateTime = new Date(contactLogRecordTime.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
							int year = localDateTime.getYear();
							List<Integer> months = contactTime.get(year);
							if (months == null) {
								months = new ArrayList<>();
							}
							int month = localDateTime.getMonthValue();
							if (!months.contains(month)) {
								months.add(month);
							}
							contactTime.put(year, months);
							if (year > lastYear) {
								lastYear = year;
							}
						}
					}
					List<Integer> lastYearMonth = contactTime.get(lastYear);
					if (lastYearMonth != null) {
						// 最近一个月
						lastMonth = Collections.max(lastYearMonth);
					}
				}
			} catch (ServiceException e) {
				logger.error("查询联系日志，处理时间错误", e);
			}
		}
		request.setAttribute("lastYear", lastYear);
		request.setAttribute("lastMonth", lastMonth);
		request.setAttribute("contactTimes", contactTime);
		request.setAttribute("supplierId", supplierId);
		return "/views/supplier/supplierContactLogTime";
	}

	/**
	 * 根据时间 供应商/客户id 查询当前用户的联系日志
	 *
	 * @param supplierId
	 *            供应商/客户id
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return 联系日志页面
	 */
	@RequestMapping("/readSupContactLogPageById/{supplierId}/{year}/{month}/{leader}")
	public String readSupContactLogPageById(@PathVariable String supplierId, @PathVariable Integer year, @PathVariable Integer month,
			@PathVariable Boolean leader) {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
		filter.getRules().add(new SearchRule("recordTime", Constants.ROP_LE, DateUtil.getMaxDateTime(year, month)));
		filter.getRules().add(new SearchRule("recordTime", Constants.ROP_GT, DateUtil.getMaxDateTime(year, month - 1)));
		filter.getOrders().add(new SearchOrder("recordTime", Constants.ROP_ASC));
		List<SupplierContactLog> contactLogs = null;
		try {
			contactLogs = supplierContactLogService.queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("查询联系日志的时候出现错误", e);
		}
		List<SupplierContactLogPageRspDto> list = new ArrayList<>();
		if (contactLogs != null) {
			for (SupplierContactLog log : contactLogs) {
				list.add(new SupplierContactLogPageRspDto(log));
			}
		}
		request.setAttribute("supplierId", supplierId);
		request.setAttribute("contactLogs", list);
		return "/views/supplier/supplierContactLog";
	}

	@PostMapping("/readSupContactLogById/{supplierId}")
	@ResponseBody
	public BaseResponse<PageResult<SupplierContactLogPageRspDto>> readSupContactLog(@Valid SupplierContactLogPageReqDto reqDto) {
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, reqDto.getSupplierId()));
			filter.getOrders().add(new SearchOrder("recordTime", Constants.ROP_DESC));
			PageResult<SupplierContactLog> page = supplierContactLogService.queryByPages(reqDto.getPageSize(), reqDto.getPage(), filter);
			if (null != page && page.getData() != null && !page.getData().isEmpty()) {
				List<SupplierContactLogPageRspDto> list = new ArrayList<>();
				for (SupplierContactLog log : page.getData()) {
					list.add(new SupplierContactLogPageRspDto(log));
				}
				PageResult<SupplierContactLogPageRspDto> temp = new PageResult<>(list, page.getCurrentPage(), page.getTotalPages(), page.getCount());
				return BaseResponse.success(temp);
			}
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("查询数据异常");
		}
		return BaseResponse.error("未查询到数据");
	}

	@PostMapping("/addContactLog")
	@ResponseBody
	public BaseResponse<PageResult<String>> addSupContactLog(@Valid AddSupContactLogReqDto reqDto) {
		try {
			User user = getOnlineUser();
			MsgCenter msgCenter = new MsgCenter();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			SupplierContactLog log = reqDto.getSupContLog();
			log.setOssUserId(user.getOssUserId());
			supplierContactLogService.save(log);
			String name = "";
			Supplier supplier = supplierService.read(log.getSupplierId());
			if (supplier != null) {
				name = supplier.getCompanyName();
			} else {
				Customer customer = customerService.read(log.getSupplierId());
				if (customer != null) {
					name = customer.getCompanyName();
					CustomerType customerType = customerTypeService.read(customer.getCustomerTypeId());
					if (customerType != null) {
						msgCenter.setCustomerType(customerType.getCustomerTypeValue());
					}
				}
			}

			String msgDetail = user.getRealName() + "新增客户/供应商日志：" + name;
			if (StringUtil.isNotBlank(log.getContent())) {
				msgDetail = msgDetail + ", " + "工作内容：" + log.getContent();
			}if (StringUtil.isNotBlank(log.getResult())) {
				msgDetail = msgDetail + ", " + "工作结果：" + log.getResult();
			}
			if (log.getWtime()!=null) {
				msgDetail = msgDetail + ", " + "记录创建时间：" + log.getWtime();
			}
			
			msgCenter.setInfotype(MsgCenter.ADD_LOG);
			msgCenter.setOssUserId(user.getOssUserId());
			msgCenter.setWtime(new Date());
			msgCenter.setMessagesourceid(log.getErpSupplierContactLogId());
			msgCenter.setMessagedetail(msgDetail);
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
			return BaseResponse.success("添加联系日志成功");
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("添加联系日志异常");
		}

	}

	@RequestMapping("/toAddContactLog/{supplierId}")
	public String toAddContactLog(@PathVariable String supplierId) {
		request.setAttribute("supplierId", supplierId);
		return "/views/supplier/supplierAddContactLog";
	}

	@PostMapping("/addSupplier")
	@ResponseBody
	public BaseResponse<String> addSupplier(@Valid AddSupplierReqDto reqDto) {
		String entitytype = request.getParameter("entityType");
		int entityType = EntityType.SUPPLIER.ordinal();
		if (StringUtil.isNotBlank(entitytype) && StringUtils.isNumeric(entitytype)) {
			Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entitytype));
			if (entityTypeOpt.isPresent()) {
				entityType = entityTypeOpt.get().ordinal();
			}
		}
		try {
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			if (existsSupplier(reqDto.getCompanyName(), reqDto.getSupplierId())) {
				return BaseResponse.error("该供应商名称已存在");
			}
			Supplier sp = new Supplier();
			reqDto.toSupplier(sp);
			sp.setOssUserId(user.getOssUserId());
			sp.setDeptId(user.getDeptId());
			if (entityType == EntityType.SUPPLIER_DS.ordinal()) {
				sp.setStatus(1);
			}
			List<InvoiceInformation> invoiceInfos = JSONArray.parseArray(XssHttpServletRequestWrapper.xssDecode(reqDto.getInvoiceInfos()),
					InvoiceInformation.class);
			List<BankAccount> bankInfos = JSON.parseArray(XssHttpServletRequestWrapper.xssDecode(reqDto.getBankInfos()), BankAccount.class);
			boolean result = supplierService.save(sp, invoiceInfos, bankInfos);
			if (result && entityType == EntityType.SUPPLIER_DS.ordinal()) {
				dsSupplierFlowService.buildDsSupplierFlow(sp, user, bankInfos);
			}
			String msgDetail = user.getRealName() + "新增供应商：" + sp.getCompanyName();
			if (StringUtil.isNotBlank(sp.getLegalPerson())) {
				msgDetail = msgDetail + ", " + "法人：" + sp.getLegalPerson();
			}
			if (StringUtil.isNotBlank(sp.getRegistrationNumber())) {
				msgDetail = msgDetail + ", " + " 营业执照号：" + sp.getRegistrationNumber();
			}
			if (StringUtil.isNotBlank(sp.getRegistrationAddress())) {
				msgDetail = msgDetail + ", " + "注册地：" + sp.getRegistrationAddress();
			}
			if (StringUtil.isNotBlank(sp.getPostalAddress())) {
				msgDetail = msgDetail + ", " + "地址：" + sp.getPostalAddress();
			}
			if (StringUtil.isNotBlank(sp.getTelephoneNumber())) {
				msgDetail = msgDetail + ", " + "电话：" + sp.getTelephoneNumber();
			}
			if (StringUtil.isNotBlank(sp.getEmail())) {
				msgDetail = msgDetail + ", " + "邮件：" + sp.getEmail();
			}
			if (StringUtil.isNotBlank(sp.getWebsite())) {
				msgDetail = msgDetail + ", " + "网站：" + sp.getWebsite();
			}
			if (StringUtil.isNotBlank(sp.getContactName())) {
				msgDetail = msgDetail + ", " + "联系人：" + sp.getContactName();
			}
			if (StringUtil.isNotBlank(sp.getContactPhone())) {
				msgDetail = msgDetail + ", " + "手机：" + sp.getContactPhone();
			}
			if (sp.getCreationDate() != null) {
				msgDetail = msgDetail + ", " + "创立日期：" + sp.getCreationDate();
			}
			if (StringUtil.isNotBlank(sp.getRegisteredCapital())) {
				msgDetail = msgDetail + ", " + "注册资本：" + sp.getRegisteredCapital();
			}
			if (StringUtil.isNotBlank(sp.getCorporateNature())) {
				msgDetail = msgDetail + ", " + "性质：" + sp.getCorporateNature();
			}
			if (StringUtil.isNotBlank(sp.getContactPhone())) {
				msgDetail = msgDetail + ", " + "税务号：" + sp.getContactPhone();
			}
			if (sp.getWtime() != null) {
				msgDetail = msgDetail + ", " + "记录创建时间：" + sp.getWtime();
			}
			MsgCenter msgCenter = new MsgCenter();
			msgCenter.setInfotype(MsgCenter.ADD_SUPPLIER);
			msgCenter.setOssUserId(user.getOssUserId());
			msgCenter.setWtime(new Date());
			msgCenter.setMessagesourceid(sp.getSupplierId());
			msgCenter.setMessagedetail(msgDetail);
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
				msgDetailEntity.setState(MsgDetail.NOT_READ);
				msgDetailEntity.setUserid(userId);
				msgDetailEntity.setWtime(new Date());
				msgDetailEntity.setMessageid(msgCenter.getMessageid());
				msgDetails.add(msgDetailEntity);
			}
			msgDetailService.saveByBatch(msgDetails);
			return BaseResponse.success("增加供应商成功");
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("增加供应商异常");
		}
	}

	@PostMapping("/editSupplier")
	@ResponseBody
	public BaseResponse<String> editSupplier(@Valid AddSupplierReqDto reqDto) {
		String entitytype = request.getParameter("entityType");
		int entityType = EntityType.SUPPLIER.ordinal();
		if (StringUtil.isNotBlank(entitytype) && StringUtils.isNumeric(entitytype)) {
			Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entitytype));
			if (entityTypeOpt.isPresent()) {
				entityType = entityTypeOpt.get().ordinal();
			}
		}
		try {
			long _start = System.currentTimeMillis();
			User user = getOnlineUser();
			if (null == user) {
				return BaseResponse.noLogin("请先登录");
			}
			if (StringUtil.isBlank(reqDto.getSupplierId())) {
				return BaseResponse.error("供应商id为空");
			}
			if (existsSupplier(reqDto.getCompanyName(), reqDto.getSupplierId())) {
				return BaseResponse.error("该供应商名称已存在");
			}
			logger.info("修改供应商信息开始");
			// 将供应商原始信息保存到历史表
			SupplierHistory sh = new SupplierHistory();
			Supplier sp = supplierService.read(reqDto.getSupplierId());
			if (sp != null) {
				BeanUtils.copyProperties(sp, sh);
				sh.setWtime(new Timestamp(System.currentTimeMillis()));
				supplierHistoryService.save(sh);
				logger.info("保存供应商信息到历史表成功，耗时：" + (System.currentTimeMillis() - _start));
			}
			// 更新供应商信息
			reqDto.toSupplier(sp);
			List<InvoiceInformation> invoiceInfos = JSONArray.parseArray(XssHttpServletRequestWrapper.xssDecode(reqDto.getInvoiceInfos()),
					InvoiceInformation.class);
			List<BankAccount> bankInfos = JSON.parseArray(XssHttpServletRequestWrapper.xssDecode(reqDto.getBankInfos()), BankAccount.class);
			if (entityType == EntityType.SUPPLIER_DS.ordinal()) {
				Supplier supplier = supplierService.read(sp.getSupplierId());
				if (sp.equals(supplier) && reqDto.getDelInvoiceIds()!=null && reqDto.getDelBankIds()!=null) {
//					if (condition) {
//						logger.info("修改供应商信息失败，供应商信息没有变化");
//						return BaseResponse.success("修改供应商信息成功");
//					}
				}
				sp.setStatus(1);
				dsSupplierFlowService.buildDsSupplierFlow(sp, user, bankInfos);
			}
			supplierService.update(sp, invoiceInfos, bankInfos, reqDto.getDelInvoiceIds(), reqDto.getDelBankIds());
			logger.info("修改供应商信息成功，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success("修改供应商信息成功");
		} catch (Exception e) {
			logger.error("修改供应商信息失败", e);
			return BaseResponse.error("修改供应商信息失败");
		}
	}

	private boolean existsSupplier(String companyName, String supplierid) {
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("companyName", Constants.ROP_EQ, companyName));
		filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 0));
		if (StringUtil.isNotBlank(supplierid)) {
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_NE, supplierid));
		}
		try {
			int count = supplierService.getCount(filter);
			if (count == 0) {
				return false;
			} 
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return true;
	}

	/**
	 * 分页加载用户的供应商信息
	 *
	 * @param companyName 公司名
	 * @param pageSize    页大小
	 * @param currentPage 当前页
	 * @return 分页的供应商信息
	 */
	@ResponseBody
	@RequestMapping(path = "/queryUserSupplierInfo")
	public PageResult<SupplierRspDto> queryUserSupplierInfo(String companyName,Integer pageSize,Integer currentPage){
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
		return supplierService.queryUserSupplier(user, companyName, pageSize, currentPage);
	}
}
