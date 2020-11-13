layui.use(['layer', 'element', 'laydate', 'flow'], function () {
	
	parent.$('[data-tag="flow"]').addClass('active');
	
    let layer = layui.layer;
    window.layer = layer;
    let laydate = layui.laydate;
    let flow = layui.flow;
    let userRoleId = window.parent.userRoleId;
    // 显示加载
    let loadIndex = layer.load(1, {
        shade: [0.1, '#fff']
    });
    // 加载用户角色流程
    loadFlowList(DEFAULT_PARAM.flowLoadCount);
    $("#date_month").bind("click", function () {
        $("body").append("<div class='data-pick-bg'></div>");
        let height = $(this).outerHeight(true);
        // 屏幕宽度不一定是不变的
        let screenWidth = $(window).width();
        let timeTool = laydate.render({
            elem: '#date_time_picked'
            , type: 'month'
            , show: true
            , closeStop: '#date_month'
            , zIndex: 99999999
            , trigger: 'click'
            , max: new Date().getFullYear() + "-" + new Date().getMonth()
            , ready: function (date) {
                let dataEle = $("#layui-laydate1");
                let calenderWidth = dataEle.outerWidth(true);
                dataEle.css({
                    "left": (screenWidth - calenderWidth - 2) + "px",
                    "top": height + "px",
                    "position": "fixed"
                });
                $("div[class='data-pick-bg']").click(function () {
                    dataEle.remove();
                    $(this).remove();
                })
            }
            , done: function (value, date, endDate) {
                $("div[class='data-pick-bg']").remove();
                loadFlowList(DEFAULT_PARAM.flowLoadCount, value);
            }
        });
    }).bind("touchstart", function () {
        $(this).addClass("flow-active");
    }).bind("touchend", function () {
        $(this).removeClass("flow-active");
    });
    /**
     * 点击搜索的按钮
     */
    $("#search_btn").bind("click", function () {
        loadFlowList(DEFAULT_PARAM.flowLoadCount);
    }).bind("touchstart", function () {
        $(this).addClass("flow-active");
    }).bind("touchend", function () {
        $(this).removeClass("flow-active");
    });

    let floatBtn = new Float("div[data-float-btn='true']", "#add-menu-list");
    floatBtn.do([{
        icon: "layui-icon-engine",
        name: "流程",
        handle: function () {
            window.parent.location.href = "/mobile/toApplyFlowList";
        }
    },{
        icon: "layui-icon-template",
        name: "客户",
        handle: function () {
            window.parent.location.href = "/mobile/toAddCustomer";
        }
    },/* {
        icon: "layui-icon-app",
        name: "产品",
        handle: function () {
            layer.msg("敬请期待")
        }
    }, */{
        icon: "layui-icon-username",
        name: "客户联系人",
        handle: function () {
            window.parent.location.href = "/mobile/toAddCustomerContact";
        }
    },/* {
        icon: "layui-icon-cellphone-fine",
        name: "联系日志",
        handle: function () {
            layer.msg("敬请期待")
        }
    },*/ ]);

    /**
     * 加载流程信息
     */
    function loadFlowList(pageSize, definedTime) {
        // 这一段是解决框架渲染的问题，防止重新加载的时候无效
        let flowContentEle = $("#flow-list-content");
        flowContentEle.empty();
        flowContentEle.append(" <ul class='flow-list' id ='flow-list'></ul>");
        flow.load({
            elem: '#flow-list', //流加载容器
            done: function (page, next) { //执行下一页的回调
                let loadIndex = layer.load(1, {
                    shade: [0.1, '#fff']
                });
                let month = $("#date_time_picked").val();
                let content = $("#search_content").val();
                if (util.isNotNull(definedTime)) {
                    month = definedTime;
                }
                $.ajax({
                    url: "/flowForMobile/readFlowEnt",
                    dataType: "json",
                    method: "POST",
                    data: {
                        "roleId": userRoleId,
                        "month": month,
                        "content": content,
                        "page": page,
                        "pageSize": pageSize
                    },
                    success: function (data) {
                        let flowListDom = renderFlowList(data.data);
                        next(flowListDom.join(''), page < data.totalPages);
                        bindFlowList("#flow-list");
                        layer.close(loadIndex);
                    },
                    error: function (data) {
                        $("#flow-list").html("暂无数据")
                    }
                });
            }
        });
    }

    /**
     * 渲染流程
     * @param dataList
     */
    function renderFlowList(dataList) {
        let flowListElement = [];
        if (dataList != null && dataList.length > 0) {
            for (let index = 0; index < dataList.length; index++) {
                let flowInfo = dataList[index];
                let flowEntId = flowInfo.flowEntId;
                let titleDom = "<li data-flow-id='" + flowEntId + "'><div class='flow-list-title'>";
                // console.log(JSON.stringify(flowInfo));
                // 日期
                let time = flowInfo.wtime;
                // 主体名称
                let entityName = util.isNull(flowInfo.entityName) ? "" : flowInfo.entityName;
                // 产品名称
                let productName = util.isNull(flowInfo.productName) ? "" : flowInfo.productName;
                // 个人流程没有主体和产品
                if (util.isNull(entityName) && util.isNull(productName)) {
                    entityName = flowInfo.flowTitle;
                }

                let title =
                    "<div class='flow-title-name'>" +
                    "  <span class='flow-title-time'>" + time + "</span>" +
                    "  <span class='flow-title-entity-name'>" + entityName + "</span>" +
                    "  <span class='flow-title-product-name'>" + productName + "</span>" +
                    "</div>";
                let flowName = flowInfo.flowName;
                let state = flowInfo.flowStatus;
                let label = "<div class='flow-title-label'><span>" + flowName + "</span>";
                let canopt = flowInfo.canOperat;
                if (canopt) {
                    label += "<span class='flow-state-unaudit'>(待处理)</span>"
                } else {
                    let nodeName = flowInfo.nodeName;
                    state = parseInt(state);
                    switch (state) {
                        case 0:
                            label += ("<span class='flow-state-process'>（<span>进行中</span>，当前节点：" + nodeName + "）</span>");
                            break;
                        case 1:
                            label += "<span class='flow-state-document'>（已归档）</span>";
                            break;
                        case 2:
                            label += ("<span class='flow-state-process'>（<span>进行中</span>，当前节点：" + nodeName + "）</span>");
                            break;
                        default:
                            label += "<span class='flow-state-cancel'>（已取消）</span>";
                    }
                }
                titleDom += (title + label + "</div></li>");
                flowListElement.push(titleDom);
            }
        }
        return flowListElement;
    }

    /**
     * 绑定流程列表事件
     * @param ele
     */
    function bindFlowList(ele) {
        let allLi = $(ele).find("li[data-flow-id]");
        $(allLi).unbind("touchstart").bind('touchstart', function (event) {
            $(allLi).removeClass("flow-touch");
            $(this).addClass("flow-touch");
        }).unbind("touchend").bind("touchend", function (event) {
            $(allLi).removeClass("flow-touch");
        }).unbind("click").bind("click", function (e) {
            let flowId = $(this).attr("data-flow-id");
            if (util.isNull(flowId)) {
                return;
            }
            let openState = $(this).attr("data-open");
            if (util.isNotNull(openState) && OPEN_STATE[openState]) {
                // 打开（点击后应该变为关闭状态）
                $(this).attr("data-open", "close");
                // 删除 流程详情
                $(this).parent().find("div[data-detail-for-id='" + flowId + "']").remove();
            } else {
                let index = layer.load(2);
                // 关闭（默认状态 点击后应该为打开状态）
                $(this).attr("data-open", "open");
                let my = this;
                // 进行数据加载
                $.ajax({
                    url: "/flow/flowDetail",
                    dataType: "json",
                    method: "POST",
                    data: {
                        "id": flowId
                    },
                    success: function (data) {
                        layer.close(index);
                        if (data.code === 200) {
                            // 渲染 流程详情
                            new FlowDetail(my).renderDetail(data.data);
                        } else {
                            layer.msg(data.msg);
                        }
                    },
                    error: function (data) {
                        layer.close(index);
                        console.log("流程详情加载异常", data);
                        layer.msg("流程详情加载错误");
                    }
                });
            }
        });
    }

    // 关闭加载层
    layer.close(loadIndex);
});

