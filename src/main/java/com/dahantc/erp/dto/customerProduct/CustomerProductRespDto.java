package com.dahantc.erp.dto.customerProduct;

import java.io.Serializable;

/**
 * 产品添加请求参数
 * 
 */
public class CustomerProductRespDto implements Serializable {

	private static final long serialVersionUID = -4088960873130272802L;

	private Long flowEntCount;

	private String productId;

	private String productName;

	private String account;

	private short productType;

	private String productTypeName;

	private int billType;

	private int billCycle;

	private int settleType;

	private String wTime;

	private String ossUserId;

	private String customerId;

	private int billTaskDay;

	private int voiceUnit;

	private String sendDemo;
	
	private String priceTimeQuantum;
	
	private String price;

	private int billPeriod;

	private boolean directConnect;

	private String yysType;

	private String yysTypeName;

	public Long getFlowEntCount() {
		return flowEntCount;
	}

	public void setFlowEntCount(Long flowEntCount) {
		this.flowEntCount = flowEntCount;
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

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
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

	public String getPriceTimeQuantum() {
		return priceTimeQuantum;
	}

	public void setPriceTimeQuantum(String priceTimeQuantum) {
		this.priceTimeQuantum = priceTimeQuantum;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getBillPeriod() {
		return billPeriod;
	}

	public void setBillPeriod(int billPeriod) {
		this.billPeriod = billPeriod;
	}

	public boolean isDirectConnect() {
		return directConnect;
	}

	public void setDirectConnect(boolean directConnect) {
		this.directConnect = directConnect;
	}

	public String getYysType() {
		return yysType;
	}

	public void setYysType(String yysType) {
		this.yysType = yysType;
	}

	public String getYysTypeName() {
		return yysTypeName;
	}

	public void setYysTypeName(String yysTypeName) {
		this.yysTypeName = yysTypeName;
	}
}
