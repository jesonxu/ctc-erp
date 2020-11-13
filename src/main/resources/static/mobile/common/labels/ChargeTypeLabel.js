/**
 * 12
 * 充值类型标签（实际上就是选择类型 只不过是固定了选项值）
 */
(function (window, factory) {
    window.ChargeTypeLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let ChargeTypeLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【充值类型标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【充值类型标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    ChargeTypeLabel.prototype.toText = function (value, defaultValue) {
        if (util.isNull(defaultValue)) {
            return this.name + "：未选择";
        }
        if (util.isNotNull(value)) {
            let chargeTypes = defaultValue.split(",");
            for (let i = 0; i < chargeTypes.length; i++) {
                let type = chargeTypes[i].split(":");
                if ((type.length >= 2) && (value === type[0])) {
                    return this.name + "：" + util.formatBlank(type[1]);
                }
            }
        }
        return this.name + "：未知类型";
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param defaultValue 默认值
     * @param value 值
     * @param required 是否必须
     */
    ChargeTypeLabel.prototype.render = function (flowEle, defaultValue, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【充值类型标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let selectDom = "<select name='" + this.id + "' lay-filter='" + this.id + "' >";
        if (util.isNotNull(defaultValue)) {
            let options = defaultValue.split(",");
            for (let optionIndex = 0; optionIndex < options.length; optionIndex++) {
                // 选项
                let optionInfo = options[optionIndex];
                // 选项信息
                let optionInfos = optionInfo.split(":");
                let optionKey = "";
                let optionValue = "";
                if (optionInfos.length === 1) {
                    optionKey = optionValue = optionInfo;
                } else if (optionInfos.length >= 2) {
                    optionKey = optionInfos[0];
                    optionValue = optionInfos[1];
                }
                if (util.isNotNull(value) && value === optionKey) {
                    selectDom += ("<option value='" + optionKey + "' selected>" + optionValue + "</option>");
                } else {
                    selectDom += ("<option value='" + optionKey + "'>" + optionValue + "</option>");
                }
            }
        }
        selectDom +="</select>";
        let labelDom =
            "<div class='layui-form-item label-type-select' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "       <div class='flow-label-content'>" + selectDom +
            "       </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    ChargeTypeLabel.prototype.getValue = function () {
        return this.getValueEle().val();
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    ChargeTypeLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    ChargeTypeLabel.prototype.verify = function () {
        // 是必须标签
        if (this.required && util.isNull(this.getValue())) {
            layer.msg(this.name + "不能为空");
            return false
        }
        return true;
    };

    ChargeTypeLabel.prototype.getValueEle = function(){
        let valueEle = $(this.flowEle).find("select[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【充值类型】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return ChargeTypeLabel;
});