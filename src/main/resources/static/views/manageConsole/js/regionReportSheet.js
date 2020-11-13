var regionRowSpanNum = 4;
var yearRowSpanNum = 4;

$(document).ready(function () {
	queryData();
});

function queryData() {
	layui.use(['layer'], function () {
		var loading = layer.load(2);
		$.ajaxSettings.async = true;
		$.post("/manageConsole/queryRegionReportData", function (data, status) {
			if (data.msg == 'success') {
				if (typeof data.data != 'undefined') {
					if (typeof data.data != 'object') {
						data.msg = JSON.stringify(data.data);
					}
					loadData(data.data);
				}
			}
			layer.close(loading);
		});
		$.ajaxSettings.async = false;
	});
}

function loadData(tableData) {
	var regions = ['上海', '北京', '华南'];
	var titles = {receivables: 0, cost: 1, grossProfit: 2, grossProfitRatio: 3};
	var titleRelations = ['收入', '成本', '毛利', '毛利率'];
	var datas = [];
	var sumData = {};
	var singleGroupData = [];
	for (var region in tableData) {
		var regionIndex = -1;
		$(regions).each(function (index, item) {
			if (regionIndex != -1) {
				return;
			}
			if (item == region) {
				regionIndex = index;
			}
		});
		if (regionIndex == -1) {
			regionIndex = regions.length;
			regions.push(region);
		}
		for (var year in tableData[region]) {
			if (datas[regionIndex] === undefined) {
				datas[regionIndex] = {};
			}
			if (datas[regionIndex][year] == undefined) {
				datas[regionIndex][year] = [];
			}
			if (sumData[year] == undefined) {
				sumData[year] = [];
			}
			$(tableData[region][year]).each(function (i, it) {
				for (var title in it) {
					var titleIndex = -1;
					if (titles[title] !== undefined) {
						titleIndex = titles[title];
					}
					if (titleIndex >= 0) {
						if (typeof datas[regionIndex][year][titleIndex] != 'object') {
							datas[regionIndex][year][titleIndex] = {regionName: region, year: year, month: titleRelations[titleIndex]};
						}
						if (typeof sumData[year][titleIndex] != 'object') {
							sumData[year][titleIndex] = {regionName: '汇总', year: year, month: titleRelations[titleIndex]};
						}
						datas[regionIndex][year][titleIndex]['month' + (i + 1)] = it[title];
						if (titleIndex != 3 && it[title] !== undefined) {
							if (sumData[year][titleIndex]['month' + (i + 1)] !== undefined) {
								sumData[year][titleIndex]['month' + (i + 1)] = it[title];
							} else {
								sumData[year][titleIndex]['month' + (i + 1)] += it[title];
							}
						}
					}
				}
			});
			
			var sumReceivables = 0;
			var sumCost = 0;
			var sumGrossProfit = 0;
			for (var i = 1; i <= 12; i++) {
				if (datas[regionIndex][year] !== undefined) {
					if (datas[regionIndex][year][0] !== undefined && datas[regionIndex][year][0]['month' + i] !== undefined) {
						sumReceivables += datas[regionIndex][year][0]['month' + i];
					}
					if (datas[regionIndex][year][1] !== undefined && datas[regionIndex][year][1]['month' + i] !== undefined) {
						sumCost += datas[regionIndex][year][1]['month' + i];
					}
					if (datas[regionIndex][year][2] !== undefined && datas[regionIndex][year][2]['month' + i] !== undefined) {
						sumGrossProfit += datas[regionIndex][year][2]['month' + i];
					}
				}
			}
			if (datas[regionIndex][year] !== undefined) {
				if (datas[regionIndex][year][0] !== undefined) {
					datas[regionIndex][year][0]['countMonth'] = sumReceivables;
				}
				if (datas[regionIndex][year][1] !== undefined) {
					datas[regionIndex][year][1]['countMonth'] = sumCost;
				}
				if (datas[regionIndex][year][2] !== undefined) {
					datas[regionIndex][year][2]['countMonth'] = sumGrossProfit;
				}
				if (datas[regionIndex][year][3] !== undefined) {
					datas[regionIndex][year][3]['countMonth'] = (sumReceivables !== undefined && sumReceivables !== null && sumReceivables > 0) ? 
							(((sumGrossProfit / sumReceivables) * 100).toFixed(2) + '%') : '-';
				}
			}
		}
	}
	
	$(datas).each(function (index, item) {
		for (var year in item) {
			mergeArray(singleGroupData, item[year]);
		}
	});
	
	var yearCount = 0;
	for (var year in sumData) {
		yearCount++;
		sumData[year][3] = {regionName: '汇总', year: year, month: '毛利率'};
		for (var monthKey in sumData[year][0]) {
			if (monthKey.length > 'month'.length && monthKey.indexOf('month') >=0) {
				var receivables = sumData[year][0][monthKey];
				var cost = sumData[year][1][monthKey];
				var grossProfit = sumData[year][2][monthKey];
				var grossProfitRatio = (receivables !== undefined && receivables !== null && receivables > 0) ? 
						(((grossProfit / receivables) * 100).toFixed(2) + '%') : '-';
				sumData[year][3][monthKey] = grossProfitRatio;
			}
		}
		
		var sumReceivables = null;
		var sumCost = null;
		var sumGrossProfit = null;
		for (var i = 1; i <= 12; i++) {
			var regSumReceivables = null;
			var regSumCost = null;
			var regSumGrossProfit = null;
			for (var j = 0; j < regions.length; j++) {
				if (datas[j]) {
					if (datas[j][year] !== undefined) {
						if (datas[j][year][0] !== undefined && datas[j][year][0]['month' + i] !== undefined) {
							if (regSumReceivables === null) {
								regSumReceivables = 0;
							}
							if (sumReceivables === null) {
								sumReceivables = 0;
							}
							regSumReceivables += datas[j][year][0]['month' + i];
							sumReceivables += datas[j][year][0]['month' + i];
						}
						if (datas[j][year][1] !== undefined && datas[j][year][1]['month' + i] !== undefined) {
							if (regSumCost === null) {
								regSumCost = 0;
							}
							if (sumCost === null) {
								sumCost = 0;
							}
							regSumCost += datas[j][year][1]['month' + i];
							sumCost += datas[j][year][1]['month' + i];
						}
						if (datas[j][year][2] !== undefined && datas[j][year][2]['month' + i] !== undefined) {
							if (regSumGrossProfit === null) {
								regSumGrossProfit = 0;
							}
							if (sumGrossProfit === null) {
								sumGrossProfit = 0;
							}
							regSumGrossProfit += datas[j][year][2]['month' + i];
							sumGrossProfit += datas[j][year][2]['month' + i];
						}
					}
				}
			}
			if (sumData[year] !== undefined) {
				if (sumData[year][0] !== undefined) {
					sumData[year][0]['month' + i] = regSumReceivables;
				}
				if (sumData[year][1] !== undefined) {
					sumData[year][1]['month' + i] = regSumCost;
				}
				if (sumData[year][2] !== undefined) {
					sumData[year][2]['month' + i] = regSumGrossProfit;
				}
			}
		}
		if (sumData[year] !== undefined) {
			if (sumData[year][0] !== undefined) {
				sumData[year][0]['countMonth'] = sumReceivables;
			}
			if (sumData[year][1] !== undefined) {
				sumData[year][1]['countMonth'] = sumCost;
			}
			if (sumData[year][2] !== undefined) {
				sumData[year][2]['countMonth'] = sumGrossProfit;
			}
			if (sumData[year][3] !== undefined) {
				sumData[year][3]['countMonth'] = (sumReceivables !== undefined && sumReceivables !== null && sumReceivables > 0) ? 
						(((sumGrossProfit / sumReceivables) * 100).toFixed(2) + '%') : '-';
			}
		}
				
		mergeArray(singleGroupData, sumData[year]);
	}
	
	regionRowSpanNum = regionRowSpanNum * yearCount;
	
	console.log(singleGroupData);
	
	renderTable(singleGroupData);
}

