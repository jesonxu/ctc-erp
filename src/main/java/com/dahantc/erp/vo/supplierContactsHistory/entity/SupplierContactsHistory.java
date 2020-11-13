package com.dahantc.erp.vo.supplierContactsHistory.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;

@Entity
@Table(name = "erp_supplier_contacts_history")
@DynamicUpdate(true)
public class SupplierContactsHistory implements Serializable {

	private static final long serialVersionUID = 8244589371204345414L;

	@Id
	@Column(name = "suppliercontactshistoryid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String supplierContactsHistoryId;

	@Column(name = "suppliercontactsid", length = 32)
	private String supplierContactsId;

	/**
	 * 供应商id
	 */
	@Column(name = "supplierid", length = 32)
	private String supplierId;

	/**
	 * 部门名称
	 */
	@Column(name = "deptmame", length = 255)
	private String deptName;

	/**
	 * 联系人姓名
	 */
	@Column(name = "contactsname", length = 255)
	private String contactsName;

	/**
	 * 职位
	 */
	@Column(length = 255)
	private String post;

	/**
	 * 手机1
	 */
	@Column(name = "firstphone", length = 255)
	private String firstPhone;

	/**
	 * 手机2
	 */
	@Column(name = "secondphone", length = 255)
	private String secondPhone;

	/**
	 * 座机
	 */
	@Column(length = 255)
	private String telephone;

	/**
	 * 邮箱
	 */
	@Column(length = 255)
	private String email;

	/**
	 * 备注
	 */
	@Column(length = 255)
	private String remark;

	/**
	 * 微信
	 */
	@Column(length = 255)
	private String wx;

	/**
	 * QQ
	 */
	@Column(length = 255)
	private String qq;

	/**
	 * 创建时间
	 */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getSupplierContactsHistoryId() {
		return supplierContactsHistoryId;
	}

	public void setSupplierContactsHistoryId(String supplierContactsHistoryId) {
		this.supplierContactsHistoryId = supplierContactsHistoryId;
	}

	public String getSupplierContactsId() {
		return supplierContactsId;
	}

	public void setSupplierContactsId(String supplierContactsId) {
		this.supplierContactsId = supplierContactsId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getContactsName() {
		return contactsName;
	}

	public void setContactsName(String contactsName) {
		this.contactsName = contactsName;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getFirstPhone() {
		return firstPhone;
	}

	public void setFirstPhone(String firstPhone) {
		this.firstPhone = firstPhone;
	}

	public String getSecondPhone() {
		return secondPhone;
	}

	public void setSecondPhone(String secondPhone) {
		this.secondPhone = secondPhone;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getWx() {
		return wx;
	}

	public void setWx(String wx) {
		this.wx = wx;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public static SupplierContactsHistory buildHistory(SupplierContacts contacts) {
		return JSON.parseObject(JSON.toJSONString(contacts), SupplierContactsHistory.class);
	}
}
