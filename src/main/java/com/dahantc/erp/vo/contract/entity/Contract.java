package com.dahantc.erp.vo.contract.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.dahantc.erp.enums.ContractFlowStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.PayType;

/**
 * 合同表
 */
@Entity
@Table(name = "erp_contract")
@DynamicUpdate(true)
public class Contract implements Serializable {

	private static final long serialVersionUID = -8682335951986123715L;

	// 合同编号
	@Id
	@Column(name = "contractid", length = 32)
	private String contractId;

	// 合同名称
	@Column(name = "contractname", length = 255)
	private String contractName;

	// 合同评审状态
	@Column(name = "status")
	private int status = ContractFlowStatus.APPLYING.getCode();

	// 申请日期
	@Column(name = "applydate")
	private Timestamp applyDate = new Timestamp(System.currentTimeMillis());

	// 创建日期
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	// 申请人
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 申请人部门
	@Column(name = "deptid", length = 32)
	private String deptId;

	// 实体类型，默认是客户
	@Column(name = "entitytype")
	private int entityType = EntityType.CUSTOMER.ordinal();

	// 客户/供应商id
	@Column(name = "entityid", length = 32)
	private String entityId;

	// 客户/供应商名称
	@Column(name = "entityname", length = 255)
	private String entityName;

	// 客户/供应商区域
	@Column(name = "entityregion")
	private int entityRegion;

	// 合同归属区域
	@Column(name = "contractregion", length = 32)
	private String contractRegion;

	// 联系人
	@Column(name = "contactname", length = 255)
	private String contactName;

	// 联系方式
	@Column(name = "contactphone", length = 255)
	private String contactPhone;

	// 联系地址
	@Column(name = "address", length = 255)
	private String address;

	// 合同类型
	@Column(name = "contracttype", length = 32)
	private String contractType;

	// 产品类型
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	// 付费方式
	@Column(name = "settletype", columnDefinition = "int default 0")
	private int settleType = PayType.Advance.ordinal();

	// 月发送量
	@Column(name = "monthcount", length = 255)
	private String monthCount;

	// 合同金额
	@Column(name = "contractamount")
	private String contractAmount;

	// 单价
	@Column(name = "price", length = 1000)
	private String price;

	// 项目负责人
	@Column(name = "projectleader", length = 255)
	private String projectLeader;

	// 合同有效期（开始日期）
	@Column(name = "validitydatestart")
	private Timestamp validityDateStart = new Timestamp(System.currentTimeMillis());

	// 合同有效期（结束日期）
	@Column(name = "validitydateend")
	private Timestamp validityDateEnd = new Timestamp(System.currentTimeMillis());

	// 项目情况说明
	@Column(name = "description", length = 1000)
	private String description;

	// 已盖章合同的扫码电子档
	@Column(name = "contractfilesscan", columnDefinition = "TEXT")
	private String contractFilesScan;

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Timestamp applyDate) {
		this.applyDate = applyDate;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public int getEntityRegion() {
		return entityRegion;
	}

	public void setEntityRegion(int entityRegion) {
		this.entityRegion = entityRegion;
	}

	public String getContractRegion() {
		return contractRegion;
	}

	public void setContractRegion(String contractRegion) {
		this.contractRegion = contractRegion;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getSettleType() {
		return settleType;
	}

	public void setSettleType(int settleType) {
		this.settleType = settleType;
	}

	public String getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(String monthCount) {
		this.monthCount = monthCount;
	}

	public String getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(String contractAmount) {
		this.contractAmount = contractAmount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Timestamp getValidityDateStart() {
		return validityDateStart;
	}

	public void setValidityDateStart(Timestamp validityDateStart) {
		this.validityDateStart = validityDateStart;
	}

	public Timestamp getValidityDateEnd() {
		return validityDateEnd;
	}

	public void setValidityDateEnd(Timestamp validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectLeader() {
		return projectLeader;
	}

	public void setProjectLeader(String projectManager) {
		this.projectLeader = projectManager;
	}

	public String getContractFilesScan() {
		return contractFilesScan;
	}

	public void setContractFilesScan(String contractFilesScan) {
		this.contractFilesScan = contractFilesScan;
	}
}
