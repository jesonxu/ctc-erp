package com.dahantc.erp.vo.dailyExpenseTj.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dailyExpenseTj.entity.Dailyexpenses;

public interface IDailyexpensesService {
	Dailyexpenses read(Serializable id) throws ServiceException;

	boolean save(Dailyexpenses entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Dailyexpenses enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Dailyexpenses> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<Dailyexpenses> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Dailyexpenses> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<Dailyexpenses> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<Dailyexpenses> objs) throws ServiceException;
}
