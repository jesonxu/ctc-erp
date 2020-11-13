package com.dahantc.erp.vo.suppliertype.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.SupplierType.SupplierTypeRspDto;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.vo.suppliertype.dao.ISupplierTypeDao;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;

@Service("supplierTypeService")
public class SupplierTypeServiceImpl implements ISupplierTypeService {
	private static Logger logger = LogManager.getLogger(SupplierTypeServiceImpl.class);

	@Autowired
	private ISupplierTypeDao SupplierTypeDao;

	@Override
	public SupplierType read(Serializable id) throws ServiceException {
		try {
			return SupplierTypeDao.read(id);
		} catch (Exception e) {
			logger.error("读取供应商类别信息表失败", e);
			throw new ServiceException("读取供应商类别信息表失败", e);
		}
	}

	@Override
	public boolean save(SupplierType entity) throws ServiceException {
		try {
			return SupplierTypeDao.save(entity);
		} catch (Exception e) {
			logger.error("保存供应商类别信息表失败", e);
			throw new ServiceException("保存供应商类别信息表失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return SupplierTypeDao.delete(id);
		} catch (Exception e) {
			logger.error("删除供应商类别信息表失败", e);
			throw new ServiceException("删除供应商类别信息表失败", e);
		}
	}

	@Override
	public boolean update(SupplierType enterprise) throws ServiceException {
		try {
			return SupplierTypeDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新供应商类别信息表失败", e);
			throw new ServiceException("更新供应商类别信息表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return SupplierTypeDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询供应商类别信息表数量失败", e);
			throw new ServiceException("查询供应商类别信息表数量失败", e);
		}
	}

	@Override
	public PageResult<SupplierType> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return SupplierTypeDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询供应商类别信息表分页信息失败", e);
			throw new ServiceException("查询供应商类别信息表分页信息失败", e);
		}
	}

	@Override
	public List<SupplierType> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return SupplierTypeDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询供应商类别信息表失败", e);
			throw new ServiceException("查询供应商类别信息表失败", e);
		}
	}

	@Override
	public List<SupplierType> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return SupplierTypeDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询供应商类别信息表失败", e);
			throw new ServiceException("查询供应商类别信息表失败", e);
		}
	}

	@Override
	public List<SupplierType> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return SupplierTypeDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询供应商类别信息表失败", e);
			throw new ServiceException("查询供应商类别信息表失败", e);
		}
	}

	@Override
	public List<SupplierType> queryAll() {
		SearchFilter filter = new SearchFilter();
		filter.getOrders().add(new SearchOrder("sequence", Constants.ROP_ASC));
		List<SupplierType> supplierTypes = null;
		try {
			// 读取供应商类型
			supplierTypes = this.queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("查询供应商类型信息出现错误", e);
		}
		return supplierTypes;
	}

	/**
	 * 统计供应商类型 未处理流程
	 *
	 * @param suppliers
	 *            供应商
	 * @return 供应商类型
	 */
	@Override
	public List<SupplierTypeRspDto> countSupplierTypes(List<SupplierRspDto> suppliers) {
		// 统计结果
		List<SupplierTypeRspDto> countResults = new ArrayList<>();
		// 供应商类型
		List<SupplierType> supplierTypes = queryAll();
		if (supplierTypes != null) {
			// 供应商
			for (SupplierType supplierType : supplierTypes) {
				String supplierTypeId = supplierType.getSupplierTypeId();
				// 供应商数量
				int supplierCount = 0;
				// 流程数量
				long flowCount = 0L;
				if (suppliers != null && !suppliers.isEmpty()) {
					for (SupplierRspDto supplierRspDto : suppliers) {
						if (supplierTypeId.equals(supplierRspDto.getSupplierTypeId())) {
							// 统计供应商数量
							supplierCount++;
							// 统计供应商类型 未处理流程数
							flowCount += (supplierRspDto.getFlowEntCount() == null ? 0 : supplierRspDto.getFlowEntCount());
						}
					}
				}
				countResults.add(new SupplierTypeRspDto(supplierType, supplierCount, flowCount));
			}
		}
		return countResults;
	}
}
