/**
 *合同编号标签 21
 */
(function (window, factory) {
    window.ContractNumberLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let ContractNumberLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【合同编号标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【合同编号标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    ContractNumberLabel.prototype.toText = function (value) {
        return this.name + "：" + util.formatBlank(value, "");
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    ContractNumberLabel.prototype.render = function (flowEle, value, required) {
        this.data = util.formatBlank(value);
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【合同编号标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let labelDom =
            "<div class='layui-form-item label-type-contract-number' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='" + this.id + "' type='text' placeholder='流程自动生成，不需要填写' class='layui-input' disabled value='" + this.data + "'/>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    ContractNumberLabel.prototype.getValue = function () {
        return this.data;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    ContractNumberLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    ContractNumberLabel.prototype.verify = function () {
        // 不需要校验（由后台进行管理）
        return true;
    };
    return ContractNumberLabel;
});