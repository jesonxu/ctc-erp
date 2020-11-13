package com.dahantc.erp.vo.roledetail.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.roledetail.dao.IRoleDetailDao;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;
import com.dahantc.erp.vo.roledetail.service.IRoleDetailService;

@Service("roleDetailService")
public class RoleDetailServiceImpl implements IRoleDetailService {
	private static Logger logger = LogManager.getLogger(RoleDetailServiceImpl.class);

	@Autowired
	private IRoleDetailDao roleDetailDao;

	@Override
	public RoleDetail read(Serializable id) throws ServiceException {
		try {
			return roleDetailDao.read(id);
		} catch (Exception e) {
			logger.error("读取角色详情信息失败", e);
			throw new ServiceException("读取角色详情信息失败", e);
		}
	}

	@Override
	public boolean save(RoleDetail entity) throws ServiceException {
		try {
			return roleDetailDao.save(entity);
		} catch (Exception e) {
			logger.error("保存角色详情信息失败", e);
			throw new ServiceException("保存角色详情信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return roleDetailDao.delete(id);
		} catch (Exception e) {
			logger.error("删除角色详情信息失败", e);
			throw new ServiceException("删除角色详情信息失败", e);
		}
	}

	@Override
	public boolean update(RoleDetail enterprise) throws ServiceException {
		try {
			return roleDetailDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新角色详情信息失败", e);
			throw new ServiceException("更新角色详情信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return roleDetailDao.getCount(filter);
		} catch (Exception e) {
			logger.error("查询角色详情信息数量失败", e);
			throw new ServiceException("查询角色详情信息数量失败", e);
		}
	}

	@Override
	public PageResult<RoleDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return roleDetailDao.queryByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询角色详情分页信息失败", e);
			throw new ServiceException("查询角色详情分页信息失败", e);
		}
	}

	@Override
	public List<RoleDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return roleDetailDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询角色详情信息数量失败", e);
			throw new ServiceException("查询角色详情信息数量失败", e);
		}
	}

	/** 根据角色id和菜单id查询菜单 */
	@Override
	public List<RoleDetail> readRoleDetailByRoleAndMenuId(String roleId, String menuId) throws ServiceException {
		try {
			return roleDetailDao.readRoleDetailByRoleAndMenuId(roleId, menuId);
		} catch (Exception e) {
			throw new ServiceException("菜单查询失败", e);
		}
	}

	@Override
	public List<RoleDetail> readRoleDetailByRole(String roleId) throws ServiceException {
		try {
			return roleDetailDao.readRoleDetailByRole(roleId);
		} catch (Exception e) {
			throw new ServiceException("菜单查询失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<RoleDetail> objs) throws ServiceException {
		try {
			return roleDetailDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<RoleDetail> objs) throws ServiceException {
		try {
			return roleDetailDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<RoleDetail> objs) throws ServiceException {
		try {
			return roleDetailDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
