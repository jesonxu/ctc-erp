package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.NeedAuto;
import com.dahantc.erp.enums.RelateStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.operateCost.service.IOperateCostService;
import com.dahantc.erp.vo.productBills.entity.OperateCostDetail;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;

@Service("billWriteOffFlowService")
public class BillWriteOffFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(BillWriteOffFlowService.class);
	public static final String FLOW_CLASS = Constants.BILL_WRITE_OFF_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.BILL_WRITE_OFF_FLOW_NAME;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IOperateCostService operateCostService;

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelValue) {
		String result = "";
		try {
			if (erpFlow == null) {
				result = "当前流程不存在";
				return result;
			}
			if (StringUtils.isBlank(labelValue)) {
				result = "流程信息不完整";
				return result;
			}
			JSONObject json = JSON.parseObject(labelValue);
			if (json == null || json.isEmpty()) {
				result = "流程信息不完整";
				return result;
			}
			return verifyInfo(json);
		} catch (Exception e) {
			logger.error("", e);
			result = "流程验证异常";
		}
		return result;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	private String verifyInfo(JSONObject json) throws Exception {
		// 解析账单 和 收款信息
		JSONArray billsInfo = null;
		JSONArray incomeInfo = null;
		Iterator<Entry<String, Object>> jsonIterator = json.entrySet().iterator();
		while (jsonIterator.hasNext()) {
			Object o = jsonIterator.next().getValue();
			if (o != null && o.toString().startsWith("[{")) {
				JSONArray arr = JSON.parseArray(o.toString());
				if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("productbillsid"))) {
					billsInfo = arr;
				} else if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("fsexpenseincomeid"))) {
					incomeInfo = arr;
				}
			}
		}
		BigDecimal billsSum = new BigDecimal(0);
		BigDecimal incomeSum = new BigDecimal(0);
		List<String> productBillsIds = new ArrayList<>();
		List<String> IncomeIds = new ArrayList<>();
		for (Object obj : billsInfo) {
			productBillsIds.add(((JSONObject) obj).getString("productbillsid"));
			billsSum = billsSum.add(new BigDecimal(((JSONObject) obj).get("thiscost").toString()));
		}
		for (Object obj : incomeInfo) {
			IncomeIds.add(((JSONObject) obj).getString("fsexpenseincomeid"));
			incomeSum = incomeSum.add(new BigDecimal(((JSONObject) obj).get("thiscost").toString()));
		}
		if (incomeSum.signum() <= 0 || billsSum.signum() <= 0 || incomeSum.subtract(billsSum).signum() != 0) {
			return "账单合计与收款合计不相等";
		}
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("id", Constants.ROP_IN, productBillsIds));
		List<ProductBills> billsList = productBillsService.queryAllBySearchFilter(filter);
		if (billsList == null || billsList.isEmpty() || billsInfo.size() != billsList.size()) {
			return "账单信息有误";
		}
		for (ProductBills productBills : billsList) {
			for (Object obj : billsInfo) {
				JSONObject billsJson = (JSONObject) obj;
				if (StringUtils.equals(billsJson.getString("productbillsid"), productBills.getId())) {
					if (productBills.getReceivables().subtract(productBills.getActualReceivables())
							.subtract(new BigDecimal(billsJson.get("thiscost").toString())).signum() != 0) {
						return "账单信息有误";
					} else {
						break;
					}
				}
			}
		}
		filter.getRules().clear();
		filter.getRules().add(new SearchRule("id", Constants.ROP_IN, IncomeIds));
		filter.getOrders().add(new SearchOrder("operateTime", Constants.ROP_ASC));
		List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
		if (incomeList == null || incomeList.isEmpty() || incomeList.size() != incomeList.size()) {
			return "收款信息有误";
		}
		List<ProductBills> updateProductBillsList = new ArrayList<>();
		for (FsExpenseIncome income : incomeList) {
			for (Object obj : incomeInfo) {
				JSONObject incomeJson = (JSONObject) obj;
				if (StringUtils.equals(incomeJson.getString("fsexpenseincomeid"), income.getId())) {
					if (income.getRemainRelatedCost().subtract(new BigDecimal(incomeJson.get("thiscost").toString())).signum() >= 0) {
						income.setRemainRelatedCost(income.getRemainRelatedCost().subtract(new BigDecimal(incomeJson.get("thiscost").toString())));
						matchBills(updateProductBillsList, billsList, income, new BigDecimal(incomeJson.get("thiscost").toString()));
					} else {
						return "收款信息有误";
					}
					break;
				}
			}
		}
		baseDao.updateByBatch(updateProductBillsList);
		baseDao.updateByBatch(incomeList);
		logger.info("手动销账流程信息：" + json);
		logger.info("手动销账后账单信息：" + JSON.toJSONString(updateProductBillsList));
		logger.info("手动销账后到款信息：" + JSON.toJSONString(incomeList));
		return "";
	}

	private void matchBills(List<ProductBills> updateProductBillsList, List<ProductBills> billsList, FsExpenseIncome income, BigDecimal thisIncomeCost) {
		Iterator<ProductBills> billsIterator = billsList.iterator();
		while (billsIterator.hasNext()) {
			if (thisIncomeCost.signum() <= 0) {
				return;
			}
			ProductBills bills = billsIterator.next();
			BigDecimal incomeCost = thisIncomeCost;
			thisIncomeCost = thisIncomeCost.subtract(bills.getReceivables().subtract(bills.getActualReceivables()));
			JSONArray jsonArray = new JSONArray();
			if (StringUtils.isNotBlank(bills.getRelatedInfo())) {
				jsonArray = JSON.parseArray(bills.getRelatedInfo());
			}
			if (thisIncomeCost.signum() < 0) {
				jsonArray.add(getRelateJson(income, incomeCost));
				bills.setActualReceivables(bills.getActualReceivables().add(incomeCost));
				bills.setRelatedInfo(jsonArray.toString());
			} else {
				jsonArray.add(getRelateJson(income, bills.getReceivables().subtract(bills.getActualReceivables())));
				bills.setActualReceivables(bills.getReceivables());
				bills.setBillStatus(BillStatus.WRITING_OFF.ordinal());
				bills.setRelatedInfo(jsonArray.toString());
				updateProductBillsList.add(bills);
				billsIterator.remove();
			}
		}
	}

	private JSONObject getRelateJson(FsExpenseIncome income, BigDecimal thisCost) {
		JSONObject relateJson = new JSONObject();
		relateJson.put("fsExpenseIncomeId", income.getId());
		relateJson.put("remain", income.getRemainRelatedCost());
		relateJson.put("thisCost", thisCost);
		relateJson.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
		return relateJson;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		try {
			flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
			JSONObject flowEntJson = JSON.parseObject(flowEnt.getFlowMsg());
			Iterator<Entry<String, Object>> jsonIterator = flowEntJson.entrySet().iterator();
			List<String> productBillsIds = null;
			List<String> fsExpenseIncomeIds = null;
			while (jsonIterator.hasNext()) {
				Object o = jsonIterator.next().getValue();
				if (o != null && o.toString().startsWith("[{")) {
					JSONArray arr = JSON.parseArray(o.toString());
					if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("productbillsid"))) {
						productBillsIds = arr.stream().map(obj -> {
							return ((JSONObject) obj).getString("productbillsid");
						}).collect(Collectors.toList());
					} else if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("fsexpenseincomeid"))) {
						fsExpenseIncomeIds = arr.stream().map(obj -> {
							return ((JSONObject) obj).getString("fsexpenseincomeid");
						}).collect(Collectors.toList());
					}
				}
			}
			if (!CollectionUtils.isEmpty(productBillsIds)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, productBillsIds));
				List<ProductBills> productBillsList = productBillsService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(productBillsList)) {
					productBillsList.forEach(bills -> {
						bills.setBillStatus(BillStatus.WRITED_OFF.ordinal());
						bills.setWriteOffTime(new Timestamp(System.currentTimeMillis()));
						// 保存运营成本到运营成本表
						OperateCostDetail operateCostDetail = bills.obtainOperateCost();
						operateCostService.saveOperateCostByBill(operateCostDetail, bills);
					});
					baseDao.updateByBatch(productBillsList);
				}
			} else {
				logger.info("销账归档未找到有效账单，flowEntId:" + flowEnt.getId());
			}
			if (!CollectionUtils.isEmpty(fsExpenseIncomeIds)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, fsExpenseIncomeIds));
				List<FsExpenseIncome> fsExpenseIncomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(fsExpenseIncomeList)) {
					fsExpenseIncomeList.forEach(income -> {
						income.setCustomerId(flowEnt.getSupplierId());
						income.setRelateStatus(RelateStatus.RELATED.ordinal());
						income.setDeptId(flowEnt.getDeptId());
					});
					baseDao.updateByBatch(fsExpenseIncomeList);
				}
			} else {
				logger.info("销账归档未找到有效到款信息，flowEntId:" + flowEnt.getId());
			}
			return true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return false;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) {
		try {
			if (auditResult == AuditResult.CANCLE.getCode()) {
				canceWriteOff(flowEnt);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	private void canceWriteOff(FlowEnt flowEnt) {
		try {
			logger.info("销账流程撤销开始。。。");
			logger.info("销账流程撤销流程信息：" + JSON.toJSONString(flowEnt));
			JSONObject flowEntJson = JSON.parseObject(flowEnt.getFlowMsg());
			Iterator<Entry<String, Object>> jsonIterator = flowEntJson.entrySet().iterator();
			List<String> productBillsIds = null;
			List<String> fsExpenseIncomeIds = null;
			Map<String, BigDecimal> billsInfo = new HashMap<>();
			Map<String, BigDecimal> incomeInfo = new HashMap<>();
			while (jsonIterator.hasNext()) {
				Object o = jsonIterator.next().getValue();
				if (o != null && o.toString().startsWith("[{")) {
					JSONArray arr = JSON.parseArray(o.toString());
					if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("productbillsid"))) {
						for (Object obj : arr) {
							JSONObject json = (JSONObject) obj;
							billsInfo.put(json.getString("productbillsid"), new BigDecimal(json.get("thiscost").toString()));
						}
						productBillsIds = arr.stream().map(obj -> {
							return ((JSONObject) obj).getString("productbillsid");
						}).collect(Collectors.toList());
					} else if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("fsexpenseincomeid"))) {
						for (Object obj : arr) {
							JSONObject json = (JSONObject) obj;
							incomeInfo.put(json.getString("fsexpenseincomeid"), new BigDecimal(json.get("thiscost").toString()));
						}
						fsExpenseIncomeIds = arr.stream().map(obj -> {
							return ((JSONObject) obj).getString("fsexpenseincomeid");
						}).collect(Collectors.toList());
					}
				}
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("id", Constants.ROP_IN, productBillsIds));
			List<ProductBills> productBillsList = productBillsService.queryAllBySearchFilter(filter);
			logger.info("撤销前账单信息：" + JSON.toJSONString(productBillsList));
			for (ProductBills bills : productBillsList) {
				BigDecimal thiscost = billsInfo.get(bills.getId());
				if (thiscost != null) {
					bills.setBillStatus(BillStatus.RECONILED.ordinal());
					bills.setActualReceivables(getMax(bills.getReceivables().subtract(thiscost), BigDecimal.ZERO));
					JSONArray relatedInfoArr = JSON.parseArray(bills.getRelatedInfo());
					if (relatedInfoArr != null && relatedInfoArr.size() > 0) {
						Iterator<Object> relatedInfoIterator = relatedInfoArr.iterator();
						while (relatedInfoIterator.hasNext()) {
							if (fsExpenseIncomeIds.contains(((JSONObject) relatedInfoIterator.next()).getString("fsExpenseIncomeId"))) {
								relatedInfoIterator.remove();
							}
						}
						bills.setRelatedInfo(relatedInfoArr.size() == 0 ? "" : relatedInfoArr.toString());
					}
					bills.setNeedAuto(NeedAuto.FALSE.ordinal());
				}
			}
			logger.info("撤销后账单信息：" + JSON.toJSONString(productBillsList));
			filter.getRules().clear();
			filter.getRules().add(new SearchRule("id", Constants.ROP_IN, fsExpenseIncomeIds));
			List<FsExpenseIncome> fsExpenseIncomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
			logger.info("撤销前到款信息：" + JSON.toJSONString(fsExpenseIncomeList));
			for (FsExpenseIncome income : fsExpenseIncomeList) {
				BigDecimal thiscost = incomeInfo.get(income.getId());
				if (thiscost != null) {
					income.setRemainRelatedCost(getMin(income.getRemainRelatedCost().add(thiscost), income.getCost()));
				}

			}
			logger.info("撤销后到款信息：" + JSON.toJSONString(fsExpenseIncomeList));
			baseDao.updateByBatch(productBillsList);
			baseDao.updateByBatch(fsExpenseIncomeList);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private BigDecimal getMin(BigDecimal val1, BigDecimal val2) {
		if (val1.subtract(val2).signum() > 0) {
			return val2;
		} else {
			return val1;
		}
	}

	private BigDecimal getMax(BigDecimal val1, BigDecimal val2) {
		if (val1.subtract(val2).signum() > 0) {
			return val1;
		} else {
			return val2;
		}
	}

}
