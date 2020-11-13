package com.dahantc.erp.dto.productoperatecost;

import java.math.BigDecimal;

/**
 * 运营固定成本管理表实体类
 *
 */
public class ProductOperateCostDto {

	/**
	 * 部门
	 */
	private String deptName;

	/**
	 * 销售
	 */
	private String saleName;

	/**
	 * 客户名称
	 */
	private String customerName;

	/**
	 * 产品名称
	 */
	private String productName;

	/**
	 * 产品Id
	 */
	private String productId;

	/**
	 * 付费方式
	 */
	private String settleType;

	/**
	 * 客户每月固定运营成本，只需填在一个产品上
	 */
	private BigDecimal productOperateFixedCost;

	/**
	 * 产品均摊每条运营成本
	 */
	private BigDecimal productOperateSingleCost;
	
	/**
	 * 产品类型
	 */
	private String productType;
	
	/**
	 * 运营成本备注
	 */
	private String remark;

	/**
	 * 账单金额比例运营成本的比例
	 */
	private BigDecimal billAmountRatio;

	/**
	 * 账单毛利润比例运营成本的比例
	 */
	private BigDecimal billGrossProfitRatio;
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public BigDecimal getProductOperateFixedCost() {
		return productOperateFixedCost;
	}

	public void setProductOperateFixedCost(BigDecimal productOperateFixedCost) {
		this.productOperateFixedCost = productOperateFixedCost;
	}

	public BigDecimal getProductOperateSingleCost() {
		return productOperateSingleCost;
	}

	public void setProductOperateSingleCost(BigDecimal productOperateSingleCost) {
		this.productOperateSingleCost = productOperateSingleCost;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public BigDecimal getBillAmountRatio() {
		return billAmountRatio;
	}

	public void setBillAmountRatio(BigDecimal billAmountRatio) {
		this.billAmountRatio = billAmountRatio;
	}

	public BigDecimal getBillGrossProfitRatio() {
		return billGrossProfitRatio;
	}

	public void setBillGrossProfitRatio(BigDecimal billGrossProfitRatio) {
		this.billGrossProfitRatio = billGrossProfitRatio;
	}
}
