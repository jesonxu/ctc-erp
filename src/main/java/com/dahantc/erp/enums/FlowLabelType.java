package com.dahantc.erp.enums;

public enum FlowLabelType {

	String("字符串"), // 0

	Integer("整型"),

	Double("浮点类型"), // 2

	Boolean("布尔类型"),

	Date("日期类型"), // 4

	DateTime("时间日期类型"),

	DateMonth("月份类型"), // 6

	Select("下拉框类型"),

	File("文件类型"), // 8

	TextArea("文本类型"), // 9

	// 调价流程专用
	Gradient("价格梯度"), // 10

	PriceType("价格类型"), // 11

	ChargeType("充值类型"), // 12

	Remuneration("酬金类型"), // 金额*酬金比例+奖励-扣款

	AccountInfo("账单信息"), // 14

	AccountBill("账单金额"), // 数量*单价

	Switch("开关类型"), // 16

	SelfInvoice("我司开票信息"), // 17

	OtherInvoice("对方开票信息"), // 18

	SelfBank("我司银行信息"), // 19

	OtherBank("对方银行信息"), // 20

	ContractNumber("合同编号"), // 21

	HistoryPrice("历史单价"), // 22

	InvoiceInfo("发票信息"), // 23

	DsApplyOrder("提单信息"), // 24

	DsMatchOrder("配单信息"), // 25

	DsOrderNumber("订单编号"), // 26

	DsMatchPeople("电商配单员"), // 27

	DsPurchaseNumber("采购单编号"), // 28

	CustInvoiceInfo("客户开票抬头"), // 29

	BillInvoiceInfo("账单开票信息"), // 30

	BankInfo("电商银行信息"), // 31

	TimeAccountBill("时间账单金额"),// 32

	PlatformAccountInfo("平台账号信息"), // 33

	UncheckedBillInfo("未对账账单"), // 34
	
	TimeSlot("时间段"), // 35
	
	CheckBox("单选框"), // 36
	
	RechargeDetail("充值详情"), // 37

	LeaveType("请假类别"), // 38
	//日期+上午下午
	NewTimeSlot("时间分段"),
	;

	private String desc;

	private static String[] descs;

	private FlowLabelType(java.lang.String desc) {
		this.desc = desc;
	}

	private FlowLabelType() {

	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		FlowLabelType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
