package com.dahantc.erp.dto.customerOperate;

import java.io.Serializable;

public class BuildBillFlowReqDto implements Serializable {

	private static final long serialVersionUID = -8739488026474483137L;

	// 账单月份
	private String billMonth;

	// 产品id
	private String productId;

	// 账单PDF文件
	private String pdfFileName;

	// 账单PDF文件路径
	private String pdfFilePath;

	// 账单Excel文件路径
	private String excelFileName;

	// 账单Excel文件路径
	private String excelFilePath;

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPdfFilePath() {
		return pdfFilePath;
	}

	public void setPdfFilePath(String pdfFilePath) {
		this.pdfFilePath = pdfFilePath;
	}

	public String getExcelFilePath() {
		return excelFilePath;
	}

	public void setExcelFilePath(String excelFilePath) {
		this.excelFilePath = excelFilePath;
	}

	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}
}
