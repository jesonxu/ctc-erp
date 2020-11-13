/**
 * 日期时间 标签 5
 * @author 8520
 */
(function (window, factory) {
    window.DateTimeLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let DateTimeLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【日期时间标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【日期时间标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    DateTimeLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        return this.name + "：" + value;
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     * @param flowEle
     * @param required
     * @param value
     */
    DateTimeLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【日期时间标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        value = util.formatBlank(value);
        let labelDom =
            "<div class='layui-form-item label-type-date-time' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name'" + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='" + this.id + "' type='text' class='layui-input' placeholder='请选择" + this.name + "' readonly value='" + value + "' />" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        // 渲染的话，等所有的标签 都加载完成后 统一渲染 此处先不渲染
        // 增加 失去焦点的事件提示
        // 一个流程里面的标签 id 应该是唯一的
        let valueEle = this.getValueEle();
        layui.use('laydate', function () {
            let laydate = layui.laydate;
            // 渲染日期
            laydate.render({
                elem: valueEle[0],
                type: "datetime",
                trigger: 'click'
            });
        });
    };

    /**
     * 获取标签值
     * @returns {*}
     */
    DateTimeLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     * @returns {*}
     */
    DateTimeLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     * @returns {boolean}
     */
    DateTimeLabel.prototype.verify = function () {
        // 是否必须 是否 为数字
        let value = this.getValue();
        if (this.required && util.isNull(value)) {
            layer.msg(this.name + "不能为空");
            return false;
        }
        return true;
    };

    /**
     * 获取值 填写元素对象
     */
    DateTimeLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【日期时间标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    return DateTimeLabel;
});