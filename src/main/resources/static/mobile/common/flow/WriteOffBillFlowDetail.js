/**
 * 销账流程单独处理
 */
(function (window, factory) {
    window.WriteOffBillFlowDetail = factory();
})(window, function () {
    /**
     * 流程详情工具
     */
    let WriteOffBillFlowDetail = function () {

    };

    /**
     * 渲染销账流程详情
     * @param targetEle
     * @param flowInfo 流程信息
     */
    WriteOffBillFlowDetail.prototype.renderRecord = function (targetEle, flowInfo) {
        if (util.isNull(flowInfo)) {
            return;
        }
        let records = flowInfo.record;
        // 流程的标签信息(可以看的)
        let labelList = flowInfo.labelList;
        // 流程状态
        let flowState = flowInfo.flowStatus;
        // 是否可以操作
        let canOperate = flowInfo.canOperat;
        if (util.isNull(canOperate)) {
            canOperate = false;
        }
        // 处理人
        let dealRoleName = flowInfo.dealRoleName;
        // 流程ID
        let flowEntId = $(targetEle).attr("data-flow-id");
        let flowDetailDom =
            "<div class='flow-detail' data-detail-for-id='" + flowEntId + "'>" +
            "  <div class='flow-record-list'>";
        // 渲染记录
        if (util.arrayNotNull(records)) {
            for (let index = 0; index < records.length; index++) {
                let record = records[index];
                let titleDom = this.title(record);
                let detailDom = this.content(record, labelList, flowInfo);
                // 处理意见
                let remark = record.remark;
                if (util.isNotNull(remark)) {
                    detailDom += ("<div class='flow-detail-line'>处理意见：" + util.formatBlank(remark) + "</div>");
                }
                let detailRecord =
                    "<div class='flow-record'>" +
                    "    <div class='flow-record-title'>" + titleDom + "</div>" +
                    "    <div class='flow-record-content'>" + detailDom + "</div>" +
                    "</div>";
                flowDetailDom += detailRecord;
            }
        }
        flowState = parseInt(flowState);
        if ((flowState === 0 || flowState === 2) && !canOperate) {
            let waitToOperate =
                "<div class='flow-record'>" +
                "    <div class='flow-record-title'>" + dealRoleName + "<span class='must-wirte'>等待处理</span></div>" +
                "</div>";
            flowDetailDom += waitToOperate;
        }
        // 先 删除，再添加
        $(targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']").remove();
        $(targetEle).after(flowDetailDom + "</div></div>");
    };

    /**
     * 处理操作记录标题
     * @param record
     * @returns {string}
     */
    WriteOffBillFlowDetail.prototype.title = function (record) {
        let dealTime = record.dealTime;
        if (util.isNull(dealTime)) {
            dealTime = "";
        }
        let userName = record.dealPerson;
        if (util.isNull(userName)) {
            userName = "";
        }
        let role = record.dealRole;
        if (util.isNull(role)) {
            role = "";
        }
        let result = record.auditResult;
        if (util.isNull(result)) {
            result = "";
        }
        return "<span class='flow-record-title-time'>" + dealTime + "</span>" +
            "<span class='flow-record-title-user-name'>" + userName + "</span>" +
            "<span class='flow-record-title-role'>[" + role + "]</span>" +
            "<span class='flow-record-title-result'>" + result + "</span>";
    };


    /**
     * 创建的节点 渲染（暂时没有优化）
     * @param record 流程记录
     * @param labelList 标签
     * @param flowInfo 流程信息
     */
    WriteOffBillFlowDetail.prototype.content = function (record, labelList, flowInfo) {
        let detailDom = [];
        let flowMsg = record.flowMsg;
        if (util.isNull(flowMsg)) {
            return "";
        }
        let recordInfo = JSON.parse(flowMsg);
        // 普通的流程标签
        $.each(labelList, function (labelIndex, label) {
            let labelName = label.name;
            let labelValue = "";
            if (recordInfo.hasOwnProperty(labelName)) {
                labelValue = recordInfo[labelName];
                if (util.isNull(labelValue)) {
                    labelValue = "";
                }
                let labelText = new WriteOffBillFlowAuditLabels().renderText(label, labelValue, flowInfo);
                if (util.isNotNull(labelText)) {
                    detailDom.push("<div class='flow-detail-line'>" + labelText + "</div>");
                }
            }
        });
        return detailDom.join("");
    };

    //添加电子账单标签(虚拟的)
    WriteOffBillFlowDetail.prototype.initElectronicBill = function (files) {
        let pdfFile = {};
        let excelFile = {};
        $(files).each(function (i, file) {
            if (file.fileName.indexOf('pdf') >= 0) {
                pdfFile = file;
            } else if (file.fileName.indexOf('xls') >= 0 || file.fileName.indexOf('xlsx') >= 0) {
                excelFile = file;
            }
        });
        return "<div style='display: inline-block;'>" + pdfFile.fileName + "&nbsp;" +
            "   <button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='fileTool.downLoadFile(" + JSON.stringify(pdfFile) + ")'>预览</button>" +
            "   <button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='fileTool.downLoadFile(" + JSON.stringify(excelFile) + ")'>下载Excel</button>" +
            "   <button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='fileTool.downLoadFile(" + JSON.stringify(pdfFile) + ")'>下载PDF</button>" +
            "</div>";
    };
    return WriteOffBillFlowDetail;
});