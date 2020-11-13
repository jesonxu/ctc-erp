package com.dahantc.erp.dto.product;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

/**
 * 产品添加请求参数
 * 
 */
public class SaveProductReqDto implements Serializable {

	private static final long serialVersionUID = -4088960873030272802L;

	private String productId;

	@NotBlank(message = "供应商id不能为空")
	private String supplierId;

	@NotBlank(message = "产品名称不能为空")
	private String productName;

	private int productType;

	private String productParam;

	private String productMark;

	@NotBlank(message = "支持省份不能为空")
	private String reachProvince;

	private String lowdissipation;

	private String unitvalue;

	private int baseProvince;

	private int settleType;

	private int rewardType;

	private double rewardRatio;

	private String ossUserId;

	private String voiceUnit;

	private int currencyType;

	@NotBlank(message = "请选择是否直连")
	private String directConnect;

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

	public String getReachProvince() {
		return reachProvince;
	}

	public void setReachProvince(String reachProvince) {
		this.reachProvince = reachProvince;
	}

	public int getBaseProvince() {
		return baseProvince;
	}

	public void setBaseProvince(int baseProvince) {
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

	public String getLowdissipation() {
		return lowdissipation;
	}

	public void setLowdissipation(String lowdissipation) {
		this.lowdissipation = lowdissipation;
	}

	public String getUnitvalue() {
		return unitvalue;
	}

	public void setUnitvalue(String unitvalue) {
		this.unitvalue = unitvalue;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}
	
	public String getVoiceUnit() {
		return voiceUnit;
	}

	public void setVoiceUnit(String voiceUnit) {
		this.voiceUnit = voiceUnit;
	}

	public int getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(int currencyType) {
		this.currencyType = currencyType;
	}

	public String getDirectConnect() {
		return directConnect;
	}

	public void setDirectConnect(String directConnect) {
		this.directConnect = directConnect;
	}
}
