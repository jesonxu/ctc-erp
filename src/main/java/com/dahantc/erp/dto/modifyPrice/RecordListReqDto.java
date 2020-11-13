package com.dahantc.erp.dto.modifyPrice;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

/**
 * 查询充值记录、调价记录请求参数
 * 
 * @author wangyang
 *
 */
public class RecordListReqDto implements Serializable {

	private static final long serialVersionUID = 6462047237698643940L;

	// 查询年份
	@NotBlank(message = "查询年份不能为空")
	private String year;

	// 查询月份
	@NotBlank(message = "查询月份不能为空")
	private String month;

	@NotBlank(message = "产品id不能为空")
	private String productId;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
