package com.dahantc.erp.vo.cashflow.entity;

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

import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.EntityType;

@Entity
@Table(name = "erp_cash_flow")
@DynamicUpdate(true)
public class CashFlow implements Serializable {

	private static final long serialVersionUID = 2834340739409275306L;

	/**
	 * id
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "productid", length = 32)
	private String productId;
	
	/**
	 * 产品类型
	 */
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;
	
	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

	@Column(name = "entityid", length = 32)
	private String entityId;

	@Column(name = "entitytype", columnDefinition = "int default 0")
	private int entityType = EntityType.SUPPLIER.ordinal();

	/**
	 * 应收金额
	 */
	@Column(name = "receivables")
	private BigDecimal receivables = new BigDecimal(0);

	/**
	 * 实收金额
	 */
	@Column(name = "actualreceivables")
	private BigDecimal actualReceivables = new BigDecimal(0);

	/**
	 * 应付金额
	 */
	@Column(name = "payables")
	private BigDecimal payables = new BigDecimal(0);

	/**
	 * 实付金额
	 */
	@Column(name = "actualpayables")
	private BigDecimal actualPayables = new BigDecimal(0);

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "deptid", length = 32)
	private String deptId;

	public String getId() {
		return id;
	}

	public String getProductId() {
		return productId;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public BigDecimal getActualReceivables() {
		return actualReceivables;
	}

	public BigDecimal getPayables() {
		return payables;
	}

	public BigDecimal getActualPayables() {
		return actualPayables;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public void setActualReceivables(BigDecimal actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public void setPayables(BigDecimal payables) {
		this.payables = payables;
	}

	public void setActualPayables(BigDecimal actualPayables) {
		this.actualPayables = actualPayables;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
}
