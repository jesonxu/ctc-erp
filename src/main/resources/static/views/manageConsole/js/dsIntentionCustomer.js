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
    init();
});

var userId;
var deptId;

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
        loadDsIntentionCustomer(sale_statistic_year, yearMonth);
    });
}

//打开客户过滤tab页
function open_filter_tab() {
    var area = ['400px', '600px'];
    layer.open({
        type: 2,
        title: '客户过滤',
        area: area,
        btn: ['确定', '取消'],
        btnAlign: 'c',
        fixed: false, //不固定
        maxmin: true,
        content: '/customer/toCustomerFilter.action',
        yes: function (index, layero) {
            var body = layer.getChildFrame('body', index);
            var year = $('#yearSelect').text().replace('月', '').replace('年', '-');
            deptId = $(body).find("input[id='checkedDeptIds']").val();
            userId = $(body).find("input[id='checkedUserIds']").val();
            loadDsIntentionCustomer(year, year);
            layer.close(index);
        }
    });
    setTimeout(function () {
        $('[id*=layui-layer-iframe]').contents().find(".keyword-filter").css('display', 'none');
    }, 50);
}

// 初始化意向客户数据统计表格
function initDsIntentionCustomerTable(id, data, yearMonth) {
    var year = yearMonth.substring(0, yearMonth.indexOf("-"));
    var month = yearMonth.substring(yearMonth.indexOf("-") + 1);
    var time = year + "年" + month + "月";
    var fontSize = 'font-size: 12px;';
    //第一个实例
    var height = 'full-40';
    table.render({
        elem: id,
        totalRow: true,
        toolbar: '#toolbarDemo',
        defaultToolbar:["print", "exports"],
        height: height,
        limit: Number.MAX_VALUE,
        cols: [
            [{
                field: 'department',
                title: '部门',
                align: 'center',
                width: 200,
                style: fontSize,
                unresize: false,
                totalRow: false,
                totalRowText: '合计'
            }, {
                field: 'saleName',
                title: '销售',
                align: 'center',
                style: fontSize,
                totalRow: false,
                totalRowText: '-'
            }, {
                field: 'newCustomerCount',
                title: '新增客户',
                align: 'center',
                templet: function (res) {
                    return res.newCustomerCount ? thousand(res.newCustomerCount) : '0';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }, {
                field: 'newLogcount',
                title: '联系日志数',
                align: 'center',
                templet: function (res) {
                    return res.newLogcount ? thousand(res.newLogcount) : '0';
                },
                style: fontSize,
                totalRow: true,
                totalConfig: {decimal: 0, thousand: true}
            }]
        ],
        data: data,
        done: function (res, curr, count) {
            $('#yearSelect').html(time);
            initChangeYear(id, data, time);
            // 取消加载中遮罩
            layer.close(loadingIndex);
            merge(res);
        }
    });
}

function jumpSaleGrossProfitDetail(productId) {
    var year = $('#yearSelect').val().replace('月', '').replace('年', '-');
    window.open("/saleGrossProfit/toSaleGrossProdift.action?year=" + year + "&temp=" + Math.random() + "&sale_product_id=" + productId);
}

//合并开始
function merge(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var _number = 1; //保持序号列数字递增
    var columsName = ['department', 'saleName']; //需要合并的列名称
    var columsIndex = [0, 1]; //需要合并的列索引值
    var mergeCondition = 'id'; //需要合并的 首要条件  在这个前提下进行内容相同的合并
    var tdArrL = layui.$('.layui-table-fixed-l > .layui-table-body').find("tr"); //序号列左定位产生的table tr
    var tdArrR = layui.$('.layui-table-fixed-r > .layui-table-body').find("tr"); //操作列定右位产生的table tr

    for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
        var trArr = layui.$(".layui-table-main>.layui-table").find("tr"); //所有行
        for (var i = 1; i < res.data.length; i++) { //这里循环表格当前的数据

            if (data[i][mergeCondition] === data[i - 1][mergeCondition]) {
                var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]); //获取当前行的当前列
                var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]); //获取相同列的第一列

                if (data[i][columsName[k]] && data[i][columsName[k]] === data[i - 1][columsName[k]]
                    && data[i][columsName[0]] === data[i - 1][columsName[0]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function() { //相同列的第一列增加rowspan属性
                        layui.$(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function() { //当前行隐藏
                        layui.$(this).css("display", "none");
                    });
                } else {
                    mergeIndex = i;
                    mark = 1; //一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                }
            } else {
                mergeIndex = i;
                mark = 1; //一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
            }


        }
        mergeIndex = 0;
        mark = 1;
    }

    //操作左右定位列的表格
    layui.$.each(layui.$("#qua_standard_table").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function(i, v) {
        if (layui.$(v).find('td').eq(2).css('display') === 'none') {
            tdArrL.eq(i).find('td').css('display', 'none');
            tdArrR.eq(i).find('td').css('display', 'none');
        } else {
            tdArrL.eq(i).find('td').find('.laytable-cell-numbers').html(_number++);
            tdArrL.eq(i).find('td').css('height', layui.$(v).find('td').eq(2)[0].clientHeight);
            tdArrR.eq(i).find('td').css('height', layui.$(v).find('td').eq(2)[0].clientHeight);

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
            loadDsIntentionCustomer(sale_statistic_year, time);
        }
    })
}

// 加载意向客户数据统计
function loadDsIntentionCustomer(year, yearMonth) {
    var loading = layer.load(2);

    // 查询意向客户数据
    $.ajaxSettings.async = true;
    $.post("/dsSaleData/getDsIntentionCustomer.action", {
        deptId: deptId,
        userId: userId,
        queryDate: yearMonth
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充意向客户数据
                initDsIntentionCustomerTable('#realRoyalty', data.data, yearMonth);
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