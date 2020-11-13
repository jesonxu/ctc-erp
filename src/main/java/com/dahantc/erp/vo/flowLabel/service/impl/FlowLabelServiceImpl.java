package com.dahantc.erp.vo.flowLabel.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.FlowLabelType;
import com.dahantc.erp.vo.flowLabel.dao.IFlowLabelDao;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("flowLabelService")
public class FlowLabelServiceImpl implements IFlowLabelService {
	private static Logger logger = LogManager.getLogger(FlowLabelServiceImpl.class);

	private static Map<String, JSONObject> flowKeyMap = new HashMap<>();

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

	static {
		// 提单流程
		String DsOrderFlow = "{" + "\"productname\": \"产品名称\"," + "\"format\": \"规格型号\"," + "\"price\": \"销售单价\"," + "\"amount\": \"数量\","
				+ "\"total\": \"销售总额\"," + "\"remark\": \"备注\"" + "}";
		// 提单流程_配单信息
		String DsOrderFlowMatch = "{" + "\"productname\": \"产品名称\"," + "\"format\": \"规格型号\"," + "\"price\": \"销售单价\"," + "\"amount\": \"数量\","
				+ "\"total\": \"销售总额\"," + "\"logisticsCost\": \"物流费\"," + "\"suppliername\": \"供应商\"," + "\"remark\": \"备注\"" + "}";
		// 采购流程_配单信息
		String DsPurchaseFlowMatch = "{" + "\"productname\": \"产品名称\"," + "\"format\": \"规格型号\"," + "\"price\": \"销售单价\"," + "\"amount\": \"数量\","
				+ "\"total\": \"销售总额\"," + "\"logisticsCost\": \"物流费\"," + "\"suppliername\": \"供应商\"," + "\"remark\": \"备注\"" + "}";
		// 充值流程_充值详情
		String PaymentFlow = "{" + "\"rechargeAccount\": \"账号\"," + "\"currentAmount\": \"当前余额\"," + "\"price\": \"单价\"," + "\"rechargeAmount\": \"充值金额\","
				+ "\"pieces\": \"条数\"}";
		flowKeyMap.put("[DsOrderFlow]", JSON.parseObject(DsOrderFlow, Feature.OrderedField));
		flowKeyMap.put("[DsOrderFlow]match", JSON.parseObject(DsOrderFlowMatch, Feature.OrderedField));
		flowKeyMap.put("[DsPurchaseFlow]match", JSON.parseObject(DsPurchaseFlowMatch, Feature.OrderedField));
		flowKeyMap.put("[PaymentFlow]", JSON.parseObject(PaymentFlow, Feature.OrderedField));
	}

	@Autowired
	private IFlowLabelDao flowLabelDao;

	@Override
	public FlowLabel read(Serializable id) throws ServiceException {
		try {
			return flowLabelDao.read(id);
		} catch (Exception e) {
			logger.error("读取流程标签信息表失败", e);
			throw new ServiceException("读取流程标签信息表失败", e);
		}
	}

	@Override
	public boolean save(FlowLabel entity) throws ServiceException {
		try {
			return flowLabelDao.save(entity);
		} catch (Exception e) {
			logger.error("保存流程标签信息表失败", e);
			throw new ServiceException("保存流程标签信息表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<FlowLabel> objs) throws ServiceException {
		try {
			return flowLabelDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return flowLabelDao.delete(id);
		} catch (Exception e) {
			logger.error("删除流程标签信息表失败", e);
			throw new ServiceException("删除流程标签信息表失败", e);
		}
	}

	@Override
	public boolean update(FlowLabel enterprise) throws ServiceException {
		try {
			return flowLabelDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新流程标签信息表失败", e);
			throw new ServiceException("更新流程标签信息表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return flowLabelDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询流程标签信息表数量失败", e);
			throw new ServiceException("查询流程标签信息表数量失败", e);
		}
	}

	@Override
	public PageResult<FlowLabel> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return flowLabelDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询流程标签信息表分页信息失败", e);
			throw new ServiceException("查询流程标签信息表分页信息失败", e);
		}
	}

