/**
 * 页面返回
 */
(function (window, factory) {
    window.pageBack = factory();
})(window, function () {
    let pageBack = function () {

    };

    pageBack.prototype.defaultBind = function () {
        $("span[class*='page-title-back']").unbind("touchstart").bind("touchstart", function (event) {
            // 跳转页面（暂时用这种方式返回）
            window.history.back();
            // window.location.href = "/mobile/toIndex";
        });
    };

    /**
     * 重新绑定
     **/
    pageBack.prototype.rebind = function (url, param) {
        // 暂时不实现
    };

    return new pageBack();
});
/**
 * 进行默认绑定
 **/
$(document).ready(function () {
    pageBack.defaultBind();
});