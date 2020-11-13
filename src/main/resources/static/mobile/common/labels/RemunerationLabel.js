/**
 *酬金类型标签 13
 */
(function (window, factory) {
    window.RemunerationLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let RemunerationLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【酬金类型标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【酬金类型标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    RemunerationLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "0.00,0.00,0.00,0.00,0.00";
        }
        let values = value.split(",");
        let valueText = "金额" + util.thousand(values[0]) + "元&nbsp;"
            + "X&nbsp;酬金比例&nbsp;" + util.thousand(values[1]) + "%&nbsp;"
            + "+&nbsp;奖励&nbsp;" + util.thousand(values[2]) + "元&nbsp;"
            + "－&nbsp;扣款&nbsp;" + util.thousand(values[3]) + "元&nbsp;"
            + "=&nbsp;" + util.thousand(values[4]) + "元";
        return this.name + "：" + valueText;
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    RemunerationLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【酬金类型标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let labelDom =
            "<div class='layui-form-item label-type-remuneration' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + " >" + this.name + ":</label>" +
            "    <div class='flow-label-content remuneration-content'>" +
            "        <div class='remuneration-row'>" +
            "           <span>金额：</span>" +
            "           <input type='text' name='money' class='layui-input' value='0.00' placeholder='金额' /> " +
            "        </div>" +
            "        <div class='remuneration-row'>" +
            "           <span>比例(%)：</span>" +
            "           <input type='text' name='ratio' class='layui-input' value='0.00' placeholder='酬金比例（百分比）' />" +
            "        </div>" +
            "        <div class='remuneration-row'>" +
            "           <span>奖励：</span>" +
            "           <input type='text' name='reward' class='layui-input' value='0.00' placeholder='奖励' /> " +
            "        </div>" +
            "        <div class='remuneration-row'>" +
            "           <span>扣款：</span> " +
            "           <input type='text' name='deduction' class='layui-input' value='0.00' placeholder='扣款' /> " +
            "        </div>" +
            "        <div class='remuneration-row'>" +
            "           <span>酬金：</span>" +
            "           <input type='text' name='deserved' class='layui-input' value='0.00' readonly placeholder='酬金' /> " +
            "        </div>" +
            "        <div class='formula'>(计算公式：金额 X 比例 + 奖励 - 扣款 = 酬金)</div>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        // 绑定改变事件
        this.bindChangeEvent();
    };

    /**
     * 绑定改变事件
     */
    RemunerationLabel.prototype.bindChangeEvent = function () {
        let rowEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        $(rowEle).find("input[name='money']").blur(function () {
            // 金额
            let money = $(this).val();
            if (util.isNull(money)) {
                layer.tips("金额不能空", $(this), {tips: 1});
                $(this).val("0.00");
                return;
            }
            //  数字
            if (!$.isNumeric(money)) {
                layer.tips("金额只能为数字", $(this), {tips: 1});
                $(this).val("0.00");
            }

            // 不能为负数
            if (parseFloat(money) < 0) {
                layer.tips("金额不能为负数", $(this), {tips: 1});
                $(this).val("0.00");
            }
        });

        $(rowEle).find("input[name='ratio']").blur(function () {
            // 酬金比例
            let ratio = $(this).val();
            if (util.isNull(ratio)) {
                layer.tips("酬金比例不能空", $(this), {tips: 1});
                $(this).val("0.00");
                return;
            }
            //  数字
            if (!$.isNumeric(ratio)) {
                layer.tips("酬金比例只能为数字", $(this), {tips: 1});
                $(this).val("0.00");
                return;
            }
            // 不能为负数
            if (parseFloat(ratio) < 0) {
                layer.tips("酬金比例不能为负数", $(this), {tips: 1});
                $(this).val("0.00");
            }
        });
        $(rowEle).find("input[name='reward']").blur(function () {
            // 奖励
            let reward = $(this).val();
            if (util.isNull(reward)) {
                $(this).val("0.00");
                return;
            }
            //  数字
            if (!$.isNumeric(reward)) {
                layer.tips("奖励只能是数字", $(this), {tips: 1});
                $(this).val("0.00");
                return;
            }
            // 不能为负数
            if (parseFloat(reward) < 0) {
                layer.tips("奖励不能为负数", $(this), {tips: 1});
                $(this).val("0.00");
            }
        });
        $(rowEle).find("input[name='deduction']").blur(function () {
            // 扣款
            let deduction = $(this).val();
            if (util.isNull(deduction)) {
                $(this).val("0.00");
                return;
            }
            //  数字
            if (!$.isNumeric(deduction)) {
                layer.tips("扣款只能为数字", $(this), {tips: 1});
                $(this).val("0.00");
                return;
            }
            // 不能为负数
            if (parseFloat(deduction) < 0) {
                layer.tips("扣款不能为负数", $(this), {tips: 1});
                $(this).val("0.00");
            }
        });
        this.compute();
    };

    /**
     * 计算 应得酬金
     */
    RemunerationLabel.prototype.compute = function () {
        let rowEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let money = $(rowEle).find("input[name='money']").val();
        if (util.isNull(money) || !$.isNumeric(money) || parseFloat(money) < 0) {
            money = 0.00;
        }
        let ratio = $(rowEle).find("input[name='ratio']").val();
        if (util.isNull(ratio) || !$.isNumeric(ratio) || parseFloat(ratio) < 0) {
            ratio = 0.0000;
        }
        let reward = $(rowEle).find("input[name='reward']").val();
        if (util.isNull(reward) || !$.isNumeric(reward) || parseFloat(reward) < 0) {
            reward = 0.00;
        }
        let deduction = $(rowEle).find("input[name='deduction']").val();
        if (util.isNull(deduction) || !$.isNumeric(deduction) || parseFloat(deduction) < 0) {
            deduction = 0.00;
        }
        // 应得酬金
        $(rowEle).find("input[name='deserved']").val(((money * ratio) / 100 + reward - deduction).toFixed(4));
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    RemunerationLabel.prototype.getValue = function () {
        let rowEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let money = $(rowEle).find("input[name='money']").val();
        if (util.isNull(money) || !$.isNumeric(money) || parseFloat(money) < 0) {
            money = 0.00;
        }
        let ratio = $(rowEle).find("input[name='ratio']").val();
        if (util.isNull(ratio) || !$.isNumeric(ratio) || parseFloat(ratio) < 0) {
            ratio = 0.00;
        }
        let reward = $(rowEle).find("input[name='reward']").val();
        if (util.isNull(reward) || !$.isNumeric(reward) || parseFloat(reward) < 0) {
            reward = 0.00;
        }
        let deduction = $(rowEle).find("input[name='deduction']").val();
        if (util.isNull(deduction) || !$.isNumeric(deduction) || parseFloat(deduction) < 0) {
            deduction = 0.00;
        }
        let deserved = $(rowEle).find("input[name='deserved']").val();
        if (util.isNull(deserved) || !$.isNumeric(deserved)) {
            deserved = 0.00;
        }
        return money + "," + ratio + "," + reward + "," + deduction + "," + deserved;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    RemunerationLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    RemunerationLabel.prototype.verify = function () {
        let rowEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let moneyEle = $(rowEle).find("input[name='money']");
        let money = $(moneyEle).val();
        if (util.isNull(money)) {
            layer.msg(this.name + "的金额不能为空");
            return false;
        }
        if (!$.isNumeric(money)) {
            layer.msg(this.name + "的金额只能为数字");
            return false;
        }
        if (parseFloat(money) < 0) {
            // layer.tips("金额不能为负数", moneyEle, {tips: 1});
            layer.msg(this.name + "的金额不能为负数");
            return false;
        }

        let ratioEle = $(rowEle).find("input[name='ratio']");
        let ratio = $(ratioEle).val();
        if (util.isNull(ratio)) {
            // layer.tips("酬金比例不能为空", ratioEle, {tips: 1});
            layer.msg(this.name + "的酬金比例不能为空");
            return false;
        }
        if (!$.isNumeric(ratio)) {
            // layer.tips("酬金比例只能为数字", ratioEle, {tips: 1});
            layer.msg(this.name + "的酬金比例只能为数字");
            return false;
        }
        if (parseFloat(ratio) < 0) {
            // layer.tips("酬金比例不能为负数", ratioEle, {tips: 1});
            layer.msg(this.name + "的酬金比例不能为负数");
            return false;
        }
        // 奖励
        let rewardEle = $(rowEle).find("input[name='reward']");
        let reward = $(rewardEle).val();
        if (util.isNotNull(reward)) {
            if (!$.isNumeric(reward)) {
                // layer.tips("奖励只能为数字", rewardEle, {tips: 1});
                layer.msg(this.name + "的奖励只能为数字");
                return false;
            }
            if (parseFloat(reward) < 0) {
                // layer.tips("奖励不能为负数", rewardEle, {tips: 1});
                layer.msg(this.name + "的奖励不能为负数");
                return false;
            }
        }
        // 扣款
        let deductionEle = $(rowEle).find("input[name='deduction']");
        let deduction = $(deductionEle).val();
        if (util.isNotNull(deduction)) {
            if (!$.isNumeric(deduction)) {
                // layer.tips("扣款只能为数字", rewardEle, {tips: 1});
                layer.msg(this.name + "的扣款只能为数字");
                return false;
            }
            if (parseFloat(deduction) < 0) {
                // layer.tips("扣款不能为负数", rewardEle, {tips: 1});
                layer.msg(this.name + "的扣款不能为负数");
                return false;
            }
        }
        return true;
    };
    return RemunerationLabel;
});