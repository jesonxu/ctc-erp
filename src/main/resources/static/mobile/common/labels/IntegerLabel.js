/**
 * 整数标签 1
 * @author 8520
 */
(function (window, factory) {
    window.IntegerLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let IntegerLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【整数标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【整数标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本
     */
    IntegerLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        return this.name + "：" + value;
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     * @param flowEle
     * @param defaultValue
     * @param value
     * @param required
     */
    IntegerLabel.prototype.render = function (flowEle, defaultValue, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【整数标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);

        // 放置表达式处理方法
        if(util.isNotNull(defaultValue)) {
            if (/^\{\{(.+?)\}\}$/.test(defaultValue)) {
                expressionTool.push(this.bindInputLinkage(defaultValue));
            } else if (util.isNull(value)) {
                value = defaultValue;
            }
        }

        value = util.formatBlank(value);
        let labelDom =
            "<div class='layui-form-item label-type-integer' data-label-id='" + this.id + "'>" +
            "    <div class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</div>" +
            "       <div class='flow-label-content'>" +
            "           <input name='" + this.id + "' type='text' value='" + value + "' placeholder='请输入" + this.name + "' class='layui-input' />" +
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
                $(this).val(value);
            }
        });
    };

    /**
     * 获取标签值
     */
    IntegerLabel.prototype.getValue = function () {
        let valueEle = this.getValueEle();
        return valueEle.val();
    };

    /**
     * 获取标签名称
     */
    IntegerLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验
     */
    IntegerLabel.prototype.verify = function () {
        // 是否必须 是否 为整数
        let value = this.getValue();
        let label = this;
        if (this.required && util.isNull(value)) {
            layer.msg(label.name + "不能为空");
            return false;
        }
        if (util.isNotNull(value) && !util.isInteger(value)) {
            layer.msg(label.name + "只能填写整数");
            return false;
        }
        return true;
    };

    /**
     * 获取值 填写元素对象
     */
    IntegerLabel.prototype.getValueEle = function () {
        let valueEle = $(this.flowEle).find("input[name='" + this.id + "']");
        if (util.isNull(valueEle) || valueEle.length === 0) {
            // 防止取值异常 不能定位
            throw new Error("【整数标签】：" + this.name + "，值区域元素未能查找到");
        }
        return valueEle;
    };

    /**
     * 处理表达式
     */
    IntegerLabel.prototype.bindInputLinkage = function (expression) {
        let targetLabelName = this.name;
        let targetInputId = this.id;
        return function(flowEle, labelList) { // 表单, 表达式, 流程标签
            if (util.isNull(expression) || util.isNull(labelList) || labelList.length === 0) {
                return;
            }
            if (/^\{\{(.+?)\}\}$/.test(expression)) {
                expression = expression.substr(2, expression.length - 4);
            }
            let variableMap = {};
            let labelArr = [];
            let labelTypeMap = {};
            $(labelList).each(function (i, item) {
                labelTypeMap[item['name']] = {
                    labelType: item['labelType'],
                    id: item['id']
                }
            });
            let list = expressionTool.getExecStrs(expression, /#\{(.+?)\}/g);
            if (list && list.length > 0) {
                $(list).each(function (i, item) {
                    variableMap[item] = null;
                    if (labelArr.indexOf(item) < 0) {
                        labelArr.push(item);
                    }
                });
            }
            $(labelArr).each(function (i, item) {
                $(flowEle).find('input[name="' + labelTypeMap[item]['id'] + '"]').bind('keyup', function () {
                    let exp = expression;
                    let vMap = util.cloneObj(variableMap);
                    for (let j = 0; j < labelArr.length; j++) { // 获取各个input的值
                        let value = $(flowEle).find('input[name="' + labelTypeMap[labelArr[j]]['id'] + '"]').val();
                        if (util.isNull(value)) {
                            if (labelTypeMap[labelArr[j]]['labelType'] == 1) {
                                value = 0;
                            } else if (labelTypeMap[labelArr[j]]['labelType'] == 2) {
                                value = 0.00;
                            } else {
                                value = '';
                            }
                        }
                        vMap[labelArr[j]] = value;
                    }
                    $(labelArr).each(function (index, v) {
                        exp = exp.replace(new RegExp('#{' + v + '}', 'gm'), (labelTypeMap[v]['labelType'] == 1
                            || labelTypeMap[v]['labelType'] == 2) ? vMap[v] : ('"' + vMap[v] + '"'));
                    });
                    let val = eval(exp);
                    if (labelTypeMap[targetLabelName]['labelType'] == 1) {
                        val = val == Infinity ? 0 : val;
                        val = isNaN(val) ? 0 : val;
                        val = val.toFixed(2);
                        val = parseInt(val);
                    } else if (labelTypeMap[targetLabelName]['labelType'] == 2) {
                        val = val == Infinity ? 0.000000 : val;
                        val = isNaN(val) ? 0.000000 : val;
                        val = parseFloat(val).toFixed(6);
                    } else {
                        val = val === undefined || val === null ? '' : val;
                    }
                    $(flowEle).find('input[name="' + targetInputId + '"]').val(val);
                });
            });
        }
    };

    return IntegerLabel;
});