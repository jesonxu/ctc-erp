var laydate;
var layer;
var element;
var form;
var product_type_info = [];
$(document).ready(function () {
    layui.use(['laydate', 'layer', 'form', 'element'], function () {
        laydate = layui.laydate;
        layer = layui.layer;
        form = layui.form;
        element = layui.element;
        load_product_type();
        init_search_btn();
        init_reset_btn();
    });
});

var tableIns;

// 初始化表格
function init_table(parameterType) {
    layui.use(['table'], function () {
        var table = layui.table;
        //列表
        tableIns = table.render({
            elem: '#parameterList',
            url: "/parameter/readPages.action?temp=" + Math.random(),
            request: {
                pageName: 'currentPage',
                limitName: 'pageSize'
            },
            height: 'full-150',
            toolbar: '#table-opts',
            defaultToolbar: [],
            even: true,
            page: true,
            limit: 10,
            limits: [10, 30, 50, 100],
            method: 'POST',
            cols: [[
                {
                    type: 'numbers',
                    fixed:'left'
                }, {
                    field: 'entityid',
                    title: 'entityid',
                    align: 'center',
                    hide: true
                }, {
                    field: 'paramkey',
                    title: '参数名',
                    align: 'center',
                    fixed:'left',
                    width: '10%',
                    minWidth: 100,
                    templet: function (d) {
                        if (d.paramType === 2 || d.paramType === "2" || d.paramType === 4 || d.paramType === "4") {
                            return product_type_info[d.paramkey];
                        } else {
                            return d.paramkey;
                        }
                    }
                }, {
                    field: 'paramvalue',
                    title: '参数值',
                    align: 'left',
                    width: '20%'
                }, {
                    field: 'paramType',
                    title: '参数类型',
                    align: 'center',
                    width: '10%',
                    templet: function (d) {
                        return parameterType[d.paramType];
                    }
                }, {
                    field: 'depict',
                    title: '参数描述',
                    align: 'left',
                    width: '15%'
                }, {
                    field: 'extended',
                    title: '扩展描述',
                    align: 'left',
                    width: '25%',
                    templet: function (d) {
                        if (isBlank(d.extended)) {
                            return "";
                        }
                        return HTMLEncode(d.extended);
                    }
                }, {
                    field: 'wtime',
                    title: '创建时间',
                    align: 'center',
                    width: '10%'
                }, {
                    title: '操作',
                    width: '10%',
                    align: 'center',
                    fixed: 'right',
                    minWidth: 100,
                    toolbar: '#table-row-opts'
                }
            ]]
            , parseData: function (res) {
                return {
                    "code": 0,
                    "count": res.data.count,
                    "data": res.data.data
                };
            }
        });

        // 点击行选中
        table.on('row(parameterList)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });
        // 表格行 操作按钮
        table.on('tool(parameterList)', function (obj) {
            var data = obj.data;
            var id = data.entityid;
            if (obj.event === 'update') {
                edit_parameter(id);
            } else if ("delete".equals(obj.event)) {
                del_parameter(id);
            } else {
                console.error("未知操作");
            }
        });
        // 表头操作按钮
        table.on('toolbar(parameterList)', function (obj) {
            var checkStatus = table.checkStatus(obj.config.id);
            if ("add".equals(obj.event)) {
                add_parameter();
            } else {
                console.log("未知操作");
            }
        });
    });
}


// 加载产品类型
function load_product_type() {
    $.ajax({
        type: "POST",
        url: "/parameter/readProductType.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                product_type_info = data.data;
                init_parameter_type_select(init_table);
            } else {
                layer.msg(data.msg);
            }
        }
    });
}


function HTMLEncode(html) {
    var temp = document.createElement("div");
    (temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
    var output = temp.innerHTML;
    temp = null;
    return output;
}

// 添加参数
function add_parameter() {
    layer.open({
        type: 2,
        area: ['650px', '465px'],
        fixed: false, //不固定e
        maxmin: true,
        content: '/parameter/toParameterEdit',
    });
}

// 编辑参数
function edit_parameter(id) {
    layer.open({
        type: 2,
        area: ['650px', '465px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/parameter/toParameterEdit?id=' + id
    });
}

// 删除参数
function del_parameter(id) {
    layer.confirm('<span style="color: #FF0000">删除此系统参数可能会引起异常，您确定删除？</span>', {
        btn: ['确认', '取消'],
        icon: 2,
        title: '警告'
    }, function () {
        $.ajax({
            type: "POST",
            url: "/parameter/delParameter.action",
            dataType: 'json',
            data: {parameterId: id},
            success: function (data) {
                if (200 !== data.code) {
                    return layer.msg(data.msg, {icon: 2});
                }
                if (data.data === true || data.data === 'true') {
                    layer.msg("删除成功", {icon: 1});
                } else {
                    layer.msg("删除失败", {icon: 2});
                }
                reload_table();
            }
        });
    });
}

// 初始化参数类型
function init_parameter_type_select(call_back) {
    $.ajax({
        type: "POST",
        url: "/parameter/readParameterTypes.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<option value=''>请选择参数类型</option>";
                for (var key in types) {
                    content += "<option value=" + key + ">" + types[key] + "</option>";
                }
                $("#parameterType").html(content);
                form.render('select');
                if (typeof call_back == "function") {
                    call_back(types);
                }
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}

// 初始化查询按钮
function init_search_btn() {
    $("#btn-search").click(function () {
        reload_table();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn() {
    $("#btn-reset").click(function () {
        $("#parameterName").val("");
        $("#parameterType").val("");
        form.render('select');
    });
}

// 重新加载表格
function reload_table() {
    tableIns.reload({
        url: "/parameter/readPages.action?temp=" + Math.random(),
        where: {
            parameterName: $("#parameterName").val(),
            parameterType: $("#parameterType").val()
        }
    });
}