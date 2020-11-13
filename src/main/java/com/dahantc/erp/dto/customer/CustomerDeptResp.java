package com.dahantc.erp.dto.customer;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * 客户或者部门的返回信息
 */
public class CustomerDeptResp implements Serializable {

	private static final long serialVersionUID = 1551242384828051068L;
	/**
	 * 是否为部门 0 客户 1 部门 2 销售
	 */
	private Integer isDept;
	/**
	 * 部门id
	 */
	private String deptId;
	/**
	 * 部门名称
	 */
	private String deptName;
	/**
	 * 客户id
	 */
	private String customerId;
	/**
	 * 客户名称
	 */
	private String companyName;
	/**
	 * 客户数量（部门才有）
	 */
	private Integer customerCount;

	/**
	 * 流程数量（客户、部门 都有）
	 */
	private Integer flowCount;

	private boolean onlyShowBasic;

	public boolean isOnlyShowBasic() {
		return onlyShowBasic;
	}

	public void setOnlyShowBasic(boolean onlyShowBasic) {
		this.onlyShowBasic = onlyShowBasic;
	}

	public Integer getIsDept() {
		return isDept;
	}

	public void setIsDept(Integer isDept) {
		this.isDept = isDept;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getFlowCount() {
		return flowCount;
	}

	public void setFlowCount(Integer flowCount) {
		this.flowCount = flowCount;
	}

	public Integer getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(Integer customerCount) {
		this.customerCount = customerCount;
	}

	@Override
	public String toString() {
		return "CustomerDeptResp{" + "isDept=" + isDept + ", deptId='" + deptId + '\'' + ", deptName='" + deptName + '\'' + ", customerId='" + customerId + '\''
				+ ", companyName='" + companyName + '\'' + ", customerCount=" + customerCount + ", flowCount=" + flowCount + '}';
	}
}
