package com.dahantc.erp.vo.checkin.entity;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.enums.CheckinExceptionType;
import com.dahantc.erp.enums.CheckinType;
import com.dahantc.erp.util.DateUtil;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * 从企业微信同步的签到记录
 * 
 * @author : 8523
 * @date : 2020/10/15 16:52
 */

@Entity
@Table(name = "erp_checkin")
@DynamicUpdate(true)
public class Checkin implements Serializable {

	private static final long serialVersionUID = -4343615281766671499L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", length = 32)
	private String id;

	// 用户表id
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 打卡规则名称
	@Column(name = "groupname")
	private String groupName;

	// 打卡类型
	@Column(name = "checkintype")
	private String checkinType;

	// 打卡类型名称
	@Column(name = "checkintypename")
	private String checkinTypeName;

	// 异常类型
	@Column(name = "exceptionype")
	private String exceptionType;

	// 异常类型名称
	@Column(name = "exceptionypename")
	private String exceptionTypeName;

	// 打卡时间
	@Column(name = "checkintime")
	private Timestamp checkinTime;

	// 打卡地点title
	@Column(name = "locationtitle")
	private String locationTitle;

	// 打卡地点详情
	@Column(name = "locationdetail")
	private String locationDetail;

	// 打卡wifi名称
	@Column(name = "wifiname")
	private String wifiName;

	// 打卡备注
	@Column(name = "notes")
	private String notes;

	// 打卡的MAC地址/bssid
	@Column(name = "wifimac")
	private String wifiMac;

	// 打卡的附件media_id，逗号分隔字符串
	@Column(name = "mediaids", length = 1000)
	private String mediaIds;

	// 位置打卡地点纬度，是实际纬度的1000000倍
	@Column(name = "lat")
	private String lat;

	// 位置打卡地点经度，是实际经度的1000000倍
	@Column(name = "lng")
	private String lng;

	// 打卡设备id
	@Column(name = "deviceid")
	private String deviceId;

	public void toCheckin(JSONObject data) {
		this.ossUserId = data.getString("userid");
		this.groupName = data.getString("groupname");
		// 打卡类型
		String checkinTypeStr = data.getString("checkin_type");
		Optional<CheckinType> checkinTypeOpt = CheckinType.getEnumsByDesc(checkinTypeStr);
		checkinTypeOpt.ifPresent(type -> this.checkinType = type.getCode() + "");
		this.checkinTypeName = checkinTypeStr;
		// 异常类型
		String exceptionTypeStr = data.getString("exception_type");
		String[] exceptions = exceptionTypeStr.split(",");
		String exceptionType = "";
		for (String exception : exceptions) {
			Optional<CheckinExceptionType> exceptionOpt = CheckinExceptionType.getEnumsByDesc(exception);
			if (exceptionOpt.isPresent()) {
				exceptionType += exceptionOpt.get().ordinal();
			}
		}
		this.exceptionType = exceptionType;
		this.exceptionTypeName = exceptionTypeStr;

		this.checkinTime = new Timestamp(data.getLong("checkin_time") * 1000);
		this.locationTitle = data.getString("location_title");
		this.locationDetail = data.getString("location_detail");
		this.wifiName = data.getString("wifiname");
		this.notes = data.getString("notes");
		this.wifiMac = data.getString("wifimac");
		this.mediaIds = data.getString("mediaids");
		this.lat = data.getString("lat");
		this.lng = data.getString("lng");
		this.deviceId = data.getString("deviceid");
	}

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

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public Timestamp getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(Timestamp checkinTime) {
		this.checkinTime = checkinTime;
	}

	public String getCheckinTypeName() {
		return checkinTypeName;
	}

	public void setCheckinTypeName(String checkinTypeName) {
		this.checkinTypeName = checkinTypeName;
	}

	public String getExceptionTypeName() {
		return exceptionTypeName;
	}

	public void setExceptionTypeName(String exceptionTypeName) {
		this.exceptionTypeName = exceptionTypeName;
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

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getWifiMac() {
		return wifiMac;
	}

	public void setWifiMac(String wifiMac) {
		this.wifiMac = wifiMac;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "Checkin{" + "ossUserId='" + ossUserId + '\'' + ", checkinTime=" + DateUtil.convert(checkinTime.getTime(), DateUtil.format2) + ", checkinType='"
				+ checkinType + '\'' + ", exceptionType='" + exceptionType + '\'' + '}';
	}
}
