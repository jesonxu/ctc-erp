package com.dahantc.erp.vo.contractIncrease.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.contractIncrease.entity.ContractIncrease;

public interface IContractIncreaseDao {
	ContractIncrease read(Serializable id) throws DaoException;

	boolean save(ContractIncrease entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(ContractIncrease enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<ContractIncrease> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<ContractIncrease> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<ContractIncrease> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<ContractIncrease> objs) throws DaoException;

	boolean saveByBatch(List<ContractIncrease> objs) throws DaoException;

	boolean deleteByBatch(List<ContractIncrease> objs) throws DaoException;

	List<ContractIncrease> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	void executeUpdateSQL(String sql) throws DaoException;
}
