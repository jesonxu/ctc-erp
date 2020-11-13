package com.dahantc.erp.dto.attendance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 正常
 *  +--正常出勤天数
 *
 * 特殊
 *  +--请假天数
 *  +--加班天数
 *  +--外勤天数
 *  +--出差天数
 *
 * 特殊
 *  +--迟到天数
 *  +--旷工天数
 */
public class MonthAttendanceDto implements Serializable {

    private static final long serialVersionUID = 2497462042477660124L;

    private String id;

    private String month;

    private String ossUserId;

    private String realName;

    private String deptId;

    private String deptName;

    // 应出勤天数
    private BigDecimal defaultWorkDays = BigDecimal.ZERO;
    private String defaultWorkDaysStr;

    /* -----------------------正常----------------------- */
    // 正常出勤天数
    private BigDecimal normalAttendanceDays = BigDecimal.ZERO;
    private String normalAttendanceDaysStr;

    /* -----------------------特殊----------------------- */
    // 特殊出勤天数
    private BigDecimal specialAttendanceDays = BigDecimal.ZERO;
    private String specialAttendanceDaysStr;

    // 请假天数总计
    private BigDecimal leaveDays = BigDecimal.ZERO;
    private String leaveDaysStr;

    // 各种假期类型的天数
    private Map<Integer, BigDecimal> leaveDaysDetail;

    // 外勤天数
    private BigDecimal outsideDays = BigDecimal.ZERO;
    private String outsideDaysStr;

    // 出差天数
    private BigDecimal businessTravelDays = BigDecimal.ZERO;
    private String businessTravelDaysStr;

    // 加班天数总计
    private BigDecimal overtimeDays = BigDecimal.ZERO;
    private String overtimeDaysStr;

    /* -----------------------异常----------------------- */
    // 异常出勤天数
    private BigDecimal exceptionalAttendanceDays = BigDecimal.ZERO;
    private String exceptionalAttendanceDaysStr;

    // 迟到天数
    private BigDecimal lateDays = BigDecimal.ZERO;
    private String lateDaysStr;

    // 迟到时长总计
    private BigDecimal lateMins = BigDecimal.ZERO;
    private String lateMinsStr;

    // 旷工天数
    private BigDecimal absenteeismDsys = BigDecimal.ZERO;
    private String absenteeismDsysStr;

    // 旷工时长总计
    private BigDecimal absenteeismMins = BigDecimal.ZERO;
    private String absenteeismMinsStr;

    /* -----------------------待确认----------------------- */
    // 未知出勤天数
    private BigDecimal unknownAttendanceDays = BigDecimal.ZERO;
    private String unknownAttendanceDaysStr;

