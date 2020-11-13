package com.dahantc.erp.vo.unitPrice.service.impl;

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
import com.dahantc.erp.vo.unitPrice.dao.IUnitPriceDao;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;

@Service("unitPriceService")
public class UnitPriceServiceImpl implements IUnitPriceService {
	private static Logger logger = LogManager.getLogger(UnitPriceServiceImpl.class);

	@Autowired
	private IUnitPriceDao unitPriceDao;

	@Override
	public UnitPrice read(Serializable id) throws ServiceException {
		try {
			return unitPriceDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商产品价格处理结果表失败", e);
			throw new ServiceException("读取供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public boolean save(UnitPrice entity) throws ServiceException {
		try {
			return unitPriceDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商产品价格处理结果表失败", e);
			throw new ServiceException("保存供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<UnitPrice> objs) throws ServiceException {
		try {
			return unitPriceDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return unitPriceDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商产品价格处理结果表失败", e);
			throw new ServiceException("删除供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public boolean update(UnitPrice enterprise) throws ServiceException {
		try {
			return unitPriceDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商产品价格处理结果表失败", e);
			throw new ServiceException("更新供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return unitPriceDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商产品价格处理结果表数量失败", e);
			throw new ServiceException("查询供应商产品价格处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<UnitPrice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return unitPriceDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商产品价格处理结果表分页信息失败", e);
			throw new ServiceException("查询供应商产品价格处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<UnitPrice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return unitPriceDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商产品价格处理结果表失败", e);
			throw new ServiceException("查询供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public List<UnitPrice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return unitPriceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商产品价格处理结果表失败", e);
			throw new ServiceException("查询供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public List<UnitPrice> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return unitPriceDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商产品价格处理结果表失败", e);
			throw new ServiceException("查询供应商产品价格处理结果表失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<UnitPrice> objs) throws ServiceException {
		try {
			return unitPriceDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
