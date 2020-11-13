package com.dahantc.erp.vo.saleAnalysisStatistics.service.impl;

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
import com.dahantc.erp.vo.saleAnalysisStatistics.dao.ISaleAnalysisDao;
import com.dahantc.erp.vo.saleAnalysisStatistics.entity.SaleAnalysis;
import com.dahantc.erp.vo.saleAnalysisStatistics.service.ISaleAnalysisService;

@Service("SaleAnalysisService")
public class SaleAnalysisServiceImpl implements ISaleAnalysisService {
	private static Logger logger = LogManager.getLogger(SaleAnalysisServiceImpl.class);

	@Autowired
	private ISaleAnalysisDao SaleAnalysisDao;

	@Override
	public SaleAnalysis read(Serializable id) throws ServiceException {
		try {
			return SaleAnalysisDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商统计详情表失败", e);
			throw new ServiceException("读取供应商统计详情表失败", e);
		}
	}

	@Override
	public boolean save(SaleAnalysis entity) throws ServiceException {
		try {
			return SaleAnalysisDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商统计详情表失败", e);
			throw new ServiceException("保存供应商统计详情表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<SaleAnalysis> objs) throws ServiceException {
		try {
			return SaleAnalysisDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return SaleAnalysisDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商统计详情表失败", e);
			throw new ServiceException("删除供应商统计详情表失败", e);
		}
	}

	@Override
	public boolean update(SaleAnalysis enterprise) throws ServiceException {
		try {
			return SaleAnalysisDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商统计详情表失败", e);
			throw new ServiceException("更新供应商统计详情表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return SaleAnalysisDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表数量失败", e);
			throw new ServiceException("查询供应商统计详情表数量失败", e);
		}
	}

	@Override
	public PageResult<SaleAnalysis> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return SaleAnalysisDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表分页信息失败", e);
			throw new ServiceException("查询供应商统计详情表分页信息失败", e);
		}
	}
	
	@Override
	public List<SaleAnalysis> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return SaleAnalysisDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表失败", e);
			throw new ServiceException("查询供应商统计详情表失败", e);
		}
	}

	@Override
	public List<SaleAnalysis> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return SaleAnalysisDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表失败", e);
			throw new ServiceException("查询供应商统计详情表失败", e);
		}
	}
	
	@Override
	public List<SaleAnalysis> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return SaleAnalysisDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商统计详情表失败", e);
			throw new ServiceException("查询供应商统计详情表失败", e);
		}
	}
}
