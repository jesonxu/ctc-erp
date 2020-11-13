package com.dahantc.erp.vo.region.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.region.entity.Region;

public interface IRegionDao {
	Region read(Serializable id) throws DaoException;

	boolean save(Region entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Region enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Region> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Region> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
}
