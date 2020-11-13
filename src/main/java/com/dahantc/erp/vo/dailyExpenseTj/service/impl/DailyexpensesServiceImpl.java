package com.dahantc.erp.vo.dailyExpenseTj.service.impl;

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
import com.dahantc.erp.vo.dailyExpenseTj.dao.IDailyexpensesDao;
import com.dahantc.erp.vo.dailyExpenseTj.entity.Dailyexpenses;
import com.dahantc.erp.vo.dailyExpenseTj.service.IDailyexpensesService;

@Service("DailyexpensesService")
public class DailyexpensesServiceImpl implements IDailyexpensesService {
	private static Logger logger = LogManager.getLogger(DailyexpensesServiceImpl.class);

	@Autowired
	private IDailyexpensesDao DailyexpensesDao;

	@Override
	public Dailyexpenses read(Serializable id) throws ServiceException {
		try {
			return DailyexpensesDao.read(id);
		} catch (Exception e) {
			logger.error("读取日常费用统计表失败", e);
			throw new ServiceException("读取日常费用统计表失败", e);
		}
	}

	@Override
	public boolean save(Dailyexpenses entity) throws ServiceException {
		try {
			return DailyexpensesDao.save(entity);
		} catch (Exception e) {
			logger.error("保存日常费用统计表失败", e);
			throw new ServiceException("保存日常费用统计表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Dailyexpenses> objs) throws ServiceException {
		try {
			return DailyexpensesDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return DailyexpensesDao.delete(id);
		} catch (Exception e) {
			logger.error("删除日常费用统计表失败", e);
			throw new ServiceException("删除日常费用统计表失败", e);
		}
	}

	@Override
	public boolean update(Dailyexpenses enterprise) throws ServiceException {
		try {
			return DailyexpensesDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新日常费用统计表失败", e);
			throw new ServiceException("更新日常费用统计表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return DailyexpensesDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询日常费用统计表数量失败", e);
			throw new ServiceException("查询日常费用统计表数量失败", e);
		}
	}

	@Override
	public PageResult<Dailyexpenses> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return DailyexpensesDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询日常费用统计表分页信息失败", e);
			throw new ServiceException("查询日常费用统计表分页信息失败", e);
		}
	}
	
	@Override
	public List<Dailyexpenses> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return DailyexpensesDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询日常费用统计表失败", e);
			throw new ServiceException("查询日常费用统计表失败", e);
		}
	}

	@Override
	public List<Dailyexpenses> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return DailyexpensesDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询日常费用统计表失败", e);
			throw new ServiceException("查询日常费用统计表失败", e);
		}
	}
	
	@Override
	public List<Dailyexpenses> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return DailyexpensesDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询日常费用统计表失败", e);
			throw new ServiceException("查询日常费用统计表失败", e);
		}
	}
}
