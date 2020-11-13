/**
 *开关标签（系统里面暂时还没有使用） 16
 */
(function (window, factory) {
    window.SwitchLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let SwitchLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【开关标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【开关标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    SwitchLabel.prototype.toText = function (value) {
        return this.name + "：" + (util.isTrue(value) ? "开" : "关");
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    SwitchLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【开关标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let open = "close";
        if (util.isTrue(value)){
            open = "open";
        }
        let labelDom = "<div class='layui-form-item label-type-switch' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='" + open + "' type='checkbox' value='1' lay-skin='switch' lay-text='开|关' lay-filter='" + this.id + "' />" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    SwitchLabel.prototype.getValue = function () {
        let switchBtn = $(this.flowEle).find("div[data-label-id='" + this.id + "']").find("div[class*='layui-form-onswitch']");
        if (switchBtn.length > 0) {
            return 1;
        } else {
            return 0;
        }
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    SwitchLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    SwitchLabel.prototype.verify = function () {
        // 初始化的时候已经有选择 不需要再校验
        return true;
    };
    return SwitchLabel;
});