package com.dahantc.erp.vo.customerChangeRecord.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.customerChangeRecord.entity.CustomerChangeRecord;
import com.dahantc.erp.vo.customerChangeRecord.vo.CustomerChangeVo;

/**
 * @author 8520
 */
public interface ICustomerChangeRecordService {

	CustomerChangeRecord read(Serializable id) throws ServiceException;

	boolean save(CustomerChangeRecord entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CustomerChangeRecord enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<CustomerChangeRecord> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<CustomerChangeRecord> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CustomerChangeRecord> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<CustomerChangeRecord> findByHql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<CustomerChangeRecord> objs) throws ServiceException;


	/**
	 * 查询客户变更统计
	 *
	 * @param user    当前登录用户
	 * @param deptIds 部门
	 * @param userIds  用户
	 * @param month   月份
	 * @return 统计信息
	 */
	List<CustomerChangeVo> queryCustomerChangeInfo(OnlineUser user, List<String> deptIds, List<String> userIds , Date month);
}
