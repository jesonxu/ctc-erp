layui.use(['jquery', 'layer', 'form'], function () {
    let layer = layui.layer;
    let form = layui.form;
    // 获取请求的参数
    let entityId = util.getUrlParam("entityId");
    let flowId = util.getUrlParam("flowId");
    let flowClass = util.getUrlParam("flowClass");
    let checkedItem = util.getUrlParam("checked");
    let windowIndex = parent.layer.getFrameIndex(window.name);

    loadBillInvoiceInfos();

    /**
     * 加载产品的账单信息
     */
    function loadBillInvoiceInfos() {
        $.ajax({
            type: "POST",
            url: "/customerOperate/readInvoiceableBills.action?temp=" + Math.random(),
            dataType: 'json',
            data: {
                customerId: entityId,
                needOrder: 'T',
                flowClass: flowClass,
                flowId: flowId
            },
            success: function (data) {
                if (data.code === 200) {
                    let invoiceEle = $("<ul class='bill-invoice-choose-list'></ul>");
                    let invoices = data.data;
                    for (let index = 0; index < invoices.length; index++) {
                        let invoiceInfo = invoices[index];
                        let checked = "";
                        if (util.isNotNull(checkedItem) && checkedItem.indexOf(invoiceInfo.id) >= 0) {
                            checked = " checked='' ";
                        }
                        // 总金额
                        let total = util.formatBlank(invoiceInfo.receivables, 0);
                        // 已开金额
                        let done = util.formatBlank(invoiceInfo.actualInvoiceAmount, 0);
                        // 使用了的
                        let usedAmount = util.formatBlank(invoiceInfo.usedAmount, 0);
                        // 剩余的（本次最多可开金额）
                        let left = util.thousand(parseFloat(total) - parseFloat(done) - parseFloat(usedAmount));
                        let value = JSON.stringify(invoiceInfo);
                        let title = invoiceInfo.title;
                        title = "<div class=\"bill-invoice-title\">" + title + "</div>" +
                            "<div class=\"bill-invoice-detail\"><span>应开金额：" + total + "</span><span>可开金额：" + left + "</span></div>";
                        invoiceEle.append(
                            "<li>" +
                            "    <input type='checkbox' lay-skin='primary' name='billInvoice' value='" + value + "' " + checked + " title='" + title + "'/>" +
                            "</li>");
                    }
                    $("#bill-invoice-list").html(invoiceEle.prop("outerHTML"));
                    form.render();
                    $("#submit").click(function () {
                        close();
                    });
                }
            }
        });
    }

    /**
     * 关闭方法
     */
    function close() {
        let closeBtn = $("#layui-layer" + windowIndex, window.parent.document).find("a[class*='layui-layer-close1']");
        $(closeBtn)[0].click();
    }
});