package com.dahantc.erp.vo.invoice.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.invoice.entity.Invoice;

public interface IInvoiceDao {
	Invoice read(Serializable id) throws DaoException;

	boolean save(Invoice entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Invoice enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Invoice> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Invoice> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Invoice> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<Invoice> objs) throws DaoException;

	boolean saveByBatch(List<Invoice> objs) throws DaoException;

	boolean deleteByBatch(List<Invoice> objs) throws DaoException;

	List<Invoice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
