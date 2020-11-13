package com.dahantc.erp.vo.chargeRecord.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.enums.CheckOutStatus;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.BusinessType;

@Entity
@Table(name = "erp_charge_record")
@DynamicUpdate(true)
public class ChargeRecord implements Serializable {

	private static final long serialVersionUID = -8749052489623980037L;

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "chargerecordid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String chargerecordId;

	@Column(name = "flowentid", length = 32)
	private String flowEntId;
	/**
	 * 供应商id
	 */
	@Column(name = "supplierid", length = 32)
	private String supplierId;

	/**
	 * 产品id
	 */
	@Column(name = "productid", length = 32)
	private String productId;

	/**
	 * 流程发起人
	 */
	@Column(name = "createrid", length = 32)
	private String createrId;

	/**
	 * 单价
	 */
	@Column(name = "price", precision = 19, scale = 6, columnDefinition = "decimal(19,6) default 0")
	private BigDecimal price = BigDecimal.ZERO;

	/**
	 * 充值金额
	 */
	@Column(name = "chargeprice")
	private BigDecimal chargePrice;

	/**
	 * 充值类型
	 */
	@Column(name = "chargetype")
	private int chargeType;

	// 业务类型
	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

	/**
	 * 付款/收款截至日期
	 */
	@Column(name = "finalpaytime")
	private Timestamp finalPayTime = new Timestamp(System.currentTimeMillis());

	/**
	 * 实际付款/收款日期
	 */
	@Column(name = "actualpaytime")
	private Timestamp actualPayTime = new Timestamp(System.currentTimeMillis());
	/**
	 * 收票/开票截至日期
	 */
	@Column(name = "invoicetime")
	private Timestamp invoiceTime = new Timestamp(System.currentTimeMillis());
	/**
	 * 实际收票/开票日期
	 */
	@Column(name = "actualinvoicetime")
	private Timestamp actualInvoiceTime = new Timestamp(System.currentTimeMillis());

	/**
	 * 创建时间
	 */
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "deptid")
	private String deptId;

	/**
	 * 账号
	 */
	@Column(name = "account")
	private String account;

	/**
	 * 备注
	 */
	@Column(name = "remark")
	private String remark;

	/**
	 * 核销状态，即充值记录和到款匹配上
	 */
	@Column(name = "checkout", columnDefinition = "int default 0")
	private int checkOut = CheckOutStatus.NO_CHECKED.ordinal();

	// 剩余未核销金额
	@Column(name = "remaincheckout", columnDefinition = "decimal(19,2) default 0")
	private BigDecimal remainCheckOut = new BigDecimal(0);

	/**
	 * 关联到款信息
	 */
	@Column(name = "checkoutinfo", length = 1000)
	private String checkOutInfo;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getChargerecordId() {
		return chargerecordId;
	}

	public void setChargerecordId(String chargerecordId) {
		this.chargerecordId = chargerecordId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getChargePrice() {
		return chargePrice;
	}

	public void setChargePrice(BigDecimal chargePrice) {
		this.chargePrice = chargePrice;
	}

	public Timestamp getFinalPayTime() {
		return finalPayTime;
	}

	public void setFinalPayTime(Timestamp finalPayTime) {
		this.finalPayTime = finalPayTime;
	}

	public Timestamp getInvoiceTime() {
		return invoiceTime;
	}

	public void setInvoiceTime(Timestamp invoiceTime) {
		this.invoiceTime = invoiceTime;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getCreaterId() {
		return createrId;
	}

	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getChargeType() {
		return chargeType;
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	public void setChargeType(int chargeType) {
		this.chargeType = chargeType;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public Timestamp getActualPayTime() {
		return actualPayTime;
	}

	public void setActualPayTime(Timestamp actualPayTime) {
		this.actualPayTime = actualPayTime;
	}

	public Timestamp getActualInvoiceTime() {
		return actualInvoiceTime;
	}

	public void setActualInvoiceTime(Timestamp actualInvoiceTime) {
		this.actualInvoiceTime = actualInvoiceTime;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public int getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(int checkOut) {
		this.checkOut = checkOut;
	}

	public BigDecimal getRemainCheckOut() {
		return remainCheckOut;
	}

	public void setRemainCheckOut(BigDecimal remainCheckOut) {
		this.remainCheckOut = remainCheckOut;
	}

	public String getCheckOutInfo() {
		return checkOutInfo;
	}

	public void setCheckOutInfo(String relatedInfo) {
		this.checkOutInfo = relatedInfo;
	}
}
