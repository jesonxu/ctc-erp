package com.dahantc.erp.vo.dsDepotHead.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsDepotHead.entity.DsDepotHead;

public interface IDsDepotHeadDao {
	DsDepotHead read(Serializable id) throws DaoException;

	boolean save(DsDepotHead entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsDepotHead enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsDepotHead> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsDepotHead> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsDepotHead> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsDepotHead> objs) throws DaoException;

	boolean saveByBatch(List<DsDepotHead> objs) throws DaoException;

	boolean deleteByBatch(List<DsDepotHead> objs) throws DaoException;
	
	List<DsDepotHead> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
