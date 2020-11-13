package com.dahantc.erp.dto.monthBills;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MonthBillsDto {

	private String billsId;
	private String billsName;
	private String productName;
	private String billsDate;
	private String billsNumber;
	private BigDecimal receivables;
	private String billsTime;
	private String finalReceiveTime;
	private String writeOffTime;
	private String billStatus;
	private List<Receive> receiveInfo = new ArrayList<>();
	private BigDecimal remainReceive;
	private BigDecimal penaltyInterest;
	private int penaltyInterestDays;

	public String getBillsId() {
		return billsId;
	}

	public void setBillsId(String billsId) {
		this.billsId = billsId;
	}

	public String getBillsName() {
		return billsName;
	}

	public void setBillsName(String billsName) {
		this.billsName = billsName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getBillsDate() {
		return billsDate;
	}

	public void setBillsDate(String billsDate) {
		this.billsDate = billsDate;
	}

	public String getBillsNumber() {
		return billsNumber;
	}

	public void setBillsNumber(String billsNumber) {
		this.billsNumber = billsNumber;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public String getBillsTime() {
		return billsTime;
	}

	public void setBillsTime(String billsTime) {
		this.billsTime = billsTime;
	}

	public String getFinalReceiveTime() {
		return finalReceiveTime;
	}

	public void setFinalReceiveTime(String finalReceiveTime) {
		this.finalReceiveTime = finalReceiveTime;
	}

	public String getWriteOffTime() {
		return writeOffTime;
	}

	public void setWriteOffTime(String writeOffTime) {
		this.writeOffTime = writeOffTime;
	}

	public String getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}

	public List<Receive> getReceiveInfo() {
		return receiveInfo;
	}

	public void setReceiveInfo(List<Receive> receiveInfo) {
		this.receiveInfo = receiveInfo;
	}

	public BigDecimal getRemainReceive() {
		return remainReceive;
	}

	public void setRemainReceive(BigDecimal remainReceive) {
		this.remainReceive = remainReceive;
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

	public static class Receive {

		private String receiveTime;
		private BigDecimal receive;

		public Receive(String receiveTime, BigDecimal receive) {
			super();
			this.receiveTime = receiveTime;
			this.receive = receive;
		}

		public Receive() {

		}

		public String getReceiveTime() {
			return receiveTime;
		}

		public void setReceiveTime(String receiveTime) {
			this.receiveTime = receiveTime;
		}

		public BigDecimal getReceive() {
			return receive;
		}

		public void setReceive(BigDecimal receive) {
			this.receive = receive;
		}

	}

}