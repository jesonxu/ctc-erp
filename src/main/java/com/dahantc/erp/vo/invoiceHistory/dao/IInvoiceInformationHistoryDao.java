package com.dahantc.erp.vo.invoiceHistory.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.invoiceHistory.entity.InvoiceInformationHistory;

public interface IInvoiceInformationHistoryDao {
	InvoiceInformationHistory read(Serializable id) throws DaoException;

	boolean save(InvoiceInformationHistory entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(InvoiceInformationHistory enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<InvoiceInformationHistory> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<InvoiceInformationHistory> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<InvoiceInformationHistory> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<InvoiceInformationHistory> objs) throws DaoException;

	boolean saveByBatch(List<InvoiceInformationHistory> objs) throws DaoException;

	boolean deleteByBatch(List<InvoiceInformationHistory> objs) throws DaoException;

	List<InvoiceInformationHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
