package com.dahantc.erp.vo.dsSaleData.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsSaleData.dao.IDsCustomerReceiveDataDao;
import com.dahantc.erp.vo.dsSaleData.entity.DsCustomerReceiveData;
import com.dahantc.erp.vo.dsSaleData.service.IDsCustomerReceiveDataService;

@Service("dsCustomerReceiveDataService")
public class DsCustomerReceiveDataServiceImpl implements IDsCustomerReceiveDataService {
	private static Logger logger = LogManager.getLogger(DsCustomerReceiveDataServiceImpl.class);

	@Autowired
	private IDsCustomerReceiveDataDao dsCustomerReceiveDataDao;

	@Override
	public DsCustomerReceiveData read(Serializable id) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.read(id);
		} catch (Exception e) {
			logger.error("读取客户应收账款统计表失败", e);
			throw new ServiceException("读取客户应收账款统计表失败", e);
		}
	}

	@Override
	public boolean save(DsCustomerReceiveData entity) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.save(entity);
		} catch (Exception e) {
			logger.error("保存客户应收账款统计表失败", e);
			throw new ServiceException("保存客户应收账款统计表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsCustomerReceiveData> objs) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.delete(id);
		} catch (Exception e) {
			logger.error("删除客户应收账款统计表失败", e);
			throw new ServiceException("删除客户应收账款统计表失败", e);
		}
	}

	@Override
	public boolean update(DsCustomerReceiveData enterprise) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新客户应收账款统计表失败", e);
			throw new ServiceException("更新客户应收账款统计表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询客户应收账款统计表数量失败", e);
			throw new ServiceException("查询客户应收账款统计表数量失败", e);
		}
	}

	@Override
	public PageResult<DsCustomerReceiveData> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询客户应收账款统计表分页信息失败", e);
			throw new ServiceException("查询客户应收账款统计表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsCustomerReceiveData> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询客户应收账款统计表失败", e);
			throw new ServiceException("查询客户应收账款统计表失败", e);
		}
	}

	@Override
	public List<DsCustomerReceiveData> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户应收账款统计表失败", e);
			throw new ServiceException("查询客户应收账款统计表失败", e);
		}
	}
	
	@Override
	public List<DsCustomerReceiveData> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsCustomerReceiveDataDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询客户应收账款统计表失败", e);
			throw new ServiceException("查询客户应收账款统计表失败", e);
		}
	}
}
