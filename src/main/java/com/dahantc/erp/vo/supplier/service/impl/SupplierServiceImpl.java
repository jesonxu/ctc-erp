package com.dahantc.erp.vo.supplier.service.impl;

import java.io.Console;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;
import com.dahantc.erp.vo.user.entity.User;
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
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.dao.IBankAccountDao;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccountHistor.entity.BankAccountHistory;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.invoice.dao.IInvoiceInformationDao;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoiceHistory.entity.InvoiceInformationHistory;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.dao.ISupplierDao;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;

@Service("supplierService")
public class SupplierServiceImpl implements ISupplierService {
	private static Logger logger = LogManager.getLogger(SupplierServiceImpl.class);

	@Autowired
	private ISupplierDao SupplierDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IInvoiceInformationDao invoiceInformationDao;

	@Autowired
	private IBankAccountDao bankAccountDao;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private ISupplierTypeService supplierTypeService;

	@Override
	public Supplier read(Serializable id) throws ServiceException {
		try {
			return SupplierDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商信息表失败", e);
			throw new ServiceException("读取供应商信息表失败", e);
		}
	}

	@Override
	public boolean save(Supplier entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos) throws ServiceException {
		try {
			// 生成关键词
			entity.buildKeyWords();
			boolean result = SupplierDao.save(entity);
			if (result) {
				String supplierId = entity.getSupplierId();
				if (!ListUtils.isEmpty(invoiceInfos)) {
					invoiceInfos = invoiceInfos.stream().map(info -> {
						info.setBasicsId(supplierId);
						info.setInvoiceType(InvoiceType.OtherInvoice.ordinal());
						return info;
					}).collect(Collectors.toList());
					result = baseDao.saveByBatch(invoiceInfos);
					logger.info("保存开票信息" + (result ? "成功" : "失败") + "，supplierId：" + entity.getSupplierId());
				}
				if (!ListUtils.isEmpty(bankInfos)) {
					bankInfos = bankInfos.stream().map(info -> {
						info.setBasicsId(supplierId);
						info.setInvoiceType(InvoiceType.OtherBank.ordinal());
						return info;
					}).collect(Collectors.toList());
					result = baseDao.saveByBatch(bankInfos);
					logger.info("保存银行信息" + (result ? "成功" : "失败") + "，supplierId：" + entity.getSupplierId());
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("保存供应商信息表失败", e);
			throw new ServiceException("保存供应商信息表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return SupplierDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商信息表失败", e);
			throw new ServiceException("删除供应商信息表失败", e);
		}
	}

	@Override
	public boolean update(Supplier entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos, String delInvoiceIds, String delBankIds)
			throws ServiceException {
		try {
			// 生成关键词
			entity.buildKeyWords();
			// 更新供应商信息
			boolean result = SupplierDao.update(entity);
			String supplierId = entity.getSupplierId();
			List<InvoiceInformation> saveInvoices = new ArrayList<>(); // 新建的开票信息
			List<InvoiceInformation> updateInvoices = new ArrayList<>(); // 修改过的开票信息
			List<BankAccount> saveBanks = new ArrayList<>(); // 新建的银行信息
			List<BankAccount> updateBanks = new ArrayList<>(); // 修改过的银行信息
			List<BankAccountHistory> bankHisList = new ArrayList<>();
			List<InvoiceInformationHistory> invoiceHisList = new ArrayList<>();
			List<InvoiceInformation> delInvoices = new ArrayList<>();
			List<BankAccount> delBanks = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, supplierId));
			// 遍历提交的开票信息
			invoiceInfos = ListUtils.isEmpty(invoiceInfos) ? new ArrayList<>() : invoiceInfos;
			for (InvoiceInformation invoice : invoiceInfos) {
				if (StringUtils.isNotBlank(invoice.getInvoiceId())) { // 是在之前的记录上修改
					InvoiceInformation inv = invoiceInformationDao.read(invoice.getInvoiceId());
					InvoiceInformationHistory invoiceHis = new InvoiceInformationHistory();
					BeanUtils.copyProperties(inv, invoiceHis);
					invoiceHisList.add(invoiceHis);
					inv.setAccountBank(invoice.getAccountBank());
					inv.setBankAccount(invoice.getBankAccount());
					inv.setCompanyAddress(invoice.getCompanyAddress());
					inv.setCompanyName(invoice.getCompanyName());
					inv.setPhone(invoice.getPhone());
					inv.setTaxNumber(invoice.getTaxNumber());
					updateInvoices.add(inv);
				} else { // 是新建的
					invoice.setBasicsId(supplierId);
					invoice.setInvoiceType(InvoiceType.OtherInvoice.ordinal());
					saveInvoices.add(invoice);
				}
			}
			// 删除
			if (StringUtils.isNotBlank(delInvoiceIds)) {
				String[] invoiceIds = delInvoiceIds.split(",");
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("invoiceId", Constants.ROP_IN, invoiceIds));
				delInvoices = invoiceInformationDao.queryAllBySearchFilter(filter);
				for (InvoiceInformation delIn : delInvoices) {
					InvoiceInformationHistory invoiceHis = new InvoiceInformationHistory();
					BeanUtils.copyProperties(delIn, invoiceHis);
					invoiceHisList.add(invoiceHis);
				}
			}
			// 遍历提交的银行信息
			bankInfos = ListUtils.isEmpty(bankInfos) ? new ArrayList<>() : bankInfos;
			for (BankAccount bank : bankInfos) {
				if (StringUtils.isNotBlank(bank.getBankAccountId())) { // 是在之前的记录上修改
					BankAccount bk = bankAccountDao.read(bank.getBankAccountId());
					BankAccountHistory bankHis = new BankAccountHistory();
					BeanUtils.copyProperties(bk, bankHis);
					bankHisList.add(bankHis);
					bk.setAccountBank(bank.getAccountBank());
					bk.setBankAccount(bank.getBankAccount());
					bk.setAccountName(bank.getAccountName());
					updateBanks.add(bk);
				} else { // 是新建的
					bank.setBasicsId(supplierId);
					bank.setInvoiceType(InvoiceType.OtherBank.ordinal());
					saveBanks.add(bank);
				}
			}
			// 删除
			if (StringUtils.isNotBlank(delBankIds)) {
				String[] bankIds = delBankIds.split(",");
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("bankAccountId", Constants.ROP_IN, bankIds));
				delBanks = bankAccountDao.queryAllBySearchFilter(filter);
				for (BankAccount delBank : delBanks) {
					BankAccountHistory bankHis = new BankAccountHistory();
					BeanUtils.copyProperties(delBank, bankHis);
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
			logger.error("更新供应商信息表失败", e);
			throw new ServiceException("更新供应商信息表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return SupplierDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商信息表数量失败", e);
			throw new ServiceException("查询供应商信息表数量失败", e);
		}
	}

