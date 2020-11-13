layui.use(['layer', 'element', 'form', 'flow'], function () {
    let layer = layui.layer;
    let flow = layui.flow;
    let form = layui.form;
    let windowIndex = parent.layer.getFrameIndex(window.name);
    let checkedEntityId = util.getUrlParam("entityId");
    // 是否只展示公共池客户
    let onlyPublic = util.isTrue(util.getUrlParam("onlyPublic")) ? 1 : 0;
    // 是否不展示公共客户（冲突的时候 不展示）
    let noPublic = util.isTrue(util.getUrlParam("noPublic")) ? 1 : 0;
    // 绑定搜索按钮
    bindSearch();
    // 默认进来加载用户数据
    loadEntityInfo();

    /**
     * 加载主体类型
     */
    function loadEntityInfo() {
        let associateType = util.getUrlParam("associateType");
        if (util.isNotNull(associateType)) {
            associateType = parseInt(associateType);
        }
        let loadUrl = "";
        if (associateType === 0) {
            // 客户
            loadUrl = "/customer/queryUserCustomerInfo";
        } else if (associateType === 1) {
            // 供应商
            loadUrl = "/supplier/queryUserSupplierInfo";
        } else {
            throw new Error("不需要加载主体的类型，请不要跳转到此页面");
        }
        // debugger;
        initEntityInfo(loadUrl)
    }

    /**
     * 加载当前用户可以看见的所有的供应商
     */
    function initEntityInfo(url) {
        $("#entity-list").html("");
        flow.load({
            elem: '#entity-list', //流加载容器
            done: function (page, next) { //执行下一页的回调
                let loadIndex = layer.load(1, {
                    shade: [0.1, '#fff']
                });
                let companyName = $("#companyName").val();
                $.ajax({
                    url: url + "?temp=" + Math.random(),
                    dataType: "json",
                    method: "POST",
                    data: {
                        "companyName": companyName,
                        "onlyPublic": onlyPublic,
                        "noPublic":noPublic,
                        "currentPage": page,
                        "pageSize": DEFAULT_PARAM.supplierLoadCount
                    },
                    success: function (data) {
                        let flowListDom = renderEntityList(data.data);
                        next(flowListDom, page < data.totalPages);
                        bindEntityList("#flow-list");
                        form.render();
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
     * 渲染主体
     * @param entityList
     */
    function renderEntityList(entityList) {
        if (util.arrayNull(entityList)) {
            return "";
        }
        let entityEle = $("<ul class='entity-choose-list'></ul>");
        for (let index = 0; index < entityList.length; index++) {
            let entityInfo = entityList[index];
            let entityId = "";
            let typeName;
            if (entityInfo.hasOwnProperty("customerId")) {
                entityId = entityInfo.customerId;
                typeName = entityInfo.customerTypeName;
            }
            if (entityInfo.hasOwnProperty("supplierId")) {
                entityId = entityInfo.supplierId;
                typeName = entityInfo.supplierTypeName;
            }
            let checked = "";
            if (util.isNotNull(checkedEntityId) && checkedEntityId === entityId) {
                checked = " checked=''";
            }
            let companyName = entityInfo.companyName;
            let title = "<span class=\"customer-name\">"+companyName + "</span><span class=\"layui-badge layui-bg-gray\">" + typeName + "</span>";
            entityEle.append(
                "<li data-entity-id='" + entityId + "' data-entity-name = '" + companyName + "'>" +
                "    <input type='radio' name='entityName' value='" + entityId + "' " + checked + " title='" + title + "'>" +
                "</li>");
        }
        return entityEle.prop("outerHTML");
    }

    function bindEntityList() {
        let entityItems = $("li[data-entity-id]");
        if (util.arrayNotNull(entityItems)) {
            for (let index = 0; index < entityItems.length; index++) {
                let item = entityItems[index];
                $(item).click(function () {
                    let entityId = $(this).attr("data-entity-id");
                    let companyName = $(this).attr("data-entity-name");
                    // 关闭本页 返回到上层
                    $("#entity-btn-name", window.parent.document).html(companyName);
                    $("#entity-id", window.parent.document).val(entityId);
                    window.parent.clearSelectedProductInfo();
                    parent.layer.close(windowIndex);
                });
            }
        }
    }

    function bindSearch() {
        $("#search").click(function () {
            loadEntityInfo();
        });
    }
});