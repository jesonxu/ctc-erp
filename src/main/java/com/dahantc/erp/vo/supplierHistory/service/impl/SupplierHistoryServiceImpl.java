package com.dahantc.erp.vo.supplierHistory.service.impl;

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
import com.dahantc.erp.vo.supplierHistory.dao.ISupplierHistoryDao;
import com.dahantc.erp.vo.supplierHistory.entity.SupplierHistory;
import com.dahantc.erp.vo.supplierHistory.service.ISupplierHistoryService;

@Service("supplierHistoryService")
public class SupplierHistoryServiceImpl implements ISupplierHistoryService {
	private static Logger logger = LogManager.getLogger(SupplierHistoryServiceImpl.class);

	@Autowired
	private ISupplierHistoryDao supplierHistoryDao;

	@Override
	public SupplierHistory read(Serializable id) throws ServiceException {
		try {
			return supplierHistoryDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商信息历史表失败", e);
			throw new ServiceException("读取供应商信息历史表失败", e);
		}
	}

	@Override
	public boolean save(SupplierHistory entity) throws ServiceException {
		try {
			return supplierHistoryDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商信息历史表失败", e);
			throw new ServiceException("保存供应商信息历史表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return supplierHistoryDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商信息历史表失败", e);
			throw new ServiceException("删除供应商信息历史表失败", e);
		}
	}

	@Override
	public boolean update(SupplierHistory entity) throws ServiceException {
		try {
			return supplierHistoryDao.update(entity);
		} catch (Exception e) {
			logger.error("更新供应商信息历史表失败", e);
			throw new ServiceException("更新供应商信息历史表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return supplierHistoryDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商信息历史表数量失败", e);
			throw new ServiceException("查询供应商信息历史表数量失败", e);
		}
	}

	@Override
	public PageResult<SupplierHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return supplierHistoryDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商信息历史表分页信息失败", e);
			throw new ServiceException("查询供应商信息历史表分页信息失败", e);
		}
	}

	@Override
	public List<SupplierHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return supplierHistoryDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商信息历史表失败", e);
			throw new ServiceException("查询供应商信息历史表失败", e);
		}
	}

	@Override
	public List<SupplierHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return supplierHistoryDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商信息历史表失败", e);
			throw new ServiceException("查询供应商信息历史表失败", e);
		}
	}

	@Override
	public List<SupplierHistory> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return supplierHistoryDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商信息历史表失败", e);
			throw new ServiceException("查询供应商信息历史表失败", e);
		}
	}
}
