var layer;
var element;
var lastScroll = 0;
// 上次结算滚动位置
var SETTLEMENT_LAST_SCROLL = 0;
// 上次运营滚动位置
var OPERATE_LAST_SCROLL = 0;
//  供应商运营信息加载状态
var LOADED_SUPPLIER_OPERATE = true;
// 供应商结算信息加载状态
var LOADED_SUPPLIER_SETTLEMENT = true;

$(document).ready(function (e) {
    layui.use(['layer', 'element'], function () {
        layer = layui.layer;
        element = layui.element;
        loadSupplierAllSettlement('');
    });
});

// 加载时间标题
function loadSupplierSettlement(supplier_id, product_id) {
    productId = product_id;
    $.post("/settlement/getSettlementTime", {
        "supplierId": supplier_id,
        "productId": product_id
    }, function (data, status) {
        if (status == "success") {
            var settlement_ele = $("#settlement_depart");
            settlement_ele.html(data);
            // 流程年月标题
            initSupplierSettlementPannel(supplier_id, product_id);
            // 流程发起按钮区域
            var buttonBody = settlement_ele.find('#buttonBody');
            if (buttonBody != null && buttonBody.length === 1) {
                initSettlementFlowButton(0, 1, product_id);
            }
            if (!isNull(resource_module_open)) {
                resource_module_open.render();
            }
        }
    }, "html");
}

// 初始化流程年月标题折叠框
function initSupplierSettlementPannel(supplier_id, product_id) {
    var settleme_pannel = new myPannel({
        openItem: function (item, itemId, optsType) {
            var itemInfo = itemId.split("||");
            if (optsType === "settlement_year"){
                // 记录点开的年份标识
                console_settlement_year = itemInfo[1];
            } else if (optsType === "settlement_month") {
                // 记录点开的月份标识
                console_settlement_year = itemInfo[1];
                console_settlement_month = itemInfo[2];
                var detail_ele = $("div[settlement_month_detail = '" + (isBlank(product_id) ? 'null' : product_id) + itemInfo[1] + itemInfo[2] + "']");
                $(detail_ele).html('');
                // 点击结算的月份，重置结算流程初始页page，绑定页面滚动事件
                scrollToLoadSupplierFlow('settlement');
            }
        }
    });
    settleme_pannel.init("#settlement_body");
    element.render("collapse", "settlement_year_title");
    openLastSupplierSettlement();
}

// 展开最后一个月的流程
function openLastSupplierSettlement() {
    var years = $("#settlement_body").find("div[data-my-opts-type='settlement_year']");
    if (!isBlank(years) && years.length > 0) {
        var last_year = years[years.length - 1];
        $(last_year).trigger("click");
    }

    var months = $("#settlement_body").find("div[data-my-opts-type='settlement_month']");
    if (!isBlank(months) && months.length > 0) {
        var last_month = months[months.length - 1];
        $(last_month).trigger("click");
    }
}

/**
 * 分页加载月份下的结算标题
 *
 * @param product_id    点击的产品id
 * @param year          结算年 yyyy
 * @param month         结算月 MM
 * @param page          第几页
 * @param pageSize      每页大小
 */
function loadSupplierSettlementByPage(product_id, year, month, page, pageSize) {
    var url;
    var data;
    if (product_id == '') { // 查询所有产品的结算记录
        url = "/settlement/getAllSettlementByPage.action";
        data = {
            "date":year+"-"+month,
            "supplierId":supplierId,
            "page":page,
            "pageSize":pageSize
        }
    } else { // 查询指定产品的结算记录
        url = "/settlement/getSettlementByPage";
        data = {
            "date":year+"-"+month,
            "productId":product_id,
            "entityType":0,
            "page":page,
            "pageSize":pageSize
        }
    }
    // $.ajaxSettings.async = false;
    $.post(url, data, function (res, status) {
        if (status == "success") {
            var loadingIndex = layer.load(2);
            res = JSON.parse(res);
            settlementPages = res.msg;
            var flowEntIds = [];
            var html = '<div style="padding-left: 15px;">没有更多了</div>';
            var recordDetail = res.data;
            if (recordDetail !== undefined && recordDetail != null && recordDetail.length > 0) {
                html = '';
                $.each(recordDetail, function (i, item) {
                    flowEntIds.push(item.id);
                    var s = '<div class="settlement-title" style="cursor: pointer;" id="' + item.id + '" entId = "' + item.id + '" productId = "' + item.productId + '"><b><span>';
                    var applyTime = item.wtime;
                    applyTime = applyTime.substring(8);
                    s += applyTime + "日 " + item.flowTitle;
                    /*if (item.nodeName != null && item.nodeName != undefined && item.nodeName != '') {
                        s += ',当前节点:' + item.nodeName
                    }*/
                    s += '</span>';
                    if (item.canOperat) {
                        s += '<span style = "color:red">(待处理)</span>'
                    }
                    if (item.flowStatus === "取消"){
                        s += "<span class='flow-state-cancel'>已取消</span>"
                    }else if (item.flowStatus === "归档"){
                        s += "<span class='flow-state-document'>已归档</span>"
                    }else{
                        s += "<span class='flow-state-process'>进行中</span>"
                    }
                    html += s + "</b></div><hr style='height: 3px; background-color: #1E9FFF'/>"
                });
            }
            var detail_ele = $("div[settlement_month_detail = '" + (product_id == '' ? 'null' : product_id) + year + month + "']");
            $(detail_ele).append(html);
            // debugger
            // console.time("初始化结算标题事件耗时");
            for (var i = 0; i < flowEntIds.length; i++) {
                $("#" + flowEntIds[i]).unbind().bind('click', function () {
                    var entId = $(this).attr('entId');
                    flowType = 1; // 结算的流程
                    var ele = $(this);
                    $.ajax({
                        type: "POST",
                        async: true,
                        url: '/flow/flowDetail.action?id=' + entId +"&temp=" + Math.random(),
                        dataType: 'json',
                        data: {},
                        success: function (data) {
                            // 渲染标签
                            if (typeof renderFlowMsg == "function") {
                                renderFlowMsg(ele, data.data);
                                element.render("collapse", "settlement_month_title");
                            }
                        }
                    });
                });
            }
            // console.timeEnd("初始化结算标题事件耗时");
            // 加载流程详情
            loadSettlementDetailByPage(flowEntIds);
            layer.close(loadingIndex);
        }
    }, "html");
}