    public void setString() {
        this.defaultWorkDaysStr = defaultWorkDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.normalAttendanceDaysStr = normalAttendanceDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.specialAttendanceDaysStr = specialAttendanceDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.leaveDaysStr = leaveDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.outsideDaysStr = outsideDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.businessTravelDaysStr = businessTravelDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.overtimeDaysStr = overtimeDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.exceptionalAttendanceDaysStr = exceptionalAttendanceDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.lateDaysStr = lateDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.lateMinsStr = lateMins.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.absenteeismDsysStr = absenteeismDsys.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.absenteeismMinsStr = absenteeismMins.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        this.unknownAttendanceDaysStr = unknownAttendanceDays.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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

    public BigDecimal getDefaultWorkDays() {
        return defaultWorkDays;
    }

    public void setDefaultWorkDays(BigDecimal defaultWorkDays) {
        this.defaultWorkDays = defaultWorkDays;
    }

    public BigDecimal getNormalAttendanceDays() {
        return normalAttendanceDays;
    }

    public void setNormalAttendanceDays(BigDecimal normalAttendanceDays) {
        this.normalAttendanceDays = normalAttendanceDays;
    }

    public BigDecimal getSpecialAttendanceDays() {
        return specialAttendanceDays;
    }

    public void setSpecialAttendanceDays(BigDecimal specialAttendanceDays) {
        this.specialAttendanceDays = specialAttendanceDays;
    }

    public BigDecimal getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(BigDecimal leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Map<Integer, BigDecimal> getLeaveDaysDetail() {
        return leaveDaysDetail;
    }

    public void setLeaveDaysDetail(Map<Integer, BigDecimal> leaveDaysDetail) {
        this.leaveDaysDetail = leaveDaysDetail;
    }

    public BigDecimal getOutsideDays() {
        return outsideDays;
    }

    public void setOutsideDays(BigDecimal outsideDays) {
        this.outsideDays = outsideDays;
    }

    public BigDecimal getBusinessTravelDays() {
        return businessTravelDays;
    }

    public void setBusinessTravelDays(BigDecimal businessTravelDays) {
        this.businessTravelDays = businessTravelDays;
    }

    public BigDecimal getOvertimeDays() {
        return overtimeDays;
    }

    public void setOvertimeDays(BigDecimal overtimeDays) {
        this.overtimeDays = overtimeDays;
    }

    public BigDecimal getExceptionalAttendanceDays() {
        return exceptionalAttendanceDays;
    }

    public void setExceptionalAttendanceDays(BigDecimal exceptionalAttendanceDays) {
        this.exceptionalAttendanceDays = exceptionalAttendanceDays;
    }

    public BigDecimal getLateDays() {
        return lateDays;
    }

    public void setLateDays(BigDecimal lateDays) {
        this.lateDays = lateDays;
    }

    public BigDecimal getLateMins() {
        return lateMins;
    }

    public void setLateMins(BigDecimal lateMins) {
        this.lateMins = lateMins;
    }

    public BigDecimal getAbsenteeismDsys() {
        return absenteeismDsys;
    }

    public void setAbsenteeismDsys(BigDecimal absenteeismDsys) {
        this.absenteeismDsys = absenteeismDsys;
    }

    public BigDecimal getAbsenteeismMins() {
        return absenteeismMins;
    }

    public void setAbsenteeismMins(BigDecimal absenteeismMins) {
        this.absenteeismMins = absenteeismMins;
    }

    public BigDecimal getUnknownAttendanceDays() {
        return unknownAttendanceDays;
    }

    public void setUnknownAttendanceDays(BigDecimal unknownAttendanceDays) {
        this.unknownAttendanceDays = unknownAttendanceDays;
    }

    public String getDefaultWorkDaysStr() {
        return defaultWorkDaysStr;
    }

    public void setDefaultWorkDaysStr(String defaultWorkDaysStr) {
        this.defaultWorkDaysStr = defaultWorkDaysStr;
    }

    public String getNormalAttendanceDaysStr() {
        return normalAttendanceDaysStr;
    }

    public void setNormalAttendanceDaysStr(String normalAttendanceDaysStr) {
        this.normalAttendanceDaysStr = normalAttendanceDaysStr;
    }

    public String getSpecialAttendanceDaysStr() {
        return specialAttendanceDaysStr;
    }

    public void setSpecialAttendanceDaysStr(String specialAttendanceDaysStr) {
        this.specialAttendanceDaysStr = specialAttendanceDaysStr;
    }

    public String getLeaveDaysStr() {
        return leaveDaysStr;
    }

    public void setLeaveDaysStr(String leaveDaysStr) {
        this.leaveDaysStr = leaveDaysStr;
    }

    public String getOutsideDaysStr() {
        return outsideDaysStr;
    }

    public void setOutsideDaysStr(String outsideDaysStr) {
        this.outsideDaysStr = outsideDaysStr;
    }

    public String getBusinessTravelDaysStr() {
        return businessTravelDaysStr;
    }

    public void setBusinessTravelDaysStr(String businessTravelDaysStr) {
        this.businessTravelDaysStr = businessTravelDaysStr;
    }

    public String getOvertimeDaysStr() {
        return overtimeDaysStr;
    }

    public void setOvertimeDaysStr(String overtimeDaysStr) {
        this.overtimeDaysStr = overtimeDaysStr;
    }

    public String getExceptionalAttendanceDaysStr() {
        return exceptionalAttendanceDaysStr;
    }

    public void setExceptionalAttendanceDaysStr(String exceptionalAttendanceDaysStr) {
        this.exceptionalAttendanceDaysStr = exceptionalAttendanceDaysStr;
    }

    public String getLateDaysStr() {
        return lateDaysStr;
    }

    public void setLateDaysStr(String lateDaysStr) {
        this.lateDaysStr = lateDaysStr;
    }

    public String getLateMinsStr() {
        return lateMinsStr;
    }

    public void setLateMinsStr(String lateMinsStr) {
        this.lateMinsStr = lateMinsStr;
    }

    public String getAbsenteeismDsysStr() {
        return absenteeismDsysStr;
    }

    public void setAbsenteeismDsysStr(String absenteeismDsysStr) {
        this.absenteeismDsysStr = absenteeismDsysStr;
    }

    public String getAbsenteeismMinsStr() {
        return absenteeismMinsStr;
    }

    public void setAbsenteeismMinsStr(String absenteeismMinsStr) {
        this.absenteeismMinsStr = absenteeismMinsStr;
    }

    public String getUnknownAttendanceDaysStr() {
        return unknownAttendanceDaysStr;
    }

    public void setUnknownAttendanceDaysStr(String unknownAttendanceDaysStr) {
        this.unknownAttendanceDaysStr = unknownAttendanceDaysStr;
    }

}
