package com.dahantc.erp.dto.checkin;

import java.io.Serializable;

/**
 * @author : 8523
 * @date : 2020/10/16 10:43
 */
public class CheckinDetailDto implements Serializable {

    private static final long serialVersionUID = 7542495912477719692L;

    private String id;

    private String ossUserId;

    private String realName;

    // 打卡规则名称
    private String groupName;

    // 打卡类型
    private String checkinType;

    // 打卡类型名称
    private String checkinTypeName;

    // 异常类型
    private String exceptionType;

    // 异常类型名称
    private String exceptionTypeName;

    // 打卡时间
    private String checkinTime;

    // 打卡地点title
    private String locationTitle;

    // 打卡地点详情
    private String locationDetail;

    // 打卡备注
    private String notes;

    // 打卡的附件media_id，逗号分隔字符串
    private String mediaIds;

    // 位置打卡地点纬度，是实际纬度的1000000倍
    private String lat;

    // 位置打卡地点经度，是实际经度的1000000倍
    private String lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCheckinType() {
        return checkinType;
    }

    public void setCheckinType(String checkinType) {
        this.checkinType = checkinType;
    }

    public String getCheckinTypeName() {
        return checkinTypeName;
    }

    public void setCheckinTypeName(String checkinTypeName) {
        this.checkinTypeName = checkinTypeName;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExceptionTypeName() {
        return exceptionTypeName;
    }

    public void setExceptionTypeName(String exceptionTypeName) {
        this.exceptionTypeName = exceptionTypeName;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public String getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(String mediaIds) {
        this.mediaIds = mediaIds;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
