/**
 * 
 */
package com.dahantc.erp.commom.dao;

import java.io.Serializable;

/**
 * 数据库排序条件封装实体类
 * 
 * @author 8531
 */
public class SearchOrder implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1754221480931798754L;

	/** 排序的字段 */
	private String orderField;

	/** 排序方式 */
	private String orderBy;

	/** 是否中文 */
	private boolean chinese;

	public SearchOrder() {

	}

	/**
	 * @param orderField
	 *            排序的字段
	 * @param orderBy
	 *            排序方式
	 */
	public SearchOrder(String orderField, String orderBy) {
		this.orderField = orderField;
		this.orderBy = orderBy;
		this.chinese = false;
	}

	public SearchOrder(String orderField, String orderBy, boolean chinese) {
		this.orderField = orderField;
		this.orderBy = orderBy;
		this.chinese = chinese;
	}

	/**
	 * @return 获取排序的字段
	 */
	public String getOrderField() {
		return orderField;
	}

	/**
	 * 设置排序字段
	 * 
	 * @param orderField
	 *            字段名
	 */
	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	/**
	 * @return 排序方式
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序方式
	 * 
	 * @param orderBy
	 *            取值仅限于"ASC"或"DESC"
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/** @return the chinese */
	public boolean isChinese() {
		return chinese;
	}

	/**
	 * @param chinese
	 *            the chinese to set
	 */
	public void setChinese(boolean chinese) {
		this.chinese = chinese;
	}

}
