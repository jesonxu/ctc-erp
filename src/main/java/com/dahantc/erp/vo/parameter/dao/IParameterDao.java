package com.dahantc.erp.vo.parameter.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.parameter.entity.Parameter;


public interface IParameterDao {
	Parameter read(Serializable id) throws DaoException;
	
	Parameter readOneByProperty(String property, Object value) throws DaoException;

	boolean save(Parameter entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Parameter enterprise) throws DaoException;

	int getCount(SearchFilter filter) throws DaoException;

	PageResult<Parameter> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Parameter> findAllByCriteria(SearchFilter filter) throws DaoException;
}
