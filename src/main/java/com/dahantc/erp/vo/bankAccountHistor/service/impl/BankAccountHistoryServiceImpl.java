package com.dahantc.erp.vo.bankAccountHistor.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.bankAccountHistor.dao.IBankAccountHistoryDao;
import com.dahantc.erp.vo.bankAccountHistor.entity.BankAccountHistory;
import com.dahantc.erp.vo.bankAccountHistor.service.IBankAccountHistoryService;

@Service("bankAccountHistoryService")
public class BankAccountHistoryServiceImpl implements IBankAccountHistoryService {
	private static Logger logger = LogManager.getLogger(BankAccountHistoryServiceImpl.class);

	@Autowired
	private IBankAccountHistoryDao bankAccountHistoryDao;

	@Override
	public BankAccountHistory read(Serializable id) throws ServiceException {
		try {
			return bankAccountHistoryDao.read(id);
		} catch (Exception e) {
			logger.error("读取银行账户历史信息处理结果表失败", e);
			throw new ServiceException("读取银行账户历史信息处理结果表失败", e);
		}
	}

	@Override
	public boolean save(BankAccountHistory entity) throws ServiceException {
		try {
			return bankAccountHistoryDao.save(entity);
		} catch (Exception e) {
			logger.error("保存银行账户历史信息处理结果表失败", e);
			throw new ServiceException("保存银行账户历史信息处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<BankAccountHistory> objs) throws ServiceException {
		try {
			return bankAccountHistoryDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return bankAccountHistoryDao.delete(id);
		} catch (Exception e) {
			logger.error("删除银行账户历史信息处理结果表失败", e);
			throw new ServiceException("删除银行账户历史信息处理结果表失败", e);
		}
	}

	@Override
	public boolean update(BankAccountHistory enterprise) throws ServiceException {
		try {
			return bankAccountHistoryDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新银行账户历史信息处理结果表失败", e);
			throw new ServiceException("更新银行账户历史信息处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return bankAccountHistoryDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询银行账户历史信息处理结果表数量失败", e);
			throw new ServiceException("查询银行账户历史信息处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<BankAccountHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return bankAccountHistoryDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询银行账户历史信息处理结果表分页信息失败", e);
			throw new ServiceException("查询银行账户历史信息处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<BankAccountHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return bankAccountHistoryDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询银行账户历史信息处理结果表失败", e);
			throw new ServiceException("查询银行账户历史信息处理结果表失败", e);
		}
	}

	@Override
	public List<BankAccountHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return bankAccountHistoryDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询银行账户历史信息处理结果表失败", e);
			throw new ServiceException("查询银行账户历史信息处理结果表失败", e);
		}
	}

	@Override
	public List<BankAccountHistory> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return bankAccountHistoryDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询银行账户历史信息处理结果表失败", e);
			throw new ServiceException("查询银行账户历史信息处理结果表失败", e);
		}
	}
}
