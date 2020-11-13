package com.dahantc.erp.vo.balanceinterest.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.balanceinterest.entity.BalanceInterest;

public interface IBalanceInterestDao {
	BalanceInterest read(Serializable id) throws DaoException;

	boolean save(BalanceInterest entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(BalanceInterest enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<BalanceInterest> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<BalanceInterest> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<BalanceInterest> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<BalanceInterest> objs) throws DaoException;

	boolean saveByBatch(List<BalanceInterest> objs) throws DaoException;

	boolean deleteByBatch(List<BalanceInterest> objs) throws DaoException;

	List<BalanceInterest> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
