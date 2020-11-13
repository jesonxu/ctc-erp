package com.dahantc.erp.vo.customer.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.dahantc.erp.dto.customer.CustomerAllDto;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;
import com.dahantc.erp.vo.supplierContactLog.service.ISupplierContactLogService;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;
import com.dahantc.erp.vo.supplierContacts.service.ISupplierContactsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customer.CustomerDeptResp;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.dto.flow.SubFlowCount;
import com.dahantc.erp.enums.CustomerChangeType;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.bankAccountHistor.entity.BankAccountHistory;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.dao.ICustomerDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerChangeRecord.entity.CustomerChangeRecord;
import com.dahantc.erp.vo.customerChangeRecord.service.ICustomerChangeRecordService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.invoiceHistory.entity.InvoiceInformationHistory;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("customerService")
public class CustomerServiceImpl implements ICustomerService {

	private static Logger logger = LogManager.getLogger(CustomerServiceImpl.class);

	@Autowired
	private ICustomerDao customerDao;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICustomerChangeRecordService customerChangeRecordService;

	@Autowired
	private ISupplierContactsService supplierContactsService;

	@Autowired
	private ISupplierContactLogService supplierContactLogService;

	@Override
	public Customer read(Serializable id) throws ServiceException {
		try {
			return customerDao.read(id);
		} catch (Exception e) {
			logger.error("读取客户信息失败", e);
			throw new ServiceException("读取客户信息失败", e);
		}
	}

	@Override
	public boolean save(Customer entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos) throws ServiceException {
		try {
			// 重新生成关键字
			try {
				User user = userService.read(entity.getOssuserId());
				entity.buildKeyWords(user.getRealName());
			} catch (Exception e) {
				logger.info("生成客户关键词异常", e);
			}
			boolean result = customerDao.save(entity);
			if (result) {
				String customerId = entity.getCustomerId();
				if (invoiceInfos != null) {
					invoiceInfos = invoiceInfos.stream().filter(info -> {
						return !StringUtils.isAllBlank(info.getAccountBank(), info.getBankAccount(), info.getCompanyAddress(), info.getCompanyName(),
								info.getPhone(), info.getTaxNumber());
					}).map(info -> {
						info.setBasicsId(customerId);
						info.setInvoiceType(InvoiceType.OtherInvoice.ordinal());
						info.setCompanyName(StringUtils.isNotBlank(info.getCompanyName()) ? info.getCompanyName().trim() : info.getCompanyName());
						return info;
					}).collect(Collectors.toList());
					result = baseDao.saveByBatch(invoiceInfos);
					logger.info("保存开票信息" + (result ? "成功" : "失败") + "，customerId：" + entity.getCustomerId());
				}
				if (bankInfos != null) {
					bankInfos = bankInfos.stream().filter(info -> {
						return !StringUtils.isAllBlank(info.getAccountBank(), info.getAccountName(), info.getBankAccount(), info.getCompanyAddress());
					}).map(info -> {
						info.setBasicsId(customerId);
						info.setInvoiceType(InvoiceType.OtherBank.ordinal());
						return info;
					}).collect(Collectors.toList());
					result = baseDao.saveByBatch(bankInfos);
					logger.info("保存银行信息" + (result ? "成功" : "失败") + "，supplierId：" + entity.getCustomerId());
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("保存客户信息失败", e);
			throw new ServiceException("保存客户信息失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Customer> objs) throws ServiceException {
		try {
			return customerDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return customerDao.delete(id);
		} catch (Exception e) {
			logger.error("删除客户信息失败", e);
			throw new ServiceException("删除客户信息失败", e);
		}
	}

	@Override
	public boolean update(Customer entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos, String delInvoiceIds, String delBankIds)
			throws ServiceException {
		try {
			try {
				User user = userService.read(entity.getOssuserId());
				entity.buildKeyWords(user.getRealName());
			} catch (Exception e) {
				logger.info("生成客户关键词异常", e);
			}
			boolean result = customerDao.update(entity);
			String customerId = entity.getCustomerId();
			List<InvoiceInformation> saveInvoices = new ArrayList<>(); // 新建的开票信息
			List<InvoiceInformation> updateInvoices = new ArrayList<>(); // 修改过的开票信息
			List<BankAccount> saveBanks = new ArrayList<>(); // 新建的银行信息
			List<BankAccount> updateBanks = new ArrayList<>(); // 修改过的银行信息
			List<BankAccountHistory> bankHisList = new ArrayList<>();
			List<InvoiceInformationHistory> invoiceHisList = new ArrayList<>();
			List<InvoiceInformation> delInvoices = new ArrayList<>();
			List<BankAccount> delBanks = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, customerId));
			if (invoiceInfos != null) {
				// 遍历提交的开票信息
				for (InvoiceInformation invoice : invoiceInfos) {
					if (StringUtils.isNotBlank(invoice.getInvoiceId())) { // 是在之前的记录上修改
						InvoiceInformation inv = invoiceInformationService.read(invoice.getInvoiceId());
						InvoiceInformationHistory invoiceHis = new InvoiceInformationHistory();
						BeanUtils.copyProperties(inv, invoiceHis);
						invoiceHis.setWtime(new Timestamp(System.currentTimeMillis()));
						invoiceHisList.add(invoiceHis);
						inv.setAccountBank(invoice.getAccountBank());
						inv.setBankAccount(invoice.getBankAccount());
						inv.setCompanyAddress(invoice.getCompanyAddress());
						inv.setCompanyName(StringUtils.isNotBlank(invoice.getCompanyName()) ? invoice.getCompanyName().trim() : invoice.getCompanyName());
						inv.setPhone(invoice.getPhone());
						inv.setTaxNumber(invoice.getTaxNumber());
						updateInvoices.add(inv);
					} else { // 是新建的
						invoice.setBasicsId(customerId);
						invoice.setInvoiceType(InvoiceType.OtherInvoice.ordinal());
						invoice.setCompanyName(StringUtils.isNotBlank(invoice.getCompanyName()) ? invoice.getCompanyName().trim() : invoice.getCompanyName());
						saveInvoices.add(invoice);
					}
				}
			}

			// 删除
			if (StringUtils.isNotBlank(delInvoiceIds)) {
				String[] invoiceIds = delInvoiceIds.split(",");
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("invoiceId", Constants.ROP_IN, invoiceIds));
				delInvoices = invoiceInformationService.queryAllBySearchFilter(filter);
				for (InvoiceInformation delIn : delInvoices) {
					InvoiceInformationHistory invoiceHis = new InvoiceInformationHistory();
					BeanUtils.copyProperties(delIn, invoiceHis);
					invoiceHis.setWtime(new Timestamp(System.currentTimeMillis()));
					invoiceHisList.add(invoiceHis);
				}
			}

			if (bankInfos != null) {
				// 遍历提交的银行信息
				for (BankAccount bank : bankInfos) {
					if (StringUtils.isNotBlank(bank.getBankAccountId())) { // 是在之前的记录上修改
						BankAccount bk = bankAccountService.read(bank.getBankAccountId());
						BankAccountHistory bankHis = new BankAccountHistory();
						BeanUtils.copyProperties(bk, bankHis);
						bankHis.setWtime(new Timestamp(System.currentTimeMillis()));
						bankHisList.add(bankHis);
						bk.setAccountName(bank.getAccountName());
						bk.setAccountBank(bank.getAccountBank());
						bk.setBankAccount(bank.getBankAccount());
						updateBanks.add(bk);
					} else { // 是新建的
						bank.setBasicsId(customerId);
						bank.setInvoiceType(InvoiceType.OtherBank.ordinal());
						saveBanks.add(bank);
					}
				}
			}
			// 删除
			if (StringUtils.isNotBlank(delBankIds)) {
				String[] bankIds = delBankIds.split(",");
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("bankAccountId", Constants.ROP_IN, bankIds));
				delBanks = bankAccountService.queryAllBySearchFilter(filter);
				for (BankAccount delBank : delBanks) {
					BankAccountHistory bankHis = new BankAccountHistory();
					BeanUtils.copyProperties(delBank, bankHis);
					bankHis.setWtime(new Timestamp(System.currentTimeMillis()));
					bankHisList.add(bankHis);
				}
			}
			if (result && !ListUtils.isEmpty(saveInvoices)) {
				result = baseDao.saveByBatch(saveInvoices);
			}
			if (result && !ListUtils.isEmpty(updateInvoices)) {
				result = baseDao.updateByBatch(updateInvoices);
			}
			if (result && !ListUtils.isEmpty(delInvoices)) {
				result = baseDao.deleteByBatch(delInvoices);
			}
			if (result && !ListUtils.isEmpty(invoiceHisList)) {
				result = baseDao.saveByBatch(invoiceHisList);
			}
			if (result && !ListUtils.isEmpty(saveBanks)) {
				result = baseDao.saveByBatch(saveBanks);
			}
			if (result && !ListUtils.isEmpty(updateBanks)) {
				result = baseDao.updateByBatch(updateBanks);
			}
			if (result && !ListUtils.isEmpty(delBanks)) {
				result = baseDao.deleteByBatch(delBanks);
			}
			if (result && !ListUtils.isEmpty(bankHisList)) {
				result = baseDao.saveByBatch(bankHisList);
			}
			return result;
		} catch (Exception e) {
			logger.error("更新客户信息失败", e);
			throw new ServiceException("更新客户信息失败", e);
		}
	}

	@Override
	public boolean update(Customer entity) throws ServiceException {
		try {
			return customerDao.update(entity);
		} catch (Exception e) {
			logger.error("更新客户信息失败", e);
			throw new ServiceException("更新客户信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return customerDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询客户信息数量失败", e);
			throw new ServiceException("查询客户信息数量失败", e);
		}
	}

