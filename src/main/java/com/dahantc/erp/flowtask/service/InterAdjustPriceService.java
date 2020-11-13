package com.dahantc.erp.flowtask.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;

@Service("interAdjustPriceService")
public class InterAdjustPriceService extends BaseFlowTask {

	private static Logger logger = LogManager.getLogger(InterAdjustPriceService.class);

	public static final String FLOW_CLASS = Constants.INTER_ADJUST_PRICE_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.INTER_ADJUST_PRICE_FLOW_NAME;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Autowired
	private IModifyPriceService modifyPriceService;

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
			result = convertJsonToModify(labelJsonVal, erpFlow.getFlowId(), productId, null);
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
		flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
		if (erpFlow != null) {
			try {
				String productId = flowEnt.getProductId();
				String supplierId = flowEnt.getSupplierId();
				ModifyPrice modifyPrice = new ModifyPrice();
				convertJsonToModify(flowEnt.getFlowMsg(), erpFlow.getFlowId(), flowEnt.getProductId(), modifyPrice);
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
				modifyPriceService.save(modifyPrice);
				saveUnitPrices(modifyPrice);
			} catch (Exception e) {
				logger.error("", e);
				return false;
			}
		}
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
	 * 按月份保存各个国家单价
	 */
	private void saveUnitPrices(ModifyPrice modifyPrice) {
		try {
			List<String[]> priceList = null;
			if (StringUtil.isNotBlank(modifyPrice.getRemark())) {
				String ext = modifyPrice.getRemark().substring(modifyPrice.getRemark().lastIndexOf(".") + 1);
				if ("xls".equals(ext)) {
					// 解析excel2003文件
					priceList = ParseFile.parseExcel2003(new File(modifyPrice.getRemark()));
				} else if ("xlsx".equals(ext)) {
					// 解析excel2007文件
					priceList = ParseFile.parseExcel2007(new File(modifyPrice.getRemark()));
				}
			}
			// 将从报价单读出的内容转换成单价列表
			priceList = convertToPriceList(priceList);
			if (!ListUtils.isEmpty(priceList)) {
				// 国别号列表
				List<String> codeList = priceList.stream().map(datas -> datas[0]).filter(StringUtils::isNotBlank).collect(Collectors.toList());

				Timestamp startTime = modifyPrice.getValidityDateStart();
				Timestamp endTime = modifyPrice.getValidityDateEnd();
				if (startTime.after(endTime)) {
					Timestamp temp = startTime;
					startTime = endTime;
					endTime = temp;
				}
				startTime = new Timestamp(DateUtil.getThisMonthFirst(startTime).getTime());
				endTime = new Timestamp(DateUtil.getMonthFinal(endTime).getTime());
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, modifyPrice.getProductId()));
				filter.getRules().add(new SearchRule("countryCode", Constants.ROP_IN, codeList));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
				List<UnitPrice> pList = unitPriceService.queryAllBySearchFilter(filter);
				Map<String, UnitPrice> priceMap = new HashMap<>();
				if (!ListUtils.isEmpty(priceList)) {
					for (UnitPrice unitPrice : pList) {
						priceMap.put(unitPrice.getCountryCode() + "," + DateUtil.convert(unitPrice.getWtime(), DateUtil.format4), unitPrice);
					}
				}
				for (; startTime.compareTo(endTime) <= 0; startTime = new Timestamp(DateUtil.getNextMonthFirst(startTime).getTime())) {
					List<UnitPrice> saveList = new ArrayList<>();
					List<UnitPrice> updateList = new ArrayList<>();
					for (String[] datas : priceList) {
						if (datas.length >= 3 && StringUtil.isNotBlank(datas[0]) && StringUtil.isNotBlank(datas[2])) {
							BigDecimal price = new BigDecimal(0);
							try {
								price = new BigDecimal(datas[2]).setScale(6, BigDecimal.ROUND_CEILING);
							} catch (Exception e) {
								logger.error("", e);
							}
							if (price.doubleValue() > 0) {
								UnitPrice unitPrice = priceMap.get(datas[0] + "," + DateUtil.convert(startTime, DateUtil.format4));
								if (unitPrice == null) {
									unitPrice = new UnitPrice();
									unitPrice.setCountryCode(datas[0]);
									unitPrice.setEntityType(modifyPrice.getEntityType());
									unitPrice.setBasicsId(modifyPrice.getProductId());
									unitPrice.setUnitPrice(price);
									unitPrice.setWtime(startTime);
									saveList.add(unitPrice);
								} else {
									unitPrice.setUnitPrice(price);
									updateList.add(unitPrice);
								}
							}
						}
					}
					if (!ListUtils.isEmpty(saveList)) {
						unitPriceService.saveByBatch(saveList);
					}
					if (!ListUtils.isEmpty(updateList)) {
						unitPriceService.updateByBatch(updateList);
					}
				}
			}

		} catch (Exception e) {
			logger.error("保存国际单价异常", e);
		}
	}

	/**
	 * 解析json字符串，转换为调价表记录
	 */
	public String convertJsonToModify(String labelValue, String flowId, String productId, ModifyPrice modifyPrice) {
		String result = "";
		try {
			if (StringUtils.isNotBlank(flowId)) {
				if (modifyPrice == null) {
					modifyPrice = new ModifyPrice();
				}
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				filter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
				List<FlowLabel> labelList = flowLabelService.queryAllBySearchFilter(filter);
				if (labelList != null && !labelList.isEmpty()) {
					JSONObject lVaule = new JSONObject();
					if (StringUtils.isNotBlank(labelValue)) {
						lVaule = JSONObject.parseObject(labelValue);
					}
					logger.info("解析" + lVaule.toJSONString() + "转换为国际调价表记录");
					for (FlowLabel flowLabel : labelList) {
						modifyPrice.setPriceType(1);
						modifyPrice.setUnit(1);
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
						// 报价单(调价excel表路径)
						if (Constants.DAHAN_QUOTATION_KEY.equals(flowLabel.getName())) {
							String enclosure = lVaule.get(Constants.DAHAN_QUOTATION_KEY).toString();
							if (StringUtils.isNotBlank(enclosure)) {
								try {
									JSONArray array = JSON.parseArray(enclosure);
									if (array != null && array.size() > 0) {
										modifyPrice.setRemark(array.getJSONObject(0).getString(Constants.FLOW_FILE_PATH_KEY));
									}
								} catch (Exception e) {
									logger.error("国际调价流程归档解析文件路径异常", e);
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error("封装数据异常:" + labelValue, e);
			result = "封装数据异常";
		}
		return result;
	}

	/**
	 * 从将报价单中读出的列表，转换成单价列表，兼容短信云的报价单、带不带标题、国别号带不带加号
	 * 
	 * @param tempList
	 *            从报价单中读出的列表
	 * @return 单价列表 [带加号的国别号，国家，单价]
	 */
	public List<String[]> convertToPriceList(List<String[]> tempList) {
		logger.info("转换报价单开始");
		List<String[]> priceList = null;
		if (ListUtils.isEmpty(tempList)) {
			logger.info("待转换的报价单为空");
			return priceList;
		}
		String[] row = tempList.get(0);
		if (row.length == 3) {
			// 3列是ERP的报价单：国别号，国家，单价
			priceList = tempList.stream().filter(data -> StringUtils.isNotBlank(data[0])).map(data -> {
				// 带不带加号
				if (StringUtils.isNotBlank(data[0])) {
					data[0] = data[0].startsWith("+") ? data[0] : "+" + data[0];
				}
				return data;
			}).collect(Collectors.toList());
			// 第一行是标题
			if (row[0].contains("国别号") || row[0].contains("国际电话区号")) {
				priceList = priceList.subList(1, priceList.size());
			}
		} else if (row.length == 4) {
			// 4列是短信云的报价单：国家，国家英文，国别号，单价
			priceList = tempList.stream().filter(data -> StringUtils.isNotBlank(data[2])).map(data -> {
				// 带不带加号
				if (StringUtils.isNotBlank(data[2])) {
					data[2] = data[2].startsWith("+") ? data[2] : "+" + data[2];
				}
				return new String[] { data[2], data[0], data[3] };
			}).collect(Collectors.toList());
			if (row[2].contains("国别号") || row[2].contains("国际电话区号")) {
				// 带表头
				priceList = priceList.subList(1, priceList.size());
			}
		}
		if (priceList != null) {
			logger.info("转换报价单结束，获得" + priceList.size() + "条单价");
		}
		return priceList;
	}
}
