package com.dahantc.erp.vo.goal.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.GoalType;

/**
 * 销售业绩目标表，每个销售每个月的目标销售额和目标毛利
 */
@Entity
@Table(name = "erp_goal")
@DynamicUpdate(true)
public class Goal implements Serializable {

	private static final long serialVersionUID = -3294702368304073750L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", length = 32)
	private String id;

	// 用户表id
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 部门表id
	@Column(name = "deptid", length = 32)
	private String deptId;

	// 目标类型，销售个人目标，部门目标，公司目标
	@Column(name = "goaltype", columnDefinition = "int(11) default 0")
	private int goalType = GoalType.DeptMonth.ordinal();

	// 目标销售额
	@Column(name = "receivables")
	private BigDecimal receivables = new BigDecimal(0);

	// 目标毛利润
	@Column(name = "grossprofit")
	private BigDecimal grossProfit = new BigDecimal(0);

	// 月份yyyy-MM-01
	@Column(name = "wtime")
	private Timestamp wtime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getGoalType() {
		return goalType;
	}

	public void setGoalType(int goalType) {
		this.goalType = goalType;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}
}
