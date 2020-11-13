package com.dahantc.erp.commom.dao;

/**
 * 基础异常类
 * 
 * @author 8515
 */
public class BaseException extends Exception {

	private static final long serialVersionUID = -421840573472941L;

	public BaseException() {

	}

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(Throwable tr) {
		super(tr);
	}

	public BaseException(String msg, Throwable tr) {
		super(msg, tr);
	}
}
