package com.dahantc.erp.vo.dsOrderDetail.service.impl;

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
import com.dahantc.erp.vo.dsOrderDetail.dao.IDsOrderDetailDao;
import com.dahantc.erp.vo.dsOrderDetail.entity.DsOrderDetail;
import com.dahantc.erp.vo.dsOrderDetail.service.IDsOrderDetailService;

@Service("dsOrderDetailService")
public class DsOrderDetailServiceImpl implements IDsOrderDetailService {
	private static Logger logger = LogManager.getLogger(DsOrderDetailServiceImpl.class);

	@Autowired
	private IDsOrderDetailDao dsOrderDetailDao;

	@Override
	public DsOrderDetail read(Serializable id) throws ServiceException {
		try {
			return dsOrderDetailDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商订单表失败", e);
			throw new ServiceException("读取电商订单表失败", e);
		}
	}

	@Override
	public boolean save(DsOrderDetail entity) throws ServiceException {
		try {
			return dsOrderDetailDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商订单表失败", e);
			throw new ServiceException("保存电商订单表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsOrderDetail> objs) throws ServiceException {
		try {
			return dsOrderDetailDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsOrderDetailDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商订单表失败", e);
			throw new ServiceException("删除电商订单表失败", e);
		}
	}

	@Override
	public boolean update(DsOrderDetail enterprise) throws ServiceException {
		try {
			return dsOrderDetailDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商订单表失败", e);
			throw new ServiceException("更新电商订单表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDetailDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商订单表数量失败", e);
			throw new ServiceException("查询电商订单表数量失败", e);
		}
	}

	@Override
	public PageResult<DsOrderDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDetailDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商订单表分页信息失败", e);
			throw new ServiceException("查询电商订单表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsOrderDetail> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDetailDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商订单表失败", e);
			throw new ServiceException("查询电商订单表失败", e);
		}
	}

	@Override
	public List<DsOrderDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDetailDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商订单表失败", e);
			throw new ServiceException("查询电商订单表失败", e);
		}
	}
	
	@Override
	public List<DsOrderDetail> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsOrderDetailDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商订单表失败", e);
			throw new ServiceException("查询电商订单表失败", e);
		}
	}
}
