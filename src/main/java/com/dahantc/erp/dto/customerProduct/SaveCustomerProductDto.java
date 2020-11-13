package com.dahantc.erp.dto.customerProduct;

import java.io.Serializable;
import java.sql.Timestamp;

public class SaveCustomerProductDto implements Serializable {

	private static final long serialVersionUID = -2882589085720631480L;

	private String productId;

	private String productName;

	private String account;

	private short productType;

	private Boolean directConnect = false;

	private int billType;

	private int billCycle;

	private int settleType;

	private String wTime;

	private String ossUserId;

	private String customerId;

	private int billTaskDay;

	private int voiceUnit;

	private String sendDemo;

	private Timestamp firstGenerateBillTime;

	private String yysType;

	public Timestamp getFirstGenerateBillTime() {
		return firstGenerateBillTime;
	}

	public void setFirstGenerateBillTime(Timestamp firstGenerateBillTime) {
		this.firstGenerateBillTime = firstGenerateBillTime;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public short getProductType() {
		return productType;
	}

	public void setProductType(short productType) {
		this.productType = productType;
	}

	public int getBillType() {
		return billType;
	}

	public void setBillType(int billType) {
		this.billType = billType;
	}

	public int getBillCycle() {
		return billCycle;
	}

	public void setBillCycle(int billCycle) {
		this.billCycle = billCycle;
	}

	public int getSettleType() {
		return settleType;
	}

	public void setSettleType(int settleType) {
		this.settleType = settleType;
	}

	public String getwTime() {
		return wTime;
	}

	public void setwTime(String wTime) {
		this.wTime = wTime;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getBillTaskDay() {
		return billTaskDay;
	}

	public void setBillTaskDay(int billTaskDay) {
		this.billTaskDay = billTaskDay;
	}

	public int getVoiceUnit() {
		return voiceUnit;
	}

	public void setVoiceUnit(int voiceUnit) {
		this.voiceUnit = voiceUnit;
	}

	public String getSendDemo() {
		return sendDemo;
	}

	public void setSendDemo(String sendDemo) {
		this.sendDemo = sendDemo;
	}

	public Boolean getDirectConnect() {
		return directConnect;
	}

	public void setDirectConnect(Boolean directConnect) {
		this.directConnect = directConnect;
	}

	public String getYysType() {
		return yysType;
	}

	public void setYysType(String yysType) {
		this.yysType = yysType;
	}
}
