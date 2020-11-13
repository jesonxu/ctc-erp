package com.dahantc.erp.vo.dept.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "erp_department")
public class Department implements Serializable {

	private static final long serialVersionUID = 5556399557977860809L;

	/**
	 * 部门ID
	 */
	@Id
	@Column(length = 32)
	private String deptid;

	/**
	 * 部门名称
	 */
	@Column(unique = true)
	private String deptname;

	/**
	 * 父节点id
	 */
	private String parentid;
	
	/**
	 * 排序
	 */
	private String sequence;
	
	/**
	 * 标记是否已删除
	 */
	private int flag;
	

	/**
	 * 数据创建时间
	 */
	private Date writetime = new Date();

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public Date getWritetime() {
		return writetime;
	}

	public void setWritetime(Date writetime) {
		this.writetime = writetime;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "Department{" +
				"deptid='" + deptid + '\'' +
				", deptname='" + deptname + '\'' +
				", flag=" + flag +
				'}';
	}
}
