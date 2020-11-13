var layer;
var element;
var form;
var table;
// 加载中遮罩
var loadingIndex;

$(document).ready(function () {
	init();
});


function init() {
    layui.use(['layer', 'table', 'form', 'element'], function () {
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        load_statistics_table();
    });
}

// 加载业绩概况数据
function load_statistics_table() {
    var loading = layer.load(2);
    // 查询产品/客户的统计数据
    $.ajaxSettings.async = true;
    $.post("/manageConsole/readBusinessReport.action?temp=" + Math.random(), {
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充统计表数据
                initStatisticsTable('#statistics', data.data);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
}

// 初始化表格数据
function initStatisticsTable(id, data) {
    var fontSize = 'font-size: 12px;';

    //第一个实例
    table.render({
        elem: id,
        limit: Number.MAX_VALUE,
        defaultToolbar: ['print'],
        toolbar:'#toolbarDemo',
        printTitle: {
        	name: '业绩概况',
        	colspan: 7
        },
        cols: [
            [{
                field: 'year',
                title: '年份',
                align: 'center',
                width: 80,
                style: fontSize,
                fixed: true,
                unresize: false
            }, {
                field: 'incomeTotal',
                title: '总收入',
                align: 'right',
                templet: function (data) {
                    return thousand(data.incomeTotal);
                },
                style: fontSize
            }, {
                field: 'costTotal',
                title: '总成本',
                align: 'right',
                templet: function (data) {
                    return thousand(data.costTotal);
                },
                style: fontSize
            }, {
                field: 'grossTotal',
                title: '总毛利',
                align: 'right',
                templet: function (data) {
                    return thousand(data.grossTotal);
                },
                style: fontSize
            }, {
                field: 'grossProfit',
                title: '毛利率',
                align: 'right',
                style: fontSize
            }, {
                field: 'taxes',
                title: '缴纳税金',
                align: 'right',
                templet: function (data) {
                    return thousand(data.taxes);
                },
                style: fontSize
            }, {
                field: 'netProfit',
                title: '净利润',
                align: 'right',
                templet: function (data) {
                    return thousand(data.netProfit);
                },
                style: fontSize
            }]
        ],
        data: data,
        done: function (res, curr, count) {
            // 取消加载中遮罩
            layer.close(loadingIndex);
        }
    });
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}
