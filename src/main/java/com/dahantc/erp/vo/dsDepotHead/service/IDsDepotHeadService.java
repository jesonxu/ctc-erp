package com.dahantc.erp.vo.dsDepotHead.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.dsDepot.DsSaveDepotDto;
import com.dahantc.erp.vo.dsDepotHead.entity.DsDepotHead;
import com.dahantc.erp.vo.user.entity.User;

public interface IDsDepotHeadService {
	DsDepotHead read(Serializable id) throws ServiceException;

	boolean save(DsDepotHead entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsDepotHead enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsDepotHead> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsDepotHead> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsDepotHead> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsDepotHead> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsDepotHead> objs) throws ServiceException;
	
	BaseResponse<String> saveDepotHead(DsSaveDepotDto dto, User user) throws ServiceException;
	
	BaseResponse<String> auditDepotHead(String id, User user) throws ServiceException;
	
	BaseResponse<String> deleteDepotHead(String id, User user) throws ServiceException;
}
