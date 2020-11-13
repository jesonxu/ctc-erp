package com.dahantc.erp.vo.unitPrice.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;

public interface IUnitPriceService {
	UnitPrice read(Serializable id) throws ServiceException;

	boolean save(UnitPrice entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(UnitPrice enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<UnitPrice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<UnitPrice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<UnitPrice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<UnitPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<UnitPrice> objs) throws ServiceException;

	boolean updateByBatch(List<UnitPrice> objs) throws ServiceException;
}
