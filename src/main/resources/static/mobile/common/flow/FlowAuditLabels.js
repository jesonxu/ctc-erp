/**
 * 应用于管理 流程标签的渲染 数据获取
 * 用于渲染 【审核】 的时候的标签
 **/
(function (window, factory) {
    window.FlowAuditLabels = factory();
})(window, function () {
    /**
     * 标签渲染 工具对象
     */
    let FlowAuditLabels = function () {
        // 初始化的标签(存放标签数据)
        this.labelList = [];
    };

    /**
     * 流程标签渲染成可编辑的内容
     * @param targetEle 渲染的地方
     * @param flowInfo 流程信息
     * @returns {boolean}
     */
    FlowAuditLabels.prototype.renderLabels = function (targetEle, flowInfo) {
        //this.targetEle, labelList, editLabelIds, labelValues
        if (util.isNull(targetEle)) {
            throw new Error("渲染标签，指定的位置位置");
        }
        // 流程的标签信息(可以看的)
        let labelList = flowInfo.labelList;
        if (util.arrayNull(labelList)) {
            return false;
        }
        // 可以编辑的标签信息
        let editLabelIds = flowInfo.editLabelIds;
        if (util.isNull(editLabelIds)) {
            return false;
        }
        // 增加 标签展示区域
        let flowEntId = $(targetEle).attr("data-flow-id");
        let flowDetailEle = $(targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']");
        let editLabelDom = "<div class='layui-form flow-edit-labels' lay-filter='" + flowEntId + "'></div>";
        $(flowDetailEle).append(editLabelDom);
        // 渲染的编辑标签地方
        let labelRenderEle = $(flowDetailEle).find("div[class*='flow-edit-labels']");
        // 可编辑标签ID
        let editLabelIdArr = editLabelIds.split(",");
        for (let labelIndex = 0; labelIndex < labelList.length; labelIndex++) {
            let label = labelList[labelIndex];
            let labelId = label.id;
            if (editLabelIdArr.indexOf(labelId) >= 0) {
                // 可以编辑的标签
                let labelRender = this.renderLabel(labelRenderEle, label, flowInfo, labelList);
                this.labelList.push(labelRender);
            }
        }
        // 渲染表单
        layui.use(['layer', 'element', 'form', 'laydate', 'flow'], function () {
            let form = layui.form;
            form.render(); //更新全部
        });
    };

    /**
     * 渲染单个标签
     * @param labelRenderEle 标签渲染的地方
     * @param label 标签
     * @param flowInfo 流程信息
     * @param labelList 所有标签信息（用于关联标签）
     */
    FlowAuditLabels.prototype.renderLabel = function (labelRenderEle, label, flowInfo,labelList) {
        let labelId = label.id;
        // 必须标签
        let mustLabelIds = util.formatBlank(flowInfo.mustLabelIds);
        // 是否必须
        let required = mustLabelIds.indexOf(labelId) >= 0;
        // 已经填写的标签值
        let labelValues = flowInfo.labelValueMap;
        //流程ID
        let flowId = flowInfo.flowId;
        // 流程类型
        let flowClass = flowInfo.flowClass;
        // 产品ID
        let productId = flowInfo.productId;
        // 实体ID（后面可能没有）
        let entityId = flowInfo.supplierId;
        // 标签名
        let labelName = label.name;
        // 标签默认值
        let defaultValue = label.defaultValue;
        // 流程实体ID
        let flowEntId = flowInfo.flowEntId;
        // 标签值
        let value = this.getValueFromMapByName(labelName, labelValues);
        // debugger
        // 标签类型
        let labelType = parseInt(label.type);
        let labelRender = null;
        if (labelType === 0) {
            // 字符串
            labelRender = new TextLabel(labelId, labelName, labelType);
            //flowEle, defaultValue, value, required
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
            //账单金额
            labelRender = new SwitchLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 17) {
            labelRender = new SelfInvoiceLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, entityId);
        } else if (labelType === 18) {
            // 对方开票信息
            labelRender = new OtherInvoiceLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, entityId);
        } else if (labelType === 19) {
            // 我司银行信息
            labelRender = new SelfBankLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, entityId);
        } else if (labelType === 20) {
            // 对方银行信息
            labelRender = new OtherBankLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, entityId);
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
            labelRender.render(labelRenderEle, value, required, flowId, flowClass, productId);
        } else if (labelType === 29) {
            //客户开票抬头
            labelRender = new CustInvoiceInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, labelList, entityId);
        } else if (labelType === 30) {
            //账单开票信息
            labelRender = new BillInvoiceInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, flowId, flowClass, entityId);
        } else if (labelType === 32) {
            labelRender = new TimeAccountBillLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, labelValues);
        } else if (labelType === 33) {
            labelRender = new PlatformAccountInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required);
        } else if (labelType === 34) {
            // 未对账账单
            labelRender = new UncheckedBillInfoLabel(labelId, labelName, labelType);
            labelRender.render(labelRenderEle, value, required, flowEntId, entityId);
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
     * 通过名称 获取已经渲染的标签
     * @param name
     */
    FlowAuditLabels.prototype.getLabelByName = function (name) {
        for (let index in this.labelList) {
            let label = this.labelList[index];
            if (label.getName() === name) {
                return label;
            }
        }
        return null;
    };

    /**
     * 获取标签值
     * @param labelName
     * @param labelValueMap
     */
    FlowAuditLabels.prototype.getValueFromMapByName = function (labelName, labelValueMap) {
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
    FlowAuditLabels.prototype.getLabelValues = function () {
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
    FlowAuditLabels.prototype.checkLabels = function () {
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


    /**
     * 标签 渲染成 展示文本
     * @param label
     * @param value
     * @param flowInfo 流程信息（整个流程的信息）
     */
    FlowAuditLabels.prototype.renderText = function (label, value, flowInfo) {
        let labelId = label.id;
        let labelName = label.name;
        let flowId = flowInfo.flowId;
        let flowEntId = flowInfo.flowEntId;
        let flowClass = flowInfo.flowClass;
        // 标签类型
        let labelType = parseInt(label.type);
        // debugger;
        if (labelType === 0) {
            // 字符串
            return new TextLabel(labelId, labelName).toText(value);
        } else if (labelType === 1) {
            // 整数
            return new IntegerLabel(labelId, labelName).toText(value);
        } else if (labelType === 2) {
            // 小数
            return new NumberLabel(labelId, labelName).toText(value);
        } else if (labelType === 3) {
            // 布尔类型(有默认值)
            return new BooleanLabel(labelId, labelName).toText(value);
        } else if (labelType === 4) {
            // 日期类型 yyyy-MM-dd
            return new DateLabel(labelId, labelName).toText(value);
        } else if (labelType === 5) {
            // 时间 日期类型 yyyy-MM-dd HH:ss:mm
            return new DateTimeLabel(labelId, labelName).toText(value);
        } else if (labelType === 6) {
            // 月份类型 yyyy-MM
            return new MonthLabel(labelId, labelName).toText(value);
        } else if (labelType === 7) {
            // 下拉框类型
            return new SelectLabel(labelId, labelName).toText(value);
        } else if (labelType === 8) {
            // 文件类型
            return new FileLabel(labelId, labelName).toText(value);
        } else if (labelType === 9) {
            // 文本 类型
            return new TextareaLabel(labelId, labelName).toText(value);
        } else if (labelType === 10) {
            // 价格梯度类型
            return new GradientLabel(labelId, labelName).toText(value);
        } else if (labelType === 11) {
            //价格类型
            return new PriceTypeLabel(labelId, labelName).toText(label.defaultValue, value);
        } else if (labelType === 12) {
            //充值类型
            return new ChargeTypeLabel(labelId, labelName).toText(value);
        } else if (labelType === 13) {
            // 酬金信息 金额*酬金比例+奖励-扣款
            return new RemunerationLabel(labelId, labelName).toText(value);
        } else if (labelType === 14) {
            let flowEntId = flowInfo.flowEntId;
            let flowId = flowInfo.flowId;
            // 账单展示
            return new AccountInfoLabel(labelId, labelName).toText(value, flowEntId, flowId, flowClass);
        } else if (labelType === 15) {
            // 账单金额标签
            return new AccountBillLabel(labelId, labelName).toText(value);
        } else if (labelType === 16) {
            // 开关类型（系统没有地方用到，但是有这个标签 暂时先放在这里）
            return new SwitchLabel(labelId, labelName).toText(value);
        } else if (labelType === 17) {
            // 我司开票信息
            return new SelfInvoiceLabel(labelId, labelName).toText(value);
        } else if (labelType === 18) {
            // 对方开票信息
            return new OtherInvoiceLabel(labelId, labelName).toText(value);
        } else if (labelType === 19) {
            // 我司银行信息
            return new SelfBankLabel(labelId, labelName).toText(value);
        } else if (labelType === 20) {
            // 对方银行信息
            return new OtherBankLabel(labelId, labelName).toText(value);
        } else if (labelType === 21) {
            // 合同编号
            return new ContractNumberLabel(labelId, labelName).toText(value);
        } else if (labelType === 22) {
            // 历史单价
            return new HistoryPriceLabel(labelId, labelName).toText(value);
        } else if (labelType === 23) {
            // 发票信息
            return new InvoiceInfoLabel(labelId, labelName).toText(value);
        } else if (labelType === 24) {
            return new DsApplyOrderLabel(labelId, labelName).toText(value);
        } else if (labelType === 25) {
            return new DsMatchOrderLabel(labelId, labelName).toText(value);
        } else if (labelType === 26) {
            // 订单编号
            return new DsOrderNumberLabel(labelId, labelName).toText(value);
        } else if (labelType === 27) {
            // 配单员信息
            return new DsMatchPeopleLabel(labelId, labelName).toText(value);
        } else if (labelType === 28) {
            // 采购单编号
            return new DsPurchaseNumberLabel(labelId, labelName).toText(value);
        } else if (labelType === 29) {
            return new CustInvoiceInfoLabel(labelId, labelName).toText(value);
        } else if (labelType === 30) {
            return new BillInvoiceInfoLabel(labelId, labelName).toText(value, flowId, flowEntId, flowClass);
        } else if (labelType === 31) {
            return new BankInfoLabel(labelId, labelName).toText(value);
        } else if (labelType === 32) {
            return new TimeAccountBillLabel(labelId, labelName).toText(value);
        } else if (labelType === 33) {
            // 平台账号信息
            return new PlatformAccountInfoLabel(labelId, labelName).toText(value);
        } else if (labelType === 34) {
            // 未对账账单
            return new UncheckedBillInfoLabel(labelId, labelName).toText(value);
        } else if (labelType == 37) {
        	// 充值详情
            return new RechargeDetail(labelId, labelName).toText(value);
        } else {
            // 不认识的，默认使用文本标签进行展示
            return new TextLabel(labelId, labelName).toText(value);
        }
    };
    return FlowAuditLabels;
});