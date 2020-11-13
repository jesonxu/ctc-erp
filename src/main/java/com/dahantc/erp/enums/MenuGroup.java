package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum MenuGroup {

	BACK_SYSTEM(1, "后台系统"),

	RESOURCES_SYSTEM(2, "资源子系统"),

	FINANCE_SYSTEM(3, "财务子系统"),

	SALE_SYSTEM(4, "销售子系统"),

	DIANSHANG_SYSTEM(5, "电商子系统");

	MenuGroup(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	private int code;
	private String msg;

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static Optional<MenuGroup> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<MenuGroup> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

	public enum ConsoleType {

		ROLE_MANAGE("角色管理", 1, BACK_SYSTEM), 
		EMPLLOYEE_MANAGE("员工管理", 2, BACK_SYSTEM), 
		FLOW_MANAGE("流程管理", 3, BACK_SYSTEM), 
		SYSTEM_PARAMTER_MANAGE("系统参数", 4, BACK_SYSTEM),
		MANAGER_CONSOLE("管理工作台", 5, BACK_SYSTEM), 
		RESOURCES_CONSOLE("资源工作台", 6, RESOURCES_SYSTEM), 
		CUSTOMER_CONSOLE("客户工作台", 7, SALE_SYSTEM), 
		DS_SUPPLIER_CONSOLE("供应商工作台", 8, DIANSHANG_SYSTEM), 
		DS_DISTRIBUTE_CONSOLE("配货工作台", 9, DIANSHANG_SYSTEM);

		private String desc;
		private int ordinal;
		private MenuGroup group;

		private ConsoleType(String desc, int ordinal, MenuGroup group) {
			this.desc = desc;
			this.ordinal = ordinal;
			this.group = group;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public int getOrdinal() {
			return ordinal;
		}

		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}

		public MenuGroup getGroup() {
			return group;
		}

		public void setGroup(MenuGroup group) {
			this.group = group;
		}

	}
}
