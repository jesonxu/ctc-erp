package com.dahantc.erp.commom;

public class Constants {
	/** 数据分析报告返回的IP校验 */
	public static String DATA_ANALYSIS_CALL_BACK_IP = "";
	/** 活跃用户sessionKey */
	public static final String SESSION_KEY = "OnlineUser";
	public static final String ROLEID_KEY = "RoleId";
	// 流程基础数据的key,对应value：JSONObject(键值对)
	public static final String FLOW_BASE_DATA_KEY = "baseData";
	public static final String BILL_FLOW_MONTH_KEY = "账单月份";
	public static final String BILL_FLOW_BUILD_PAYMENT_KEY = "自动发起账单付款";
	public static final String BILL_FLOW_BUILD_REMUNERATION_KEY = "自动发起酬金收款";
	public static final String DAHAN_SUCCESS_COUNT_KEY = "平台成功数";
	public static final String DAHAN_PRICE_KEY = "单价(元)";
	public static final String DAHAN_PROVINCE_SUCCESS_COUNT_KEY = "省网成功数";
	public static final String DAHAN_PROVINCE_PRICE_KEY = "省网单价(元)";
	public static final String DAHAN_PAYMENT_AMOUNT_KEY = "平台账单金额";
	public static final String DAHAN_BILL_NUM_KEY = "账单编号";
	public static final String DAHAN_REMARK_KEY = "备注";
	public static final String DAHAN_ENCLOSURE_KEY = "附件";
	public static final String DAHAN_BILL_FILE_KEY = "DAHAN_BILL_FILE_KEY";
	public static final String DAHAN_QUOTATION_KEY = "报价单";
	public static final String BILL_FLOW_BUILD_RECEIVABLES_KEY = "自动发起账单收款";
	public static final String DAHAN_BILL_LAST_COPY_FILE_KEY = "lastCopyFilePath";

	public static final String OPERARE_COST_KEY_PREFIX = "unified_operate_single_cost_";

	// 请求生成数据分析报告的地址
	public static final String ACQUIRE_REPORT_DETAIL_URL = "acquire_report_detail_url";
	// 数据分析报告生成后的回调地址，taskId为对账流程id
	public static final String REPORT_DETAIL_CALL_BACK_URL = "report_detail_call_back_url";
	// 数据分析报告生成后的回调地址，taskId为客户id+时间，用于立即生成报告
	public static final String REPORT_DETAIL_CALL_BACK_URL_TEMP = "report_detail_call_back_url_temp";
	// 请求服务器时用于校验的token
	public static final String ACQUIRE_REPORT_DETAIL_TOKEN = "acquire_report_detail_token";

	// 客户账单相关
	public static final String DAHAN_CUSTOMER_SCCUESS_COUNT = "客户成功数";
	public static final String DAHAN_REAL_BILL_MONEY = "实际账单金额";
	public static final String DAHAN_CUSTOMER_BILL_MONEY = "客户账单金额";

	public static final String PRICE_TYPE_KEY = "价格类型";
	public static final String PRICE_ADJUSTMENT_KEY = "调价梯度";
	public static final String PRICE_START_DATE_KEY = "价格起始日期";
	public static final String PRICE_END_DATE_KEY = "价格截止日期";
	public static final String PRICE_BEFORE_ADJUST_KEY = "原来价格";

	public static final String SUPPLIER_SUCCESS_MONEY_KEY = "供应商账单金额";
	public static final String SUPPLIER_SUCCESS_COUNT_KEY = "供应商成功数";
	public static final String PAYMENT_AMOUNT_KEY = "实际账单金额";
	public static final String REMUNERATION_KEY = "酬金";

	public static final String CUSTOMER_SUCCESS_COUNT_KEY = "客户成功数";
	public static final String RECEIVABLES_AMOUNT_KEY = "实际账单金额";

	public static final String RECEIVABLES_END_TIME_KEY = "收款截止日期";

	public static final String ACTUALLY_RECEIVABLES_KEY = "已收金额";

