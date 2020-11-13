package com.dahantc.erp.dto.modifyPrice;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeductionPriceReqDto implements Serializable {
	private static final long serialVersionUID = 5548078581621203298L;

	/**
	 * 单价
	 */
	@NotNull(message = "单价")
	private BigDecimal price;

	/**
	 * 最小发送量
	 */
	private String minSend;

	/**
	 * 最大发送量
	 */
	private String maxSend;

	/**
	 * 省份占比
	 */
	private BigDecimal provinceProportion;

	/**
	 * 投诉率
	 */
	private BigDecimal complaintRrate;

	/**
	 * 是否默认
	 */
	@JsonProperty("isDefault")
	public boolean isDefault;

	/**
	 * 梯度（记录梯度数）
	 */
	public int gradient;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getMinSend() {
		return minSend;
	}

	public void setMinSend(String minSend) {
		this.minSend = minSend;
	}

	public String getMaxSend() {
		return maxSend;
	}

	public void setMaxSend(String maxSend) {
		this.maxSend = maxSend;
	}

	public BigDecimal getProvinceProportion() {
		return provinceProportion;
	}

	public void setProvinceProportion(BigDecimal provinceProportion) {
		this.provinceProportion = provinceProportion;
	}

	public BigDecimal getComplaintRrate() {
		return complaintRrate;
	}

	public void setComplaintRrate(BigDecimal complaintRrate) {
		this.complaintRrate = complaintRrate;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public int getGradient() {
		return gradient;
	}

	public void setGradient(int gradient) {
		this.gradient = gradient;
	}

}
