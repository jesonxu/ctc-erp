package com.dahantc.erp.vo.royalty.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.royalty.entity.Royalty;

public interface IRoyaltyDao {
	Royalty read(Serializable id) throws DaoException;

	boolean save(Royalty entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Royalty enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Royalty> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Royalty> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Royalty> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<Royalty> objs) throws DaoException;

	boolean saveByBatch(List<Royalty> objs) throws DaoException;

	boolean deleteByBatch(List<Royalty> objs) throws DaoException;

	List<Royalty> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
