/**
 *发票信息标签 23
 */
(function (window, factory) {
    window.InvoiceInfoLabel = factory();
})(window, function () {

    /**
     * 展示名字 PRODUCT_BILL_LABEL_NAME
     */
    const PRODUCT_BILL_LABEL_NAME = {
        //付款流程
        "[BillPaymentFlow]": {
            payableName: "应付金额：",
            actualPayableName: "已付金额：",
            leftShouldPayName: "剩余应付：",
            thisPaymentName: "本次付款：",
            totalName: "合计付款："
        },
        // 收款流程
        "[RemunerationFlow]": {
            payableName: "应收佣金：",
            actualPayableName: "已收佣金：",
            leftShouldPayName: "剩余收款：",
            thisPaymentName: "本次收款：",
            totalName: "合计收款："
        },
        //销售收款流程
        "[BillReceivablesFlow]": {
            payableName: "应收金额：",
            actualPayableName: "已收金额：",
            leftShouldPayName: "剩余应收：",
            thisPaymentName: "本次收款：",
            totalName: "合计收款："
        },
        // 发票流程
        "[InvoiceFlow]": {
            payableName: "应开金额：",
            actualPayableName: "已开金额：",
            leftShouldPayName: "剩余应开：",
            thisPaymentName: "本次开票：",
            totalName: "合计开票："
        },
    };

    /**
     * 标签名称
     */
    const PRODUCT_BILL_INPUT_NAME = {
        //付款流程
        "[BillPaymentFlow]": {
            payableName: "payables",
            actualPayableName: "actualpayables",
            leftShouldPayName: "left_should_pay",
            thisPaymentName: "thisPayment"
        },

        // 收款流程
        "[RemunerationFlow]": {
            payableName: "receivables",
            actualPayableName: "actualReceivables",
            leftShouldPayName: "left_should_receive",
            thisPaymentName: "thisReceivables"
        },

        //销售收款流程
        "[BillReceivablesFlow]": {
            payableName: "receivables",
            actualPayableName: "actualReceivables",
            leftShouldPayName: "left_should_receive",
            thisPaymentName: "thisReceivables"
        },

        // 发票流程
        "[InvoiceFlow]": {
            payableName: "receivables",
            actualPayableName: "actualInvoiceAmount",
            leftShouldPayName: "left_should_receive：",
            thisPaymentName: "thisReceivables"
        }
    };


    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let InvoiceInfoLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【发票信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【发票信息标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 值为空的返回
     * @returns {string}
     */
    InvoiceInfoLabel.prototype.blank = function () {
        return this.name + ":无";
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    InvoiceInfoLabel.prototype.toText = function (value, defaultValue, flowEntId, flowClass, flowId) {
        if (util.isNull(value)) {
            return this.blank();
        }
        let invoiceItems = (typeof value == 'object') ? value : JSON.parse(value);
        if (util.arrayNull(invoiceItems)) {
            return this.blank();
        }
        // 本次收付款
        let thisTimePay = 0;
        // 展示名称
        let showName = PRODUCT_BILL_LABEL_NAME[flowClass];
        // 输入框名称
        let inputName = PRODUCT_BILL_INPUT_NAME[flowClass];
        let invoiceInfoDom = "";
        $.each(invoiceItems, function (index, productBill) {
            // 应该 支付|收款
            let payableName = inputName.payableName;
            let payable = util.formatBlank(productBill[payableName], 0);

            // 实际(已经) 支付|收款
            let actualPayableName = inputName.actualPayableName;
            let actualPayable = util.formatBlank(productBill[actualPayableName], 0);

            // 本次 支付|收款
            let thisPaymentName = inputName.thisPaymentName;
            let thisPayment = util.formatBlank(productBill[thisPaymentName], 0);

            // 剩余应该 支付|收款（固定不变[要求]）
            let left_should_pay_name = inputName.leftShouldPayName;
            let leftShouldPay = parseFloat(payable) - parseFloat(actualPayable);

            // 去除未审核和驳回的账单金额
            let type = '';
            if (flowClass === '[BillReceivablesFlow]') {
                type = 'thisReceivables';
            }
            if (util.isNotNull(type)) {
                $.ajax({
                    type: "POST",
                    url: '/operate/queryApplying.action?v=' + new Date().getTime(),
                    data: {
                        flowId: flowId,
                        billId: productBill.id,
                        type: type,
                        flowEntId: flowEntId
                    },
                    async: false,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 200 || data.code === "200") {
                            leftShouldPay -= parseFloat(data.msg);
                        }
                    }
                });
            }

            // 本次应付（总）
            thisTimePay += parseFloat(thisPayment);
            invoiceInfoDom += "<br/>" + productBill.title + "<br/>";
            invoiceInfoDom += showName.payableName + payable.toFixed(2) + "元，";
            invoiceInfoDom += showName.actualPayableName + actualPayable.toFixed(2) + "元，";
            invoiceInfoDom += showName.leftShouldPayName + leftShouldPay.toFixed(2) + "元，";
            invoiceInfoDom += showName.thisPaymentName + thisPayment.toFixed(2) + "元";
        });
        invoiceInfoDom += "<br/>" + showName.totalName + util.formatNum(thisTimePay, 2) + "元";
        return this.name + "：" + invoiceInfoDom;
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param flowId 流程id
     * @param flowClass 流程类型
     * @param productId 产品ID
     */
    InvoiceInfoLabel.prototype.render = function (flowEle, value, required, flowId, flowClass, productId) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【发票信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        // 为了回显
        this.data = util.formatBlank(value);
        this.productId = util.formatBlank(productId);
        try {
            if (util.isNotNull(this.data)) {
                // 数组 对象
                let dataArr = JSON.parse(this.data);
                let dataIds = [];
                $(dataArr).each(function (index, item) {
                    dataIds.push(item.id);
                });
                this.checkedIds = dataIds;
            }
        } catch (e) {
            console.log("转换原有的值异常被捕获", e);
        }
        let labelDom =
            "<div class='layui-form-item label-type-invoice-info' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <button name='" + this.id + "' type='button' class='layui-btn layui-btn-primary'>请选择" + this.name + "</button>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent(flowId, flowClass);
    };

    /**
     * 绑定点击事件
     */
    InvoiceInfoLabel.prototype.bindEvent = function (flowId, flowClass) {
        let btn = $(this.flowEle).find("div[data-label-id='" + this.id + "']").find("button[name='" + this.id + "']");
        let mine = this;
        $(btn).click(function (event) {
            if (util.isNull(mine.productId) && typeof getProductId === "function") {
                // 用于申请
                mine.productId = getProductId();
            }
            if (util.isNull(mine.productId)) {
                layer.tips("请先选择产品", btn[0], {tips: 1});
                return null;
            }
            let checkedIds = mine.checkedIds;
            if (util.isNotNull(checkedIds)) {
                checkedIds = checkedIds.join(",");
            }
            // 打开发票信息详情页面
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/invoiceItemDetail?productId=" + mine.productId + "&flowClass=" + encodeURI(flowClass) + "&checked=" + encodeURI(checkedIds),
                cancel: function (index, layero) {
                    let checkedItems = $($(layero[0]).find("iframe").contents()).find("input[name='invoice']:checked");
                    let checkedDatas = [];
                    let checkedIds = [];
                    if (checkedItems.length > 0) {
                        $(checkedItems).each(function (itemIndex, item) {
                            let checkedItem = $(item).val();
                            checkedDatas.push(checkedItem);
                            checkedIds.push(JSON.parse(checkedItem).id);
                        });
                    }
                    mine.data = checkedDatas;
                    mine.checkedIds = checkedIds;
                    // 需要渲染 数据
                    mine.renderSelectedInvoice(checkedDatas, flowId, flowClass);
                }
            });
            layer.full(index);
        });
    };

    /**
     * 渲染选择了的
     * @param invoices
     * @param flowId 流程ID
     * @param flowClass 流程类型
     */
    InvoiceInfoLabel.prototype.renderSelectedInvoice = function (invoices, flowId, flowClass) {
        if (util.arrayNotNull(invoices)) {
            let billInfoDom = [];
            billInfoDom.push("<div data-invoice-info-id='" + this.id + "' class='invoice-detail'>");
            // 本次收付款
            let thisTimePay = 0;
            // 展示名称
            let labelName = PRODUCT_BILL_LABEL_NAME[flowClass];
            // 输入框名称
            let inputName = PRODUCT_BILL_INPUT_NAME[flowClass];
            // 遍历组装内容
            for (let billIndex = 0; billIndex < invoices.length; billIndex++) {
                let productBill = JSON.parse(invoices[billIndex]);
                // 应该 支付|收款|开票
                let payableName = inputName.payableName;
                let payable = parseFloat(util.formatBlank(productBill[payableName], 0));

                // 实际(已经) 支付|收款|开票
                let actualPayableName = inputName.actualPayableName;
                let actualPayable = parseFloat(util.formatBlank(productBill[actualPayableName], 0));

                // 本次 支付|收款|开票
                let thisPaymentName = inputName.thisPaymentName;
                let thisPayment = parseFloat(util.formatBlank(productBill[thisPaymentName], 0));

                // 剩余应该 支付|收款（固定不变[要求]）
                let leftShouldPayName = inputName.leftShouldPayName;
                let leftShouldPay = payable - actualPayable;

                // 去除未审核和驳回的账单金额
                let type = '';
                if (flowClass === '[BillPaymentFlow]') {
                    type = 'thisPayment';
                } else if (flowClass === 'RemunerationFlow' || flowClass === '[BillReceivablesFlow]' || flowClass === '[InvoiceFlow]') {
                    type = 'thisReceivables';
                }
                if (type !== '') {
                    $.ajax({
                        type: "POST",
                        url: '/operate/queryApplying.action?v=' + new Date().getTime(),
                        data: {
                            flowId: flowId,
                            billId: productBill.id,
                            type: type,
                            flowEntId: ''
                        },
                        async: false,
                        dataType: 'json',
                        success: function (data) {
                            if (data.code === 200 || data.code === "200") {
                                leftShouldPay -= parseFloat(data.msg);
                            }
                        }
                    });
                }

                leftShouldPay = leftShouldPay.toFixed(3);
                leftShouldPay = leftShouldPay.substring(0, leftShouldPay.length - 1);
                // 本次应付（总）
                thisTimePay = parseFloat(thisTimePay) + parseFloat(leftShouldPay);
                let title = productBill.title;
                let itemId = productBill.id;
                billInfoDom.push(
                    "<div class='invoice-item' data-invoice-item-id='" + itemId + "' data-invoice-item-title='" + title + "'>" +
                    "    <span class='product-bill-title'>" + title + "</span>" +
                    "    <div class='invoice-item-line'>" +
                    "       <span>" + labelName.payableName + "</span>" +
                    "       <input name='payable' data-param-name='" + payableName + "' class='layui-input' value='" + payable.toFixed(2) + "' disabled/>" +
                    "    </div>" +
                    "    <div class='invoice-item-line'>" +
                    "       <span>" + labelName.actualPayableName + "</span>" +
                    "       <input name='actualPayable' data-param-name='" + actualPayableName + "' class='layui-input' value='" + actualPayable.toFixed(2) + "' disabled/>" +
                    "    </div>" +
                    "    <div class='invoice-item-line'>" +
                    "       <span>" + labelName.leftShouldPayName + "</span>" +
                    "       <input name ='leftShouldPay' data-param-name='" + leftShouldPayName + "' class='layui-input' value='" + leftShouldPay + "'  disabled/>" +
                    "    </div>" +
                    "    <div class='invoice-item-line'>" +
                    "       <span>" + labelName.thisPaymentName + "</span>" +
                    "       <input name='thisPayment' data-param-name='" + thisPaymentName + "' class='layui-input' data-left='" + leftShouldPay + "' value='" + leftShouldPay + "'/>" +
                    "    </div>" +
                    "    <div class='invoice-item-line'>" +
                    "       <button type='button' data-delete-id='" + itemId + "' class='layui-btn layui-btn-xs layui-btn-primary'>" +
                    "       <i class='layui-icon layui-icon-delete'></i>删除" +
                    "       </button>" +
                    "	 </div>" +
                    "</div>");
            }
            // 合计
            billInfoDom.push("<i class='invoice-count-tip'>" + labelName.totalName +
                "<span data-this-pay='" + this.id + "' class='bill-this-time-pay'>" + util.formatNum(thisTimePay, 2) + "</span>&nbsp;元</i>");
            billInfoDom.push("</div>");
            // 删除原有的标签
            $(this.flowEle).find("div[data-invoice-info-id='" + this.id + "']").remove();
            $(this.flowEle).find("button[name='" + this.id + "']").after(billInfoDom.join(""));
            // 绑定改变事件
            this.bindBillItemEvent();
        }
    };

    /**
     * 绑定账单详情项事件
     */
    InvoiceInfoLabel.prototype.bindBillItemEvent = function () {
        let billDetailEle = $(this.flowEle).find("div[data-invoice-info-id='" + this.id + "']");
        let mine = this;
        if (billDetailEle.length > 0) {
            // 删除按钮
            let deleteBtnEleArr = $(billDetailEle).find("button[data-delete-id]");
            if (deleteBtnEleArr.length > 0) {
                $(deleteBtnEleArr).each(function (index, item) {
                    $(item).click(function (event) {
                        let deleteBillItem = $(this).attr("data-delete-id");
                        // 点击删除事件
                        $(this).parent().parent().remove();
                        let afterBillItems = [];
                        $(mine.datas).each(function (billIndex, billItem) {
                            let billInfo = JSON.parse(billItem);
                            if (deleteBillItem !== billInfo.id) {
                                afterBillItems.push(billItem);
                            }
                        });
                        mine.datas = afterBillItems;
                        mine.reComputeTotal();
                    });
                });
            }
            // 绑定填写的失去焦点事件
            let thisPayEleArr = $(billDetailEle).find("input[name='thisPayment']");
            if (thisPayEleArr.length > 0) {
                $(thisPayEleArr).each(function (index, item) {
                    $(item).blur(function (event) {
                        let dataLeft = parseFloat(util.formatBlank($(this).attr("data-left"))).toFixed(2);
                        let thisTimePay = $(this).val();
                        if (util.isNull(thisTimePay)) {
                            layer.tips("不能为空", $(this), {tips: 1});
                            $(this).val(dataLeft);
                            return;
                        }
                        if (!$.isNumeric(thisTimePay)) {
                            layer.tips("只能填写数字", $(this), {tips: 1});
                            $(this).val(dataLeft);
                            return;
                        }
                        if (parseFloat(thisTimePay) > dataLeft) {
                            layer.tips("不能大于" + dataLeft, $(this), {tips: 1});
                            $(this).val(dataLeft);
                            return;
                        }
                        if (parseFloat(thisTimePay) <= 0) {
                            layer.tips("必须大于0", $(this), {tips: 1});
                            $(this).val(dataLeft);
                            return;
                        }
                        mine.reComputeTotal();
                    });
                });
            }
        }
    };

    /**
     * 重新计算总计
     */
    InvoiceInfoLabel.prototype.reComputeTotal = function () {
        let billDetailEle = $(this.flowEle).find("div[data-invoice-info-id='" + this.id + "']");
        let total = 0;
        if (billDetailEle.length > 0) {
            let thisPayEleArr = $(billDetailEle).find("input[name='thisPayment']");
            if (thisPayEleArr.length > 0) {
                $(thisPayEleArr).each(function (index, item) {
                    total += parseFloat(util.formatBlank($(item).val(), 0));
                });
            } else {
                // 全部都删除后，就不需要再展示了
                $(billDetailEle).remove();
            }
        }
        $(this.flowEle).find("span[data-this-pay='" + this.id + "']").text(util.formatNum(total, 2));
    };


    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    InvoiceInfoLabel.prototype.getValue = function () {
        return this.data;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    InvoiceInfoLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    InvoiceInfoLabel.prototype.verify = function () {
        let tipEle = $(this.flowEle).find("button[name='" + this.id + "']")[0];
        // 如果是必须的 就需要检查是否已经选择
        if (util.isTrue(this.required) && util.isNull(this.datas)) {
            layer.msg(this.name + "必须选择");
            return false;
        }
        let billDetailEle = $(this.flowEle).find("div[data-invoice-info-id='" + this.id + "']");
        if (billDetailEle.length > 0) {
            let thisPayEleArr = $(billDetailEle).find("input[name='thisPayment']");
            if (thisPayEleArr.length > 0) {
                for (let index = 0; index < thisPayEleArr.length; index++) {
                    let item = thisPayEleArr[index];
                    let itemName = util.formatBlank($(item).attr("data-param-name"));
                    let dataLeft = parseFloat(util.formatBlank($(item).attr("data-left")));
                    let thisTimePay = $(item).val();
                    if (util.isNull(thisTimePay)) {
                        layer.msg(this.name + itemName + "不能为空");
                        return false;
                    }
                    if (!$.isNumeric(thisTimePay)) {
                        layer.msg(this.name + itemName + "只能填写数字");
                        return false;
                    }
                    if (parseFloat(thisTimePay) > dataLeft) {
                        layer.msg(this.name + itemName + "不能大于" + dataLeft);
                        return false;
                    }
                    if (parseFloat(thisTimePay) <= 0) {
                        layer.msg(this.name + itemName + "必须大于0");
                        return false;
                    }
                }
            }
        }
        return true;
    };
    return InvoiceInfoLabel;
});