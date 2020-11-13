package com.dahantc.erp.vo.dsOutDepotDetail.service.impl;

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
import com.dahantc.erp.vo.dsOutDepotDetail.dao.IDsOutDepotDetailDao;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;
import com.dahantc.erp.vo.dsOutDepotDetail.service.IDsOutDepotDetailService;

@Service("dsOutDepotDetailService")
public class DsOutDepotDetailServiceImpl implements IDsOutDepotDetailService {
	private static Logger logger = LogManager.getLogger(DsOutDepotDetailServiceImpl.class);

	@Autowired
	private IDsOutDepotDetailDao dsOutDepotDetailDao;

	@Override
	public DsOutDepotDetail read(Serializable id) throws ServiceException {
		try {
			return dsOutDepotDetailDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商出库详情表失败", e);
			throw new ServiceException("读取电商出库详情表失败", e);
		}
	}

	@Override
	public boolean save(DsOutDepotDetail entity) throws ServiceException {
		try {
			return dsOutDepotDetailDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商出库详情表失败", e);
			throw new ServiceException("保存电商出库详情表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsOutDepotDetail> objs) throws ServiceException {
		try {
			return dsOutDepotDetailDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsOutDepotDetailDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商出库详情表失败", e);
			throw new ServiceException("删除电商出库详情表失败", e);
		}
	}

	@Override
	public boolean update(DsOutDepotDetail enterprise) throws ServiceException {
		try {
			return dsOutDepotDetailDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商出库详情表失败", e);
			throw new ServiceException("更新电商出库详情表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDetailDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商出库详情表数量失败", e);
			throw new ServiceException("查询电商出库详情表数量失败", e);
		}
	}

	@Override
	public PageResult<DsOutDepotDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDetailDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商出库详情表分页信息失败", e);
			throw new ServiceException("查询电商出库详情表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsOutDepotDetail> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDetailDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商出库详情表失败", e);
			throw new ServiceException("查询电商出库详情表失败", e);
		}
	}

	@Override
	public List<DsOutDepotDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDetailDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商出库详情表失败", e);
			throw new ServiceException("查询电商出库详情表失败", e);
		}
	}
	
	@Override
	public List<DsOutDepotDetail> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsOutDepotDetailDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商出库详情表失败", e);
			throw new ServiceException("查询电商出库详情表失败", e);
		}
	}
}
