package com.dahantc.erp.vo.suppliertype.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.SupplierType.SupplierTypeRspDto;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;

public interface ISupplierTypeService {
	SupplierType read(Serializable id) throws ServiceException;

	boolean save(SupplierType entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SupplierType enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SupplierType> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<SupplierType> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SupplierType> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<SupplierType> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	/**
	 * 查询所有的供应商类型数据
	 *
	 * @return 供应商类型
	 */
	List<SupplierType> queryAll();

	/**
	 * 统计供应商类型 未处理流程
	 *
	 * @param suppliers
	 *            供应商
	 * @return 供应商类型
	 */
	List<SupplierTypeRspDto> countSupplierTypes(List<SupplierRspDto> suppliers);
}
