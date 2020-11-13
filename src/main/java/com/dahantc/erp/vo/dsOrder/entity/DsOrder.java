package com.dahantc.erp.vo.dsOrder.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "erp_ds_order")
@DynamicUpdate(true)
public class DsOrder implements Serializable {

	private static final long serialVersionUID = 5752098231196384541L;

	/**
	 * 订单编号
	 */
	@Id
	@Column(name = "orderid", columnDefinition = "varchar(32) COMMENT '订单编号'")
	private String orderId;

	/**
	 * 销售id
	 */
	@Column(name = "ossuserid", columnDefinition = "varchar(32) COMMENT '销售id'")
	private String ossUserId;

	/**
	 * 销售名
	 */
	@Column(name = "ossusername", columnDefinition = "varchar(32) COMMENT '销售名'")
	private String ossUserName;

	/**
	 * 项目id（产品id）
	 */
	@Column(name = "projectid", columnDefinition = "varchar(32) COMMENT '项目id'")
	private String projectId;

	/**
	 * 项目名称
	 */
	@Column(name = "projectname", columnDefinition = "varchar(255) COMMENT '项目名称'")
	private String projectName;

	/**
	 * 客户id
	 */
	@Column(name = "customerid", columnDefinition = "varchar(32) COMMENT '客户id'")
	private String customerId;

	/**
	 * 客户名称
	 */
	@Column(name = "customername", columnDefinition = "varchar(255) COMMENT '客户名称'")
	private String customerName;

	/**
	 * 交付日期
	 */
	@Column(name = "duetime", columnDefinition = "DATETIME COMMENT '交付日期'")
	private Date dueTime;

	/**
	 * 销售金额，销售输入
	 */
	@Column(name = "salesmoney", columnDefinition = "Decimal(19,2) COMMENT '销售金额'")
	private BigDecimal salesMoney;

	/**
	 * 采购成本总额，自动计算（包装设计费 + 采购物流费总计 + 每个配单商品的销售总额）（每个商品的销售总额也自动计算，每个商品的单价*数量）
	 */
	@Column(name = "purchasecost", columnDefinition = "Decimal(19,2) COMMENT '采购成本金额'")
	private BigDecimal purchaseCost;

	/**
	 * 包装设计费，销售输入
	 */
	@Column(name = "designfee", columnDefinition = "Decimal(19,2) COMMENT '包装设计费'")
	private BigDecimal designFee;

	/**
	 * 采购物流费总计，自动计算，每个商品的物流费之和
	 */
	@Column(name = "logisticscosts", columnDefinition = "Decimal(19,2) COMMENT '采购物流费'")
	private BigDecimal logisticsCosts;

	/**
	 * 发票种类
	 */
	@Column(name = "invoicetype", columnDefinition = "varchar(255) COMMENT '发票种类'")
	private String invoiceType;

	/**
	 * 发票税点
	 */
	@Column(name = "rant", columnDefinition = "int(11) COMMENT '发票税点'")
	private int rant;

	/**
	 * 发货形式(2:集采 1：一件代发)
	 */
	@Column(name = "sendtype", columnDefinition = "int(2) default 2 COMMENT '发货形式(2:集采 1：一件代发)'")
	private int sendType;

	/**
	 * 配送地址
	 */
	@Column(name = "sendaddress", columnDefinition = "varchar(255) COMMENT '配送地址'")
	private String sendAddress;

	/**
	 * 配送地址附件json字符串，fileName filePath
	 */
	@Column(name = "sendaddressfile", columnDefinition = "varchar(1000) COMMENT '配送地址附件'")
	private String sendAddressFile;

	/**
	 * 报价单有效期
	 */
	@Column(name = "validtime", columnDefinition = "DATETIME COMMENT '报价单有效期'")
	private Date validTime;

	/**
	 * 创建日期
	 */
	@Column(name = "wtime", columnDefinition = "DATETIME COMMENT '创建日期'")
	private Date wtime;

	/**
	 * 流水号
	 */
	@Column(name = "serialno", columnDefinition = "int(2) COMMENT '流水号'")
	private int serialNo;

	/**
	 * 订单状态
	 */
	@Column(name = "orderstatus", columnDefinition = "int(2) default 0 COMMENT '订单状态'")
	private int orderStatus;

