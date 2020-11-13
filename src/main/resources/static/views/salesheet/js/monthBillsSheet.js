var layer;
var element;
var form;
var table;
var params;
var loadingIndex;
var sale_statistic_year;
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
        var now = new Date();
        var month = (now.getMonth() + 1) + "";
        if (month.length === 1) {
            month = "0" + month;
        }
        var yearMonth = now.getFullYear() + "-" + month;
        loadSaleRoyalty(sale_statistic_year, getLastMonth());
    });
}

function buildTableData(info, rowArr) {
	rowArr.push({
		col1: '账单编号',
		col2: info.billsNumber
	});
	rowArr.push({
		col1: '产品',
		col2: info.productName
	});
	rowArr.push({
		col1: '应收总额',
		col2: info.receivables.toFixed(2)
	});
	rowArr.push({
		col1: '账单日期',
		col2: info.billsDate ? info.billsDate : ''
	});
	rowArr.push({
		col1: '收款截止日',
		col2: info.finalReceiveTime ? info.finalReceiveTime : ''
	});
	rowArr.push({
		col1: '销账日期',
		col2: info.writeOffTime ? info.writeOffTime : '-'
	});
	rowArr.push({
		col1: '账单状态',
		col2: info.billStatus ? info.billStatus : ''
	});
	rowArr.push({
		col1: '逾期天数',
		col2: info.penaltyInterestDays ? info.penaltyInterestDays : 0
	});
	rowArr.push({
		col1: '逾期罚息',
		col2: info.penaltyInterest.toFixed(2) ? info.penaltyInterestDays : '0.00'
	});
	rowArr.push({
		col1: '',
		col2: '',
		merge: {
			mergeCol: 'col1',
			mergeCount: 2
		}
	});
	
	if (info.receiveInfo && info.receiveInfo.length > 0) {
		rowArr.push({
			col1: '到款日期',
			col2: '金额'
		});
		$(info.receiveInfo).each(function (index, item) {
			rowArr.push({
				col1: item.receiveTime,
				col2: item.receive ? item.receive.toFixed(2) : '0.00'
			});
		});
		
		rowArr.push({
			col1: '',
			col2: '',
			merge: {
				mergeCol: 'col1',
				mergeCount: 2
			}
		});
	}
	
	rowArr.push({
		col1: '',
		col2: '',
		merge: {
			mergeCol: 'col1',
			mergeCount: 2,
			borderTop: true
		}
	});
	return rowArr;
}

function buildTableTh(info, hidden) {
	return [
        [
        	{
        	    align: 'right',
        	    colspan: 1,
        	    field: 'col1',
        	    title: '',
        	    hide: hidden,
        	    templet: function (data) {
        	    	return toMergeSpan(data.col1, 'col1', data.merge);
        	    } 
        	},
        	{
        	    align: 'right',
        	    colspan: 1,
        	    field: 'col2',
        	    title: '',
        	    hide: hidden,
        	    templet: function (data) {
        	    	return toMergeSpan(thousand(data.col2), 'col2', data.merge);
        	    } 
        	}]
    ]
}

function toMergeSpan(value, colName, merge) {
	if (merge && merge.mergeCol == colName) {
		return '<span class="merge-col" value="' + merge.mergeCount + '"' + (merge.borderTop ? 'bordertop="true"' : '') +'>' + value + '</span>';
	}
	return value;
}

function mergeCol() {
	$('.merge-col').each(function () {
		$(this).parents('td').attr({colspan: $(this).attr('value'), align: 'center'});
		if ($(this).attr('bordertop')) {
			$(this).parents('tr').prev().find('td').css('border-bottom', '0');
		}
		var ele = $(this).parents('td');
		for (var i = 1; i < $(this).attr('value'); i++) {
			ele.next().css('display', 'none');
			ele = ele.next();
		}
	});
}

