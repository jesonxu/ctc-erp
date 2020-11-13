package com.dahantc.erp.controller.bill;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.bill.BillReqDto;
import com.dahantc.erp.dto.bill.BillRespDto;
import com.dahantc.erp.dto.bill.UncheckedBillDto;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.task.AcquireAccountReportDetailHandler;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerOperate.service.ICustomerOperateService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/bill")
public class ProductBillsAction extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ProductBillsAction.class);

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ICustomerOperateService customerOperateService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private AcquireAccountReportDetailHandler acquireAccountReportDetailHandler;

	@Autowired
	private IProductTypeService productTypeService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	private static final String billFileDir = "billfiles";

	private static final String analysisFileDir = "analysisfiles";

	private static final String RESULT_SUCCESS = "成功";

	private static final String RESULT_FAILED = "失败";

	@RequestMapping("/toBillsModify")
	public String toBillsModify() {
		return "/views/billsModify/listBillsPage";
	}

	/**
	 * 分页查询系账单
	 *
	 * @return 账单分页
	 */
	@ResponseBody
	@RequestMapping(value = "/readBillsPages")
	public BaseResponse<PageResult<BillRespDto>> readBillsPages(@Valid BillReqDto reqDto) {
		logger.info("分页查询账单开始");
		long _start = System.currentTimeMillis();
		int page = 0;
		int limit = 0;
		PageResult<BillRespDto> pageResult = new PageResult<>(null, 0);
		if (StringUtil.isNotBlank(reqDto.getPage())) {
			page = Integer.parseInt(reqDto.getPage());
		}
		if (StringUtil.isNotBlank(reqDto.getLimit())) {
			limit = Integer.parseInt(reqDto.getLimit());
		}

		String selectSql = "select eb.id, eu.realName, ee.companyname, ep.productname, eb.wtime, eb.billstatus, eb.platformcount, eb.cost, eb.suppliercount, eb.receivables, eb.entitytype";
		StringBuilder countSql = new StringBuilder("select count(1) ");

		// 拼接查询条件sql (from …… where ……)
		String queryString = buildQueryString(reqDto);

		if (StringUtil.isBlank(queryString)) {
			logger.info("拼接的查询条件sql为空");
			return BaseResponse.error("查询条件为空");
		}
		countSql.append(queryString);

		int total = 0;
		try {
			logger.info("获取账单数量的sql：" + countSql.toString());
			List<Object> countResult = (List<Object>) baseDao.selectSQL(countSql.toString());
			if (!CollectionUtils.isEmpty(countResult)) {
				total = ((Number) countResult.get(0)).intValue();
				logger.info("查询到账单数：" + total);
			} else {
				logger.info("查询结果为空");
			}
		} catch (Exception e) {
			logger.error("查询账单数量异常", e);
		}
		if (total <= 0) {
			return BaseResponse.success(pageResult);
		}
		pageResult.setCount(total);
		pageResult.setTotalPages((total % limit == 0) ? (total / limit) : (total / limit + 1));
		if (page > pageResult.getTotalPages()) {
			// 请求页已经是最后一页，不用再查
			return BaseResponse.success(pageResult);
		}

		List<Object[]> list = null;
		List<BillRespDto> dtoList = new ArrayList<>();
		try {
			logger.info("分页查询账单的sql：" + selectSql + queryString);
			list = (List<Object[]>) baseDao.selectSQL(selectSql + queryString);
			if (CollectionUtils.isEmpty(list)) {
				logger.info("分页查询账单结果为空");
				return BaseResponse.success(pageResult);
			}
			// eb.id, eu.realName, ee.companyname, ep.productname, eb.wtime, eb.billstatus, eb.platformcount, eb.cost, eb.suppliercount, eb.receivables, eb.entitytype
			for (Object[] obj : list) {
				BillRespDto dto = new BillRespDto();
				dto.setBillId((String) obj[0]);
				dto.setRealName((String) obj[1]);
				dto.setCompanyName((String) obj[2]);
				dto.setProductName((String) obj[3]);
				dto.setBillMonth(DateUtil.convert((Timestamp) obj[4], DateUtil.format4));
				dto.setBillStatus(BillStatus.getBillStatus(((Number) obj[5]).intValue()));
				dto.setPlatformSuccessCount(((Number) obj[6]).toString());
				dto.setCost(((Number) obj[7]).toString());
				dto.setCheckedSuccessCount(((Number) obj[8]).toString());
				dto.setReceivables(((Number) obj[9]).toString());
				dto.setEntityTypeName(EntityType.getEntityType(((Number) obj[10]).intValue()));
				dtoList.add(dto);
			}
			pageResult.setData(dtoList);
			logger.info("分页查询账单结束，本页" + dtoList.size() + "条");
			return BaseResponse.success(pageResult);
		} catch (BaseException e) {
			logger.error("分页查询账单异常", e);
			return BaseResponse.error(pageResult);
		}
	}

	/**
	 * 拼接条件sql
	 * 
	 * @param reqDto
	 *            请求参数封装
	 * @return sql
	 */
	private String buildQueryString(BillReqDto reqDto) {
		// 主体类型（默认是客户）
		int entityTypeValue = StringUtil.isNotBlank(reqDto.getEntityType()) ? Integer.parseInt(reqDto.getEntityType()) : EntityType.CUSTOMER.getCode();

		// 拼接查询sql
		StringBuilder fromSql = new StringBuilder(" from erp_bill eb ");
		StringBuilder whereSql = new StringBuilder(" where eb.entityType=").append(entityTypeValue);
		String orderSql = " order by eu.realName, ee.companyname, ep.productname, eb.wtime, eb.billstatus";

		if (EntityType.CUSTOMER.ordinal() == entityTypeValue) {
			fromSql.append(" left join erp_customer ee on eb.entityid = ee.customerid ");
			fromSql.append(" left join erp_customer_product ep on eb.productid = ep.productid ");
		} else if (EntityType.SUPPLIER.ordinal() == entityTypeValue) {
			fromSql.append(" left join erp_supplier ee on eb.entityid = ee.supplierid ");
			fromSql.append(" left join erp_product ep on eb.productid = ep.productid ");
		} else if (EntityType.SUPPLIER_DS.ordinal() == entityTypeValue) {
			fromSql.append(" left join erp_supplier ee on eb.entityid = ee.supplierid ");
			fromSql.append(" left join erp_dianshang_product ep on eb.productid = ep.dsproductid ");
		} else {
			logger.info("错误的主体类型：" + reqDto.getEntityType());
			return null;
		}
		// 公司名称、产品名称
		if (StringUtil.isNotBlank(reqDto.getSearchCompanyName())) {
			whereSql.append(" and ee.companyname like '%").append(reqDto.getSearchCompanyName()).append("%'");
		}
		if (StringUtil.isNotBlank(reqDto.getSearchProductName())) {
			whereSql.append(" and ep.productname like '%").append(reqDto.getSearchProductName()).append("%'");
		}
		if (StringUtil.isNotBlank(reqDto.getSettleType()) && EntityType.SUPPLIER_DS.ordinal() != entityTypeValue) {
			if (reqDto.getSettleType().equals(SettleType.Prepurchase.ordinal() + "")) {
				// 结算方式：预购/预付
				whereSql.append(" and ep.settletype in (0, 1)");
			} else {
				// 结算方式：后付
				whereSql.append(" and ep.settletype = 2");
			}
		}
		// 销售/商务
		fromSql.append(" left join erp_user eu on ee.ossuserid = eu.ossUserId ");
		if (StringUtil.isNotBlank(reqDto.getRealName())) {
			whereSql.append(" and eu.realName like '%").append(reqDto.getRealName()).append("%'");
		}
		// 账单月份
		if (StringUtil.isNotBlank(reqDto.getBillMonth())) {
			whereSql.append(" and eb.wtime ='").append(reqDto.getBillMonth()).append("-01'");
		} else {
			whereSql.append(" and eb.wtime ='").append(DateUtil.convert(new Date(), DateUtil.format4)).append("-01'");
		}
		// 账单状态（默认查所有状态）
		if (StringUtil.isNotBlank(reqDto.getBillStatus())) {
			whereSql.append(" and billStatus=").append(reqDto.getBillStatus());
		}

		return fromSql.toString() + whereSql.toString() + orderSql;
	}

	/**
	 * 获取账单状态下拉框
	 *
	 * @return
	 */
	@RequestMapping("getBillStatusSelect")
	@ResponseBody
	public JSONArray getBillStatusSelect() {
		JSONArray result = new JSONArray();
		BillStatus[] billStatuses = BillStatus.values();
		for (BillStatus billStatus : billStatuses) {
			JSONObject type = new JSONObject();
			type.put("value", billStatus.ordinal());
			type.put("name", billStatus.getDesc());
			result.add(type);
		}
		return result;
	}

	/**
	 * 获取主体类型下拉框
	 *
	 * @return
	 */
	@RequestMapping("getEntityTypeSelect")
	@ResponseBody
	public JSONArray getEntityTypeSelect() {
		JSONArray result = new JSONArray();
		EntityType[] entityTypes = EntityType.values();
		for (EntityType entityType : entityTypes) {
			JSONObject type = new JSONObject();
			type.put("value", entityType.getCode());
			type.put("name", entityType.getMsg());
			result.add(type);
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/queryBillData")
	public JSONObject queryBillData(@RequestParam String id, @RequestParam String update) {
		logger.info("重新统计账单数据");
		JSONObject result = new JSONObject();
		long successCount = 0L;
		// 成本
		BigDecimal totalCost = new BigDecimal(0);
		// 平均销售单价
		BigDecimal custPrice = new BigDecimal(0);
		try {
			ProductBills bill = productBillsService.read(id);
			if (bill.getEntityType() == EntityType.CUSTOMER.ordinal()) {
				Date startDate = new Date(bill.getWtime().getTime());
				Date endDate = DateUtil.getNextMonthFirst(startDate);
				CustomerProduct product = customerProductService.read(bill.getProductId());
				String account = product.getAccount();
				// 产品类型
				int productType = product.getProductType();
				// 查产品的所有账号的总成功数，月初<= <下月初
				if (productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS) == productType) {
					Map<String, Long> countryCountMap = customerOperateService.getPlatformSuccessCount4Inter(productType, account, startDate, endDate);
					successCount = countryCountMap.values().stream().mapToLong(count -> count).sum();
				} else {
					successCount = customerOperateService.getPlatformSuccessCount(productType, account, startDate, endDate);
				}
				List<String> loginNameList = Arrays.asList(account.split("\\|"));
				loginNameList = loginNameList.stream().map(String::trim).filter(StringUtil::isNotBlank).collect(Collectors.toList());
				if (ListUtils.isEmpty(loginNameList)) {
					logger.info("该产品下没有有效账号");
				} else {
					// 查产品在当月的总成本，月初<= <=月末
					Timestamp startTime = new Timestamp(startDate.getTime());
					Timestamp endTime = new Timestamp(DateUtil.getMonthFinal(startDate).getTime());
					totalCost = customerProductService.queryCustomerProductCost(startTime, endTime, loginNameList, productType, product.getYysType());
				}
				// TODO 查平台账单金额
				result.put("平台成功数", successCount);
				result.put("综合成本", totalCost.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
				result.put("当前有效账号", String.join(",", loginNameList));
				if (StringUtils.equals(update, "true")) {
					bill.setCost(totalCost.setScale(2, BigDecimal.ROUND_HALF_UP));
					bill.setPlatformCount(successCount);
					bill.setGrossProfit(bill.getReceivables().subtract(totalCost).setScale(2, BigDecimal.ROUND_HALF_UP));
					boolean success = productBillsService.update(bill);
					if (success) {
						logger.info("更新账单id：" + id + "的平台成功数为 " + successCount + " ，更新成本为 " + totalCost.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
								+ " ，更新毛利润为 " + bill.getGrossProfit().toPlainString());
					} else {
						logger.info("更新账单id：" + id + "的平台数据失败");
					}
					result.put("处理结果", success ? "成功" : "失败");
				}
			} else if (bill.getEntityType() == EntityType.SUPPLIER.ordinal()) {
				logger.info("不查供应商账单");
			}
		} catch (Exception e) {
			logger.error("重新统计账单数据异常", e);
			return result;
		}
		return result;
	}

	/**
	 * 查询客户所有未对账账单
	 * 
	 * @param customerId
	 *            客户id
	 * @param flowEntId
	 *            对账流程id（同时查此流程对账中的账单）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/readUncheckedBills")
	public BaseResponse<List<UncheckedBillDto>> readUncheckedBills(@RequestParam String customerId, @RequestParam(required = false) String flowEntId) {
		logger.info("按客户查询未对账的账单开始，customerId：" + customerId);
		List<UncheckedBillDto> dtos = new ArrayList<UncheckedBillDto>();
		try {
			Customer customer = customerService.read(customerId);
			if (null == customer) {
				logger.info("按客户id找不到对应的客户：" + customerId);
				return BaseResponse.success(dtos);
			}
			String hql = "from ProductBills where entityId = :entityId and entityType = :entityType";
			Map<String, Object> params = new HashMap<>();
			params.put("entityId", customerId);
			params.put("entityType", EntityType.CUSTOMER.ordinal());
			// 检查在对账中，未对完账的账单
			List<String> billIdList = null;
			if (StringUtil.isNotBlank(flowEntId)) {
				billIdList = queryCheckingBill(flowEntId);
			}
			if (!CollectionUtils.isEmpty(billIdList)) {
				hql += " and (billStatus = :billStatus or (billStatus = :checkingStatus and id in (:billIdList)))";
				params.put("billStatus", BillStatus.NO_RECONCILE.ordinal());
				params.put("checkingStatus", BillStatus.RECONILING.ordinal());
				params.put("billIdList", billIdList);
			} else {
				hql += " and billStatus = :billStatus";
				params.put("billStatus", BillStatus.NO_RECONCILE.ordinal());
			}
			List<ProductBills> billList = productBillsService.findByhql(hql, params, 0);
			if (CollectionUtils.isEmpty(billList)) {
				logger.info("该客户未查询到未对账的账单，customerId：" + customerId);
				return BaseResponse.success(dtos);
			}
			for (ProductBills bill : billList) {
				UncheckedBillDto dto = new UncheckedBillDto();
				dto.setId(bill.getId());
				dto.setBillNumber(bill.getBillNumber());
				dto.setEntityId(bill.getEntityId());
				dto.setEntityType(EntityType.CUSTOMER.getCode());
				dto.setProductId(bill.getProductId());
				// 平台数据
				dto.setPlatformAmount(bill.getReceivables().setScale(2, BigDecimal.ROUND_HALF_UP));
				dto.setPlatformSuccessCount(bill.getPlatformCount());
				dto.setPlatformUnitPrice(bill.getPlatformCount() > 0 ? bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0.000000"));
				// 客户数据默认用平台数据
				dto.setCustomerSuccessCount(dto.getPlatformSuccessCount());
				dto.setCustomerAmount(dto.getPlatformAmount());
				dto.setCustomerUnitPrice(dto.getPlatformUnitPrice());
				// 对账数据默认用平台数据
				dto.setCheckedSuccessCount(dto.getPlatformSuccessCount());
				dto.setCheckedAmount(dto.getPlatformAmount());
				dto.setCheckedUnitPrice(dto.getPlatformUnitPrice());

				Date date = bill.getWtime();
				String dateString = DateUtil.convert(date, DateUtil.format4);
				dto.setBillMonth(dateString);
				CustomerProduct temp = customerProductService.read(bill.getProductId());

				dto.setTitle("账单-" + dateString + "-" + temp.getProductName());
				dto.setRemark(bill.getRemark());

				String billFiles = bill.getFiles();
				if (StringUtil.isNotBlank(billFiles)) {
					JSONArray files = JSON.parseArray(billFiles);
					for (Object file : files) {
						String fileName = ((JSONObject) file).getString("fileName");
						if (StringUtils.endsWith(fileName, ".pdf")) {
							dto.setBillFile(((JSONObject) file).toJSONString());
						}
					}
				}

				dtos.add(dto);
			}
			logger.info("按客户查询未对账的账单数：" + dtos.size());
		} catch (ServiceException e) {
			logger.info("按客户查询未对账的账单异常", e);
		}
		return BaseResponse.success(dtos);
	}

	@RequestMapping("/toBuildBill")
	public String toBuildBill(@RequestParam String customerId) {
		try {
			Customer customer = customerService.read(customerId);
			if (null == customer) {
				logger.info("未查询到客户，customerId：" + customerId);
				return "";
			}
			request.setAttribute("customerId", customer.getCustomerId());
			request.setAttribute("companyName", customer.getCompanyName());
		} catch (ServiceException e) {
			logger.error("查询客户异常，customerId：" + customerId, e);
			return "";
		}
		logger.info("跳转生成产品月账单页面，customerId：" + customerId);
		return "/views/billsModify/buildBillPage";
	}

	/**
	 * 生成账单
	 * 
	 * @param productIds
	 *            产品id
	 * @param billMonth
	 *            账单月份
	 * @param redo
	 *            是否覆盖原来的
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/buildBill")
	public BaseResponse<List<UncheckedBillDto>> buildBill(@RequestParam String productIds, @RequestParam String billMonth, @RequestParam String redo) {
		if (StringUtils.isAnyBlank(productIds, billMonth)) {
			logger.info("产品id和账单月份不能为空");
			return BaseResponse.error("生成失败：产品id和账单月份不能为空");
		}
		List<UncheckedBillDto> dtos = new ArrayList<>();
		StringBuffer msg = new StringBuffer();
		String[] productIdList = productIds.split(",");
		for (String productId : productIdList) {
			BaseResponse<ProductBills> billResp = productBillsService.buildCustomerBill(productId, billMonth, Boolean.parseBoolean(redo), false);
			ProductBills bill = billResp.getData();
			if (null == bill) {
				msg.append("产品id：").append(productId).append("，").append(billResp.getMsg()).append("<br/>");
			} else {
				UncheckedBillDto dto = new UncheckedBillDto();
				dto.setPlatformSuccessCount(bill.getPlatformCount());
				dto.setPlatformAmount(bill.getReceivables().setScale(2, BigDecimal.ROUND_HALF_UP));
				dto.setPlatformUnitPrice(bill.getPlatformCount() > 0 ? bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0.000000"));
				dtos.add(dto);
			}
		}
		if (dtos.size() > 0) {
			return BaseResponse.success(msg.toString(), dtos);
		} else {
			return BaseResponse.error(msg.toString());
		}
	}

	/**
	 * 重新统计账单数据
	 *
	 * @param productId
	 *            产品id
	 * @param billMonth
	 *            账单月份
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/rebuildBill")
	public BaseResponse<UncheckedBillDto> rebuildBill(@RequestParam String productId, @RequestParam String billMonth) {
		BaseResponse<ProductBills> billResp = productBillsService.buildCustomerBill(productId, billMonth, true, false);
		ProductBills bill = billResp.getData();
		if (null == bill) {
			return BaseResponse.error(billResp.getMsg());
		} else {
			UncheckedBillDto dto = new UncheckedBillDto();
			dto.setPlatformSuccessCount(bill.getPlatformCount());
			dto.setPlatformAmount(bill.getReceivables().setScale(2, BigDecimal.ROUND_HALF_UP));
			dto.setPlatformUnitPrice(bill.getPlatformCount() > 0 ? bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0.000000"));
			dto.setCustomerSuccessCount(bill.getSupplierCount());
			dto.setCustomerAmount(bill.getReceivables().setScale(2, BigDecimal.ROUND_HALF_UP));
			dto.setCustomerUnitPrice(bill.getSupplierCount() > 0 ? bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0.000000"));
			dto.setCheckedSuccessCount(bill.getSupplierCount());
			dto.setCheckedAmount(bill.getReceivables().setScale(2, BigDecimal.ROUND_HALF_UP));
			dto.setCheckedUnitPrice(bill.getSupplierCount() > 0 ? bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0.000000"));
			return BaseResponse.success(billResp.getMsg(), dto);
		}
	}

	/**
	 * 根据选中的账单，合并生成对账pdf
	 * 
	 * @param billIds
	 *            选中的账单编号
	 * @param billTotal
	 *            填写的对账数据的总计
	 * @param options
	 * 	          选项，勾选
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/buildCheckBillFile")
	public BaseResponse<JSONObject> buildCheckBillFile(@RequestParam String billIds, @RequestParam String billTotal, @RequestParam String options) {
		List<String> billIdList = Arrays.asList(billIds.split(","));
		// 需要的内容，billFile对账单，dataDetail数据详情
		List<String> optionList = Arrays.asList(options.split(","));
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("id", Constants.ROP_IN, billIdList));
		JSONObject billFile = new JSONObject();
		try {
			List<ProductBills> billList = productBillsService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(billList)) {
				logger.info("未找到账单");
				return BaseResponse.error("未找到账单");
			}
			Customer customer = customerService.read(billList.get(0).getEntityId());
			if (customer == null) {
				logger.info("客户不存在，customerId：" + billList.get(0).getEntityId());
				return BaseResponse.error("客户不存在");
			}
			JSONObject billTotalJson = JSON.parseObject(billTotal);
			Date today = new Date();
			String fileName = customer.getCompanyName() + "-已选中账单的对账单-" + DateUtil.convert(today, DateUtil.format10) + ".pdf";
			String filePath = Constants.RESOURCE + File.separator + billFileDir + File.separator + customer.getCustomerId() + File.separator
					+ DateUtil.convert(today, "yyyyMM") + File.separator + fileName;
			billFile.put("fileName", fileName);
			billFile.put("filePath", filePath);
			billFile.put("options", JSON.toJSONString(optionList));
			String msg = productBillsService.createMergePdf(billList, filePath, billTotalJson, optionList);
			if (StringUtil.isNotBlank(msg)) {
				return BaseResponse.error(msg);
			}
			return BaseResponse.success(billFile);
		} catch (ServiceException e) {
			logger.error("生成对账单异常", e);
			return BaseResponse.error("生成对账单异常，请联系管理员");
		}
	}

	/**
	 * 获取正在对账的账单
	 *
	 * @param flowEntId
	 *            在走的对账流程id
	 * @return
	 */
	private List<String> queryCheckingBill(String flowEntId) {
		List<String> billIdList = new ArrayList<>();
		try {
			FlowEnt flowEnt = flowEntService.read(flowEntId);
			if (flowEnt != null) {
				String flowMsg = flowEnt.getFlowMsg();
				if (StringUtil.isNotBlank(flowMsg)) {
					JSONObject flowMsgJson = JSONObject.parseObject(flowMsg);
					if (flowMsgJson != null) {
						String uncheckBillInfo = flowMsgJson.getString(Constants.UNCHECKED_BILL_KEY);
						JSONObject uncheckBillInfoJson = JSONObject.parseObject(uncheckBillInfo);
						if (uncheckBillInfoJson != null) {
							String billInfosStr = uncheckBillInfoJson.getString("billInfos");
							JSONArray billInfos = JSON.parseArray(billInfosStr);
							if (billInfos != null) {
								billInfos.forEach(billInfo -> {
									billIdList.add(((JSONObject) billInfo).getString("id"));
								});
							}
						}
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("查询流程异常", e);
		}
		return billIdList;
	}

	/**
	 * 生成数据分析报告
	 * 
	 * @param billIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/buildDataAnalysisFile")
	public BaseResponse<JSONObject> buildDataAnalysisFile(@RequestParam String billIds) {
		List<String> billIdList = Arrays.asList(billIds.split(","));
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("id", Constants.ROP_IN, billIdList));
		JSONObject analysisFile = new JSONObject();
		try {
			List<ProductBills> billList = productBillsService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(billList)) {
				logger.info("未找到账单");
				return BaseResponse.error("未找到账单");
			}
			Customer customer = customerService.read(billList.get(0).getEntityId());
			if (customer == null) {
				logger.info("客户不存在，customerId：" + billList.get(0).getEntityId());
				return BaseResponse.error("客户不存在");
			}
			Date today = new Date();
			String fileName = customer.getCompanyName() + "-数据分析报告-" + DateUtil.convert(today, DateUtil.format10) + ".pdf";
			String filePath = Constants.RESOURCE + File.separator + analysisFileDir + File.separator + customer.getCustomerId() + File.separator
					+ DateUtil.convert(today, "yyyyMM") + File.separator + fileName;
			analysisFile.put("fileName", fileName);
			analysisFile.put("filePath", filePath);
			String taskId = customer.getCustomerId() + "-" + DateUtil.convert(today, DateUtil.format10);
			boolean result = acquireAccountReportDetailHandler.doAcquireReportDetail(billList, taskId);
			if (result) {
				logger.info("提交请求成功");
				return BaseResponse.success(analysisFile);
			}
			logger.info("提交请求失败");
			return BaseResponse.error("请求数据分析报告失败");
		} catch (ServiceException e) {
			logger.error("生成对账单异常", e);
			return BaseResponse.error("生成对账单异常，请联系管理员");
		}
	}

	/**
	 * 跳转账单操作
	 * 
	 * @return
	 */
	@RequestMapping("/toOperateBill")
	public String toOperateBill() {
		request.setAttribute("billId", request.getParameter("billId"));
		return "/views/billsModify/operateBillPage";
	}

	/**
	 * 获取要操作的账单
	 * 
	 * @param billId
	 *            账单id
	 * @return
	 */
	@RequestMapping("/getBillDetail")
	@ResponseBody
	public BaseResponse<Object> getBillDetail(@RequestParam String billId) {
		try {
			ProductBills bill = productBillsService.read(billId);
			if (null == bill) {
				logger.info("账单不存在，账单id：" + billId);
				return BaseResponse.error("账单不存在");
			}
			BillRespDto dto = new BillRespDto(bill);
			if (dto.getEntityType() == EntityType.CUSTOMER.getCode()) {
				dto.setEntityTypeName(EntityType.CUSTOMER.getMsg());
				Customer customer = customerService.read(dto.getEntityId());
				if (null == customer) {
					logger.info("账单对应的客户不存在，customerId：" + dto.getEntityId());
				} else {
					dto.setCompanyName(customer.getCompanyName());
					dto.setOssUserId(customer.getOssuserId());
				}
				CustomerProduct product = customerProductService.read(dto.getProductId());
				if (null == product) {
					logger.info("账单对应的客户产品不存在，productId：" + dto.getProductId());
				} else {
					dto.setProductName(product.getProductName());
					dto.setProductType(product.getProductType());
					dto.setSettleType(SettleType.getSettleType(product.getSettleType()));
					dto.setBillPeriod(product.getBillPeriod() + "个月");
				}
			} else if (dto.getEntityType() == EntityType.SUPPLIER.getCode()) {
				dto.setEntityTypeName(EntityType.SUPPLIER.getMsg());
				Supplier supplier = supplierService.read(dto.getEntityId());
				if (null == supplier) {
					logger.info("账单对应的供应商不存在，supplierId：" + dto.getEntityId());
				} else {
					dto.setCompanyName(supplier.getCompanyName());
					dto.setOssUserId(supplier.getOssUserId());
				}
				Product product = productService.read(dto.getProductId());
				if (null == product) {
					logger.info("账单对应的供应商产品不存在，productId：" + dto.getProductId());
				} else {
					dto.setProductName(product.getProductName());
					dto.setProductType(product.getProductType());
					dto.setSettleType(SettleType.getSettleType(product.getSettleType()));
				}
			}

			dto.setBillTitle("账单-" + dto.getBillMonth() + "-" + dto.getProductName());

			if (StringUtil.isNotBlank(dto.getOssUserId())) {
				User user = userService.read(dto.getOssUserId());
				if (null == user) {
					logger.info("账单对应的用户不存在，ossUserId：" + dto.getOssUserId());
				} else {
					dto.setRealName(user.getRealName());
				}
			} else {
				logger.info("账单对应的用户Id为空");
			}
			String productTypeName = productTypeService.getProductTypeNameByValue(dto.getProductType());
			dto.setProductTypeName(productTypeName);
			return BaseResponse.success(dto);
		} catch (ServiceException e) {
			logger.error("查询账单详情异常，账单id：" + billId, e);
			return BaseResponse.error("查询账单详情异常");
		}
	}

	@RequestMapping("/operateDeleteMonthBill")
	@ResponseBody
	public String operateDeleteMonthBill(@RequestParam String month, @RequestParam(required = false) String settleType) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除月份账单】，月份：" + month + "，付费方式：" + settleType);
		String msg = "";
		try {
			List<String> billIdList = null;
			SearchFilter filter = new SearchFilter();
			// settleType为空查全部产品，不为空时只查预付/后付的产品
			if (StringUtil.isNotBlank(settleType)) {
				int settleTypeValue = Integer.parseInt(settleType);
				if (settleTypeValue == SettleType.Prepurchase.ordinal()) {
					// 预购预付
					filter.getRules().add(new SearchRule("settleType", Constants.ROP_IN, Arrays.asList(SettleType.Prepurchase.ordinal(), SettleType.Advance.ordinal())));
				} else {
					// 后付
					filter.getRules().add(new SearchRule("settleType", Constants.ROP_EQ, SettleType.After.ordinal()));
				}
				List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
				if (CollectionUtils.isEmpty(productList)) {
					msg = "无客户产品";
					logger.info("付费方式：" + settleType + "，无客户产品");
					return msg;
				}
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("productId", Constants.ROP_IN, productList.stream().map(CustomerProduct::getProductId).collect(Collectors.toList())));
			}

			filter.getRules().add(new SearchRule("wtime", Constants.ROP_EQ, DateUtil.convert(month, DateUtil.format4)));
			List<ProductBills> billList = productBillsService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(billList)) {
				billIdList = billList.stream().map(ProductBills::getId).collect(Collectors.toList());
			} else {
				logger.info("月份账单为空，月份：" + month);
				return "月份账单为空";
			}

			int count = 0;
			String result = "";
			List<String> failedIdList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(billIdList)) {
				count = billIdList.size();
				for (String billId : billIdList) {
					BillRespDto data = new BillRespDto();
					data.setBillId(billId);
					result = operateDeleteBill(data);
					if (!result.equals(RESULT_SUCCESS)) {
						failedIdList.add(billId);
					}
				}
			}
			msg += "要删除账单数：" + count;
			msg += CollectionUtils.isEmpty(failedIdList) ? "，全部成功" : "，部分成功，失败数：" + failedIdList.size();
			logger.info("执行操作【删除月份账单】，" + msg);
		} catch (ServiceException e) {
			logger.error("删除账单异常", e);
		}
		return msg;
	}

	@RequestMapping("/operateDeleteBill")
	@ResponseBody
	public String operateDeleteBill(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除账单】，账单id：" + data.getBillId());
		String msg = "";
		try {
			operateDeleteAllPenaltyInterest(data);
			operateDeleteOperateCost(data);
			operateDeleteRealRoyalty(data);
			operateReleaseIncome(data);

			boolean result = productBillsService.delete(data.getBillId());
			msg += result ? RESULT_SUCCESS : RESULT_FAILED;
			logger.info(msg);
		} catch (ServiceException e) {
			logger.error("删除账单异常", e);
		}
		return msg;
	}

	@RequestMapping("/operateModifyBill")
	@ResponseBody
	public String operateModifyBill(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【修改账单数据】");
		String msg = "";
		StringBuilder operationLog = new StringBuilder();
		if (StringUtil.isBlank(data.getBillId())) {
			logger.info("账单id不能为空");
			msg = "账单id不能为空";
			return msg;
		}
		try {
			ProductBills bill = productBillsService.read(data.getBillId());
			if (null == bill) {
				logger.info("账单不存在，账单id：" + data.getBillId());
				msg = "账单不存在";
				return msg;
			}
			if (StringUtil.isNotBlank(data.getPlatformSuccessCount())) {
				if (StringUtils.isNumeric(data.getPlatformSuccessCount())) {
					long platformSuccessCount = Long.parseLong(data.getPlatformSuccessCount());
					if (platformSuccessCount != bill.getPlatformCount()) {
						operationLog.append("【平台成功数】由").append(bill.getPlatformCount()).append(" 改为 ").append(platformSuccessCount).append("；");
						bill.setPlatformCount(platformSuccessCount);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getCost())) {
				if (StringUtil.isMoneyNumber(data.getCost())) {
					BigDecimal cost = new BigDecimal(data.getCost());
					if (cost.compareTo(bill.getCost()) != 0) {
						operationLog.append("【综合成本】由").append(bill.getCost().toPlainString()).append(" 改为 ").append(cost.toPlainString()).append("；");
						bill.setCost(cost);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getCheckedSuccessCount())) {
				if (StringUtils.isNumeric(data.getCheckedSuccessCount())) {
					long checkedSuccessCount = Long.parseLong(data.getCheckedSuccessCount());
					if (checkedSuccessCount != bill.getSupplierCount()) {
						operationLog.append("【实际成功数】由").append(bill.getSupplierCount()).append(" 改为 ").append(checkedSuccessCount).append("；");
						bill.setSupplierCount(checkedSuccessCount);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getReceivables())) {
				if (StringUtil.isMoneyNumber(data.getReceivables())) {
					BigDecimal receivables = new BigDecimal(data.getReceivables());
					if (receivables.compareTo(bill.getReceivables()) != 0) {
						operationLog.append("【应收】由").append(bill.getReceivables().toPlainString()).append(" 改为 ").append(receivables.toPlainString())
								.append("；");
						bill.setReceivables(receivables);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getActualReceivables())) {
				if (StringUtil.isMoneyNumber(data.getActualReceivables())) {
					BigDecimal actualReceivables = new BigDecimal(data.getActualReceivables());
					if (actualReceivables.compareTo(bill.getActualReceivables()) != 0) {
						operationLog.append("【实收】由").append(bill.getActualReceivables().toPlainString()).append(" 改为 ")
								.append(actualReceivables.toPlainString()).append("；");
						bill.setActualReceivables(actualReceivables);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getPayables())) {
				if (StringUtil.isMoneyNumber(data.getPayables())) {
					BigDecimal payables = new BigDecimal(data.getPayables());
					if (payables.compareTo(bill.getPayables()) != 0) {
						operationLog.append("【应付】由").append(bill.getPayables().toPlainString()).append(" 改为 ").append(payables.toPlainString()).append("；");
						bill.setPayables(payables);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getActualPayables())) {
				if (StringUtil.isMoneyNumber(data.getActualPayables())) {
					BigDecimal actualPayables = new BigDecimal(data.getActualPayables());
					if (actualPayables.compareTo(bill.getActualPayables()) != 0) {
						operationLog.append("【实付】由").append(bill.getActualPayables().toPlainString()).append(" 改为 ").append(actualPayables.toPlainString())
								.append("；");
						bill.setActualPayables(actualPayables);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getActualInvoiceAmount())) {
				if (StringUtil.isMoneyNumber(data.getActualInvoiceAmount())) {
					BigDecimal actualInvoiceAmount = new BigDecimal(data.getActualInvoiceAmount());
					if (actualInvoiceAmount.compareTo(bill.getActualInvoiceAmount()) != 0) {
						operationLog.append("【已开金额】由").append(bill.getActualInvoiceAmount().toPlainString()).append(" 改为 ")
								.append(actualInvoiceAmount.toPlainString()).append("；");
						bill.setActualInvoiceAmount(actualInvoiceAmount);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getUnitPrice())) {
				if (StringUtil.isMoneyNumber(data.getUnitPrice())) {
					BigDecimal unitPrice = new BigDecimal(data.getUnitPrice());
					if (unitPrice.compareTo(bill.getUnitPrice()) != 0) {
						operationLog.append("【平均销售单价】由").append(bill.getUnitPrice().toPlainString()).append(" 改为 ").append(unitPrice.toPlainString())
								.append("；");
						bill.setUnitPrice(unitPrice);
					}
				}
			}
			if (StringUtil.isNotBlank(data.getGrossProfit())) {
				if (StringUtil.isMoneyNumber(data.getGrossProfit())) {
					BigDecimal grossProfit = new BigDecimal(data.getGrossProfit());
					if (grossProfit.compareTo(bill.getGrossProfit()) != 0) {
						operationLog.append("【毛利润】由").append(bill.getGrossProfit().toPlainString()).append(" 改为 ").append(grossProfit.toPlainString())
								.append("；");
						bill.setGrossProfit(grossProfit);
					}
				}
			}
			// 保存修改记录
			if (operationLog.length() > 0) {
				JSONArray oldLog = null;
				String oldOperationLog = bill.getOperationLog();
				if (StringUtil.isNotBlank(oldOperationLog)) {
					oldLog = JSON.parseArray(oldOperationLog);
				} else {
					oldLog = new JSONArray();
				}
				JSONObject log = new JSONObject();
				log.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
				log.put("ossUserId", onlineUser.getUser().getOssUserId());
				log.put("operator", operator);
				log.put("operation", operationLog.toString());
				oldLog.add(log);
				bill.setOperationLog(oldLog.toJSONString());
			}
			boolean result = productBillsService.update(bill);
			msg = result ? RESULT_SUCCESS : RESULT_FAILED;
			logger.info(msg);
		} catch (ServiceException e) {
			logger.error("修改账单数据异常", e);
			msg = "修改账单数据异常";
		}
		return msg;
	}

	@RequestMapping("/operateBillUnChecked")
	@ResponseBody
	public String operateBillUnChecked(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【修改为未对账】，账单id：" + data.getBillId());
		String msg = "";
		ProductBills bill;
		try {

			// 删除账单罚息
			operateDeleteAllPenaltyInterest(data);
			// 删除运营成本
			operateDeleteOperateCost(data);
			// 删除实际提成
			operateDeleteRealRoyalty(data);
			// 还原到款
			operateReleaseIncome(data);

			bill = productBillsService.read(data.getBillId());
			if (null == bill) {
				logger.info("账单不存在，账单id：" + data.getBillId());
				msg = "账单不存在";
				return msg;
			}

			// 保存修改记录
			JSONArray oldLog = null;
			String oldOperationLog = bill.getOperationLog();
			if (StringUtil.isNotBlank(oldOperationLog)) {
				oldLog = JSON.parseArray(oldOperationLog);
			} else {
				oldLog = new JSONArray();
			}
			JSONObject log = new JSONObject();
			log.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
			log.put("ossUserId", onlineUser.getUser().getOssUserId());
			log.put("operator", operator);
			log.put("operation", "【账单状态】修改为未对账");
			oldLog.add(log);
			bill.setOperationLog(oldLog.toJSONString());

			// 账单状态改为未对账
			bill.setBillStatus(BillStatus.NO_RECONCILE.ordinal());
			boolean result = productBillsService.update(bill);
			msg = result ? RESULT_SUCCESS : RESULT_FAILED;
			logger.info(msg);
		} catch (ServiceException e) {
			logger.error("修改为未对账异常", e);
			msg = "修改为未对账异常";
		}
		return msg;
	}

	/**
	 * 解除账单关联，还原到款
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping("/operateDeleteCheckFlow")
	@ResponseBody
	public String operateDeleteCheckFlow(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除对账流程】，账单id：" + data.getBillId());
		String msg = "";
		ProductBills bill;
		try {
			bill = productBillsService.read(data.getBillId());
			if (null == bill) {
				logger.info("账单不存在，账单id：" + data.getBillId());
				msg = "账单不存在";
				return msg;
			}
			List<String> delIdList = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			String flowHql = "select ent from FlowEnt ent left join ErpFlow ef on ent.flowId = ef.flowId where ef.flowClass = '"
					+ Constants.CHECK_BILL_FLOW_CLASS + "' and ent.flowMsg like '%" + bill.getId() + "%' and ent.supplierId = '" + bill.getEntityId() + "'";
			logger.info("查对账流程的Hql：" + flowHql);
			List<FlowEnt> flowEntList = flowEntService.findByhql(flowHql, null, 0);
			if (!CollectionUtils.isEmpty(flowEntList)) {
				flowEntList.forEach(ent -> {
					String flowMsg = ent.getFlowMsg();
					JSONObject flowMsgJson = JSON.parseObject(flowMsg);
					String checkBillLabel = flowMsgJson.getString(Constants.UNCHECKED_BILL_KEY);
					if (StringUtil.isNotBlank(checkBillLabel)) {
						JSONObject checkBillJson = JSON.parseObject(checkBillLabel);
						String billInfos = checkBillJson.getString("billInfos");
						JSONArray billInfoList = JSON.parseArray(billInfos);
						// 只此一个账单的对账流程
						if (billInfoList.size() == 1) {
							delIdList.add(ent.getId());
						}
					}
				});
			}
			if (delIdList.size() > 0) {
				String sql1 = "delete from erp_flow_ent where id in (" + delIdList.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")) + ")";
				String sql2 = "delete from erp_flow_log where flowentid in (" + delIdList.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","))
						+ ")";
				logger.info("删除流程实体的sql：" + sql1);
				logger.info("删除流程处理记录的sql：" + sql2);
				try {
					baseDao.executeUpdateSQL(sql1);
					baseDao.executeUpdateSQL(sql2);
					logger.info("删除成功");
					msg = RESULT_SUCCESS;
				} catch (Exception e) {
					logger.info("删除失败", e);
					msg = RESULT_FAILED;
				}
			} else {
				logger.info("此账单无对账流程，或对账流程包含其他账单");
				msg = "此账单无对账流程，或对账流程包含其他账单";
			}
			logger.info(msg);
		} catch (ServiceException e) {
			logger.error("删除对账流程异常", e);
			msg = "删除对账流程异常";
		}
		return msg;
	}

	@RequestMapping("/operateBillUnWriteOff")
	@ResponseBody
	public String operateBillUnWriteOff(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【修改为未销账(已对账)】，账单id：" + data.getBillId());
		String msg = "";
		ProductBills bill;
		try {
			// 删除运营成本
			operateDeleteOperateCost(data);
			// 删除实际提成
			operateDeleteRealRoyalty(data);
			// 还原到款
			operateReleaseIncome(data);

			bill = productBillsService.read(data.getBillId());
			if (null == bill) {
				logger.info("账单不存在，账单id：" + data.getBillId());
				msg = "账单不存在";
				return msg;
			}

			// 保存修改记录
			JSONArray oldLog = null;
			String oldOperationLog = bill.getOperationLog();
			if (StringUtil.isNotBlank(oldOperationLog)) {
				oldLog = JSON.parseArray(oldOperationLog);
			} else {
				oldLog = new JSONArray();
			}
			JSONObject log = new JSONObject();
			log.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
			log.put("ossUserId", onlineUser.getUser().getOssUserId());
			log.put("operator", operator);
			log.put("operation", "【账单状态】修改为未销账(已对账)");
			oldLog.add(log);
			bill.setOperationLog(oldLog.toJSONString());

			// 账单状态改为未销账
			bill.setBillStatus(BillStatus.RECONILED.ordinal());
			bill.setActualReceivables(BigDecimal.ZERO);
			bill.setWriteOffTime(null);
			boolean result = productBillsService.update(bill);
			msg = "修改为未销账(已对账)" + (result ? RESULT_SUCCESS : RESULT_FAILED);
			logger.info(msg);
		} catch (ServiceException e) {
			logger.error("修改为未销账(已对账)异常", e);
			msg = "修改为未销账(已对账)异常";
		}
		return msg;
	}

	/**
	 * 解除账单关联，还原到款
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping("/operateDeleteWriteOffFlow")
	@ResponseBody
	public String operateDeleteWriteOffFlow(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除销账流程】，账单id：" + data.getBillId());
		String msg = "";
		ProductBills bill;
		try {
			bill = productBillsService.read(data.getBillId());
			if (null == bill) {
				logger.info("账单不存在，账单id：" + data.getBillId());
				msg = "账单不存在";
				return msg;
			}
			List<String> delIdList = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			String flowHql = "select ent from FlowEnt ent left join ErpFlow ef on ent.flowId = ef.flowId where ef.flowClass = '"
					+ Constants.BILL_WRITE_OFF_FLOW_CLASS + "' and ent.flowMsg like '%" + bill.getId() + "%' and ent.supplierId = '" + bill.getEntityId() + "'";
			logger.info("查销账流程的Hql：" + flowHql);
			List<FlowEnt> flowEntList = flowEntService.findByhql(flowHql, null, 0);
			if (!CollectionUtils.isEmpty(flowEntList)) {
				flowEntList.forEach(ent -> {
					String flowMsg = ent.getFlowMsg();
					JSONObject flowMsgJson = JSON.parseObject(flowMsg);
					String billLabel = flowMsgJson.getString(Constants.BILL_INFO_KEY);
					if (StringUtil.isNotBlank(billLabel)) {
						JSONArray billInfoList = JSON.parseArray(billLabel);
						// 只此一个账单的对账流程
						if (billInfoList.size() == 1) {
							delIdList.add(ent.getId());
						}
					}
				});
			}
			if (delIdList.size() > 0) {
				String sql1 = "delete from erp_flow_ent where id in (" + delIdList.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")) + ")";
				String sql2 = "delete from erp_flow_log where flowentid in (" + delIdList.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","))
						+ ")";
				logger.info("删除流程实体的sql：" + sql1);
				logger.info("删除流程处理记录的sql：" + sql2);
				try {
					baseDao.executeUpdateSQL(sql1);
					baseDao.executeUpdateSQL(sql2);
					logger.info("删除成功");
					msg = RESULT_SUCCESS;
				} catch (Exception e) {
					logger.info("删除失败", e);
					msg = RESULT_FAILED;
				}
				logger.info(msg);
			} else {
				logger.info("此账单无销账流程，或销账流程包含其他账单");
				msg = "此账单无销账流程，或销账流程包含其他账单";
			}
		} catch (ServiceException e) {
			logger.error("删除销账流程异常", e);
			msg = "删除销账流程异常";
		}
		return msg;
	}

	/**
	 * 解除账单关联，还原到款
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping("/operateReleaseIncome")
	@ResponseBody
	public String operateReleaseIncome(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【还原到款】，账单id：" + data.getBillId());
		String msg = "";
		ProductBills bill;
		try {
			bill = productBillsService.read(data.getBillId());
			if (null == bill) {
				logger.info("账单不存在，账单id：" + data.getBillId());
				msg = "账单不存在";
				return msg;
			}
			Map<String, String> incomeIdMap = new HashMap<>();
			String relatedInfo = bill.getRelatedInfo();
			if (StringUtil.isNotBlank(relatedInfo)) {
				JSONArray incomesJson = JSON.parseArray(relatedInfo);
				for (Object incomeJson : incomesJson) {
					incomeIdMap.put(((JSONObject) incomeJson).getString("fsExpenseIncomeId"), ((JSONObject) incomeJson).getString("thisCost"));
				}
			} else {
				logger.info("账单关联的到款为空");
				msg = "账单关联的到款为空";
				return msg;
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(incomeIdMap.keySet())));
			List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(incomeList)) {
				logger.info("账单关联的到款为空");
				msg = "账单关联的到款为空";
				return msg;
			}
			// 还原到款的剩余金额
			for (FsExpenseIncome income : incomeList) {
				String thisCost = incomeIdMap.getOrDefault(income.getId(), "0.00");
				income.setRemainRelatedCost(income.getRemainRelatedCost().add(new BigDecimal(thisCost)));
			}
			boolean result = fsExpenseIncomeService.updateByBatch(incomeList);
			msg = "还原到款的剩余销账金额" + (result ? RESULT_SUCCESS : RESULT_FAILED);
			logger.info(msg);

			// 保存修改记录
			JSONArray oldLog = null;
			String oldOperationLog = bill.getOperationLog();
			if (StringUtil.isNotBlank(oldOperationLog)) {
				oldLog = JSON.parseArray(oldOperationLog);
			} else {
				oldLog = new JSONArray();
			}
			JSONObject log = new JSONObject();
			log.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
			log.put("ossUserId", onlineUser.getUser().getOssUserId());
			log.put("operator", operator);
			log.put("operation", "【关联到款】修改为空");
			oldLog.add(log);
			bill.setOperationLog(oldLog.toJSONString());

			// 清空账单关联到款
			bill.setRelatedInfo(null);
			productBillsService.update(bill);
		} catch (ServiceException e) {
			logger.error("还原到款异常", e);
			msg = "还原到款异常";
		}
		return msg;
	}

	@RequestMapping("/operateDeleteRealRoyalty")
	@ResponseBody
	public String operateDeleteRealRoyalty(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除实际提成】，账单id：" + data.getBillId());
		String msg = "";
		if (StringUtil.isBlank(data.getBillId())) {
			logger.info("账单id不能为空");
			msg = "账单id不能为空";
			return msg;
		}
		try {
			String sql = "delete from erp_real_royalty where billid = ?";
			baseDao.executeSqlUpdte(sql, new Object[] { data.getBillId() }, new Type[] { StandardBasicTypes.STRING });
			msg = "删除实际提成" + RESULT_SUCCESS;
			logger.info(msg);
		} catch (Exception e) {
			logger.error("删除实际提成异常", e);
			msg = "删除实际提成异常";
		}
		return msg;
	}

	@RequestMapping("/operateDeleteOperateCost")
	@ResponseBody
	public String operateDeleteOperateCost(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除运营成本】，账单id：" + data.getBillId());
		String msg = "";
		if (StringUtil.isBlank(data.getBillId())) {
			logger.info("账单id不能为空");
			msg = "账单id不能为空";
			return msg;
		}
		try {
			String sql = "delete from erp_operate_cost where billid = ?";
			baseDao.executeSqlUpdte(sql, new Object[] { data.getBillId() }, new Type[] { StandardBasicTypes.STRING });
			msg = "删除运营成本" + RESULT_SUCCESS;
			logger.info(msg);
		} catch (Exception e) {
			logger.error("删除运营成本异常", e);
			msg = "删除运营成本异常";
		}
		return msg;
	}

	@RequestMapping("/operateDeletePenaltyInterest")
	@ResponseBody
	public String operateDeletePenaltyInterest(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除账单当月罚息】，账单id：" + data.getBillId());
		String msg = "";
		if (StringUtil.isBlank(data.getBillId())) {
			logger.info("账单id不能为空");
			msg = "账单id不能为空";
			return msg;
		}
		try {
			String sql = "delete from erp_bill_penalty_interest where billid = ? and penaltyinterestmonth = ?";
			baseDao.executeSqlUpdte(sql, new Object[] { data.getBillId(), DateUtil.getThisMonthFirst() },
					new Type[] { StandardBasicTypes.STRING, StandardBasicTypes.TIMESTAMP });
			msg = RESULT_SUCCESS;
			logger.info(msg);
		} catch (Exception e) {
			logger.error("删除账单当月罚息异常", e);
			msg = "删除账单当月罚息异常";
		}
		return msg;
	}

	@RequestMapping("/operateDeleteAllPenaltyInterest")
	@ResponseBody
	public String operateDeleteAllPenaltyInterest(BillRespDto data) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		String operator = onlineUser.getUser().getRealName();
		logger.info(operator + "执行操作【删除账单所有罚息】，账单id：" + data.getBillId());
		String msg = "";
		if (StringUtil.isBlank(data.getBillId())) {
			logger.info("账单id不能为空");
			msg = "账单id不能为空";
			return msg;
		}
		try {
			String sql = "delete from erp_bill_penalty_interest where billid = ?";
			baseDao.executeSqlUpdte(sql, new Object[] { data.getBillId() }, new Type[] { StandardBasicTypes.STRING });
			msg = RESULT_SUCCESS;
			logger.info(msg);
		} catch (Exception e) {
			logger.error("删除账单所有罚息异常", e);
			msg = "删除账单所有罚息异常";
		}
		return msg;
	}
}
