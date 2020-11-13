package com.dahantc.erp.dto.SupplierType;

import com.dahantc.erp.vo.suppliertype.entity.SupplierType;

import java.io.Serializable;

public class SupplierTypeRspDto implements Serializable {

	private static final long serialVersionUID = -2433265180188827149L;

	private String supplierTypeId;

	private String supplierTypeName;
	
	private Long flowEntCount;

	/**
	 * 供应商数量
	 */
	private Integer supplierCount;

	public SupplierTypeRspDto(SupplierType supplierType, Integer supplierCount) {
		this.supplierTypeId = supplierType.getSupplierTypeId();
		this.supplierTypeName = supplierType.getSupplierTypeName();
		this.supplierCount = supplierCount;
	}
	
	public SupplierTypeRspDto(SupplierType supplierType, Integer supplierCount, Long flowEntCount) {
		this.supplierTypeId = supplierType.getSupplierTypeId();
		this.supplierTypeName = supplierType.getSupplierTypeName();
		this.supplierCount = supplierCount;
		this.flowEntCount = flowEntCount;
	}

	public String getSupplierTypeId() {
		return supplierTypeId;
	}

	public void setSupplierTypeId(String supplierTypeId) {
		this.supplierTypeId = supplierTypeId;
	}

	public String getSupplierTypeName() {
		return supplierTypeName;
	}

	public void setSupplierTypeName(String supplierTypeName) {
		this.supplierTypeName = supplierTypeName;
	}

	public Integer getSupplierCount() {
		return supplierCount;
	}

	public void setSupplierCount(Integer supplierCount) {
		this.supplierCount = supplierCount;
	}

	public Long getFlowEntCount() {
		return flowEntCount;
	}

	public void setFlowEntCount(Long flowEntCount) {
		this.flowEntCount = flowEntCount;
	}
	
}
