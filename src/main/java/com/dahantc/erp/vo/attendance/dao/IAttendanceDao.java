package com.dahantc.erp.vo.attendance.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.attendance.entity.Attendance;

public interface IAttendanceDao {
	Attendance read(Serializable id) throws DaoException;

	boolean save(Attendance entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Attendance enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Attendance> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<Attendance> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Attendance> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<Attendance> objs) throws DaoException;

	boolean saveByBatch(List<Attendance> objs) throws DaoException;

	boolean deleteByBatch(List<Attendance> objs) throws DaoException;
	
	List<Attendance> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	Attendance readOneByProperty(String property, Object value) throws DaoException;
}
