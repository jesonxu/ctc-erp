package com.dahantc.erp.vo.billpenaltyinterest.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.billpenaltyinterest.entity.BillPenaltyInterest;
import com.dahantc.erp.vo.billpenaltyinterest.service.impl.BillPenaltyInterestServiceImpl.PenaltyInterest;

public interface IBillPenaltyInterestService {

	BillPenaltyInterest read(Serializable id) throws ServiceException;

	boolean save(BillPenaltyInterest entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(BillPenaltyInterest enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<BillPenaltyInterest> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<BillPenaltyInterest> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<BillPenaltyInterest> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	boolean saveByBatch(List<BillPenaltyInterest> objs) throws ServiceException;

	PenaltyInterest queryPenaltyInterestAmountByBillId(String billId) throws ServiceException;

	PenaltyInterest queryPenaltyInterestByBillId(String billId) throws ServiceException;

	List<BillPenaltyInterest> queryPenaltyInterestListByBillId(String billId) throws ServiceException;

}
