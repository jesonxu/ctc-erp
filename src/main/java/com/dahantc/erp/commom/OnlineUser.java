package com.dahantc.erp.commom;

import com.dahantc.erp.vo.user.entity.User;

public class OnlineUser {

	private User user;

	private String roleId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

}
