package com.dahantc.erp.vo.operationlog.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.operationlog.dao.IOperationLogDao;
import com.dahantc.erp.vo.operationlog.entity.OperationLog;
import com.dahantc.erp.vo.operationlog.service.IOperationLogService;

@Service("operationLogService")
public class OperationLogServiceImpl implements IOperationLogService {
	private static Logger logger = LogManager.getLogger(OperationLogServiceImpl.class);

	@Autowired
	private IOperationLogDao operationLogDao;

	@Override
	public OperationLog read(Serializable id) throws ServiceException {
		try {
			return operationLogDao.read(id);
		} catch (Exception e) {
			logger.error("读取系统日志信息失败", e);
			throw new ServiceException("读取系统日志信息失败", e);
		}
	}

	@Override
	public boolean save(OperationLog entity) throws ServiceException {
		try {
			return operationLogDao.save(entity);
		} catch (Exception e) {
			logger.error("保存系统日志信息失败", e);
			throw new ServiceException("保存系统日志信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return operationLogDao.delete(id);
		} catch (Exception e) {
			logger.error("删除系统日志信息失败", e);
			throw new ServiceException("删除系统日志信息失败", e);
		}
	}

	@Override
	public boolean update(OperationLog enterprise) throws ServiceException {
		try {
			return operationLogDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新系统日志信息失败", e);
			throw new ServiceException("更新系统日志信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return operationLogDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询系统日志信息数量失败", e);
			throw new ServiceException("查询系统日志信息数量失败", e);
		}
	}

	@Override
	public PageResult<OperationLog> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return operationLogDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询系统日志信息分页信息失败", e);
			throw new ServiceException("查询系统日志信息分页信息失败", e);
		}
	}

	@Override
	public List<OperationLog> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return operationLogDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询系统日志信息数量失败", e);
			throw new ServiceException("查询系统日志信息数量失败", e);
		}
	}
}
