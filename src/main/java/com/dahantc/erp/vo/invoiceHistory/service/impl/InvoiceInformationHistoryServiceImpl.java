package com.dahantc.erp.vo.invoiceHistory.service.impl;

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
import com.dahantc.erp.vo.invoiceHistory.dao.IInvoiceInformationHistoryDao;
import com.dahantc.erp.vo.invoiceHistory.entity.InvoiceInformationHistory;
import com.dahantc.erp.vo.invoiceHistory.service.IInvoiceInformationHistoryService;

@Service("invoiceInformationHistoryService")
public class InvoiceInformationHistoryServiceImpl implements IInvoiceInformationHistoryService {
	private static Logger logger = LogManager.getLogger(InvoiceInformationHistoryServiceImpl.class);

	@Autowired
	private IInvoiceInformationHistoryDao invoiceInformationHistoryDao;

	@Override
	public InvoiceInformationHistory read(Serializable id) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.read(id);
		} catch (Exception e) {
			logger.error("读取开票信息处理结果表失败", e);
			throw new ServiceException("读取开票信息处理结果表失败", e);
		}
	}

	@Override
	public boolean save(InvoiceInformationHistory entity) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.save(entity);
		} catch (Exception e) {
			logger.error("保存开票信息处理结果表失败", e);
			throw new ServiceException("保存开票信息处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<InvoiceInformationHistory> objs) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.delete(id);
		} catch (Exception e) {
			logger.error("删除开票信息处理结果表失败", e);
			throw new ServiceException("删除开票信息处理结果表失败", e);
		}
	}

	@Override
	public boolean update(InvoiceInformationHistory enterprise) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新开票信息处理结果表失败", e);
			throw new ServiceException("更新开票信息处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表数量失败", e);
			throw new ServiceException("查询开票信息处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<InvoiceInformationHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表分页信息失败", e);
			throw new ServiceException("查询开票信息处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<InvoiceInformationHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表失败", e);
			throw new ServiceException("查询开票信息处理结果表失败", e);
		}
	}

	@Override
	public List<InvoiceInformationHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表失败", e);
			throw new ServiceException("查询开票信息处理结果表失败", e);
		}
	}

	@Override
	public List<InvoiceInformationHistory> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return invoiceInformationHistoryDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表失败", e);
			throw new ServiceException("查询开票信息处理结果表失败", e);
		}
	}
}
