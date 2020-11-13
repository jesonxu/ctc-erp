package com.dahantc.erp.vo.deductionPrice.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.deductionPrice.dao.IDeductionPriceDao;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.deductionPrice.service.IDeductionPriceService;

@Service("deductionPriceService")
public class DeductionPriceServiceImpl implements IDeductionPriceService {
	private static Logger logger = LogManager.getLogger(DeductionPriceServiceImpl.class);

	@Autowired
	private IDeductionPriceDao deductionPriceDao;

	@Override
	public DeductionPrice read(Serializable id) throws ServiceException {
		try {
			return deductionPriceDao.read(id);
		} catch (Exception e) {
			logger.error("读取梯度价格表失败", e);
			throw new ServiceException("读取梯度价格表失败", e);
		}
	}

	@Override
	public boolean save(DeductionPrice entity) throws ServiceException {
		try {
			return deductionPriceDao.save(entity);
		} catch (Exception e) {
			logger.error("保存梯度价格表失败", e);
			throw new ServiceException("保存梯度价格表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return deductionPriceDao.delete(id);
		} catch (Exception e) {
			logger.error("删除梯度价格表失败", e);
			throw new ServiceException("删除梯度价格表失败", e);
		}
	}

	@Override
	public boolean update(DeductionPrice enterprise) throws ServiceException {
		try {
			return deductionPriceDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新梯度价格表失败", e);
			throw new ServiceException("更新梯度价格表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return deductionPriceDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询梯度价格表数量失败", e);
			throw new ServiceException("查询梯度价格表数量失败", e);
		}
	}

	@Override
	public PageResult<DeductionPrice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return deductionPriceDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询梯度价格表分页信息失败", e);
			throw new ServiceException("查询梯度价格表分页信息失败", e);
		}
	}

	@Override
	public List<DeductionPrice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return deductionPriceDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询梯度价格表失败", e);
			throw new ServiceException("查询梯度价格表失败", e);
		}
	}

	@Override
	public List<DeductionPrice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return deductionPriceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询梯度价格表失败", e);
			throw new ServiceException("查询梯度价格表失败", e);
		}
	}

	@Override
	public List<DeductionPrice> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return deductionPriceDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询梯度价格表失败", e);
			throw new ServiceException("查询梯度价格表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DeductionPrice> objs) throws DaoException {
		try {
			return deductionPriceDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error("批量保存梯度价格信息失败", e);
			throw new DaoException(e);
		}
	}
}