	public static final String BILL_PRICE_INFO_KEY = "BILL_PRICE_INFO_KEY";

	public static final String NO_BILL_INVOICE_AMOUNT_KEY = "无账单的开票金额";

	public static final String INVOICE_SERVICE_NAME_KEY = "开票服务名称";

	public static final String INVOICE_TYPE_KEY = "发票类型";

	public static final String USER_LEAVE_TYPE_KEY = "请假类别";
	public static final String USER_LEAVE_TIME_KEY_FROM = "请假开始时间";
	public static final String USER_LEAVE_TIME_KEY_TO = "请假结束时间";
	public static final String USER_LEAVE_DAYS = "天数";
	public static final String USER_OVER_TIME_KEY = "加班日期";
	public static final String WEDDING_LEAVE_DAYS_KEY_1 = "WEDDING_LEAVE_DAYS_1";
	public static final String WEDDING_LEAVE_DAYS_KEY_2 = "WEDDING_LEAVE_DAYS_2";

	//外勤
	public static final String USER_OUTSIDE_PLACE_KEY = "外勤地点";
	public static final String USER_OUTSIDE_TIME_KEY = "外勤时间";

	//出差
	public static final String USER_TRAVEL_PLACE_KEY = "出差地点";
	public static final String USER_TRAVEL_TIME_KEY = "出差时间";

	// 初次结婚婚假天数
	public static final double WEDDING_LEAVE_DAYS_1 = 10;
	// 非初次结婚婚假天数
	public static final double WEDDING_LEAVE_DAYS_2 = 3;

	public static String ANNUAL_LEAVE_RESET_KEY = "ANNUAL_LEAVE_RESET";

	public static final String PAYMENT_END_TIME_KEY = "付款截止日期";
	public static final String PAYMENT_MONEY_KEY = "充值金额";
	public static final String PAYMENT_TYPE_KEY = "充值类型";
	public static final String PAYMENT_ACCOUNT_KEY = "账号";
	public static final String PAYMENT_PRICE_KEY = "单价";
	public static final String PAYMENT_DETAIL_KEY = "充值详情";
	public static final String UNCHECKED_BILL_KEY = "未对账账单";

	public static final String COMMON_FLOW_NAME = "普通流程";
	public static final String PAYMENT_FLOW_NAME = "充值流程";
	public static final String BILL_PAYMENT_FLOW_NAME = "账单付款流程";
	public static final String ADJUST_PRICE_FLOW_NAME = "调价流程";
	public static final String INTER_ADJUST_PRICE_FLOW_NAME = "国际调价流程";
	public static final String INVOICE_NAME = "发票流程";
	public static final String BILL_FLOW_NAME = "账单流程";
	public static final String INTER_BILL_FLOW_NAME = "国际账单流程";
	public static final String REMUNERATION_FLOW_NAME = "酬金流程";
	public static final String CUSTOMER_BILL_FLOW_NAME = "销售账单流程";
	public static final String CUSTOMER_INTER_BILL_FLOW_NAME = "销售国际账单流程";
	public static final String BILL_RECEIVABLES_FLOW_NAME = "销售收款流程";
	public static final String BILL_WRITE_OFF_FLOW_NAME = "销账流程";
	public static final String DS_ORDER_FLOW_NAME = "电商购销单流程";
	public static String DS_PURCHASE_FLOW_NAME = "供应商采购单流程";
	public static final String DS_SUPPLIER_FLOW_NAME = "电商供应商审核流程";
	public static final String CONTRACT_FLOW_NAME = "合同流程";
	public static final String SALE_CONTRACT_FLOW_NAME = "销售合同评审流程";
	public static final String PAYMENT_PERIOD_FLOW_NAME = "账单周期流程";
	public static final String ACCOUNT_FLOW_NAME = "开户流程";
	public static final String APPLY_CUSTOMER_FLOW_NAME = "申请客户流程";
	public static final String CHECK_BILL_FLOW_NAME = "对账流程";
	public static final String CUSTOMER_CHECK_BILL_FLOW = "销售对账流程";
	public static final String USER_LEAVE_FLOW_NAME = "请假流程";
	public static final String USER_OVERTIME_FLOW_NAME = "加班流程";
	public static final String User_OUTSIDE_FLOW_NAME = "外勤流程";
	public static final String USER_BUSINESS_TRAVEL_FLOW_NAME = "出差流程";

