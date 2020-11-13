package com.dahantc.erp.controller.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.NumberUtils;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.product.ProductRespDto;
import com.dahantc.erp.dto.product.SaveProductReqDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl.TimeQuantum;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.region.entity.Region;
import com.dahantc.erp.vo.region.service.IRegionService;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;

/**
 * 
 * @Description: 产品控制
 * 
 */
@Controller
@RequestMapping(value = "/product")
public class ProductAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(ProductAction.class);

	@Autowired
	private IProductService productService;

	@Autowired
	private IRegionService regionService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IModifyPriceService modifyPirceService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IProductTypeService productTypeService;

	/**
	 * 跳转添加页面
	 */
	@RequestMapping("/toAddProduct")
	public String toAddProduct(Model model) {
		model.addAttribute("supplierId", request.getParameter("supplierId"));
		model.addAttribute("regions", loadRegion());
		return "/views/product/addProduct";
	}

	/**
	 * 跳转修改页面
	 */
	@RequestMapping("/toEditProduct")
	public String toEditProduct(Model model) {
		try {
			String productId = request.getParameter("productId");
			Product product = productService.read(productId);
			model.addAttribute("product", product);
			model.addAttribute("regions", loadRegion());
			model.addAttribute("reachProvince", productService.getReachProvinces(product));
			model.addAttribute("baseProvince", productService.getBaseProvince(product));
			return "/views/product/editProduct";
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "";
	}

	/**
	 * 遍历协议类型
	 * 
	 * @return
	 */
	@RequestMapping("/loadProductParamType")
	@ResponseBody
	public List<Parameter> loadProductParamType() {
		logger.info("读取协议类型开始");
		long startTime = System.currentTimeMillis();
		List<Parameter> params = new ArrayList<Parameter>();
		try {
			String paramType = request.getParameter("paramType");
			if (StringUtils.isNotEmpty(paramType) && NumberUtils.isNumeric(paramType)) {
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, Integer.valueOf(paramType)));
				params = parameterService.findAllByCriteria(searchFilter);
			}
			logger.info("读取协议类型结束，耗时：" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			logger.error("读取协议类型异常", e);
		}
		return params;
	}

	/**
	 * 添加/修改产品
	 */
	@PostMapping("/save")
	@ResponseBody
	public BaseResponse<String> save(@Valid SaveProductReqDto dto) throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			dto.setProductName(StringEscapeUtils.unescapeHtml4(dto.getProductName()));
			dto.setProductParam(convertToXml(dto.getProductParam()));
			dto.setOssUserId(onlineUser.getOssUserId());
			return productService.saveProduct(dto);
		} catch (Exception e) {
			logger.error("产品保存异常：", e);
			return BaseResponse.error("保存失败");
		}
	}

	private String convertToXml(String srcString) {
		StringBuffer xmlString = new StringBuffer();
		xmlString.append("<?xml version='1.0' encoding='UTF-8'?>\n<es>\n");
		String[] lines = srcString.split("\n");
		for (String line : lines) {
			if (line.trim().length() > 0) {
				String[] kv = line.split(":");
				xmlString.append("<e k='" + kv[0] + "'>" + kv[1] + "</e>\n");
			}
		}
		xmlString.append("</es>");
		return xmlString.toString();

	}

	/**
	 * 获取全部产品信息
	 */
	@PostMapping("/queryProducts")
	public String queryProducts(Model model) {
		long startTime = System.currentTimeMillis();
		List<ProductRespDto> dtos = null;
		try {
			String supplierId = request.getParameter("supplierId");
			if (StringUtils.isNotBlank(supplierId)) {
				List<FlowEntDealCount> countList = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				Map<String, IntSummaryStatistics> countMap = null;
				if (countList != null && !countList.isEmpty()) {
					countMap = countList.stream().filter(c -> StringUtils.equals(c.getSupplierId(), supplierId) && StringUtils.isNotBlank(c.getProductId()))
							.collect(Collectors.groupingBy(FlowEntDealCount::getProductId, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
				}
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
				List<Product> products = productService.queryAllBySearchFilter(filter);
				dtos = buildRespDto(products, countMap);
				logger.info("查询全部产品信息耗时：[" + (System.currentTimeMillis() - startTime) + "]毫秒");
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			model.addAttribute("pagePermission", roleService.getPagePermission(roleId));
			model.addAttribute("dtos", dtos);
		} catch (ServiceException e) {
			logger.error("查询全部产品信息异常：", e);
		}
		return "/views/product/productCollapseTemplate";
	}

	/**
	 * 获取所有归属地
	 * 
	 * @return
	 */
	@RequestMapping("/loadRegion")
	@ResponseBody
	public List<Map<String, Object>> loadRegion() {
		logger.info("加载省份归属地开始");
		long startTime = System.currentTimeMillis();
		List<Map<String, Object>> regionList = new ArrayList<Map<String, Object>>();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ilevel", Constants.ROP_EQ, 2));
			List<Region> regions = regionService.queryAllBySearchFilter(filter);
			Comparator<Region> regionComparator = new Comparator<Region>() {
				@Override
				public int compare(Region region1, Region region2) {
					String region1PinYin = region1.getPinyin();
					String region2PinYin = region2.getPinyin();
					if (StringUtils.isBlank(region1PinYin) && StringUtils.isBlank(region2PinYin)) {
						return 0;
					} else if (StringUtils.isBlank(region1PinYin)) {
						return -1;
					} else if (StringUtils.isBlank(region2PinYin)) {
						return 1;
					}
					return region1PinYin.compareTo(region2PinYin);
				}
			};
			Collections.sort(regions, regionComparator);
			for (Region region : regions) {
				if (StringUtils.equals("未知", region.getRegionName())) {
					continue;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bitwise", region.getBitwise());
				map.put("regionName", region.getRegionName());
				map.put("id", region.getId());
				regionList.add(map);
			}
			logger.info("加载省份归属地结束,耗时：[" + (System.currentTimeMillis() - startTime) + "]毫秒");
		} catch (Exception e) {
			logger.error("加载省份归属地异常", e);
		}
		return regionList;
	}

	private List<ProductRespDto> buildRespDto(List<Product> products, Map<String, IntSummaryStatistics> countMap) {
		List<ProductRespDto> dtos = new ArrayList<>();
		try {
			for (Product product : products) {
				Map<TimeQuantum, BigDecimal> priceInfo = modifyPirceService.findCurrentProductPriceInfo(product.getProductId(),
						DateUtil.getCurrentStartDateTime(), DateUtil.getCurrentEndDateTime(), product.getProductType());
				ProductRespDto dto = new ProductRespDto();
				if (!CollectionUtils.isEmpty(priceInfo)) {
					Entry<TimeQuantum, BigDecimal> entry = priceInfo.entrySet().iterator().next();
					dto.setPrice(entry.getValue().toString());
					dto.setPriceTimeQuantum(DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1) + "~"
							+ DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1));
				}
				dto.setBaseProvince(productService.getBaseProvince(product));
				BeanUtils.copyProperties(product, dto);
				dto.setProductName(StringEscapeUtils.escapeHtml4(product.getProductName()));
				dto.setReachProvince(productService.getReachProvinces(product));
				String productTypeName = productTypeService.getProductTypeNameByValue(product.getProductType());
				dto.setProductTypeName(productTypeName);
				long flowCount = 0l;
				if (countMap != null) {
					IntSummaryStatistics statistics = countMap.get(product.getProductId());
					if (statistics != null) {
						flowCount = statistics.getSum();
					} else {
						dto.setFlowEntCount(0l);
					}
				}
				dto.setFlowEntCount(flowCount);
				dtos.add(dto);
			}
			dtos.sort((ProductRespDto dto1, ProductRespDto dto2) -> dto2.getFlowEntCount().compareTo(dto1.getFlowEntCount()));
		} catch (Exception e) {
			logger.error("获取产品信息异常", e);
		}
		return dtos;
	}

	/**
	 * 获取直连通道下拉框
	 */
	@RequestMapping("/getDirectChannel")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public String getDirectChannel(@RequestParam(required = false) String productId) {
		long _start = System.currentTimeMillis();
		JSONArray channelList = new JSONArray();
		try {
			// 查没有被客户产品关联的直连通道
			String sql = "select productmark, productname from erp_product where directconnect = 1 and productmark not in ("
					+ "select account from erp_customer_product cp where cp.directconnect = 1"
					+ (StringUtil.isBlank(productId) ? "" : " and cp.productId <> '" + productId + "'") + ")";
			List<Object[]> dataList = (List<Object[]>) baseDao.selectSQL(sql);
			if (!CollectionUtils.isEmpty(dataList)) {
				for (Object[] data : dataList) {
					JSONObject json = new JSONObject();
					json.put("value", String.valueOf(data[0]));
					json.put("name", String.valueOf(data[1]));
					channelList.add(json);
				}
			}
			logger.info("获取直连通道下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("获取直连通道下拉项异常", e);
		}
		return channelList.toJSONString();
	}

	/**
	 * 根据供应商Id 获取对应的产品信息
	 */
	@ResponseBody
	@RequestMapping("/getSupplierProduct")
	public BaseResponse<List<JSONObject>> getSupplierProduct(String supplierId) {
		if (StringUtil.isBlank(supplierId)) {
			return BaseResponse.error("请求参数错误");
		}
		try {
			SearchFilter productFilter = new SearchFilter();
			productFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
			List<Product> productList = productService.queryAllBySearchFilter(productFilter);
			if (productList == null || productList.isEmpty()) {
				return BaseResponse.success("暂无产品");
			}
			List<JSONObject> productInfos = productList.stream().map(product -> {
				JSONObject productInfo = new JSONObject();
				productInfo.put("productId", product.getProductId());
				productInfo.put("productName", product.getProductName());
				return productInfo;
			}).collect(Collectors.toList());
			return BaseResponse.success(productInfos);
		} catch (Exception e) {
			logger.error("数据查询异常", e);
		}
		return BaseResponse.error("数据查询异常");
	}
}
