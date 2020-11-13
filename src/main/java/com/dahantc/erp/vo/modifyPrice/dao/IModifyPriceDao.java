package com.dahantc.erp.vo.modifyPrice.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;

public interface IModifyPriceDao {
	ModifyPrice read(Serializable id) throws DaoException;

	boolean save(ModifyPrice entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(ModifyPrice enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<ModifyPrice> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<ModifyPrice> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<ModifyPrice> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<ModifyPrice> objs) throws DaoException;

	boolean saveByBatch(List<ModifyPrice> objs) throws DaoException;

	boolean deleteByBatch(List<ModifyPrice> objs) throws DaoException;

	List<ModifyPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
