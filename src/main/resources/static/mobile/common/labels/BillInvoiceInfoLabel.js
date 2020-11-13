/**
 * 账单开票信息标签 30
 */
(function (window, factory) {
    window.BillInvoiceInfoLabel = factory();
})(window, function () {
    // 不同流程的标签名称
    const productBillLabelName = {
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
        }
    };

    // 标签名称
    const productBillInputName = {
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
    let BillInvoiceInfoLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【账单开票信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【账单开票信息标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     * @param value 标签值
     * @param flowId 流程ID
     * @param flowEntId 流程实体ID
     * @param flowClass 流程类型
     * @returns {string}
     */
    BillInvoiceInfoLabel.prototype.toText = function (value, flowId, flowEntId, flowClass) {
        console.log("账单开票信息标签：name:" + this.name + " - value:" + value);

        if (util.isNull(value)) {
            return this.blank();
        }
        let productBills = (typeof value == 'object') ? value : JSON.parse(value);
        if (productBills.length === 0) {
            return this.blank();
        }
        // 本次收付款
        let thisTimePay = 0;
        // 展示名称
        let showName = productBillLabelName[flowClass];
        // 输入框名称
        let inputName = productBillInputName[flowClass];
        // 总计名称
        let totalName = showName.totalName;
        let html = "";
        $.each(productBills, function (index, productBill) {
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
            let leftShouldPayName = inputName.leftShouldPayName;
            let leftShouldPay = parseFloat(payable) - parseFloat(actualPayable);

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
            html += "<div>" + productBill.title + "<br/>";
            html += showName.payableName + util.thousand(parseFloat(payable).toFixed(2)) + "元，<br/>";
            html += showName.actualPayableName + util.thousand(parseFloat(actualPayable).toFixed(2)) + "元，<br/>";
            html += showName.leftShouldPayName + util.thousand(parseFloat(leftShouldPay).toFixed(2)) + "元，<br/>";
            html += showName.thisPaymentName + util.thousand(parseFloat(thisPayment).toFixed(2)) + "元</div>";
        });
        html += totalName + util.formatNum(thisTimePay, 2) + "元";
        return this.name + ":<div class='flow-record-2-content'>" + html + "</div>";
    };

    /**
     * 值为空的返回
     * @returns {string}
     */
    BillInvoiceInfoLabel.prototype.blank = function () {
        return this.name + "：无";
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param flowId 流程id
     * @param flowClass 流程
     * @param entityId 主体ID
     */
    BillInvoiceInfoLabel.prototype.render = function (flowEle, value, required, flowId, flowClass, entityId) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【账单开票信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        this.entityId = util.formatBlank(entityId);
        this.data = [];
        if (util.isNotNull(value)) {
            this.data = JSON.parse(value);
        }
        let labelDom =
            "<div class='layui-form-item label-type-bill-invoice' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <button name='" + this.id + "' type='button' class='layui-btn layui-btn-primary'>请选择" + this.name + "</button>" +
            "        <div class='bill-invoice-items' data-bill-invoice-id='" + this.id + "'></div>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent(flowId, flowClass);
        if (this.data.length > 0) {
            this.renderChoosedItems();
        }
    };

    /**
     * 绑定点击事件
     * @param flowId 流程id
     * @param flowClass 流程类型
     */
    BillInvoiceInfoLabel.prototype.bindEvent = function (flowId, flowClass) {
        let btn = $($(this.flowEle).find("div[data-label-id='" + this.id + "']")).find("button[name='" + this.id + "']");
        let mine = this;
        $(btn).click(function () {
            if (util.isNull(mine.entityId) && typeof getEntityId === "function") {
                mine.entityId = getEntityId();
            }
            if (util.isNull(mine.entityId)) {
                layer.tips("请先选择主体（客户或供应商）", btn, {tips: 1});
                return false;
            }
            let infoArr = [];
            if (util.arrayNotNull(mine.data)) {
                $(mine.data).each(function (dataIndex, dataItem) {
                    infoArr.push(dataItem.id);
                });
            }
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/billInvoiceDetail?entityId=" + mine.entityId + "&flowId=" + flowId + "&flowClass=" + encodeURI(flowClass) + "&checked=" + infoArr.join(",") + "&time=" + new Date().getTime(),
                cancel: function (index, layero) {
                    let checkedItems = $($(layero[0]).find("iframe").contents()).find("input[name='billInvoice']:checked");
                    let checkedInvoiceArr = [];
                    if (checkedItems.length > 0) {
                        $(checkedItems).each(function (index, item) {
                            let checkedValue = $(item).val();
                            checkedInvoiceArr.push(JSON.parse(checkedValue));
                        });
                    }
                    mine.data = checkedInvoiceArr;
                    mine.renderChoosedItems();
                }
            });
            layer.full(index);
        });
    };

    /**
     * 渲染已经选择的内容
     */
    BillInvoiceInfoLabel.prototype.renderChoosedItems = function () {
        let invoiceItemsDom = [];
        let thisTimeTotal = 0;
        if (util.arrayNotNull(this.data)) {
            $(this.data).each(function (index, item) {
                let itemId = item.id;
                let title = item.title;
                let total = parseFloat(util.formatBlank(item.receivables, 0)).toFixed(2);
                // 已开金额
                let done = util.formatBlank(item.actualInvoiceAmount, 0);
                // 使用了的
                let usedAmount = util.formatBlank(item.usedAmount, 0);
                let leftNum = parseFloat(parseFloat(total) - parseFloat(done) - parseFloat(usedAmount));
                if (util.isNotNull(item.thisReceivables)) {
                    // 兼容回显数据
                    leftNum = parseFloat(item.thisReceivables);
                }
                let left = leftNum.toFixed(2);
                thisTimeTotal += leftNum;
                invoiceItemsDom.push(
                    "<div class='bill-invoice-item' data-bill-invoice-item-id='" + itemId + "'>" +
                    "    <span class='bill-invoice-title'>" + title + "</span>" +
                    "    <div class='bill-invoice-item-line'>" +
                    "       <span>应开金额：</span>" +
                    "       <input name='payable' class='layui-input' value='" + total + "' disabled/>" +
                    "    </div>" +
                    "    <div class='bill-invoice-item-line'>" +
                    "       <span>可开金额：</span>" +
                    "       <input name='actualPayable' class='layui-input' value='" + left + "' disabled/>" +
                    "    </div>" +
                    "    <div class='bill-invoice-item-line'>" +
                    "       <span>本次开票金额：</span>" +
                    "       <input name='thisPayment' class='layui-input' data-left='" + left + "' value='" + left + "'/>" +
                    "    </div>" +
                    "    <div class='bill-invoice-item-line'>" +
                    "       <button type='button' data-delete-id='" + itemId + "' class='layui-btn layui-btn-xs layui-btn-primary'>" +
                    "           <i class='layui-icon layui-icon-delete'></i>删除" +
                    "       </button>" +
                    "	 </div>" +
                    "</div>");
            });
        }
        if (invoiceItemsDom.length > 0) {
            invoiceItemsDom.push("<i class='bill-invoice-count-tip'>账单合计：<span data-this-pay='" + this.id
                + "' class='bill-invoice-this-time-pay' data-bill-invoice-total='" + thisTimeTotal.toFixed(2) + "'>"
                + util.thousand(thisTimeTotal.toFixed(2))
                + "</span>&nbsp;元</i>");
        }
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let detailEle = $(labelEle).find("div[data-bill-invoice-id='" + this.id + "']");
        $(detailEle).html(invoiceItemsDom.join(""));
        this.bindItemEvent();
    };

    /**
     * 绑定事件
     */
    BillInvoiceInfoLabel.prototype.bindItemEvent = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let detailEle = $(labelEle).find("div[data-bill-invoice-id='" + this.id + "']");
        let dataItems = $(detailEle).find("div[data-bill-invoice-item-id]");
        if (dataItems.length > 0) {
            let mine = this;
            $(dataItems).each(function (index, item) {
                let invoiceItemId = $(item).attr("data-bill-invoice-item-id");
                // 按钮
                let itemBtn = $(item).find("button[data-delete-id='" + invoiceItemId + "']");
                $(itemBtn).click(function (event) {
                    let thisItem = $(this).parent().parent();
                    $(thisItem).remove();
                    mine.renderTotal();
                });

                // 输入框（需要校验和渲染总数）
                let thisPaymentEle = $(item).find("input[name='thisPayment']");
                // 剩余应开金额
                let billInvoiceLeft = $(thisPaymentEle).attr("data-left");
                $(thisPaymentEle).blur(function (event) {
                    let thisTimePay = $(this).val();
                    if (util.isNull(thisTimePay)) {
                        layer.tips("本次开票金额不能为空", $(this), {tips: 1});
                        $(this).val(billInvoiceLeft);
                        mine.renderTotal();
                        return
                    }
                    if (!$.isNumeric(thisTimePay)) {
                        layer.tips("本次开票金额只能填写数字", $(this), {tips: 1});
                        $(this).val(billInvoiceLeft);
                        mine.renderTotal();
                        return
                    }
                    if (parseFloat(thisTimePay) <= 0) {
                        layer.tips("本次开票金额必须大于0", $(this), {tips: 1});
                        $(this).val(billInvoiceLeft);
                        mine.renderTotal();
                        return;
                    }
                    if (parseFloat(thisTimePay) > parseFloat(billInvoiceLeft)) {
                        layer.tips("本次开票金额不能大于可开金额", $(this), {tips: 1});
                        $(this).val(billInvoiceLeft);
                        mine.renderTotal();
                        return;
                    }
                    mine.renderTotal();
                });
            });
        }
    };

    /**
     * 渲染总数
     */
    BillInvoiceInfoLabel.prototype.renderTotal = function () {
        // 标签项
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 详情项
        let detailEle = $(labelEle).find("div[data-bill-invoice-id='" + this.id + "']");
        // 数据项
        let dataItems = $(detailEle).find("div[data-bill-invoice-item-id]");
        let total = 0;
        if (dataItems.length > 0) {
            $(dataItems).each(function (index, item) {
                let thisTimePay = $(item).find("input[name='thisPayment']").val();
                total += parseFloat(util.formatBlank(thisTimePay, 0));
            });
            let totalEle = $(detailEle).find("span[data-this-pay='" + this.id + "']");
            $(totalEle).html(util.thousand(total.toFixed(2)));
            $(totalEle).attr("data-bill-invoice-total", total.toFixed(2))
        } else {
            // 清除所有
            $(detailEle).html("");
        }
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    BillInvoiceInfoLabel.prototype.getValue = function () {
        let data = this.data;
        // 标签项
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 详情项
        let detailEle = $(labelEle).find("div[data-bill-invoice-id='" + this.id + "']");
        // 这次需要特定的数据
        let accountData = [];
        $(data).each(function (index, item) {
            // 数据项
            let dataItems = $(detailEle).find("div[data-bill-invoice-item-id='" + item.id + "']");
            // 本次开票金额
            let thisTimeBillEle = $(dataItems).find("input[name='thisPayment']");
            let left = $(thisTimeBillEle).attr("data-left");
            let thisTimeBill = $(thisTimeBillEle).val();
            accountData.push(util.sortObjectKey({
                id: item.id,
                title: item.title,
                receivables: item.receivables,                // 应开
                actualInvoiceAmount: item.actualInvoiceAmount,// 已开
                invoiceableAmount: left,                      // 可开
                thisReceivables: thisTimeBill                 // 本次开票
            }))
        });
        return accountData;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    BillInvoiceInfoLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    BillInvoiceInfoLabel.prototype.verify = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let detailEle = $(labelEle).find("div[data-bill-invoice-id='" + this.id + "']");
        let dataItems = $(detailEle).find("div[data-bill-invoice-item-id]");
        if (dataItems.length > 0) {
            for (let index = 0; index < dataItems.length; index++) {
                let item = dataItems[index];
                // 输入框（需要校验和渲染总数）
                let thisPaymentEle = $(item).find("input[name='thisPayment']");
                // 剩余应开金额
                let billInvoiceLeft = $(thisPaymentEle).attr("data-left");
                let thisTimePay = $(thisPaymentEle).val();
                if (util.isNull(thisTimePay)) {
                    layer.msg(this.name + "的本次开票金额不能为空");
                    return false;
                }
                if (!$.isNumeric(thisTimePay)) {
                    layer.msg(this.name + "的本次开票金额只能填写数字");
                    return false;
                }
                if (parseFloat(thisTimePay) <= 0) {
                    layer.msg(this.name + "的本次开票金额必须大于0");
                    return false;
                }
                if (parseFloat(thisTimePay) > parseFloat(billInvoiceLeft)) {
                    layer.msg(this.name + "的本次开票金额不能大于可开金额");
                    return false;
                }
            }
        }
        return true;
    };

    return BillInvoiceInfoLabel;
})
;