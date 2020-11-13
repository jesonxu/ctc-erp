package com.dahantc.erp.vo.customerStatistics.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.customerStatistics.entity.CustomerStatistics;
import org.omg.CORBA.INTERNAL;

public interface ICustomerStatisticsService {
	CustomerStatistics read(Serializable id) throws ServiceException;

	boolean save(CustomerStatistics entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CustomerStatistics enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<CustomerStatistics> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<CustomerStatistics> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CustomerStatistics> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<CustomerStatistics> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<CustomerStatistics> objs) throws ServiceException;

	/**
	 * 查询客户多少月多条天内的消耗量（不分种类）
	 * 
	 * @param customerIds
	 *            客户Id
	 * @param month
	 *            月数
	 * @param days
	 *            缇娜书
	 * @return 客户消耗统计信息
	 */
	Map<String, Long> findCustomerCost(List<String> customerIds, Integer month, Integer days);
}
