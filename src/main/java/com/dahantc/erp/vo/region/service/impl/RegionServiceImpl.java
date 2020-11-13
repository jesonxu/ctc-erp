package com.dahantc.erp.vo.region.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.region.dao.IRegionDao;
import com.dahantc.erp.vo.region.entity.Region;
import com.dahantc.erp.vo.region.service.IRegionService;

@Service("regionService")
public class RegionServiceImpl implements IRegionService {
	private static Logger logger = LogManager.getLogger(RegionServiceImpl.class);

	@Autowired
	private IRegionDao regionDao;

	@Override
	public Region read(Serializable id) throws ServiceException {
		try {
			return regionDao.read(id);
		} catch (Exception e) {
			logger.error("读取归属地失败", e);
			throw new ServiceException("读取归属地失败", e);
		}
	}

	@Override
	public boolean save(Region entity) throws ServiceException {
		try {
			return regionDao.save(entity);
		} catch (Exception e) {
			logger.error("保存归属地失败", e);
			throw new ServiceException("保存归属地失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return regionDao.delete(id);
		} catch (Exception e) {
			logger.error("删除归属地失败", e);
			throw new ServiceException("删除归属地失败", e);
		}
	}

	@Override
	public boolean update(Region enterprise) throws ServiceException {
		try {
			return regionDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新归属地失败", e);
			throw new ServiceException("更新归属地失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return regionDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询归属地数量失败", e);
			throw new ServiceException("查询归属地数量失败", e);
		}
	}

	@Override
	public PageResult<Region> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return regionDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询归属地分页信息失败", e);
			throw new ServiceException("查询归属地分页信息失败", e);
		}
	}

	@Override
	public List<Region> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return regionDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询归属地数量失败", e);
			throw new ServiceException("查询归属地数量失败", e);
		}
	}
}
