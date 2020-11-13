/**
 * 流程操作工具
 */
(function (window, factory) {
    window.rejectSelect = factory();
})(window, function () {

    let rejectSelect = function () {

    };

    /**
     * 驳回
     * @returns {string}
     */
    rejectSelect.prototype.rejectTo = function (bindEle, callback) {
        $(bindEle).unbind().bind("touchstart", function (e) {
            let Y = $(this).offset().top;
            let X = $(this).offset().left;
            let width = $(this).width();
            let height = $(this).height();
            var displayEle = $(this).next();
            $(displayEle).css({
                "top": parseFloat(Y) + parseFloat(height),
                "left": X
            });
            $(displayEle).toggleClass("reject-display");
        });
        // 角色列表
        var rejectRoleEle = $(bindEle).next();
        var flowEntId = $(rejectRoleEle).attr("data-reject-flow-ent-id");
        if (util.isNull(flowEntId)) {
            return;
        }
        var roleEleList = $(rejectRoleEle).find("dd");
        if (roleEleList.length > 0) {
            for (var roleEleIndex = 0; roleEleIndex < roleEleList.length; roleEleIndex++) {
                let rejectRoleItem = roleEleList[roleEleIndex];
                $(rejectRoleItem).unbind("click").bind("click", function () {
                    let nodeName = $(this).attr("data-node-name");
                    let roleName = $(this).attr("data-role-name");
                    let eleId = $(this).attr("data-ele-id");
                    let nodeIndex = $(this).attr("data-index");
                    if (typeof callback === "function") {
                        callback(nodeName, roleName, eleId, nodeIndex);
                    }
                    $(rejectRoleEle).toggleClass("reject-display");
                });
            }
        }
    };

    return new rejectSelect();
});