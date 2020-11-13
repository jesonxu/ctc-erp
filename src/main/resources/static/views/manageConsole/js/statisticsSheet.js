var layer;
var element;
var form;
var table;
// 需要用到的参数
var params;
// 加载中遮罩
var loadingIndex;

function init() {
    layui.use(['layer', 'form', 'element'], function () {
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        var supplier_statistic_year = $("#year").val();
        loadStatisticsTable(supplier_statistic_year);
    });
}

// 按年加载统计表数据
function loadStatisticsTable(year) {
    $.ajaxSettings.async = true;
    $.get("/statistics/read", {
        supplierTypeId: isNotBlank(params.supplierTypeId) ? params.supplierTypeId : '',
        supplierId: isNotBlank(params.supplierId) ? params.supplierId : '',
        productId: isNotBlank(params.productId) ? params.productId : '',
        reqYear: year,
        keyWord: isNotBlank(params.keyWord) ? params.keyWord : ''
    }, function (data, status) {
        if (status == "success") {
            if (!isNull(data.data) && !isNull(data.data[year])) {
                // 填充统计表数据
                initStatisticsTable('#statistics-' + year, data.data[year]);
            }
        }
    });
    $.ajaxSettings.async = false;
}

function initStatisticsTable(id, data) {
    layui.use('table', function () {
        var fontSize = 'font-size: 12px;padding:0px;'
        var table = layui.table;
        //第一个实例
        table.render({
            elem: id,
            totalRow: true,
            limit: Number.MAX_VALUE,
            cols: [
                [{
                    field: 'month',
                    title: '月份',
                    align: 'center',
                    width: 200,
                    fixed: 'left',
                    style: fontSize,
                    unresize: false,
                    totalRowText: '合计'
                }, {
                    field: 'platformCount',
                    title: '平台成功数',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.platformCount);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 0, thousand: true}
                }, {
                    field: 'supplierCount',
                    title: '供应商成功数',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.supplierCount);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 0, thousand: true}
                }, {
                    field: 'receivables',
                    title: '结算佣金',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.receivables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    field: 'actualReceivables',
                    title: '实到佣金',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.actualReceivables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    field: 'payables',
                    title: '结算金额',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.payables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    field: 'actualPayables',
                    title: '已付金额',
                    align: 'right',
                    fixed: 'right',
                    templet: function (data) {
                        return thousand(data.actualPayables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }]
            ],
            data: data,
            done: function () {
                // 取消加载中遮罩
                layer.close(loadingIndex);
            }
        });
    });
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

function setParams(data) {
    params = data;
}