	@Override
	public PageResult<Supplier> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return SupplierDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商信息表分页信息失败", e);
			throw new ServiceException("查询供应商信息表分页信息失败", e);
		}
	}

	@Override
	public List<Supplier> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return SupplierDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商信息表失败", e);
			throw new ServiceException("查询供应商信息表失败", e);
		}
	}

	@Override
	public List<Supplier> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return SupplierDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商信息表失败", e);
			throw new ServiceException("查询供应商信息表失败", e);
		}
	}

	@Override
	public List<Supplier> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return SupplierDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商信息表失败", e);
			throw new ServiceException("查询供应商信息表失败", e);
		}
	}

	/**
	 * 读取当前登录用户的供应商信息（根据权限获取）
	 *
	 * @param onlineUser
	 *            在线用户
	 * @param deptIds
	 *            部门Ids
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            关键词
	 * @return List<CustomerRespDto>
	 */
	@Override
	public List<SupplierRspDto> querySuppliers(OnlineUser onlineUser, String deptIds, String supplierId, String supplierTypeId, String keyWord,
			int searchType) {
		// 用户角色
		String roleId = onlineUser.getRoleId();
		// 用户id
		String ossuserId = onlineUser.getUser().getOssUserId();
		Role role = null;
		try {
			role = roleService.read(roleId);
		} catch (ServiceException e) {
			logger.error("查询角色信息错误：", e);
		}
		if (role == null) {
			return null;
		}
		List<Supplier> supplierList = readSuppliers(onlineUser, deptIds, supplierId, supplierTypeId, keyWord, searchType);
		// 统计流程数
		return countSupplier(supplierList, roleId, ossuserId);
	}

	/**
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param deptIds
	 *            部门Ids
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            关键词
	 * @return
	 */
	@Override
	public List<Supplier> readSuppliers(OnlineUser onlineUser, String deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType) {
		// 用户角色
		String roleId = onlineUser.getRoleId();
		return readSuppliersByRole(onlineUser, roleId, deptIds, supplierId, supplierTypeId, keyWord, searchType, null);
	}

	@Override
	public List<Supplier> readSuppliers(OnlineUser onlineUser, String deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType,
			String companyName) {
		// 用户角色
		String roleId = onlineUser.getRoleId();
		return readSuppliersByRole(onlineUser, roleId, deptIds, supplierId, supplierTypeId, keyWord, searchType, companyName);
	}

	/**
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param roleId
	 *            角色Id
	 * @param deptIds
	 *            部门Ids
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            关键词
	 * @param companyName
	 *            公司名称
	 * @return
	 */
	@Override
	public List<Supplier> readSuppliersByRole(OnlineUser onlineUser, String roleId, String deptIds, String supplierId, String supplierTypeId, String keyWord,
			int searchType, String companyName) {
		// 用户
		User user = onlineUser.getUser();
		Role role = null;
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
		List<Supplier> supplierList = null;
		if (DataPermission.All.ordinal() == dataPermission) {
			// 全部
			supplierList = queryAllSupplier(deptIdList, supplierId, supplierTypeId, keyWord, searchType, companyName);
		} else if (DataPermission.Dept.ordinal() == dataPermission) {
			// 部门权限
			supplierList = querySupplierByDept(user.getDeptId(), deptIdList, supplierId, supplierTypeId, keyWord, searchType, companyName);
		} else if (DataPermission.Flow.ordinal() == dataPermission) {
			// 流程权限
			supplierList = querySupplierByFlow(roleId, user.getOssUserId(), deptIdList, supplierId, supplierTypeId, keyWord, searchType, companyName);
		} else if (DataPermission.Self.ordinal() == dataPermission) {
			// 自己
			supplierList = querySelfSupplier(user.getOssUserId(), deptIdList, supplierId, supplierTypeId, keyWord, searchType, companyName);
		} else if (DataPermission.Customize.ordinal() == dataPermission) {
			// 自定义
			supplierList = queryCustomizeSupplier(role.getDeptIds(), deptIdList, supplierId, supplierTypeId, keyWord, searchType, companyName);
		}
		return supplierList;
	}

	/**
	 * 查询所有的供应商信息
	 *
	 * @param deptIds
	 *            部门
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            关键词
	 * @param companyName
	 *            公司名称
	 * @return List<SupplierRspDto>
	 */
	private List<Supplier> queryAllSupplier(List<String> deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType,
			String companyName) {
		// 全部
		SearchFilter filter = new SearchFilter();
		if (searchType == SearchType.SUPPLIER.ordinal()) {
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 0));
		}
		if (deptIds != null && !deptIds.isEmpty()) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds));
		}
		if (StringUtil.isNotBlank(supplierId)) {
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
		}
		if (StringUtil.isNotBlank(supplierTypeId)) {
			filter.getRules().add(new SearchRule("supplierTypeId", Constants.ROP_EQ, supplierTypeId));
		}
		if (StringUtil.isNotBlank(keyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, keyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("查询所有权限 查询供应商信息错误", e);
		}
		return null;
	}

	/**
	 * 部门权限 根据用户的部门查询对应的供应商信息
	 *
	 * @param userDeptId
	 *            用户的部门id
	 * @param deptIds
	 *            查询条件的部门id
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            查询条件 关键词
	 * @param companyName
	 *            公司名
	 * @return 供应商信息
	 */
	private List<Supplier> querySupplierByDept(String userDeptId, List<String> deptIds, String supplierId, String supplierTypeId, String keyWord,
			int searchType, String companyName) {
		List<String> subDeptIds = new ArrayList<>();
		try {
			// 部门
			Set<Department> subDept = departmentService.getSubDept(userDeptId);
			subDept.add(departmentService.read(userDeptId)); // 加上自己所在部门
			if (subDept != null && !subDept.isEmpty()) {
				subDeptIds = subDept.stream().map(Department::getDeptid).collect(Collectors.toList());
				if (deptIds != null && !deptIds.isEmpty()) {
					subDeptIds.retainAll(deptIds);
				}
			}
		} catch (ServiceException e) {
			logger.error("查询子部门错误", e);
		}
		SearchFilter filter = new SearchFilter();
		if (searchType == SearchType.SUPPLIER.ordinal()) {
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 0));
		}
		if (StringUtil.isNotBlank(supplierId)) {
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
		}
		if (StringUtil.isNotBlank(supplierTypeId)) {
			filter.getRules().add(new SearchRule("supplierTypeId", Constants.ROP_EQ, supplierTypeId));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		if (StringUtil.isNotBlank(keyWord)) {
			// 关键词条件
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, keyWord));
		}
		if (!subDeptIds.isEmpty()) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, subDeptIds));
			try {
				return queryAllBySearchFilter(filter);
			} catch (ServiceException e) {
				logger.error("根据部门权限 查询供应商信息错误", e);
			}
		}
		return null;
	}

	/**
	 * 根据未处理流程查询供应商
	 *
	 * @param roleId
	 *            角色id
	 * @param ossuserId
	 *            用户id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            查询条件 关键词
	 * @param companyName
	 *            公司名
	 * @return 供应商信息
	 */
	private List<Supplier> querySupplierByFlow(String roleId, String ossuserId, List<String> deptIds, String supplierId, String supplierTypeId, String keyWord,
			int searchType, String companyName) {
		// 查询待处理和已处理的流程的供应商
		List<String> supplierIds = flowEntService.queryFlowEntityId(roleId, ossuserId, EntityType.SUPPLIER);
		if (supplierIds != null && !supplierIds.isEmpty()) {
			SearchFilter filter = new SearchFilter();
			if (searchType == SearchType.SUPPLIER.ordinal()) {
				filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 0));
			}
			if (deptIds != null && !deptIds.isEmpty()) {
				filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds));
			}
			if (StringUtil.isNotBlank(supplierId)) {
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
			} else {
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, supplierIds));
			}
			if (StringUtil.isNotBlank(supplierTypeId)) {
				filter.getRules().add(new SearchRule("supplierTypeId", Constants.ROP_EQ, supplierTypeId));
			}
			if (StringUtil.isNotBlank(keyWord)) {
				filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, keyWord));
			}
			if (StringUtil.isNotBlank(companyName)) {
				filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
			}
			try {
				return queryAllBySearchFilter(filter);
			} catch (ServiceException e) {
				logger.error("根据流程权限 查询供应商信息错误", e);
			}
		}
		return null;
	}

	/**
	 * 查询自己的供应商
	 *
	 * @param ossuserId
	 *            用户id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            查询条件 关键词
	 * @param companyName
	 *            公司名
	 * @return 供应商信息
	 */
	private List<Supplier> querySelfSupplier(String ossuserId, List<String> deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType, String companyName) {
		SearchFilter filter = new SearchFilter();
		if (searchType == SearchType.SUPPLIER.ordinal()) {
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 0));
		}
		if (deptIds != null && !deptIds.isEmpty()) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIds));
		}
		if (StringUtil.isNotBlank(supplierId)) {
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
		}
		if (StringUtil.isNotBlank(supplierTypeId)) {
			filter.getRules().add(new SearchRule("supplierTypeId", Constants.ROP_EQ, supplierTypeId));
		}
		if (StringUtil.isNotBlank(keyWord)) {
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, keyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		// 自己
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossuserId));
		try {
			return queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("根据 自己 权限 查询供应商信息错误", e);
		}
		return null;
	}

	/**
	 * 查询自定义部门的供应商
	 *
	 * @param userDeptIds
	 *            自定义部门id
	 * @param deptIds
	 *            查询条件 部门id
	 * @param supplierId
	 *            供应商id
	 * @param supplierTypeId
	 *            供应商类型id
	 * @param keyWord
	 *            查询条件 关键词
	 * @param companyName
	 *            公司名
	 * @return 供应商信息
	 */
	private List<Supplier> queryCustomizeSupplier(String userDeptIds, List<String> deptIds, String supplierId, String supplierTypeId, String keyWord,
			int searchType, String companyName) {
		List<String> subDeptIds = new ArrayList<>();
		// 自定义部门id
		if (StringUtils.isNotBlank(userDeptIds)) {
			subDeptIds = new ArrayList<>(Arrays.asList(userDeptIds.split(",")));
		}
		// 过滤条件部门id
		if (deptIds != null && !deptIds.isEmpty()) {
			subDeptIds.retainAll(deptIds); // 取交集
		}
		SearchFilter filter = new SearchFilter();
		if (searchType == SearchType.SUPPLIER.ordinal()) {
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, 0));
		}
		if (StringUtil.isNotBlank(supplierId)) {
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
		}
		if (StringUtil.isNotBlank(supplierTypeId)) {
			filter.getRules().add(new SearchRule("supplierTypeId", Constants.ROP_EQ, supplierTypeId));
		}
		if (StringUtil.isNotBlank(keyWord)) {
			// 关键字
			filter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, keyWord));
		}
		if (StringUtil.isNotBlank(companyName)) {
			// 公司名条件
			filter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
		}
		if (!subDeptIds.isEmpty()) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, subDeptIds));
			try {
				return queryAllBySearchFilter(filter);
			} catch (ServiceException e) {
				logger.error("根据自定义权限 查询供应商信息错误", e);
			}
		}
		return null;
	}

	/**
	 * 统计供应商未处理的流程
	 *
	 * @param supplierList
	 *            供应商信息
	 * @param roleId
	 *            角色id
	 * @param ossuserId
	 *            用户id
	 * @return 供应商信息
	 */
	private List<SupplierRspDto> countSupplier(List<Supplier> supplierList, String roleId, String ossuserId) {
		// 流程统计数据
		List<FlowEntDealCount> countList = null;
		try {
			// 查询统计数据
			countList = flowEntService.queryFlowEntDealCount(roleId, ossuserId);
		} catch (ServiceException e) {
			logger.error("查询未处理流程数据错误", e);
		}
		Map<String, IntSummaryStatistics> supplierCount = null;
		if (countList != null && !countList.isEmpty()) {
			// 根据供应商 id进行统计
			supplierCount = countList.stream().filter(o -> StringUtil.isNotBlank(o.getSupplierId()))
					.collect(Collectors.groupingBy(FlowEntDealCount::getSupplierId, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
		}
		List<SupplierRspDto> resultInfo = new ArrayList<>();
		if (supplierList != null && !supplierList.isEmpty()) {
			for (Supplier supplier : supplierList) {
				String supplierId = supplier.getSupplierId();
				String supplierTypeId = supplier.getSupplierTypeId();
				String compayName = supplier.getCompanyName();
				long count = 0L;
				if (supplierCount != null) {
					IntSummaryStatistics summaryStatistics = supplierCount.get(supplierId);
					if (summaryStatistics != null) {
						count = summaryStatistics.getSum();
					}
				}
				resultInfo.add(new SupplierRspDto(supplierId, compayName, supplierTypeId, count));
			}
			resultInfo.sort((SupplierRspDto dto1, SupplierRspDto dto2) -> dto2.getFlowEntCount().compareTo(dto1.getFlowEntCount()));
		}
		return resultInfo;
	}

	/**
	 * 查询用户的供应商(保证可以查看自己的)
	 *
	 * @param user
	 *            登录用户
	 * @param companyName
	 *            公司名
	 * @param pageSize
	 *            一页大小
	 * @param currentPage
	 *            当前页
	 * @return 供应商信息
	 */
	@Override
	public PageResult<SupplierRspDto> queryUserSupplier(OnlineUser user, String companyName, Integer pageSize, Integer currentPage) {
		try {
			String roleId = user.getRoleId();
			Role role = roleService.read(roleId);
			int permission = role.getDataPermission();
			SearchFilter supplierFilter = new SearchFilter();
			String userId = user.getUser().getOssUserId();
			String userDeptId = user.getUser().getDeptId();
			if (DataPermission.Self.ordinal() == permission) {
				// 看自己的
				supplierFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, userId));
			} else if (DataPermission.Customize.ordinal() == permission) {
				Set<String> deptIdSet = new HashSet<>();
				if (StringUtil.isNotBlank(userDeptId)) {
					deptIdSet.add(userDeptId);
				}
				String deptIds = role.getDeptIds();
				if (StringUtil.isNotBlank(deptIds)) {
					deptIdSet.addAll(Arrays.asList(deptIds.split(",")));
				}
				// 可以查看对应部门的
				supplierFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, new ArrayList<>(deptIdSet)));
			} else if (DataPermission.Dept.ordinal() == permission) {
				// 可以查看自己应部门的
				supplierFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, userDeptId));
			} else if (DataPermission.Flow.ordinal() == permission) {
				// 查询可见流程
				Set<String> flowEntityIds = flowEntService.queryEntityIdFromFlowEnt(roleId, userId, EntityType.SUPPLIER.getCode());
				if (flowEntityIds != null && !flowEntityIds.isEmpty()) {
					// 可以查看对应部门的和自己的供应商
					supplierFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, new ArrayList<>(flowEntityIds)));
				} else {
					return PageResult.empty("暂无数据");
				}
			} else if (DataPermission.All.ordinal() != permission) {
				return PageResult.empty("角色无权限");
			}
			if (StringUtil.isNotBlank(companyName)) {
				supplierFilter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, companyName));
			}
			supplierFilter.getOrders().add(new SearchOrder("supplierTypeId", "asc"));
			PageResult<Supplier> supplierPageResult = this.queryByPages(pageSize, currentPage, supplierFilter);
			List<Supplier> supplierList = supplierPageResult.getData();
			if (supplierList == null || supplierList.isEmpty()) {
				return PageResult.empty("暂无数据");
			}

			List<SupplierType> supplierTypeList = supplierTypeService.queryAllBySearchFilter(null);
			Map<String, String> supplierTypeMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(supplierTypeList)) {
				supplierTypeMap = supplierTypeList.stream().collect(Collectors.toMap(SupplierType::getSupplierTypeId, SupplierType::getSupplierTypeName));
			}

			// 供应商信息
			List<SupplierRspDto> supplierDtoList = new ArrayList<>();
			for (Supplier supplier : supplierList) {
				SupplierRspDto supplierDto = new SupplierRspDto(supplier.getSupplierId(), supplier.getCompanyName());
				supplierDto.setSupplierTypeId(supplier.getSupplierTypeId());
				supplierDto.setSupplierTypeName(supplierTypeMap.getOrDefault(supplier.getSupplierTypeId(), ""));
				supplierDtoList.add(supplierDto);
			}
			PageResult<SupplierRspDto> pageResult = new PageResult<>();
			BeanUtils.copyProperties(supplierPageResult, pageResult);
			pageResult.setData(supplierDtoList);
			return pageResult;
		} catch (Exception e) {
			logger.error("分页查询供应商信息异常", e);
		}
		return PageResult.empty("查询错误");
	}
}
