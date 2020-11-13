package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.DefaultPrice;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.PriceType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.deductionPrice.service.IDeductionPriceService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;

@Service("adjustPriceService")
public class AdjustPriceService extends BaseFlowTask {

	private static Logger logger = LogManager.getLogger(AdjustPriceService.class);

	public static final String FLOW_CLASS = Constants.ADJUST_PRICE_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.ADJUST_PRICE_FLOW_NAME;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Autowired
	private IModifyPriceService modifyPriceService;

	@Autowired
	private IDeductionPriceService deductionPriceService;

	@Autowired
	private IUnitPriceService unitPriceService;

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal) {
		String result = "";
		if (erpFlow != null) {
			result = convertJsonToModify(labelJsonVal, erpFlow.getFlowId(), productId, null, null);
		} else {
			result = "当前流程不存在";
		}
		return result;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		logger.info(FLOW_NAME + " 流程归档开始，flowEntId：" + flowEnt.getId());
		flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
		if (erpFlow != null) {
			try {
				String productId = flowEnt.getProductId();
				String supplierId = flowEnt.getSupplierId();
				ModifyPrice modifyPrice = new ModifyPrice();
				List<DeductionPrice> deductionPrices = new ArrayList<DeductionPrice>();
				convertJsonToModify(flowEnt.getFlowMsg(), erpFlow.getFlowId(), flowEnt.getProductId(), modifyPrice, deductionPrices);
				modifyPrice.setCreaterId(flowEnt.getOssUserId());
				modifyPrice.setProductId(productId);
				modifyPrice.setEntityId(supplierId);
				modifyPrice.setWtime(new Timestamp(System.currentTimeMillis()));
				modifyPrice.setFlowEntId(flowEnt.getId());
				int entityType = 0;
				if (StringUtils.isNotBlank(flowEnt.getFlowMsg())) {
					JSONObject json = JSONObject.parseObject(flowEnt.getFlowMsg());
					entityType = json.getIntValue("entityType");
				}
				modifyPrice.setEntityType(entityType);
				modifyPriceService.save(modifyPrice); // 保存调价记录
				for (DeductionPrice deductionPrice : deductionPrices) {
					deductionPrice.setModifyPriceId(modifyPrice.getModifyPriceId());
				}
				deductionPriceService.saveByBatch(deductionPrices); // 保存梯度价格
				saveUnitPrices(deductionPrices, modifyPrice); // 修改单价
			} catch (Exception e) {
				logger.error(FLOW_NAME + " 流程归档异常，flowEntId：" + flowEnt.getId(), e);
				return false;
			}
		}
		logger.info(FLOW_NAME + " 流程归档结束");
		return true;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) {
		logger.info("不处理信息变更操作");
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	/**
	 * 按月份保存默认单价
	 */
	private void saveUnitPrices(List<DeductionPrice> deductionPrices, ModifyPrice modifyPrice) {
		logger.info("根据调价默认梯度生成默认单价开始");
		try {
			if (!ListUtils.isEmpty(deductionPrices) && modifyPrice != null) {
				Timestamp startTime = modifyPrice.getValidityDateStart();
				Timestamp endTime = modifyPrice.getValidityDateEnd();
				if (startTime.after(endTime)) {
					Timestamp temp = startTime;
					startTime = endTime;
					endTime = temp;
				}
				startTime = new Timestamp(DateUtil.getThisMonthFirst(startTime).getTime());
				endTime = new Timestamp(DateUtil.getMonthFinal(endTime).getTime());
				for (DeductionPrice deductionPrice : deductionPrices) {
					if (deductionPrice.getIsDefault() == DefaultPrice.DEFAULT.ordinal()) {
						double price = deductionPrice.getPrice().doubleValue();
						SearchFilter filter = new SearchFilter();
						filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, modifyPrice.getProductId()));
						filter.getRules().add(new SearchRule("countryCode", Constants.ROP_EQ, Constants.CHINA_COUNTRY_CODE));
						filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
						filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
						List<UnitPrice> priceList = unitPriceService.queryAllBySearchFilter(filter);
						Map<Timestamp, UnitPrice> priceMap = new HashMap<>();
						if (!ListUtils.isEmpty(priceList)) {
							for (UnitPrice unitPrice : priceList) {
								priceMap.put(unitPrice.getWtime(), unitPrice);
							}
						}
						List<UnitPrice> saveList = new ArrayList<>();
						List<UnitPrice> updateList = new ArrayList<>();
						for (; startTime.compareTo(endTime) <= 0; startTime = new Timestamp(DateUtil.getNextMonthFirst(startTime).getTime())) {
							UnitPrice unitPrice = priceMap.get(startTime);
							if (unitPrice == null) {
								unitPrice = new UnitPrice();
								unitPrice.setBasicsId(modifyPrice.getProductId());
								unitPrice.setCountryCode(Constants.CHINA_COUNTRY_CODE);
								unitPrice.setWtime(startTime);
								unitPrice.setEntityType(modifyPrice.getEntityType());
								unitPrice.setUnitPrice(new BigDecimal(price));
								saveList.add(unitPrice);
							} else {
								unitPrice.setUnitPrice(new BigDecimal(price));
								updateList.add(unitPrice);
							}

						}
						if (!ListUtils.isEmpty(saveList)) {
							unitPriceService.saveByBatch(saveList);
						}
						if (!ListUtils.isEmpty(updateList)) {
							unitPriceService.updateByBatch(updateList);
						}
						break;
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("调价归档保存价格异常", e);
		}
	}

	/**
	 * 解析json字符串，转换为调价表记录
	 */
	public String convertJsonToModify(String labelValue, String flowId, String productId, ModifyPrice modifyPrice, List<DeductionPrice> deductionPrices) {
		logger.info("根据 调价流程 生成 调价记录 开始");
		String result = "";
		try {
			if (StringUtils.isNotBlank(flowId)) {
				if (modifyPrice == null) {
					modifyPrice = new ModifyPrice();
				}
				if (deductionPrices == null) {
					deductionPrices = new ArrayList<DeductionPrice>();
				}
				// 1、查询流程所有标签
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				filter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
				List<FlowLabel> labelList = flowLabelService.queryAllBySearchFilter(filter);
				if (labelList != null && !labelList.isEmpty()) {
					JSONObject lVaule = new JSONObject();
					if (StringUtils.isNotBlank(labelValue)) {
						lVaule = JSONObject.parseObject(labelValue);
					}
					logger.info("解析" + lVaule.toJSONString() + "转换为调价表记录");
					for (FlowLabel flowLabel : labelList) {
						// 价格类型
						if (Constants.PRICE_TYPE_KEY.equals(flowLabel.getName())) {
							String priceType = lVaule.getString(Constants.PRICE_TYPE_KEY);
							if (StringUtils.isNumeric(priceType)) {
								modifyPrice.setPriceType(Integer.parseInt(priceType));
							} else {
								result = Constants.PRICE_TYPE_KEY + "不正确";
								break;
							}
						}

						// 价格有效期起始
						if (Constants.PRICE_START_DATE_KEY.equals(flowLabel.getName())) {
							String validityDateStart = lVaule.getString(Constants.PRICE_START_DATE_KEY);
							String dateFormat = null;
							if (StringUtils.isNotBlank(validityDateStart) && validityDateStart.length() == 10) {
								dateFormat = DateUtil.format1;
							} else {
								dateFormat = DateUtil.format4;
							}
							Timestamp timestamp = new Timestamp(0);
							result += verifyDate(validityDateStart, timestamp, dateFormat);
							if (StringUtils.isBlank(result)) {
								modifyPrice.setValidityDateStart(timestamp);
							} else {
								result = Constants.PRICE_START_DATE_KEY + result;
								break;
							}
						}
						// 价格有效期结束
						if (Constants.PRICE_END_DATE_KEY.equals(flowLabel.getName())) {
							String validityDateStart = lVaule.getString(Constants.PRICE_END_DATE_KEY);
							String dateFormat = null;
							if (StringUtils.isNotBlank(validityDateStart) && validityDateStart.length() == 10) {
								dateFormat = DateUtil.format1;
							} else {
								dateFormat = DateUtil.format4;
							}
							Timestamp timestamp = new Timestamp(0);
							result += verifyDate(validityDateStart, timestamp, dateFormat);
							if (StringUtils.isBlank(result)) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(StringUtils.equals(dateFormat, DateUtil.format4) ? DateUtil.getMonthFinal(timestamp) : timestamp);
								cal.set(Calendar.HOUR_OF_DAY, 23);
								cal.set(Calendar.MINUTE, 59);
								cal.set(Calendar.SECOND, 59);
								cal.set(Calendar.MILLISECOND, 0);
								modifyPrice.setValidityDateEnd(new Timestamp(cal.getTime().getTime()));
							} else {
								result = Constants.PRICE_END_DATE_KEY + result;
								break;
							}
						}
						// 备注
						if (Constants.DAHAN_REMARK_KEY.equals(flowLabel.getName())) {
							String remark = lVaule.getString(Constants.DAHAN_REMARK_KEY);
							modifyPrice.setRemark(remark);
						}

						// 封装调价详情数据
						if (Constants.PRICE_ADJUSTMENT_KEY.equals(flowLabel.getName())) {
							result = convertJsonToDeductions(lVaule, modifyPrice, deductionPrices, flowLabel);
							if (StringUtils.isNotBlank(result)) {
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("根据 调价流程 生成 调价记录 异常:" + labelValue, e);
			result = "封装数据异常";
		}
		return result;
	}

	/**
	 * 解析json字符串，转换为梯度价格表记录
	 */
	public String convertJsonToDeductions(JSONObject lVaule, ModifyPrice modifyPrice, List<DeductionPrice> deductionPrices, FlowLabel flowLabel) {
		String result = "";
		String array = lVaule.getString(Constants.PRICE_ADJUSTMENT_KEY);
		if (StringUtils.isNotBlank(array)) {
			JSONArray deductions = JSONArray.parseArray(array);
			// 将每一个价格梯度转换为一条记录
			for (Object o : deductions) {
				JSONObject deductionValue = new JSONObject();
				if (StringUtils.isNotBlank(o.toString())) {
					deductionValue = JSONObject.parseObject(o.toString());
				}
				DeductionPrice deductionPrice = new DeductionPrice();

				// 单位值
				String voiceUnit = deductionValue.getString("voiceUnit");
				if (StringUtils.isNotBlank(voiceUnit)) {
					Matcher voiceUnitMatcher = ratioPattern.matcher(voiceUnit);
					if (voiceUnitMatcher.matches()) {
						Integer value = Integer.parseInt(voiceUnit);
						if (value <= 0) {
							result = "语音产品单位价格大于0";
							break;
						} else {
							modifyPrice.setUnit(value);
						}
					}
				}

				String price = deductionValue.getString("price");
				if (StringUtils.isNotBlank(price)) {
					Matcher matcher = pricePattern.matcher(price);
					if (matcher.matches()) {
						deductionPrice.setPrice(new BigDecimal(price));
					} else {
						result = "价格只能是数字并且小数位数不能超过六位";
						break;
					}
				} else {
					result = "价格不能为空";
					break;
				}
				// 统一价
				if (PriceType.UNIFORM_PRICE.getCode() == modifyPrice.getPriceType()) {
					String provinceprice = deductionValue.getString("provinceprice");
					if (StringUtils.isNotBlank(provinceprice)) {
						Matcher matcher = pricePattern.matcher(provinceprice);
						if (matcher.matches()) {
							deductionPrice.setProvincePrice(new BigDecimal(provinceprice));
						} else {
							result = "省网价格只能是数字并且小数位数不能超过六位";
							break;
						}
					}
					deductionPrice.setIsDefault(DefaultPrice.DEFAULT.ordinal());
				} else { // 阶梯价、阶段价
					// 本梯度最小发送量
					String minSend = deductionValue.getString("minsend");
					Matcher minSendMatcher = ratioPattern.matcher(minSend);
					if (minSendMatcher.matches()) {
						deductionPrice.setMinSend(Long.parseLong(minSend));
					}
					// 本梯度最大发送量
					String maxSend = deductionValue.getString("maxsend");
					Matcher maxSendMatcher = ratioPattern.matcher(maxSend);
					if (maxSendMatcher.matches()) {
						deductionPrice.setMaxSend(Long.parseLong(maxSend));
					}
					// 本梯度省占比
					String provinceProportion = deductionValue.getString("provinceproportion");
					if (StringUtils.isNotBlank(provinceProportion)) {
						Matcher matcher = ratioPattern.matcher(provinceProportion);
						if (matcher.matches()) {
							deductionPrice.setProvinceProportion(new BigDecimal(provinceProportion));
						} else {
							result = "省份占比只能是数字并且小数位数不能超过四位";
							break;
						}
					}
					// 本梯度百万投比
					String complaintRate = deductionValue.getString("complaintrate");
					if (StringUtils.isNotBlank(complaintRate)) {
						Matcher matcher = ratioPattern.matcher(complaintRate);
						if (matcher.matches()) {
							deductionPrice.setComplaintRrate(new BigDecimal(complaintRate));
						} else {
							result = "百万投比只能是数字并且小数位数不能超过四位";
							break;
						}
					}
					String isDefault = deductionValue.getString("isdefault");
					if (StringUtils.isNotBlank(isDefault)) {
						if ("1".equals(isDefault)) {
							deductionPrice.setIsDefault(DefaultPrice.DEFAULT.ordinal());
						} else if ("0".equals(isDefault)) {
							deductionPrice.setIsDefault(DefaultPrice.NON_DEFAULT.ordinal());
						}
					}
					// 梯度顺序
					String gradient = deductionValue.getString("gradient");
					if (StringUtils.isNotBlank(gradient)) {
						deductionPrice.setGradient(Integer.parseInt(gradient));
					}
				}
				deductionPrices.add(deductionPrice);
			}
		} else {
			result = "价格信息不能为空";
		}
		return result;
	}

}
