/**
 * 重新加载的方法 （商务-供应商 控制台刷新 全局的方法）
 * 其他地方不一定适应 需要调用本js中的加载方法的时候，请悉知方法作用
 */

// 用于存储 全局变量 以供各个模块能够调用
// 供应商id(默认为空)
var console_supplier_id = "";
// 供应商操作（展开的值）
var console_supplier_opts = [];

// 产品id(默认为空)
var console_product_id = "";
// 产品结算方式
var console_product_settle_type = "";

//运营年份(默认为空)
var console_operate_year = "";

// 运营月份(默认为空)
var console_operate_month = "";

// 结算年份(默认为空)
var console_settlement_year = "";

// 结算月份(默认为空)
var console_settlement_month = "";

// 统计年份(默认为空)
var console_statistic_year = "";

// 删除数组中大于某个数组的所有项
function delete_larger_item(arr, num) {
    if (isNotBlank(arr) && isNotBlank(num)) {
        for (var arr_index = 0; arr_index < arr.length; arr_index++) {
            var arr_temp = arr[arr_index];
            if (typeof arr_temp == "number" && arr_temp >= num) {
                arr.splice(arr_index, 1);
                arr_index--;
            }
        }
    }
}

// 清空所有的记录数据
function clear_all_record() {
    console_supplier_id = "";
    // 清空其他值
    console_supplier_opts = [];
    console_product_id = "";
    console_product_settle_type = "";
    console_operate_year = "";
    console_operate_month = "";
    console_settlement_year = "";
    console_settlement_month = "";
}

// 记录供应商id信息
function record_supplier_info(supplier_id) {
    clear_all_record();
    console_supplier_id = supplier_id;
}

// 记录产品信息
function record_product_info(product_id) {
    var supplier_id = console_supplier_id;
    clear_all_record();
    console_supplier_id = supplier_id;
    console_product_id = product_id;
}

// 数组不为空
function arr_not_blank(arr) {
    return isNotBlank(console_supplier_opts) && console_supplier_opts.length > 0;
}


// 重新加载供应商信息
// type 加载类型
// opts_type 对应折叠筐的 data-my-opts-type
function reload_supplier_info(type, opts_type) {
    // 刷新整个页面范围（添加供应商）
    if (type === 0 || type === "0") {
        // 加载整个供应商信息
        $('#demoAdmin', window.parent.document).attr('src', function (i, val) {
            return val;
        });
    } else if (type === 1 || type === "1") {
        // 折叠供应商信息（编辑供应商）
        if (isNotBlank(console_supplier_id)) {
            var supplier_ele = $("div[data-my-opts-type='1'][data-my-id=" + console_supplier_id + "]");
            if ($(supplier_ele).hasClass("my_active")) {
                // 打开的情况下进行关闭
                $(supplier_ele).children().filter("i[class*='my_i_left_tool']").trigger("click");
            }
        }
    } else if (type === 2 || type === "2") {
        // 折叠供应商下面的所有折叠筐（修改、添加部门联系日志|修改部门信息）
        var supplier_item_ele = $("div[data-my-opts-type=" + opts_type + "][data-my-id=" + console_supplier_id + "]");
        if ($(supplier_item_ele).hasClass("my_active")) {
            // 打开的情况下进行关闭
            $(supplier_item_ele).trigger("click");
        }
    } else {
        // 未知刷新操作 重新加载整个页面
        $('#demoAdmin', window.parent.document).attr('src', function (i, val) {
            return val;
        });
    }
}


// 重新加载产品信息
function reload_product_info() {
    if (isNotBlank(console_supplier_id)) {
        // 重新加载产品信息
        if (typeof loadSupplierProducts == "function") {
            loadSupplierProducts(console_supplier_id,1,10);
        }
        if (isNotBlank(console_product_id)) {
            // 选中指定产品id的产品
            // 触发其点击事件
            var product_item_ele = $("#product_panel").find("div[data-my-id=" + console_product_id + "]");
            if (!$(product_item_ele).hasClass("my_active")) {
                $(product_item_ele).trigger("click");
            }
        }
    }
}

