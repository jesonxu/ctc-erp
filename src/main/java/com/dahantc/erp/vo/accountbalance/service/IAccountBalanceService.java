package com.dahantc.erp.vo.accountbalance.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.accountbalance.entity.AccountBalance;

public interface IAccountBalanceService {
	AccountBalance read(Serializable id) throws ServiceException;

	boolean save(AccountBalance entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(AccountBalance enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<AccountBalance> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<AccountBalance> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<AccountBalance> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<AccountBalance> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<AccountBalance> objs) throws ServiceException;

	BigDecimal queryTotalBalance(Date endDate, OnlineUser onlineUser);
}
