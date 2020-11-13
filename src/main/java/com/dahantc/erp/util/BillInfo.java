package com.dahantc.erp.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BillInfo {

	private String companyName;
	// 客户联系人
	private String contactsName;
	// 客户联系方式
	private String phone;
	private Date billDate;
	private String billNumber;
	// 产品下每个账号的成功数等信息
	private List<DetailInfo> accountInfos;
	// 实际总计
	private DetailInfo realFeeInfo;
	// 出账日期
	private Date createDate;
	private Date finalPayDate;
	private String saleName;
	private String salePhone;
	private String productType;

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactsName() {
		return contactsName;
	}

	public void setContactsName(String contactsName) {
		this.contactsName = contactsName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public List<DetailInfo> getAccountInfos() {
		return accountInfos;
	}

	public void setAccountInfos(List<DetailInfo> accountInfos) {
		this.accountInfos = accountInfos;
	}

	public DetailInfo getRealFeeInfo() {
		return realFeeInfo;
	}

	public void setRealFeeInfo(DetailInfo realFeeInfo) {
		this.realFeeInfo = realFeeInfo;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getFinalPayDate() {
		return finalPayDate;
	}

	public void setFinalPayDate(Date finalPayDate) {
		this.finalPayDate = finalPayDate;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public String getSalePhone() {
		return salePhone;
	}

	public void setSalePhone(String salePhone) {
		this.salePhone = salePhone;
	}
	
	/**
	 * 账号成功数等信息
	 */
	public static class DetailInfo {

		private String accountName;
		private BigDecimal unitPrice;
		private BigDecimal feeCount;
		private BigDecimal fee;

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}

		public BigDecimal getFeeCount() {
			return feeCount;
		}

		public void setFeeCount(BigDecimal feeCount) {
			this.feeCount = feeCount;
		}

		public BigDecimal getFee() {
			return fee;
		}

		public void setFee(BigDecimal fee) {
			this.fee = fee;
		}

	}

}