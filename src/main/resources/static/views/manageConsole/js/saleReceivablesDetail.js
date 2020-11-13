var layer;
var element;
var form;
var table;
var laydate;
var year;
// 加载中遮罩
var loadingIndex;

$(document).ready(function () {
    init();
});


function init() {
    year = new Date().getFullYear();
    layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        table = layui.table;
        laydate = layui.laydate;
        loadSaleBusinessDetail(year);
    });
}

// 加载业绩概况数据
function loadSaleBusinessDetail(year) {
    var loading = layer.load(2);
    // 查询产品/客户的统计数据
    $.ajaxSettings.async = true;
    $.post("/manageConsole/readSaleReceivablesDetail.action?temp=" + Math.random(), {
        year: year
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充统计表数据
                initSaleBusinessDetail('#saleReceivablesDetail', data.data);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
}

// 初始化表格数据
function initSaleBusinessDetail(id, data) {
    var fontSize = 'font-size: 12px;';

    //第一个实例
    table.render({
        elem: id,
        limit: Number.MAX_VALUE,
        height: 'full-40',
        toolbar: '#toolbarDemo',
        defaultToolbar: ['print'],
        printTitle: {
        	name: year + ' 年各事业部/销售人员收款明细表',
        	colspan: 4
        },
        cols: [
            [{
                title: '事业部名称',
                align: 'center',
                width: 100,
                field: 'deptName',
                style: fontSize,
            }, {
                title: '销售',
                align: 'center',
                width: 100,
                field: 'salerName',
                style: fontSize,
            }, {
                title: '预计收款',
                align: 'right',
                field: 'receivables',
                templet: function (data) {
                    return thousand(data.receivables);
                },
                style: fontSize,
            }, {
                title: '实际收款',
                align: 'right',
                field: 'actualreceivables',
                templet: function (data) {
                    return thousand(data.actualreceivables);
                },
                style: fontSize,
            }, {
                title: '实际收款占比',
                align: 'right',
                style: fontSize,
                field: 'actualReceivablesPercent',
            }]
        ],
        data: data,
        done: function (res, curr, count) {
            layer.close(loadingIndex);
            element.render();
            initChangeYearBtn();
            if (count > 0) {
            	merge(res)
            }
        }
    });
}

//合并开始
function merge(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var _number = 1; //保持序号列数字递增
    var columsName = ['deptName', 'salerName']; //需要合并的列名称
    var columsIndex = [0, 1, 2]; //需要合并的列索引值
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

    // 操作左右定位列的表格
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

function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}