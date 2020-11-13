package com.dahantc.erp.vo.dsDepotItem.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;

public interface IDsDepotItemDao {
	DsDepotItem read(Serializable id) throws DaoException;

	boolean save(DsDepotItem entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsDepotItem enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsDepotItem> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsDepotItem> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsDepotItem> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsDepotItem> objs) throws DaoException;

	boolean saveByBatch(List<DsDepotItem> objs) throws DaoException;

	boolean deleteByBatch(List<DsDepotItem> objs) throws DaoException;
	
	List<DsDepotItem> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
