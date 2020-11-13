package com.dahantc.erp.vo.chargeRecord.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;

public interface IChargeRecordService {
	ChargeRecord read(Serializable id) throws ServiceException;

	boolean save(ChargeRecord entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(ChargeRecord enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<ChargeRecord> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<ChargeRecord> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<ChargeRecord> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<ChargeRecord> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

}
