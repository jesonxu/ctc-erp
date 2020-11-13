package com.dahantc.erp.dto.bill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品一天的发送量详情
 */
public class DateDetail implements Serializable {

	private static final long serialVersionUID = -5833697011699301262L;

	private String date;

	private String productName;

	private String loginName;

	private long totalCount = 0;

	private long successCount = 0;

	private long failCount = 0;

	private BigDecimal successRatio = BigDecimal.ZERO;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getFailCount() {
		return failCount;
	}

	public void setFailCount(long failCount) {
		this.failCount = failCount;
	}

	public BigDecimal getSuccessRatio() {
		return successRatio;
	}

	public void setSuccessRatio(BigDecimal successRatio) {
		this.successRatio = successRatio;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}
