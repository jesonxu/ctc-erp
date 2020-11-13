package com.dahantc.erp.vo.rolerelation.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import com.dahantc.erp.vo.rolerelation.dao.IRoleRelationDao;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
@Repository("roleRelationDao")
public class RoleRelationDaoImpl implements IRoleRelationDao {
	private static final Logger logger = LogManager.getLogger(RoleRelationDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public RoleRelation read(Serializable id) throws DaoException {
		try {
			RoleRelation RoleRelation = (RoleRelation) baseDao.get(RoleRelation.class, id);
			return RoleRelation;
		} catch (Exception e) {
			logger.error("读取用户角色关系信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean save(RoleRelation entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存用户角色关系信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		RoleRelation RoleRelation = read(id);
		try {
			return baseDao.delete(RoleRelation);
		} catch (Exception e) {
			logger.error("删除用户角色关系信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(RoleRelation enterprise) throws DaoException {
		try {
			return baseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新用户角色关系信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCountByCriteria(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(RoleRelation.class);
			List<Order> orderList = new ArrayList<Order>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, orderList);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<RoleRelation> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(RoleRelation.class);
			List<Order> orderList = new ArrayList<Order>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, orderList);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, orderList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<RoleRelation> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(RoleRelation.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	/** 根据属性和值查询数据 */
	@Override
	public RoleRelation readOneByProperty(String property, Object value) throws BaseException {
		try {
			Object object = baseDao.getEntityByProperty(property, value, RoleRelation.class);
			return object != null ? (RoleRelation) object : null;
		} catch (Exception e) {
			logger.error("读取用户角色关系异常", e);
			throw new BaseException("读取用户角色关系异常", e);
		}
	}

	@Override
	public boolean updateByBatch(List<RoleRelation> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<RoleRelation> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<RoleRelation> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
