package com.dahantc.erp.vo.deductionPrice.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;

public interface IDeductionPriceService {
	DeductionPrice read(Serializable id) throws ServiceException;

	boolean save(DeductionPrice entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DeductionPrice enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DeductionPrice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<DeductionPrice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DeductionPrice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<DeductionPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<DeductionPrice> objs) throws DaoException;
}
