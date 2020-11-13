//产品页面的所有全局参数的集合
var product = {};
$.ajaxSettings.async = false;

layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    dropdown: 'dropdown'
});

$(document).ready( function (e) {
    bind_supplier_search();
});

/* 控制台点击展开 */
//var resource_module_open = new module_open("#resource-console-module",{});

// 绑定搜索按钮事件
function bind_supplier_search() {
    $("#search_supplier").click(function (e) {
        layer.open({
            type: 1,
            title: '搜索供应商',
            area: ['400px', '200px'],
            fixed: false, //不固定
            maxmin: true,
            content: $("#search_input"),
            btn: ['确定', '取消'],
            yes: function (index, layero) {
                supplierKeyWord = $("#keyWord").val();
                search_supplier_by_keyword();
                layer.close(index);
            }
        });
    });
}

// 按名称搜索供应商
function search_supplier_by_keyword() {
    $.ajax({
        type: "POST",
        url: "/resourceConsole/queryByKeyWord.action?temp=" + Math.random(),
        dataType: "html",
        async:true,
        data: {keyWord: supplierKeyWord},
        success: function (data) {
            $(".supplier_depart").html(data);
            productId = '';
            productName = '';
            supplierId = '';
            supplierName = '';
            supplierTypeId = '';
            supplierTypeName = '';
            bind_supplier_search();
            reload_resource_product();
            reload_resource_operate();
            reload_resource_settlement();
            reload_resource_statistic();
        }
    });
}

/*function reload_resource_product() {
    if (typeof loadSupplierProducts == "function") {
        loadSupplierProducts(supplierId,1,10);
    }
}*/

function reload_resource_operate() {
    if (typeof loadSupplierOperate == "function") {
        loadSupplierOperate(99, '');
    }
}

function reload_resource_settlement() {
    if (typeof loadSupplierSettlement == "function") {
        loadSupplierSettlement('', '');
    }
}

function reload_resource_statistic() {
    if (typeof load_statistics_time == 'function') {
        load_statistics_time(3);
    }
}