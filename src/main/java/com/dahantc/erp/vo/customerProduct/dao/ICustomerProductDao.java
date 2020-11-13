package com.dahantc.erp.vo.customerProduct.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;

public interface ICustomerProductDao {
    CustomerProduct read(Serializable id) throws DaoException;

    boolean save(CustomerProduct entity) throws DaoException;

    boolean delete(Serializable id) throws DaoException;

    boolean update(CustomerProduct entity) throws DaoException;

    int getCountByCriteria(SearchFilter filter) throws DaoException;

    PageResult<CustomerProduct> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

    List<CustomerProduct> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

    List<CustomerProduct> queryAllByFilter(SearchFilter filter) throws DaoException;

    List<CustomerProduct> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

    boolean saveByBatch(List<CustomerProduct> objs) throws DaoException;

    boolean updateByBatch(List<CustomerProduct> objs) throws DaoException;
}
