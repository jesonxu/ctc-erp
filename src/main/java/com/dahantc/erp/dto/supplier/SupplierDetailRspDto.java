package com.dahantc.erp.dto.supplier;

import java.io.Serializable;
import java.sql.Timestamp;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.supplier.entity.Supplier;

public class SupplierDetailRspDto implements Serializable {

	private static final long serialVersionUID = -7408567478275129055L;

	private String supplierId;

	private String companyName;

	private String legalPerson;

	private String registrationNumber;

	private String registrationAddress;

	private String postalAddress;

	private String telephoneNumber;

	private String email;

	private String website;

	private String contactName;

	private String contactPhone;

	private String creationDate;

	private String registeredCapital;

	private String corporateNature;

	private String taxation;

	private String wtime;

	private String supplierTypeId;

	// 创建者
	private String creatUser;
	
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

	public SupplierDetailRspDto(Supplier supplier) {
		this.setSupplierId(supplier.getSupplierId());
		this.setCompanyName(supplier.getCompanyName());
		this.setLegalPerson(supplier.getLegalPerson());
		this.setRegistrationNumber(supplier.getRegistrationNumber());
		this.setRegistrationAddress(supplier.getRegistrationAddress());
		this.setPostalAddress(supplier.getPostalAddress());
		this.setTelephoneNumber(supplier.getTelephoneNumber());
		this.setEmail(supplier.getEmail());
		this.setWebsite(supplier.getWebsite());
		this.setContactName(supplier.getContactName());
		this.setContactPhone(supplier.getContactPhone());
		Timestamp creationDate = supplier.getCreationDate();
		this.setCreationDate(creationDate != null ? DateUtil.convert(creationDate, DateUtil.format1) : "");
		this.setRegisteredCapital(supplier.getRegisteredCapital());
		this.setCorporateNature(supplier.getCorporateNature());
		this.setTaxation(supplier.getTaxation());
		this.setWtime(DateUtil.convert(supplier.getWtime(), DateUtil.format2));
		this.setSupplierTypeId(supplier.getSupplierTypeId());
		this.setCompanyIntroduction(supplier.getCompanyIntroduction());
		this.setLegalRisk(supplier.getLegalRisk());		
		this.setDeliveryCycle(supplier.getDeliveryCycle());
		this.setCooperationType(supplier.getCooperationType());
		this.setSettlementType(supplier.getSettlementType());
		this.setSaleType(supplier.getSaleType());
		this.setCertification(supplier.getCertification());
		this.setContractFiles(supplier.getContractFiles());
		this.setCorporateCredit(supplier.getCorporateCredit());
		this.setProductRange(supplier.getProductRange());
		this.setAdvantageProduct(supplier.getAdvantageProduct());
		this.setLogistics(supplier.getLogistics());
		this.setCaseContract(supplier.getCaseContract());
		this.setCompanyIntroduction(supplier.getCompanyIntroduction());
		this.setAnnualIncome(supplier.getAnnualIncome());
		this.setIsIncomeProve(supplier.getIsIncomeProve());
		this.setManageCertificationFile(supplier.getManageCertificationFile());
		this.setFinancialFile(supplier.getFinancialFile());
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

	public String getWtime() {
		return wtime;
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

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getSupplierTypeId() {
		return supplierTypeId;
	}

	public void setSupplierTypeId(String supplierTypeId) {
		this.supplierTypeId = supplierTypeId;
	}

	public String getCreatUser() {
		return creatUser;
	}

	public void setCreatUser(String creatUser) {
		this.creatUser = creatUser;
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
