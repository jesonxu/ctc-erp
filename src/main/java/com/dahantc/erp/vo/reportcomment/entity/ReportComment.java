package com.dahantc.erp.vo.reportcomment.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 报告评论表
 *
 */
@Entity
@Table(name = "erp_report_comment")
public class ReportComment implements Serializable {

	private static final long serialVersionUID = 4861317227871404400L;

	/**
	 * 主键
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/** 汇报id */
	@Column(name = "reportid", length = 32)
	private String reportId;

	/** 评论人ID */
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	/** 评论人部门ID */
	@Column(name = "deptid", length = 32)
	private String deptId;

	/** 评论、回复内容 */
	@Column(length = 255)
	private String comment;

	/** 评论发表时间 */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

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

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}