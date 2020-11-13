package com.dahantc.erp.vo.dianshangProduct.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.dsProduct.DsSaveProductDto;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dianshangProduct.dao.IDianShangProductDao;
import com.dahantc.erp.vo.dianshangProduct.entity.DianShangProduct;
import com.dahantc.erp.vo.dianshangProduct.service.IDianShangProductService;

@Service("dianshangProductService")
public class DianShangProductServiceImpl implements IDianShangProductService {
	private static Logger logger = LogManager.getLogger(DianShangProductServiceImpl.class);

	@Autowired
	private IDianShangProductDao dianshangProductDao;

	@Override
	public DianShangProduct read(Serializable id) throws ServiceException {
		try {
			return dianshangProductDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商商品表失败", e);
			throw new ServiceException("读取电商商品表失败", e);
		}
	}

	@Override
	public boolean save(DianShangProduct entity) throws ServiceException {
		try {
			return dianshangProductDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商商品表失败", e);
			throw new ServiceException("保存电商商品表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DianShangProduct> objs) throws ServiceException {
		try {
			return dianshangProductDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dianshangProductDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商商品表失败", e);
			throw new ServiceException("删除电商商品表失败", e);
		}
	}

	@Override
	public boolean update(DianShangProduct enterprise) throws ServiceException {
		try {
			return dianshangProductDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商商品表失败", e);
			throw new ServiceException("更新电商商品表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dianshangProductDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商商品表数量失败", e);
			throw new ServiceException("查询电商商品表数量失败", e);
		}
	}

	@Override
	public PageResult<DianShangProduct> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dianshangProductDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商商品表分页信息失败", e);
			throw new ServiceException("查询电商商品表分页信息失败", e);
		}
	}
	
	@Override
	public List<DianShangProduct> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dianshangProductDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商商品表失败", e);
			throw new ServiceException("查询电商商品表失败", e);
		}
	}

	@Override
	public List<DianShangProduct> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dianshangProductDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商商品表失败", e);
			throw new ServiceException("查询电商商品表失败", e);
		}
	}
	
	@Override
	public List<DianShangProduct> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dianshangProductDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商商品表失败", e);
			throw new ServiceException("查询电商商品表失败", e);
		}
	}
	
	/**
	 * 保存产品基本信息
	 * 
	 * @param creatorid
	 */
	@Override
	public BaseResponse<String> saveProduct(DsSaveProductDto dto) throws ServiceException {
		long start = System.currentTimeMillis();
		// 判断是否是添加
		boolean isCreate = StringUtils.isBlank(dto.getProductid());
		try {
			DianShangProduct product = isCreate ? new DianShangProduct() : read(dto.getProductid());
			if (!isCreate) {
				product = read(dto.getProductid());
			}
			if (product == null) {
				logger.info("产品不存在");
				return BaseResponse.error("产品不存在");
			}
			if (isCreate) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("productname", Constants.ROP_EQ, dto.getProductname()));
				filter.getRules().add(new SearchRule("supplierid", Constants.ROP_EQ, dto.getSupplierid()));
				List<DianShangProduct> list = queryAllBySearchFilter(filter);
				if (list != null && !list.isEmpty()) {
					logger.info("产品名称已存在");
					return BaseResponse.error("产品名称已存在");
				}
			} else {
				if (!StringUtils.equals(product.getProductname(), dto.getProductname())) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("productname", Constants.ROP_EQ, dto.getProductname()));
					List<DianShangProduct> list = queryAllBySearchFilter(filter);
					if (list != null && !list.isEmpty()) {
						logger.info("产品名称已存在");
						return BaseResponse.error("产品名称已存在");
					}
				}
			}
			if (!isCreate) {
				product.setDsproductid(dto.getProductid());
			} else {
				product.setSupplierid(dto.getSupplierid());
			}
			product.setProductname(dto.getProductname());
			product.setFormat(dto.getFormat());
			if (dto.getGroupnumber() > 1) {
				product.setGroupnumber(dto.getGroupnumber());
			} else {
				logger.info("商品团购数量不合法");
				return BaseResponse.error("商品团购数量不合法");
			}
			BigDecimal groupprice = new BigDecimal(dto.getGroupprice());
			groupprice=groupprice.setScale(2, BigDecimal.ROUND_HALF_UP);
			product.setGroupprice(groupprice);
			product.setOssuserid(dto.getOssuserid());
			product.setPcode(dto.getPcode());
			product.setOnsale(dto.getOnsale());
			if (StringUtils.isNotEmpty(dto.getPeriod())) {
				product.setPeriod(Integer.parseInt(dto.getPeriod()));
			}
			if (StringUtils.isNotEmpty(dto.getPicture())) {
				product.setPicture(dto.getPicture());
			}
			product.setProducttype(dto.getProducttype());
			product.setRant(Integer.parseInt(dto.getRant()));
			if (StringUtils.isNoneEmpty(dto.getRemark())) {
				product.setRemark(dto.getRemark());
			}
			BigDecimal standardprice = new BigDecimal(dto.getStandardprice());
			standardprice=standardprice.setScale(2, BigDecimal.ROUND_HALF_UP);
			product.setStandardprice(standardprice);
			if (StringUtil.isNotBlank(dto.getWholesaleprice())) {
				BigDecimal wholesaleprice = new BigDecimal(dto.getWholesaleprice());
				wholesaleprice=wholesaleprice.setScale(2, BigDecimal.ROUND_HALF_UP);
				product.setWholesaleprice(wholesaleprice);
			}
			boolean result = false;
			if (isCreate) {
				result = save(product);
			} else {
				result = update(product);
			}
			if (!result) {
				return BaseResponse.error("保存失败");
			}
		} catch (Exception e) {
			logger.error("保存失败", e);
			return BaseResponse.error("保存失败");
		}
		logger.info("保存产品信息成功,共耗时:" + (System.currentTimeMillis() - start));
		return BaseResponse.success("保存成功");
	}
}
