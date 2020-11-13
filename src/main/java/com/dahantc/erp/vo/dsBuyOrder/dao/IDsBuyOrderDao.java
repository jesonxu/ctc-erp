package com.dahantc.erp.vo.dsBuyOrder.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsBuyOrder.entity.DsBuyOrder;

public interface IDsBuyOrderDao {
	DsBuyOrder read(Serializable id) throws DaoException;

	boolean save(DsBuyOrder entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsBuyOrder enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsBuyOrder> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsBuyOrder> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsBuyOrder> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsBuyOrder> objs) throws DaoException;

	boolean saveByBatch(List<DsBuyOrder> objs) throws DaoException;

	boolean deleteByBatch(List<DsBuyOrder> objs) throws DaoException;
	
	List<DsBuyOrder> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
