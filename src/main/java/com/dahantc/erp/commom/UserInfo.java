package com.dahantc.erp.commom;

public class UserInfo {

	private String loginName;

	private int failSize = 0;

	// 下次允许请求的时间
	private long nextReuestTime = System.currentTimeMillis();

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public long getNextReuestTime() {
		return nextReuestTime;
	}

	public void setNextReuestTime(long nextReuestTime) {
		this.nextReuestTime = nextReuestTime;
	}

	public synchronized int addFailSize() {
		return ++failSize;
	}

	public synchronized void setFailSize(int failSize) {
		this.failSize = failSize;
	}
}
