package com.dahantc.erp.vo.invoice.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;

public interface IInvoiceInformationService {
	InvoiceInformation read(Serializable id) throws ServiceException;

	boolean save(InvoiceInformation entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(InvoiceInformation enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<InvoiceInformation> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<InvoiceInformation> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<InvoiceInformation> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<InvoiceInformation> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<InvoiceInformation> objs) throws ServiceException;
}
