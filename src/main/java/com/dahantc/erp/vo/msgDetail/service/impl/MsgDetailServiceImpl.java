package com.dahantc.erp.vo.msgDetail.service.impl;

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
import com.dahantc.erp.vo.msgDetail.dao.IMsgDetailDao;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;

@Service("msgDetailService")
public class MsgDetailServiceImpl implements IMsgDetailService {
	private static Logger logger = LogManager.getLogger(MsgDetailServiceImpl.class);

	@Autowired
	private IMsgDetailDao msgDetailDao;

	@Override
	public MsgDetail read(Serializable id) throws ServiceException {
		try {
			return msgDetailDao.read(id);
		} catch (Exception e) {
			logger.error("读取消息阅读计详情表失败", e);
			throw new ServiceException("读取消息阅读计详情表失败", e);
		}
	}

	@Override
	public boolean save(MsgDetail entity) throws ServiceException {
		try {
			return msgDetailDao.save(entity);
		} catch (Exception e) {
			logger.error("保存消息阅读计详情表失败", e);
			throw new ServiceException("保存消息阅读计详情表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<MsgDetail> objs) throws ServiceException {
		try {
			return msgDetailDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return msgDetailDao.delete(id);
		} catch (Exception e) {
			logger.error("删除消息阅读计详情表失败", e);
			throw new ServiceException("删除消息阅读计详情表失败", e);
		}
	}

	@Override
	public boolean update(MsgDetail enterprise) throws ServiceException {
		try {
			return msgDetailDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新消息阅读计详情表失败", e);
			throw new ServiceException("更新消息阅读计详情表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return msgDetailDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询消息阅读计详情表数量失败", e);
			throw new ServiceException("查询消息阅读计详情表数量失败", e);
		}
	}

	@Override
	public PageResult<MsgDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return msgDetailDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询消息阅读计详情表分页信息失败", e);
			throw new ServiceException("查询消息阅读计详情表分页信息失败", e);
		}
	}
	
	@Override
	public List<MsgDetail> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return msgDetailDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询消息阅读计详情表失败", e);
			throw new ServiceException("查询消息阅读计详情表失败", e);
		}
	}

	@Override
	public List<MsgDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return msgDetailDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询消息阅读计详情表失败", e);
			throw new ServiceException("查询消息阅读计详情表失败", e);
		}
	}
	
	@Override
	public List<MsgDetail> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return msgDetailDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询消息阅读计详情表失败", e);
			throw new ServiceException("查询消息阅读计详情表失败", e);
		}
	}
}
