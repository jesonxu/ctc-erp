package com.dahantc.erp.dto.operate;

import com.dahantc.erp.enums.EntityType;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class InvoiceDto implements Serializable {

	private static final long serialVersionUID = -3883370025913092050L;

	private String id;

	/**
	 * 标题
	 */
	@NotBlank(message = "标题值不能为空")
	private String title;

	/**
	 * 主体类型，供应商/客户
	 */
	private int entityType = EntityType.SUPPLIER.ordinal();

	/**
	 * 主体id，供应商id/客户id
	 */
	private String entityId;

	/**
	 * 产品id
	 */
	private String productId;

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/**
	 * 应收金额
	 */
	private BigDecimal receivables = new BigDecimal(0);

	/**
	 * 实收金额
	 */
	private BigDecimal actualReceivables = new BigDecimal(0);

	/**
	 * 开票银行ID 关联InvoiceInformation
	 */
	private String bankInvoiceId;

	/**
	 * 对方银行ID 关联InvoiceInformation
	 */
	private String oppositeBankInvoiceId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public BigDecimal getActualReceivables() {
		return actualReceivables;
	}

	public void setActualReceivables(BigDecimal actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public String getBankInvoiceId() {
		return bankInvoiceId;
	}

	public void setBankInvoiceId(String bankInvoiceId) {
		this.bankInvoiceId = bankInvoiceId;
	}

	public String getOppositeBankInvoiceId() {
		return oppositeBankInvoiceId;
	}

	public void setOppositeBankInvoiceId(String oppositeBankInvoiceId) {
		this.oppositeBankInvoiceId = oppositeBankInvoiceId;
	}
}
