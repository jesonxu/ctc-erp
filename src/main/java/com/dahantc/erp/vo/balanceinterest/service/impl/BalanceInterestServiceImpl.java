package com.dahantc.erp.vo.balanceinterest.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.dao.SearchOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.balanceaccount.BalanceInterestDto;
import com.dahantc.erp.dto.balanceaccount.BalanceInterestParam;
import com.dahantc.erp.dto.balanceaccount.InterestDetailDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.balanceinterest.dao.IBalanceInterestDao;
import com.dahantc.erp.vo.balanceinterest.entity.BalanceInterest;
import com.dahantc.erp.vo.balanceinterest.service.IBalanceInterestService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("balanceInterestService")
public class BalanceInterestServiceImpl implements IBalanceInterestService {
	private static Logger logger = LogManager.getLogger(BalanceInterestServiceImpl.class);

	@Autowired
	private IBalanceInterestDao balanceInterestDao;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public BalanceInterest read(Serializable id) throws ServiceException {
		try {
			return balanceInterestDao.read(id);
		} catch (Exception e) {
			logger.error("读取余额利息处理结果表失败", e);
			throw new ServiceException("读取余额利息处理结果表失败", e);
		}
	}

	@Override
	public boolean save(BalanceInterest entity) throws ServiceException {
		try {
			return balanceInterestDao.save(entity);
		} catch (Exception e) {
			logger.error("保存余额利息处理结果表失败", e);
			throw new ServiceException("保存余额利息处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<BalanceInterest> objs) throws ServiceException {
		try {
			return balanceInterestDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return balanceInterestDao.delete(id);
		} catch (Exception e) {
			logger.error("删除余额利息处理结果表失败", e);
			throw new ServiceException("删除余额利息处理结果表失败", e);
		}
	}

	@Override
	public boolean update(BalanceInterest enterprise) throws ServiceException {
		try {
			return balanceInterestDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新余额利息处理结果表失败", e);
			throw new ServiceException("更新余额利息处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return balanceInterestDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表数量失败", e);
			throw new ServiceException("查询余额利息处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<BalanceInterest> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return balanceInterestDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表分页信息失败", e);
			throw new ServiceException("查询余额利息处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<BalanceInterest> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return balanceInterestDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表失败", e);
			throw new ServiceException("查询余额利息处理结果表失败", e);
		}
	}

	@Override
	public List<BalanceInterest> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return balanceInterestDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表失败", e);
			throw new ServiceException("查询余额利息处理结果表失败", e);
		}
	}

