package com.dahantc.erp.vo.customerProduct.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customerProduct.dao.ICustomerProductDao;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;

@Repository("customerProductDao")
public class CustomerProductDaoImpl implements ICustomerProductDao {
    private static final Logger logger = LogManager.getLogger(CustomerProductDaoImpl.class);
    @Resource
    private IBaseDao baseDao;

    @Override
    public CustomerProduct read(Serializable id) throws DaoException {
        try {
            CustomerProduct customerProduct = (CustomerProduct) baseDao.get(CustomerProduct.class, id);
            return customerProduct;
        } catch (Exception e) {
            logger.error("读取产品失败", e);
        }
        return null;
    }

    @Override
    public boolean save(CustomerProduct entity) throws DaoException {
        try {
            return baseDao.save(entity);
        } catch (Exception e) {
            logger.error("保存产品失败", e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Serializable id) throws DaoException {
        CustomerProduct customerProduct = read(id);
        try {
            return baseDao.delete(customerProduct);
        } catch (Exception e) {
            logger.error("删除产品失败", e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(CustomerProduct entity) throws DaoException {
        try {
            return baseDao.update(entity);
        } catch (Exception e) {
            logger.error("更新产品失败", e);
            throw new DaoException(e);
        }
    }

    @Override
    public int getCountByCriteria(SearchFilter filter) throws DaoException {
        try {
            DetachedCriteria dc = DetachedCriteria.forClass(CustomerProduct.class);
            List<Order> list = new ArrayList<>();
            DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
            return baseDao.getCountByCriteria(dc);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    public PageResult<CustomerProduct> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
        try {
            DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CustomerProduct.class);
            List<Order> list = new ArrayList<>();
            DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
            return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<CustomerProduct> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
        try {
            DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CustomerProduct.class);
            List<Order> list = new ArrayList<>();
            DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
            return (List<CustomerProduct>) baseDao.findByFilter(detachedCriteria, size, start, list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    public List<CustomerProduct> queryAllByFilter(SearchFilter filter) throws DaoException {
        try {
            DetachedCriteria dc = DetachedCriteria.forClass(CustomerProduct.class);
            DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
            return baseDao.findAllByCriteria(detachedCriteria);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<CustomerProduct> findByhql(String hql, Map<String, Object> params, int maxCount) throws DaoException {
        try {
            return baseDao.findByhql(hql, params, maxCount);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean saveByBatch(List<CustomerProduct> objs) throws DaoException {
        try {
            return baseDao.saveByBatch(objs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean updateByBatch(List<CustomerProduct> objs) throws DaoException {
        try {
            return baseDao.updateByBatch(objs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }
}
