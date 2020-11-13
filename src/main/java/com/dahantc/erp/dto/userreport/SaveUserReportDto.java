package com.dahantc.erp.dto.userreport;

import javax.validation.constraints.NotBlank;

public class SaveUserReportDto {

	private String reportId;

	@NotBlank(message = "请输入汇报内容")
	private String content;

	private String files;

	private Integer reportType;

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

}
