package com.dahantc.erp.vo.menuItem.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * ema_menu_item 实体类 Mon Feb 25 17:24:32 CST 2019 wangyang
 */

@Entity
@Table(name = "erp_menu_item")
@DynamicUpdate(true)
public class MenuItem implements Serializable {

	private static final long serialVersionUID = 5650556610141628154L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String menuid;

	/** 菜单显示标题 */
	@Column(length = 100)
	private String title;

	@Column(name = "menusequence", columnDefinition = "int default 0")
	private int menusequence;

	@Column(name = "menuGroup", columnDefinition = "int default 0")
	private int menuGroup;

	@Column(name = "url", length = 255)
	private String url;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "icon", length = 50)
	private String icon;
	
	@Column(name = "consoletype", columnDefinition = "int default 0")
	private int consoleType;

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public String getMenuid() {
		return menuid;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getMenusequence() {
		return menusequence;
	}

	public void setMenusequence(int menusequence) {
		this.menusequence = menusequence;
	}

	public int getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(int menuGroup) {
		this.menuGroup = menuGroup;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getConsoleType() {
		return consoleType;
	}

	public void setConsoleType(int consoleType) {
		this.consoleType = consoleType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + menuGroup;
		result = prime * result + ((menuid == null) ? 0 : menuid.hashCode());
		result = prime * result + menusequence;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((wtime == null) ? 0 : wtime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuItem other = (MenuItem) obj;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (menuGroup != other.menuGroup)
			return false;
		if (menuid == null) {
			if (other.menuid != null)
				return false;
		} else if (!menuid.equals(other.menuid))
			return false;
		if (menusequence != other.menusequence)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (wtime == null) {
			if (other.wtime != null)
				return false;
		} else if (!wtime.equals(other.wtime))
			return false;
		return true;
	}
}
