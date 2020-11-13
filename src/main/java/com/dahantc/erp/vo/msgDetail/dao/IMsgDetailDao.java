package com.dahantc.erp.vo.msgDetail.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;

public interface IMsgDetailDao {
	MsgDetail read(Serializable id) throws DaoException;

	boolean save(MsgDetail entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(MsgDetail enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<MsgDetail> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<MsgDetail> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<MsgDetail> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<MsgDetail> objs) throws DaoException;

	boolean saveByBatch(List<MsgDetail> objs) throws DaoException;

	boolean deleteByBatch(List<MsgDetail> objs) throws DaoException;
	
	List<MsgDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
