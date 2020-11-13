package com.dahantc.erp.controller.productType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.productType.ProductTypeDto;
import com.dahantc.erp.dto.productType.ProductTypeReqDto;
import com.dahantc.erp.dto.productType.UpdateProductTypeDto;
import com.dahantc.erp.enums.CostPriceType;
import com.dahantc.erp.enums.VisibleStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.productType.entity.ProductType;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping(value = "/productType")
public class ProductTypeAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ProductTypeAction.class);

	@Autowired
	private IProductTypeService productTypeService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBaseDao baseDao;

	@RequestMapping("toProductTypeSheet")
	public String toProductTypeSheet() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		return "/views/productType/productTypeSheet";
	}

	/**
	 * 获取成本类型下拉框
	 * 
	 * @return
	 */
	@RequestMapping("getCostPriceTypeSelect")
	@ResponseBody
	public JSONArray getCostPriceTypeSelect() {
		JSONArray result = new JSONArray();
		CostPriceType[] costPriceTypes = CostPriceType.values();
		for (CostPriceType costPriceType : costPriceTypes) {
			JSONObject type = new JSONObject();
			type.put("value", costPriceType.ordinal());
			type.put("name", costPriceType.getDesc());
			result.add(type);
		}
		return result;
	}

	/**
	 * 分页查询产品类型
	 *
	 * @return 分页
	 */
	@RequestMapping(value = "/readPages")
	@ResponseBody
	public BaseResponse<PageResult<ProductTypeDto>> readPages(@Valid ProductTypeReqDto reqDto) {
		PageResult<ProductTypeDto> result = new PageResult<>();
		logger.info("查询产品类型开始");
		try {
			long _start = System.currentTimeMillis();
			int page = 0;
			int limit = 0;
			if (StringUtil.isNotBlank(reqDto.getPage())) {
				page = Integer.parseInt(reqDto.getPage());
			}
			if (StringUtil.isNotBlank(reqDto.getLimit())) {
				limit = Integer.parseInt(reqDto.getLimit());
			}
			SearchFilter filter = new SearchFilter();
			if (StringUtil.isNotBlank(reqDto.getProductTypeName())) {
				filter.getRules().add(new SearchRule("productTypeName", Constants.ROP_CN, reqDto.getProductTypeName()));
			}
			if (StringUtil.isNotBlank(reqDto.getDate()) && StringUtil.isNotBlank(reqDto.getEndDate())) {
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert(reqDto.getDate(), DateUtil.format2)));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.convert(reqDto.getEndDate(), DateUtil.format2)));
				filter.getRules().add(new SearchRule("productTypeName", Constants.ROP_CN, reqDto.getProductTypeName()));
			}
			String visible = reqDto.getVisible();
			if (StringUtils.isNotBlank(visible)) {
				String[] states = visible.split(",");
				if (states.length == 1) {
					filter.getRules().add(new SearchRule("visible", Constants.ROP_EQ, Integer.parseInt(visible)));
				}
			}
			filter.getOrders().add(new SearchOrder("wtime", "desc"));
			PageResult<ProductType> preResult = productTypeService.queryByPages(limit, page, filter);
			result = new PageResult<>(buildDto(preResult.getData()), preResult.getCurrentPage(), preResult.getTotalPages(), preResult.getCount());
			logger.info("查询产品类型结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询产品类型异常", e);
			result.setData(new ArrayList<>());
		}
		return BaseResponse.success(result);
	}

	private List<ProductTypeDto> buildDto(List<ProductType> productTypeList) {
		List<ProductTypeDto> dtoList = new ArrayList<>();
		if (CollectionUtils.isEmpty(productTypeList)) {
			return dtoList;
		}
		Map<String, String> userNameMap = new HashMap<>();
		List<String> userIdList = productTypeList.stream().map(ProductType::getOssUserId).collect(Collectors.toList());
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
		try {
			List<User> userList = userService.queryAllBySearchFilter(filter);
			for (User user : userList) {
				userNameMap.put(user.getOssUserId(), user.getRealName());
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		for (ProductType productType : productTypeList) {
			ProductTypeDto dto = new ProductTypeDto();
			dto.setId(productType.getId());
			dto.setProductTypeName(productType.getProductTypeName());
			dto.setProductTypeKey(productType.getProductTypeKey());
			dto.setProductTypeValue(productType.getProductTypeValue());
			dto.setUserName(userNameMap.getOrDefault(productType.getOssUserId(), "未知"));
			dto.setCostPrice(productType.getCostPrice().setScale(6, BigDecimal.ROUND_HALF_UP).toPlainString());
			dto.setCostPriceType(CostPriceType.getCostPriceType(productType.getCostPriceType()));
			dto.setWtime(DateUtil.convert(productType.getWtime(), DateUtil.format1));
			dto.setVisible(productType.getVisible());
			dto.setRemark(productType.getRemark());
			dtoList.add(dto);
		}
		return dtoList;
	}

	@RequestMapping(value = "/toUpdateProductType")
	public String toUpdateProductType(@RequestParam String type, @RequestParam(required = false) String id) {
		if (StringUtil.isNotBlank(id)) {
			try {
				ProductType productType = productTypeService.read(id);
				request.setAttribute("productType", productType);
			} catch (ServiceException e) {
				logger.error("", e);
			}
		}
		request.setAttribute("type", type);
		return "/views/productType/updateProductType";
	}

	@RequestMapping(value = "/addProductType")
	@ResponseBody
	public BaseResponse<String> addProductType(@Valid UpdateProductTypeDto dto) {
		SearchFilter filter = new SearchFilter();
		// 类型名或类型值相同
		filter.getOrRules()
				.add(new SearchRule[] {
						new SearchRule("productTypeName", Constants.ROP_EQ, dto.getProductTypeName()),
						new SearchRule("productTypeKey", Constants.ROP_EQ, dto.getProductTypeKey()),
						new SearchRule("productTypeValue", Constants.ROP_EQ, Integer.parseInt(dto.getProductTypeValue())) });
		try {
			List<ProductType> repeats = productTypeService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(repeats)) {
				ProductType productType = new ProductType();
				productType.setProductTypeName(dto.getProductTypeName());
				productType.setProductTypeKey(dto.getProductTypeKey());
				productType.setProductTypeValue(Integer.parseInt(dto.getProductTypeValue()));
				productType.setCostPriceType(Integer.parseInt(dto.getCostPriceType()));
				if (StringUtil.isNotBlank(dto.getCostPrice())) {
					productType.setCostPrice(new BigDecimal(dto.getCostPrice()));
				}
				productType.setOssUserId(getOnlineUser().getOssUserId());
				productType.setRemark(dto.getRemark());
				boolean result = productTypeService.save(productType);
				return BaseResponse.success("添加产品类型" + (result ? "成功" : "失败"));
			} else {
				String msg = "";
				List<String> nameList = repeats.stream().map(ProductType::getProductTypeName).collect(Collectors.toList());
				if (nameList.contains(dto.getProductTypeName())) {
					msg += "类型名";
				}
				List<String> keyList = repeats.stream().map(type -> type.getProductTypeKey() + "").collect(Collectors.toList());
				if (keyList.contains(dto.getProductTypeKey())) {
					msg += msg.length() > 0 ? "、类型标识" : "类型标识";
				}
				List<String> valueList = repeats.stream().map(type -> type.getProductTypeValue() + "").collect(Collectors.toList());
				if (valueList.contains(dto.getProductTypeValue())) {
					msg += msg.length() > 0 ? "、类型值" : "类型值";
				}
				msg += "重复";
				return BaseResponse.error(msg);
			}
		} catch (ServiceException e) {
			logger.error("添加产品类型异常", e);
			return BaseResponse.error("添加产品类型异常");
		}
	}

	@RequestMapping(value = "/editProductType")
	@ResponseBody
	public BaseResponse<String> editProductType(@Valid UpdateProductTypeDto dto) {
		SearchFilter filter = new SearchFilter();
		// 类型名或类型值相同
		filter.getRules().add(new SearchRule("id", Constants.ROP_NE, dto.getId()));
		filter.getOrRules()
				.add(new SearchRule[] {
						new SearchRule("productTypeName", Constants.ROP_EQ, dto.getProductTypeName()),
						new SearchRule("productTypeKey", Constants.ROP_EQ, dto.getProductTypeKey()),
						new SearchRule("productTypeValue", Constants.ROP_EQ, Integer.parseInt(dto.getProductTypeValue())) });
		try {
			List<ProductType> repeats = productTypeService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(repeats)) {
				ProductType productType = productTypeService.read(dto.getId());
				productType.setProductTypeName(dto.getProductTypeName());
				productType.setProductTypeKey(dto.getProductTypeKey());
				productType.setProductTypeValue(Integer.parseInt(dto.getProductTypeValue()));
				productType.setCostPriceType(Integer.parseInt(dto.getCostPriceType()));
				if (Integer.parseInt(dto.getCostPriceType()) == CostPriceType.MANUAL.ordinal()) {
					productType.setCostPrice(new BigDecimal(dto.getCostPrice()));
				} else {
					productType.setCostPrice(new BigDecimal(0));
				}
				productType.setRemark(dto.getRemark());
				boolean result = productTypeService.update(productType);
				return BaseResponse.success("修改产品类型" + (result ? "成功" : "失败"));
			} else {
				String msg = "";
				List<String> nameList = repeats.stream().map(ProductType::getProductTypeName).collect(Collectors.toList());
				if (nameList.contains(dto.getProductTypeName())) {
					msg += "类型名";
				}
				List<String> keyList = repeats.stream().map(type -> type.getProductTypeKey() + "").collect(Collectors.toList());
				if (keyList.contains(dto.getProductTypeKey())) {
					msg += msg.length() > 0 ? "、类型标识" : "类型标识";
				}
				List<String> valueList = repeats.stream().map(type -> type.getProductTypeValue() + "").collect(Collectors.toList());
				if (valueList.contains(dto.getProductTypeValue())) {
					msg += msg.length() > 0 ? "、类型值" : "类型值";
				}
				msg += "重复";
				return BaseResponse.error(msg);
			}
		} catch (ServiceException e) {
			logger.error("修改产品类型异常", e);
			return BaseResponse.error("修改产品类型异常");
		}
	}

	/**
	 * 删除产品类型之前检查是否正在使用
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/deleteProductType")
	@ResponseBody
	public BaseResponse<String> deleteProductType(@RequestParam String id) {
		if (StringUtil.isBlank(id)) {
			return BaseResponse.error("产品类型id不能为空");
		}
		try {
			ProductType productType = productTypeService.read(id);
			if (null == productType) {
				logger.info("产品类型不存在，id：" + id);
				return BaseResponse.success("产品类型不存在");
			}
			// 此产品类型是否被使用
			boolean using = false;
			StringBuffer usingProductName = new StringBuffer();
			Set<String> productNameSet = new HashSet<>();

			String cpSql = "select concat(ec.companyname, '-', ecp.productname) from erp_customer_product ecp "
					+ " left join erp_customer ec on ecp.customerid = ec.customerid where ecp.producttype = " + productType.getProductTypeValue()
					+ " and ec.companyname is not null limit 3";

			String spSql = "select concat(es.companyname, '-', esp.productname) from erp_product esp "
					+ " left join erp_supplier es on esp.supplierid = es.supplierid where esp.producttype = " + productType.getProductTypeValue()
					+ " and es.companyname is not null limit 3";
			try {
				List<Object> cpResult = (List<Object>) baseDao.selectSQL(cpSql);
				List<Object> spResult = (List<Object>) baseDao.selectSQL(spSql);
				if (!CollectionUtils.isEmpty(cpResult)) {
					for (Object cp : cpResult) {
						productNameSet.add((String) cp);
					}
				}
				if (!CollectionUtils.isEmpty(spResult)) {
					for (Object sp : spResult) {
						productNameSet.add((String) sp);
					}
				}
			} catch (BaseException e) {
				logger.error("查询使用产品类型的产品异常", e);
			}
			if (productNameSet.size() > 0) {
				using = true;
				for (String p : productNameSet) {
					usingProductName.append("<br>").append(p);
				}
				usingProductName.append("……");
			}
			if (using) {
				return BaseResponse.success("产品类型正在使用，不能删除：" + usingProductName.toString());
			}
			boolean result = productTypeService.delete(id);
			return BaseResponse.success("删除产品类型" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
			return BaseResponse.error("删除产品类型异常");
		}
	}

	/**
	 * 隐藏/展示产品类型
	 * 
	 * @param id
	 *            产品类型id
	 * @param visible
	 *            展示状态：0展示，1隐藏
	 * @return
	 */
	@RequestMapping(value = "/toggleVisible")
	@ResponseBody
	public BaseResponse<String> toggleVisible(@RequestParam String id, @RequestParam String visible) {
		try {
			ProductType productType = productTypeService.read(id);
			if (null != productType) {
				Optional<VisibleStatus> optional = VisibleStatus.getEnumsByCode(Integer.parseInt(visible));
				if (optional.isPresent()) {
					productType.setVisible(optional.get().ordinal());
					boolean result = productTypeService.update(productType);
					return BaseResponse.success(result ? "成功" : "失败");
				}
				return BaseResponse.error("可见状态不正确");
			}
			return BaseResponse.error("类型不存在");
		} catch (ServiceException e) {
			logger.error("修改可见状态异常", e);
			return BaseResponse.error("修改可见状态异常");
		}
	}

	/**
	 * 获取产品类型下拉框
	 * 
	 * @return
	 */
	@RequestMapping("getProductTypeSelect")
	@ResponseBody
	public JSONArray getProductTypeSelect() {
		JSONArray result = new JSONArray();
		List<ProductType> productTypeList = null;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("visible", Constants.ROP_EQ, VisibleStatus.SHOW.ordinal()));
			productTypeList = productTypeService.queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		if (!CollectionUtils.isEmpty(productTypeList)) {
			for (ProductType productType : productTypeList) {
				JSONObject type = new JSONObject();
				type.put("value", productType.getProductTypeValue());
				type.put("name", productType.getProductTypeName());
				result.add(type);
			}
		}
		return result;
	}

	@RequestMapping("getProductTypeValueByKey")
	@ResponseBody
	public String getProductTypeValueByKey(@RequestParam String key) {
		return "" + productTypeService.getProductTypeValueByKey(key);
	}
}
