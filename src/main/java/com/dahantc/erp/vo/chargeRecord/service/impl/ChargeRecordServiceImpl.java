package com.dahantc.erp.vo.chargeRecord.service.impl;

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
import com.dahantc.erp.vo.chargeRecord.dao.IChargeRecordDao;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;

@Service("chargeRecordService")
public class ChargeRecordServiceImpl implements IChargeRecordService {
	private static Logger logger = LogManager.getLogger(ChargeRecordServiceImpl.class);

	@Autowired
	private IChargeRecordDao chargeRecordDao;

	@Override
	public ChargeRecord read(Serializable id) throws ServiceException {
		try {
			return chargeRecordDao.read(id);
		} catch (Exception e) {
			logger.error("读取充值记录表失败", e);
			throw new ServiceException("读取充值记录表失败", e);
		}
	}

	@Override
	public boolean save(ChargeRecord entity) throws ServiceException {
		try {
			return chargeRecordDao.save(entity);
		} catch (Exception e) {
			logger.error("保存充值记录表失败", e);
			throw new ServiceException("保存充值记录表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return chargeRecordDao.delete(id);
		} catch (Exception e) {
			logger.error("删除充值记录表失败", e);
			throw new ServiceException("删除充值记录表失败", e);
		}
	}

	@Override
	public boolean update(ChargeRecord enterprise) throws ServiceException {
		try {
			return chargeRecordDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新充值记录表失败", e);
			throw new ServiceException("更新充值记录表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return chargeRecordDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询充值记录表数量失败", e);
			throw new ServiceException("查询充值记录表数量失败", e);
		}
	}

	@Override
	public PageResult<ChargeRecord> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return chargeRecordDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询充值记录表分页信息失败", e);
			throw new ServiceException("查询充值记录表分页信息失败", e);
		}
	}

	@Override
	public List<ChargeRecord> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return chargeRecordDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询充值记录表失败", e);
			throw new ServiceException("查询充值记录表失败", e);
		}
	}

	@Override
	public List<ChargeRecord> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return chargeRecordDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询充值记录表失败", e);
			throw new ServiceException("查询充值记录表失败", e);
		}
	}

	@Override
	public List<ChargeRecord> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return chargeRecordDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询充值记录表失败", e);
			throw new ServiceException("查询充值记录表失败", e);
		}
	}

}
