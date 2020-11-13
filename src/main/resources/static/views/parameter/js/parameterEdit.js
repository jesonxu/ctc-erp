var laydate;
var layer;
var element;
var form;
var index = parent.layer.getFrameIndex(window.name);
layui.use(['laydate', 'layer', 'form', 'element'], function () {
    laydate = layui.laydate;
    layer = layui.layer;
    form = layui.form;
    element = layui.element;
    load_parameter_type();
    //监听提交
    form.on('submit(formDemo)', function (data) {
        console.log(data);
        submit_data(data.field);
        return false;
    });
});

// 提交数据
function submit_data(params) {
    $.ajax({
        type: "POST",
        url: "/parameter/addOrEdit.action",
        dataType: 'json',
        data: params,
        success: function (data) {
            if (data.code === 200 && (data.data === true || data.data === "true")) {
                parent.layer.msg("操作成功");
                parent.reload_table();
                parent.layer.close(index);
            } else {
                layer.msg(data.msg, {icon: 2});
            }
        },
        error: function () {
            layer.msg("系统内部错误", {icon: 2});
        }
    });
}

function load_parameter_type() {
    var type = null;
    if (isNotBlank(window.parameter)) {
        type = window.parameter.paramType + "";
    }
    $.ajax({
        type: "POST",
        url: "/parameter/readParameterTypes.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<option value=''>请选择参数类型</option>";
                for (var key in types) {
                    if (isNotBlank(type) && (type == key)) {
                        content += "<option selected value=" + key + ">" + types[key] + "</option>";
                    } else {
                        content += "<option value=" + key + ">" + types[key] + "</option>";
                    }
                }
                $("#paramType").html(content);
                form.render('select');
                parameter_type_change();
                check_parameter_name();
            }
        }
    });
}

// 产品类型改变
function parameter_type_change() {
    form.on('select(paramType)', function (data) {
        var choosed_type = data.value;
        if (isNotBlank(choosed_type)) {
            if (choosed_type === 2 || choosed_type === "2" || choosed_type === 4 || choosed_type === "4") {
                // 提成比例
                load_product_type();
            } else {
                // 默认用原来的
                $("#parameter-type-name").html(" <input type='text' id='paramkey' name='paramkey' required" +
                    " lay-verify='required' placeholder='请输入系统参数名称' autocomplete='off' class='layui-input'/>");
            }
        }
    });
}

// 加载产品类型
function load_product_type() {
    var type = null;
    if (isNotBlank(window.parameter)) {
        type = window.parameter.paramkey;
    }
    $.ajax({
        type: "POST",
        url: "/parameter/readProductType.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<select id='paramkey' name='paramkey' lay-filter='paramkey' >";
                for (var key in types) {
                    if (isNotBlank(type) && type == key) {
                        content += "<option selected value=" + key + ">" + types[key] + "</option>";
                    } else {
                        content += "<option value=" + key + ">" + types[key] + "</option>";
                    }
                }
                content +="</select>";
                $("#parameter-type-name").html(content);
                form.render('select');
            }
        }
    });
}

// 检查参数类型和名称（确保对应）
function check_parameter_name() {
    // 类型
    var paramType = $("#paramType").val();
    if (paramType === 2 || paramType === "2" ){
        load_product_type();
    }
}