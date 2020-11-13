package com.dahantc.erp.vo.customerChangeRecord.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.customerChangeRecord.entity.CustomerChangeRecord;

/**
 * 客户变更记录Dao
 * @author 8520
 */
public interface ICustomerChangeRecordDao {

    CustomerChangeRecord read(Serializable id) throws DaoException;

    boolean save(CustomerChangeRecord entity) throws DaoException;

    boolean delete(Serializable id) throws DaoException;

    boolean update(CustomerChangeRecord enterprise) throws DaoException;

    int getCountByCriteria(SearchFilter filter) throws DaoException;

    PageResult<CustomerChangeRecord> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

    List<CustomerChangeRecord> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

    List<CustomerChangeRecord> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

    boolean updateByBatch(List<CustomerChangeRecord> objs) throws DaoException;

    boolean saveByBatch(List<CustomerChangeRecord> objs) throws DaoException;

    boolean deleteByBatch(List<CustomerChangeRecord> objs) throws DaoException;

    List<CustomerChangeRecord> findByHql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
