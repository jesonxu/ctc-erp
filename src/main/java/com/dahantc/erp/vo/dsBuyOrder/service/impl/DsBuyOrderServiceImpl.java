package com.dahantc.erp.vo.dsBuyOrder.service.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.dsBuyOrder.dao.IDsBuyOrderDao;
import com.dahantc.erp.vo.dsBuyOrder.entity.DsBuyOrder;
import com.dahantc.erp.vo.dsBuyOrder.service.IDsBuyOrderService;
import com.dahantc.erp.vo.user.entity.User;

@Service("dsBuyOrderService")
public class DsBuyOrderServiceImpl implements IDsBuyOrderService {
	private static Logger logger = LogManager.getLogger(DsBuyOrderServiceImpl.class);

	@Autowired
	private IDsBuyOrderDao dsBuyOrderDao;

	@Override
	public DsBuyOrder read(Serializable id) throws ServiceException {
		try {
			return dsBuyOrderDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商采购单表失败", e);
			throw new ServiceException("读取电商采购单表失败", e);
		}
	}

	@Override
	public boolean save(DsBuyOrder entity) throws ServiceException {
		try {
			return dsBuyOrderDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商采购单表失败", e);
			throw new ServiceException("保存电商采购单表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsBuyOrder> objs) throws ServiceException {
		try {
			return dsBuyOrderDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsBuyOrderDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商采购单表失败", e);
			throw new ServiceException("删除电商采购单表失败", e);
		}
	}

	@Override
	public boolean update(DsBuyOrder enterprise) throws ServiceException {
		try {
			return dsBuyOrderDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商采购单表失败", e);
			throw new ServiceException("更新电商采购单表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsBuyOrderDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商采购单表数量失败", e);
			throw new ServiceException("查询电商采购单表数量失败", e);
		}
	}

	@Override
	public PageResult<DsBuyOrder> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsBuyOrderDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商采购单表分页信息失败", e);
			throw new ServiceException("查询电商采购单表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsBuyOrder> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsBuyOrderDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商采购单表失败", e);
			throw new ServiceException("查询电商采购单表失败", e);
		}
	}

	@Override
	public List<DsBuyOrder> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsBuyOrderDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商采购单表失败", e);
			throw new ServiceException("查询电商采购单表失败", e);
		}
	}
	
	@Override
	public List<DsBuyOrder> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsBuyOrderDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商采购单表失败", e);
			throw new ServiceException("查询电商采购单表失败", e);
		}
	}
	
	
    @Override
	public String buildBuyOrderNo(User user, Date date) throws ServiceException {
		String buyOrderNo = null;
		//获取6位时间字符串
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String dateString = simpleDateFormat.format(date);
        dateString = dateString.substring( 0, 2);
      //获取下一个流水号
        SearchFilter filter = new SearchFilter();
        Date startDate = DateUtil.getThisYearFirst();
        Date endDate = DateUtil.getCurrYearLast();
        filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startDate));
        filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
        String serialNo = String.valueOf(getCount(filter) + 1); 
        if (serialNo.length()==1) {
        	serialNo = "000"+serialNo;
		}else if (serialNo.length()==2) {
        	serialNo = "00"+serialNo;
		}else if (serialNo.length()==3) {
        	serialNo = "0"+serialNo;
		}
		//获取订单号
        buyOrderNo = "DG-DS-" + dateString + "-c" + serialNo + "(D)";
		return buyOrderNo;
	}
    
}
