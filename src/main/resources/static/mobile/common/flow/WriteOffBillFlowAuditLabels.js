/**
 * 销账流程的标签渲染
 **/
(function (window, factory) {
    window.WriteOffBillFlowAuditLabels = factory();
})(window, function () {
    /**
     * 销账流程标签渲染 工具对象
     */
    let WriteOffBillFlowAuditLabels = function () {
        // 初始化的标签(存放标签数据)
        this.labelList = [];
    };

    /**
     * 流程标签渲染成可编辑的内容
     * @param targetEle 渲染的地方
     * @param flowInfo 流程信息
     * @returns {boolean}
     */
    WriteOffBillFlowAuditLabels.prototype.renderLabels = function (targetEle, flowInfo) {
        if (util.isNull(targetEle)) {
            throw new Error("渲染标签，指定的位置位置");
        }
        // 流程的标签信息(可以看的)
        let labelList = flowInfo.labelList;
        // 可以编辑的标签信息
        let editLabelIds = flowInfo.editLabelIds;
        // 必须标签
        let mustLabelIds = util.formatBlank(flowInfo.mustLabelIds);
        // 已经填写的标签值
        let labelValues = flowInfo.labelValueMap;
        if (util.isNull(editLabelIds)) {
            return false;
        }
        if (util.arrayNull(labelList)) {
            return false;
        }
        // 增加 标签展示区域
        let flowEntId = $(targetEle).attr("data-flow-id");
        let flowDetailEle = $(targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']");
        let editLabelDom = "<div class='layui-form flow-edit-labels' lay-filter='" + flowEntId + "'></div>";
        $(flowDetailEle).append(editLabelDom);
        let entityId = flowInfo.supplierId;
        // 产品ID
        let productId = flowInfo.productId;
        // 渲染的编辑标签地方
        let labelRenderEle = $(flowDetailEle).find("div[class*='flow-edit-labels']");
        // 可编辑标签ID
        let editLabelIdArr = editLabelIds.split(",");
        for (let labelIndex = 0; labelIndex < labelList.length; labelIndex++) {
            let label = labelList[labelIndex];
            let labelId = label.id;
            if (editLabelIdArr.indexOf(labelId) >= 0) {
                // 标签名
                let labelName = label.name;
                // 标签默认值
                let defaultValue = label.defaultValue;
                // 标签值
                let value = this.getValueFromMapByName(labelName, labelValues);
                // 是否必须
                let required = mustLabelIds.indexOf(labelId) >= 0;
                let labelRender = null;
                // 标签位置
                let labelIndex = parseInt(label.position);
                if (labelIndex === 0) {
                    // 自动销账(是|否)
                    labelRender = new BooleanLabel(labelId, labelName);
                    labelRender.render(labelRenderEle, defaultValue, value, required);
                } else if (labelIndex === 1) {
                    //账单信息
                    labelRender = new UnWriteOffBillLabel(labelId, labelName);
                    labelRender.render(labelRenderEle, value, required,productId);
                } else if (labelIndex === 2) {
                    // 收款信息,提交校验的时候,关联账单信息
                    labelRender = new UnWriteOffReceiptLabel(labelId, labelName);
                    labelRender.render(labelRenderEle, value, required, entityId, productId, labelList);
                } else {
                    // 此处调用的时候就已经渲染了 labelRenderEle, label, flowInfo,labelList
                    labelRender = new FlowAuditLabels().renderLabel(labelRenderEle, label, flowInfo, labelList);
                }
                if (util.isNotNull(labelRender)) {
                    this.labelList.push(labelRender);
                }
            }
        }
        // 渲染表单
        layui.use(['layer', 'element', 'form', 'laydate', 'flow'], function () {
            let form = layui.form;
            form.render(); //更新全部
        });
    };


    /**
     * 标签 渲染成 展示文本
     * @param label
     * @param value
     * @param flowInfo 流程信息（整个流程的信息）
     */
    WriteOffBillFlowAuditLabels.prototype.renderText = function (label, value, flowInfo) {
        let labelId = label.id;
        let labelName = label.name;
        // 标签在流程中的序号
        let labelIndex = parseInt(label.position);
        //针对销账流程 是根据标签的序号进行渲染 不针对其他进行渲染（在序号之外的标签按照标签类型进行渲染）
        if (labelIndex === 0) {
            // 自动销账(是|否)
            return new BooleanLabel(labelId, labelName).toText(value);
        } else if (labelIndex === 1) {
            //账单信息
            return new UnWriteOffBillLabel(labelId, labelName).toText(value);
        } else if (labelIndex === 2) {
            // 收款信息
            return new UnWriteOffReceiptLabel(labelId, labelName).toText(value, flowInfo.productId);
        } else {
            // 其余的就按照 类型处理
            return new FlowAuditLabels().renderText(label, value, flowInfo);
        }
    };

    /**
     * 获取标签值
     * @param labelName
     * @param labelValueMap
     */
    WriteOffBillFlowAuditLabels.prototype.getValueFromMapByName = function (labelName, labelValueMap) {
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
     * 获取流程所有可以编辑的标签值
     */
    WriteOffBillFlowAuditLabels.prototype.getLabelValues = function () {
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
     * 校验所有可以编辑的标签值
     * 这里只管结果 不管提示 （具体的提示 在标签里面写）
     */
    WriteOffBillFlowAuditLabels.prototype.checkLabels = function () {
        let labelList = this.labelList;
        if (labelList.length > 0) {
            for (let index = 0; index < labelList.length; index++) {
                let label = labelList[index];
                let verify = label.verify();
                if (!verify) {
                    return false;
                }
            }
        }
        return true;
    };
    return WriteOffBillFlowAuditLabels;
});