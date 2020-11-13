package com.dahantc.erp.vo.supplierHistory.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.supplierHistory.entity.SupplierHistory;

public interface ISupplierHistoryService {
	SupplierHistory read(Serializable id) throws ServiceException;

	boolean save(SupplierHistory entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SupplierHistory entity) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SupplierHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<SupplierHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SupplierHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<SupplierHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
}
