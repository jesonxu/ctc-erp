var layer;
var element;
var form;
var table;
var excel;
var laydate;
// 加载中遮罩
var loadingIndex;
// 查询条件年
var year; // 公司
var year1; // 销售
var yearLimit = 5;

$(document).ready(function () {
    layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
        layer = layui.layer;
        // loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        laydate = layui.laydate;
        // excel = layui.excel;
        initDate();
        initTable();
    });
});

// 初始化日期控件
function initDate() {
    var nowYear = new Date().getFullYear();
    // 公司
    year = isBlank(year) ? nowYear : year;
    var minYear = nowYear - yearLimit + "-01-01";
    var maxYear = nowYear + yearLimit + "-12-31";
    $('#year').text(year + '年');
    laydate.render({
        elem: '#year',
        type: 'year',
        trigger: 'click',
        value: year, // 默认今年
        max: maxYear, // 最大值为今年5年后
        min: minYear, // 最小值为今年5年前
        btns: ['now', 'confirm'],
        done: function (value, date) {
            year = value;
            $('#year').text(year + '年');
            loadCompanyGoalTable();
        }
    });
    // 销售
    year1 = isBlank(year1) ? nowYear : year1;
    $('#year1').text(year1 + '年');
    laydate.render({
        elem: '#year1',
        type: 'year',
        trigger: 'click',
        value: year1, // 默认今年
        max: maxYear, // 最大值为今年5年后
        min: minYear, // 最小值为今年5年前
        btns: ['now', 'confirm'],
        done: function (value, date) {
            year1 = value;
            $('#year1').text(year1 + '年');
            loadSalesGoalTable();
        }
    });
}

// 加载公司业绩目标数据
function loadCompanyGoalTable() {
    if (window.companyGoalTable) {
        var loading = layer.load(2);
        $.ajaxSettings.async = true;
        $.post("/goal/readCompanyGoalDetail.action?temp=" + Math.random(), {'year': year}, function (data, status) {
            if (status === "success") {
                if (!isNull(data.data)) {
                    renderCompanyGoalTable('#companyGoalTable', data.data);
                }
            }
            layer.close(loading);
        });
    }
}

// 加载销售业绩目标数据
function loadSalesGoalTable() {
    if (window.salesmanGoalTable) {
        var loading1 = layer.load(2);
        $.post("/goal/readSalesmanGoalDetail.action?temp=" + Math.random(), {'year': year1}, function (data, status) {
            if (status === "success") {
                if (!isNull(data.data)) {
                    renderSalesmanGoalTable('#salesmanGoalTable', data.data);
                }
            }
            layer.close(loading1);
        });
        $.ajaxSettings.async = false;
    }
}


