package com.dahantc.erp.vo.supplierStatistics.service.impl;

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
import com.dahantc.erp.vo.supplierStatistics.dao.ISupplierStatisticsDao;
import com.dahantc.erp.vo.supplierStatistics.entity.SupplierStatistics;
import com.dahantc.erp.vo.supplierStatistics.service.ISupplierStatisticsService;

@Service("supplierStatisticsService")
public class SupplierStatisticsServiceImpl implements ISupplierStatisticsService {
	private static Logger logger = LogManager.getLogger(SupplierStatisticsServiceImpl.class);

	@Autowired
	private ISupplierStatisticsDao supplierStatisticsDao;

	@Override
	public SupplierStatistics read(Serializable id) throws ServiceException {
		try {
			return supplierStatisticsDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商统计详情表失败", e);
			throw new ServiceException("读取供应商统计详情表失败", e);
		}
	}

	@Override
	public boolean save(SupplierStatistics entity) throws ServiceException {
		try {
			return supplierStatisticsDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商统计详情表失败", e);
			throw new ServiceException("保存供应商统计详情表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<SupplierStatistics> objs) throws ServiceException {
		try {
			return supplierStatisticsDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return supplierStatisticsDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商统计详情表失败", e);
			throw new ServiceException("删除供应商统计详情表失败", e);
		}
	}

	@Override
	public boolean update(SupplierStatistics enterprise) throws ServiceException {
		try {
			return supplierStatisticsDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商统计详情表失败", e);
			throw new ServiceException("更新供应商统计详情表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return supplierStatisticsDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表数量失败", e);
			throw new ServiceException("查询供应商统计详情表数量失败", e);
		}
	}

	@Override
	public PageResult<SupplierStatistics> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return supplierStatisticsDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表分页信息失败", e);
			throw new ServiceException("查询供应商统计详情表分页信息失败", e);
		}
	}
	
	@Override
	public List<SupplierStatistics> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return supplierStatisticsDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表失败", e);
			throw new ServiceException("查询供应商统计详情表失败", e);
		}
	}

	@Override
	public List<SupplierStatistics> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return supplierStatisticsDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表失败", e);
			throw new ServiceException("查询供应商统计详情表失败", e);
		}
	}
	
	@Override
	public List<SupplierStatistics> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return supplierStatisticsDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表失败", e);
			throw new ServiceException("查询供应商统计详情表失败", e);
		}
	}
}
