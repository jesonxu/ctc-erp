package com.dahantc.erp.vo.customerHistory.entity;

import com.dahantc.erp.util.DateUtil;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 客户历史记录表
 * 
 * @author 8520
 */
@Entity
@DynamicUpdate(true)
@Table(name = "erp_customer_history")
public class CustomerHistory implements Serializable {

	private static final long serialVersionUID = 8405395128594551732L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "customerhistoryid", length = 32)
	private String customerHistoryId;

	@Column(name = "customerid", length = 32)
	private String customerId;

	/**
	 * 公司名
	 **/
	@Column(name = "companyname")
	private String companyName;

	/**
	 * 法人
	 */
	@Column(name = "legalperson")
	private String legalPerson;

	/**
	 * 公司注册号
	 */
	@Column(name = "registrationnumber")
	private String registrationNumber;

	/**
	 * 公司注册地址
	 */
	@Column(name = "registrationaddress")
	private String registrationAddress;

	/**
	 * 通讯地址
	 */
	@Column(name = "postaladdress")
	private String postalAddress;

	/**
	 * 电话号码
	 */
	@Column(name = "telephonenumber")
	private String telePhoneNumber;

	/**
	 * 电子邮件（公司的）
	 */
	@Column(name = "email")
	private String email;

	/**
	 * 网站
	 */
	@Column(name = "website")
	private String website;

	/**
	 * 业务联系人名称
	 */
	@Column(name = "contactname")
	private String contactName;

	/**
	 * 业务联系电话
	 */
	@Column(name = "contactphone")
	private String contactPhone;

	/**
	 * 成立时间
	 */
	@Column(name = "creationdate")
	private Date creationDate;

	/**
	 * 注册资本
	 */
	@Column(name = "registeredcapital")
	private String registeredCapital;

	/**
	 * 公司性质
	 */
	@Column(name = "corporatenature")
	private String corporateNature;

	/**
	 * 税务号
	 */
	@Column(name = "taxation")
	private String taxation;

	/**
	 * 创建用户id
	 */
	@Column(name = "ossuserid", length = 32)
	private String ossuserId;

	/**
	 * 创建时间
	 */
	@Column(name = "wtime")
	private String wtime;

	/**
	 * 客户类型
	 */
	@Column(name = "customertypeid", length = 32)
	private String customerTypeId;

	/**
	 * 部门id（公司的部门）
	 */
	@Column(name = "deptid", length = 32)
	private String deptId;

	/**
	 * 合同（文件路径和文件原来文件名称 JSON数组字符串）
	 */
	@Column(name = "contractfiles", length = 2000)
	private String contractFiles;

	/**
	 * 合同日期
	 */
	@Column(name = "contractdate")
	private Timestamp contractDate;

	/**
	 * 开始使用日期
	 */
	@Column(name = "usedate")
	private Timestamp useDate;

	/**
	 * 客户地区
	 */
	@Column(name = "customerregion", columnDefinition = "int default 39")
	private int customerRegion;

	/**
	 * 公司情况介绍
	 */
	@Column(name = "companyintroduction", length = 1000)
	private String companyIntroduction;

	/**
	 * 业务应用模式
	 */
	@Column(name = "businessmode", length = 200)
	private String businessMode;
	
	/**
	 * 绑定的公司银行信息
	 */
	@Column(name = "bankaccountid")
	private String bankAccountId;
	
	public String getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	public String getCustomerHistoryId() {
		return customerHistoryId;
	}

	public void setCustomerHistoryId(String customerHistoryId) {
		this.customerHistoryId = customerHistoryId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getLegalPerson() {
		return legalPerson;
	}

	public void setLegalPerson(String legalPerson) {
		this.legalPerson = legalPerson;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getRegistrationAddress() {
		return registrationAddress;
	}

	public void setRegistrationAddress(String registrationAddress) {
		this.registrationAddress = registrationAddress;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getTelePhoneNumber() {
		return telePhoneNumber;
	}

	public void setTelePhoneNumber(String telePhoneNumber) {
		this.telePhoneNumber = telePhoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getRegisteredCapital() {
		return registeredCapital;
	}

	public void setRegisteredCapital(String registeredCapital) {
		this.registeredCapital = registeredCapital;
	}

	public String getCorporateNature() {
		return corporateNature;
	}

	public void setCorporateNature(String corporateNature) {
		this.corporateNature = corporateNature;
	}

	public String getTaxation() {
		return taxation;
	}

	public void setTaxation(String taxation) {
		this.taxation = taxation;
	}

	public String getOssuserId() {
		return ossuserId;
	}

	public void setOssuserId(String ossuserId) {
		this.ossuserId = ossuserId;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(String customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getContractFiles() {
		return contractFiles;
	}

	public void setContractFiles(String contractFiles) {
		this.contractFiles = contractFiles;
	}

	public String getCompanyIntroduction() {
		return companyIntroduction;
	}

	public void setCompanyIntroduction(String companyIntroduction) {
		this.companyIntroduction = companyIntroduction;
	}

	public String getBusinessMode() {
		return businessMode;
	}

	public void setBusinessMode(String businessMode) {
		this.businessMode = businessMode;
	}

	public Timestamp getContractDate() {
		return contractDate;
	}

	public void setContractDate(Timestamp contractDate) {
		this.contractDate = contractDate;
	}

	public Timestamp getUseDate() {
		return useDate;
	}

	public void setUseDate(Timestamp useDate) {
		this.useDate = useDate;
	}

	public int getCustomerRegion() {
		return customerRegion;
	}

	public void setCustomerRegion(int customerRegion) {
		this.customerRegion = customerRegion;
	}

	@Override
	public String toString() {
		return "CustomerHistory{" +
				"customerHistoryId='" + customerHistoryId + '\'' +
				", customerId='" + customerId + '\'' +
				", companyName='" + companyName + '\'' +
				", legalPerson='" + legalPerson + '\'' +
				", registrationNumber='" + registrationNumber + '\'' +
				", registrationAddress='" + registrationAddress + '\'' +
				", postalAddress='" + postalAddress + '\'' +
				", telePhoneNumber='" + telePhoneNumber + '\'' +
				", email='" + email + '\'' +
				", website='" + website + '\'' +
				", contactName='" + contactName + '\'' +
				", contactPhone='" + contactPhone + '\'' +
				", creationDate=" + creationDate +
				", registeredCapital='" + registeredCapital + '\'' +
				", corporateNature='" + corporateNature + '\'' +
				", taxation='" + taxation + '\'' +
				", ossuserId='" + ossuserId + '\'' +
				", wtime=" + DateUtil.convert(wtime, DateUtil.format1) +
				", customerTypeId='" + customerTypeId + '\'' +
				", deptId='" + deptId + '\'' +
				", contractDate=" + DateUtil.convert(contractDate, DateUtil.format1) +
				", useDate=" + DateUtil.convert(useDate, DateUtil.format1) +
				", customerRegion=" + customerRegion +
				", companyIntroduction='" + companyIntroduction + '\'' +
				", businessMode='" + businessMode + '\'' +
				", bankAccountId='" + bankAccountId + '\'' +
				'}';
	}
}