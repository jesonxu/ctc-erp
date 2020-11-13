package com.dahantc.erp.dto.checkin;

import java.io.Serializable;

/**
 * 一条的打卡记录
 * @author : 8523
 * @date : 2020/10/16 11:36
 */
public class CheckinDto implements Serializable {

    private static final long serialVersionUID = 4408263955802325417L;

    // 日期
    private String date;

    private String ossUserId;

    private String realName;

    private String deptId;

    private String deptName;

    // 上班打卡时间
    private String checkinTime;

    // 上班打卡地点
    private String checkinLocationTitle;

    // 上班打卡地点详细
    private String checkinLocationDetail;

    // 上班打卡地点坐标
    private String checkinLocation;

    // 上班打卡结果描述，成功，或是什么异常
    private String checkinInfo = "未打卡";

    // 下班打卡时间
    private String checkoutTime;

    private String checkoutLocationTitle;

    private String checkoutLocationDetail;

    private String checkoutLocation;

    private String checkoutInfo = "未打卡";

    // 外出打卡次数
    private int outsideCheckTimes = 0;

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

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckinLocationTitle() {
        return checkinLocationTitle;
    }

    public void setCheckinLocationTitle(String checkinLocationTitle) {
        this.checkinLocationTitle = checkinLocationTitle;
    }

    public String getCheckinLocation() {
        return checkinLocation;
    }

    public void setCheckinLocation(String checkinLocation) {
        this.checkinLocation = checkinLocation;
    }

    public String getCheckinInfo() {
        return checkinInfo;
    }

    public void setCheckinInfo(String checkinInfo) {
        this.checkinInfo = checkinInfo;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getCheckoutLocationTitle() {
        return checkoutLocationTitle;
    }

    public void setCheckoutLocationTitle(String checkoutLocationTitle) {
        this.checkoutLocationTitle = checkoutLocationTitle;
    }

    public String getCheckoutLocation() {
        return checkoutLocation;
    }

    public void setCheckoutLocation(String checkoutLocation) {
        this.checkoutLocation = checkoutLocation;
    }

    public String getCheckoutInfo() {
        return checkoutInfo;
    }

    public void setCheckoutInfo(String checkoutInfo) {
        this.checkoutInfo = checkoutInfo;
    }

    public int getOutsideCheckTimes() {
        return outsideCheckTimes;
    }

    public void setOutsideCheckTimes(int outsideCheckTimes) {
        this.outsideCheckTimes = outsideCheckTimes;
    }

    public String getCheckinLocationDetail() {
        return checkinLocationDetail;
    }

    public void setCheckinLocationDetail(String checkinLocationDetail) {
        this.checkinLocationDetail = checkinLocationDetail;
    }

    public String getCheckoutLocationDetail() {
        return checkoutLocationDetail;
    }

    public void setCheckoutLocationDetail(String checkoutLocationDetail) {
        this.checkoutLocationDetail = checkoutLocationDetail;
    }
}
