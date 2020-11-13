package com.dahantc.erp.vo.role.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.dao.DaoException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.role.AddRoleReqDto;
import com.dahantc.erp.dto.role.EditRoleReqDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.DefaultMenuType;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;
import com.dahantc.erp.vo.menuItem.service.IMenuItemService;
import com.dahantc.erp.vo.role.dao.IRoleDao;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;
import com.dahantc.erp.vo.roledetail.service.IRoleDetailService;

@Service("roleService")
public class RoleServiceImpl implements IRoleService {
	private static Logger logger = LogManager.getLogger(RoleServiceImpl.class);

	@Autowired
	private IRoleDao roleDao;

	@Autowired
	private IRoleDetailService roleDetailService;

	@Autowired
	private IMenuItemService menuItemService;

	@Override
	public Role read(Serializable id) throws ServiceException {
		try {
			return roleDao.read(id);
		} catch (Exception e) {
			logger.error("读取角色信息失败", e);
			throw new ServiceException("读取角色信息失败", e);
		}
	}

	@Override
	public boolean save(Role entity) throws ServiceException {
		try {
			return roleDao.save(entity);
		} catch (Exception e) {
			logger.error("保存角色信息失败", e);
			throw new ServiceException("保存角色信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return roleDao.delete(id);
		} catch (Exception e) {
			logger.error("删除角色信息失败", e);
			throw new ServiceException("删除角色信息失败", e);
		}
	}

	@Override
	public boolean update(Role enterprise) throws ServiceException {
		try {
			return roleDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新角色信息失败", e);
			throw new ServiceException("更新角色信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return roleDao.getCount(filter);
		} catch (Exception e) {
			logger.error("查询角色信息数量失败", e);
			throw new ServiceException("查询角色信息数量失败", e);
		}
	}

	@Override
	public PageResult<Role> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return roleDao.queryByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询角色分页信息失败", e);
			throw new ServiceException("查询角色分页信息失败", e);
		}
	}

