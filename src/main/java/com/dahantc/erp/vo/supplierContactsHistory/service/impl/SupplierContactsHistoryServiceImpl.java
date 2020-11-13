package com.dahantc.erp.vo.supplierContactsHistory.service.impl;

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
import com.dahantc.erp.vo.supplierContactsHistory.dao.ISupplierContactsHistoryDao;
import com.dahantc.erp.vo.supplierContactsHistory.entity.SupplierContactsHistory;
import com.dahantc.erp.vo.supplierContactsHistory.service.ISupplierContactsHistoryService;

@Service("supplierContactsHistoryService")
public class SupplierContactsHistoryServiceImpl implements ISupplierContactsHistoryService {
	private static Logger logger = LogManager.getLogger(SupplierContactsHistoryServiceImpl.class);

	@Autowired
	private ISupplierContactsHistoryDao supplierContactsHistoryDao;

	@Override
	public SupplierContactsHistory read(Serializable id) throws ServiceException {
		try {
			return supplierContactsHistoryDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商联系人历史表失败", e);
			throw new ServiceException("读取供应商联系人历史表失败", e);
		}
	}

	@Override
	public boolean save(SupplierContactsHistory entity) throws ServiceException {
		try {
			return supplierContactsHistoryDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商联系人历史表失败", e);
			throw new ServiceException("保存供应商联系人历史表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return supplierContactsHistoryDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商联系人历史表失败", e);
			throw new ServiceException("删除供应商联系人历史表失败", e);
		}
	}

	@Override
	public boolean update(SupplierContactsHistory enterprise) throws ServiceException {
		try {
			return supplierContactsHistoryDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商联系人历史表失败", e);
			throw new ServiceException("更新供应商联系人历史表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsHistoryDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人历史表数量失败", e);
			throw new ServiceException("查询供应商联系人历史表数量失败", e);
		}
	}

	@Override
	public PageResult<SupplierContactsHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsHistoryDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人历史表分页信息失败", e);
			throw new ServiceException("查询供应商联系人历史表分页信息失败", e);
		}
	}

	@Override
	public List<SupplierContactsHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsHistoryDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人历史表失败", e);
			throw new ServiceException("查询供应商联系人历史表失败", e);
		}
	}

	@Override
	public List<SupplierContactsHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsHistoryDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人历史表失败", e);
			throw new ServiceException("查询供应商联系人历史表失败", e);
		}
	}

	@Override
	public List<SupplierContactsHistory> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return supplierContactsHistoryDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商联系人历史表失败", e);
			throw new ServiceException("查询供应商联系人历史表失败", e);
		}
	}
}
