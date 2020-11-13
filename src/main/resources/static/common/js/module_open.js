'use strict';
(function (window, factory) {
    window.module_open = factory();
})(window, function () {

    var defConfig = {
        /**最小宽度 计算错误的时候 默认值**/
        min_width: 8
    };

    /* 模块 展开事件 */
    var module_open = function (ele, config) {
        // 目标对象
        this.target_ele = ele;
        var myConfig = config || defConfig;
        var my = this;
        for (var i in defConfig) {
            my[i] = my.is_null(myConfig[i]) ? defConfig[i] : myConfig[i];
        }
        this.module_all_info = {};
        // 初始化
        my.init();
        $(window).resize(function () {
            if ($(window).outerWidth() < 975) {
                $("div[class*='move_outer']").css({"width": ""});
            }
        });
    };

    /* 计算最小宽度 保证标题不被压缩 */
    module_open.prototype.cud_min_width = function (module_outers) {
        var module_min_width = {};
        var my = this;
        if (!my.is_null(module_outers)) {
            for (var module_index = 0; module_index < module_outers.length; module_index++) {
                // 模块信息div
                var module = module_outers[module_index];
                var title = $(module).find("b[class*='module_title']");
                var siblings = $(title).siblings();
                var width = $(title).outerWidth();
                for (var i = 0; i < siblings.length; i++) {
                    width += $(siblings[i]).outerWidth();
                }
                var module_id = $(module).attr("data-ele-id");
                module_min_width[module_id] = width;
            }
        }
        return module_min_width;
    };


    /* 初始化模块的信息 */
    module_open.prototype.init = function () {
        var my = this;
        // 模块div
        var module_outers = $(my.target_ele).children().filter("div[class*='move_outer']");
        if (!my.is_null(module_outers)) {
            // 记录模块数
            var module_count = 0;
            for (var module_index = 0; module_index < module_outers.length; module_index++) {

                // 模块信息div
                var module = module_outers[module_index];
                // 模块id
                var module_id = $(module).attr("data-ele-id");
                // 初始化模块信息
                var module_info = {};
                if (my.is_null(module_id)) {
                    // 初始化模块信息
                    module_id = my.ele_uid();
                    //记录模块id
                    module_info.e_id = module_id;
                    // 用来存储 宽度 （百分比）
                    module_info.move_width = my.obtain_module_width($(module));
                    my.module_all_info[module_id] = module_info;
                }
                $(module).attr("data-ele-id", module_id);

                // 处理标题
                var title = $(module).find("b[class*='module_title']");
                if (!my.is_null(title)) {
                    // 事件绑定标识 防止重复绑定点击事件
                    var click_evn_flag = $(title).attr("data-open-evn");
                    if (my.is_null(click_evn_flag)) {
                        $(title).attr("data-open-evn", true);
                        /* 监听处理模块标题的点击事件 */
                        $(title).click(function (e) {
                            var module_div = $(this).parents().filter("div[class*='move_outer']");
                            var ele_id = $(module_div).attr("data-ele-id");
                            var status = $(module_div).attr("data-open-status");
                            my.large_click_module(module_div, status, ele_id);
                        });
                    }
                }
                module_count++;
            }
            // 重新计算最小的宽度
            this.min_width = my.cud_min_width(module_outers);
            this.fixed_min_width = {};
            this.module_count = module_count;
        }
    };

    /* 记录移动的数据 */
    module_open.prototype.record_module_move_info = function (ele_u_id, width) {
        var module_info = this.module_all_info[ele_u_id];
        if (this.is_null(module_info)) {
            module_info = {};
            module_info.e_id = ele_u_id;
        }
        module_info.move_width = width;
        this.module_all_info[ele_u_id] = module_info;
    };

    /* 放大/还原 点击的模块 */
    module_open.prototype.large_click_module = function (module) {
        var my = this;
        var ele_id = $(module).attr("data-ele-id");
        var status = $(module).attr("data-module-status");
        if (!my.is_null(status) && "enlarge" === status) {
            // 原来为扩大 现在变为还原  扩展开的需要首先收窄 不然会出现 闪现 换行
            var opened_module_info = my.module_all_info[ele_id];
            var opened_dom = $("div[data-ele-id=" + ele_id + "]");
            opened_dom.css({"width": opened_module_info.move_width * 100 + '%'});

            my.module_fold(ele_id);
            opened_dom.removeAttr("data-module-status");
            $(module).removeAttr("data-module-status");
        } else {
            var width = 8;
            var total_width = $(window).outerWidth();
            // 触发条件的限制
            if (total_width > 975) {
                var count = 0;
                // 折叠的模块
                for (var module_id in my.module_all_info) {
                    count++;
                    if (ele_id !== module_id) {
                        // 折叠的模块
                        var fold_dom = $("div[data-ele-id=" + module_id + "]");
                        if (isBlank(my.fixed_min_width[module_id])) {
                            var module_width = my.min_width[module_id] / total_width * 100;
                            if (module_width >= 8) {
                                width = (module_width).toFixed(0);
                            }
                            my.fixed_min_width[module_id] = width;
                        } else {
                            width = my.fixed_min_width[module_id]
                        }

                        fold_dom.removeAttr("data-module-status");
                        fold_dom.css({"width": width + '%'});
                    }
                }
                my.module_expand(ele_id);
            }
        }
    };

    /* 折叠调用 */
    module_open.prototype.module_fold = function (ele_id) {
        var my = this;
        for (var module_id in my.module_all_info) {
            if (ele_id !== module_id) {
                var module_info = my.module_all_info[module_id];
                var dom = $("div[data-ele-id=" + module_id + "]");
                dom.css({"width": module_info.move_width * 100 + '%'});
                dom.removeAttr("data-module-status");
            }
        }
    };


    /* 扩展调用 */
    module_open.prototype.module_expand = function (ele_id) {
        var my = this;
        var width = 100;
        // 扩展模块
        var enlarge_dom = $("div[data-ele-id=" + ele_id + "]");
        for (var module_id in my.fixed_min_width) {
            if (ele_id != module_id) {
                width = width - parseInt(my.fixed_min_width[module_id]);
            }
        }
        enlarge_dom.attr("data-module-status", "enlarge");
        enlarge_dom.css({"width": width + '%'});
    };

    /* 判断是否为空 */
    module_open.prototype.is_null = function (str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    };

    /* 产生唯一性 id */
    module_open.prototype.ele_uid = function () {
        return 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    /* 提取指定 module 对应的宽度百分比 （0-1）*/
    module_open.prototype.obtain_module_width = function (ele) {
        var total_width = $(this.target_ele).outerWidth();
        var this_width = $(ele).outerWidth();
        return (parseFloat(this_width) / parseFloat(total_width)).toFixed(2);
    };

    /* 重新渲染指定 模块(模块对象) */
    module_open.prototype.render = function () {
        var module_all_info = this.module_all_info;
        var my = this;
        if (my.is_null(module_all_info)) {
            // 为空的时候 重新初始化
            module_all_info = {};
            this.module_all_info = module_all_info;
        }
        my.init();
    };
    return module_open;
});