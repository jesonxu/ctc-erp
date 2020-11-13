package com.dahantc.erp.dto.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.dahantc.erp.vo.productBills.entity.ProductBills;
import org.apache.commons.lang3.StringUtils;

public class StatisticsRspDto implements Serializable {

	private static final long serialVersionUID = -34466219840792371L;

	public StatisticsRspDto() {

	}

	public StatisticsRspDto(ProductBills bill) {
		this.supplierCount = bill.getSupplierCount();
		this.platformCount = bill.getPlatformCount();
		this.receivables = bill.getReceivables().setScale(2, RoundingMode.HALF_UP).toString();
		this.actualReceivables = bill.getActualReceivables().setScale(2, RoundingMode.HALF_UP).toString();
		this.payables = bill.getPayables().setScale(2, RoundingMode.HALF_UP).toString();
		this.actualPayables = bill.getActualPayables().setScale(2, RoundingMode.HALF_UP).toString();
	}

	/**
	 * 月份
	 */
	private String year;
	/**
	 * 月份
	 */
	private String month;

	/**
	 * 供应商成功数
	 */
	private long supplierCount;

	/**
	 * 平台成功数
	 */
	private long platformCount;
	/**
	 * 应收金额
	 */
	private String receivables;

	/**
	 * 实收金额
	 */
	private String actualReceivables;

	/**
	 * 应付金额
	 */
	private String payables;

	/**
	 * 实付金额
	 */
	private String actualPayables;

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

	public long getSupplierCount() {
		return supplierCount;
	}

	public long getPlatformCount() {
		return platformCount;
	}

	public String getReceivables() {
		return receivables;
	}

	public String getActualReceivables() {
		return actualReceivables;
	}

	public String getPayables() {
		return payables;
	}

	public String getActualPayables() {
		return actualPayables;
	}

	public void setSupplierCount(long supplierCount) {
		this.supplierCount = supplierCount;
	}

	public void setPlatformCount(long platformCount) {
		this.platformCount = platformCount;
	}

	public void setReceivables(String receivables) {
		this.receivables = receivables;
	}

	public void setActualReceivables(String actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public void setPayables(String payables) {
		this.payables = payables;
	}

	public void setActualPayables(String actualPayables) {
		this.actualPayables = actualPayables;
	}

	public void addSupplierCount(long supplierCount) {
		this.supplierCount += supplierCount;

	}

	public void addPlatformCount(long platformCount) {
		this.platformCount += platformCount;
	}

	public void addReceivables(String receivables) {
		if (StringUtils.isBlank(receivables)) {
			receivables = "0";
		}
		if (StringUtils.isBlank(this.receivables)) {
			this.receivables = "0";
		}
		this.receivables = String.format("%.2f", new BigDecimal(this.receivables).add(new BigDecimal(receivables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addActualReceivables(String actualReceivables) {
		if (StringUtils.isBlank(actualReceivables)) {
			actualReceivables = "0";
		}
		if (StringUtils.isBlank(this.actualReceivables)) {
			this.actualReceivables = "0";
		}
		this.actualReceivables = String.format("%.2f",
				new BigDecimal(this.actualReceivables).add(new BigDecimal(actualReceivables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addPayables(String payables) {
		if (StringUtils.isBlank(payables)) {
			payables = "0";
		}
		if (StringUtils.isBlank(this.payables)) {
			this.payables = "0";
		}
		this.payables = String.format("%.2f", new BigDecimal(this.payables).add(new BigDecimal(payables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addActualPayables(String actualPayables) {
		if (StringUtils.isBlank(actualPayables)) {
			actualPayables = "0";
		}
		if (StringUtils.isBlank(this.actualPayables)) {
			this.actualPayables = "0";
		}
		this.actualPayables = String.format("%.2f",
				new BigDecimal(this.actualPayables).add(new BigDecimal(actualPayables)).setScale(2, BigDecimal.ROUND_CEILING));
	}
}
