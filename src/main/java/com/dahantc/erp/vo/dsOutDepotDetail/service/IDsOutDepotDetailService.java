package com.dahantc.erp.vo.dsOutDepotDetail.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;

public interface IDsOutDepotDetailService {
	DsOutDepotDetail read(Serializable id) throws ServiceException;

	boolean save(DsOutDepotDetail entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsOutDepotDetail enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsOutDepotDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsOutDepotDetail> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsOutDepotDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsOutDepotDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsOutDepotDetail> objs) throws ServiceException;
}
