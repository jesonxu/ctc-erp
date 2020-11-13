var layer;
var element;
var form;
var table;
var laydate;

var tableIns;

var userId;
var deptId;

var loadingIndex;

var selectYear = new Date().getFullYear();
var selectSettleType;

$(document).ready(function () {
    init();
});

function init() {
    layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
        layer = layui.layer;
        element = layui.element;
        form = layui.form;
        table = layui.table;
        laydate = layui.laydate;
        renderTable(null);
    });
}

// 打开客户过滤tab页
function openFilterTab() {
    var area = ['400px', '600px'];
    layer.open({
        type: 2,
        title: '客户过滤',
        area: area,
        btn: ['确定', '取消'],
        btnAlign: 'c',
        fixed: false, // 不固定
        maxmin: true,
        content: '/customer/toCustomerFilter.action',
        yes: function (index, layero) {
        	var body = layer.getChildFrame('body', index);
        	deptId = $(body).find("input[id='checkedDeptIds']").val();
            userId = $(body).find("input[id='checkedUserIds']").val();
            search();
        	layer.close(index);
        }
    });
    setTimeout(function () {
		$('[id*=layui-layer-iframe]').contents().find(".keyword-filter").css('display', 'none');
	}, 100);
}

function initTool() {
	$('[lay-event="LAYTABLE_EXPORT"]').unbind().bind('click', function () {
		var table2excel = new Table2Excel();
	   	 /**
	   	 * 此处的show()是为了避免table2excel将hide属性带入excel中
	   	 * 导致下载后的excel中所有数据被隐藏
	   	 */
	   	$('#receivableAccount').show();
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
	   	table2excel.export($('#receivableAccount'), selectYear + "年应收账款表");
	   	$('#receivableAccount').hide();
	});
}

function settleTypeChange(settleType) {
	if (settleType !== undefined && settleType !== null && settleType !== '') {
		$('#settleTypeSelect').val(settleType);
	}
	var settleTypeContent = '全部';
	if (settleType == 1) {
		settleTypeContent = '预付';
	} else if (settleType == 2) {
		settleTypeContent = '后付';
	}
	$('#settleTypeSelectDiv input').val(settleTypeContent);
	form.on('select(settleTypeSelect)', function (data) {
		selectSettleType = data.value;
		var url = "/receivableAccount/getAllReceivableAccount.action?queryDate=" 
			+ selectYear
			+ "&settleType=" + ((selectSettleType && selectSettleType != -1) ? selectSettleType : '')
			+ "&userId=" + (userId ? userId : '') 
			+ "&deptId=" + (deptId ? deptId : '')
			+ "&temp=" + Math.random();
		renderTable(url);
    });
}

function createCols() {
	var fontSize = 'font-size: 12px;';
	var cols = [];
	cols[0] = [{
        field: 'deptName',
        title: '部门',
        align: 'left',
        width: 80,
        fixed: 'left',
        style: fontSize,
        rowspan: 2,
        unresize: false,
        totalRow: false,
        totalRowText: '合计'
    }, {
        field: 'saleName',
        title: '销售',
        align: 'left',
        width: 80,
        fixed: 'left',
        style: fontSize,
        rowspan: 2,
        totalRow: false,
        totalRowText: '-'
    }, {
        field: 'customerName',
        title: '客户名称',
        align: 'left',
        style: fontSize,
        rowspan: 2,
        width: 220,
        fixed: 'left',
        totalRow: false,
        totalRowText: '-'
    }, {
    	title: '应收金额',
    	align: 'center',
    	style: fontSize,
    	colspan: 13
    }, {
        title: '已开票金额',
        align: 'center',
        style: fontSize,
        colspan: 13
    }, {
        title: '已收款金额',
        align: 'center',
        style: fontSize,
        colspan: 13
    }, {
    	field: 'notInvoice',
        title: '未开票金额',
        align: 'right',
        style: fontSize,
        width: 90,
        fixed: 'right',
        templet: function (res) {
        	var value = parseFloat(res.notInvoice ? res.notInvoice : 0).toFixed(2)
            return thousand(value, true);
        },
        totalRow: true,
        totalConfig: {decimal: 2, thousand: true},
        rowspan: 2
    }, {
    	field: 'invoicedNotReceive',
        title: '已开票<br>未回款金额',
        align: 'right',
        style: fontSize,
        width: 90,
        fixed: 'right',
        templet: function (res) {
        	var value = parseFloat(res.invoicedNotReceive ? res.invoicedNotReceive : 0).toFixed(2)
        	return thousand(value, true);
        },
        totalRow: true,
        totalConfig: {decimal: 2, thousand: true},
        rowspan: 2
    }, {
    	field: 'notReceive',
        title: '未回款金额',
        align: 'right',
        style: fontSize,
        width: 90,
        fixed: 'right',
        templet: function (res) {
        	var value = parseFloat(res.notReceive ? res.notReceive : 0).toFixed(2)
        	return thousand(value, true);
        },
        totalRow: true,
        totalConfig: {decimal: 2, thousand: true},
        rowspan: 2
    }];
	cols[1] = [];
	// 应收
	for (var i = 0; i < 12; i++) {
		cols[1].push(getMonthCols('receivables', i));
	}
	cols[1].push(getMonthSum('receivables'));
	// 已开票金额
	for (var i = 0; i < 12; i++) {
		cols[1].push(getMonthCols('invoiceds', i));
	}
	cols[1].push(getMonthSum('invoiceds'));
	// 已收
	for (var i = 0; i < 12; i++) {
		cols[1].push(getMonthCols('receiveds', i));
	}
	cols[1].push(getMonthSum('receiveds'));
	return cols;
}

