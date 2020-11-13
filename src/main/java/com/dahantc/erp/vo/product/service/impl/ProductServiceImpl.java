package com.dahantc.erp.vo.product.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import com.dahantc.erp.dto.product.SaveProductReqDto;
import com.dahantc.erp.vo.product.dao.IProductDao;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.region.entity.Region;
import com.dahantc.erp.vo.region.service.IRegionService;
import com.dahantc.erp.vo.supplier.dao.ISupplierDao;
import com.dahantc.erp.vo.supplier.entity.Supplier;

@Service("productService")
public class ProductServiceImpl implements IProductService {
	private static Logger logger = LogManager.getLogger(ProductServiceImpl.class);

	@Autowired
	private IProductDao productDao;

	@Autowired
	private IRegionService regionService;

	@Autowired
	private ISupplierDao supplierDao;

	@Override
	public Product read(Serializable id) throws ServiceException {
		try {
			return productDao.read(id);
		} catch (Exception e) {
			logger.error("读取产品信息失败", e);
			throw new ServiceException("读取产品信息失败", e);
		}
	}

	@Override
	public boolean save(Product entity) throws ServiceException {
		try {
			return productDao.save(entity);
		} catch (Exception e) {
			logger.error("保存产品信息失败", e);
			throw new ServiceException("保存产品信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return productDao.delete(id);
		} catch (Exception e) {
			logger.error("删除产品信息失败", e);
			throw new ServiceException("删除产品信息失败", e);
		}
	}

	@Override
	public boolean update(Product enterprise) throws ServiceException {
		try {
			return productDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新产品信息失败", e);
			throw new ServiceException("更新产品信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return productDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询产品信息数量失败", e);
			throw new ServiceException("查询产品信息数量失败", e);
		}
	}

	@Override
	public PageResult<Product> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return productDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询产品信息分页信息失败", e);
			throw new ServiceException("查询产品信息分页信息失败", e);
		}
	}

	@Override
	public List<Product> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return productDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询产品信息数量失败", e);
			throw new ServiceException("查询产品信息数量失败", e);
		}
	}

	/**
	 * 保存产品信息（新增或更新）
	 *
	 * @param dto
	 *            后台提交数据封装的对象
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public BaseResponse<String> saveProduct(SaveProductReqDto dto) throws ServiceException {
		long start = System.currentTimeMillis();
		// 判断是新增还是修改，后台提交数据里没带productId说明是新增产品，带了说明是修改产品
		boolean needCreate = StringUtils.isBlank(dto.getProductId());
		try {
			Product product = needCreate ? new Product() : read(dto.getProductId());

			String oldProductName = null;
			String newProductName = null;
			if (!needCreate) {
				oldProductName = product.getProductName();
			}
			newProductName = dto.getProductName();

			if (product == null) {
				logger.info("要修改的产品不存在，productId：" + dto.getProductId());
				return BaseResponse.error("要修改的产品不存在");
			}
			if (needCreate) {
				// 新增产品，校验产品名称
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("productName", Constants.ROP_EQ, dto.getProductName()));
				List<Product> list = queryAllBySearchFilter(filter);
				if (list != null && !list.isEmpty()) {
					logger.info("产品名称已存在：" + dto.getProductName());
					return BaseResponse.error("产品名称已存在：" + dto.getProductName());
				}
			} else {
				// 修改产品，如果改了产品名称，不能跟已有的产品重名
				if (!StringUtils.equals(product.getProductName(), dto.getProductName())) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("productName", Constants.ROP_EQ, dto.getProductName()));
					List<Product> list = queryAllBySearchFilter(filter);
					if (list != null && !list.isEmpty()) {
						logger.info("产品名称已存在：" + dto.getProductName());
						return BaseResponse.error("产品名称已存在：" + dto.getProductName());
					}
				}
			}
			if (needCreate) {
				product.setSupplierId(dto.getSupplierId());
			} else {
				product.setProductId(dto.getProductId());
			}
			// 套餐最低金额
			if (NumberUtils.isParsable(dto.getLowdissipation())) {
				product.setLowdissipation(Double.parseDouble(dto.getLowdissipation()));
			}
			// 套餐最低条数
			if (NumberUtils.isParsable(dto.getUnitvalue())) {
				product.setUnitvalue(Integer.parseInt(dto.getUnitvalue()));
			}
			// 产品标识（短信云的通道id）
			product.setProductMark(dto.getProductMark());
			product.setProductName(dto.getProductName());
			// 产品参数（短信云的通道参数）
			product.setProductParam(dto.getProductParam());
			// 语音单位时长
			if (NumberUtils.isParsable(dto.getVoiceUnit())) {
				product.setVoiceUnit(Integer.parseInt(dto.getVoiceUnit()));
			}
			// 结算币种
			product.setCurrencyType(dto.getCurrencyType());
			if (needCreate) {
				product.setOssUserId(dto.getOssUserId());
			}
			// 到达省份
			String reachProvince = dto.getReachProvince();
			int reachProvinces = 0;
			if (StringUtils.isNotBlank(reachProvince)) {
				String[] provinces = reachProvince.split(",");
				for (String j : provinces) {
					if (NumberUtils.isDigits(j)) {
						int i = Integer.parseInt(j);
						if (Integer.parseInt(j) != 2147483647) {
							reachProvinces |= (0x1 << (i - 1));
						} else {
							reachProvinces = 2147483647;
							break;
						}
					}
				}
			}
			product.setReachProvince(reachProvinces);
			// 落地省份
			product.setBaseProvince(dto.getBaseProvince());
			product.setProductType(dto.getProductType());
			product.setSettleType(dto.getSettleType());
			if (StringUtil.isNotBlank(dto.getDirectConnect())) {
				product.setDirectConnect(Boolean.parseBoolean(dto.getDirectConnect()));
			}
			boolean result = false;
			if (needCreate) {
				result = save(product);
			} else {
				result = update(product);
			}
			if (!result) {
				return BaseResponse.error("保存产品失败");
			}

			Supplier supplier = supplierDao.read(product.getSupplierId());
			if (supplier != null) {
				if (StringUtils.isBlank(oldProductName)) {
					supplier.setKeyWords(supplier.getKeyWords() + "," + newProductName);
				} else {
					String[] keyWords = supplier.getKeyWords().split(",");
					for (int i = 0; i < keyWords.length; i++) {
						if (StringUtils.equals(oldProductName, keyWords[i])) {
							keyWords[i] = newProductName;
						}
					}
					supplier.setKeyWords(String.join(",", keyWords));
				}
				supplierDao.save(supplier);
			}

		} catch (Exception e) {
			logger.error("保存产品失败", e);
			return BaseResponse.error("保存产品失败");
		}
		logger.info("保存产品信息成功，耗时:" + (System.currentTimeMillis() - start));
		return BaseResponse.success("保存产品成功");
	}

	/**
	 * 返回支持的省份
	 */
	@Override
	public String getReachProvinces(Product product) throws ServiceException {
		try {
			if (product.getReachProvince() == 2147483647)
				return "全国";
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ilevel", Constants.ROP_EQ, 2));
			List<Region> regions = regionService.queryAllBySearchFilter(filter);
			StringBuffer sb = new StringBuffer();
			for (Region region : regions) {
				int digit = region.getBitwise() - 1;// 获取省份所在位数
				if ((product.getReachProvince() & (1 << digit)) != 0) {
					if (!StringUtils.equals("未知", region.getRegionName())) {
						sb.append(region.getRegionName() + ",");
					}
				}
			}
			if (sb.length() > 0) {
				return sb.substring(0, sb.lastIndexOf(","));
			} else {
				return sb.toString();
			}
		} catch (Exception e) {
			throw new ServiceException("转换到达省份失败", e);
		}
	}

	/**
	 * 返回基地的省份
	 */
	@Override
	public String getBaseProvince(Product product) throws ServiceException {
		try {
			if (product == null) {
				return "";
			} else {
				Region region = regionService.read(product.getBaseProvince());
				return region == null ? "" : region.getRegionName();
			}
		} catch (Exception e) {
			throw new ServiceException("转换到达省份失败", e);
		}
	}


	@Override
	public Map<String, String> findProductName(List<String> ids) {
		Map<String, String> productNames = new HashMap<>();
		if (ids == null || ids.isEmpty()) {
			return productNames;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, ids));
		List<Product> productList = null;
		try {
			productList = queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询异常", e);
		}
		if (productList != null && !productList.isEmpty()) {
			productNames.putAll(productList.stream().collect(Collectors.toMap(Product::getProductId, Product::getProductName, (o, n) -> n)));
		}
		return productNames;
	}
}
