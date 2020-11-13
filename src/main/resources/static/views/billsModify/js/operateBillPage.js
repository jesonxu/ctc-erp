var form;
var layer;
var table;

$(document).ready(function () {
    var billId = $('#billId').text();
    layui.use(['form', 'layer', 'table'], function () {
        form = layui.form;
        layer = layui.layer;
        table = layui.table;
        $.get('/bill/getBillDetail?billId=' + billId, function (res) {
            if (res.code == 200) {
                var bill = res.data;
                showBillDetail(bill);
                showOperationLog(bill.operationLog)
                showOperations(bill.billStatus);
            } else {
                layer.msg(res.msg);
            }
        })
    })
});

/**
 * 账单详情
 * @param bill
 */
function showBillDetail(bill) {

    $('#billTitle').text(bill.billTitle);
    $('#billId').text(bill.billId);
    $('#billNumber').text(bill.billNumber);
    $('#billStatus').text(bill.billStatus);
    $('#entityTypeName').text(bill.entityTypeName);
    $('#realName').text(bill.realName);
    $('#companyName').text(bill.companyName);
    $('#productName').text(bill.productName);
    $('#productTypeName').text(bill.productTypeName);
    $('#billPeriod').text(bill.billPeriod);
    $('#settleType').text(bill.settleType);
    $('#loginName').text(bill.loginName);
    $('#billMonth').text(bill.billMonth);
    $('#writeOffTime').text(bill.writeOffTime);
    $('#remark').text(bill.remark);

    // 可编辑数据
    $('#platformSuccessCount').val(bill.platformSuccessCount);
    $('#cost').val(bill.cost);
    $('#checkedSuccessCount').val(bill.checkedSuccessCount);
    $('#receivables').val(bill.receivables);
    $('#actualReceivables').val(bill.actualReceivables);
    $('#payables').val(bill.payables);
    $('#actualPayables').val(bill.actualPayables);
    $('#actualInvoiceAmount').val(bill.actualInvoiceAmount);
    $('#unitPrice').val(bill.unitPrice);
    $('#grossProfit').val(bill.grossProfit);

}

/**
 * 操作按钮
 * @param billStatus
 */
function showOperations(billStatus) {
    if (typeof BILL_OPERATIONS != 'object') {
        return
    }
    var opts = Object.keys(BILL_OPERATIONS);
    var divEle = $('div.operations');
    var html = "";
    for (var index = 0; index < opts.length; index++) {
        var opt = BILL_OPERATIONS[opts[index]];
        if (isNull(opt)) {
            continue
        }
        html += "<div class='operation-item'>";
        html += "    <button type='button' class='layui-btn layui-btn-sm' title='"+ opt.desc + "' onclick='operateBill(this, \"" + opt.operation + "\")'>" + opt.title + "</button>";
        html += "    <span>" + opt.desc + "</span>";
        html += "</div>";
    }
    $(divEle).html(html);
}

function operateBill(ele, url) {
    var title = $(ele).text();
    var data = {
        billId: $('#billId').text(),
        platformSuccessCount: $('#platformSuccessCount').val(),
        cost: $('#cost').val(),
        checkedSuccessCount: $('#checkedSuccessCount').val(),
        receivables: $('#receivables').val(),
        actualReceivables: $('#actualReceivables').val(),
        payables: $('#payables').val(),
        actualPayables: $('#actualPayables').val(),
        actualInvoiceAmount: $('#actualInvoiceAmount').val(),
        unitPrice: $('#unitPrice').val(),
        grossProfit: $('#grossProfit').val()
    }
    layer.confirm("确认执行【" + title + "】操作?", {
        title: "确认操作",
        icon: 3,
        btn: ["确认", "取消"],
        skin: "reject-confirm"
    }, function (index) {
        $.post(url, data, function (res) {
            layer.tips(res, $(ele));
        });
        layer.close(index);
    }, function () {

    });
}

function showOperationLog(operationLog) {
    if (isNull(operationLog)) {
        return
    }
    operationLog = typeof operationLog == 'object' ? operationLog : JSON.parse(operationLog);
    table.init('operationLog', {limit: 999});
    table.reload('operationLog', {
        url: '',
        data: operationLog
    });
    table.render();
}