function getMonthSum(colName) {
	return {
		field: colName + 'Sum',
        title: '合计',
        width: 80,
        align: 'right',
        templet: function (res) {
        	var value = res[colName + 'Sum'];
            return (value === undefined || value === null) ? '-' : thousand(value, true);
        },
        style: 'font-size: 12px;',
        totalRow: true,
        totalConfig: {decimal: 2, thousand: true}
    };
}

function getMonthCols(colName, i) {
	selectYear = selectYear2 ? selectYear2 : selectYear;
	var displayNull = false;
	if (selectYear == new Date().getFullYear() && i > new Date().getMonth()) {
		displayNull = true;
	}
	var result = {
		field: colName + i,
        title: (i + 1) + '月',
        width: 65,
        align: 'right',
        templet: function (res) {
        	var value = res[colName + i];
            return displayNull ? '-' : (value === undefined || value === null ? '0' : thousand(value));
        },
        style: 'font-size: 12px;',
        totalRow: true,
        totalConfig: {decimal: 2, thousand: true}
    };
	if (displayNull) {
		result['totalRow'] = false;
		result['totalRowText'] = '-';
	} else {
		result['totalRow'] = true;
		result['totalConfig'] = {decimal: 2, thousand: true};
	}
	return result;
}

function renderTable(url) {
	selectYear = selectYear2 ? selectYear2 : selectYear;
	loadingIndex = layer.load(2);
	var params = {
        elem: '#receivableAccount',
        totalRow: true,
        toolbar: '#toolbarDemo',
        defaultToolbar: ['exports'],
        page: false,
        fixed: true,
        method: 'POST',
        height: 'full-40',
        limit: Number.MAX_VALUE,
        cols: createCols(),
        done: function (res, curr, count) {
        	$('#yearSelect').html(new Date().getFullYear());
            initSelect(new Date().getFullYear(), selectSettleType);
            layer.close(loadingIndex);
            // merge(res);
            getEXportData();
            initTool();
        },
        parseData: function (res) { // res 即为原始返回的数据
            return {
                "code": 0, // 解析接口状态
                "count": Number.MAX_VALUE, // 解析数据长度
                "data": parseData(res)
                // 解析数据列表
            };
        },
    };
	if (url) {
		params.url = url;
	} else {
		params.data = [];
	}
	tableIns = table.render(params);
}