// 重新加载运营信息
function reload_operate_info() {
    // 有可能会在重新加载的时候改变这个值、导致无法还原
    var temp_operate_year = console_operate_year;
    var temp_operate_month = console_operate_month;
    console.log("运营信息重新加载" + console_operate_year + "--->" + console_operate_month);
    if (isNotBlank(console_supplier_id)) {
        /*if (isNotBlank(console_product_id)) {
            // 查询指定产品的运营信息
            if (typeof loadSupplierOperate == 'function') {
                loadSupplierOperate(0, console_product_id);
            }
        } else {
            // 查询供应商的运营信息 （加载所有运营）
            if (typeof loadSupplierAllOperate == 'function') {
                loadSupplierAllOperate(console_supplier_id);
            }
        }*/
        // 查询供应商的运营信息 （加载所有运营）
        if (typeof loadSupplierAllOperate == 'function') {
            loadSupplierAllOperate(console_supplier_id);
        }
    } else {
        loadSupplierAllOperate('');
    }

    // 展开指定年份、月份
    if (isNotBlank(temp_operate_year)) {
        // 年份不为空
        // 运营部分
        var operate_depart = $("#operate_pannel");
        var operate_year = operate_depart.find("div[data-my-id=" + temp_operate_year + "]");
        var is_open = $(operate_year).attr("data-my-open");
        if (isBlank(is_open) || is_open === false || is_open === "false") {
            $(operate_year).trigger("click");
        }
        if (isNotBlank(temp_operate_month)) {
            // 月份不为空
            var operate_month = operate_depart.find("div[data-my-id=" + temp_operate_month + "]");
            var month_is_open = $(operate_month).attr("data-my-open");
            if (isBlank(month_is_open) || month_is_open === false || month_is_open === "false") {
                $(operate_month).trigger("click");
            }
        }
    }
    // 审核过后 刷新统计数据
    reload_statistic_info();
}

// 重新加载结算信息
function reload_settlement_info() {
    // 有可能会在重新加载的时候改变这个值、导致无法还原
    var temp_operate_year = console_settlement_year;
    var temp_operate_month = console_settlement_month;
    if (isNotBlank(console_supplier_id)) {
        /*if (isNotBlank(console_product_id)) {
            // 查询指定产品的结算信息
            if (typeof loadSupplierSettlement == 'function') {
                loadSupplierSettlement("", console_product_id);
            }
        } else {
            // 查询供应商的结算信息
            // 加载所有结算
            if (typeof loadSupplierAllSettlement == 'function') {
                loadSupplierAllSettlement(console_supplier_id);
            }
        }*/
        // 查询供应商的结算信息
        // 加载所有结算
        if (typeof loadSupplierAllSettlement == 'function') {
            loadSupplierAllSettlement(console_supplier_id);
        }
    } else {
        loadSupplierAllSettlement('');
    }

    // 展开指定年份、月份
    if (isNotBlank(temp_operate_year)) {
        // 年份不为空
        // 运营部分
        var settlement_depart = $("#settlement_depart");
        var settlement_year = settlement_depart.find("div[data-my-id='" + temp_operate_year + "']");
        var year_is_open = $(settlement_year).attr("data-my-open");
        if (isBlank(year_is_open) || year_is_open === false || year_is_open === "false") {
            $(settlement_year).trigger("click");
        }
        if (isNotBlank(temp_operate_month)) {
            // 月份不为空
            var settlement_month = settlement_depart.find("div[data-my-id='" + temp_operate_month + "']");
            var month_is_open = $(settlement_month).attr("data-my-open");
            if (isBlank(month_is_open) || month_is_open === false || month_is_open === "false") {
                $(settlement_month).trigger("click");
            }
        }
    }
    // 审核过后刷新统计数据
    reload_statistic_info();
}

// 重新加载统计信息
function reload_statistic_info() {
    if (isNotBlank(console_supplier_id)) {
        /*if (isNotBlank(console_product_id)) {
            // 查询指定产品的统计信息
            if (typeof load_statistics_time == "function") {
                load_statistics_time(2);
            }
        } else {
            // 查询供应商的统计信息
            if (typeof load_statistics_time == "function") {
                load_statistics_time(1);
            }
        }*/
        // 查询供应商的统计信息
        if (typeof load_statistics_time == "function") {
            load_statistics_time(1);
        }
    } else {
        // 查询当前用户的所有统计信息
        if (typeof load_statistics_time == "function") {
            load_statistics_time();
        }
    }
}