	/**
	 * 付款形式 0：预付费 1：后付费
	 */
	@Column(name = "paytype", columnDefinition = "int(2) default 0 COMMENT '付款形式 0：预付费 1：后付费'")
	private int payType;

	/**
	 * 付款周期
	 */
	@Column(name = "payperiod", columnDefinition = "int(3) COMMENT '付款周期'")
	private int payPeriod;

	/**
	 * 负责人id
	 */
	@Column(name = "matchpeopleid", columnDefinition = "varchar(32) COMMENT '负责人id'")
	private String matchPeopleId;

	/**
	 * 负责人名字
	 */
	@Column(name = "matchpeoplename", columnDefinition = "varchar(255) COMMENT '负责人名字'")
	private String matchPeopleName;

	/**
	 * 收货联系人
	 */
	@Column(name = "contactperson", columnDefinition = "varchar(255) COMMENT '联系人'")
	private String contactPerson;

	/**
	 * 联系电话
	 */
	@Column(name = "contactno", columnDefinition = "varchar(32) COMMENT '联系电话'")
	private String contactNo;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getOssUserName() {
		return ossUserName;
	}

	public void setOssUserName(String ossUserName) {
		this.ossUserName = ossUserName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public int getRant() {
		return rant;
	}

	public void setRant(int rant) {
		this.rant = rant;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public String getSendAddressFile() {
		return sendAddressFile;
	}

	public void setSendAddressFile(String sendAddressFile) {
		this.sendAddressFile = sendAddressFile;
	}

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public BigDecimal getPurchaseCost() {
		return purchaseCost;
	}

	public void setPurchaseCost(BigDecimal purchaseAmount) {
		this.purchaseCost = purchaseAmount;
	}

	public BigDecimal getDesignFee() {
		return designFee;
	}

	public void setDesignFee(BigDecimal designFee) {
		this.designFee = designFee;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public int getPayPeriod() {
		return payPeriod;
	}

	public void setPayPeriod(int payPeriod) {
		this.payPeriod = payPeriod;
	}

	public String getMatchPeopleId() {
		return matchPeopleId;
	}

	public void setMatchPeopleId(String matchPeopleId) {
		this.matchPeopleId = matchPeopleId;
	}

	public String getMatchPeopleName() {
		return matchPeopleName;
	}

	public void setMatchPeopleName(String matchPeopleName) {
		this.matchPeopleName = matchPeopleName;
	}

	public BigDecimal getSalesMoney() {
		return salesMoney;
	}

	public void setSalesMoney(BigDecimal salesMoney) {
		this.salesMoney = salesMoney;
	}

	public BigDecimal getLogisticsCosts() {
		return logisticsCosts;
	}

	public void setLogisticsCosts(BigDecimal logisticsCosts) {
		this.logisticsCosts = logisticsCosts;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	@Override
	public String toString() {
		return "DsOrder{" + "orderId='" + orderId + '\'' + ", ossUserId='" + ossUserId + '\'' + ", ossUserName='" + ossUserName + '\'' + ", projectId='"
				+ projectId + '\'' + ", projectName='" + projectName + '\'' + ", customerId='" + customerId + '\'' + ", customerName='" + customerName + '\''
				+ ", dueTime=" + dueTime + ", salesMoney=" + salesMoney + ", purchaseCost=" + purchaseCost + ", designFee=" + designFee + ", logisticsCosts="
				+ logisticsCosts + ", invoiceType='" + invoiceType + '\'' + ", rant=" + rant + ", sendType=" + sendType + ", sendAddress='" + sendAddress + '\''
				+ ", sendAddressFile='" + sendAddressFile + '\'' + ", validTime=" + validTime + ", wtime=" + wtime + ", serialNo=" + serialNo + ", orderStatus="
				+ orderStatus + ", payType=" + payType + ", payPeriod=" + payPeriod + ", matchPeopleId='" + matchPeopleId + '\'' + ", matchPeopleName='"
				+ matchPeopleName + '\'' + ", contactPerson='" + contactPerson + '\'' + ", contactNo='" + contactNo + '\'' + '}';
	}
}
