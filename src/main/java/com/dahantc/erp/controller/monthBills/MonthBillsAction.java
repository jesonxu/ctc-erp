package com.dahantc.erp.controller.monthBills;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.monthBills.MonthBillsDto;
import com.dahantc.erp.dto.monthBills.MonthBillsDto.Receive;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.billpenaltyinterest.service.IBillPenaltyInterestService;
import com.dahantc.erp.vo.billpenaltyinterest.service.impl.BillPenaltyInterestServiceImpl.PenaltyInterest;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.productBills.entity.FsExpenseincomeInfo;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;

@Controller
@RequestMapping("/monthBills")
public class MonthBillsAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(MonthBillsAction.class);

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IBillPenaltyInterestService billPenaltyInterestService;

	@ResponseBody
	@PostMapping("/getMonthBills")
	public BaseResponse<Object> getMonthBills(@RequestParam(required = false) String customerId, @RequestParam(required = false) String productId,
			@RequestParam(required = false) String queryDate) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}
			long _start = System.currentTimeMillis();
			List<MonthBillsDto> resultList = new ArrayList<>();
			List<CustomerProduct> productList = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) {
				CustomerProduct product = customerProductService.read(productId);
				if (product != null) {
					productList.add(product);
					customerId = product.getCustomerId();
				}
			} else {
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				productList = customerProductService.queryAllByFilter(filter);
			}
			if (CollectionUtils.isEmpty(productList)) {
				return BaseResponse.error("未查询到数据");
			}
			Map<String, CustomerProduct> cacheProductMap = productList.stream().collect(Collectors.toMap(CustomerProduct::getProductId, product -> product));
			List<String> productIdList = productList.stream().map(CustomerProduct::getProductId).collect(Collectors.toList());
			Customer customer = customerService.read(customerId);
			Date monthDate = DateUtil.getLastMonthFirst();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			filter.getRules().clear();
			filter.getRules().add(new SearchRule("productId", Constants.ROP_IN, productIdList));
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, monthDate));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.getNextMonthFirst(monthDate)));
			filter.getOrders().add(new SearchOrder("finalReceiveTime", Constants.ROP_ASC));
			filter.getOrders().add(new SearchOrder("id", Constants.ROP_DESC));
			List<ProductBills> productBillsList = productBillsService.queryAllBySearchFilter(filter);

			List<FsExpenseIncome> incomeList = queryAllIncomeInfo(customer);

			if (!CollectionUtils.isEmpty(productBillsList)) {
				for (ProductBills productBills : productBillsList) {
					MonthBillsDto dto = buildMonthBills(productBills, cacheProductMap.get(productBills.getProductId()), incomeList, customer);
					if (dto != null) {
						resultList.add(dto);
					}
				}
			}
			logger.info("查询利润提成耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");

	}

	private MonthBillsDto buildMonthBills(ProductBills productBills, CustomerProduct customerProduct, List<FsExpenseIncome> incomeList, Customer customer)
			throws Exception {
		MonthBillsDto dto = new MonthBillsDto();
		dto.setReceivables(productBills.getReceivables());
		dto.setBillsDate(DateUtil.convert(productBills.getWtime(), DateUtil.format1));
		dto.setRemainReceive(BigDecimal.ZERO);
		dto.setProductName(customerProduct.getProductName());
		dto.setBillsId(productBills.getId());
		dto.setBillsName(Constants.buildProductBillTitle(customer.getCompanyName(), customerProduct.getProductName(),
				DateUtil.convert(productBills.getWtime(), DateUtil.format4)));
		dto.setBillsNumber(productBillsService.getBillsNumber(productBills));
		Date billsTime = productBills.getWtime();
		dto.setBillsTime(DateUtil.convert(billsTime, DateUtil.format1));

		// 最后收款时间
		Date finalReceiveTime = productBills.getFinalReceiveTime();
		if (finalReceiveTime != null) {
			dto.setFinalReceiveTime(DateUtil.convert(finalReceiveTime, DateUtil.format1));
		}

		// 销账时间
		Date writeOffTime = productBills.getWriteOffTime();
		if (writeOffTime != null) {
			dto.setWriteOffTime(DateUtil.convert(writeOffTime, DateUtil.format1));
		}

		// 账单状态
		dto.setBillStatus(BillStatus.values()[productBills.getBillStatus()].getDesc());

		PenaltyInterest penaltyInterest = billPenaltyInterestService.queryPenaltyInterestByBillId(productBills.getId());
		dto.setPenaltyInterestDays(penaltyInterest.getPenaltyInterestDays());
		dto.setPenaltyInterest(penaltyInterest.getPenaltyInterest());

		if (productBills.getBillStatus() == BillStatus.WRITED_OFF.ordinal() || productBills.getBillStatus() == BillStatus.WRITING_OFF.ordinal()) {
			List<FsExpenseincomeInfo> infoList = productBills.getFsExpenseIncomeInfos();
			if (!CollectionUtils.isEmpty(infoList)) {
				Map<String, FsExpenseincomeInfo> relateInfoMap = new HashMap<>();
				List<String> fsExpenseIncomeIdList = infoList.stream().map(info -> {
					relateInfoMap.put(info.getFsExpenseIncomeId(), info);
					return info.getFsExpenseIncomeId();
				}).filter(StringUtils::isNotBlank).collect(Collectors.toList());
				getWritedOffIncomeInfo(dto, fsExpenseIncomeIdList, relateInfoMap);
			} else {
				logger.info("关联收支信息缺失，productBillsId：" + productBills.getProductId());
			}
		} else {
			if (productBills.getActualReceivables().signum() > 0) {
				dto.getReceiveInfo().add(new Receive("", productBills.getActualReceivables()));
			}
			dto.setRemainReceive(productBills.getReceivables().subtract(productBills.getActualReceivables()));
		}
		return dto;
	}

	private void getWritedOffIncomeInfo(MonthBillsDto dto, List<String> fsExpenseIncomeIdList, Map<String, FsExpenseincomeInfo> relateInfoMap)
			throws Exception {
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("id", Constants.ROP_IN, fsExpenseIncomeIdList));
		filter.getOrders().add(new SearchOrder("operateTime", Constants.ROP_ASC));
		List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
		if (!CollectionUtils.isEmpty(incomeList)) {
			for (FsExpenseIncome fsExpenseIncome : incomeList) {
				FsExpenseincomeInfo info = relateInfoMap.get(fsExpenseIncome.getId());
				dto.getReceiveInfo().add(new Receive(DateUtil.convert(fsExpenseIncome.getOperateTime(), DateUtil.format1), info.getThisCostNumber()));
			}
		}
		dto.setRemainReceive(BigDecimal.ZERO);
	}

	private List<FsExpenseIncome> queryAllIncomeInfo(Customer customer) throws Exception {
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, customer.getCustomerId()));
		filter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, InvoiceType.OtherInvoice.ordinal()));
		List<InvoiceInformation> infoList = invoiceInformationService.queryAllBySearchFilter(filter);
		if (!CollectionUtils.isEmpty(infoList)) {
			List<String> companyNameList = infoList.stream().map(InvoiceInformation::getCompanyName).collect(Collectors.toList());
			if (companyNameList == null) {
				companyNameList = new ArrayList<>();
			}
			companyNameList.add(customer.getCompanyName());
			filter.getRules().clear();
			filter.getRules().add(new SearchRule("depict", Constants.ROP_IN, companyNameList));
			filter.getRules().add(new SearchRule("remainRelatedCost", Constants.ROP_GT, BigDecimal.ZERO));
			filter.getRules().add(new SearchRule("isIncome", Constants.ROP_EQ, 0));
			return fsExpenseIncomeService.queryAllBySearchFilter(filter);
		}
		return null;
	}

	@RequestMapping("/toMonthBillsSheet")
	public String toCashFlowSheet() {
		JSONObject params = new JSONObject();
		params.put("deptIds", request.getParameter("deptIds"));
		params.put("sale_open_customer_type_id", request.getParameter("sale_open_customer_type_id"));
		params.put("sale_customer_id", request.getParameter("sale_customer_id"));
		params.put("sale_customer_opts", request.getParameter("sale_customer_opts"));
		params.put("sale_product_id", request.getParameter("sale_product_id"));
		params.put("sale_product_settle_type", request.getParameter("sale_product_settle_type"));
		params.put("sale_operate_year", request.getParameter("sale_operate_year"));
		params.put("sale_operate_month", request.getParameter("sale_operate_month"));
		params.put("sale_settlement_year", request.getParameter("sale_settlement_year"));
		params.put("sale_settlement_month", request.getParameter("sale_settlement_month"));
		params.put("sale_statistic_year", request.getParameter("sale_statistic_year"));
		params.put("sale_open_dept_id", request.getParameter("sale_open_dept_id"));
		params.put("sale_open_sub_dept_id", request.getParameter("sale_open_sub_dept_id"));
		params.put("sale_open_customer_type_name", request.getParameter("sale_open_customer_type_name"));
		params.put("customerKeyWord", request.getParameter("customerKeyWord"));
		request.setAttribute("params", params);
		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("title", request.getParameter("title"));
		return "/views/salesheet/monthBillsSheet";
	}
}
