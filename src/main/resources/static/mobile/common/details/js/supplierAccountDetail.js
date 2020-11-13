layui.use(['jquery', 'layer', 'element'], function () {
    let layer = layui.layer;
    let element = layui.element;
    // 获取请求的参数
    let productId = util.getUrlParam("productId");
    let flowClass = util.getUrlParam("flowClass");
    loadProductBillInfos();

    /**
     * 加载产品的账单信息
     */
    function loadProductBillInfos() {
        $.ajax({
            url: "/operate/readProductBills.action?temp=" + Math.random(),
            type: 'POST',
            dataType: 'json',
            data: {
                "productId": productId,
                "flowClass": flowClass,
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
        if (util.arrayNull(bills)) {
            return "";
        }
        let billInfoDoms = [];
        if (flowClass === '[BillPaymentFlow]') {
            for (let billIndex = 0; billIndex < bills.length; billIndex++) {
                let billItem = bills[billIndex];
                let itemJson = JSON.stringify(billItem);
                let id = billItem.id;
                let receivables = billItem.receivables;
                let actualPayable = billItem.actualpayables;
                let itemDom =
                    "<div class='bill-info-item' data-item-id='" + id + "' data-item-data='" + itemJson + "'>" +
                    "   <div>" +
                    "       <input type='checkbox' class='layui-form-checkbox'/>" +
                    "   </div>" +
                    "   <div>" +
                    "       <div>" + billItem.title + "</div>" +
                    "       <div>" +
                    "           <span>应付金额：</span><span>" + util.formatBlank(receivables, "0") + "</span>" +
                    "           <span>实付金额：</span><span>" + util.formatBlank(actualPayable, "0") + "</span>" +
                    "       </div>" +
                    "   </div>" +
                    "</div>";
                billInfoDoms.push(itemDom);
            }
        } else if (flowClass === "[RemunerationFlow]") {
            for (let billIndex = 0; billIndex < bills.length; billIndex++) {
                let billItem = bills[billIndex];
                let itemJson = JSON.stringify(billItem);
                let id = billItem.id;
                let receivables = billItem.receivables;
                let actualInvoiceAmount = billItem.actualInvoiceAmount;
                let itemDom =
                    "<div class='bill-info-item' data-item-id='" + id + "' data-item-data='" + itemJson + "'>" +
                    "   <div class='account-detail-item-check'>" +
                    "       <input type='checkbox' value='" + itemJson + "' class='layui-form-checkbox'/>" +
                    "   </div>" +
                    "   <div class='account-detail-item'>" +
                    "       <div class='item-title'>" + billItem.title + "</div>" +
                    "       <div class='item-detail'>" +
                    "           <span>应收佣金：</span><span>" + util.formatBlank(receivables, "0") + "</span>" +
                    "           <span>实收佣金：</span><span>" + util.formatBlank(actualInvoiceAmount, "0") + "</span>" +
                    "       </div>" +
                    "   </div>" +
                    "</div>";
                billInfoDoms.push(itemDom);
            }
        }
        $("#account-bill-list").html(billInfoDoms.join(""));
        element.render();
    }


    /**
     * 关闭方法
     */
    function close() {
        let closeBtn = $("#layui-layer" + windowIndex, window.parent.document).find("a[class*='layui-layer-close1']");
        $(closeBtn)[0].click();
    }
});