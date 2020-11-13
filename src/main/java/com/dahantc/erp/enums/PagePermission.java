package com.dahantc.erp.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.dahantc.erp.enums.MenuGroup.ConsoleType;

public enum PagePermission {

	/* 客户工作台相关 */
	customerInfoOnly("customerInfoOnly", "客户-只允许查看基本信息", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerInfoEdit("customerInfoEdit", "客户-允许编辑客户", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerApplyFlow("customerApplyFlow", "客户-允许发起销售流程", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerStatistics("customerStatistics", "客户-统计表", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerCashFlow("customerCashFlow", "客户-现金流表", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerGrossProfit("customerGrossProfit", "客户-权益毛利表", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerProfitRoyalty("customerProfitRoyalty", "客户-利润提成表", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerMonthBills("customerMonthBills", "客户-月度账单", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerFilter("customerFilter", "客户-按部门过滤", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerProductEdit("customerProductEdit", "客户-允许编辑产品", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),
	
	customerProductEdit1stTime("customerProductEditFirstTime", "客户-修改第一次账单时间", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerTransfer("customerTransfer", "客户-转移客户", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	saleRoyalty("saleRoyalty", "销售-提成表", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	customerSearch("customerSearch", "客户-按关键词搜索", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	/* 资源工作台相关 */

	supplierInfoOnly("supplierInfoOnly", "供应商-只允许查看基本信息", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

	supplierInfoEdit("supplierInfoEdit", "供应商-允许编辑供应商", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

	supplierApplyFlow("supplierApplyFlow", "供应商-允许发起资源流程", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

	supplierStatistics("supplierStatistics", "供应商-统计表", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

	supplierCashFlow("supplierCashFlow", "供应商-现金流表", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

	supplierSearch("supplierSearch", "供应商-按关键词搜索", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

	supplierProductEdit("supplierProductEdit", "供应商-允许编辑产品", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

//	supplierIncomeManage("supplierIncomeManage", "供应商-收款管理", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

//	customerIncomeManage("customerIncomeManage", "客户-收款管理", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

//	supplierImportIncome("supplierImportIncome", "供应商-收入导入", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),

//	customerImportIncome("customerImportIncome", "客户-收入导入", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

//	supplierIncomeAssociate("supplierIncomeAssociate", "供应商-收入关联", MenuGroup.RESOURCES_SYSTEM.getCode(), ConsoleType.RESOURCES_CONSOLE),
	
//	customerAssociate("customerAssociate", "客户-到款客户关联", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

//	customerIncomeAssociate("customerIncomeAssociate", "客户-收入关联", MenuGroup.SALE_SYSTEM.getCode(), ConsoleType.CUSTOMER_CONSOLE),

	/* 电商供应商工作台相关 */

	dsSupplierInfoOnly("dsSupplierInfoOnly", "电商供应商-只允许查看基本信息", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierInfoEdit("dsSupplierInfoEdit", "电商供应商-允许编辑供应商", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierApplyFlow("dsSupplierApplyFlow", "电商供应商-允许发起资源流程", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierStatistics("dsSupplierStatistics", "电商供应商-统计表", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierCashFlow("dsSupplierCashFlow", "电商供应商-现金流表", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierSearch("dsSupplierSearch", "电商供应商-按关键词搜索", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierProductEdit("dsSupplierProductEdit", "电商供应商-允许编辑产品", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierIncomeManage("dsSupplierIncomeManage", "电商供应商-收款管理", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierImportIncome("dsSupplierImportIncome", "电商供应商-收入导入", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsDepotUpdate("dsDepotUpdate", "电商供应商-入库编辑", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsDepotVerify("dsDepotVerify", "电商供应商-入库审核", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsDepotDelete("dsDepotDelete", "电商供应商-入库删除", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsDepotAdd("dsDepotAdd", "电商供应商-添加入库", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsOutDepotUpdate("dsOutDepotUpdate", "电商供应商-出库编辑", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsOutDepotVerify("dsOutDepotVerify", "电商供应商-出库审核", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsOutDepotDelete("dsOutDepotDelete", "电商供应商-出库删除", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsOutDepotAdd("dsOutDepotAdd", "电商供应商-添加出库", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsProduct("dsProduct", "电商供应商-商品页面", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsDepot("dsDepot", "电商供应商-商品入库页面", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),
	
	dsOutDepot("dsOutDepot", "电商供应商-商品出库页面", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	dsSupplierIncomeAssociate("dsSupplierIncomeAssociate", "电商供应商-收入关联", MenuGroup.DIANSHANG_SYSTEM.getCode(), ConsoleType.DS_SUPPLIER_CONSOLE),

	/* 管理工作台相关 */

	businessGeneralTable("businessGeneralTable", "管理工作台-业绩概况", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	communicationRecentYearsTable("communicationRecentYearsTable", "管理工作台-通信集团近年经营报表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	cloudcomSaleaChieveTable("cloudcomSaleaChieveTable", "管理工作台-云通讯销售业绩明细表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	regionPerformacnceTable("regionPerformacnceTable", "管理工作台-各大区域经营状况表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	deptSaleIncomeDetailTable("deptSaleIncomeDetailTable", "管理工作台-各事业部/销售人员收款明细表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	employeePerformamceTable("employeePerformamceTable", "管理工作台-员工绩效分析表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	cloudProductIncomeTable("cloudProductIncomeTable", "管理工作台-云通讯产品收入结构表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	saleManEquiltyGrossProfitTable("saleManEquiltyGrossProfitTable", "管理工作台-权益毛利表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	saleManRealRoyaltyTable("saleManRealRoyaltyTable", "管理工作台-销售提成表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	contractStatement("contractStatement", "管理工作台-合同报表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	customerOperateCostManage("customerOperateCostManage", "管理工作台-客户运营成本管理", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	
	balanceInterestTable("balanceInterestTable", "管理工作台-余额计息表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	
	dsSaleDataTable("dsSaleDataTable", "管理工作台-电商销售数据统计表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	
	dsCustomerReceivablesTable("dsCustomerReceivablesTable", "管理工作台-电商客户应收账款统计表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	
	dsSignStatisticsTable("dsSignStatisticsTable", "管理工作台-电商签单统计表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	companyGoalTable("companyGoalTable", "管理工作台-年度业绩目标表-公司", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	salesmanGoalTable("salesmanGoalTable", "管理工作台-年度业绩目标表-销售", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	
	dsIntentionCustomerTable("dsIntentionCustomerTable", "管理工作台-电商意向客户表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	
	receivableAccountTable("receivableAccountTable", "管理工作台-应收账款表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	noInterestAccountConfigTable("noInterestAccountConfigTable", "管理工作台-不计息账号配置", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	customerChangeTable("customerChangeTable", "管理工作台-客户变更统计表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	incomeDetailTable("incomeDetailTable", "管理工作台-到款信息表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	customerIncomeImport("customerIncomeImport", "管理工作台-收入导入", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	customerIncomeAssociate("customerIncomeAssociate", "管理工作台-到款客户关联", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	financialOperateReportTable("financialOperateReportTable", "管理工作台-财务经营权责表", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	productTypeConfig("productTypeConfig", "管理工作台-产品类型配置", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	billOperate("billOperate", "管理工作台-账单操作", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	invoiceManagement("invoiceManagement", "管理工作台-发票管理", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	chargeRecordCheckOut("chargeRecordCheckOut", "管理工作台-充值核销", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	userManagement("userManagement", "管理工作台-员工管理", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	userInfoEdit("userInfoEdit", "管理工作台-编辑员工信息", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	userLeave("userLeave", "管理工作台-员工假期", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	checkinSheet("checkinSheet", "管理工作台-员工打卡记录", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	specialAttendanceRecord("specialAttendanceRecord", "管理工作台-特殊出勤报备", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),

	attendanceSheet("attendanceSheet", "管理工作台-员工出勤记录", MenuGroup.BACK_SYSTEM.getCode(), ConsoleType.MANAGER_CONSOLE),
	;

	private String desc;

	private String name;

	private int type;

	private ConsoleType consoleType;

	private static String[] descs;

	private static String[] names;

	private static Map<String, String> result;

	PagePermission() {

	}

	PagePermission(String desc, String name, int type, ConsoleType consoleType) {
		this.desc = desc;
		this.name = name;
		this.type = type;
		this.consoleType = consoleType;
	}

	public String getDesc() {
		return this.desc;
	}

	public String getName() {
		return this.name;
	}

	public int getType() {
		return type;
	}

	public ConsoleType getConsoleType() {
		return consoleType;
	}

	public static String[] getDescs() {
		PagePermission[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static String[] getNames() {
		PagePermission[] values = values();
		names = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			names[i] = values[i].getName();
		}
		values = null;
		return descs;
	}

	public static Map<String, String> getAll() {
		PagePermission[] values = values();
		result = new HashMap<>();
		for (int i = 0; i < values.length; i++) {
			result.put(values[i].desc, values[i].name);
		}
		values = null;
		return result;
	}

	public static Map<String, String> getAllByType(List<Integer> typeList) {
		PagePermission[] values = values();
		result = new HashMap<>();
		for (int i = 0; i < values.length; i++) {
			if (typeList.contains(values[i].type))
				result.put(values[i].desc, values[i].name);
		}
		values = null;
		return result;
	}

	public static Map<Integer, Map<String, String>> getAllByConsoleType(List<Integer> typeList) {
		PagePermission[] values = values();
		Map<Integer, Map<String, String>> result = new TreeMap<>();
		for (int i = 0; i < values.length; i++) {
			if (typeList.contains(values[i].consoleType.getOrdinal())) {
				if (result.get(values[i].consoleType.getOrdinal()) == null) {
					result.put(values[i].consoleType.getOrdinal(), new HashMap<>());
				}
				result.get(values[i].consoleType.getOrdinal()).put(values[i].desc, values[i].name);
			}
		}
		values = null;
		return result;
	}
}
