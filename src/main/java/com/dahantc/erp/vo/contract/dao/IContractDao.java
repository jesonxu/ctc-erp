package com.dahantc.erp.vo.contract.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.contract.entity.Contract;

public interface IContractDao {
	Contract read(Serializable id) throws DaoException;

	boolean save(Contract entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Contract enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Contract> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<Contract> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Contract> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<Contract> objs) throws DaoException;

	boolean saveByBatch(List<Contract> objs) throws DaoException;

	boolean deleteByBatch(List<Contract> objs) throws DaoException;
	
	List<Contract> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
