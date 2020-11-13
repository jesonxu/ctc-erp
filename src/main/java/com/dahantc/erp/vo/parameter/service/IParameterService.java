package com.dahantc.erp.vo.parameter.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.parameter.entity.Parameter;

public interface IParameterService {
	Parameter read(Serializable id) throws ServiceException;

	Parameter readOneByProperty(String property, Object value) throws ServiceException;

	boolean save(Parameter entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Parameter enterprise) throws ServiceException;

	PageResult<Parameter> findByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Parameter> findAllByCriteria(SearchFilter filter) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	boolean saveByBatch(List<Parameter> objs) throws DaoException;

	List<Parameter> readByIds(List<String> entitieIds);

	Parameter getOneParameterByProperty(String propertyName, Object propertyValue) throws ServiceException;

	BigDecimal getPenaltyInterestRatio();

	String getSysParam(String key);

}
