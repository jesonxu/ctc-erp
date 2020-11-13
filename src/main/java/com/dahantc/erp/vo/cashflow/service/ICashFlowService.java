package com.dahantc.erp.vo.cashflow.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;

public interface ICashFlowService {
	CashFlow read(Serializable id) throws ServiceException;

	boolean save(CashFlow entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CashFlow enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<CashFlow> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<CashFlow> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CashFlow> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<CashFlow> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<CashFlow> objs) throws ServiceException;
}
