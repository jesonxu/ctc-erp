package com.dahantc.erp.vo.dsBuyOrder.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsBuyOrder.entity.DsBuyOrder;
import com.dahantc.erp.vo.user.entity.User;

public interface IDsBuyOrderService {
	DsBuyOrder read(Serializable id) throws ServiceException;

	boolean save(DsBuyOrder entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsBuyOrder enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsBuyOrder> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsBuyOrder> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsBuyOrder> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsBuyOrder> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsBuyOrder> objs) throws ServiceException;
	
	String buildBuyOrderNo(User user, Date date) throws ServiceException;
}
