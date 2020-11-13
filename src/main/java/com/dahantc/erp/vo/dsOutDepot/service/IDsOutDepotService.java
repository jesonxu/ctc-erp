package com.dahantc.erp.vo.dsOutDepot.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.dsOutDepot.DsSaveOutDepotDto;
import com.dahantc.erp.vo.dsOutDepot.entity.DsOutDepot;
import com.dahantc.erp.vo.user.entity.User;

public interface IDsOutDepotService {
	DsOutDepot read(Serializable id) throws ServiceException;

	boolean save(DsOutDepot entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsOutDepot enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsOutDepot> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsOutDepot> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsOutDepot> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsOutDepot> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsOutDepot> objs) throws ServiceException;
	
	BaseResponse<String> saveDsOutDepot(DsSaveOutDepotDto dto, User user) throws ServiceException;
	
	BaseResponse<String> auditDsOutDepot(String id, User user) throws ServiceException;
	
	BaseResponse<String> deleteDsOutDepot(String id, User user) throws ServiceException;
}
