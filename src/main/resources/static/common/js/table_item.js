'use strict';
(function (window, factory) {
    window.table_item = factory();
})(window, function () {

    // 针对某一类选项
    var table_item = function (ele, param, call_back) {
        var my = this;
        this.def_param = param;
        var data_items = $(ele);
        if (my.is_null(data_items) || data_items.length <= 0) {
            return;
        }
        if (this.is_null(call_back) || typeof call_back != "function") {
            call_back = {};
        }
        this.call_back = call_back;
        for (var item_index = 0; item_index < data_items.length; item_index++) {
            var table_item = data_items[item_index];
            // 对应的表格id
            var table_id = $(table_item).attr("data-table-show-id");
            if (this.is_null(table_id)) {
                console.error("对象参数缺失，初始化错误");
                return;
            }
            // 默认显示
            var default_show = $(table_item).attr("data-table-show");
            if (!this.is_null(default_show) && (default_show === true || default_show === "true")) {
                // 年
                var table_param = $(table_item).attr("data-table-param");
                // 展示表格数据
                my.show_table(table_id, table_param);
            } else {
                default_show = false;
            }
            // 标记对象
            $(table_item).attr("data-show-info", default_show);
            // 绑定点击事件
            my.click(table_item);
        }
    };

    table_item.prototype.click = function (table_item) {
        var my = this;
        $(table_item).click(function (e) {
            // 是否已经展示
            var data_show = $(this).attr("data-show-info");
            // 表格id
            var data_table_id = $(this).attr("data-table-show-id");
            // 年(也支持HTML对象定义)
            var table_param = $(this).attr("data-table-param");
            if (!my.is_null(data_show) && (data_show === true || data_show === "true")) {
                // 已经展示(进行关闭操作)
                my.hidden_table(data_table_id);
                data_show = false;
            } else {
                // 没有展示（展示操作）
                my.show_table(data_table_id, table_param);
                data_show = true;
            }
            $(this).attr("data-show-info", data_show);
        });
    };

    /* 显示表格 */
    table_item.prototype.show_table = function (table_id, table_param) {
        var my = this;
        my.call_back(table_param, my.def_param);
        var table_div = $("div[lay-id='" + table_id + "']");
        if (!my.is_null(table_div)) {
            table_div.fadeIn(500);
        }
    };

    /* 隐藏表格 */
    table_item.prototype.hidden_table = function (table_id) {
        var my = this;
        var table_div = $("div[lay-id='" + table_id + "']");
        if (!my.is_null(table_div)) {
            table_div.fadeOut(200);
        }
    };

    /* 判断是否为空 */
    table_item.prototype.is_null = function (str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    };

    /* 产生唯一性 id */
    table_item.prototype.ele_uid = function () {
        return 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    return table_item;
});