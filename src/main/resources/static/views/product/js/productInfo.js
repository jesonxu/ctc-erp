var layer;
var element;
var form;

$(document).ready(function () {
    layui.use(['layer', 'form', 'element'], function () {
        layer = layui.layer;
        element = layui.element;
        form = layui.form;
    });
});

// 加载供应商所有产品
function loadSupplierProducts(supplierid){
    var loadingIndex = layer.load(2);
    // console.time('加载供应商所有产品耗时');
    $.ajaxSettings.async = true;
    $.post("/product/queryProducts.action", {supplierId: supplierid}, function (data) {
        if (data != null && data.length != 0) {
            $('#productCard').html(data);
            // 初始化产品的折叠框
            initProductPanel();
            // 绑定添加按钮
            initButtonClick();
        }
        // console.timeEnd('加载供应商所有产品耗时');
        layer.close(loadingIndex);
    },'html');
    $.ajaxSettings.async = false;
}

// 初始化 折叠框
function initProductPanel() {
    var productPanel = new myPannel({
        right: function (item, itemId, optsType) {
            if (!productPanel.isNull(optsType)) {
                if ((optsType === 1||optsType === '1')) {
                    // 修改产品
                    editProduct(itemId)
                }
            }
        },
        middle: function (item, itemId, optsType) {
            if (typeof record_product_info == "function"){
                // 记录产品信息
                record_product_info(itemId);
            }
            if (!productPanel.isNull(optsType) && (optsType === 1||optsType === '1')) {
            	productId = itemId;
                if (typeof loadSupplierOperate == 'function') {
                    loadSupplierOperate(0, productId);
                }
                if (typeof loadSupplierSettlement == 'function') {
                    loadSupplierSettlement(supplierId, productId);
                }
            }
            var t = $(item).clone();
            t.find('.layui-badge').remove();
            productName = $(t.find('.my_text_title')).text().trim();
            if (typeof load_statistics_time == 'function') {
                load_statistics_time(2);
            }
        }
    });
    productPanel.init("#productBody");
    element.render();
}

function editProduct(productId) {
    layer.open({
        title: ['修改产品'],
        type: 2,
        area: ['700px', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/product/toEditProduct.action?productId=' + productId
    });
}

function initButtonClick() {
    $('#addProductBtn').click(function() {
        layer.open({
            title: ['添加产品'],
            type: 2,
            area: ['700px', '80%'],
            fixed: false, //不固定
            maxmin: true,
            content: '/product/toAddProduct.action?supplierId=' + supplierId
        });
    });
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
