/**
 * 月份标签 6
 * @author 8520
 */
(function (window, factory) {
    window.MonthLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let MonthLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【月份标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【月份标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    MonthLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        return this.name + "：" + value;
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     */
    MonthLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【月份标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        value = util.formatBlank(value);
        let labelDom =
            "<div class='layui-form-item label-type-month' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + " >" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='" + this.id + "' type='text' placeholder='请选择" + this.name + "' value='" + value + "' class='layui-input' readonly />" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        // 一个流程里面的标签 id 应该是唯一的
        let valueEle = this.getValueEle();
        layui.use('laydate', function () {
            let laydate = layui.laydate;
            // 渲染日期
            laydate.render({
                elem: valueEle[0],
                type: "month",
                trigger: 'click'
            });
        });
    };

    /**
     * 获取标签值
     * @returns {*}
     */
    MonthLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     * @returns {*}
     */
    MonthLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     * @returns {boolean}
     */
    MonthLabel.prototype.verify = function () {
        // 是否必须 是否 为数字
        let value = this.getValue();
        let label = this;
        if (this.required && util.isNull(value)) {
            layer.msg(this.name + "不能为空");
            return false;
        }
        return true;
    };

    /**
     * 获取值 填写元素对象
     */
    MonthLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【月份标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return MonthLabel;
});