package com.dahantc.erp.vo.msgDetail.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;

public interface IMsgDetailService {
	MsgDetail read(Serializable id) throws ServiceException;

	boolean save(MsgDetail entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(MsgDetail enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<MsgDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<MsgDetail> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<MsgDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<MsgDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<MsgDetail> objs) throws ServiceException;
}
