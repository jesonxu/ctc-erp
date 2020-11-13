package com.dahantc.erp.vo.operationlog.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.LogType;

/**
 * 操作日志实体类
 * 
 * @author 8541
 */
@Entity
@Table(name = "erp_operation_log")
public class OperationLog implements Serializable {

	private static final long serialVersionUID = 124364485978138983L;

	/**
	 * 日志编号
	 */
	@Id
	@Column(length = 32, name = "logid")
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String logId;

	/**
	 * 操作名称
	 */
	@Column(length = 100, name = "actionname")
	private String actionName;

	/**
	 * 操作影响的实体对象类
	 */
	@Column(length = 255, name = "entityclass")
	private String entityClass;

	/**
	 * 日志内容
	 */
	@Column(length = 2000, name = "logmsg")
	private String logMsg;

	/**
	 * 日志类型
	 */
	@Column(name = "logtype", columnDefinition = "int default 0")
	private int logType = LogType.OperationLog.ordinal();

	/**
	 * 操作用户id
	 */
	@Column(length = 32, name = "userid")
	private String userId;

	/**
	 * 操作用户部门id
	 */
	@Column(length = 32, name = "deptid")
	private String deptId;

	/**
	 * 操作者来访IP
	 */
	@Column(length = 100, name = "operatorip")
	private String operatorIp;

	/**
	 * 数据创建时间
	 */

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public OperationLog(String actionName, String entityClass, String logMsg, int logType, String userId, String deptId, String operatorIp, Timestamp wtime) {
		this.actionName = actionName;
		this.entityClass = entityClass;
		this.logMsg = logMsg;
		this.logType = logType;
		this.userId = userId;
		this.deptId = deptId;
		this.operatorIp = operatorIp;
		this.wtime = wtime;
	}

	public OperationLog() {
		super();
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}

	public int getLogType() {
		return logType;
	}

	public void setLogType(int logType) {
		this.logType = logType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getOperatorIp() {
		return operatorIp;
	}

	public void setOperatorIp(String operatorIp) {
		this.operatorIp = operatorIp;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
