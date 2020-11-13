/**
 * 价格梯度标签 10
 * @author 8520
 */
(function (window, factory) {
    window.GradientLabel = factory();
})(window, function () {
    /**
     * 统一价
     * @type {{
     *     price: number, 单价
     *     priceType: string, 价格类型 固定
     *     provinceprice: number 省网价格
     * }}
     */
    let uniform = function (obj) {
        this.price = 0.0000;
        this.provinceprice = 0.0000;
        if (util.isNotNull(obj)) {
            for (let key in obj) {
                let value = obj[key];
                if (util.isNotNull(value)) {
                    this[key] = value;
                }
            }
        }
        this.priceType = "uniform";
    };

    /**
     * 阶梯价 和 阶段价（展示、属性是相同的）
     * @type {{
     *      maxsend: number, 最大发送量 可为空
     *      provinceproportion: number, 省占比
     *      complaintrate: number, 投诉比
     *      price: number, 单价
     *      gradient: number,  梯度
     *      priceType: string, 价格类型 固定
     *      isdefault: number,  是否默认
     *      minsend: number  最小发送量
     * }}
     */
    let Gradient = function (obj) {
        this.isdefault = "";
        this.minsend = "";
        this.maxsend = "";
        this.price = "";
        this.provinceproportion = "";
        this.complaintrate = "";
        this.gradient = "";
        if (util.isNotNull(obj)) {
            for (let key in obj) {
                let value = obj[key];
                if (util.isNotNull(value)) {
                    this[key] = value;
                }
            }
        }
        this.priceType = "gradient";
    };

    /**
     * 统一价 名称
     */
    const UNIFORM_NAME = {
        "price": "单价",
        "priceType": "价格类型", // 价格梯度标签中，价格类型不展示
        "provinceprice": "省网价格"
    };

    /**
     * 梯度价 名称
     */
    const GRADIENT_NAME = {
        "gradient": "梯度",
        "minsend": "最小发送量",
        "maxsend": "最大发送量",
        "price": "单价",
        "provinceproportion": "省占比",
        "complaintrate": "投诉比",
        "priceType": "", // 价格梯度标签中，价格类型不展示
        "isdefault": "是否默认",
    };

    /**
     * 价格类型 对应数据格式
     * @type {{"1": string, "2": string, "3": string}}
     */
    const PRICE_TYPE = {
        1: "uniform",
        2: "gradient",
        3: "gradient"
    };

    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let GradientLabel = function (labelId, labelName, labelType) {
        // 渲染的位置（对应元素下面 直接添加）
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【价格梯度标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【价格梯度标签】ID为空");
        }
        this.labelType = labelType;
        // 价格梯度分三种类型 1:统一价,2:阶段价,3:阶梯价 [默认 （统一价）]
        this.type = 1;
        // 关联的 标签名称
        this.refereLabelName = "价格类型";
    };

    /**
     * 转换为文本
     * @param value 值
     * @returns {string}
     */
    GradientLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            value = "";
        }
        this.detectTypeByValue(value);
        let labelTxt = "";
        if (this.type === 1) {
            labelTxt = this.toUniformText(value);
        } else {
            labelTxt = this.toGradientText(value);
        }
        return this.name + "：" + labelTxt;
    };

    /**
     * 通过内容 判断类型（只能判断 展示的格式，足够）
     * @param value
     */
    GradientLabel.prototype.detectTypeByValue = function (value) {
        // 默认 统一价
        this.type = 1;
        if (util.isNull(value)) {
            return;
        }
        let priceInfos = typeof value == "object" ? value : JSON.parse(value);
        if (util.arrayNotNull(priceInfos)) {
            for (let index = 0; index < priceInfos.length; index++) {
                let priceInfo = priceInfos[index];
                if (priceInfo.hasOwnProperty("gradient")) {
                    this.type = 2;
                    return;
                }
            }
        }
    };

    /**
     * 统一价格 展示方式
     * @param value
     */
    GradientLabel.prototype.toUniformText = function (value) {
        // 价格是按照 数据进行存储的
        let uniformInfo = JSON.parse(value);
        if (util.arrayNotNull(uniformInfo)) {
            let uniformDate = uniformInfo[0];
            let labelInfo = [];
            for (let key in uniformDate) {
                console.log("统一价格，标签名：" + key);
                // 价格类型 不展示
                if (key !== "priceType") {
                    let keyName = "";
                    if (UNIFORM_NAME.hasOwnProperty(key)) {
                        keyName = UNIFORM_NAME[key];
                    }
                    if (util.isNotNull(keyName)) {
                        let keyValue = uniformDate[key];
                        if (util.isNull(keyValue)) {
                            keyValue = "-";
                        }
                        labelInfo.push(keyName + ":" + keyValue);
                    }
                }
            }
            return labelInfo.join(",");
        }
        return "";
    };

    /**
     * 转换成梯度价格文本
     * @param value
     * @returns {string}
     */
    GradientLabel.prototype.toGradientText = function (value) {
        // 价格是按照 数据进行存储的
        let gradientInfos = typeof value == "object" ? value : JSON.parse(value);
        if (util.arrayNotNull(gradientInfos)) {
            let labelInfo = [];
            for (let gradientIndex = 0; gradientIndex < gradientInfos.length; gradientIndex++) {
                let gradientInfo = gradientInfos[gradientIndex];
                let gradient = parseInt(gradientInfo.gradient) + 1;
                let minsend = util.thousand(gradientInfo.minsend);
                let maxsend = util.isNull(gradientInfo.maxsend) ? "∞" : util.thousand(gradientInfo.maxsend);
                let price = util.thousand(gradientInfo.price);
                let provinceRate = util.formatBlank(gradientInfo.provinceproportion, "空");
                if (provinceRate !== "空") {
                    provinceRate = provinceRate + "%";
                }
                let complaintRate = util.formatBlank(gradientInfo.complaintrate, "空");
                if (complaintRate !== "空") {
                    complaintRate = complaintRate + "%";
                }
                let isDefault = util.isTrue(gradientInfo.isdefault) ? "默认" : "";
                let gradeItem = "梯度:" + gradient + "【" + minsend + " 条 =< 发送量 < " + maxsend + " 条】" + isDefault + "，单价：" + price
                    + "，省占比：" + provinceRate + "，投诉比：" + complaintRate;
                labelInfo.push("<br><span class='flow-record-2-content'>" + gradeItem + "</span>")
            }
            return labelInfo.join("");
        }
        return "";
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     * @param flowEle 渲染的地方
     * @param value 已经有的值
     * @param labelValues 流程所有的值
     * @param labelList 流程的所有标签
     * @param required 是否必须
     */
    GradientLabel.prototype.render = function (flowEle, value, labelValues, labelList, required) {
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【价格梯度标签】对应的位置元素不存在");
        }
        if (util.isNotNull(required)) {
            this.required = util.isTrue(required);
        }
        value = util.formatBlank(value);
        this.detectRenderType(value, labelValues, labelList);
        if (this.type === 1 || this.type === '1') {
            this.renderUniform(value);
        } else {
            this.renderGradient(value);
        }
        // 绑定校验事件
        this.bindVerifyEvent();
        // 监听关联改变
        this.changeType();
    };

    /**
     * 渲染 统一价
     * @param value
     */
    GradientLabel.prototype.renderUniform = function (value) {
        let price = "";
        let provincePrice = "";
        if (util.isNotNull(value)) {
            if (value.hasOwnProperty("price")) {
                price = value.price;
                if (util.isNull(price)) {
                    price = "";
                }
            }
            if (value.hasOwnProperty("provinceprice")) {
                provincePrice = value.provinceprice;
                if (util.isNull(provincePrice)) {
                    provincePrice = "";
                }
            }
        }
        let labelDom =
            "<div class='layui-form-item label-type-gradient' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' required='true'>价格:</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='price' type='text' value='" + price + "' class='layui-input' >" +
            "    </div><br/>" +
            "    <label class='flow-label-name'>省网价格:</label>" +
            "    <div class='flow-label-content'>" +
            "        <input name='provincePrice' type='text' value='" + provincePrice + "' class='layui-input' >" +
            "    </div>" +
            "</div>";
        if (util.isNull(this.labelEle)) {
            $(this.flowEle).append(labelDom);
        } else {
            $(this.labelEle).after(labelDom);
            $(this.labelEle).remove();
        }
        // 找到自己所在的标签元素
        this.initLabelEle();
    };

    /**
     * 渲染 梯度价格
     */
    GradientLabel.prototype.renderGradient = function (value) {
        let labelDom = "<div class='layui-form-item label-type-gradient' data-label-id='" + this.id + "'>";
        if (util.isNotNull(value)) {
            value = JSON.parse(value);
            let itemCount = value.length;
            for (let index = 0; index < itemCount; index++) {
                let data = value[index];
                let last = index === (itemCount - 1);
                labelDom += this.gradientItemDom(index, data, last);
            }
        } else {
            labelDom += this.gradientItemDom(0, null, true);
        }
        labelDom += "</div>";
        if (util.isNull(this.labelEle)) {
            $(this.flowEle).append(labelDom);
        } else {
            $(this.labelEle).after(labelDom);
            $(this.labelEle).remove();
        }
        // 找到自己所在的标签元素
        this.initLabelEle();
        // 绑定 校验 和 按钮的事件
        this.bindBtnEvent();
    };

    /**
     * 绑定校验事件
     */
    GradientLabel.prototype.bindVerifyEvent = function () {
        // 根据类型绑定校验
        if (this.type === 1) {
            // 单价
            $("input[name='price']").blur(function (e) {
                let price = $(this).val();
                if (util.isNull(price)) {
                    layer.tips("价格不能为空", this, {tips: 1});
                    return;
                }
                if (!$.isNumeric(price)) {
                    layer.tips("价格只能填数字", this, {tips: 1});
                    $(this).val("");
                }
            });
            // 省网价格
            $("input[name='provincePrice']").blur(function (e) {
                let provincePrice = $(this).val();
                if (util.isNotNull(provincePrice) && !$.isNumeric(provincePrice)) {
                    layer.tips("省网价格只能填数字", this, {tips: 1});
                    $(this).val("");
                }
            });
        } else {
            // 梯度价格
            // 最小发送量
            let minSendArr = $(this.labelEle).find("input[name='minSend']");
            for (let minSendIndex = 0; minSendIndex < minSendArr.length; minSendIndex++) {
                let minSendEle = minSendArr[minSendIndex];
                // 所有的最小发送量 不可编辑 且 第一个 默认设置为0
                $(minSendEle).attr("readonly", "readonly");
                if (minSendIndex === 0) {
                    $(minSendEle).val(0);
                }
            }
            // 最大发送量
            let maxSendArr = $(this.labelEle).find("input[name='maxSend']");
            for (let maxSendIndex = 0; maxSendIndex < maxSendArr.length; maxSendIndex++) {
                let maxSendEle = maxSendArr[maxSendIndex];
                $(maxSendEle).blur(function () {
                    let maxSendCount = $(this).val();
                    if (util.isNotNull(maxSendCount) && !$.isNumeric(maxSendCount)) {
                        $(this).val("");
                        layer.tips("最大发送量只能填数字", this, {tips: 1});
                    }
                });
            }

            // 价格
            let priceArr = $(this.labelEle).find("input[name='price']");
            for (let priceIndex = 0; priceIndex < priceArr.length; priceIndex++) {
                let priceEle = priceArr[priceIndex];
                $(priceEle).blur(function () {
                    let price = $(this).val();
                    if (util.isNull(price)) {
                        layer.tips("价格不能为空", this, {tips: 1});
                        return;
                    }
                    if (!$.isNumeric(price)) {
                        $(this).val("");
                        layer.tips("价格只能填数字", this, {tips: 1});
                    }
                });
            }

            // 百万投比
            let complaintRateArr = $(this.labelEle).find("input[name='complaintRate']");
            for (let complainIndex = 0; complainIndex < complaintRateArr.length; complainIndex++) {
                let complaintRateEle = complaintRateArr[complainIndex];
                $(complaintRateEle).blur(function () {
                    let price = $(this).val();
                    if (util.isNotNull(price) && !$.isNumeric(price)) {
                        $(this).val("");
                        layer.tips("百万投比只能填数字", this, {tips: 1});
                    }
                });
            }

            // 省占比
            let provinceRateArr = $(this.labelEle).find("input[name='provinceProportion']");
            for (let provinceRateIndex = 0; provinceRateIndex < provinceRateArr.length; provinceRateIndex++) {
                let provinceRateEle = provinceRateArr[provinceRateIndex];
                $(provinceRateEle).blur(function () {
                    let price = $(this).val();
                    if (util.isNotNull(price) && !$.isNumeric(price)) {
                        $(this).val("");
                        layer.tips("省占比只能填数字", this, {tips: 1});
                    }
                });
            }
            this.bindBtnEvent();
        }
    };

    /**
     * 绑定 按钮事件
     */
    GradientLabel.prototype.bindBtnEvent = function () {
        let my = this;
        // 统一价 没有新增按钮
        if (this.type !== 1) {
            let addBtnEle = $(this.labelEle).find("button[data-operate='add']");
            if (util.isNotNull(addBtnEle) && addBtnEle.length > 0) {
                $(addBtnEle[0]).unbind("click").bind("click", function (e) {
                    my.addItem(this);
                });
            }
            let deleteBtnEle = $(this.labelEle).find("button[data-operate='delete']");
            if (util.isNotNull(deleteBtnEle) && deleteBtnEle.length > 0) {
                $(deleteBtnEle[0]).unbind("click").bind("click", function (e) {
                    my.deleteItem(this);
                });
            }
        }
    };

    /**
     * 新增项
     * @param ele
     */
    GradientLabel.prototype.addItem = function (ele) {
        let itemIndex = $(ele).parent().attr("data-gradient-index");
        if (util.isNull(itemIndex)) {
            throw new Error("按钮无法确定梯度");
        }
        itemIndex = parseInt(itemIndex);
        // 校验数据
        let verifyResult = this.verify(true);
        if (!verifyResult) {
            return;
        }
        verifyResult = this.verifySendCount(true);
        if (null == verifyResult) {
            return;
        }
        let newItemDom = this.gradientItemDom(itemIndex + 1, verifyResult, true);
        // 添加新的按钮
        $(this.labelEle).append(newItemDom);
        // 删除原来的
        $(ele).parent().remove();
        this.bindBtnEvent();
        layui.use(['form'], function () {
            let form = layui.form;
            form.render();
        });
    };

    /**
     * 删除项
     * @param ele
     */
    GradientLabel.prototype.deleteItem = function (ele) {
        let itemIndex = $(ele).parent().attr("data-gradient-index");
        if (util.isNull(itemIndex)) {
            throw new Error("按钮无法确定梯度");
        }
        itemIndex = parseInt(itemIndex);
        // 校验数据
        let preItemBtnDom = this.gradientOperateBtn(itemIndex - 1, true);
        // 添加新的按钮
        let preItem = $(this.labelEle).find("div[data-item-index='" + (itemIndex - 1) + "']")[0];
        $(preItem).append(preItemBtnDom);
        // 启用上一个梯度的最大值输入框
        $(preItem).find('input[name=maxSend]').removeAttr("disabled");
        $(ele).parents('div.gradient-item').remove();
        this.bindBtnEvent();
    };

    /**
     * 获取 梯度项DOM
     * @param index     梯度从0开始
     * @param value
     * @param last
     * @returns {string}
     */
    GradientLabel.prototype.gradientItemDom = function (index, value, last) {
        let gradient = new Gradient(value);
        // 1选中默认，0未选中
        let isDefault = (gradient.isdefault === 1 || gradient.isdefault === '1') ? "checked" : "";
        let gradientItem =
            "<div class='gradient-item' data-item-index='" + index + "'>" +
            "    <div class='layui-form-item'>" +
            // "        <label class='flow-label-name'>设为默认:</label>" +
            "        <div class='flow-label-content'>" +
            "            <input name='isdefault' type='radio' value='" + index + "' " + isDefault + " class='layui-input' title='设为默认'>" +
            "        </div>" +
            "    </div>" +
            "    <div class='layui-form-item'>" +
            "        <label class='flow-label-name'>最小发送量:</label>" +
            "        <div class='flow-label-content'>" +
            "            <input name='minSend' type='text' value='" + gradient.minsend + "' class='layui-input' placeholder='请填写最少发送量' disabled>" +
            "        </div>" +
            "    </div>" +
            "    <div class='layui-form-item'>" +
            "       <label class='flow-label-name'>最大发送量:</label>" +
            "       <div class='flow-label-content'>" +
            "           <input name='maxSend' type='text' value='" + gradient.maxsend + "' class='layui-input' placeholder='请填写最大发送量'" + (last ? "" : " disabled ") + ">" +
            "       </div>" +
            "    </div>" +
            "    <div class='layui-form-item'>" +
            "       <label class='flow-label-name'>价格:</label>" +
            "       <div class='flow-label-content'>" +
            "           <input name='price' type='text' value='" + gradient.price + "' class='layui-input' placeholder='请填写价格' >" +
            "       </div>" +
            "    </div>" +
            "    <div class='layui-form-item'>" +
            "       <label class='flow-label-name'>百万投比:</label>" +
            "       <div class='flow-label-content'>" +
            "           <input name='complaintRate' type='text' value='" + gradient.complaintrate + "' class='layui-input' placeholder='请填写百万投比' >" +
            "       </div>" +
            "    </div>" +
            "    <div class='layui-form-item'>" +
            "       <label class='flow-label-name'>省占比:</label>" +
            "       <div class='flow-label-content'>" +
            "           <input name='provinceProportion' type='text' value='" + gradient.provinceproportion + "' class='layui-input' placeholder='请填写省占比' >" +
            "       </div>" +
            "    </div>";
        gradientItem += this.gradientOperateBtn(index, last);
        gradientItem += "</div>";
        return gradientItem;
    };

    /**
     * 获取梯度项的 操作按钮
     * @param index
     * @param last
     */
    GradientLabel.prototype.gradientOperateBtn = function (index, last) {
        if (util.isTrue(last)) {
            // 第一个梯度不能删
            if (index === 0) {
                return "<div class='layui-form-item gradient-opts' data-gradient-index='" + index + "'>" +
                    "    <button class='layui-btn layui-btn-primary layui-btn-xs' data-operate = 'add'>" +
                    "        <i class='layui-icon layui-icon-add-circle'></i>添加" +
                    "    </button>" +
                    "</div>";
            } else {
                return "<div class='layui-form-item gradient-opts' data-gradient-index='" + index + "' >" +
                    "    <button class='layui-btn layui-btn-primary layui-btn-xs' data-operate = 'add'>" +
                    "        <i class='layui-icon layui-icon-add-circle'></i>添加" +
                    "    </button>" +
                    "    <button class='layui-btn layui-btn-danger layui-btn-xs' data-operate='delete'>" +
                    "        <i class='layui-icon layui-icon-close'></i>删除" +
                    "    </button>" +
                    "</div>";
            }
        }
        return "";
    };

    /**
     * 改变类型(暂时不实现)
     */
    GradientLabel.prototype.changeType = function () {
        let my = this;
        if (util.isNotNull(this.refereLabelId)) {
            layui.use(['form'], function () {
                let form = layui.form;
                form.on("select(" + my.refereLabelId + ")", function (data) {
                    console.log("监听到" + my.refereLabelName + "选项事件改变：" + data.value);
                    // 价格类型
                    my.type = parseInt(data.value);
                    if (my.type === 1) {
                        my.renderUniform("");
                    } else {
                        my.renderGradient("");
                    }
                    // 找到自己所在的标签元素
                    my.initLabelEle();
                    // 绑定校验事件
                    my.bindVerifyEvent();
                    form.render();
                });
            });
        }
    };

    /**
     * 探测 渲染的类型
     * @param labelValues 所有的标签值
     * @param value 标签值
     * @param labelList 标签 列表
     */
    GradientLabel.prototype.detectRenderType = function (value, labelValues, labelList) {
        // 默认 统一价
        this.type = 1;

        let hasDetected = false;
        if (util.arrayNotNull(labelValues)) {
            // 通过关联标签 进行判断类型
            if (labelValues.hasOwnProperty(this.refereLabelName)) {
                // 标签值
                let labelType = labelValues[this.refereLabelName];
                if (util.isNotNull(labelType) && util.isInteger(labelType)) {
                    this.type = parseInt(labelType);
                    hasDetected = true;
                }
            }
        }
        if ((!hasDetected) && util.isNotNull(value)) {
            // 关联标签 无法判断的时候 需要进行 值的判断
            this.detectTypeByValue(value);
        }
        let my = this;
        // 查找绑定的标签ID
        if (util.arrayNotNull(labelList)) {
            for (let labelIndex in labelList) {
                let label = labelList[labelIndex];
                if (label.name === my.refereLabelName) {
                    this.refereLabelId = label.id;
                    break;
                }
            }
        }
    };


    /**
     * 获取标签值
     */
    GradientLabel.prototype.getValue = function () {
        // 最后的结果
        let result = [];
        // 根据类型绑定校验
        if (this.type === 1) {
            // 单价
            let uniformPrice = $(this.labelEle).find("input[name='price']").val();
            // 省网价格
            let provincePrice = $(this.labelEle).find("input[name='provincePrice']").val();
            result.push({"price": uniformPrice, "priceType": "uniform", "provinceprice": provincePrice});
        } else {
            // 梯度价格
            let gradientItems = $(this.labelEle).find("div[data-item-index]");
            for (let itemIndex = 0; itemIndex < gradientItems.length; itemIndex++) {
                let gradientItemEle = gradientItems[itemIndex];
                // 序号
                let gradientIndex = $(gradientItemEle).attr("data-item-index");
                let minSend = $(gradientItemEle).find("input[name='minSend']").val();
                // 最大发送量
                let maxSend = $(gradientItemEle).find("input[name='maxSend']").val();
                // 价格
                let price = $(gradientItemEle).find("input[name='price']").val();
                // 百万投比
                let complaintRate = $(gradientItemEle).find("input[name='complaintRate']").val();
                // 省占比
                let provinceRate = $(this.labelEle).find("input[name='provinceProportion']").val();
                // 默认
                let isDefault = $(gradientItemEle).find("input[name=isdefault]").prop('checked') ? 1 : 0;
                result.push({
                    "isdefault": util.formatBlank(isDefault),
                    "minsend": util.formatBlank(minSend),
                    "maxsend": util.formatBlank(maxSend),
                    "price": util.formatBlank(price),
                    "provinceproportion": util.formatBlank(provinceRate),
                    "complaintrate": util.formatBlank(complaintRate),
                    "gradient": util.formatBlank(gradientIndex),
                    "priceType": "gradient"
                });
            }
        }
        return result;
    };

    /**
     * 获取标签名称
     */
    GradientLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外暴露的接口)
     */
    GradientLabel.prototype.verify = function (test) {
        // 标签 元素
        // 根据类型绑定校验
        if (this.type === 1) {
            // 单价
            let uniformPriceEle = $(this.labelEle).find("input[name='price']");
            let uniformPrice = $(uniformPriceEle).val();
            if (util.isNull(uniformPrice)) {
                layer.msg(this.name + "的价格不能为空");
                return false;
            }
            if (!$.isNumeric(uniformPrice)) {
                layer.msg(this.name + "的价格只能填数字");
                $(uniformPriceEle).val("");
                return false;
            }

            // 省网价格
            let provincePriceEle = $(this.labelEle).find("input[name='provincePrice']");
            let provincePrice = $(provincePriceEle).val();
            if (util.isNotNull(provincePrice) && !$.isNumeric(provincePrice)) {
                layer.msg(this.name + "省网价格只能填数字");
                $(this).val("");
                return false;
            }
        } else {
            // 梯度价格
            if (!test) {
                // 提交的时候
                let gradientItems = $(this.labelEle).find('div.gradient-item');
                if (gradientItems.length === 1) {
                    layer.msg('至少有两个梯度');
                    return false;
                }

                // 默认的序号
                let defaultGradient = $(this.labelEle).find("input[name='isdefault']:checked");
                if (defaultGradient.length === 0) {
                    layer.msg('请选择默认梯度');
                    return false;
                }
            }

            // 最大发送量
            let maxSendArr = $(this.labelEle).find("input[name='maxSend']");
            for (let maxSendIndex = 0; maxSendIndex < maxSendArr.length; maxSendIndex++) {
                let maxSendEle = maxSendArr[maxSendIndex];
                let maxSendCount = $(maxSendEle).val();
                if (!test && maxSendIndex === maxSendArr.length - 1 && maxSendCount !== "") {
                    layer.msg('最后一个梯度的最大值应该为空，表示正无穷');
                    return false;
                }
                if (util.isNotNull(maxSendCount) && !$.isNumeric(maxSendCount)) {
                    layer.msg(this.name + "的最大发送量只能填数字");
                    return false;
                }
            }

            // 价格
            let priceArr = $(this.labelEle).find("input[name='price']");
            for (let priceIndex = 0; priceIndex < priceArr.length; priceIndex++) {
                let priceEle = priceArr[priceIndex];
                let price = $(priceEle).val();
                if (util.isNull(price)) {
                    layer.msg(this.name + "的价格不能为空");
                    return false;
                }
                if (!$.isNumeric(price)) {
                    layer.msg(this.name + "的价格只能填数字");
                    $(priceEle).val("");
                    return false;
                }
            }

            // 百万投比
            let complaintRateArr = $(this.labelEle).find("input[name='complaintRate']");
            for (let complainIndex = 0; complainIndex < complaintRateArr.length; complainIndex++) {
                let complaintRateEle = priceArr[complainIndex];
                let complaintRate = $(complaintRateEle).val();
                if (util.isNotNull(complaintRate) && !$.isNumeric(complaintRate)) {
                    layer.msg(this.name + "的百万投比只能填数字");
                    $(complaintRateEle).val("");
                    return false;
                }
            }

            // 省占比
            let provinceRateArr = $(this.labelEle).find("input[name='provinceProportion']");
            for (let provinceRateIndex = 0; provinceRateIndex < provinceRateArr.length; provinceRateIndex++) {
                let provinceRateEle = priceArr[provinceRateIndex];
                let provinceRate = $(provinceRateEle).val();
                if (util.isNotNull(provinceRate) && !$.isNumeric(provinceRate)) {
                    layer.msg(this.name + "的省占比只能填数字");
                    $(provinceRateEle).val("");
                    return false;
                }
            }
        }
        return true;
    };

    /**
     * 校验 发送量
     */
    GradientLabel.prototype.verifySendCount = function (add) {
        // 根据类型绑定校验 // 梯度价格
        let result = {};
        if (this.type !== 1) {
            let gradientItems = $(this.labelEle).find("div[data-item-index]");
            let gradientCount = gradientItems.length;
            for (let itemIndex = 0; itemIndex < gradientCount; itemIndex++) {
                let gradientItemEle = gradientItems[itemIndex];
                // 序号
                let gradientIndex = $(gradientItemEle).attr("data-item-index");
                let last = parseInt(gradientIndex) === (gradientCount - 1);
                let minSend = $(gradientItemEle).find("input[name='minSend']").val();

                let maxEle = $(gradientItemEle).find("input[name='maxSend']");
                // 最大发送量
                let maxSend = $(maxEle).val();
                if (!last) {
                    if (util.isNull(maxSend)) {
                        layer.tips("最大发送量不能为空", maxEle, {tips: 1});
                        return null;
                    }
                    if (parseInt(maxSend) <= parseInt(minSend)) {
                        layer.tips("最大发送量必须大于最少发送量", maxEle, {tips: 1});
                        return null;
                    }
                } else {
                    add = util.isTrue(add);
                    if (add) {
                        // 添加的时候 必须要填写上一个的最大值
                        if (util.isNull(maxSend)) {
                            layer.tips("最大发送量不能为空", maxEle, {tips: 1});
                            return null;
                        }
                        if (util.isNotNull(maxSend) && parseInt(maxSend) <= parseInt(minSend)) {
                            layer.tips("最大发送量必须大于最少发送量", maxEle, {tips: 1});
                            return null;
                        }
                        // 上一个梯度的最大值是下一个梯度的最小值，禁用最后一个梯度的最大值输入框
                        result.minsend = maxSend;
                        $(maxEle).attr('disabled', 'disabled');
                    }
                }
            }
        }
        return result;
    };

    /**
     * 初始化 自己的元素 标签所在元素
     */
    GradientLabel.prototype.initLabelEle = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        if (util.isNull(labelEle) || labelEle.lenght === 0) {
            this.labelEle = null;
        }
        this.labelEle = labelEle[0];
    };

    return GradientLabel;
});