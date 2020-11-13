init();

function init() {
	var titleName = '通信集团近年经营报表（集团/大区）';
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
            url: '/group/yearReportForm.action?temp=' + Math.random(),
            method: 'POST',
            cellMinWidth: 30,
            limit: 30,
            toolbar: '#toolbarDemo',
            defaultToolbar: ['print'],
            printTitle: {
            	name: titleName,
            	colspan: 7
            },
            cols: [[
                {align: 'center', colspan: 6, title: '大汉三通通信集团近三年经营报表（集团/大区）', hide: true}
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
                	    field: 'before3rdYear',
                	    title: '<span year="' + (currentYear - 3) + '">' + (currentYear - 3 + '年') + '</span>',
                	    templet: function (data) {
                	        return thousand(data.yearData[3], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'before2rdYear',
                	    title: '<span year="' + (currentYear - 2) + '">' + (currentYear - 2 + '年') + '</span>',
                	    templet: function (data) {
                	        return thousand(data.yearData[2], data);
                	    } 
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'before1rdYear',
                	    title: '<span year="' + (currentYear - 1) + '">' + (currentYear - 1 + '年') + '</span>',
                	    templet: function (data) {
                	        return thousand(data.yearData[1], data);
                	    }
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'thisYear',
                	    title: '<span year="' + currentYear + '">' + (currentYear + '年') + '</span>',
                	    templet: function (data) {
                	        return thousand(data.yearData[0], data);
                	    } 
                	},
                	{
                	    align: 'right',
                	    colspan: 1,
                	    field: 'fluctuation',
                	    title: '同比变动',
                	    hide: true
                	}
                ]
            ], done: function (res, curr, count) {
                merge();
                $('th[data-key="1-1-0"] div').attr('align', 'right');
                $('th[data-key="1-1-1"] div').attr('align', 'left');
                
                $('span[year]').each(function (index, item) {
                	$(item).unbind().bind('click', function () {
                		var year = $(this).attr('year');
                		window.open("/manageConsole/toComGroupReport.action?year=" + year + "&temp=" + Math.random());
                	});
                });
            }
        });
    });
}

//处理千分位
function thousand(num, data) {
	if (data && data.projectName == '毛利率') {
    	return num.toFixed(2) + '%';
    }
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
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
