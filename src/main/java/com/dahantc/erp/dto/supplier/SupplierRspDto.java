package com.dahantc.erp.dto.supplier;

public class SupplierRspDto {

	private String supplierId;

	private String companyName;

	private String supplierTypeId;

	private String supplierTypeName;

	private Long flowEntCount;

	public SupplierRspDto(String supplierId, String companyName) {
		this.supplierId = supplierId;
		this.companyName = companyName;
	}

	public SupplierRspDto(String supplierId, String companyName, String supplierTypeId) {
		this.supplierId = supplierId;
		this.companyName = companyName;
		this.supplierTypeId = supplierTypeId;
	}

	public SupplierRspDto(String supplierId, String companyName, String supplierTypeId, Long flowEntCount) {
		this.supplierId = supplierId;
		this.companyName = companyName;
		this.supplierTypeId = supplierTypeId;
		this.flowEntCount = flowEntCount;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public Long getFlowEntCount() {
		return flowEntCount;
	}

	public void setFlowEntCount(Long flowEntCount) {
		this.flowEntCount = flowEntCount;
	}

}
