package com.dahantc.erp.vo.cashflow.service.impl;

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
import com.dahantc.erp.vo.cashflow.dao.ICashFlowDao;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;
import com.dahantc.erp.vo.cashflow.service.ICashFlowService;

@Service("cashflowService")
public class CashFlowServiceImpl implements ICashFlowService {
	private static Logger logger = LogManager.getLogger(CashFlowServiceImpl.class);

	@Autowired
	private ICashFlowDao cashflowDao;

	@Override
	public CashFlow read(Serializable id) throws ServiceException {
		try {
			return cashflowDao.read(id);
		} catch (Exception e) {
			logger.error("读取现金流表失败", e);
			throw new ServiceException("读取现金流表失败", e);
		}
	}

	@Override
	public boolean save(CashFlow entity) throws ServiceException {
		try {
			return cashflowDao.save(entity);
		} catch (Exception e) {
			logger.error("保存现金流表失败", e);
			throw new ServiceException("保存现金流表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<CashFlow> objs) throws ServiceException {
		try {
			return cashflowDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return cashflowDao.delete(id);
		} catch (Exception e) {
			logger.error("删除现金流表失败", e);
			throw new ServiceException("删除现金流表失败", e);
		}
	}

	@Override
	public boolean update(CashFlow enterprise) throws ServiceException {
		try {
			return cashflowDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新现金流表失败", e);
			throw new ServiceException("更新现金流表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return cashflowDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询现金流表数量失败", e);
			throw new ServiceException("查询现金流表数量失败", e);
		}
	}

	@Override
	public PageResult<CashFlow> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return cashflowDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询现金流表分页信息失败", e);
			throw new ServiceException("查询现金流表分页信息失败", e);
		}
	}

	@Override
	public List<CashFlow> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return cashflowDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询现金流表失败", e);
			throw new ServiceException("查询现金流表失败", e);
		}
	}

	@Override
	public List<CashFlow> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return cashflowDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询现金流表失败", e);
			throw new ServiceException("查询现金流表失败", e);
		}
	}

	@Override
	public List<CashFlow> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return cashflowDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询现金流表失败", e);
			throw new ServiceException("查询现金流表失败", e);
		}
	}
}
