package com.dahantc.erp.dto.userreport;

public class ReportCommentDto {

	private String id; // 主键

	private String reportId; // 所属汇报id

	private String userName;

	private String comment;

	private long wtime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getWtime() {
		return wtime;
	}

	public void setWtime(long wtime) {
		this.wtime = wtime;
	}
	
}
