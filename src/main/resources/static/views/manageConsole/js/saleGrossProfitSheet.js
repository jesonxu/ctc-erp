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
        loadSaleRoyalty(sale_statistic_year, getCurrentDiffStr(0), getCurrentMonthFirst());
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
        	var year2 = $('#yearSelect2').text().replace('月', '-').replace('年', '-').replace('日', '');
        	var year = $('#yearSelect').text().replace('月', '-').replace('年', '-').replace('日', '');
        	deptId = $(body).find("input[id='checkedDeptIds']").val();
            userId = $(body).find("input[id='checkedUserIds']").val();
        	loadSaleRoyalty(year, year, year2);
        	layer.close(index);
        }
    });
    setTimeout(function () {
		$('[id*=layui-layer-iframe]').contents().find(".keyword-filter").css('display', 'none');
	}, 50);
}

// 初始化现金流表格
function initRoyaltyTable(id, data, yearMonth, yearMonth2) {
	var year = yearMonth.substring(0, 4);
    var month = yearMonth.substring(5, 7);
    var day = yearMonth.substring(8);
    var year2 = yearMonth2.substring(0, 4);
    var month2 = yearMonth2.substring(5, 7);
    var day2 = yearMonth2.substring(8);
    var time = year + "年" + month + "月" + day + "日";
    var time2 = year2 + "年" + month2 + "月" + day2 + "日";
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
                field: 'deptName',
                title: '部门',
                align: 'left',
                width: 100,
                style: fontSize,
                unresize: false
            }, {
                field: 'saleName',
                title: '销售',
                align: 'left',
                width: 100,
                style: fontSize
            }, {
                field: 'customerName',
                title: '客户名称',
                align: 'left',
                style: fontSize,
                width: 300
            }, {
                field: 'productName',
                title: '产品名称',
                align: 'left',
                width: 300,
                style: fontSize,
                templet: function (rowdata){
                	if (rowdata.productName == '-') {
                		return '-';
                	}
                	if (!rowdata.productId || !rowdata.productName) {
                		return '';
                	}
                	return '<a style="color: #1E9FFF;cursor: pointer;" onclick="jumpSaleGrossProfitDetail(&quot;' + rowdata.productId + '&quot;)">' + rowdata.productName + '</a>'
                }
            },{
                field: 'loginName',
                title: '账号',
                align: 'left',
                style: fontSize
            }, {
                field: 'productType',
                title: '产品类型',
                align: 'left',
                style: fontSize,
                width: 80
            }, {
                field: 'sendCount',
                title: '计费数',
                width: 150,
                align: 'right',
                templet: function (res) {
                    return res.sendCount ? thousand(res.sendCount) : '0';
                },
                style: fontSize
            }, {
                field: 'salesVolume',
                title: '销售额',
                align: 'right',
                width: 120,
                templet: function (res) {
                    return res.salesVolume ? thousand(parseFloat(res.salesVolume).toFixed(2)) : '0.00';
                },
                style: fontSize
            }, {
                field: 'customerPrice',
                title: '销售价',
                align: 'right',
                width: 120,
                templet: function (res) {
                    return res.customerPrice ? (res.customerPrice < 0 ? '-' : thousand(parseFloat(res.customerPrice).toFixed(6))) : '0.000000';
                },
                style: fontSize
            }, {
                field: 'unitPrice',
                title: '成本价',
                align: 'right',
                width: 120,
                templet: function (res) {
                    return res.unitPrice ? (res.unitPrice < 0 ? '-' : thousand(parseFloat(res.unitPrice).toFixed(6))) : '0.000000';
                },
                style: fontSize
            }, {
                field: 'grossProfit',
                title: '毛利润',
                align: 'right',
                width: 120,
                templet: function (res) {
                    return res.grossProfit ? thousand(parseFloat(res.grossProfit).toFixed(2)) : '0.00';
                },
                style: fontSize,
                sort: true
            }]
        ],
        data: data,
        done: function (res, curr, count) {
        	$('#yearSelect2').html(time2);
        	$('#yearSelect').html(time);
            initChangeYear(id, data, time, time2);
            // 取消加载中遮罩
            layer.close(loadingIndex);
//            merge(res);
            getExportData();
            bindExportEvent();
        }
    });
}

