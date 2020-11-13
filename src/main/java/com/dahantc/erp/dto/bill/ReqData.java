package com.dahantc.erp.dto.bill;

import java.util.List;

public class ReqData {

	/**
	 * 产品类型
	 */
	private int product;

	/**
	 * 账号列表
	 */
	private String[] account;

	/**
	 * 月份yyyy-MM
	 */
	private String[] month;

	public int getProduct() {
		return product;
	}

	public void setProduct(int product) {
		this.product = product;
	}

	public String[] getAccount() {
		return account;
	}

	public void setAccount(String[] account) {
		this.account = account;
	}

	public String[] getMonth() {
		return month;
	}

	public void setMonth(String[] month) {
		this.month = month;
	}
}
