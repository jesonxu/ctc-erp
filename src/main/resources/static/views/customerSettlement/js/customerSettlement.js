var layer;
var element;
var lastScroll = 0;
// 结算 一次加载是否完成
var SETTLEMENT_LOAD_COMPLETED = true;
// 运营 一次加载是否完成
var OPERATE_LOAD_COMPLETED = true;
$(document).ready(function (e) {
    layui.use(['layer', 'element'], function () {
        layer = layui.layer;
        element = layui.element;
        element.on('tab(settlementCard)', function (data) {
            var type = $(this).attr('data-my-type');
            customerFlowType = type;
            // console.log(type); //当前Tab标题所在的原始DOM元素
            if (isSettlement(type)) {
                if (isNotBlank(productId)) {
                    loadCustomerSettlement(customerId, productId, type);
                } else {
                    loadCustomerAllSettlement(customerId, type);
                }
            } else {
            	if (productId) {
            		type = 2;
            	} else if (customerId) {
            		type = 1;
            	} else {
            		type = 0;
            	}
            	load_sale_statistics_time(type);
            }
        });
        // 默认加载对账流程
        loadCustomerAllSettlement(customerId, customerFlowType);
    });
});

/**
 * 判断点击的tab是否是流程，结算栏前3个子栏目是流程，第4个子栏目是统计
 *
 * @param type
 * @returns {boolean}
 */
function isSettlement(type) {
    return isBlank(type) ? true : type !== 'statistics';
}

/**
 * 加载客户指定产品的结算时间标题
 *
 * @param customer_id   客户id
 * @param product_id    产品id
 * @param flow_type     流程类型，2对账，3发票，4销账，默认2
 */
function loadCustomerSettlement(customer_id, product_id, flow_type) {
	if (flow_type == 'statistics') {
		if (product_id) {
			load_sale_statistics_time(2);
		} else if (customer_id) {
			load_sale_statistics_time(1);
		} else {
			load_sale_statistics_time(0);
		}
		return;
	}
    productId = product_id;
    customerId = customer_id;
    flow_type = isBlank(flow_type) ? 2 : flow_type;
    $.post("/customerSettlement/getSettlementTime", {
        "supplierId": customer_id,
        "productId": product_id,
        "flowType": flow_type
    }, function (data, status) {
        if (status == "success") {
            var settlement_ele = $("#content-" + flow_type);
            settlement_ele.html(data);
            // 流程年月标题
            initCustomerSettlementPannel(customer_id, product_id, flow_type);
            // 流程发起按钮区域
            var buttonBody = settlement_ele.find('#buttonBody');
            if (buttonBody != null && buttonBody.length === 1) {
                initCustSetFlowButton(1, flow_type, product_id);
            }
            if (!isNull(customer_module_open)) {
                customer_module_open.render();
            }
        }
    }, "html");
}

/**
 * 初始化流程年月标题折叠框
 * @param supplier_id   客户id
 * @param product_id    产品id
 * @param flow_type     流程类型，2对账，3发票，4销账
 */
function initCustomerSettlementPannel(supplier_id, product_id, flow_type) {
    var settleme_pannel = new myPannel({
        openItem: function (item, itemId, optsType) {
            var itemInfo = itemId.split("||");
            if (optsType === "settlement_year") {
                // 记录点开的年份标识
                sale_settlement_year = itemInfo[1];
            } else if (optsType === "settlement_month") {
                sale_settlement_year = itemInfo[1]; // yyyy
                sale_settlement_month = itemInfo[2]; // MM
                var detail_ele = $("div[settlement_month_detail = '" + (isBlank(product_id) ? 'null' : product_id) + itemInfo[1] + itemInfo[2] + "']");
                $(detail_ele).html('');
                // 点击结算的月份，重置结算流程初始页page，绑定页面滚动事件
                scrollToLoadCustomerFlow(flow_type);
            }
        }
    });
    settleme_pannel.init("#settlement_body_" + flow_type);
    element.render("collapse", "settlement_year_title_" + flow_type);
    openLastCustomerSettlement(flow_type);
}

