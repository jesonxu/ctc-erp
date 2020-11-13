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
    var entityName = "customer" === from ? "客户名称" : "供应商名称";
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#product-bill',
        height: 'full-200',
        url: '/fsExpenseIncome/readProductBills.action',
        page: true,
        limit: 20,
        method: 'POST',
        limits: [10, 20, 30, 50, 100],
        request: {
            pageName: 'page',
            limitName: 'pageSize'
        },
        where: {
            startTime: $("#startTime").val(),
            endTime: $("#endTime").val(),
            entityName: $("#entityName").val(),
            deptId: $("#deptId").val(),
            from: from,
            income: incomeType
        },
        cols: [[{
            type: 'radio'
        }, {
            field: 'id',
            hide: true
        }, {
            field: 'companyName',
            title: entityName,
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'productName',
            title: '产品名称',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize
        }, {
            field: 'receivables',
            title: '应收账款',
            align: 'right',
            style: fontSize,
            hide:!isIncome,
            templet: function (data) {
                return thousand(data.receivables);
            }
        }, {
            field: 'actualReceivables',
            title: '实收账款',
            align: 'right',
            style: fontSize,
            hide:!isIncome,
            templet: function (data) {
                return thousand(data.actualReceivables);
            }
        }, {
            field: 'thisReceive',
            title: '本次收款',
            align: 'right',
            hide:!isIncome,
            style: fontSize,
            templet: function (data) {
                var receive = parseFloat(data.receivables);
                if (receive == null || receive.length === 0) {
                    receive = 0;
                }
                var actualReceive = parseFloat(data.actualReceivables);
                if (actualReceive === undefined ||actualReceive == null || actualReceive.length === 0) {
                    actualReceive = 0;
                }
                var dis = parseFloat(receive) - parseFloat(actualReceive);
                if (dis < 0) {
                    dis = 0;
                }
                var showDis = thousand(dis);
                var id = "this_receive_"+ data.id;
                var hide_id = "origin_this_receive_" + data.id;
                return "<input type='text'  onblur='check_receive_input(this,\""+dis +"\")'" +
                    " onfocus='set_receive_data(this,\""+dis +"\")' " +
                    " class='table-edit-input' disabled id='" + id + "' value='" + showDis + "'/>";
            }
        },{
            field: 'payables',
            title: '应付账款',
            align: 'right',
            hide: isIncome,
            style: fontSize,
            templet: function (data) {
                return thousand(data.payables);
            }

        }, {
            field: 'actualpayables',
            title: '实付账款',
            align: 'right',
            hide: isIncome,
            style: fontSize,
            templet: function (data) {
                return thousand(data.actualpayables);
            }
        }, {
            field: 'thispay',
            title: '本次付款',
            align: 'right',
            hide: isIncome,
            style: fontSize,
            edit: 'text',
            templet: function (data) {
                var pay = parseFloat(data.payables);
                if (pay == null || pay.length === 0) {
                    pay = 0;
                }
                var actualPay = parseFloat(data.actualpayables);
                if (actualPay === undefined || actualPay == null || actualPay.length === 0) {
                    actualPay = 0;
                }
                var dis = parseFloat(pay) - parseFloat(actualPay);
                if (dis < 0) {
                    dis = 0;
                }
                var showDis =  thousand(dis);
                var id = "this_pay_"+ data.id;
                return "<input type='text'  onblur='check_receive_input(this,\""+dis +"\")'" +
                    " onfocus='set_receive_data(this,\""+dis +"\")' " +
                    " class='table-edit-input' disabled id='" + id + "' value='" + showDis + "'/>";
            }
        }, {
            field: 'wTime',
            title: '账单时间',
            align: 'center',
            style: fontSize
        }]],
        done: function () {
            // 取消加载中遮罩
            layer.close(loadingIndex);
            $("th[data-key='1-0-0']").find("i[class='layui-icon layui-icon-ok']").remove();
        }
    });

    table.on('row(product-bill)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    table.on('radio(product-bill)', function(obj){
        console.log("单选框 选中");
        var row = obj.data;
        var id = row.id;
        var beforeInput = $(".table-edit-input-tip");
        beforeInput.removeClass("table-edit-input-tip");
        beforeInput.attr("disabled", "disabled");
        if (isIncome){
            // 收入
            var incomeInput = $("input[id='this_receive_" + id + "'");
            incomeInput.focus();
            incomeInput.removeAttr("disabled");
            incomeInput.addClass("table-edit-input-tip");
        }else{
            // 收入
            var payInput = $("input[id='this_pay_" + id + "'");
            payInput.focus();
            payInput.removeAttr("disabled");
            payInput.addClass("table-edit-input-tip");
        }
    });
}


