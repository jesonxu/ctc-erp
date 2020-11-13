// 客户工作台
isCustomerConsole = true;
$.ajaxSettings.async = false;
// 控制台点击展开
var customer_module_open = new module_open("#customer-console-module",{});

layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    dropdown: 'dropdown'
});

$(document).ready(function (e) {
    // bind_customer_filter();
    // 转移客户按钮
    bind_transfer_customer();
});

var clickEle = null;
// 客户过滤 重新加载客户信息
function reload_customer() {
	console.log('触发了cust渲染');
    productId = '';
    productName = '';
    customerId = '';
    customerName = '';
    customerTypeId = '';
    customerTypeName = '';
    sale_product_id = '';
    sale_product_name = '';
    sale_customer_id = '';
    sale_customer_name = '';
    sale_open_customer_type_id = '';
    sale_open_customer_type_name = '';
    // 加载客户模块（只加载类型）
    if (typeof load_customer_type === "function"){
        load_customer_type();
    }
    bind_transfer_customer();
    if (!isQuery) {
    	reload_customer_product();
    	reload_customer_operate();
    	reload_customer_settlement();
    	reload_customer_statistic();
    } else {
    	setTimeout(function () {
    		var triggered = false;
    		$('[data-my-opts-type="customer_type"]').each(function (i, item) {
    			if (triggered) {
    				return;
    			}
    			var textContent = $(this).find('.my_text_title').text();
    			var custCount = parseInt(textContent.substring(textContent.indexOf('(') + 1, textContent.indexOf('家')));
    			if (custCount > 0) {
    				triggered = true;
    				clickEle = $(this);
    				$(this).trigger('click');
    			}
    		});
    	}, 500);
    }
}

// 重置产品
function reload_customer_product() {
    if (typeof query_customer_products == "function") {
        query_customer_products('');
    }
}

// 重置 运营
function reload_customer_operate() {
    if (typeof loadCustomerOperate == "function") {
        loadCustomerOperate(99, '');
    }
}

// 重置 结算
function reload_customer_settlement() {
    if (typeof loadCustomerSettlement == "function") {
        loadCustomerSettlement('', '', customerFlowType);
    }
}

// 重新加载统计
function reload_customer_statistic() {
    if (typeof load_sale_statistics_time == "function") {
        load_sale_statistics_time(3);
    }
}

// 绑定转移客户按钮的点击事件
function bind_transfer_customer() {
    $(".transfer-customer").unbind().bind('click', function (e) {
        toTransferCustomer();
    });
}

// 转移客户弹窗
function toTransferCustomer() {
    layer.open({
        type: 2,
        title: '客户转移',
        area: ['1000px', '600px'],
        btn: ['确认转移', '取消'],
        btnAlign: 'c',
        fixed: false, //不固定
        maxmin: true,
        content: '/customer/toTransferCustomer.action',
        yes: function (index, layero) {
            var body = layer.getChildFrame('body', index);
            var customerIds = '';
            $(body).find("input[type='checkbox']").each(function () {
                if ($(this).is(":checked")) {
                    customerIds += $(this).val() + ',';
                }
            });
            if (customerIds == '') {
                layer.msg("请选择要转移哪些客户");
                return;
            }
            customerIds.substring(0, customerIds.length - 1);
            var targetUserId = $(body).find("input[name='targetSalemanId']").val();
            if (targetUserId == null || targetUserId == undefined || targetUserId == '') {
                layer.msg("请选择要转移给哪位销售");
                return;
            }
            $.ajax({
                type: "POST",
                async: false,
                url: "/customer/transferCustomer",
                data: {
                    customerIds: customerIds,
                    targetUserId: targetUserId
                },
                success: function (data) {
                    if (data.code == 200) {
                        parent.layer.msg("转移成功");
                        layer.close(index);
                    } else {
                        parent.layer.msg(data.msg);
                    }
                }
            });
        }
    });
}