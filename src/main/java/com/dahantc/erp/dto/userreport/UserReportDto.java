package com.dahantc.erp.dto.userreport;

import java.util.List;

import com.dahantc.erp.vo.userreport.entity.EnclosureFile;

public class UserReportDto {

	private String userName;
	private String id;
	private String cycle;
	private String content;
	private List<EnclosureFile> enclosure;
	private boolean isLeader;
	private String deptId;
	private long wtime;
	private List<ReportCommentDto> comments;
	private boolean modify;
	private boolean blankReport;

	public boolean isBlankReport() {
		return blankReport;
	}

	public void setBlankReport(boolean blankReport) {
		this.blankReport = blankReport;
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public long getWtime() {
		return wtime;
	}

	public void setWtime(long wtime) {
		this.wtime = wtime;
	}

	public List<ReportCommentDto> getComments() {
		return comments;
	}

	public void setComments(List<ReportCommentDto> comments) {
		this.comments = comments;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<EnclosureFile> getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(List<EnclosureFile> enclosure) {
		this.enclosure = enclosure;
	}

}
