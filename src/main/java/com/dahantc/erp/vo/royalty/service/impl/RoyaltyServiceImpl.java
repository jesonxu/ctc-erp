package com.dahantc.erp.vo.royalty.service.impl;

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
import com.dahantc.erp.vo.royalty.dao.IRoyaltyDao;
import com.dahantc.erp.vo.royalty.entity.Royalty;
import com.dahantc.erp.vo.royalty.service.IRoyaltyService;

@Service("royaltyService")
public class RoyaltyServiceImpl implements IRoyaltyService {
	private static Logger logger = LogManager.getLogger(RoyaltyServiceImpl.class);

	@Autowired
	private IRoyaltyDao royaltyDao;

	@Override
	public Royalty read(Serializable id) throws ServiceException {
		try {
			return royaltyDao.read(id);
		} catch (Exception e) {
			logger.error("读取提成表失败", e);
			throw new ServiceException("读取提成表失败", e);
		}
	}

	@Override
	public boolean save(Royalty entity) throws ServiceException {
		try {
			return royaltyDao.save(entity);
		} catch (Exception e) {
			logger.error("保存提成表失败", e);
			throw new ServiceException("保存提成表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Royalty> objs) throws ServiceException {
		try {
			return royaltyDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return royaltyDao.delete(id);
		} catch (Exception e) {
			logger.error("删除提成表失败", e);
			throw new ServiceException("删除提成表失败", e);
		}
	}

	@Override
	public boolean update(Royalty enterprise) throws ServiceException {
		try {
			return royaltyDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新提成表失败", e);
			throw new ServiceException("更新提成表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return royaltyDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询提成表数量失败", e);
			throw new ServiceException("查询提成表数量失败", e);
		}
	}

	@Override
	public PageResult<Royalty> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return royaltyDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询提成表分页信息失败", e);
			throw new ServiceException("查询提成表分页信息失败", e);
		}
	}

	@Override
	public List<Royalty> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return royaltyDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询提成表失败", e);
			throw new ServiceException("查询提成表失败", e);
		}
	}

	@Override
	public List<Royalty> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return royaltyDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询提成表失败", e);
			throw new ServiceException("查询提成表失败", e);
		}
	}

	@Override
	public List<Royalty> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return royaltyDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询提成表失败", e);
			throw new ServiceException("查询提成表失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<Royalty> objs) throws ServiceException {
		try {
			return royaltyDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
