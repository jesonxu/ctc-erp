package com.dahantc.erp.dto.attendance;

import java.io.Serializable;

public class AttendanceDto implements Serializable {

    private static final long serialVersionUID = 2497462042477660124L;

    private String id;

    private String date;

    private String ossUserId;

    private String realName;

    private String deptId;

    private String deptName;

    private int workStatus;

    private int work;

    private String workInfo;

    private String workMins;

    private int leave;

    private String leaveInfo;

    private String leaveMins;

    private int overtime;

    private String overtimeInfo;

    private String overtimeMins;

    private int outside;

    private String outsideInfo;

    private String outsideMins;

    private int businessTravel;

    private String businessTravelInfo;

    private String businessTravelMins;

    private int late;

    private String lateInfo;

    private String lateMins;

    private int absenteeism;

    private String absenteeismInfo;

    private String absenteeismMins;

    private String operationLog;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public String getWorkInfo() {
        return workInfo;
    }

    public void setWorkInfo(String workInfo) {
        this.workInfo = workInfo;
    }

    public String getWorkMins() {
        return workMins;
    }

    public void setWorkMins(String workMins) {
        this.workMins = workMins;
    }

    public int getLeave() {
        return leave;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }

    public String getLeaveInfo() {
        return leaveInfo;
    }

    public void setLeaveInfo(String leaveInfo) {
        this.leaveInfo = leaveInfo;
    }

    public String getLeaveMins() {
        return leaveMins;
    }

    public void setLeaveMins(String leaveMins) {
        this.leaveMins = leaveMins;
    }

    public int getOvertime() {
        return overtime;
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    public String getOvertimeInfo() {
        return overtimeInfo;
    }

    public void setOvertimeInfo(String overtimeInfo) {
        this.overtimeInfo = overtimeInfo;
    }

    public String getOvertimeMins() {
        return overtimeMins;
    }

    public void setOvertimeMins(String overtimeMins) {
        this.overtimeMins = overtimeMins;
    }

    public int getOutside() {
        return outside;
    }

    public void setOutside(int outside) {
        this.outside = outside;
    }

    public String getOutsideInfo() {
        return outsideInfo;
    }

    public void setOutsideInfo(String outsideInfo) {
        this.outsideInfo = outsideInfo;
    }

    public String getOutsideMins() {
        return outsideMins;
    }

    public void setOutsideMins(String outsideMins) {
        this.outsideMins = outsideMins;
    }

    public int getBusinessTravel() {
        return businessTravel;
    }

    public void setBusinessTravel(int businessTravel) {
        this.businessTravel = businessTravel;
    }

    public String getBusinessTravelInfo() {
        return businessTravelInfo;
    }

    public void setBusinessTravelInfo(String businessTravelInfo) {
        this.businessTravelInfo = businessTravelInfo;
    }

    public String getBusinessTravelMins() {
        return businessTravelMins;
    }

    public void setBusinessTravelMins(String businessTravelMins) {
        this.businessTravelMins = businessTravelMins;
    }

    public int getLate() {
        return late;
    }

    public void setLate(int late) {
        this.late = late;
    }

    public String getLateInfo() {
        return lateInfo;
    }

    public void setLateInfo(String lateInfo) {
        this.lateInfo = lateInfo;
    }

    public String getLateMins() {
        return lateMins;
    }

    public void setLateMins(String lateMins) {
        this.lateMins = lateMins;
    }

    public int getAbsenteeism() {
        return absenteeism;
    }

    public void setAbsenteeism(int absenteeism) {
        this.absenteeism = absenteeism;
    }

    public String getAbsenteeismInfo() {
        return absenteeismInfo;
    }

    public void setAbsenteeismInfo(String absenteeismInfo) {
        this.absenteeismInfo = absenteeismInfo;
    }

    public String getAbsenteeismMins() {
        return absenteeismMins;
    }

    public void setAbsenteeismMins(String absenteeismMins) {
        this.absenteeismMins = absenteeismMins;
    }

    public String getOperationLog() {
        return operationLog;
    }

    public void setOperationLog(String operationLog) {
        this.operationLog = operationLog;
    }
}
