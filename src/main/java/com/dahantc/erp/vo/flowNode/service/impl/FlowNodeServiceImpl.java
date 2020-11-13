package com.dahantc.erp.vo.flowNode.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.flowNode.dao.IFlowNodeDao;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;

@Service("flowNodeService")
public class FlowNodeServiceImpl implements IFlowNodeService {
	private static Logger logger = LogManager.getLogger(FlowNodeServiceImpl.class);

	@Autowired
	private IFlowNodeDao flowNodeDao;

	@Override
	public FlowNode read(Serializable id) throws ServiceException {
		try {
			return flowNodeDao.read(id);
		} catch (Exception e) {
			logger.error("读取流程节点表失败", e);
			throw new ServiceException("读取流程节点表失败", e);
		}
	}

	@Override
	public boolean save(FlowNode entity) throws ServiceException {
		try {
			return flowNodeDao.save(entity);
		} catch (Exception e) {
			logger.error("保存流程节点表失败", e);
			throw new ServiceException("保存流程节点表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<FlowNode> objs) throws ServiceException {
		try {
			return flowNodeDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<FlowNode> objs) throws ServiceException {
		try {
			return flowNodeDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return flowNodeDao.delete(id);
		} catch (Exception e) {
			logger.error("删除流程节点表失败", e);
			throw new ServiceException("删除流程节点表失败", e);
		}
	}

	@Override
	public boolean update(FlowNode entity) throws ServiceException {
		try {
			return flowNodeDao.update(entity);
		} catch (Exception e) {
			logger.error("更新流程节点表失败", e);
			throw new ServiceException("更新流程节点表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return flowNodeDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询流程节点表数量失败", e);
			throw new ServiceException("查询流程节点表数量失败", e);
		}
	}

	@Override
	public PageResult<FlowNode> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return flowNodeDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询流程节点表分页信息失败", e);
			throw new ServiceException("查询流程节点表分页信息失败", e);
		}
	}

	@Override
	public List<FlowNode> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return flowNodeDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询流程节点表失败", e);
			throw new ServiceException("查询流程节点表失败", e);
		}
	}

	@Override
	public List<FlowNode> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return flowNodeDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询流程节点表失败", e);
			throw new ServiceException("查询流程节点表失败", e);
		}
	}

	@Override
	public List<FlowNode> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return flowNodeDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询流程节点表失败", e);
			throw new ServiceException("查询流程节点表失败", e);
		}
	}

	@Override
	public List<Object> findBySql(String sql, Object[] params) throws ServiceException {
		try {
			return flowNodeDao.findBySql(sql, params);
		} catch (Exception e) {
			logger.error("sql查询流程节点表失败", e);
			throw new ServiceException("sql查询流程节点表失败", e);
		}
	}

	/**
	 * 根据id集合查询流程节点
	 * @param ids id集合
	 * @return 流程节点信息
	 */
	@Override
	public List<FlowNode> findFlowNodeByIds(List<String> ids) {
		List<FlowNode> flowNodeList = new ArrayList<>();
		if (ids == null || ids.isEmpty()) {
			return flowNodeList;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("nodeId", Constants.ROP_IN, new ArrayList<>(new HashSet<>(ids))));
		try {
			flowNodeList = flowNodeDao.queryAllBySearchFilter(searchFilter);
		} catch (DaoException e) {
			logger.error("根据ids查询流程节点信息异常", e);
		}
		return flowNodeList;
	}
}
