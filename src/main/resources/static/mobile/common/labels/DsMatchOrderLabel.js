/**
 *配单信息标签
 */
(function (window, factory) {
    window.DsMatchOrderLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @constructor
     */
    var DsMatchOrderLabel = function (labelId, labelName) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【配单信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【配单信息标签】ID为空");
        }
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    DsMatchOrderLabel.prototype.toText = function (value) {
        console.log("配单信息标签：name:" + this.name + " - value:" + value);
        return this.name + ":" + util.formatBlank(value);
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    DsMatchOrderLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【配单信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        var prop = "";
        if (this.required) {
            prop = " required = true "
        }
        var labelDom = "";
        // TODO:具体的渲染方法
        $(this.flowEle).append(labelDom);
        // TODO:元素渲染完成后的操作
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    DsMatchOrderLabel.prototype.getValue = function () {
        return null;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    DsMatchOrderLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    DsMatchOrderLabel.prototype.verify = function () {
        // 初始化的时候已经有选择 不需要再校验
        return true;
    };
    return DsMatchOrderLabel;
});