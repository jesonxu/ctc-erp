/**
 *客户开票抬头标签 29
 */
(function (window, factory) {
    window.CustInvoiceInfoLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let CustInvoiceInfoLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【客户开票抬头标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【客户开票抬头标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    CustInvoiceInfoLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        if (typeof value == 'object') {
            value = JSON.stringify(value);
        }
        let invoiceDom = [];
        if (value.startsWith('{')) {
            value = JSON.parse(value);
            invoiceDom.push(this.typeInvoiceInfoToString(value.custInvoiceInfo));
        } else if (value.startsWith('[')) {
            value = JSON.parse(value);
            let invoiceItemDom = '<div>';
            for (let index = 0; index < value.length; index++) {
                let invoiceInfo = value[index];
                invoiceItemDom += this.typeInvoiceInfoToString(invoiceInfo.custInvoiceInfo);
                if (util.isNull(invoiceItemDom)) {
                    continue;
                }
                invoiceItemDom += "<br/>已收金额：" + util.thousand(invoiceInfo.receivables);
                invoiceItemDom += "<br/>开票金额：" + util.thousand(invoiceInfo.thisReceivables);
            }
            invoiceDom.push(invoiceItemDom + "</div>");
        }
        return this.name + "：<div class='flow-record-2-content'>" + invoiceDom.join("") + "</div>";
    };


    /**
     * 开票信息以字符串展示
     *
     * @param labelValue    开票信息标签内容
     * @returns {string}
     */
    CustInvoiceInfoLabel.prototype.typeInvoiceInfoToString = function (labelValue) {
        if (util.isNull(labelValue)) {
            return "";
        }
        let json = {};
        let invoiceInfoArray = labelValue.split('####');
        for (let index = 0; index < invoiceInfoArray.length; index++) {
            json[invoiceInfoArray[index].split(':')[0]] = invoiceInfoArray[index].split(':')[1];
        }
        return "<br/>公司名称：" + util.formatBlank(json.companyName) +
            "<br/>税务号：" + util.formatBlank(json.taxNumber) +
            "<br/>公司地址：" + util.formatBlank(json.companyAddress) +
            "<br/>联系电话：" + util.formatBlank(json.phone) +
            "<br/>开户银行：" + util.formatBlank(json.accountBank) +
            "<br/>银行账号：" + util.formatBlank(json.bankAccount) + "<br/>";
    };


    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param labelList 标签
     * @param entityId
     */
    CustInvoiceInfoLabel.prototype.render = function (flowEle, value, required, labelList, entityId) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【客户开票抬头标签】对应的位置元素不存在");
        }
        // 用于查找关联标签（账单信息）
        this.labelList = labelList;
        this.entityId = util.formatBlank(entityId);
        this.required = util.isTrue(required);
        this.data = [];
        if (util.isNotNull(value)) {
            this.data = JSON.parse(value);
        }
        let labelDom =
            "<div class='layui-form-item label-type-customer-invoice' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <button name='" + this.id + "' type='button' class='layui-btn layui-btn-primary'>请选择" + this.name + "</button>" +
            "        <div class='customer-invoice-items' data-customer-invoice-id='" + this.id + "'></div>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
        if (this.data.length > 0) {
            this.renderChosenItems();
        }
    };

    /**
     * 绑定按钮事件
     */
    CustInvoiceInfoLabel.prototype.bindEvent = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let labelBtn = $(labelEle).find("button[name='" + this.id + "']");
        let mine = this;
        $(labelBtn).click(function () {
            if (util.isNull(mine.entityId) && typeof getEntityId === "function") {
                mine.entityId = getEntityId();
            }
            if (util.isNull(mine.entityId)) {
                layer.tips("请先选择客户", labelBtn, {tips: 1});
                return false;
            }
            let infoArr = [];
            if (util.isNotNull(mine.data)) {
                $(mine.data).each(function (index, item) {
                    let invoiceStr = item.custInvoiceInfo;
                    infoArr.push(invoiceStr.split("####")[0].split(":")[1]);
                });
            }
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/invoiceDetailMulti?entityId=" + mine.entityId + "&type=1&checked=" + infoArr.join(",") + "&time=" + new Date().getTime(),
                cancel: function (index, layero) {
                    let checkedItems = $($(layero[0]).find("iframe").contents()).find("input[name='invoice']:checked");
                    let checkedValues = [];
                    if (checkedItems.length > 0) {
                        $(checkedItems).each(function (index, item) {
                            let value = util.formatBlank($(item).val());
                            checkedValues.push({
                                custInvoiceInfo: value
                            });
                        });
                    }
                    mine.data = checkedValues;
                    mine.renderChosenItems();
                }
            });
            layer.full(index);
        });
    };

    /**
     * 渲染已经选择的项
     */
    CustInvoiceInfoLabel.prototype.renderChosenItems = function () {
        let chosenItemDoms = [];
        let total = 0;
        if (util.arrayNotNull(this.data)) {
            $(this.data).each(function (index, item) {
                // 开票抬头信息
                let itemInfo = {};
                let itemProps = item.custInvoiceInfo.split("####");
                $(itemProps).each(function (itemIndex, propValue) {
                    let itemProp = propValue.split(":");
                    if (itemProp.length >= 2) {
                        itemInfo[itemProp[0]] = itemProp[1]
                    }
                });
                // 标题
                let itemTitle = itemInfo.companyName + "【" + itemInfo.accountBank + ":" + itemInfo.bankAccount + "】";
                let itemId = itemInfo.basicsId;
                // 已收金额
                let received = 0;
                if (util.isNotNull(item.receivables)) {
                    received = parseFloat(item.receivables);
                }
                // 开票金额
                let invoiceAmount = 0;
                if (util.isNotNull(item.thisReceivables)) {
                    invoiceAmount = parseFloat(item.thisReceivables);
                }
                total += invoiceAmount;
                let chosenItemDom =
                    "<div class='customer-invoice-item' data-customer-invoice-id='" + itemId + "' data-customer-invoice-value='" + item.custInvoiceInfo + "'>" +
                    "   <label class='customer-invoice-item-title'>" + itemTitle + "</label>" +
                    "   <div class='customer-invoice-line'>" +
                    "      <label class='customer-invoice-line-title' required='true'>已收金额：</label>" +
                    "      <div class='customer-invoice-line-value'>" +
                    "          <input type='text' name='receivables' class='layui-input' value='" + received.toFixed(2) + "'/>" +
                    "      </div>" +
                    "   </div>" +
                    "   <div class='customer-invoice-line'>" +
                    "      <label class='customer-invoice-line-title' required='true'>开票金额：</label>" +
                    "      <div class='customer-invoice-line-value'>" +
                    "           <input type='text' name='invoiceInfo' class='layui-input' value='" + invoiceAmount.toFixed(2) + "'/>" +
                    "      </div>" +
                    "   </div>" +
                    "    <div class='customer-invoice-opts'>" +
                    "       <button type='button' data-delete-id='" + itemId + "' class='layui-btn layui-btn-xs layui-btn-primary'>" +
                    "           <i class='layui-icon layui-icon-delete'></i>删除" +
                    "       </button>" +
                    "	 </div>" +
                    "</div>";
                chosenItemDoms.push(chosenItemDom);
            });
        }
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let labelDetailEle = $(labelEle).find("div[data-customer-invoice-id='" + this.id + "']");
        if (chosenItemDoms.length > 0) {
            chosenItemDoms.push("<i class='customer-invoice-count-tip'>合计开票：<span data-this-pay='" + this.id
                + "' class='customer-invoice-this-time-pay'>" + total.toFixed(2) + "</span>&nbsp;元</i>");
        }
        $(labelDetailEle).html(chosenItemDoms.join(""));
        this.bindChosenEvent();
    };

    /**
     * 绑定选择了的事件
     */
    CustInvoiceInfoLabel.prototype.bindChosenEvent = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let labelDetailEle = $(labelEle).find("div[data-customer-invoice-id='" + this.id + "']");
        let labelItems = $(labelDetailEle).find("div[data-customer-invoice-id]");
        let mine = this;
        if (labelItems.length > 0) {
            $(labelItems).each(function (index, labelItem) {
                // id
                let labelItemId = $(labelItem).attr("data-customer-invoice-id");
                // 已收金额
                let receivablesEle = $(labelItem).find("input[name='receivables']");
                // 开票金额
                let invoiceInfoEle = $(labelItem).find("input[name='invoiceInfo']");
                $(receivablesEle).blur(function () {
                    let receivable = $(this).val();
                    if (util.isNull(receivable)) {
                        layer.tips('已收金额不能为空', $(this), {tips: 1});
                        $(this).val(0.00);
                        mine.renderTotal();
                        return false;
                    }
                    if (!$.isNumeric(receivable)) {
                        layer.tips('只能填写数字', $(this), {tips: 1});
                        $(this).val(0.00);
                        mine.renderTotal();
                        return false;
                    }
                    if (parseFloat(receivable) < 0) {
                        layer.tips('不能小于0', $(this), {tips: 1});
                        $(this).val(0.00);
                        mine.renderTotal();
                        return false;
                    }
                    mine.renderTotal();
                });

                $(invoiceInfoEle).blur(function () {
                    let invoice = $(this).val();
                    if (util.isNull(invoice)) {
                        layer.tips('开票金额不能为空', $(this), {tips: 1});
                        $(this).val(0.00);
                        mine.renderTotal();
                        return false;
                    }
                    if (!$.isNumeric(invoice)) {
                        layer.tips('只能填写数字', $(this), {tips: 1});
                        $(this).val(0.00);
                        mine.renderTotal();
                        return false;
                    }
                    if (parseFloat(invoice) <= 0) {
                        layer.tips('开票金额必须大于0', $(this), {tips: 1});
                        $(this).val(0.00);
                        mine.renderTotal();
                        return false;
                    }
                    let relationTotal = mine.findRelationLabel();
                    if (relationTotal !== null) {
                        // 表名关联标签存在
                        if (parseFloat(invoice) > relationTotal) {
                            layer.tips('不能大于账单总金额', $(this), {tips: 1});
                            $(this).val(0.00);
                            mine.renderTotal();
                            return false;
                        }
                    }
                    mine.renderTotal();
                });

                // 按钮
                let itemBtnEle = $(labelItem).find("button[data-delete-id='" + labelItemId + "']");
                $(itemBtnEle).click(function () {
                    $(this).parent().parent().remove();
                    // 渲染总数
                    mine.renderTotal();
                });
            });
        }
    };

    /**
     * 渲染总数
     */
    CustInvoiceInfoLabel.prototype.renderTotal = function () {
        // 标签项
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 详情项
        let detailEle = $(labelEle).find("div[data-customer-invoice-id='" + this.id + "']");
        // 数据项
        let dataItems = $(detailEle).find("div[data-customer-invoice-id]");
        let total = 0;
        if (dataItems.length > 0) {
            $(dataItems).each(function (index, item) {
                let invoiceInfo = $(item).find("input[name='invoiceInfo']").val();
                total += parseFloat(util.formatBlank(invoiceInfo, 0));
            });
            let totalEle = $(detailEle).find("span[data-this-pay='" + this.id + "']");
            $(totalEle).html(util.thousand(total.toFixed(2)));
        } else {
            // 清除所有
            $(detailEle).html("");
        }
    };

    /**
     * 查找关联的标签信息
     */
    CustInvoiceInfoLabel.prototype.findRelationLabel = function () {
        let relationLabel = null;
        if (util.arrayNotNull(this.labelList)) {
            for (let index = 0; index < this.labelList.length; index++) {
                let label = this.labelList[index];
                // 关联到账单金额标签（如果有多个，默认为第一个）
                if (parseInt(label.type) === 30) {
                    relationLabel = label;
                    break;
                }
            }
        }
        if (util.isNotNull(relationLabel)) {
            // 查找标签
            let relationLabelEle = $(this.flowEle).find("div[data-label-id='" + relationLabel.id + "']");
            // 查找到 总数
            let totalEle = $(relationLabelEle).find("span[data-this-pay='" + relationLabel.id + "']");
            let total = $(totalEle).attr("data-bill-invoice-total");
            if (util.isNotNull(total) && $.isNumeric(total)) {
                return parseFloat(total);
            }
            return 0;
        }
        return null;
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    CustInvoiceInfoLabel.prototype.getValue = function () {
        // 标签项
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 详情项
        let detailEle = $(labelEle).find("div[data-customer-invoice-id='" + this.id + "']");
        // 数据项
        let dataItems = $(detailEle).find("div[data-customer-invoice-id]");
        var custInvoiceData = [];
        if (dataItems.length > 0) {
            $(dataItems).each(function (index, item) {
                // 开票金额
                let invoiceInfo = $(item).find("input[name='invoiceInfo']").val();
                // 已收金额
                let receivablesPrice = $(item).find("input[name='receivables']").val();
                // 开票抬头
                let custInvoiceInfo = $(item).attr("data-customer-invoice-value");
                let invoice = {};
                invoice.custInvoiceInfo = custInvoiceInfo;
                invoice.thisReceivables = invoiceInfo;
                invoice.receivables = receivablesPrice;
                custInvoiceData.push(util.sortObjectKey(invoice));
            });
        }
        return custInvoiceData;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    CustInvoiceInfoLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    CustInvoiceInfoLabel.prototype.verify = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let labelDetailEle = $(labelEle).find("div[data-customer-invoice-id='" + this.id + "']");
        let labelItems = $(labelDetailEle).find("div[data-customer-invoice-id]");
        if (labelItems.length > 0) {
            let invoiceTotal = 0.0;
            for (let index = 0; index < labelItems.length; index++) {
                let labelItem = labelItems[index];
                // 已收金额
                let receivablesEle = $(labelItem).find("input[name='receivables']");
                // 开票金额
                let invoiceInfoEle = $(labelItem).find("input[name='invoiceInfo']");
                let receivable = $(receivablesEle).val();
                if (util.isNull(receivable)) {
                    layer.msg(this.name + '的已收金额不能为空');
                    return false;
                }
                if (!$.isNumeric(receivable)) {
                    layer.msg(this.name + '的已收金额只能填写数字');
                    return false;
                }
                if (parseFloat(receivable) < 0) {
                    layer.msg(this.name + '的已收金额不能小于0');
                    return false;
                }
                let invoice = $(invoiceInfoEle).val();
                if (util.isNull(invoice)) {
                    layer.msg(this.name + '的开票金额不能为空');
                    return false;
                }
                if (!$.isNumeric(invoice)) {
                    layer.msg(this.name + '的开票金额只能填写数字');
                    return false;
                }
                if (parseFloat(invoice) <= 0) {
                    layer.msg(this.name + '的开票金额必须大于0');
                    return false;
                }
                invoiceTotal = util.accAdd(invoiceTotal, invoice);
            }
            // 获取 客户开票抬头标签 关联的 账单信息标签
            let relationTotal = this.findRelationLabel();
            if (relationTotal !== null) {
                // 开票金额合计 必须等于 账单金额合计
                if(parseFloat(invoiceTotal + '').toFixed(2) !== parseFloat(relationTotal).toFixed(2)){
                    layer.msg('开票金额合计 必须等于 账单金额合计');
                    return false;
                }
            }
        } else if (util.isTrue(this.required)) {
            layer.msg(this.name + "不能为空");
            return false;
        }
        return true;
    };

    return CustInvoiceInfoLabel;
});