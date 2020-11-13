package com.dahantc.erp.dto.specialAttendance;

import java.io.Serializable;

public class SpecialAttendanceDto implements Serializable {

    private static final long serialVersionUID = 2497462042477660124L;

    private String ossUserId;

    private String realName;

    private String deptId;

    private String deptName;

    // 申请日期
    private String wtime;

    // 操作类型
    private String specialAttendanceType;

    // 请假类型
    private String leaveType;

    // 时长
    private String days;

    // 开始时间
    private String startTime;

    // 结束时间
    private String endTime;

    // 是否有效
    private String valid;

    // 是否度过
    private String timeState;

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

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getSpecialAttendanceType() {
        return specialAttendanceType;
    }

    public void setSpecialAttendanceType(String specialAttendanceType) {
        this.specialAttendanceType = specialAttendanceType;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getTimeState() {
        return timeState;
    }

    public void setTimeState(String timeState) {
        this.timeState = timeState;
    }
}
