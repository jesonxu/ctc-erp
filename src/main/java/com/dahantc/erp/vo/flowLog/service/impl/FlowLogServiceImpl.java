package com.dahantc.erp.vo.flowLog.service.impl;

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
import com.dahantc.erp.vo.flowLog.dao.IFlowLogDao;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;

@Service("flowLogService")
public class FlowLogServiceImpl implements IFlowLogService {
	private static Logger logger = LogManager.getLogger(FlowLogServiceImpl.class);

	@Autowired
	private IFlowLogDao flowLogDao;

	@Override
	public FlowLog read(Serializable id) throws ServiceException {
		try {
			return flowLogDao.read(id);
		} catch (Exception e) {
			logger.error("读取流程节点处理结果表失败", e);
			throw new ServiceException("读取流程节点处理结果表失败", e);
		}
	}

	@Override
	public boolean save(FlowLog entity) throws ServiceException {
		try {
			return flowLogDao.save(entity);
		} catch (Exception e) {
			logger.error("保存流程节点处理结果表失败", e);
			throw new ServiceException("保存流程节点处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<FlowLog> objs) throws ServiceException {
		try {
			return flowLogDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return flowLogDao.delete(id);
		} catch (Exception e) {
			logger.error("删除流程节点处理结果表失败", e);
			throw new ServiceException("删除流程节点处理结果表失败", e);
		}
	}

	@Override
	public boolean update(FlowLog enterprise) throws ServiceException {
		try {
			return flowLogDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新流程节点处理结果表失败", e);
			throw new ServiceException("更新流程节点处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return flowLogDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询流程节点处理结果表数量失败", e);
			throw new ServiceException("查询流程节点处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<FlowLog> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return flowLogDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询流程节点处理结果表分页信息失败", e);
			throw new ServiceException("查询流程节点处理结果表分页信息失败", e);
		}
	}
	
	@Override
	public List<FlowLog> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return flowLogDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询流程节点处理结果表失败", e);
			throw new ServiceException("查询流程节点处理结果表失败", e);
		}
	}

	@Override
	public List<FlowLog> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return flowLogDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询流程节点处理结果表失败", e);
			throw new ServiceException("查询流程节点处理结果表失败", e);
		}
	}
	
	@Override
	public List<FlowLog> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return flowLogDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询流程节点处理结果表失败", e);
			throw new ServiceException("查询流程节点处理结果表失败", e);
		}
	}
}
