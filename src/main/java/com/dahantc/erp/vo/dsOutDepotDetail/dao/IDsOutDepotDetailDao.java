package com.dahantc.erp.vo.dsOutDepotDetail.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;

public interface IDsOutDepotDetailDao {
	DsOutDepotDetail read(Serializable id) throws DaoException;

	boolean save(DsOutDepotDetail entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsOutDepotDetail enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsOutDepotDetail> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsOutDepotDetail> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsOutDepotDetail> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsOutDepotDetail> objs) throws DaoException;

	boolean saveByBatch(List<DsOutDepotDetail> objs) throws DaoException;

	boolean deleteByBatch(List<DsOutDepotDetail> objs) throws DaoException;
	
	List<DsOutDepotDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