// 展开最后1个月的流程记录
function openLastCustomerSettlement(flow_type) {
    var years = $("#settlement_body_" + flow_type).find("div[data-my-opts-type='settlement_year']");
    if (!isBlank(years) && years.length > 0) {
        var last_year = years[years.length - 1];
        $(last_year).trigger("click");
    }

    var months = $("#settlement_body_" + flow_type).find("div[data-my-opts-type='settlement_month']");
    if (!isBlank(months) && months.length > 0) {
        var last_month = months[months.length - 1];
        $(last_month).trigger("click");
    }
}

/**
 * 加载客户所有产品的结算时间标题
 *
 * @param customer_id   客户id，为空时查权限下所有客户
 * @param flow_type     流程类型，2对账，3发票，4销账，默认2
 */
function loadCustomerAllSettlement(customer_id, flow_type) {
	if (flow_type == 'statistics') {
		if (customer_id) {
			load_sale_statistics_time(1);
		} else {
			load_sale_statistics_time(0);
		}
		return;
	}

    var loadingIndex = layer.load(2);
    // console.time('加载全部结算时间耗时');
    productId = '';
    customerId = customer_id;
    flow_type = isBlank(flow_type) ? 2 : flow_type;
    $.ajaxSettings.async = true;
    $.post("/customerSettlement/getAllSettlementTime?temp=" + Math.random(),
        {
            "customerId": customer_id,
            "flowType": flow_type
        }, function (data, status) {
            if (status == "success") {
                $("#content-" + flow_type).html(data);
                initCustomerSettlementPannel(customer_id, '', flow_type);
            }
            // console.timeEnd('加载全部结算时间耗时');
            layer.close(loadingIndex);
        }, "html");
    $.ajaxSettings.async = false;
}

// 初始化客户结算流程的发起按钮
function initCustSetFlowButton(entityType, flowType, productid) {
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
            var buttonBody = $('#content-' + flowType).find('#buttonBody');
            buttonBody.find("button[class*='applyFlow']").remove();
            if (data !== "") {
                var html = "";
                $.each(data, function (i, item) {
                    html += "<button type='button' class='layui-btn layui-btn-sm layui-btn-primary applyFlow' style='margin: 0'" +
                        " data-my-id='" + item.flowId + "' title='" + item.flowName + "'>" + item.flowName + "</button>";
                });
                buttonBody.append(html);
            }
            // 绑定按钮点击事件
            if (typeof bindApplyFlowClick == 'function') {
                bindApplyFlowClick(buttonBody);
            }
        }
    });
}

/**
 * 滚动加载下一页流程 标题+详情
 *
 * @param type  类型 operate运营，2对账，3发票，4销账
 */
function scrollToLoadCustomerFlow(type) {
    // 运营、结算展开的月份所在元素
    var operate_month_ele = $("div[operate-name-id=" + sale_operate_month + "]");
    var settlement_month_ele = $("#settlement_body_" + customerFlowType).find("div[settlement_month_detail = '" + (sale_product_id == '' ? 'null' : sale_product_id) + sale_settlement_year + sale_settlement_month + "']");
    // 点击月份标题后加载几页流程，让最后一个流程的框超出页面范围，为了让页面出现滚动条
    if (type === 'operate') {
        $(operate_month_ele).html('');
        operatePage = 1;
        // month yyyy-MM
        loadCustomerOperateByPage(sale_operate_month, sale_product_id, operatePage, 5);
        var flows = $(operate_month_ele).find(".operate-title");
        if (flows.length > 0) {
            var pos = flows[flows.length - 1].getBoundingClientRect();
            while (pos.bottom <= $(window).height() && operatePage < operatePages) {
                loadCustomerOperateByPage(sale_operate_month, sale_product_id, ++operatePage, 5);
                flows = $(operate_month_ele).find(".operate-title");
                pos = flows[flows.length - 1].getBoundingClientRect();
            }
        }
    } else {
        $(settlement_month_ele).html('');
        settlementPage = 1;
        loadCustomerSettlementByPage(sale_product_id, sale_settlement_year, sale_settlement_month, settlementPage, 5, type);
        var flows = $(settlement_month_ele).find(".settlement-title");
        if (flows.length > 0) {
            var pos = flows[flows.length - 1].getBoundingClientRect();
            while (pos.bottom <= $(window).height() && settlementPage < settlementPages) {
                loadCustomerSettlementByPage(sale_product_id, sale_settlement_year, sale_settlement_month, ++settlementPage, 5, type);
                flows = $(settlement_month_ele).find(".settlement-title");
                pos = flows[flows.length - 1].getBoundingClientRect();
            }
        }
    }
    // 监听窗口滚动事件，当最后一个流程的框进入页面范围时，加载下一页
    $("#operate").off('scroll');
    $("#operate").scroll(function () {
        // 记录本次滚动位置
        var thisScroll = $(this).scrollTop();
        if (thisScroll > lastScroll) { // lastScroll记录上次滚动位置
            // 往下滚时加载，往上时不加载
            flows = $(operate_month_ele).find(".operate-title");
            if (flows.length > 0) {
                var pos = flows[flows.length - 1].getBoundingClientRect();
                while (pos.bottom <= $(window).height() && operatePage < operatePages) {
                    var loadingIndex = layer.load(2);
                    loadCustomerOperateByPage(sale_operate_month, sale_product_id, ++operatePage, 5);
                    flows = $(operate_month_ele).find(".operate-title");
                    pos = flows[flows.length - 1].getBoundingClientRect();
                    layer.close(loadingIndex);
                }
            }
        }
        lastScroll = thisScroll;
    });
    $("#settlement").off('scroll');
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
                    loadCustomerSettlementByPage(sale_product_id, sale_settlement_year, sale_settlement_month, ++settlementPage, 5, customerFlowType);
                    flows = $(settlement_month_ele).find(".settlement-title");
                    pos = flows[flows.length - 1].getBoundingClientRect();
                    layer.close(loadingIndex);
                }
            }
        }
        lastScroll = thisScroll;
    });
}

