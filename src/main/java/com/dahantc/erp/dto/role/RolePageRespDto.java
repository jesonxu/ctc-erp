package com.dahantc.erp.dto.role;

import java.io.Serializable;

/**
 * 角色分页响应数据
 * 
 * @author wangyang
 *
 */
public class RolePageRespDto implements Serializable {

	private static final long serialVersionUID = -4336996061266632949L;

	private String roleid;

	private String rolename;

	private String wtime;

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

}
