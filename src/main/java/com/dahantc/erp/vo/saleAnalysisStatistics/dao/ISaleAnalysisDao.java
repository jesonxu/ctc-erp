package com.dahantc.erp.vo.saleAnalysisStatistics.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.saleAnalysisStatistics.entity.SaleAnalysis;

public interface ISaleAnalysisDao {
	SaleAnalysis read(Serializable id) throws DaoException;

	boolean save(SaleAnalysis entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SaleAnalysis enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SaleAnalysis> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<SaleAnalysis> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SaleAnalysis> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<SaleAnalysis> objs) throws DaoException;

	boolean saveByBatch(List<SaleAnalysis> objs) throws DaoException;

	boolean deleteByBatch(List<SaleAnalysis> objs) throws DaoException;
	
	List<SaleAnalysis> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