/**
 * 一次加载多个结算流程的详情
 *
 * @param ids 流程实体id数组
 */
function loadSettlementDetailByPage(ids) {
    if (!(Object.prototype.toString.call(ids) === '[object Array]') || ids.length === 0) {
        // 不是数组
        return;
    }
    // console.time("加载结算流程详情耗时" + ids);
    $.ajax({
        type: "POST",
        async: true,
        url: '/flow/getFlowDetailByPage.action?temp=' + Math.random(),
        dataType: 'json',
        data: {
            'ids': ids.join(",")
        },
        success: function (data) {
            var details = isBlank(data.data) ? [] : data.data;
            // 渲染标签
            // console.time("渲染5个结算流程详情耗时");
            for (var i = 0; i < ids.length; i++) {
                // console.time("渲染1个结算流程详情耗时");
                renderFlowMsg($("#" + ids[i]), details[ids[i]]);
                // console.timeEnd("渲染1个结算流程详情耗时");
            }
            element.render("collapse", "settlement_month_title"); // 放在后面统一render
            // console.timeEnd("渲染5个结算流程详情耗时");
            // console.timeEnd("加载结算流程详情耗时" + ids);
        }
    });
}

// 加载供应商所有产品的结算时间标题
function loadSupplierAllSettlement(supplierid) {
    var loadingIndex = layer.load(2);
    productId = '';
    supplierId = supplierid;
    $.ajaxSettings.async = true;
    $.post("/settlement/getAllSettlement", {"supplierId": supplierId}, function (data, status) {
        if (status == "success") {
            $("#settlement_depart").html(data);
            initSupplierSettlementPannel(supplierId, productId);
            if (!isNull(resource_module_open)) {
                resource_module_open.render();
            }
        }
        layer.close(loadingIndex);
    }, "html");
    $.ajaxSettings.async = false;
}

/**
 * 初始化流程发起按钮
 * @param entityType    0供应商，1客户
 * @param flowType      0运营，1结算
 * @param productid     点击产品id
 */
function initSettlementFlowButton(entityType, flowType, productid) {
    $.ajax({
        type: "POST",
        async: false,
        url: '/flow/getFlowByType.action',
        dataType: 'json',
        data: {
            entityType: entityType,
            flowType: flowType,
            productId: productid
        },
        success: function (data) {
            // 展示流程发起按钮
            var buttonBody = $('#settlement_depart #buttonBody');
            buttonBody.find("button[class*='applyFlow']").remove();
            if (data !== "") {
                var html = "";
                $.each(data, function (i, item) {
                    html += "<button type='button' class='layui-btn layui-btn-sm layui-btn-primary applyFlow' style='margin: 0'" +
                        " data-my-id='" + item.flowId + "' title='"+item.flowName+"'>" + item.flowName + "</button>";
                });
                buttonBody.append(html);
            }
            if (typeof bindApplyFlowClick == 'function') {
                bindApplyFlowClick(buttonBody);
            }
        }
    });
}

/**
 * 滚动加载下一页流程 标题+详情
 *
 * @param type  类型 operate：运营；settlement：结算
 */
