/**
 * 浮动可移动工具
 */
(function (window, factory) {
    window.Float = factory();
})(window, function () {
    let Float = function (triggerEle, showEle) {
        this.moveSize = 15;
        if (util.isNull(triggerEle) || triggerEle.length === 0) {
            throw new Error("未知渲染对象");
        }
        this.ele = triggerEle;
        this.eleMenuId = util.uuid();
        $(this.ele).attr("data-menu-list-id", this.eleMenuId);
        if (util.isNotNull(showEle) && showEle.length > 0) {
            this.showEle = showEle;
        } else {
            this.showEle = $("#add-menu-list");
        }
        if (util.isNull(this.showEle) || this.showEle.length === 0) {
            throw new Error("未知展示区域");
        }
    };

    /**
     * 开始渲染
     * @param menuItems
     * @returns {string}
     */
    Float.prototype.do = function (menuItems) {
        if (util.arrayNull(menuItems)) {
            return "";
        }
        let mine = this;
        this.renderMenuItems(menuItems);
        $(this.ele).unbind().bind("click", function () {
            let menuListId = $(this).attr("data-menu-list-id");
            let itemShow = $(this).attr("item-show");
            if (util.isTrue(itemShow)) {
                $(this).attr("item-show", false);
                $(mine.showEle).fadeOut();
                mine.showBg(false);
            } else {
                $(this).attr("item-show", true);
                $(mine.showEle).fadeIn();
                mine.showBg(true);
            }
        });
    };

    /**
     * 展示背景
     * @param show 展示
     */
    Float.prototype.showBg = function (show) {
        // 展示 存在就删除
        $("#" + this.eleMenuId).remove();
        let mine = this;
        if (util.isTrue(show)) {
            // 新增
            $(this.showEle).after("<div id='" + this.eleMenuId + "' class='menu-list-bg'></div>");
            $("#" + this.eleMenuId).click(function () {
                // 点击事件
                $(mine.ele).attr("item-show", false);
                $(mine.showEle).fadeOut();
                $(this).remove();
            });
        }
    };

    /**
     * 渲染菜单项
     * @param menuItems
     */
    Float.prototype.renderMenuItems = function (menuItems) {
        if (util.arrayNotNull(menuItems)) {
            let menus = [];
            let menuMap = {};
            for (var menuIndex = 0; menuIndex < menuItems.length; menuIndex++) {
                var menuItem = menuItems[menuIndex];
                var itemId = util.uuid();
                menus.push("<li data-id='" + itemId + "' class='layui-anim'>" +
                    "<span class='menu-name'>" + menuItem.name + "</span>" +
                    "<span class='menu-item-icon layui-icon " + menuItem.icon + "'></span>" +
                    "</li>");
                menuMap[itemId] = menuItem;
            }
            $(this.showEle).hide();
            $(this.showEle).html(menus.join(""));
            // 绑定事件
            for (let menuId in menuMap) {
                $("li[data-id='" + menuId + "']").unbind().bind("click", function () {
                    // 绑定回调
                    let menuItem = menuMap[menuId];
                    if (util.isNotNull(menuItem) && typeof menuItem.handle === "function") {
                        menuItem.handle();
                    }
                    $(this).toggleClass("layui-anim-scaleSpring");
                    var item = this;
                    setTimeout(function () {
                        $(item).removeClass("layui-anim-scaleSpring");
                    }, 500);
                });
            }
        }
    };
    return Float;
});