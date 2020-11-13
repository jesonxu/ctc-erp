package com.dahantc.erp.vo.fsExpenseIncome.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;

public interface IFsExpenseIncomeDao {

    FsExpenseIncome read(Serializable id) throws DaoException;

    boolean save(FsExpenseIncome entity) throws DaoException;

    boolean delete(Serializable id) throws DaoException;

    boolean update(FsExpenseIncome enterprise) throws DaoException;

    int getCountByCriteria(SearchFilter filter) throws DaoException;

    PageResult<FsExpenseIncome> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

    List<FsExpenseIncome> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

    List<FsExpenseIncome> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

    boolean updateByBatch(List<FsExpenseIncome> objs) throws DaoException;

    boolean saveByBatch(List<FsExpenseIncome> objs) throws DaoException;

    boolean deleteByBatch(List<FsExpenseIncome> objs) throws DaoException;

    List<FsExpenseIncome> findByhql(final String hql, final Map<String, Object> params, int maxCount)
        throws DaoException;
}
