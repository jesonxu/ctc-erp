package com.dahantc.erp.vo.invoice.service.impl;

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
import com.dahantc.erp.vo.invoice.dao.IInvoiceInformationDao;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;

@Service("invoiceInformationService")
public class InvoiceInformationServiceImpl implements IInvoiceInformationService {
	private static Logger logger = LogManager.getLogger(InvoiceInformationServiceImpl.class);

	@Autowired
	private IInvoiceInformationDao invoiceInformationDao;

	@Override
	public InvoiceInformation read(Serializable id) throws ServiceException {
		try {
			return invoiceInformationDao.read(id);
		} catch (Exception e) {
			logger.error("读取开票信息处理结果表失败", e);
			throw new ServiceException("读取开票信息处理结果表失败", e);
		}
	}

	@Override
	public boolean save(InvoiceInformation entity) throws ServiceException {
		try {
			return invoiceInformationDao.save(entity);
		} catch (Exception e) {
			logger.error("保存开票信息处理结果表失败", e);
			throw new ServiceException("保存开票信息处理结果表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<InvoiceInformation> objs) throws ServiceException {
		try {
			return invoiceInformationDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return invoiceInformationDao.delete(id);
		} catch (Exception e) {
			logger.error("删除开票信息处理结果表失败", e);
			throw new ServiceException("删除开票信息处理结果表失败", e);
		}
	}

	@Override
	public boolean update(InvoiceInformation enterprise) throws ServiceException {
		try {
			return invoiceInformationDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新开票信息处理结果表失败", e);
			throw new ServiceException("更新开票信息处理结果表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表数量失败", e);
			throw new ServiceException("查询开票信息处理结果表数量失败", e);
		}
	}

	@Override
	public PageResult<InvoiceInformation> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表分页信息失败", e);
			throw new ServiceException("查询开票信息处理结果表分页信息失败", e);
		}
	}

	@Override
	public List<InvoiceInformation> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表失败", e);
			throw new ServiceException("查询开票信息处理结果表失败", e);
		}
	}

	@Override
	public List<InvoiceInformation> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return invoiceInformationDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表失败", e);
			throw new ServiceException("查询开票信息处理结果表失败", e);
		}
	}

	@Override
	public List<InvoiceInformation> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return invoiceInformationDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询开票信息处理结果表失败", e);
			throw new ServiceException("查询开票信息处理结果表失败", e);
		}
	}
}
