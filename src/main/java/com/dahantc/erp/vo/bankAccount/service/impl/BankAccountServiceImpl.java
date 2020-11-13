package com.dahantc.erp.vo.bankAccount.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.vo.BankInfoVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.bankAccount.dao.IBankAccountDao;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;

@Service("bankAccountService")
public class BankAccountServiceImpl implements IBankAccountService {
	private static Logger logger = LogManager.getLogger(BankAccountServiceImpl.class);

	@Autowired
	private IBankAccountDao bankAccountDao;

	@Override
	public BankAccount read(Serializable id) throws ServiceException {
		try {
			return bankAccountDao.read(id);
		} catch (Exception e) {
			logger.error("读取银行账户信息处理结果表失败", e);
			throw new ServiceException("读取银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public boolean save(BankAccount entity) throws ServiceException {
		try {
			return bankAccountDao.save(entity);
		} catch (Exception e) {
			logger.error("保存银行账户信息处理结果表失败", e);
			throw new ServiceException("保存银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<BankAccount> objs) throws ServiceException {
		try {
			return bankAccountDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return bankAccountDao.delete(id);
		} catch (Exception e) {
			logger.error("删除银行账户信息处理结果表失败", e);
			throw new ServiceException("删除银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public boolean update(BankAccount enterprise) throws ServiceException {
		try {
			return bankAccountDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新银行账户信息处理结果表失败", e);
			throw new ServiceException("更新银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return bankAccountDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询银行账户信息处理结果表数量失败", e);
			throw new ServiceException("查询银行账户信息处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<BankAccount> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return bankAccountDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询银行账户信息处理结果表分页信息失败", e);
			throw new ServiceException("查询银行账户信息处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<BankAccount> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return bankAccountDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询银行账户信息处理结果表失败", e);
			throw new ServiceException("查询银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public List<BankAccount> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return bankAccountDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询银行账户信息处理结果表失败", e);
			throw new ServiceException("查询银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public List<BankAccount> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return bankAccountDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询银行账户信息处理结果表失败", e);
			throw new ServiceException("查询银行账户信息处理结果表失败", e);
		}
	}

	@Override
	public List<BankInfoVo> queryBankAccountByType(String basicId,int type) {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, type));
		if (StringUtil.isNotBlank(basicId)){
			searchFilter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, basicId));
		}
		searchFilter.getOrders().add(new SearchOrder("wtime",Constants.ROP_ASC));
		List<BankAccount> bankAccountList = null;
		try {
			bankAccountList = this.queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("根据类型TYPE{}查询银行信息的时候出现异常{}", type, e);
		}
		if (bankAccountList == null || bankAccountList.isEmpty()) {
			return null;
		}
		return bankAccountList.stream().map(BankInfoVo::new).collect(Collectors.toList());
	}
}
