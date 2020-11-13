package com.dahantc.erp.dto.operate;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;

/**
 * 
 * @author wangyang
 *
 */
public class ProductBillsDto implements Serializable {

	private static final long serialVersionUID = -3883379925913092050L;

	/**
	 * 账单id
	 */
	@NotBlank(message = "账单id不能为空")
	private String id;

	/**
	 * 应收账款
	 */
	private BigDecimal receivables;

	/**
	 * 实收账款
	 */
	private BigDecimal actualReceivables;

	/**
	 * 应付账款
	 */
	private BigDecimal payables;

	/**
	 * 实付账款
	 */
	private BigDecimal actualpayables;

	/**
	 * 已开发票金额
	 */
	private BigDecimal actualInvoiceAmount;

	/**
	 * 被未走完的流程使用了的金额，比如未走完流程的开票金额，未走完流程的付款金额
	 */
	private BigDecimal usedAmount;

	/**
	 * 标题
	 */
	@NotBlank(message = "标题值不能为空")
	private String title;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public BigDecimal getPayables() {
		return payables;
	}

	public void setPayables(BigDecimal payables) {
		this.payables = payables;
	}

	public BigDecimal getActualpayables() {
		return actualpayables;
	}

	public void setActualpayables(BigDecimal actualpayables) {
		this.actualpayables = actualpayables;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getActualInvoiceAmount() {
		return actualInvoiceAmount;
	}

	public void setActualInvoiceAmount(BigDecimal actualInvoiceAmount) {
		this.actualInvoiceAmount = actualInvoiceAmount;
	}

	public BigDecimal getUsedAmount() {
		return usedAmount;
	}

	public void setUsedAmount(BigDecimal usedAmount) {
		this.usedAmount = usedAmount;
	}
}
