package com.dahantc.erp.dto.messageCenter;

import java.sql.Timestamp;

public class MsgInfoRespDto {

	private String messageid;

	private int infotype;

	private String messagesourceid;

	private String messagedetail;
	
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

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

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	@Override
	public String toString() {
		return "MsgInfoRespDto [messageid=" + messageid + ", infotype=" + infotype + ", messagesourceid="
				+ messagesourceid + ", messagedetail=" + messagedetail + ", wtime=" + wtime + "]";
	}
	
}