	public static final String APPLY_CUSTOMER_FLOW_CLASS = "[ApplyCustomer]";
	public static final String COMMON_FLOW_CLASS = "[CommonFlow]";
	public static final String PAYMENT_FLOW_CLASS = "[PaymentFlow]";
	public static final String BILL_PAYMENT_FLOW_CLASS = "[BillPaymentFlow]";
	public static final String REMUNERATION_FLOW_CLASS = "[RemunerationFlow]";
	public static final String ADJUST_PRICE_FLOW_CLASS = "[AdjustPriceFlow]";
	public static final String INTER_ADJUST_PRICE_FLOW_CLASS = "[InterAdjustPriceFlow]";
	public static final String INVOICE_CLASS = "[InvoiceFlow]";
	public static final String BILL_FLOW_CLASS = "[BillFlowService]";
	public static final String CUSTOMER_BILL_FLOW_CLASS = "[CustomerBillFlowService]";
	public static final String BILL_RECEIVABLES_FLOW_CLASS = "[BillReceivablesFlow]";
	public static final String BILL_WRITE_OFF_FLOW_CLASS = "[BillWriteOffFlow]";
	public static final String DS_ORDER_FLOW_CLASS = "[DsOrderFlow]";
	public static final String DS_PURCHASE_FLOW_CLASS = "[DsPurchaseFlow]";
	public static final String DS_SUPPLIER_FLOW_CLASS = "[DsSupplierFlow]";
	public static final String CONTRACT_FLOW_CLASS = "[ContractFlow]";
	public static final String PAYMENT_PERIOD_FLOW_CLASS = "[PaymentPeriodFlow]";
	public static final String ACCOUNT_FLOW_CLASS = "[AccountFlow]";
	public static final String CHECK_BILL_FLOW_CLASS = "[CheckBillFlow]";
	public static final String USER_LEAVE_FLOW_CLASS = "[UserLeaveFlow]";
	public static final String USER_OVERTIME_FLOW_CLASS = "[UserOvertimeFlow]";
	public static final String USER_OUTSIDE_FLOW_CLASS = "[UserOutsideFlow]";
	public static final String USER_BUSINESS_TRAVEL_FLOW_CLASS = "[UserTravelFlow]";

	public static final String NEW_PAYMENT_PERIOD_KEY = "新账单周期";
	public static final String OLD_PAYMENT_PERIOD_KEY = "原账单周期";
	public static final String CHANGE_RANGE_KEY = "修改范围";
	public static final String CHANGE_RANGE_VALUE_ALL = "所有产品";
	public static final String CHANGE_RANGE_VALUE_SINGLE = "单个产品";

	public static final String BILL_INFO_KEY = "账单信息";

	public static final String CUSTOMER_INVOICE_INFO = "客户开票信息";

	public static final String OTHER_INVOICE_INFO = "对方开票信息";

	public static final String INVOICE_INFO_KEY = "发票信息";

	public static final String CONTRACT_NUMBER = "合同编号";

	public static final String CUSTOMER_TYPE_CONTRACT = "合同客户";

	public static final String CONTRACT_NAME = "合同名称";

	public static final String CONTRACT_REGION = "合同归属";

	public static final String CONTRACT_TYPE = "合同类型";

	public static final String MONTH_COUNT = "月发送量";

	public static final String CONTRACT_AMOUNT = "合同金额";

	public static final String PAY_TYPE = "付费方式";

	public static final String CONTRACT_FILE = "合同附件";

	public static final String CONTRACT_FILES_SCAN = "合同扫描件";

	public static final String UNIT_PRICE = "单价";

	public static final String PROJECT_DESCRIPTION = "项目情况说明";

	public static final String VALIDITY_DATE_START = "开始有效期";

