package com.dahantc.erp.vo.role.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.enums.DataPermission;

/**
 * ema_role 实体类 Mon Feb 25 17:21:20 CST 2019 wangyang
 */

@Entity
@Table(name = "erp_role")
@DynamicUpdate(true)
public class Role implements Serializable {

	private static final long serialVersionUID = 618217776105241460L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String roleid;

	@Column(name = "rolename", length = 100)
	private String rolename;

	@Column(name = "status", columnDefinition = "int default 1")
	private int status = 1;
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "creatorid", length = 32)
	private String creatorid;

	// 工作台页面权限
	@Column(name = "pagepermission", length = 4000)
	private String pagePeimission;

	// 数据查询权限
	@Column(name = "datapermission", columnDefinition = "int default 0")
	private int dataPermission = DataPermission.Self.ordinal();

	// 自定义数据权限的部门id
	@Column(name = "deptids", length = 2000)
	private String deptIds;

	public Map<String, Boolean> getPagePermissionMap() {
		Map<String, Boolean> permissions = new HashMap<>();
		String perStr = this.getPagePeimission();
		if (StringUtils.isNotBlank(perStr)) {
			JSONObject json = JSON.parseObject(perStr);
			for (Object key : json.keySet()) {
				Boolean value = json.getBoolean(key.toString());
				permissions.put(key.toString(), value);
			}
		}
		return permissions;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getRolename() {
		return rolename;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setCreatorid(String creatorid) {
		this.creatorid = creatorid;
	}

	public String getCreatorid() {
		return creatorid;
	}

	public String getPagePeimission() {
		return pagePeimission;
	}

	public void setPagePeimission(String pagePeimission) {
		this.pagePeimission = pagePeimission;
	}

	public int getDataPermission() {
		return dataPermission;
	}

	public void setDataPermission(int dataPermission) {
		this.dataPermission = dataPermission;
	}

	public String getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(String deptIds) {
		this.deptIds = deptIds;
	}
}
