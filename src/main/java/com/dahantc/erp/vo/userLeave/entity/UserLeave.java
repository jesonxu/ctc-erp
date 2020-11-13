package com.dahantc.erp.vo.userLeave.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.vo.user.entity.User;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.LeaveType;

/**
 * 员工假期数据
 */
@Entity
@Table(name = "erp_user_leave")
@DynamicUpdate(true)
public class UserLeave implements Serializable {

	private static final long serialVersionUID = 6306366140547282516L;

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

	// 假期类型
	@Column(name = "leavetype", columnDefinition = "int(11) default 0")
	private int leaveType = LeaveType.PERSONAL_LEAVE.ordinal();

	// 总天数
	@Column(name = "totaldays", columnDefinition = "decimal(11,2) default 0")
	private BigDecimal totalDays = BigDecimal.ZERO;

	// 已用天数
	@Column(name = "useddays", columnDefinition = "decimal(11,2) default 0")
	private BigDecimal usedDays = BigDecimal.ZERO;

	// 剩余天数
	@Column(name = "leftdays", columnDefinition = "decimal(11,2) default 0")
	private BigDecimal leftDays = BigDecimal.ZERO;

	// 所属年份
	@Column(name = "year")
	private Date year;

	@Column(name = "remark", length = 1000)
	private String remark;

	// 生成时间
	@Column(name = "wtime")
	private Date wtime = new Date();

	// 有效开始时间
	@Column(name = "validstartdate")
	private Date validStartDate;

	// 有效结束时间
	@Column(name = "validenddate")
	private Date validEndDate;

	public UserLeave() {}

	public UserLeave(User user) {
		this.ossUserId = user.getOssUserId();
		this.deptId = user.getDeptId();
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

	public BigDecimal getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(BigDecimal totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getUsedDays() {
		return usedDays;
	}

	public void setUsedDays(BigDecimal usedDays) {
		this.usedDays = usedDays;
	}

	public BigDecimal getLeftDays() {
		return leftDays;
	}

	public void setLeftDays(BigDecimal leftDays) {
		this.leftDays = leftDays;
	}

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public Date getValidStartDate() {
		return validStartDate;
	}

	public void setValidStartDate(Date validStartDate) {
		this.validStartDate = validStartDate;
	}

	public Date getValidEndDate() {
		return validEndDate;
	}

	public void setValidEndDate(Date validEndDate) {
		this.validEndDate = validEndDate;
	}
}
