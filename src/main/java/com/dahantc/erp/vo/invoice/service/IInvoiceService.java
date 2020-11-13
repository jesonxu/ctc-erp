package com.dahantc.erp.vo.invoice.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.invoice.entity.Invoice;

public interface IInvoiceService {
	Invoice read(Serializable id) throws ServiceException;

	boolean save(Invoice entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Invoice enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Invoice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Invoice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Invoice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<Invoice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<Invoice> objs) throws ServiceException;

	List<InvoiceExtendDto> queryInvoices(Date yearDate, List<String> userIds, String settleType);
}
