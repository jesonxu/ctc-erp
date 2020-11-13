var layer;
var element;
var form;
var table;
var laydate;
var excel;
// 加载中遮罩
var loadingIndex;
// 查询年份
var year;

// 加载Excel插件
layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    excel: 'excel'
});

$(document).ready(function () {
    init();
});


function init() {
    // 默认查去年
    year = new Date().getFullYear();
    layui.use(['layer', 'table', 'form', 'element', 'laydate', 'excel'], function () {
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        laydate = layui.laydate;
        excel = layui.excel;
        loadSaleBusinessDetail(year);

        table.on('row(saleBusinessDetail)', function (obj) {
            $(".layui-table-body tr ").attr({"style": "background:#FFFFFF"});//其他tr恢复原样
            $(obj.tr.selector).attr({"style": "background:#F2F2F2"});//改变当前tr颜色
        });

        table.on('toolbar(saleBusinessDetail)', function (obj) {
            // 导出
            if (obj.event === 'EXPORT_EXCEL') {
                var data = table.cache['saleBusinessDetail'];
                data = excel.filterExportData(data, {
                    regionName: 'regionName',
                    parentDeptName: 'parentDeptName',
                    deptName: 'deptName',
                    saleName: 'saleName',
                    receivables1: function (value, line, data) {
                        var monthData = line['months']['01'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit1: function (value, line, data) {
                        var monthData = line['months']['01'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income1: function (value, line, data) {
                        var monthData = line['months']['01'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables2: function (value, line, data) {
                        var monthData = line['months']['02'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit2: function (value, line, data) {
                        var monthData = line['months']['02'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income2: function (value, line, data) {
                        var monthData = line['months']['02'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables3: function (value, line, data) {
                        var monthData = line['months']['03'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit3: function (value, line, data) {
                        var monthData = line['months']['03'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income3: function (value, line, data) {
                        var monthData = line['months']['03'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables4: function (value, line, data) {
                        var monthData = line['months']['04'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit4: function (value, line, data) {
                        var monthData = line['months']['04'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income4: function (value, line, data) {
                        var monthData = line['months']['04'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables5: function (value, line, data) {
                        var monthData = line['months']['05'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit5: function (value, line, data) {
                        var monthData = line['months']['05'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income5: function (value, line, data) {
                        var monthData = line['months']['05'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables6: function (value, line, data) {
                        var monthData = line['months']['06'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit6: function (value, line, data) {
                        var monthData = line['months']['06'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income6: function (value, line, data) {
                        var monthData = line['months']['06'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables7: function (value, line, data) {
                        var monthData = line['months']['07'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit7: function (value, line, data) {
                        var monthData = line['months']['07'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income7: function (value, line, data) {
                        var monthData = line['months']['07'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables8: function (value, line, data) {
                        var monthData = line['months']['08'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit8: function (value, line, data) {
                        var monthData = line['months']['08'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income8: function (value, line, data) {
                        var monthData = line['months']['08'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables9: function (value, line, data) {
                        var monthData = line['months']['09'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit9: function (value, line, data) {
                        var monthData = line['months']['09'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income9: function (value, line, data) {
                        var monthData = line['months']['09'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables10: function (value, line, data) {
                        var monthData = line['months']['10'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit10: function (value, line, data) {
                        var monthData = line['months']['10'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income10: function (value, line, data) {
                        var monthData = line['months']['10'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables11: function (value, line, data) {
                        var monthData = line['months']['11'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit11: function (value, line, data) {
                        var monthData = line['months']['11'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income11: function (value, line, data) {
                        var monthData = line['months']['11'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    receivables12: function (value, line, data) {
                        var monthData = line['months']['12'];
                        var receivables = '-';
                        if (monthData != null) {
                            receivables = monthData[0];
                        }
                        return {
                            v: receivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    grossProfit12: function (value, line, data) {
                        var monthData = line['months']['12'];
                        var grossProfit = '-';
                        if (monthData != null) {
                            grossProfit = monthData[1];
                        }
                        return {
                            v: grossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    income12: function (value, line, data) {
                        var monthData = line['months']['12'];
                        var income = '-';
                        if (monthData != null) {
                            income = monthData[2];
                        }
                        return {
                            v: income,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                    sumReceivables: function (value, line, data) {
                        var sumReceivables = line['sumReceivables'];
                        return {
                            v: sumReceivables,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    left: {style: 'thin'}
                                }
                            }
                        }
                    },
                    sumGrossProfit: function (value, line, data) {
                        var sumGrossProfit = line['sumGrossProfit'];
                        return {
                            v: sumGrossProfit,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                }
                            }
                        }
                    },
                    sumIncome: function (value, line, data) {
                        var sumIncome = line['sumIncome'];
                        return {
                            v: sumIncome,
                            s: {
                                alignment: {
                                    horizontal: 'right',
                                    vertical: 'center',
                                },
                                border: {
                                    right: {style: 'thin'}
                                }
                            }
                        }
                    },
                });
                // 加入表头
                data.unshift({
                    regionName: '区域',
                    parentDeptName: '事业部',
                    deptName: '部门',
                    saleName: '员工',
                    receivables1: '收入',
                    grossProfit1: '毛利',
                    income1: '收款',
                    receivables2: '收入',
                    grossProfit2: '毛利',
                    income2: '收款',
                    receivables3: '收入',
                    grossProfit3: '毛利',
                    income3: '收款',
                    receivables4: '收入',
                    grossProfit4: '毛利',
                    income4: '收款',
                    receivables5: '收入',
                    grossProfit5: '毛利',
                    income5: '收款',
                    receivables6: '收入',
                    grossProfit6: '毛利',
                    income6: '收款',
                    receivables7: '收入',
                    grossProfit7: '毛利',
                    income7: '收款',
                    receivables8: '收入',
                    grossProfit8: '毛利',
                    income8: '收款',
                    receivables9: '收入',
                    grossProfit9: '毛利',
                    income9: '收款',
                    receivables10: '收入',
                    grossProfit10: '毛利',
                    income10: '收款',
                    receivables11: '收入',
                    grossProfit11: '毛利',
                    income11: '收款',
                    receivables12: '收入',
                    grossProfit12: '毛利',
                    income12: '收款',
                    sumReceivables: '收入',
                    sumGrossProfit: '毛利',
                    sumIncome: '收款'
                });
                data.unshift({
                    regionName: '区域',
                    parentDeptName: '事业部',
                    deptName: '部门',
                    saleName: '员工',
                    receivables1: '1月',
                    grossProfit1: '1月',
                    income1: '1月',
                    receivables2: '2月',
                    grossProfit2: '2月',
                    income2: '2月',
                    receivables3: '3月',
                    grossProfit3: '3月',
                    income3: '3月',
                    receivables4: '4月',
                    grossProfit4: '4月',
                    income4: '4月',
                    receivables5: '5月',
                    grossProfit5: '5月',
                    income5: '5月',
                    receivables6: '6月',
                    grossProfit6: '6月',
                    income6: '6月',
                    receivables7: '7月',
                    grossProfit7: '7月',
                    income7: '7月',
                    receivables8: '8月',
                    grossProfit8: '8月',
                    income8: '8月',
                    receivables9: '9月',
                    grossProfit9: '9月',
                    income9: '9月',
                    receivables10: '10月',
                    grossProfit10: '10月',
                    income10: '10月',
                    receivables11: '11月',
                    grossProfit11: '11月',
                    income11: '11月',
                    receivables12: '12月',
                    grossProfit12: '12月',
                    income12: '12月',
                    sumReceivables: '合计',
                    sumGrossProfit: '合计',
                    sumIncome: '合计',
                });
                // 合并表头单元格
                var mergeConf = excel.makeMergeConfig([
                    ['E1', 'G1'],
                    ['H1', 'J1'],
                    ['K1', 'M1'],
                    ['N1', 'P1'],
                    ['Q1', 'S1'],
                    ['T1', 'V1'],
                    ['W1', 'Y1'],
                    ['Z1', 'AB1'],
                    ['AC1', 'AE1'],
                    ['AF1', 'AH1'],
                    ['AI1', 'AK1'],
                    ['AL1', 'AN1'],
                    ['AO1', 'AQ1'],
                    ['A1', 'A2'],
                    ['B1', 'B2'],
                    ['C1', 'C2'],
                    ['D1', 'D2'],
                ]);
                // 设置表头单元格样式
                excel.setExportCellStyle(data, 'A1:AQ2', {
                    s: {
                        alignment: {
                            horizontal: 'right',
                            vertical: 'center'
                        }
                    }
                });
                excel.exportExcel({
                    sheet1: data
                }, year + '年云通讯销售业绩明细经营状况.xlsx', 'xlsx', {
                    extend: {
                        '!merges': mergeConf /*合并选项*/
                    }
                });
            }
        })
    });
}

// 切换年份按钮
function initChangeYearBtn() {
    $('#year').text(year + '年');

    laydate.render({
        elem: '#changeYear',
        type: 'year',
        trigger: 'click',
        value: year, // 默认去年
        max: new Date().getFullYear() + '-12-31', // 最大值为今年
        btns: ['now', 'confirm'],
        done: function (value, date) {
            year = value;
            loadSaleBusinessDetail(value);
            $('#year').text(year + '年');
        }
    })
}

// 加载业绩概况数据
function loadSaleBusinessDetail(year) {
    var loading = layer.load(2);
    // 查询产品/客户的统计数据
    $.ajaxSettings.async = true;
    $.post("/manageConsole/readSaleBusinessDetail.action?temp=" + Math.random(), {
        'year': year
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充统计表数据
                initSaleBusinessDetail('#saleBusinessDetail', data.data);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
}

// 初始化表格数据
function initSaleBusinessDetail(id, data) {
    var fontSize = 'font-size: 12px;';
    var leftBorder = 'border-left: 1px solid;font-size: 12px;';
    var rightBorder = 'border-right: 1px solid;font-size: 12px;';

    //第一个实例
    table.render({
        elem: id,
        limit: Number.MAX_VALUE,
        height: 'full-40',
        toolbar: '#toolbarDemo',
        // defaultToolbar: ['print', {title: '导出', layEvent: 'LAYTABLE_TIPS', icon: 'layui-icon-tips'}],
        defaultToolbar: false,
        printTitle: {
            name: year + "年云通讯销售业绩明细经营状况",
            colspan: 43
        },
        cols: [
            [
                {
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
                title: '员工',
                align: 'center',
                style: fontSize,
                rowspan: 2,
                width: 80,
                field: 'saleName',
                fixed: 'left'
            }, {
                title: '1月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '2月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '3月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '4月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '5月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '6月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '7月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '8月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '9月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '10月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '11月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '12月',
                align: 'center',
                style: fontSize,
                colspan: 3
            }, {
                title: '合计',
                align: 'center',
                style: fontSize,
                colspan: 3,
            }],
            [
                {
                field: 'receivables1',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit1',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income1',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['01'])) {
                        var monthData = res['months']['01'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables2',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit2',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income2',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['02'])) {
                        var monthData = res['months']['02'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables3',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit3',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income3',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['03'])) {
                        var monthData = res['months']['03'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables4',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit4',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income4',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['04'])) {
                        var monthData = res['months']['04'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables5',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit5',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income5',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['05'])) {
                        var monthData = res['months']['05'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables6',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit6',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income6',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['06'])) {
                        var monthData = res['months']['06'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables7',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit7',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income7',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['07'])) {
                        var monthData = res['months']['07'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables8',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit8',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income8',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['08'])) {
                        var monthData = res['months']['08'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables9',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit9',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income9',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['09'])) {
                        var monthData = res['months']['09'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables10',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit10',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income10',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['10'])) {
                        var monthData = res['months']['10'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables11',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit11',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income11',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['11'])) {
                        var monthData = res['months']['11'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'receivables12',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[0]);
                    }
                    return '-';
                }
            }, {
                field: 'grossProfit12',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[1]);
                    }
                    return '-';
                }
            }, {
                field: 'income12',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['months']) && !isNull(res['months']['12'])) {
                        var monthData = res['months']['12'];
                        return thousand(monthData[2]);
                    }
                    return '-';
                }
            }, {
                field: 'sumReceivables',
                title: '收入',
                align: 'right',
                width: 80,
                style: leftBorder,
                templet: function (res) {
                    if (!isNull(res['sumReceivables'])) {
                        var monthData = res['sumReceivables'];
                        return thousand(monthData);
                    }
                    return '-';
                }
            }, {
                field: 'sumGrossProfit',
                align: 'right',
                title: '毛利',
                width: 80,
                style: fontSize,
                templet: function (res) {
                    if (!isNull(res['sumGrossProfit'])) {
                        var monthData = res['sumGrossProfit'];
                        return thousand(monthData);
                    }
                    return '-';
                }
            }, {
                field: 'sumIncome',
                align: 'right',
                title: '收款',
                width: 80,
                style: rightBorder,
                templet: function (res) {
                    if (!isNull(res['sumIncome'])) {
                        var monthData = res['sumIncome'];
                        return thousand(monthData);
                    }
                    return '-';
                }
            }
            ]
        ],
        data: data,
        done: function (res, curr, count) {
            // 取消加载中遮罩
            layer.close(loadingIndex);
            mergeSaleBusinessDetail(res); //合并单元格
            element.render();
            initChangeYearBtn();
        }
    });
}

//合并开始
function mergeSaleBusinessDetail(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var columsName = ['regionName', 'parentDeptName', 'deptName']; //需要合并的列名称
    var columsIndex = [0, 1, 2]; //需要合并的列索引值
    var mergeCondition = 'regionName'; //需要合并的 首要条件  在这个前提下进行内容相同的合并
    var tdArrL = layui.$('.layui-table-fixed-l > .layui-table-body').find("tr"); //序号列左定位产生的table tr

    for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
        var trArr = layui.$(".layui-table-main>.layui-table").find("tr"); //所有行
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

    // 合并汇总单元格
    var sumColumnsName = ['parentDeptName', 'deptName']; //一行中需要合并的汇总列名称
    var sumColumnsIndex = [1, 2]; //汇总的列索引值
    var sumCondition = 'parentDeptName'; //需要合并的 首要条件  在这个前提下进行内容相同的合并
    mergeIndex = 0; //定位需要添加合并属性的行数
    mark = 0;

    var trArr = layui.$(".layui-table-main>.layui-table").find("tr"); //所有行
    for (var i = 0; i < data.length; i++) { //这里循环表格当前的数据
        var sumColumn = data[i][sumCondition];
        if (isNotBlank(sumColumn) && sumColumn.endWith('汇总')) {
            for (var j = 1; j < sumColumnsName.length; j++) {
                var tdCurArr = trArr.eq(i).find("td").eq(sumColumnsIndex[j]); //获取当前行的当前列
                var tdPreArr = trArr.eq(i).find("td").eq(sumColumnsIndex[mergeIndex]); //获取相同行最开始要合并的列

                if (data[i][sumColumnsName[j]] === data[i][sumColumnsName[j - 1]]) { //后一列的值与前一列的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () { //相同行最开始要合并的列增加colspan属性
                        layui.$(this).attr("colspan", mark);
                    });
                    tdCurArr.each(function () { //当前列隐藏
                        layui.$(this).css("display", "none");
                    });
                } else {
                    mergeIndex = j;
                    mark = 1; //一旦前后两列的值不一样了，那么需要合并的格子数mark就需要重新计算
                }
            }
        } else {
            mergeIndex = 0;
            mark = 1; //一旦前后两列的值不一样了，那么需要合并的格子数mark就需要重新计算
        }
    }

    //操作左右固定列的表格
    $.each($("#saleBusinessDetail").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function (i, v) {
        for (var k = 0; k < columsName.length; k++) {
            var tdCur = $(v).find('td').eq(columsIndex[k]);
            if (tdCur.css('display') === 'none') {
                tdArrL.eq(i).find('td').eq(columsIndex[k]).css('display', 'none');
            } else {
                tdArrL.eq(i).find('td').eq(columsIndex[k]).attr('rowspan', tdCur.attr('rowspan'));
            }
        }
        for (var k = 0; k < sumColumnsName.length; k++) {
            var tdCur = $(v).find('td').eq(sumColumnsIndex[k]);
            if (tdCur.css('display') === 'none') {
                tdArrL.eq(i).find('td').eq(sumColumnsIndex[k]).css('display', 'none');
            } else {
                tdArrL.eq(i).find('td').eq(sumColumnsIndex[k]).attr('colspan', tdCur.attr('colspan'));
            }
        }
    });
}