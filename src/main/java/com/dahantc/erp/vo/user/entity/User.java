package com.dahantc.erp.vo.user.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.enums.MaritalStatus;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.enums.UserStatus;

@Entity
@Table(name = "erp_user")
@DynamicUpdate(true)
public class User implements Serializable {
	private static final long serialVersionUID = -4383842163439043317L;

	@Id
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 登录名
	@Column(name = "loginname", length = 32)
	private String loginName;

	// 登录密码
	@Column(name = "webpwd", length = 32)
	private String webPwd;

	@Column(name = "deptid", length = 32)
	private String deptId;

	@Column(name = "status", columnDefinition = "int default 0")
	private int status = EntityStatus.NORMAL.ordinal();

	@Column(name = "ustate", columnDefinition = "int default 0")
	private int ustate = UserStatus.ACTIVE.ordinal();

	@Column(name = "realname", length = 32)
	private String realName;

	@Column(name = "contactemail", length = 255)
	private String contacteMail;

	@Column(name = "contactmobile", length = 255)
	private String contactMobile;

	@Column(name = "contactphone", length = 32)
	private String contactPhone;

	/**
	 * 岗位类型，枚举名以,分隔
	 */
	@Column(name = "jobtype", length = 255)
	private String jobType;

	@Column(name = "identitytype", columnDefinition = "int default 0")
	private int identityType = IdentityType.ORDINARY_MEMBER.ordinal();

	/** 性别: 0-男，2-女 */
	@Column(columnDefinition = "int default 0")
	private int sex = 0;

	/** 办公地址 */
	@Column(name = "officeaddress")
	private String officeAddress;

	/** 入职时间 */
	@Column(name = "entrytime")
	private Timestamp entryTime;

	/** 住址 */
	@Column(name = "address")
	private String address;

	/** 生日 */
	@Column(name = "birthday")
	private Date birthday;

	/** 毕业时间 */
	@Column(name = "graduationdate")
	private Date graduationDate;

	// 婚姻状况
	@Column(name = "maritalstatus", columnDefinition = "int default 0")
	private int maritalStatus = MaritalStatus.SINGLE.getCode();

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public Timestamp getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Timestamp entryTime) {
		this.entryTime = entryTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getWebPwd() {
		return webPwd;
	}

	public String getDeptId() {
		return deptId;
	}

	public int getStatus() {
		return status;
	}

	public int getUstate() {
		return ustate;
	}

	public String getRealName() {
		return realName;
	}

	public String getContacteMail() {
		return contacteMail;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setWebPwd(String webPwd) {
		this.webPwd = webPwd;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setUstate(int ustate) {
		this.ustate = ustate;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setContacteMail(String contacteMail) {
		this.contacteMail = contacteMail;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public int getIdentityType() {
		return identityType;
	}

	public void setIdentityType(int identityType) {
		this.identityType = identityType;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getGraduationDate() {
		return graduationDate;
	}

	public void setGraduationDate(Date graduationDate) {
		this.graduationDate = graduationDate;
	}

	public int getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(int maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	@Override
	public String toString() {
		return "User{" +
				"ossUserId='" + ossUserId + '\'' +
				", deptId='" + deptId + '\'' +
				", realName='" + realName + '\'' +
				'}';
	}
}
