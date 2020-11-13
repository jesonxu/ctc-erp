(function (window, factory) {
    window.LoadUserRole = factory();
})(window, function () {
    /**
     * 加载用户角色
     */
    let LoadUserRole = function (target) {
        let dataTag = $(target).attr("data-tag");
        if (util.isNotNull(dataTag) && dataTag === "role") {
            this.targetEle = target;
        } else {
            let mineItems = $(target).parent().find("li[data-tag='role']");
            if (util.isNotNull(mineItems) && mineItems.length > 0) {
                this.targetEle = mineItems[0];
            } else {
                throw new Error("无法确认，角色绑定位置");
            }
        }
        var roleEle = $(this.targetEle).find("dl[data-role='menu-role-list']");
        if (util.isNull(roleEle) || roleEle.length === 0) {
            throw new Error("无法确认，内容渲染位置");
        }
        this.roleEle = roleEle;
        this.dataTag = dataTag
    };

    /**
     * 渲染用户角色信息
     */
    LoadUserRole.prototype.render = function () {
        // console.log("渲染用户的角色信息===");
        let close = this.close();
        if (close) {
            // 关闭的话 不用再加载
            return;
        }
        $(this.targetEle).addClass("active");
        let my = this;
        // 获取用户角色
        $.ajax({
            url: "/mobile/getUserRole",
            dataType: "json",
            method: "POST",
            success: function (data) {
                if (data.code === 200) {
                    let roleItem = [];
                    let roles = data.data;
                    for (let index = 0; index < roles.length; index++) {
                        let role = roles[index];
                        let now = "";
                        if (role.now === 1 || role.now === "1") {
                            now = "data-role-now";
                        }
                        roleItem.push("<dd data-id='" + role.id + "' " + now + ">" + role.name + "</dd>");
                    }
                    my.roleEle.html(roleItem.join(""));
                    let bgId = util.uuid();
                    this.bgId = bgId;
                    // 增加背景阴影（点击其他地方可以直接关闭）
                    $("body").append("<div class='menu-item-list-bg' id='user-role-list-bg-" + bgId+ "'></div>");
                    $("#user-role-list-bg-" + bgId).unbind().bind("click", function () {
                        $(this).remove();
                        my.close();
                    });
                    // 展示
                    my.roleEle.show();
                    // 打开的
                    $(my.targetEle).attr("data-open","open");
                    // 绑定 事件
                    my.roleEle.find("dd").click(function (e) {
                        my.changeUserRole(this);
                        e.stopPropagation();
                    });
                } else {
                    layer.msg(data.msg);
                }
            },
            error: function (data) {
                console.log("流程详情加载异常", data);
                layer.msg("加载用户角色信息错误");
            }
        });
    };

    /**
     * 改变用户的角色
     * @param e
     */
    LoadUserRole.prototype.changeUserRole = function (e) {
        let my = this;
        let roleId = $(e).attr("data-id");
        $.ajax({
            url: "/mobile/changeUserRole",
            dataType: "json",
            method: "POST",
            data: {
                roleId: roleId
            },
            success: function (data) {
                if (data.code === 200) {
                    // 设置系统角色
                    window.userRoleId = roleId;
                    // 重新加载嵌套的页面
                    document.getElementById("my-view-frame").contentWindow.location.reload(true);
                    // 重新加载角色(直接就关闭了，原来是打开的)
                    my.render();
                } else {
                    layer.msg(data.msg);
                }
            },
            error: function (data) {
                console.log("流程详情加载异常", data);
                layer.msg("切换用户角色信息错误");
            }
        });
    };

    /**
     * 关闭（移除）
     * @returns {boolean} 是否关闭
     */
    LoadUserRole.prototype.close = function () {
        let menuItem = this.targetEle;
        let open = $(menuItem).attr("data-open");
        $(menuItem).removeAttr("data-open");
        $(menuItem).removeClass("active");
        if (util.isNotNull(open) && open === "open") {
            let roleEle = $(menuItem).find("dl[data-role='menu-role-list']");
            roleEle.hide();
            // 防止后台改变了角色 ，但是前台无法点击加载新的角色列表
            roleEle.html("");
            $(".menu-item-list-bg").remove();
            return true;
        }
        return false;
    };

    /**
     * 获取选择了的用户角色对象
     * @returns {jQuery|HTMLElement|jQuery._attributes|undefined|*|null}
     */
    LoadUserRole.prototype.getUserNowRoleId = function () {
        var roleEle = $(this.targetEle).find("dd[data-role-now]");
        var roleId = "";
        if (util.isNotNull(roleEle) && roleEle.length >0){
            roleId = $(roleEle).attr("data-id");
        }
        if (util.isNull(roleId)){
            roleId = "";
        }
        return roleId;
    };

    /**
     * 获取用户当前角色名称
     * @returns {string}
     */
    LoadUserRole.prototype.getUserNowRoleName = function () {
        var roleEle = $(this.targetEle).find("dd[data-role-now]");
        var roleName = "";
        if (util.isNotNull(roleEle) && roleEle.length >0){
            roleName = $(roleEle).text();
        }
        if (util.isNull(roleName)){
            roleName = "";
        }
        return roleName;
    };

    return LoadUserRole;
});