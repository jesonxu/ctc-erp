package com.dahantc.erp.dto.chargeRecord;

import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;

import java.io.Serializable;

public class ChargeRecordDto implements Serializable {

    private static final long serialVersionUID = 483287463749753473L;

    private String id;

    private String entityId;

    private String companyName;

    private String ossUserId;

    private String realName;

    private String account;

    private String deptId;

    private String deptName;

    private String chargeType;

    private String price;

    private String chargePrice;

    private int checkOut;

    private String remainCheckOut;

    private String finalReceiveTime;

    private String actualReceiveTime;

    private String wtime;

    private String remark;

    public ChargeRecordDto() {

    }

    public ChargeRecordDto(ChargeRecord chargeRecord) {
        this.id = chargeRecord.getChargerecordId();
        this.entityId = chargeRecord.getSupplierId();
        this.ossUserId = chargeRecord.getCreaterId();
        this.account = chargeRecord.getAccount();
        this.deptId = chargeRecord.getDeptId();
        this.chargeType = IncomeExpenditureType.getTypeName(chargeRecord.getChargeType());
        this.price = chargeRecord.getPrice() == null ? "" : chargeRecord.getPrice().toPlainString();
        this.chargePrice = chargeRecord.getChargePrice() == null ? "0.0" : chargeRecord.getChargePrice().toPlainString();
        this.checkOut = chargeRecord.getCheckOut();
        this.remainCheckOut = chargeRecord.getRemainCheckOut() == null ? "0.0" : chargeRecord.getRemainCheckOut().toPlainString();
        this.finalReceiveTime = chargeRecord.getFinalPayTime() == null ? "" : DateUtil.convert(chargeRecord.getFinalPayTime(), DateUtil.format1);
        this.actualReceiveTime = chargeRecord.getActualPayTime() == null ? "" : DateUtil.convert(chargeRecord.getActualPayTime(), DateUtil.format1);
        this.wtime = chargeRecord.getWtime() == null ? "" : DateUtil.convert(chargeRecord.getWtime(), DateUtil.format1);
        this.remark = chargeRecord.getRemark();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOssUserId() {
        return ossUserId;
    }

    public void setOssUserId(String ossUserId) {
        this.ossUserId = ossUserId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChargePrice() {
        return chargePrice;
    }

    public void setChargePrice(String chargePrice) {
        this.chargePrice = chargePrice;
    }

    public int getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(int checkOut) {
        this.checkOut = checkOut;
    }

    public String getRemainCheckOut() {
        return remainCheckOut;
    }

    public void setRemainCheckOut(String remainCheckOut) {
        this.remainCheckOut = remainCheckOut;
    }

    public String getFinalReceiveTime() {
        return finalReceiveTime;
    }

    public void setFinalReceiveTime(String finalReceiveTime) {
        this.finalReceiveTime = finalReceiveTime;
    }

    public String getActualReceiveTime() {
        return actualReceiveTime;
    }

    public void setActualReceiveTime(String actualReceiveTime) {
        this.actualReceiveTime = actualReceiveTime;
    }

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
