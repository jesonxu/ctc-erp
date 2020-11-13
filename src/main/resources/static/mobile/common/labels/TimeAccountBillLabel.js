/**
 *时间账单金额标签 32
 */
(function (window, factory) {
    window.TimeAccountBillLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let TimeAccountBillLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【时间账单金额标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【时间账单金额标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    TimeAccountBillLabel.prototype.toText = function (value) {
        console.log("时间账单金额标签：name:" + this.name + " - value:" + value);
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        let accountBillObj = null;
        try {
            accountBillObj = typeof value == 'object' ? value : JSON.parse(value);
        } catch (e) {
        }
        let billInfoDom = [];
        if (util.isNull(accountBillObj)) {
            // 兼容原来数据
            let accountBillInfo = value.split(",");
            if (accountBillInfo.length >= 3) {
                billInfoDom.push("(全月)成功数：" + util.thousand(accountBillInfo[0]) + "条 X "
                    + util.thousand(accountBillInfo[1]) + "元 = "
                    + util.thousand(accountBillInfo[2]) + "元");
            }
        } else {
            let accountMoneyDom = "<div class='flow-record-2-content'>";
            for (let index = 0; index < accountBillObj.length; index++) {
                let item = accountBillObj[index];
                accountMoneyDom += ("[" + item.start + " ~ " + item.end + "]："
                    + item.success + "条 X " + item.price + "元 = " + item.total + "元");
                if (index !== (accountBillObj.length - 1)) {
                    accountMoneyDom += "<br>";
                }
            }
            billInfoDom.push(accountMoneyDom + "</span>");
        }
        return this.name + "：" + util.formatBlank(billInfoDom.join(""), "无");
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param labelValues 标签值
     */
    TimeAccountBillLabel.prototype.render = function (flowEle, value, required, labelValues) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【时间账单金额标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        this.parseValue(value, labelValues);
        let timeAccountItemDoms = [];
        let totalSuccess = 0;
        let totalMoney = 0.00;
        if (util.arrayNotNull(this.data)) {
            for (let timeIndex = 0; timeIndex < this.data.length; timeIndex++) {
                let timeAccountBill = this.data[timeIndex];
                let billIndex = timeIndex + 1;
                timeAccountItemDoms.push(this.getItemDom(timeAccountBill, billIndex));
                totalSuccess += parseInt(timeAccountBill.success);
                totalMoney += parseFloat(timeAccountBill.total);
            }
        } else {
            timeAccountItemDoms.push(this.getItemDom());
        }
        let labelDom =
            "<div class='layui-form-item label-type-time-account-bill' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" + timeAccountItemDoms +
            "    <i>" +
            "       <span class='total-depart'>" +
            "           总成功数：<span  data-total-success='" + totalSuccess + "'>" + totalSuccess + "</span>" +
            "       </span>" +
            "       <span class='total-depart'>" +
            "           总金额：<span data-total-money='" + totalMoney.toFixed(2) + "'>" + totalMoney.toFixed(2) + "</span>" +
            "       </span>" +
            "    </i>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
    };

    /**
     * 绑定事件
     **/
    TimeAccountBillLabel.prototype.bindEvent = function (itemIndex) {
        // 标签
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        if (util.isNull(itemIndex)) {
            itemIndex = "";
        } else {
            itemIndex = "='" + itemIndex + "'";
        }
        // 项
        let itemEles = $(labelEle).find("div[data-time-account-bill-index" + itemIndex + "]");
        let mine = this;
        if (util.isNotNull(itemEles) && itemEles.length > 0) {
            $(itemEles).each(function (itemIndex, item) {
                let startTimeEle = $(item).find("input[name='startTime']");
                let endTimeEle = $(item).find("input[name='endTime']");
                // 日期插件进行时间渲染
                layui.use('laydate', function () {
                    let laydate = layui.laydate;
                    // 开始时间
                    let start = laydate.render({
                        elem: startTimeEle[0],
                        type: 'date',
                        format: 'yyyy-MM-dd', //日期格式
                        zIndex: 99999999,
                        trigger: 'click',
                        done: function (value, date) {
                            end.config.min = {
                                year: date.year,
                                month: date.month - 1,
                                date: date.date
                            };
                        }
                    });
                    // 结束时间
                    let end = laydate.render({
                        elem: endTimeEle[0],
                        type: 'date',
                        format: 'yyyy-MM-dd', //日期格式
                        zIndex: 99999999,
                        trigger: 'click',
                        done: function (value, date) {
                            start.config.max = {
                                year: date.year,
                                month: date.month - 1,
                                date: date.date
                            };
                        }
                    });
                });
                // 成功数
                let successEle = $(item).find("input[name='success']");
                $(successEle).blur(function () {
                    let thisItem = $(this).parent().parent().parent();
                    let success = $(this).val();
                    if (util.isNull(success)) {
                        layer.tips('成功数不能为空', $(this), {tips: 1});
                        $(this).val(0);
                    } else if (!util.isInteger(success)) {
                        layer.tips('只能填写整数', $(this), {tips: 1});
                        $(this).val(0);
                    } else if (parseFloat(success) < 0) {
                        layer.tips('不能小于0', $(this), {tips: 1});
                        $(this).val(0);
                    }
                    mine.renderItemTotal(thisItem);
                    mine.renderTotal();
                });

                // 单价
                let priceEle = $(item).find("input[name='price']");
                $(priceEle).blur(function () {
                    let thisItem = $(this).parent().parent().parent();
                    let price = $(this).val();
                    if (util.isNull(price)) {
                        layer.tips('单价不能为空', $(this), {tips: 1});
                        $(this).val("0.000000");
                    } else if (!$.isNumeric(price)) {
                        layer.tips('只能填写数字', $(this), {tips: 1});
                        $(this).val("0.000000");
                    } else if (parseFloat(price) < 0) {
                        layer.tips('不能小于0', $(this), {tips: 1});
                        $(this).val("0.000000");
                    }
                    mine.renderItemTotal(thisItem);
                    mine.renderTotal();
                });

                // 总数
                let totalEle = $(item).find("input[name='total']");
                $(totalEle).blur(function () {
                    let thisItem = $(this).parent().parent().parent();
                    let total = $(this).val();
                    if (util.isNull(total)) {
                        layer.tips('已收金额不能为空', $(this), {tips: 1});
                        mine.renderItemTotal(thisItem);
                    } else if (!$.isNumeric(total)) {
                        layer.tips('只能填写数字', $(this), {tips: 1});
                        mine.renderItemTotal(thisItem);
                    } else if (parseFloat(total) < 0) {
                        layer.tips('不能小于0', $(this), {tips: 1});
                        mine.renderItemTotal(thisItem);
                    }
                    mine.renderTotal();
                });

                // 添加
                let addBtnEle = $(item).find("button[name='add']");
                $(addBtnEle).click(function () {
                    // 需要先校验
                    if (!mine.verify()) {
                        return;
                    }
                    let thisItemEle = $(this).parent().parent();
                    let itemIndex = $(thisItemEle).attr("data-time-account-bill-index");
                    itemIndex = parseInt(util.formatBlank(itemIndex, 1)) + 1;
                    let newItemDom = mine.getItemDom(null, itemIndex);
                    $(thisItemEle).after(newItemDom);
                    mine.bindEvent(itemIndex);
                    // 删除原来的操作按钮
                    $(thisItemEle).find("div[class*='operate-line']").hide();
                });

                // 删除按钮
                let deleteBtnEle = $(item).find("button[name='delete']");
                if (deleteBtnEle.length > 0) {
                    $(deleteBtnEle).click(function () {
                        let thisItemEle = $(this).parent().parent();
                        let thisItemIndex = $(thisItemEle).attr("data-time-account-bill-index");
                        let labelContentEle = $(thisItemEle).parent();
                        $(thisItemEle).remove();
                        // 将前面的展示出来
                        let preItemEle = $(labelContentEle).find("div[data-time-account-bill-index='" + (parseInt(thisItemIndex) - 1) + "']");
                        $(preItemEle).find("div[class*='operate-line']").show();
                    });
                }
            });
        }
    };

    /**
     * 获取行数据
     * @param accountInfo
     * @param itemIndex
     * @returns {string}
     */
    TimeAccountBillLabel.prototype.getItemDom = function (accountInfo, itemIndex) {
        if (util.isNull(itemIndex)) {
            itemIndex = 1;
        }
        // 开始时间
        let startTime = "";
        // 结束时间
        let endTime = "";
        let success = 0;
        let price = 0.000000;
        let total = 0.00;
        if (util.isNotNull(accountInfo)) {
            startTime = util.formatBlank(accountInfo.start);
            endTime = util.formatBlank(accountInfo.end);
            success = util.formatBlank(accountInfo.success, "0");
            price = util.formatBlank(accountInfo.price, "0.000000");
            total = util.formatBlank(accountInfo.total, "0.00");
        }
        return "<div class='time-account-bill-item' data-time-account-bill-index='" + itemIndex + "'>" +
            "   <div class='time-account-bill-line'>" +
            "       <label required='true'>开始时间：</label>" +
            "       <div class='line-value'>" +
            "           <input type='text' class='layui-input' name='startTime' value='" + startTime + "' readonly placeholder='请选择开始时间'/>" +
            "       </div>" +
            "   </div>" +
            "   <div class='time-account-bill-line'>" +
            "       <label required='true'>结束时间：</label>" +
            "       <div class='line-value'>" +
            "           <input type='text' class='layui-input' name='endTime' value='" + endTime + "' readonly placeholder='请选择结束时间'/>" +
            "       </div>" +
            "   </div>" +
            "   <div class='time-account-bill-line'>" +
            "       <label required='true'>成功数：</label>" +
            "       <div class='line-value'>" +
            "           <input type='text' class='layui-input' name='success' value='" + success + "' placeholder='请填写成功数'/>" +
            "       </div>" +
            "   </div>" +
            "   <div class='time-account-bill-line'>" +
            "       <label required='true'>单价：</label>" +
            "       <div class='line-value'>" +
            "           <input type='text' class='layui-input' name='price' value='" + parseFloat(price).toFixed(6) + "' placeholder='请填写单价'/>" +
            "       </div>" +
            "   </div>" +
            "   <div class='time-account-bill-line'>" +
            "       <label required='true'>金额：</label>" +
            "       <div class='line-value'>" +
            "           <input type='text' class='layui-input' name='total' value='" + parseFloat(total).toFixed(2) + "' placeholder='请填写金额'/>" +
            "       </div>" +
            "   </div>" + this.getItemOpts(itemIndex) +
            "</div>";
    };

    /**
     * 获取操作按钮
     * @param itemIndex 序号
     */
    TimeAccountBillLabel.prototype.getItemOpts = function (itemIndex) {
        let optsDom =
            "<div class='time-account-bill-line operate-line'>" +
            "   <button type='button' class='layui-btn layui-btn-sm layui-btn-primary' name='add'>" +
            "       <i class='layui-icon layui-icon-add-1'></i>添加" +
            "   </button>";

        if (itemIndex > 1) {
            optsDom +=
                "<button type='button' class='layui-btn layui-btn-sm layui-btn-primary' name='delete' data-delete-id='" + itemIndex + "'>" +
                "   <i class='layui-icon layui-icon-delete'></i>删除" +
                "</button>";
        }
        optsDom += "</div>";
        return optsDom;
    };


    /**
     * 解析时间账单金额的需要的数据
     * @param value
     * @param labelValues
     */
    TimeAccountBillLabel.prototype.parseValue = function (value, labelValues) {
        if (util.isNull(value)) {
            // 没有数据
            this.data = [];
            return null;
        }
        let accountBills = null;
        try {
            accountBills = JSON.parse(value);
        } catch (e) {
            console.log("捕获到数据解析异常");
        }
        if (util.isNull(accountBills)) {
            accountBills = [];
            let accountBillInfo = value.split(",");
            let start = "";
            let end = "";
            // 根据账单月份 进行时间的渲染
            try {
                if (util.isNotNull(labelValues)) {
                    let billMonth = labelValues['账单月份'];
                    if (util.isNotNull(billMonth)) {
                        start = billMonth + "-01";
                        end = new Date(billMonth.split("-")[0], parseInt(billMonth.split("-")[1]), 0);
                        let month = end.getMonth() + "";
                        end = end.getFullYear() + "-" + (month.length === 1 ? "0" + month : month) + "-" + end.getDate();
                    }
                }
            } catch (e2) {
                console.log("捕获数据解析异常")
            }
            accountBills.push({
                start: start,
                end: end,
                success: parseInt(util.formatBlank(accountBillInfo[0], 0)),
                price: parseFloat(util.formatBlank(accountBillInfo[1], 0)).toFixed(6),
                total: parseFloat(util.formatBlank(accountBillInfo[2], 0))
            });
        }
        this.data = accountBills;
    };

    /**
     * 渲染总数
     */
    TimeAccountBillLabel.prototype.renderItemTotal = function (item) {
        // 成功数
        let success = $(item).find("input[name='success']").val();
        // 单价
        let price = $(item).find("input[name='price']").val();
        // 总数
        let totalEle = $(item).find("input[name='total']");
        $(totalEle).val((parseInt(util.formatBlank(success, 0)) * parseFloat(util.formatBlank(price, 0))).toFixed(2));
    };

    /**
     * 渲染总数
     */
    TimeAccountBillLabel.prototype.renderTotal = function () {
        // 标签
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 项
        let itemEles = $(labelEle).find("div[data-time-account-bill-index]");
        let totalSuccess = 0;
        let totalMoney = 0;
        if (util.isNotNull(itemEles) && itemEles.length > 0) {
            for (let itemIndex = 0; itemIndex < itemEles.length; itemIndex++) {
                let item = itemEles[itemIndex];
                // 成功数
                let success = $(item).find("input[name='success']").val();
                totalSuccess += parseInt(util.formatBlank(success, 0));
                // 总金额
                let total = $(item).find("input[name='total']").val();
                totalMoney += parseFloat(util.formatBlank(total, 0));
            }
        }
        let totalSuccessEle = $(labelEle).find("span[data-total-success]");
        $(totalSuccessEle[0]).html(totalSuccess);
        let totalMoneyEle = $(labelEle).find("span[data-total-money]");
        $(totalMoneyEle[0]).html(totalMoney.toFixed(2));
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    TimeAccountBillLabel.prototype.getValue = function () {
        // 标签
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 项
        let itemEles = $(labelEle).find("div[data-time-account-bill-index]");
        let resultDatas = [];
        if (util.isNotNull(itemEles) && itemEles.length > 0) {
            for (let itemIndex = 0; itemIndex < itemEles.length; itemIndex++) {
                let item = itemEles[itemIndex];
                let index = $(item).attr("data-platform-account-index");
                // 开始时间
                let startTime = $(item).find("input[name='startTime']").val();
                // 结束时间
                let endTime = $(item).find("input[name='endTime']").val();
                // 成功数
                let success = $(item).find("input[name='success']").val();
                // 单价
                let price = $(item).find("input[name='price']").val();
                // 总金额
                let total = $(item).find("input[name='total']").val();
                resultDatas.push({
                    index: index,
                    start: startTime,
                    end: endTime,
                    success: success,
                    price: price,
                    total: total
                });
            }
        }
        return resultDatas;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    TimeAccountBillLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    TimeAccountBillLabel.prototype.verify = function () {
        // 标签
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 项
        let itemEles = $(labelEle).find("div[data-time-account-bill-index]");
        if (util.isNotNull(itemEles) && itemEles.length > 0) {
            for (let itemIndex = 0; itemIndex < itemEles.length; itemIndex++) {
                let item = itemEles[itemIndex];
                // 开始时间
                let startTimeEle = $(item).find("input[name='startTime']");
                let startTime = $(startTimeEle).val();
                if (util.isNull(startTime)) {
                    // layer.tips("开始时间不能为空", startTimeEle[0], {tips: 1});
                    layer.msg(this.name + "的开始时间不能为空");
                    return false;
                }
                let endTimeEle = $(item).find("input[name='endTime']");
                // 结束时间
                let endTime = $(endTimeEle).val();
                if (util.isNull(endTime)) {
                    // layer.tips("结束时间不能为空", endTimeEle[0], {tips: 1});
                    layer.msg(this.name + "的结束时间不能为空");
                    return false;
                }

                // 成功数
                let successEle = $(item).find("input[name='success']");
                let success = $(successEle).val();
                if (util.isNull(success)) {
                    // layer.tips('成功数不能为空', successEle, {tips: 1});
                    layer.msg(this.name + "的成功数不能为空");
                    return false;
                }
                if (!util.isInteger(success)) {
                    // layer.tips('只能填写整数', successEle, {tips: 1});
                    layer.msg(this.name + "的成功数只能填写整数");
                    return false;
                }
                if (parseFloat(success) <= 0) {
                    // layer.tips('成功数必须大于0', successEle, {tips: 1});
                    layer.msg(this.name + "的成功数必须大于0");
                    return false;
                }

                // 单价
                let priceEle = $(item).find("input[name='price']");
                let price = $(priceEle).val();
                if (util.isNull(price)) {
                    // layer.tips('单价不能为空', priceEle, {tips: 1});
                    layer.msg(this.name + "的单价不能为空");
                    return false;
                }
                if (!$.isNumeric(price)) {
                    // layer.tips('只能填写数字', priceEle, {tips: 1});
                    layer.msg(this.name + "的单价只能填写数字");
                    return false;
                }
                if (parseFloat(price) < 0) {
                    // layer.tips('不能小于0', priceEle, {tips: 1});
                    layer.msg(this.name + "的单价不能小于0");
                    return false;
                }

                // 总金额
                let totalEle = $(item).find("input[name='total']");
                let total = $(totalEle).val();
                if (util.isNull(total)) {
                    // layer.tips('已收金额不能为空', totalEle[0], {tips: 1});
                    layer.msg(this.name + "的已收金额不能为空");
                    return false;
                }
                if (!$.isNumeric(total)) {
                    // layer.tips('只能填写数字', totalEle[0], {tips: 1});
                    layer.msg(this.name + "的已收金额只能填写数字");
                    return false;
                }
                if (parseFloat(total) < 0) {
                    // layer.tips('不能小于0', totalEle[0], {tips: 1});
                    layer.msg(this.name + "的已收金额不能小于0");
                    return false;
                }
            }
        }
        return true;
    };
    return TimeAccountBillLabel;
});