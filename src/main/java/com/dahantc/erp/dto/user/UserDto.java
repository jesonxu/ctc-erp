package com.dahantc.erp.dto.user;

import com.dahantc.erp.vo.user.entity.User;

public class UserDto extends User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3752307084791889317L;

	/** 用户编号 */
	private String ossUserId;
	
	/** 登录名 */
	private String loginName;
	
	/** 真实姓名 */
	private String realName;
	
	/** 禁用激活 */
	private String uState;
	
	/** 角色编号 */
	private String roleId;
	
	/** 用户角色 */
	private String roleName;

	/** 部门名称 */
	private String deptName;
	
	/** 联系电话 */
	private String contactMobile;

	/** 手机号码 */
	private String contactPhone;

	/** 联系邮箱 */
	private String contactEmail;

	/** 岗位类型 */
	private String jobType;

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

	public String getuState() {
		return uState;
	}

	public void setuState(String uState) {
		this.uState = uState;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
}
