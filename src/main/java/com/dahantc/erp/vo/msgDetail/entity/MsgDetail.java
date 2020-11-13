package com.dahantc.erp.vo.msgDetail.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_message_detail")
@DynamicUpdate(true)
public class MsgDetail implements Serializable{

	private static final long serialVersionUID = 5147082587054809004L;

	public static final int NOT_READ= 1;
	
	public static final int IS_READ = 0;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "messagedetailid", length = 32)
	private String messagedetailid;

	/**
	 * 对应用户信息id
	 */
	@Column(name = "messageid", length = 32)
	private String messageid;

	/**
	 * 需要提示消息的用户id
	 */
	@Column(name = "userid", length = 32)
	private String userid;
	
	/**
	 * 消息读取状态 0.已读 1.未读
	 **/
	@Column(name = "state", columnDefinition = "int default 2")
	private int state;
	
	/**
	 * 创建时间
	 */
	@Column(name = "wtime")
	private Date wtime;
	
	/**
	 * 阅读时间
	 */
	@Column(name = "readtime")
	private Date readtime;

	public String getMessagedetailid() {
		return messagedetailid;
	}

	public void setMessagedetailid(String messagedetailid) {
		this.messagedetailid = messagedetailid;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public Date getReadtime() {
		return readtime;
	}

	public void setReadtime(Date readtime) {
		this.readtime = readtime;
	}

	@Override
	public String toString() {
		return "MsgDetail [messagedetailid=" + messagedetailid + ", messageid=" + messageid + ", userid=" + userid
				+ ", state=" + state + ", wtime=" + wtime + "]";
	}
	
}
