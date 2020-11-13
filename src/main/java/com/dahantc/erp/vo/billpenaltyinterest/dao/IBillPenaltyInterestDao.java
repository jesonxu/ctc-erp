package com.dahantc.erp.vo.billpenaltyinterest.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.billpenaltyinterest.entity.BillPenaltyInterest;

public interface IBillPenaltyInterestDao {

	BillPenaltyInterest read(Serializable id) throws DaoException;

	boolean save(BillPenaltyInterest entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(BillPenaltyInterest enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<BillPenaltyInterest> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<BillPenaltyInterest> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<BillPenaltyInterest> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<BillPenaltyInterest> objs) throws DaoException;

	boolean saveByBatch(List<BillPenaltyInterest> objs) throws DaoException;

	boolean deleteByBatch(List<BillPenaltyInterest> objs) throws DaoException;

}
