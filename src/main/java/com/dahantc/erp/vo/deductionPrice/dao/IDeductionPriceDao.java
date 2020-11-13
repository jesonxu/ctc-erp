package com.dahantc.erp.vo.deductionPrice.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;

public interface IDeductionPriceDao {
	DeductionPrice read(Serializable id) throws DaoException;

	boolean save(DeductionPrice entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DeductionPrice enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DeductionPrice> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<DeductionPrice> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DeductionPrice> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<DeductionPrice> objs) throws DaoException;

	boolean saveByBatch(List<DeductionPrice> objs) throws DaoException;

	boolean deleteByBatch(List<DeductionPrice> objs) throws DaoException;

	List<DeductionPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