function scrollToLoadSupplierFlow(type) {
    // 运营、结算展开的月份所在元素
    var operate_month_ele = $("div[operate-name-id=" + console_operate_month + "]");
    var settlement_month_ele = $("div[settlement_month_detail = '" + (console_product_id == '' ? 'null' : console_product_id) + console_settlement_year + console_settlement_month + "']");
    // 点击月份标题后加载几页流程，让最后一个流程的框超出页面范围，为了让页面出现滚动条
    if (type === 'operate') {
        $(operate_month_ele).html('');
        operatePage = 1;
        // month yyyy-MM
        loadSupplierOperateByPage(console_operate_month, console_product_id, operatePage, 5);
        var flows = $(operate_month_ele).find(".operate-title");
        if (flows.length > 0) {
            var pos = flows[flows.length - 1].getBoundingClientRect();
            while (pos.bottom <= $(window).height() && operatePage < operatePages) {
                loadSupplierOperateByPage(console_operate_month, console_product_id, ++operatePage, 5);
                flows = $(operate_month_ele).find(".operate-title");
                pos = flows[flows.length - 1].getBoundingClientRect();
            }
        }
    } else {
        $(settlement_month_ele).html('');
        settlementPage = 1;
        loadSupplierSettlementByPage(console_product_id, console_settlement_year, console_settlement_month, settlementPage, 5);
        var flows = $(settlement_month_ele).find(".settlement-title");
        if (flows.length > 0) {
            var pos = flows[flows.length - 1].getBoundingClientRect();
            while (pos.bottom <= $(window).height() && settlementPage < settlementPages) {
                loadSupplierSettlementByPage(console_product_id, console_settlement_year, console_settlement_month, ++settlementPage, 5);
                flows = $(settlement_month_ele).find(".settlement-title");
                pos = flows[flows.length - 1].getBoundingClientRect();
            }
        }
    }
    // 监听窗口滚动事件，当最后一个流程的框进入页面范围时，加载下一页
    $(window).off('scroll');
    $("#operate").scroll(function () {
        // 记录本次滚动位置
        var thisScroll = $(this).scrollTop();
        if (thisScroll > lastScroll) { // lastScroll记录上次滚动位置
            // 往下滚时加载，往上时不加载
            var flows = $(settlement_month_ele).find(".settlement-title");
            /*if (flows.length > 0) {
                var pos = flows[flows.length - 1].getBoundingClientRect();
                while (pos.bottom <= $(window).height() && settlementPage < settlementPages) {
                    var loadingIndex = layer.load(2);
                    loadSupplierSettlementByPage(console_product_id, console_settlement_year, console_settlement_month, ++settlementPage, 5);
                    flows = $(settlement_month_ele).find(".settlement-title");
                    pos = flows[flows.length - 1].getBoundingClientRect();
                    layer.close(loadingIndex);
                }
            }*/
            var flows = $(operate_month_ele).find(".operate-title");
            if (flows.length > 0) {
                var pos = flows[flows.length - 1].getBoundingClientRect();
                while (pos.bottom <= $(window).height() && operatePage < operatePages) {
                    var loadingIndex = layer.load(2);
                    loadSupplierOperateByPage(console_operate_month, console_product_id, ++operatePage, 5);
                    flows = $(operate_month_ele).find(".operate-title");
                    pos = flows[flows.length - 1].getBoundingClientRect();
                    layer.close(loadingIndex);
                }
            }
        }
        lastScroll = thisScroll;
    });
    $("#settlement").scroll(function () {
        // 记录本次滚动位置
        var thisScroll = $(this).scrollTop();
        if (thisScroll > lastScroll) { // lastScroll记录上次滚动位置
            // 往下滚时加载，往上时不加载
            var flows = $(settlement_month_ele).find(".settlement-title");
            if (flows.length > 0) {
                var pos = flows[flows.length - 1].getBoundingClientRect();
                while (pos.bottom <= $(window).height() && settlementPage < settlementPages) {
                    var loadingIndex = layer.load(2);
                    loadSupplierSettlementByPage(console_product_id, console_settlement_year, console_settlement_month, ++settlementPage, 5);
                    flows = $(settlement_month_ele).find(".settlement-title");
                    pos = flows[flows.length - 1].getBoundingClientRect();
                    layer.close(loadingIndex);
                }
            }
            /*flows = $(operate_month_ele).find(".operate-title");
            if (flows.length > 0) {
                var pos = flows[flows.length - 1].getBoundingClientRect();
                while (pos.bottom <= $(window).height() && operatePage < operatePages) {
                    var loadingIndex = layer.load(2);
                    loadSupplierOperateByPage(console_operate_month, console_product_id, ++operatePage, 5);
                    flows = $(operate_month_ele).find(".operate-title");
                    pos = flows[flows.length - 1].getBoundingClientRect();
                    layer.close(loadingIndex);
                }
            }*/
        }
        lastScroll = thisScroll;
    });
}