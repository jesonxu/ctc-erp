layui.use(['jquery', 'layer', 'form', 'element'], function () {
    let layer = layui.layer;
    let form = layui.form;
    // 获取请求的参数
    let entityId = util.getUrlParam("entityId");
    let productId = util.getUrlParam("productId");
    let checked = util.getUrlParam("checked");
    if (util.isNotNull(checked)) {
        checked = checked.split(",");
    }

    let windowIndex = parent.layer.getFrameIndex(window.name);
    // 加载 未对账账单
    loadNotWriteOffReceiptInfos();

    /**
     * 加载产品的账单信息
     */
    function loadNotWriteOffReceiptInfos() {
        if (util.isNull(entityId)) {
            layer.msg("请先选择客户或供应商");
            return null;
        }
        if (util.isNull(productId)) {
            layer.msg("请先选择产品");
            return null;
        }
        $.ajax({
            url: "/fsExpenseIncome/readFsExpenseIncomesByProduct.action?temp=" + Math.random(),
            type: 'POST',
            dataType: 'json',
            data: {
                customerId: entityId,
                productId: productId
            },
            success: function (result) {
                if (result.code === 200) {
                    console.log(result)
                    //渲染账单信息
                    renderNotWriteOffReceiptInfo(result.data);
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
     * 渲染未对账账单信息
     * @param receipts
     */
    function renderNotWriteOffReceiptInfo(receipts) {

        let billInfoDoms = [];
        if (util.arrayNotNull(receipts)) {
            for (let receiptIndex = 0; receiptIndex < receipts.length; receiptIndex++) {
                let receiptItem = receipts[receiptIndex];
                let id = receiptItem.id;
                let itemValue = JSON.stringify(receiptItem);
                let title =
                    "<div class=\"receipt-line\">" +
                    "   <span class=\"receipt-title\">到款时间：</span>" +
                    "   <span>" + receiptItem.wTime + "</span>" +
                    "</div>" +
                    "<div class=\"receipt-line\">" +
                    "   <span class=\"receipt-title\">客户名称：</span>" +
                    "   <span>" + receiptItem.depict + "</span>" +
                    "</div>" +
                    "<div class=\"receipt-line\">" +
                    "   <span class=\"receipt-title\">收款金额：</span>" +
                    "   <span>" + receiptItem.cost + "</span>" +
                    "</div>" +
                    "<div class=\"receipt-line\">" +
                    "   <span class=\"receipt-title\">剩余金额：</span>" +
                    "   <span>" + receiptItem.remainRelatedCost + "</span>" +
                    "</div>";
                let itemChecked = "";
                if (checked.indexOf(id) >= 0) {
                    itemChecked = " checked='true' ";
                }
                let itemDom =
                    "<li class='receipt-info-item' data-item-id='" + id + "'>" +
                    "    <input type='checkbox' class='layui-form-checkbox' value='" + itemValue + "' lay-skin='primary' name='receiptInfo' title='" + title + "' " + itemChecked + "/>" +
                    "</li>";
                billInfoDoms.push(itemDom);
            }
        } else {
            billInfoDoms.push("<div class='empty-data'>暂无数据</div>");
        }
        $("#not-write-off-receipt-list").html(billInfoDoms.join(""));
        form.render();
    }


    /**
     * 关闭方法
     */
    function close() {
        let closeBtn = $("#layui-layer" + windowIndex, window.parent.document).find("a[class*='layui-layer-close1']");
        $(closeBtn)[0].click();
    }
});