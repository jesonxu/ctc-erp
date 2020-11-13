package com.dahantc.erp.controller.customerProduct;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customerProduct.CustomerProductRespDto;
import com.dahantc.erp.dto.customerProduct.SaveCustomerProductDto;
import com.dahantc.erp.enums.PagePermission;
import com.dahantc.erp.enums.YysType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl.TimeQuantum;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping(value = "/customerProduct")
public class CustomerProductAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(CustomerProductAction.class);

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IModifyPriceService modifyPirceService;

	@Autowired
	private IProductTypeService productTypeService;

	/**
	 * 跳转添加页面
	 */
	@RequestMapping("/toAddProduct")
	public String toAddProduct(Model model) {
		try {
			model.addAttribute("customerId", request.getParameter("customerId"));
			String customerTypeId = request.getParameter("customerTypeId");
			if (StringUtils.isNotBlank(customerTypeId)) {
				CustomerType customerType = customerTypeService.read(customerTypeId);
				if (customerType != null) {
					int type = customerType.getCustomerTypeValue();
					if (type != 1) { // 非合同客户，账号非必填
						model.addAttribute("accountRequired", "false");
					}
				}
			}

			Parameter parameter = new Parameter();
			List<String> collect = new ArrayList<>();
			parameter = parameterService.readOneByProperty("paramkey", "product_items");
			Pattern p = Pattern.compile("[,]");
			String[] s = p.split(parameter.getDepict());
			collect = Arrays.stream(s).collect(Collectors.toList());
			model.addAttribute("product_items", collect);
			if (isSale()) {
				model.addAttribute("isSale", "T");
			}
			// 运营商类型选项
			List<YysType> yysTypes = YysType.getNormalYysType();
			model.addAttribute("yysTypes", yysTypes);
			return "/views/customerProduct/addProduct";
		} catch (ServiceException e) {
			logger.error("跳转添加产品页面异常：", e);
		}
		return "";
	}

	/**
	 * 跳转修改页面
	 */
	@RequestMapping("/toEditProduct")
	public String toEditProduct(Model model) {
		try {
			String productId = request.getParameter("productId");
			CustomerProduct customerProduct = customerProductService.read(productId);
			customerProduct.setAccount(customerProduct.getAccount().replace("|", "\n"));
			model.addAttribute("customerProduct", customerProduct);
			String customerTypeId = request.getParameter("customerTypeId");
			if (StringUtils.isNotBlank(customerTypeId)) {
				CustomerType customerType = customerTypeService.read(customerTypeId);
				if (customerType != null) {
					int type = customerType.getCustomerTypeValue();
					if (type != 1) { // 非合同客户，账号非必填
						model.addAttribute("accountRequired", "false");
					}
				}
			}

			// 是否能修改第一次账单时间
			boolean canEdit1stBillTime = false;
			Role role = roleService.read(getOnlineUserAndOnther().getRoleId());
			if (role.getPagePermissionMap() != null && role.getPagePermissionMap().containsKey(PagePermission.customerProductEdit1stTime.getDesc())) {
				canEdit1stBillTime = role.getPagePermissionMap().get(PagePermission.customerProductEdit1stTime.getDesc());
			}
			model.addAttribute("canEdit1stBillTime", canEdit1stBillTime);
			model.addAttribute("firstGenerateBillTime",
					customerProduct.getFirstGenerateBillTime() == null ? "" : DateUtil.convert(customerProduct.getFirstGenerateBillTime(), DateUtil.format1));

			Parameter parameter = new Parameter();
			List<String> collect = new ArrayList<>();
			parameter = parameterService.readOneByProperty("paramkey", "product_items");
			Pattern p = Pattern.compile("[,]");
			String[] s = p.split(parameter.getDepict());
			collect = Arrays.stream(s).collect(Collectors.toList());
			model.addAttribute("product_items", collect);
			if (isSale()) {
				model.addAttribute("isSale", "T");
			}
			// 产品的运营商类型
			model.addAttribute("yysType", customerProductService.getProductYysType(customerProduct, true));
			// 运营商类型选项
			List<YysType> yysTypes = YysType.getNormalYysType();
			model.addAttribute("yysTypes", yysTypes);
			return "/views/customerProduct/editProduct";
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "";
	}

	// 获取客户账号 充值详情标签
	@RequestMapping("/queryAccounts")
	@ResponseBody
	public List<String> queryAccounts(@RequestParam(required = false) String productId, @RequestParam(required = false) String customerId) {
		List<String> accountList = new ArrayList<>();
		try {
			if (!StringUtils.isAllBlank(productId, customerId)) {
				if (StringUtils.isBlank(customerId)) {
					CustomerProduct customerProduct = customerProductService.read(productId);
					customerId = customerProduct.getCustomerId();
				}
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
				if (!CollectionUtils.isEmpty(productList)) {
					for (CustomerProduct product : productList) {
						String account = product.getAccount();
						if (StringUtils.isNotBlank(account)) {
							for (String accountStr : Arrays.asList(account.split("\\|"))) {
								if (!accountList.contains(accountStr)) {
									accountList.add(accountStr);
								}
							}
						}
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return accountList;
	}

	/**
	 * 获取全部产品信息
	 */
	@RequestMapping("/queryProducts")
	public String queryProducts(Model model) {
		long startTime = System.currentTimeMillis();
		List<CustomerProductRespDto> dtos = null;
		Parameter parameter = new Parameter();
		List<String> collect = new ArrayList<>();
		boolean onlyShowBasic = false;
		try {
			String customerId = request.getParameter("customerId");
			if (StringUtils.isNotBlank(customerId)) {
				Customer customer = customerService.read(customerId);
				if (customer != null) {
					onlyShowBasic = isSale() && customerTypeService.validatePublicType(customer.getCustomerTypeId());
				}
				List<FlowEntDealCount> countList = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				Map<String, IntSummaryStatistics> countMap = null;
				if (countList != null && !countList.isEmpty()) {
					countMap = countList.stream().filter(c -> StringUtils.equals(c.getSupplierId(), customerId) && StringUtils.isNotBlank(c.getProductId()))
							.collect(Collectors.groupingBy(FlowEntDealCount::getProductId, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
				}
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
				List<CustomerProduct> products = customerProductService.queryAllByFilter(filter);
				dtos = buildRespDto(products, countMap);
				logger.info("查询全部产品结束，耗时：" + (System.currentTimeMillis() - startTime));
			}
			if (!onlyShowBasic) {
				String roleId = getOnlineUserAndOnther().getRoleId();
				model.addAttribute("pagePermission", roleService.getPagePermission(roleId));
				model.addAttribute("dtos", dtos);
				parameter = parameterService.readOneByProperty("paramkey", "product_items");
				Pattern p = Pattern.compile("[,]");
				String[] s = p.split(parameter.getDepict());
				collect = Arrays.stream(s).collect(Collectors.toList());
				model.addAttribute("product_items", collect);
			} else {
				model.addAttribute("pagePermission", new HashMap<>());
				model.addAttribute("dtos", new ArrayList<>());
				model.addAttribute("product_items", new ArrayList<>());
			}
			model.addAttribute("onlyShowBasic", onlyShowBasic);
			return "/views/customerProduct/customerProductTemplate";
		} catch (ServiceException e) {
			logger.error("查询全部产品信息异常：", e);
		}
		return "";
	}

	private List<CustomerProductRespDto> buildRespDto(List<CustomerProduct> products, Map<String, IntSummaryStatistics> countMap) {
		List<CustomerProductRespDto> dtos = new ArrayList<>();
		try {
			if (!ListUtils.isEmpty(products)) {
				for (CustomerProduct product : products) {
					Map<TimeQuantum, BigDecimal> priceInfo = modifyPirceService.findCurrentProductPriceInfo(product.getProductId(),
							DateUtil.getCurrentStartDateTime(), DateUtil.getCurrentEndDateTime(), product.getProductType());
					CustomerProductRespDto dto = new CustomerProductRespDto();
					if (!CollectionUtils.isEmpty(priceInfo)) {
						Entry<TimeQuantum, BigDecimal> entry = priceInfo.entrySet().iterator().next();
						dto.setPrice(entry.getValue().toString());
						dto.setPriceTimeQuantum(DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1) + "~"
								+ DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1));
					}
					BeanUtils.copyProperties(product, dto);
					dto.setProductType((short) product.getProductType());
					String productTypeName = productTypeService.getProductTypeNameByValue(product.getProductType());
					dto.setProductTypeName(productTypeName);
					dto.setProductName(StringEscapeUtils.escapeHtml4(product.getProductName()));
					dto.setAccount(product.getAccount().replace("|", "<br>"));
					dto.setwTime(DateUtil.convert(new Date(product.getWtime().getTime()), DateUtil.format2));
					dto.setYysTypeName(YysType.getNormalYysTypeName(dto.getProductType(), dto.getYysType()));
					long flowCount = 0l;
					if (countMap != null) {
						IntSummaryStatistics statistics = countMap.get(product.getProductId());
						if (statistics != null) {
							flowCount = statistics.getSum();
						} else {
							dto.setFlowEntCount(0l);
						}
					}
					dto.setBillPeriod(product.getBillPeriod());
					dto.setFlowEntCount(flowCount);
					dtos.add(dto);
				}
			}
		} catch (Exception e) {
			logger.error("获取产品信息异常", e);
		}
		return dtos;
	}

	/**
	 * 添加/修改产品
	 */
	@PostMapping("/save")
	@ResponseBody
	public BaseResponse<String> saveProduct() throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			SaveCustomerProductDto dto = new SaveCustomerProductDto();
			String customerId = request.getParameter("customerId");
			if (StringUtil.isBlank(customerId)) {
				logger.info("客户id不能为空");
				return BaseResponse.error("客户id不能为空");
			}
			String productId = request.getParameter("productId");
			String productName = StringEscapeUtils.unescapeHtml4(request.getParameter("productName"));
			String productType = request.getParameter("productType");
			String directConnect = request.getParameter("directConnect");
			String account = request.getParameter("account");
			List<String> accountList = Arrays.asList(account.replaceAll("[\n,，；;|、]", "|").split("\\|"));
			Set<String> accountSet = accountList.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
			account = String.join("|", accountSet);
			String billType = request.getParameter("billType");
			String billCycle = request.getParameter("billCycle");
			String settleType = request.getParameter("settleType");
			String billTaskDay = request.getParameter("billTaskDay");
			String firstGenerateBillTime = request.getParameter("firstGenerateBillTime");
			String yysType = request.getParameter("yysType");
			if (StringUtils.isNotBlank(firstGenerateBillTime)) {
				dto.setFirstGenerateBillTime(new Timestamp(DateUtil.convert(firstGenerateBillTime, DateUtil.format1).getTime()));
			}
			String voiceUnit = request.getParameter("voiceUnit");
			if (StringUtil.isNotBlank(voiceUnit)) {
				dto.setVoiceUnit(Integer.parseInt(voiceUnit));
			}
			String sendDemo = request.getParameter("sendDemo");
			dto.setCustomerId(customerId);
			dto.setProductId(productId);
			dto.setProductName(productName);
			if (StringUtil.isNotBlank(productType)) {
				dto.setProductType(Short.parseShort(productType));
			}
			if (StringUtil.isNotBlank(directConnect)) {
				dto.setDirectConnect(Boolean.parseBoolean(directConnect));
			}
			dto.setAccount(account);
			if (StringUtil.isNotBlank(billType)) {
				dto.setBillType(Integer.parseInt(billType));
			}
			if (StringUtil.isNotBlank(billCycle)) {
				dto.setBillCycle(Integer.parseInt(billCycle));
			}
			if (StringUtil.isNotBlank(settleType)) {
				dto.setSettleType(Integer.parseInt(settleType));
			}
			if (StringUtil.isNotBlank(billTaskDay)) {
				dto.setBillTaskDay(Integer.parseInt(billTaskDay));
			}
			dto.setOssUserId(onlineUser.getOssUserId());
			dto.setSendDemo(sendDemo);
			if (StringUtil.isNotBlank(yysType)) {
				dto.setYysType(yysType);
			}
			return customerProductService.saveProduct(dto);
		} catch (Exception e) {
			logger.error("保存产品异常：", e);
			return BaseResponse.error("保存产品失败");
		}
	}

	@ResponseBody
	@RequestMapping("/getProductSelect")
	public String getProductSelect(@RequestParam String customerId) {
		logger.info("获取客户的所有产品开始，customerId：" + customerId);
		JSONArray result = new JSONArray();
		try {
			Customer customer = customerService.read(customerId);
			if (null == customer) {
				logger.info("按客户id找不到对应的客户：" + customerId);
				return result.toJSONString();

			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
			List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
			if (CollectionUtils.isEmpty(productList)) {
				logger.info("该客户下没有产品，customerId：" + customerId);
				return result.toJSONString();
			}
			for (CustomerProduct product : productList) {
				JSONObject item = new JSONObject();
				item.put("name", product.getProductName());
				item.put("value", product.getProductId());
				item.put("selected", "");
				item.put("disabled", "");
				result.add(item);
			}
			logger.info("按客户获取到产品数：" + productList.size() + "，customerId：" + customerId);
		} catch (ServiceException e) {
			logger.error("获取客户的所有产品异常，customerId：" + customerId, e);
		}
		return result.toJSONString();
	}

	/**
	 * 获取客户产品信息
	 *
	 * @param customerId
	 *            客户id
	 * @return 产品信息 {productId:"",productName:""}
	 */
	@ResponseBody
	@RequestMapping("/getCustomerProduct")
	public BaseResponse<List<JSONObject>> getCustomerProduct(String customerId) {
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
			List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
			if (CollectionUtils.isEmpty(productList)) {
				return BaseResponse.success("暂无产品");
			}
			List<JSONObject> customerProducts = productList.stream().map(product -> {
				JSONObject productInfo = new JSONObject();
				productInfo.put("productId", product.getProductId());
				productInfo.put("productName", product.getProductName());
				return productInfo;
			}).collect(Collectors.toList());
			return BaseResponse.success(customerProducts);
		} catch (ServiceException e) {
			logger.error("获取客户的所有产品异常，customerId：" + customerId, e);
		}
		return BaseResponse.error("数据查询异常");
	}
}
