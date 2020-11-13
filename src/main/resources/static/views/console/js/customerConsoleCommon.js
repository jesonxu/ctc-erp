/**
 * 重新加载的方法 （销售 控制台刷新 全局的方法）
 * 其他地方不一定适应 需要调用本js中的加载方法的时候，请悉知方法作用
 */

// 用于存储 全局变量 以供各个模块能够调用
//---------------- 点击展开的客户类型----------------
var sale_open_customer_type_id = "";
// 客户类型名称
var sale_open_customer_type_name = "";

// ----------------点击展开部门ID----------------
var sale_open_dept_id = "";
// 点击部门名称
var sale_open_dept_name = "";
// 点开部门的所有子部门（不含点击的部门）
var sale_open_sub_dept_id = [];

//----------------客户id(默认为空)----------------
var sale_customer_id = "";
// 销售客户名称
var sale_customer_name = "";

// ----------------产品id(默认为空)----------------
var sale_product_id = "";
// 销售的产品名称
var sale_product_name = "";

// 销售操作（展开的值）
var sale_customer_opts = [];
// 产品结算方式
var sale_product_settle_type = "";
// 运营年份(默认为空)
var sale_operate_year = "";
// 运营月份(默认为空)
var sale_operate_month = "";

// 结算年份(默认为空)
var sale_settlement_year = "";
// 结算月份(默认为空)
var sale_settlement_month = "";

// 统计年份(默认为空)
var sale_statistic_year = "";

// 清空所有的记录数据
function clear_all_record() {
    sale_open_customer_type_id = "";
    sale_customer_id = "";
    sale_customer_opts = [];
    sale_product_id = "";
    sale_product_settle_type = "";
    sale_operate_year = "";
    sale_operate_month = "";
    sale_settlement_year = "";
    sale_settlement_month = "";
    sale_statistic_year = "";
    sale_open_dept_id = "";
    sale_open_sub_dept_id = [];
    sale_open_customer_type_name="";
}

// 记录客户类型
function sale_record_open_customer_type(customer_type_id, customer_type_name) {
    clear_all_record();
    sale_open_customer_type_id = customer_type_id;
    sale_open_customer_type_name = customer_type_name;
}

// 记录打开部门信息(部门只记录最近点击的一个部门)
function sale_record_open_dept(open_dept_id, open_dept_sub_dept, open_dept_name) {
    var customer_type_id = sale_open_customer_type_id;
    var customer_type_name =  sale_open_customer_type_name;
    clear_all_record();

    sale_open_customer_type_id = customer_type_id;
    sale_open_customer_type_name = customer_type_name;

    sale_open_dept_id = open_dept_id;
    sale_open_sub_dept_id = open_dept_sub_dept;
    sale_open_dept_name = open_dept_name;
}

// 记录客户信息
function sale_record_open_customer(customer_id, customer_name) {
    var customer_type_id = sale_open_customer_type_id;
    var customer_type_name =  sale_open_customer_type_name;

    var open_dept_id = sale_open_dept_id;
    var open_dept_sub_dept = sale_open_sub_dept_id;
    var open_dept_name = sale_open_dept_name;
    clear_all_record();
    sale_open_customer_type_id = customer_type_id;
    sale_open_customer_type_name = customer_type_name;

    sale_open_dept_id = open_dept_id;
    sale_open_sub_dept_id = open_dept_sub_dept;
    sale_open_dept_name = open_dept_name;

    sale_customer_id = customer_id;
    sale_customer_name = customer_name;
}

// 记录产品信息
function sale_record_product_info(product_id, product_name) {
    var customer_type_id = sale_open_customer_type_id;
    var customer_type_name = sale_open_customer_type_name;

    var open_dept_id = sale_open_dept_id;
    var open_dept_sub_dept = sale_open_sub_dept_id;
    var open_dept_name = sale_open_dept_name;

    var customer_id = sale_customer_id;
    var customer_name = sale_customer_name;

    clear_all_record();

    sale_open_customer_type_id = customer_type_id;
    sale_open_customer_type_name = customer_type_name;

    sale_open_dept_id = open_dept_id;
    sale_open_sub_dept_id = open_dept_sub_dept;
    sale_open_dept_name = open_dept_name;

    sale_customer_id = customer_id;
    sale_customer_name = customer_name;

    sale_product_name = product_name;
    sale_product_id = product_id;
}


