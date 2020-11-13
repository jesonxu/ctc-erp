package com.dahantc.erp.vo.royalty.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.royalty.entity.RealRoyalty;

public interface IRealRoyaltyService {
	RealRoyalty read(Serializable id) throws ServiceException;

	boolean save(RealRoyalty entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(RealRoyalty enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<RealRoyalty> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<RealRoyalty> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<RealRoyalty> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<RealRoyalty> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<RealRoyalty> objs) throws ServiceException;

	boolean updateByBatch(List<RealRoyalty> objs) throws ServiceException;

	JSONObject queryBillGrossProfit(Date startDate, Date endDate, OnlineUser onlineUser);
}
