package com.dahantc.erp.vo.operateCost.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
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
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.operateCost.dao.IOperateCostDao;
import com.dahantc.erp.vo.operateCost.entity.OperateCost;
import com.dahantc.erp.vo.operateCost.service.IOperateCostService;
import com.dahantc.erp.vo.productBills.entity.OperateCostDetail;
import com.dahantc.erp.vo.productBills.entity.ProductBills;

@Service("operateCostService")
public class OperateCostServiceImpl implements IOperateCostService {
	private static Logger logger = LogManager.getLogger(OperateCostServiceImpl.class);

	@Autowired
	private IOperateCostDao operateCostDao;

	@Override
	public OperateCost read(Serializable id) throws ServiceException {
		try {
			return operateCostDao.read(id);
		} catch (Exception e) {
			logger.error("读取运营成本表失败", e);
			throw new ServiceException("读取运营成本表失败", e);
		}
	}

	@Override
	public boolean save(OperateCost entity) throws ServiceException {
		try {
			return operateCostDao.save(entity);
		} catch (Exception e) {
			logger.error("保存运营成本表失败", e);
			throw new ServiceException("保存运营成本表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<OperateCost> objs) throws ServiceException {
		try {
			return operateCostDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return operateCostDao.delete(id);
		} catch (Exception e) {
			logger.error("删除运营成本表失败", e);
			throw new ServiceException("删除运营成本表失败", e);
		}
	}

	@Override
	public boolean update(OperateCost enterprise) throws ServiceException {
		try {
			return operateCostDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新运营成本表失败", e);
			throw new ServiceException("更新运营成本表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return operateCostDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询运营成本表数量失败", e);
			throw new ServiceException("查询运营成本表数量失败", e);
		}
	}

	@Override
	public PageResult<OperateCost> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return operateCostDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询运营成本表分页信息失败", e);
			throw new ServiceException("查询运营成本表分页信息失败", e);
		}
	}
	
	@Override
	public List<OperateCost> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return operateCostDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询运营成本表失败", e);
			throw new ServiceException("查询运营成本表失败", e);
		}
	}

	@Override
	public List<OperateCost> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return operateCostDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询运营成本表失败", e);
			throw new ServiceException("查询运营成本表失败", e);
		}
	}
	
	@Override
	public List<OperateCost> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return operateCostDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询运营成本表失败", e);
			throw new ServiceException("查询运营成本表失败", e);
		}
	}

	@Override
	public OperateCost saveOperateCostByBill(OperateCostDetail detail, ProductBills bill) {
		if (null == bill) {
			logger.info("账单不能为null");
			return null;
		}
		OperateCost operateCost = null;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, bill.getProductId()));
			filter.getRules().add(new SearchRule("billMonth", Constants.ROP_EQ, bill.getWtime()));
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<OperateCost> operateCostList = queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(operateCostList)) {
				operateCost = operateCostList.get(0);
			}
		} catch (ServiceException e) {
			logger.error("查询运营成本异常，账单id：" + bill.getId(), e);
		}
		operateCost = null == operateCost ? new OperateCost() : operateCost;
		operateCost.setEntityId(bill.getEntityId());
		operateCost.setProductId(bill.getProductId());
		operateCost.setBillId(bill.getId());
		operateCost.setBillMonth(bill.getWtime());
		if (null != detail) {
			if (detail.getUnifiedOperateSingleCost() != null) {
				operateCost.setUnifiedSingleCostTotal(new BigDecimal(bill.getPlatformCount()).multiply(detail.getUnifiedOperateSingleCost()));
			}
			if (detail.getProductOperateSingleCost() != null) {
				operateCost.setProductSingleCostTotal(new BigDecimal(bill.getPlatformCount()).multiply(detail.getProductOperateSingleCost()));
			}
			if (detail.getProductOperateFixedCost() != null) {
				operateCost.setCustomerFixedCost(detail.getProductOperateFixedCost());
			}
			if (detail.getBillMoneyRatio() != null) {
				operateCost.setBillMoneyCost(bill.getReceivables().multiply(detail.getBillMoneyRatio()));
			}
			if (detail.getBillGrossProfitRatio() != null) {
				operateCost.setBillGrossProfitCost(bill.getGrossProfit().multiply(detail.getBillGrossProfitRatio()));
			}
		}
		boolean result = false;
		try {
			result = this.save(operateCost);
			logger.info("保存或更新运营成本" + (result ? "成功" : "失败") + "，账单id：" + bill.getId());
		} catch (ServiceException e) {
			logger.error("保存运营成本异常，账单id：" + bill.getId(), e);
		}
		return result ? operateCost : null;
	}
}
