layui.use(['layer', 'element','laydate','flow'], function () {
    loadUserApplyFlow();
    /**
     * 加载用户的流程信息
     */
    function loadUserApplyFlow() {
        $.ajax({
            type: "POST",
            async: false,
            url: '/flowForMobile/getApplyFlow.action',
            dataType: 'json',
            success: function (data) {
                if (data.code === 200 && util.isNotNull(data.data)) {
                    let flowItemIds =  initApplyFlow(data.data);
                    bindApplyFlowEvent(flowItemIds);
                } else {
                    layer.msg(data.msg);
                }
            }
        });
    }

    /**
     * 渲染流程信息
     * @param flowInfos
     */
    function initApplyFlow(flowInfos) {
        let flowDomArr = [];
        let flowItemIds = [];
        if (util.isNotNull(flowInfos)) {
            for (let flowType in flowInfos) {
                if (flowInfos.hasOwnProperty(flowType)) {
                    let typeFlowArr = flowInfos[flowType];
                    if (typeFlowArr.length > 0) {
                        let typeEle = $("<ul class='apply-flow-group'></ul>");
                        typeEle.append("<lable class='flow-group-title'>" + FLOW_TYPE[flowType] + "流程</lable>");
                        for (let flowIndex = 0; flowIndex < typeFlowArr.length; flowIndex++) {
                            let flowInfo = typeFlowArr[flowIndex];
                            let flowInfoStr = JSON.stringify(flowInfo);
                            let flowItemId = util.uuid();
                            flowItemIds.push(flowItemId);
                            typeEle.append("<li class='apply-flow' data-flow-id='" + flowItemId + "' data-flow='" + flowInfoStr + "'>" + util.formatBlank(flowInfo.flowName) + "</li>");
                        }
                        flowDomArr.push(typeEle.prop("outerHTML"));
                    }
                }
            }
        }
        $("#apply-flow-list").html(flowDomArr.join(""));
        return flowItemIds;
    }

    /**
     * 绑定流程的点击事件
     * @param flowItemIds
     */
    function bindApplyFlowEvent(flowItemIds) {
        if (flowItemIds.length > 0) {
            for (let itemIndex = 0; itemIndex < flowItemIds.length; itemIndex++) {
                let flowItemId = flowItemIds[itemIndex];
                $("li[data-flow-id='"+flowItemId+"']").click(function () {
                    let dataFlow = $(this).attr("data-flow");
                    let flowId = JSON.parse(dataFlow).flowId;
                    window.location.href = "/mobile/toApplyFlowDetail?flowId=" + flowId + "&t=" + new Date().getTime();
                });
            }
        }
    }
});