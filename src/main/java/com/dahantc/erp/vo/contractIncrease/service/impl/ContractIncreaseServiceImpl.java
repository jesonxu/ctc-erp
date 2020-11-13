package com.dahantc.erp.vo.contractIncrease.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.contractIncrease.dao.IContractIncreaseDao;
import com.dahantc.erp.vo.contractIncrease.entity.ContractIncrease;
import com.dahantc.erp.vo.contractIncrease.service.IContractIncreaseService;

@Service("contractIncreaseService")
public class ContractIncreaseServiceImpl implements IContractIncreaseService {
	private static Logger logger = LogManager.getLogger(ContractIncreaseServiceImpl.class);

	private final static Object obj = new Object();
	@Autowired
	private IContractIncreaseDao contractIncreaseDao;

	@Override
	public ContractIncrease read(Serializable id) throws ServiceException {
		try {
			return contractIncreaseDao.read(id);
		} catch (Exception e) {
			logger.error("读取合同编号自增表失败", e);
			throw new ServiceException("读取合同编号自增表失败", e);
		}
	}

	@Override
	public boolean save(ContractIncrease entity) throws ServiceException {
		try {
			return contractIncreaseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存合同编号自增表失败", e);
			throw new ServiceException("保存合同编号自增表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<ContractIncrease> objs) throws ServiceException {
		try {
			return contractIncreaseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return contractIncreaseDao.delete(id);
		} catch (Exception e) {
			logger.error("删除合同编号自增表失败", e);
			throw new ServiceException("删除合同编号自增表失败", e);
		}
	}

	@Override
	public boolean update(ContractIncrease enterprise) throws ServiceException {
		try {
			return contractIncreaseDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新合同编号自增表失败", e);
			throw new ServiceException("更新合同编号自增表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return contractIncreaseDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询合同编号自增表数量失败", e);
			throw new ServiceException("查询合同编号自增表数量失败", e);
		}
	}

	@Override
	public PageResult<ContractIncrease> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return contractIncreaseDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询合同编号自增表分页信息失败", e);
			throw new ServiceException("查询合同编号自增表分页信息失败", e);
		}
	}

	@Override
	public List<ContractIncrease> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return contractIncreaseDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询合同编号自增表失败", e);
			throw new ServiceException("查询合同编号自增表失败", e);
		}
	}

	@Override
	public List<ContractIncrease> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return contractIncreaseDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询合同编号自增表失败", e);
			throw new ServiceException("查询合同编号自增表失败", e);
		}
	}

	@Override
	public List<ContractIncrease> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return contractIncreaseDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询合同编号自增表失败", e);
			throw new ServiceException("查询合同编号自增表失败", e);
		}
	}

	@Override
	public String getNextContractNo(String productType) throws ServiceException {
		synchronized (obj) {
			try {
				SearchFilter filter = new SearchFilter();
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
				List<ContractIncrease> list = contractIncreaseDao.findByFilter(1, 0, filter);
				if (list != null && !list.isEmpty()) {
					if (!DateUtil.getCurrentDate().equals(DateUtil.convert(list.get(0).getWtime(), DateUtil.format1))) {
						String sql = "truncate table erp_contract_increase;";
						contractIncreaseDao.executeUpdateSQL(sql);
					}
				}
				ContractIncrease cc = new ContractIncrease();
				contractIncreaseDao.save(cc);
				int id = (cc.getId() - 1) % 10000;
				return "DH-" + productType + "-" + DateUtil.convert(System.currentTimeMillis(), DateUtil.format8).substring(0, 8) + "-"
						+ String.format("%04d", id);
			} catch (Exception e) {
				logger.error("获取合同编号失败", e);
				throw new ServiceException("查询合同编号自增表失败", e);
			}
		}
	}
}
