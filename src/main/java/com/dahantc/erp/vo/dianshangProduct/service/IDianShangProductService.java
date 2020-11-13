package com.dahantc.erp.vo.dianshangProduct.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.dsProduct.DsSaveProductDto;
import com.dahantc.erp.vo.dianshangProduct.entity.DianShangProduct;

public interface IDianShangProductService {
	DianShangProduct read(Serializable id) throws ServiceException;

	boolean save(DianShangProduct entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DianShangProduct enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DianShangProduct> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DianShangProduct> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DianShangProduct> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DianShangProduct> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DianShangProduct> objs) throws ServiceException;
	
	BaseResponse<String> saveProduct(DsSaveProductDto dto) throws ServiceException;
}
