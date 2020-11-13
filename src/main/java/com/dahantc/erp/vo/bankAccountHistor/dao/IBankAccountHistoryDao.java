package com.dahantc.erp.vo.bankAccountHistor.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.bankAccountHistor.entity.BankAccountHistory;

public interface IBankAccountHistoryDao {
	BankAccountHistory read(Serializable id) throws DaoException;

	boolean save(BankAccountHistory entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(BankAccountHistory enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<BankAccountHistory> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<BankAccountHistory> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<BankAccountHistory> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<BankAccountHistory> objs) throws DaoException;

	boolean saveByBatch(List<BankAccountHistory> objs) throws DaoException;

	boolean deleteByBatch(List<BankAccountHistory> objs) throws DaoException;

	List<BankAccountHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
