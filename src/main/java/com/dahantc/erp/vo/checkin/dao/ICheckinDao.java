package com.dahantc.erp.vo.checkin.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.checkin.entity.Checkin;

public interface ICheckinDao {
	Checkin read(Serializable id) throws DaoException;

	boolean save(Checkin entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Checkin enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Checkin> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<Checkin> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Checkin> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<Checkin> objs) throws DaoException;

	boolean saveByBatch(List<Checkin> objs) throws DaoException;

	boolean deleteByBatch(List<Checkin> objs) throws DaoException;
	
	List<Checkin> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	Checkin readOneByProperty(String property, Object value) throws DaoException;
}
