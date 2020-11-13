package com.dahantc.erp.vo.accountbalance.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.accountbalance.dao.IAccountBalanceDao;
import com.dahantc.erp.vo.accountbalance.entity.AccountBalance;
import com.dahantc.erp.vo.accountbalance.service.IAccountBalanceService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;

@Service("accountBalanceService")
public class AccountBalanceServiceImpl implements IAccountBalanceService {

	private static Logger logger = LogManager.getLogger(AccountBalanceServiceImpl.class);

	@Autowired
	private IAccountBalanceDao accountBalanceDao;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Override
	public AccountBalance read(Serializable id) throws ServiceException {
		try {
			return accountBalanceDao.read(id);
		} catch (Exception e) {
			logger.error("读取账户余额处理结果表失败", e);
			throw new ServiceException("读取账户余额处理结果表失败", e);
		}
	}

	@Override
	public boolean save(AccountBalance entity) throws ServiceException {
		try {
			return accountBalanceDao.save(entity);
		} catch (Exception e) {
			logger.error("保存账户余额处理结果表失败", e);
			throw new ServiceException("保存账户余额处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<AccountBalance> objs) throws ServiceException {
		try {
			return accountBalanceDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return accountBalanceDao.delete(id);
		} catch (Exception e) {
			logger.error("删除账户余额处理结果表失败", e);
			throw new ServiceException("删除账户余额处理结果表失败", e);
		}
	}

	@Override
	public boolean update(AccountBalance enterprise) throws ServiceException {
		try {
			return accountBalanceDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新账户余额处理结果表失败", e);
			throw new ServiceException("更新账户余额处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return accountBalanceDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询账户余额处理结果表数量失败", e);
			throw new ServiceException("查询账户余额处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<AccountBalance> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return accountBalanceDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询账户余额处理结果表分页信息失败", e);
			throw new ServiceException("查询账户余额处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<AccountBalance> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return accountBalanceDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询账户余额处理结果表失败", e);
			throw new ServiceException("查询账户余额处理结果表失败", e);
		}
	}

	@Override
	public List<AccountBalance> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return accountBalanceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询账户余额处理结果表失败", e);
			throw new ServiceException("查询账户余额处理结果表失败", e);
		}
	}

	@Override
	public List<AccountBalance> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return accountBalanceDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询账户余额处理结果表失败", e);
			throw new ServiceException("查询账户余额处理结果表失败", e);
		}
	}

	/**
	 * 查用户数据权限下所有客户在某一天的余额
	 * 
	 * @param endDate
	 *            日期
	 * @param onlineUser
	 *            当前用户
	 * @return
	 */
	@Override
	public BigDecimal queryTotalBalance(Date endDate, OnlineUser onlineUser) {
		logger.info("查询客户余额开始");
		BigDecimal totalBalance = new BigDecimal(0);
		List<Customer> customerList = customerService.readCustomers(onlineUser, null, null, null, null);
		if (CollectionUtils.isEmpty(customerList)) {
			logger.info("用户数据权限下没有客户");
			return totalBalance;
		}
		List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		List<String> loginNameList = new ArrayList<>(customerProductService.queryLoginNameByCustomer(customerIdList));
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("account", Constants.ROP_IN, loginNameList));
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_EQ, endDate));
		try {
			List<AccountBalance> accountBalanceList = queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(accountBalanceList)) {
				return totalBalance;
			}
			for (AccountBalance accountBalance : accountBalanceList) {
				totalBalance = totalBalance.add(accountBalance.getAccountBalance());
			}
		} catch (ServiceException e) {
			logger.error("查询客户余额异常", e);
		}
		return totalBalance;
	}
}
