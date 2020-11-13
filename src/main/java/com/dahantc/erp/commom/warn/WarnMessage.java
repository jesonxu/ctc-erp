package com.dahantc.erp.commom.warn;

import java.util.Calendar;
import java.util.Date;

public class WarnMessage {

	private TriggerType triggerType;
	private WarnLevel warnLevel; // 警告级别；
	private String location; // 警告位置
	private Date time; // 出现时间
	private String warnMsg; // 警告消息

	public WarnMessage() {

	}

	public WarnMessage(TriggerType triggerType, WarnLevel warnLevel, String location, String warnMsg) {
		super();
		this.triggerType = triggerType;
		this.warnLevel = warnLevel;
		this.location = location;
		this.warnMsg = warnMsg;
		time = Calendar.getInstance().getTime();
	}

	public WarnLevel getWarnLevel() {
		return warnLevel;
	}

	public void setWarnLevel(WarnLevel warnLevel) {
		this.warnLevel = warnLevel;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getWarnMsg() {
		return warnMsg;
	}

	public void setWarnMsg(String warnMsg) {
		this.warnMsg = warnMsg;
	}

	public TriggerType getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}

}
