layui.use(['layer', 'element', 'laydate'], function () {
    let layer = layui.layer;
    let flowApplyLabels = null;
    /** 关联类型 */
    const ASSOCIATE_TYPE = {
        0: "客户",
        1: "供应商",
        2: "个人"
    };
    const APPLY_URL = {
        '0': '/customerOperate/applyFlow',
        '1': '/operate/applyProcess',
        '2': '/operate/applyProcess'
    }
    // 流程ID
    let flowId = util.getUrlParam("flowId");
    // 流程关联类型
    let associateType = null;

    // 加载流程详情
    loadFlowInfo();

    /**
     * 加载流程详情
     */
    function loadFlowInfo() {
        let loadIndex = layer.load(1, {
            shade: [0.1, '#fff']
        });
        $.ajax({
            type: "POST",
            url: '/flowForMobile/getFlowDetail/' + flowId,
            dataType: 'json',
            success: function (data) {
                if (data.code === 200 && util.isNotNull(data.data)) {
                    let flowInfo = data.data;
                    let flowName = flowInfo.flowName;
                    let bindType = flowInfo.bindType;
                    let flowClass = flowInfo.flowClass;
                    $("#apply-flow-name").html("发起" + util.formatBlank(flowName));
                    associateType = flowInfo.associateType;
                    // debugger;
                    // 客户申请流程 就只能查询公共池的客户
                    let onlyPublic = false;
                    if (flowClass === "[ApplyCustomer]") {
                        onlyPublic = true;
                    }
                    // 初始化 客户
                    initEntityChoose(associateType, onlyPublic);
                    // 初始化 产品
                    initProductChoose(associateType, bindType);
                    // 渲染编辑的标签
                    renderLabels(flowInfo);
                    // 绑定 申请按钮
                    bindSubmitBtnEvent(bindType);
                } else {
                    layer.msg(data.msg);
                }
                layer.close(loadIndex);
            }
        });
    }


    /**
     * 初始化 客户/供应商 关联信息（选择框）
     * @param associateType
     * @param onlyPublic
     */
    function initEntityChoose(associateType, onlyPublic) {
        if (associateType === 0 || associateType === 1) {
            // 添加客户选择
            $("#entity-choose-item").show();
            // 设置对应名称
            $("#entity-label-name").html("请选择" + ASSOCIATE_TYPE[associateType] + "：");
            $("#entity-btn-name").html("点击选择" + ASSOCIATE_TYPE[associateType]);
            $("#entity-choose").click(function () {
                let entityId = $("#entity-id").val();
                let index = layer.open({
                    type: 2,
                    area: ['100%', '100%'],
                    title: "选择" + ASSOCIATE_TYPE[associateType],
                    content: "/mobile/toEntityChoose?associateType=" + associateType + "&entityId=" + entityId + "&onlyPublic=" + onlyPublic
                });
                layer.full(index);
            });
        }
    }

    /**
     * 初始化产品选择
     * @param associateType 关联类型
     * @param bindType 绑定类型
     */
    function initProductChoose(associateType, bindType) {
        if (bindType === 1) {
            // 不展示(直接绑定的是客户)
            return;
        }
        if (associateType === 0 || associateType === 1) {
            // 添加客户选择
            $("#product-choose-item").show();
            // 设置对应名称
            $("#product-label-name").html("请选择" + ASSOCIATE_TYPE[associateType] + "产品：");
            $("#product-btn-name").html("点击选择" + ASSOCIATE_TYPE[associateType] + "产品");
            $("#product-choose").click(function () {
                let entityId = $("#entity-id").val();
                let productId = $("#product-id").val();
                if (util.isNull(entityId)) {
                    layer.msg("请先选择" + ASSOCIATE_TYPE[associateType]);
                    return;
                }
                let index = layer.open({
                    type: 2,
                    area: ['100%', '100%'],
                    title: "选择" + ASSOCIATE_TYPE[associateType] + "产品",
                    content: "/mobile/toProductChoose?associateType=" + associateType + "&entityId=" + entityId + "&productId=" + productId
                });
                layer.full(index);
            });
        }
    }

    /**
     * 渲染 标签
     * @param flowInfo 流程信息
     * associateType: 0
     * bindType: 0
     * editLabels: ["73d0607d5bb64d8ca6b3e297b6571012",…]
     * flowClass: "[InvoiceFlow]"
     * flowId: "402812816e68fba3016e690189480003"
     * flowName: "无账单开票流程"
     * labels: [{
     *   defaultValue: "电信服务*增值电信服务"
     *   flowId: "402812816e68fba3016e690189480003"
     *   id: "d1f8d29e23d443c0ae2d8e06660c449c"
     *   name: "开票服务名称"
     *   position: 0
     *   type: 0
     * },…]
     * mustLabels: ["1cc6ce7a06a94970bf1deda6ec6b06c4"]
     */
    function renderLabels(flowInfo) {
        // debugger
        // 这里对于特殊流程 做兼容 （销账流程）
        let flowClass = flowInfo.flowClass;
        if (flowClass === "[BillWriteOffFlow]") {
            flowApplyLabels = new WriteOffBillFlowApplyLabels();
            flowApplyLabels.renderLabels($("#flow-labels"), flowInfo);
        } else {
            flowApplyLabels = new FlowApplyLabels();
            flowApplyLabels.renderLabels($("#flow-labels"), flowInfo);
        }
    }

    /**
     * 提交绑定
     */
    function bindSubmitBtnEvent(bindType) {
        $("#submit-flow").click(function () {
            let entityId = getEntityId();
            // 绑定级别，0产品，1主体，2无
            if ((bindType !== 2 && bindType !== '2') && util.isNull(entityId)) {
                // 绑定到主体/产品级别
                if (associateType === 0 || associateType === '0') {
                    layer.msg("请先选择客户");
                    return;
                } else if (associateType === 1 || associateType === '1') {
                    layer.msg("请先选择供应商");
                    return;
                }
            }
            let productId = getProductId();
            if ((bindType === 0 || bindType === '0') && util.isNull(productId)) {
                // 绑定到产品级别
                layer.msg("请先选择产品");
                return;
            }
            if (flowApplyLabels.checkLabels()) {
                let labelValues = flowApplyLabels.getLabelValues();
                let data = {
                    flowId: flowId,
                    supplierId: entityId,
                    productId: productId,
                    flowMsg: JSON.stringify(labelValues),
                    platform: 1
                };
                // 提交 暂时还不提交
                layer.confirm("确认发起流程吗？", {
                    title: "确认操作",
                    icon: 3,
                    btn: ["确认", "取消"],
                    skin: "reject-confirm"
                }, function (index, ele) {
                    $.ajax({
                        type: "POST",
                        async: true,
                        url: APPLY_URL[associateType],
                        dataType: 'json',
                        contentType: "application/json;charset=utf-8",
                        data: JSON.stringify(data),
                        beforeSend: function () {
                            return layer.msg("申请中。。。");
                        },
                        success: function (resp) {
                            layer.msg(resp.msg);
                            if (resp.code === 200) {
                                // 刷新
                                layer.confirm('申请成功，跳转页面', {
                                    btn: ['返回主页', '继续发起']
                                }, function () {
                                    window.location.href = "/mobile/toIndex";
                                }, function () {
                                    window.location.href = "/mobile/toApplyFlowList";
                                })
                            }
                        }
                    });
                    layer.close(index);
                }, function () {
                    layer.msg("取消");
                });
            }
        });
    }
});

/**
 * 清空产品的值(子界面调用方法)
 */
function clearSelectedProductInfo() {
    $("#product-btn-name").html("点击选择客户产品");
    $("#product-id").val("");
}

/**
 * 需要全局实现
 * 获取产品ID（供标签里面进行调用 在申请）
 */
function getProductId() {
    return $("#product-id").val();
}

/**
 * 需要全局实现
 * 获取实体ID（供标签里面进行调用）
 */
function getEntityId() {
    return $("#entity-id").val();
}