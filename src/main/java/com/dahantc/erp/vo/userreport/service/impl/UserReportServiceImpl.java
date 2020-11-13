package com.dahantc.erp.vo.userreport.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.userreport.dao.IUserReportDao;
import com.dahantc.erp.vo.userreport.entity.UserReport;
import com.dahantc.erp.vo.userreport.service.IUserReportService;

@Service("userReportService")
public class UserReportServiceImpl implements IUserReportService {
	private static Logger logger = LogManager.getLogger(UserReportServiceImpl.class);

	@Autowired
	private IUserReportDao userReportDao;
	
	@Autowired
	private IBaseDao baseDao;
	
	@Override
	public UserReport read(Serializable id) throws ServiceException {
		try {
			return userReportDao.read(id);
		} catch (Exception e) {
			logger.error("读取用户报告信息失败", e);
			throw new ServiceException("读取用户报告信息失败", e);
		}
	}

	@Override
	public boolean save(UserReport entity) throws ServiceException {
		try {
			return userReportDao.save(entity);
		} catch (Exception e) {
			logger.error("保存用户报告信息失败", e);
			throw new ServiceException("保存用户报告信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return userReportDao.delete(id);
		} catch (Exception e) {
			logger.error("删除用户报告信息失败", e);
			throw new ServiceException("删除用户报告信息失败", e);
		}
	}

	@Override
	public boolean update(UserReport enterprise) throws ServiceException {
		try {
			return userReportDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新用户报告信息失败", e);
			throw new ServiceException("更新用户报告信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return userReportDao.getCount(filter);
		} catch (Exception e) {
			logger.error("查询用户报告信息数量失败", e);
			throw new ServiceException("查询用户报告信息数量失败", e);
		}
	}

	@Override
	public PageResult<UserReport> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return userReportDao.queryByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询用户报告分页信息失败", e);
			throw new ServiceException("查询用户报告分页信息失败", e);
		}
	}

	@Override
	public List<UserReport> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return userReportDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询用户报告信息数量失败", e);
			throw new ServiceException("查询用户报告信息数量失败", e);
		}
	}

	/** 根据角色id和菜单id查询菜单 */
	@Override
	public List<UserReport> readUserReportByRoleAndMenuId(String roleId, String menuId) throws ServiceException {
		try {
			return userReportDao.readUserReportByRoleAndMenuId(roleId, menuId);
		} catch (Exception e) {
			throw new ServiceException("用户报告失败", e);
		}
	}

	@Override
	public List<UserReport> readUserReportByRole(String roleId) throws ServiceException {
		try {
			return userReportDao.readUserReportByRole(roleId);
		} catch (Exception e) {
			throw new ServiceException("用户报告失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<UserReport> objs) throws ServiceException {
		try {
			return userReportDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<UserReport> objs) throws ServiceException {
		try {
			return userReportDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<UserReport> objs) throws ServiceException {
		try {
			return userReportDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public List<String> queryHasReport(Integer type, String userId, List<String> deptIdList, Date startDate, Date endDate) {
		List<String> result = new ArrayList<>();
		try {
			Map<String, Object> params = new HashMap<>();
			String hql = "SELECT wtime FROM UserReport WHERE reportType = :reportType";
			params.put("reportType", type);
			if (StringUtils.isNotBlank(userId)) {
				hql += " AND ossUserId = :userId";
				params.put("userId", userId);
			} else {
				hql += " AND deptId IN :deptIds";
				params.put("deptIds", deptIdList);
			}
			if (startDate != null) {
				hql += " AND wtime >= :startDate AND wtime < :endDate";
				params.put("startDate", startDate);
				params.put("endDate", endDate);
				hql += " GROUP BY YEAR(wtime), MONTH(wtime), DAY(wtime) ORDER BY wtime ASC";
			} else {
				hql += " GROUP BY YEAR(wtime) ORDER BY wtime ASC";
			}
			List<Object> list = baseDao.findByhql(hql, params, 0);
			if (!CollectionUtils.isEmpty(list)) {
				list.forEach(obj -> {
					result.add(DateUtil.convert((Date) obj, DateUtil.format2));
				});
			}
		} catch (BaseException e) {
			logger.error("", e);
		}
		return result;
	}
	
}
