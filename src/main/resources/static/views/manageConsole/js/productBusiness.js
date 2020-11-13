var layer;
var table;
var laydate;
layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
    layer = layui.layer;
    table = layui.table;
    laydate = layui.laydate;
    // 查询年份 // 默认查去年
    var year = new Date().getFullYear();
    initSaleBusinessDetail(year);
});

// 初始化表格数据
function initSaleBusinessDetail(year) {
    var loading = layer.load(2);
    var fontSize = 'font-size: 12px;';
    var time = year + "年";
    //第一个实例
    table.render({
        elem: "#productBussinessSheet",
        url: "/manageConsole/readProductBusiness?m=" + Math.random(),
        limit: Number.MAX_VALUE,
        height: 'full-60',
        totalRow: false,
        page: false,
        toolbar:'#toolbarDemo',
        defaultToolbar:["print"],
        where:{
        	year: year
        },
        cols: [[{
            title: '月份',
            align: 'center',
            style: fontSize,
            width: 70,
            field: 'month'
        }, {
            title: '产品类型',
            align: 'center',
            style: fontSize,
            width: 120,
            field: 'productType'
        }, {
            title: '总成功数',
            align: 'right',
            style: fontSize,
            field: 'totalSuccessCount',
            totalRow: true,
            templet: function (data) {
                return thousand(data.totalSuccessCount);
            }
        }, {
            title: '计费数',
            align: 'right',
            style: fontSize,
            field: 'successCount',
            totalRow: true,
            templet: function (data) {
                return thousand(data.successCount);
            }
        }, {
            title: '平均销售单价',
            align: 'right',
            style: fontSize,
            totalRow: true,
            field: 'salePrice'
        }, {
            title: '权益收入',
            align: 'right',
            totalRow: true,
            style: fontSize,
            field: "equityIncome",
            templet: function (data) {
                return thousand(data.equityIncome);
            }
        },/* {
            title: '预付结余',
            align: 'right',
            totalRow: true,
            style: fontSize,
            field: "balance",
            templet: function (data) {
                return thousand(data.balance);
            }
        }, */ {
            title: '欠款',
            align: 'right',
            totalRow: true,
            style: fontSize,
            field: "arrears",
            templet: function (data) {
                return thousand(data.arrears);
            }
        }, {
            title: '平均成本单价',
            align: 'right',
            totalRow: true,
            style: fontSize,
            field: "costPrice"
        }, {
            title: '成本',
            align: 'right',
            totalRow: true,
            style: fontSize,
            field: "cost",
            templet: function (data) {
                return thousand(data.cost);
            }
        }, {
            title: '毛利',
            align: 'right',
            totalRow: true,
            style: fontSize,
            field: "gross",
            templet: function (data) {
                return thousand(data.gross);
            }
        }]],
        done: function (res, curr, count) {
            $('#year').html(time);
            initChangeYear(time);
            // 取消加载中遮罩
            layer.close(loading);
            merge(res);
        },
        parseData: function (res) {
            var code = 0;
            if (res.code !== 200) {
                code = res.code;
            }
            var data = res.data;
            return {
                "code": code,
                "msg": res.msg, //解析提示文本
                "data": data //解析数据列表
            };
        }, printTitle: {
            name: time + "云通讯产品业绩结构分析表",
            colspan: 10
        }
    });
}

//合并开始
function merge(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var _number = 1; //保持序号列数字递增
    var columsName = ['month', 'productType']; //需要合并的列名称
    var columsIndex = [0]; //需要合并的列索引值
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
    layui.$.each(layui.$("#productBussinessSheet").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function(i, v) {
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



// 切换年份按钮
function initChangeYear(yearMonth) {
    laydate.render({
        elem: '#year',
        type: 'year',
        trigger: 'click',
        max: yearMonth, // 最大值为今年
        format:"yyyy年",
        done: function (value, date) {
            $('#year').html(value);
            var year = value.substring(0, value.indexOf("年"));
            var time = year;
            initSaleBusinessDetail(time);
        }
    })
}


//处理千分位
function thousand(num) {
    if (num === undefined || num === null) {
        return '-';
    }
    if (typeof num != 'number') {
        return num;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1].substr(0, 2)) : '');
}