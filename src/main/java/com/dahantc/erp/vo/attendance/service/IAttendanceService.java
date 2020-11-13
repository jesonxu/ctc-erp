package com.dahantc.erp.vo.attendance.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.attendance.entity.Attendance;
import com.dahantc.erp.vo.user.entity.User;

public interface IAttendanceService {
	Attendance read(Serializable id) throws ServiceException;

	boolean save(Attendance entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Attendance enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Attendance> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<Attendance> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Attendance> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<Attendance> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean updateByBatch(List<Attendance> objs) throws ServiceException;

	boolean saveByBatch(List<Attendance> objs) throws ServiceException;

	boolean deleteByBatch(List<Attendance> objs) throws ServiceException;

	Attendance readOneByProperty(String property, Object value) throws ServiceException;

	void buildUserAttendance(List<String> userIdList, Date date);
}
