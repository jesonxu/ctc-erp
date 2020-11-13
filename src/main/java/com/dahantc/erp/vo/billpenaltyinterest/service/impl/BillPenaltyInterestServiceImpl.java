package com.dahantc.erp.vo.billpenaltyinterest.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.billpenaltyinterest.dao.IBillPenaltyInterestDao;
import com.dahantc.erp.vo.billpenaltyinterest.entity.BillPenaltyInterest;
import com.dahantc.erp.vo.billpenaltyinterest.service.IBillPenaltyInterestService;

@Service("billPenaltyInterestService")
public class BillPenaltyInterestServiceImpl implements IBillPenaltyInterestService {

	private static Logger logger = LogManager.getLogger(BillPenaltyInterestServiceImpl.class);

	@Autowired
	private IBillPenaltyInterestDao billPenaltyInterestDao;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public BillPenaltyInterest read(Serializable id) throws ServiceException {
		try {
			return billPenaltyInterestDao.read(id);
		} catch (Exception e) {
			logger.error("读取余额利息处理结果表失败", e);
			throw new ServiceException("读取余额利息处理结果表失败", e);
		}
	}

	@Override
	public boolean save(BillPenaltyInterest entity) throws ServiceException {
		try {
			return billPenaltyInterestDao.save(entity);
		} catch (Exception e) {
			logger.error("保存余额利息处理结果表失败", e);
			throw new ServiceException("保存余额利息处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<BillPenaltyInterest> objs) throws ServiceException {
		try {
			return billPenaltyInterestDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return billPenaltyInterestDao.delete(id);
		} catch (Exception e) {
			logger.error("删除余额利息处理结果表失败", e);
			throw new ServiceException("删除余额利息处理结果表失败", e);
		}
	}

	@Override
	public boolean update(BillPenaltyInterest enterprise) throws ServiceException {
		try {
			return billPenaltyInterestDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新余额利息处理结果表失败", e);
			throw new ServiceException("更新余额利息处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return billPenaltyInterestDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表数量失败", e);
			throw new ServiceException("查询余额利息处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<BillPenaltyInterest> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return billPenaltyInterestDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表分页信息失败", e);
			throw new ServiceException("查询余额利息处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<BillPenaltyInterest> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return billPenaltyInterestDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表失败", e);
			throw new ServiceException("查询余额利息处理结果表失败", e);
		}
	}

	@Override
	public List<BillPenaltyInterest> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return billPenaltyInterestDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询余额利息处理结果表失败", e);
			throw new ServiceException("查询余额利息处理结果表失败", e);
		}
	}

	@Override
	public PenaltyInterest queryPenaltyInterestAmountByBillId(String billId) throws ServiceException {
		return queryPenaltyInterestByBillId(billId);
	}

	@Override
	public PenaltyInterest queryPenaltyInterestByBillId(String billId) throws ServiceException {
		try {
			String sql = "SELECT SUM(penaltyinterestdays), SUM(penaltyinterest) FROM erp_bill_penalty_interest WHERE billId = :billId";
			Map<String, Object> params = new HashMap<>();
			params.put("billId", billId);
			List<Object> result = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(result) && result.get(0) != null) {
				Object[] arr = (Object[]) result.get(0);
				if (arr[0] != null && arr[1] != null) {
					return new PenaltyInterest(((Number) arr[0]).intValue(),
							new BigDecimal(((Number) arr[1]).doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			}
			return new PenaltyInterest(0, BigDecimal.ZERO);
		} catch (Exception e) {
			logger.error("查询账单总罚息失败", e);
			throw new ServiceException("查询账单总罚息异常", e);
		}
	}

	@Override
	public List<BillPenaltyInterest> queryPenaltyInterestListByBillId(String billId) throws ServiceException {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("billId", Constants.ROP_EQ, billId));
			return queryAllBySearchFilter(searchFilter);
		} catch (Exception e) {
			logger.error("查询账单总罚息失败", e);
			throw new ServiceException("查询账单总罚息异常", e);
		}
	}

	public class PenaltyInterest {

		private int penaltyInterestDays;

		private BigDecimal penaltyInterest;

		public PenaltyInterest(int penaltyInterestDays, BigDecimal penaltyInterest) {
			this.penaltyInterestDays = penaltyInterestDays;
			this.penaltyInterest = penaltyInterest;
		}

		public int getPenaltyInterestDays() {
			return penaltyInterestDays;
		}

		public void setPenaltyInterestDays(int penaltyInterestDays) {
			this.penaltyInterestDays = penaltyInterestDays;
		}

		public BigDecimal getPenaltyInterest() {
			return penaltyInterest;
		}

		public void setPenaltyInterest(BigDecimal penaltyInterest) {
			this.penaltyInterest = penaltyInterest;
		}

	}

}