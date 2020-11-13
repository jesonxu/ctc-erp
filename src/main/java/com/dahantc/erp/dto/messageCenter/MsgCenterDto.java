package com.dahantc.erp.dto.messageCenter;

import java.util.Date;

public class MsgCenterDto {

	private String messageid;
	
	private int infotype;

	private String messagesourceid;

	private String messagedetail;
	
	private Date wtime;

	// 阅读状态
	private int state;

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public int getInfotype() {
		return infotype;
	}

	public void setInfotype(int infotype) {
		this.infotype = infotype;
	}

	public String getMessagesourceid() {
		return messagesourceid;
	}

	public void setMessagesourceid(String messagesourceid) {
		this.messagesourceid = messagesourceid;
	}

	public String getMessagedetail() {
		return messagedetail;
	}

	public void setMessagedetail(String messagedetail) {
		this.messagedetail = messagedetail;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgCenterDto [messageid=" + messageid + ", infotype=" + infotype + ", messagesourceid="
				+ messagesourceid + ", messagedetail=" + messagedetail + ", wtime=" + wtime + ", state=" + state + "]";
	}
	
}
