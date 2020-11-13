package com.dahantc.erp.vo.specialAttendance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.TimeState;
import com.dahantc.erp.util.DateUtil;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.SpecialAttendanceType;

/**
 * 特殊出勤报备记录（请假、加班、外勤、出差）
 */
@Entity
@Table(name = "erp_special_attendance_record")
@DynamicUpdate(true)
public class SpecialAttendanceRecord implements Serializable {

	private static final long serialVersionUID = -9142803610601144635L;

	@Id
	@Column(length = 32, name = "id")
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	// 员工
	@Column(length = 32, name = "ossuserid")
	private String ossUserId;

	// 部门
	@Column(length = 32, name = "deptid")
	private String deptId;

	// 特殊出勤类型
	@Column(name = "specialattendancetype", columnDefinition = "int(11) default 0")
	private int specialAttendanceType = SpecialAttendanceType.Leave.ordinal();

	// 请假类型
	@Column(name = "leavetype", columnDefinition = "int(11) default null")
	private Integer leaveType;

	// 影响的假期，json字符串
	@Column(length = 500, name = "leaveinfo")
	private String leaveInfo;

	// 备注
	@Column(name = "remark")
	private String remark;

	// 天数
	@Column(name = "days", columnDefinition = "decimal(11,2) default 0")
	private BigDecimal days = BigDecimal.ZERO;

	// 关联流程id
	@Column(length = 32, name = "flowentid")
	private String flowEntId;

	// 开始时间
	@Column(name = "starttime")
	private Date startTime;

	// 结束时间
	@Column(name = "endtime")
	private Date endTime;

	// 创建时间
	@Column(name = "wtime")
	private Date wtime;

	// 是否有效 TODO
	@Column(name = "valid", columnDefinition = "int(11) default 1")
	private int valid ;

	// 时间状态
	@Column(name = "timeState", columnDefinition = "int(11) default 0")
	private int timeState = TimeState.READY.ordinal();

	public SpecialAttendanceRecord() {}

	public SpecialAttendanceRecord(Date startTime, Date endTime, Date wtime, int leaveType) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.wtime = wtime;
		this.leaveType = leaveType;
	}

	@Override
	public String toString() {
		return "SpecialAttendanceRecord{" +
				"leaveType=" + leaveType +
				", startTime=" + DateUtil.convert(startTime, DateUtil.format2) +
				", endTime=" + DateUtil.convert(endTime, DateUtil.format2) +
				", wtime=" + DateUtil.convert(wtime, DateUtil.format2) +
				'}';
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLeaveInfo() {
		return leaveInfo;
	}

	public void setLeaveInfo(String leaveId) {
		this.leaveInfo = leaveId;
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

	public int getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(int leaveType) {
		this.leaveType = leaveType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getDays() {
		return days;
	}

	public void setDays(BigDecimal days) {
		this.days = days;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public int getSpecialAttendanceType() {
		return specialAttendanceType;
	}

	public void setSpecialAttendanceType(int specialAttendanceType) {
		this.specialAttendanceType = specialAttendanceType;
	}

	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public int getTimeState() {
		return timeState;
	}

	public void setTimeState(int timeState) {
		this.timeState = timeState;
	}
}
