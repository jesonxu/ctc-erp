layui.use(['jquery', 'layer', 'element', 'form'], function () {
    let layer = layui.layer;
    let element = layui.element;
    let form = layui.form;
    let windowIndex = parent.layer.getFrameIndex(window.name);
    // 获取请求的参数
    let productId = util.getUrlParam("productId");
    let flowClass = util.getUrlParam("flowClass");
    // 已选的项
    let checkedItems = util.getUrlParam("checkedItem");
    if (util.isNotNull(checkedItems)) {
        checkedItems = checkedItems.split(",");
    }
    if (util.isNull(checkedItems)) {
        checkedItems = [];
    }
    loadProductBillInfos();

    /**
     * 加载产品的账单信息
     */
    function loadProductBillInfos() {
        $.ajax({
            url: "/customerOperate/readProductBills.action?temp=" + Math.random(),
            type: 'POST',
            dataType: 'json',
            data: {
                "productId": productId,
                "flowClass": flowClass
            },
            success: function (result) {
                if (result.code === 200) {
                    //渲染账单信息
                    renderBillInfo(result.data);
                } else {
                    // 加载异常
                    layer.msg(result.msg);
                }
            },
            error: function (result) {
                layer.msg("数据加载错误");
            }
        });
        $("#submit").click(function (event) {
            close();
        });
    }

    /**
     * 渲染账单信息
     * @param bills
     */
    function renderBillInfo(bills) {
        let billInfoDoms = [];
        if (util.arrayNull(bills)) {
            billInfoDoms.push("<div class='empty-data'>暂无数据</div>");
        } else {
            if (flowClass === '[BillReceivablesFlow]') {
                for (let billIndex = 0; billIndex < bills.length; billIndex++) {
                    let billItem = bills[billIndex];
                    let itemJson = JSON.stringify(billItem);
                    let id = billItem.id;
                    let checked = "";
                    if (checkedItems.indexOf(id) >= 0) {
                        checked = " checked ";
                    }
                    let receivables = billItem.receivables;
                    let actualReceivables = billItem.actualReceivables;
                    let itemDom =
                        "<div class='layui-form-item bill-info-item' data-item-id='" + id + "'>" +
                        "   <div class='account-detail-item-check'>" +
                        "       <input id='" + id + "' type='checkbox' name='accountItem' value='" + itemJson + "' lay-skin='primary' " + checked + " />" +
                        "   </div>" +
                        "   <div class='account-detail-item'>" +
                        "       <div class='item-title'>" + billItem.title + "</div>" +
                        "       <div class='item-detail'>" +
                        "           <span class='should'>应收：" + util.formatBlank(receivables, "0") + "元</span>" +
                        "           <span class='actual'>实收：" + util.formatBlank(actualReceivables, "0") + "元</span>" +
                        "       </div>" +
                        "   </div>" +
                        "</div>";
                    billInfoDoms.push(itemDom);
                }
            } else if (flowClass === "[InvoiceFlow]") {
                for (let billIndex = 0; billIndex < bills.length; billIndex++) {
                    let billItem = bills[billIndex];
                    let itemJson = JSON.stringify(billItem);
                    let id = billItem.id;
                    let receivables = billItem.receivables;
                    let actualInvoiceAmount = billItem.actualInvoiceAmount;
                    let itemDom =
                        "<div class='layui-form-item bill-info-item' data-item-id='" + id + "'>" +
                        "   <div class='account-detail-item-check'>" +
                        "       <input id='" + id + "' type='checkbox' name='accountItem' value='" + itemJson + "' " + checked + " />" +
                        "   </div>" +
                        "   <div class='account-detail-item'>" +
                        "       <div class='item-title'>" + billItem.title + "</div>" +
                        "       <div class='item-detail'>" +
                        "           <span class='should'>应开：" + util.formatBlank(receivables, "0") + "元</span>" +
                        "           <span class='actual'>实开：" + util.formatBlank(actualInvoiceAmount, "0") + "元</span>" +
                        "       </div>" +
                        "   </div>" +
                        "</div>";
                    billInfoDoms.push(itemDom);
                }
            }
        }
        $("#account-bill-list").html(billInfoDoms.join(""));
        form.render();
        bindItemEvent();
    }


    /**
     * 绑定账单项
     */
    function bindItemEvent() {
        let billItems = $("div[data-item-id]");
        if (billItems.length > 0) {
            for (let itemIndex = 0; itemIndex < billItems.length; itemIndex++) {
                let item = billItems[itemIndex];
                $(item).click(function () {
                    let dataItemId = $(this).attr("data-item-id");
                    let item = $("#" + dataItemId);
                    if (item[0].hasAttribute("checked")) {
                        item.removeAttr("checked")
                    } else {
                        $(item).attr("checked", "");
                    }
                    form.render();
                });
            }
        }
    }

    /**
     * 关闭方法
     */
    function close() {
        let closeBtn = $("#layui-layer" + windowIndex, window.parent.document).find("a[class*='layui-layer-close1']");
        $(closeBtn)[0].click();
    }
});