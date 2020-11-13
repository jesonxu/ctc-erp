package com.dahantc.erp.dto.supplier;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.supplier.entity.Supplier;

public class AddSupplierReqDto implements Serializable {

	private static final long serialVersionUID = 5148557220777936157L;

	private String supplierId;

	@NotBlank(message = "公司名称不能为空")
	private String companyName;

	private String legalPerson;

	private String registrationNumber;

	private String registrationAddress;

	private String postalAddress;

	private String telephoneNumber;

	private String email;

	private String website;

	@NotBlank(message = "联系人不能为空")
	private String contactName;

	@NotBlank(message = "联系电话不能为空")
	private String contactPhone;

	private String creationDate;

	private String registeredCapital;

	private String corporateNature;

	private String taxation;

	private String supplierTypeId;
	
	private String delInvoiceIds;
	
	private String delBankIds;
	
	private String invoiceInfos;
	
	@NotBlank(message = "银行信息不能为空")
	private String bankInfos;
	
	private String companyQualification;
	
	private String legalRisk;

	private String deliveryCycle;
	
	private String cooperationType;
	
	private String settlementType;

	private String saleType;

	private String certification;
	
	private String contractFiles;
	
	private String corporateCredit;

	private String productRange;
	
	private String advantageProduct;

	private String logistics;

	private String caseContract;

	private String companyIntroduction;

	private String annualIncome;
	
	private String isIncomeProve;
	
	private String manageCertificationFile;

	private String financialFile;

	public void toSupplier(Supplier supplier) {
		supplier.setSupplierId(supplierId);
		supplier.setCompanyName(this.getCompanyName());
		supplier.setLegalPerson(this.getLegalPerson());
		supplier.setRegistrationNumber(this.getRegistrationNumber());
		supplier.setRegistrationAddress(this.getRegistrationAddress());
		supplier.setPostalAddress(this.getPostalAddress());
		supplier.setTelephoneNumber(this.getTelephoneNumber());
		supplier.setEmail(this.getEmail());
		supplier.setWebsite(this.getWebsite());
		supplier.setContactName(this.getContactName());
		supplier.setContactPhone(this.getContactPhone());
		supplier.setSupplierTypeId(this.supplierTypeId);
		if (StringUtils.isNotBlank(creationDate)) {
			supplier.setCreationDate(new Timestamp(DateUtil.convert1(this.getCreationDate()).getTime()));
		}
		supplier.setRegisteredCapital(this.getRegisteredCapital());
		supplier.setCorporateNature(this.getCorporateNature());
		supplier.setTaxation(this.getTaxation());
		supplier.setCompanyQualification(this.getCompanyQualification());
		supplier.setLegalRisk(this.getLegalRisk());
		supplier.setDeliveryCycle(this.getDeliveryCycle());
		supplier.setCooperationType(this.getCooperationType());
		supplier.setSettlementType(this.getSettlementType());
		supplier.setSaleType(this.getSaleType());
		supplier.setCertification(this.getCertification());
		supplier.setContractFiles(this.getContractFiles());
		supplier.setCorporateCredit(this.getCorporateCredit());
		supplier.setProductRange(this.getProductRange());		
		supplier.setAdvantageProduct(this.getAdvantageProduct());		
		supplier.setLogistics(this.getLogistics());
		supplier.setCaseContract(this.getCaseContract());		
		supplier.setCompanyIntroduction(this.getCompanyIntroduction());
		supplier.setAnnualIncome(this.getAnnualIncome());
		supplier.setIsIncomeProve(this.getIsIncomeProve());
		supplier.setManageCertificationFile(this.getManageCertificationFile());		
		supplier.setFinancialFile(this.getFinancialFile());		
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
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

	public String getTelephoneNumber() {
		return telephoneNumber;
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

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
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

	public String getSupplierTypeId() {
		return supplierTypeId;
	}

	public void setSupplierTypeId(String supplierTypeId) {
		this.supplierTypeId = supplierTypeId;
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

	public String getCompanyQualification() {
		return companyQualification;
	}

	public void setCompanyQualification(String companyQualification) {
		this.companyQualification = companyQualification;
	}

	public String getLegalRisk() {
		return legalRisk;
	}

	public void setLegalRisk(String legalRisk) {
		this.legalRisk = legalRisk;
	}

	public String getDeliveryCycle() {
		return deliveryCycle;
	}

	public void setDeliveryCycle(String deliveryCycle) {
		this.deliveryCycle = deliveryCycle;
	}

	public String getCooperationType() {
		return cooperationType;
	}

	public void setCooperationType(String cooperationType) {
		this.cooperationType = cooperationType;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getSaleType() {
		return saleType;
	}

	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getContractFiles() {
		return contractFiles;
	}

	public void setContractFiles(String contractFiles) {
		this.contractFiles = contractFiles;
	}

	public String getCorporateCredit() {
		return corporateCredit;
	}

	public void setCorporateCredit(String corporateCredit) {
		this.corporateCredit = corporateCredit;
	}

	public String getProductRange() {
		return productRange;
	}

	public void setProductRange(String productRange) {
		this.productRange = productRange;
	}

	public String getAdvantageProduct() {
		return advantageProduct;
	}

	public void setAdvantageProduct(String advantageProduct) {
		this.advantageProduct = advantageProduct;
	}

	public String getLogistics() {
		return logistics;
	}

	public void setLogistics(String logistics) {
		this.logistics = logistics;
	}

	public String getCaseContract() {
		return caseContract;
	}

	public void setCaseContract(String caseContract) {
		this.caseContract = caseContract;
	}

	public String getCompanyIntroduction() {
		return companyIntroduction;
	}

	public void setCompanyIntroduction(String companyIntroduction) {
		this.companyIntroduction = companyIntroduction;
	}

	public String getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(String annualIncome) {
		this.annualIncome = annualIncome;
	}

	public String getIsIncomeProve() {
		return isIncomeProve;
	}

	public void setIsIncomeProve(String isIncomeProve) {
		this.isIncomeProve = isIncomeProve;
	}

	public String getManageCertificationFile() {
		return manageCertificationFile;
	}

	public void setManageCertificationFile(String manageCertificationFile) {
		this.manageCertificationFile = manageCertificationFile;
	}

	public String getFinancialFile() {
		return financialFile;
	}

	public void setFinancialFile(String financialFile) {
		this.financialFile = financialFile;
	}

}
