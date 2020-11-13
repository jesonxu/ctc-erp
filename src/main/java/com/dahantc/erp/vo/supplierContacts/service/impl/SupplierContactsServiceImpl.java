package com.dahantc.erp.vo.supplierContacts.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.contact.AddContactDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.supplierContacts.dao.ISupplierContactsDao;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;
import com.dahantc.erp.vo.supplierContacts.service.ISupplierContactsService;
import com.dahantc.erp.vo.supplierContactsHistory.entity.SupplierContactsHistory;
import org.springframework.util.CollectionUtils;

@Service("supplierContactsService")
public class SupplierContactsServiceImpl implements ISupplierContactsService {
	private static Logger logger = LogManager.getLogger(SupplierContactsServiceImpl.class);

	@Autowired
	private ISupplierContactsDao supplierContactsDao;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public SupplierContacts read(Serializable id) throws ServiceException {
		try {
			return supplierContactsDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商联系人表失败", e);
			throw new ServiceException("读取供应商联系人表失败", e);
		}
	}

	@Override
	public boolean save(SupplierContacts entity) throws ServiceException {
		try {
			return supplierContactsDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商联系人表失败", e);
			throw new ServiceException("保存供应商联系人表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return supplierContactsDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商联系人表失败", e);
			throw new ServiceException("删除供应商联系人表失败", e);
		}
	}

	@Override
	public boolean update(SupplierContacts enterprise) throws ServiceException {
		try {
			return supplierContactsDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商联系人表失败", e);
			throw new ServiceException("更新供应商联系人表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人表数量失败", e);
			throw new ServiceException("查询供应商联系人表数量失败", e);
		}
	}

	@Override
	public PageResult<SupplierContacts> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人表分页信息失败", e);
			throw new ServiceException("查询供应商联系人表分页信息失败", e);
		}
	}

	@Override
	public List<SupplierContacts> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人表失败", e);
			throw new ServiceException("查询供应商联系人表失败", e);
		}
	}

	@Override
	public List<SupplierContacts> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return supplierContactsDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商联系人表失败", e);
			throw new ServiceException("查询供应商联系人表失败", e);
		}
	}

	@Override
	public List<SupplierContacts> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return supplierContactsDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商联系人表失败", e);
			throw new ServiceException("查询供应商联系人表失败", e);
		}
	}

	@Override
	public void editSupplierDept(List<SupplierContacts> contacts) {
		List<SupplierContacts> addContacts = new ArrayList<>();
		List<SupplierContacts> modityContacts = new ArrayList<>();
		List<SupplierContactsHistory> historys = new ArrayList<>();
		try {
			if (contacts != null && !contacts.isEmpty()) {
				contacts.forEach(c -> {
					try {
						if (StringUtils.isNotBlank(c.getSupplierContactsId())) {
							SupplierContacts sc = read(c.getSupplierContactsId());
							if (sc != null) {
								historys.add(SupplierContactsHistory.buildHistory(sc));
								c.setSupplierContactsId(sc.getSupplierContactsId());
								c.setSupplierId(sc.getSupplierId());
								c.setWtime(new Timestamp(System.currentTimeMillis()));
								modityContacts.add(c);
								return;
							}
						}
						addContacts.add(c);
					} catch (Exception e) {
						logger.error("处理供应商部门信息异常", e);
					}
				});
				if (!historys.isEmpty()) {
					baseDao.saveByBatch(historys, true);
				}
				if (!addContacts.isEmpty()) {
					baseDao.saveByBatch(addContacts, true);
				}
				if (!modityContacts.isEmpty()) {
					baseDao.updateByBatch(modityContacts, true);
				}
			}
		} catch (Exception e) {
			logger.error("修改供应商部门异常", e);
		}
	}

	/**
	 * 获取客户的联系人，在一段时间内是否有变动
	 *
	 * @param customerIds
	 *            客户id
	 * @param month
	 *            月
	 * @param days
	 *            天
	 * @return 联系人在指定时间内，是否有变更信息
	 */
	@Override
	public Map<String, Boolean> customerContactChangeInfo(List<String> customerIds, Integer month, Integer days) {
		Map<String, Boolean> changeInfo = new HashMap<>();
		if (customerIds == null || customerIds.isEmpty() || (month == null && days == null)) {
			return changeInfo;
		}
		Timestamp time = null;
		if (month != null) {
			time = new Timestamp(DateUtil.getMonthBefore(month));
		}
		if (days != null) {
			Date timePoint = new Date();
			if (time != null) {
				timePoint = new Date(time.getTime());
			}
			time = new Timestamp(DateUtil.getDayBefore(timePoint, days));
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, customerIds));
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, time));
		List<SupplierContacts> contactsList = null;
		try {
			contactsList = queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询客户联系人异常", e);
		}
		if (contactsList != null && !contactsList.isEmpty()) {
			Map<String, IntSummaryStatistics> contactCountInfo = contactsList.stream()
					.filter(contact -> StringUtil.isNotBlank(contact.getFirstPhone())
							|| StringUtil.isNotBlank(contact.getSecondPhone())
							|| StringUtil.isNotBlank(contact.getTelephone())
							|| StringUtil.isNotBlank(contact.getWx())
							|| StringUtil.isNotBlank(contact.getQq())
							|| StringUtil.isNotBlank(contact.getEmail()))
					.collect(Collectors.groupingBy(SupplierContacts::getSupplierId, Collectors.summarizingInt(value -> 1)));
			for (String customerId:customerIds){
				IntSummaryStatistics summary = contactCountInfo.get(customerId);
				changeInfo.put(customerId, summary !=null && summary.getSum() > 0);
			}
		}
		return changeInfo;
	}

	/**
	 * 添加联系人
	 *
	 * @param contactDto 添加参数
	 * @return 添加结果
	 */
	@Override
	public BaseResponse<Boolean> addContact(AddContactDto contactDto) {
		try {
			String contactName = contactDto.getContactsName();
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("contactsName", Constants.ROP_CN, contactName));
			searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, contactDto.getEntityId()));
			List<SupplierContacts> contactsList = supplierContactsDao.queryAllBySearchFilter(searchFilter);
			if (!CollectionUtils.isEmpty(contactsList)) {
				return BaseResponse.error("联系人已经存在");
			}
			boolean saveResult = supplierContactsDao.save(contactDto.getContactInfo());
			if (saveResult) {
				return BaseResponse.success("添加联系人成功");
			}
		} catch (Exception e) {
			logger.error("单个添加联系人错误", e);
		}
		return BaseResponse.error("添加失败");
	}
}
