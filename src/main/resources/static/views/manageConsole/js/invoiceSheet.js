var layer;
var element;
var form;
var table;
var excel;
// 加载中遮罩
var loadingIndex;
// 表实例
var _tableIns;
// 申请日期（开始）
var _applyDate;
// 申请日期（结束）
var _applyDateEnd;
var tipsIns;
var contractFilesMap = {};
$(document).ready(function () {
    initDate();
    initTable();
    initButton();
    autoCompleteCompany();
});

// 初始化日期控件
function initDate() {
    var nowDate = new Date();
    var today = {
        date: nowDate.getDate(),
        month: nowDate.getMonth(),
        year: nowDate.getFullYear()
    };
    $('#applyDate').val(today.year + "-" + (today.month + 1) + "-01");
    $('#applyDateEnd').val(nowDate.format("yyyy-MM-dd"))
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        _applyDate = laydate.render({
            elem: '#applyDate',
            value: new Date(today.year, today.month, 1),
            format: 'yyyy-MM-dd',
            max: 0,
            type: 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新结束日期的最小日期
                _applyDateEnd.config.min = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });

        _applyDateEnd = laydate.render({
            elem: '#applyDateEnd',
            value: new Date(),
            format: 'yyyy-MM-dd',
            max: 0,
            type: 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新开始日期的最大日期
                _applyDate.config.max = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });
    });
}

// 重置日期控件的值
function resetDate() {
    var nowDate = new Date();
    var today = {
        date: nowDate.getDate(),
        month: nowDate.getMonth(),
        year: nowDate.getFullYear()
    };
    // 开始日期的最小值
    _applyDate.config.min = lay.extend({}, today, {
        date: 1,
        month: 0,
        year: 1900
    });
    // 开始日期的最大值
    _applyDate.config.max = lay.extend({}, today, {
        date: today.date,
        month: today.month
    });
    // 结束日期的最小值
    _applyDateEnd.config.min = lay.extend({}, today, {
        date: 1,
        month: 0,
        year: 1900
    });
    // 结束日期的最大值
    _applyDateEnd.config.max = lay.extend({}, today, {
        date: today.date,
        month: today.month
    });
}

function initTable() {
    layui.use(['table', 'form', 'excel'], function () {
        table = layui.table;
        form = layui.form;
        excel = layui.excel;
        _tableIns = table.render({
            elem: '#invoiceTable',
            url: '/invoice/readInvoiceByPage.action?temp=' + Math.random(),
            data: [],
            toolbar: '#toolbarDemo',
            defaultToolbar: false,
            limit: 15,
            limits: [15, 30, 60, 100],
            method: 'POST',
            height: 'full-140',
            page: true,
            where: getParameter(),
            cols: [
                [{
                    field: 'id',
                    hide: true,
                }, {
                    field: 'wtime',
                    title: '开票日期',
                    align: 'center',
                    width: '6%',
                    fixed: 'left'
                }, {
                    field: 'realName',
                    title: '申请人',
                    align: 'center',
                    width: '6%',
                    fixed: 'left'
                }, {
                    field: 'applyTime',
                    title: '申请日期',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'entityId',
                    hide: true,
                }, {
                    field: 'entityName',
                    title: '公司名称',
                    align: 'center',
                    width: '12%',
                }, {
                    field: 'entityType',
                    title: '主体类型',
                    align: 'center',
                    width: '6%',
                    templet: function (row) {
                        if (row.entityType == 0) {
                            return ' 供应商 ';
                        } else if (row.entityType == 1) {
                            return ' 客户 ';
                        } else if (row.status == 2) {
                            return ' 电商供应商 ';
                        }
                    }
                }, {
                    field: 'receivables',
                    title: '开票金额',
                    align: 'right',
                    width: '8%',
                }, {
                    field: 'actualReceivables',
                    title: '已收金额',
                    align: 'right',
                    width: '8%',
                }, {
                    field: 'bankInvoiceInfo',
                    title: '我司开票信息',
                    align: 'center',
                    width: '12%',
                }, {
                    field: 'oppositeBankInvoiceInfo',
                    title: '对方开票信息',
                    align: 'center',
                    width: '12%',
                }, {
                    field: 'invoiceStatus',
                    title: '发票状态',
                    align: 'center',
                    width: '6%',
                    templet: function (row) {
                        if (row.invoiceStatus == 0) {
                            return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已开票 </div>';
                        } else if (row.invoiceStatus == 1) {
                            return '<div style = "background-color:#FF5722;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已作废 </div>';
                        } else {
                            return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                        }
                    }
                }, {
                    field: 'serviceName',
                    title: '开票服务名称',
                    align: 'center',
                    width: '7%',
                }, {
                    field: 'invoiceType',
                    title: '发票类型',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'remark',
                    title: '备注',
                    align: 'center',
                    width: '8%',
                }, {
                    title: '操作',
                    width: '6%',
                    align: 'center',
                    minWidth: 100,
                    toolbar: '#table-row-opts'
                }]
            ],
            parseData: function (res) { // res 即为原始返回的数据
                return {
                    "code": 0, // 解析接口状态
                    "count": res.data.count, // 解析数据长度
                    "data": res.data.data
                    // 解析数据列表
                };
            }
        });
        form.render();
        table.on('toolbar(invoiceTable)', function (obj) {
            if (obj.event === 'EXPORT_EXCEL') {
                // 导出
                $.post("/invoice/exportInvoice", getParameter(), function (data) {
                    if (data.code == 500) {
                        layer.msg(data.msg);
                    } else if (data.code == 200) {
                        down_load(data.data);
                    }
                });
            }
        });
        table.on('tool(invoiceTable)', function (obj) {
            var rowData = obj.data;
            if (obj.event === 'voidInvoice') {
                voidInvoiceProcess(rowData);
            }
        });
    });
}

