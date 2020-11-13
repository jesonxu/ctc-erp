package com.dahantc.erp.dto.customer;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.entity.Customer;

/**
 * 添加客户参数实体类
 * 
 * @author 8520
 */
public class AddCustomerReqDto implements Serializable {

	private static final long serialVersionUID = 2024260693085023164L;

	private String customerId;

	@NotBlank(message = "公司名称不能为空")
	private String companyName;

	private String legalPerson;

	private String registrationNumber;

	private String registrationAddress;

	private String postalAddress;

	private String telePhoneNumber;

	private String email;

	private String website;

	@NotBlank(message = "客户联系人不能为空")
	private String contactName;
	
	private String contactDept;

	private String contactPosition;

	private String contactTelephone;

	@NotBlank(message = "联系人手机不能为空")
	private String contactPhone;

	private String creationDate;

	private String registeredCapital;

	private String corporateNature;

	private String taxation;

	private String customerTypeId;

	private String contractFiles;

	private String contractDate;

	private String useDate;

	// @NotBlank(message = "开票信息不能为空")
	private String invoiceInfos;

	// @NotBlank(message = "银行信息不能为空")
	private String bankInfos;

	private String delInvoiceIds;

	private String delBankIds;

	private int customerRegion;

	// @NotBlank(message = "公司情况介绍不能为空")
	private String companyIntroduction;

	// @NotBlank(message = "业务应用模式不能为空")
	private String businessMode;

	@NotBlank(message = "我方银行信息不能为空")
	private String bankAccountId;

	// 名片路径
	private String businessCardPath;

	public void toCustomer(Customer customer) {
		customer.setCustomerId(this.getCustomerId());
		customer.setCompanyName(this.getCompanyName());
		customer.setLegalPerson(this.getLegalPerson());
		customer.setRegistrationNumber(this.getRegistrationNumber());
		customer.setRegistrationAddress(this.getRegistrationAddress());
		customer.setTelePhoneNumber(this.getTelePhoneNumber());
		customer.setPostalAddress(this.getPostalAddress());
		customer.setEmail(this.getEmail());
		customer.setWebsite(this.getWebsite());
		customer.setContactName(this.getContactName());
		customer.setContactPhone(this.getContactPhone());
		customer.setCustomerTypeId(this.getCustomerTypeId());
		customer.setRegisteredCapital(this.getRegisteredCapital());
		customer.setCorporateNature(this.getCorporateNature());
		customer.setTaxation(this.getTaxation());
		customer.setCustomerRegion(this.getCustomerRegion());
		customer.setCompanyIntroduction(this.getCompanyIntroduction());
		customer.setBusinessMode(this.getBusinessMode());
		customer.setBankAccountId(this.getBankAccountId());
		if (StringUtils.isNotBlank(creationDate)) {
			customer.setCreationDate(new Timestamp(DateUtil.convert1(this.getCreationDate()).getTime()));
		}
		if (StringUtils.isNotBlank(contractDate)) {
			customer.setContractDate(new Timestamp(DateUtil.convert1(this.getContractDate()).getTime()));
		}
		if (StringUtils.isNotBlank(useDate)) {
			customer.setUseDate(new Timestamp(DateUtil.convert1(this.getUseDate()).getTime()));
		}
		customer.setContractFiles(this.getContractFiles());
		customer.setContactPosition(contactPosition);
		customer.setContactTelephone(contactTelephone);
		customer.setContactDept(contactDept);
		customer.setBusinessCardPath(businessCardPath);
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

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
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

	public String getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(String customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	public String getContractFiles() {
		return contractFiles;
	}

	public void setContractFiles(String contractFiles) {
		this.contractFiles = contractFiles;
	}

	public String getInvoiceInfos() {
		return invoiceInfos;
	}

	public void setInvoiceInfos(String invoiceInfos) {
		this.invoiceInfos = invoiceInfos;
	}

	public String getBankInfos() {
		return bankInfos;
	}

	public void setBankInfos(String bankInfos) {
		this.bankInfos = bankInfos;
	}

	public String getDelInvoiceIds() {
		return delInvoiceIds;
	}

	public void setDelInvoiceIds(String delInvoiceIds) {
		this.delInvoiceIds = delInvoiceIds;
	}

	public String getDelBankIds() {
		return delBankIds;
	}

	public void setDelBankIds(String delBankIds) {
		this.delBankIds = delBankIds;
	}

	public String getContractDate() {
		return contractDate;
	}

	public String getUseDate() {
		return useDate;
	}

	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}

	public void setUseDate(String useDate) {
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

	public String getBusinessCardPath() {
		return businessCardPath;
	}

	public void setBusinessCardPath(String businessCardPath) {
		this.businessCardPath = businessCardPath;
	}
}