function getExportData() {
	var header_tr = $($("#realRoyalty").next().find(".layui-table-header")[0]).find("tr");
	var body_tr = $($("#realRoyalty").next().find(".layui-table-body")[0]).find("tr");
	var header_html = "";
	var body_html = "";
    // 获取表头html，包括单元格的合并
    $.each(header_tr,function (i,tr) {
        let header_th = $(tr).find("th");
        header_html += "<tr>";
        $.each(header_th,function (j,th) {
            let rowspan_num = $(th).attr("rowspan");// 行合并数
            let colspan_num = $(th).attr("colspan");// 列合并数
            if (rowspan_num && !colspan_num){// 只有行合并时
                header_html += '<th rowspan= "'+ rowspan_num +'">';
            } else if (colspan_num && !rowspan_num){// 只有列合并时
                header_html += '<th colspan= "'+ colspan_num +'">';
            } else if (rowspan_num && colspan_num){// 行列合并均有时
                header_html += '<th rowspan= "'+ rowspan_num +'" colspan="'+ colspan_num +'">';
            } else {// 没有发生单元格合并
                header_html += '<th>';
            }
            header_html += $(th).children().children().text() + '</th>';// 获取表头名称并拼接th标签
        })
        header_html += '</tr>';
    })
    // 获取表格body数据
    $.each(body_tr,function (i,tr) {
        let body_td = $(tr).find("td");
        body_html += '<tr>';
        $.each(body_td,function (j,td) {
            body_html += '<td>' + $(td).children().text() + '</td>';
        })
        body_html += '</tr>';
    })
    $("#realRoyalty tr").remove();// 清除之前的doom结构
    $("#realRoyalty").append(header_html).append(body_html);
    $("#realRoyalty").hide();
}

function bindExportEvent() {
	$('[lay-event="LAYTABLE_EXPORT"]').unbind().bind('click', function () {
		var table2excel = new Table2Excel();
	   	 /**
	   	 * 此处的show()是为了避免table2excel将hide属性带入excel中
	   	 * 导致下载后的excel中所有数据被隐藏
	   	 */
	   	$('#realRoyalty').show();
	   	Table2Excel.extend(function(cell, cellText) {
	   		if (cellText !== undefined && cellText !== null && cellText !== '') {
	   			if (/^-?(\d+)+(\.\d+)?$/.test(cellText)) { // 纯数字
	   				return {
	   					t: 'n',
	   					v: cellText
	   				};
	   			} else if (/^-?(\d+,\d+)+(\.\d+)?$/.test(cellText) && cellText !== '-') {
	   				return {
	   					t: 'n',
	   					v: cellText.replace(/,/gi, '')
	   				};
	   			}
	   		} else {
	   			return {
	   				t: 'text',
	   				v: cellText
	   			};
	   			return null;
	   		}
		});
	   	table2excel.export($('#realRoyalty'), "权益毛利表");
	   	$('#realRoyalty').hide();
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
    var columsName = ['deptName', 'saleName', 'customerName']; //需要合并的列名称
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
function initChangeYear(id, data, yearMonth, yearMonth2) {
    laydate.render({
        elem: '#yearSelect',
        type: 'date',
        trigger: 'click',
        max: yearMonth, // 最大值为今年
        format:"yyyy年MM月dd日",
        done: function (value, date) {
            $('#yearSelect').html(value);
            var year = value.substring(0, value.indexOf("年"));
            var month = value.substring(value.indexOf("年") + 1, value.indexOf("月"));
            var day = value.substring(value.indexOf("月") + 1, value.length - 1);
            var time = year + "-" + month + "-" + day;
            var value2 = $('#yearSelect2').html();
            var year2 = value2.substring(0, value2.indexOf("年"));
            var month2 = value2.substring(value2.indexOf("年") + 1, value2.indexOf("月"));
            var day2 = value2.substring(value2.indexOf("月") + 1, value2.length - 1);
            var time2 = year2 + "-" + month2 + "-" + day2;
            loadSaleRoyalty(sale_statistic_year, time, time2);
        }
    });
    laydate.render({
        elem: '#yearSelect2',
        type: 'date',
        trigger: 'click',
        max: yearMonth, // 最大值为今年
        format:"yyyy年MM月dd日",
        done: function (value, date) {
            $('#yearSelect2').html(value);
            var year = value.substring(0, value.indexOf("年"));
            var month = value.substring(value.indexOf("年") + 1, value.indexOf("月"));
            var day = value.substring(value.indexOf("月") + 1, value.length - 1);
            var time = year + "-" + month + "-" + day;
            var value2 = $('#yearSelect').html();
            var year2 = value2.substring(0, value2.indexOf("年"));
            var month2 = value2.substring(value2.indexOf("年") + 1, value2.indexOf("月"));
            var day2 = value2.substring(value2.indexOf("月") + 1, value2.length - 1);
            var time2 = year2 + "-" + month2 + "-" + day2;
            loadSaleRoyalty(sale_statistic_year, time2, time);
        }
    })
}

// 加载销售提成数据
function loadSaleRoyalty(year, yearMonth, yearMonth2) {
    var loading = layer.load(2);

    // 查询销售的提成数据
    $.ajaxSettings.async = true;
    $.post("/saleGrossProfit/getSaleGrossProdift2Manager", {
    	deptId: deptId,
        userId: userId,
        queryDate2: yearMonth2,
        queryDate: yearMonth
    }, function (data, status) {
        if (status === "success") {
            if (!isNull(data.data)) {
                // 填充提成数据
                initRoyaltyTable('#realRoyalty', data.data, yearMonth, yearMonth2);
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