'use strict';
var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) {
    return typeof obj;
} : function (obj) {
    return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
};


(function (window, factory) {
    window.myPannel = factory();
})(window, function () {
    //针对IE的一些处理
    if (window.Map == undefined) {
        var _Map = function _Map() {
            this.value = {};
        };
        _Map.prototype.set = function (key, val) {
            this.value[key] = val;
        };
        _Map.prototype.get = function (key) {
            return this.value[key];
        };
        _Map.prototype.has = function (key) {
            return this.value.hasOwnProperty(key);
        };
        _Map.prototype.delete = function (key) {
            delete this.value[key];
        };
        window.Map = _Map;
    }

    var defconfig = {
        right_icon: "layui-icon-tips", // 默认编辑
        left: function (item, item_id) {
            return true;
        },
        right: function (item, item_id) {
            return true;
        },
        middle: function (item, item_id) {
            return true;
        },
        openItem: function (item, itemId, optsType) {
            return true;
        },
        closeItem: function (item, itemId, optsType) {
            return true;
        }
    };

    // 初始化对象
    var myPannel = function (config) {
        var myConfig = config || defconfig;
        var my = this;
        $.each(defconfig, function (e) {
            my[e] = my.isNull(myConfig[e]) ? defconfig[e] : myConfig[e];
        });
    };

    // 初始化
    myPannel.prototype.init = function (selector) {
        if (this.isNull(selector)) {
            console.log("找不到指定的对象");
            return false;
        }
        this.selector = selector;
        // 保存原有的html
        this.origin = $(selector).html();
        this.dealEle(selector);
    };

    // 一个开始
    myPannel.prototype.dealEle = function (selector) {
        var ele = this.findEle(selector);
        var ele_start = ele.children();
        // 折叠筐开始 只有一个
        var start = ele_start.filter(".layui-collapse");
        if (!this.isNull(start)) {
            var items = start.children().filter(".layui-colla-item");
            this.dealItems(items);
        }
    };

    //批量处理选项
    myPannel.prototype.dealItems = function (items) {
        if (this.isNull(items) || items.size() <= 0) {
            return false;
        }
        var temp = this;
        items.each(function (item) {
            temp.dealItem(this);
        });
    };

    // 处理具体选项
    myPannel.prototype.dealItem = function (item) {
        var childs = $(item).children();
        // 正式的标签头（只有一个）
        var titles = childs.filter(".layui-colla-title");
        if (!this.isNull(titles) && titles.size() > 0) {
            this.resetTitle(item, titles);
        }
        // 标签的内容（只有一个）
        var contents = childs.filter(".layui-colla-content");
        if (!this.isNull(contents) && contents.size() > 0) {
            this.dealContent(contents);
        }
    };

    // 处理内容
    myPannel.prototype.dealContent = function (content) {
        if (this.isNull(content) || content.size() <= 0) {
            return false;
        }
        var next = $(content).children().filter(".layui-collapse");
        if (!this.isNull(next) && next.size() > 0) {
            // 开启处理下面的 折叠筐
            this.dealEle(content);
        }
    };

    // 重置标签头 批量处理
    myPannel.prototype.resetTitle = function (item, titles) {
        if (this.isNull(titles) || titles.size() <= 0) {
            return false;
        }
        // 设置原有的不展示
        $(titles).css("display", "none");
        $(titles).children().filter("i").text("");
        // 获取原有的标题
        var title_content = $(titles).text();
        // 原有div
        var my_title = $(item).children().filter(".layui-colla-title");
        // 右侧工具点击事件(是否需要)
        var right_tool = $(my_title).attr("data-my-right-tool");
        var title_size = $(my_title).attr("data-my-size");
        var flow_ent_count = $(my_title).attr("flow_ent_count");
        title_size = this.trim(title_size) == null ? "" : title_size;

        // 重新加入标题内容
        var mytitle = '<div class="my_span_title ' + title_size + '" style="padding-right: 0px">' + '<span class="my_text_title">';
        mytitle = mytitle + title_content + '</span>';
        // 是否为文件夹 默认都是文件夹
        var data_my_folder = $(my_title).attr("data-my-folder");
        var left_flag_show= '';
        if (!this.isTrue(data_my_folder,true)){
            left_flag_show = 'style="display:none" ';
        }
        // 加左侧展开
        mytitle = mytitle + '<i class="layui-icon layui-colla-icon layui-icon-right" '+left_flag_show+' data-my-left-flag="true"></i>';

        // 右边的icon
        var right_tool_icon = $(my_title).attr("data-my-tool-right-icon");
        if (this.isTrue(right_tool)) {
            // 加入右侧按钮
            var icon = this.trim(right_tool_icon) == null ? this.right_icon : right_tool_icon;
            var tips = '';
            if (typeof icon == 'string' && icon.indexOf('add') >= 0) {
                tips = 'title="添加"';
            } else if (typeof icon == 'string' && icon.indexOf('edit') >= 0) {
                tips = 'title="修改"';
            }
            mytitle += '<span class="layui-icon span_title_edit ' + icon + '" ' + tips + '></span>';
        }
        if (!this.isNull(flow_ent_count) && parseInt(flow_ent_count) > 0) {
            // 增加右侧数字展示
            mytitle = mytitle + '<span class="layui-badge" style="float: right">' + flow_ent_count + '</span>'
        }
        mytitle += '</div>';
        $(item).prepend(mytitle);
        // 增加事件
        this.myEvent(item);
    };

    // 处理自己的事件
    myPannel.prototype.myEvent = function (item) {
        var temp = this;
        var my_title = $(item).children().filter(".layui-colla-title");

        // 唯一标识
        var item_id = $(my_title).attr("data-my-id");
        // 点击左边打开
        var open_left = $(my_title).attr("data-my-open-left");
        // 右侧工具点击事件(是否需要)
        var right_tool = $(my_title).attr("data-my-right-tool");
        // 中间点击事件
        var title_tool = $(my_title).attr("data-my-title-tool");
        // 操作类型
        var data_my_opts_type = $(my_title).attr("data-my-opts-type");
        // tag标识
        var data_my_tag = $(my_title).attr("data-my-tag");
        // 自定义的title
        var my_span_title = $(item).children().filter(".my_span_title");
        // 转移属性（为了支持事件绑定）
        my_span_title.attr({
            "data-my-id": item_id,
            "data-my-opts-type": data_my_opts_type,
            "data-my-tag": data_my_tag
        });

        // 是否为文件夹
        var data_my_folder = $(my_title).attr("data-my-folder");
        if (!this.isNull(data_my_folder)) {
            my_span_title.attr("data-my-folder", data_my_folder);
        }

        if (this.isTrue(title_tool)) {
            my_span_title.attr("data-my-title-tool", title_tool);
        }
        if (this.isTrue(right_tool)) {
            my_span_title.attr("data-my-right-tool", right_tool);
        }

        // 删除属性
        my_title.removeAttr("data-my-id");
        my_title.removeAttr("data-my-opts-type");
        my_title.removeAttr("data-my-title-tool");
        my_title.removeAttr("data-my-right-tool");
        my_title.removeAttr("data-my-tag");
        my_title.removeAttr("data-my-size");
        my_title.removeAttr("title_size");
        my_title.removeAttr("data-my-folder");
        // 展开
        if (this.isTrue(open_left)) {
            var left_tool_ele = $(my_span_title).children().filter("i");
            $(left_tool_ele).addClass("my_i_left_tool");
            //查找左边（给i 标签加上点击事件）
            left_tool_ele.click(function (e) {
                // 绑定open 事件
                temp.open(e, item, my_span_title);
            });
        } else {
            $(my_span_title).click(function (e) {
                temp.open(e, item, my_span_title);
            });
        }

        // 右侧工具栏（不管选中）
        if (this.isTrue(right_tool)) {
            $(my_span_title).children().filter(".span_title_edit").click(function (e) {
                e = e || window.event;
                if (e.stopPropagation) { //W3C阻止冒泡方法
                    e.stopPropagation();
                } else {
                    e.cancelBubble = true; //IE阻止冒泡方法
                }
                var optsType = $(my_span_title).attr("data-my-opts-type");
                // 右侧工具点击事件
                temp.right(item, item_id, optsType);
            });
        }

        // 点击中间事件(需要显示选中样式)
        if (this.isTrue(title_tool)) {
            // 添加选中事件
            $(my_span_title).click(function (e) {
                e = e || window.event;
                if (e.stopPropagation) { //W3C阻止冒泡方法
                    e.stopPropagation();
                } else {
                    e.cancelBubble = true; //IE阻止冒泡方法
                }
                $(item).siblings().find("div[class*='my_active']").removeClass("my_active");
                $(this).addClass("my_active");
            });

            //查找左边（给i 标签加上点击事件）
            $(my_span_title).children().filter("span").click(function (e) {
                var optsType = $(my_span_title).attr("data-my-opts-type");
                // 不阻止事件冒泡
                temp.middle(item, item_id, optsType);
            });
        }
    };

    // 重新加载
    myPannel.prototype.reload = function () {
        $(this.selector).empty();
        $(this.selector).html(this.origin);
        this.init(this.selector);
    };

    // 设置选中
    myPannel.prototype.myChoosed = function () {
        // 初始化
        var all_down = $(this.selector).find(".layui-icon-down");
        all_down.removeClass("layui-icon-down");
        all_down.addClass("layui-icon-right");
        var origin_show = $(this.selector).find(".layui-show");
        $(this.selector).find(".my_active").removeClass("my_active");
        $.each(origin_show, function (e) {
            var titles = $(this).siblings().filter(".my_span_title");
            titles.addClass("my_active");
            var left_icon = titles.children().filter("i");
            $(left_icon).removeClass("layui-icon-right");
            $(left_icon).addClass("layui-icon-down");
        });
    };

    // 点击展开
    myPannel.prototype.open = function (e, item, my_span_title) {
        e = e || window.event;
        if (e.stopPropagation) { //W3C阻止冒泡方法
            e.stopPropagation();
        } else {
            e.cancelBubble = true; //IE阻止冒泡方法
        }
        // 左边的标识（左右箭头）
        var left_flag = $(item).children().filter(".my_span_title").children().filter("i[data-my-left-flag='true']");
        var open = $(left_flag).attr("data-my-open");
        if (this.isNull(open)) {
            open = false;
        }

        // 原来的title
        var origin_title = $(item).children().filter(".layui-colla-title");
        // 触发原有的事件，保持折叠展示
        $(origin_title).trigger("click");

        // 文件夹配置
        var folder = $($(item).children().filter("div[class*='my_span_title']")).attr("data-my-folder");
        if (this.isTrue(folder, true)) {
            if (this.isTrue(open)) {
                // 打开变成关闭
                $(left_flag).removeClass("layui-icon-down");
                $(left_flag).addClass("layui-icon-right");
                $(left_flag).attr("data-my-open", false);
                $(my_span_title).attr("data-my-open", false);
            } else {
                // 关闭变成打开
                $(left_flag).removeClass("layui-icon-right");
                $(left_flag).addClass("layui-icon-down");
                $(left_flag).attr("data-my-open", true);
                $(my_span_title).attr("data-my-open", true);
            }
            this.set_brother_close(item);
        } else {
            // 直接隐藏对应的标签  data-my-left-flag="true"
            $(left_flag).hide();
        }
        var itemId = $(my_span_title).attr("data-my-id");
        var optsType = $(my_span_title).attr("data-my-opts-type");
        if (!this.isTrue(open)) {
            this.openItem(item, itemId, optsType);
        } else {
            this.closeItem(item, itemId, optsType);
        }
        this.myChoosed();
    };

    // 查找对象
    myPannel.prototype.set_brother_close = function (item) {
        var my = this;
        var all_items = $(item).siblings();
        if (!my.isNull(all_items)) {
            for (var index = 0; index < all_items.length; index++) {
                var item = all_items[index];
                $(item).find("i[class*='layui-colla-icon']").attr("data-my-open",false);
            }
        }
    };

    // 查找对象
    myPannel.prototype.findEle = function (selector) {
        return $(selector);
    };

    // 判断是否为空
    myPannel.prototype.isNull = function (str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    };

    // 判断是否为空
    myPannel.prototype.trim = function (str) {
        if (this.isNull(str)) {
            return null;
        } else {
            return str;
        }
    };

    // 判断是否为空
    myPannel.prototype.isTrue = function (str, defaultResult) {
        if (this.isNull(str)) {
            if (!this.isNull(defaultResult)) {
                return defaultResult;
            }
            return false;
        } else if (str instanceof Boolean) {
            return str;
        } else {
            return str === 'true' || str === "true";
        }
    };
    return myPannel;
});