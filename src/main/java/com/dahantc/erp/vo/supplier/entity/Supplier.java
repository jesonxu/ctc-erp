package com.dahantc.erp.vo.supplier.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "erp_supplier")
@DynamicUpdate(true)
public class Supplier implements Serializable {

	private static final long serialVersionUID = -8492398660535402778L;

	@Id
	@Column(name = "supplierid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String supplierId;

	@Column(name = "companyname", length = 255)
	private String companyName;

	/** 供应商类型 SupplierTypeId */
	@Column(name = "suppliertypeid", length = 32)
	private String supplierTypeId;

	@Column(name = "legalperson", length = 255)
	private String legalPerson;

	@Column(name = "registrationnumber", length = 255)
	private String registrationNumber;

	@Column(name = "registrationaddress", length = 255)
	private String registrationAddress;

	@Column(name = "postaladdress", length = 255)
	private String postalAddress;

	@Column(name = "telephonenumber", length = 255)
	private String telephoneNumber;

	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "website", length = 255)
	private String website;

	@Column(name = "contactname", length = 255)
	private String contactName;

	@Column(name = "contactphone", length = 255)
	private String contactPhone;

	@Column(name = "creationdate")
	private Timestamp creationDate = new Timestamp(System.currentTimeMillis());

	@Column(name = "registeredcapital", length = 32)
	private String registeredCapital;

	@Column(name = "corporatenature", length = 32)
	private String corporateNature;

	@Column(name = "taxation", length = 32)
	private String taxation;

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	@Column(name = "deptid", length = 32)
	private String deptId;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "companyqualification", columnDefinition = "varchar(32) COMMENT '公司资质'")
	private String companyQualification;

	@Column(name = "legalrisk", columnDefinition = "varchar(32)  COMMENT '法律风险'")
	private String legalRisk;

	@Column(name = "deliverycycle", columnDefinition = "varchar(32) COMMENT '正常交货周期'")
	private String deliveryCycle;

	@Column(name = "cooperationtype", columnDefinition = "varchar(32) COMMENT '合作形式'")
	private String cooperationType;

	@Column(name = "settlementtype", columnDefinition = "varchar(32) COMMENT '结算方式'")
	private String settlementType;

	@Column(name = "saletype", columnDefinition = "varchar(32) COMMENT '销售方式'")
	private String saleType;

	@Column(name = "certification", columnDefinition = "varchar(32) COMMENT '相关技术或资质认证'")
	private String certification;

	@Column(name = "contractfiles", columnDefinition = "varchar(1000) COMMENT '认证文件'")
	private String contractFiles;

	@Column(name = "corporatecredit", columnDefinition = "varchar(255) COMMENT '法人征信'")
	private String corporateCredit;

	@Column(name = "productrange", columnDefinition = "varchar(255) COMMENT '产品范围'")
	private String productRange;

	@Column(name = "advantageproduct", columnDefinition = "varchar(255) COMMENT '优势产品'")
	private String advantageProduct;

	@Column(name = "logistics", columnDefinition = "varchar(255) COMMENT '配送物流'")
	private String logistics;

	@Column(name = "casecontract", columnDefinition = "varchar(255) COMMENT '合作客户案例合同'")
	private String caseContract;

	@Column(name = "companyintroduction", columnDefinition = "varchar(32) COMMENT '行业水平及外部评价'")
	private String companyIntroduction;

	@Column(name = "annualincome", columnDefinition = "varchar(32) COMMENT '近两年任一年度主营收入'")
	private String annualIncome;

	@Column(name = "isincomeprove", columnDefinition = "varchar(32) COMMENT '是否提供有效营收证明'")
	private String isIncomeProve;

	@Column(name = "managecertificationfile", columnDefinition = "varchar(1000) COMMENT '公司管理相关认证'")
	private String manageCertificationFile;

	@Column(name = "financialfile", columnDefinition = "varchar(1000) COMMENT '纳税证明或完整的审计报告或上市公司财报等'")
	private String financialFile;

	@Column(name = "status", columnDefinition = "int(2) default 0 COMMENT '供应商助状态 0:审核通过 1：待审核'")
	private int status;
	/**
	 * 用以按各种条件搜索的关键词
	 */
	@Column(name = "keywords", columnDefinition = "TEXT")
	private String keyWords;

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
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

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
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

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
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

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getSupplierTypeId() {
		return supplierTypeId;
	}

	public void setSupplierTypeId(String supplierTypeId) {
		this.supplierTypeId = supplierTypeId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 生成关键字
	 */
	public void buildKeyWords() {
		StringBuffer kw = new StringBuffer(getCompanyName());
		kw.append(",").append(getLegalPerson());
		kw.append(",").append(getRegistrationNumber());
		kw.append(",").append(getRegistrationAddress());
		kw.append(",").append(getPostalAddress());
		kw.append(",").append(getTelephoneNumber());
		kw.append(",").append(getEmail());
		kw.append(",").append(getWebsite());
		kw.append(",").append(getContactName());
		kw.append(",").append(getContactPhone());
		kw.append(",").append(getTaxation());
		setKeyWords(kw.toString());
	}

	@Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;//地址相等
        }

        if(obj == null){
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }

        if(obj instanceof Supplier){
        	Supplier other = (Supplier) obj;
            //需要比较的字段相等，则这两个对象相等
            if(equalsStr(this.supplierId, other.supplierId) && equalsStr(this.companyName, other.companyName)
            		&& equalsStr(this.supplierTypeId, other.supplierTypeId)&& equalsStr(this.legalPerson, other.legalPerson)
            		&& equalsStr(this.registrationNumber, other.registrationNumber)&& equalsStr(this.registrationAddress, other.registrationAddress)
            		&& equalsStr(this.telephoneNumber, other.telephoneNumber)&& equalsStr(this.email, other.email)
            		&& equalsStr(this.website, other.website)&& equalsStr(this.contactName, other.contactName)
            		&& equalsStr(this.contactPhone, other.contactPhone)&& equalsStr(this.financialFile, other.financialFile)
            		&& equalsStr(this.registeredCapital, other.registeredCapital)&& equalsStr(this.corporateNature, other.corporateNature)
            		&& equalsStr(this.taxation, other.taxation)&& equalsStr(this.ossUserId, other.ossUserId)
            		&& equalsStr(this.deptId, other.deptId)&& equalsStr(this.companyQualification, other.companyQualification)
            		&& equalsStr(this.legalRisk, other.legalRisk)&& equalsStr(this.deliveryCycle, other.deliveryCycle)
            		&& equalsStr(this.cooperationType, other.cooperationType)&& equalsStr(this.settlementType, other.settlementType)
            		&& equalsStr(this.saleType, other.saleType)&& equalsStr(this.certification, other.certification)
            		&& equalsStr(this.contractFiles, other.contractFiles)&& equalsStr(this.corporateCredit, other.corporateCredit)
            		&& equalsStr(this.productRange, other.productRange)&& equalsStr(this.advantageProduct, other.advantageProduct)
            		&& equalsStr(this.logistics, other.logistics)&& equalsStr(this.caseContract, other.caseContract)
            		&& equalsStr(this.companyIntroduction, other.companyIntroduction)&& equalsStr(this.annualIncome, other.annualIncome)
            		&& equalsStr(this.isIncomeProve, other.isIncomeProve)&& equalsStr(this.manageCertificationFile, other.manageCertificationFile)){
                return true;
            }
        }
        return false;
    }

	private boolean equalsStr(String str1, String str2) {
		if (StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)) {
			return true;
		}
		if (!StringUtils.isEmpty(str1) && str1.equals(str2)) {
			return true;
		}
		return false;
	}

}