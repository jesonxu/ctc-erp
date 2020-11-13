layui.use(['jquery', 'layer','form', 'element'], function () {
    let layer = layui.layer;
    let form = layui.form;
    // 获取请求的参数
    let productId = util.getUrlParam("productId");
    let checked = util.getUrlParam("checked");
    if (util.isNotNull(checked)){
        checked = checked.split(",");
    }

    let windowIndex = parent.layer.getFrameIndex(window.name);
    // 加载 未对账账单
    loadunWriteOffBillInfos();

    /**
     * 加载产品的账单信息
     */
    function loadunWriteOffBillInfos() {
        if (util.isNull(productId)){
            layer.msg("请先选择产品");
            return null;
        }
        $.ajax({
            url: "/customerOperate/readProductBills.action?temp=" + Math.random(),
            type: 'POST',
            dataType: 'json',
            data: {
                productId : productId,
                needOrder : 'T',
                flowClass : '[BillReceivablesFlow]'
            },
            success: function (result) {
                if (result.code === 200) {
                    //渲染账单信息
                    renderUnWriteOffBillInfo(result.data);
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
     * @param bills
     */
    function renderUnWriteOffBillInfo(bills) {
        let billInfoDoms = [];
        if (util.arrayNotNull(bills)){
            for (let billIndex = 0; billIndex < bills.length; billIndex++) {
                let billItem = bills[billIndex];
                let id = billItem.id;
                let receivables = billItem.receivables;
                let itemValue = JSON.stringify(billItem);
                let title =
                    "<div class=\"bill-title\">" + billItem.title + "</div>" +
                    "<div class=\"bill-detail\">" +
                    "    <span class=\"bill-label\">账单金额：</span>" +
                    "    <span class=\"bill-value\">" + util.formatBlank(receivables, "0") + "</span>" +
                    "</div>";
                let itemChecked = "";
                if (checked.indexOf(id) >= 0) {
                    itemChecked = " checked='true' ";
                }
                let itemDom =
                    "<li class='bill-info-item' data-item-id='" + id + "'>" +
                    "    <input type='checkbox' class='layui-form-checkbox' value='" + itemValue + "' lay-skin='primary' name='billInfo' title='" + title + "' " + itemChecked + "/>" +
                    "</li>";
                billInfoDoms.push(itemDom);
            }
        }else{
            billInfoDoms.push("<div class='empty-data'>暂无数据</div>")
        }
        $("#unwrite-off-bill-list").html(billInfoDoms.join(""));
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