function parseData(res) {
	if (res && res.data) {
		for (var i = 0; i < res.data.length; i++) {
			var sum = 0;
			for (var j = 0; j < res.data[i]['receivables'].length; j++) {
				res.data[i]['receivables' + j] = res.data[i]['receivables'][j];
				sum += res.data[i]['receivables'][j] ? res.data[i]['receivables'][j] : 0;
			}
			res.data[i]['receivablesSum'] = sum;
			sum = 0;
			for (var j = 0; j < res.data[i]['receiveds'].length; j++) {
				res.data[i]['receiveds' + j] = res.data[i]['receiveds'][j];
				sum += res.data[i]['receiveds'][j] ? res.data[i]['receiveds'][j] : 0;
			}
			res.data[i]['receivedsSum'] = sum;
			sum = 0;
			for (var j = 0; j < res.data[i]['invoiceds'].length; j++) {
				res.data[i]['invoiceds' + j] = res.data[i]['invoiceds'][j];
				sum += res.data[i]['invoiceds'][j] ? res.data[i]['invoiceds'][j] : 0;
			}
			res.data[i]['invoicedsSum'] = sum;
		}
		return res.data;
	} else {
		return null;
	}
}

function merge(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var _number = 1; //保持序号列数字递增
    var columsName = ['deptName', 'saleName', 'customerName']; //需要合并的列名称
    var columsIndex = [0, 1]; //需要合并的列索引值
    var tdArrL = layui.$('.layui-table-fixed-l > .layui-table-body').find("tr"); //序号列左定位产生的table tr
    var tdArrR = layui.$('.layui-table-fixed-r > .layui-table-body').find("tr"); //操作列定右位产生的table tr

    for (var k = 0; k < columsName.length; k++) { // 这里循环所有要合并的列
        var trArr = layui.$('.layui-table-body:eq(1)').find("table").find("tr"); // 所有行
        for (var i = 1; i < res.data.length; i++) { // 这里循环表格当前的数据
            var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]); // 获取当前行的当前列
            var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]); // 获取相同列的第一列

            if (data[i][columsName[k]] && data[i][columsName[k]] === data[i - 1][columsName[k]] 
            	&& data[i][columsName[0]] === data[i - 1][columsName[0]]) { // 后一行的值与前一行的值做比较，相同就需要合并
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
        }
        mergeIndex = 0;
        mark = 1;
    }

    //操作左右定位列的表格
    layui.$.each(layui.$("#receivableAccount").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function(i, v) {
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
var selectYear2 = null;
function initSelect(selectYear, settleType) {
	selectYear = selectYear2 ? selectYear2 : selectYear;
	var value = selectYear ? selectYear : new Date().getFullYear();
	value = value + '';
	value = value.indexOf('年') >= 0 ? value : (value + '年');
    laydate.render({
        elem: '#yearSelect',
        type: 'year',
        trigger: 'click',
        max: new Date().getFullYear() + '年', // 最大值为今年
        value: value,
        format:"yyyy年",
        done: function (value, date) {
        	selectYear2 = value;
            $('#yearSelect').html(value);
            search();
        }
    });
    settleTypeChange(settleType);
}

// 处理千分位
function thousand(num, needFormat) {
    if (!num) {
        return 0;
    }
	var reg = /\d{1,3}(?=(\d{3})+$)/g;
	var tempArr = (num + '').split('.');
	return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

var numberFormat = function (value) {
	var param = {};
    var k = 10000,
        sizes = ['', '万', '亿', '万亿'],
        i;
    if(value < k){
        param.value =value
        param.unit=''
    }else{
        i = Math.floor(Math.log(value) / Math.log(k)); 
  
        param.value = ((value / Math.pow(k, i))).toFixed(2);
        param.unit = sizes[i];
    }
    return param;
}

function search() {
  var url = "/receivableAccount/getAllReceivableAccount.action?queryDate=" 
	+ selectYear
	+ "&settleType=" + ((selectSettleType && selectSettleType != -1) ? selectSettleType : '')
	+ "&userId=" + (userId ? userId : '') 
	+ "&deptId=" + (deptId ? deptId : '')
	+ "&temp=" + Math.random();
	renderTable(url);
}

function getEXportData() {
	var header_tr = $($("#receivableAccount").next().find(".layui-table-header")[0]).find("tr");
	var body_tr = $($("#receivableAccount").next().find(".layui-table-body")[0]).find("tr");
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
    $("#receivableAccount tr").remove();// 清除之前的doom结构
    $("#receivableAccount").append(header_html).append(body_html);
    $("#receivableAccount").hide();
}