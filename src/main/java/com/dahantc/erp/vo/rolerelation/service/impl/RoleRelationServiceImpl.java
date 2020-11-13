package com.dahantc.erp.vo.rolerelation.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.rolerelation.dao.IRoleRelationDao;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
@Service("roleRelationService")
public class RoleRelationServiceImpl implements IRoleRelationService {
	private static Logger logger = LogManager.getLogger(RoleRelationServiceImpl.class);

	@Autowired
	private IRoleRelationDao roleRelationDao;

	@Override
	public RoleRelation read(Serializable id) throws ServiceException {
		try {
			return roleRelationDao.read(id);
		} catch (Exception e) {
			logger.error("读取用户角色关系角色关系信息失败", e);
			throw new ServiceException("读取用户角色关系角色关系信息失败", e);
		}
	}

	@Override
	public boolean save(RoleRelation entity) throws ServiceException {
		try {
			return roleRelationDao.save(entity);
		} catch (Exception e) {
			logger.error("保存用户角色关系角色关系信息失败", e);
			throw new ServiceException("保存用户角色关系角色关系信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return roleRelationDao.delete(id);
		} catch (Exception e) {
			logger.error("删除用户角色关系角色关系信息失败", e);
			throw new ServiceException("删除用户角色关系角色关系信息失败", e);
		}
	}

	@Override
	public boolean update(RoleRelation enterprise) throws ServiceException {
		try {
			return roleRelationDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新用户角色关系角色关系信息失败", e);
			throw new ServiceException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return roleRelationDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询用户角色关系角色关系信息数量失败", e);
			throw new ServiceException("查询用户角色关系角色关系信息数量失败", e);
		}
	}

	@Override
	public PageResult<RoleRelation> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return roleRelationDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询用户角色关系角色关系信息分页信息失败", e);
			throw new ServiceException("查询用户角色关系角色关系信息分页信息失败", e);
		}
	}

	@Override
	public List<RoleRelation> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return roleRelationDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("条件查询用户角色关系角色关系信息失败", e);
			throw new ServiceException("条件查询用户角色关系角色关系信息失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<RoleRelation> objs) throws ServiceException {
		try {
			return roleRelationDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<RoleRelation> objs) throws ServiceException {
		try {
			return roleRelationDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<RoleRelation> objs) throws ServiceException {
		try {
			return roleRelationDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
