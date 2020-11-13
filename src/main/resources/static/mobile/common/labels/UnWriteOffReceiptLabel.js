/**
 * 未销账流程 收款信息 （无标号）
 */
(function (window, factory) {
    window.UnWriteOffReceiptLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let UnWriteOffReceiptLabel = function (labelId, labelName, labelType) {
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
    UnWriteOffReceiptLabel.prototype.toText = function (value, productId) {
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        let receiptInfos = value;
        if (typeof value === "string") {
            receiptInfos = JSON.parse(value);
        }
        if (util.arrayNull(receiptInfos)) {
            return this.name + "：无";
        }

        let tableDom = $("<table class='un-write-off-table'></table>");
        let tableHeaderDom = $("<tr class='receipt-header'></tr>");
        tableHeaderDom.append("<th style='width:19%'>到款时间</th>");
        tableHeaderDom.append("<th style='width:25%'>到款信息</th>");
        tableHeaderDom.append("<th style='width:20%'>到款</th>");
        tableHeaderDom.append("<th style='width:20%'>余额</th>");
        tableHeaderDom.append("<th style='width:20%'>销账</th>");
        tableDom.append(tableHeaderDom);

        // 收入总计
        let incomeSum = 0;
        let receiptIds = [];
        $(receiptInfos).each(function (i, receiptInfo) {
            if (util.isNull(receiptInfo.remain)) {
                receiptIds.push(receiptInfo.fsexpenseincomeid);
            }
        });

        let remainJson = {};
        let fsExpenseIncomeInfos = null;
        if (productId || receiptIds.length > 0) {
            $.ajax({
                type: "POST",
                async: false,
                url: "/fsExpenseIncome/readFsExpenseIncomesByProduct.action?temp=" + Math.random(),
                dataType: 'json',
                data: {
                    self: 'T',
                    productId: productId,
                    ids: receiptIds.join(',')
                },
                success: function (res) {
                    if (util.isNotNull(res.data)) {
                        fsExpenseIncomeInfos = res.data;
                        $(fsExpenseIncomeInfos).each(function (i, expenseIncomeInfo) {
                            remainJson[expenseIncomeInfo.id] = expenseIncomeInfo.remainRelatedCost;
                        });
                    }
                }
            });
        }

        let existIds = [];
        $(receiptInfos).each(function (index, receiptInfo) {
            existIds.push(receiptInfo.fsexpenseincomeid);
            let remain = 0;
            if (!receiptInfo.remain) {
                let remainRelatedCost = remainJson[receiptInfo.fsexpenseincomeid];
                if (util.isNotNull(remainRelatedCost)) {
                    remain = remainRelatedCost + receiptInfo.thiscost;
                } else {
                    remain = receiptInfo.cost;
                }
            } else {
                remain = receiptInfo.remain;
            }

            let tableTrHtml = $("<tr></tr>");
            tableTrHtml.append("<td class='time'>" + receiptInfo.operatetime + "</td>");
            tableTrHtml.append("<td class='company'>" + receiptInfo.banckcustomername + "</td>");
            tableTrHtml.append("<td class='number'>" + util.thousand(receiptInfo.cost) + "</td>");
            tableTrHtml.append("<td class='number'>" + util.thousand(remain) + "</td>");
            tableTrHtml.append("<td class='number'>" + util.thousand(receiptInfo.thiscost) + "</td>");
            tableDom.append(tableTrHtml);
            incomeSum += receiptInfo.thiscost;
        });

        if (fsExpenseIncomeInfos) {
            existIds = existIds.join(',');
            $(fsExpenseIncomeInfos).each(function (i, expenseIncomeInfo) {
                if (existIds.indexOf(expenseIncomeInfo.id) < 0) {
                    let incomeRowDom = $("<tr></tr>");
                    incomeRowDom.append("<td class='time'>" + expenseIncomeInfo.operateTime + "</td>");
                    incomeRowDom.append("<td class='company'>" + expenseIncomeInfo.depict + "</td>");
                    incomeRowDom.append("<td class='number'>" + util.thousand(expenseIncomeInfo.cost) + "</td>");
                    incomeRowDom.append("<td class='number'>" + util.thousand(expenseIncomeInfo.remainRelatedCost) + "</td>");
                    incomeRowDom.append("<td class='number'>" + util.thousand(0) + "</td>");
                    tableDom.append(incomeRowDom);
                }
            });
        }
        // 总计行
        let totalRowDom = $("<tr></tr>");
        totalRowDom.append("<td class='total-name'>销账合计</td>");
        totalRowDom.append("<td class='number' colspan='4'>" + util.thousand(incomeSum.toFixed(2)) + "</td>");
        tableDom.append(totalRowDom);
        return this.name + "：<br/>" + tableDom.prop("outerHTML");
    };

    /**
     * 需要进行关联标签
     * 渲染(有值 和没有值 区别回显)
     */
    UnWriteOffReceiptLabel.prototype.render = function (flowEle, value, required, entityId, productId, labelList) {
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【未销账账单标签】对应的位置元素不存在");
        }
        if (util.isNotNull(required)) {
            this.required = util.isTrue(required);
        }
        this.entityId = util.formatBlank(entityId);
        this.productId = util.formatBlank(productId);
        // 用于查找关联标签
        this.labelList = labelList;
        value = util.formatBlank(value);
        if (util.isNotNull(value) && typeof value === "string") {
            value = JSON.parse(value);
        }
        this.data = value;
        let labelDom =
            "<div class='layui-form-item label-type-not-write-off-receipt' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + "：</label>" +
            "    <div class='flow-label-content'>" +
            "        <button type='button' class='layui-btn layui-btn-primary' data-btn-id='" + this.id + "'>请选择" + this.name + "</button>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
        if (util.arrayNotNull(this.data)) {
            // 用于数据回显
            this.renderCheckedItems();
        }
    };

    /**
     * 绑定按钮的事件
     */
    UnWriteOffReceiptLabel.prototype.bindEvent = function () {
        let labelBtnEle = $($(this.flowEle).find("div[data-label-id='" + this.id + "']")).find("button[data-btn-id='" + this.id + "']");
        let mine = this;
        $(labelBtnEle).click(function () {
            if (util.isNull(mine.entityId) && typeof getEntityId === "function") {
                mine.entityId = getEntityId();
            }
            if (util.isNull(mine.entityId)) {
                layer.tips("请先选择客户或供应商", labelBtnEle, {tips: 1});
                return false;
            }
            if (util.isNull(mine.productId) && typeof getProductId === "function") {
                mine.productId = getProductId();
            }
            if (util.isNull(mine.productId)) {
                layer.tips("请先选择产品", labelBtnEle, {tips: 1});
                return false;
            }

            let checkedIds = [];
            if (util.arrayNotNull(mine.data)) {
                $(mine.data).each(function (index, item) {
                    checkedIds.push(item.fsexpenseincomeid);
                });
            }
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/unWriteOffReceiptDetail?entityId=" + mine.entityId + "&productId=" + mine.productId + "&checked=" + checkedIds.join(","),
                cancel: function (index, layero) {
                    let checkedItem = $($(layero[0]).find("iframe").contents()).find("input[name='receiptInfo']:checked");
                    // 需要进行重新渲染
                    let checkedValue = [];
                    if (checkedItem.length > 0) {
                        for (let itemIndex = 0; itemIndex < checkedItem.length; itemIndex++) {
                            let itemValue = $(checkedItem[itemIndex]).val();
                            if (util.isNotNull(itemValue)) {
                                let itemObj = JSON.parse(itemValue);
                                checkedValue.push({
                                    fsexpenseincomeid: itemObj.id,
                                    cost: itemObj.cost,
                                    operatetime: itemObj.operateTime,
                                    banckcustomername: itemObj.depict,
                                    thiscost: itemObj.remainRelatedCost
                                });
                                // thiscost 在获取值的时候需要重新计算
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
    UnWriteOffReceiptLabel.prototype.renderCheckedItems = function () {
        // debugger;
        // 标签信息
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 标签按钮
        let labelBtnEle = $(labelEle).find("button[data-btn-id='" + this.id + "']");
        $(labelEle).find("ul[data-receipt-detail-id='" + this.id + "']").remove();
        let detailItemDoms = $("<ul class='not-write-off-receipt-detail' data-receipt-detail-id='" + this.id + "'></ul>");
        let total = 0;
        if (util.arrayNotNull(this.data)) {
            $(this.data).each(function (itemIndex, item) {
                let thisCost = parseFloat(util.formatBlank(item.thiscost, 0));
                // 总数
                total += thisCost;
                let itemDom =
                    "<li data-receipt-item-id='" + util.formatBlank(item.fsexpenseincomeid) + "'>" +
                    "    <div class='receipt-item-line'>客户名称：" + item.banckcustomername + "</div>" +
                    "    <div class='receipt-item-line'>收款金额：" + item.cost + "</div>" +
                    "    <div class='receipt-item-line'>剩余金额：" + item.thiscost + "</div>" +
                    "</li>";
                detailItemDoms.append(itemDom);
            });
            // 显示总计
            detailItemDoms.append("<i class='not-receipt-off-total'>收款合计：<span class='not-receipt-total-value'>" + total + "</span></i>");
            $(labelBtnEle).after(detailItemDoms.prop("outerHTML"));
        }
    };

    /**
     * 获取标签值
     */
    UnWriteOffReceiptLabel.prototype.getValue = function () {
        // 账单金额合计
        let total = this.getAssociatedLabelTotal();
        if (total !== null) {
            if (util.arrayNotNull(this.data)) {
                let incomesInfo = [];
                incomesInfo.push({isHandApplay: "T"});
                $(this.data).each(function (index, income) {
                    if (total <= 0) {
                        return false;
                    }
                    // 本条到款的剩余金额
                    let thisCost = parseFloat(util.formatBlank(income.thiscost, 0));
                    // 账单金额合计的剩余金额 - 本条到款的可用金额
                    let left = util.accSub(total, thisCost);
                    if (left >= 0) {
                        // 本条到款不够销，全部用上
                        incomesInfo.push(income);
                    } else if (total > 0 && thisCost - total > 0) {
                        // 本条到款够销，只用部分
                        income.thiscost = total;
                        incomesInfo.push(income);
                    }
                    total = util.accSub(total, thisCost);
                });
                return incomesInfo;
            }
        }
        // 没有检测到关联标签的情况下 直接返回全部
        this.data.unshift({isHandApplay: "T"});
        return this.data;
    };

    /**
     * 获取关联账单的总金额
     */
    UnWriteOffReceiptLabel.prototype.getAssociatedLabelTotal = function () {
        let total = null;
        let associateLabelId = "";
        let label = this.getAssociatedLabel();
        if (util.isNotNull(label)) {
            associateLabelId = label.id;
        }
        if (util.isNull(associateLabelId)) {
            return total;
        }
        let associateLabelEle = $(this.flowEle).find("div[data-label-id='" + associateLabelId + "']");
        if (util.arrayNull(associateLabelEle)) {
            return total;
        }
        let associateLabelDetailEle = $(associateLabelEle).find("ul[data-bill-detail-id='" + associateLabelId + "']");
        if (util.isNull(associateLabelDetailEle)) {
            return 0;
        }
        let detailItemEles = $(associateLabelDetailEle).find("li[data-bill-item-id]");
        if (util.arrayNull(detailItemEles)) {
            return 0;
        }
        $(detailItemEles).each(function (detailIndex, detailItem) {
            let billInfo = $(detailItem).attr("data-bill-info");
            if (util.isNotNull(billInfo)) {
                let billObj = JSON.parse(billInfo);
                total = util.formatBlank(total, 0) + parseFloat(util.formatBlank(billObj.thiscost, 0));
            }
        });
        return total;
    };

    /**
     * 关联标签
     */
    UnWriteOffReceiptLabel.prototype.getAssociatedLabel = function () {
        let associatedLabel = null;
        if (util.arrayNotNull(this.labelList)) {
            $(this.labelList).each(function (index, label) {
                if (label.position === 1) {
                    associatedLabel = label;
                    return false
                }
            });
        }
        return associatedLabel;
    };
    /**
     * 获取标签名称
     */
    UnWriteOffReceiptLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     */
    UnWriteOffReceiptLabel.prototype.verify = function () {
        // 是必须标签 需要进行关联校验
        if (this.required && util.isNull(this.data)) {
            layer.msg(this.name + "必须选择");
            return false
        }
        let associateTotal = this.getAssociatedLabelTotal();
        let label = this.getAssociatedLabel();
        let associateLabelName = "";
        if (util.isNotNull(label)) {
            associateLabelName = label.name;
        }
        if (util.isNotNull(associateTotal) && associateTotal <= 0) {
            layer.msg(associateLabelName + "必须选择");
            return false
        }
        if (util.isNotNull(associateTotal) && associateTotal > this.getCheckedTotal()) {
            layer.msg(this.name + "合计金额必须大于等于" + associateLabelName + "总金额");
            return false
        }
        return true;
    };

    /**
     * 获取选择的到款信息
     * @returns {number}
     */
    UnWriteOffReceiptLabel.prototype.getCheckedTotal = function () {
        let total = 0;
        if (util.arrayNotNull(this.data)) {
            $(this.data).each(function (dataIndex, dataItem) {
                let thisCost = parseFloat(util.formatBlank(dataItem.thiscost, 0));
                total += parseFloat(thisCost)
            });
        }
        return total;
    };

    return UnWriteOffReceiptLabel;
});