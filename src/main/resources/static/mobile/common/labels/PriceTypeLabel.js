/**
 * 价格类型标签（兼容对 价格梯度标签的渲染 ） 11
 */
(function (window, factory) {
    window.PriceTypeLabel = factory();
})(window, function () {

    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let PriceTypeLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【价格标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【价格标签】ID为空");
        }
        this.labelType = labelType;
        // 1:统一价,2:阶段价,3:阶梯价
    };

    /**
     * 转换为文本
     */
    PriceTypeLabel.prototype.toText = function (defaultValue, value) {
        this.initOption(defaultValue);
        if (this.options.hasOwnProperty(value)) {
            value = this.options[value];
        }
        value = util.formatBlank(value);
        return this.name + "：" + value;
    };

    /**
     *
     * @param defaultValue
     */
    PriceTypeLabel.prototype.initOption = function (defaultValue) {
        let optionObj = {};
        if (util.isNotNull(defaultValue)) {
            // 选项
            let options = defaultValue.split(",");
            for (let optionIndex = 0; optionIndex < options.length; optionIndex++) {
                let option = options[optionIndex];
                let optionArr = option.split(":");
                let optionKey, optionValue = option;
                if (optionArr.length > 1) {
                    optionKey = optionArr[0];
                    optionValue = optionArr[1];
                }
                optionObj[optionKey] = optionValue;
            }
        }
        this.options = optionObj;
    };


    /**
     * 渲染(有值 和没有值 区别回显)
     */
    PriceTypeLabel.prototype.render = function (flowEle, defaultValue, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【价格标签】对应的位置元素不存在");
        }
        if (util.isNotNull(required)) {
            this.required = util.isTrue(required);
        }
        value = util.formatBlank(value);
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
        selectDom += "</select>";
        let labelDom =
            "<div class='layui-form-item label-type-select' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "       <div class='flow-label-content'>" + selectDom +
            "       </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值
     */
    PriceTypeLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     */
    PriceTypeLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     */
    PriceTypeLabel.prototype.verify = function () {
        // 是必须标签
        if (this.required && util.isNull(this.getValue())) {
            layer.msg(this.name + "不能为空");
            return false
        }
        return true;
    };

    PriceTypeLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("select[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【价格标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return PriceTypeLabel;
});