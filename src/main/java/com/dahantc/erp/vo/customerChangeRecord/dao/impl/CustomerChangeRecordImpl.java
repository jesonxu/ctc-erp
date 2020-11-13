package com.dahantc.erp.vo.customerChangeRecord.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customerChangeRecord.dao.ICustomerChangeRecordDao;
import com.dahantc.erp.vo.customerChangeRecord.entity.CustomerChangeRecord;

/**
 * @author 8520
 */
@Repository(value = "customerChangeRecordDao")
public class CustomerChangeRecordImpl implements ICustomerChangeRecordDao {

    private static final Logger logger = LoggerFactory.getLogger(CustomerChangeRecordImpl.class);

    @Resource
    private IBaseDao baseDao;

    @Override
	public CustomerChangeRecord read(Serializable id) throws DaoException {
		try {
			return (CustomerChangeRecord) baseDao.get(CustomerChangeRecord.class, id);
		} catch (Exception e) {
			logger.error("读取客户变更记录失败", e);
		}
		return null;
	}

    @Override
    public boolean save(CustomerChangeRecord entity) throws DaoException {
        try {
            return baseDao.save(entity);
        } catch (Exception e) {
            logger.error("保存客户变更记录信息失败", e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Serializable id) throws DaoException {
        CustomerChangeRecord customer = read(id);
        try {
            return baseDao.delete(customer);
        } catch (Exception e) {
            logger.error("删除客户变更记录失败", e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(CustomerChangeRecord enterprise) throws DaoException {
        try {
            return baseDao.update(enterprise);
        } catch (Exception e) {
            logger.error("更新客户变更记录失败", e);
            throw new DaoException(e);
        }
    }

    @Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(CustomerChangeRecord.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

    @Override
    public PageResult<CustomerChangeRecord> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
        try {
            DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CustomerChangeRecord.class);
            List<Order> list = new ArrayList<>();
            DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
            return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
	public List<CustomerChangeRecord> findByFilter(int size, int start, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CustomerChangeRecord.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return (List<CustomerChangeRecord>) baseDao.findByFilter(detachedCriteria, size, start, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

    @Override
	public List<CustomerChangeRecord> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(CustomerChangeRecord.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

    @Override
	public boolean updateByBatch(List<CustomerChangeRecord> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

    @Override
    public boolean saveByBatch(List<CustomerChangeRecord> objs) throws DaoException {
        try {
            return baseDao.saveByBatch(objs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean deleteByBatch(List<CustomerChangeRecord> objs) throws DaoException {
        try {
            return baseDao.deleteByBatch(objs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    @Override
    public List<CustomerChangeRecord> findByHql(String hql, Map<String, Object> params, int maxCount) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
    }
}