// 填充公司目标表格数据
function renderCompanyGoalTable(id, data) {
    var fontSize = 'font-size: 12px;';
    var leftBorder = 'border-left: 1px solid;font-size: 12px;';
    var rightBorder = 'border-right: 1px solid;font-size: 12px;';

    //第一个实例
    table.render({
        elem: id,
        limit: Number.MAX_VALUE,
        height: '400px',
        toolbar: '#companyToolbar',
        defaultToolbar: false,
        cols: [
            [{
                title: '区域',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                field: 'regionName',
            }, {
                title: '事业部',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                width: 120,
                field: 'parentDeptName',
            }, {
                title: '年总目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2,
            }, {
                title: '1月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '2月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '3月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '4月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '5月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '6月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '7月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '8月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '9月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '10月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '11月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '12月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }],
            [ {
                field: 'sumReceivables',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['sumReceivables'])) {
                        var monthData = res['sumReceivables'];
                        return thousand(monthData);
                    }
                    return '';
                }
            }, {
                field: 'sumGrossProfit',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['sumGrossProfit'])) {
                        var monthData = res['sumGrossProfit'];
                        return thousand(monthData);
                    }
                    return '';
                }
            }, {
                field: 'receivables1',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables1)) {
                        return thousand(res.receivables1);
                    } else if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit1',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit1)) {
                        return thousand(res.grossProfit1);
                    } else if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables2',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables2)) {
                        return thousand(res.receivables2);
                    } else if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit2',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit2)) {
                        return thousand(res.grossProfit2);
                    } else if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables3',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables3)) {
                        return thousand(res.receivables3);
                    } else if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit3',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit3)) {
                        return thousand(res.grossProfit3);
                    } else if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables4',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables4)) {
                        return thousand(res.receivables4);
                    } else if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit4',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit4)) {
                        return thousand(res.grossProfit4);
                    } else if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables5',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables5)) {
                        return thousand(res.receivables5);
                    } else if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit5',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit5)) {
                        return thousand(res.grossProfit5);
                    } else if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables6',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables6)) {
                        return thousand(res.receivables6);
                    } else if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit6',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit6)) {
                        return thousand(res.grossProfit6);
                    } else if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables7',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables7)) {
                        return thousand(res.receivables7);
                    } else if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit7',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit7)) {
                        return thousand(res.grossProfit7);
                    } else if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables8',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables8)) {
                        return thousand(res.receivables8);
                    } else if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit8',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit8)) {
                        return thousand(res.grossProfit8);
                    } else if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables9',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables9)) {
                        return thousand(res.receivables9);
                    } else if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit9',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit9)) {
                        return thousand(res.grossProfit9);
                    } else if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables10',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables10)) {
                        return thousand(res.receivables10);
                    } else if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit10',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit10)) {
                        return thousand(res.grossProfit10);
                    } else if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables11',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables11)) {
                        return thousand(res.receivables11);
                    } else if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit11',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit11)) {
                        return thousand(res.grossProfit11);
                    } else if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables12',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables12)) {
                        return thousand(res.receivables12);
                    } else if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit12',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit12)) {
                        return thousand(res.grossProfit12);
                    } else if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }
            ]
        ],
        data: data,
        done: function (res, curr, count) {
            // 合并单元格
            mergeCompanyGoalDetail(res);
            element.render();
            initDate();
        }
    });
}

// 填充销售目标表格数据
function renderSalesmanGoalTable(id, data) {
    var fontSize = 'font-size: 12px;';
    var leftBorder = 'border-left: 1px solid;font-size: 12px;';
    var rightBorder = 'border-right: 1px solid;font-size: 12px;';

    //第一个实例
    table.render({
        elem: id,
        limit: Number.MAX_VALUE,
        height: 'full-40',
        toolbar: '#salesmanToolbar',
        defaultToolbar: false,
        cols: [
            [{
                title: '区域',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                field: 'regionName',
                fixed: 'left'
            }, {
                title: '事业部',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                width: 120,
                field: 'parentDeptName',
                fixed: 'left'
            }, {
                title: '部门',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                width: 120,
                field: 'deptName',
                fixed: 'left'
            }, {
                title: '销售',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                width: 80,
                field: 'realName',
                fixed: 'left'
            }, {
                title: '年总目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2,
            }, {
                title: '1月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '2月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '3月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '4月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '5月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '6月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '7月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '8月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '9月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '10月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '11月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }, {
                title: '12月目标(万)',
                align: 'center',
                style: fontSize,
                colspan: 2
            }],
            [ {
                field: 'sumReceivables',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['sumReceivables'])) {
                        var monthData = res['sumReceivables'];
                        return thousand(monthData);
                    }
                    return '';
                }
            }, {
                field: 'sumGrossProfit',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['sumGrossProfit'])) {
                        var monthData = res['sumGrossProfit'];
                        return thousand(monthData);
                    }
                    return '';
                }
            }, {
                field: 'receivables1',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables1)) {
                        return thousand(res.receivables1);
                    } else if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit1',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit1)) {
                        return thousand(res.grossProfit1);
                    } else if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables2',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables2)) {
                        return thousand(res.receivables2);
                    } else if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit2',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit2)) {
                        return thousand(res.grossProfit2);
                    } else if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables3',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables3)) {
                        return thousand(res.receivables3);
                    } else if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit3',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit3)) {
                        return thousand(res.grossProfit3);
                    } else if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables4',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables4)) {
                        return thousand(res.receivables4);
                    } else if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit4',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit4)) {
                        return thousand(res.grossProfit4);
                    } else if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables5',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables5)) {
                        return thousand(res.receivables5);
                    } else if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit5',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit5)) {
                        return thousand(res.grossProfit5);
                    } else if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables6',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables6)) {
                        return thousand(res.receivables6);
                    } else if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit6',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit6)) {
                        return thousand(res.grossProfit6);
                    } else if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables7',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables7)) {
                        return thousand(res.receivables7);
                    } else if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit7',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit7)) {
                        return thousand(res.grossProfit7);
                    } else if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables8',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables8)) {
                        return thousand(res.receivables8);
                    } else if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit8',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit8)) {
                        return thousand(res.grossProfit8);
                    } else if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables9',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables9)) {
                        return thousand(res.receivables9);
                    } else if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit9',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit9)) {
                        return thousand(res.grossProfit9);
                    } else if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables10',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables10)) {
                        return thousand(res.receivables10);
                    } else if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit10',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit10)) {
                        return thousand(res.grossProfit10);
                    } else if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables11',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables11)) {
                        return thousand(res.receivables11);
                    } else if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit11',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit11)) {
                        return thousand(res.grossProfit11);
                    } else if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }, {
                field: 'receivables12',
                title: '销售额',
                align: 'right',
                width: 90,
                style: leftBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.receivables12)) {
                        return thousand(res.receivables12);
                    } else if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[0]);
                    }
                    return '';
                }
            }, {
                field: 'grossProfit12',
                align: 'right',
                title: '毛利',
                width: 70,
                style: rightBorder,
                edit: 'text',
                templet: function (res) {
                    if (!isNull(res.grossProfit12)) {
                        return thousand(res.grossProfit12);
                    } else if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[1]);
                    }
                    return '';
                }
            }
            ]
        ],
        data: data,
        done: function (res, curr, count) {
            //合并单元格
            mergeSalesGoalDetail(res);
            element.render();
            initDate();
        }
    });
}

