layui.use(['laydate', 'layer', 'form', 'element'], function () {
    var laydate = layui.laydate;
    //注意：parent 是 JS 自带的全局对象，可用于操作父页面
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

    //取当天日期时间
    var date = new Date();
    var dataStart = dateFormatter(date);

    function dateFormatter(date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
    }

    //常规用法
    laydate.render({
        elem: '#recordtime',
        max: dataStart,
    });

    // 提交
    $("#supplier_submit").click(function (e) {
        var form_data = {
            supplierId: $("#supplierId").val(),
            recordtime: $("#recordtime").val(),
            contacts: $("#contacts").val(),
            contactsForm: $("#contactsForm").val(),
            content: $("#content").val(),
            result: $("#result").val()
        };
        if (!validation(form_data)) {
            return;
        }
        $.ajax({
            type: "POST",
            async: false,
            url: "/supplier/addContactLog.action?temp=" + Math.random(),
            dataType: 'json',
            data: form_data,
            success: function (data) {
                if (data.code == 200) {
                    window.parent.layer.msg("添加成功");
                    window.parent.load_contact_log_time($("#supplierId").val());
                    close_window();
                } else {
                    layer.msg(data.msg);
                }
            }
        });
    });

    // 校验参数
    function validation(param) {
        if (isNull(param.recordtime)) {
            layer.tips('日期不能为空', '#recordtime', {tips: 2});
            return false;
        }
        if (isNull(param.contacts)) {
            layer.tips('联系人不能为空', '#contacts', {tips: 2});
            return false;
        }
        if (isNull(param.contactsForm)) {
            layer.tips('联系方式不能为空', '#contactsForm', {tips: 2});
            return false;
        }
        if (isNull(param.content)) {
            layer.tips('联系内容不能为空', '#content', {tips: 1});
            return false;
        }
        if (isNull(param.result)) {
            layer.tips('工作成果不能为空', '#result', {tips: 1});
            return false;
        }
        return true;
    }

    // 取消
    $("#supplier_cancel").click(function (e) {
        close_window();
    });

    //关闭窗口
    function close_window() {
        parent.layer.close(index);
    }

    function isNull(str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    }

    /* function init_contact_log(supplier_id) {
         // 默认加载的时候，清空数据
         $("ul[supplier-contact-log-content=" + supplier_id + "]", parent.document).html("");
         var load_btn = $("button[contact-log-load-more=" + supplier_id + "]", parent.document);
         load_btn.text("点击加载更多");
         load_supplier_contact_log(supplier_id, 1, 3, true);

     }*/


    // 根据id加载供应商信息
    /*function load_supplier_contact_log(supplier_id, current_page, page_size, init) {
        $.ajax({
            type: "GET",
            async: false,
            url: "/supplier/readSupContactLogPageById/" + supplier_id +
                "/" + current_page +
                "/" + page_size + ".action?temp=" + Math.random(),
            dataType: "html",
            success: function (data) {
                var load_btn = $("button[contact-log-load-more=" + supplier_id + "]", parent.document);
                load_btn.show();
                //去掉最外层的标签
                if (data != null && data != undefined && data != "") {
                    $("ul[supplier-contact-log-content=" + supplier_id + "]", parent.document).append(data);
                    if (init) {
                        load_btn.attr("current-page", 2);
                        load_btn.attr("page-size", 3);
                    } else {
                        var page = parseInt(current_page) + 1;
                        load_btn.attr("current-page", page);
                    }
                } else {
                    $("ul[supplier-contact-log-content=" + supplier_id + "]", parent.document).append(
                        "<li class='layui-timeline-item'><i class='layui-icon layui-timeline-axis'></i>" +
                        "<div class='layui-timeline-content layui-text'>哎呀，没有了</div></li>");
                    load_btn.text("没有更多日志");
                    load_btn.hide();
                }
                close_window();
            }
        });
    }*/

});