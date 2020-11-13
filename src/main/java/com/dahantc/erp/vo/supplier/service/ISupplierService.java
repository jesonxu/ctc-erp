package com.dahantc.erp.vo.supplier.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.supplier.entity.Supplier;

public interface ISupplierService {
    Supplier read(Serializable id) throws ServiceException;

    boolean save(Supplier entity, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos) throws ServiceException;

    boolean delete(Serializable id) throws ServiceException;

    boolean update(Supplier enterprise, List<InvoiceInformation> invoiceInfos, List<BankAccount> bankInfos, String delInvoiceIds, String delBankIds)
            throws ServiceException;

    int getCount(SearchFilter filter) throws ServiceException;

    PageResult<Supplier> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

    List<Supplier> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

    List<Supplier> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

    List<Supplier> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

    /**
     * 读取当前登录用户的供应商信息（根据权限获取，可带条件）
     *
     * @param onlineUser     当前用户
     * @param deptIds        部门Ids
     * @param supplierId     供应商id
     * @param supplierTypeId 供应商类型id
     * @param keyWord        关键词
     * @return List<CustomerRespDto>
     */
    List<SupplierRspDto> querySuppliers(OnlineUser onlineUser, String deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType);

    /**
     * 查询当前登录用户的所有供应商（按数据权限，可带部门、公司名称条件）
     *
     * @param onlineUser     当前用户
     * @param deptIds        部门Ids
     * @param supplierId     供应商id
     * @param supplierTypeId 供应商类型id
     * @param keyWord        关键词
     * @return
     */
    List<Supplier> readSuppliers(OnlineUser onlineUser, String deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType);

    /**
     * 查询当前登录用户的所有供应商（按数据权限，可带部门、公司名称条件）
     *
     * @param onlineUser     当前用户
     * @param deptIds        部门Ids
     * @param supplierId     供应商id
     * @param supplierTypeId 供应商类型id
     * @param keyWord        关键词
     * @param searchType     搜索类型
     * @param companyName    公司名称
     * @return
     */
    List<Supplier> readSuppliers(OnlineUser onlineUser, String deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType, String companyName);

    /**
     * 查询当前登录用户的所有供应商（按数据权限，可带部门、公司名称条件）
     *
     * @param onlineUser     当前用户
     * @param roleId         角色Id
     * @param deptIds        部门Ids
     * @param supplierId     供应商id
     * @param supplierTypeId 供应商类型id
     * @param keyWord        关键词
     * @return
     */
    List<Supplier> readSuppliersByRole(OnlineUser onlineUser, String roleId, String deptIds, String supplierId, String supplierTypeId, String keyWord, int searchType, String companyName);

    /**
     * 查询用户的供应商
     *
     * @param user        登录用户
     * @param companyName 公司名
     * @param pageSize    一页大小
     * @param currentPage 当前页
     * @return 供应商信息
     */
    PageResult<SupplierRspDto> queryUserSupplier(OnlineUser user, String companyName, Integer pageSize, Integer currentPage);

}
