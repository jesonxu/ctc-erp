package com.dahantc.erp.vo.rolerelation.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_role_relation")
@DynamicUpdate(true)
public class RoleRelation implements Serializable {
	private static final long serialVersionUID = -4383842163439043317L;

	@Id
	@Column(name = "rolerelationid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String roleRelationId;

	/** 参数说明 **/
	@Column(name = "roleid", length = 32)
	private String roleId;

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleRelationId() {
		return roleRelationId;
	}

	public void setRoleRelationId(String roleRelationId) {
		this.roleRelationId = roleRelationId;
	}
}
