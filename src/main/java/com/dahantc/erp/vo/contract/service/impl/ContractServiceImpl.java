package com.dahantc.erp.vo.contract.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.contract.dao.IContractDao;
import com.dahantc.erp.vo.contract.entity.Contract;
import com.dahantc.erp.vo.contract.service.IContractService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

@Service("contractService")
public class ContractServiceImpl implements IContractService {
	private static Logger logger = LogManager.getLogger(ContractServiceImpl.class);

	@Autowired
	private IContractDao contractDao;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public Contract read(Serializable id) throws ServiceException {
		try {
			return contractDao.read(id);
		} catch (Exception e) {
			logger.error("读取合同表失败", e);
			throw new ServiceException("读取合同表失败", e);
		}
	}

	@Override
	public boolean save(Contract entity) throws ServiceException {
		try {
			return contractDao.save(entity);
		} catch (Exception e) {
			logger.error("保存合同表失败", e);
			throw new ServiceException("保存合同表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Contract> objs) throws ServiceException {
		try {
			return contractDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return contractDao.delete(id);
		} catch (Exception e) {
			logger.error("删除合同表失败", e);
			throw new ServiceException("删除合同表失败", e);
		}
	}

	@Override
	public boolean update(Contract enterprise) throws ServiceException {
		try {
			return contractDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新合同表失败", e);
			throw new ServiceException("更新合同表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return contractDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询合同表数量失败", e);
			throw new ServiceException("查询合同表数量失败", e);
		}
	}

	@Override
	public PageResult<Contract> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return contractDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询合同表分页信息失败", e);
			throw new ServiceException("查询合同表分页信息失败", e);
		}
	}

	@Override
	public List<Contract> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return contractDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询合同表失败", e);
			throw new ServiceException("查询合同表失败", e);
		}
	}

	@Override
	public List<Contract> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return contractDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询合同表失败", e);
			throw new ServiceException("查询合同表失败", e);
		}
	}

	@Override
	public List<Contract> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return contractDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询合同表失败", e);
			throw new ServiceException("查询合同表失败", e);
		}
	}

	/**
	 * 生成合同编号
	 *
	 * @param entityType
	 *            实体类型，0供应商，1客户
	 * @param productType
	 *            产品类型
	 * @return
	 */
	@Override
	public String buildContractNumber(int entityType, int productType) {
		String contractNumber = "";
		logger.info("生成合同编号开始");
		try {
			// 获取今天的流水号，查今天有几个合同，得到新的流水号
			SearchFilter filter = new SearchFilter();
			Date startDate = DateUtil.getCurrentStartDateTime();
			Date endDate = DateUtil.getCurrentEndDateTime();
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endDate));
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, entityType));
			int serialNo = (getCount(filter)) % 10000;

			// 产品类型的名称
			String productTypeKey = productTypeService.getProductTypeKeyByValue(productType);
			if (StringUtil.isBlank(productTypeKey)) {
				logger.info("错误的产品类型：" + productType);
				return null;
			}

			// 合同编号规则：DH-SMS-yyyymmdd-4位流水号
			contractNumber = "DH-" + productTypeKey + "-" + DateUtil.convert(startDate, DateUtil.format13) + "-" + String.format("%04d", serialNo);
		} catch (Exception e) {
			logger.error("生成合同编号异常，entityType：" + entityType + "，productType：" + productType, e);
			return null;
		}
		logger.info("生成合同编号成功，合同编号：" + contractNumber);
		return contractNumber;
	}

	/**
	 * 查询主体对应的合同信息
	 *
	 * @param entityIds
	 *            主体ID
	 * @param entityType
	 *            主体类型
	 * @return 合同信息
	 */
	@Override
	public Map<String, List<Contract>> findEntityContract(List<String> entityIds, Integer entityType) {
		Map<String, List<Contract>> contractMap = new HashMap<>();
		if (entityIds == null || entityIds.isEmpty()) {
			return contractMap;
		}
		// 查询所有的客户合同
		SearchFilter contractFilter = new SearchFilter();
		contractFilter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, new ArrayList<>(new HashSet<>(entityIds))));
		contractFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, entityType));
		List<Contract> customerContractList = null;
		try {
			customerContractList = queryAllBySearchFilter(contractFilter);
		} catch (ServiceException e) {
			logger.error("查询合同信息异常", e);
		}
		if (customerContractList != null && !customerContractList.isEmpty()) {
			contractMap = customerContractList.stream().collect(Collectors.groupingBy(Contract::getEntityId));
		}
		return contractMap;
	}
}
