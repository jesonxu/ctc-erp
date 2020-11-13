/**
 * 
 */
package com.dahantc.erp.commom.dao;

import java.io.Serializable;

/**
 * 查询条件
 * 
 * @author 8531
 */
public class SearchRule implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3416653872853849042L;

	/** 字段值 */
	private Object data;

	/** 查询字段 */
	private String field;

	/** 查询操作 */
	private String op;

	public SearchRule() {
		super();
	}

	public SearchRule(String field, String op, Object data) {
		super();
		this.field = field;
		this.op = op;
		this.data = data;
	}

	public String getField() {
		return field;
	}

	public String getOp() {
		return op;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}