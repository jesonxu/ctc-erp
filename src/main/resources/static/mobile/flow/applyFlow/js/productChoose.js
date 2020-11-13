layui.use(['layer', 'form', 'element'], function () {
    let layer = layui.layer;
    let form = layui.form;
    let windowIndex = parent.layer.getFrameIndex(window.name);
    let entityId = util.getUrlParam("entityId");
    let checkedProductId = util.getUrlParam("productId");
    // 加载产品信息
    loadProductInfo();
    // 绑定搜索按钮
    bindSearch();

    /**
     * 加载产品信息
     */
    function loadProductInfo() {
        let associateType = util.getUrlParam("associateType");
        if (util.isNotNull(associateType)) {
            associateType = parseInt(associateType);
        }
        let loadUrl = "";
        if (associateType === 0) {
            // 客户
            loadUrl = "/customerProduct/getCustomerProduct";
        } else if (associateType === 1) {
            // 供应商
            loadUrl = "/product/getSupplierProduct";
        } else {
            throw new Error("不需要加载产品的类型，请不要跳转到此页面");
        }
        // debugger;
        initProductInfo(loadUrl)
    }

    /**
     * 初始化产品信息
     */
    function initProductInfo(url) {
        $("#product-list").html("");
        let loadIndex = layer.load(1, {
            shade: [0.1, '#fff']
        });
        $.ajax({
            url: url + "?temp=" + Math.random(),
            dataType: "json",
            method: "POST",
            data: {
                "supplierId": entityId,
                "customerId": entityId,
            },
            success: function (data) {
                let productDom = renderProductList(data.data);
                $("#product-list").html(productDom);
                form.render();
                bindProductList();
                layer.close(loadIndex);
            },
            error: function (data) {
                $("#flow-list").html("暂无数据")
            }
        });
    }

    /**
     * 渲染主体
     * @param productList
     */
    function renderProductList(productList) {
        if (util.arrayNull(productList)) {
            return "<li class='empty-data'>暂无数据</li>";
        }
        let entityEle = $("<ul class='product-choose-list'></ul>");
        for (let index = 0; index < productList.length; index++) {
            let entityInfo = productList[index];
            let productId = entityInfo.productId;
            let checked = "";
            if (util.isNotNull(checkedProductId) && checkedProductId === productId) {
                checked = " checked='' ";
            }
            let productName = entityInfo.productName;
            entityEle.append(
                "<li data-product-id='" + productId + "' data-product-name = '" + productName + "'>" +
                "    <input type='radio' name='entityName' value='" + productId + "' " + checked + " title='" + productName + "'>" +
                "</li>");
        }
        return entityEle.prop("outerHTML");
    }

    /**
     * 点击查询
     */
    function bindSearch() {
        $("#product-search").click(function () {
            loadProductInfo();
        });
    }


    /**
     * 绑定点击选择事件
     */
    function bindProductList() {
        let productItems = $("li[data-product-id]");
        if (util.arrayNotNull(productItems)) {
            for (let index = 0; index < productItems.length; index++) {
                let productItem = productItems[index];
                $(productItem).click(function () {
                    let productId = $(this).attr("data-product-id");
                    let productName = $(this).attr("data-product-name");
                    // 关闭本页 返回到上层
                    $("#product-btn-name", window.parent.document).html(productName);
                    $("#product-id", window.parent.document).val(productId);
                    parent.layer.close(windowIndex);
                });
            }
        }
    }
});