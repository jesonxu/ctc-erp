/** 在layui上增加部分仙姑需要的功能 **/
$(document).ready(function () {
    var edit_ele = $(".span_title_edit");
    // 清理掉原来的符号
    edit_ele.html("");
    edit_ele.click(function (e) {
        e = e || window.event;
        if (e.stopPropagation) { //W3C阻止冒泡方法
            e.stopPropagation();
        } else {
            e.cancelBubble = true; //IE阻止冒泡方法
        }
    });
    // 激活的第一级
    window.active_first = "";

    // 激活的第二级
    window.active_second = "";

    console.log("info");

    var left_icon =$(".span_title>i");
    left_icon.html("");
    left_icon.addClass("layui-icon-right");
    left_icon.click(function (e) {
        e = e || window.event;
        if (e.stopPropagation) { //W3C阻止冒泡方法
            e.stopPropagation();
        } else {
            e.cancelBubble = true; //IE阻止冒泡方法
        }
        var open = $(this).attr("open");
        if(!open){
            $(this).attr("open",false)
        }
        var title = $(this).parent();
        var classz = $(title).attr('class');
        var thisLeave = 0;
        if (!isNull(classz)){
            if (classz.indexOf("first") >= 0){
                thisLeave = 1;
                window.active_first = $($(this).parent()).attr("data_info");
                window.active_second =  null;
            } else if (classz.indexOf("second") >= 0) {
                thisLeave = 2;
                // 直接激活第一级不用管
                // window.active_first = $($(this).parent()).attr("data_info");
                window.active_second =  null;
            }else{
                thisLeave = 0;
            }
        }else{
            // 未知级别
            thisLeave = 0;
        }
        $(".active").removeClass("active");
        if(thisLeave != 1){
            var parents = $(this).parents();
            window.active_second =  $($(this).parent()).attr("data_info");
            for (var index =0;index < parents.size();index++){
                var temp_class = $(parents[index]).attr("class");
                if (!isNull(temp_class) && temp_class.indexOf("layui-colla-content") >= 0){
                    var brother = $(parents[index]).siblings();
                    for (var bIndex=0;bIndex< brother.size();bIndex++){
                        var b_temp_class = $(brother[bIndex]).attr("class");
                        if (!isNull(b_temp_class) && b_temp_class.indexOf("first_span_title")>=0){
                            $( brother[bIndex]).addClass("active");
                            // 第一级
                            window.active_first = $($(this).parent()).attr("data_info");
                            break;
                        }
                    }
                }
            }
        }
        $(title).addClass("active");
        var t = $(title).next();
        $(t).trigger("click");
        if(!$(this).attr("open")){
            $(this).attr("open",true);
            $(this).removeClass("layui-icon-right");
            $(this).addClass("layui-icon-down");
        }else{
            $(this).attr("open",false);
            $(this).removeClass("layui-icon-down");
            $(this).addClass("layui-icon-right");
        }
    });

    // 点击正文
    $(".span_title").click(function (e) {
        var classz = $(this).attr('class');
        var thisLeave = 0;
        if (!isNull(classz)){
            if (classz.indexOf("first") >= 0){
                thisLeave = 1;
                window.active_first = $(this).attr("data_info");
                window.active_second =  null;
            } else if (classz.indexOf("second") >= 0) {
                thisLeave = 2;
                window.active_second =  $(this).attr("data_info");
            }else{
                thisLeave = 0;
            }
        }else{
            // 未知级别
            thisLeave = 0;
        }
        $(".active").removeClass("active");
        if(thisLeave != 1){
            var parents = $(this).parents();
            for (var index =0;index < parents.size();index++){
                var temp_class = $(parents[index]).attr("class");
                if (!isNull(temp_class) && temp_class.indexOf("layui-colla-content") >= 0){
                    var brother = $(parents[index]).siblings();
                    for (var bIndex=0;bIndex< brother.size();bIndex++){
                        var b_temp_class = $(brother[bIndex]).attr("class");
                        if (!isNull(b_temp_class) && b_temp_class.indexOf("first_span_title")>=0){
                            $(brother[bIndex]).addClass("active");
                            window.active_first = $( brother[bIndex]).attr("data_info");
                            break;
                        }
                    }
                }
            }
        }
        $(this).addClass("active");
    });

    function isNull(str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    }
});