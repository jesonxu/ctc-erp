$(document).ready(function () {
    layui.use(['jquery', 'element','layer'], function(){
        var $ = layui.$, element = layui.element;
        element.on('collapse(test)', function(data){
            var obj = data.title.find('svg use:first-child');
            if(data.show) {
                //面板展开
                obj.attr('href','#icon-offline');
            } else {
                //面板折叠
                obj.attr('href','#icon-addition');
            }
        });
    });
    // 工作台上显示气泡
    if (typeof loadConsoleFlowCount == 'function') {
        loadConsoleFlowCount();
    }
	if (typeof loadRoleFlowCount == 'function') {
		loadRoleFlowCount();
	}
    initCount();
});
