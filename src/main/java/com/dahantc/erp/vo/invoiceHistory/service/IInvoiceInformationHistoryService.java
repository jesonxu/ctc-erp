package com.dahantc.erp.vo.invoiceHistory.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.invoiceHistory.entity.InvoiceInformationHistory;

public interface IInvoiceInformationHistoryService {
	InvoiceInformationHistory read(Serializable id) throws ServiceException;

	boolean save(InvoiceInformationHistory entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(InvoiceInformationHistory enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<InvoiceInformationHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<InvoiceInformationHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<InvoiceInformationHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<InvoiceInformationHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<InvoiceInformationHistory> objs) throws ServiceException;
}
