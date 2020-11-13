var laydate;
var layer;
var element;
var form;
var table;

$(document).ready(function () {
    layui.use(['laydate', 'layer', 'form', 'element', 'table'], function () {
    	table = layui.table;
        laydate = layui.laydate;
        layer = layui.layer;
        form = layui.form;
        element = layui.element;
        init_search_btn();
        init_reset_btn();
        init_table();
    });
});

var tableIns;

// 初始化表格
function init_table() {
    tableIns = table.render({
        elem: '#productOperateCostList',
        url: "/customerOperateCostManage/readPages.action?temp=" + Math.random(),
        height: 'full-70',
        toolbar: '#table-opts',
        defaultToolbar: [],
        page: true,
        limit: 20,
        limits: [20, 30, 50, 100],
        method: 'POST',
        cols: [[
        	{
                field: 'deptName',
                title: '部门',
                align: 'center',
                width: 100,
                // fixed:"left"
            }, {
                field: 'saleName',
                title: '销售',
                align: 'center',
                width: 100,
                // fixed:"left"
            }, {
                field: 'customerName',
                title: '客户',
                align: 'left',
                width: 200,
            }, {
                field: 'productType',
                title: '产品类型',
                align: 'center',
                width: 50,
            }, {
                field: 'productName',
                title: '产品名称',
                align: 'center',
                width: 200,
            }, {
                field: 'settleType',
                title: '结算方式',
                align: 'center',
                width: 50,
            }, {
                field: 'customerFixedCost',
                title: '客户固定运营成本（元）',
                align: 'center',
                width: '12%',
                templet: function (rowData) {
                	var html = '<div>';
                	var productOperateFixedCost = rowData.productOperateFixedCost;
                	productOperateFixedCost = productOperateFixedCost ? productOperateFixedCost : 0;
                	html += '<input value="' + productOperateFixedCost + '" type="text" id="productOperateFixedCost_' + rowData.productId + '" placeholder="保留两位小数" autocomplete="off" class="layui-input table-inner-input" />';
                	html += '</div>';
                	return html;
                }
            }, {
				field: 'productOperateSingleCost',
				title: '产品单条运营成本（元）',
				align: 'right',
				width: '12%',
				templet: function (rowData) {
					var html = '<div>';
					var productOperateSingleCost = rowData.productOperateSingleCost;
					productOperateSingleCost = productOperateSingleCost ? productOperateSingleCost : 0;
					html += '<input value="' + productOperateSingleCost + '" type="text" id="productOperateSingleCost_' + rowData.productId + '" placeholder="保留六位小数" autocomplete="off" class="layui-input table-inner-input" />';
					html += '</div>';
					return html;
				}
			}, {
				field: 'billAmountRatio',
				title: '账单金额运营成本比例',
				align: 'right',
				width: '12%',
				templet: function (rowData) {
					var html = '<div>';
					var billAmountRatio = rowData.billAmountRatio;
					billAmountRatio = billAmountRatio ? billAmountRatio : 0;
					html += '<input value="' + billAmountRatio + '" type="text" id="billAmountRatio_' + rowData.productId + '" placeholder="保留六位小数" autocomplete="off" class="layui-input table-inner-input" />';
					html += '</div>';
					return html;
				}
			}, {
				field: 'billGrossProfitRatio',
				title: '账单毛利润运营成本比例',
				align: 'right',
				width: '12%',
				templet: function (rowData) {
					var html = '<div>';
					var billGrossProfitRatio = rowData.billGrossProfitRatio;
					billGrossProfitRatio = billGrossProfitRatio ? billGrossProfitRatio : 0;
					html += '<input value="' + billGrossProfitRatio + '" type="text" id="billGrossProfitRatio_' + rowData.productId + '" placeholder="保留六位小数" autocomplete="off" class="layui-input table-inner-input" />';
					html += '</div>';
					return html;
				}
			}, {
                field: 'remark',
                title: '备注',
                align: 'center',
				width: '8%',
                templet: function (rowData) {
                	var html = '<div>';
                	html += '<input value="' + (rowData.remark ? rowData.remark : '')
                		 + '" type="text" maxlength="255" id="remark_' + rowData.productId + '" placeholder="备注" autocomplete="off" class="layui-input table-inner-input table-inner-remark" />';
                	html += '</div>';
                	return html;
                }
            }, {
                title: '操作',
                // width: '4%',
                align: 'center',
                width: 80,
                fixed:"right",
                toolbar: '#table-row-opts'
            }, {
                field: 'productId',
                title: 'productId',
                hide: true
            }
        ]]
        , parseData: function (res) {
            return {
                "code": 0,
                "count": res.data.count,
                "data": res.data.data
            };
        }
    	, done: function (res, curr, count) {
    		if (count > 0) {
    			merge(res);
    		}
        }
    });

    // 点击行选中
    table.on('row(productOperateCostList)', function (obj) {
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });
    
    // 表格行操作按钮
    table.on('tool(productOperateCostList)', function (obj) {
        var data = obj.data;
        var id = data.entityid;
        if (obj.event === 'save') {
        	saveProductOperateCost(obj.data);
        }
    });
    
    // 表头操作按钮
    table.on('toolbar(productOperateCostList)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        if ("save".equals(obj.event)) {
        	saveUnifiedOperateSingleCost();
        }
    });
}

