//注意：parent 是 JS 自带的全局对象，可用于操作父页面
var index = window.parent.layer.getFrameIndex(window.name);
var layer;
// 加载中遮罩
var loadingIndex;
var table;
// 总量
var total = $("#total").val();
var incomeType = $("#incomeType").val();
// 是否为收入
var isIncome = incomeType === 0 || incomeType === '0';
// 导入ID
var expenseIncomeId = $("#expenseIncomeId").val();
layui.use(['layer', 'form', 'element', "table"], function () {
    table = layui.table;
    layer = layui.layer;
    loadingIndex = layer.load(2);
    init_expense_income_table();
});

/**
 * 加载已经导入的表格数据
 */
function init_expense_income_table() {
    var from = $("#from").val();
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#customer',
        height: 'full-200',
        url: '/customer/queryCustomerPage.action',
        page: true,
        limit: 20,
        method: 'POST',
        limits: [10, 20, 30, 50, 100],
        where: {
            customerName: ''
        },
        cols: [[{
            type: 'radio'
        }, {
            field: 'companyId',
            hide: true
        }, {
            field: 'saleName',
            title: '销售',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'customerType',
            title: '客户类型',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'companyName',
            title: '客户名称',
            align: 'center',
            style: fontSize
        }, {
            field: 'createTimeStr',
            title: '创建时间',
            align: 'center',
            style: fontSize
        }]],
        done: function () {
            // 取消加载中遮罩
            layer.close(loadingIndex);
            $("th[data-key='1-0-0']").find("i[class='layui-icon layui-icon-ok']").remove();
        }
    });
    
    table.on('row(customer)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });
}

// 确定
$("#ok_btn").click(function (e) {
    var checkStatus = table.checkStatus('customer');
    if (checkStatus.data.length < 1){
    	return layer.msg("请选择客户", {icon: 2});
    } else if (checkStatus.data.length > 1) {
    	return layer.msg("请选择一个客户", {icon: 2});
    } else {
    	var rowData = checkStatus.data[0];
    	layer.confirm('确定绑定客户【' + rowData.companyName + '】吗？', {
            btn: ['确认', '取消'],
            icon: 1,
            title: '确认'
        }, function () {
        	$.ajax({
                type: "POST",
                url: "/fsExpenseIncome/bindCustomer.action",
                dataType: "json",
                data: {
                    customerId: rowData.companyId,
                    incomeId: expenseIncomeId
                },
                success: function (data) {
                    if (data.code === '200' || data.code === 200) {
                    	layer.msg("关联客户成功!", {time: 2000, icon: 1});
                    	parent.layui.layer.close(parent.bindCustomterIndex);
                    	return parent.refresh(parent.$("#expense-income-year").val());
                    } else {
                        return layer.msg("关联客户失败!", {time: 2000, icon: 2});
                    }
                }
            });
        });
    }
});


// 取消
$("#cancel_btn").click(function (e) {
    parent.layer.close(index);
});

$("#bill-search").click(function () {
    refresh();
});

$("#bill-reset").click(function () {
    $("#customerName").val('');
});

// 清空部门
$("#empty").click(function() {
    $("#customerName").val('');
});


function refresh() {
    table.reload('customer', {
        url: '/customer/queryCustomerPage.action',
        where: {
        	customerName: $("#customerName").val()
        }
    });
}
