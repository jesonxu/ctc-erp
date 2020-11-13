/**
 * com.ctc.common.ServiceException.java
 * 2012-2-9
 */
package com.dahantc.erp.commom.dao;

/**
 * @author 8531
 *
 */
public class ServiceException extends BaseException {

	private static final long serialVersionUID = 520754423762836634L;

	public ServiceException() {

	}
	
	public ServiceException(String msg) {
		super(msg);
	}
	
	public ServiceException(Throwable tr) {
		super(tr);
	}

	public ServiceException(String msg, Throwable tr) {
		super(msg, tr);
	}
}
