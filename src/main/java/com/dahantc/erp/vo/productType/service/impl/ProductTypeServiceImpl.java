package com.dahantc.erp.vo.productType.service.impl;

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
import com.dahantc.erp.vo.productType.dao.IProductTypeDao;
import com.dahantc.erp.vo.productType.entity.ProductType;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

@Service("productTypeService")
public class ProductTypeServiceImpl implements IProductTypeService {
	private static Logger logger = LogManager.getLogger(ProductTypeServiceImpl.class);

	@Autowired
	private IProductTypeDao productTypeDao;

	@Override
	public ProductType read(Serializable id) throws ServiceException {
		try {
			return productTypeDao.read(id);
		} catch (Exception e) {
			logger.error("读取产品类型表失败", e);
			throw new ServiceException("读取产品类型表失败", e);
		}
	}

	@Override
	public boolean save(ProductType entity) throws ServiceException {
		try {
			return productTypeDao.save(entity);
		} catch (Exception e) {
			logger.error("保存产品类型表失败", e);
			throw new ServiceException("保存产品类型表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<ProductType> objs) throws ServiceException {
		try {
			return productTypeDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return productTypeDao.delete(id);
		} catch (Exception e) {
			logger.error("删除产品类型表失败", e);
			throw new ServiceException("删除产品类型表失败", e);
		}
	}

	@Override
	public boolean update(ProductType enterprise) throws ServiceException {
		try {
			return productTypeDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新产品类型表失败", e);
			throw new ServiceException("更新产品类型表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return productTypeDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询产品类型表数量失败", e);
			throw new ServiceException("查询产品类型表数量失败", e);
		}
	}

	@Override
	public PageResult<ProductType> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return productTypeDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询产品类型表分页信息失败", e);
			throw new ServiceException("查询产品类型表分页信息失败", e);
		}
	}
	
	@Override
	public List<ProductType> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return productTypeDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询产品类型表失败", e);
			throw new ServiceException("查询产品类型表失败", e);
		}
	}

	@Override
	public List<ProductType> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return productTypeDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询产品类型表失败", e);
			throw new ServiceException("查询产品类型表失败", e);
		}
	}
	
	@Override
	public List<ProductType> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return productTypeDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询产品类型表失败", e);
			throw new ServiceException("查询产品类型表失败", e);
		}
	}

	@Override
	public ProductType readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return productTypeDao.readOneByProperty(property, value);
		} catch (Exception e) {
			logger.error("查询产品类型失败", e);
			throw new ServiceException("查询产品类型失败", e);
		}
	}

	@Override
	public String getProductTypeNameByValue(int productTypeValue) {
		try {
			ProductType productType = readOneByProperty("productTypeValue", productTypeValue);
			if (null != productType) {
				return productType.getProductTypeName();
			}
		} catch (ServiceException e) {
			logger.error("查询产品类型名异常", e);
		}
		return "";
	}

	@Override
	public String getProductTypeKeyByValue(int productTypeValue) {
		try {
			ProductType productType = readOneByProperty("productTypeValue", productTypeValue);
			if (null != productType) {
				return productType.getProductTypeKey();
			}
		} catch (ServiceException e) {
			logger.error("查询产品类型标识异常", e);
		}
		return "";
	}

	@Override
	public Integer getProductTypeValueByName(String productTypeName) {
		try {
			ProductType productType = readOneByProperty("productTypeName", productTypeName);
			if (null != productType) {
				return productType.getProductTypeValue();
			}
		} catch (ServiceException e) {
			logger.error("查询产品类型值异常", e);
		}
		return -1;
	}

	@Override
	public Integer getProductTypeValueByKey(String productTypeKey) {
		try {
			ProductType productType = readOneByProperty("productTypeKey", productTypeKey);
			if (null != productType) {
				return productType.getProductTypeValue();
			}
		} catch (ServiceException e) {
			logger.error("查询产品类型值异常", e);
		}
		return -1;
	}

	@Override
	public Integer getCostPriceType(int productTypeValue) {
		try {
			ProductType productType = readOneByProperty("productTypeValue", productTypeValue);
			if (null != productType) {
				return productType.getCostPriceType();
			}
		} catch (ServiceException e) {
			logger.error("查询产品类型值异常", e);
		}
		return -1;
	}
}
