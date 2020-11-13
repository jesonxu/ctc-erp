package com.dahantc.erp.vo.dsDepotItem.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;

public interface IDsDepotItemService {
	DsDepotItem read(Serializable id) throws ServiceException;

	boolean save(DsDepotItem entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsDepotItem enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsDepotItem> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsDepotItem> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsDepotItem> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsDepotItem> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsDepotItem> objs) throws ServiceException;
}
