package com.dahantc.erp.dto.role;

import com.dahantc.erp.enums.DataPermission;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 角色修改请求参数
 * 
 * @author wangyang
 *
 */
public class EditRoleReqDto implements Serializable {

	private static final long serialVersionUID = -4088960873030272802L;
	@NotBlank(message = "角色id不能为空")
	private String roleId;
	@NotEmpty(message = "角色名称不能为空")
	private String roleName;

	private String defaultMenuId;

	private List<String> otherMenuIds;

	private String pagePermission = "";

	private int dataPermission = DataPermission.Self.ordinal();

	private String deptIds = "";

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDefaultMenuId() {
		return defaultMenuId;
	}

	public void setDefaultMenuId(String defaultMenuId) {
		this.defaultMenuId = defaultMenuId;
	}

	public List<String> getOtherMenuIds() {
		return otherMenuIds;
	}

	public void setOtherMenuIds(List<String> otherMenuIds) {
		this.otherMenuIds = otherMenuIds;
	}

	public String getPagePermission() {
		return pagePermission;
	}

	public void setPagePermission(String pagePermission) {
		this.pagePermission = pagePermission;
	}

	public int getDataPermission() {
		return dataPermission;
	}

	public void setDataPermission(int dataPermission) {
		this.dataPermission = dataPermission;
	}

	public String getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(String deptIds) {
		this.deptIds = deptIds;
	}
}