function initButton() {
    $("#btn-search").click(function () {
        search();
    });
    $("#btn-reset").click(function () {
        clearAll();
    });
}

// 获取查询条件
function getParameter() {
    var entityId = $("#entityId").val().trim();
    var entityType = $("#entityType").val().trim();
    var companyName = $("#companyName").val().trim();
    var applyDate = $("#applyDate").val().trim();
    var invoiceStatus = $("#invoiceStatus").val().trim();
    if (isNotBlank(applyDate)) {
        applyDate = applyDate + " 00:00:00";
    } else {
        applyDate = '';
    }
    var applyDateEnd = $("#applyDateEnd").val().trim();

    if (isNotBlank(applyDateEnd)) {
        applyDateEnd = applyDateEnd + " 23:59:59";
    } else {
        applyDateEnd = '';
    }
    return {
        applyDate: applyDate,
        applyDateEnd: applyDateEnd,
        entityId: entityId,
        companyName: companyName,
        invoiceStatus: invoiceStatus,
        entityType: entityType
    }
}

// 搜索
function search() {
    var parameter = getParameter();
    if (isBlank(parameter)) {
        return;
    }
    _tableIns.reload({
        url: "/invoice/readInvoiceByPage.action?temp=" + Math.random(),
        where: parameter
    });
}

// 自动补全公司名称
function autoCompleteCompany() {
    layui.config({
        base: '/common/js/'
    }).extend({ // 设定模块别名
        autocomplete: 'autocomplete'
    });
    layui.use('autocomplete', function () {
        var autocomplete = layui.autocomplete;
        autocomplete.render({
            elem: $('#companyName'),
            hidelem: $('#entityId'),
            url: '/invoice/queryAllCompanyByAuto.action',
            template_val: '{{d.id}}',
            template_txt: '{{d.companyName}} <span class=\'layui-badge layui-bg-gray\'>{{d.realName}}</span>',
            onselect: function (resp) {
                $("#companyName").val(resp.companyName);
                $("#entityId").val(resp.id);
                $('#entityId').attr("data-companyName", resp.realName);
            }
        });
        $("#companyName").change(function () {
            var entityId = $("#entityId");
            var realName = entityId.attr("data-companyName");
            var displayName = $(this).val();
            if (displayName !== realName) {
                entityId.val("");
                entityId.attr("data-companyName", "");
            }
        });
    })
}

function clearAll() {
    $("#companyName").val('');
    $("#entityId").val('');
    $("#entityType").val('');
    $("#invoiceStatus").val('');
    resetDate();
    layui.use('form', function () {
        var form = layui.form, layer = layui.layer;
        form.render();
    });
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

function editInvoiceStatus(rowData) {
    // 弹出选择框，修改发票状态，重新加载
    var editIndex = layer.open({
        title: '发票退回',
        type: 1,
        btn: ['确认', '取消'],
        area: ['565px', '260px'],
        fixed: false, //不固定
        maxmin: true,
        content: $("#withdrawDialog"),
        yes: function () {
            var data = {
                withdrawRemark: $("#withdrawRemark").val().trim(),
                invoiceId: rowData.id
            };
            if (isNull(data.withdrawRemark)) {
                layer.msg('退回备注不能为空');
                return false;
            }
            $.ajax({
                type: "POST",
                async: false,
                url: "/invoice/editInvoiceStatus.action?temp=" + Math.random(),
                dataType: 'json',
                data: data,
                success: function (data) {
                    if (data.code == 200) {
                        window.parent.layer.msg(data.msg);
                        // 关闭弹出层
                        layer.close(editIndex);
                        // 重新加载表格数据
                        search();
                    } else {
                        layer.msg(data.msg);
                    }
                }
            });
        },
        end: function () {
            // 清空文本域
            $("#withdrawRemark").val("");
        }
    });
}

function voidInvoiceProcess(rowData) {
    layer.open({
        title: ['发票作废', 'font-size:18px;'],
        type: 2,
        area: ['600px', '400px'],
        fixed: true, //不固定
        fix: false,
        maxmin: true,
        content: '/invoice/toVoidInvoicePage/' + rowData.id + '?receivables=' + rowData.receivables + '&temp=' + Math.random(),
        btn: ['确认', '取消'],
        yes: function (index, layero) {
            var body = layer.getChildFrame('body', index);
            var deductionAmount = $(body).find("span[name='deductionAmount']").text();
            var receivables = $(body).find("span[name='receivables']").text();
            if (parseFloat(deductionAmount) !== parseFloat(receivables)) {
                layer.msg("扣减金额与发票金额不等");
                return
            }
            var frame = $(layero).find("iframe")[0];
            var data = [];
            var tableData = frame.contentWindow.obtainTableData();
            var selectData = tableData.data;
            $(selectData).each(function (index, bill) {
                data.push({
                    id: bill.id,                                     // 账单id
                    deductionAmount: bill.deductionAmount                // 扣减金额
                });
            });
            var remark = $(frame).contents().find("#remark").val();

            $.ajax({
                type: "POST",
                async: false,
                contentType: "application/json",
                url: "/invoice/voidInvoiceProcess.action?temp=" + Math.random(),
                data: JSON.stringify({
                    id: rowData.id,
                    receivables: rowData.receivables,
                    productBillsJSONObjectList: data,
                    remark: remark
                }),
                success: function (data) {
                    if (data.code == 200) {
                        parent.layer.msg("作废成功");
                        layer.close(index);
                        search();
                    } else {
                        layer.msg(data.msg);
                    }
                }
            });
        },
        cancel: function (index, layero) {
            layer.close(index);
        }
    });
}