package com.dahantc.erp.vo.saleAnalysisStatistics.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.saleAnalysisStatistics.entity.SaleAnalysis;

public interface ISaleAnalysisService {
	SaleAnalysis read(Serializable id) throws ServiceException;

	boolean save(SaleAnalysis entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SaleAnalysis enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SaleAnalysis> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<SaleAnalysis> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SaleAnalysis> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<SaleAnalysis> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<SaleAnalysis> objs) throws ServiceException;
}
