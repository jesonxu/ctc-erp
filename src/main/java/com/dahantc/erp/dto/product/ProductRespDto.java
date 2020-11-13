package com.dahantc.erp.dto.product;

import java.io.Serializable;

/**
 * 产品添加请求参数
 * 
 */
public class ProductRespDto implements Serializable {

	private static final long serialVersionUID = -4088960873030272802L;
	
	private Long flowEntCount;

	private String productId;

	private String supplierId;

	private String productName;

	private int productType;

	private String productTypeName;

	private String productParam;

	private String productMark;

	private double lowdissipation;
	
	private long unitvalue;

	private String reachProvince;

	private String baseProvince;

	private int settleType;

	private int rewardType;

	private double rewardRatio;
	
	private int currencyType;
	
	private String priceTimeQuantum;
	
	private String price;

	private boolean directConnect;

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

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String getProductParam() {
		return productParam;
	}

	public void setProductParam(String productParam) {
		this.productParam = productParam;
	}

	public String getProductMark() {
		return productMark;
	}

	public void setProductMark(String productMark) {
		this.productMark = productMark;
	}

	public double getLowdissipation() {
		return lowdissipation;
	}

	public void setLowdissipation(double lowdissipation) {
		this.lowdissipation = lowdissipation;
	}

	public long getUnitvalue() {
		return unitvalue;
	}

	public void setUnitvalue(long unitvalue) {
		this.unitvalue = unitvalue;
	}

	public String getReachProvince() {
		return reachProvince;
	}

	public void setReachProvince(String reachProvince) {
		this.reachProvince = reachProvince;
	}

	public String getBaseProvince() {
		return baseProvince;
	}

	public void setBaseProvince(String baseProvince) {
		this.baseProvince = baseProvince;
	}

	public int getSettleType() {
		return settleType;
	}

	public void setSettleType(int settleType) {
		this.settleType = settleType;
	}

	public int getRewardType() {
		return rewardType;
	}

	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}

	public double getRewardRatio() {
		return rewardRatio;
	}

	public void setRewardRatio(double rewardRatio) {
		this.rewardRatio = rewardRatio;
	}

	public int getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(int currencyType) {
		this.currencyType = currencyType;
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

	public boolean isDirectConnect() {
		return directConnect;
	}

	public void setDirectConnect(boolean directConnect) {
		this.directConnect = directConnect;
	}
}
