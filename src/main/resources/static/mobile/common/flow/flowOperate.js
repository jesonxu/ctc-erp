/**
 * 流程操作工具
 */
(function (window, factory) {
    window.flowOperate = factory();
})(window, function () {
    var flowOperate = function () {

    };

    /**
     * 通过
     * @returns {string}
     * @param flowEntId 流程实体ID
     * @param nodeId 节点ID
     * @param auditOpinion 审核意见
     * @param labelValues 标签值
     * @param baseData 平台基础数据
     */
    flowOperate.prototype.auditPass = function (flowEntId, nodeId, auditOpinion, labelValues, baseData) {
        if (util.isNull(baseData)){
            baseData = {};
        }
        // 审核数据
        let auditData = {
            flowEntId: flowEntId,
            nodeId: nodeId,
            labelValueMap: JSON.stringify(labelValues),
            baseDataMap: JSON.stringify(baseData),
            operateType: 2,
            remark: auditOpinion,
            rejectToNode: "",
            platform: 1
        };
        $.ajax({
            type: "POST",
            async: false,
            url: "/operate/auditProcess.action",
            dataType: 'json',
            data: auditData,
            success: function (resp) {
                if (resp.code === 200 || resp.code === "200") {
                    // 刷新页面
                    window.parent.frames[0].location.reload();
                }
                layer.msg(resp.msg);
            }
        });
    };

    /**
     * 驳回
     * @returns {string}
     * @param flowEntId 流程id
     * @param nodeId 节点ID
     * @param auditOpinion 审核意见
     * @param labelValues 标签数据
     * @param baseData 基础数据
     * @param rejectNode 驳回节点
     */
    flowOperate.prototype.auditReject = function (flowEntId, nodeId, auditOpinion, labelValues, baseData, rejectNode) {
        if (util.isNull(baseData)){
            baseData = {};
        }
        let auditData = {
            flowEntId: flowEntId,
            nodeId: nodeId,
            labelValueMap: JSON.stringify(labelValues),
            baseDataMap: JSON.stringify(baseData),
            operateType: 3,
            remark: auditOpinion,
            rejectToNode: rejectNode,
            platform: 1
        };
        $.ajax({
            type: "POST",
            async: false,
            url: "/operate/auditProcess.action",
            dataType: 'json',
            data: auditData,
            success: function (resp) {
                if (resp.code === 200 || resp.code === "200") {
                    // 刷新页面
                    window.parent.frames[0].location.reload();
                }
                layer.msg(resp.msg);
            }
        });
    };

    /**
     * 撤回
     * @returns {string}
     * @param flowEntId 流程id
     * @param revokeReason 撤回原因
     */
    flowOperate.prototype.auditRevoke = function (flowEntId, revokeReason) {
        let auditData = {
            flowEntId: flowEntId,
            revokeReson: revokeReason,
            platform: 1
        };
        $.ajax({
            type: "POST",
            async: false,
            url: "/operate/revokeProcess.action",
            dataType: 'json',
            data: auditData,
            success: function (resp) {
                if (resp.code === 200 || resp.code === "200") {
                    // 刷新页面
                    window.parent.frames[0].location.reload();
                }
                layer.msg(resp.msg);
            }
        });
    };

    /**
     * 审核取消
     * @param flowEntId
     * @param nodeId
     * @param auditOpinion
     * @param labelValues
     * @param baseData
     * @param rejectNode
     */
    flowOperate.prototype.auditCancel = function (flowEntId, nodeId, auditOpinion, labelValues, baseData) {
        if (util.isNull(baseData)) {
            baseData = {};
        }
        let auditData = {
            flowEntId: flowEntId,
            nodeId: nodeId,
            labelValueMap: JSON.stringify(labelValues),
            baseDataMap: JSON.stringify(baseData),
            operateType: 4,
            remark: auditOpinion,
            platform: 1
        };
        $.ajax({
            type: "POST",
            async: false,
            url: "/operate/auditProcess.action",
            dataType: 'json',
            data: auditData,
            success: function (resp) {
                if (resp.code === 200 || resp.code === "200") {
                    // 刷新页面
                    window.parent.frames[0].location.reload();
                }
                layer.msg(resp.msg);
            }
        });
    }

    return new flowOperate();
});