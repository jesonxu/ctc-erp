package com.dahantc.erp.vo.specialAttendance.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.specialAttendance.dao.ISpecialAttendanceRecordDao;
import com.dahantc.erp.util.DetachedCriteriaUtil;

@Repository("specialAttendanceRecordDao")
public class SpecialAttendanceRecordDaoImpl implements ISpecialAttendanceRecordDao {
	private static final Logger logger = LogManager.getLogger(SpecialAttendanceRecordDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public SpecialAttendanceRecord read(Serializable id) throws DaoException {
		try {
			SpecialAttendanceRecord specialAttendanceRecord = (SpecialAttendanceRecord) baseDao.get(SpecialAttendanceRecord.class, id);
			return specialAttendanceRecord;
		} catch (Exception e) {
			logger.error("读取特殊出勤报备记录失败", e);
		}
		return null;
	}

	@Override
	public boolean save(SpecialAttendanceRecord entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存特殊出勤报备记录失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		SpecialAttendanceRecord specialAttendanceRecord = read(id);
		try {
			return baseDao.delete(specialAttendanceRecord);
		} catch (Exception e) {
			logger.error("删除特殊出勤报备记录失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(SpecialAttendanceRecord enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新特殊出勤报备记录失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(SpecialAttendanceRecord.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	
	@Override
	public PageResult<SpecialAttendanceRecord> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SpecialAttendanceRecord.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SpecialAttendanceRecord> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SpecialAttendanceRecord.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<SpecialAttendanceRecord>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<SpecialAttendanceRecord> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(SpecialAttendanceRecord.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateByBatch(List<SpecialAttendanceRecord> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<SpecialAttendanceRecord> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<SpecialAttendanceRecord> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
	
	@Override
	public List<SpecialAttendanceRecord> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
    public SpecialAttendanceRecord readOneByProperty(String property, Object value) throws DaoException {
        try {
            return (SpecialAttendanceRecord) baseDao.getEntityByProperty(property, value, SpecialAttendanceRecord.class);
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }
}
