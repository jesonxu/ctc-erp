layui.use(['layer', 'element'], function () {

    bindConsoleEvent();

    bindFloatBtn();

    /**
     * 绑定菜单的事件
     */
    function bindConsoleEvent() {
        let menus = $("#console-menus").find("div[data-url]");
        $(menus).each(function (index, menuEle) {
            $(menuEle).unbind("click").bind("click", function (event) {
                window.parent.location.href = $(this).attr('data-url');
            });
        });
    }

    /**
     * 绑定浮动菜单事件
     */
    function bindFloatBtn() {
        let floatBtn = new Float("div[data-float-btn='true']", "#add-menu-list");
        floatBtn.do([{
            icon: "layui-icon-engine",
            name: "流程",
            handle: function () {
                window.parent.location.href = "/mobile/toApplyFlowList";
            }
        },{
            icon: "layui-icon-template",
            name: "客户",
            handle: function () {
                window.parent.location.href = "/mobile/toAddCustomer";
            }
        },/* {
        icon: "layui-icon-app",
        name: "产品",
        handle: function () {
            layer.msg("敬请期待")
        }
    }, */{
            icon: "layui-icon-username",
            name: "客户联系人",
            handle: function () {
                window.parent.location.href = "/mobile/toAddCustomerContact";
            }
        },/* {
        icon: "layui-icon-cellphone-fine",
        name: "联系日志",
        handle: function () {
            layer.msg("敬请期待")
        }
    },*/ ]);
    }

});