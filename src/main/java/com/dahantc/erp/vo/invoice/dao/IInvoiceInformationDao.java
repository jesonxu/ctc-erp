package com.dahantc.erp.vo.invoice.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;

public interface IInvoiceInformationDao {
	InvoiceInformation read(Serializable id) throws DaoException;

	boolean save(InvoiceInformation entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(InvoiceInformation enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<InvoiceInformation> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<InvoiceInformation> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<InvoiceInformation> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<InvoiceInformation> objs) throws DaoException;

	boolean saveByBatch(List<InvoiceInformation> objs) throws DaoException;

	boolean deleteByBatch(List<InvoiceInformation> objs) throws DaoException;

	List<InvoiceInformation> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
