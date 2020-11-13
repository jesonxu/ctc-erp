package com.dahantc.erp.vo.msgCenter.entity;

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
@Table(name = "erp_message_center")
@DynamicUpdate(true)
public class MsgCenter implements Serializable {

	private static final long serialVersionUID = 3690627837649086279L;

	public static final int ADD_CUSTOMER = 1;

	public static final int ADD_SUPPLIER = 2;

	public static final int ADD_LOG = 3;

	public static final int PUBLIC_INFO = 5;

	public static final int CUSTOMER_WARNING = 6;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "messageid", length = 32)
	private String messageid;

	/**
	 * 消息类型 1.新增客户 2.新增供应商 3.新增客户日志 4.新增联系供应商日志 5.公告信息 6.客户警告通知
	 **/
	@Column(name = "infotype", columnDefinition = "int default 2")
	private int infotype;

	/**
	 * 消息来源id
	 */
	@Column(name = "messagesourceid", length = 32)
	private String messagesourceid;

	/**
	 * 消息内容详情
	 */
	@Column(name = "messagedetail", columnDefinition = "TEXT")
	private String messagedetail;

	/**
	 * 客户类型id
	 */
	@Column(name = "customertype", columnDefinition = "int(11) COMMENT '客户类型'")
	private Integer customerType;

	/**
	 * 添加人id
	 */
	@Column(name = "ossuserid", columnDefinition = "varchar(32) COMMENT '添加人id'")
	private String ossUserId;

	/**
	 * 创建时间
	 */
	@Column(name = "wtime")
	private Date wtime;

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

	public int getCustomerType() {
		return customerType;
	}

	public void setCustomerType(int customerType) {
		this.customerType = customerType;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	@Override
	public String toString() {
		return "MsgCenter [messageid=" + messageid + ", infotype=" + infotype + ", messagesourceid=" + messagesourceid + ", messagedetail=" + messagedetail
				+ ", customerType=" + customerType + ", ossUserId=" + ossUserId + ", wtime=" + wtime + "]";
	}

}