function mergeArray(array1, array2) {
	$(array2).each(function (index, item) {
		array1.push(item);
	});
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


function renderTable(datas) {
	layui.use(['element', 'upload', 'laydate', 'table', 'form'], function() {
		var element = layui.element,
		table = layui.table,
		laypage = layui.laypage,
		form = layui.form,
		upload = layui.upload,
		laydate = layui.laydate;
		
		var fontSize = 'font-size: 12px;';
		
		//主页面数据
		table.render({
			elem: '#qua_standard_table',
			id: 'qua_standard_table',
			//url:'',
			data: datas,
			method: 'POST',
			title: '数据表',
			limit: Number.MAX_VALUE,
			toolbar: '#toolbarDemo',
			defaultToolbar: ['print'],
			printTitle: {
				name: '各大区域经营状况表',
				colspan: 16
			},
			cols: [
				[
					{
						align: 'center',
						style: fontSize,
						rowspan: regionRowSpanNum,
						field: 'regionName',
						title: '区域',
						width: 90
					},
					{
						align: 'center',
						style: fontSize,
						rowspan: yearRowSpanNum,
						field: 'year',
						title: '年度',
						width: 90
					},
					{
						align: 'center',
						style: fontSize,
						rowspan: 1,
						field: 'month',
						title: '月份',
						width: 100
						
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month1',
						title: '1月',
						templet: function (data) {
							return thousand(data.month1);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month2',
						title: '2月',
						templet: function (data) {
							return thousand(data.month2);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month3',
						title: '3月',
						templet: function (data) {
							return thousand(data.month3);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month4',
						title: '4月',
						templet: function (data) {
							return thousand(data.month4);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month5',
						title: '5月',
						templet: function (data) {
							return thousand(data.month5);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month6',
						title: '6月',
						templet: function (data) {
							return thousand(data.month6);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month7',
						title: '7月',
						templet: function (data) {
							return thousand(data.month7);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month8',
						title: '8月',
						templet: function (data) {
							return thousand(data.month8);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month9',
						title: '9月',
						templet: function (data) {
							return thousand(data.month9);
						}
					},
					{
						align: 'right',
						style: fontSize,
						rowspan: 1,
						field: 'month10',
						title: '10月',
						templet: function (data) {
							return thousand(data.month10);
						}
					},
					{
						align: 'center',
						style: fontSize,
						rowspan: 1,
						field: 'month11',
						title: '11月',
						align: 'right',
						templet: function (data) {
							return thousand(data.month11);
						}
					},
					{
						align: 'center',
						style: fontSize,
						rowspan: 1,
						field: 'month12',
						title: '12月',
						align: 'right',
						templet: function (data) {
							return thousand(data.month12);
						}
					},
					{
						align: 'center',
						style: fontSize,
						rowspan: 1,
						field: 'countMonth',
						title: '合计',
						align: 'right',
						width: 140,
						templet: function (data) {
							return thousand(data.countMonth);
						}
					}]
				],
				done: function(res, curr, count) {
					element.init();
					layui.$('#qua_standard_table').siblings('div').find('dl').find('.layui-this').click(); //模拟点击 初始化数据
					merge(res); //合并单元格
				}
		});
		
	});
}

//合并开始
function merge(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var _number = 1; //保持序号列数字递增
    var columsName = ['regionName', 'year', 'month', 'month1', 'month2', 'month3', 'month4', 'month5', 'month6', 'month7', 'month8', 'month9', 'month10', 'month11', 'month12', 'countMonth']; //需要合并的列名称
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

                if (data[i][columsName[k]] === data[i - 1][columsName[k]] 
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