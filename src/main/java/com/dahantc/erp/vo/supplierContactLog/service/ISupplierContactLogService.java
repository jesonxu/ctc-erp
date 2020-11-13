package com.dahantc.erp.vo.supplierContactLog.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;

public interface ISupplierContactLogService {
	SupplierContactLog read(Serializable id) throws ServiceException;

	boolean save(SupplierContactLog entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SupplierContactLog enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SupplierContactLog> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<SupplierContactLog> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SupplierContactLog> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<SupplierContactLog> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	/**
	 * 查询客户一个月内的联系日志
	 * 
	 * @param customerIds
	 *            客户Id
	 * @param month
	 *            几个月内
	 * @return 客户的联系日志情况
	 */
	Map<String, Integer> queryCustomerContactLog(List<String> customerIds, Integer month, Integer day);
}
