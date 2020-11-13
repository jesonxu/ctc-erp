package com.dahantc.erp.dto.messageCenter;

public class MsgCountRespDto {
	
	/**
	 * 计数
	 */
	private int count;
	
	/**
	 * 消息类型 0.未读消息 1.新增客户 3.新增客户日志 
	 */
	private int infoType;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getInfoType() {
		return infoType;
	}

	public void setInfoType(int infoType) {
		this.infoType = infoType;
	}

	@Override
	public String toString() {
		return "MsgCountRespDto [count=" + count + ", infoType=" + infoType + "]";
	}
	
}