	@Override
	public List<BalanceInterest> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return balanceInterestDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表失败", e);
			throw new ServiceException("查询余额利息处理结果表失败", e);
		}
	}

	/**
	 * 根据条件分页查询统计数据
	 *
	 * @param user
	 *            登录用户
	 * @param param
	 *            参数
	 * @return 分页 计息数据
	 */
	@Override
	public PageResult<BalanceInterestDto> queryBalanceInterestByPage(OnlineUser user, BalanceInterestParam param) {
		long startTime = System.currentTimeMillis();
		try {
			// 统计数据
			StringBuilder dataSql = new StringBuilder(
					" select b.deptid,b.ossuserid,b.customerid,sum(b.accountbalance)," + "sum(b.interest) from erp_balance_interest b ");
			Map<String, Object> sqlParam = new HashMap<>();
			// 按人过滤
			if (StringUtils.isNotBlank(param.getUserId())) {
				dataSql.append(" where b.ossuserid in (:userIds)");
				sqlParam.put("userIds", param.getUserIds());
			} else {
				// 数据权限下的部门
				List<String> deptIdList = departmentService.getDeptIdsByPermission(user);
				// 按部门过滤
				if (StringUtils.isNotBlank(param.getDeptId()) && !CollectionUtils.isEmpty(deptIdList)) {
					deptIdList.retainAll(param.getDeptIds());
				}
				if (!CollectionUtils.isEmpty(deptIdList)) {
					dataSql.append(" where  b.deptid in (:deptIds) ");
					sqlParam.put("deptIds", deptIdList);
				} else {
					dataSql.append(" where b.ossuserid=:userId  ");
					sqlParam.put("userId", user.getUser().getOssUserId());
				}
			}
			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(param.getQueryDate())) {
				monthDate = DateUtil.convert4(param.getQueryDate());
			}
			dataSql.append(" and b.wtime >= :startTime and b.wtime < :endTime group by b.customerId order by b.wtime desc  ");
			sqlParam.put("startTime", monthDate);
			sqlParam.put("endTime", DateUtil.getNextMonthFirst(monthDate));
			// 统计数量
			String countSql = " select count(1) from ( " + dataSql + " ) n";
			// 分页查询 SQL
			logger.info("分页查询 计息信息数量SQL：" + countSql);
			List<Object> countList = baseDao.selectSQL(countSql.toString(), sqlParam);
			if (countList == null || countList.isEmpty()) {
				return PageResult.empty("暂无数据");
			}
			Object countInfo = countList.get(0);
			int total = 0;
			if (countInfo instanceof Number) {
				total = ((Number) countInfo).intValue();
			}
			if (total == 0) {
				return PageResult.empty("暂无数据");
			}
			int totalPage = total / param.getPageSize();
			if (total % param.getPageSize() != 0) {
				totalPage++;
			}
			if (totalPage < param.getCurrentPage()) {
				return PageResult.empty("暂无数据");
			}
			// 分页查询数据
			dataSql.append(" limit ").append(param.getPageStart()).append(",").append(param.getPageSize());
			logger.info("分页查询 计息信息SQL：" + dataSql);
			List<Object> dataList = baseDao.selectSQL(dataSql.toString(), sqlParam);
			// 结果
			List<BalanceInterestDto> resultList = new ArrayList<>();
			for (Object row : dataList) {
				if (row != null && row.getClass().isArray()) {
					BalanceInterestDto balanceInterest = new BalanceInterestDto();
					if (balanceInterest.setObjectData((Object[]) row)) {
						resultList.add(balanceInterest);
					}
				}
			}
			// 分页查询到的数据
			if (resultList.isEmpty()) {
				return PageResult.empty("暂无数据");
			}
			// 部门名称
			List<String> deptIds = resultList.stream().map(BalanceInterestDto::getDeptId).collect(Collectors.toList());
			Map<String, String> deptNames = departmentService.queryDeptName(deptIds);
			// 销售名称
			List<String> saleUserIds = resultList.stream().map(BalanceInterestDto::getSaleId).collect(Collectors.toList());
			Map<String, String> saleNames = userService.findUserNameByIds(saleUserIds);
			// 客户信息
			List<String> customerIds = resultList.stream().map(BalanceInterestDto::getCustomerId).collect(Collectors.toList());
			Map<String, String> customerNames = customerService.queryCustomerName(customerIds);

			for (BalanceInterestDto balanceInterest : resultList) {
				balanceInterest.setSaleName(saleNames.get(balanceInterest.getSaleId()));
				balanceInterest.setCustomerName(customerNames.get(balanceInterest.getCustomerId()));
				balanceInterest.setDeptName(deptNames.get(balanceInterest.getDeptId()));
				balanceInterest.setInterestRatio(balanceInterest.getInterest().divide(balanceInterest.getAccountBalance(), 10, BigDecimal.ROUND_HALF_UP));
			}
			logger.info("查询利润提成耗时:[" + (System.currentTimeMillis() - startTime) + "]毫秒");
			return new PageResult<>(resultList, param.getCurrentPage(), totalPage, total);
		} catch (Exception e) {
			logger.error("利润提成查询异常", e);
		}
		return PageResult.empty("数据查询异常");
	}

	/**
	 * 查询 计息详情
	 *
	 * @param companyId
	 *            公司ID
	 * @param month
	 *            月份
	 * @param user
	 *            登录用户
	 * @return 计息详情
	 */
	@Override
	public List<InterestDetailDto> queryInterestDetail(String companyId, Date month, OnlineUser user) {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, companyId));
		Date timeEnd = DateUtil.getNextMonthFirst(month);
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, month));
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, timeEnd));
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
		List<BalanceInterest> balanceInterests = null;
		try {
			balanceInterests = this.queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询计息详情异常");
		}
		if (balanceInterests == null || balanceInterests.isEmpty()) {
			return null;
		}
		return balanceInterests.stream().map(balanceInterest -> {
			InterestDetailDto interestDetail = new InterestDetailDto();
			interestDetail.setTime(balanceInterest.getWtime());
			interestDetail.setInterest(balanceInterest.getInterest());
			interestDetail.setLeftMoney(balanceInterest.getAccountBalance());
			interestDetail.setRate(balanceInterest.getInterestRatio());
			return interestDetail;
		}).collect(Collectors.toList());
	}
}
