package com.dahantc.erp.vo.royalty.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.royalty.entity.Royalty;

public interface IRoyaltyService {
	Royalty read(Serializable id) throws ServiceException;

	boolean save(Royalty entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Royalty enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Royalty> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Royalty> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Royalty> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<Royalty> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<Royalty> objs) throws ServiceException;

	boolean updateByBatch(List<Royalty> objs) throws ServiceException;

}
