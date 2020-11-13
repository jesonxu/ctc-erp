package com.dahantc.erp.vo.customerProduct.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customerProduct.SaveCustomerProductDto;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;

public interface ICustomerProductService {
	CustomerProduct read(Serializable id) throws ServiceException;

	boolean save(CustomerProduct entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CustomerProduct entity) throws ServiceException;

	BaseResponse<String> saveProduct(SaveCustomerProductDto dto) throws ServiceException;

	int getCountByCriteria(SearchFilter filter) throws ServiceException;

	PageResult<CustomerProduct> findByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<CustomerProduct> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CustomerProduct> queryAllByFilter(SearchFilter filter) throws ServiceException;

	List<CustomerProduct> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<CustomerProduct> objs) throws ServiceException;

	boolean updateByBatch(List<CustomerProduct> objs) throws ServiceException;

	BigDecimal queryCustomerProductCost(Timestamp startDate, Timestamp endDate, List<String> loginNameList, int productType, String yysType);

	PageResult<CustomerProduct> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	Set<String> queryLoginNameByCustomer(List<String> customerIdList);

	Set<String> queryLoginNameByProduct(List<CustomerProduct> productList);

	/**
	 * 客户的产品中 是否含有账号（多个产品，只要其中一个有账号就算）
	 *
	 * @param customerIds
	 *            客户id
	 * @return 客户产品还有账号的情况
	 */
	Map<String,Boolean> customerProductHasProduct(List<String> customerIds);

	Map<String, Map<String, Object>> getProductAndCustomerInfo();

	/**
	 * 根据id 查询产品名称
	 *
	 * @param ids
	 *            id
	 * @return 名称
	 */
	Map<String, String> findProductName(List<String> ids);

	List<String> getProductYysType(CustomerProduct product, boolean toNormal);
}