// 判断总数
function check_receive_input(ele, left) {
    var write = $(ele).val();
    if (write == null || write === "") {
        layer.tips("金额不能为空", ele);
        set_receive_data(ele, left);
        return;
    }
    write = write.replace(",", "");
    if (!$.isNumeric(write)) {
        layer.tips("请填写数字", ele);
        set_receive_data(ele, left);
        return;
    }
    // 总部不能小于 填写的
    if (total < parseFloat(write)) {
        layer.tips("金额大于总金额,请重新填写", ele);
        set_receive_data(ele, left);
        return;
    }
    // 填写的不能大于需要的
    if (parseFloat(left) < parseFloat(write.replace(",", ""))) {
        write = left;
        layer.tips("金额大于账单金额,请重新填写", ele);
    }
    $(ele).val(thousand(write));
}

// 设置总数
function set_receive_data(ele,left) {
    console.log("设置总数");
    var write =  $(ele).val();
    if(parseFloat(left) > parseFloat(total)){
        write = total;
    }
    $(ele).val(thousand(write));
    return write;
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


// 确定
$("#ok_btn").click(function (e) {
    var checkStatus = table.checkStatus('product-bill');
    if (checkStatus.data.length < 1){
        layer.msg("请点击表格关联账单", {icon: 2});
        return;
    }
    var rowData = checkStatus.data[0];
    var billId =rowData.id;
    var cost = $("#this_receive_" + billId).val();
    if (!isIncome){
        // 支出
        cost = $("#this_pay_" + billId).val();
    }
    if (cost == null || cost === "" || cost === undefined) {
        layer.msg("请先关联账单",{icon:2});
        return;
    }
    cost = cost.replace(",", "");
    if (!$.isNumeric(cost)){
        layer.msg("关联金额错误",{icon:2});
        return;
    }
    $.ajax({
        type: "POST",
        url: "/fsExpenseIncome/bindFsExpenseIncome.action",
        dataType: "json",
        data: {
            productBillId: billId,
            id: expenseIncomeId,
            cost: cost
        },
        success: function (data) {
            if (data.code === '200' || data.code === 200) {
                var year = $("#year").val();
                parent.layer.msg(data.msg);
                parent.layer.close(index);
                parent.refresh(year);
            } else {
                layer.msg(data.msg, {time: 2000, icon: 2});
            }
        }
    });
});


// 取消
$("#cancel_btn").click(function (e) {
    parent.layer.close(index);
});

layui.use('laydate', function () {
    var laydate = layui.laydate;
    laydate.render({
        elem: '#time'
        , range: true
    });
});

$("#bill-search").click(function () {
    refresh();
});

$("#bill-reset").click(function () {
    $("#entityName").val("");
    $("#deptId").val("");
    $("#time").val("");
    $("#deptName").val('');
});

// 部门选择
$("#deptName").click(function() {
    layer.open({
        type : 2,
        title : '部门选择',
        area: ['360px', '270px'],
        id : 'lay_dept',
        btn : [ '确定', '取消' ],
        content : '/account/toDepts.action?isEditAble=false',
    });
});

// 清空部门
$("#empty").click(function() {
    $("#deptName").val('');
    $("#deptId").val('');
});


function refresh() {
    table.reload('product-bill', {
        url: '/fsExpenseIncome/readProductBills.action',
        where: {
            startTime: $("#startTime").val(),
            endTime: $("#endTime").val(),
            entityName: $("#entityName").val(),
            deptId: $("#deptId").val(),
            from: $("#from").val(),
            income: incomeType
        }, request: {
            pageName: 'page',
            limitName: 'pageSize'
        }
    });
}
