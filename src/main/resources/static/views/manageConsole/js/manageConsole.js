var ppValue;
$(document).ready(function () {
    initButton();
});
// 表格分组 名称
const tableGroup = {
	'1': "管理报表",
	'2': "销售报表",
	'3': "财务报表",
	'4': "资源报表",
	'5': "配置入口",
    '6': "人事相关"
};

const tableGroupStyle = {
    '1': "#009688",
    '2': "#5FB878",
    '3': "#01AAED",
    '4': "#393D49",
    '5': "#FF5722",
    '6': "#1E9FFF"
}

var tablesMap = {
    businessGeneralTable: {
        type: 'button',
        text: '业绩概况',
        group: '1',
        icon: 'icon-one icon-sale-performance',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toBusinessReport.action?temp=" + Math.random());
        }
    },
    communicationRecentYearsTable: {
        type: 'button',
        text: '通信集团近年经营报表',
        group: '1',
        icon: 'layui-icon-group',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toYearForm.action?temp=" + Math.random());
        }
    },
    cloudcomSaleaChieveTable: {
        type: 'button',
        text: '云通讯销售业绩明细表',
        group: '1',
        icon: 'layui-icon-template',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toSaleBusinessDetail.action?temp=" + Math.random());
        }
    },
    regionPerformacnceTable: {
        type: 'button',
        text: '各大区域经营状况表',
        group: '1',
        icon: 'layui-icon-chart-screen',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toRegionReport.action?temp=" + Math.random());
        }
    },
    deptSaleIncomeDetailTable: {
        type: 'button',
        text: '销售收款汇总表',
        group: '1',
        icon: 'icon-one icon-money-package',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toSaleDetail.action?temp=" + Math.random());
        }
    },
    employeePerformamceTable: {
        type: 'button',
        text: '员工绩效分析表',
        group: '1',
        icon: 'layui-icon-username',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toSaleManaAchievementReport.action?temp=" + Math.random());
        }
    },
    cloudProductIncomeTable: {
        type: 'button',
        text: '云通讯产品收入结构表',
        group: '1',
        icon: 'layui-icon-component',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/manageConsole/toProductBusiness.action?temp=" + Math.random());
        }
    },
    saleManEquiltyGrossProfitTable: {
        type: 'button',
        text: '权益毛利表',
        group: '2',
        icon: 'icon-one icon-gross-margin',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/saleGrossProfit/toSaleGrossProdift2Manager.action?isManageConsole=T&temp=" + Math.random());
        }
    },
    saleManRealRoyaltyTable: {
        type: 'button',
        text: '销售提成表',
        group: '2',
        icon: 'icon-one icon-payment',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/realRoyalty/toRealRoyaltySheet2Manager.action?temp=" + Math.random());
        }
    },
    contractStatement: {
        type: 'button',
        text: '合同报表',
        group: '2',
        icon: 'layui-icon-form',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/contract/toContractSheet.action?temp=" + Math.random());
        }
    },
    customerOperateCostManage: {
        type: 'button',
        text: '客户运营成本管理',
        group: '5',
        icon: 'layui-icon-set',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/customerOperateCostManage/toCustomerOperateCostManage.action?temp=" + Math.random());
        }
    },
    balanceInterestTable: {
        type: 'button',
        text: '余额计息表',
        group: '2',
        icon: 'icon-one icon-interest',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/balanceInterest/toBalanceInterestManager.action?temp=" + Math.random());
        }
    },
    dsSaleDataTable: {
        type: 'button',
        text: '电商销售数据统计表',
        group: '1',
        icon: 'layui-icon-table',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/dsSaleData/toDsSaleDataPage.action?temp=" + Math.random());
        }
    },
    dsCustomerReceivablesTable: {
        type: 'button',
        text: '电商客户应收账款表',
        group: '3',
        icon: 'layui-icon-table',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/dsSaleData/toDsReturnMoneyPage.action?temp=" + Math.random());
        }
    },
    dsSignStatisticsTable: {
        type: 'button',
        text: '电商签单统计表',
        group: '1',
        icon: 'layui-icon-table',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/dsSaleData/toDsSignStatisticsPage.action?temp=" + Math.random());
        }
    },
    dsIntentionCustomerTable: {
        type: 'button',
        text: '意向客户统计表',
        group: '2',
        icon: 'layui-icon-table',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/dsSaleData/toDsIntentionCustomerPage.action?temp=" + Math.random());
        }
    },
    companyGoalTable: {
        type: 'button',
        text: '年度业绩目标表-公司',
        group: '1',
        icon: 'icon-one icon-target',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/goal/toGoalSheet.action?temp=" + Math.random());
        }
    },
    salesmanGoalTable: {
        type: 'button',
        text: '年度业绩目标表-销售',
        group: '2',
        icon: 'icon-one icon-target',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/goal/toGoalSheet.action?temp=" + Math.random());
        }
    },
    receivableAccountTable: {
        type: 'button',
        text: '应收账款表',
        group: '3',
        icon: 'layui-icon-read',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/receivableAccount/toReceivableAccount.action?temp=" + Math.random());
        }
    },
    noInterestAccountConfigTable: {
        type: 'button',
        text: '不计息账号配置',
        group: '5',
        icon: 'layui-icon-set',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/notInterestAccount/toAccountConfigTable.action?temp=" + Math.random());
        }
    },
    customerChangeTable: {
        type: 'button',
        text: '客户变更统计表',
        group: '2',
        icon: 'icon-one icon-up-down',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/customerChangeRecord/toCustomerChangeTable.action?temp=" + Math.random());
        }
    },
    incomeDetailTable: {
        type: 'button',
        text: '到款信息表',
        group: '3',
        icon: 'icon-one icon-money-package',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/fsExpenseIncome/toIncomeSheet.action?temp=" + Math.random());
        }
    },
    financialOperateReportTable: {
        type: 'button',
        text: '财务经营权责表',
        group: '3',
        icon: 'icon-one icon-financial-analysis',
        // bodyStyle: 'layui-btn-normal',
        handler: function () {
            window.open("/financialOperateReport/toFinancialOperateReportPage.action?temp=" + Math.random());
        }
    },
    productTypeConfig: {
        type: 'button',
        text: '产品类型配置',
        group: '5',
        icon: 'layui-icon-set',
        handler: function () {
            window.open("/productType/toProductTypeSheet.action?temp=" + Math.random());
        }
    },
    billOperate: {
        type: 'button',
        text: '账单操作',
        group: '5',
        icon: 'layui-icon-set',
        handler: function () {
            window.open("/bill/toBillsModify.action?temp=" + Math.random());
        }
    },
    invoiceManagement: {
        type: 'button',
        text: '发票管理',
        group: '2',
        icon: 'layui-icon-form',
        handler: function () {
            window.open("/invoice/toInvoiceSheet.action?temp=" + Math.random());
        }
    },
    chargeRecordCheckOut: {
        type: 'button',
        text: '充值核销',
        group: '3',
        icon: 'layui-icon-ok-circle',
        handler: function () {
            window.open("/chargeRecord/toChargeRecordSheet.action?temp=" + Math.random());
        }
    },
    userManagement: {
        type: 'button',
        text: '员工管理',
        group: '6',
        icon: 'layui-icon-user',
        handler: function () {
            window.open("/user/toUserManagement.action?temp=" + Math.random());
        }
    },
    userLeave: {
        type: 'button',
        text: '员工假期',
        group: '6',
        icon: 'layui-icon-tree',
        handler: function () {
            window.open("/userLeave/toUserLeave.action?temp=" + Math.random());
        }
    },
    checkinSheet: {
        type: 'button',
        text: '员工打卡记录',
        group: '6',
        icon: 'layui-icon-location',
        handler: function () {
            window.open("/checkin/toCheckinSheet.action?temp=" + Math.random());
        }
    },
    specialAttendanceRecord: {
        type: 'button',
        text: '特殊出勤报备',
        group: '6',
        icon: 'layui-icon-date',
        handler: function () {
            window.open("/specialAttendance/toSpecialAttendanceRecord.action?temp=" + Math.random());
        }
    },
    attendanceSheet: {
        type: 'button',
        text: '员工出勤记录',
        group: '6',
        icon: 'layui-icon-date',
        handler: function () {
            window.open("/attendance/toAttendanceSheet.action?temp=" + Math.random());
        }
    }
};

