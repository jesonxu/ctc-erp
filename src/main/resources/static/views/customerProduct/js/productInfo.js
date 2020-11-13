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

// 加载客户产品（包含对部门的处理）
function query_customer_products(click_customerId){
    var loadingIndex = layer.load(2);
    // console.time('加载客户所有产品耗时');
    $.ajaxSettings.async = true;
    // 加载客户产品信息
    $.post("/customerProduct/queryProducts.action", {customerId: click_customerId}, function (data) {
        if (data != null && data.length != 0) {
            $('#productCard').html(data);
            // 初始化产品的折叠框
            init_customer_product_panel();
            // 绑定添加按钮
            bind_add_customer_product();
        }
        // console.timeEnd('加载客户所有产品耗时');
        layer.close(loadingIndex);
    },'html');
    $.ajaxSettings.async = false;
}

// 初始化 客户产品 折叠框
function init_customer_product_panel() {
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
            var t = $(item).clone();
            t.find('.layui-badge').remove();
            productName = $(t.find('.my_text_title')).text().trim();
            if (typeof sale_record_product_info == "function"){
                // 记录产品信息
                sale_record_product_info(itemId, productName);
            }
            if (!productPanel.isNull(optsType) && (optsType === 1 || optsType === '1')) {
                productId = itemId;
                if (typeof loadCustomerOperate == 'function') {
                    loadCustomerOperate(0, productId);
                }
                if (typeof loadCustomerSettlement == 'function') {
                    loadCustomerSettlement(customerId, productId, customerFlowType);
                }
            }
            // 销售统计表
            /*if (typeof load_sale_statistics_time == 'function') {
                load_sale_statistics_time(2);
            }*/
        }
    });
    productPanel.init("#product_panel");
    element.render("collapse", "product_title");
}

// 添加产品的按钮
function bind_add_customer_product() {
	var title = getMsg("product.add");
    $('#addProductBtn').unbind().bind('click' , function() {
        layer.open({
            title: [title],
            type: 2,
            area: ['700px', '80%'],
            fixed: false, //不固定
            maxmin: true,
            content: '/customerProduct/toAddProduct.action?customerId=' + customerId + "&customerTypeId=" + sale_open_customer_type_id
        });
    });
}

// 修改产品
function editProduct(productId) {
	var title = getMsg("product.edit");
    layer.open({
        title: [title],
        type: 2,
        area: ['700px', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/customerProduct/toEditProduct.action?productId=' + productId + "&customerTypeId=" + sale_open_customer_type_id
    });
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
