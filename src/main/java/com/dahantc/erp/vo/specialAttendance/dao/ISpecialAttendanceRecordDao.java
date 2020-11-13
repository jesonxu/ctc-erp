package com.dahantc.erp.vo.specialAttendance.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;

public interface ISpecialAttendanceRecordDao {
	SpecialAttendanceRecord read(Serializable id) throws DaoException;

	boolean save(SpecialAttendanceRecord entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SpecialAttendanceRecord enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SpecialAttendanceRecord> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<SpecialAttendanceRecord> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SpecialAttendanceRecord> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<SpecialAttendanceRecord> objs) throws DaoException;

	boolean saveByBatch(List<SpecialAttendanceRecord> objs) throws DaoException;

	boolean deleteByBatch(List<SpecialAttendanceRecord> objs) throws DaoException;
	
	List<SpecialAttendanceRecord> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	SpecialAttendanceRecord readOneByProperty(String property, Object value) throws DaoException;
}
