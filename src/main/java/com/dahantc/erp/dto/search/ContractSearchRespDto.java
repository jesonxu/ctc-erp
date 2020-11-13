package com.dahantc.erp.dto.search;

import java.io.Serializable;
import java.math.BigDecimal;

public class ContractSearchRespDto implements Serializable {

    private static final long serialVersionUID = -8983707587644128012L;

    // 合同编号
    private String contractId;

    private String contractName;

    private String status;

    private String wtime;

    // 申请人
    private String ossUserName;

    // 申请人部门
    private String deptName;

    // 实体类型，默认是客户
    private String entityType;

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
    private String settleType;

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

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getOssUserName() {
        return ossUserName;
    }

    public void setOssUserName(String ossUserName) {
        this.ossUserName = ossUserName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
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
}
