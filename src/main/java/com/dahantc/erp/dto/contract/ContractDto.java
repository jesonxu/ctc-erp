package com.dahantc.erp.dto.contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dahantc.erp.enums.ContractFlowStatus;
import com.dahantc.erp.enums.SettleType;

public class ContractDto implements Serializable {

	private static final long serialVersionUID = 3179172332509396905L;

	// 合同编号
	private String contractId;

	// 合同名称
	private String contractName;

	// 合同评审状态
	private String status = ContractFlowStatus.APPLYING.getMsg();

	// 申请日期
	private String applyDate;

	// 创建日期
	private String wtime;

	// 申请人
	private String realName;

	// 申请人部门
	private String deptName;

	// 客户/供应商名称
	private String entityName;

	// 客户/供应商区域
	private String entityRegion;

	// 合同归属区域
	private String contractRegion;

	// 联系人
	private String contactName;

	// 联系方式
	private String contactPhone;

	// 联系地址
	private String address;

	// 合同类型
	private String contractType;

	// 产品类型
	private String productType;

	// 付费方式
	private String settleType = SettleType.Prepurchase.getDesc();

	// 月发送量
	private String monthCount;

	// 合同金额
	private String contractAmount;

	// 单价
	private String price;

	// 项目负责人
	private String projectLeader;

	// 合同有效期（开始日期）
	private String validityDateStart;

	// 合同有效期（结束日期）
	private String validityDateEnd;

	// 项目情况说明
	private String description;

	// 已盖章合同的扫码电子档
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityRegion() {
		return entityRegion;
	}

	public void setEntityRegion(String entityRegion) {
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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
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

	public String getProjectLeader() {
		return projectLeader;
	}

	public void setProjectLeader(String projectLeader) {
		this.projectLeader = projectLeader;
	}

	public String getValidityDateStart() {
		return validityDateStart;
	}

	public void setValidityDateStart(String validityDateStart) {
		this.validityDateStart = validityDateStart;
	}

	public String getValidityDateEnd() {
		return validityDateEnd;
	}

	public void setValidityDateEnd(String validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContractFilesScan() {
		return contractFilesScan;
	}

	public void setContractFilesScan(String contractFilesScan) {
		this.contractFilesScan = contractFilesScan;
	}

	public String[] toExportData(int length) {
		List<String> dataList = new ArrayList<>();
		dataList.add(contractId);
		dataList.add(contractName);
		dataList.add(status);
		dataList.add(applyDate);
		dataList.add(realName);
		dataList.add(deptName);
		dataList.add(entityRegion);
		dataList.add(entityName);
		dataList.add(contactName);
		dataList.add(contractRegion);
		dataList.add(contactPhone);
		dataList.add(address);
		dataList.add(contractType);
		dataList.add(productType);
		dataList.add(settleType);
		dataList.add(monthCount);
		dataList.add(contractAmount);
		dataList.add(price);
		dataList.add(projectLeader);
		dataList.add(validityDateStart);
		dataList.add(validityDateEnd);
		dataList.add(description);
		return dataList.toArray(new String[length]);
	}
}
