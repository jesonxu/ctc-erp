package com.dahantc.erp.vo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.vo.user.entity.User;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.WorkStatus;
import com.dahantc.erp.util.DateUtil;

/**
 * 员工出勤记录表
 * 
 * @author : 8523
 */

@Entity
@Table(name = "erp_attendance")
@DynamicUpdate(true)
public class Attendance implements Serializable {

	private static final long serialVersionUID = -4343615281766671499L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", length = 32)
	private String id;

	// 日期
	@Column(name = "date")
	private Date date;

	// 用户表id
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	@Column(name = "deptid", length = 32)
	private String deptId;

	// 状态，默认待确认
	@Column(name = "workstatus", columnDefinition = "int(11) default 0")
	private int workStatus = WorkStatus.Unknown.ordinal();

	// 是否上班
	@Column(name = "work", columnDefinition = "tinyint(1) default 0")
	private Boolean work = false;

	// 上班时间段 json数组字符串
	@Column(name = "workinfo", length = 400)
	private String workInfo;

	// 上班时长
	@Column(name = "workmins", columnDefinition = "int(11) default 0")
	private int workMins;

	// 是否请假
	@Column(name = "`leave`", columnDefinition = "tinyint(1) default 0")
	private Boolean leave = false;

	// 请假时间段 json数组字符串
	@Column(name = "leaveinfo", length = 400)
	private String leaveInfo;

	// 请假时长
	@Column(name = "leavemins", columnDefinition = "int(11) default 0")
	private int leaveMins;

	// 是否加班
	@Column(name = "overtime", columnDefinition = "tinyint(1) default 0")
	private Boolean overtime = false;

	// 加班时间段 json数组字符串
	@Column(name = "overtimeinfo", length = 400)
	private String overtimeInfo;

	// 加班时长
	@Column(name = "overtimemins", columnDefinition = "int(11) default 0")
	private int overtimeMins;

	// 外勤打卡次数
	@Column(name = "outside", columnDefinition = "int(11) default 0")
	private int outside = 0;

	// 外勤时间段 json数组字符串
	@Column(name = "outsideinfo", length = 400)
	private String outsideInfo;

	// 外勤时长
	@Column(name = "outsidemins", columnDefinition = "int(11) default 0")
	private int outsideMins;

	// 是否出差
	@Column(name = "businesstravel", columnDefinition = "tinyint(1) default 0")
	private Boolean businessTravel = false;

	// 出差时间段 json数组字符串
	@Column(name = "businesstravelinfo", length = 400)
	private String businessTravelInfo;

	// 出差时长
	@Column(name = "businesstravelmins", columnDefinition = "int(11) default 0")
	private int businessTravelMins;

	// 是否迟到
	@Column(name = "late", columnDefinition = "tinyint(1) default 0")
	private Boolean late = false;

	// 迟到时间段 json数组字符串
	@Column(name = "lateinfo", length = 400)
	private String lateInfo;

	// 迟到时长
	@Column(name = "latemins", columnDefinition = "int(11) default 0")
	private int lateMins;

	// 是否旷工
	@Column(name = "absenteeism", columnDefinition = "tinyint(1) default 0")
	private Boolean absenteeism = false;

	// 旷工时间段 json数组字符串
	@Column(name = "absenteeisminfo", length = 400)
	private String absenteeismInfo;

	// 旷工时长
	@Column(name = "absenteeismmins", columnDefinition = "int(11) default 0")
	private int absenteeismMins;

	// 修改记录
	@Column(name = "operationlog", length = 2000)
	private String operationLog;

	public Attendance() {
	}

	public Attendance(User user, Date date) {
		this.ossUserId = user.getOssUserId();
		this.deptId = user.getDeptId();
		this.date = date;
	}

	public void cleanWork() {
		setWork(false);
		setWorkInfo(null);
		setWorkMins(0);
	}

	public void cleanLeave() {
		setLeave(false);
		setLeaveInfo(null);
		setLeaveMins(0);
	}

	public void cleanOvertime() {
		setOvertime(false);
		setOvertimeInfo(null);
		setOvertimeMins(0);
	}

	public void cleanOutside() {
		setOutside(0);
		setOutsideInfo(null);
		setOutsideMins(0);
	}

	public void cleanBusinessTravel() {
		setBusinessTravel(false);
		setBusinessTravelInfo(null);
		setBusinessTravelMins(0);
	}

