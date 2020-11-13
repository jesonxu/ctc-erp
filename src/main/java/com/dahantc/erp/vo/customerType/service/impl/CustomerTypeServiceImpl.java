package com.dahantc.erp.vo.customerType.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.vo.customerType.dao.ICustomerTypeDao;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;

@Service("customerTypeService")
public class CustomerTypeServiceImpl implements ICustomerTypeService {
	private static Logger logger = LogManager.getLogger(CustomerTypeServiceImpl.class);

	@Autowired
	private ICustomerTypeDao customerTypeDao;

	@Override
	public CustomerType read(Serializable id) throws ServiceException {
		try {
			return customerTypeDao.read(id);
		} catch (Exception e) {
			logger.error("读取客户类型失败", e);
			throw new ServiceException("读取客户类型失败", e);
		}
	}

	@Override
	public boolean save(CustomerType entity) throws ServiceException {
		try {
			return customerTypeDao.save(entity);
		} catch (Exception e) {
			logger.error("保存客户类型失败", e);
			throw new ServiceException("保存客户类型失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<CustomerType> objs) throws ServiceException {
		try {
			return customerTypeDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return customerTypeDao.delete(id);
		} catch (Exception e) {
			logger.error("删除客户类型失败", e);
			throw new ServiceException("删除客户类型失败", e);
		}
	}

	@Override
	public boolean update(CustomerType enterprise) throws ServiceException {
		try {
			return customerTypeDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新客户类型失败", e);
			throw new ServiceException("更新客户类型失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return customerTypeDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询客户类型数量失败", e);
			throw new ServiceException("查询客户类型数量失败", e);
		}
	}

	@Override
	public PageResult<CustomerType> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return customerTypeDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询客户类型分页信息失败", e);
			throw new ServiceException("查询客户类型分页信息失败", e);
		}
	}

	@Override
	public List<CustomerType> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return customerTypeDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询客户类型失败", e);
			throw new ServiceException("查询客户类型失败", e);
		}
	}

	@Override
	public List<CustomerType> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return customerTypeDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户类型失败", e);
			throw new ServiceException("查询客户类型失败", e);
		}
	}

	@Override
	public List<CustomerType> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return customerTypeDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询客户类型失败", e);
			throw new ServiceException("查询客户类型失败", e);
		}
	}

	/**
	 * 查询所有的客户类型
	 *
	 * @return List<CustomerType>
	 */
	@Override
	public List<CustomerType> findAllCustomerType() {
		List<CustomerType> countList = new ArrayList<>();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
			countList = queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("查询客户类型，数据错误：" + e.getMessage(), e);
		}
		return countList;
	}

	/**
	 * 统计不同类型的客户的未处理流程的数量
	 *
	 * @param customers
	 *            客户信息列表
	 * @return
	 */
	@Override
	public List<CustomerTypeRespDto> countCustomerType(List<CustomerRespDto> customers) {
		// 统计结果
		List<CustomerTypeRespDto> countResults = new ArrayList<>();
		// 客户类型
		List<CustomerType> customerTypes = findAllCustomerType();
		if (customerTypes != null) {
			// 遍历客户类型列表
			for (CustomerType customerType : customerTypes) {
				// 此类型id
				String customerTypeId = customerType.getCustomerTypeId();
				// 供应商数量
				int customerCount = 0;
				// 流程数量
				long flowCount = 0L;
				if (customers != null && !customers.isEmpty()) {
					// 遍历客户信息列表，统计此类型的客户数和未处理流程数
					for (CustomerRespDto customer : customers) {
						if (customerTypeId.equals(customer.getCustomerTypeId())) {
							// 统计客户数量
							customerCount++;
							// 统计客户类型 未处理流程数
							flowCount += (customer.getFlowEntCount() == null ? 0 : customer.getFlowEntCount());
						}
					}
				}
				countResults.add(new CustomerTypeRespDto(customerType, flowCount, customerCount));
			}
		}
		return countResults;
	}

	@Override
	public String getCustomerTypeIdByValue(int value) {
		try {
			CustomerType customerType = readOneByProperty("customerTypeValue", value);
			if (null != customerType) {
				return customerType.getCustomerTypeId();
			}
		} catch (ServiceException e) {
			logger.error("按客户类型值获取客户类型id异常", e);
		}
		return null;
	}

	@Override
	public CustomerType getCustomerTypeByValue(int value) {
		try {
			return readOneByProperty("customerTypeValue", value);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CustomerType readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return customerTypeDao.readOneByProperty(property, value);
		} catch (DaoException e) {
			throw new ServiceException(e);
		} catch (Exception e) {
			logger.error("查询客户类型失败", e);
			throw new ServiceException("查询客户类型失败", e);
		}
	}
	
	@Override
	public boolean validatePublicType(String customerTypeId) {
		// 判断是否是公共池类型
		boolean isPublicType = false;
		if (StringUtil.isBlank(customerTypeId))
			return false;
		try {
			CustomerType type = read(customerTypeId);
			if (type != null && type.getCustomerTypeValue() == CustomerTypeValue.PUBLIC.getCode()) {
				isPublicType = true;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return isPublicType;
	}
}
