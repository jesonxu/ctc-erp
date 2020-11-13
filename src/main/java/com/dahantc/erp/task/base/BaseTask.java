package com.dahantc.erp.task.base;

import java.util.Date;

import com.dahantc.erp.commom.CTCThread;

/**
 * 系统后台任务封装类
 * 
 * @author 8515
 *
 */
public class BaseTask extends CTCThread {

	private boolean isPause;

	private Date lastStopTime;

	/**
	 * @return the isPause
	 */
	public boolean isPause() {
		return isPause;
	}

	/**
	 * @param isPause
	 *            the isPause to set
	 */
	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	public Date getLastStopTime() {
		return lastStopTime;
	}

	public void setLastStopTime(Date lastStopTime) {
		this.lastStopTime = lastStopTime;
	}
}
