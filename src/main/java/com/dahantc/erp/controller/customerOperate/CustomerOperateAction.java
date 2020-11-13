package com.dahantc.erp.controller.customerOperate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customerOperate.BuildBillFlowReqDto;
import com.dahantc.erp.dto.customerOperate.ToApplyFlowRespDto;
import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.dto.operate.InvoiceDto;
import com.dahantc.erp.dto.operate.ProductBillsDto;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerOperate.service.ICustomerOperateService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.invoice.entity.Invoice;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.invoice.service.IInvoiceService;
import com.dahantc.erp.vo.operate.service.OperateService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;

@Controller
@RequestMapping("/customerOperate")
public class CustomerOperateAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(CustomerOperateAction.class);

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private ICustomerOperateService customerOperateService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IInvoiceService invoiceService;

	@Autowired
	private OperateService operateService;

	/**
	 * 根据产品id查询运营流程数量和时间标题 有哪些年、月
	 *
	 * @param type
	 * @param productId
	 * @return
	 */
	@RequestMapping("/queryOperate")
	public String queryOperate(int type, String productId) {
		logger.info("查询客户运营时间标题和未处理流程数开始，productId:" + productId);
		// 从产品创建时间到现在的年月集合
		Map<String, List<ToQueryMonthRespDto>> timeMap = new LinkedHashMap<>();
		// 是否没有流程要展示
		boolean empty = true;
		// 是否显示发起按钮
		boolean buttonBody = false;
		try {
			if (StringUtils.isNotBlank(productId)) {
				// 查询当前用户在每个客户的每个产品等待处理的运营/结算流程数（结果不会为null）
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				// 过滤出指定客户产品的运营流程
				flowCount = flowCount.stream().filter(c -> c.getEntityType() == EntityType.CUSTOMER.ordinal() && c.getFlowType() == FlowType.OPERATE.ordinal()
						&& StringUtils.equals(productId, c.getProductId())).collect(Collectors.toList());
				// 年未处理流程数
				Map<String, Long> yearFlowCount = new HashMap<>();
				Timestamp chargeStart = null;
				buttonBody = true;
				CustomerProduct product = customerProductService.read(productId);
				if (product != null) {
					// 使用客户的创建时间作为运营的开始时间
					Customer customer = customerService.read(product.getCustomerId());
					if (customer != null) {
						chargeStart = customer.getWtime();
					} else {
						chargeStart = product.getWtime();
					}
				} else {
					logger.info("产品id为" + productId + "的产品不存在");
				}
				if (chargeStart != null) {
					// 获取运营月份
					timeMap = DateUtil.getMonthBetweenDate(chargeStart, new Date());
					// debug，打印每个运营月
					if (timeMap != null && !timeMap.isEmpty()) {
						for (Map.Entry<String, List<ToQueryMonthRespDto>> entry : timeMap.entrySet()) {
							logger.info("运营年份" + entry.getKey() + ",月份"
									+ entry.getValue().stream().map(ToQueryMonthRespDto::getMonth).collect(Collectors.toList()));
						}
					}
					if (flowCount != null && !flowCount.isEmpty() && timeMap != null && !timeMap.isEmpty()) {
						// 获取每年的流程数统计数据 {年 -> 数据统计对象}
						Map<Integer, IntSummaryStatistics> map = flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
						// 将 每年数据统计数据中的总数 放到 年未处理流程数Map，得到 {年 -> 当年未处理流程总数}
						map.entrySet().stream().forEachOrdered(entry -> yearFlowCount.put(entry.getKey() + "", entry.getValue().getSum()));
						// 每年的集合 [年 -> [每个月]]
						Set<Map.Entry<String, List<ToQueryMonthRespDto>>> set = timeMap.entrySet();
						// 遍历每年
						for (Map.Entry<String, List<ToQueryMonthRespDto>> entry : set) {
							// 当年有未处理流程
							if (yearFlowCount.get(entry.getKey()) != null && yearFlowCount.get(entry.getKey()) > 0) {
								// 从 所有流程 中过滤出 当前年的流程，再获得每个月的未处理流程数 {每个月 ->
								// 当月未处理流程数}
								Map<Integer, Integer> monthFlowCount = flowCount.stream().filter(c -> StringUtils.equals(c.getYear() + "", entry.getKey()))
										.collect(Collectors.toMap(FlowEntDealCount::getMonth, FlowEntDealCount::getFlowEntCount));
								// 把 每个月的未处理流程数 放到 每月里
								List<ToQueryMonthRespDto> dtoList = entry.getValue().stream().map(dto -> {
									if (dto == null) {
										return null;
									}
									Integer value = monthFlowCount.get(dto.getMonth());
									if (value != null) {
										dto.setFlowEntCount(value.longValue());
									} else {
										dto.setFlowEntCount(0l);
									}
									return dto;
								}).collect(Collectors.toList());
								timeMap.replace(entry.getKey(), dtoList);
							}
						}
					}
				}
				if (timeMap != null && !timeMap.isEmpty()) {
					empty = false;
					request.setAttribute("timeMap", timeMap);
				}
				request.setAttribute("yearFlowCount", yearFlowCount);
			}
			if (99 != type) {
				buttonBody = true;
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("empty", empty);
			request.setAttribute("buttonBody", buttonBody);
		} catch (ServiceException e) {
			logger.info("查询客户运营时间标题和未处理流程数异常", e);
			return "";
		}
		return "/views/customerOperate/customerOperateTemplate";
	}

	// 申请流程方法
	@PostMapping("/applyFlow")
	@ResponseBody
	public BaseResponse<String> applyFlow(@RequestBody @Valid ApplyProcessReqDto reqDto) {
		return customerOperateService.applyProcess(reqDto, getOnlineUserAndOnther());
	}

	// 跳转选择账单页面
	@RequestMapping("/toQueryAccount")
	public String toQueryAccount(@RequestParam String productId, @RequestParam String flowClass) {
		// 根据productId查产品
		try {
			if (StringUtils.isBlank(productId)) {
				logger.error("产品id不能为空");
				return "";
			}
			CustomerProduct product = customerProductService.read(productId);
			if (product == null) {
				logger.error("id为" + productId + "产品不存在");
				return "";
			}
			ToApplyFlowRespDto dto = new ToApplyFlowRespDto();
			String customerId = product.getCustomerId();
			// 根据customerId查询客户
			Customer customer = customerService.read(customerId);
			if (customer != null) {
				dto.setCustomerId(customer.getCustomerId());
				dto.setCustomerName(customer.getCompanyName());
			}
			dto.setProductId(product.getProductId());
			dto.setProductName(product.getProductName());
			dto.setProductTypeInt(product.getProductType());
			request.setAttribute("dto", dto);
			request.setAttribute("flowClass", flowClass);
		} catch (Exception e) {
			logger.error("", e);
			return "";
		}
		return "/views/customerOperate/queryAccount";
	}

	private List<ProductBills> getProductBillsDtos(String productId, String flowClass, String needOrder, Customer customer) throws ServiceException {
		CustomerProduct product = customerProductService.read(productId);
		if (product == null) {
			logger.error("id为" + productId + "产品不存在");
			return null;
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, product.getCustomerId()));
		Customer _customer = customerService.queryAllBySearchFilter(filter).get(0);
		BeanUtils.copyProperties(_customer, customer);

		String hql = "select t From ProductBills t WHERE t.entityId = :entityId and billStatus = " + BillStatus.RECONILED.ordinal();
		if (Constants.BILL_PAYMENT_FLOW_CLASS.equals(flowClass)) { // 账单流程，只显示未付清的账单
			hql += " and t.payables > t.actualPayables";
		} else if (Constants.REMUNERATION_FLOW_CLASS.equals(flowClass)) { // 酬金流程，只显示未收完的账单
			hql += " and t.receivables > t.actualReceivables";
		} else if (Constants.BILL_RECEIVABLES_FLOW_CLASS.equals(flowClass)) { // 销售收款流程，只显示未收完的账单
			hql += " and t.receivables > t.actualReceivables";
		} else if (Constants.INVOICE_CLASS.equals(flowClass)) { // 发票流程，显示未开完的账单
			hql += " and t.receivables > t.actualInvoiceAmount";
		}
		if (StringUtils.equals(needOrder, "T")) {
			hql += " order by wtime asc";
		}

		Map<String, Object> params = new HashMap<>();
		params.put("entityId", product.getCustomerId());
		return productBillsService.findByhql(hql, params, 0);
	}

	// 查询客户下所有产品的账单记录
	@PostMapping("/readProductBills")
	@ResponseBody
	public BaseResponse<List<ProductBillsDto>> readProductBills(@RequestParam String productId, @RequestParam String flowClass,
			@RequestParam(required = false) String needOrder) {
		List<ProductBillsDto> dtos = new ArrayList<ProductBillsDto>();
		Customer customer = new Customer();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			List<ProductBills> bills = getProductBillsDtos(productId, flowClass, needOrder, customer);
			if (bills != null && !bills.isEmpty()) {
				for (ProductBills bill : bills) {
					ProductBillsDto dto = new ProductBillsDto();
					dto.setId(bill.getId());
					dto.setPayables(bill.getPayables());
					dto.setActualpayables(bill.getActualPayables());
					dto.setActualReceivables(bill.getActualReceivables());
					dto.setReceivables(bill.getReceivables());
					dto.setActualInvoiceAmount(bill.getActualInvoiceAmount());

					Date date = bill.getWtime();
					String dateString = formatter.format(date);
					CustomerProduct temp = customerProductService.read(bill.getProductId());

					dto.setTitle("账单-" + dateString + "-" + customer.getCompanyName() + "-" + temp.getProductName());
					dtos.add(dto);
				}
			}
		} catch (ServiceException e) {
			logger.info("查询产品账单异常", e);
		}
		return BaseResponse.success(dtos);
	}

	/**
	 * 查询客户下所有产品的未开完票的账单，要减去正在走开票流程的开票金额
	 * 
	 * @param customerId
	 *            客户id
	 * @param flowClass
	 *            流程类别
	 * @param flowId
	 *            要检查的流程id
	 * @param flowEntId
	 *            要排除的流程实体id
	 * @param needOrder
	 *            是否需要按时间排序
	 * @return
	 */
	@PostMapping("/readInvoiceableBills")
	@ResponseBody
	public BaseResponse<List<ProductBillsDto>> readInvoiceableBills(@RequestParam String customerId, @RequestParam String flowClass,
			@RequestParam String flowId, @RequestParam(required = false) String flowEntId, @RequestParam(required = false) String needOrder) {
		List<ProductBillsDto> dtos = new ArrayList<ProductBillsDto>();
		try {
			Customer customer = customerService.read(customerId);
			if (null == customer) {
				logger.info("按客户id找不到对应的客户：" + customerId);
				return BaseResponse.success(dtos);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			// 先查出所有未开完票的账单
			List<ProductBills> bills = productBillsService.getTodoBills(EntityType.CUSTOMER.getCode(), customerId, flowClass, needOrder);
			if (!CollectionUtils.isEmpty(bills)) {
				for (ProductBills bill : bills) {
					// 被占用的开票金额，账单的本次开票是放在json的“thisReceivables”字段中（operateInfo.js
					// getBillInvoiceInfo()）
					String usedAmountStr = operateService.queryApplying(flowId, bill.getId(), Constants.BILL_THIS_RECEIVABLES_KEY, flowEntId).getMsg();
					BigDecimal usedAmount = new BigDecimal(usedAmountStr);
					// 应开金额 - 已开金额 - 未走完的开票金额 > 0 的账单才能继续开票
					if (bill.getReceivables().subtract(bill.getActualInvoiceAmount()).subtract(usedAmount).compareTo(BigDecimal.ZERO) <= 0) {
						logger.info("账单id：" + bill.getId() + "，账单编号：" + bill.getBillNumber() + "，应开金额-已开金额-未走完的开票金额 <= 0，跳过本账单");
						continue;
					}
					ProductBillsDto dto = new ProductBillsDto();
					dto.setId(bill.getId());
					dto.setPayables(bill.getPayables());
					dto.setActualpayables(bill.getActualPayables());
					dto.setActualReceivables(bill.getActualReceivables());
					dto.setReceivables(bill.getReceivables());
					dto.setActualInvoiceAmount(bill.getActualInvoiceAmount());
					dto.setUsedAmount(usedAmount);
					Date date = bill.getWtime();
					String dateString = formatter.format(date);
					CustomerProduct temp = customerProductService.read(bill.getProductId());

					dto.setTitle("账单-" + dateString + "-" + customer.getCompanyName() + "-" + temp.getProductName());
					dtos.add(dto);
				}
			}
		} catch (ServiceException e) {
			logger.info("查询产品账单异常", e);
		}
		return BaseResponse.success(dtos);
	}

	/**
	 * 根据客户ID查询所有运营流程数量和时间标题
	 *
	 * @param customerId
	 *            客户id
	 * @return
	 */
	@RequestMapping("/queryAllOperate")
	public String queryAllOperate(String customerId) {
		logger.info("查询客户运营的时间和数量开始，客户id：" + customerId);
		long startTimeMills = System.currentTimeMillis();
		// 存放年-月份的流程数据
		Map<String, List<ToQueryMonthRespDto>> timeMap = new LinkedHashMap<>();
		boolean empty = true;
		boolean buttonBody = true;
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			Timestamp startTime = null; // 运营的最早时间
			// 有客户id，查询客户，获取客户的产品的最早开始时间
			if (StringUtils.isNotBlank(customerId)) {
				Customer customer = customerService.read(customerId);
				if (customer != null) {
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
					searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
					List<CustomerProduct> resultList = customerProductService.queryAllByFilter(searchFilter);
					if (resultList != null && resultList.size() > 0) {
						startTime = resultList.get(0).getWtime();
					} else {
						startTime = customer.getWtime();
					}
				}
			} else { // 没有客户id，获取能看到的所有流程，得到流程的最早时间
				// List<FlowEnt> flowEnts = flowEntService.queryFlowEntByDate(onlineUser,
				// EntityType.CUSTOMER.ordinal(), FlowType.OPERATE.ordinal(), "", "", "");
				Role role = roleService.read(onlineUser.getRoleId());
				FlowEnt flowEnt = flowEntService.queryEarliestFlowEntByRole(onlineUser.getUser(), role, EntityType.CUSTOMER.ordinal(),
						FlowType.OPERATE.ordinal());
				if (flowEnt != null) {
					startTime = flowEnt.getWtime();
				}
			}

			if (startTime != null) {
				// 获取从开始时间到现在的年每个月集合
				timeMap = DateUtil.getMonthBetweenDate(startTime, new Date());
				// 获取当前用户角色的所有未处理流程
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(onlineUser.getRoleId(), onlineUser.getUser().getOssUserId());
				// 获取当前用户的所有客户或指定客户
				List<Customer> customers = customerService.readCustomers(onlineUser, "", customerId, "", "");
				List<String> customerIds = new ArrayList<String>();
				if (customers != null && !customers.isEmpty()) {
					customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				}
				if (flowCount != null && !flowCount.isEmpty()) {
					// 过滤出属于用户下的客户的流程
					List<String> finalCustomerIds = customerIds;
					flowCount = flowCount.stream().filter(flow -> flow.getFlowType() == FlowType.OPERATE.ordinal()
							&& flow.getEntityType() == EntityType.CUSTOMER.ordinal() && finalCustomerIds.contains(flow.getSupplierId()))
							.collect(Collectors.toList());
					// 年-年未处理流程数
					Map<String, Long> yearFlowCount = new HashMap<>();
					if (timeMap != null && !timeMap.isEmpty()) {
						// 统计年-流程数量
						flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)))
								.forEach((year, yearCount) -> yearFlowCount.put(year + "", yearCount.getSum()));
						// 统计时间（月份-流程数量）
						Set<Map.Entry<String, List<ToQueryMonthRespDto>>> yearMonthTime = timeMap.entrySet();
						for (Map.Entry<String, List<ToQueryMonthRespDto>> yearMonthInfo : yearMonthTime) {
							// 年份
							String year = yearMonthInfo.getKey();
							// 年未处理流程数
							Long yearCount = yearFlowCount.get(year);
							if (yearCount != null && yearCount > 0) {
								Map<Integer, IntSummaryStatistics> monthFlowCount = flowCount.stream()
										// 当前年份的流程-未处理数
										.filter(dealCount -> StringUtils.equals(dealCount.getYear() + "", year))
										// 统计对应年-月份 未处理流程数量
										.collect(Collectors.groupingBy(FlowEntDealCount::getMonth,
												Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
								// 年-月 流程信息
								List<ToQueryMonthRespDto> yearMonthCount = yearMonthInfo.getValue().stream().map(monthInfo -> {
									if (monthInfo == null) {
										return null;
									}
									// 月流程数
									IntSummaryStatistics monthCount = monthFlowCount.get(monthInfo.getMonth());
									if (monthCount != null) {
										monthInfo.setFlowEntCount(monthCount.getSum());
									} else {
										monthInfo.setFlowEntCount(0L);
									}
									return monthInfo;
								}).collect(Collectors.toList());
								timeMap.replace(year, yearMonthCount);
							}
						}
					}
					request.setAttribute("yearFlowCount", yearFlowCount);
				}
				if (timeMap != null && !timeMap.isEmpty()) {
					empty = false;
					request.setAttribute("timeMap", timeMap);
				}
			}

			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("empty", empty);
			request.setAttribute("buttonBody", buttonBody);
		} catch (ServiceException e) {
			logger.info("查询客户运营的时间和数量异常", e);
			return "";
		}
		logger.info("查询客户运营的时间和数量结束，耗时：" + (System.currentTimeMillis() - startTimeMills));
		return "/views/customerOperate/customerOperateTemplate";
	}

	@RequestMapping("/getInvoice")
	@ResponseBody
	public BaseResponse<JSONArray> getInvoice(@RequestParam int type, @RequestParam String supplierId) {
		JSONArray jsonArray = new JSONArray();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			if (InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.SelfInvoice.ordinal()) {
				filter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, InvoiceType.SelfInvoice.ordinal()));
			} else if (InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.OtherInvoice.ordinal()) {
				filter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, InvoiceType.OtherInvoice.ordinal()));
				if (StringUtils.isBlank(supplierId)) {
					return BaseResponse.success(jsonArray);
				} else {
					filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, supplierId));
				}
			}
			List<Object> jsonList = null;
			if (InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.SelfInvoice.ordinal()
					|| InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.OtherInvoice.ordinal()) {
				List<InvoiceInformation> list = invoiceInformationService.queryAllBySearchFilter(filter);
				jsonList = list.stream().map(info -> {
					JSONObject json = new JSONObject();
					json.put("invoiceId", info.getInvoiceId());
					json.put("companyName", info.getCompanyName());
					return json;
				}).collect(Collectors.toList());
			} else if (InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.SelfBank.ordinal()
					|| InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.OtherBank.ordinal()) {
				List<BankAccount> list = bankAccountService.queryAllBySearchFilter(filter);
				jsonList = list.stream().map(account -> {
					JSONObject json = new JSONObject();
					String accountBank = account.getAccountBank();
					String bankAccount = account.getBankAccount();
					json.put("bankAccountId", account.getBankAccountId());
					json.put("accountBanck", accountBank + "[" + bankAccount.substring(bankAccount.length() - 4, bankAccount.length()) + "]");
					return json;
				}).collect(Collectors.toList());
			}
			if (jsonList != null && !jsonList.isEmpty()) {
				return BaseResponse.success(new JSONArray(jsonList));
			}
		} catch (Exception e) {
			logger.error("查询开票信息异常", e);
		}
		return BaseResponse.success(jsonArray);
	}

	// 创建销售账单流程
	@RequestMapping("/buildBillFlow")
	@ResponseBody
	public BaseResponse<String> buildBillFlow(@RequestBody @Valid BuildBillFlowReqDto reqDto) {
		String flowId = null;
		int flowType = 0;
		String viewerRoleId = null;
		try {
			// 查询销售账单流程
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.CUSTOMER_BILL_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
			} else {
				logger.error("系统无" + Constants.CUSTOMER_BILL_FLOW_NAME);
				return BaseResponse.error("系统无" + Constants.CUSTOMER_BILL_FLOW_NAME);
			}
			return customerOperateService.buildBillFlow(reqDto, flowId, flowType, viewerRoleId);
		} catch (Exception e) {
			logger.error("生成'" + Constants.CUSTOMER_BILL_FLOW_NAME + "'异常：", e);
			return BaseResponse.error("生成'" + Constants.CUSTOMER_BILL_FLOW_NAME + "'异常");
		}
	}

	// 跳转选择发票页面
	@RequestMapping("/toQueryInvoice")
	public String toQueryInvoice(@RequestParam String productId, @RequestParam String flowClass) {
		// 根据productId查产品
		try {
			if (StringUtils.isBlank(productId)) {
				logger.error("产品id不能为空");
				return "";
			}
			CustomerProduct product = customerProductService.read(productId);
			if (product == null) {
				logger.error("id为" + productId + "产品不存在");
				return "";
			}
			request.setAttribute("productId", productId);
			request.setAttribute("flowClass", flowClass);
		} catch (Exception e) {
			logger.error("", e);
			return "";
		}
		return "/views/customerOperate/queryInvoice";
	}

	// 查询客户下所有产品的账单记录
	@PostMapping("/readInvoices")
	@ResponseBody
	public BaseResponse<List<InvoiceDto>> readInvoices(@RequestParam String productId, @RequestParam String flowClass) {
		List<InvoiceDto> dtos = new ArrayList<InvoiceDto>();
		try {
			CustomerProduct product = customerProductService.read(productId);

			if (product == null) {
				logger.error("id为" + productId + "产品不存在");
				return BaseResponse.success(new ArrayList<InvoiceDto>());
			}
			Customer customer = customerService.read(product.getCustomerId());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			String hql = "select t From Invoice t  WHERE t.entityId = :entityId";
			if (Constants.BILL_RECEIVABLES_FLOW_CLASS.equals(flowClass)) { // 销售收款流程，只显示未收完的发票
				hql += " and t.receivables > t.actualReceivables";
			}

			Map<String, Object> params = new HashMap<>();
			params.put("entityId", product.getCustomerId());
			List<Invoice> invoices = invoiceService.findByhql(hql, params, 0);

			for (Invoice invoice : invoices) {
				InvoiceDto dto = new InvoiceDto();
				dto.setId(invoice.getId());
				dto.setActualReceivables(invoice.getActualReceivables());
				dto.setReceivables(invoice.getReceivables());
				dto.setEntityType(invoice.getEntityType());
				dto.setEntityId(invoice.getEntityId());
				dto.setProductId(invoice.getProductId());
				dto.setWtime(invoice.getWtime());

				Date date = invoice.getWtime();
				String dateString = formatter.format(date);
				CustomerProduct temp = customerProductService.read(invoice.getProductId());
				dto.setTitle("发票-" + dateString + "-" + customer.getCompanyName() + "-" + temp.getProductName());
				dtos.add(dto);
			}
		} catch (ServiceException e) {
			logger.info("查询发票异常", e);
		}
		return BaseResponse.success(dtos);
	}
}
