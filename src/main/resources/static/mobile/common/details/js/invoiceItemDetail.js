layui.use(['jquery', 'layer', 'form'], function () {
    let layer = layui.layer;
    let form = layui.form;
    // 获取请求的参数
    let productId = util.getUrlParam("productId");
    let flowClass = util.getUrlParam("flowClass");
    let checkedItem = util.getUrlParam("checked");
    if (util.isNotNull(checkedItem)){
        checkedItem = checkedItem.split(",");
    }
    let windowIndex = parent.layer.getFrameIndex(window.name);

    loadInvoices();

    /**
     * 加载产品的发票信息
     */
    function loadInvoices() {
        $.ajax({
            type: "POST",
            url: "/customerOperate/readInvoices.action?temp=" + Math.random(),
            dataType: 'json',
            data: {
                productId: productId,
                flowClass: flowClass
            },
            success: function (data) {
                if (data.code === 200) {
                    let invoiceEle = $("<ul class='invoice-choose-list'></ul>");
                    let invoices = data.data;
                    for (let index = 0; index < invoices.length; index++) {
                        let invoiceInfo = invoices[index];
                        let value = invoiceInfo.value;
                        let checked = "";
                        if (util.isNotNull(checkedItem) && checkedItem.indexOf(invoiceInfo.id) >= 0) {
                            checked = " checked='' ";
                        }
                        let text = invoiceInfo.text;
                        let title = invoiceInfo.title;
                        // 发票金额
                        let receivable = invoiceInfo.receivables;
                        // 已收金额
                        let actualReceivable = invoiceInfo.actualReceivables;
                        let item = "<div class=\"item-title\">" + title + "</div>" +
                            "<div class=\"item-detail\">" +
                            "   <span>发票金额：" + receivable + "</span>" +
                            "   <span>已收金额:" + actualReceivable + "</span>" +
                            "</div>";
                        invoiceEle.append("<li><input type='checkbox' name='invoice' lay-skin='primary' value='" + JSON.stringify(invoiceInfo) + "' " + checked + " ' title='" + item + "'></li>");
                    }
                    $("#invoice-items").html(invoiceEle.prop("outerHTML"));
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