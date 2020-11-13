package com.dahantc.erp.vo.dsOutDepot.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsOutDepot.entity.DsOutDepot;

public interface IDsOutDepotDao {
	DsOutDepot read(Serializable id) throws DaoException;

	boolean save(DsOutDepot entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsOutDepot enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsOutDepot> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsOutDepot> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsOutDepot> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsOutDepot> objs) throws DaoException;

	boolean saveByBatch(List<DsOutDepot> objs) throws DaoException;

	boolean deleteByBatch(List<DsOutDepot> objs) throws DaoException;
	
	List<DsOutDepot> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
