package com.dahantc.erp.vo.dsOrder.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
import com.dahantc.erp.vo.user.entity.User;

public interface IDsOrderService {
	DsOrder read(Serializable id) throws ServiceException;

	boolean save(DsOrder entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsOrder enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsOrder> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsOrder> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsOrder> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsOrder> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsOrder> objs) throws ServiceException;
	
	String buildOrderNo(User user, Date date, String sendType) throws ServiceException;
}