	@Override
	public Role readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return roleDao.readOneByProperty(property, value);
		} catch (Exception e) {
			logger.error("读取角色信息异常", e);
			throw new ServiceException("读取角色信息异常", e);
		}
	}

	@Override
	public List<Role> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return roleDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询角色信息数量失败", e);
			throw new ServiceException("查询角色信息数量失败", e);
		}
	}

	/**
	 * 创建角色
	 * 
	 * @param creatorid
	 */
	@Override
	public BaseResponse<String> addRole(AddRoleReqDto roleReqDto, String creatorid) throws ServiceException {
		long start = System.currentTimeMillis();
		String defaultMenuId = roleReqDto.getDefaultMenuId();
		List<String> otherMenuIds = roleReqDto.getOtherMenuIds();
		try {
			if ((otherMenuIds != null && !otherMenuIds.isEmpty()) && StringUtils.isBlank(defaultMenuId)) {
				return BaseResponse.error("默认菜单不能为空");
			}
			// 1、生成角色记录
			Role role = new Role();
			role.setRolename(roleReqDto.getRoleName());
			role.setStatus(EntityStatus.NORMAL.ordinal());
			role.setWtime(new Timestamp(System.currentTimeMillis()));
			role.setCreatorid(creatorid);
			role.setDataPermission(roleReqDto.getDataPermission());
			role.setPagePeimission(roleReqDto.getPagePermission());
			if (DataPermission.Customize.ordinal() == roleReqDto.getDataPermission()) {
				role.setDeptIds(roleReqDto.getDeptIds());
			} else {
				role.setDeptIds("");
			}
			boolean save = this.save(role);
			if (!save) {
				return BaseResponse.error("添加角色失败");
			}
			// 2、生成roleDetail数据
			// 添加默认菜单
			String roleid = role.getRoleid();
			addRoleDetail(roleid, roleReqDto.getDefaultMenuId(), DefaultMenuType.DEFAULT.ordinal());
			if (otherMenuIds != null && !otherMenuIds.isEmpty()) {
				for (String menuId : otherMenuIds) {
					addRoleDetail(roleid, menuId, DefaultMenuType.NON_DEFAULT.ordinal());
				}
			}
		} catch (Exception e) {
			logger.error("创建角色失败", e);
			return BaseResponse.error("添加角色失败");
		}
		logger.info("添加角色成功,共耗时:" + (System.currentTimeMillis() - start));
		return BaseResponse.success("添加角色成功");
	}

	/**
	 * 角色修改
	 * 
	 * @throws ServiceException
	 */
	@Override
	public BaseResponse<String> updateRole(EditRoleReqDto roleReqDto) throws ServiceException {
		String defaultMenuId = roleReqDto.getDefaultMenuId();
		List<String> otherMenuIds = roleReqDto.getOtherMenuIds();
		long start = System.currentTimeMillis();
		try {
			if ((otherMenuIds != null && !otherMenuIds.isEmpty()) && StringUtils.isBlank(defaultMenuId)) {
				return BaseResponse.error("默认菜单不能为空");
			}
			Role role = this.read(roleReqDto.getRoleId());
			if (role == null) {
				return BaseResponse.error("要修改的角色不存在");
			}
			if (!role.getRolename().equals(roleReqDto.getRoleName())) {
				role.setRolename(roleReqDto.getRoleName());
			}
			if (roleReqDto.getDataPermission() >= 0 && roleReqDto.getDataPermission() < DataPermission.getDescs().length) {
				role.setDataPermission(roleReqDto.getDataPermission());
				if (DataPermission.Customize.ordinal() == roleReqDto.getDataPermission()) {
					role.setDeptIds(roleReqDto.getDeptIds());
				} else {
					role.setDeptIds("");
				}
			}
			role.setPagePeimission(roleReqDto.getPagePermission());
			// 先删除，后创建
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("roleid", Constants.ROP_EQ, role.getRoleid()));
			List<RoleDetail> _list = this.roleDetailService.queryAllBySearchFilter(filter);
			if (_list != null && !_list.isEmpty()) {
				roleDetailService.deleteByBatch(_list);
			}
			String roleid = role.getRoleid();
			// 添加默认菜单
			addRoleDetail(roleid, roleReqDto.getDefaultMenuId(), DefaultMenuType.DEFAULT.ordinal());
			// 2、生成roleDetail数据
			if (otherMenuIds != null && !otherMenuIds.isEmpty()) {
				for (String menuId : otherMenuIds) {
					addRoleDetail(roleid, menuId, DefaultMenuType.NON_DEFAULT.ordinal());
				}
			}
			this.update(role);
		} catch (Exception e) {
			logger.error("修改角色失败", e);
			return BaseResponse.error("修改角色失败");
		}
		logger.info("修改id为:" + roleReqDto.getRoleId() + "的角色成功,共耗时:" + (System.currentTimeMillis() - start));
		return BaseResponse.success("修改角色成功");
	}

	private boolean addRoleDetail(String roleId, String menuId, int defaultMenu) throws ServiceException {
		if (StringUtils.isNoneBlank(menuId, roleId)) {
			RoleDetail detail = new RoleDetail();
			detail.setDefalutMenuType(defaultMenu);
			detail.setMenuid(menuId);
			detail.setRoleid(roleId);
			return roleDetailService.save(detail);
		}
		return false;
	}

	/**
	 * 将页面权限字符串解析成map
	 * 
	 * @param roleId
	 *            角色id
	 * @return 页面权限map
	 */
	@Override
	public Map<String, Boolean> getPagePermission(Serializable roleId) throws ServiceException {
		Map<String, Boolean> permissions = new HashMap<>();
		String perStr = this.read(roleId).getPagePeimission();
		if (StringUtils.isNotBlank(perStr)) {
			JSONObject json = JSON.parseObject(perStr);
			for (Object key : json.keySet()) {
				Boolean value = json.getBoolean(key.toString());
				permissions.put(key.toString(), value);
			}
		}
		return permissions;
	}

	/**
	 * 校验 角色 访问菜单路径是否正确
	 *
	 * @param roleId
	 *            角色id
	 * @param url
	 *            访问路径
	 * @return 校验结果
	 */
	@Override
	public boolean checkRoleMenuUrl(String roleId, String url) {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("url", Constants.ROP_EQ, url));
		List<MenuItem> menuItems = null;
		try {
			menuItems = menuItemService.queryAllBySearchFilter(searchFilter);
			if (menuItems != null && !menuItems.isEmpty()) {
				String menuId = menuItems.get(0).getMenuid();
				// 是访问菜单
				List<RoleDetail> roleDetails = roleDetailService.readRoleDetailByRole(roleId);
				if (roleDetails == null || roleDetails.isEmpty()) {
					return false;
				}
				List<String> menuIds = roleDetails.stream().map(RoleDetail::getMenuid).collect(Collectors.toList());
				return menuIds.contains(menuId);
			}
		} catch (ServiceException e) {
			logger.error("查询数据校验异常", e);
		}
		return true;
	}

	/**
	 * 根据ID 批量查询 角色信息
	 *
	 * @param roleIds
	 *            角色Id
	 * @return 角色信息
	 */
	@Override
	public List<Role> queryRoleByIds(List<String> roleIds) {
		List<Role> result = new ArrayList<>();
		if (roleIds == null || roleIds.isEmpty()) {
			return result;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("roleid", Constants.ROP_IN, new ArrayList<>(new HashSet<>(roleIds))));
		List<Role> roleInfos = null;
		try {
			roleInfos = queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询异常", e);
		}
		if (roleInfos != null && !roleInfos.isEmpty()) {
			result.addAll(roleInfos);
		}
		return result;
	}

	@Override
	public List<Role> findByHql(String hql, Map<String, Object> params, Integer max) {
		try {
			return roleDao.queryByHql(hql, params, max);
		} catch (DaoException e) {
			logger.error("Hql查询用户角色信息错误", e);
		}
		return null;
	}

	/**
	 * 查找用户的所有角色信息
	 *
	 * @param userId
	 *            用户
	 * @return 角色信息
	 */
	@Override
	public List<Role> findUserAllRole(String userId) {
		try {
			String hql = "select r from Role r inner join RoleRelation rr on r.roleid = rr.roleId where rr.ossUserId=:userId ";
			Map<String, Object> params = new HashMap<>();
			params.put("userId", userId);
			return findByHql(hql, params, Integer.MAX_VALUE);
		}catch (Exception e){
			logger.error("查询用户的角色信息异常", e);
		}
		return null;
	}
}
