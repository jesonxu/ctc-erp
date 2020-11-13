var form;

$(document).ready(function () {
    autoComplete();
    $("#customerSearch").click(function () {
        queryCustomer();
    });

    layui.use('form', function(){
        form = layui.form;
        form.render();
    });

    // 全选
    $("#check-all").click(function (e) {
        var checks =  $("input[type='checkbox']");
        checks.each(function () {
            $(this).attr('checked', true);
        });
        form.render();
    });

    // 反选
    $("#check-opposite").click(function (e) {
        $("input[type='checkbox']").each(function () {
            if ($(this).is(':checked')) {
                $(this).attr('checked', false);
            } else {
                $(this).attr('checked', true);
            }
        });
        form.render();
    });

    // 清空
    $("#clear-all").click(function (e) {
        $("input[type='checkbox']").each(function () {
            $(this).attr('checked', false);
        });
        form.render();
    });

    // 清空销售输入框
    $("#clearSaleman").click(function (e) {
        $("#salemanName").val('');
        $("#salemanId").val('');
        form.render();
    });

    // 清空目标销售输入框
    $("#clearTargetSaleman").click(function (e) {
        $("#targetSaleman").val('');
        $("#targetSalemanId").val('');
        form.render();
    });
});

// 模糊搜索客户
function queryCustomer() {
    var companyName = $('#companyName').val().trim();
    var userId = $('#salemanId').val().trim();

    if (companyName.length === 0 && userId.length === 0) {
        layer.msg('请输入公司名称或销售姓名');
        return;
    }
    $.ajax({
        url: '/customer/queryCustomerInfos.action?companyName=' + companyName + '&userId=' + userId,
        async: false,
        type: "POST",
        dataType: "json",
        success: function (resp) {
            var html = "";
            if (resp.code === 200) {
                var data = resp.data;
                if (data != null && data !== ""){
                    $.each(data, function (i, item) {
                        html += "<div class='layui-input-block customerList'><input type='checkbox' name='customer' lay-skin='primary' lay-filter='customer' value='" + item.id + "' title='" + item.name + "'/><div class='layui-inline' style='height: 0'><span class='layui-badge layui-bg-gray'>" + item.deptName + "/" + item.userName + "</span></div></div>";
                    });
                }
                if (data == null || "" === data) {
                    html ="<label>未查询到数据</label>";;
                }else{
                    $('.toolbar').show();
                }
                $('#customerList').html(html);
                form.render();
            } else {
                html += "<label>未查询到数据</label>";
                $('#customerList').html(html);
                $('.toolbar').hide();
            }
        }
    });
}

function autoComplete() {
    layui.config({
        base: '/common/js/'
    }).extend({ // 设定模块别名
        autocomplete: 'autocomplete'
    });
    layui.use('autocomplete', function () {
        var autocomplete = layui.autocomplete;
        // 自动补全原销售
        autocomplete.render({
            elem: $('#salemanName'),
            hidelem: $('#salemanId'),
            url: '/user/queryUserByAuto',
            template_val: '{{d.ossUserId}}',
            template_txt: '<div>{{d.realName}} <span class="layui-badge layui-bg-gray">{{d.deptName}}</span></div>',
            onselect: function (resp) {
                $("#salemanName").val(resp.realName);
                $("#salemanId").val(resp.ossUserId);
                queryCustomer();
            }
        });

        // 自动补全目标销售
        autocomplete.render({
            elem: $('#targetSaleman'),
            hidelem: $('#targetSalemanId'),
            url: '/user/queryUserByAuto',
            template_val: '{{d.ossUserId}}',
            template_txt: '<div>{{d.realName}} <span class="layui-badge layui-bg-gray">{{d.deptName}}</span></div>',
            onselect: function (resp) {
                $("#targetSaleman").val(resp.realName);
                $("#targetSalemanId").val(resp.ossUserId);
            }
        });
    });
}