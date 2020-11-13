package com.dahantc.erp.vo.supplierContactsHistory.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.supplierContactsHistory.entity.SupplierContactsHistory;

public interface ISupplierContactsHistoryService {
	SupplierContactsHistory read(Serializable id) throws ServiceException;

	boolean save(SupplierContactsHistory entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SupplierContactsHistory enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SupplierContactsHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<SupplierContactsHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SupplierContactsHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<SupplierContactsHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
}
