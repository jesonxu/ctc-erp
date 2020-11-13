package com.dahantc.erp.dto.chargeRecord;

import java.sql.Timestamp;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;

public class IncomeCheckOutDto {

    private String id;

    /** 时间(收付款时间,精确到日) */
    private String operateTime;

    /** 银行名称 */
    private String bankName;

    /** 部门名称 */
    private String deptName;

    /** 姓名 */
    private String optionName;

    /** 摘要: 广州某某信息科技有限公司 */
    private String depict;

    /** 金额 */
    private String cost;

    private String remark;

    /** 记录创建者名称 */
    private String creatorName;

    /** 导入时间 */
    private String wtime;

    private String customerId;

    private String serialNumber;

    private String customerName;

    // 核销状态
    private int checkOut;

    // 剩余可核销金额
    private String remainCheckOut;

    // 本次销账
    private String thisCheckOut;

    private String ossUserId;

    private String realName;

    public IncomeCheckOutDto() {

    }

    public IncomeCheckOutDto(FsExpenseIncome fsExpenseIncome) {
        if (fsExpenseIncome != null) {
            this.id = fsExpenseIncome.getId();
            Timestamp operateTime = fsExpenseIncome.getOperateTime();
            this.operateTime = operateTime != null ? DateUtil.convert(operateTime, DateUtil.format1) : "";
            this.bankName = fsExpenseIncome.getBankName();
            this.deptName = fsExpenseIncome.getDeptName();
            this.optionName = fsExpenseIncome.getOperator();
            this.depict = fsExpenseIncome.getDepict();
            this.cost = fsExpenseIncome.getCost().toPlainString();
            this.remark = fsExpenseIncome.getRemark();
            Timestamp wtime = fsExpenseIncome.getWtime();
            this.wtime = wtime != null ? DateUtil.convert(wtime, DateUtil.format2) : "";
            this.serialNumber = fsExpenseIncome.getSerialNumber();
            this.customerId = fsExpenseIncome.getCustomerId();
            this.remainCheckOut = fsExpenseIncome.getRemainCheckOut().toPlainString();
            this.checkOut = fsExpenseIncome.getCheckOut();
            this.ossUserId = fsExpenseIncome.getUserId();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getThisCheckOut() {
        return thisCheckOut;
    }

    public void setThisCheckOut(String thisCheckOut) {
        this.thisCheckOut = thisCheckOut;
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
}