/**
 * 分页加载月份下的结算标题
 *
 * @param product_id    点击的产品id
 * @param year          结算年 yyyy
 * @param month         结算月 MM
 * @param page          第几页
 * @param pageSize      每页大小
 * @param flowType      流程类型，2对账，3发票，4销账
 * @param callback      回调函数
 */
function loadCustomerSettlementByPage(product_id, year, month, page, pageSize, flowType, callback) {
    // console.time("分页加载结算标题耗时");
    var url;
    var data;
    if (isBlank(product_id)) { // 查询所有产品的结算记录
        url = "/customerSettlement/getAllSettlementByPage.action";
        data = {
            "date": year + "-" + month,
            "customerId": customerId,
            "page": page,
            "pageSize": pageSize,
            "flowType": flowType
        }
    } else { // 查询指定产品的结算记录
        url = "/settlement/getSettlementByPage.action";
        data = {
            "date": year + "-" + month,
            "productId": product_id,
            "entityType": 1,
            "page": page,
            "pageSize": pageSize,
            "flowType": flowType
        }
    }
    $.ajaxSettings.async = false;
    $.post(url, data, function (res, status) {
        if (status === "success") {
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
            var detail_ele = $("#settlement_body_" + flowType).find("div[settlement_month_detail = '" + (product_id == '' ? 'null' : product_id) + year + month + "']");
            $(detail_ele).append(html);
            // console.timeEnd("分页加载结算标题耗时");
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
                        url: '/flow/flowDetail.action?id=' + entId + "&temp=" + Math.random(),
                        dataType: 'json',
                        data: {},
                        success: function (data) {
                            // 渲染标签
                            if (typeof renderFlowMsg == "function") {
                                renderFlowMsg(ele, data.data);
                                element.render("collapse", "settlement_month_title_" + flowType);
                            }
                        }
                    });
                });
            }
            // console.timeEnd("初始化结算标题事件耗时");
            // 加载流程详情
            loadSettlementDetailByPage(flowEntIds, flowType, callback);
            layer.close(loadingIndex);
        }
    }, "html");
}

/**
 * 一次加载多个结算流程的详情
 *
 * @param ids 流程实体id数组
 * @param flow_type 流程类型
 * @param callback 方法执行完成回调函数
 */
function loadSettlementDetailByPage(ids, flow_type, callback) {
    if (!(Object.prototype.toString.call(ids) === '[object Array]') || ids.length === 0) {
        // 不是数组
        return;
    }
    // console.time("加载结算流程详情耗时" + ids);
    $.ajax({
        type: "POST",
        async: false,
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
            element.render("collapse", "settlement_month_title_" + flow_type); // 放在后面统一render
            // console.timeEnd("渲染5个结算流程详情耗时");
            // console.timeEnd("加载结算流程详情耗时" + ids);
            if (typeof callback == "function") {
                callback();
            }
        }
    });
}