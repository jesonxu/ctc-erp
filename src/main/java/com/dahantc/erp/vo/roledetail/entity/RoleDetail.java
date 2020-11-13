package com.dahantc.erp.vo.roledetail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * ema_role_detail 实体类 Mon Feb 25 17:22:37 CST 2019 wangyang
 */

@Entity
@Table(name = "erp_role_detail")
@DynamicUpdate(true)
public class RoleDetail implements Serializable {

	private static final long serialVersionUID = 9183806586845229536L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String rdetailid;

	@Column(name = "defaultMenu", columnDefinition = "int default 0")
	private int defalutMenuType;// 是否默认菜单，0-否，1-是

	@Column(name = "menuid", length = 32)
	private String menuid;

	@Column(name = "roleid", length = 32)
	private String roleid;

	public void setRdetailid(String rdetailid) {
		this.rdetailid = rdetailid;
	}

	public String getRdetailid() {
		return rdetailid;
	}

	public int getDefalutMenuType() {
		return defalutMenuType;
	}

	public void setDefalutMenuType(int defalutMenuType) {
		this.defalutMenuType = defalutMenuType;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public String getMenuid() {
		return menuid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRoleid() {
		return roleid;
	}

}
