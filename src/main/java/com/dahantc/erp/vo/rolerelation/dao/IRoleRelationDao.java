package com.dahantc.erp.vo.rolerelation.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
public interface IRoleRelationDao {
	RoleRelation read(Serializable id) throws DaoException;

	boolean save(RoleRelation entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(RoleRelation enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<RoleRelation> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<RoleRelation> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	RoleRelation readOneByProperty(String property, Object value) throws BaseException;

	boolean updateByBatch(List<RoleRelation> objs) throws DaoException;

	boolean saveByBatch(List<RoleRelation> objs) throws DaoException;

	boolean deleteByBatch(List<RoleRelation> objs) throws DaoException;
}
