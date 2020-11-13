/**
 * 账单金额标签 15
 */
(function (window, factory) {
    window.AccountBillLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let AccountBillLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【账单金额标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【账单金额标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    AccountBillLabel.prototype.toText = function (value) {
        let supplierSuccess = 0;
        let supplierPrice = 0;
        let totalMoney = 0;
        if (util.isNotNull(value)) {
            let billMoney = value.split(",");
            if (billMoney.length >= 3) {
                supplierSuccess = util.isNull(billMoney[0]) ? 0 : parseInt(billMoney[0]);
                supplierPrice = util.isNull(billMoney[1]) ? 0 : parseFloat(billMoney[1]).toFixed(4);
                totalMoney = util.isNull(billMoney[2]) ? 0 : parseFloat(billMoney[2]).toFixed(2);
            }
        }
        let accountBillDom = util.thousand(supplierSuccess) + "条" + "&nbsp;X&nbsp;"
            + util.thousand(supplierPrice) + "元 &nbsp;=&nbsp;" + util.thousand(totalMoney) + "元";
        return this.name + "：" + accountBillDom;
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    AccountBillLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【账单金额标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let moneyArr = [];
        if (util.isNotNull(value)) {
            moneyArr = value.split(",");
        }
        if (moneyArr.length < 3) {
            // 给定默认值
            moneyArr = [0, 0.000000, 0.00];
        }
        let labelDom =
            "<div class='layui-form-item label-type-account-bill' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <span class='account-bill-success-title'>成功数</span>" +
            "        <input type='text' class='layui-input account-bill-success ' name='success' value='" + moneyArr[0] + "' placeholder='成功数' />" +
            "        <span class='account-bill-multiple'> X </span>" +
            "        <span class='account-bill-price-title'>单价</span>" +
            "        <input type='text' class='layui-input account-bill-price ' name='price' value='" + parseFloat(moneyArr[1]).toFixed(6) + "'  placeholder='单价'/>" +
            "        <span class='account-bill-equal'>=</span>" +
            "        <span class='account-bill-total-title'>金额</span>" +
            "        <input type='text' class='layui-input account-bill-total-money' name='totalMoney' value='" + parseFloat(moneyArr[2]).toFixed(2) + "' placeholder='金额' />" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
    };

    /**
     * 绑定事件
     */
    AccountBillLabel.prototype.bindEvent = function () {
        let thisLabelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 成功数
        let successEle = $(thisLabelEle).find("input[name='success']");
        let mine = this;
        if (successEle.length > 0) {
            $(successEle[0]).blur(function () {
                // 只能填写正整数
                let successCount = $(this).val();
                if (util.isTrue(mine.required) && util.isNull(successCount)) {
                    layer.tips("不能为空", $(this), {tips: 1});
                    $(this).val(0);
                    return;
                }
                if (util.isNotNull(successCount)) {
                    if (!$.isNumeric(successCount)) {
                        layer.tips("只能填写正整数", $(this), {tips: 1});
                        $(this).val(0);
                        return;
                    }
                    if (!util.isInteger(successCount)) {
                        layer.tips("只能填写正整数", $(this), {tips: 1});
                        $(this).val(0);
                        return;
                    }
                    if (parseInt(successCount) < 0) {
                        layer.tips("只能填写正整数", $(this), {tips: 1});
                        $(this).val(0);
                    }
                }
            });
        }

        // 单价
        let priceEle = $(thisLabelEle).find("input[name='success']");
        $(priceEle[0]).blur(function () {
            // 只能填写正数
            let price = $(this).val();
            if (util.isTrue(mine.required) && util.isNull(price)) {
                layer.tips("单价不能为空", $(this), {tips: 1});
                $(this).val("0.000000");
                return;
            }
            if (util.isNotNull(price)) {
                if (!$.isNumeric(price)) {
                    layer.tips("单价只能为大于0的数字", $(this), {tips: 1});
                    $(this).val("0.000000");
                    return;
                }
                if (parseFloat(price) < 0) {
                    layer.tips("单价只能为大于0的数字", $(this), {tips: 1});
                    $(this).val("0.000000");
                }
            }
        });

        // 总金额
        let totalMoneyEle = $(thisLabelEle).find("input[name='success']");
        $(totalMoneyEle[0]).blur(function () {
            // 只能填写正数
            let totalMoney = $(this).val();
            let success = $(successEle).val();
            let price = $(priceEle).val();
            if (util.isNull(success) || $.isNumeric(success)) {
                success = 0;
            }
            if (util.isNull(price) || $.isNumeric(price)) {
                price = 0;
            }
            let defaultTotal = (parseInt(success) * parseFloat(price)).toFixed(2);
            if (util.isTrue(mine.required) && util.isNull(totalMoney)) {
                layer.tips("金额不能为空", $(this), {tips: 1});
                $(this).val(defaultTotal);
                return;
            }
            if (util.isNotNull(totalMoney)) {
                if (!$.isNumeric(totalMoney)) {
                    layer.tips("金额只能为大于0的数字", $(this), {tips: 1});
                    $(this).val(defaultTotal);
                    return;
                }
                if (parseFloat(totalMoney) < 0) {
                    layer.tips("金额只能为大于0的数字", $(this), {tips: 1});
                    $(this).val(defaultTotal);
                }
            }
        });
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    AccountBillLabel.prototype.getValue = function () {
        let thisLabelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 成功数
        let success = $($(thisLabelEle).find("input[name='success']")).val();
        let price = $($(thisLabelEle).find("input[name='price']")).val();
        let money = $($(thisLabelEle).find("input[name='totalMoney']")).val();
        if (util.isNull(success) || !util.isInteger(success) || parseInt(success) < 0) {
            success = 0;
        }
        if (util.isNull(price) || !$.isNumeric(price) || parseFloat(price) < 0) {
            price = 0.000000;
        }
        if (util.isNull(money) || !$.isNumeric(price) || parseFloat(money) < 0) {
            money = 0;
        }
        return success + "," + price + "," + money;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    AccountBillLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    AccountBillLabel.prototype.verify = function () {
        let thisLabelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        // 成功数
        let successEle = $(thisLabelEle).find("input[name='success']");
        // 只能填写正整数
        let success = $(successEle).val();
        if (util.isTrue(this.required) && util.isNull(success)) {
            layer.msg(this.name + "成功数不能为空");
            return false;
        }
        if (util.isNotNull(success)) {
            if (!$.isNumeric(success)) {
                layer.msg(this.name + "只能填写正整数");
                return false;
            }
            if (!util.isInteger(success)) {
                layer.msg(this.name + "只能填写正整数");
                return false;
            }
            if (parseInt(success) < 0) {
                layer.msg(this.name + "只能填写正整数");
                return false;
            }
        }

        // 单价
        let priceEle = $(thisLabelEle).find("input[name='success']");
        // 只能填写正数
        let price = $(priceEle).val();
        if (util.isTrue(this.required) && util.isNull(price)) {
            layer.msg(this.name + "的单价不能为空");
            return false;
        }
        if (util.isNotNull(price)) {
            if (!$.isNumeric(price)) {
                layer.msg(this.name + "的单价只能为大于0的数字");
                return false;
            }
            if (parseFloat(price) < 0) {
                layer.msg(this.name + "的单价只能为大于0的数字");
                return false;
            }
        }

        // 总金额
        let totalMoneyEle = $(thisLabelEle).find("input[name='success']");
        // 只能填写正数
        let totalMoney = $(totalMoneyEle).val();
        if (util.isTrue(this.required) && util.isNull(totalMoney)) {
            layer.msg(this.name + "的金额不能为空");
            return false;
        }
        if (util.isNotNull(totalMoney)) {
            if (!$.isNumeric(totalMoney)) {
                layer.msg(this.name + "的金额只能为大于0的数字");
                return false;
            }
            if (parseFloat(totalMoney) < 0) {
                layer.msg(this.name + "的金额只能为大于0的数字");
                return false;
            }
        }
        return true;
    };

    return AccountBillLabel;
});