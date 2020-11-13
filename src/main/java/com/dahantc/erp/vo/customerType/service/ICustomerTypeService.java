package com.dahantc.erp.vo.customerType.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.vo.customerType.entity.CustomerType;

public interface ICustomerTypeService {
	CustomerType read(Serializable id) throws ServiceException;

	boolean save(CustomerType entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CustomerType enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<CustomerType> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<CustomerType> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CustomerType> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<CustomerType> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<CustomerType> objs) throws ServiceException;

	/**
	 * 查询所有的客户类型
	 * @return List<CustomerType>
	 */
	List<CustomerType> findAllCustomerType();

	/**
	 * 统计供应商类型 未处理流程
	 *
	 * @param customers 供应商
	 * @return 供应商类型
	 */
	List<CustomerTypeRespDto> countCustomerType(List<CustomerRespDto> customers);

	String getCustomerTypeIdByValue(int value);

	CustomerType getCustomerTypeByValue(int value);

	CustomerType readOneByProperty(String property, Object value) throws ServiceException;

	boolean validatePublicType(String customerTypeId);
}
