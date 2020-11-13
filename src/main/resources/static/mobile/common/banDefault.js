document.write("<script type='application/javascript' src='/common/js/ua-parser.js'></script>");
layui.use(['layer', 'element', 'laydate', 'flow'], function () {
    let layer = layui.layer;
    // 只允许移动端进行访问
    // onlyAccessOnMobile();

    /**
     * 提示只能在移动端进行访问
     */
    function onlyAccessOnMobile() {
        let parser = new UAParser();
        let deviceInfo = parser.getResult().device;
        if (util.isNull(deviceInfo.type) || deviceInfo.type !== "mobile") {
            layer.msg("请在移动端进行访问");
            setTimeout(function () {
                // 返回到首页
                window.location.href = "/erp";
            }, 1000);
        }
    }
});

