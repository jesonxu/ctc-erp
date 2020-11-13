queryData();

function queryData() {
	layui.use(['layer'], function () {
		var loading = layer.load(2);
		$.ajaxSettings.async = true;
		$.post('/manageConsole/queryComGroupReportData.action?year=' + $('#year').val() + 'temp=' + Math.random(), function (data, status) {
			if (data.msg == 'success') {
				if (typeof data.data != 'undefined') {
					if (typeof data.data != 'object') {
						data.msg = JSON.stringify(data.data);
					}
					init(data.data);
				}
			}
			layer.close(loading);
		});
		$.ajaxSettings.async = false;
	});
}

//处理千分位
function thousand(num, data) {
    if (num === null || num === undefined) {
        return '-';
    }
    if (data && data.projectName == '毛利率' && typeof num == 'number') {
    	return num.toFixed(2) + '%';
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

function init(data) {
	var titleName = $('#tableTitel').text();
	var sumProfit = 0;
	var sumIncome = 0;
	var currentYear = new Date().getFullYear();
    layui.use(['element', 'upload', 'laydate', 'table', 'form'], function () {
        var element = layui.element
            , table = layui.table,
            laypage = layui.laypage,
            form = layui.form,
            upload = layui.upload,
            laydate = layui.laydate;

        //主页面数据
        table.render({
            elem: '#qua_standard_table',
            method: 'POST',
            limit: Number.MAX_VALUE,
            data: data,
            toolbar:'#toolbarDemo',
            defaultToolbar: ['print'],
            printTitle: {
            	name: titleName,
            	colspan: 16
            },
            cols: [[
                {align: 'center', colspan: 16, hide: true}
            ],
                [
                	{
                	    align: 'center',
                	    colspan: 1,
                	    field: 'projectName',
                	    title: '项',
                	    width: 100,
                	    unresize: true
                	},
                	{
                	    align: 'center',
                	    colspan: 1,
                	    field: 'projectName',
                	    title: '目',
                	    width: 100,
                	    unresize: true
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '1月',
                	    templet: function(data){
                	        return thousand(data.monthData[0], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '2月',
                	    templet: function(data){
                	        return thousand(data.monthData[1], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '3月',
                	    templet: function(data){
                	        return thousand(data.monthData[2], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '4月',
                	    templet: function(data){
                	        return thousand(data.monthData[3], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '5月',
                	    templet: function(data){
                	        return thousand(data.monthData[4], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '6月',
                	    templet: function(data){
                	        return thousand(data.monthData[5], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '7月',
                	    templet: function(data){
                	        return thousand(data.monthData[6], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '8月',
                	    templet: function(data){
                	        return thousand(data.monthData[7], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '9月',
                	    templet: function(data){
                	        return thousand(data.monthData[8], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '10月',
                	    templet: function(data){
                	        return thousand(data.monthData[9], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '11月',
                	    templet: function(data){
                	        return thousand(data.monthData[10], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'monthData',
                	    title: '12月',
                	    templet: function(data){
                	        return thousand(data.monthData[11], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'total',
                	    title: '合计',
                	    templet: function(data){
                	    	if (data && data.projectName == '收入小计') {
                	    		sumIncome = data.total;
                	    	} else if (data && data.projectName == '毛利') {
                	    		sumProfit = data.total;
                	    	} else if (data && data.projectName == '毛利率') {
                	    		if (sumIncome > 0) {
                	    			data.total = (sumProfit / sumIncome) * 100;
                	    		} else {
                	    			data.total = '-';
                	    		}
                	    	}
                	        return thousand(data.total, data);
                	    }
                	}
                ]
            ], done: function (res, curr, count) {
                merge();
                $('th[data-key="1-1-0"] div').attr('align', 'right');
                $('th[data-key="1-1-1"] div').attr('align', 'left');
            }
        });
    });
}

function merge() {
	$('.layui-table-main .layui-table tr').each(function () {
		var firstEle = $(this).find('td[data-key="1-1-0"]');
		var secondEle = $(this).find('td[data-key="1-1-1"]');
		if (firstEle.find('div').text().indexOf('-') >= 0) {
			firstEle.find('div').text(firstEle.find('div').text().split('-')[0]);
			secondEle.find('div').text(secondEle.find('div').text().split('-')[1]);
		} else {
			firstEle.attr('colspan', 2);
			secondEle.css('display', 'none');
		}
	});
	var ele = null;
	$('.layui-table-main .layui-table tr td[data-key="1-1-0"]').each(function () {
		if (ele === null) {
			ele = $(this);
			return;
		}
		if (ele.find('div').text() == $(this).find('div').text()) {
			var rowspan = parseInt(ele.attr('rowspan') + '');
			if (!rowspan) {
				rowspan = 1;
			}
			ele.attr('rowspan', rowspan + 1);
			$(this).css('display', 'none');
		} else {
			ele = $(this);
			return;
		}
	});
}

