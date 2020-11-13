package com.dahantc.erp.vo.chargeRecord.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;

public interface IChargeRecordDao {
	ChargeRecord read(Serializable id) throws DaoException;

	boolean save(ChargeRecord entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(ChargeRecord enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<ChargeRecord> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<ChargeRecord> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<ChargeRecord> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<ChargeRecord> objs) throws DaoException;

	boolean saveByBatch(List<ChargeRecord> objs) throws DaoException;

	boolean deleteByBatch(List<ChargeRecord> objs) throws DaoException;

	List<ChargeRecord> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
