package com.dahantc.erp.vo.dsSaleData.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsSaleData.entity.DsCustomerReceiveData;

public interface IDsCustomerReceiveDataDao {
	DsCustomerReceiveData read(Serializable id) throws DaoException;

	boolean save(DsCustomerReceiveData entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsCustomerReceiveData enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsCustomerReceiveData> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsCustomerReceiveData> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsCustomerReceiveData> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsCustomerReceiveData> objs) throws DaoException;

	boolean saveByBatch(List<DsCustomerReceiveData> objs) throws DaoException;

	boolean deleteByBatch(List<DsCustomerReceiveData> objs) throws DaoException;
	
	List<DsCustomerReceiveData> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
