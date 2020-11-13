package com.dahantc.erp.vo.dsSaleData.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsSaleData.entity.DsSaleData;

public interface IDsSaleDataDao {
	DsSaleData read(Serializable id) throws DaoException;

	boolean save(DsSaleData entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsSaleData enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsSaleData> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsSaleData> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsSaleData> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsSaleData> objs) throws DaoException;

	boolean saveByBatch(List<DsSaleData> objs) throws DaoException;

	boolean deleteByBatch(List<DsSaleData> objs) throws DaoException;
	
	List<DsSaleData> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
