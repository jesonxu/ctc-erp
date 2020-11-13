layui.use(['jquery', 'layer', 'element', 'form'], function () {
    let layer = layui.layer;
    let element = layui.element;
    let form = layui.form;
    let windowIndex = parent.layer.getFrameIndex(window.name);
    // 获取请求的参数
    let bill = formBillData();
    let html = "";

    // 我司数据，后台统计的，不可修改
    html +=
        "<div class='bill-data' data-type='platform'>" +
        "   <div class='bill-data-title'>我司数据</div>" +
        "   <div class='bill-data-line'>" +
        "       <span>成功数：" + bill.platformSuccessCount + "</span>" +
        "   </div>" +
        "   <div class='bill-data-line'>" +
        "       <span>单价：" + bill.platformUnitPrice + "</span>" +
        "   </div>" +
        "   <div class='bill-data-line'>" +
        "       <span>金额：" + bill.platformAmount + "</span>" +
        "   </div>" +
        "</div>"; // platform

    // 客户数据，默认填充的是后台统计数据，可修改
    html +=
        "<div class='bill-data' data-type='customer'>" +
        "   <div class='bill-data-title'>客户数据</div>" +
        "   <div class='bill-data-line'>" +
        "       <span>成功数：</span>" +
        "       <input type='text' class='layui-input trigger-change' name='successCount' value='" + bill.customerSuccessCount + "'/>" +
        "   </div>" +
        "   <div class='bill-data-line'>" +
        "       <span>单价：</span>" +
        "       <input type='text' class='layui-input trigger-change' name='unitPrice' value='" + bill.customerUnitPrice + "'/>" +
        "   </div>" +
        "   <div class='bill-data-line'>" +
        "       <span>金额：</span>" +
        "       <input type='text' class='layui-input trigger-total' name='amount' value='" + bill.customerAmount + "'/>" +
        "   </div>" +
        "</div>"; // customer

    // 对完账后实际得出的数据，默认填充的是后台统计数据，可修改
    html +=
        "<div class='bill-data' data-type='checked'>" +
        "   <div class='bill-data-title'>对账数据</div>" +
        "   <div class='bill-data-line'>" +
        "       <span>成功数：</span>" +
        "       <input type='text' class='layui-input trigger-change' name='successCount' value='" + bill.checkedSuccessCount + "'/>" +
        "   </div>" +
        "   <div class='bill-data-line'>" +
        "       <span>单价：</span>" +
        "       <input type='text' class='layui-input trigger-change' name='unitPrice' value='" + bill.checkedUnitPrice + "'/>" +
        "   </div>" +
        "   <div class='bill-data-line'>" +
        "       <span>金额：</span>" +
        "       <input type='text' class='layui-input trigger-total' name='amount' value='" + bill.checkedAmount + "'/>" +
        "   </div>" +
        "</div>"; // checked

    html += "</div>"; // unchecked-bill-item

    $('div.edit-bill-data').html(html);
    bindEvent();

    /**
     * 封装账单数据
     */
    function formBillData() {
        return {
            'customerSuccessCount': util.getUrlParam('customerSuccessCount'),
            'customerUnitPrice': util.getUrlParam('customerUnitPrice'),
            'customerAmount': util.getUrlParam('customerAmount'),
            'checkedSuccessCount': util.getUrlParam('checkedSuccessCount'),
            'checkedUnitPrice': util.getUrlParam('checkedUnitPrice'),
            'checkedAmount': util.getUrlParam('checkedAmount'),
            'platformSuccessCount': util.getUrlParam('platformSuccessCount'),
            'platformUnitPrice': util.getUrlParam('platformUnitPrice'),
            'platformAmount': util.getUrlParam('platformAmount')
        }
    }

    /**
     * 绑定输入框事件
     */
    function bindEvent() {
        // 账单数据修改
        let billDataInput = $('.trigger-change');
        if (billDataInput.length > 0) {
            $(billDataInput).each(function () {
                let inputEle = $(this);
                inputEle.unbind().bind('change', function () {
                    takeUncheckedBillChange(inputEle);
                })
            })
        }

        let billAmountInput = $('.trigger-total');
        if (billAmountInput.length > 0) {
            $(billAmountInput).each(function () {
                let inputEle = $(this);
                inputEle.unbind().bind('change', function () {
                    let othis = $(this);
                    if (!$.isNumeric(othis.val())) {
                        layer.tips('金额只能是数字', othis, {tips: 1});
                        othis.val(0);
                    }
                })
            })
        }
    }

    /**
     * 编辑数量、单价之后，自动计算金额
     */
    function takeUncheckedBillChange(ele) {
        let parentEle = $(ele).parents('.bill-data');
        let successCountEle = parentEle.find('input[name=successCount]');
        if (!$.isNumeric(successCountEle.val())) {
            layer.tips('数量只能是数字', successCountEle, {tips: 1});
            $(successCountEle).val(0);
        } else if (!/^[0-9]*$/.test(successCountEle.val())) {
            layer.tips('数量只能是整数', successCountEle, {tips: 1});
            $(successCountEle).val(0);
        }

        let unitPriceEle = parentEle.find('input[name=unitPrice]');
        if (!$.isNumeric(unitPriceEle.val())) {
            layer.tips('单价只能是数字', unitPriceEle, {tips: 1});
            $(unitPriceEle).val('0.000000');
        } else {
            $(unitPriceEle).val(util.toFixed(unitPriceEle.val(), 6));
        }
        let amount = util.accMulti(parseInt(successCountEle.val()), parseFloat(unitPriceEle.val()));
        parentEle.find('input[name=amount]').val(util.toFixed(amount, 2));
    }

    /**
     * 关闭方法
     */
    function close() {
        let closeBtn = $("#layui-layer" + windowIndex, window.parent.document).find("a[class*='layui-layer-close1']");
        $(closeBtn)[0].click();
    }
});