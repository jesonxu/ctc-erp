package com.dahantc.erp.dto.bill;

import java.io.Serializable;
import java.math.BigDecimal;

public class UncheckedBillDto implements Serializable {

	private static final long serialVersionUID = -1228748493942278791L;

	private String id;

	// 账单编号
	private String billNumber;

	// 主体id
	private String entityId;

	// 产品id
	private String productId;

	// 主体类型
	private Integer entityType;

	// 账单月份
	private String billMonth;

	// 平台数据-成功数
	private Long platformSuccessCount;

	// 平台数据-平均客户产品单价
	private BigDecimal platformUnitPrice;

	// 平台数据-销售额
	private BigDecimal platformAmount;

	// 客户数据-成功数
	private Long customerSuccessCount;

	// 客户数据-平均客户产品单价
	private BigDecimal customerUnitPrice;

	// 客户数据-销售额
	private BigDecimal customerAmount;

	// 实际的对账成功数
	private Long checkedSuccessCount;

	// 实际的平均客户产品单价
	private BigDecimal checkedUnitPrice;

	// 实际的账单金额
	private BigDecimal checkedAmount;

	private String title;

	// 备注，放单价和发送量详情
	private String remark;

	// pdf电子账单
	private String billFile;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
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

	public Integer getEntityType() {
		return entityType;
	}

	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public Long getPlatformSuccessCount() {
		return platformSuccessCount;
	}

	public void setPlatformSuccessCount(Long platformSuccessCount) {
		this.platformSuccessCount = platformSuccessCount;
	}

	public BigDecimal getPlatformUnitPrice() {
		return platformUnitPrice;
	}

	public void setPlatformUnitPrice(BigDecimal platformUnitPrice) {
		this.platformUnitPrice = platformUnitPrice;
	}

	public BigDecimal getPlatformAmount() {
		return platformAmount;
	}

	public void setPlatformAmount(BigDecimal platformAmount) {
		this.platformAmount = platformAmount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getCustomerSuccessCount() {
		return customerSuccessCount;
	}

	public void setCustomerSuccessCount(Long customerSuccessCount) {
		this.customerSuccessCount = customerSuccessCount;
	}

	public BigDecimal getCustomerUnitPrice() {
		return customerUnitPrice;
	}

	public void setCustomerUnitPrice(BigDecimal customerUnitPrice) {
		this.customerUnitPrice = customerUnitPrice;
	}

	public BigDecimal getCustomerAmount() {
		return customerAmount;
	}

	public void setCustomerAmount(BigDecimal customerAmount) {
		this.customerAmount = customerAmount;
	}

	public Long getCheckedSuccessCount() {
		return checkedSuccessCount;
	}

	public void setCheckedSuccessCount(Long checkedSuccessCount) {
		this.checkedSuccessCount = checkedSuccessCount;
	}

	public BigDecimal getCheckedUnitPrice() {
		return checkedUnitPrice;
	}

	public void setCheckedUnitPrice(BigDecimal checkedUnitPrice) {
		this.checkedUnitPrice = checkedUnitPrice;
	}

	public BigDecimal getCheckedAmount() {
		return checkedAmount;
	}

	public void setCheckedAmount(BigDecimal checkedAmount) {
		this.checkedAmount = checkedAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBillFile() {
		return billFile;
	}

	public void setBillFile(String billFile) {
		this.billFile = billFile;
	}
}
