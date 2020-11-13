var layer;
var element;
var form;
// 需要用到的参数
var params;
// 加载中遮罩
var loadingIndex;
var table;
var expense_income_year;
var import_file_md5_info = [];
var bindCustomterIndex;
// 是否销售，是销售的话可以看到到款情况和完成率
var isSale;

$(document).ready(function () {
    layui.use(['layer', 'form', 'element', "table"], function () {
        table = layui.table;
        layer = layui.layer;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        isSale = $('#isSale').val() === 'true';
        if (isSale) {
            $('#income-table-div').addClass('income-table-sale');
        }
        var year = new Date().getFullYear();
        bind_button(year);
        init_tool();
        init_expense_income_table(year);
    });
})

/**
 * 加载已经导入的表格数据
 */
function init_expense_income_table(year) {
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#expense-income',
        height: isSale ? 'full-230' : 'full-150',
        url: '/fsExpenseIncome/queryExpenseIncomePage.action',
        page: true,
        limit: 20,
        limits: [10, 20, 30, 50, 100],
        request: {
            pageName: 'page',
            limitName: 'pageSize'
        },
        where: {
            year: year,
            startTime: $("#startTime").val(),
            endTime: $("#endTime").val(),
            bankName: $("#bankName").val(),
            linkStatus: '0,1',
            from: ""
        },
        cols: [[{
            type: 'radio'
        }, {
            field: 'id',
            hide: true
        }, {
            field: 'operateTime',
            title: '到款时间',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'serialNumber',
            title: '流水号',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'bankName',
            title: '银行名称',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize
        }, {
            field: 'regionName',
            title: '区域',
            align: 'center',
            style: fontSize
        }, {
            field: 'feeType',
            title: '分类',
            align: 'center',
            style: fontSize
        }, {
            field: 'depict',
            title: '内容摘要',
            align: 'left',
            width: 300,
            style: fontSize
        }, {
            field: 'isIncome',
            title: '收入',
            align: 'right',
            templet: function (data) {
                if (data.isIncome === 0 || data.isIncome === '0'){
                    return thousand(data.cost);
                }else{
                    return "";
                }
            },
            style: fontSize
        }, {
            field: 'isIncome',
            title: '支出',
            align: 'right',
            templet: function (data) {
                if (data.isIncome === 1 || data.isIncome === '1'){
                    return thousand(data.cost);
                }else{
                    return "";
                }
            },
            style: fontSize
        }, {
            field: 'remainRelatedCost',
            title: '剩余消账金额',
            align: 'right',
            templet: function (data) {
                return thousand(data.remainRelatedCost);
            },
            style: fontSize
        }, {
            field: 'remark',
            title: '备注',
            align: 'center',
            style: fontSize
        }, {
            field: 'wTime',
            title: '导入时间',
            align: 'center',
            style: fontSize
        }, {
            field: 'relateStatus',
            title: '关联状态',
            align: 'center',
            style: fontSize,
            templet: function (row) {
                if (row.relateStatus == '1') {
                    return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已关联 </div>';
                } else if (row.relateStatus == '0') {
                    return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未关联 </div>';
                }
            }
        }, {
            field: 'customerName',
            title: '客户名称',
            align: 'center',
            width: 260,
            style: fontSize
        }, {
            field: 'settleType',
            title: '付费类型',
            align: 'center',
            width: 100,
            style: fontSize
        }]],
        done: function () {
            // 取消加载中遮罩
            layer.close(loadingIndex);
            if (isSale) {
                queryIncomeInfo();
                queryGoalCompletion();
            }
        }
    });

    table.on('row(expense-income)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });
}

// 查询到款和销账
function queryIncomeInfo() {
    $.post("/fsExpenseIncome/readSaleIncomeInfo", {
        startTime: $("#startTime").val(),
        endTime: $("#endTime").val(),
        timeType: $("#timeType").val()
    }, function (res) {
        if (res.code == 200) {
            var data = res.data;
            $('#totalIncome').text(data.totalIncome);
            $('#totalWriteOff').text(data.totalWriteOff);
            $('#totalBalance').text(data.totalBalance);
        }
    })
}

