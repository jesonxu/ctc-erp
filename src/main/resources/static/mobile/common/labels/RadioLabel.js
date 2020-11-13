/**
 * 单选框标签 36
 */
(function (window, factory) {
    window.RadioLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let RadioLabel = function (labelId, labelName, labelType) {
        // 渲染的位置（对应元素下面 直接添加）
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【单选框标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【单选框标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    RadioLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        return this.name + "：" + value;
    };

    /**
     * 渲染
     * @param flowEle
     * @param defaultValue
     * @param value
     * @param required
     * @param flowEntId
     */
    RadioLabel.prototype.render = function (flowEle, defaultValue, value, required, flowEntId) {
        // 渲染的位置（对应元素下面 直接添加）
        let othis = this;
        this.flowEle = flowEle;
        this.flowEntId = flowEntId;
        if (util.isNull(this.flowEle)) {
            throw new Error("【单选框标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        if (util.isNull(defaultValue)) {
            // 优先展示 本次的值 没有本次的值 就渲染为 默认值
            defaultValue = '';
        }
        let labelDom =
            "<div class='layui-form-item label-type-radio' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>";
        let options = defaultValue.split(',');
        $.each(options, function (index, item) {
            labelDom += "<input name='" + flowEntId + "_" + othis.id + "' type='radio' value='" + item + "' title='" + item + "' " + (value == item ? " checked " : "") + " >";
        });
        labelDom +=
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值
     */
    RadioLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        for (let index = 0; index < valueEle.length; index++) {
            if ($(valueEle[index]).attr('checked')) {
                return $(valueEle[index]).val();
            }
        }
        return null;
    };

    /**
     * 获取标签名称
     */
    RadioLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     */
    RadioLabel.prototype.verify = function () {
        let checked = false;
        // 是必须标签
        if (this.required) {
            let valueEle = this.getValueEle();
            if (util.isNotNull(valueEle)) {
                for (let index = 0; index < valueEle.length; index++) {
                    if ($(valueEle[index]).attr('checked')) {
                        checked = true;
                    }
                }
                if (!checked) {
                    layer.msg("请选择" + this.name);
                }
            } else {
                layer.msg("没有选项，请联系管理员");
            }
            return checked
        }
        return true;
    };

    RadioLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.flowEntId + "_" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【单选框标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return RadioLabel;
});