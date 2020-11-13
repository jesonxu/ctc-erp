$(document).ready(function () {
	init();
});

var layer;
var table;
var laydate;
function init() {
	layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
		layer = layui.layer;
		table = layui.table;
		laydate = layui.laydate;
		// 查询年份 // 默认查去年
		var now = new Date();
		var month = (now.getMonth() + 1) + "";
		if (month.length === 1) {
			month = "0" + month;
		}
		var yearMonth = now.getFullYear() + "-" + month;
		var title = '员工绩效分析表';
		renderTable(title, yearMonth);
	});
}

//处理千分位
function thousand(num) {
    if (num === undefined || num === null) {
        return '-';
    }
    if (typeof num != 'number') {
    	return num;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}


function renderTable(title, yearMonth) {
	var fontSize = 'font-size: 12px;';
	$('#htmlTitle').html(title);
	//主页面数据
	table.render({
		elem: '#qua_standard_table',
		id: 'qua_standard_table',
        url: '/manageConsole/readSaleanalysis.action?year=' + yearMonth + '-01&temp=' + Math.random(),
		method: 'POST',
		limit: Number.MAX_VALUE,
		toolbar: '#toolbarDemo',
		printMergeField: ['deptName', 'saleUserName'],
		defaultToolbar: ['print'],
		printTitle: {
			name: title,
			colspan: 17,
		},
		cols: [
			[
				{
					align: 'center',
					style: fontSize,
					colspan: 17,
					title: title,
					hide: true
				}
			],
			[
				{
				    align: 'center',
				    style: fontSize,
				    field: 'deptName',
				    title: '部门',
				    width: 130,
				    fixed: true
				},
				{
				    align: 'center',
				    style: fontSize,
				    field: 'saleUserName',
				    title: '员工',
				    width: 110,
				    fixed: true
				},
				{
				    align: 'center',
				    style: fontSize,
				    field: 'customerName',
				    title: '客户',
				    width: 220,
				    fixed: true
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'receivables',
				    title: '到款金额',
				    templet: function (data) {
				        return thousand(data.receivables);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'successCount',
				    title: '发送条数',
				    templet: function (data) {
				        return thousand(data.successCount);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'saleUnitPrice',
				    title: '销售单价'
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'expenses',
				    title: '消费金额',
				    templet: function (data) {
				        return thousand(data.expenses);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'costUnitPrice',
				    title: '成本单价'
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'costSum',
				    title: '成本金额',
				    templet: function (data) {
				        return thousand(data.costSum);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'grossProfit',
				    title: '毛利额',
				    templet: function (data) {
				        return thousand(data.grossProfit);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'royalty',
				    title: '权益提成',
				    templet: function (data) {
				        return thousand(data.royalty);
				    }
				},
				{
				    align: 'center',
				    style: fontSize,
				    field: 'settleType',
				    title: '结算方式'
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'accounTperiod',
				    title: '账期'
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'arrears',
				    title: '累计欠款',
				    templet: function (data) {
				        return thousand(data.arrears);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'currentbalance',
				    title: '预付结余',
				    templet: function (data) {
				        return thousand(data.currentbalance);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'sellingExpenses',
				    title: '销售费用',
				    templet: function (data) {
				        return thousand(data.sellingExpenses);
				    }
				},
				{
				    align: 'right',
				    style: fontSize,
				    field: 'correctGrossprofit',
				    title: '修正后毛利',
				    templet: function (data) {
				        return thousand(data.correctGrossprofit);
				    }
				}]
			
			],
			done: function(res, curr, count) {
				var year = yearMonth.substring(0, yearMonth.indexOf("-"));
	            var month = yearMonth.substring(yearMonth.indexOf("-") + 1);
				layui.$('#qua_standard_table').siblings('div').find('dl').find('.layui-this').click(); //模拟点击 初始化数据
				merge(['deptName', 'saleUserName']); //合并单元格
				var time = year + "年" + month + "月";
	            $('#year').html(time);
	            initChangeYear(time);
	            $('#tableTitle').html(title);
			}
	});
}

function merge(colNames) {
	$(colNames).each(function (index, item) {
		var ele = null;
		$('.layui-table-fixed-l .layui-table tr td[data-field="' + item + '"]').each(function () {
			if (ele === null) {
				ele = $(this);
				return;
			}
			if (ele.find('div').text() == $(this).find('div').text()) {
				var rowspan = parseInt(ele.attr('rowspan') + '');
				if (!rowspan) {
					rowspan = 1;
				}
				ele.attr('rowspan', rowspan + 1);
				$(this).css('display', 'none');
			} else {
				ele = $(this);
				return;
			}
		});
	});
}

//切换年份按钮
function initChangeYear(yearMonth) {
    laydate.render({
        elem: '#year',
        type: 'month',
        trigger: 'click',
        max: yearMonth, // 最大值为今年
        format:"yyyy年MM月",
        done: function (value, date) {
            $('#year').html(value);
            var year = value.substring(0, value.indexOf("年"));
            var month = value.substring(value.indexOf("年") + 1, value.length - 1);
            var time = year + "-" + month;
            var title = '员工绩效分析表';
            renderTable(title, time);
        }
    })
}