// 查询目标完成情况
function queryGoalCompletion() {
    var month = $("#goalMonth").text();
    if (isBlank(month)) {
        return;
    }
    $.post("/goal/readSaleGoalCompletion", {
        startTime: month,
        timeType: $("#timeType").val()
    }, function (res) {
        if (res.code == 200) {
            var data = res.data;
            $('#totalGoalReceivable').text(data.totalGoalReceivable);
            $('#totalActualReceivable').text(data.totalActualReceivable);
            $('#receivableRatio').text(data.receivableRatio);
            $('#totalGoalGrossProfit').text(data.totalGoalGrossProfit);
            $('#totalGrossProfit').text(data.totalGrossProfit);
            $('#grossProfitRatio').text(data.grossProfitRatio);
        }
    })
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

function setParams(data) {
    params = data;
}

var insStart;
var insEnd;

function init_tool() {
    var date = new Date();
    var year = date.getFullYear();
    var month = (date.getMonth() + 1) + "";
    if (month.length === 1) {
        month = "0" + month;
    }
    // 本月第一天日期
    var begin = year + "-" + month + "-01";
    $("#startTime").val(begin);
    var day = date.getDate() + "";
    if (day.length === 1) {
        day = "0" + day;
    }
    var end = year + "-" + month + "-" + day;
    $("#endTime").val(end);

    var min = year - 2 + '-01-01';
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        insStart = laydate.render({
            elem: '#startTime',
            format: 'yyyy-MM-dd',
            type: 'date',
            min: min,
            max: end,
            value:  $("#startTime").val(),
            trigger: 'click',
            done: function (value, date) {
                // 更新结束日期的最小日期
                insEnd.config.min = lay.extend({}, date, {
                    month: date.month - 1
                });
                // 自动弹出结束日期的选择器
                insEnd.config.elem[0].focus();
            }
        });

        insEnd = laydate.render({
            elem: '#endTime',
            min: min,
            max: end,
            value: $("#endTime").val(),
            trigger: 'click',
            done: function (value, date) {
                // 更新开始日期的最大日期
                insStart.config.max = lay.extend({}, date, {
                    month: date.month - 1
                });
            }
        });

        var nowMonth = year + '年' + month + '月';
        $('#goalMonth').text(nowMonth);
        laydate.render({
            elem: '#goalMonth',
            format: 'yyyy年MM月',
            type: 'month',
            min: min,
            max: end,
            value: nowMonth,
            trigger: 'click',
            done: function (value, date) {
                $('#goalMonth').text(value);
                queryGoalCompletion();
            }
        });
    });
}

// 按钮绑定
function bind_button(year) {
    // 刷新
    $("#refresh").unbind().click(function () {
        refresh();
    });

    // 重置
    $("#reset").unbind().click(function () {
        insStart="";
        insEnd = "";
        init_tool();
        $("#bankName").val("");
        $("#depict").val("");
        $("#timeType").val(1);
        $('#writeoffstatus_0').attr('checked', true);
        $('#writeoffstatus_2').attr('checked', false);
        $('#notLink').attr('checked', true);
        $('#linked').attr('checked', true);
        form.render();
        refresh();
    });

    // 导入
    $("#import").unbind().click(function () {
        layer.open({
            type: 2,
            area: ['700px', '450px'],
            fixed: false, //不固定
            maxmin: true,
            content: '/fsExpenseIncome/toUploadPage.action?year=' + year,
            cancel: function(index, layero){
                var md5arr = [];
                for (var fileIndex = 0; fileIndex < import_file_md5_info.length; fileIndex++) {
                    md5arr.push(import_file_md5_info[fileIndex].md5);
                }
                if (md5arr.length >0){
                    deleteUploadFileInfo(md5arr);
                }
                return true;
            }
        });
    });
    
    //导出
    $("#export").unbind().bind('click',function(){
    	var writeoffstatus = new Array();
    	var stateActive_0 = $("#writeoffstatus_0").attr('checked');
    	if(stateActive_0) {
    	    writeoffstatus.push($("#writeoffstatus_0").val())
    	}
    	var stateActive_2 = $("#writeoffstatus_2").attr('checked');
    	if(stateActive_2) {
    	    writeoffstatus.push($("#writeoffstatus_2").val())
    	}
    	if(writeoffstatus.length < 1) {
    		layer.msg("请选择消账状态！");
    		return false;
    	}
    	//内容描述
    	var depict = $('#depict').val();
    	//时间类型 1-到账时间 2-导入时间
    	var timeType = $('#timeType').val();
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
        var bankName = $("#bankName").val();
        $.ajax({
            type: "GET",
            url: "/fsExpenseIncome/exportFsExpenseIncome.action",
            data: {
            	year: year,
            	depict: depict,
            	timeType: timeType,
            	startTime: startTime,
            	endTime: endTime,
            	bankName: bankName,
            	writeoffstatus : writeoffstatus.join(','),
                from: ""
            },
            success: function (data) {
            	if(data.code == 500) {
            		layer.msg(data.msg);
            	}else if(data.code == 200){
            		down_load(data.data);
            	}
            }
        });

    });

  /*  // 关联账单
    $("#associate").unbind().click(function () {
        var checkStatus = table.checkStatus('expense-income');
        var checkedData = checkStatus.data;
        if (checkedData == null || checkedData.length === 0){
            layer.msg("请选择一条导入记录");
            return;
        }else if (checkedData.length >1){
            layer.msg("只能选择一条导入记录");
            return;
        }
        var id = checkedData[0].id;
        var from =  $("#from").val();
        layer.open({
            type: 2,
            area: ['940px', '560px'],
            fixed: false, //不固定
            maxmin: true,
            content: '/fsExpenseIncome/toAssociate.action?expenseIncomeId=' + id + '&from=' +from
        });
    });
    */
    // 关联客户
    $("#customerAssociate").unbind().click(function () {
        var checkStatus = table.checkStatus('expense-income');
        var checkedData = checkStatus.data;
        if (checkedData == null || checkedData.length === 0){
            layer.msg("请选择一条导入记录");
            return;
        } else if (checkedData.length > 1) {
            layer.msg("只能选择一条导入记录");
            return;
        } else if (checkedData.isIncome == 1) {
        	return layer.msg("只能选择收入记录");
        }
        var id = checkedData[0].id;
        var from =  $("#from").val();
        bindCustomterIndex = layer.open({
            type: 2,
            area: ['940px', '560px'],
            fixed: false, //不固定
            maxmin: true,
            content: '/fsExpenseIncome/toCustomerAssociate.action?expenseIncomeId=' + id + '&from=' + from
        });
    });

    $('#showIncomeAndGoal').unbind().click(function () {
        var btnTextEle = $(this).find('span');
        var iconEle = $(this).find('i');
        if (btnTextEle.text().indexOf('收起') > -1) {
            $('#incomeAndGoal').hide();
            btnTextEle.text('展开总览');
            iconEle.toggleClass('layui-icon-up');
            iconEle.toggleClass('layui-icon-down');
            $('#income-table-div').toggleClass('income-table-sale');
            table.reload('expense-income', {
               height: 'full-150'
            });
        } else {
            $('#incomeAndGoal').show();
            btnTextEle.text('收起总览');
            iconEle.toggleClass('layui-icon-down');
            iconEle.toggleClass('layui-icon-up');
            $('#income-table-div').toggleClass('income-table-sale');
            table.reload('expense-income', {
                height: 'full-230'
            });
        }
    });
}

