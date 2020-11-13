package com.dahantc.erp.vo.accountbalance.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.accountbalance.entity.AccountBalance;

public interface IAccountBalanceDao {
	
	AccountBalance read(Serializable id) throws DaoException;

	boolean save(AccountBalance entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(AccountBalance enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<AccountBalance> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<AccountBalance> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<AccountBalance> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<AccountBalance> objs) throws DaoException;

	boolean saveByBatch(List<AccountBalance> objs) throws DaoException;

	boolean deleteByBatch(List<AccountBalance> objs) throws DaoException;

	List<AccountBalance> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
