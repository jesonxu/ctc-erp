package com.dahantc.erp.vo.checkin.service.impl;

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
import com.dahantc.erp.vo.checkin.dao.ICheckinDao;
import com.dahantc.erp.vo.checkin.entity.Checkin;
import com.dahantc.erp.vo.checkin.service.ICheckinService;

@Service("checkinService")
public class CheckinServiceImpl implements ICheckinService {
	private static Logger logger = LogManager.getLogger(CheckinServiceImpl.class);

	@Autowired
	private ICheckinDao checkinDao;

	@Override
	public Checkin read(Serializable id) throws ServiceException {
		try {
			return checkinDao.read(id);
		} catch (Exception e) {
			logger.error("读取打卡记录失败", e);
			throw new ServiceException("读取打卡记录失败", e);
		}
	}

	@Override
	public boolean save(Checkin entity) throws ServiceException {
		try {
			return checkinDao.save(entity);
		} catch (Exception e) {
			logger.error("保存打卡记录失败", e);
			throw new ServiceException("保存打卡记录失败", e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return checkinDao.delete(id);
		} catch (Exception e) {
			logger.error("删除打卡记录失败", e);
			throw new ServiceException("删除打卡记录失败", e);
		}
	}

	@Override
	public boolean update(Checkin enterprise) throws ServiceException {
		try {
			return checkinDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新打卡记录失败", e);
			throw new ServiceException("更新打卡记录失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<Checkin> objs) throws ServiceException {
		try {
			return checkinDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<Checkin> objs) throws ServiceException {
		try {
			return checkinDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<Checkin> objs) throws ServiceException {
		try {
			return checkinDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return checkinDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询打卡记录数量失败", e);
			throw new ServiceException("查询打卡记录数量失败", e);
		}
	}

	@Override
	public PageResult<Checkin> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return checkinDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询打卡记录分页信息失败", e);
			throw new ServiceException("查询打卡记录分页信息失败", e);
		}
	}
	
	@Override
	public List<Checkin> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return checkinDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询打卡记录失败", e);
			throw new ServiceException("查询打卡记录失败", e);
		}
	}

	@Override
	public List<Checkin> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return checkinDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询打卡记录失败", e);
			throw new ServiceException("查询打卡记录失败", e);
		}
	}
	
	@Override
	public List<Checkin> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return checkinDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询打卡记录失败", e);
			throw new ServiceException("查询打卡记录失败", e);
		}
	}

    @Override
	public Checkin readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return checkinDao.readOneByProperty(property, value);
		} catch (Exception e) {
			logger.error("查询打卡记录失败", e);
			throw new ServiceException("查询打卡记录失败", e);
		}
	}
}
