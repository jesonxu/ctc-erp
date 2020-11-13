package com.dahantc.erp.vo.royalty.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.royalty.entity.RealRoyalty;

public interface IRealRoyaltyDao {
	RealRoyalty read(Serializable id) throws DaoException;

	boolean save(RealRoyalty entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(RealRoyalty enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<RealRoyalty> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<RealRoyalty> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<RealRoyalty> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<RealRoyalty> objs) throws DaoException;

	boolean saveByBatch(List<RealRoyalty> objs) throws DaoException;

	boolean deleteByBatch(List<RealRoyalty> objs) throws DaoException;

	List<RealRoyalty> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
