package com.dahantc.erp.vo.bankAccount.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.vo.BankInfoVo;

public interface IBankAccountService {
	BankAccount read(Serializable id) throws ServiceException;

	boolean save(BankAccount entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(BankAccount enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<BankAccount> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<BankAccount> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<BankAccount> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<BankAccount> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<BankAccount> objs) throws ServiceException;

	/**
	 * 根据银行信息类型 查询银行信息
	 *
	 * @param basicId 供应商 / 客户id
	 * @param type 类型
	 * @return 银行信息
	 */
	List<BankInfoVo> queryBankAccountByType(String basicId,int type);
}
