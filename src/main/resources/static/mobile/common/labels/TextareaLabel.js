/**
 * 文本标签 展示 9
 */
(function (window, factory) {
    window.TextareaLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let TextareaLabel = function (labelId, labelName, labelType) {
        // 渲染的位置（对应元素下面 直接添加）
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【文本标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【文本标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    TextareaLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        return this.name + "：" + value;
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     */
    TextareaLabel.prototype.render = function (flowEle, defaultValue, value, required) {
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【文本标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        if (util.isNull(value)) {
            value = defaultValue;
        }
        value = util.formatBlank(value);
        let labelDom =
            "<div class='layui-form-item label-type-textarea' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <textarea name='" + this.id + "' class='layui-textarea' placeholder='请填写" + this.name + "' >" + value + "</textarea>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        // 渲染的话，等所有的标签 都加载完成后 统一渲染 此处先不渲染
        let valueEle = this.getValueEle();
        let label = this;
        $(valueEle).blur(function (e) {
            // 校验就行
            label.verify();
        });
    };

    /**
     * 获取标签值
     */
    TextareaLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     */
    TextareaLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     */
    TextareaLabel.prototype.verify = function () {
        // 是必须标签
        if (this.required && util.isNull(this.getValue())) {
            // layer.tips(this.name + "不能为空", this.getValueEle(), {tips: 1});
            layer.msg(this.name + "不能为空");
            return false
        }
        return true;
    };

    TextareaLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("textarea[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【文本标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return TextareaLabel;
});