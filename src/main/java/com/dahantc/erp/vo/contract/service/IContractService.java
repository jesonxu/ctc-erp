package com.dahantc.erp.vo.contract.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.contract.entity.Contract;

public interface IContractService {
	Contract read(Serializable id) throws ServiceException;

	boolean save(Contract entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Contract enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Contract> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<Contract> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Contract> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<Contract> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<Contract> objs) throws ServiceException;

	String buildContractNumber(int entityType, int productType);

	/**
	 * 查询主体对应的合同信息
	 * 
	 * @param entityIds
	 *            主体ID
	 * @param entityType
	 *            主体类型
	 * @return 合同信息
	 */
	Map<String,List<Contract>> findEntityContract(List<String> entityIds,Integer entityType);
}
