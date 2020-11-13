package com.dahantc.erp.vo.royalty.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.royalty.dao.IRealRoyaltyDao;
import com.dahantc.erp.vo.royalty.entity.RealRoyalty;
import com.dahantc.erp.vo.royalty.service.IRealRoyaltyService;

@Service("realRoyaltyService")
public class RealRoyaltyServiceImpl implements IRealRoyaltyService {
	private static Logger logger = LogManager.getLogger(RealRoyaltyServiceImpl.class);

	@Autowired
	private IRealRoyaltyDao realRoyaltyDao;

	@Override
	public RealRoyalty read(Serializable id) throws ServiceException {
		try {
			return realRoyaltyDao.read(id);
		} catch (Exception e) {
			logger.error("读取提成表失败", e);
			throw new ServiceException("读取提成表失败", e);
		}
	}

	@Override
	public boolean save(RealRoyalty entity) throws ServiceException {
		try {
			return realRoyaltyDao.save(entity);
		} catch (Exception e) {
			logger.error("保存提成表失败", e);
			throw new ServiceException("保存提成表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<RealRoyalty> objs) throws ServiceException {
		try {
			return realRoyaltyDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return realRoyaltyDao.delete(id);
		} catch (Exception e) {
			logger.error("删除提成表失败", e);
			throw new ServiceException("删除提成表失败", e);
		}
	}

	@Override
	public boolean update(RealRoyalty enterprise) throws ServiceException {
		try {
			return realRoyaltyDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新提成表失败", e);
			throw new ServiceException("更新提成表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return realRoyaltyDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询提成表数量失败", e);
			throw new ServiceException("查询提成表数量失败", e);
		}
	}

	@Override
	public PageResult<RealRoyalty> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return realRoyaltyDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询提成表分页信息失败", e);
			throw new ServiceException("查询提成表分页信息失败", e);
		}
	}

	@Override
	public List<RealRoyalty> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return realRoyaltyDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询提成表失败", e);
			throw new ServiceException("查询提成表失败", e);
		}
	}

	@Override
	public List<RealRoyalty> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return realRoyaltyDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询提成表失败", e);
			throw new ServiceException("查询提成表失败", e);
		}
	}

	@Override
	public List<RealRoyalty> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return realRoyaltyDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询提成表失败", e);
			throw new ServiceException("查询提成表失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<RealRoyalty> objs) throws ServiceException {
		try {
			return realRoyaltyDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 查账单毛利润
	 *
	 * @param startDate
	 *            开始时间 <=
	 * @param endDate
	 *            结束时间 <
	 * @param onlineUser
	 *            当前用户
	 * @return
	 */
	@Override
	public JSONObject queryBillGrossProfit(Date startDate, Date endDate, OnlineUser onlineUser) {
		JSONObject grossProfitInfo = new JSONObject();
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		try {
			BigDecimal totalGrossProfit = new BigDecimal(0);
			List<RealRoyalty> realRoyaltyList = queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(realRoyaltyList)) {
				for (RealRoyalty realRoyalty : realRoyaltyList) {
					totalGrossProfit = totalGrossProfit.add(realRoyalty.getGrossProfit());
				}
			}
			grossProfitInfo.put("totalGrossProfit", totalGrossProfit.toPlainString());
			return grossProfitInfo;
		} catch (ServiceException e) {
			logger.error("", e);
			return null;
		}
	}
}
