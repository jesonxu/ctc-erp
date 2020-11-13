package com.dahantc.erp.dto.customer;

import java.io.Serializable;
import java.util.Date;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.entity.Customer;

public class CustomerDetailRspDto implements Serializable {

	private static final long serialVersionUID = -676166504303911691L;

	private String customerId;

	/**
	 * 公司名
	 **/
	private String companyName;

	/**
	 * 法人
	 */
	private String legalPerson;

	/**
	 * 公司注册号
	 */
	private String registrationNumber;

	/**
	 * 公司注册地址
	 */
	private String registrationAddress;

	/**
	 * 通讯地址
	 */
	private String postalAddress;

	/**
	 * 电话号码
	 */
	private String telePhoneNumber;

	/**
	 * 电子邮件（公司的）
	 */
	private String email;

	/**
	 * 网站
	 */
	private String website;

	/**
	 * 业务联系人名称
	 */
	private String contactName;

	/**
	 * 业务联系电话
	 */
	private String contactPhone;

	/**
	 * 成立时间
	 */
	private String creationDate;

	/**
	 * 注册资本
	 */
	private String registeredCapital;

	/**
	 * 公司性质
	 */
	private String corporateNature;

	/**
	 * 税务号
	 */
	private String taxation;

	/**
	 * 创建用户
	 */
	private String creatUser;

	private String contactDept;

	private String contactPosition;

	private String contactTelephone;
	private String businessCardPath;

	public CustomerDetailRspDto() {

	}

	public CustomerDetailRspDto(Customer cus) {
		this.customerId = cus.getCustomerId();
		this.companyName = cus.getCompanyName();
		this.legalPerson = cus.getLegalPerson();
		this.registrationNumber = cus.getRegistrationNumber();
		this.registrationAddress = cus.getRegistrationAddress();
		this.postalAddress = cus.getPostalAddress();
		this.telePhoneNumber = cus.getTelePhoneNumber();
		this.email = cus.getEmail();
		this.website = cus.getWebsite();
		this.contactName = cus.getContactName();
		this.contactPhone = cus.getContactPhone();
		Date creationDate = cus.getCreationDate();
		this.setCreationDate(creationDate != null ? DateUtil.convert(creationDate, DateUtil.format1) : "");
		this.registeredCapital = cus.getRegisteredCapital();
		this.corporateNature = cus.getCorporateNature();
		this.taxation = cus.getTaxation();
		this.contactDept = cus.getContactDept();
		this.contactPosition = cus.getContactPosition();
		this.contactTelephone = cus.getContactTelephone();
		this.businessCardPath = cus.getBusinessCardPath();
	}

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

	public String getCustomerId() {
		return customerId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getLegalPerson() {
		return legalPerson;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public String getRegistrationAddress() {
		return registrationAddress;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public String getTelePhoneNumber() {
		return telePhoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getWebsite() {
		return website;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getRegisteredCapital() {
		return registeredCapital;
	}

	public String getCorporateNature() {
		return corporateNature;
	}

	public String getTaxation() {
		return taxation;
	}

	public String getCreatUser() {
		return creatUser;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setLegalPerson(String legalPerson) {
		this.legalPerson = legalPerson;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public void setRegistrationAddress(String registrationAddress) {
		this.registrationAddress = registrationAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public void setTelePhoneNumber(String telePhoneNumber) {
		this.telePhoneNumber = telePhoneNumber;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public void setRegisteredCapital(String registeredCapital) {
		this.registeredCapital = registeredCapital;
	}

	public void setCorporateNature(String corporateNature) {
		this.corporateNature = corporateNature;
	}

	public void setTaxation(String taxation) {
		this.taxation = taxation;
	}

	public void setCreatUser(String creatUser) {
		this.creatUser = creatUser;
	}

	public String getBusinessCardPath() {
		return businessCardPath;
	}

	public void setBusinessCardPath(String businessCardPath) {
		this.businessCardPath = businessCardPath;
	}
}
