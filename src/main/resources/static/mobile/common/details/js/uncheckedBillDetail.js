layui.use(['jquery', 'layer', 'element', 'form'], function () {
    let layer = layui.layer;
    let element = layui.element;
    let form = layui.form;
    let windowIndex = parent.layer.getFrameIndex(window.name);
    // 获取请求的参数
    let entityId = util.getUrlParam("entityId");
    let flowEntId = util.getUrlParam("flowEntId");
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
            url: "/bill/readUncheckedBills.action?temp=" + Math.random(),
            type: 'POST',
            dataType: 'json',
            data: {
                customerId: entityId,
                flowEntId: flowEntId
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
            for (let billIndex = 0; billIndex < bills.length; billIndex++) {
                let billItem = bills[billIndex];
                let itemJson = JSON.stringify(billItem);
                let id = billItem.id;
                let checked = "";
                if (checkedItems.indexOf(id) >= 0) {
                    checked = " checked ";
                }
                let itemTitle =
                    "<div class=\"bill-item-detail\">" +
                    "    <div class=\"bill-title\">" + billItem.title + "</div>" +
                    "    <div class=\"bill-detail\">" +
                    "       <div class=\"bill-detail-line\">" +
                    "           <label>我司数据：</label>" +
                    "           <span>" + util.formatBlank(billItem.platformSuccessCount, "0") + "</span>" +
                    "           <span>X</span>" +
                    "           <span>" + util.formatBlank(billItem.platformUnitPrice, "0.000000") + "</span>" +
                    "           <span>=</span>" +
                    "           <span>" + util.formatBlank(billItem.platformAmount, "0.00") + "</span>" +
                    "       </div>" +
                    "       <div class=\"bill-detail-line\">" +
                    "           <label>客户数据：</label>" +
                    "           <span>" + util.formatBlank(billItem.customerSuccessCount, "0") + "</span>" +
                    "           <span>X</span>" +
                    "           <span>" + util.formatBlank(billItem.customerUnitPrice, "0.000000") + "</span>" +
                    "           <span>=</span>" +
                    "           <span>" + util.formatBlank(billItem.customerAmount, "0.00") + "</span>" +
                    "       </div>" +
                    "       <div class=\"bill-detail-line\">" +
                    "           <label>对账数据：</label>" +
                    "           <span>" + util.formatBlank(billItem.checkedSuccessCount, "0") + "</span>" +
                    "           <span>X</span>" +
                    "           <span>" + util.formatBlank(billItem.checkedUnitPrice, "0.000000") + "</span>" +
                    "           <span>=</span>" +
                    "           <span>" + util.formatBlank(billItem.checkedAmount, "0.00") + "</span>" +
                    "       </div>" +
                    "    </div>" +
                    "</div>" ;


                let itemDom =
                    "<div class='layui-form-item bill-info-item' data-bill-id='" + id + "'>" +
                    "   <div class='bill-item-check'>" +
                    "       <input id='" + id + "' type='checkbox' name='billItem' value='" + itemJson + "' title='" + itemTitle + "' lay-skin='primary' " + checked + " />" +
                    "   </div>" +
              /*      "   <div class='bill-item-detail'>" +
                    "       <div class='bill-detail'>" +
                    "           <span>我司数据：</span>" +
                    "           <span>" + util.formatBlank(billItem.platformSuccessCount, "0") + "&nbsp;X&nbsp;</span>" +
                    "           <span>" + util.formatBlank(billItem.platformUnitPrice, "0.000000") + "&nbsp;=&nbsp;</span>" +
                    "           <span>" + util.formatBlank(billItem.platformAmount, "0.00") + "</span><br>" +
                    "           <span>客户数据：</span>" +
                    "           <span>" + util.formatBlank(billItem.customerSuccessCount, "0") + "&nbsp;X&nbsp;</span>" +
                    "           <span>" + util.formatBlank(billItem.customerUnitPrice, "0.000000") + "&nbsp;=&nbsp;</span>" +
                    "           <span>" + util.formatBlank(billItem.customerAmount, "0.00") + "</span><br>" +
                    "           <span>对账数据：</span>" +
                    "           <span>" + util.formatBlank(billItem.checkedSuccessCount, "0") + "&nbsp;X&nbsp;</span>" +
                    "           <span>" + util.formatBlank(billItem.checkedUnitPrice, "0.000000") + "&nbsp;=&nbsp;</span>" +
                    "           <span>" + util.formatBlank(billItem.checkedAmount, "0.00") + "</span><br>" +
                    "       </div>" +
                    "   </div>" +*/
                    "</div>";
                billInfoDoms.push(itemDom);
            }
        }
        $("#unchecked-bill-list").html(billInfoDoms.join(""));
        form.render();
        // bindItemEvent();
    }


    /**
     * 绑定账单项
     */
    function bindItemEvent() {
        let billItems = $("div[data-bill-id]");
        if (billItems.length > 0) {
            for (let itemIndex = 0; itemIndex < billItems.length; itemIndex++) {
                let item = billItems[itemIndex];
                $(item).click(function () {
                    let billId = $(this).attr("data-bill-id");
                    let item = $("#" + billId);
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