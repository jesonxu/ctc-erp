package com.dahantc.erp.vo.invoice.entity;

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

import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceStatus;

/**
 * 发票表
 */
@Entity
@Table(name = "erp_invoice")
@DynamicUpdate(true)
public class Invoice implements Serializable {

	private static final long serialVersionUID = -3257587341765186727L;
	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	// 申请人
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 申请时间
	@Column(name = "applytime")
	private Timestamp applyTime = new Timestamp(System.currentTimeMillis());

	@Column(name = "entitytype")
	private int entityType = EntityType.CUSTOMER.getCode();;

	@Column(name = "entityid", length = 32)
	private String entityId;

	@Column(name = "productid", length = 32)
	private String productId;

	// 开票金额，即此发票的应收金额
	@Column(name = "receivables", columnDefinition = "decimal(19,2) default 0")
	private BigDecimal receivables = new BigDecimal(0);

	// 已收金额，即开票流程填的已收金额
	@Column(name = "actualreceivables", columnDefinition = "decimal(19,2) default 0")
	private BigDecimal actualReceivables = new BigDecimal(0);

	// 我司开票信息ID 关联InvoiceInformation
	@Column(name = "bankinvoiceid", length = 32)
	private String bankInvoiceId;

	// 对方开票信息ID 关联InvoiceInformation
	@Column(name = "oppositebankinvoiceid", length = 32)
	private String oppositeBankInvoiceId;

	// 发票状态
	@Column(name = "invoicestatus", columnDefinition = "int default 0")
	private int invoiceStatus = InvoiceStatus.INVOICED.ordinal();

	// 开票服务名称
	@Column(name = "servicename")
	private String serviceName;

	// 发票类型
	@Column(name = "invoicetype")
	private String invoiceType;

	// 开票流程id
	@Column(name = "flowentid", length = 32)
	private String flowEntId;

	// 备注
	@Column(name = "remark", length = 400)
	private String remark;

	// 开票时间，即开票流程归档时间
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public Timestamp getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Timestamp applyTime) {
		this.applyTime = applyTime;
	}

	public int getEntityType() {
		return entityType;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getProductId() {
		return productId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public BigDecimal getActualReceivables() {
		return actualReceivables;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public void setActualReceivables(BigDecimal actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public String getId() {
		return id;
	}

	public String getBankInvoiceId() {
		return bankInvoiceId;
	}

	public String getOppositeBankInvoiceId() {
		return oppositeBankInvoiceId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setBankInvoiceId(String bankInvoiceId) {
		this.bankInvoiceId = bankInvoiceId;
	}

	public void setOppositeBankInvoiceId(String oppositeBankInvoiceId) {
		this.oppositeBankInvoiceId = oppositeBankInvoiceId;
	}

	public int getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(int invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowId) {
		this.flowEntId = flowId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}
}