//合并开始
function merge(res) {
    var data = res.data;
    var mergeIndex = 0; //定位需要添加合并属性的行数
    var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
    var _number = 1; //保持序号列数字递增
    var columsName = ['deptName', 'saleName', 'customerName']; //需要合并的列名称
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
    /*layui.$.each(layui.$("#productOperateCostList").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function(i, v) {
        if (layui.$(v).find('td').eq(2).css('display') === 'none') {
            tdArrL.eq(i).find('td').css('display', 'none');
            tdArrR.eq(i).find('td').css('display', 'none');
        } else {
            tdArrL.eq(i).find('td').find('.laytable-cell-numbers').html(_number++);
            tdArrL.eq(i).find('td').css('height', layui.$(v).find('td').eq(2)[0].clientHeight);
            tdArrR.eq(i).find('td').css('height', layui.$(v).find('td').eq(2)[0].clientHeight);

        }
    });*/
}

function saveUnifiedOperateSingleCost() {
	
	var allInput = $('.unified-cost-input');
	
	var flag = true;
	var vlaues = [];
	var tips = [];
	
	$(allInput).each(function (i, item) {
		if (flag) {
			flag = validate($(item).val(), $(item));
			vlaues.push($(item).val());
			tips.push($(item).prev('span').text() + '【' + $(item).val() + '】');
		}
	});
	
	if (!flag) {
		return;
	}
	
	layer.confirm("确认保存【统一运营成本（元/条）】：" + tips.join('，') + "吗？", {
        title: "确认",
        icon: 3,
        btn: ["确认", "取消"]
    }, function () {
	 
		$.post("/customerOperateCostManage/saveUnifiedOperateSingleCost", {
			unifiedCosts: vlaues.join(','),
	    }, function (data, status) {
	        if (data.code == 200) {
	        	layer.msg("保存成功!");
	        } else {
	        	layer.msg("保存失败!");
	        }
	    }, "json");
		
    });
}

function saveProductOperateCost(rowData) {
	var productId = rowData.productId;
    var flag = true;
    var arr = [
    	$('#productOperateFixedCost_' + productId),
		$('#productOperateSingleCost_' + productId),
		$('#billAmountRatio_' + productId),
		$('#billGrossProfitRatio_' + productId)
	];
    $(arr).each(function (i, item) {
    	if (flag) {
    		flag = validate($(item).val(), $(item));
    	}
    });
    if (!flag) {
    	return;
    }
    
	layer.confirm("确认保存【" + rowData.productName + "】：" +
		"客户每月固定费用【" + arr[0].val() + "】（元），" +
		"单条产品运营费用【" + arr[1].val() + "】（元）" +
		"账单金额运营费用比例【" + arr[2].val() + "】（元）" +
		"毛利润运营费用比例【" + arr[3].val() + "】（元）" +
		"吗？", {
        title: "确认",
        icon: 3,
        btn: ["确认", "取消"]
    }, function () {
    	$.post("/customerOperateCostManage/saveProductOperateCost", {
        	productId: productId,
        	productOperateFixedCost: arr[0].val(),
        	productOperateSingleCost: arr[1].val(),
			billAmountRatio: arr[2].val(),
			billGrossProfitRatio: arr[3].val(),
        	costRemark: $('#' + productId + 'remark').find('[name="remark"]').val(),
        }, function (data, status) {
            if (data.code == 200) {
            	layer.msg("保存成功!");
            	tableIns.reload();
            } else {
            	layer.msg("保存失败!");
            }
        }, "json");
    }, function () {
    	layer.msg("取消");
    });
	
}

function getUnifiedOperateSingleCost() {
	$.post("/customerOperateCostManage/getUnifiedOperateSingleCost", {
    	productId: productId,
    	productOperateFixedCost: $('#' + productId).find('[name="productOperateFixedCost"]').val(),
    	productOperateSingleCost: $('#' + productId).find('[name="productOperateSingleCost"]').val()
    }, function (data, status) {
        if (status =="success" && data.msg == 'success') {
        	$('#unifiedFixedCost').text(data.data);
        }
    }, "json");
}

function validate(val, ele) {
	if (!val) {
    	tipsError(ele, '不能为空');
    	return false;
    }
	if (!val) {
    	tipsError(ele, '不能为空');
    	return false;
    } else {
    	if (!/^(([0-9]*)|(([0]\.\d{1,6}|[1-9][0-9]*\.\d{1,6})))$/.test(val)){
    		tipsError(ele, '格式不正确');
    		return false;
    	} else if (val > 1000000000) {
    		tipsError(ele, '数字过大');
    		return false;
    	}
    }
	return true;
}

function tipsError(ele, msg) {
	$(ele).focus();
    return layer.tips(msg, $(ele));
}

// 初始化查询按钮
function init_search_btn() {
    $("#btn-search").click(function () {
        reload_table();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn() {
    $("#btn-reset").click(function () {
        $("#customerName").val("");
    });
}

// 重新加载表格
function reload_table() {
    tableIns.reload({
        where: {
            customerName: $("#customerName").val(),
        },
        page : {
			curr : 1
		}
    });
}