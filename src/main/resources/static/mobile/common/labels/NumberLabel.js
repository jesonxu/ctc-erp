/**
 * 数字标签 2
 * @author 8520
 */
(function (window, factory) {
    window.NumberLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let NumberLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【数字标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【数字标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    NumberLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        return this.name + "：" + value;
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     */
    NumberLabel.prototype.render = function (flowEle, defaultValue, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【数字标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
       /* if (util.isNull(value)) {
            value = defaultValue;
        }*/
        value = util.formatBlank(value);
        let labelDom =
            "<div class='layui-form-item label-type-number' data-label-id='" + this.id + "'>" +
            "    <div class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</div>" +
            "       <div class='flow-label-content'>" +
            "           <input name='" + this.id + "' type='text' value='" + value + "' placeholder='请输入" + this.name + "' class='layui-input' >" +
            "       </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        // 渲染的话，等所有的标签 都加载完成后 统一渲染 此处先不渲染
        // 增加 失去焦点的事件提示
        // 一个流程里面的标签 id 应该是唯一的
        let valueEle = this.getValueEle();
        let label = this;
        $(valueEle).blur(function (e) {
            let verifyResult = label.verify();
            if (!verifyResult) {
                $(this).val("");
            }
        });
    };

    /**
     * 获取标签值
     * @returns {*}
     */
    NumberLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     * @returns {*}
     */
    NumberLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     * @returns {boolean}
     */
    NumberLabel.prototype.verify = function () {
        // 是否必须 是否 为数字
        let value = this.getValue();
        if (this.required && util.isNull(value)) {
            layer.msg(this.name + "不能为空");
            return false;
        }
        if (util.isNotNull(value) && !$.isNumeric(value)) {
            layer.msg(this.name + "只能填写数字");
            return false;
        }
        return true;
    };

    /**
     * 获取值 填写元素对象
     */
    NumberLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【数字标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };
    return NumberLabel;
});