	public static final String VALIDITY_DATE_END = "结束有效期";

	public static final String APPLY_DATE = "申请日期";

	public static final String PLATFORM_ACCOUNT_INFO = "账号信息";

	public static final String DS_ORDER_NUMBER = "订单编号";

	public static final String DS_SALES_MONTY = "采购金额";

	public static final String DS_SEND_TYPE = "发货形式";

	public static final String DS_DUE_TIME = "交付日期";

	public static final String DS_INVOICE_TYPE = "发票种类";

	public static final String DS_INVOICE_RANT = "发票税点";

	public static final String DS_SEND_ADDRESS = "配送地址";

	public static final String DS_SEND_ADDRESS_FILE = "配送地址附件";

	public static final String DS_PURCHASE_ORDER_FILE = "采购单附件";

	public static final String DS_VALID_TIME = "配货单有效截止日期";

	public static final String DS_MATCH_PEOPLE = "配单员";

	// 供应商流程使用--开始
	public static final String COMPANY_NAME = "公司名称";

	public static final String LEGAL_PERSON = "法人";

	public static final String REGISTRATION_NUMBER = "统一社会信用代码";

	public static final String POSTAL_ADDRESS = "公司地址";

	public static final String TELEPHONE_NUMBER = "公司电话";

	public static final String EMAIL = "电子邮件";

	public static final String WEBSITE = "公司网页";

	public static final String CONTACT_NAME = "业务联系人";

	public static final String CONTACT_PHONE = "联系手机";

	public static final String CREATION_DATE = "公司创立日期";

	public static final String REGISTERED_CAPITAL = "注册资本(万元)";

	public static final String CORPORATE_NATURE = "公司性质";

	public static final String SUPPLIER_TYPE_ID = "供应商类别ID";

	public static final String COMPANY_QUALIFICATION = "公司资质";

	public static final String LEGAL_RISK = "法律风险";

	public static final String DELIVERY_CYCLE = "正常交货周期";

	public static final String COOPERATION_TYPE = "合作形式";

	public static final String SETTLEMENT_TYPE = "结算方式";

	public static final String SALE_TYPE = "销售方式";

	public static final String CERTIFICATION = "相关技术或资质认证";

	public static final String CONTRACT_FILES = "认证文件";

	public static final String CORPORATE_CREDIT = "法人征信";

	public static final String PRODUCT_RANGE = "产品范围";

	public static final String ADVANTAGE_PRODUCT = "优势产品";

	public static final String LOGISTICS = "配送物流";

	public static final String CASE_CONTRACT = "合作客户案例合同";

	public static final String COMPANY_INTRODUCTION = "行业水平及外部评价";

	public static final String ANNUAL_INCOME = "近两年任一年度主营收入";

	public static final String IS_INCOME_PROVE = "是否提供有效营收证明";

	public static final String FINANCIAL_FILE = "纳税证明或完整的审计报告或上市公司财报等";

	public static final String MANAGE_CERTIFICATION_FILE = "公司管理相关认证";

	public static final String DS_BANK_INFO = "银行信息";
	// 供应商流程使用--结束

	// 角色名 - 电商配单员
	public static final String ROLE_NAME_MATCH_ORDER = "电商配单员";

	public static final String DS_PURCHASE_COST = "采购成本总额";

	public static final String DS_LOGISTICS_COSTS = "采购物流费";

	public static final String DS_TOTAL = "合计";

	public static final String DS_DESIGN_FEE = "包装设计费";

	public static final String DS_PAY_TYPE = "付款形式";

	public static final String DS_PAY_PERIOD = "付款周期";

	public static final String DS_BUY_ORDER_NUMBER = "采购单编号";

	public static final String DS_BUY_CONTRACT_NO = "框架合同编号";

	public static final String DS_BUY_CONTACT_PERSON = "联系人";

	public static final String DS_BUY_CONTACT_NO = "联系电话";

	public static final String DS_REMARK = "备注";

	public static final String DS_PRODUCT_PROMISE = "产品约定";

