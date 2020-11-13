package com.dahantc.erp.vo.dsDepotItem.service.impl;

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
import com.dahantc.erp.vo.dsDepotItem.dao.IDsDepotItemDao;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;
import com.dahantc.erp.vo.dsDepotItem.service.IDsDepotItemService;

@Service("dsDepotItemService")
public class DsDepotItemServiceImpl implements IDsDepotItemService {
	private static Logger logger = LogManager.getLogger(DsDepotItemServiceImpl.class);

	@Autowired
	private IDsDepotItemDao dsDepotItemDao;

	@Override
	public DsDepotItem read(Serializable id) throws ServiceException {
		try {
			return dsDepotItemDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商入库详情表失败", e);
			throw new ServiceException("读取电商入库详情表失败", e);
		}
	}

	@Override
	public boolean save(DsDepotItem entity) throws ServiceException {
		try {
			return dsDepotItemDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商入库详情表失败", e);
			throw new ServiceException("保存电商入库详情表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsDepotItem> objs) throws ServiceException {
		try {
			return dsDepotItemDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsDepotItemDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商入库详情表失败", e);
			throw new ServiceException("删除电商入库详情表失败", e);
		}
	}

	@Override
	public boolean update(DsDepotItem enterprise) throws ServiceException {
		try {
			return dsDepotItemDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商入库详情表失败", e);
			throw new ServiceException("更新电商入库详情表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsDepotItemDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商入库详情表数量失败", e);
			throw new ServiceException("查询电商入库详情表数量失败", e);
		}
	}

	@Override
	public PageResult<DsDepotItem> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsDepotItemDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商入库详情表分页信息失败", e);
			throw new ServiceException("查询电商入库详情表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsDepotItem> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsDepotItemDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商入库详情表失败", e);
			throw new ServiceException("查询电商入库详情表失败", e);
		}
	}

	@Override
	public List<DsDepotItem> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsDepotItemDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商入库详情表失败", e);
			throw new ServiceException("查询电商入库详情表失败", e);
		}
	}
	
	@Override
	public List<DsDepotItem> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsDepotItemDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商入库详情表失败", e);
			throw new ServiceException("查询电商入库详情表失败", e);
		}
	}
}
