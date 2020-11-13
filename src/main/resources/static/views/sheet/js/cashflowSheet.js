var layer;
var element;
var form;
var table;
// 需要用到的参数
var params;
// 加载中遮罩
var loadingIndex;

$(document).ready(function () {
    var data = $('#params').val();
    setParams(JSON.parse(data));
    init();
});

function init() {
    layui.use(['layer', 'table', 'form', 'element'], function () {
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        var year = $("#year").val();
        var from = $("#from").val();
        if (from === 'customer') {
            loadCustomerCashFlow(year);
        } else if (from === 'supplier') {
            loadSupplierCashFlow(year);
        }
    });
}

// 初始化现金流表格
function initCashFlowTable(id, data) {
    layui.use('table', function () {
        var fontSize = 'font-size: 12px;';
        var table = layui.table;
        // 现金流表
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
                    style: fontSize,
                    fixed: true,
                    unresize: false,
                    totalRowText: '合计'
                }, {
                    field: 'receivables',
                    title: '应收金额',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.receivables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    field: 'actualReceivables',
                    title: '实收金额',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.actualReceivables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    field: 'payables',
                    title: '应付金额',
                    align: 'right',
                    templet: function (data) {
                        return thousand(data.payables);
                    },
                    style: fontSize,
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    field: 'actualPayables',
                    title: '实付金额',
                    align: 'right',
                    fixed: "right",
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

// 查询客户/产品的现金流数据
function loadCustomerCashFlow(year) {
    var loading = layer.load(2);
    var search_dept_ids ="";
    if (isNotBlank(params.sale_open_dept_id)) {
        var sub_dept_ids = params.sale_open_sub_dept_id.split(',');
        sub_dept_ids.push(params.sale_open_dept_id);
        if (isNotBlank(params.deptIds) && params.deptIds.length > 0) {
            var dept_ids = params.deptIds.split(",");
            var same_ids = [];
            for (var index = 0; index < dept_ids.length; index++) {
                var temp_ele = dept_ids[index];
                if (sub_dept_ids.indexOf(temp_ele) >= 0) {
                    same_ids.push(temp_ele);
                }
            }
            if (same_ids.length > 0) {
                search_dept_ids = same_ids.join(",");
            }
        } else {
            search_dept_ids = sub_dept_ids.join(",");
        }
    } else {
        search_dept_ids = params.deptIds;
    }
    $.ajaxSettings.async = true;
    $.post("/cashFlow/getCustomerCashFlow/", {
        deptIds: isNotBlank(search_dept_ids) ? search_dept_ids : '',
        customerTypeId: isNotBlank(params.sale_open_customer_type_id) ? params.sale_open_customer_type_id : '',
        customerId: isNotBlank(params.sale_customer_id) ? params.sale_customer_id : '',
        productId: isNotBlank(params.sale_product_id) ? params.sale_product_id : '',
        reqYear: year,
        customerKeyWord: isNotBlank(params.customerKeyWord) ? params.customerKeyWord : ''
    }, function (data, status) {
        if (status == "success") {
            if (typeof data.data != 'undefined' && typeof data.data[year] != 'undefined') {
                cash_flow_loaded_data = data.data[year];
                initCashFlowTable('#cashflow-' + year, data.data[year]);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
}

// 查询供应商/产品的现金流数据
function loadSupplierCashFlow(year) {
    var loading = layer.load(2);
	$.ajaxSettings.async = true;
    $.post("/cashFlow/getResourceCashFlow/", {
        deptIds: isNotBlank(params.deptIds) ? params.deptIds : '',
        supplierTypeId: isNotBlank(params.supplierTypeId) ? params.supplierTypeId : '',
        supplierId: isNotBlank(params.supplierId) ? params.supplierId : '',
        productId: isNotBlank(params.productId) ? params.productId : '',
        keyWord: isNotBlank(params.keyWord) ? params.keyWord : '',
        reqYear: year
    }, function (data, status) {
        if (status == "success") {
            if (typeof data.data != 'undefined' && typeof data.data[year] != 'undefined') {
                initCashFlowTable('#cashflow-' + year, data.data[year]);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
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

function setParams(data) {
    params = data;
}