//下载
function down_load(file_info) {
    //console.log("下载文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.location.href = "/operate/downloadFile?" + file_params;
}

/**
 * 删除上传文件信息
 */
function deleteUploadFileInfo(md5s,delAfter){
    $.ajax({
        type: "POST",
        url: "/fsExpenseIncome/delUploadFileInfo.action",
        dataType: "json",
        data: {
            md5s: md5s.join(",")
        },
        success: function (data) {
            if (data.code === '200' || data.code === 200) {
                if (delAfter != null){
                    delAfter(md5s,data);
                }
            }
        }
    });
}

function refresh() {
    
	var writeoffstatus = [];
	var stateActive_0 = $("#writeoffstatus_0").attr('checked');
	if(stateActive_0) {
	    writeoffstatus.push($("#writeoffstatus_0").val())
	}
	var stateActive_2 = $("#writeoffstatus_2").attr('checked');
	if(stateActive_2) {
	    writeoffstatus.push($("#writeoffstatus_2").val())
	}
	if(writeoffstatus.length < 1) {
		layer.msg("请选择消账状态！");
		return false;
	}
	var linkStatus = [];
	var notLink = $("#notLink").attr('checked');
	if(notLink) {
		linkStatus.push($("#notLink").val())
	}
	var linked = $("#linked").attr('checked');
	if(linked) {
		linkStatus.push($("#linked").val())
	}
	if(linkStatus.length < 1) {
		layer.msg("请选择关联状态！");
		return false;
	}
	//内容描述
	var depict = $('#depict').val();
	//时间类型 1-到账时间 2-导入时间
	var timeType = $('#timeType').val();
	
    table.reload('expense-income', {
        url: '/fsExpenseIncome/queryExpenseIncomePage.action',
        where: {
            year: new Date().getFullYear(),
            startTime: $("#startTime").val(),
            endTime: $("#endTime").val(),
            bankName: $("#bankName").val(),
            depict: depict,
            timeType: timeType,
            writeoffstatus : writeoffstatus.join(','),
            linkStatus: linkStatus.join(','),
            from: ""
        }, request: {
            pageName: 'page',
            limitName: 'pageSize'
        }
    });
}


