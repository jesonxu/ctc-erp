package com.dahantc.erp.dto.royalty;

import com.dahantc.erp.vo.royalty.entity.Royalty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

public class RoyaltyDto implements Serializable {

	private static final long serialVersionUID = 3717053546065654444L;

	public RoyaltyDto() {
		this.currentbalance = "0.00";
		this.profit = "0.00";
		this.royalty = "0.00";
	}

	public RoyaltyDto(Royalty royalty) {
		this.profit = royalty.getProfit().setScale(2, BigDecimal.ROUND_CEILING).toString();
		this.royalty = royalty.getRoyalty().setScale(2, BigDecimal.ROUND_CEILING).toString();
	}

	/**
	 * 时间类型，0-周，1-月，2-季，3-年，4-天
	 */
	private int dateType;

	/**
	 * 时间
	 */
	private String date;

	/**
	 * 客户余额
	 */
	private String currentbalance;

	/**
	 * 提成
	 */
	private String royalty;

	/**
	 * 利润
	 */
	private String profit;

	public int getDateType() {
		return dateType;
	}

	public void setDateType(int dateType) {
		this.dateType = dateType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCurrentbalance() {
		return currentbalance;
	}

	public void setCurrentbalance(String currentbalance) {
		this.currentbalance = currentbalance;
	}

	public String getRoyalty() {
		return royalty;
	}

	public void setRoyalty(String royalty) {
		this.royalty = royalty;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public void addCurrentbalance(String currentbalance) {
		if (StringUtils.isBlank(currentbalance)) {
			currentbalance = "0";
		}
		if (StringUtils.isBlank(this.currentbalance)) {
			this.currentbalance = "0";
		}
		this.currentbalance = String.format("%.2f", new BigDecimal(this.currentbalance).add(new BigDecimal(currentbalance)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addProfit(String profit) {
		if (StringUtils.isBlank(profit)) {
			profit = "0";
		}
		if (StringUtils.isBlank(this.profit)) {
			this.profit = "0";
		}
		this.profit = String.format("%.2f", new BigDecimal(this.profit).add(new BigDecimal(profit)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addRoyalty(String royalty) {
		if (StringUtils.isBlank(royalty)) {
			royalty = "0";
		}
		if (StringUtils.isBlank(this.royalty)) {
			this.royalty = "0";
		}
		this.royalty = String.format("%.2f", new BigDecimal(this.royalty).add(new BigDecimal(royalty)).setScale(2, BigDecimal.ROUND_CEILING));
	}
}
