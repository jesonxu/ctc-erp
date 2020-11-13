package com.dahantc.erp.vo.region.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.region.entity.Region;

public interface IRegionService {
	Region read(Serializable id) throws ServiceException;

	boolean save(Region entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Region enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Region> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Region> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
}
