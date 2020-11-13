package com.dahantc.erp.dto.supplier;

public class DsSupplierRspDto {
	
	private String supplierId;
	
	private String companyName;

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "DsSupplierRspDto [supplierId=" + supplierId + ", companyName=" + companyName + "]";
	}
	
}
