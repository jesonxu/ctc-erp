package com.dahantc.erp.vo.invoice.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.invoice.dao.IInvoiceDao;
import com.dahantc.erp.vo.invoice.entity.Invoice;
import com.dahantc.erp.vo.invoice.service.IInvoiceService;
import com.dahantc.erp.vo.invoice.service.InvoiceExtendDto;

@Service("invoiceService")
public class InvoiceServiceImpl implements IInvoiceService {
	private static Logger logger = LogManager.getLogger(InvoiceServiceImpl.class);

	private static final String QUERY_COMPLETE_INVOICE_SQL = "SELECT u.deptid, cp.ossuserid, i.entityid, SUM(i.receivables), SUM(i.actualreceivables), i.wtime FROM erp_invoice i"
			+ " INNER JOIN erp_customer_product cp ON i.productid = cp.productid INNER JOIN erp_user u ON cp.ossuserid = u.ossuserid WHERE i.entitytype = "
			+ EntityType.CUSTOMER.ordinal();

	@Autowired
	private IInvoiceDao invoiceDao;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public Invoice read(Serializable id) throws ServiceException {
		try {
			return invoiceDao.read(id);
		} catch (Exception e) {
			logger.error("读取发票表失败", e);
			throw new ServiceException("读取发票表失败", e);
		}
	}

	@Override
	public boolean save(Invoice entity) throws ServiceException {
		try {
			return invoiceDao.save(entity);
		} catch (Exception e) {
			logger.error("保存发票表失败", e);
			throw new ServiceException("保存发票表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Invoice> objs) throws ServiceException {
		try {
			return invoiceDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return invoiceDao.delete(id);
		} catch (Exception e) {
			logger.error("删除发票表失败", e);
			throw new ServiceException("删除发票表失败", e);
		}
	}

	@Override
	public boolean update(Invoice enterprise) throws ServiceException {
		try {
			return invoiceDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新发票表失败", e);
			throw new ServiceException("更新发票表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return invoiceDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询发票表数量失败", e);
			throw new ServiceException("查询发票表数量失败", e);
		}
	}

	@Override
	public PageResult<Invoice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return invoiceDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询发票表分页信息失败", e);
			throw new ServiceException("查询发票表分页信息失败", e);
		}
	}

	@Override
	public List<Invoice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return invoiceDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询发票表失败", e);
			throw new ServiceException("查询发票表失败", e);
		}
	}

	@Override
	public List<Invoice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return invoiceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询发票表失败", e);
			throw new ServiceException("查询发票表失败", e);
		}
	}

	@Override
	public List<Invoice> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return invoiceDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询发票表失败", e);
			throw new ServiceException("查询发票表失败", e);
		}
	}

	@Override
	public List<InvoiceExtendDto> queryInvoices(Date yearDate, List<String> userIds, String settleType) {
		try {
			String sql = QUERY_COMPLETE_INVOICE_SQL;
			Map<String, Object> params = new HashMap<>();
			if (StringUtils.isNotBlank(settleType) && NumberUtils.isParsable(settleType)) {
				int sType = Integer.valueOf(settleType);
				if (sType > 0) {
					if (sType == SettleType.After.ordinal()) {
						sql += " AND cp.settletype = :settleType";
						params.put("settleType", SettleType.After.ordinal());
					} else {
						sql += " AND cp.settletype <> :settleType";
						params.put("settleType", SettleType.After.ordinal());
					}
				}
			}
			if (!CollectionUtils.isEmpty(userIds)) {
				sql += " AND cp.ossuserid IN (:ossUserIds)";
				params.put("ossUserIds", userIds);
			}
			sql += " AND i.wtime >= :startDate AND i.wtime < :endDate GROUP BY i.entityid";
			params.put("startDate", yearDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(yearDate);
			calendar.add(Calendar.YEAR, 1);
			params.put("endDate", calendar.getTime());
			List<Object[]> list = baseDao.selectSQL(sql, params);
			Map<String, InvoiceExtendDto> map = new HashMap<>();
			list.stream().filter(obj -> obj != null && obj.length >= 6 && StringUtils.isNoneBlank((String) obj[0], (String) obj[1])).forEach(obj -> {
				String key = (String) obj[0] + (String) obj[1] + (String) obj[2] + DateUtil.convert((Date) obj[5], DateUtil.format4);
				if (!map.containsKey(key)) {
					map.put(key, new InvoiceExtendDto());
					map.get(key).setDeptId((String) obj[0]);
					map.get(key).setOssUserId((String) obj[1]);
					map.get(key).setEntityId((String) obj[2]);
					map.get(key).setWtime(new Timestamp(DateUtil.convert4(DateUtil.convert((Date) obj[5], DateUtil.format4)).getTime()));
				}
				map.get(key).setReceivables(map.get(key).getReceivables().add(obj[3] == null ? BigDecimal.ZERO : new BigDecimal(obj[3].toString())));
				map.get(key)
						.setActualReceivables(map.get(key).getActualReceivables().add(obj[4] == null ? BigDecimal.ZERO : new BigDecimal(obj[4].toString())));
			});
			return new ArrayList<>(map.values());
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
}