function initButton() {
	var buttons = [];
	var tablePermission = $('#tablePermission').val();
	if (tablePermission) {
		// 分组的报表
		var groupButtons = {};
		var arr = tablePermission.split(',');
		for (var i = arr.length - 1; i >= 0; i--) {
			var button = tablesMap[arr[i]];
			if (button) {
				var buttons = groupButtons[button.group];
				if (isBlank(buttons)) {
					buttons = [];
				}
				buttons.push(button);
				groupButtons[button.group] = buttons;
			}
		}

		for (let group in tableGroup) {
			var groupBtnArr = groupButtons[group];
			if (isNotBlank(groupBtnArr) && groupBtnArr.length > 0) {
				$("#manage_toolbar").append("<li data-group='" + group + "'><span class='table-group-title'>" + tableGroup[group] + "</span></li>");
				var groupEle = $("li[data-group='" + group + "']")[0];
				var toolbar = new Toolbar({
					renderTo: groupEle,
					items: groupBtnArr,
				});
				toolbar.render();
			}
		}
		for (let group in tableGroupStyle) {
		    var style = tableGroupStyle[group];
		    var groupDom = $('#manage_toolbar > li[data-group=' + group + ']');
		    if (groupDom.length > 0) {
		        $(groupDom).find('button').each(function (index, item) {
		            $(this).css('background-color', style);
                })
            }
        }
	}
}