package com.dahantc.erp.dto.role;

import java.io.Serializable;

/**
 * 角色分页请求数据
 * 
 * @author wangyang
 *
 */
public class RolePageReqDto implements Serializable {

	private static final long serialVersionUID = -5788302174099604694L;

	private String roleName;

	// 默认查询第一页
	private int page = 1;
	// 默认查询前10条
	private int limit = 10;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