	public static final String DS_PACKAGE_PROMISE = "包装约定";

	public static final String DS_LOGISTICS_PROMISE = "物流约定";

	public static final String DS_BUY_TIME = "采购日期";

	public static final String DS_MATCH_ORDER = "配单信息";
	
	public static final String DS_ORDER = "提单信息";

	public static String DS_BUY_ORDER_PATH = "d:/resource";

	public static final String DS_PRODUCT_NAME_KEY = "productname";

	public static final String DS_PRODUCT_ID_KEY = "dsproductid";

	public static final String DS_SUPPLIER_NAME_KEY = "suppliername";

	public static final String DS_SUPPLIER_ID_KEY = "supplierid";

	// 数量
	public static final String DS_AMOUNT_KEY = "amount";
	
	// 库存数量
	public static final String DS_DEPOT_NUM_KEY = "depotNumber";
	
	// 仓库类型
	public static final String DS_DEPOT_TYPE_KEY = "depotType";
		
	// 库存批次id
	public static final String DS_DEPOT_HEAD_ID = "depotHeadId";
	
	// 库存详情id
	public static final String DS_DEPOT_ITEM_ID = "depotItemId";

	// 规格型号
	public static final String DS_FORMAT_KEY = "format";

	// 单价（集采取产品的集采价，一件代发取产品的一件代发价）
	public static final String DS_PRICE_KEY = "price";

	// 单个产品的总额，单价*数量
	public static final String DS_TOTAL_KEY = "total";

	// 物流费
	public static final String DS_LOGISTICS_COST_KEY = "logisticsCost";

	public static final String DS_REMARK_KEY = "remark";

	public static final String FLOW_FILE_PATH_KEY = "filePath";

	public static final String BILL_THIS_RECEIVABLES_KEY = "thisReceivables";

	public static final String BILL_RECEIVABLES_KEY = "receivables";

	public static final String BILL_THIS_PAYMENT_KEY = "thisPayment";

	public static final String BILL_PAYABLES_KEY = "payables";

	public static final String ID_KEY = "id";

	public static final String BILL_TITLE_KEY = "title";

	// 开票信息阈值判断依据，默认是公司名称
	public static String INVOICE_INFO_THRESHOLD_KEY = "INVOICE_INFO_THRESHOLD_KEY";

	/* 客户开票信息 */
	public static final String CUST_INVOICE_INFO = "custInvoiceInfo";

	public static String RESOURCE;

	// 统计延迟天数，覆盖统计
	public static int STATISTICS_DELAY_DAYS = 2;

	public static final String CHINA_COUNTRY_CODE = "+86";

	public static final String CUST_PRODUCT_BILL_NUM_KEY = "-1-";

	// 工作时间
	public static String WORK_TIME_KEY = "WORK_TIME";

	// 默认工作时间
	public static String DEFAULT_WORK_TIME = "8:30-11:30,13:00-18:00";

	// 迟到分钟数key
	public static String TIME_DELAY_KEY_1 = "TIME_DELAY_1";

	// 默认迟到分钟数，迟到超过5分钟算迟到，5分钟以内算正常打卡
	public static int DEFAULT_LATE_MINUTES = 5;

	// 旷工分钟数key
	public static String TIME_DELAY_KEY_2 = "TIME_DELAY_2";

	// 默认旷工分钟数，迟到超过60分钟算旷工
	public static int DEFAULT_ABSENTEEISM_MINUTES = 60;

	// 打卡记录延迟天数，覆盖同步
	public static int CHECKIN_DELAY_DAYS = 7;

	/** 账单常量信息 */
	public static String EXCEL_BILL_TITLE = "大汉三通通信云对账单";
	public static String EXCEL_BILL_COMPANY_NAME = "上海大汉三通数据通信有限公司";
	public static String EXCEL_BILL_BANK_NAME = "交通银行上海张江支行";
	public static String EXCEL_BILL_BANK_ACCOUNT = "3100 6686 5018 8000 06013";
	public static String EXCEL_BILL_COMPANY_ADDRESS = "上海浦东新区张江高科郭守敬路498号浦东软件园20号楼5层";

