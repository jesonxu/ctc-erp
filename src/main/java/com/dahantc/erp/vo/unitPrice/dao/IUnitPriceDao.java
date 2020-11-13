package com.dahantc.erp.vo.unitPrice.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;

public interface IUnitPriceDao {
	UnitPrice read(Serializable id) throws DaoException;

	boolean save(UnitPrice entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(UnitPrice enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<UnitPrice> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<UnitPrice> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<UnitPrice> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<UnitPrice> objs) throws DaoException;

	boolean saveByBatch(List<UnitPrice> objs) throws DaoException;

	boolean deleteByBatch(List<UnitPrice> objs) throws DaoException;

	List<UnitPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