	public void cleanLate() {
		setLate(false);
		setLateInfo(null);
		setLateMins(0);
	}

	public void cleanAbsenteeism() {
		setAbsenteeism(false);
		setAbsenteeismInfo(null);
		setAbsenteeismMins(0);
	}

	public void cleanAll() {
		cleanWork();
		cleanLeave();
		cleanOvertime();
		cleanOutside();
		cleanBusinessTravel();
		cleanLate();
		cleanAbsenteeism();
	}

	public boolean isWork() {
		return this.work;
	}

	public boolean isLeave() {
		return this.leave;
	}

	public boolean isOvertime() {
		return this.overtime;
	}

	public boolean isBusinessTravel() {
		return this.businessTravel;
	}

	public boolean isLate() {
		return this.late;
	}

	public boolean isAbsenteeism() {
		return this.absenteeism;
	}

	public boolean isOutside() {
		return this.outside > 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public int getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(int workStatus) {
		this.workStatus = workStatus;
	}

	public Boolean getWork() {
		return work;
	}

	public void setWork(Boolean work) {
		this.work = work;
	}

	public String getWorkInfo() {
		return workInfo;
	}

	public void setWorkInfo(String workInfo) {
		this.workInfo = workInfo;
	}

	public int getWorkMins() {
		return workMins;
	}

	public void setWorkMins(int workMins) {
		this.workMins = workMins;
	}

	public Boolean getLeave() {
		return leave;
	}

	public void setLeave(Boolean leave) {
		this.leave = leave;
	}

	public String getLeaveInfo() {
		return leaveInfo;
	}

	public void setLeaveInfo(String leaveInfo) {
		this.leaveInfo = leaveInfo;
	}

	public int getLeaveMins() {
		return leaveMins;
	}

	public void setLeaveMins(int leaveMins) {
		this.leaveMins = leaveMins;
	}

	public Boolean getOvertime() {
		return overtime;
	}

	public void setOvertime(Boolean overtime) {
		this.overtime = overtime;
	}

	public String getOvertimeInfo() {
		return overtimeInfo;
	}

	public void setOvertimeInfo(String overtimeInfo) {
		this.overtimeInfo = overtimeInfo;
	}

	public int getOvertimeMins() {
		return overtimeMins;
	}

	public void setOvertimeMins(int overtimeMins) {
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

	public int getOutsideMins() {
		return outsideMins;
	}

	public void setOutsideMins(int outsideMins) {
		this.outsideMins = outsideMins;
	}

	public Boolean getBusinessTravel() {
		return businessTravel;
	}

	public void setBusinessTravel(Boolean businessTravel) {
		this.businessTravel = businessTravel;
	}

	public String getBusinessTravelInfo() {
		return businessTravelInfo;
	}

	public void setBusinessTravelInfo(String businessTravelInfo) {
		this.businessTravelInfo = businessTravelInfo;
	}

	public int getBusinessTravelMins() {
		return businessTravelMins;
	}

	public void setBusinessTravelMins(int businessTravelMins) {
		this.businessTravelMins = businessTravelMins;
	}

	public Boolean getLate() {
		return late;
	}

	public void setLate(Boolean late) {
		this.late = late;
	}

	public String getLateInfo() {
		return lateInfo;
	}

	public void setLateInfo(String lateInfo) {
		this.lateInfo = lateInfo;
	}

	public int getLateMins() {
		return lateMins;
	}

	public void setLateMins(int lateMins) {
		this.lateMins = lateMins;
	}

	public Boolean getAbsenteeism() {
		return absenteeism;
	}

	public void setAbsenteeism(Boolean absenteeism) {
		this.absenteeism = absenteeism;
	}

	public String getAbsenteeismInfo() {
		return absenteeismInfo;
	}

	public void setAbsenteeismInfo(String absenteeismInfo) {
		this.absenteeismInfo = absenteeismInfo;
	}

	public int getAbsenteeismMins() {
		return absenteeismMins;
	}

	public void setAbsenteeismMins(int absenteeismMins) {
		this.absenteeismMins = absenteeismMins;
	}

	public String getOperationLog() {
		return operationLog;
	}

	public void setOperationLog(String operationLog) {
		this.operationLog = operationLog;
	}

	@Override
	public String toString() {
		return "Attendance{" + "date=" + DateUtil.convert(date, DateUtil.format1) + ", ossUserId='" + ossUserId + '\'' + ", workStatus=" + workStatus + '}';
	}
}
