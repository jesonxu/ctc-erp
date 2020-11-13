/**
 * 账单信息标签 14
 */
(function (window, factory) {
    window.AccountInfoLabel = factory();
})(window, function () {

    // 不同流程的标签名称
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
        }
    };

    // 标签名称
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
    let AccountInfoLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【账单信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【账单信息标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     * @param value 标签值
     * @param flowEntId 流程实体ID
     * @param flowId 流程id
     * @param flowClass 流程类型
     * @returns {string}
     */
    AccountInfoLabel.prototype.toText = function (value, flowEntId, flowId, flowClass) {
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        let productBills = null;
        try {
            productBills = JSON.parse(value);
        } catch (e) {
            console.log("[账单信息标签]捕获异常，在转换数据的时候错误", e)
        }
        if (util.arrayNull(productBills)) {
            return this.name + "：无";
        }
        // 本次收付款
        let thisTimePay = 0;
        // 展示名称
        let showName = PRODUCT_BILL_LABEL_NAME[flowClass];
        // 输入框名称
        let inputName = PRODUCT_BILL_INPUT_NAME[flowClass];
        let accountItems = [];
        $.each(productBills, function (index, productBill) {
            // 应该 支付|收款
            let payableName = inputName.payableName;
            let payable = util.isNull(productBill[payableName]) ? 0 : parseFloat(productBill[payableName]);

            // 实际(已经) 支付|收款
            let actualPayableName = inputName.actualPayableName;
            let actualPayable = util.isNull(productBill[actualPayableName]) ? 0 : parseFloat(productBill[actualPayableName]);

            // 本次 支付|收款
            let thisPaymentName = inputName.thisPaymentName;
            let thisPayment = util.isNull(productBill[thisPaymentName]) ? 0 : parseFloat(productBill[thisPaymentName]);

            // 剩余应该 支付|收款（固定不变[要求]）
            let left_should_pay_name = inputName.leftShouldPayName;
            let leftShouldPay = payable - actualPayable;
            // 去除未审核和驳回的账单金额
            let type = '';
            if (flowClass === '[BillPaymentFlow]') {
                type = 'thisPayment';
            } else if (flowClass === 'RemunerationFlow' || flowClass === '[BillReceivablesFlow]' || flowClass === '[InvoiceFlow]') {
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
                        if (data.code === 200 || data.code === '200') {
                            leftShouldPay -= parseFloat(data.msg);
                        }
                    }
                });
            }
            // 本次应付（总）
            thisTimePay += parseFloat(thisPayment);
            let item = "<br/>" + productBill.title + "<br/>"
                + showName.payableName + util.thousand(payable.toFixed(2)) + "元，"
                + showName.actualPayableName + util.thousand(actualPayable.toFixed(2)) + "元，"
                + showName.leftShouldPayName + util.thousand(leftShouldPay.toFixed(2)) + "元，"
                + showName.thisPaymentName + util.thousand(thisPayment.toFixed(2)) + "元";
            accountItems.push(item);
        });
        accountItems.push("<br/>" + showName.totalName + util.formatNum(thisTimePay, 2) + "元<br/>");
        return this.name + ":" + accountItems.join("");
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param flow 流程信息
     * @param productId 产品ID
     */
    AccountInfoLabel.prototype.render = function (flowEle, value, required, flow, productId) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【账单信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let labelDom =
            "<div class='layui-form-item label-type-account-info' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <button name='" + this.id + "' type='button' class='layui-btn layui-btn-primary'>" +
            "           <i class='layui-icon layui-icon-list'></i>请选择" + this.name +
            "        </button>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        if (util.isNotNull(value)) {
            // 如果原来有数据 需要进行回显
            if (typeof value === "string") {
                this.datas = JSON.parse(value);
            } else {
                this.datas = value;
            }
            // 渲染
            this.renderSelectedBill(this.datas, flow.flowId, flow.flowClass);
        }
        // 绑定点击事件
        this.bindEvent(flow, productId);
    };

    /**
     * 绑定事件
     * @param flowInfo 流程信息
     * @param productId 产品ID
     */
    AccountInfoLabel.prototype.bindEvent = function (flowInfo, productId) {
        let accountButton = $(this.flowEle).find("div[data-label-id='" + this.id + "']").find("button[name='" + this.id + "']");
        if (accountButton.length > 0) {
            // 关联类型
            let associateType = flowInfo.associateType;
            // 流程类型
            let flowClass = flowInfo.flowClass;
            // 流程ID
            let flowId = flowInfo.flowId;
            let url = "";
            if (associateType === 0) {
                // 客户
                url = "/mobileLabel/customerAccountDetail";
            } else if (associateType === 1) {
                // 供应商
                url = "/mobileLabel/supplierAccountDetail";
            } else {
                // 无法确定类型
                throw new Error("账单标签无法确定加载类型");
            }

            let mine = this;
            $(accountButton).click(function () {
                if (util.isNull(productId)) {
                    // 调用 外部实现的 获取产品 ID
                    productId = getProductId();
                }
                if (util.isNull(productId)) {
                    layer.msg("请先选择产品");
                    return;
                }
                let checkedItems = [];
                if (util.isNotNull(mine.datas)) {
                    $(mine.datas).each(function (index, item) {
                        checkedItems.push(JSON.parse(item).id);
                    });
                }
                let index = layer.open({
                    skin: 'account-class',
                    type: 2,
                    area: ['100%', '100%'],
                    title: "选择产品账单",
                    content: url + "?productId=" + productId + "&flowClass=" + encodeURI(flowClass) + "&checkedItem=" + checkedItems.join(","),
                    /*    btn: ['确认', '取消'],
                        yes: function (index, layero) {
                            console.log(layero);
                            let checkedItems = $($(layero[0]).find("iframe").contents()).find("input[name='accountItem']:checked");
                            let checkedAccountItems = [];
                            $(checkedItems).each(function () {
                                checkedAccountItems.push($(this).val())
                            });
                            layer.close(index);
                            mine.datas = checkedAccountItems;
                            // 渲染
                            mine.renderSelectedBill(checkedAccountItems, flowId, flowClass);
                        },*/
                    cancel: function (index, layero) {
                        let checkedItems = $($(layero[0]).find("iframe").contents()).find("input[name='accountItem']:checked");
                        let checkedAccountItems = [];
                        $(checkedItems).each(function () {
                            checkedAccountItems.push($(this).val())
                        });
                        layer.close(index);
                        mine.datas = checkedAccountItems;
                        // 渲染
                        mine.renderSelectedBill(checkedAccountItems, flowId, flowClass);
                    }
                });
                layer.full(index);
            });
        }
    };

    /**
     * 渲染选择了的
     * @param billItems
     * @param flowId 流程ID
     * @param flowClass 流程类型
     */
    AccountInfoLabel.prototype.renderSelectedBill = function (billItems, flowId, flowClass) {
        if (util.arrayNotNull(billItems)) {
            let billInfoDom = [];
            billInfoDom.push("<div data-bill-info-id='" + this.id + "' class='account-bill-detail'>");
            // 本次收付款
            let thisTimePay = 0;
            // 展示名称
            let labelName = PRODUCT_BILL_LABEL_NAME[flowClass];
            // 输入框名称
            let inputName = PRODUCT_BILL_INPUT_NAME[flowClass];
            // 遍历组装内容
            for (let billIndex = 0; billIndex < billItems.length; billIndex++) {
                let productBill = typeof billItems[billIndex] == 'object' ? billItems[billIndex] : JSON.parse(billItems[billIndex]);
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
                    "<div class='account-bill-item' data-bill-item-id='" + itemId + "' data-bill-item-title='" + title + "'>" +
                    "    <span class='product-bill-title'>" + title + "</span>" +
                    "    <div class='bill-item-line'>" +
                    "       <span>" + labelName.payableName + "</span>" +
                    "       <input name='payable' data-param-name='" + payableName + "' class='layui-input' value='" + payable.toFixed(2) + "' disabled/>" +
                    "    </div>" +
                    "    <div class='bill-item-line'>" +
                    "       <span>" + labelName.actualPayableName + "</span>" +
                    "       <input name='actualPayable' data-param-name='" + actualPayableName + "' class='layui-input' value='" + actualPayable.toFixed(2) + "' disabled/>" +
                    "    </div>" +
                    "    <div class='bill-item-line'>" +
                    "       <span>" + labelName.leftShouldPayName + "</span>" +
                    "       <input name ='leftShouldPay' data-param-name='" + leftShouldPayName + "' class='layui-input' value='" + leftShouldPay + "'  disabled/>" +
                    "    </div>" +
                    "    <div class='bill-item-line'>" +
                    "       <span>" + labelName.thisPaymentName + "</span>" +
                    "       <input name='thisPayment' data-param-name='" + thisPaymentName + "' class='layui-input' data-left='" + leftShouldPay + "' value='" + leftShouldPay + "'/>" +
                    "    </div>" +
                    "    <div class='bill-item-line'>" +
                    "       <button type='button' data-delete-id='" + itemId + "' class='layui-btn layui-btn-xs layui-btn-primary'>" +
                    "       <i class='layui-icon layui-icon-delete'></i>删除" +
                    "       </button>" +
                    "	 </div>" +
                    "</div>");
            }
            // 合计
            billInfoDom.push("<i class='bill-count-tip'>" + labelName.totalName +
                "<span data-this-pay='" + this.id + "' class='bill-this-time-pay'>" + util.formatNum(thisTimePay, 2) + "</span>&nbsp;元</i>");
            billInfoDom.push("</div>");
            // 删除原有的标签
            $(this.flowEle).find("div[data-bill-info-id='" + this.id + "']").remove();
            $(this.flowEle).find("button[name='" + this.id + "']").after(billInfoDom.join(""));
            // 绑定改变事件
            this.bindBillItemEvent();
        } else {
            $(this.flowEle).find("div[data-bill-info-id='" + this.id + "']").remove();
        }
    };

    /**
     * 绑定账单详情项事件
     */
    AccountInfoLabel.prototype.bindBillItemEvent = function () {
        let billDetailEle = $(this.flowEle).find("div[data-bill-info-id='" + this.id + "']");
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
    AccountInfoLabel.prototype.reComputeTotal = function () {
        let billDetailEle = $(this.flowEle).find("div[data-bill-info-id='" + this.id + "']");
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
    AccountInfoLabel.prototype.getValue = function () {
        // 账单金额详情ele
        let accountBillDetailEle = $(this.flowEle).find("div[data-bill-info-id='" + this.id + "']");
        if (accountBillDetailEle.length === 0) {
            return null;
        }
        let billItems = $(accountBillDetailEle).find("div[data-bill-item-id]");
        if (billItems.length === 0) {
            return null;
        }
        let productBills = [];
        for (let billIndex = 0; billIndex < billItems.length; billIndex++) {
            let billItem = billItems[billIndex];
            let id = $(billItem).attr("data-bill-item-id");
            let title = $(billItem).attr("data-bill-item-title");
            // 应付（此处只做标识 实际含义不是一定这个）
            let payableEle = $(billItem).find("input[name='payable']");
            let payableVal = $(payableEle).val();
            let payableName = $(payableEle).attr("data-param-name");

            // 实际支付
            let actualPayableEle = $(billItem).find("input[name='actualPayable']");
            let actualPayableVal = $(actualPayableEle).val();
            let actualPayableName = $(actualPayableEle).attr("data-param-name");

            // 剩余应付
            let leftShouldPayEle = $(billItem).find("input[name='leftShouldPay']");
            let leftShouldPayVal = $(leftShouldPayEle).val();
            let leftShouldPayName = $(leftShouldPayEle).attr("data-param-name");

            // 本次支付
            let thisPaymentEle = $(billItem).find("input[name='thisPayment']");
            let thisPaymentVal = $(thisPaymentEle).val();
            let thisPaymentName = $(thisPaymentEle).attr("data-param-name");

            let productBill = {};
            productBill.id = util.formatBlank(id);
            productBill.title = util.formatBlank(title);
            productBill[actualPayableName] = parseFloat(util.formatBlank(actualPayableVal, 0)).toFixed(2);
            productBill[payableName] = parseFloat(util.formatBlank(payableVal, 0)).toFixed(2);
            productBill[thisPaymentName] = parseFloat(util.formatBlank(thisPaymentVal, 0)).toFixed(2);
            productBills.push(productBill);
        }
        return productBills;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    AccountInfoLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    AccountInfoLabel.prototype.verify = function () {
        let tipEle = $(this.flowEle).find("button[name='" + this.id + "']")[0];
        // 如果是必须的 就需要检查是否已经选择
        if (util.isTrue(this.required) && util.isNull(this.datas)) {
            layer.msg(this.name + "必须选择");
            return false;
        }
        let billDetailEle = $(this.flowEle).find("div[data-bill-info-id='" + this.id + "']");
        if (billDetailEle.length > 0) {
            let thisPayEleArr = $(billDetailEle).find("input[name='thisPayment']");
            if (thisPayEleArr.length > 0) {
                for (let index = 0; index < thisPayEleArr.length; index++) {
                    let item = thisPayEleArr[index];
                    let labelItemName = util.formatBlank($(item).attr("data-param-name"));
                    let dataLeft = parseFloat(util.formatBlank($(item).attr("data-left")));
                    let thisTimePay = $(item).val();
                    if (util.isNull(thisTimePay)) {
                        layer.msg(this.name + labelItemName + "不能为空");
                        return false;
                    }
                    if (!$.isNumeric(thisTimePay)) {
                        layer.msg(this.name + labelItemName + "只能填写数字");
                        return false;
                    }
                    if (parseFloat(thisTimePay) > dataLeft) {
                        layer.msg(this.name + labelItemName + "不能大于" + dataLeft);
                        return false;
                    }
                    if (parseFloat(thisTimePay) <= 0) {
                        layer.msg(this.name + labelItemName + "必须大于0");
                        return false;
                    }
                }
            }
        }
        return true;
    };

    return AccountInfoLabel;
});