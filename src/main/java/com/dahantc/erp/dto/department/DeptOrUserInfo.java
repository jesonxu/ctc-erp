package com.dahantc.erp.dto.department;

/**
 * 部门下的同级节点，可能是子部门，也可能是挂在部门下的用户
 */
public class DeptOrUserInfo {

	/** 部门或者用户id */
	private String id;

	/** 部门或者用户名称 */
	private String name;

	/** 类型：0.部门 1.用户 */
	private int type;

	private int status;

	public DeptOrUserInfo(String id, String name, int type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public DeptOrUserInfo(String id, String name, int type, int status) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
