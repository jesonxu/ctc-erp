package com.dahantc.erp.vo.specialAttendance.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;

public interface ISpecialAttendanceRecordService {
	SpecialAttendanceRecord read(Serializable id) throws ServiceException;

	boolean save(SpecialAttendanceRecord entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SpecialAttendanceRecord enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SpecialAttendanceRecord> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<SpecialAttendanceRecord> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SpecialAttendanceRecord> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<SpecialAttendanceRecord> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean updateByBatch(List<SpecialAttendanceRecord> objs) throws ServiceException;

	boolean saveByBatch(List<SpecialAttendanceRecord> objs) throws ServiceException;

	boolean deleteByBatch(List<SpecialAttendanceRecord> objs) throws ServiceException;

	SpecialAttendanceRecord readOneByProperty(String property, Object value) throws ServiceException;

}
