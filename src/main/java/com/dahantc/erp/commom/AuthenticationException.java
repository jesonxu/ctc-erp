package com.dahantc.erp.commom;

import com.dahantc.erp.commom.dao.BaseException;

/**
 * 用户认证异常
 * 
 * @author 8515
 */
public class AuthenticationException extends BaseException {

	private static final long serialVersionUID = 7305887195510115551L;

	public AuthenticationException() {
		super();
	}

	/**
	 * @param msg
	 */
	public AuthenticationException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param tr
	 */
	public AuthenticationException(String msg, Throwable tr) {
		super(msg, tr);
	}

	/**
	 * @param tr
	 */
	public AuthenticationException(Throwable tr) {
		super(tr);
	}

}
