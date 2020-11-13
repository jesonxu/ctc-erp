package com.dahantc.erp.vo.customer.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;

@Entity
@Table(name = "erp_customer")
@DynamicUpdate(true)
public class Customer implements Serializable {

	private static final long serialVersionUID = 4270337061201901212L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
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
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

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
	 * 用以按各种条件搜索的关键词
	 */
	@Column(name = "keywords", columnDefinition = "TEXT")
	private String keyWords;

	/**
	 * 绑定的公司银行信息
	 */
	@Column(name = "bankaccountid", length = 32)
	private String bankAccountId;

	/** 客户联系人部门 */
	@Column(name = "customerdept", length = 50)
	private String contactDept;

	/** 客户联系人职位 */
	@Column(name = "contactposition", length = 50)
	private String contactPosition;

	/** 客户联系人座机 */
	@Column(name = "contacttelephone", length = 50)
	private String contactTelephone;

	/** 公司联系人名片图片路径 */
	@Column(name = "businesscardpath", length = 1000)
	private String businessCardPath;

	public String getContactDept() {
		return contactDept;
	}

	public void setContactDept(String contactDept) {
		this.contactDept = contactDept;
	}

	public String getContactPosition() {
		return contactPosition;
	}

	public void setContactPosition(String contactPosition) {
		this.contactPosition = contactPosition;
	}

	public String getContactTelephone() {
		return contactTelephone;
	}

	public void setContactTelephone(String contactTelephone) {
		this.contactTelephone = contactTelephone;
	}

	public String getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
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

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
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

	public Timestamp getContractDate() {
		return contractDate;
	}

	public Timestamp getUseDate() {
		return useDate;
	}

	public void setContractDate(Timestamp contractDate) {
		this.contractDate = contractDate;
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

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getBusinessCardPath() {
		return businessCardPath;
	}

	public void setBusinessCardPath(String businessCardPath) {
		this.businessCardPath = businessCardPath;
	}

	@Override
	public String toString() {
		return "Customer{" +
				"customerId='" + customerId + '\'' +
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
				", wtime=" + wtime +
				", customerTypeId='" + customerTypeId + '\'' +
				", deptId='" + deptId + '\'' +
				", contractFiles='" + contractFiles + '\'' +
				", contractDate=" + contractDate +
				", useDate=" + useDate +
				", customerRegion=" + customerRegion +
				", companyIntroduction='" + companyIntroduction + '\'' +
				", businessMode='" + businessMode + '\'' +
				", keyWords='" + keyWords + '\'' +
				", bankAccountId='" + bankAccountId + '\'' +
				", contactDept='" + contactDept + '\'' +
				", contactPosition='" + contactPosition + '\'' +
				", contactTelephone='" + contactTelephone + '\'' +
				", businessCardPath='" + businessCardPath + '\'' +
				'}';
	}

	/**
	 * 生成关键字
	 */
	public void buildKeyWords(String realName) throws Exception {
		try {
			StringBuffer kw = new StringBuffer(getCompanyName());
			kw.append(",").append(getLegalPerson());
			kw.append(",").append(getRegistrationNumber());
			kw.append(",").append(getRegistrationAddress());
			kw.append(",").append(getPostalAddress());
			kw.append(",").append(getTelePhoneNumber());
			kw.append(",").append(getEmail());
			kw.append(",").append(getWebsite());
			kw.append(",").append(getContactName());
			kw.append(",").append(getContactPhone());
			kw.append(",").append(getTaxation());
			kw.append(",").append(getCompanyIntroduction());
			String files = getContractFiles();
			if (StringUtil.isNotBlank(files)) {
				JSONArray fileArray = JSON.parseArray(files);
				if (fileArray.size() > 0) {
					for (int i = 0; i < fileArray.size(); i++) {
						JSONObject file = fileArray.getJSONObject(i);
						kw.append(",").append(file.getString("fileName"));
					}
				}
			}
			setKeyWords(kw.toString());
		} catch (Exception e) {
			setKeyWords(null);
			throw e;
		}
	}
}
