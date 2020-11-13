package com.dahantc.erp.vo.dsSaleData.service.impl;

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
import com.dahantc.erp.vo.dsSaleData.dao.IDsSaleDataDao;
import com.dahantc.erp.vo.dsSaleData.entity.DsSaleData;
import com.dahantc.erp.vo.dsSaleData.service.IDsSaleDataService;

@Service("dsSaleDataService")
public class DsSaleDataServiceImpl implements IDsSaleDataService {
	private static Logger logger = LogManager.getLogger(DsSaleDataServiceImpl.class);

	@Autowired
	private IDsSaleDataDao dsSaleDataDao;

	@Override
	public DsSaleData read(Serializable id) throws ServiceException {
		try {
			return dsSaleDataDao.read(id);
		} catch (Exception e) {
			logger.error("读取销售数据统计表失败", e);
			throw new ServiceException("读取销售数据统计表失败", e);
		}
	}

	@Override
	public boolean save(DsSaleData entity) throws ServiceException {
		try {
			return dsSaleDataDao.save(entity);
		} catch (Exception e) {
			logger.error("保存销售数据统计表失败", e);
			throw new ServiceException("保存销售数据统计表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsSaleData> objs) throws ServiceException {
		try {
			return dsSaleDataDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsSaleDataDao.delete(id);
		} catch (Exception e) {
			logger.error("删除销售数据统计表失败", e);
			throw new ServiceException("删除销售数据统计表失败", e);
		}
	}

	@Override
	public boolean update(DsSaleData enterprise) throws ServiceException {
		try {
			return dsSaleDataDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新销售数据统计表失败", e);
			throw new ServiceException("更新销售数据统计表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsSaleDataDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询销售数据统计表数量失败", e);
			throw new ServiceException("查询销售数据统计表数量失败", e);
		}
	}

	@Override
	public PageResult<DsSaleData> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsSaleDataDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询销售数据统计表分页信息失败", e);
			throw new ServiceException("查询销售数据统计表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsSaleData> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsSaleDataDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询销售数据统计表失败", e);
			throw new ServiceException("查询销售数据统计表失败", e);
		}
	}

	@Override
	public List<DsSaleData> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsSaleDataDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询销售数据统计表失败", e);
			throw new ServiceException("查询销售数据统计表失败", e);
		}
	}
	
	@Override
	public List<DsSaleData> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsSaleDataDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询销售数据统计表失败", e);
			throw new ServiceException("查询销售数据统计表失败", e);
		}
	}
}