	@Override
	public List<FlowLabel> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return flowLabelDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询流程标签信息表失败", e);
			throw new ServiceException("查询流程标签信息表失败", e);
		}
	}

	@Override
	public List<FlowLabel> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return flowLabelDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询流程标签信息表失败", e);
			throw new ServiceException("查询流程标签信息表失败", e);
		}
	}

	@Override
	public List<FlowLabel> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return flowLabelDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询流程标签信息表失败", e);
			throw new ServiceException("查询流程标签信息表失败", e);
		}
	}

	@Override
	public List<FlowLabel> readByIds(List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, ids));
		try {
			return queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("根据id批量查询流程标签信息表失败", e);
		}
		return null;
	}

	@Override
	public List<LabelValue> getAllLabelValue(String flowMsg, String flowClass, String flowId, Map<String, FlowLabel> cacheFlowLabelMap) {
		if (StringUtils.isBlank(flowMsg)) {
			return null;
		}
		List<LabelValue> result = new ArrayList<>();
		JSONObject json = JSON.parseObject(flowMsg);
		List<LabelValue> converBaseData = converBaseData(flowClass,
				json.get(Constants.FLOW_BASE_DATA_KEY) == null ? "" : json.get(Constants.FLOW_BASE_DATA_KEY).toString());
		if (!CollectionUtils.isEmpty(converBaseData)) {
			result.addAll(converBaseData);
		}
		json.forEach((key, value) -> {
			FlowLabel flowLabel = cacheFlowLabelMap.get(flowId + key);
			String convertValue = convertLabel(flowClass, key, value == null ? "" : value.toString(), flowLabel);
			if (convertValue != null) {
				result.add(new LabelValue(key, convertValue, flowLabel.getPosition()));
			}
		});
		result.sort((o1, o2) -> o1.getPosition() - o2.getPosition() == 0 ? o1.getKey().compareTo(o2.getKey()) : o1.getPosition() - o2.getPosition());
		return result;
	}

	private List<LabelValue> converBaseData(String flowClass, String baseData) {
		if (StringUtils.isBlank(baseData)) {
			return null;
		}
		JSONObject json = JSON.parseObject(baseData);
		List<LabelValue> result = new ArrayList<>();
		json.forEach((key, value) -> {
			if (StringUtils.equals("账单编号", key) || StringUtils.equals("单价(元)", key) || StringUtils.equals("平台账单金额", key) || StringUtils.equals("备注", key)) {
				result.add(new LabelValue(key, value == null ? "" : value.toString(), -1));
			} else if (StringUtils.equals("BILL_PRICE_INFO_KEY", key)) {
				List<String> list = new ArrayList<>();
				JSONArray arr = JSON.parseArray(value.toString());
				arr.forEach(obj -> {
					JSONObject itemJson = (JSONObject) obj;
					list.add("时间段：" + itemJson.get("timeQuantum") + "，价格信息：" + itemJson.get("modifyPriceInfo") + "，发送量：" + itemJson.get("successCount"));
				});
				result.add((new LabelValue("我司数据", String.join("；", list), -1)));
			} else if (StringUtils.equals("DAHAN_BILL_FILE_KEY", key)) {
				result.add((new LabelValue("电子账单", value == null ? "" : value.toString(), -1)));
			}
		});
		return result;
	}

	private String convertLabel(String flowClass, String labelName, String labelValue, FlowLabel label) {
		if (label == null) {
			return null;
		}
		int type = label.getType();
		if (StringUtils.isBlank(labelValue)) {
			if (type == FlowLabelType.DsMatchPeople.ordinal()) {
				return "未选择";
			}
			return label.getDefaultValue();
		}
		switch (type) {
		case 0: // 字符串
		{
			if (StringUtils.equals(flowClass, Constants.BILL_FLOW_CLASS)) {
				if (StringUtils.equals(Constants.DAHAN_REAL_BILL_MONEY, labelName) || StringUtils.equals(Constants.DAHAN_CUSTOMER_BILL_MONEY, labelName)) {
					String[] arr = labelValue.split(",");
					if (arr.length == 3) {
						return arr[0] + "条 X " + arr[1] + "元 = " + arr[2] + "元";
					}
				}
			} else if (StringUtils.equals(flowClass, Constants.BILL_WRITE_OFF_FLOW_CLASS)) {
				List<String> list = new ArrayList<>();
				if (labelValue.startsWith("[{")) {
					JSONArray arr = JSON.parseArray(labelValue);
					if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("productbillsid"))) { // 账单信息
						BigDecimal total = BigDecimal.ZERO;
						for (Object obj : arr) {
							JSONObject json = (JSONObject) obj;
							list.add("账单名称：" + json.get("title") + "，账单金额：" + json.get("thiscost"));
							total = total.add(json.get("thiscost") == null ? BigDecimal.ZERO : new BigDecimal(json.get("thiscost").toString()));
						}
						list.add("合计：" + total);
						return String.join("；", list);
					} else if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("fsexpenseincomeid"))) { // 收款信息
						BigDecimal total = BigDecimal.ZERO;
						for (Object obj : arr) {
							JSONObject json = (JSONObject) obj;
							list.add("收款时间：" + json.get("operatetime") + "银行客户名称：" + json.get("banckcustomername") + "收款金额：" + json.get("cost") + "销账金额："
									+ json.get("thiscost"));
							total = total.add(json.get("thiscost") == null ? BigDecimal.ZERO : new BigDecimal(json.get("thiscost").toString()));
						}
						list.add("收款合计：" + total);
						return String.join("；", list);
					}
				}
			}
			return labelValue;
		}
		case 1: // 整型
			return labelValue;
		case 2: // 浮点类型
			return labelValue;
		case 3: // 布尔类型
			return StringUtils.equals("0", labelValue) ? "否" : "是";
		case 4: // 日期类型
			return labelValue;
		case 5: // 时间日期类型
			return labelValue;
		case 6: // 月份类型
			return labelValue;
		case 7: // 下拉框类型
			return labelValue;
		case 8: // 文件类型
		{
			if (labelValue.startsWith("[{")) {
				JSONArray array = JSON.parseArray(labelValue);
				return String.join("；", array.stream().filter(obj -> ((JSONObject) obj).get("filePath") != null)
						.map(obj -> ((JSONObject) obj).get("filePath").toString()).collect(Collectors.toList()));
			} else if (StringUtils.equals("[]", labelValue)) {
				return "";
			}
			return labelValue;
		}
		case 9: // 文本类型
			return labelValue;
		case 10: // 价格梯度
		{
			JSONArray array = JSON.parseArray(labelValue);
			List<String> list = new ArrayList<>();
			array.sort((o1, o2) -> {
				JSONObject json1 = (JSONObject) o1;
				JSONObject json2 = (JSONObject) o2;
				if (json1.get("gradient") == null || StringUtils.isBlank(json1.get("gradient").toString())) {
					return -1;
				}
				if (json2.get("gradient") == null || StringUtils.isBlank(json2.get("gradient").toString())) {
					return 1;
				}
				return (json1.get("gradient") == null ? "" : json1.get("gradient").toString())
						.compareTo(json2.get("gradient") == null ? "" : json2.get("gradient").toString());
			});
			for (Object obj : array) {
				JSONObject json = (JSONObject) obj;
				if (labelValue.contains("gradient")) {
					list.add(json.get("minsend") + "条 <= 发送量 < "
							+ (json.get("maxsend") == null || StringUtils.isBlank(json.get("maxsend").toString()) ? "∞" : json.get("maxsend")) + "条，价格： "
							+ json.get("price") + "，百万投比："
							+ (json.get("complaintrate") == null || StringUtils.isBlank(json.get("complaintrate").toString()) ? "空"
									: (json.get("complaintrate") + "%"))
							+ "，省占比："
							+ (json.get("provinceproportion") == null || StringUtils.isBlank(json.get("provinceproportion").toString()) ? "空"
									: (json.get("provinceproportion") + "%"))
							+ (json.get("isdefault") == null || StringUtils.equals(json.get("isdefault").toString(), "0") ? "" : "(默认)"));
				} else {
					list.add("价格：" + json.get("price") + "，省网价格：" + (json.get("price") == null ? "" : json.get("price")));
				}
			}
			return String.join("；", list);
		}
		case 11: // 价格类型
		{
			String defaultValue = label.getDefaultValue();
			String[] arr = defaultValue.split(",");
			Map<Integer, String> map = new HashMap<>();
			for (String str : arr) {
				String[] tArr = str.split(":");
				if (StringUtils.isNotBlank(tArr[0]) && NumberUtils.isParsable(labelValue)) {
					map.put(Integer.valueOf(tArr[0]), tArr[1]);
				}
			}
			if (NumberUtils.isParsable(labelValue)) {
				return map.get(Integer.valueOf(labelValue));
			}
			return "未知类型";
		}
		case 12: // 充值类型
		{
			String defaultValue = label.getDefaultValue();
			String[] arr = defaultValue.split(",");
			Map<Integer, String> map = new HashMap<>();
			for (String str : arr) {
				String[] tArr = str.split(":");
				if (StringUtils.isNotBlank(tArr[0]) && NumberUtils.isParsable(labelValue)) {
					map.put(Integer.valueOf(tArr[0]), tArr[1]);
				}
			}
			if (NumberUtils.isParsable(labelValue)) {
				return map.get(Integer.valueOf(labelValue));
			}
			return labelValue;
		}
		case 13: // 酬金类型
		{

			if (StringUtils.isBlank(labelValue)) {
				labelValue = "0.00,0.00,0.00,0.00,0.00";
			}
			String[] values = labelValue.split(",");
			return "金额" + values[0] + "元 X 酬金比例 " + values[1] + "% + 奖励 " + values[2] + "元 － 扣款 " + values[3] + "元 = " + values[4] + "元";
		}
		case 14: // 账单信息
		{
			if (StringUtils.isBlank(labelValue)) {
				return "";
			}
			List<String> list = new ArrayList<>();
			JSONArray array = JSON.parseArray(labelValue);
			array.forEach(obj -> {
				JSONObject json = (JSONObject) obj;
				list.add(json.get("title") + "应开金额：" + json.get("receivables") + "元，已开金额：" + json.get("actualInvoiceAmount") + "元，剩余应开："
						+ (json.get("receivables") == null ? BigDecimal.ZERO : new BigDecimal(json.get("receivables").toString())).subtract(
								json.get("actualInvoiceAmount") == null ? BigDecimal.ZERO : new BigDecimal(json.get("actualInvoiceAmount").toString()))
						+ "元，本次开票：" + json.get("thisReceivables") + "元");
			});
			return String.join("；", list);
		}
		case 15: // 账单金额
		{
			String supplierSuccess = "0";
			String supplierPrice = "0";
			String totalMoney = "0";
			if (StringUtils.isNotBlank(labelValue)) {
				String[] arr = labelValue.split(",");
				if (arr.length >= 3) {
					supplierSuccess = StringUtils.isBlank(arr[0]) ? "0" : arr[0];
					supplierPrice = StringUtils.isBlank(arr[1]) ? "0" : arr[1];
					totalMoney = StringUtils.isBlank(arr[2]) ? "0" : arr[2];
				}
			}
			return supplierSuccess + "条" + " X " + supplierPrice + "元" + " = " + totalMoney + "元";
		}
		case 16: // 开关类型
			return labelValue;
		case 17: // 我司开票信息
		case 18: // 对方开票信息
			return getInvoiceInfo(labelValue);
		case 19: // 我司银行信息
		{
			Map<String, String> map = new HashMap<>();
			for (String str : labelValue.split("####")) {
				String[] arr = str.split(":");
				map.put(arr[0], arr[1]);
			}
			return map.get("companyName") + "【开户银行：" + map.get("accountBank") + "：" + map.get("bankAccount") + "】";
		}
		case 20: // 对方银行信息
			return labelValue;
		case 21: // 合同编号
			return labelValue;
		case 22: // 历史单价
			return labelValue;
		case 23: // 发票信息
			return labelValue;
		case 24: // 提单信息
		{
			List<String> result = new ArrayList<>();
			JSONObject json = flowKeyMap.get(flowClass + "match");
			JSONArray arr = JSON.parseArray(labelValue);
			arr.forEach(obj -> {
				JSONObject valueJson = (JSONObject) obj;
				List<String> list = new ArrayList<>();
				json.keySet().forEach(key -> {
					list.add(json.get(key) + ":" + (valueJson.get(key) == null ? "" : valueJson.get(key).toString()));
				});
				result.add(String.join("，", list));
			});
			return String.join("；", result);
		}
		case 25: // 配单信息
		{
			List<String> result = new ArrayList<>();
			JSONObject json = flowKeyMap.get(flowClass + "match");
			JSONArray arr = JSON.parseArray(labelValue);
			arr.forEach(obj -> {
				JSONObject valueJson = (JSONObject) obj;
				List<String> list = new ArrayList<>();
				json.keySet().forEach(key -> {
					list.add(json.get(key) + ":" + (valueJson.get(key) == null ? "" : valueJson.get(key).toString()));
				});
				result.add(String.join("，", list));
			});
			return String.join("；", result);
		}
		case 26: // 订单编号
			return labelValue;
		case 27: // 电商配单员
			return getSelectRole(labelValue);
		case 28: // 采购单编号
			return labelValue;
		case 29: // 客户开票抬头
		{
			if (StringUtils.isBlank(labelValue)) {
				return "";
			}
			if (labelValue.contains("{")) {
				List<String> list = new ArrayList<>();
				Object obj = JSON.parse(labelValue);
				if (obj instanceof JSONArray) {
					JSONArray arr = (JSONArray) obj;
					arr.forEach(object -> {
						JSONObject json = null;
						if (obj instanceof JSONArray) {
							JSONArray jsonArr = (JSONArray) obj;
							if (jsonArr.size() > 0) {
								json = jsonArr.getJSONObject(0);
							}
						} else if (obj instanceof JSONObject) {
							json = (JSONObject) obj;
						}
						if (json != null) {
							list.add(getInvoiceInfo((json.get("custInvoiceInfo") == null ? "" : json.get("custInvoiceInfo").toString()) + "，已收金额："
									+ json.get("receivables") + "，开票金额：" + json.get("thisReceivables")));
						}
					});
				} else if (obj instanceof JSONObject) {
					JSONObject json = (JSONObject) obj;
					list.add(getInvoiceInfo(json.get("custInvoiceInfo") == null ? "" : json.get("custInvoiceInfo").toString()));
				}
				return String.join("；", list);
			} else {
				return getInvoiceInfo(labelValue);
			}
		}
		case 30: // 账单开票信息
		{
			List<String> list = new ArrayList<>();
			JSONArray array = JSON.parseArray(labelValue);
			BigDecimal total = BigDecimal.ZERO;
			for (Object obj : array) {
				JSONObject json = (JSONObject) obj;
				list.add(json.get("title") + "，应开金额：" + json.get("receivables") + "元，已开金额：" + json.get("actualInvoiceAmount") + "元，"
						+ (json.get("left_should_receive") == null ? "" : ("剩余应开：" + json.get("left_should_receive") + "元，")) + "本次开票："
						+ json.get("thisReceivables") + "元");
				total = total.add(json.get("thisReceivables") == null ? BigDecimal.ZERO : new BigDecimal(json.get("thisReceivables").toString()));
			}
			return String.join("；", list) + "；合计开票：" + total + "元";
		}
		case 31: // 电商银行信息
		{
			List<String> list = new ArrayList<>();
			JSONArray array = JSON.parseArray(labelValue);
			array.forEach(obj -> {
				JSONObject json = (JSONObject) obj;
				list.add("名称：" + json.get("accountName") + "，开户银行：" + json.get("accountBank") + "，银行账号：" + json.get("bankAccount"));
			});
			return String.join("；", list);
		}
		case 34: // 未对账账单
		{
			if (StringUtils.isBlank(labelValue)) {
				return "";
			}
			List<String> list = new ArrayList<>();
			JSONObject json = JSON.parseObject(labelValue);
			if (json.containsKey("billInfos")) {
				JSONArray billInfos = JSON.parseArray(json.getString("billInfos"));
				for (Object billInfo : billInfos) {
					JSONObject bill = (JSONObject) billInfo;
					StringBuffer line = new StringBuffer();
					line.append(bill.getString("title")).append("\n");
					line.append("  我司数据：").append(bill.getString("platformSuccessCount")).append("X").append(bill.getString("platformUnitPrice")).append(" = ")
							.append(bill.getString("platformAmount")).append("\n");
					line.append("  客户数据：").append(bill.getString("customerSuccessCount")).append("X").append(bill.getString("customerUnitPrice")).append(" = ")
							.append(bill.getString("customerAmount")).append("\n");
					line.append("  对账数据：").append(bill.getString("checkedSuccessCount")).append("X").append(bill.getString("checkedUnitPrice")).append(" = ")
							.append(bill.getString("checkedAmount")).append("\n");

					list.add(line.toString());
				}
			}
			if (json.containsKey("billTotal")) {
				JSONObject total = JSON.parseObject(json.getString("billTotal"));
				StringBuffer line = new StringBuffer("对账总计：\n");
				line.append("我司数据：计费数：").append(total.getString("platformSuccessCount")).append("，金额：").append(total.getString("platformAmount")).append("\n");
				line.append("对账数据：计费数：").append(total.getString("checkedSuccessCount")).append("，金额：").append(total.getString("checkedAmount")).append("\n");
				list.add(line.toString());
			}
			if (json.containsKey("billFile")) {
				JSONObject billFile = JSON.parseObject(json.getString("billFile"));
				StringBuffer line = new StringBuffer("电子账单：");
				line.append(billFile.getString("fileName")).append("\n");
				list.add(line.toString());
			}
			if (json.containsKey("analysisFile")) {
				JSONObject analysisFile = JSON.parseObject(json.getString("analysisFile"));
				StringBuffer line = new StringBuffer("数据报告：");
				line.append(analysisFile.getString("fileName")).append("\n");
				list.add(line.toString());
			}
			return String.join("", list);
		}
		case 37: // 充值详情
		{
			if (StringUtils.isBlank(labelValue)) {
				return "";
			}
			JSONArray arr = JSON.parseArray(labelValue);
			List<String> result = new ArrayList<>();
			JSONObject labelJson = flowKeyMap.get(Constants.PAYMENT_FLOW_CLASS);
			for (Object object : arr) {
				JSONObject json = (JSONObject) object;
				result.add(labelJson.getString("rechargeAmount") + "【" + json.get("rechargeAmount") + "】，" + labelJson.getString("currentAmount") + "【"
						+ json.get("currentAmount") + "】，" + labelJson.getString("price") + "【" + json.get("price") + "】，"
						+ labelJson.getString("rechargeAmount") + "【" + json.get("rechargeAmount") + "】，" + labelJson.getString("pieces") + "【"
						+ json.get("pieces") + "】");
			}
			return StringUtils.join(result, "；") + "。";
		}
		default:
			return labelValue;
		}

	}

	private String getInvoiceInfo(String labelValue) {
		Map<String, String> map = new HashMap<>();
		for (String str : labelValue.split("####")) {
			String[] arr = str.split(":");
			map.put(arr[0], arr[1]);
		}
		return "公司名称：" + map.get("companyName") + "，税务号：" + map.get("taxNumber") + "，公司地址：" + map.get("companyAddress") + "，联系电话：" + map.get("phone") + "，开户银行："
				+ map.get("accountBank") + "，银行账号：" + map.get("bankAccount");
	}

	private String getSelectRole(String ossUserId) {
		try {

			// 查询“电商配单员”角色
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("rolename", Constants.ROP_EQ, Constants.ROLE_NAME_MATCH_ORDER));
			List<Role> dataList = roleService.queryAllBySearchFilter(filter);

			// 查询“电商配单员”角色
			SearchFilter roleRelationFilter = new SearchFilter();
			roleRelationFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
			roleRelationFilter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, dataList.get(0).getRoleid()));
			List<RoleRelation> roleRelationList = roleRelationService.queryAllBySearchFilter(roleRelationFilter);

			for (RoleRelation roleRelation : roleRelationList) {
				User user = userService.read(roleRelation.getOssUserId());
				if (user != null) {
					return user.getRealName();
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public class LabelValue {

		private String key;
		private String value;
		private int position;

		public LabelValue(String key, String value, int position) {
			this.key = key;
			this.value = value;
			this.position = position;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
