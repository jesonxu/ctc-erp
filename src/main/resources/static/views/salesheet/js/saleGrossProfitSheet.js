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
var laydate;
function init() {
    layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        sale_statistic_year = $("#year").val();
        laydate = layui.laydate;
        // 查询年份 // 默认查去年
        var now = new Date();
        var month = (now.getMonth() + 1) + "";
        if (month.length === 1) {
            month = "0" + month;
        }
        var yearMonth = now.getFullYear() + "-" + month;
        loadSaleRoyalty(sale_statistic_year, yearMonth);
    });
}

// 初始化现金流表格
function initRoyaltyTable(id, data, yearMonth) {
	var year = yearMonth.substring(0, yearMonth.indexOf("-"));
    var month = yearMonth.substring(yearMonth.indexOf("-") + 1);
    var time = year + "年" + month + "月";
    var fontSize = 'font-size: 12px;';
    //第一个实例
    var height = '';
    if (sale_royalty_show_date_type === 4) {
        height = 'full-40';
    }
    table.render({
        elem: id,
        totalRow: true,
        toolbar: '#toolbarDemo',
        defaultToolbar:["print", "exports"],
        height: height,
        limit: Number.MAX_VALUE,
        cols: [
            [{
                field: 'day',
                title: '日期',
                align: 'center',
                width: 200,
                style: fontSize,
                unresize: false,
                totalRow: false,
                totalRowText: '合计'
            }, {
                field: 'week',
                title: '星期',
                align: 'center',
                style: fontSize,
                templet: function (res) {
                	var weekArr = ['一', '二', '三', '四', '五', '六', '日'];
                	res.week = weekArr[res.week - 1 < 0 ? 6 : res.week - 1];
                	return res.week;
                },
                totalRow: false,
                totalRowText: '-'
            }, {
                field: 'productName',
                title: '产品名称',
                align: 'center',
                style: fontSize,
                totalRowText: '-'
            }, {
                field: 'productType',
                title: '产品类型',
                align: 'center',
                style: fontSize,
                totalRow: false,
                totalRowText: '-'
            }, {
                field: 'loginName',
                title: '账号',
                align: 'left',
                style: fontSize,
                totalRow: false,
                totalRowText: '-'
            }, {
                field: 'sendCount',
                title: '发送量',
                align: 'right',
                templet: function (res) {
                    return res.sendCount ? thousand(res.sendCount) : '0';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }, {
                field: 'salesVolume',
                title: '销售额',
                align: 'right',
                templet: function (res) {
                    return res.salesVolume ? thousand(parseFloat(res.salesVolume).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }, {
                field: 'customerPrice',
                title: '销售价',
                align: 'right',
                width: 120,
                templet: function (res) {
                    return res.customerPrice ? thousand(parseFloat(res.customerPrice).toFixed(6)) : '0.000000';
                },
                style: fontSize,
            }, {
                field: 'unitPrice',
                title: '成本价',
                align: 'right',
                templet: function (res) {
                    return res.unitPrice ? thousand(parseFloat(res.unitPrice).toFixed(6)) : '0.000000';
                },
                style: fontSize
            }, {
                field: 'grossProfit',
                title: '毛利润',
                align: 'right',
                templet: function (res) {
                    return res.grossProfit ? thousand(parseFloat(res.grossProfit).toFixed(2)) : '0.00';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 2, thousand: true}
            }]
        ],
        data: data,
        done: function () {
        	$('#yearSelect').html(time);
            initChangeYear(id, data, time);
            // 取消加载中遮罩
            layer.close(loadingIndex);
        }
    });

}

//切换年份按钮
function initChangeYear(id, data, yearMonth) {
    laydate.render({
        elem: '#yearSelect',
        type: 'month',
        trigger: 'click',
        max: yearMonth, // 最大值为今年
        format:"yyyy年MM月",
        done: function (value, date) {
            $('#yearSelect').html(value);
            var year = value.substring(0, value.indexOf("年"));
            var month = value.substring(value.indexOf("年") + 1, value.length - 1);
            var time = year + "-" + month;
            loadSaleRoyalty(sale_statistic_year, time);
        }
    })
}

// 加载销售提成数据
function loadSaleRoyalty(year, yearMonth) {
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
    $.post("/saleGrossProfit/getSaleGrossProdift", {
        deptIds: isNotBlank(search_dept_ids) ? search_dept_ids : '',
        customerTypeId: isNotBlank(params.sale_open_customer_type_id) ? params.sale_open_customer_type_id : '',
        customerId: isNotBlank(params.sale_customer_id) ? params.sale_customer_id : '',
        productId: isNotBlank(params.sale_product_id) ? params.sale_product_id : '',
        dateType: sale_royalty_show_date_type,
        customerKeyWord: isNotBlank(params.customerKeyWord) ? params.customerKeyWord : '',
        queryDate: yearMonth
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充提成数据
                initRoyaltyTable('#realRoyalty-' + year, data.data, yearMonth);
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