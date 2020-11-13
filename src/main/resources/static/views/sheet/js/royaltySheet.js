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

// 销售统计表 展示时间类型 （默认是按天 4-天，0-周，1-月，2-季，3-年）
var sale_royalty_show_date_type = 4;

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
        loadSaleRoyalty(sale_statistic_year);
    });
}

// 初始化现金流表格
function initRoyaltyTable(id, data) {
    var fontSize = 'font-size: 12px;';
    //第一个实例
    var height = '';
    if (sale_royalty_show_date_type === 4) {
        height = 'full-40';
    }
    table.render({
        elem: id,
        totalRow: true,
        toolbar: "#timeScale",
        defaultToolbar: [],
        height: height,
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
                field: 'currentbalance',
                title: '客户总余额',
                align: 'right',
                templet: function (res) {
                    return res.currentbalance ? thousand(parseFloat(res.currentbalance).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }, {
                field: 'profit',
                title: '利润',
                align: 'right',
                templet: function (res) {
                    return res.profit ? thousand(parseFloat(res.profit).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }, {
                field: 'royalty',
                title: '应得提成',
                align: 'right',
                templet: function (res) {
                    return res.royalty ? thousand(parseFloat(res.royalty).toFixed(2)) : '0.00';
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

    // 监听工具栏按钮
    table.on("toolbar(" + id.replace("#", "") + ")", function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'showByDay':
                sale_royalty_show_date_type = 4;
                break;
            case 'showByWeek':
                sale_royalty_show_date_type = 0;
                break;
            case 'showByMonth':
                sale_royalty_show_date_type = 1;
                break;
            case 'showBySeason':
                sale_royalty_show_date_type = 2;
                break;
            case 'showByYear':
                sale_royalty_show_date_type = 3;
                break;
        }
        loadSaleRoyalty(sale_statistic_year);
    });
}

// 加载销售提成数据
function loadSaleRoyalty(year) {
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

    // 查询销售的提成数据
    $.ajaxSettings.async = true;
    $.post("/royalty/getRoyalty", {
        deptIds: isNotBlank(search_dept_ids) ? search_dept_ids : '',
        customerTypeId: isNotBlank(params.sale_open_customer_type_id) ? params.sale_open_customer_type_id : '',
        customerId: isNotBlank(params.sale_customer_id) ? params.sale_customer_id : '',
        productId: isNotBlank(params.sale_product_id) ? params.sale_product_id : '',
        dateType: sale_royalty_show_date_type,
        customerKeyWord: isNotBlank(params.customerKeyWord) ? params.customerKeyWord : ''
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充提成数据
                initRoyaltyTable('#royalty-' + year, data.data);
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