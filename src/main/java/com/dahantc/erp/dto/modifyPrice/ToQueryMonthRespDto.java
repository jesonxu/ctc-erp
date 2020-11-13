package com.dahantc.erp.dto.modifyPrice;

import java.io.Serializable;

public class ToQueryMonthRespDto implements Serializable {

	private static final long serialVersionUID = -7039259969940869177L;

	private int month;

	private String monthStr;

	private String clazz = "layui-colla-content";
	
	private Long flowEntCount;

	public ToQueryMonthRespDto() {
	}

	public ToQueryMonthRespDto(int month, String monthStr) {
		this.month = month;
		this.monthStr = monthStr;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getMonthStr() {
		return monthStr;
	}

	public void setMonthStr(String monthStr) {
		this.monthStr = monthStr;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Long getFlowEntCount() {
		return flowEntCount;
	}

	public void setFlowEntCount(Long flowEntCount) {
		this.flowEntCount = flowEntCount;
	}

}
