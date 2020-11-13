package com.dahantc.erp.dto.userLeave;

import java.io.Serializable;

public class UserLeaveDto implements Serializable {

    private static final long serialVersionUID = 2497462042477660124L;

    private String ossUserId;

    private String realName;

    private String deptId;

    private String deptName;

    // 毕业日期
    private String graduationDate;

    // 工作时长
    private String workMonth;

    // 入职时间
    private String entryTime;

    // 在职时长
    private String entryMonth;

    // 年份
    private String year;

    // 年假总天数
    private String annualLeaveTotal;

    // 剩余可用年假
    private String annualLeaveLeft;

    // 加班总天数
    private String overtimeTotal;

    // 剩余可用调休
    private String overtimeLeft;

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

    public String getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(String graduationDate) {
        this.graduationDate = graduationDate;
    }

    public String getWorkMonth() {
        return workMonth;
    }

    public void setWorkMonth(String workMonth) {
        this.workMonth = workMonth;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAnnualLeaveTotal() {
        return annualLeaveTotal;
    }

    public void setAnnualLeaveTotal(String annualLeaveTotal) {
        this.annualLeaveTotal = annualLeaveTotal;
    }

    public String getAnnualLeaveLeft() {
        return annualLeaveLeft;
    }

    public void setAnnualLeaveLeft(String annualLeaveLeft) {
        this.annualLeaveLeft = annualLeaveLeft;
    }

    public String getOvertimeTotal() {
        return overtimeTotal;
    }

    public void setOvertimeTotal(String overtimeTotal) {
        this.overtimeTotal = overtimeTotal;
    }

    public String getOvertimeLeft() {
        return overtimeLeft;
    }

    public void setOvertimeLeft(String overtimeLeft) {
        this.overtimeLeft = overtimeLeft;
    }

    public String getEntryMonth() {
        return entryMonth;
    }

    public void setEntryMonth(String entryMonth) {
        this.entryMonth = entryMonth;
    }
}
