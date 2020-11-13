/**
 * 应用于管理 流程标签的渲染 数据获取
 * 用于渲染 【申请】 的时候的标签
 **/
(function (window, factory) {
    window.FlowApplyLabels = factory();
})(window, function () {
    /**
     * 标签渲染 工具对象
     */
    let FlowApplyLabels = function () {
        // 初始化的标签(存放标签数据)
        this.labelList = [];
    };

    /**
     * 流程标签渲染成可编辑的内容
     * @param targetEle 渲染的地方
     * @param flowInfo 流程信息
     * @returns {boolean}
     */
    FlowApplyLabels.prototype.renderLabels = function (targetEle, flowInfo) {
        //this.targetEle, labelList, editLabelIds, labelValues
        if (util.isNull(targetEle)) {
            throw new Error("渲染标签，指定的位置位置");
        }
        // 流程的标签信息(可以看的)
        let labelList = flowInfo.labels;
        // 可以编辑的标签信息
        let editLabelIds = flowInfo.editLabels;
        // 已经填写的标签值
        if (util.isNull(editLabelIds)) {
            return false;
        }
        if (util.arrayNull(labelList)) {
            return false;
        }
        //debugger;
        // 可编辑标签ID
        for (let labelIndex = 0; labelIndex < labelList.length; labelIndex++) {
            let label = labelList[labelIndex];
            if (editLabelIds.indexOf(label.id) >= 0) {
                let labelRender = this.renderLabel(targetEle, flowInfo, label);
                if (util.isNotNull(labelRender)) {
                    this.labelList.push(labelRender);
                }
            }
        }
        // 执行表达式
        expressionTool.execAll(targetEle, this.labelList);
        // 渲染表单
        layui.use(['layer', 'element', 'form', 'laydate', 'flow'], function () {
            let form = layui.form;
            form.render(); //更新全部
        });
    };


    /**
     * 流程标签渲染成可编辑的内容
     * @param labelRenderEle 标签渲染的地方
     * @param flowInfo 流程信息
     * @param label 需要渲染的标签
     */
    FlowApplyLabels.prototype.renderLabel = function (labelRenderEle, flowInfo, label) {
        // 流程的标签信息(可以看的)
        let labelList = flowInfo.labels;
        // 流程类型
        let flowClass = flowInfo.flowClass;
        // 已经填写的标签值
        let labelValues = flowInfo.labelValueMap;
        // 流程ID
        let flowId = flowInfo.flowId;
        // 标签ID
        let labelId = label.id;
        // 标签名
        let labelName = label.name;
        // 标签默认值
        let defaultValue = label.defaultValue;
        // 标签值
        let value = this.getValueFromMapByName(labelName, labelValues);
        // 是否必须
        let required = util.formatBlank(flowInfo.mustLabels).indexOf(labelId) >= 0;
        // 标签类型
        let labelType = parseInt(label.type);
        let labelRender;
        // 产品id
        let productId = flowInfo.productId;
        if (labelType === 0) {
            // 字符串
            labelRender = new TextLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 1) {
            // 整数
            labelRender = new IntegerLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 2) {
            // 小数
            labelRender = new NumberLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 3) {
            // 布尔类型(有默认值)
            labelRender = new BooleanLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 4) {
            // 日期类型 yyyy-MM-dd
            labelRender = new DateLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 5) {
            // 时间 日期类型 yyyy-MM-dd HH:ss:mm
            labelRender = new DateTimeLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 6) {
            // 月份类型 yyyy-MM
            labelRender = new MonthLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 7) {
            // 下拉框类型 (flowEle, defaultValue, value, required)
            labelRender = new SelectLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 8) {
            // 文件类型
            labelRender = new FileLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 9) {
            // 文本 类型
            labelRender = new TextareaLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 10) {
            // 价格梯度类型
            labelRender = new GradientLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, labelValues, labelList, required);
        } else if (labelType === 11) {
            //价格类型
            labelRender = new PriceTypeLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 12) {
            //充值类型
            labelRender = new ChargeTypeLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required);
        } else if (labelType === 13) {
            //酬金
            labelRender = new RemunerationLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 14) {
            //账单
            labelRender = new AccountInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, flowInfo);
        } else if (labelType === 15) {
            //账单金额
            labelRender = new AccountBillLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 16) {
            //开关类型
            labelRender = new SwitchLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 17) {
            // 我司开票信息
            labelRender = new SelfInvoiceLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 18) {
            // 对方开票信息
            labelRender = new OtherInvoiceLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 19) {
            // 我司银行信息
            labelRender = new SelfBankLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 20) {
            // 对方银行信息
            labelRender = new OtherBankLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 21) {
            // 合同编号
            labelRender = new ContractNumberLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 22) {
            // 历史单价
            labelRender = new HistoryPriceLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 23) {
            // 发票信息
            labelRender = new InvoiceInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, flowId, flowClass);
        } else if (labelType === 29) {
            //客户开票抬头
            labelRender = new CustInvoiceInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, labelList);
        } else if (labelType === 30) {
            //账单开票信息
            labelRender = new BillInvoiceInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, flowId, flowClass);
        } else if (labelType === 32) {
            labelRender = new TimeAccountBillLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, labelValues);
        } else if (labelType === 33) {
            labelRender = new PlatformAccountInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 34) {
            // 未对账账单
            labelRender = new UncheckedBillInfoLabel(labelId, labelName, labelType);
            // 无法找到对应的实体ID 需要在标签里面进行判断
            labelRender.render(labelRenderEle, value, required, flowId, null);
        } else if (labelType === 35) {
            // 时间选择
            labelRender = new TimeSlotLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 36) {
            // 单选
            labelRender = new RadioLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, defaultValue, value, required, flowId);
        } else if (labelType === 37) {
        	// 充值详情
        	labelRender = new RechargeDetail(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, productId);
        } else {
            // 字符串(设置为默认的)
            labelRender = new TextLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        }
        return labelRender;
    };


    /**
     * 获取标签值
     * @param labelName
     * @param labelValueMap
     */
    FlowApplyLabels.prototype.getValueFromMapByName = function (labelName, labelValueMap) {
        if (util.isNull(labelName)) {
            return "";
        }
        if (util.isNull(labelValueMap)) {
            return "";
        }
        if (labelValueMap.hasOwnProperty(labelName)) {
            return labelValueMap[labelName];
        }
        return "";
    };

    /**
     * 获取流程所有值
     */
    FlowApplyLabels.prototype.getLabelValues = function () {
        let labelList = this.labelList;
        let result = {};
        if (labelList.length > 0) {
            for (let index = 0; index < labelList.length; index++) {
                let label = labelList[index];
                let labelName = label.getName();
                result[labelName] = label.getValue();
            }
        }
        return result;
    };

    /**
     * 校验标签
     * 这里只管结果 不管提示 （具体的提示 在标签里面写）
     */
    FlowApplyLabels.prototype.checkLabels = function () {
        let labelList = this.labelList;
        if (labelList.length > 0) {
            for (let index = 0; index < labelList.length; index++) {
                let label = labelList[index];
                if (!label.verify()) {
                    return false;
                }
            }
        }
        return true;
    };

    return FlowApplyLabels;
});