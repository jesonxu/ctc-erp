var layer;
var element;
var form;
var table;
// 需要用到的参数
var params;
// 加载中遮罩
var loadingIndex;

// 统计年份，用于按时间类型重新加载
var sale_statistic_year;

// 销售统计表 按部门展示（默认1 0-按部门，1-不按部门 ）
var sale_statistics_show_department = 1;

// 销售统计表 展示时间类型 （默认是按周 0-周，1-月，2-季）
var sale_statistics_show_date_type = 0;

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
        sale_statistic_year = $("#year").val();
        load_statistics_table(sale_statistic_year);
    });
}

// 加载销售统计数据
function load_statistics_table(year) {
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

    // 查询产品/客户的统计数据
    $.ajaxSettings.async = true;
    $.post("/saleStatistics/getStatisticsDetail.action", {
        deptIds: isNotBlank(search_dept_ids) ? search_dept_ids : '',
        customerTypeId: isNotBlank(params.sale_open_customer_type_id) ? params.sale_open_customer_type_id : '',
        dateType: sale_statistics_show_date_type,
        customerId: isNotBlank(params.sale_customer_id) ? params.sale_customer_id : '',
        productId: isNotBlank(params.sale_product_id) ? params.sale_product_id : '',
        reqYear: year,
        deptName: sale_statistics_show_department,
        customerKeyWord: isNotBlank(params.customerKeyWord) ? params.customerKeyWord : '',
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充统计表数据
                initStatisticsTable('#statistics-' + year, data.data);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
}

// 初始化表格数据
function initStatisticsTable(id, data) {
    var fontSize = 'font-size: 12px;';
    var profitSum = 0;
    for (var i = 0; i < data.length; i++) {
        profitSum += (data[i].receivables ? data[i].receivables : 0) - (data[i].cost ? data[i].cost : 0);
    }

    // 是否展示部门
    var dept_name_show = (sale_statistics_show_department === 0);
    //第一个实例
    table.render({
        elem: id,
        totalRow: true,
        toolbar: "#toolbarDemo",
        defaultToolbar: [],
        limit: Number.MAX_VALUE,
        cols: [
            [{
                field: 'date',
                title: '时间',
                align: 'center',
                width: 200,
                style: fontSize,
                fixed: true,
                unresize: false,
                totalRowText: '合计'
            }, {
                field: 'deptName',
                title: '部门名称',
                align: 'center',
                width: 150,
                style: fontSize,
                unresize: false,
                hide: !dept_name_show,
                totalRow: false,
                totalRowText: '-'
            }, {
                field: 'customerCount',
                title: '客户成功数',
                align: 'right',
                templet: function (data) {
                    return thousand(data.customerCount);
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }, {
                field: 'platformCount',
                title: '平台成功总数',
                align: 'right',
                templet: function (data) {
                    return thousand(data.platformCount);
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }, {
                field: 'receivables',
                title: '结算金额',
                align: 'right',
                templet: function (data) {
                    return data.receivables ? thousand(parseFloat(data.receivables).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }, {
                field: 'actualReceivables',
                title: '到款金额',
                align: 'right',
                templet: function (data) {
                    return data.actualReceivables ? thousand(parseFloat(data.actualReceivables).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }, {
                field: 'cost',
                title: '实际成本',
                align: 'right',
                templet: function (data) {
                    return data.cost ? thousand(parseFloat(data.cost).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }, {
                field: 'newCusCount',
                title: '新增客户数',
                align: 'right',
                templet: function (data) {
                    return thousand(data.newCusCount);
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }, {
                field: 'newCusSendCount',
                title: '新增客户发送量',
                align: 'right',
                templet: function (data) {
                    return data.newCusSendCount ? thousand(parseInt(data.newCusSendCount)) : '0';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }, {
                field: 'profit',
                title: '毛利润',
                align: 'right',
                fixed: "right",
                templet: function (data) {
                    var profit = (data.receivables ? data.receivables : 0) - (data.cost ? data.cost : 0);
                    return thousand(profit.toFixed(2));
                },
                style: fontSize,
                totalConfig: {decimal: 2, thousand: true},
                totalRowText: thousand(profitSum.toFixed(2)) + ''
            }]
        ],
        data: data,
        done: function (res, curr, count) {
            if (dept_name_show) {
                // 合并单元格（按部门展示的时候 需要 合并时间）
                var table_span = new layui_table_span(id);
                // 根据内容 自动合并（列）
                table_span.rowspan("date");
            }
            // 取消加载中遮罩
            layer.close(loadingIndex);
        }
    });

    // 监听工具栏按钮
    table.on("toolbar(" + id.replace("#", "") + ")", function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'showByWeek':
                sale_statistics_show_date_type = 0;
                break;
            case 'showByMonth':
                sale_statistics_show_date_type = 1;
                break;
            case 'showBySeason':
                sale_statistics_show_date_type = 2;
                break;
            case 'showByYear':
                sale_statistics_show_date_type = 3;
                break;
        }
        load_statistics_table(sale_statistic_year);
    });

    // 监听是否为按部门展示
    form.on('checkbox(showByDepartment)', function (data) {
        sale_statistics_show_department = data.elem.checked ? 0 : 1;
        load_statistics_table(sale_statistic_year);
    });

    // 设置按部门展示状态
    form.val("dept-from", {
        "show_depart": (sale_statistics_show_department === 0)
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

function setParams(data) {
    params = data;
}