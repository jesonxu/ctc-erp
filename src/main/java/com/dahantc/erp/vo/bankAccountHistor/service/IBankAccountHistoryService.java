package com.dahantc.erp.vo.bankAccountHistor.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.bankAccountHistor.entity.BankAccountHistory;

public interface IBankAccountHistoryService {
	BankAccountHistory read(Serializable id) throws ServiceException;

	boolean save(BankAccountHistory entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(BankAccountHistory enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<BankAccountHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<BankAccountHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<BankAccountHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<BankAccountHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<BankAccountHistory> objs) throws ServiceException;
}
