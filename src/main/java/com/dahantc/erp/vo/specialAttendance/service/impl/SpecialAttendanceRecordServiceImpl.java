package com.dahantc.erp.vo.specialAttendance.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.specialAttendance.dao.ISpecialAttendanceRecordDao;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;

@Service("specialAttendanceRecordService")
public class SpecialAttendanceRecordServiceImpl implements ISpecialAttendanceRecordService {
	private static Logger logger = LogManager.getLogger(SpecialAttendanceRecordServiceImpl.class);

	@Autowired
	private ISpecialAttendanceRecordDao specialAttendanceRecordDao;

	@Override
	public SpecialAttendanceRecord read(Serializable id) throws ServiceException {
		try {
			return specialAttendanceRecordDao.read(id);
		} catch (Exception e) {
			logger.error("读取特殊出勤报备记录失败", e);
			throw new ServiceException("读取特殊出勤报备记录失败", e);
		}
	}

	@Override
	public boolean save(SpecialAttendanceRecord entity) throws ServiceException {
		try {
			return specialAttendanceRecordDao.save(entity);
		} catch (Exception e) {
			logger.error("保存特殊出勤报备记录失败", e);
			throw new ServiceException("保存特殊出勤报备记录失败", e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return specialAttendanceRecordDao.delete(id);
		} catch (Exception e) {
			logger.error("删除特殊出勤报备记录失败", e);
			throw new ServiceException("删除特殊出勤报备记录失败", e);
		}
	}

	@Override
	public boolean update(SpecialAttendanceRecord enterprise) throws ServiceException {
		try {
			return specialAttendanceRecordDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新特殊出勤报备记录失败", e);
			throw new ServiceException("更新特殊出勤报备记录失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<SpecialAttendanceRecord> objs) throws ServiceException {
		try {
			return specialAttendanceRecordDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<SpecialAttendanceRecord> objs) throws ServiceException {
		try {
			return specialAttendanceRecordDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<SpecialAttendanceRecord> objs) throws ServiceException {
		try {
			return specialAttendanceRecordDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return specialAttendanceRecordDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询特殊出勤报备记录数量失败", e);
			throw new ServiceException("查询特殊出勤报备记录数量失败", e);
		}
	}

	@Override
	public PageResult<SpecialAttendanceRecord> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return specialAttendanceRecordDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询特殊出勤报备记录分页信息失败", e);
			throw new ServiceException("查询特殊出勤报备记录分页信息失败", e);
		}
	}
	
	@Override
	public List<SpecialAttendanceRecord> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return specialAttendanceRecordDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询特殊出勤报备记录失败", e);
			throw new ServiceException("查询特殊出勤报备记录失败", e);
		}
	}

	@Override
	public List<SpecialAttendanceRecord> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return specialAttendanceRecordDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询特殊出勤报备记录失败", e);
			throw new ServiceException("查询特殊出勤报备记录失败", e);
		}
	}
	
	@Override
	public List<SpecialAttendanceRecord> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return specialAttendanceRecordDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询特殊出勤报备记录失败", e);
			throw new ServiceException("查询特殊出勤报备记录失败", e);
		}
	}

    @Override
	public SpecialAttendanceRecord readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return specialAttendanceRecordDao.readOneByProperty(property, value);
		} catch (Exception e) {
			logger.error("查询特殊出勤报备记录失败", e);
			throw new ServiceException("查询特殊出勤报备记录失败", e);
		}
	}
}