function getDateDiff(startDate, endDate){
	var startTime = null;
	if (typeof startDate === 'string') {
		if (startDate.length == 10) {
			startDate += ' 23:59:59';
		}
		startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();
	} else {
		startTime = startDate;
	}
	var endTime = null;
	if (typeof endDate === 'string') {
		if (endDate.length == 10) {
			endDate += ' 23:59:59';
		}
		endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();
	} else {
		endTime = endDate;
	}
    var dates = Math.ceil((startTime - endTime) / (1000 * 60 * 60 * 24));
    return dates;
}
var currentPageNum = 0;
function initRoyaltyTable(id, data, yearMonth, index) {
	var year = yearMonth.substring(0, yearMonth.indexOf("-"));
    var month = yearMonth.substring(yearMonth.indexOf("-") + 1);
    var time = year + "年" + month + "月";
    var fontSize = 'font-size: 12px;';
    var cols = null;
    if (data && data.length > 0) {
    	cols = buildTableTh(data[0], false);
    } else {
    	cols = buildTableTh({}, true);
    }
    var rowData = [];
    if (data && data.length > 0) {
    	$(data).each(function (index, item) {
    		buildTableData(item, rowData);
    	});
    }
    var height = '';
    if (sale_royalty_show_date_type === 4) {
        height = 'full-40';
    }
    table.render({
        elem: id,
        toolbar: "#toolbarDemo",
        defaultToolbar:["print", "exports"],
        height: height,
        limit: Number.MAX_VALUE,
        cols: cols,
        data: rowData,
        done: function () {
        	$('#yearSelect').html(time);
            initChangeYear(id, data, time);
            layer.close(loadingIndex);
            mergeCol();
        }
    });
}

function appendPageTool(id, data, yearMonth, ele) {
	if (!ele) {
		ele = $('.page-tool-clone').clone();
	}
	ele.css({'display': '', 
		'display': 'none',
		'background-color': 'rgb(242, 242, 242)', 
		'padding': '0 0 4px 14px', 
		'border-bottom': '1px solid rgb(230, 230, 230)'});
	$(ele).find('.button-pre').unbind().click(function () {
		if (data && data.length > 0) {
			currentPageNum -= 1;
			currentPageNum = currentPageNum < 0 ? 0 : currentPageNum;
			initRoyaltyTable(id, data, yearMonth, currentPageNum);
			appendPageTool(id, data, yearMonth, ele.clone());
		}
	});
	$(ele).find('.button-next').unbind().click(function () {
		if (data && data.length > 0) {
			currentPageNum += 1;
			currentPageNum = currentPageNum > (data.length - 1) ? (data.length - 1) : currentPageNum;
			initRoyaltyTable(id, data, yearMonth, currentPageNum);
			appendPageTool(id, data, yearMonth, ele.clone());
		}
	});
	$(ele).find('.button-all').unbind().click(function () {
		if (data && data.length > 0) {
			initRoyaltyTable(id, data, yearMonth, -1);
			var newEle = ele.clone();
		}
	});
	$(ele).find('.button-page').unbind().click(function () {
		if (data && data.length > 0) {
			currentPageNum = 0;
			initRoyaltyTable(id, data, yearMonth, currentPageNum);
			var newEle = ele.clone();
			$(newEle).find('.button-pre').css('display', '');
			$(newEle).find('.button-next').css('display', '');
			$(newEle).find('.button-page').css('display', 'none');
			$(newEle).find('.button-all').css('display', '');
			appendPageTool(id, data, yearMonth, newEle);
		}
	});
	$('.layui-table-tool').after(ele);
}

function getLastMonth(){
    var nowDate = new Date();
    var year = nowDate.getFullYear();
    var month = nowDate.getMonth();
    if(month == 0){
        month = 12;
        year = year - 1;
    }
    var lastDay = new Date(year,month,0);
    return (year + "-" + (month < 10 ? ('0' + month) : month));
}

function initChangeYear(id, data, yearMonth) {
	var d = new Date();
    laydate.render({
        elem: '#yearSelect',
        type: 'month',
        trigger: 'click',
        value: yearMonth,
        max: yearMonth,
        format: "yyyy年MM月",
        done: function (value, date) {
            $('#yearSelect').html(value);
            var year = value.substring(0, value.indexOf("年"));
            var month = value.substring(value.indexOf("年") + 1, value.length - 1);
            var time = year + "-" + month;
            loadSaleRoyalty(sale_statistic_year, time);
        }
    })
}

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

    $.ajaxSettings.async = true;
    $.post("/monthBills/getMonthBills", {
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
            	currentPageNum = 0;
            	initRoyaltyTable('#realRoyalty-' + year, data.data, yearMonth);
//                appendPageTool('#realRoyalty-' + year, data.data, yearMonth);
            }
        }
        layer.close(loading);
    });
    $.ajaxSettings.async = false;
}

function thousand(num) {
    if (!num) {
        return 0;
    }
    if ((num + '').indexOf('-') >= 0) {
    	return num;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

function setParams(data) {
    params = data;
}