// 重新加载客户信息
// type 加载类型
// opts_type 对应折叠筐的 data-my-opts-type
function sale_reload_customer_info(type, opts_type) {
    // 刷新整个页面范围（添加供应商）
    if (type === 0 || type === "0") {
        // 加载整个客户信息
        $('#demoAdmin', window.parent.document).attr('src', function (i, val) {
            return val;
        });
    } else if (type === 1 || type === "1") {
        // 折叠客户信息（编辑客户）
        if (isNotBlank(sale_customer_id)) {
            var customer_ele = $("div[data-my-opts-type='customer-base-info'][data-my-id=" + sale_customer_id + "]");
            if ($(customer_ele).hasClass("my_active")) {
                // 打开的情况下进行关闭
                $(customer_ele).children().filter("i[class*='my_i_left_tool']").trigger("click");
            }
        }
    } else if (type === 2 || type === "2") {
        // 折叠客户下面的所有折叠筐（修改、添加部门联系日志|修改部门信息）
        var customer_item_ele = $("div[data-my-opts-type='customer-dept-info'][data-my-id=" + sale_customer_id + "]");
        if ($(customer_item_ele).hasClass("my_active")) {
            // 打开的情况下进行关闭
            $(customer_item_ele).trigger("click");
        }
    } else {
        // 未知刷新操作 重新加载整个页面
        $('#demoAdmin', window.parent.document).attr('src', function (i, val) {
            return val;
        });
    }
}

// 重新加载产品信息
function reload_sale_product_info() {
    if (isNotBlank(sale_customer_id)) {
        // 重新加载产品信息
        if (typeof query_customer_products == "function") {
            query_customer_products(sale_customer_id);
        }
        if (isNotBlank(sale_product_id)) {
            // 选中指定产品id的产品
            // 触发其点击事件
            var product_item_ele = $("#product_panel").find("div[data-my-id=" + sale_product_id + "]");
            if (!$(product_item_ele).hasClass("my_active")) {
                $(product_item_ele).trigger("click");
            }
        }
    }
}

// 重新加载运营信息
function reload_sale_operate_info() {
    // 有可能会在重新加载的时候改变这个值、导致无法还原
    var temp_operate_year = sale_operate_year;
    var temp_operate_month = sale_operate_month;
    if (isNotBlank(sale_customer_id)) { // 通过点击客户加载的运营
        if (isNotBlank(sale_product_id)) {
            // 查询指定产品的运营信息
            if (typeof loadCustomerOperate == 'function') {
                loadCustomerOperate(0, sale_product_id);
            }
        } else {
            // 查询供应商的运营信息 （加载所有运营）
            if (typeof loadCustomerAllOperate == 'function') {
                loadCustomerAllOperate(sale_customer_id);
            }
        }
    } else { // 进入工作台直接加载的运营
        loadCustomerAllOperate('');
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
            var operate_month = operate_depart.find("div[data-my-id=" + temp_operate_year + "-" + temp_operate_month + "]");
            var month_is_open = $(operate_month).attr("data-my-open");
            if (isBlank(month_is_open) || month_is_open === false || month_is_open === "false") {
                $(operate_month).trigger("click");
            }
        }
    }
}

// 重新加载结算信息
function reload_sale_settlement_info() {
    // 有可能会在重新加载的时候改变这个值、导致无法还原
    var temp_settlement_year = sale_settlement_year;
    var temp_settlement_month = sale_settlement_month;
    if (isNotBlank(sale_customer_id)) {
        if (isNotBlank(sale_product_id)) {
            // 查询指定产品的结算信息
            if (typeof loadCustomerSettlement == 'function') {
                loadCustomerSettlement(sale_customer_id, sale_product_id, customerFlowType);
            }
        } else {
            // 查询供应商的结算信息
            // 加载所有结算
            if (typeof loadCustomerAllSettlement == 'function') {
                loadCustomerAllSettlement(sale_customer_id, customerFlowType);
            }
        }
    } else {
        loadCustomerAllSettlement(sale_customer_id, customerFlowType);
    }

    // 展开指定年份、月份
    if (isNotBlank(temp_settlement_year)) {
        // 年份不为空
        // 运营部分
        var settlement_depart = $("#settlement_depart");
        var settlement_year = settlement_depart.find("div[data-my-id='" + temp_settlement_year + "']");
        var year_is_open = $(settlement_year).attr("data-my-open");
        if (isBlank(year_is_open) || year_is_open === false || year_is_open === "false") {
            $(settlement_year).trigger("click");
        }
        if (isNotBlank(temp_settlement_month)) {
            // 月份不为空
            var settlement_month = settlement_depart.find("div[data-my-id='" + temp_settlement_month + "']");
            var month_is_open = $(settlement_month).attr("data-my-open");
            if (isBlank(month_is_open) || month_is_open === false || month_is_open === "false") {
                $(settlement_month).trigger("click");
            }
        }
    }
    // 审核过后刷新统计数据
    reload_sale_statistic_info();
}

// 重新加载统计信息
function reload_sale_statistic_info() {
    if (isNotBlank(sale_customer_id)) {
        if (isNotBlank(sale_product_id)) {
            // 查询指定产品的统计信息
            if (typeof load_statistics_time == "function") {
                load_statistics_time(2);
            }
        } else {
            // 查询供应商的统计信息
            if (typeof load_statistics_time == "function") {
                load_statistics_time(1);
            }
        }
    } else {
        // 查询当前用户的所有统计信息
        if (typeof load_statistics_time == "function") {
            load_statistics_time();
        }
    }
}