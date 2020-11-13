var laydate;
var layer;
var element;
var form;

var tableIns;
var insStart;

$(document).ready(function () {
    initSelect();
    layui.use(['laydate', 'layer', 'form', 'element'], function () {
        laydate = layui.laydate;
        layer = layui.layer;
        form = layui.form;
        element = layui.element;
        initDate();
        init_search_btn();
        init_reset_btn();
        init_table();
        initButton();
    });
});

function initDate() {
    var yearMonth = new Date().Format('yyyy-MM');
    $('#billMonth').val(yearMonth);
    insStart = laydate.render({
        elem: '#billMonth',
        type: 'month',
        trigger: 'click',
        max: yearMonth, // 最大值为今年今月
        format: "yyyy-MM",
        done: function (value, date) {
            $('#billMonth').val(value);
            reload_table();
        }
    });
}

// 初始化表格
function init_table() {
    layui.use(['table'], function () {
        var table = layui.table;
        //列表
        tableIns = table.render({
            elem: '#billList',
            url: "/bill/readBillsPages.action?temp=" + Math.random(),
            height: 'full-170',
            even: true,
            page: true,
            limit: 15,
            limits: [15, 30, 50, 100],
            method: 'POST',
            where: {
                entityType: 1
            },
            cols: [[
                {
                    field: 'billId',
                    title: '账单id',
                    align: 'center',
                    hide: true
                }, {
                    field: 'entityTypeName',
                    title: '主体类型',
                    align: 'center',
                    width: '5%',
                    minWidth: 100
                }, {
                    field: 'realName',
                    title: '销售/资源姓名',
                    align: 'center',
                    width: '8%'
                }, {
                    field: 'companyName',
                    title: '公司名称',
                    align: 'center',
                    width: '15%',
                    minWidth: 100
                }, {
                    field: 'productName',
                    title: '产品名称',
                    align: 'center',
                    width: '8%'
                }, {
                    field: 'billMonth',
                    title: '账单月份',
                    align: 'center',
                    width: '8%'
                }, {
                    field: 'billStatus',
                    title: '账单状态',
                    align: 'center',
                    width: '8%'
                }, {
                    field: 'platformSuccessCount',
                    title: '平台成功数',
                    align: 'right'
                }, {
                    field: 'cost',
                    title: '成本',
                    align: 'right'
                }, {
                    field: 'checkedSuccessCount',
                    title: '实际成功数',
                    align: 'right'
                }, {
                    field: 'receivables',
                    title: '应收',
                    align: 'right'
                }, {
                    title: '操作',
                    width: '10%',
                    align: 'center',
                    minWidth: 100,
                    toolbar: '#table-row-opts'
                }
            ]]
            , parseData: function (res) {
                return {
                    "code": 0,
                    "count": res.data.count,
                    "data": res.data.data
                };
            }
        });

        // 表格行 操作按钮
        table.on('tool(billList)', function (obj) {
            var rowData = obj.data;
            if (obj.event === 'update') {
                $.post("/bill/queryBillData", {
                    id: rowData.billId,
                    update: false
                }, function (res, status) {
                    if (obj.event === 'update') {
                        reloadBillData(rowData, res);
                    }
                });
            } else if (obj.event === 'operate') {
                openTab('操作账单', '/bill/toOperateBill?billId=' + rowData.billId, null, null);
            }
        });
    });
}

// 重新统计账单
function reloadBillData(rowData, res) {
    $('#billsDate').text(rowData.billsDate);
    $('#customerName').text(rowData.customerName);
    $('#productName').text(rowData.productName);
    $('#oldPcount').text(rowData.platformCount);
    $('#oldCost').text(rowData.cost);
    $('#oldUnitPrice').text(rowData.unitPrice);
    $('#billLoginName').text(rowData.loginName);
    $('#newPcount').text(res['平台成功数']);
    $('#newCost').text(res['平台账单金额']);
    $('#newUnitPrice').text(res['平均成本单价']);
    $('#nowLoginName').text(res['当前有效账号']);
    var index = layer.open({
        title: ['确认重新计算？', 'font-size:18px;' ],
        type: 1,
        content: $("#modifyBillsConfirm"),
        btn:'确定',
        area: '400px',
        yes: function(){
            $.ajax({
                type: "POST",
                async: false,
                url: "/bill/queryBillData.action",
                dataType: 'json',
                data: {id: rowData.billId, update: true},
                success: function (data) {
                    layer.close(index);
                    tableIns.reload();
                }
            });
        }
    });
}

// 初始化参数类型
function initSelect() {
    // 主体类型
    $.get("/bill/getEntityTypeSelect", function (res) {
        var options = '<option value="">---请选择---</option>';
        var entityTypes = typeof res == 'object' ? res : JSON.parse(res);
        $.each(entityTypes, function (index, item) {
            options += "<option value=" + item.value + ">" + item.name + "</option>";
        });
        $('#entityType').html(options);
    });

    // 账单状态
    $.get("/bill/getBillStatusSelect", function (res) {
        var options = '<option value="">---请选择---</option>';
        var billStatuses = typeof res == 'object' ? res : JSON.parse(res);
        $.each(billStatuses, function (index, item) {
            options += "<option value=" + item.value + ">" + item.name + "</option>";
        });
        $('#billStatus').html(options);
    });
}

// 初始化查询按钮
function init_search_btn() {
    $("#btn-search").unbind().bind('click', function () {
        reload_table();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn() {
    $("#btn-reset").unbind().bind('click', function () {
        form.render('select');
    });
}

// 重新加载表格
function reload_table() {
    tableIns.reload({
        url: "/bill/readBillsPages.action?temp=" + Math.random(),
        where: {
            entityType: $('#entityType').val(),
            searchProductName: $('#searchProductName').val(),
            searchCompanyName: $('#searchCompanyName').val(),
            realName: $('#realName').val(),
            billMonth: $('#billMonth').val(),
            billStatus: $('#billStatus').val(),
            settleType: $('#settleType').val()
        }
    });
}

function initButton() {
    $('#btn-delete').unbind().bind('click', function () {
        var settleType = $('#settleType').val();
        var type = '全部';
        if ('0' == settleType) {
            type = '所有预付';
        } else if ('1' == settleType) {
            type = '所有后付';
        }
        layer.confirm("此操作会删除月份【" + type + "】账单！！！", {
            title: "确认操作",
            icon: 3,
            btn: ["确认", "取消"],
            skin: "reject-confirm"
        }, function (index) {
            deleteMonthBill();
            layer.close(index);
        });
    });
}

function deleteMonthBill() {
    var billMonth = $('#billMonth').val()
    if (isBlank(billMonth)) {
        return layer.msg('请选择账单月份');
    }
    $.post('/bill/operateDeleteMonthBill', {
        month: billMonth,
        settleType: $('#settleType').val()
    }, function (res) {
        layer.msg(res);
        reload_table();
    });
}