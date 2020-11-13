package com.dahantc.erp.dto.goal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dahantc.erp.enums.GoalType;

public class GoalDto implements Serializable {

	private static final long serialVersionUID = -8779622422964694891L;

	private String id;

	private String regionName;

	private String deptName;

	private String deptId;

	private String parentDeptName;

	private String realName;

	private String ossUserId;

	private BigDecimal sumReceivables;

	private BigDecimal sumGrossProfit;

	// 每个月的数据 MM -> [销售额，毛利]
	private Map<String, BigDecimal[]> monthDataMap;

	public GoalDto() {
		this.id = UUID.randomUUID().toString().replaceAll("-", "");
		this.regionName = "-";
		this.deptName = "-";
		this.parentDeptName = "-";
		this.realName = "-";
		this.sumReceivables = new BigDecimal(0);
		this.sumGrossProfit = new BigDecimal(0);
		this.monthDataMap = new HashMap<>();
	}

	public void addSumReceivables(BigDecimal receivables) {
		setSumReceivables(this.sumReceivables.add(receivables));
	}

	public void addSumGrossProfit(BigDecimal grossProfit) {
		setSumGrossProfit(this.sumGrossProfit.add(grossProfit));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getParentDeptName() {
		return parentDeptName;
	}

	public void setParentDeptName(String parentDeptName) {
		this.parentDeptName = parentDeptName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public BigDecimal getSumReceivables() {
		return sumReceivables;
	}

	public void setSumReceivables(BigDecimal sumReceivables) {
		this.sumReceivables = sumReceivables;
	}

	public BigDecimal getSumGrossProfit() {
		return sumGrossProfit;
	}

	public void setSumGrossProfit(BigDecimal sumGrossProfit) {
		this.sumGrossProfit = sumGrossProfit;
	}

	public Map<String, BigDecimal[]> getMonths() {
		return monthDataMap;
	}

	public void setMonths(Map<String, BigDecimal[]> monthDataMap) {
		this.monthDataMap = monthDataMap;
	}

	// 获取某个月的数据 [销售额，毛利]
	public BigDecimal[] getMonthData(String month) {
		return this.monthDataMap.getOrDefault(month, new BigDecimal[] { new BigDecimal(0), new BigDecimal(0) });
	}

	// 存放某个月的数据 [销售额，毛利]
	public void setMonthData(String month, BigDecimal[] data) {
		this.monthDataMap.put(month, data);
	}

	/**
	 * 转换成导出数据
	 * 
	 * @param type
	 *            类型，0部门，1销售
	 * @param length
	 *            标题数组长度
	 * @return
	 */
	public String[] toExportData(int type, int length) {
		List<String> dataList = new ArrayList<>();
		dataList.add(regionName);
		dataList.add(parentDeptName);
		// 导出销售的数据时需要
		if (GoalType.SelfMonth.ordinal() == type) {
			dataList.add(deptName);
			dataList.add(realName);
		}
		dataList.add(sumReceivables.toPlainString());
		dataList.add(sumGrossProfit.toPlainString());
		BigDecimal[] values = null;
		for (int i = 1; i <= 12; i++) {
			String monthKey = (i < 10 ? "0" : "") + i;
			values = getMonthData(monthKey);
			dataList.add(values[0].toPlainString());
			dataList.add(values[1].toPlainString());
		}
		return dataList.toArray(new String[length]);
	}
}
