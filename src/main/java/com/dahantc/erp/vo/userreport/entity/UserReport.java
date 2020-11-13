package com.dahantc.erp.vo.userreport.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.enums.ReportType;

/**
 * 用户报告表
 *
 */
@Entity
@Table(name = "erp_user_report", indexes = { @Index(name = "user_report_deptid", columnList = "deptId"),
		@Index(name = "user_report_userid", columnList = "ossUserId") })
public class UserReport implements Serializable {

	private static final long serialVersionUID = 4861317227871404400L;

	/**
	 * 主键
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/** 报告类型 */
	@Column(name = "reporttype", columnDefinition = "int default 0")
	private int reportType = ReportType.DAYLY.ordinal();

	/** 报告周期（周：第几周，季：第几季，半年：上半年、下半年） */
	@Column(name = "reportcycle", columnDefinition = "int default 0")
	private int reportCyle = 0;

	/** 员工ID */
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	/** 员工部门ID */
	@Column(name = "deptid", length = 32)
	private String deptId;

	/** 发表时间 */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/** 内容 */
	@Column(name = "content", length = 4000)
	private String content;

	/**
	 * 附件（图片格式、word、excel格式，路径） {@link JSONObject} {@value EnclosureFile}
	 */
	@Column(name = "enclosure", length = 4000)
	private String enclosure;

	/** 已读人的 ID（标记是否可以点击已阅） */
	@Column(name = "readeruserid", length = 4000)
	private String readedUserId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public int getReportCyle() {
		return reportCyle;
	}

	public void setReportCyle(int reportCyle) {
		this.reportCyle = reportCyle;
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

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}

	public String getReadedUserId() {
		return readedUserId;
	}

	public void setReadedUserId(String readedUserId) {
		this.readedUserId = readedUserId;
	}

	public List<EnclosureFile> getEnclosureFile() {
		if (StringUtils.isBlank(this.enclosure)) {
			return new ArrayList<>();
		}
		return JSON.parseArray(this.enclosure, EnclosureFile.class);
	}

}
