package com.dahantc.erp.vo.customer.service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.customer.CustomerAllDto;
import com.dahantc.erp.dto.customer.CustomerDeptResp;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.user.entity.User;

public interface ICustomerService {
	Customer read(Serializable id) throws ServiceException;

	boolean save(Customer entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Customer entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos, String delInvoiceIds, String delBankIds)
			throws ServiceException;

	boolean update(Customer entity) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Customer> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Customer> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Customer> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<Customer> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	PageResult<Customer> findByhql(String hql, String countHql, final Map<String, Object> params, int page, int pageSize) throws ServiceException;

	boolean saveByBatch(List<Customer> objs) throws ServiceException;

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
	List<Customer> readCustomers(OnlineUser onlineUser, String deptIds, String customerId, String customerTypeId, String customerKeyWord);

	/**
	 * 查询当前登录用户的所有客户（按数据权限，可带部门、客户id条件,客户名称）
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
	 * @param companyName
	 *            客户名称
	 * @return 客户信息
	 */
	List<Customer> readCustomers(OnlineUser onlineUser, String deptIds, String customerId, String customerTypeId, String customerKeyWord, String companyName);

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
	List<Customer> readCustomersByRole(OnlineUser onlineUser, String roleId, String deptIds, String customerId, String customerTypeId, String customerKeyWord);

	/**
	 * 读取当前登录用户的客户信息
	 *
	 * @param onlineUser
	 *            在线用户
	 * @param deptIds
	 *            部门Ids
	 * @return List<CustomerRespDto>
	 */
	List<CustomerRespDto> readCustomersByDept(OnlineUser onlineUser, String deptIds);

	/**
	 * 读取当前登录用户的客户信息（根据权限获取） 通过权限 部门、名称条件查询 用户可以查看的用户信息
	 *
	 * @param onlineUser
	 *            在线用户
	 * @param deptIds
	 *            部门Ids
	 * @param customerId
	 *            客户id
	 * @param customerTypeId
	 *            客户id
	 * @param customerKeyWord
	 *            客户关键词
	 * @return List<CustomerRespDto>
	 */
	List<CustomerRespDto> queryCustomers(OnlineUser onlineUser, String deptIds, String customerId, String customerTypeId, String customerKeyWord);

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
	 * @param openOssUserId
	 *            销售id（展开条件）
	 * @param deptIds
	 *            部门id(查询条件)
	 * @param customerId
	 *            客户id（查询条件）
	 * @param customerKeyWord
	 *            客户关键词（查询条件）
	 * @return 客户部门信息
	 */
	List<CustomerDeptResp> queryCustomerAndDept(Role role, String ossuserId, String uDeptId, String customerTypeId, String deptId, String openOssUserId,
			String deptIds, String customerId, String customerKeyWord, String userIds);

	/**
	 * 查询客户类型
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
	List<CustomerTypeRespDto> getCustomerType(OnlineUser onlineUser, String searchDeptIds, String searchCustomerId, String customerKeyWord,
			String searchUserIds);

	/**
	 * 查询客户
	 * 
	 * @param userDeptId
	 *            用户所在部门
	 * @param ossuserId
	 *            登录用户
	 * @param role
	 *            登录角色
	 * @param customerTypeId
	 *            客户类型（点击）
	 * @param searchDeptIds
	 *            部门id（查询条件）
	 * @param searchCustomerId
	 *            客户id （查询条件）
	 * @param customerKeyWord
	 *            客户关键词 （查询条件）
	 * @return
	 */
	List<CustomerRespDto> queryCustomerCount(String userDeptId, String ossuserId, Role role, String customerTypeId, List<String> searchDeptIds,
			String searchCustomerId, String customerKeyWord, List<String> searchUserIds);

	/**
	 * 改变客户的级别
	 *
	 * @param customer
	 *            客户
	 * @param user
	 *            用户（这个客户的拥有者）
	 * @param from
	 *            原来的客户类型（级别）
	 * @param to
	 *            现在的客户类型（级别）
	 * @param clearSale
	 *            是否清除销售
	 */
	void changeGrade(Customer customer, User user, CustomerType from, CustomerType to, String depict, boolean clearSale) throws ServiceException;

	/**
	 * 查找客户处于这种状态的时间
	 * 
	 * @param customers
	 *            客户
	 * @return 客户状态时间
	 */
	Map<String, Timestamp> findCustomerInThisStateTime(List<Customer> customers);


	/**
	 * 查询客户名称
	 *
	 * @param ids 客户id
	 * @return id -> 名称
	 */
	Map<String, String> queryCustomerName(List<String> ids);


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
	PageResult<CustomerRespDto> queryUserCustomer(OnlineUser user, String companyName,boolean onlyPublic,
												  boolean noPublic, Integer pageSize, Integer currentPage);

	/**
	 * 根据客户端的详情
	 *
	 * @param customerId 客户ID
	 * @return 客户详情
	 */
	CustomerAllDto queryCustomerDetailById(String customerId);

}
