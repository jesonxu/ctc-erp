package com.dahantc.erp.dto.flow;

import java.io.Serializable;

/**
 * 主体（客户 | 供应商）对应的未处理流程数量
 *
 * @author 8520
 */
public class SubFlowCount implements Serializable {
	private static final long serialVersionUID = -319724264677813113L;

	/**
	 * 主体id （客户id 或者 供应商id）
	 */
	private String subId;

	/**
	 * 部门id
	 */
	private String deptId;

	/**
	 * 流程数量
	 */
	private Integer flowCount;

	/**
	 * 产品销售id
	 */
	private String ossUserId;

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public Integer getFlowCount() {
		return flowCount;
	}

	public void setFlowCount(Integer flowCount) {
		this.flowCount = flowCount;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public SubFlowCount() {

	}

	public SubFlowCount(String subId, Integer flowCount) {
		this.subId = subId;
		this.flowCount = flowCount;
	}

	public SubFlowCount(String subId, String deptId, Integer flowCount, String ossUserId) {
		this.subId = subId;
		this.deptId = deptId;
		this.flowCount = flowCount;
		this.ossUserId = ossUserId;
	}

	@Override
	public String toString() {
		return "SubjectFlowCount{" + "subId='" + subId + "', flowCount=" + flowCount + ", ossUserId='" + ossUserId + "'}";
	}
}
