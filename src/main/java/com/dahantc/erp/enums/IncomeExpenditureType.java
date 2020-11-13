package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum IncomeExpenditureType  {

	PREPURCHASE(0, "预购充值"),

	ADVANCE(1, "预付充值"),

	DEPOSIT(2, "押金"),

	REMUNERATION(3, "酬金"),

	BILL(4, "账单金额"),

	MANPWAGES(5, "人力工资"),

	DAILYFEES(6, "日常费用"),

	TRAVELFEES(7, "差旅费用"),

	ADMINISTRATIVEFEES(8, "行政支出"),

	PROPERTYFEE(9, "房租物业水电费"),

	FIXEDASSETSFEES(10, "固定资产费用"),

	SERVERFEES(11, "服务器费用"),

	FINANCEFEES(12, "财务费用"),

	INTERESTFEES(13, "利息支出"),

	SERVICECHARGE(14, "手续费"),

	ADDITIONALCOST(15, "附加税");


	IncomeExpenditureType(int code, String msg) {
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

	public static Optional<IncomeExpenditureType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<IncomeExpenditureType> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

	public static String getTypeName(int code) {
		String type = "未知";
		Optional<IncomeExpenditureType> opt = getEnumsByCode(code);
		if (opt.isPresent()) {
			type = opt.get().msg;
		}
		return type;
	}

}
