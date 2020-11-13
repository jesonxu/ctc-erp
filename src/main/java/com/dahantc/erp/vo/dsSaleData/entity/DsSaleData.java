package com.dahantc.erp.vo.dsSaleData.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_ds_sale_data")
@DynamicUpdate(true)
public class DsSaleData implements Serializable {
	
	private static final long serialVersionUID = -8832375588940060471L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", length = 32)
	private String id;

	/**
	 * 部门名称
	 */
	@Column(name = "deptname", columnDefinition = "varchar(32) COMMENT '部门名称'")
	private String deptName;

	/**
	 * 销售名
	 */
	@Column(name = "ossusername", columnDefinition = "varchar(32) COMMENT '销售名'")
	private String ossUserName;

	/**
	 * 新增客户数
	 */
	@Column(name = "addcustomercount", columnDefinition = "int(11) COMMENT '新增客户数'")
	private int addCustomerCount;
	
	/**
	 * 客户数
	 */
	@Column(name = "customercount", columnDefinition = "int(11) COMMENT '客户数'")
	private int customerCount;
	
	/**
	 * 新客户数
	 */
	@Column(name = "newcustomercount", columnDefinition = "int(11) COMMENT '新客户数'")
	private int newCustomerCount;
	
	/**
	 * 老客户数
	 */
	@Column(name = "oldcustomercount", columnDefinition = "int(11) COMMENT '老客户数'")
	private int oldCustomerCount;
	
	/**
	 * 新增日志数
	 */
	@Column(name = "addlogcount", columnDefinition = "int(11) COMMENT '新增日志数'")
	private int addLogCount;
	
	/**
	 * 签单数
	 */
	@Column(name = "ordercount", columnDefinition = "int(11) COMMENT '签单数'")
	private int orderCount;
	
	/**
	 * 签单金额
	 */
	@Column(name = "ordertotalprice", columnDefinition = "Decimal(19,2) COMMENT '签单金额'")
	private BigDecimal orderTotalPrice;
	
	/**
	 * 累计客户毛利
	 */
	@Column(name = "grossprofit", columnDefinition = "Decimal(19,2) COMMENT '累计客户毛利'")
	private BigDecimal grossProfit;
	
	/**
	 * 毛利率
	 */
	@Column(name = "grossprofitrate", columnDefinition = "Decimal(19,2) COMMENT '毛利率'")
	private BigDecimal grossProfitRate;
	
	/**
	 * 客户回款
	 */
	@Column(name = "returnmoney", columnDefinition = "Decimal(19,2) COMMENT '客户回款'")
	private BigDecimal returnMoney;
	
	/**
	 * 业绩目标
	 */
	@Column(name = "performancegoal", columnDefinition = "Decimal(19,2) COMMENT '业绩目标'")
	private BigDecimal performanceGoal;
	
	/**
	 * 利润目标
	 */
	@Column(name = "profitgoal", columnDefinition = "Decimal(19,2) COMMENT '利润目标'")
	private BigDecimal profitGoal;
	
	/**
	 * 业绩完成率
	 */
	@Column(name = "performancegoalrate", columnDefinition = "Decimal(19,2) COMMENT '业绩完成率'")
	private BigDecimal performanceGoalRate;
	
	/**
	 * 毛利完成率
	 */
	@Column(name = "profitgoalrate", columnDefinition = "Decimal(19,2) COMMENT '毛利完成率'")
	private BigDecimal profitGoalRate;
	
	/**
	 * 创建日期
	 */
	@Column(name = "wtime", columnDefinition = "DATETIME COMMENT '创建日期'")
	private Date wtime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getOssUserName() {
		return ossUserName;
	}

	public void setOssUserName(String ossUserName) {
		this.ossUserName = ossUserName;
	}

	public int getAddCustomerCount() {
		return addCustomerCount;
	}

	public void setAddCustomerCount(int addCustomerCount) {
		this.addCustomerCount = addCustomerCount;
	}

	public int getAddLogCount() {
		return addLogCount;
	}

	public void setAddLogCount(int addLogCount) {
		this.addLogCount = addLogCount;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public BigDecimal getOrderTotalPrice() {
		return orderTotalPrice;
	}

	public void setOrderTotalPrice(BigDecimal orderTotalPrice) {
		this.orderTotalPrice = orderTotalPrice;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getGrossProfitRate() {
		return grossProfitRate;
	}

	public void setGrossProfitRate(BigDecimal grossProfitRate) {
		this.grossProfitRate = grossProfitRate;
	}

	public BigDecimal getReturnMoney() {
		return returnMoney;
	}

	public void setReturnMoney(BigDecimal returnMoney) {
		this.returnMoney = returnMoney;
	}

	public BigDecimal getPerformanceGoal() {
		return performanceGoal;
	}

	public void setPerformanceGoal(BigDecimal performanceGoal) {
		this.performanceGoal = performanceGoal;
	}

	public BigDecimal getProfitGoal() {
		return profitGoal;
	}

	public void setProfitGoal(BigDecimal profitGoal) {
		this.profitGoal = profitGoal;
	}

	public BigDecimal getPerformanceGoalRate() {
		return performanceGoalRate;
	}

	public void setPerformanceGoalRate(BigDecimal performanceGoalRate) {
		this.performanceGoalRate = performanceGoalRate;
	}

	public BigDecimal getProfitGoalRate() {
		return profitGoalRate;
	}

	public void setProfitGoalRate(BigDecimal profitGoalRate) {
		this.profitGoalRate = profitGoalRate;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public int getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(int customerCount) {
		this.customerCount = customerCount;
	}

	public int getNewCustomerCount() {
		return newCustomerCount;
	}

	public void setNewCustomerCount(int newCustomerCount) {
		this.newCustomerCount = newCustomerCount;
	}

	public int getOldCustomerCount() {
		return oldCustomerCount;
	}

	public void setOldCustomerCount(int oldCustomerCount) {
		this.oldCustomerCount = oldCustomerCount;
	}

	@Override
	public String toString() {
		return "DsSaleData [id=" + id + ", deptName=" + deptName + ", ossUserName=" + ossUserName
				+ ", addCustomerCount=" + addCustomerCount + ", customerCount=" + customerCount + ", newCustomerCount="
				+ newCustomerCount + ", oldCustomerCount=" + oldCustomerCount + ", addLogCount=" + addLogCount
				+ ", orderCount=" + orderCount + ", orderTotalPrice=" + orderTotalPrice + ", grossProfit=" + grossProfit
				+ ", grossProfitRate=" + grossProfitRate + ", returnMoney=" + returnMoney + ", performanceGoal="
				+ performanceGoal + ", profitGoal=" + profitGoal + ", performanceGoalRate=" + performanceGoalRate
				+ ", profitGoalRate=" + profitGoalRate + ", wtime=" + wtime + "]";
	}
	
}
