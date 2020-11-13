package com.dahantc.erp.vo.checkin.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.checkin.entity.Checkin;

public interface ICheckinService {
	Checkin read(Serializable id) throws ServiceException;

	boolean save(Checkin entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Checkin enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Checkin> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<Checkin> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Checkin> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<Checkin> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean updateByBatch(List<Checkin> objs) throws ServiceException;

	boolean saveByBatch(List<Checkin> objs) throws ServiceException;

	boolean deleteByBatch(List<Checkin> objs) throws ServiceException;

	Checkin readOneByProperty(String property, Object value) throws ServiceException;

}
