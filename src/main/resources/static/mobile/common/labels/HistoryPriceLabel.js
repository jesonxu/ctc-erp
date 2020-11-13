/**
 *历史单价标签 22 （线上没有查找到使用的地方）
 */
(function (window, factory) {
    window.HistoryPriceLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let HistoryPriceLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【历史单价标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【历史单价标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    HistoryPriceLabel.prototype.toText = function (value) {
        return this.name + "：" + util.thousand(util.formatBlank(value, 0));
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    HistoryPriceLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【历史单价标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let labelDom =
            "<div class='layui-form-item label-type-history-price' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='" + this.id + "' type='text' value='" + value + "' placeholder='请填写" + this.name + "' class='layui-input' >" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    HistoryPriceLabel.prototype.getValue = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']");
        return $(valueEle).val();
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    HistoryPriceLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    HistoryPriceLabel.prototype.verify = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']");
        if (this.required && util.isNull(this.getValue())) {
            layer.msg(this.name + "不能为空");
            return false
        }
        return true;
    };
    return HistoryPriceLabel;
});