	// 对账单内容--电子账单
	public static String BILL_OPTION_BILL_FILE = "billFile";

	// 对账单内容--数据详情
	public static String BILL_OPTION_DATA_DETAIL = "dataDetail";

	// 产品类型标识——短信
	public static String PRODUCT_TYPE_KEY_SMS = "SMS";

	// 产品类型标识——彩信
	public static String PRODUCT_TYPE_KEY_MMS = "MMS";

	// 产品类型标识——超级短信
	public static String PRODUCT_TYPE_KEY_SUPER_MMS = "SUPER_MMS";

	// 产品类型标识——国际短信
	public static String PRODUCT_TYPE_KEY_INTER_SMS = "INTER_SMS";

	// 产品类型标识——语音(按时计费)
	public static String PRODUCT_TYPE_KEY_VOICE_TIME = "VOICE_TIME";

	// 产品类型标识——移动认证
	public static String PRODUCT_TYPE_KEY_MOBILE_AUTH = "MOBILE_AUTH";

	/**
	 * 移动认证通道标识
	 */
	// 中国移动
	public static final String CMCC = "ydrz-1";
	// 中国联通
	public static final String CUCC = "ydrz-2";
	// 中国电信
	public static final String CTCC = "ydrz-3";

	/** 等于 */
	public static final String ROP_EQ = "eq";
	/** 不等于 */
	public static final String ROP_NE = "ne";
	/** 小于 */
	public static final String ROP_LT = "lt";
	/** 大于 */
	public static final String ROP_GT = "gt";
	/** 小于等于 */
	public static final String ROP_LE = "le";
	/** 大于等于 */
	public static final String ROP_GE = "ge";
	/** 包含 */
	public static final String ROP_CN = "cn";
	/** 模糊查询 */
	public static final String ROP_LIKE = "like";
	/** 左包含 */
	public static final String ROP_LCN = "lcn";
	/** 右包含 */
	public static final String ROP_RCN = "rcn";
	/** 非左包含 */
	public static final String ROP_NLCN = "nlcn";
	/** in */
	public static final String ROP_IN = "in";
	/** 降序 */
	public static final String ROP_DESC = "desc";
	/** 升序 */
	public static final String ROP_ASC = "asc";

	public static final String[] WEEK_NAME = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };

	/** 生成账单流程标题 */
	public static String buildBillFlowTilte(String msgType, String companyName, String productName, String dateMonth) {
		return Constants.BILL_FLOW_NAME + "(" + companyName + "-" + productName + "-" + msgType + ")-" + dateMonth;
	}

	/** 生成账单付款流程标题 */
	public static String buildBillPaymentFlowTitle(String companyName, String productName, String dateMonth) {
		return Constants.BILL_PAYMENT_FLOW_NAME + "(" + companyName + "-" + productName + ")-" + dateMonth;
	}

	/** 生成酬金流程标题 */
	public static String buildRemunerationFlowTitle(String companyName, String productName, String dateMonth) {
		return Constants.REMUNERATION_FLOW_NAME + "(" + companyName + "-" + productName + ")-" + dateMonth;
	}

	/** 生成产品账单记录的标题 */
	public static String buildProductBillTitle(String companyName, String productName, String dateMonth) {
		return "账单-" + dateMonth + "-" + companyName + "-" + productName;
	}

	/** 生成账单收款流程标题 */
	public static String buildBillReceivablesFlowTitle(String companyName, String productName, String dateMonth) {
		return Constants.BILL_RECEIVABLES_FLOW_NAME + "(" + companyName + "-" + productName + ")-" + dateMonth;
	}
	
	/** 系统参数 */
	public static final String BILL_SEND_COUNT_THRESHOLD = "billSendCountThreshold";

	/** 开票流程 */
	public static final String PARAM_BLANK = "isblank";
	public static final String ERROR_CLASS = "errorClass";
	public static final String HAS_BILL = "hasbill";
	public static final String NO_BILL = "nobill";
}
