package com.dahantc.erp.vo.dsOrderDetail.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsOrderDetail.entity.DsOrderDetail;

public interface IDsOrderDetailDao {
	DsOrderDetail read(Serializable id) throws DaoException;

	boolean save(DsOrderDetail entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsOrderDetail enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsOrderDetail> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsOrderDetail> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsOrderDetail> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsOrderDetail> objs) throws DaoException;

	boolean saveByBatch(List<DsOrderDetail> objs) throws DaoException;

	boolean deleteByBatch(List<DsOrderDetail> objs) throws DaoException;
	
	List<DsOrderDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