	@Override
	public PageResult<Customer> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询客户信息分页信息失败", e);
			throw new ServiceException("查询客户信息分页信息失败", e);
		}
	}

	@Override
	public List<Customer> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return customerDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询客户信息失败", e);
			throw new ServiceException("查询客户信息失败", e);
		}
	}

	@Override
	public List<Customer> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return customerDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户信息失败", e);
			throw new ServiceException("查询客户信息失败", e);
		}
	}

	@Override
	public List<Customer> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return customerDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询客户信息失败", e);
			throw new ServiceException("查询客户信息失败", e);
		}
	}

	@Override
	public PageResult<Customer> findByhql(String hql, String countHql, Map<String, Object> params, int page, int pageSize) throws ServiceException {
		try {
			return customerDao.findPageByHql(hql, countHql, params, page, pageSize);
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 查询当前登录用户的所有客户（按数据权限，可带部门、客户id条件）
	 *
	 * @param onlineUser
	 *            登录用户
	 * @param deptIds
	 *            部门条件
	 * @param customerId
	 *            客户id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词
	 * @return
	 */
	@Override
	public List<Customer> readCustomers(OnlineUser onlineUser, String deptIds, String customerId, String customerTypeId, String customerKeyWord) {
		// 用户当前登录角色
		String roleId = onlineUser.getRoleId();
		return readCustomersByRole(onlineUser, roleId, deptIds, customerId, customerTypeId, customerKeyWord);
	}

	@Override
	public List<Customer> readCustomers(OnlineUser onlineUser, String deptIds, String customerId, String customerTypeId, String customerKeyWord,
			String companyName) {
		List<Customer> customerList = null;
		// 用户id
		User user = onlineUser.getUser();
		Role role = null;
		List<String> userIds = new ArrayList<>();
		try {
			role = roleService.read(onlineUser.getRoleId());
		} catch (ServiceException e) {
			logger.error("查询角色信息错误：", e);
		}
		if (role == null) {
			return null;
		}
		// 部门id过滤条件
		List<String> deptIdList = new ArrayList<>();
		if (StringUtil.isNotBlank(deptIds)) {
			String[] deptIdsArr = deptIds.split(",");
			deptIdList.addAll(Arrays.asList(deptIdsArr));
		}
		// 角色数据权限
		int dataPermission = role.getDataPermission();
		if (DataPermission.All.ordinal() == dataPermission) {
			// 全部
			customerList = queryAllCustomer(deptIdList, customerId, customerTypeId, customerKeyWord, userIds, companyName);
		} else if (DataPermission.Dept.ordinal() == dataPermission) {
			// 部门权限
			customerList = queryCustomerByDept(user.getDeptId(), deptIdList, customerId, customerTypeId, customerKeyWord, companyName);
		} else if (DataPermission.Flow.ordinal() == dataPermission) {
			// 流程权限
			customerList = queryCustomerByFlow(onlineUser.getRoleId(), user.getOssUserId(), deptIdList, customerId, customerTypeId, customerKeyWord,
					companyName);
		} else if (DataPermission.Self.ordinal() == dataPermission) {
			// 自己
			customerList = querySelfCustomer(user.getOssUserId(), deptIdList, customerId, customerTypeId, customerKeyWord, companyName);
		} else if (DataPermission.Customize.ordinal() == dataPermission) {
			// 自定义
			customerList = queryCustomizeCustomer(role.getDeptIds(), deptIdList, customerId, customerTypeId, customerKeyWord, companyName);
		}
		return customerList;
	}

	/**
	 * 查询当前登录用户的一个角色能看到的所有客户（按数据权限，可带部门、客户id条件）
	 *
	 * @param onlineUser
	 *            登录用户
	 * @param roleId
	 *            角色Id
	 * @param deptIds
	 *            部门条件
	 * @param customerId
	 *            客户id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词
	 * @return
	 */
	@Override
	public List<Customer> readCustomersByRole(OnlineUser onlineUser, String roleId, String deptIds, String customerId, String customerTypeId,
			String customerKeyWord) {
		// 用户id
		User user = onlineUser.getUser();
		Role role = null;
		List<String> userIds = new ArrayList<>();
		try {
			role = roleService.read(roleId);
		} catch (ServiceException e) {
			logger.error("查询角色信息错误：", e);
		}
		if (role == null) {
			return null;
		}
		// 部门id过滤条件
		List<String> deptIdList = new ArrayList<>();
		if (StringUtil.isNotBlank(deptIds)) {
			String[] deptIdsArr = deptIds.split(",");
			deptIdList.addAll(Arrays.asList(deptIdsArr));
		}
		// 角色数据权限
		int dataPermission = role.getDataPermission();
		List<Customer> customerList = null;
		if (DataPermission.All.ordinal() == dataPermission) {
			// 全部
			customerList = queryAllCustomer(deptIdList, customerId, customerTypeId, customerKeyWord, userIds, null);
		} else if (DataPermission.Dept.ordinal() == dataPermission) {
			// 部门权限
			customerList = queryCustomerByDept(user.getDeptId(), deptIdList, customerId, customerTypeId, customerKeyWord, null);
		} else if (DataPermission.Flow.ordinal() == dataPermission) {
			// 流程权限
			customerList = queryCustomerByFlow(roleId, user.getOssUserId(), deptIdList, customerId, customerTypeId, customerKeyWord, null);
		} else if (DataPermission.Self.ordinal() == dataPermission) {
			// 自己
			customerList = querySelfCustomer(user.getOssUserId(), deptIdList, customerId, customerTypeId, customerKeyWord, null);
		} else if (DataPermission.Customize.ordinal() == dataPermission) {
			// 自定义
			customerList = queryCustomizeCustomer(role.getDeptIds(), deptIdList, customerId, customerTypeId, customerKeyWord, null);
		}
		return customerList;
	}

	/**
	 * 读取当前登录用户的客户信息
	 *
	 * @param onlineUser
	 *            在线用户
	 * @return List<CustomerRespDto>
	 */
	@Override
	public List<CustomerRespDto> readCustomersByDept(OnlineUser onlineUser, String deptIds) {
		// 返回结果数据
		List<CustomerRespDto> resultInfo = new ArrayList<>();
		try {
			if (null == onlineUser) {
				return resultInfo;
			}
			// 存放部门id
			List<String> userDeptIds = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			User user = onlineUser.getUser();
			if (StringUtil.isBlank(deptIds)) {
				String userDeptId = user.getDeptId();
				Set<Department> subDept = departmentService.getSubDept(user.getDeptId());
				if (subDept != null && subDept.size() > 0) {
					userDeptIds = subDept.stream().map(Department::getDeptid).collect(Collectors.toList());
				}
				userDeptIds.add(userDeptId);
			} else {
				userDeptIds = new ArrayList<>(Arrays.asList(deptIds.split(",")));
			}
			// 根据部门查询客户
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, userDeptIds));
			List<Customer> customerList = queryAllBySearchFilter(filter);
			if (customerList != null && !customerList.isEmpty()) {
				// 获取未处理的流程
				List<FlowEntDealCount> countList = flowEntService.queryFlowEntDealCount(onlineUser.getRoleId(), user.getOssUserId());
				Map<String, IntSummaryStatistics> untakeFlowCountMap = null;
				if (countList != null && !countList.isEmpty()) {
					untakeFlowCountMap = countList.stream()
							.collect(Collectors.groupingBy(FlowEntDealCount::getSupplierId, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
				}
				for (Customer customer : customerList) {
					String customerId = customer.getCustomerId();
					long flowCount = 0L;
					if (untakeFlowCountMap != null) {
						IntSummaryStatistics statistics = untakeFlowCountMap.get(customerId);
						if (statistics != null) {
							flowCount = statistics.getSum();
						}
					}
					resultInfo.add(new CustomerRespDto(customerId, customer.getCompanyName(), customer.getCustomerTypeId(), flowCount));
				}
				resultInfo.sort((cusFirst, cusSecond) -> cusSecond.getFlowEntCount().compareTo(cusFirst.getFlowEntCount()));
				return resultInfo;
			}
		} catch (Exception e) {
			BaseResponse.error("查询数据异常");
		}
		return resultInfo;
	}

	/**
	 * 根据当前用户的角色的数据权限查客户
	 *
	 * @param onlineUser
	 *            在线用户
	 * @param deptIds
	 *            部门Ids（过滤条件）
	 * @param customerId
	 *            客户id（过滤条件）
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词（过滤条件）
	 * @return List<CustomerRespDto>
	 */
	@Override
	public List<CustomerRespDto> queryCustomers(OnlineUser onlineUser, String deptIds, String customerId, String customerTypeId, String customerKeyWord) {
		// 用户角色
		String roleId = onlineUser.getRoleId();
		Role role = null;
		try {
			role = roleService.read(roleId);
		} catch (ServiceException e) {
			logger.error("查询角色信息错误：", e);
		}
		if (role == null) {
			return null;
		}
		List<Customer> customerList = readCustomers(onlineUser, deptIds, customerId, customerTypeId, customerKeyWord);
		// 统计流程数
		return countCustomerInfo(customerList, roleId, onlineUser.getUser().getOssUserId(), null);
	}

	/**
	 * [全部]查询所有的客户信息
	 *
	 * @param deptIds
	 *            部门id（过滤条件）
	 * @param customerId
	 *            客户id（过滤条件）
	 * @param customerTypeId
	 *            客户类型（点击）
	 * @param customerKeyWord
	 *            客户关键词（过滤条件）
	 * @param companyName
	 *            公司名
	 * @return List<Customer>
	 */
	private List<Customer> queryAllCustomer(List<String> deptIds, String customerId, String customerTypeId, String customerKeyWord, List<String> userIds,
			String companyName) {
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
		SearchFilter filter = new SearchFilter();
		if (!CollectionUtils.isEmpty(userIds)) {
			if (!isPublicType) {
				filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, userIds));
			}
		} else if (CollectionUtils.isEmpty(userIds) && deptIds != null && !deptIds.isEmpty()) {
			if (!isPublicType) {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds));
			}
		}
		if (StringUtil.isNotBlank(customerId)) {
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
		}
		if (StringUtil.isNotBlank(customerTypeId)) {
			filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("查询所有权限 查询客户信息错误", e);
		}
		return null;
	}

	/**
	 * 部门权限 根据用户的部门查询对应的客户信息
	 *
	 * @param userDeptId
	 *            用户的部门id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param customerId
	 *            查询条件 客户id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词
	 * @param companyName
	 *            公司名称
	 * @return 客户信息
	 */
	private List<Customer> queryCustomerByDept(String userDeptId, List<String> deptIds, String customerId, String customerTypeId, String customerKeyWord,
			String companyName) {
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
		// filter的部门条件
		List<String> deptIdList = new ArrayList<>();
		try {
			// 用户部门下的子部门
			Set<Department> subDept = departmentService.getSubDept(userDeptId);
			// 加上自己所在部门
			subDept.add(departmentService.read(userDeptId));
			if (subDept != null && !subDept.isEmpty()) {
				// 部门权限的所有部门，与查询条件带的部门取交集，得出最终的filter的部门条件
				deptIdList = subDept.stream().map(Department::getDeptid).collect(Collectors.toList());
				if (deptIds != null && !deptIds.isEmpty()) {
					deptIdList.retainAll(deptIds);
				}
			}
		} catch (ServiceException e) {
			logger.error("查询子部门错误", e);
		}
		SearchFilter filter = new SearchFilter();
		if (StringUtil.isNotBlank(customerId)) {
			// 客户id条件
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
		}
		if (StringUtil.isNotBlank(customerTypeId)) {
			filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		if (!deptIdList.isEmpty()) {
			if (!isPublicType) {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
			}
			try {
				return queryAllBySearchFilter(filter);
			} catch (ServiceException e) {
				logger.error("根据部门权限 查询客户信息错误", e);
			}
		}
		return null;
	}

	/**
	 * 根据未处理流程查询客户
	 *
	 * @param roleId
	 *            角色id
	 * @param ossuserId
	 *            用户id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param customerId
	 *            查询条件 客户id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词
	 * @param companyName
	 *            公司名
	 * @return 客户信息
	 */
	private List<Customer> queryCustomerByFlow(String roleId, String ossuserId, List<String> deptIds, String customerId, String customerTypeId,
			String customerKeyWord, String companyName) {
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
		// 查询待处理和已处理的流程的客户
		List<String> customerIds = flowEntService.queryFlowEntityId(roleId, ossuserId, EntityType.CUSTOMER);
		SearchFilter filter = new SearchFilter();
		if (customerIds != null && !customerIds.isEmpty()) {
			if (deptIds != null && !deptIds.isEmpty()) {
				if (!isPublicType) {
					filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds));
				}
			}
			if (StringUtil.isNotBlank(customerId)) {
				if (customerIds.contains(customerId)) {
					filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				} else {
					return null;
				}
			} else {
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIds));
			}
			if (StringUtil.isNotBlank(customerTypeId)) {
				filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
			}
			if (StringUtil.isNotBlank(customerKeyWord)) {
				filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
			}
			if (StringUtil.isNotBlank(companyName)) {
				filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
			}
			try {
				return queryAllBySearchFilter(filter);
			} catch (ServiceException e) {
				logger.error("根据流程权限 查询客户信息错误", e);
			}
		}
		return null;
	}

	/**
	 * 查询自己的客户
	 *
	 * @param ossuserId
	 *            用户id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param customerId
	 *            查询条件 客户id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词
	 * @param companyName
	 *            公司名
	 * @return 客户信息
	 */
	private List<Customer> querySelfCustomer(String ossuserId, List<String> deptIds, String customerId, String customerTypeId, String customerKeyWord,
			String companyName) {
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
		SearchFilter filter = new SearchFilter();
		if (deptIds != null && !deptIds.isEmpty()) {
			if (isPublicType) {
				filter.getOrRules().add(new SearchRule[] { new SearchRule("deptId", Constants.ROP_IN, deptIds), new SearchRule("deptId", Constants.ROP_EQ, ""),
						new SearchRule("deptId", Constants.ROP_EQ, null) });
			} else {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds));
			}
		}
		if (StringUtil.isNotBlank(customerId)) {
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
		}
		if (StringUtil.isNotBlank(customerTypeId)) {
			filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		// 自己
		if (isPublicType) {
			filter.getOrRules().add(new SearchRule[] { new SearchRule("ossuserId", Constants.ROP_EQ, ossuserId),
					new SearchRule("ossuserId", Constants.ROP_EQ, ""), new SearchRule("ossuserId", Constants.ROP_EQ, null) });
		} else {
			filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, ossuserId));
		}
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("根据 自己 权限 查询客户信息错误", e);
		}
		return null;
	}

	/**
	 * 查询自定义部门的客户
	 *
	 * @param userDeptIds
	 *            自定义部门id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param customerId
	 *            查询条件 客户id
	 * @param customerTypeId
	 *            客户类型id
	 * @param customerKeyWord
	 *            客户关键词
	 * @param companyName
	 *            公司名称
	 * @return 供应商信息
	 */
	private List<Customer> queryCustomizeCustomer(String userDeptIds, List<String> deptIds, String customerId, String customerTypeId, String customerKeyWord,
			String companyName) {
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
		List<String> subDeptIds = new ArrayList<>();
		// 自定义部门id
		if (StringUtils.isNotBlank(userDeptIds)) {
			subDeptIds = new ArrayList<>(Arrays.asList(userDeptIds.split(",")));
		}
		// 过滤条件部门id
		if (deptIds != null && !deptIds.isEmpty()) {
			// 取交集
			subDeptIds.retainAll(deptIds);
		}
		SearchFilter filter = new SearchFilter();
		if (StringUtil.isNotBlank(customerId)) {
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
		}
		if (StringUtil.isNotBlank(customerTypeId)) {
			filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		if (!subDeptIds.isEmpty()) {
			if (!isPublicType) {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, subDeptIds));
			}
			try {
				return queryAllBySearchFilter(filter);
			} catch (ServiceException e) {
				logger.error("根据自定义权限 查询客户信息错误", e);
			}
		}
		return null;
	}

	/**
	 * 统计客户未处理的流程
	 *
	 * @param customerList
	 *            客户信息
	 * @param roleId
	 *            角色id
	 * @return 客户信息
	 */
	private List<CustomerRespDto> countCustomerInfo(List<Customer> customerList, String roleId, String userId, List<String> deptIds) {
		if (customerList == null || customerList.isEmpty()) {
			return null;
		}
		List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		// 流程统计数据
		List<SubFlowCount> countList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, userId);
		return customerList.stream().map(customer -> {
			CustomerRespDto customerDto = new CustomerRespDto();
			customerDto.setCustomerTypeId(customer.getCustomerTypeId());
			customerDto.setCustomerId(customer.getCustomerId());
			customerDto.setCompanyName(customer.getCompanyName());
			Integer flowCount = 0;
			if (countList != null) {
				for (SubFlowCount countIndo : countList) {
					if (countIndo.getSubId().equalsIgnoreCase(customer.getCustomerId())
							&& (CollectionUtils.isEmpty(deptIds) || deptIds.contains(countIndo.getDeptId()))) {
						flowCount = countIndo.getFlowCount();
						break;
					}
				}
			}
			customerDto.setFlowEntCount(flowCount.longValue());
			return customerDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 按部门查询客户信息（子部门 + 客户）
	 *
	 * @param role
	 *            登录角色（用户属性）
	 * @param ossuserId
	 *            用户id（用户属性）
	 * @param uDeptId
	 *            用户部门（用户属性）
	 * @param customerTypeId
	 *            客户类型id（展开条件）
	 * @param deptId
	 *            上级部门id（展开条件）
	 * @param opendOssUserId
	 *            销售id（展开条件）
	 * @param searchDeptIds
	 *            部门id(查询条件)
	 * @param searchCustomerId
	 *            客户id（查询条件）
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户部门信息
	 */
	@Override
	public List<CustomerDeptResp> queryCustomerAndDept(Role role, String ossuserId, String uDeptId, String customerTypeId, String deptId, String opendOssUserId,
			String searchDeptIds, String searchCustomerId, String customerKeyWord, String searchUserIds) {
		String roleId = role.getRoleid();
		// 角色数据权限
		int dataPermission = role.getDataPermission();
		// 部门查询条件
		List<String> searchDeptIdList = new ArrayList<>();
		if (StringUtil.isNotBlank(searchDeptIds)) {
			String[] deptIdsArr = searchDeptIds.split(",");
			searchDeptIdList.addAll(Arrays.asList(deptIdsArr));
		}
		// 销售查询条件
		List<String> searchUserIdList = new ArrayList<>();
		if (StringUtil.isNotBlank(searchUserIds)) {
			String[] userIdsArr = searchUserIds.split(",");
			searchUserIdList.addAll(Arrays.asList(userIdsArr));
			searchDeptIdList.removeAll(searchUserIdList);
		}
		List<CustomerDeptResp> customerDeptResps = null;
		if (DataPermission.All.ordinal() == dataPermission) {
			// 全部权限
			customerDeptResps = getOpenInfoByAll(customerTypeId, deptId, opendOssUserId, searchDeptIdList, searchCustomerId, roleId, customerKeyWord,
					searchUserIdList, ossuserId);
		} else if (DataPermission.Dept.ordinal() == dataPermission) {
			// 部门权限
			customerDeptResps = getOpenInfoByDept(customerTypeId, deptId, opendOssUserId, searchDeptIdList, searchCustomerId, roleId, uDeptId, customerKeyWord,
					searchUserIdList, ossuserId);
			boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
			if (isPublicType && !CollectionUtils.isEmpty(customerDeptResps)) {
				customerDeptResps = customerDeptResps.stream().filter(item -> item.getIsDept() == 0).collect(Collectors.toList());
			}
		} else if (DataPermission.Flow.ordinal() == dataPermission) {
			// 查询流程 对应的部门
			customerDeptResps = getOpenInfoByFlow(customerTypeId, deptId, opendOssUserId, searchDeptIdList, searchCustomerId, ossuserId, roleId,
					customerKeyWord, searchUserIdList);
		} else if (DataPermission.Self.ordinal() == dataPermission) {
			// 只查询自己的客户（没有部门）
			customerDeptResps = getOpenInfoBySelf(customerTypeId, searchDeptIdList, searchCustomerId, ossuserId, roleId, customerKeyWord, searchUserIdList);
		} else if (DataPermission.Customize.ordinal() == dataPermission) {
			// 自定义 自己客户 + 定义部门最上层
			String userDeptIds = role.getDeptIds();
			// 自定义部门id
			if (StringUtils.isBlank(userDeptIds)) {
				return null;
			}
			List<String> defDeptIds = new ArrayList<>(Arrays.asList(userDeptIds.split(",")));
			customerDeptResps = getOpenInfoByCustomize(customerTypeId, deptId, opendOssUserId, searchDeptIdList, searchCustomerId, roleId, defDeptIds,
					customerKeyWord, searchUserIdList, ossuserId);
		}
		return customerDeptResps;
	}

	/**
	 * 【全部】获取展开信息 （查询客户|部门信息）
	 *
	 * @param openCustomerTypeId
	 *            客户类型Id(展开条件 必定有)
	 * @param openDeptId
	 *            部门id（展开选项部门id）
	 * @param searchDeptIds
	 *            部门Id集合（查询条件的部门id）
	 * @param searchCustomerId
	 *            客户id（查询条件）
	 * @param roleId
	 *            用户角色id（自身属性）
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户部门信息
	 */
	private List<CustomerDeptResp> getOpenInfoByAll(String openCustomerTypeId, String openDeptId, String openUserId, List<String> searchDeptIds,
			String searchCustomerId, String roleId, String customerKeyWord, List<String> searchUserIds, String ossuserId) {
		boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);
		// 返回结果
		List<CustomerDeptResp> customerDeptResp = new ArrayList<>();
		if (StringUtil.isNotBlank(openCustomerTypeId)) {
			if (StringUtil.isBlank(openDeptId) && !customerTypeService.validatePublicType(openCustomerTypeId)) {
				// 展开分类
				// 最上层部门id为 权限的最开始点
				List<Department> rootDepartments = departmentService.getRootDept();
				if (rootDepartments == null) {
					// 没有根节点(数据错误的情况)
					return null;
				}

				// 获取所有的子部门信息
				List<Department> departments = departmentService.queryAll();
				List<String> allDeptIds = departments.stream().map(Department::getDeptid).collect(Collectors.toList());
				if (searchDeptIds != null && !searchDeptIds.isEmpty()) {
					allDeptIds.retainAll(searchDeptIds);
				}

				// 查询节点挂载的客户(有可能查询条件会过滤掉一部门)
				List<Customer> customerList = queryCustomer(allDeptIds, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
				if (customerList == null) {
					// 没有查询到客户信息（被筛选掉）
					return null;
				}
				List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				// 全部未处理流程数量
				List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, ossuserId);

				for (Department department : rootDepartments) {
					CustomerDeptResp customerDept = new CustomerDeptResp();
					customerDept.setDeptId(department.getDeptid());
					customerDept.setDeptName(department.getDeptname());
					customerDept.setIsDept(1);

					List<String> deptIds = new ArrayList<>();
					deptIds.add(department.getDeptid());

					// 全部中 过滤这部分部门
					List<String> subDeptIds = departmentService.getSubDeptIds(department.getDeptid());
					if (subDeptIds != null && !subDeptIds.isEmpty()) {
						deptIds.addAll(subDeptIds);
					}
					// 客户数
					int customerCount = (int) customerList.stream().filter(customer -> deptIds.contains(customer.getDeptId())).count();
					// 没有客户的部门不展示
					if (customerCount == 0) {
						continue;
					}
					int flowCount = 0;
					if (flowCountList != null && !flowCountList.isEmpty()) {
						flowCount = flowCountList.stream().filter(flowCountInfo -> deptIds.contains(flowCountInfo.getDeptId()))
								.mapToInt(SubFlowCount::getFlowCount).sum();
					}
					customerDept.setFlowCount(flowCount);
					customerDept.setCustomerCount(customerCount);
					customerDeptResp.add(customerDept);
				}
				return customerDeptResp;
			} else {
				// 有上级部门 下级部门 以及客户信息
				// 查询下级部门
				List<Department> subDepartments = departmentService.getDeptByFatherId(openDeptId, searchDeptIds);
				if (subDepartments != null && !subDepartments.isEmpty()) {
					List<String> queryDeptIds = new ArrayList<>();
					// 当前各个部门及其对应的子部门
					Map<String, List<String>> deptIdAndSubIds = new HashMap<>();
					// 部门id
					subDepartments.forEach(department -> {
						queryDeptIds.add(department.getDeptid());
						List<String> subDeptIds = departmentService.getSubDeptIds(department.getDeptid());
						deptIdAndSubIds.put(department.getDeptid(), subDeptIds);

						if (subDeptIds != null) {
							queryDeptIds.addAll(subDeptIds);
						}
					});

					if (searchDeptIds != null && !searchDeptIds.isEmpty()) {
						queryDeptIds.retainAll(searchDeptIds);
					}
					// 查询对应的用户
					List<Customer> customerList = queryCustomer(queryDeptIds, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);

					// 查询未处理流程
					List<SubFlowCount> customerFlowCounts = new ArrayList<>();
					if (customerList != null && !customerList.isEmpty()) {
						// 获取到的子部门客户
						List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
						// 子部门客户对应流程
						List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, ossuserId);
						if (flowCountList != null && !flowCountList.isEmpty()) {
							customerFlowCounts.addAll(flowCountList);
						}
					}

					for (Department department : subDepartments) {
						CustomerDeptResp deptResp = new CustomerDeptResp();
						deptResp.setDeptId(department.getDeptid());
						deptResp.setDeptName(department.getDeptname());
						deptResp.setIsDept(1);
						int customerCount = 0;
						int flowCount = 0;
						List<String> subDeptids = new ArrayList<>();
						List<String> deptids = deptIdAndSubIds.get(department.getDeptid());
						if (deptids != null) {
							subDeptids.addAll(deptids);
						}
						subDeptids.add(department.getDeptid());
						if (customerList != null && !customerList.isEmpty()) {
							customerCount = (int) customerList.stream().filter(customer -> subDeptids.contains(customer.getDeptId())).count();
						}
						// 没有客户的部门不展示
						if (customerCount == 0) {
							continue;
						}
						if (!customerFlowCounts.isEmpty()) {
							flowCount = customerFlowCounts.stream().filter(subFlowCount -> subDeptids.contains(subFlowCount.getDeptId()))
									.mapToInt(SubFlowCount::getFlowCount).sum();
						}
						deptResp.setCustomerCount(customerCount);
						deptResp.setFlowCount(flowCount);
						customerDeptResp.add(deptResp);
					}
				} else if (!customerTypeService.validatePublicType(openCustomerTypeId)) {
					// 展开的客户信息
					if (StringUtils.isBlank(openUserId) && !isPublicType) {
						// 点开的是部门且不是公共池客户类型，查询当前部门的所有销售
						List<Object[]> list = queryDeptUser(openDeptId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
						if (!CollectionUtils.isEmpty(list)) {
							List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(null, openDeptId, roleId, EntityType.CUSTOMER, ossuserId);
							customerDeptResp.addAll(list.stream().map(arr -> {
								CustomerDeptResp customerDept = new CustomerDeptResp();
								customerDept.setIsDept(2); // 销售
								customerDept.setDeptName((String) arr[2]);
								customerDept.setDeptId((String) arr[1]);
								// 流程数量
								int flowCount = 0;
								if (flowCountList != null && !flowCountList.isEmpty()) {
									flowCount = flowCountList.stream().filter(subFlowCount -> StringUtils.equals((String) arr[1], subFlowCount.getOssUserId()))
											.mapToInt(SubFlowCount::getFlowCount).sum();
								}
								customerDept.setCustomerCount(((Number) arr[0]).intValue());
								customerDept.setFlowCount(flowCount);
								return customerDept;
							}).collect(Collectors.toList()));
						}
						return customerDeptResp;
					} else {
						// 点开的是客户，或者公共池客户类型
						List<Customer> thisDeptCustomer = queryUserCustomer(openUserId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
						if (thisDeptCustomer != null && !thisDeptCustomer.isEmpty()) {
							// 部门客户ID
							List<String> cIds = thisDeptCustomer.stream().map(Customer::getCustomerId).collect(Collectors.toList());
							// 部门客户流程
							List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(cIds, null, roleId, EntityType.CUSTOMER, ossuserId);
							customerDeptResp.addAll(thisDeptCustomer.stream().map(customer -> {
								CustomerDeptResp customerDept = new CustomerDeptResp();
								customerDept.setIsDept(0);
								customerDept.setCompanyName(customer.getCompanyName());
								customerDept.setCustomerId(customer.getCustomerId());
								int flowCount = 0;
								if (flowCountList != null && !flowCountList.isEmpty()) {
									flowCount = flowCountList.stream().filter(subFlowCount -> subFlowCount.getSubId().equals(customer.getCustomerId()))
											.mapToInt(SubFlowCount::getFlowCount).sum();
								}
								customerDept.setFlowCount(flowCount);
								return customerDept;
							}).collect(Collectors.toList()));
						}
						return customerDeptResp;
					}
				}
				// 展开的客户信息
				if (StringUtils.isBlank(openUserId) && !isPublicType) {
					// 点开的是部门且不是公共池客户类型，查询当前部门的所有销售
					List<Object[]> list = queryDeptUser(openDeptId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
					if (!CollectionUtils.isEmpty(list)) {
						List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(null, openDeptId, roleId, EntityType.CUSTOMER, ossuserId);
						customerDeptResp.addAll(list.stream().map(arr -> {
							CustomerDeptResp customerDept = new CustomerDeptResp();
							customerDept.setIsDept(2); // 销售
							customerDept.setDeptName((String) arr[2]);
							customerDept.setDeptId((String) arr[1]);
							// 流程数量
							int flowCount = 0;
							if (flowCountList != null && !flowCountList.isEmpty()) {
								flowCount = flowCountList.stream().filter(subFlowCount -> StringUtils.equals((String) arr[1], subFlowCount.getOssUserId()))
										.mapToInt(SubFlowCount::getFlowCount).sum();
							}
							customerDept.setCustomerCount(((Number) arr[0]).intValue());
							customerDept.setFlowCount(flowCount);
							return customerDept;
						}).collect(Collectors.toList()));
					}
					return customerDeptResp;
				} else {
					// 点开的是客户，或者公共池客户类型
					List<Customer> thisDeptCustomer = queryUserCustomer(openUserId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
					// 查询部门的客户信息
					if (thisDeptCustomer != null && !thisDeptCustomer.isEmpty()) {
						// 部门客户ID
						List<String> cIds = thisDeptCustomer.stream().map(Customer::getCustomerId).collect(Collectors.toList());
						// 部门客户流程
						List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(cIds, null, roleId, EntityType.CUSTOMER, ossuserId);
						customerDeptResp.addAll(thisDeptCustomer.stream().map(customer -> {
							CustomerDeptResp customerDept = new CustomerDeptResp();
							customerDept.setIsDept(0);
							customerDept.setCompanyName(customer.getCompanyName());
							customerDept.setCustomerId(customer.getCustomerId());
							int flowCount = 0;
							if (flowCountList != null && !flowCountList.isEmpty()) {
								flowCount = flowCountList.stream().filter(subFlowCount -> subFlowCount.getSubId().equals(customer.getCustomerId()))
										.mapToInt(SubFlowCount::getFlowCount).sum();
							}
							customerDept.setFlowCount(flowCount);
							return customerDept;
						}).collect(Collectors.toList()));
					}
					return customerDeptResp;
				}
			}
		}
		return customerDeptResp;
	}

	/**
	 * 根据部门，用户类型 ，客户id查询客户信息
	 *
	 * @param customerDeptIds
	 *            客户部门id（必填 且不为空）
	 * @param openCustomerTypeId
	 *            客户类型 （必填 且不为空）
	 * @param searchCustomerId
	 *            客户id （不必填 查询条件）
	 * @param customerKeyWord
	 *            客户关键词（不必填 查询条件）
	 * @return 客户信息
	 */
	private List<Customer> queryCustomer(List<String> customerDeptIds, String openCustomerTypeId, String searchCustomerId, String customerKeyWord,
			List<String> customerUserIds) {
		boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);
		// 查询节点挂载的客户(有可能查询条件会过滤掉一部门)
		SearchFilter filter = new SearchFilter();
		if (!CollectionUtils.isEmpty(customerUserIds)) {
			if (isPublicType) {
				filter.getOrRules().add(new SearchRule[] { new SearchRule("ossuserId", Constants.ROP_IN, customerUserIds),
						new SearchRule("ossuserId", Constants.ROP_EQ, ""), new SearchRule("ossuserId", Constants.ROP_EQ, null) });
			} else {
				filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, customerUserIds));
			}
		} else {
			if (isPublicType) {
				filter.getOrRules().add(new SearchRule[] { new SearchRule("deptId", Constants.ROP_IN, customerDeptIds),
						new SearchRule("deptId", Constants.ROP_EQ, ""), new SearchRule("deptId", Constants.ROP_EQ, null) });
			} else {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, customerDeptIds));
			}
		}
		filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, openCustomerTypeId));
		if (StringUtil.isNotBlank(searchCustomerId)) {
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, searchCustomerId));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("查询所有权限 查询客户信息错误", e);
		}
		return null;
	}

	/**
	 * 查询指定部门、指定客户类型的客户
	 *
	 * @param openDeptId
	 *            部门(必须)
	 * @param openCustomerTypeId
	 *            客户类型(必须)
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户信息
	 */
	@SuppressWarnings("unused")
	private List<Customer> queryDeptCustomer(String openDeptId, String openCustomerTypeId, String searchCustomerId, String customerKeyWord,
			List<String> customerUserIds) {
		boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);
		if (!isPublicType && (StringUtil.isBlank(openDeptId) || StringUtil.isBlank(openCustomerTypeId))) {
			return null;
		}

		Map<String, Object> params = new HashMap<>();
		StringBuffer hql = new StringBuffer();
		if (isPublicType) {
			hql.append("select customerId,companyName,customerTypeId, deptId from Customer where customerTypeId = :customerTypeId ");
		} else {
			hql.append("select customerId,companyName,customerTypeId, deptId from Customer where deptId = :deptId and customerTypeId = :customerTypeId ");
			params.put("deptId", openDeptId);
		}
		params.put("customerTypeId", openCustomerTypeId);
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and customerId = :customerId");
			params.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			params.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		if (!CollectionUtils.isEmpty(customerUserIds)) {
			if (!isPublicType) {
				hql.append(" and ossuserId in (:ossuserIds) ");
				params.put("ossuserIds", customerUserIds);
			}
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), params, 0);
			if (results != null && !results.isEmpty()) {
				return results.stream().map(result -> {
					Customer customer = new Customer();
					Object[] rArr = (Object[]) result;
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCompanyName(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCustomerTypeId(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("部门客户查询失败", e);
		}
		return null;
	}

	/**
	 * 查询指定销售、指定客户类型的客户
	 *
	 * @param openUserId
	 *            部门(必须)
	 * @param openCustomerTypeId
	 *            客户类型(必须)
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户信息
	 */
	private List<Customer> queryUserCustomer(String openUserId, String openCustomerTypeId, String searchCustomerId, String customerKeyWord,
			List<String> customerUserIds) {
		boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);
		if (!isPublicType && (StringUtil.isBlank(openUserId) || StringUtil.isBlank(openCustomerTypeId))) {
			return null;
		}

		Map<String, Object> params = new HashMap<>();
		StringBuffer hql = new StringBuffer();
		if (isPublicType) {
			hql.append("select customerId,companyName,customerTypeId, deptId from Customer where customerTypeId = :customerTypeId");
		} else {
			hql.append("select customerId,companyName,customerTypeId, deptId from Customer where ossuserId = :ossUserId and customerTypeId = :customerTypeId");
			params.put("ossUserId", openUserId);
		}
		params.put("customerTypeId", openCustomerTypeId);
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and customerId = :customerId");
			params.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			params.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		if (!CollectionUtils.isEmpty(customerUserIds)) {
			if (!isPublicType) {
				hql.append(" and ossuserId in (:ossuserIds) ");
				params.put("ossuserIds", customerUserIds);
			}
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), params, 0);
			if (results != null && !results.isEmpty()) {
				return results.stream().map(result -> {
					Customer customer = new Customer();
					Object[] rArr = (Object[]) result;
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCompanyName(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCustomerTypeId(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("部门客户查询失败", e);
		}
		return null;
	}

	/**
	 * 查询指定部门、指定客户类型的销售
	 *
	 * @param openDeptId
	 *            部门(必须)
	 * @param openCustomerTypeId
	 *            客户类型(必须)
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 销售信息 0.customerCount 1.ossUserId 2.realName
	 */
	private List<Object[]> queryDeptUser(String openDeptId, String openCustomerTypeId, String searchCustomerId, String customerKeyWord,
			List<String> customerUserIds) {

		if (StringUtil.isBlank(openDeptId) || StringUtil.isBlank(openCustomerTypeId)) {
			return null;
		}

		Map<String, Object> params = new HashMap<>();
		StringBuffer hql = new StringBuffer("SELECT COUNT(customerId), ossuserId FROM Customer WHERE customerTypeId = :customerTypeId");
		params.put("customerTypeId", openCustomerTypeId);

		if (StringUtils.isNotBlank(openDeptId)) {
			hql.append(" AND deptId = :openDeptId");
			params.put("openDeptId", openDeptId);
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" AND customerId = :customerId");
			params.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" AND keyWords LIKE :customerKeyWord");
			params.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		if (!CollectionUtils.isEmpty(customerUserIds)) {
			hql.append(" AND ossuserId IN (:ossUserIds)");
			params.put("ossUserIds", customerUserIds);
		}
		hql.append(" GROUP BY ossuserid");
		try {
			List<Object[]> results = baseDao.findByhql(hql.toString(), params, 0);
			if (!CollectionUtils.isEmpty(results)) {
				results = results.stream().filter(arr -> {
					return arr.length == 2 && arr[0] != null && ((Number) arr[0]).intValue() > 0;
				}).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(results)) {
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, openDeptId));
					List<User> userList = userService.queryAllBySearchFilter(searchFilter);
					if (!CollectionUtils.isEmpty(userList)) {
						Map<String, String> cacheUserMap = userList.stream().collect(Collectors.toMap(User::getOssUserId, User::getRealName, (v1, v2) -> v1));
						results = results.stream().map(arr -> {
							Object[] newArr = new Object[3];
							System.arraycopy(arr, 0, newArr, 0, 2);
							newArr[2] = cacheUserMap.get((String) arr[1]);
							return newArr;
						}).collect(Collectors.toList());
						return results;
					}
				}
			}
		} catch (BaseException e) {
			logger.error("用户查询失败", e);
		}
		return null;
	}

	/**
	 * 查询指定用户、指定客户类型的客户
	 *
	 * @param openDeptId
	 *            部门(必须)
	 * @param openCustomerTypeId
	 *            客户类型(必须)
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户信息
	 */
	@SuppressWarnings("unused")
	private List<Customer> queryDeptCustomerByUserId(String openDeptId, String openCustomerTypeId, String searchCustomerId, String customerKeyWord,
			List<String> searchUserIds) {
		boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);

		if (StringUtil.isBlank(openDeptId) || StringUtil.isBlank(openCustomerTypeId)) {
			return null;
		}

		Map<String, Object> params = new HashMap<>();
		StringBuffer hql = new StringBuffer();
		hql.append("select customerId,companyName,customerTypeId, deptId from Customer where  customerTypeId = :customerTypeId ");

		params.put("customerTypeId", openCustomerTypeId);
		if (!CollectionUtils.isEmpty(searchUserIds)) {
			if (!isPublicType) {
				hql.append(" and ossuserId in (:ossuserIds)");
				params.put("ossuserIds", searchUserIds);
			}
		}
		if (!isPublicType) {
			hql.append(" and deptId = :deptId ");
			params.put("deptId", openDeptId);
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and customerId = :customerId");
			params.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			params.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), params, 0);
			if (results != null && !results.isEmpty()) {
				return results.stream().map(result -> {
					Customer customer = new Customer();
					Object[] rArr = (Object[]) result;
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCompanyName(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCustomerTypeId(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("部门客户查询失败", e);
		}
		return null;
	}

	/**
	 * 【部门】获取展开信息 （查询客户|部门信息）
	 *
	 * @param openCustomerTypeId
	 *            客户类型Id(展开条件 必定有)
	 * @param openDeptId
	 *            部门id（展开选项部门id）
	 * @param searchDeptIds
	 *            部门Id集合（查询条件的部门id）
	 * @param searchCustomerId
	 *            客户id（查询条件）
	 * @param roleId
	 *            用户角色id（自身属性）
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户部门信息
	 */
	private List<CustomerDeptResp> getOpenInfoByDept(String openCustomerTypeId, String openDeptId, String openUserId, List<String> searchDeptIds,
			String searchCustomerId, String roleId, String uDept, String customerKeyWord, List<String> searchUserIds, String ossuserId) {
		if (StringUtil.isNotBlank(openCustomerTypeId)) {
			boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);
			if (StringUtil.isBlank(openDeptId)) {
				openDeptId = uDept;
			}
			// 展开部门
			List<CustomerDeptResp> cusAndDeptList = new ArrayList<>();
			// 有上级部门 下级部门 以及客户信息
			// 查询下级部门
			List<Department> departments = departmentService.getDeptByFatherId(openDeptId, searchDeptIds);
			if (departments != null && !departments.isEmpty()) {
				// 查询的子部门id
				List<String> allSubDeptIds = new ArrayList<>();
				// 当前各个部门及其对应的子部门
				Map<String, List<String>> deptIdAndSubIds = new HashMap<>();

				// 部门id
				departments.forEach(department -> {
					allSubDeptIds.add(department.getDeptid());
					// 部门 对应的子部门
					List<String> deptSubDeptIds = departmentService.getSubDeptIds(department.getDeptid());
					deptIdAndSubIds.put(department.getDeptid(), deptSubDeptIds);
					if (deptSubDeptIds != null && deptSubDeptIds.isEmpty()) {
						allSubDeptIds.addAll(deptSubDeptIds);
					}
				});

				if (searchDeptIds != null && !searchDeptIds.isEmpty()) {
					allSubDeptIds.retainAll(searchDeptIds);
				}
				// 查询对应的用户
				List<Customer> customerList = queryAllCustomer(allSubDeptIds, searchCustomerId, openCustomerTypeId, customerKeyWord, searchUserIds, null);
				// 查询未处理流程
				List<SubFlowCount> customerFlowCounts = new ArrayList<>();
				if (customerList != null && !customerList.isEmpty()) {
					List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
					List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, ossuserId);
					if (flowCountList != null && !flowCountList.isEmpty()) {
						customerFlowCounts.addAll(flowCountList);
					}
				}
				for (Department department : departments) {
					CustomerDeptResp deptResp = new CustomerDeptResp();
					deptResp.setDeptId(department.getDeptid());
					deptResp.setDeptName(department.getDeptname());
					deptResp.setIsDept(1);
					int customerCount = 0;
					int flowCount = 0;
					List<String> subDeptids = new ArrayList<>();
					subDeptids.add(department.getDeptid());
					List<String> subIds = deptIdAndSubIds.get(department.getDeptid());
					if (subIds != null) {
						subDeptids.addAll(subIds);
					}
					if (customerList != null && !customerList.isEmpty()) {
						customerCount = (int) customerList.stream().filter(customer -> subDeptids.contains(customer.getDeptId())).count();
					}
					// 没有客户的部门不展示
					if (customerCount == 0) {
						continue;
					}
					if (!customerFlowCounts.isEmpty()) {
						flowCount = customerFlowCounts.stream().filter(subFlowCount -> {
							if (isPublicType) {
								return subDeptids.contains(subFlowCount.getDeptId()) && subDeptids.contains(subFlowCount.getDeptId());
							} else {
								return subDeptids.contains(subFlowCount.getDeptId());
							}
						}).mapToInt(SubFlowCount::getFlowCount).sum();
					}
					deptResp.setCustomerCount(customerCount);
					deptResp.setFlowCount(flowCount);
					cusAndDeptList.add(deptResp);
				}
			}

			// 展开的客户信息
			if (StringUtils.isBlank(openUserId) && !isPublicType) {
				// 点开的是部门且不是公共池客户类型，查询当前部门的所有销售
				List<Object[]> list = queryDeptUser(openDeptId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
				if (!CollectionUtils.isEmpty(list)) {
					List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(null, openDeptId, roleId, EntityType.CUSTOMER, ossuserId);
					cusAndDeptList.addAll(list.stream().map(arr -> {
						CustomerDeptResp customerDept = new CustomerDeptResp();
						customerDept.setIsDept(2); // 销售
						customerDept.setDeptName((String) arr[2]);
						customerDept.setDeptId((String) arr[1]);
						// 流程数量
						int flowCount = 0;
						if (flowCountList != null && !flowCountList.isEmpty()) {
							flowCount = flowCountList.stream().filter(subFlowCount -> StringUtils.equals((String) arr[1], subFlowCount.getOssUserId()))
									.mapToInt(SubFlowCount::getFlowCount).sum();
						}
						customerDept.setCustomerCount(((Number) arr[0]).intValue());
						customerDept.setFlowCount(flowCount);
						return customerDept;
					}).collect(Collectors.toList()));
				}
				return cusAndDeptList;
			} else {
				// 查询本部门用户(需要加上部门查询的限制)
				List<Customer> thisDeptCustomer = queryUserCustomer(openUserId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
				if (thisDeptCustomer != null && !thisDeptCustomer.isEmpty()) {
					// 公共池客户
					List<String> deptIds = new ArrayList<>();
					if (isPublicType) {
						try {
							User user = userService.read(ossuserId);
							Set<Department> deptSet = departmentService.getSubDept(user.getDeptId());
							if (!CollectionUtils.isEmpty(deptSet)) {
								deptIds.addAll(deptSet.stream().map(Department::getDeptid).collect(Collectors.toList()));
							}
							deptIds.add(user.getDeptId());
						} catch (ServiceException e) {
							logger.error("", e);
						}
					}
					List<String> cIds = thisDeptCustomer.stream().map(Customer::getCustomerId).collect(Collectors.toList());
					List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(cIds, null, roleId, EntityType.CUSTOMER, ossuserId);
					cusAndDeptList.addAll(thisDeptCustomer.stream().map(customer -> {
						CustomerDeptResp customerDept = new CustomerDeptResp();
						customerDept.setIsDept(0);
						customerDept.setCompanyName(customer.getCompanyName());
						customerDept.setCustomerId(customer.getCustomerId());
						int flowCount = 0;
						if (flowCountList != null) {
							flowCount = flowCountList.stream().filter(subFlowCount -> {
								if (!isPublicType) {
									return subFlowCount.getSubId().equals(customer.getCustomerId());
								} else {
									return deptIds.contains(customer.getDeptId()) && subFlowCount.getSubId().equals(customer.getCustomerId());
								}
							}).mapToInt(SubFlowCount::getFlowCount).sum();
						}
						customerDept.setFlowCount(flowCount);
						return customerDept;
					}).collect(Collectors.toList()));
				}
				return cusAndDeptList.stream().filter(Objects::nonNull).collect(Collectors.toList());
			}
		} else {
			return null;
		}
	}

	/**
	 * 【流程】获取展开信息 （查询客户|部门信息）
	 *
	 * @param openCustomerTypeId
	 *            客户类型Id(展开条件 必定有)
	 * @param openDeptId
	 *            部门id（展开选项部门id）
	 * @param searchDeptIds
	 *            部门Id集合（查询条件的部门id）
	 * @param searchCustomerId
	 *            客户id（查询条件）
	 * @param roleId
	 *            用户角色id（自身属性）
	 * @param customerKeyWord
	 *            客户关键词(查询条件)
	 * @return 客户部门信息
	 */
	private List<CustomerDeptResp> getOpenInfoByFlow(String openCustomerTypeId, String openDeptId, String openUserId, List<String> searchDeptIds,
			String searchCustomerId, String userId, String roleId, String customerKeyWord, List<String> searchUserIds) {
		// 点开的是分类
		// 查询已处理和待处理流程对应的客户id
		List<String> customerIds = flowEntService.queryFlowEntityId(roleId, userId, EntityType.CUSTOMER);
		if (customerIds == null || customerIds.isEmpty()) {
			return null;
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			if (customerIds.contains(searchCustomerId)) {
				customerIds.clear();
				customerIds.add(searchCustomerId);
			} else {
				// 查询的客户没有流程数据
				return null;
			}
		}

		// 查询客户信息
		SearchFilter customerFilter = new SearchFilter();
		customerFilter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, openCustomerTypeId));
		customerFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIds));
		// 部门筛选条件
		if (StringUtils.isNotBlank(openUserId)) {
			customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, openUserId));
		} else if (!CollectionUtils.isEmpty(searchUserIds)) {
			customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, searchUserIds));
		} else if (!CollectionUtils.isEmpty(searchDeptIds)) {
			customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchDeptIds));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			customerFilter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		List<Customer> customerList = new ArrayList<>();
		try {
			List<Customer> customers = queryAllBySearchFilter(customerFilter);
			if (customers != null) {
				customerList.addAll(customers);
				customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
			}
		} catch (ServiceException e) {
			logger.error("查询客户信息异常", e);
		}
		if (customerList.isEmpty()) {
			return null;
		}
		// 查询对应的流程信息（流程权限 没有流程 就是没有展示的信息）
		List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, userId);

		// 结果数据
		List<CustomerDeptResp> customerDeptResult = new ArrayList<>();
		if (StringUtil.isNotBlank(openCustomerTypeId)) {
			if (StringUtil.isBlank(openDeptId)) {
				// 最上层部门id为 权限的最开始点
				List<Department> rootDepartments = departmentService.getRootDept();
				if (rootDepartments == null) {
					// 没有根节点(数据错误的情况)
					return null;
				}
				for (Department department : rootDepartments) {
					CustomerDeptResp customerDeptResp = new CustomerDeptResp();
					customerDeptResp.setDeptId(department.getDeptid());
					customerDeptResp.setDeptName(department.getDeptname());
					customerDeptResp.setIsDept(1);
					int customerCount = 0;
					// 子部门
					List<String> subDeptids = departmentService.getSubDeptIds(department.getDeptid());
					subDeptids.add(department.getDeptid());
					if (!customerList.isEmpty()) {
						customerCount = (int) customerList.stream()
								.filter(customer -> subDeptids.contains(customer.getDeptId()) && customer.getCustomerTypeId().equals(openCustomerTypeId))
								.count();
					}
					if (customerCount == 0) {
						// 没有客户 就不用展示
						continue;
					}
					int flowCount = 0;
					if (flowCountList != null && !flowCountList.isEmpty()) {
						flowCountList.stream().filter(subFlowCount -> subDeptids.contains(subFlowCount.getDeptId())).mapToInt(SubFlowCount::getFlowCount).sum();
					}
					customerDeptResp.setCustomerCount(customerCount);
					customerDeptResp.setFlowCount(flowCount);
					customerDeptResult.add(customerDeptResp);
				}
			} else {
				// 最上层部门id为 权限的最开始点
				List<Department> childDepartments = departmentService.getDeptByFatherId(openDeptId, searchDeptIds);
				if (childDepartments != null && !childDepartments.isEmpty()) {
					for (Department department : childDepartments) {
						int customerCount = 0;
						// 子部门
						List<String> deptIds = new ArrayList<>();
						deptIds.add(department.getDeptid());
						List<String> subDeptIds = departmentService.getSubDeptIds(department.getDeptid());
						if (subDeptIds != null && !subDeptIds.isEmpty()) {
							deptIds.addAll(subDeptIds);
						}
						if (!customerList.isEmpty()) {
							customerCount = (int) customerList.stream()
									.filter(customer -> deptIds.contains(customer.getDeptId()) && customer.getCustomerTypeId().equals(openCustomerTypeId))
									.count();
						}
						if (customerCount == 0) {
							// 没有客户 就不用展示
							continue;
						}
						int flowCount = 0;
						if (flowCountList != null && !flowCountList.isEmpty()) {
							flowCount = flowCountList.stream().filter(subFlowCount -> deptIds.contains(subFlowCount.getDeptId()))
									.mapToInt(SubFlowCount::getFlowCount).sum();
						}
						CustomerDeptResp deptResp = new CustomerDeptResp();
						deptResp.setDeptId(department.getDeptid());
						deptResp.setDeptName(department.getDeptname());
						deptResp.setIsDept(1);
						deptResp.setCustomerCount(customerCount);
						deptResp.setFlowCount(flowCount);
						customerDeptResult.add(deptResp);
					}
				}
				if (StringUtils.isBlank(openUserId)) { // 查询销售
					try {
						SearchFilter searchFilter = new SearchFilter();
						searchFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, openDeptId));
						List<User> userList = userService.queryAllBySearchFilter(searchFilter);
						if (!CollectionUtils.isEmpty(userList)) {
							Map<String, String> cacheUserMap = userList.stream().collect(Collectors.toMap(User::getOssUserId, User::getRealName));
							Map<String, Integer> customerCountMap = new HashMap<>();
							customerList.forEach(customer -> {
								if (StringUtils.equals(openDeptId, customer.getDeptId())) {
									if (!customerCountMap.containsKey(customer.getCustomerId())) {
										customerCountMap.put(customer.getOssuserId(), 0);
									}
									customerCountMap.put(customer.getOssuserId(), customerCountMap.get(customer.getOssuserId()) + 1);
								}
							});
							customerCountMap.forEach((ossUserId, count) -> {
								CustomerDeptResp customerDept = new CustomerDeptResp();
								customerDept.setIsDept(2);
								customerDept.setDeptId(ossUserId);
								customerDept.setDeptName(cacheUserMap.get(ossUserId));
								customerDept.setCustomerCount(count);
								// 流程数量
								int flowCount = 0;
								if (flowCountList != null && !flowCountList.isEmpty()) {
									flowCount = flowCountList.stream().filter(subFlowCount -> StringUtils.equals(subFlowCount.getOssUserId(), ossUserId))
											.mapToInt(SubFlowCount::getFlowCount).sum();
								}
								customerDept.setFlowCount(flowCount);
								customerDeptResult.add(customerDept);
							});
						}
					} catch (ServiceException e) {
						logger.error("", e);
					}
				} else { // 查询客户
					// 查询客户
					customerDeptResult.addAll(customerList.stream().filter(customer -> {
						if (StringUtils.isNotBlank(openUserId)) {
							return StringUtils.equals(customer.getOssuserId(), openUserId)
									&& StringUtils.equals(customer.getCustomerTypeId(), openCustomerTypeId);
						} else {
							return StringUtils.equals(customer.getDeptId(), openDeptId) && StringUtils.equals(customer.getCustomerTypeId(), openCustomerTypeId);
						}
					}).map(customer -> {
						CustomerDeptResp customerDept = new CustomerDeptResp();
						customerDept.setIsDept(0);
						customerDept.setCompanyName(customer.getCompanyName());
						customerDept.setCustomerId(customer.getCustomerId());
						// 流程数量
						int flowCount = 0;
						if (flowCountList != null && !flowCountList.isEmpty()) {
							flowCount = flowCountList.stream().filter(subFlowCount -> subFlowCount.getSubId().equals(customer.getCustomerId()))
									.mapToInt(SubFlowCount::getFlowCount).sum();
						}
						customerDept.setFlowCount(flowCount);
						return customerDept;
					}).collect(Collectors.toList()));
				}

			}
			return customerDeptResult;
		} else {
			logger.error("未知展开情况");
			return null;
		}
	}

	/**
	 * 【自己】获取展开信息 （查询客户 不用管部门）
	 *
	 * @param customerTypeId
	 *            客户类型id
	 * @param searchDeptIds
	 *            部门Id集合（查询条件的部门id）
	 * @param searchCustomerId
	 *            客户id（查询条件）
	 * @param userId
	 *            登录用户id
	 * @param roleId
	 *            用户角色id（自身属性）
	 * @param customerKeyWord
	 *            客户关键词（查询条件）
	 * @return 客户部门信息
	 */
	private List<CustomerDeptResp> getOpenInfoBySelf(String customerTypeId, List<String> searchDeptIds, String searchCustomerId, String userId, String roleId,
			String customerKeyWord, List<String> searchUserIds) {
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);
		// 查询客户信息
		SearchFilter customerFilter = new SearchFilter();
		if (isPublicType) {
			customerFilter.getOrRules().add(new SearchRule[] { new SearchRule("ossuserId", Constants.ROP_EQ, userId),
					new SearchRule("ossuserId", Constants.ROP_EQ, ""), new SearchRule("ossuserId", Constants.ROP_EQ, null) });
		} else {
			customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, userId));
		}
		customerFilter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeId));
		// 增加筛选条件
		if (!CollectionUtils.isEmpty(searchUserIds)) {
			if (isPublicType) {
				customerFilter.getOrRules().add(new SearchRule[] { new SearchRule("ossuserId", Constants.ROP_IN, searchUserIds),
						new SearchRule("ossuserId", Constants.ROP_EQ, ""), new SearchRule("ossuserId", Constants.ROP_EQ, null) });
			} else {
				customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, searchUserIds));
			}
		} else if (CollectionUtils.isEmpty(searchUserIds) && !CollectionUtils.isEmpty(searchDeptIds)) {
			if (isPublicType) {
				customerFilter.getOrRules().add(new SearchRule[] { new SearchRule("deptId", Constants.ROP_IN, searchDeptIds),
						new SearchRule("deptId", Constants.ROP_EQ, ""), new SearchRule("deptId", Constants.ROP_EQ, null) });
			} else {
				customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, searchDeptIds));
			}
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			customerFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, searchCustomerId));
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			customerFilter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
		}
		// 客户信息
		List<Customer> customerList = new ArrayList<>();
		try {
			List<Customer> customers = queryAllBySearchFilter(customerFilter);
			if (customers != null) {
				customerList.addAll(customers);
			}
		} catch (ServiceException e) {
			logger.error("查询客户信息异常", e);
		}
		if (customerList.isEmpty()) {
			return null;
		}
		List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		// 查询对应的流程信息（流程权限 没有流程 就是没有展示的信息）
		List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, userId);
		return customerList.stream().map(customer -> {
			CustomerDeptResp customerDept = new CustomerDeptResp();
			customerDept.setIsDept(0);
			customerDept.setCompanyName(customer.getCompanyName());
			customerDept.setCustomerId(customer.getCustomerId());
			// 流程数量
			int flowCount = 0;
			if (flowCountList != null && !flowCountList.isEmpty()) {
				flowCount = flowCountList.stream().filter(subFlowCount -> subFlowCount.getSubId().equals(customer.getCustomerId()))
						.mapToInt(SubFlowCount::getFlowCount).sum();
			}
			customerDept.setFlowCount(flowCount);
			return customerDept;
		}).collect(Collectors.toList());
	}

	/**
	 * 【自定义】获取展开信息 （查询客户|部门信息）
	 *
	 * @param openCustomerTypeId
	 *            客户类型Id(展开条件 必定有)
	 * @param openDeptId
	 *            部门id（展开选项部门id）
	 * @param searchDeptIds
	 *            部门Id集合（查询条件的部门id）
	 * @param searchCustomerId
	 *            客户id（查询条件）
	 * @param roleId
	 *            用户角色id（自身属性）
	 * @param customerKeyWord
	 *            客户关键词（查询条件）
	 * @return 客户部门信息
	 */
	private List<CustomerDeptResp> getOpenInfoByCustomize(String openCustomerTypeId, String openDeptId, String openUserId, List<String> searchDeptIds,
			String searchCustomerId, String roleId, List<String> defDeptIds, String customerKeyWord, List<String> searchUserIds, String ossuserId) {
		boolean isPublicType = customerTypeService.validatePublicType(openCustomerTypeId);
		List<CustomerDeptResp> resultList = new ArrayList<>();
		if (StringUtil.isNotBlank(openCustomerTypeId)) {
			if (StringUtil.isBlank(openDeptId) && StringUtils.isBlank(openUserId) && !isPublicType) {
				// 过滤条件部门id
				if (searchDeptIds != null && !searchDeptIds.isEmpty()) {
					// 取交集
					defDeptIds.retainAll(searchDeptIds);
				}
				// 筛选过后没有部门
				if (defDeptIds.isEmpty()) {
					return null;
				}
				SearchFilter filter = new SearchFilter();
				if (!CollectionUtils.isEmpty(searchUserIds)) {
					if (!isPublicType) {
						filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, searchUserIds));
					}
				} else {
					if (!isPublicType) {
						filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, defDeptIds));
					}
				}
				filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, openCustomerTypeId));
				if (StringUtil.isNotBlank(searchCustomerId)) {
					// 公司名条件
					filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, searchCustomerId));
				}
				if (StringUtil.isNotBlank(customerKeyWord)) {
					filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
				}
				// 客户信息
				List<Customer> customerList = new ArrayList<>();
				try {
					List<Customer> customers = queryAllBySearchFilter(filter);
					if (customers != null) {
						customerList.addAll(customers);
					}
				} catch (ServiceException e) {
					logger.error("根据自定义权限 查询客户信息错误", e);
				}
				if (customerList.isEmpty()) {
					return null;
				}
				// 客户id
				List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				// 查询部门信息(符合条件的所有部门 需要找出第一级)
				SearchFilter deptFilter = new SearchFilter();
				deptFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, defDeptIds));
				List<Department> departments = null;
				try {
					departments = departmentService.queryAllBySearchFilter(deptFilter);
				} catch (ServiceException e) {
					logger.error("部门查询异常", e);
				}
				if (departments == null || departments.isEmpty()) {
					// 只有数据出错的情况下存在
					return null;
				}

				// 流程信息
				List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, ossuserId);
				// 找出第一级部门（有缺陷 有上下级部门 出现不连续勾选的时候会有错误 ）
				List<String> deptIds = departments.stream().map(Department::getDeptid).collect(Collectors.toList());
				departments = departments.stream().filter(department -> !deptIds.contains(department.getParentid())).collect(Collectors.toList());
				for (Department department : departments) {
					CustomerDeptResp deptResp = new CustomerDeptResp();
					deptResp.setDeptId(department.getDeptid());
					deptResp.setDeptName(department.getDeptname());
					deptResp.setIsDept(1);
					int customerCount = 0;

					List<String> thisDeptids = departmentService.getSubDeptIds(department.getDeptid());
					// 子部门
					List<String> subDeptids = new ArrayList<>();
					if (thisDeptids != null && !thisDeptids.isEmpty()) {
						subDeptids.addAll(thisDeptids);
					}
					subDeptids.add(department.getDeptid());
					if (!customerList.isEmpty()) {
						customerCount = (int) customerList.stream().filter(customer -> subDeptids.contains(customer.getDeptId())).count();
					}
					// 没有客户的部门不展示
					if (customerCount == 0) {
						continue;
					}
					int flowCount = 0;
					if (flowCountList != null && !flowCountList.isEmpty()) {
						flowCount = flowCountList.stream().filter(subFlowCount -> subDeptids.contains(subFlowCount.getDeptId()))
								.mapToInt(SubFlowCount::getFlowCount).sum();
					}
					deptResp.setCustomerCount(customerCount);
					deptResp.setFlowCount(flowCount);
					resultList.add(deptResp);
				}
				return resultList;
			} else {
				// 点开部门
				List<Department> childDepartments = null;
				if (StringUtils.isBlank(openUserId)) {
					childDepartments = departmentService.getDeptByFatherId(openDeptId, searchDeptIds);
				}
				if (childDepartments != null && !childDepartments.isEmpty()) {
					List<String> subDeptIds = departmentService.getSubDeptIds(openDeptId);
					// 定义部门中 展开部门的子部门 有效部门
					defDeptIds.retainAll(subDeptIds);
					if (searchDeptIds != null && !searchDeptIds.isEmpty()) {
						// 筛选部门
						defDeptIds.retainAll(searchDeptIds);
					}
					if (!defDeptIds.isEmpty()) {
						// 查询客户
						SearchFilter filter = new SearchFilter();
						if (!CollectionUtils.isEmpty(searchUserIds)) {
							filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, searchUserIds));
						} else {
							filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, defDeptIds));
						}
						filter.getRules().add(new SearchRule("customerTypeId", Constants.ROP_EQ, openCustomerTypeId));
						if (StringUtil.isNotBlank(searchCustomerId)) {
							// 公司名条件
							filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, searchCustomerId));
						}
						if (StringUtil.isNotBlank(customerKeyWord)) {
							filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, customerKeyWord));
						}
						// 客户信息
						List<Customer> customerList = new ArrayList<>();
						try {
							List<Customer> customers = queryAllBySearchFilter(filter);
							if (customers != null) {
								customerList.addAll(customers);
							}
						} catch (ServiceException e) {
							logger.error("根据自定义权限 查询客户信息错误", e);
						}
						List<SubFlowCount> flowCountList = new ArrayList<>();
						if (!customerList.isEmpty()) {
							// 查询流程信息
							List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
							List<SubFlowCount> flowCounts = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, ossuserId);
							if (flowCounts != null) {
								flowCountList.addAll(flowCounts);
							}
						}
						childDepartments = childDepartments.stream().filter(childDepartment -> subDeptIds.contains(childDepartment.getDeptid()))
								.collect(Collectors.toList());
						for (Department department : childDepartments) {
							CustomerDeptResp deptResp = new CustomerDeptResp();
							deptResp.setDeptId(department.getDeptid());
							deptResp.setDeptName(department.getDeptname());
							deptResp.setIsDept(1);
							int customerCount = 0;
							List<String> tDeptids = departmentService.getSubDeptIds(department.getDeptid());
							// 子部门
							List<String> subDeptids = new ArrayList<>();
							if (tDeptids != null && !tDeptids.isEmpty()) {
								subDeptids.addAll(tDeptids);
							}
							subDeptids.add(department.getDeptid());
							if (!customerList.isEmpty()) {
								customerCount = (int) customerList.stream().filter(customer -> subDeptids.contains(customer.getDeptId())).count();
							}
							// 没有客户的部门不展示
							if (customerCount == 0) {
								continue;
							}
							int flowCount = 0;
							if (!flowCountList.isEmpty()) {
								flowCount = flowCountList.stream().filter(subFlowCount -> subDeptids.contains(subFlowCount.getDeptId()))
										.mapToInt(SubFlowCount::getFlowCount).sum();
							}
							deptResp.setCustomerCount(customerCount);
							deptResp.setFlowCount(flowCount);
							resultList.add(deptResp);
						}
					}
				}

				// 展开的客户信息
				if (StringUtils.isBlank(openUserId) && !isPublicType) {
					// 点开的是部门且不是公共池客户类型，查询当前部门的所有销售
					List<Object[]> list = queryDeptUser(openDeptId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
					if (!CollectionUtils.isEmpty(list)) {
						List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(null, openDeptId, roleId, EntityType.CUSTOMER, ossuserId);
						resultList.addAll(list.stream().map(arr -> {
							CustomerDeptResp customerDept = new CustomerDeptResp();
							customerDept.setIsDept(2); // 销售
							customerDept.setDeptName((String) arr[2]);
							customerDept.setDeptId((String) arr[1]);
							// 流程数量
							int flowCount = 0;
							if (flowCountList != null && !flowCountList.isEmpty()) {
								flowCount = flowCountList.stream().filter(subFlowCount -> StringUtils.equals((String) arr[1], subFlowCount.getOssUserId()))
										.mapToInt(SubFlowCount::getFlowCount).sum();
							}
							customerDept.setCustomerCount(((Number) arr[0]).intValue());
							customerDept.setFlowCount(flowCount);
							return customerDept;
						}).collect(Collectors.toList()));
					}
					return resultList;
				} else {
					// 点开的是客户，或者公共池客户类型
					List<Customer> customerList = queryUserCustomer(openUserId, openCustomerTypeId, searchCustomerId, customerKeyWord, searchUserIds);
					if (customerList != null && !customerList.isEmpty()) {
						List<String> customerIds = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
						List<SubFlowCount> flowCountList = flowEntService.queryFlowCountBySub(customerIds, null, roleId, EntityType.CUSTOMER, ossuserId);
						resultList.addAll(customerList.stream().map(customer -> {
							CustomerDeptResp customerDept = new CustomerDeptResp();
							customerDept.setIsDept(0);
							customerDept.setCompanyName(customer.getCompanyName());
							customerDept.setCustomerId(customer.getCustomerId());
							// 流程数量
							int flowCount = 0;
							if (flowCountList != null && !flowCountList.isEmpty()) {
								flowCount = flowCountList.stream().filter(subFlowCount -> subFlowCount.getSubId().equals(customer.getCustomerId()))
										.mapToInt(SubFlowCount::getFlowCount).sum();
							}
							customerDept.setFlowCount(flowCount);
							return customerDept;
						}).collect(Collectors.toList()));
					}
					return resultList;
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 查询客户类型信息（类型名称、客户数、未处理流程数）
	 *
	 * @param onlineUser
	 *            客户信息
	 * @param searchDeptIds
	 *            部门id（查询条件）
	 * @param searchCustomerId
	 *            客户id （查询条件）
	 * @param customerKeyWord
	 *            客户关键词 （查询条件）
	 * @return 客户类型
	 */
	@Override
	public List<CustomerTypeRespDto> getCustomerType(OnlineUser onlineUser, String searchDeptIds, String searchCustomerId, String customerKeyWord,
			String searchUserIds) {
		logger.info("查询客户类型信息开始");
		long _start = System.currentTimeMillis();
		// 所有客户类型
		List<CustomerType> customerTypes = customerTypeService.findAllCustomerType();
		if (customerTypes == null || customerTypes.isEmpty()) {
			return null;
		}
		String userDeptId = onlineUser.getUser().getDeptId();
		String ossuserId = onlineUser.getUser().getOssUserId();
		String onlineRoleId = onlineUser.getRoleId();
		Role role = null;
		try {
			role = roleService.read(onlineRoleId);
		} catch (ServiceException e) {
			logger.error("查询用户登录角色异常", e);
		}
		if (role == null) {
			return null;
		}
		List<String> searchDeptId = null;
		if (StringUtil.isNotBlank(searchDeptIds)) {
			searchDeptId = Arrays.asList(searchDeptIds.split(","));
		}
		List<String> searchUserId = null;
		if (StringUtil.isNotBlank(searchUserIds)) {
			searchUserId = Arrays.asList(searchUserIds.split(","));
		}

		List<CustomerTypeRespDto> result = new ArrayList<>(customerTypes.size());
		// 组装客户类型
		for (CustomerType customerType : customerTypes) {
			String customerTypeId = customerType.getCustomerTypeId();
			Long flowCount = 0L;
			Integer customerCount = 0;
			List<CustomerRespDto> customers = queryCustomerCount(userDeptId, ossuserId, role, customerTypeId, searchDeptId, searchCustomerId, customerKeyWord,
					searchUserId);
			if (customers != null && !customers.isEmpty()) {
				for (CustomerRespDto customer : customers) {
					customerCount++;
					flowCount += customer.getFlowEntCount();
				}
			}
			result.add(new CustomerTypeRespDto(customerType, flowCount, customerCount));
			logger.info("客户类型：" + customerType.getCustomerTypeName() + "，查询到客户数：" + customerCount + "，待处理流程数：" + flowCount);
		}
		logger.info("查询客户类型信息结束，耗时：" + (System.currentTimeMillis() - _start));
		return result;
	}

	/**
	 * 统计客户类型的客户数和待处理流程数
	 *
	 * @param ossuserId
	 *            用户id
	 * @param role
	 *            登录角色
	 * @param customerTypeId
	 *            客户类型
	 * @param searchDeptIds
	 *            筛选部门
	 * @param searchCustomerId
	 *            筛选客户
	 * @param customerKeyWord
	 *            客户关键词
	 * @return 客户统计
	 */
	@Override
	public List<CustomerRespDto> queryCustomerCount(String userDeptId, String ossuserId, Role role, String customerTypeId, List<String> searchDeptIds,
			String searchCustomerId, String customerKeyWord, List<String> searchUserIds) {
		// 角色数据权限
		int dataPermission = role.getDataPermission();
		List<Customer> customerList = new ArrayList<>();

		// 判断是不是 公共池客户 类型
		boolean isPublicType = customerTypeService.validatePublicType(customerTypeId);

		if (DataPermission.All.ordinal() == dataPermission) {
			// 全部
			customerList = queryAllCustomerByType(searchDeptIds, searchCustomerId, customerTypeId, customerKeyWord, searchUserIds, isPublicType);
		} else if (DataPermission.Dept.ordinal() == dataPermission) {
			// 部门权限
			customerList = queryDeptCustomerByType(userDeptId, customerTypeId, searchDeptIds, searchCustomerId, customerKeyWord, searchUserIds, isPublicType);
		} else if (DataPermission.Flow.ordinal() == dataPermission) {
			// 流程权限
			customerList = queryFlowCustomerByType(role.getRoleid(), ossuserId, searchDeptIds, searchCustomerId, customerTypeId, customerKeyWord, searchUserIds,
					isPublicType);
		} else if (DataPermission.Self.ordinal() == dataPermission) {
			// 自己
			customerList = querySelfCustomerByType(customerTypeId, ossuserId, searchDeptIds, searchCustomerId, customerKeyWord, isPublicType);
		} else if (DataPermission.Customize.ordinal() == dataPermission) {
			// 自定义
			customerList = queryCustomizeCustomertByType(role.getDeptIds(), customerTypeId, searchDeptIds, searchCustomerId, customerKeyWord, searchUserIds,
					isPublicType);
		}
		// 统计流程数
		if ((DataPermission.Dept.ordinal() == dataPermission || DataPermission.Customize.ordinal() == dataPermission) && isPublicType) {
			// 存放用户部门权限下的所有部门
			List<String> subDeptIds = new ArrayList<>();
			subDeptIds.add(userDeptId);
			// 查询用户部门的子部门
			List<String> uSubDeptIds = departmentService.getSubDeptIds(userDeptId);
			if (!CollectionUtils.isEmpty(uSubDeptIds)) {
				subDeptIds.addAll(uSubDeptIds);
			}
			// 用户部门 和 部门过滤条件 取交集
			if (!CollectionUtils.isEmpty(searchDeptIds)) {
				subDeptIds.retainAll(searchDeptIds);
			}
			return countCustomerInfo(customerList, role.getRoleid(), ossuserId, subDeptIds);
		} else {
			return countCustomerInfo(customerList, role.getRoleid(), ossuserId, null);
		}
	}

	/**
	 * 全部权限 按条件查询客户关键信息
	 *
	 * @param searchDeptIds
	 *            过滤条件 部门id列表
	 * @param searchCustomerId
	 *            过滤条件 客户id
	 * @param customerTypeId
	 *            过滤条件 客户类型id
	 * @param customerKeyWord
	 *            过滤条件 关键词
	 * @param searchUserIds
	 *            过滤条件 用户id列表
	 * @return
	 */
	private List<Customer> queryAllCustomerByType(List<String> searchDeptIds, String searchCustomerId, String customerTypeId, String customerKeyWord,
			List<String> searchUserIds, boolean isPublicType) {
		logger.info("全部权限 按条件查客户关键信息开始，customerTypeId：" + customerTypeId);
		long _start = System.currentTimeMillis();
		if (StringUtil.isBlank(customerTypeId)) {
			return null;
		}
		// 参数
		Map<String, Object> param = new HashMap<>();
		StringBuilder hql = null;
		if (isPublicType) {
			hql = new StringBuilder("select c.customerId, c.customerTypeId, c.companyName, c.deptId from Customer c where c.customerTypeId = :customerTypeId ");
		} else {
			hql = new StringBuilder("select c.customerId,c.customerTypeId,c.companyName,c.deptId"
					+ " from Customer c inner join Department d on c.deptId = d.deptid" + " where c.customerTypeId = :customerTypeId and d.flag = 0 ");
		}
		param.put("customerTypeId", customerTypeId);
		if (!CollectionUtils.isEmpty(searchUserIds)) {
			if (!isPublicType) {
				hql.append(" and c.ossuserId in (:ossuserIds) ");
				param.put("ossuserIds", searchUserIds);
			}
		} else if (!CollectionUtils.isEmpty(searchDeptIds)) {
			if (!isPublicType) {
				hql.append(" and c.deptId in (:deptIds) ");
				param.put("deptIds", searchDeptIds);
			}
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and c.customerId = :customerId ");
			param.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and c.keyWords like :customerKeyWord ");
			param.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), param, 0);
			if (!CollectionUtils.isEmpty(results)) {
				logger.info("全部权限 按条件查客户关键信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return results.stream().map(result -> {
					Object[] rArr = (Object[]) result;
					Customer customer = new Customer();
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCustomerTypeId(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCompanyName(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("全部权限 按条件查客户关键信息异常", e);
		}
		return null;
	}

	/**
	 * 部门权限 按条件查客户关键信息
	 *
	 * @param userDeptId
	 *            用户部门
	 * @param customerTypeId
	 *            过滤条件 客户类型id
	 * @param searchDeptIds
	 *            过滤条件 部门id列表
	 * @param searchCustomerId
	 *            过滤条件 客户id
	 * @param customerKeyWord
	 *            过滤条件 关键词
	 * @param searchUserIds
	 *            过滤条件 用户id列表
	 * @return
	 */
	private List<Customer> queryDeptCustomerByType(String userDeptId, String customerTypeId, List<String> searchDeptIds, String searchCustomerId,
			String customerKeyWord, List<String> searchUserIds, boolean isPublicType) {
		logger.info("部门权限 按条件查客户关键信息开始，customerTypeId：" + customerTypeId);
		long _start = System.currentTimeMillis();
		if (StringUtil.isBlank(userDeptId)) {
			return null;
		}
		// 存放用户部门权限下的所有部门
		List<String> subDeptIds = new ArrayList<>();
		subDeptIds.add(userDeptId);
		// 查询用户部门的子部门
		List<String> uSubDeptIds = departmentService.getSubDeptIds(userDeptId);
		if (!CollectionUtils.isEmpty(uSubDeptIds)) {
			subDeptIds.addAll(uSubDeptIds);
		}
		// 用户部门 和 部门过滤条件 取交集
		if (!CollectionUtils.isEmpty(searchDeptIds)) {
			subDeptIds.retainAll(searchDeptIds);
		}
		if (subDeptIds.isEmpty()) {
			return null;
		}
		// 参数
		Map<String, Object> param = new HashMap<>();
		StringBuilder hql = new StringBuilder(
				"select customerId,customerTypeId,companyName, deptId" + " from Customer where customerTypeId = :customerTypeId ");
		param.put("customerTypeId", customerTypeId);
		// 用户过滤条件优先
		if (!CollectionUtils.isEmpty(searchUserIds)) {
			if (!isPublicType) {
				hql.append(" and ossuserId in (:ossuserIds) ");
				param.put("ossuserIds", searchUserIds);
			}
		} else if (!subDeptIds.isEmpty()) {
			if (!isPublicType) {
				hql.append(" and deptId in (:deptIds) ");
				param.put("deptIds", subDeptIds);
			}
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and customerId = :customerId ");
			param.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			param.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), param, 0);
			if (!CollectionUtils.isEmpty(results)) {
				logger.info("部门权限 按条件查客户关键信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return results.stream().map(result -> {
					Object[] rArr = (Object[]) result;
					Customer customer = new Customer();
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCustomerTypeId(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCompanyName(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("部门权限 按条件查客户关键信息异常", e);
		}
		return null;
	}

	/**
	 * 流程权限 按条件查客户关键信息
	 *
	 * @param roleId
	 *            用户当前角色
	 * @param ossuserId
	 *            当前用户
	 * @param searchDeptIds
	 *            过滤条件 部门id列表
	 * @param searchCustomerId
	 *            过滤条件 客户id
	 * @param customerTypeId
	 *            过滤条件 客户类型id
	 * @param customerKeyWord
	 *            过滤条件 关键词
	 * @param searchUserIds
	 *            过滤条件 用户id列表
	 * @return
	 */
	private List<Customer> queryFlowCustomerByType(String roleId, String ossuserId, List<String> searchDeptIds, String searchCustomerId, String customerTypeId,
			String customerKeyWord, List<String> searchUserIds, boolean isPublicType) {
		logger.info("流程权限 按条件查客户关键信息开始，customerTypeId：" + customerTypeId);
		long _start = System.currentTimeMillis();
		// 查待处理和已处理流程所属的客户id
		List<String> customerIds = flowEntService.queryFlowEntityId(roleId, ossuserId, EntityType.CUSTOMER);
		if (CollectionUtils.isEmpty(customerIds) || isPublicType) {
			return null;
		}
		// 客户id条件
		if (StringUtil.isNotBlank(searchCustomerId)) {
			if (!customerIds.contains(searchCustomerId)) {
				return null;
			} else {
				customerIds.clear();
				customerIds.add(searchCustomerId);
			}
		}

		// 参数
		Map<String, Object> param = new HashMap<>();
		StringBuilder hql = new StringBuilder("select customerId, customerTypeId, companyName , deptId");
		hql.append(" from Customer where customerTypeId = :customerTypeId and  customerId in(:customerIds) ");
		param.put("customerTypeId", customerTypeId);
		param.put("customerIds", customerIds);

		if (!CollectionUtils.isEmpty(searchUserIds)) {
			if (!isPublicType) {
				hql.append(" and c.ossuserId in (:ossuserIds) ");
				param.put("ossuserIds", searchUserIds);
			}
		} else if (!CollectionUtils.isEmpty(searchDeptIds)) {
			if (!isPublicType) {
				hql.append(" and c.deptId in (:deptIds) ");
				param.put("deptIds", searchDeptIds);
			}
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			param.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), param, 0);
			if (!CollectionUtils.isEmpty(results)) {
				logger.info("流程权限 按条件查客户关键信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return results.stream().map(result -> {
					Object[] rArr = (Object[]) result;
					Customer customer = new Customer();
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCustomerTypeId(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCompanyName(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("流程权限 按条件查客户关键信息异常", e);
		}
		return null;
	}

	/**
	 * 自己权限 按条件查客户关键信息
	 *
	 * @param customerTypeId
	 *            过滤条件 客户类型id
	 * @param ossuserId
	 *            当前用户
	 * @param searchDeptIds
	 *            过滤条件 部门id列表
	 * @param searchCustomerId
	 *            过滤条件 客户id
	 * @param customerKeyWord
	 *            过滤条件 关键词
	 * @return
	 */
	private List<Customer> querySelfCustomerByType(String customerTypeId, String ossuserId, List<String> searchDeptIds, String searchCustomerId,
			String customerKeyWord, boolean isPublicType) {
		logger.info("自己权限 按条件查客户关键信息开始，customerTypeId：" + customerTypeId);
		long _start = System.currentTimeMillis();
		// 参数
		Map<String, Object> param = new HashMap<>();
		StringBuilder hql = new StringBuilder("select customerId, customerTypeId, companyName, deptId");
		if (isPublicType) {
			hql.append(" from Customer where customerTypeId = :customerTypeId and (ossuserId= :ossuserId or ossuserId = '' or ossuserId is null) ");
		} else {
			hql.append(" from Customer where customerTypeId = :customerTypeId and  ossuserId= :ossuserId ");
		}
		param.put("customerTypeId", customerTypeId);
		param.put("ossuserId", ossuserId);

		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and customerId = :customerId ");
			param.put("customerId", searchCustomerId);
		}
		if (!CollectionUtils.isEmpty(searchDeptIds)) {
			if (isPublicType) {
				hql.append(" and (deptId in (:searchDeptIds) or deptId = '' or deptId is null) ");
			} else {
				hql.append(" and deptId in (:searchDeptIds) ");
			}
			param.put("searchDeptIds", searchDeptIds);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			param.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), param, 0);
			if (!CollectionUtils.isEmpty(results)) {
				logger.info("自己权限 按条件查客户关键信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return results.stream().map(result -> {
					Object[] rArr = (Object[]) result;
					Customer customer = new Customer();
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCustomerTypeId(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCompanyName(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("自己权限 按条件查客户关键信息异常", e);
		}
		return null;
	}

	/**
	 * 自定义权限 按条件查客户关键信息开始
	 *
	 * @param userDeptIds
	 *            自定义部门id 逗号分隔
	 * @param customerTypeId
	 *            过滤条件 客户类型id
	 * @param searchDeptIds
	 *            过滤条件 部门id列表
	 * @param searchCustomerId
	 *            过滤条件 客户id
	 * @param customerKeyWord
	 *            过滤条件 关键词
	 * @param searchUserIds
	 *            过滤条件 用户id列表
	 * @return
	 */
	private List<Customer> queryCustomizeCustomertByType(String userDeptIds, String customerTypeId, List<String> searchDeptIds, String searchCustomerId,
			String customerKeyWord, List<String> searchUserIds, boolean isPublicType) {
		logger.info("自定义权限 按条件查客户关键信息开始，customerTypeId：" + customerTypeId);
		long _start = System.currentTimeMillis();
		// 自定义权限选中的部门为空
		if (StringUtil.isBlank(userDeptIds)) {
			return null;
		}
		// 自定义权限选中的部门id列表
		List<String> subDeptIds = new ArrayList<>(Arrays.asList(userDeptIds.split(",")));
		if (subDeptIds.isEmpty()) {
			return null;
		}
		// 自定义部门 和 部门过滤条件 取交集
		if (!CollectionUtils.isEmpty(searchDeptIds)) {
			subDeptIds.retainAll(searchDeptIds);
		}
		if (subDeptIds.isEmpty()) {
			return null;
		}
		// 参数
		Map<String, Object> param = new HashMap<>();
		StringBuilder hql = new StringBuilder("select customerId, customerTypeId, companyName , deptId");
		hql.append(" from Customer where customerTypeId = :customerTypeId");
		param.put("customerTypeId", customerTypeId);
		if (!CollectionUtils.isEmpty(searchUserIds)) {
			if (!isPublicType) {
				hql.append(" and ossuserId in (:ossuserIds) ");
				param.put("ossuserIds", searchUserIds);
			}
		} else if (!CollectionUtils.isEmpty(subDeptIds)) {
			if (!isPublicType) {
				hql.append(" and deptId in (:deptIds) ");
				param.put("deptIds", subDeptIds);
			}
		}
		if (StringUtil.isNotBlank(searchCustomerId)) {
			hql.append(" and customerId = :customerId ");
			param.put("customerId", searchCustomerId);
		}
		if (StringUtil.isNotBlank(customerKeyWord)) {
			hql.append(" and keyWords like :customerKeyWord ");
			param.put("customerKeyWord", "%" + customerKeyWord + "%");
		}
		try {
			List<Object> results = baseDao.findByhql(hql.toString(), param, 0);
			if (!CollectionUtils.isEmpty(results)) {
				logger.info("自定义权限 按条件查客户关键信息结束，耗时：" + (System.currentTimeMillis() - _start));
				return results.stream().map(result -> {
					Object[] rArr = (Object[]) result;
					Customer customer = new Customer();
					customer.setCustomerId(rArr[0] == null ? null : String.valueOf(rArr[0]));
					customer.setCustomerTypeId(rArr[1] == null ? null : String.valueOf(rArr[1]));
					customer.setCompanyName(rArr[2] == null ? null : String.valueOf(rArr[2]));
					customer.setDeptId(rArr[3] == null ? null : String.valueOf(rArr[3]));
					return customer;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("自定义权限 按条件查客户关键信息异常", e);
		}
		return null;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void changeGrade(Customer customer, User user, CustomerType from, CustomerType to, String depict, boolean clearSale) throws ServiceException {
		// 写变更记录
		CustomerChangeRecord customerChangeRecord = new CustomerChangeRecord();
		customerChangeRecord.setOssUserId(user.getOssUserId());
		customerChangeRecord.setDeptId(user.getDeptId());
		customerChangeRecord.setDepict(depict);
		customerChangeRecord.setChangeTime(new Timestamp(System.currentTimeMillis()));
		customerChangeRecord.setCustomerId(customer.getCustomerId());
		customerChangeRecord.setCompanyName(customer.getCompanyName());
		customerChangeRecord.setOriginCustomerType(from.getCustomerTypeValue());
		customerChangeRecord.setNowCustomerType(to.getCustomerTypeValue());
		if (from.getCustomerTypeValue() > to.getCustomerTypeValue()) {
			// 值越小 级别越高
			customerChangeRecord.setChangeType(CustomerChangeType.UPGRADE.ordinal());
		} else {
			customerChangeRecord.setChangeType(CustomerChangeType.DOWNGRADE.ordinal());
		}
		try {
			boolean saveResult = customerChangeRecordService.save(customerChangeRecord);
			// 变更客户类型
			if (saveResult) {
				customer.setCustomerTypeId(to.getCustomerTypeId());
				if (clearSale) {
					customer.setDeptId("");
					customer.setOssuserId("");
					String updateProduct = "update erp_customer_product set ossuserid = '' where customerid = ?";
					try {
						baseDao.executeSqlUpdte(updateProduct, new Object[] { customer.getCustomerId() });
					} catch (Exception e) {
						logger.error("变更客户为公共池客户时，清空产品的ossUserId异常");
					}
				}
				if (!update(customer)) {
					throw new ServiceException("更新客户级别失败");
				}
			}
		} catch (ServiceException e) {
			logger.error("改变用户等级异常", e);
			throw new ServiceException(e.getMessage(), e);
		}
	}

	/**
	 * 查找客户处于这种状态的时间
	 *
	 * @param customers
	 *            客户
	 * @return 客户状态时间
	 */
	@Override
	public Map<String, Timestamp> findCustomerInThisStateTime(List<Customer> customers) {
		Map<String, Timestamp> stateTimeInfo = new HashMap<>();
		if (customers == null || customers.isEmpty()) {
			return stateTimeInfo;
		}
		// 获取 状态改变的时间
		SearchFilter changeFilter = new SearchFilter();
		List<String> customerIds = customers.stream().map(Customer::getCustomerId).distinct().collect(Collectors.toList());
		changeFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIds));
		List<CustomerChangeRecord> changeRecordList = null;
		try {
			// 客户类型变更记录 ，只有客户类型变更才会写
			changeRecordList = customerChangeRecordService.queryAllBySearchFilter(changeFilter);
		} catch (ServiceException e) {
			logger.error("查询客户的最后变更记录异常", e);
		}
		Map<String, Timestamp> lastChangeTime = new HashMap<>();
		if (changeRecordList != null && !changeRecordList.isEmpty()) {
			lastChangeTime = changeRecordList.stream().collect(
					Collectors.toMap(CustomerChangeRecord::getCustomerId, CustomerChangeRecord::getChangeTime, (o, n) -> (o.getTime() > n.getTime()) ? o : n));
		}
		for (Customer customer : customers) {
			Timestamp lastTime = lastChangeTime.get(customer.getCustomerId());
			if (lastTime == null) {
				lastTime = customer.getWtime();
			}
			stateTimeInfo.put(customer.getCustomerId(), lastTime);
		}
		return stateTimeInfo;
	}

	@Override
	public Map<String, String> queryCustomerName(List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return new HashMap<>();
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, new ArrayList<>(new HashSet<>(ids))));
		// 部门信息
		List<Customer> customers = null;
		try {
			customers = this.queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询客户信息错误", e);
		}
		if (customers == null || customers.isEmpty()) {
			return new HashMap<>();
		}
		return customers.stream().collect(Collectors.toMap(Customer::getCustomerId, Customer::getCompanyName, (o, n) -> n));
	}

	/**
	 * 查询用户的客户信息
	 *
	 * @param user        登录用户
	 * @param companyName 公司名
	 * @param pageSize    一页大小
	 * @param currentPage 当前页
	 * @param onlyPublic  只查询公共池客户
	 * @param noPublic 不查询公共客户
	 * @return 客户信息
	 */
	@Override
	public PageResult<CustomerRespDto> queryUserCustomer(OnlineUser user, String companyName, boolean onlyPublic,
														 boolean noPublic,Integer pageSize, Integer currentPage) {
		try {
			String roleId = user.getRoleId();
			Role role = roleService.read(roleId);
			int permission = role.getDataPermission();
			SearchFilter customerFilter = new SearchFilter();
			String userId = user.getUser().getOssUserId();
			String userDeptId = user.getUser().getDeptId();
			if (onlyPublic) {
				// 只查询公共客户
				customerFilter.getOrRules()
						.add(new SearchRule[] { new SearchRule("ossuserId", Constants.ROP_EQ, null), new SearchRule("ossuserId", Constants.ROP_EQ, "") });
			} else {
				if (DataPermission.Self.ordinal() == permission) {
					// 看自己的和公共池客户
					if (noPublic) {
						customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, userId));
					} else {
						customerFilter.getOrRules().add(new SearchRule[]{
								new SearchRule("ossuserId", Constants.ROP_EQ, userId),
								new SearchRule("ossuserId", Constants.ROP_EQ, null),
								new SearchRule("ossuserId", Constants.ROP_EQ, "")
						});
					}
				} else if (DataPermission.Customize.ordinal() == permission) {
					Set<String> deptIdSet = new HashSet<>();
					if (StringUtil.isNotBlank(userDeptId)) {
						deptIdSet.add(userDeptId);
					}
					String deptIds = role.getDeptIds();
					if (StringUtil.isNotBlank(deptIds)) {
						deptIdSet.addAll(Arrays.asList(deptIds.split(",")));
					}
					// 可以查看对应部门的和公共池客户
					if (noPublic) {
						customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, new ArrayList<>(deptIdSet)));
					} else {
						customerFilter.getOrRules().add(new SearchRule[]{
								new SearchRule("deptId", Constants.ROP_IN, new ArrayList<>(deptIdSet)),
								new SearchRule("ossuserId", Constants.ROP_EQ, null),
								new SearchRule("ossuserId", Constants.ROP_EQ, "")
						});
					}
				} else if (DataPermission.Dept.ordinal() == permission) {
					// 可以查看自己应部门的和公共池客户
					if (noPublic) {
						customerFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, userDeptId));
					} else {
						customerFilter.getOrRules().add(new SearchRule[]{
								new SearchRule("deptId", Constants.ROP_EQ, userDeptId),
								new SearchRule("ossuserId", Constants.ROP_EQ, null),
								new SearchRule("ossuserId", Constants.ROP_EQ, "")
						});
					}
				} else if (DataPermission.Flow.ordinal() == permission) {
					// 查询可见流程
					Set<String> flowEntityIds = flowEntService.queryEntityIdFromFlowEnt(roleId, userId, EntityType.SUPPLIER.getCode());
					if (flowEntityIds != null && !flowEntityIds.isEmpty()) {
						// 可以查看对应部门的和自己的供应商和公共池客户
						if (noPublic) {
							customerFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, new ArrayList<>(flowEntityIds)));
						} else {
							customerFilter.getOrRules().add(new SearchRule[]{
									new SearchRule("customerId", Constants.ROP_IN, new ArrayList<>(flowEntityIds)),
									new SearchRule("ossuserId", Constants.ROP_EQ, null),
									new SearchRule("ossuserId", Constants.ROP_EQ, "")
							});
						}
					} else {
						return PageResult.empty("暂无数据");
					}
				} else if (DataPermission.All.ordinal() != permission) {
					return PageResult.empty("角色无权限");
				}
			}

			if (StringUtil.isNotBlank(companyName)) {
				customerFilter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
			}
			customerFilter.getOrders().add(new SearchOrder("customerTypeId", Constants.ROP_ASC));
			PageResult<Customer> customerPageResult = this.queryByPages(pageSize, currentPage, customerFilter);
			List<Customer> customerList = customerPageResult.getData();
			if (customerList == null || customerList.isEmpty()) {
				return PageResult.empty("暂无数据");
			}

			List<CustomerType> customerTypeList = customerTypeService.queryAllBySearchFilter(null);
			Map<String, String> customerTypeMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(customerTypeList)) {
				customerTypeMap = customerTypeList.stream().collect(Collectors.toMap(CustomerType::getCustomerTypeId, CustomerType::getCustomerTypeName));
			}

			// 客户信息
			List<CustomerRespDto> customerDtoList = new ArrayList<>();
			for (Customer customer : customerList) {
				CustomerRespDto customerDto = new CustomerRespDto();
				customerDto.setCompanyName(customer.getCompanyName());
				customerDto.setCustomerId(customer.getCustomerId());
				customerDto.setCustomerTypeId(customer.getCustomerTypeId());
				customerDto.setCustomerTypeName(customerTypeMap.getOrDefault(customer.getCustomerTypeId(), ""));
				customerDtoList.add(customerDto);
			}
			PageResult<CustomerRespDto> pageResult = new PageResult<>();
			BeanUtils.copyProperties(customerPageResult, pageResult);
			long count = customerPageResult.getCount();
			long totalPage = count / pageSize;
			if (count % pageSize != 0) {
				totalPage += 1;
			}
			pageResult.setTotalPages((int) totalPage);
			pageResult.setData(customerDtoList);
			return pageResult;
		} catch (Exception e) {
			logger.error("分页查询客户信息异常", e);
		}
		return PageResult.empty("查询错误");
	}


	/**
	 * 根据客户端的详情
	 *
	 * @param customerId 客户ID
	 * @return 客户详情
	 */
	@Override
	public CustomerAllDto queryCustomerDetailById(String customerId) {
		if (StringUtil.isBlank(customerId)) {
			return null;
		}
		try {
			Customer customer = customerDao.read(customerId);
			if (customer == null) {
				return null;
			}
			String userId = customer.getOssuserId();
			CustomerAllDto customerAllDto = new CustomerAllDto(customer);
			if (StringUtil.isNotBlank(userId)) {
				User user = userService.read(userId);
				customerAllDto.getCustomerDetail().setCreatUser(user.getRealName());
			}
			// 查询开票信息
			SearchFilter invoiceFilter = new SearchFilter();
			invoiceFilter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, customer.getCustomerId()));
			invoiceFilter.getOrders().add(new SearchOrder("wtime",Constants.ROP_ASC));
			List<InvoiceInformation> invoiceInformationList = invoiceInformationService.queryAllBySearchFilter(invoiceFilter);
			customerAllDto.setInvoices(invoiceInformationList);

			// 查询客户银行信息
			SearchFilter banckAccountFilter = new SearchFilter();
			banckAccountFilter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, customer.getCustomerId()));
			banckAccountFilter.getOrders().add(new SearchOrder("wtime",Constants.ROP_ASC));
			List<BankAccount> bankAccountList = bankAccountService.queryAllBySearchFilter(banckAccountFilter);
			customerAllDto.setBankAccounts(bankAccountList);

			// 客户联系部门信息
			SearchFilter contactFilter = new SearchFilter();
			contactFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, customer.getCustomerId()));
			contactFilter.getOrders().add(new SearchOrder("wtime",Constants.ROP_ASC));
			List<SupplierContacts> contactsList = supplierContactsService.queryAllBySearchFilter(contactFilter);
			customerAllDto.setContacts(contactsList);
			// 联系日志
			SearchFilter contactLogFilter = new SearchFilter();
			contactLogFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, customer.getCustomerId()));
			contactLogFilter.getOrders().add(new SearchOrder("wtime",Constants.ROP_ASC));
			List<SupplierContactLog> contactLogList = supplierContactLogService.queryAllBySearchFilter(contactLogFilter);
			customerAllDto.setContactLogs(contactLogList);
			return customerAllDto;
		} catch (Exception e) {
			logger.error("根据ID查询客户信息错误", e);
		}
		return null;
	}
}
