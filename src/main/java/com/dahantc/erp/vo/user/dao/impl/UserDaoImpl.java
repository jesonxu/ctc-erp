package com.dahantc.erp.vo.user.dao.impl;

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
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.user.dao.IUserDao;
import com.dahantc.erp.vo.user.entity.User;


/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
@Repository("userDao")
public class UserDaoImpl implements IUserDao {
	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public User read(Serializable id) throws DaoException {
		try {
			User user = (User) baseDao.get(User.class, id);
			return user;
		} catch (Exception e) {
			logger.error("读取用户信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean save(User entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存用户信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		User user = read(id);
		try {
			return baseDao.delete(user);
		} catch (Exception e) {
			logger.error("删除用户信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(User enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新用户信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(User.class);
			List<Order> orderList = new ArrayList<Order>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, orderList);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<User> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(User.class);
			List<Order> orderList = new ArrayList<Order>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, orderList);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, orderList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<User> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(User.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	/** 根据属性和值查询数据 */
	@Override
	public User readOneByProperty(String property, Object value) throws BaseException {
		try {
			Object object = baseDao.getEntityByProperty(property, value, User.class);
			return object != null ? (User) object : null;
		} catch (Exception e) {
			logger.error("读取用户异常", e);
			throw new BaseException("读取用户异常", e);
		}
	}

	@Override
	public List<User> queryByHql(String hql, Map<String, Object> params, Integer max) throws DaoException {
		try {
			return baseDao.findByhql(hql, params, max);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean updateByBatch(List<User> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<User> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<User> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
