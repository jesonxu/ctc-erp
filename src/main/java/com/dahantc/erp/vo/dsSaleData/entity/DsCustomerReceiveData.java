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
@Table(name = "erp_ds_customer_receive")
@DynamicUpdate(true)
public class DsCustomerReceiveData implements Serializable {

	private static final long serialVersionUID = -7132848539944037028L;

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
	 * 客户名
	 */
	@Column(name = "customername", columnDefinition = "varchar(32) COMMENT '客户名'")
	private String customerName;

	/**
	 * 签单金额
	 */
	@Column(name = "ordertotalprice", columnDefinition = "Decimal(19,2) COMMENT '签单金额'")
	private BigDecimal orderTotalPrice;
	
	/**
	 * 客户回款
	 */
	@Column(name = "returnmoney", columnDefinition = "Decimal(19,2) COMMENT '客户回款'")
	private BigDecimal returnMoney;
	
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getOrderTotalPrice() {
		return orderTotalPrice;
	}

	public void setOrderTotalPrice(BigDecimal orderTotalPrice) {
		this.orderTotalPrice = orderTotalPrice;
	}

	public BigDecimal getReturnMoney() {
		return returnMoney;
	}

	public void setReturnMoney(BigDecimal returnMoney) {
		this.returnMoney = returnMoney;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	@Override
	public String toString() {
		return "DsCustomerReceiveData [id=" + id + ", deptName=" + deptName + ", ossUserName=" + ossUserName
				+ ", customerName=" + customerName + ", orderTotalPrice=" + orderTotalPrice + ", returnMoney="
				+ returnMoney + ", wtime=" + wtime + "]";
	}
	
}
