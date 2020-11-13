layui.use(['laydate', 'layer', 'element'], function () {
	
	$('.xm-select-title').css('height', '28px');
	
	var laydate = layui.laydate,
		layer = layui.layer,
		element = layui.element,
		formSelects = layui.formSelects;
		selectType = [0],
		selectYearMonth = new Date().Format('yyyy-MM');
	
	var currentMonth = new Date().Format('yyyy年MM月');
	var tableIndex = 0;
	var sectionArr = [{
		start: '',
		end: '5%'
	}, {
		start: '5%',
		end: '10%'
	}, {
		start: '10%',
		end: '15%'
	}, {
		start: '15%',
		end: '20%'
	}, {
		start: '20%',
		end: '25%'
	}, {
		start: '25%',
		end: '30%'
	}, {
		start: '30%',
		end: '35%'
	}, {
		start: '35%',
		end: '40%'
	}, {
		start: '40%',
		end: '45%'
	}, {
		start: '45%',
		end: '50%'
	}, {
		start: '50%',
		end: ''
	}];
	
	var selectDate = laydate.render({
        elem: '#selectDate',
        type: 'month',
        trigger: 'click',
        value: currentMonth,
        max: currentMonth, // 最大值为今年今月
        format: "yyyy年MM月",
        done: function (value, date) {
        	selectYearMonth = value.replace('年', '-').replace('月', '');
        	init();
        	layer.msg('查询完成');
        }
    });
	
	// 产品类型
	formSelects.on('productType', function(id, vals, val, isAdd, isDisabled){
		if (vals && vals.length > 0) {
			selectType = [];
			var fontLength = 0;
			for (var i = 0; i < vals.length; i++) {
				if (vals[i].name) {
					fontLength += vals[i].name.length;
				}
			}
			var totalWidth = 14 * fontLength + vals.length * 32 + 6 * (vals.length - 1) + 35;
			$('.xm-select-parent').css('width', totalWidth + 'px');
		}
	    return true;   
	}, true);
	
	var mutationObserver = new MutationObserver(function callback(mutationsList, observer) { // 回调事件
		if (!$('.xm-form-select').hasClass('xm-form-selected')) {
			selectType = formSelects.value('productType', 'val');
			var flag = true;
			for (var i = 0; i < selectType; i++) {
				if (selectType[i] == '-1') {
					selectType = [];
					flag = false;
					break;
				} else if (flag) {
					selectType.push(selectType[i]);
				}
			}
			init();
        	layer.msg('查询完成');
		}
	});
	mutationObserver.observe($('.xm-form-select')[0],  { // options：监听的属性
		attributes: true, 
		childList: false,
		subtree: false,
		attributeOldValue: false
	});
	
	var mutationObserver2 = new MutationObserver(function callback(mutationsList, observer) { // 回调事件
		if (!$('.xm-form-select').hasClass('xm-form-selected')) {
			selectType = formSelects.value('productType', 'val');
			for (var i = 0; i < selectType; i++) {
				if (selectType[i] == '-1') {
					selectType = [];
					break;
				} else if (flag) {
					selectType.push(selectType[i]);
				}
			}
			init();
        	layer.msg('查询完成');
		}
	});
	mutationObserver2.observe($('.xm-input')[0],  { // options：监听的属性
		attributes: true, 
		childList: false,
		subtree: false,
		attributeOldValue: false
	});
	
	initCreateAffiliatedTable();
	init(new Date().Format('yyyy-MM'));
	
	function init() {
		initMainTable();
		initGetSmallCustCount();
		initNegativeTable();
		initSectionTable();
		initBindEvent();
	}
	
	// 导出
	function initBindEvent() {
		$('.layui-icon-export').unbind().bind('click', function () {
			Table2Excel.extend(function(cell, cellText) {
				return {
					t: 'text',
				    v: cellText,
				};
				return null;
			});
			var table2excel = new Table2Excel();
		   	table2excel.export($(this).parents('.table-container').next().find('table'), '财务经营权责表_table');
		});
	}
	
	function initCreateAffiliatedTable() {
		$(sectionArr).each(function (i, item) {
			var ele = $('#affiliatedTableTmp > div').clone(true);
			ele.find('table').attr('index', i);
			var arr = [];
			if (item.start && item.end) {
				arr.push('在' + item.start + '~' + item.end + '之间');
			} else if (item.end) {
				arr.push('低于' + item.end);
			} else if (item.start) {
				arr.push('高于' + item.start);
			}
			ele.find('.affiliated-table-container').html('<div><span>附表1-' + (2 + i) 
					+ '：毛利率' + arr.join('且') + '，业绩靠前客户明细（总客户量</span><span class="report-table-cust-count">0</span><span>）</span>'
					+ '<div class="layui-inline report-table-export" title="导出"><i class="layui-icon layui-icon-export"></i></div></div>');
			$('#affiliatedTable').append(ele.prop('outerHTML'));
			
			$('#affiliatedTable > div').each(function (i, item) {
				var width = $(this).find('.affiliated-table-container>div').width();
				$(this).find('.affiliated-table-container>div').css('margin-left', '-' + width / 2 + 'px');
			});
		});
	}
	
	// 主表
	function initMainTable() {
		var productType = !selectType || selectType.length == 0 ? '' : selectType.join(',');
		$.ajax({
            type: "POST",
            async: true,
            url: "/financialOperateReport/getEverySectionRateOverview?temp=" + Math.random(),
            dataType: 'json',
            data: {
            	date: selectYearMonth,
            	productType: productType == -1 ? '' : productType
            },
            success: function (result) {
            	if (result && result.code == 200 
            			&& result.data && result.data.length > 0) {
            		$('#mainTable tbody').html('');
            		var ele = $('#mainTable tbody');
            		var totalReceive = 0;
            		var totalGrossProfit = 0;
            		var totalCustCount = 0;
            		var top3IndexReceive = getTop3Index(result.data, 'receive');
            		var top3IndexGrossPorift = getTop3Index(result.data, 'grossProfit');
            		var top3IndexCustCount = getTop3Index(result.data, 'custCount');
            		$(result.data).each(function (i, item) {
            			totalReceive += item.receive;
            			totalGrossProfit += item.grossProfit;
            			totalCustCount += item.custCount;
            			ele.append('<tr><td align="left">' + item.rateSection + '</td>' // 毛利区间 
    							+ '<td align="right">' + toPercent(item.sectionReceiveRate, 2) + '</td>' // 区间收入占比
    							+ '<td align="right"' + (top3IndexReceive.indexOf(i) >= 0 ? 'style="color: red;"' : '') + '>' + thousand(item.receive.toFixed(2)) + '</td>' // 收入
    							+ '<td align="right"' + (top3IndexGrossPorift.indexOf(i) >= 0 ? 'style="color: red;"' : '') + '>' + thousand(item.grossProfit.toFixed(2)) + '</td>' // 毛利
    							+ '<td align="right">' + toPercent(item.sectionGrossProfitRate, 2) + '</td>' // 区间毛利占比
    							+ '<td align="right"' + (top3IndexCustCount.indexOf(i) >= 0 ? 'style="color: red;"' : '') + '>' + thousand(item.custCount) + '</td></tr>'); // 客户量
            		});
            		ele.append('<tr>'
            				+ '<td align="right"></td>'
            				+ '<td align="right">100%</td>'
            				+ '<td align="right">'+ thousand(totalReceive.toFixed(2)) + '</td>'
            				+ '<td align="right">'+ thousand(totalGrossProfit.toFixed(2)) + '</td>'
            				+ '<td align="right">100%</td>'
            				+ '<td align="right">'+ thousand(totalCustCount) + '</td>');
            	} else { // 暂无数据
            		$('#mainTable tbody').html('<tr><td colspan="6" align="center">暂无数据</td></tr>');
            	}
            }
		});
	}
	
	function initGetSmallCustCount() {
		var productType = !selectType || selectType.length == 0 ? '' : selectType.join(',');
		$.ajax({
            type: "POST",
            async: true,
            url: "/financialOperateReport/getSmallCustCount?temp=" + Math.random(),
            dataType: 'json',
            data: {
            	date: selectYearMonth,
            	productType: productType == -1 ? '' : productType
            },
            success: function (result) {
            	if (result && result.code == 200) {
            		$('#smallCustCount').html(result.data);
            	}
            }
		});
	}
	
	function getTop3Index(data, key) {
		var indexArr = [];
		for (var i = 0; i < data.length; i++) {
			var item = data[i];
			if (indexArr.length < 3) {
				indexArr.push({
					index: i,
					value: item[key]
				});
			} else if (indexArr[indexArr.length - 1].value < item[key]) {
				indexArr[indexArr.length - 1] = {
					index: i,
					value: item[key]
				};
				indexArr.sort(function (index1, index2) {
					return index2['value'] - index1['value'];
				});
			}
		}
		for (var i = 0; i < indexArr.length; i++) {
			indexArr[i] = indexArr[i]['index'];
		}
		return indexArr;
	}
	
	// 负的
	function initNegativeTable() {
		var productType = !selectType || selectType.length == 0 ? '' : selectType.join(',');
		$.ajax({
            type: "POST",
            async: true,
            url: "/financialOperateReport/getNegativeGrossProfitCusts?temp=" + Math.random(),
            dataType: 'json',
            data: {
            	date: selectYearMonth,
            	productType: productType == -1 ? '' : productType
            },
            success: function (result) {
            	if (result && result.code == 200 
            			&& result.data && result.data.count > 0) {
            		$('#affiliatedTable .affiliated-table-container:first .report-table-cust-count').html(result.data.count);
            		var ele = $('#negativeTable tbody');
            		ele.html('');
            		$(result.data.content).each(function (i, item) {
            			ele.append('<tr><td align="' + (item.customerName == '合计' ? 'center' : 'left') + '">' + item.customerName + '</td>' // 客户
    							+ '<td align="right">' + thousand(item.receive.toFixed(2)) + '</td>' // 收入
    							+ '<td align="right">' + thousand(item.grossProfit.toFixed(2)) + '</td></tr>'); // 毛利
            		});
            	} else { // 暂无数据
            		$('#negativeTable tbody').html('<tr><td colspan="6" align="center">暂无数据</td></tr>');
            	}
            }
		});
	}
	
	// 各个阶段的
	function initSectionTable() {
		var productType = !selectType || selectType.length == 0 ? '' : selectType.join(',');
		if (tableIndex >= sectionArr.length) {
			tableIndex = 0;
			return;
		}
		$.ajax({
            type: "POST",
            async: true,
            url: "/financialOperateReport/getSectionMainCusts?temp=" + Math.random(),
            dataType: 'json',
            data: {
            	date: selectYearMonth,
            	index: tableIndex,
            	productType: productType == -1 ? '' : productType
            },
            success: function (result) {
            	if (result && result.code == 200 
            			&& result.data && result.data.count > 0) {
            		var divEle = $('#affiliatedTable>div:eq(' + (tableIndex + 1) +')');
            		divEle.find('.affiliated-table-container .report-table-cust-count').html(result.data.count);
            		var ele = divEle.find('.report-table tbody');
            		ele.html('');
            		$(result.data.content).each(function (i, item) {
            			if (item.customerName == '对应区间占比') {
            				ele.append('<tr style="color: red;"><td align="center">' + item.customerName + '</td>' // 客户
            						+ '<td align="right">' + toPercent(item.receive, 2) + '</td>' // 收入
            						+ '<td align="right">' + toPercent(item.grossProfit, 2) + '</td>' // 毛利
            						+ '<td align="right">-</td></tr>'); // 毛利率
            			} else {
            				ele.append('<tr><td align="' + (item.customerName == '合计' ? 'center' : 'left') + '">' + item.customerName + '</td>' // 客户
            						+ '<td align="right">' + thousand(item.receive.toFixed(2)) + '</td>' // 收入
            						+ '<td align="right">' + thousand(item.grossProfit.toFixed(2)) + '</td>' // 毛利
            						+ '<td align="right">' + toPercent(item.grossProfitRate, 2) + '</td></tr>'); // 毛利率
            			}
            		});
            	} else { // 暂无数据
            		$('#affiliatedTable>div:eq(' + (tableIndex + 1) +') .report-table tbody').html('<tr><td colspan="6" align="center">暂无数据</td></tr>');
            	}
            	tableIndex++;
            	initSectionTable(selectDate);
            },
            error: function () {
            	tableIndex++;
            	initSectionTable(selectDate);
            }
		});
	}
	
});