package com.dahantc.erp.vo.operateCost.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.operateCost.entity.OperateCost;
import com.dahantc.erp.vo.productBills.entity.OperateCostDetail;
import com.dahantc.erp.vo.productBills.entity.ProductBills;

public interface IOperateCostService {
	OperateCost read(Serializable id) throws ServiceException;

	boolean save(OperateCost entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(OperateCost enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<OperateCost> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<OperateCost> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<OperateCost> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<OperateCost> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<OperateCost> objs) throws ServiceException;

	OperateCost saveOperateCostByBill(OperateCostDetail detail, ProductBills bill);
}
