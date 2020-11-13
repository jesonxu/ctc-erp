package com.dahantc.erp.vo.productType.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.productType.entity.ProductType;

public interface IProductTypeService {
	ProductType read(Serializable id) throws ServiceException;

	boolean save(ProductType entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(ProductType enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<ProductType> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<ProductType> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<ProductType> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<ProductType> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<ProductType> objs) throws ServiceException;

	ProductType readOneByProperty(String property, Object value) throws ServiceException;

	String getProductTypeNameByValue(int productTypeValue);

	String getProductTypeKeyByValue(int productTypeValue);

	Integer getProductTypeValueByName(String productTypeName);

	Integer getProductTypeValueByKey(String productTypeKey);

	Integer getCostPriceType(int productTypeValue);
}
