layui.use(['jquery', 'layer', 'element', 'form'], function () {
    let layer = layui.layer;
    let element = layui.element;
    let form = layui.form;
    // 获取请求的参数
    let entityId = util.getUrlParam("entityId");
    let type = util.getUrlParam("type");
    let checkedItem = util.getUrlParam("checked");
    if (util.isNotNull(checkedItem)) {
        checkedItem = checkedItem.split(",");
    }
    let windowIndex = parent.layer.getFrameIndex(window.name);

    loadInvoiceInfos();

    /**
     * 加载产品的账单信息
     */
    function loadInvoiceInfos() {
        $.ajax({
            type: "POST",
            url: "/operate/getInvoice.action",
            dataType: 'json',
            data: {
                type: type,
                supplierId: entityId
            },
            success: function (data) {
                if (data.code === 200) {
                    let invoiceEle = $("<ul class='invoice-choose-list'></ul>");
                    let invoices = data.data;
                    for (let index = 0; index < invoices.length; index++) {
                        let invoiceInfo = invoices[index];
                        let value = invoiceInfo.value;
                        let itemId = value.split("###")[0].split(":")[1];
                        let checked = "";
                        if (util.isNotNull(checkedItem) && checkedItem.indexOf(itemId) >= 0) {
                            checked = " checked='' ";
                        }
                        let text = invoiceInfo.text;
                        let title = invoiceInfo.title;
                        title = title.replace(/(\r\n)|(\n)|(\r)/g, '<br>');
                        invoiceEle.append("<li><input type='checkbox'  name='invoice' lay-skin='primary' value='" + value + "' " + checked + " data-invoice-text='" + text + "' title='" + title + "'></li>");
                    }
                    $("#invoice-info-list").html(invoiceEle.prop("outerHTML"));
                    form.render();
                    $("#submit").click(function (event) {
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