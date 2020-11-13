(function (window, factory) {
    window.FlowDetail = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     */
    let FlowDetail = function (target) {
        this.targetEle = target;
    };

    /**
     * 处理暂时不支持的流程
     * @param flowDetailEle
     * @param flowInfo
     * @returns {boolean}
     */
    FlowDetail.prototype.nonsupport = function (flowDetailEle, flowInfo) {
        let flowEntId = flowInfo.flowEntId;
        let nodeIndex = flowInfo.nodeIndex;
        if (util.isNotNull(nodeIndex) && (nodeIndex === 0 || nodeIndex === "0")) {
            $(flowDetailEle).append("<div class='nonsupport-flow'>移动端暂时不支持操作处于起点的流程</div>");
            return true;
        }
        return false;
    };

    /**
     * 渲染
     */
    FlowDetail.prototype.renderDetail = function (flowInfo) {
        // console.log("本次渲染的流程详情数据：" + JSON.stringify(flowInfo));
        if (util.isNull(flowInfo)) {
            return "";
        }
        // 流程实体ID
        let flowEntId = flowInfo.flowEntId;
        // 是否可以操作
        let canOperate = flowInfo.canOperat;
        if (util.isNull(canOperate)) {
            canOperate = false;
        }
        // 是否可以撤回
        let canRevoke = flowInfo.canRevoke;
        if (util.isNull(canRevoke)) {
            canRevoke = false;
        }
        let flowClass = flowInfo.flowClass;
        if (flowClass === "[BillWriteOffFlow]") {
            // 需要这么写，兼容原来设计的缺陷
            new WriteOffBillFlowDetail().renderRecord(this.targetEle, flowInfo);
        } else {
            // 渲染处理记录
            new FlowRecord().renderRecord(this.targetEle, flowInfo);
        }
        // 流程id
        let flowId = flowInfo.flowId;
        // 节点序号
        let nodeIndex = flowInfo.nodeIndex;
        // 查找 是否有
        let flowDetailEle = $(this.targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']");
        // 只有能够操作才渲染 标签
        if (canOperate) {
            // 流程标签
            let flowAuditLabel = new FlowAuditLabels();
            if (flowClass === "[BillWriteOffFlow]") {
                // 渲染销账流程标签（数据兼容，实属无奈）
                flowAuditLabel = new WriteOffBillFlowAuditLabels();
            }
            // 渲染标签
            flowAuditLabel.renderLabels(this.targetEle, flowInfo);
            // 用于获取 渲染的标签值
            this.flowLabelObj = flowAuditLabel;
            // 操作 审核
            $(flowDetailEle).append(
                "<div class='layui-form flow-audit'>" +
                "   <div class='layui-form-item audit-value'> " +
                "       <label class='opinion-label' required='true'>处理意见：</label>" +
                "       <div class='opinion-content'>" +
                "            <textarea name='audit-remark' class='layui-textarea audit-remark'></textarea>" +
                "       </div>" +
                "   </div>" +
                "   <div class='layui-form-item audit-opts'>" + this.initAuditButton(flowId, flowEntId, nodeIndex) +
                "   </div>" +
                "</div>");
        } else if (canRevoke) { // 可撤销
            $(flowDetailEle).append(
                "<div class='layui-form flow-revoke'>" +
                "   <div class='layui-form-item'>" +
                "       <label class='flow-label-name' required='true'>撤销原因：</label>" +
                "       <div class='opinion-content'>" +
                "            <textarea name='revoke-opinion' placeholder='请输入撤销原因' class='layui-textarea revoke-reason'></textarea>" +
                "       </div>" +
                "   </div>" +
                "   <div class='layui-form-item audit-opts'>" +
                "       <button type='button' class='layui-btn layui-btn-sm audit-revoke'>" +
                "            <i class='layui-icon layui-icon-ok'></i>撤销" +
                "       </button>" +
                "   </div>" +
                "</div>");
        }
        // 绑定操作
        this.bindEvent(flowEntId, flowInfo);
    };

    /**
     *  当前 绑定事件
     * @param flowEntId 流程ID
     * @param flowInfo 流程信息
     */
    FlowDetail.prototype.bindEvent = function (flowEntId, flowInfo) {
        let flowDetailEle = $(this.targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']");
        // 节点ID
        let nodeId = flowInfo.nodeId;
        // 审核意见
        let auditOpinion = "";
        // 标签值
        let labelValues = flowInfo.labelValueMap;
        // 平台基础数据
        let baseData = flowInfo.baseData;
        let flowDetailObj = this;
        // 审核通过
        let auditPassEle = $(flowDetailEle).find("div[class*='flow-audit']").find("button[class*='audit-pass']");
        if (auditPassEle.length > 0) {
            $(auditPassEle[0]).unbind().bind("touchstart", function (event) {
                if (flowDetailObj.verifyFlowData()) {
                    // 意见数据
                    let auditRemarkEle = $(flowDetailObj.targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']").find("textarea[name='audit-remark']");
                    auditOpinion = $(auditRemarkEle).val();
                    if (util.isNull(auditOpinion)) {
                        layer.tips("审核意见不能为空", auditRemarkEle, {tips: 1});
                        return false;
                    }
                    // console.log("审核通过事件");
                    //驳回确认弹框
                    layer.confirm("确认通过本流程吗？", {
                        title: "流程通过确认",
                        icon: 3,
                        btn: ["确认", "取消" ],
                        skin: "reject-confirm"
                    }, function () {
                        // flowEntId, nodeId, auditOpinion, labelValues, baseData
                        labelValues = flowDetailObj.getFlowData(labelValues);
                        flowOperate.auditPass(flowEntId, nodeId, auditOpinion, labelValues, baseData);
                    }, function () {
                        layer.msg("取消");
                    });
                }
            });
        }
        // 驳回
        let auditRejectEle = $(flowDetailEle).find("div[class*='flow-audit']").find("button[class*='audit-reject']");
        if (auditRejectEle.length > 0) {
            //绑定拒绝的渲染
            rejectSelect.rejectTo(auditRejectEle[0], function (nodeName, roleName, eleId, rejectToIndex) {
                // 意见数据
                let detail = $(flowDetailObj.targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']");
                let auditRemarkEle = $(detail).find("textarea[name='audit-remark']");
                auditOpinion = $(auditRemarkEle).val();
                if (util.isNull(auditOpinion)) {
                    layer.tips("驳回意见不能为空", auditRemarkEle, {tips: 1});
                    return false;
                }
                layer.confirm("确定驳回给节点：<span style='color: red'>" + nodeName + "</span>?<br>该节点的角色是：<span style='color: red'>" + roleName + "</span>", {
                    title: "驳回确认",
                    icon: 3,
                    btn: ["确认", "取消"],
                    skin: "reject-confirm"
                }, function () {
                    labelValues = flowDetailObj.getFlowData(labelValues);
                    // flowEntId, nodeId, auditOpinion, labelValues, baseData, rejectNode
                    flowOperate.auditReject(flowEntId, nodeId, auditOpinion, labelValues, baseData, rejectToIndex);
                }, function () {
                    layer.msg("取消");
                });
            });
        }

        // 撤回
        let auditRevokeEle = $(flowDetailEle).find("div[class*='flow-revoke']").find("button[class*='audit-revoke']");
        if (auditRevokeEle.length > 0) {
            $(auditRevokeEle[0]).unbind().bind("touchstart", function (event) {
                let auditRemarkEle = $(flowDetailObj.targetEle).parent()
                    .find("div[data-detail-for-id='" + flowEntId + "']").find("textarea[name='revoke-opinion']");
                auditOpinion = $(auditRemarkEle).val();
                if (util.isNull(auditOpinion)) {
                    layer.tips("撤回意见不能为空", auditRemarkEle, {tips: 1});
                    return false;
                }
                layer.confirm("确认“撤销本流程”吗？", {
                    title: "撤销确认",
                    icon: 3,
                    btn: ["确认", "取消"],
                    skin: "reject-confirm"
                }, function () {
                    flowOperate.auditRevoke(flowEntId, auditOpinion);
                }, function () {
                    layer.msg("取消");
                })
            });
        }

        // 取消
        let auditCancelEle = $(flowDetailEle).find("div[class*='audit-opts']").find("button[class*='audit-cancel']");
        if (auditCancelEle.length > 0) {
            $(auditCancelEle[0]).unbind().bind("touchstart", function (event) {
                // 意见数据
                let auditRemarkEle = $(flowDetailObj.targetEle).parent().find("div[data-detail-for-id='" + flowEntId + "']").find("textarea[name='audit-remark']");
                auditOpinion = $(auditRemarkEle).val();
                if (util.isNull(auditOpinion)) {
                    layer.tips("审核意见不能为空", auditRemarkEle, {tips: 1});
                    return false;
                }
                //取消确认弹框
                layer.confirm("确认放弃本流程吗？", {
                    title: "流程放弃确认",
                    icon: 3,
                    btn: ["确认放弃", "取消" ],
                    skin: "reject-confirm"
                }, function () {
                    // flowEntId, nodeId, auditOpinion, labelValues, baseData
                    labelValues = flowDetailObj.getFlowData(labelValues);
                    flowOperate.auditCancel(flowEntId, nodeId, auditOpinion, labelValues, baseData);
                }, function () {
                    layer.msg("取消");
                });
            });
        }
    };

    /**
     * 获取流程详情数据（极不推荐 将所有数据都交到前台）
     * @returns {{}}
     */
    FlowDetail.prototype.getFlowData = function (labelValueMap) {
        let flowLabelUtil = this.flowLabelObj;
        if (util.isNotNull(flowLabelUtil)) {
            let labelValueInfo = flowLabelUtil.getLabelValues();
            if (util.isNotNull(labelValueInfo)) {
                for (let labelName in labelValueInfo) {
                    labelValueMap[labelName] = labelValueInfo[labelName];
                }
            }
        }
        return labelValueMap;
    };

    /**
     * 校验 流程数据
     * @returns {boolean}
     */
    FlowDetail.prototype.verifyFlowData = function () {
        let flowLabelUtil = this.flowLabelObj;
        if (util.isNotNull(flowLabelUtil)) {
            return flowLabelUtil.checkLabels();
        }
        return true;
    };

    FlowDetail.prototype.initAuditButton = function(flowId, flowEntId, nodeIndex) {
        let btns = "";
        btns +=
        "<button class='layui-btn layui-btn-xs audit-pass'>" +
        "    <i class='layui-icon layui-icon-ok-circle'></i>通过" +
        "</button>";
        if (nodeIndex == 0) {
            btns +=
                "<button class='layui-btn layui-btn-xs layui-btn-danger audit-cancel'>" +
                "   <i class='layui-icon layui-icon-close'></i>放弃申请" +
                "</button>";
        } else {
            btns +=
            "<button class='layui-btn layui-btn-xs layui-btn-danger audit-reject'>" +
            "   <i class='layui-icon layui-icon-triangle-r'></i>驳回" +
            "</button>" + this.initRejectOptions(flowId, flowEntId, nodeIndex);
        }
        return btns;
    }

    /**
     * 初始化拒绝选项
     * @param flowId
     * @param flowEntId
     * @param nodeIndex
     * @returns {*}
     */
    FlowDetail.prototype.initRejectOptions = function (flowId, flowEntId, nodeIndex) {
        let rejectDom = [];
        rejectDom.push("<dl class='audit-reject-role-list' data-reject-flow-ent-id='" + flowEntId + "'>");
        $.ajax({
            type: "POST",
            async: false,
            url: "/flow/getFlowNodeBefore.action",
            dataType: 'json',
            data: {
                flowId: flowId,
                nodeIndex: nodeIndex
            },
            success: function (res) {
                if (res.code === 200 || res.code === '200') {
                    $.each(res.data, function (index, item) {
                        rejectDom.push("<dd data-node-name='" + item.nodeName + "'" +
                            " data-role-name='" + item.roleName + "'" +
                            " data-ele-id ='" + flowEntId + "' " +
                            " data-index='" + index + "' >" +
                            "<span>" + item.nodeName + "[" + item.roleName + "]</span>" +
                            "</dd>");
                    });
                }
            }
        });
        rejectDom.push("</dl>");
        return rejectDom.join("");
    };
    /**
     * 获取流程 可以编辑的 已经 渲染的标签信息
     */
    FlowDetail.prototype.getEditLabel = function () {
        return this.editLabels;
    };

    return FlowDetail;
});