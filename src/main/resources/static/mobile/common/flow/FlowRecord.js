/**
 * 用于流程记录的渲染 和 数据获取
 */
(function (window, factory) {
    window.FlowRecord = factory();
})(window, function () {
    /**
     * 流程详情工具
     */
    let FlowRecord = function () {

    };

    /**
     * 渲染流程处理记录详情
     * {
     *    "dealPerson": "平台管理员",
     *    "dealRole": "资源总监",
     *    "dealTime": "2020-05-12 15:13",
     *    "auditResult": "创建",
     *    "remark": "",
     *    "flowMsg": "{\"联系手机\":\"1\",\"供应商类别ID\":\"2c9282f16c94ddf8016c94e843100511\",\"法律风险\":\"无\",\"产品范围\":\"\",\"银行信息\":\"[{\\\"bankAccount\\\":\\\"1\\\",\\\"accountBank\\\":\\\"2\\\",\\\"accountName\\\":\\\"1\\\"}]\",\"公司名称\":\"101\",\"纳税证明或完整的审计报告或上市公司财报等\":\"[]\",\"法人\":\"\",\"注册资本(万元)\":\"122\",\"业务联系人\":\"1\",\"公司地址\":\"1\",\"优势产品\":\"\",\"公司电话\":\"1\",\"结算方式\":\"账期\",\"公司网页\":\"\",\"近两年任一年度主营收入\":\"\",\"公司资质\":\"增值税一般纳税人资质\",\"是否提供有效营收证明\":\"\",\"正常交货周期\":\"1\",\"销售方式\":\"直销\",\"公司性质\":\"民营\",\"合作形式\":\"一件代发\",\"法人征信\":\"\",\"相关技术或资质认证\":\"是\",\"合作客户案例合同\":\"\",\"行业水平及外部评价\":\"\",\"公司创立日期\":\"2020-04-24\",\"电子邮件\":\"\",\"统一社会信用代码\":\"111\",\"配送物流\":\"\",\"公司管理相关认证\":\"[]\",\"认证文件\":\"[]\"}"
     * }
     * @param targetEle
     * @param flowInfo 流程信息
     */
    FlowRecord.prototype.renderRecord = function (targetEle, flowInfo) {
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
                // console.log("流程处理记录信息："+JSON.stringify(record));
                let titleDom = this.title(record);
                let detailDom = this.content(labelList, record, flowInfo);
                // 处理意见
                let remark = record.remark;
                if (util.isNotNull(remark)) {
                    detailDom += ("<div class='flow-detail-line'>" + "处理意见：" + util.formatBlank(remark) + "</div>");
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
    FlowRecord.prototype.title = function (record) {
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
     * 处理流程记录内容
     * @param record 处理记录
     * @param labelList 标签
     * @param flowInfo 流程信息
     * @returns {string}
     */
    FlowRecord.prototype.content = function (labelList, record, flowInfo) {
        if (util.arrayNull(labelList)) {
            return "";
        }
        let content = [];
        // 如果是创建节点 需要做一些平台数据的处理
        let auditResult = record.auditResult;
        // 流程处理消息
        let flowMsg = record.flowMsg;
        if (auditResult === '创建') {
            let recordUnlabeledData = new RecordUnlabeledData(flowMsg);
            // 平台基础数据（只会在特定流程展示）
            let baseDataText = recordUnlabeledData.baseDateToText();
            if (util.isNotNull(baseDataText)) {
                content.push(baseDataText);
            }
            // 原来价格展示（只会在特定流程展示）
            let beforePriceText = recordUnlabeledData.beforePriceToText(flowInfo.flowClass);
            if (util.isNotNull(beforePriceText)) {
                content.push(beforePriceText);
            }
        }
        if (util.isNull(flowMsg)) {
            return "";
        }
        let recordInfo = JSON.parse(flowMsg);
        // 标签展示内容
        for (let labelIndex = 0; labelIndex < labelList.length; labelIndex++) {
            let label = labelList[labelIndex];
            let labelName = label.name;
            let labelValue = "";
            if (recordInfo.hasOwnProperty(labelName)) {
                // 只能渲染 流程处理内容里面有的数据（有不有值不重要，关键要有这个字段）
                labelValue = recordInfo[labelName];
                //debugger;
                if (util.isNull(labelValue)) {
                    labelValue = "";
                }
                let labelText = new FlowAuditLabels().renderText(label, labelValue, flowInfo);
                if (util.isNotNull(labelText)) {
                    labelText = "<div class='flow-detail-line'>" + labelText + "</div>";
                }
                // 渲染 标签 值
                content.push(labelText);
            }
        }
        return content.join("");
    };
    return FlowRecord;
});