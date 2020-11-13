package com.dahantc.erp.dto.modifyPrice;

import java.io.Serializable;
import java.util.List;

/**
 * 查询充值记录、调价记录响应封装
 * 
 * @author wangyang
 *
 */
public class RecordListRespDto implements Serializable {

	private static final long serialVersionUID = 8569204726563014254L;

	// 记录详情
	private List<String> recordDetail;

	// 记录月份
	private String month;

	// 记录年份
	private String year;

	public List<String> getRecordDetail() {
		return recordDetail;
	}

	public void setRecordDetail(List<String> recordDetail) {
		this.recordDetail = recordDetail;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
