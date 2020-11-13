/**
 * com.ctc.common.DaoException.java
 * 2012-2-9
 */
package com.dahantc.erp.commom.dao;

/**
 * @author 8531
 */
public class DaoException extends BaseException {

	private static final long serialVersionUID = -1497224962560588548L;

	public DaoException() {

	}
	
	public DaoException(String msg) {
		super(msg);
	}
	
	public DaoException(Throwable tr) {
		super(tr);
	}

	public DaoException(String msg, Throwable tr) {
		super(msg, tr);
	}
}
