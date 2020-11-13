$(document).ready(function () {

    $('div[class*="move_outer"]').mousedown(function (event) {
        set_none_select(true);

        var min_width = 150;
        var total_width = $(this).parent().width() + 2;
        event = event || window.event;
        if (event.stopPropagation) { //W3C阻止冒泡方法
            event.stopPropagation();
        } else {
            event.cancelBubble = true; //IE阻止冒泡方法
        }
        var isMove = true;
        var ele = this;

        //当前的操作
        var this_width = $(this).outerWidth();
        // 开始的位置
        var before = event.pageX;

        // 后面一个
        var next = $(this).next();
        var next_width = $(next).outerWidth();
        var total = next_width + this_width;
        $(document).mousemove(function (event) {
            if (isMove) {
                // 改变的距离
                var change_width = event.pageX - before;
                // 要有可以变更的距离
                if (change_width > 0) {
                    // 向右 左边+ 右边-
                    if ((next_width - Math.floor(change_width)) > min_width) {
                        this_width = this_width + change_width;
                        next_width = total - this_width;
                        var total_percent = total / total_width * 100;
                        $(ele).css({
                            'width': (this_width / total_width) * 100 + "%"
                        });
                        $(next).css({
                            'width': (total_percent - (this_width / total_width) * 100) + "%"
                        });
                    }
                } else {
                    if ((this_width - Math.abs(change_width)) > min_width) {
                        this_width = this_width - Math.abs(change_width);
                        // 向左
                        next_width = total - this_width;
                        $(ele).css({
                            'width': (this_width / total_width) * 100 + "%"
                        });
                        $(next).css({
                            'width': (next_width / total_width) * 100 + "%"
                        });
                    }
                }
                before = event.pageX;
            }
        }).mouseup(function () {
            isMove = false;
            set_none_select(false);
        });
    });

    // 阻止点击到内部的
    $(".move_inner").mousedown(function (event) {
        event = event || window.event;
        if (event.stopPropagation) { //W3C阻止冒泡方法
            event.stopPropagation();
        } else {
            event.cancelBubble = true; //IE阻止冒泡方法
        }
    });
    
    // 放大/缩小箭头
	$('.move_outer').css('cursor', 'w-resize');
	$('.move_inner').css('cursor', 'default');

    function set_none_select(status) {
        if (status) {
            $("body").addClass("un_select");
        } else {
            $("body").removeClass("un_select");
        }
    }
});