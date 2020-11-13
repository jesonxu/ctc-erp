'use strict';
(function (window, factory) {
    window.layui_table_span = factory();
})(window, function () {

    /**
     * layui 表格合并 工具对象（初始化时获取所有的基本数据）
     * @param table_ele 目标
     */
    var layui_table_span = function (table_ele) {
        var my = this;
        if (my.is_null(table_ele)) {
            throw new Error("对象错误，无法找到对应表格");
        }
        var table_dom = $(table_ele);
        if (my.is_null(table_dom) || table_ele.length === 0) {
            throw new Error("对象错误，无法找到对应表格");
        }
        this.target_table = table_ele;
        my.get_opts_div();
        my.get_header();
    };

    /**
     * 获取实际操作的div
     */
    layui_table_span.prototype.get_opts_div = function () {
        var my = this;
        var target_table = $(my.target_table);
        // 获取到指定的id
        var target_id = target_table.attr("id");
        var opts_div = "";
        if (my.is_null(target_id)) {
            opts_div = target_table.next();
        } else {
            opts_div = $("div[lay-id='" + target_id + "']");
        }
        // 表格最外层的div
        this.target_div = opts_div;
        // 表格
        this.target_content = $(my.target_div).children().filter("div[class*='layui-table-box']");
    };


    /**
     * 获取表头
     */
    layui_table_span.prototype.get_header = function () {
        var my = this;
        var header_div = $(my.target_content).children().filter("div[class*='layui-table-header']");
        // 表头
        var headers = [];
        var header_ths = $(header_div).find("th");
        // 全部
        if (!my.is_null(header_ths)) {
            for (var th_index = 0; th_index < header_ths.length; th_index++) {
                var header_th = header_ths[th_index];
                var header_name = $(header_th).attr("data-field");
                headers.push(header_name);
            }
        }
        this.headers = headers;
        // 左边固定
        var left_headers = [];
        var left_header_div = $(my.target_content).children().filter("div[class*='layui-table-fixed-l']");
        if (!my.is_null(left_header_div)) {
            var left_header_ths = $(left_header_div).find("th");
            if (!my.is_null(left_header_ths) && left_header_ths.length > 0) {
                for (var left_th_index = 0; left_th_index < left_header_ths.length; left_th_index++) {
                    var left_header_th = left_header_ths[left_th_index];
                    var left_header_name = $(left_header_th).attr("data-field");
                    left_headers.push(left_header_name);
                }
            }
        }
        this.left_headers = left_headers;
        // 右边固定
        var rigth_headers = [];
        var rigth_header_div = $(my.target_content).children().filter("div[class*='layui-table-fixed-r']");
        if (!my.is_null(rigth_header_div)) {
            var rigth_header_ths = $(rigth_header_div).find("th");
            if (!my.is_null(rigth_header_ths) && rigth_header_ths.length > 0) {
                for (var right_th_index = 0; right_th_index < rigth_header_ths.length; right_th_index++) {
                    var right_header_th = rigth_header_ths[right_th_index];
                    var right_header_name = $(right_header_th).attr("data-field");
                    rigth_headers.push(right_header_name);
                }
            }
        }
        this.right_headers = rigth_headers;
    };

    /**
     * 列合并 列名(单个)
     */
    layui_table_span.prototype.exec_rowspan = function (fieldName, from, to) {
        var my = this;
        // 序号
        var col_index = my.headers.indexOf(fieldName);
        if (col_index < 0) {
            console.error("合并的列名不存在");
            return "";
        }
        var table_main = $(my.target_content).children().filter("div[class*='layui-table-main']");
        if (!my.is_null(table_main)) {
            // 数据行（某一列数据）
            var table_data_trs = $(table_main).find("td[data-field='" + fieldName + "']");
            if (!my.is_null(table_data_trs) && table_data_trs.length > 0) {
                // 开始的内容
                var begain_content = "";
                // 记录合并多少行
                var span_row_count = 0;
                // 根据内容 开始索引
                var begain_content_index = 0;
                // 总行数
                var total_row = table_data_trs.length;
                for (var data_index = 0; data_index < table_data_trs.length; data_index++) {
                    // 数据 行（只有一列）
                    var data_row = $(table_data_trs[data_index]);
                    // 行号
                    var row_index = data_index + 1;
                    if (!my.is_null(from) && !my.is_null(to)) {
                        // 手动指定合并行数
                        if (row_index === from) {
                            data_row.attr("rowspan", (to - from + 1));
                        }
                        // 区间其他的设置为不可见（如果有问题可以删除 这里不删除 是防止数据获取失败）
                        if (row_index > from && row_index <= to) {
                            data_row.hide();
                        }
                    } else {
                        // 内容
                        var content = data_row.children().text();
                        if (data_index === 0) {
                            begain_content = content;
                            span_row_count++;
                            begain_content_index = 0;
                        }

                        if (data_index !== begain_content_index &&
                            data_index < (total_row - 1) &&
                            my.equals(begain_content, content)) {
                            span_row_count++;
                        } else {
                            if (span_row_count > 1) {
                                $(table_data_trs[begain_content_index]).attr("rowspan", span_row_count);
                                // 设置区间的单元格不可见
                                while (span_row_count > 1) {
                                    $(table_data_trs[begain_content_index + span_row_count - 1]).hide();
                                    span_row_count--;
                                }
                                // 分割的时候 默认又有一个
                                span_row_count++;
                            }
                            // 重新记录
                            begain_content_index = data_index;
                            begain_content = content;
                        }
                    }
                }
            }
        }
    };


    /**
     * 合并左边的单元格
     */
    layui_table_span.prototype.exec_left_rowspan = function (fieldName, from, to) {
        var my = this;
        // 序号
        var col_index = my.left_headers.indexOf(fieldName);
        if (col_index < 0) {
            return "";
        }
        // 左边冻结行数
        var left_row_num = my.right_headers.length;
        // 左边冻结表格
        var table_left = $(my.target_content).children().filter("div[class*='layui-table-fixed-l']");
        if (!my.is_null(table_left)) {
            // 数据行（某一列数据）
            var table_left_data_trs = $(table_left).find("td[data-field='" + fieldName + "']");
            if (!my.is_null(table_left_data_trs) && table_left_data_trs.length > 0) {
                // 开始的内容
                var left_begain_content = "";
                // 记录合并多少行
                var left_span_row_count = 0;
                // 根据内容 开始索引
                var left_begain_content_index = 0;
                // 总行数
                var left_total_row = table_left_data_trs.length;
                for (var left_data_index = 0; left_data_index < table_left_data_trs.length; left_data_index++) {
                    // 数据 行（只有一列）
                    var data_row = $(table_left_data_trs[left_data_index]);
                    // 行号
                    var row_index = left_data_index + 1;
                    // 记录合并多少行
                    var span_row_count = 0;
                    if (!my.is_null(from) && !my.is_null(to)) {
                        // 手动指定合并行数
                        if (row_index === from) {
                            if (left_row_num === 1) {
                                // 需要设置高度，否则会出现问题
                                var height = data_row.outerHeight();
                                // 高度差值
                                var left_diff = data_row.outerHeight() - height;
                                // 真实的高度
                                var left_span_cell_height = height * (to - from + 1) - left_diff * (to - from );
                                $(table_left_data_trs[from - 1]).height(left_span_cell_height);
                            }
                            data_row.attr("rowspan", (to - from + 1));
                        }
                        // 区间其他的设置为不可见（如果有问题可以删除 这里不删除 是防止数据获取失败）
                        if (row_index > from && row_index <= to) {
                            data_row.hide();
                        }
                    } else {
                        // 内容
                        var left_content = data_row.children().text();
                        if (left_data_index === 0) {
                            left_begain_content = left_content;
                            left_span_row_count++;
                            left_begain_content_index = 0;
                        }

                        if (left_data_index !== left_begain_content_index &&
                            left_data_index < (left_total_row - 1) &&
                            my.equals(left_begain_content, left_content)) {
                            left_span_row_count++;
                        } else {
                            if (left_span_row_count > 1) {
                                if (left_row_num === 1) {
                                    // 需要设置高度，否则会出现问题
                                    var left_height = data_row.height();
                                    // 高度差值
                                    var diff = data_row.outerHeight() - left_height;
                                    // 真实的高度
                                    var span_cell_height = left_height * left_span_row_count - diff * (left_span_row_count-1);
                                    $(table_left_data_trs[left_begain_content_index]).height(span_cell_height);
                                }

                                $(table_left_data_trs[left_begain_content_index]).attr("rowspan", left_span_row_count);
                                // 设置区间的单元格不可见
                                while (left_span_row_count > 1) {
                                    $(table_left_data_trs[left_begain_content_index + span_row_count - 1]).hide();
                                    left_span_row_count--;
                                }
                                // 分割的时候 默认又有一个
                                left_span_row_count++;
                            }
                            // 重新记录
                            left_begain_content_index = left_data_index;
                            left_begain_content = left_content;
                        }
                    }
                }
            }
        }
    };

    /**
     * 合并右边的单元格
     */
    layui_table_span.prototype.exec_right_rowspan = function (fieldName, from, to) {
        var my = this;
        // 序号
        var col_index = my.right_headers.indexOf(fieldName);
        if (col_index < 0) {
            return "";
        }
        // 右边冻结列数
        var right_row_num = my.right_headers.length;
        var table_right = $(my.target_content).children().filter("div[class*='layui-table-fixed-r']");
        if (!my.is_null(table_right)) {
            // 数据行（某一列数据）
            var table_right_data_trs = $(table_right).find("td[data-field='" + fieldName + "']");
            if (!my.is_null(table_right_data_trs) && table_right_data_trs.length > 0) {
                // 开始的内容
                var right_begain_content = "";
                // 记录合并多少行
                var right_span_row_count = 0;
                // 根据内容 开始索引
                var right_begain_content_index = 0;
                // 总行数
                var right_total_row = table_right_data_trs.length;
                for (var right_data_index = 0; right_data_index < table_right_data_trs.length; right_data_index++) {
                    // 数据 行（只有一列）
                    var data_row = $(table_right_data_trs[right_data_index]);
                    // 行号
                    var row_index = right_data_index + 1;
                    if (!my.is_null(from) && !my.is_null(to)) {
                        // 手动指定合并行数
                        if (row_index === from) {
                            if (right_row_num === 1) {
                                // 需要设置高度，否则会出现问题
                                var line_height = data_row.outerHeight();
                                // 高度差值
                                var diff = data_row.outerHeight() - line_height;
                                // 真实的高度
                                var span_cell_height = line_height * (to - from + 1) - diff * (to - from);
                                $(table_right_data_trs[from - 1]).height(span_cell_height);
                            }
                            data_row.attr("rowspan", (to - from + 1));
                        }
                        // 区间其他的设置为不可见（如果有问题可以删除 这里不删除 是防止数据获取失败）
                        if (row_index > from && row_index <= to) {
                            data_row.hide();
                        }
                    } else {
                        // 内容
                        var right_content = data_row.children().text();
                        if (right_data_index === 0) {
                            right_begain_content = right_content;
                            right_span_row_count++;
                            right_begain_content_index = 0;
                        }

                        if (right_data_index !== right_begain_content_index &&
                            right_data_index < (right_total_row - 1) &&
                            my.equals(right_begain_content, right_content)) {
                            right_span_row_count++;
                        } else {
                            if (right_span_row_count > 1) {
                                if (right_row_num === 1) {
                                    // 需要设置高度，否则会出现问题
                                    var height = data_row.height();
                                    // 外框高度
                                    var out_height = data_row.outerHeight();
                                    // 高度差值
                                    var diff = out_height - height;
                                    // 真实的高度
                                    var span_cell_height = out_height * right_span_row_count;
                                    $(table_right_data_trs[right_begain_content_index]).height(span_cell_height);
                                }

                                $(table_right_data_trs[right_begain_content_index]).attr("rowspan", right_span_row_count);
                                // 设置区间的单元格不可见
                                while (right_span_row_count > 1) {
                                    $(table_right_data_trs[right_begain_content_index + right_span_row_count - 1]).hide();
                                    right_span_row_count--;
                                }
                                // 分割的时候 默认又有一个
                                right_span_row_count++;
                            }
                            // 重新记录
                            right_begain_content_index = right_data_index;
                            right_begain_content = right_content;
                        }
                    }
                }
            }
        }
    };

    /**
     * 合并数据表格行 fieldNameTmp 列名(单个 或者数组)
     */
    layui_table_span.prototype.rowspan = function (fieldNameTmp, from, to) {
        var my = this;
        try {
            if (!my.is_null(from)) {
                from = parseInt(from);
            }
            if (!my.is_null(to)) {
                to = parseInt(to);
            }
        } catch (e) {
            console.error("数据错误" + e);
            return;
        }
        var fieldName = [];
        if (!my.is_null(fieldNameTmp)) {
            if (typeof fieldNameTmp == "string") {
                fieldName.push(fieldNameTmp);
            } else {
                fieldName = fieldName.concat(fieldNameTmp);
            }
            for (var i = 0; i < fieldName.length; i++) {
                my.exec_rowspan(fieldName[i], from, to);
                my.exec_left_rowspan(fieldName[i], from, to);
                my.exec_right_rowspan(fieldName[i], from, to);
            }
        } else {
            console.error("合并表格的列为空");
        }
    };

    /**
     * 比较
     * param 比较参数
     * value 值
     * strict 是否严格相等
     */
    layui_table_span.prototype.equals = function (param, value, strict) {
        var my = this;
        if (my.is_null(strict) || strict === false || strict === 'false') {
            // 不是严格相等
            return (param === value || param + "" === value + "");
        } else {
            // 严格相等(包含类型)
            return (param === value && typeof param === typeof value);
        }
    };

    /**
     * 判断是否为空
     */
    layui_table_span.prototype.is_null = function (str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    };
    return layui_table_span;
});