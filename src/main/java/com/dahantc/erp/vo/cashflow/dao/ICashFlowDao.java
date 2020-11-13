package com.dahantc.erp.vo.cashflow.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;

public interface ICashFlowDao {
	CashFlow read(Serializable id) throws DaoException;

	boolean save(CashFlow entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(CashFlow enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<CashFlow> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<CashFlow> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<CashFlow> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<CashFlow> objs) throws DaoException;

	boolean saveByBatch(List<CashFlow> objs) throws DaoException;

	boolean deleteByBatch(List<CashFlow> objs) throws DaoException;

	List<CashFlow> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
