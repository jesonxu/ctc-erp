/**
 * 未销账账单标签 (在系统里面没有这个标签类型 ，实际上有单独的处理)
 */
(function (window, factory) {
    window.UnWriteOffBillLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let UnWriteOffBillLabel = function (labelId, labelName, labelType) {
        // 渲染的位置（对应元素下面 直接添加）
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【未销账账单标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【未销账账单标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    UnWriteOffBillLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        let billInfos = value;
        if (typeof value === "string") {
            billInfos = JSON.parse(value);
        }
        if (util.arrayNull(billInfos)) {
            return this.name + "：无";
        }
        let billTableDom = $('<table class="un-write-off-table"></table>');
        let tableHeader = $('<tr></tr>');
        tableHeader.append("<th style='width:65%'>账单名称</th>");
        tableHeader.append("<th style='width:35%'>账单金额</th>");
        billTableDom.append(tableHeader);
        let billsSum = 0;
        $(billInfos).each(function (index, billInfo) {
            let billRowDom = $("<tr></tr>");
            billRowDom.append("<td>" + billInfo.title + "</td>");
            billRowDom.append("<td class='number'>" + util.thousand(billInfo.thiscost) + "</td>");
            billTableDom.append(billRowDom);
            billsSum += billInfo.thiscost;
        });
        let tableTotalRow = $("<tr></tr>");
        tableTotalRow.append("<td class='total-name'>账单合计</td>");
        tableTotalRow.append("<td class='number'>" + util.thousand(billsSum.toFixed(2)) + "</td>");
        billTableDom.append(tableTotalRow);
        return this.name + ":<br/>" + billTableDom.prop("outerHTML");
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     */
    UnWriteOffBillLabel.prototype.render = function (flowEle, value, required, productId) {
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【未销账账单标签】对应的位置元素不存在");
        }
        if (util.isNotNull(required)) {
            this.required = util.isTrue(required);
        }
        value = util.formatBlank(value);
        // debugger;
        // 先进行转义 解析
        if (util.isNotNull(value) && typeof value === "string") {
            value = JSON.parse(value);
        }
        this.data = value;
        this.productId = util.formatBlank(productId);
        let labelDom =
            "<div class='layui-form-item label-type-not-write-off-bill' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + "：</label>" +
            "    <div class='flow-label-content'>" +
            "        <button type='button' class='layui-btn layui-btn-primary' data-btn-id='" + this.id + "'>请选择" + this.name + "</button>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
        if (util.arrayNotNull(this.data)){
            this.renderCheckedItems();
        }
    };

    /**
     * 绑定事件
     */
    UnWriteOffBillLabel.prototype.bindEvent = function () {
        let btn = $($(this.flowEle).find("div[data-label-id='" + this.id + "']")).find("button[data-btn-id='" + this.id + "']");
        let mine = this;
        $(btn).click(function () {
            if (util.isNull(mine.productId) && typeof getProductId === "function") {
                mine.productId = getProductId();
            }
            if (util.isNull(mine.productId)) {
                layer.tips("请先选择产品", btn, {tips: 1});
                return false;
            }
            let checkedIds = [];
            if (util.arrayNotNull(mine.data)) {
                $(mine.data).each(function (index, item) {
                    checkedIds.push(item.productbillsid);
                });
            }
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/unWriteOffBillDetail?productId=" + mine.productId + "&checked=" + checkedIds.join(","),
                cancel: function (index, layero) {
                    let checkedItem = $($(layero[0]).find("iframe").contents()).find("input[name='billInfo']:checked");
                    // 需要进行重新渲染
                    let checkedValue = [];
                    if (checkedItem.length > 0) {
                        for (let itemIndex = 0; itemIndex < checkedItem.length; itemIndex++) {
                            let itemValue = $(checkedItem[itemIndex]).val();
                            if (util.isNotNull(itemValue)) {
                                let itemObj = JSON.parse(itemValue);
                                checkedValue.push({
                                    productbillsid: itemObj.id,
                                    title: itemObj.title,
                                    receivables: itemObj.receivables,
                                    thiscost: util.accSub(itemObj.receivables, itemObj.actualReceivables)
                                });
                            }
                        }
                    }
                    mine.data = checkedValue;
                    mine.renderCheckedItems();
                }
            });
            layer.full(index);
        });
    };

    /**
     * 渲染选择的项
     */
    UnWriteOffBillLabel.prototype.renderCheckedItems = function () {
        // 标签信息
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 标签按钮
        let labelBtnEle = $(labelEle).find("button[data-btn-id='" + this.id + "']");
        $(labelEle).find("ul[data-bill-detail-id='" + this.id + "']").remove();
        let detailItemDoms = $("<ul class='unwirte-off-detail' data-bill-detail-id='" + this.id + "'></ul>");
        let total = 0;
        if (util.arrayNotNull(this.data)) {
            $(this.data).each(function (itemIndex, item) {
                let thisCost = parseFloat(util.formatBlank(item.thiscost, 0));
                // 总数
                total += thisCost;
                let itemJson = JSON.stringify(item);
                let itemDom =
                    "<li data-bill-item-id='" + util.formatBlank(item.productbillsid) + "' data-bill-info='" + itemJson + "'>" +
                    "    <div class='bill-item-title'>" + item.title + "</div>" +
                    "    <div class='bill-item-cost'> " +
                    "       <span>账单金额：</span>" +
                    "       <span>" + util.thousand(thisCost) + "</span>" +
                    "    </div>" +
                    "</li>";
                detailItemDoms.append(itemDom);
            });
            // 显示总计
            detailItemDoms.append("<i class='unwrite-off-total'>账单合计：<span class='unwrite-bill-total-value'>" + total + "</span></i>");
            $(labelBtnEle).after(detailItemDoms.prop("outerHTML"));
        }
    };

    /**
     * 获取标签值
     */
    UnWriteOffBillLabel.prototype.getValue = function () {
        return this.data;
    };

    /**
     * 获取标签名称
     */
    UnWriteOffBillLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     */
    UnWriteOffBillLabel.prototype.verify = function () {
        // debugger
        // 是必须标签 账单必须选择
        if (util.isTrue(this.required) && util.arrayNull(this.getValue())) {
            layer.msg(this.name + "不能为空");
            return false
        }
        return true;
    };

    return UnWriteOffBillLabel;
});