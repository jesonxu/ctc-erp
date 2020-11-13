package com.dahantc.erp.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.dto.bill.DataAnalysisReqDto;
import com.dahantc.erp.dto.bill.ReqData;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.HttpUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

@Component("acquireAccountReportDetailHandler")
public class AcquireAccountReportDetailHandler {
	private final Logger logger = LogManager.getLogger(AcquireAccountReportDetailHandler.class);

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IProductTypeService productTypeService;

	public boolean doAcquireReportDetail(String flowEntId) {
		boolean result = false;
		try {
			FlowEnt flowEnt = flowEntService.read(flowEntId);
			if (flowEnt == null) {
				logger.info("流程不存在，flowEntId：" + flowEntId);
				return result;
			}
			return doAcquireReportDetail(flowEnt);
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	public boolean doAcquireReportDetail(FlowEnt flowEnt) {
		boolean result = false;
		try {
			String reqUrl = parameterService.getSysParam(Constants.ACQUIRE_REPORT_DETAIL_URL);
			if (StringUtils.isBlank(reqUrl)) {
				logger.info("未获取到获取账号详细报告的URL，请配置：acquire_report_detail_url");
				return result;
			}
			String callbackUrl = parameterService.getSysParam(Constants.REPORT_DETAIL_CALL_BACK_URL);
			if (StringUtils.isBlank(callbackUrl)) {
				logger.info("未获取到报告回调的URL，请配置：report_detail_call_back_url");
				return result;
			}
			String token = parameterService.getSysParam(Constants.ACQUIRE_REPORT_DETAIL_TOKEN);
			if (StringUtils.isBlank(token)) {
				logger.info("未获取到报告请求TOKEN，请配置：acquire_report_detail_token");
				return result;
			}
			// 获取账单信息
			List<ProductBills> bills = getBillIdsByFlowEnt(flowEnt);
			if (!CollectionUtils.isEmpty(bills)) {
				// 获取产品信息
				Set<String> productIds = bills.stream().map(ProductBills::getProductId).collect(Collectors.toSet());
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, new ArrayList<>(productIds)));
				List<CustomerProduct> products = customerProductService.queryAllByFilter(searchFilter);
				if (CollectionUtils.isEmpty(products)) {
					logger.info("产品不存在，productId：" + String.join(",", productIds));
					return false;
				}
				// 生成Map
				Map<String, CustomerProduct> prodcutsMap = products.stream().collect(Collectors.toMap(CustomerProduct::getProductId, product -> product));
				// 按照产品分组
				Map<String, List<ProductBills>> productGroup = bills.stream().collect(Collectors.groupingBy(ProductBills::getProductId));
				JSONObject reqParam = getReqParams(flowEnt, callbackUrl, token, productGroup, prodcutsMap);
				String reqResult = HttpUtil.postMethod(reqUrl, reqParam);
				logger.info("流程：" + flowEnt.getFlowTitle() + "，URL：" + reqUrl + "参数：" + reqParam + "，请求获取账号详细报告完成，响应结果：" + reqResult);
			} else {
				logger.info("无账单id，flowEntId：" + flowEnt.getId());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	private JSONObject getReqParams(FlowEnt flowEnt, String callbackUrl, String token, Map<String, List<ProductBills>> productGroup,
			Map<String, CustomerProduct> prodcutsMap) {
		DataAnalysisReqDto dto = new DataAnalysisReqDto();
		dto.setTaskId(flowEnt.getId());
		dto.setCallback(callbackUrl);
		dto.setToken(token);
		List<ReqData> reqDataList = new ArrayList<>();
		dto.setData(reqDataList);
		productGroup.forEach((productId, bills) -> {
			ReqData reqData = new ReqData();
			if (CollectionUtils.isEmpty(bills)) {
				logger.info("无账单，productId：" + productId);
				return;
			}
			CustomerProduct product = prodcutsMap.get(productId);
			if (product == null) {
				logger.info("产品不存在");
				return;
			}
			if (StringUtils.isBlank(product.getAccount())) {
				logger.info("产品无账号，productId：" + productId);
				return;
			}
			reqData.setProduct(converType(product.getProductType()));
			reqData.setAccount(product.getAccount().split("\\|"));
			reqData.setMonth(bills.stream().map(bill -> DateUtil.convert(bill.getWtime(), DateUtil.format4)).toArray(String[]::new));
			reqDataList.add(reqData);
		});
		return (JSONObject) JSON.toJSON(dto);
	}

	/**
	 * 从对账流程中获取本次对账的账单
	 * 
	 * @param flowEnt
	 *            对账流程id
	 * @return
	 */
	private List<ProductBills> getBillIdsByFlowEnt(FlowEnt flowEnt) {
		try {
			if (flowEnt != null && StringUtils.isNotBlank(flowEnt.getFlowMsg())) {
				JSONObject flowMsgJson = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
				if (flowMsgJson.get(Constants.UNCHECKED_BILL_KEY) != null && StringUtils.isNotBlank(flowMsgJson.get(Constants.UNCHECKED_BILL_KEY).toString())) {
					List<String> productBillsIds = new ArrayList<>();
					JSONObject labelValue = JSON.parseObject(flowMsgJson.getString(Constants.UNCHECKED_BILL_KEY));
					JSONArray billInfos = JSONObject.parseArray(labelValue.getString("billInfos"));
					billInfos.forEach(obj -> productBillsIds.add(((JSONObject) obj).getString("id")));
					if (!CollectionUtils.isEmpty(productBillsIds)) {
						SearchFilter filter = new SearchFilter();
						filter.getRules().add(new SearchRule("id", Constants.ROP_IN, productBillsIds));
						return productBillsService.queryAllBySearchFilter(filter);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	private int converType(int productType) {
		String key = productTypeService.getProductTypeKeyByValue(productType);
		if (Constants.PRODUCT_TYPE_KEY_SMS.equals(key)) {
			return 0;
		} else if (Constants.PRODUCT_TYPE_KEY_MMS.equals(key)) {
			return 1;
		} else if (Constants.PRODUCT_TYPE_KEY_SUPER_MMS.equals(key)) {
			return 2;
		} else if (Constants.PRODUCT_TYPE_KEY_INTER_SMS.equals(key)) {
			return 3;
		} else if (Constants.PRODUCT_TYPE_KEY_VOICE_TIME.equals(key)) {
			return 4;
		}
		return -1;
	}

	/**
	 * 根据账单请求生成数据分析报告
	 * 
	 * @param billList
	 * @return
	 */
	public boolean doAcquireReportDetail(List<ProductBills> billList, String taskId) {
		boolean result = false;
		try {
			String reqUrl = parameterService.getSysParam(Constants.ACQUIRE_REPORT_DETAIL_URL);
			if (StringUtils.isBlank(reqUrl)) {
				logger.info("未获取到获取账号详细报告的URL，请配置：acquire_report_detail_url");
				return result;
			}
			String callbackUrl = parameterService.getSysParam(Constants.REPORT_DETAIL_CALL_BACK_URL_TEMP);
			if (StringUtils.isBlank(callbackUrl)) {
				logger.info("未获取到报告回调的URL，请配置：report_detail_call_back_url");
				return result;
			}
			String token = parameterService.getSysParam(Constants.ACQUIRE_REPORT_DETAIL_TOKEN);
			if (StringUtils.isBlank(token)) {
				logger.info("未获取到报告请求TOKEN，请配置：acquire_report_detail_token");
				return result;
			}
			// 获取账单信息
			if (!CollectionUtils.isEmpty(billList)) {
				// 获取产品信息
				Set<String> productIds = billList.stream().map(ProductBills::getProductId).collect(Collectors.toSet());
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, new ArrayList<>(productIds)));
				List<CustomerProduct> products = customerProductService.queryAllByFilter(searchFilter);
				if (CollectionUtils.isEmpty(products)) {
					logger.info("产品不存在，productId：" + String.join(",", productIds));
					return false;
				}
				Customer customer = customerService.read(products.get(0).getCustomerId());
				if (null == customer) {
					logger.info("客户不存在，customerId：" + products.get(0).getCustomerId());
					return false;
				}
				// 生成Map
				Map<String, CustomerProduct> prodcutsMap = products.stream().collect(Collectors.toMap(CustomerProduct::getProductId, product -> product));
				// 按照产品分组
				Map<String, List<ProductBills>> productGroup = billList.stream().collect(Collectors.groupingBy(ProductBills::getProductId));
				JSONObject reqParam = getReqParams(taskId, callbackUrl, token, productGroup, prodcutsMap);
				String reqResult = HttpUtil.postMethod(reqUrl, reqParam);
				logger.info("客户：" + customer.getCompanyName() + "，URL：" + reqUrl + "参数：" + reqParam + "，请求获取账号详细报告完成，响应结果：" + reqResult);
				JSONObject resp = JSON.parseObject(reqResult);
				if (resp.containsKey("result") && StringUtils.equals(resp.getString("result"), "success")) {
					return true;
				}
			} else {
				logger.info("请求生成数据分析报告的账单列表为空");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	private JSONObject getReqParams(String taskId, String callbackUrl, String token, Map<String, List<ProductBills>> productGroup,
			Map<String, CustomerProduct> prodcutsMap) {
		DataAnalysisReqDto dto = new DataAnalysisReqDto();
		dto.setTaskId(taskId);
		dto.setCallback(callbackUrl);
		dto.setToken(token);
		List<ReqData> reqDataList = new ArrayList<>();
		dto.setData(reqDataList);
		productGroup.forEach((productId, bills) -> {
			ReqData reqData = new ReqData();
			if (CollectionUtils.isEmpty(bills)) {
				logger.info("无账单，productId：" + productId);
				return;
			}
			CustomerProduct product = prodcutsMap.get(productId);
			if (product == null) {
				logger.info("产品不存在");
				return;
			}
			if (StringUtils.isBlank(product.getAccount())) {
				logger.info("产品无账号，productId：" + productId);
				return;
			}
			reqData.setProduct(converType(product.getProductType()));
			reqData.setAccount(product.getAccount().split("\\|"));
			reqData.setMonth(bills.stream().map(bill -> DateUtil.convert(bill.getWtime(), DateUtil.format4)).toArray(String[]::new));
			reqDataList.add(reqData);
		});
		return (JSONObject) JSON.toJSON(dto);
	}
}
