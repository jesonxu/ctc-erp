package com.dahantc.erp.vo.rolerelation.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
public interface IRoleRelationService {
	RoleRelation read(Serializable id) throws ServiceException;

	boolean save(RoleRelation entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(RoleRelation enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<RoleRelation> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<RoleRelation> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	boolean updateByBatch(List<RoleRelation> objs) throws ServiceException;

	boolean saveByBatch(List<RoleRelation> objs) throws ServiceException;

	boolean deleteByBatch(List<RoleRelation> objs) throws ServiceException;
}