function initTable() {
    loadCompanyGoalTable();
    loadSalesGoalTable();
    table.on('edit(companyGoalTable)', function (obj) {
        var data = {
            'field': obj.field,
            'deptId': obj.data.deptId,
            'value': obj.value,
            'year': year
        };
        $.post("/goal/saveGoal", data, (function (obj) {
            return function (data) {
                var selector = 'div[lay-id=companyGoalTable] ' + obj.tr.selector+' td[data-field="'+obj.field+'"] div';
                // 更新修改后的值失败，需要恢复修改前的值
                if (data.code == 500) {
                    var oldtext = $(selector).text().replace(/,/g, '');
                    var goalData = table.cache['companyGoalTable'];
                    var index = 0;
                    // 计算当前行的index
                    for (var i = 0; i < goalData.length; i++) {
                        if (goalData[i].id === obj.data.id) {
                            index = i;
                            break;
                        }
                    }
                    goalData[index][obj.field] = oldtext;
                    table.reload('companyGoalTable', {
                        url: '',
                        data: goalData
                    });
                }
                layer.tips(data.msg, selector, {
                    tips: [1, '#0FA6D8'] //还可配置颜色
                });
            }
        })(obj), "json")
    });
    table.on('edit(salesmanGoalTable)', function (obj) {
        var data = {
            'field': obj.field,
            'deptId': obj.data.deptId,
            'ossUserId': obj.data.ossUserId,
            'value': obj.value,
            'year': year1
        };
        $.post("/goal/saveGoal", data, (function (obj) {
            return function (data) {
                var selector = 'div[lay-id=salesmanGoalTable] ' + obj.tr.selector+' td[data-field="'+obj.field+'"] div';
                if (data.code == 500) {
                    var oldtext = $(selector).text();
                    var goalData = table.cache['salesmanGoalTable'];
                    var index = 0;
                    // 计算当前行的index
                    for (var i = 0; i < goalData.length; i++) {
                        if (goalData[i].id === obj.data.id) {
                            index = i;
                            break;
                        }
                    }
                    goalData[index][obj.field] = oldtext;
                    table.reload('salesmanGoalTable', {
                        url: '',
                        data: goalData
                    });
                }
                layer.tips(data.msg, selector, {
                    tips: [1, '#0FA6D8'] //还可配置颜色
                });
            }
        })(obj), "json")
    });
    table.on('toolbar(companyGoalTable)', function (obj) {
        // 导出，type：0部门，1销售
        if (obj.event === 'EXPORT_EXCEL') {
            $.post("/goal/exportGoalDetail", {'year': year, 'type': 0}, function (data) {
                    if (data.code == 500) {
                        layer.msg(data.msg);
                    } else if (data.code == 200) {
                        down_load(data.data);
                    }
                }
            );
        }
    });
    table.on('toolbar(salesmanGoalTable)', function (obj) {
        // 导出，type：0部门，1销售
        if (obj.event === 'EXPORT_EXCEL') {
            $.post("/goal/exportGoalDetail", {'year': year1, 'type': 1}, function (data) {
                    if (data.code == 500) {
                        layer.msg(data.msg);
                    } else if (data.code == 200) {
                        down_load(data.data);
                    }
                }
            );
        }
    });
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    // debugger
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    num = (num + '').replace(/,/g, '');
    var tempArr = num.split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

//合并公司目标表
function mergeCompanyGoalDetail(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var columsName = ['regionName']; //需要合并的列名称
    var columsIndex = [0]; //需要合并的列索引值
    var mergeCondition = 'regionName'; //需要合并的 首要条件  在这个前提下进行内容相同的合并
    var tdArrL = layui.$('div[lay-id=companyGoalTable]').find('.layui-table-fixed-l > .layui-table-body').find("tr"); //序号列左定位产生的table tr

    for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
        var trArr = layui.$('div[lay-id=companyGoalTable]').find(".layui-table-main>.layui-table").find("tr"); //所有行
        for (var i = 1; i < data.length; i++) { //这里循环表格当前的数据

            if (data[i][mergeCondition] === data[i - 1][mergeCondition]) {
                var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]); //获取当前行的当前列
                var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]); //获取相同列的第一列

                if (data[i][columsName[k]] === data[i - 1][columsName[k]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () { //相同列的第一列增加rowspan属性
                        layui.$(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () { //当前行隐藏
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

    //操作左右固定列的表格
    $.each($("#companyGoalTable").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function (i, v) {
        for (var k = 0; k < columsName.length; k++) {
            var tdCur = $(v).find('td').eq(columsIndex[k]);
            if (tdCur.css('display') === 'none') {
                tdArrL.eq(i).find('td').eq(columsIndex[k]).css('display', 'none');
            } else {
                tdArrL.eq(i).find('td').eq(columsIndex[k]).attr('rowspan', tdCur.attr('rowspan'));
            }
        }
    });
}

//合并销售目标表
function mergeSalesGoalDetail(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var columsName = ['regionName', 'parentDeptName', 'deptName']; //需要合并的列名称
    var columsIndex = [0, 1, 2]; //需要合并的列索引值
    var mergeCondition = 'regionName'; //需要合并的 首要条件  在这个前提下进行内容相同的合并
    var tdArrL = layui.$('div[lay-id=salesmanGoalTable]').find('.layui-table-fixed-l > .layui-table-body').find("tr"); //序号列左定位产生的table tr

    for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
        var trArr = layui.$('div[lay-id=salesmanGoalTable]').find(".layui-table-main>.layui-table").find("tr"); //所有行
        for (var i = 1; i < data.length; i++) { //这里循环表格当前的数据

            if (data[i][mergeCondition] === data[i - 1][mergeCondition]) {
                var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]); //获取当前行的当前列
                var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]); //获取相同列的第一列

                if (data[i][columsName[k]] === data[i - 1][columsName[k]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () { //相同列的第一列增加rowspan属性
                        layui.$(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () { //当前行隐藏
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

    //操作左右固定列的表格
    $.each($("#salesmanGoalTable").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function (i, v) {
        for (var k = 0; k < columsName.length; k++) {
            var tdCur = $(v).find('td').eq(columsIndex[k]);
            if (tdCur.css('display') === 'none') {
                tdArrL.eq(i).find('td').eq(columsIndex[k]).css('display', 'none');
            } else {
                tdArrL.eq(i).find('td').eq(columsIndex[k]).attr('rowspan', tdCur.attr('rowspan'));
            }
        }
    });
}