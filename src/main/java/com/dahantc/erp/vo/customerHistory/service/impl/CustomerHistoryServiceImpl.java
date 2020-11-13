package com.dahantc.erp.vo.customerHistory.service.impl;

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
import com.dahantc.erp.vo.customerHistory.dao.ICustomerHistoryDao;
import com.dahantc.erp.vo.customerHistory.entity.CustomerHistory;
import com.dahantc.erp.vo.customerHistory.service.ICustomerHistoryService;

@Service("customerHistoryService")
public class CustomerHistoryServiceImpl implements ICustomerHistoryService {
	private static Logger logger = LogManager.getLogger(CustomerHistoryServiceImpl.class);

	@Autowired
	private ICustomerHistoryDao customerHistoryDao;

	@Override
	public CustomerHistory read(Serializable id) throws ServiceException {
		try {
			return customerHistoryDao.read(id);
		} catch (Exception e) {
			logger.error("读取客户历史表失败", e);
			throw new ServiceException("读取客户历史表失败", e);
		}
	}

	@Override
	public boolean save(CustomerHistory entity) throws ServiceException {
		try {
			return customerHistoryDao.save(entity);
		} catch (Exception e) {
			logger.error("保存客户历史表失败", e);
			throw new ServiceException("保存客户历史表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<CustomerHistory> objs) throws ServiceException {
		try {
			return customerHistoryDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return customerHistoryDao.delete(id);
		} catch (Exception e) {
			logger.error("删除客户历史表失败", e);
			throw new ServiceException("删除客户历史表失败", e);
		}
	}

	@Override
	public boolean update(CustomerHistory enterprise) throws ServiceException {
		try {
			return customerHistoryDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新客户历史表失败", e);
			throw new ServiceException("更新客户历史表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return customerHistoryDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询客户历史表数量失败", e);
			throw new ServiceException("查询客户历史表数量失败", e);
		}
	}

	@Override
	public PageResult<CustomerHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerHistoryDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询客户历史表分页信息失败", e);
			throw new ServiceException("查询客户历史表分页信息失败", e);
		}
	}
	
	@Override
	public List<CustomerHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return customerHistoryDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询客户历史表失败", e);
			throw new ServiceException("查询客户历史表失败", e);
		}
	}

	@Override
	public List<CustomerHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return customerHistoryDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户历史表失败", e);
			throw new ServiceException("查询客户历史表失败", e);
		}
	}
	
	@Override
	public List<CustomerHistory> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return customerHistoryDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询客户历史表失败", e);
			throw new ServiceException("查询客户历史表失败", e);
		}
	}
}
