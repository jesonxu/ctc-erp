package com.dahantc.erp.vo.product.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.product.SaveProductReqDto;
import com.dahantc.erp.vo.product.entity.Product;

public interface IProductService {
	Product read(Serializable id) throws ServiceException;

	boolean save(Product entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Product enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Product> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<Product> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	BaseResponse<String> saveProduct(SaveProductReqDto dto) throws ServiceException;

	String getReachProvinces(Product product) throws ServiceException;

	String getBaseProvince(Product product) throws ServiceException;

	/**
	 * 查找客户名称
	 * 
	 * @param ids
	 *            id
	 * @return 客户名称
	 */
	Map<String, String> findProductName(List<String> ids);
}
