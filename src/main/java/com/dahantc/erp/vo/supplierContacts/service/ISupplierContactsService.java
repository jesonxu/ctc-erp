package com.dahantc.erp.vo.supplierContacts.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.contact.AddContactDto;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;

public interface ISupplierContactsService {
	SupplierContacts read(Serializable id) throws ServiceException;

	boolean save(SupplierContacts entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SupplierContacts enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SupplierContacts> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<SupplierContacts> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SupplierContacts> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<SupplierContacts> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	void editSupplierDept(List<SupplierContacts> contacts);

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
	Map<String, Boolean> customerContactChangeInfo(List<String> customerIds, Integer month, Integer days);

	/**
	 * 添加联系人
	 *
	 * @param contactDto 添加参数
	 * @return 添加结果
	 */
	BaseResponse<Boolean> addContact(AddContactDto contactDto);
}
