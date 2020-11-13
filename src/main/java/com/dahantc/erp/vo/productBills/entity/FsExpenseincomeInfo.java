package com.dahantc.erp.vo.productBills.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.dahantc.erp.util.DateUtil;

public class FsExpenseincomeInfo {

	private String fsExpenseIncomeId;
	private String thisCost;
	private String wtime;
	private BigDecimal penaltyInterestRatio = BigDecimal.ZERO;

	public BigDecimal getPenaltyInterestRatio() {
		return penaltyInterestRatio;
	}

	public void setPenaltyInterestRatio(BigDecimal penaltyInterestRatio) {
		this.penaltyInterestRatio = penaltyInterestRatio;
	}

	public String getFsExpenseIncomeId() {
		return fsExpenseIncomeId;
	}

	public void setFsExpenseIncomeId(String fsExpenseIncomeId) {
		this.fsExpenseIncomeId = fsExpenseIncomeId;
	}

	public String getThisCost() {
		return thisCost;
	}

	public void setThisCost(String thisCost) {
		this.thisCost = thisCost;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public BigDecimal getThisCostNumber() {
		if (StringUtils.isBlank(this.thisCost) || !NumberUtils.isParsable(thisCost)) {
			return null;
		}
		return new BigDecimal(thisCost);
	}

	public Date getWriteOffDate() {
		if (StringUtils.isBlank(this.wtime)) {
			return null;
		}
		return DateUtil.convert1(this.wtime);
	}

	public BigDecimal calculatePenaltyInterest(int dayDiffer, BigDecimal penaltyInterestRatio) {
		BigDecimal thisCost = getThisCostNumber();
		if (this.penaltyInterestRatio.signum() <= 0 && penaltyInterestRatio != null) {
			this.penaltyInterestRatio = penaltyInterestRatio;
		}
		if (dayDiffer <= 0 || this.penaltyInterestRatio.signum() <= 0 || thisCost.signum() <= 0) {
			return BigDecimal.ZERO;
		}
		return thisCost.multiply(this.penaltyInterestRatio).multiply(new BigDecimal(dayDiffer));
	}

}