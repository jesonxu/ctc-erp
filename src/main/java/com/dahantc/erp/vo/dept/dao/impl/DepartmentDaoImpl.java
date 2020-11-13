package com.dahantc.erp.vo.dept.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.dao.IDepartmentDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.role.dao.impl.RoleDaoImpl;

@Repository("departmentDao")
public class DepartmentDaoImpl implements IDepartmentDao {
	private static final Logger logger = LogManager.getLogger(RoleDaoImpl.class);

	@Resource
	private IBaseDao baseDao;

	@Override
	public List<Department> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Department.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public Department read(Serializable id) throws DaoException {
		try {
			Department dept = (Department) baseDao.get(Department.class, id);
			return dept;
		} catch (Exception e) {
			logger.error("读取角色信息失败", e);
		}
		return null;
	}
}
