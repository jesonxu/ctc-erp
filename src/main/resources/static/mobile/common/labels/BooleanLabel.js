/**
 * 布尔标签 3
 * @author 8520
 */
(function (window, factory) {
    window.BooleanLabel = factory();
})(window, function () {

    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let BooleanLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【布尔标签】标签名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【布尔标签】标签ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    BooleanLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = false;
        }
        return (this.name + "：") + (util.isTrue(value) ? "是" : "否");
    };

    /**
     * 渲染可以编辑的标签
     * @param flowEle 渲染的地方
     * @param defaultValue 默认值
     * @param value 值
     * @param required 是否必须
     */
    BooleanLabel.prototype.render = function (flowEle, defaultValue, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【布尔标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        if (util.isNull(value)) {
            // 优先展示 本次的值 没有本次的值 就渲染为 默认值
            value = defaultValue;
        }
        value = util.formatBlank(value, 0);
        let labelDom =
            "<div class='layui-form-item label-type-bool' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='" + this.id + "' type='radio' value='1' title='是' " + (util.isTrue(value) ? " checked " : "") + " >" +
            "        <input name='" + this.id + "' type='radio' value='0' title='否' " + (util.isTrue(value) ? "" : " checked ") + " >" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值
     * @returns {*}
     */
    BooleanLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     * @returns {*}
     */
    BooleanLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     * @returns {boolean}
     */
    BooleanLabel.prototype.verify = function () {
        // 初始化的时候已经有选择 不需要再校验
        return true;
    };

    /**
     * 获取值 填写元素对象
     */
    BooleanLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']:checked");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            throw new Error("【布尔标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return BooleanLabel;
});