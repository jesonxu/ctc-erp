package com.dahantc.erp.dto.role;

import java.io.Serializable;

public class AddRoleDataRespDto implements Serializable {

	private static final long serialVersionUID = 1721069124935546541L;
	// 菜单id
	private String id;
	// 菜单名称
	private String title;

	private String pId;

	private int sequence;

	private boolean status;

	private boolean lay_is_checked;

	private String lay_icon;

	private boolean lay_is_open;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isLay_is_checked() {
		return lay_is_checked;
	}

	public void setLay_is_checked(boolean lay_is_checked) {
		this.lay_is_checked = lay_is_checked;
	}

	public String getLay_icon() {
		return lay_icon;
	}

	public void setLay_icon(String lay_icon) {
		this.lay_icon = lay_icon;
	}

	public boolean isLay_is_open() {
		return lay_is_open;
	}

	public void setLay_is_open(boolean lay_is_open) {
		this.lay_is_open = lay_is_open;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

}
