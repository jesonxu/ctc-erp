package com.dahantc.erp.vo.bankAccount.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;

public interface IBankAccountDao {
	BankAccount read(Serializable id) throws DaoException;

	boolean save(BankAccount entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(BankAccount enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<BankAccount> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<BankAccount> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<BankAccount> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<BankAccount> objs) throws DaoException;

	boolean saveByBatch(List<BankAccount> objs) throws DaoException;

	boolean deleteByBatch(List<BankAccount> objs) throws DaoException;

	List<BankAccount> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
