package com.dahantc.erp.vo.billpenaltyinterest.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 产品账单表
 * 
 */
@Entity
@Table(name = "erp_bill_penalty_interest", indexes = { @Index(name = "bill_pi_bill", columnList = "billId"),
		@Index(name = "bill_pi_customerid", columnList = "customerId"), @Index(name = "bill_pi_pimonth", columnList = "penaltyInterestMonth"),
		@Index(name = "bill_pi_wtime", columnList = "wtime"), @Index(name = "bill_pi_customeridpim", columnList = "customerId,penaltyInterestMonth") })
public class BillPenaltyInterest implements Serializable {

	private static final long serialVersionUID = 6841467664601747186L;

	/**
	 * id billid + yyyyMM 形式
	 */
	@Id
	@Column(length = 38)
	private String id;

	@Column(length = 32, name = "billid")
	private String billId;

	/** 所属客户id */
	@Column(length = 32, name = "customerid")
	private String customerId;

	/** 罚息率 */
	@Column(name = "penaltyinterestratio", columnDefinition = "decimal(19,6) default 0", precision = 19, scale = 6)
	private BigDecimal penaltyInterestRatio;

	/** 罚息 */
	@Column(name = "penaltyinterest", columnDefinition = "decimal(19,6) default 0", precision = 19, scale = 6)
	private BigDecimal penaltyInterest;

	/** 这一个月算罚息天数 */
	@Column(name = "penaltyinterestdays", columnDefinition = "int default 0")
	private int penaltyInterestDays;

	/** 罚息月份（哪个月产生的罚息部分） */
	@Column(name = "penaltyinterestmonth")
	private Timestamp penaltyInterestMonth = new Timestamp(System.currentTimeMillis());

	/** 记录时间（罚息放到哪个月） */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public BigDecimal getPenaltyInterestRatio() {
		return penaltyInterestRatio;
	}

	public void setPenaltyInterestRatio(BigDecimal penaltyInterestRatio) {
		this.penaltyInterestRatio = penaltyInterestRatio;
	}

	public BigDecimal getPenaltyInterest() {
		return penaltyInterest;
	}

	public void setPenaltyInterest(BigDecimal penaltyInterest) {
		this.penaltyInterest = penaltyInterest;
	}

	public int getPenaltyInterestDays() {
		return penaltyInterestDays;
	}

	public void setPenaltyInterestDays(int penaltyInterestDays) {
		this.penaltyInterestDays = penaltyInterestDays;
	}

	public Timestamp getPenaltyInterestMonth() {
		return penaltyInterestMonth;
	}

	public void setPenaltyInterestMonth(Timestamp penaltyInterestMonth) {
		this.penaltyInterestMonth = penaltyInterestMonth;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
