package com.dahantc.erp.dto.dsSaleData;

import java.math.BigDecimal;

public class CustomerReturnDto {
	//部门
	private String department;
	//销售人员
	private String saleName;
	//客户名称
	private String customerName;
	//签单金额
	private BigDecimal orderTotalPrice;
	//应收账款
	private BigDecimal needReturn;
	//实收账款
	private BigDecimal realReturn;

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public BigDecimal getNeedReturn() {
		return needReturn;
	}

	public void setNeedReturn(BigDecimal needReturn) {
		this.needReturn = needReturn;
	}

	public BigDecimal getRealReturn() {
		return realReturn;
	}

	public void setRealReturn(BigDecimal realReturn) {
		this.realReturn = realReturn;
	}

	public BigDecimal getOrderTotalPrice() {
		return orderTotalPrice;
	}

	public void setOrderTotalPrice(BigDecimal orderTotalPrice) {
		this.orderTotalPrice = orderTotalPrice;
	}

	@Override
	public String toString() {
		return "CustomerReturnDto [department=" + department + ", saleName=" + saleName + ", customerName="
				+ customerName + ", orderTotalPrice=" + orderTotalPrice + ", needReturn=" + needReturn + ", realReturn="
				+ realReturn + "]";
	}
	
}
