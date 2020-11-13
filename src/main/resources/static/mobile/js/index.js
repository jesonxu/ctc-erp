$(document).ready(function () {
    // 加载有可能会出现顺序异常
    layui.use(['layer', 'element'], function () {
        let layer = layui.layer;
        let items = $("#mobile-menu").find("li");
        console.log("获取的菜单项" + items.length);
        if (items.length > 0) {
            for (let itemIndex = 0; itemIndex < items.length; itemIndex++) {
                let item = items[itemIndex];
                $(item).unbind().bind("click", function (e) {
                    // 显示加载
                    let loadIndex = layer.load(1, {
                        shade: [0.1, '#fff']
                    });
                    let tag = $(this).attr("data-tag");
                    let loadUserRole = new LoadUserRole(this);
                    if (util.isNotNull(tag) && tag === "role") {
                        loadUserRole.render();
                    } else {
                        $(items).removeClass("active");
                        loadUserRole.close();
                        $(this).addClass("active");
                    }
                    // 根据点击 加载不同的控制台
                    let src = $(this).attr("data-src");
                    // console.log("切换工作台");
                    if (util.isNotNull(src)) {
                        // 改变url 重新加载
                        document.getElementById("my-view-frame").src = src;
                    }
                    layer.close(loadIndex);
                    e.stopPropagation();
                });
            }
        }
    });
});