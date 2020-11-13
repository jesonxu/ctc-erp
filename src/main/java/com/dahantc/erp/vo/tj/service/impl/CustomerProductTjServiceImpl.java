package com.dahantc.erp.vo.tj.service.impl;

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
import com.dahantc.erp.vo.tj.dao.ICustomerProductTjDao;
import com.dahantc.erp.vo.tj.entity.CustomerProductTj;
import com.dahantc.erp.vo.tj.service.ICustomerProductTjService;

@Service("customerProductTjService")
public class CustomerProductTjServiceImpl implements ICustomerProductTjService {
	private static Logger logger = LogManager.getLogger(CustomerProductTjServiceImpl.class);

	@Autowired
	private ICustomerProductTjDao customerProductTjDao;

	@Override
	public CustomerProductTj read(Serializable id) throws ServiceException {
		try {
			return customerProductTjDao.read(id);
		} catch (Exception e) {
			logger.error("读取客户统计明细表失败", e);
			throw new ServiceException("读取客户统计明细表失败", e);
		}
	}

	@Override
	public boolean save(CustomerProductTj entity) throws ServiceException {
		try {
			return customerProductTjDao.save(entity);
		} catch (Exception e) {
			logger.error("保存客户统计明细表失败", e);
			throw new ServiceException("保存客户统计明细表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<CustomerProductTj> objs) throws ServiceException {
		try {
			return customerProductTjDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return customerProductTjDao.delete(id);
		} catch (Exception e) {
			logger.error("删除客户统计明细表失败", e);
			throw new ServiceException("删除客户统计明细表失败", e);
		}
	}

	@Override
	public boolean update(CustomerProductTj enterprise) throws ServiceException {
		try {
			return customerProductTjDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新客户统计明细表失败", e);
			throw new ServiceException("更新客户统计明细表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return customerProductTjDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询客户统计明细表数量失败", e);
			throw new ServiceException("查询客户统计明细表数量失败", e);
		}
	}

	@Override
	public PageResult<CustomerProductTj> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerProductTjDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询客户统计明细表分页信息失败", e);
			throw new ServiceException("查询客户统计明细表分页信息失败", e);
		}
	}
	
	@Override
	public List<CustomerProductTj> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return customerProductTjDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询客户统计明细表失败", e);
			throw new ServiceException("查询客户统计明细表失败", e);
		}
	}

	@Override
	public List<CustomerProductTj> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return customerProductTjDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户统计明细表失败", e);
			throw new ServiceException("查询客户统计明细表失败", e);
		}
	}
	
	@Override
	public List<CustomerProductTj> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return customerProductTjDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询客户统计明细表失败", e);
			throw new ServiceException("查询客户统计明细表失败", e);
		}
	}
}
