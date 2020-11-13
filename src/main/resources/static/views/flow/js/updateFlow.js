var labelArr = [];
var layer;
var form;
var element;
var formSelects;
// 记录阈值数量
var threshold_count = 1;
$(document).ready(function () {
    layui.use(['laydate', 'layer', 'form', 'element', 'upload'], function () {
        laydate = layui.laydate;
        layer = layui.layer;
        form = layui.form;
        element = layui.element;
        formSelects = layui.formSelects;
        var upload = layui.upload;
        var methodtype = $("#methodtype").val();
        initTable();
        initBtum(methodtype);
        initSelect();
        initFlowType();
        initFlowClass();
        // 初始化阈值参数
        init_flow_threshold_parameter();
        // 初始化 阈值项 操作按钮
        init_threshold_btn();
        init_threshold_file_btn(upload);
    });
});

// 初始化节点的几个标签下拉框
function initSelect() {
    var flowId = $("#flowId").val();
    // 设置角色下拉框
    formSelects.data('selectRoles', 'server', {
        url: '/account/getSelectRole.action',
        linkageWidth: 80
    });

    // 设置可查看此流程的角色下拉框
    formSelects.data('viewerRoles', 'server', {
        url: '/flow/getViewerRoles.action?id=' + flowId,
        linkageWidth: 80,
        success: function (id, url, searchVal, result) {      //使用远程方式的success回调
            console.log(result);    //返回的结果
        }
    });

    // 选择完展示标签后，设置可编辑标签和必要标签的下拉框
    formSelects.closed('viewLabel', function (id) {
        var arr = formSelects.value(id);
        var editArr = formSelects.value('editLabel', 'val');

        // 可编辑标签
        formSelects.data('editLabel', 'local', {
            arr: arr
        });
        formSelects.value('editLabel', editArr);

        // 必须标签
        var mustArr = formSelects.value('mustLabel', 'val');
        formSelects.data('mustLabel', 'local', {
            arr: arr
        });
        formSelects.value('mustLabel', mustArr);
        // 更改后就重新生成
        reset_threshold_select();
    });

    // 选择完可编辑标签后，设置必要标签的下拉框
    formSelects.closed('editLabel', function (id) {
        var arr = formSelects.value(id);
        var mustArr = formSelects.value('mustLabel', 'val');
        formSelects.data('mustLabel', 'local', {
            arr: arr
        });
        formSelects.value('mustLabel', mustArr);
    });
    reset_threshold_select();
}

// 清空节点弹窗内容
function clearNodeData() {
    var formSelects = layui.formSelects;
    var arr = [];
    $("#flowNodeName").val("");
    formSelects.value('selectRoles', arr);
    formSelects.value('viewLabel', arr);
    formSelects.value('editLabel', arr);
    formSelects.value('mustLabel', arr);
    formSelects.value("flow_threshold_id_1", arr);
    $("select[name='opts']").val("");
    $("select[name='flow_threshold_value']").val("");
    if (threshold_count > 1) {
        for (var threshold_index = 2; threshold_index <= threshold_count; threshold_index++) {
            formSelects.value("flow_threshold_id_" + threshold_index, arr);
            $("div[data-my-flow-threshold=" + threshold_index + "]").remove();
        }
    }
    threshold_count = 1;
    $("div[lay-filter='my-defined-threshold']").html("");
    $('#dueTime').val('');
    $('#timeUnit').val('hour');
    form.render("select");
}

// 初始化标签table和节点table
function initTable() {
    layui.use(['table', 'form'], function () {
        var table = layui.table;
        var flowId = $("#flowId").val();
        // 节点table
        table.render({
            elem: "#nodeTable",
            limit: 20,
            url: '/flow/readFlowNode.action?id=' + flowId,
            cols: [
                [{
                    field: 'id',
                    hide: true
                }, {
                    field: 'name',
                    title: "名称",
                    align: 'center',
                    width: "10%"
                }, {
                    field: 'roleId',
                    hide: true
                }, {
                    field: 'role',
                    title: "角色",
                    align: 'center',
                    width: "10%"
                }, {
                    field: 'dueTime',
                    title: "处理期限",
                    align: 'center',
                    width: "5%",
                    templet: function (row) {
                        if (isBlank(row.id)) {
                            return '';
                        }
                        var hour = 0;
                        if (isNotBlank(row.dueTime)) {
                            hour = parseInt(row.dueTime);
                        }
                        if (hour === 0) {
                            return '不限';
                        } else if (hour <= 24 || hour % 24 > 0) {
                            return hour + '小时';
                        } else {
                            return hour / 24 + '天';
                        }
                    }
                }, {
                    field: 'viewLabel',
                    title: "展示标签",
                    align: 'center',
                    width: "15%"
                }, {
                    field: 'viewLabelId',
                    hide: true
                }, {
                    field: 'editLabel',
                    title: "可编辑标签",
                    align: 'center',
                    width: "15%"
                }, {
                    field: 'editLabelId',
                    hide: true
                }, {
                    field: 'mustLabel',
                    title: "必要标签",
                    align: 'center',
                    width: "10%"
                }, {
                    field: 'mustLabelId',
                    hide: true
                }, {
                    field: 'thresholdInfos',
                    title: "流程阈值",
                    align: 'center',
                    width: "10%"
                }, {
                    field: 'thresholds',
                    hide: true
                },  {
                    field: 'thresholdFileName',
                    title: "阈值脚本文件",
                    align: 'center',
                    width: "15%"
                },  {
                    field: 'thresholdFile',
                    hide: true
                },{
                    title: '操作',
                    fixed: 'right',
                    align: 'center',
                    minWidth: 200,
                    toolbar: '#nodeTools'
                }]
            ],
            parseData: function (res) { // 数据加载后回调
                var srcData = res.data.data;
                if (srcData != undefined) {
                    srcData.splice(0, 0, {});
                }
                return {
                    "code": res.data.code, //解析接口状态
                    "msg": res.msg, //解析提示文本
                    "count": res.data.count, //解析数据长度
                    "data": res.data.data //解析数据列表
                };
            }
        });

        table.on('tool(nodeTable)', function (obj) {
            reset_threshold_file();
            var layEvent = obj.event;
            if (layEvent === 'new') { //添加
                addNode(obj);
            } else if (layEvent === 'delete') { //删除
                layer.confirm('真的要删除行吗？', function (index) {
                    //删除对应行（tr）的DOM结构，并更新缓存
                    var nodeData = table.cache["nodeTable"];
                    var next = 1;
                    // 计算当前行下一行的index
                    for (var i = 0; i < nodeData.length; i++) {
                        if (nodeData[i].id == obj.data.id) {
                            next = i + 1;
                            break;
                        }
                    }
                    // 删除数据
                    nodeData.splice(next - 1, 1);
                    table.reload('nodeTable', {
                        url: '',
                        data: nodeData
                    });
                    layer.close(index);
                });
            } else if (layEvent === 'modify') { //修改
                editNode(obj);
            }
        });

        // 标签table
        table.render({
            elem: '#labelTable',
            url: '/flow/readFlowLabel.action?id=' + flowId,
            width: 555,
            limit: 100,
            page: false,
            cols: [[
                {
                    field: 'id',
                    title: 'id',
                    align: 'center',
                    hide: true
                }, {
                    field: 'name',
                    title: '标签名',
                    align: 'center',
                    width: 200
                }, {
                    field: 'type',
                    title: '类型',
                    align: 'center',
                    hide: true
                }, {
                    field: 'typeName',
                    title: '类型',
                    align: 'center',
                    width: 100
                }, {
                    field: 'defaultValue',
                    title: '默认值',
                    align: 'center',
                    width: 100
                }, {
                    title: '操作',
                    fixed: 'right',
                    align: 'center',
                    width: 150,
                    toolbar: '#tools'
                }
            ]],
            parseData: function (res) { // 数据加载后回调
                var srcData = res.data.data;
                if (srcData != undefined) {
                    srcData.splice(0, 0, {});
                }
                return {
                    "code": res.data.code, //解析接口状态
                    "msg": res.msg, //解析提示文本
                    "count": res.data.count, //解析数据长度
                    "data": res.data.data //解析数据列表
                };
            },
            // 对标签table操作完成后，更新节点弹窗的几个下拉框
            done: function (res, curr, count) {
                labelArr = new Array();
                if (res.data != undefined) {
                    for (var index = 1; index < res.data.length; index++) {
                        var element = res.data[index];
                        labelArr.push({
                            'value': element.id,
                            'name': element.name,
                        });
                    }
                }
                var formSelects = layui.formSelects;
                formSelects.data('viewLabel', 'local', {
                    arr: labelArr
                });
                formSelects.data('editLabel', 'local', {
                    arr: labelArr
                });
                formSelects.data('mustLabel', 'local', {
                    arr: labelArr
                });
            }
        });
        table.on('row(labelTable)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });

        table.on('tool(labelTable)', function (obj) {
            var layEvent = obj.event; //获得 lay-event 对应的值
            if (layEvent === 'new') { //添加
                addLabel(obj);
            } else if (layEvent === 'delete') { //删除
                del_node(obj);
            } else if (layEvent === 'modify') { // 修改
                editLabel(obj);
            }
        });
    });
}


// 添加节点
function addNode(obj) {
    clearNodeData();
    layer.open({
        type: 1,
        area: ['730px', '90%'],
        fixed: false,
        maxmin: true,
        btnAlign: 'c',
        content: $("#nodeAddContent"),
        btn: ['确定', '取消'],
        yes: function (index, layero) {
            if (add_node_info(obj) === false) {
                return;
            }
            layer.close(index);
        }
    });
}

// 添加节点
function add_node_info(obj) {
    var formSelects = layui.formSelects;
    var roleId = formSelects.value('selectRoles', 'valStr');
    var nodename = $("#flowNodeName").val().trim();
    if (!nodename) {
        layer.open({
            content: "请输入节点名称！",
            time: 2000
        });
        return false;
    }
    if (!roleId) {
        layer.open({
            content: "请选择节点处理角色！",
            time: 2000
        });
        return false;
    }
    var dueTime = $('#dueTime').val();
    if (isNotBlank(dueTime) && !/\d*/.test(dueTime)) {
        layer.msg('处理期限只能是数字');
        return false;
    }
    if ($('#timeUnit').val() === 'day') {
        dueTime = parseInt(dueTime) * 24;
    }
    if (!check_flow_threshold_infos()) {
        return false;
    }
    var creatDate = {
        'id': guid(),
        'name': nodename,
        'roleId': formSelects.value('selectRoles', 'valStr'),
        'role': formSelects.value('selectRoles', 'nameStr'),
        'viewLabel': formSelects.value('viewLabel', 'nameStr'),
        'viewLabelId': formSelects.value('viewLabel', 'valStr'),
        'editLabel': formSelects.value('editLabel', 'nameStr'),
        'editLabelId': formSelects.value('editLabel', 'valStr'),
        'mustLabel': formSelects.value('mustLabel', 'nameStr'),
        'mustLabelId': formSelects.value('mustLabel', 'valStr'),
        'thresholds': get_flow_threshold_infos(),
        'thresholdInfos': get_flow_threshold_show_infos(),
        'thresholdFile': get_threshold_file_info(),
        'thresholdFileName': $("#threshold_file_name").val(),
        'dueTime' : dueTime || 0
    };

    var table = layui.table;
    var oldData = table.cache["nodeTable"];
    var next = 1;
    // 计算当前行下一行的index
    for (var i = 0; i < oldData.length; i++) {
        if (oldData[i].id == obj.data.id) {
            next = i + 1;
            break;
        }
    }
    // 将新数据添加到当前行下一行
    oldData.splice(next, 0, creatDate);
    // 重新加载节点table
    table.reload('nodeTable', {
        url: '',
        data: oldData
    });
    clearNodeData();
    return true;
}

// 校验阈值填写信息
function check_flow_threshold_infos() {
    var formSelects = layui.formSelects;
    for (var index = 1; index <= threshold_count; index++) {
        var threshold_item = $("div[data-my-flow-threshold=" + index + "]");
        if (isNotBlank(threshold_item)) {
            // 获取对应的id
            var label_ids = formSelects.value("flow_threshold_id_" + index, "val");
            // 操作
            var operation_select = $(threshold_item).find("select[name='opts']");
            var operation = $(operation_select).val();
            // 值
            var threshold_select = $(threshold_item).find("select[name='flow_threshold_value']");
            var threshold_value = $(threshold_select).val();
            if ((isNotBlank(label_ids) && label_ids.length > 0) || isNotBlank(operation) || isNotBlank(threshold_value)) {
                if (isBlank(label_ids)) {
                    var id_dom = $(threshold_item).find("input[name='threshold_select']");
                    $(id_dom).focus();
                    layer.tips("此项为空，导致阈值设置不完全", id_dom);
                    return false;
                }
                if (isBlank(operation)) {
                    var operation_tip_dom = $(operation_select).next();
                    $(operation_tip_dom).focus();
                    layer.tips("此项为空，导致阈值设置不完全", operation_tip_dom);
                    return false;
                }
                if (isBlank(threshold_value)) {
                    var threshold_select_tip_dom = $(threshold_select).next();
                    $(threshold_select_tip_dom).focus();
                    layer.tips("此项为空，导致阈值设置不完全", threshold_select_tip_dom);
                    return false;
                }
            }
        }
    }
    return true;
}


// 获取阈值信息
function get_flow_threshold_infos() {
    var formSelects = layui.formSelects;
    var threshold_result = [];
    for (var index = 1; index <= threshold_count; index++) {
        var threshold_item = $("div[data-my-flow-threshold=" + index + "]");
        if (isNotBlank(threshold_item)) {
            // 获取对应的id
            var label_ids = formSelects.value("flow_threshold_id_" + index, "val");
            // 操作
            var operation_select = $(threshold_item).find("select[name='opts']");
            var operation = $(operation_select).val();
            // 值
            var threshold_select = $(threshold_item).find("select[name='flow_threshold_value']");
            var threshold_value = $(threshold_select).val();

            if (isNotBlank(label_ids) && isNotBlank(operation) && isNotBlank(threshold_value)) {
                for (var label_id_index in label_ids) {
                    var label_id = label_ids[label_id_index];
                    threshold_result.push({
                        labelId: label_id,
                        relationship: operation,
                        thresholdValue: threshold_value
                    });
                }
            }
        }
    }
    return threshold_result;
}

// 阈值的显示数据
function get_flow_threshold_show_infos() {
    var formSelects = layui.formSelects;
    var threshold_show_info = "";
    for (var index = 1; index <= threshold_count; index++) {
        var threshold_item = $("div[data-my-flow-threshold=" + index + "]");
        if (isNotBlank(threshold_item)) {
            // 操作
            var operation_select = $(threshold_item).find("select[name='opts']");
            var operation = $(operation_select).val();

            // 值
            var threshold_input = $(threshold_item).find("select[name='flow_threshold_value']");
            var threshold_value = $(threshold_input).find("option:selected").text();

            // 获取对应的id
            var label_names = formSelects.value("flow_threshold_id_" + index, "name");

            if (isNotBlank(label_names) && isNotBlank(operation_select) && isNotBlank(threshold_value)) {
                for (var label_index in label_names) {
                    threshold_show_info = threshold_show_info + "["
                        + label_names[label_index] + "  "
                        + get_operation_name(operation) + "  "
                        + threshold_value + "]";
                }
            }
        }
    }
    return threshold_show_info;
}

function get_operation_name(operation) {
    if (isBlank(operation)) {
        return "";
    }
    if (operation === "eq") {
        return "等于";
    } else if (operation === "ne") {
        return "不等于";
    } else if (operation === "lt") {
        return "小于";
    } else if (operation === "gt") {
        return "大于";
    } else if (operation === "le") {
        return "小于等于";
    } else if (operation === "ge") {
        return "大于等于";
    }
    return "";
}


// 初始化 阈值的操作按钮
function init_threshold_btn() {
    var threshold_item = $("div[data-my-flow-threshold=" + threshold_count + "]");
    // 添加按钮
    threshold_item.find("div[class*='add_threshold']").click(function () {
        add_threshold_item()
    });
    // 删除按钮
    threshold_item.find("div[class*='del_threshold']").click(function () {
        if (threshold_count > 1) {
            $("div[data-my-flow-threshold=" + threshold_count + "]").remove();
            threshold_count--;
            var before_item = $("div[data-my-flow-threshold='" + threshold_count + "']");
            var before_add = before_item.find("div[class*='add_threshold']");
            $(before_add).show();
            if (threshold_count > 1) {
                var before_del = before_item.find("div[class*='del_threshold']");
                $(before_del).show();
            }
        }
    });
}

// 添加阈值项
function add_threshold_item(threshold_info) {
    var before_item = $("div[data-my-flow-threshold='" + threshold_count + "']");
    var befor_dom = before_item.html();
    before_item.find("div[class*='del_threshold']").hide();
    before_item.find("div[class*='add_threshold']").hide();
    threshold_count++;
    $(before_item).after("<div class='layui-form-item' data-my-flow-threshold='" + threshold_count + "'>" +
        befor_dom + "</div>");
    form.render();
    var threshold_item = $("div[data-my-flow-threshold=" + threshold_count + "]");
    if (threshold_count > 1) {
        threshold_item.find("div[class*='del_threshold']").show();
        threshold_item.find("div[class*='add_threshold']").show();
    }
    init_threshold_btn();
    // 初始化选择框
    init_threshold_select();
    if (threshold_info) {
        // 回显对应的信息
        reshow_threshold_info(threshold_info);
    }
}

// 回显阈值信息
function reshow_threshold_info(threshold_info) {
    if (isNotBlank(threshold_info)) {
        // 回显选项
        var formSelects = layui.formSelects;
        var idstr = threshold_info.labelId;
        if (isNotBlank(idstr)) {
            formSelects.value('flow_threshold_id_' + threshold_count, idstr.split(","));
        }
        // 回显操作
        var threshold_item = $("div[data-my-flow-threshold=" + threshold_count + "]");
        $(threshold_item).find("select[name='opts']").val(threshold_info.relationship);
        // 回显值
        $(threshold_item).find("select[name='flow_threshold_value']").val(threshold_info.thresholdValue);
        form.render();
    }
}


// 初始化对应的选项（新的，单个）
function init_threshold_select() {
    var formSelects = layui.formSelects;
    var threshold_item = $("div[data-my-flow-threshold=" + threshold_count + "]");
    var select = $(threshold_item).find("select[_name='threshold_select']");
    $(select).attr("xm-select", "flow_threshold_id_" + threshold_count);
    // 可以看见的标签信息
    var viewLabels = formSelects.value('viewLabel', 'all');
    // 全部已经选择了的
    var all_selected = [];
    for (var index = 1; index <= threshold_count; index++) {
        var selected = [];
        try {
            selected = formSelects.value("flow_threshold_id_" + index, 'all');
        } catch (e) {
            console.log("捕获异常", e);
        }
        // 前面已经选中的内容
        all_selected = all_selected.concat(selected);
    }
    var left = difference_set(viewLabels, all_selected, "value");
    formSelects.data("flow_threshold_id_" + threshold_count, 'local', {
        arr: left
    });
    // 改变的时候需要重新设置选项
    formSelects.closed("flow_threshold_id_" + threshold_count, function (id, vals, val, isAdd, isDisabled) {
        reset_threshold_select();
    });
}


// 初始化对应的选项
function reset_threshold_select() {
    var formSelects = layui.formSelects;
    // 可以看见的标签信息
    var viewLabels = formSelects.value('viewLabel', 'all');
    // 前面所有已经选中的
    var all_selected = [];
    for (var init_index = 1; init_index <= threshold_count; init_index++) {
        var item_selected = formSelects.value("flow_threshold_id_" + init_index, 'all');
        // 前面已经选中的内容
        all_selected = all_selected.concat(item_selected);
    }

    for (var set_index = 1; set_index <= threshold_count; set_index++) {
        var set_selected = formSelects.value("flow_threshold_id_" + set_index, 'all');
        var left = difference_set(viewLabels, all_selected, "value").concat(set_selected);
        formSelects.data("flow_threshold_id_" + set_index, 'local', {
            arr: left
        });
        var select_values = [];
        for (var index in set_selected) {
            select_values.push(set_selected[index].value);
        }
        formSelects.value("flow_threshold_id_" + set_index, select_values);
    }
}

// 修改节点
function editNode(obj) {
    clearNodeData();
    var formSelects = layui.formSelects;
    var data = obj.data; // 要修改的那一行的数据
    // 展示标签选中的值
    var viewLabelArr = data.viewLabel.split(',');
    var viewLabelIdArr = data.viewLabelId.split(',');

    // 设置可编辑标签的下拉框
    var editDateArr = [];
    for (var i in viewLabelIdArr) {
        if (viewLabelIdArr[i] != null && viewLabelIdArr[i] !== '') {
            editDateArr.push({
                'value': viewLabelIdArr[i],
                'name': viewLabelArr[i]
            });
        }
    }

    // 可编辑标签选中的值
    var editLabelArr = data.editLabel.split(',');
    var editLabelIdArr = data.editLabelId.split(',');
    // 设置必要标签的下拉框
    var mustDateArr = [];
    for (var i in editLabelArr) {
        if (editLabelIdArr[i] != null && editLabelIdArr[i] !== '') {
            mustDateArr.push({
                'value': editLabelIdArr[i],
                'name': editLabelArr[i],
            });
        }
    }
    // 必要标签选中的值
    var mustLabelIdArr = data.mustLabelId.split(',');
    $("#flowNodeName").val(data.name);
    // 设置可编辑标签的下拉框
    formSelects.data('editLabel', 'local', {
        arr: editDateArr
    });
    // 设置必要标签的下拉框
    formSelects.data('mustLabel', 'local', {
        arr: mustDateArr
    });
    // 文件信息
    var fileInfo = data.thresholdFile;
    if (isNotBlank(fileInfo)) {
        show_threshold_file(JSON.parse(fileInfo));
    }
    var dueTime = data.dueTime;
    var timeUnit = 'hour';
    if (isNotBlank(dueTime)) {
        dueTime = parseInt(dueTime);
        if (dueTime > 24 && dueTime % 24 === 0) {
            timeUnit = 'day';
            dueTime = dueTime / 24;
        }
    }
    $('#dueTime').val(dueTime);
    $('#timeUnit').val(timeUnit);
    layer.open({
        type: 1,
        area: ['730px', '90%'],
        fixed: false, //不固定
        maxmin: true,
        btnAlign: 'c',
        content: $("#nodeAddContent"),
        btn: ['确定', '取消'],
        success: function (layero, index) {
            var formSelects = layui.formSelects;
            // 角色下拉框选中项
            formSelects.value('selectRoles', data.roleId.split(','));
            // 展示标签的选中项
            formSelects.value('viewLabel', viewLabelIdArr);
            // 可编辑标签的选中项
            formSelects.value('editLabel', editLabelIdArr);
            // 必要标签的选中项
            formSelects.value('mustLabel', mustLabelIdArr);
            // 设置 阈值标签 选择情况
            var thresholdInfos = data.thresholds;
            if (isNotBlank(thresholdInfos)) {
                // 遍历分组
                for (var threshold_index = 0; threshold_index < thresholdInfos.length; threshold_index++) {
                    // 阈值信息
                    var thresholdInfo = thresholdInfos[threshold_index];
                    if (threshold_index === 0) {
                        init_threshold_select();
                        // 默认有 只用回显
                        reshow_threshold_info(thresholdInfo)
                    } else {
                        add_threshold_item(thresholdInfo);
                    }
                }
            } else {
                reset_threshold_select();
            }
            form.render();
        },
        yes: function (index, layero) {
            var nodename = $("#flowNodeName").val().trim();
            var roleId = formSelects.value('selectRoles', 'valStr');
            if (!nodename) {
                layer.open({
                    content: "请输入节点名称！",
                    time: 2000
                });
                return;
            }
            if (!roleId) {
                layer.open({
                    content: "请选择节点处理角色！",
                    time: 2000
                });
                return;
            }
            // 阈值填写
            if (!check_flow_threshold_infos()) {
                return;
            }
            var dueTime = $('#dueTime').val();
            if (isNotBlank(dueTime) && !/\d*/.test(dueTime)) {
                layer.msg('处理期限只能是数字');
                return;
            }
            if ($('#timeUnit').val() === 'day') {
                dueTime = parseInt(dueTime) * 24;
            }
            var creatDate = {
                'id': obj.data.id,
                'name': $("#flowNodeName").val(),
                'roleId': formSelects.value('selectRoles', 'valStr'),
                'role': formSelects.value('selectRoles', 'nameStr'),
                'viewLabel': formSelects.value('viewLabel', 'nameStr'),
                'viewLabelId': formSelects.value('viewLabel', 'valStr'),
                'editLabel': formSelects.value('editLabel', 'nameStr'),
                'editLabelId': formSelects.value('editLabel', 'valStr'),
                'mustLabel': formSelects.value('mustLabel', 'nameStr'),
                'mustLabelId': formSelects.value('mustLabel', 'valStr'),
                'thresholdInfos': get_flow_threshold_show_infos(),
                'thresholds': get_flow_threshold_infos(),
                'thresholdFile': get_threshold_file_info(),
                'thresholdFileName': $("#threshold_file_name").val(),
                'dueTime': dueTime || 0
            };
            update_table(obj, creatDate);
            clearNodeData();
            layer.close(index);
        }
    });
}

function update_table(obj, creatDate) {
    // obj.update(creatDate); 会出现无法预测的问题，暂时不用这个方法
    var table = layui.table;
    var oldData = table.cache["nodeTable"];
    var id = obj.data.id;
    var r_index = 0;
    for (var old_index in oldData) {
        if (oldData[old_index].id === id) {
            r_index = old_index
        }
    }

    // 将新数据添加到当前行下一行
    oldData.splice(r_index, 1, creatDate);
    // 重新加载节点table
    table.reload('nodeTable', {
        url: '',
        data: oldData
    });
}


// 删除节点
function del_node(obj) {
    layer.confirm('真的要删除行吗？', function (layindex) {
        var table = layui.table;
        //先删除节点表中标签数据
        var oldData = table.cache["nodeTable"];
        for (var index = 1; index < oldData.length; index++) {
            var element = oldData[index];
            delNodeLabel(element, obj.data);
        }
        table.reload('nodeTable', {
            url: '',
            data: oldData
        });
        //删除对应行（tr）的DOM结构，并更新缓存
        var labelData = table.cache["labelTable"];
        var next = 1;
        // 计算当前行下一行的index
        for (var i = 0; i < labelData.length; i++) {
            if (labelData[i].id == obj.data.id) {
                next = i + 1;
                break;
            }
        }
        // 删除数据
        labelData.splice(next - 1, 1)
        table.reload('labelTable', {
            url: '',
            data: labelData
        });

        layer.close(layindex);
    });
}


// 初始化流程类型下拉框
function initFlowType() {
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            type: "POST",
            async: false,
            url: '/flow/getFlowType.action?temp=' + Math.random(),
            dataType: 'json',
            data: {},
            success: function (data) {
                var select = $("#flowType");
                var flowType = $("#flowTypeH").val();
                select.empty().append('<option value="">---请选择类型---</option>');
                for (var i = 0; i < data.length; i++) {
                    if (flowType == data[i].value) {
                        select.append('<option value="' + data[i].value + '" selected="selected">' + data[i].name + '</option>');
                    } else {
                        select.append('<option value="' + data[i].value + '">' + data[i].name + '</option>');
                    }
                }
                layui.form.render('select');
            }
        });
    });
}

// 删除标签时更新节点的几个标签
function delNodeLabel(element, data) {
    // element是节点table的一行，data是被删除的标签 展示标签原本的内容
    var viewLabelArr = element.viewLabel.split(',');
    var viewLabelIdArr = element.viewLabelId.split(',');
    // 排除被删除的标签，未被删除的标签放到新列表中
    var newViewLabelArr = new Array();
    var newViewLabelIdArr = new Array();
    for (var i = 0; i < viewLabelIdArr.length; i++) {
        if (data.id != viewLabelIdArr[i]) {
            newViewLabelIdArr.push(viewLabelIdArr[i])
            newViewLabelArr.push(viewLabelArr[i])
        }
    }
    // 将新的内容赋给展示标签
    element.viewLabel = newViewLabelArr.join(",");
    element.viewLabelId = newViewLabelIdArr.join(",");
    // 可编辑标签原本的内容
    var editLabelArr = element.editLabel.split(',');
    var editLabelIdArr = element.editLabelId.split(',');
    // 排除被删除的标签，未被删除的标签放到新列表中
    var newEditLabelArr = new Array();
    var newEditLabelIdArr = new Array();
    for (var i = 0; i < editLabelIdArr.length; i++) {
        if (data.id != editLabelIdArr[i]) {
            newEditLabelIdArr.push(editLabelIdArr[i])
            newEditLabelArr.push(editLabelArr[i])
        }
    }
    // 将新的内容赋给可编辑标签
    element.editLabel = newEditLabelArr.join(",");
    element.editLabelId = newEditLabelIdArr.join(",");
    // 必要标签原本的内容
    var mustLabelArr = element.mustLabel.split(',');
    var mustLabelIdArr = element.mustLabelId.split(',');
    // 排除被删除的标签，未被删除的标签放到新列表中
    var newMustLabelArr = new Array();
    var newMustLabelIdArr = new Array();
    for (var i = 0; i < mustLabelIdArr.length; i++) {
        if (data.id != mustLabelIdArr[i]) {
            newMustLabelIdArr.push(mustLabelIdArr[i])
            newMustLabelArr.push(mustLabelArr[i])
        }
    }
    // 将新的内容赋给必要标签
    element.mustLabel = newMustLabelArr.join(",");
    element.mustLabelId = newMustLabelIdArr.join(",");
}

// 初始化标签的数据类型
function initFlowLabelType() {
    layui.use('form', function () {
        var form = layui.form;
        var priceTypeDefaultValue;
        var chargeTypeDefaultValue;
        $.ajax({
            type: "POST",
            async: false,
            url: '/flow/getFlowLabelType.action?temp=' + Math.random(),
            dataType: 'json',
            data: {},
            success: function (data) {
                var select = $("#typeH");
                select.empty().append('<option value="">---请选择类型---</option>');
                for (var i = 0; i < data.length; i++) {
                    select.append('<option value="' + data[i].value + '">' + data[i].name + '</option>');
                    if (i == 11) {
                        priceTypeDefaultValue = data[i].defaultValue;
                    } else if (i == 12) {
                        chargeTypeDefaultValue = data[i].defaultValue;
                    }
                }
                layui.form.render('select');
            }
        });

        form.on('select(typeH)', function (data) {
            if (data.value == 11) {
                $("#defaultValueH").val(priceTypeDefaultValue);
                $("#defaultValueH").attr("disabled", true);
            } else if (data.value == 12) {
                $("#defaultValueH").val(chargeTypeDefaultValue);
                $("#defaultValueH").attr("disabled", true);
            } else {
                $("#defaultValueH").val('');
                $("#defaultValueH").attr("disabled", false);
            }

        });
    });
}

// 添加标签
function addLabel(obj) {
    initFlowLabelType();
    $("#nameH").val('');
    $("#defaultValueH").val('');
    layer.open({
        title: ['添加新标签', 'font-size:18px;'],
        type: 1,
        id: 'lay_add',
        content: $("#newLabel"), // 展示的容器
        btn: ['确定', '取消'],
        area: ['360px', '450px'],
        yes: function (index) {
            var name = $("#nameH").val();
            var type = $("#typeH option:selected").val();
            var typeName = $("#typeH option:selected").text();
            var defaultValue = $("#defaultValueH").val();
            if (!name || name.length == 0) {
                layer.open({
                    content: "输入标签名！",
                    time: 2000
                });
                return;
            }
            if (!type) {
                layer.open({
                    content: "请选择类型！",
                    time: 2000
                });
                return;
            }
            var flag = true;
            var table = layui.table;
            var oldData = table.cache["labelTable"];
            var next = 1; // 新标签的index
            $.each(oldData, function (i, item) {
                // 标签名不能重复
                if (item.name == name) {
                    layer.open({
                        content: "标签名重复！",
                        time: 2000
                    });
                    flag = false;
                }
                // 计算新标签的index，放在当前行下一行
                if (item.id == obj.data.id) {
                    next = i + 1;
                }
            })
            if (flag) {
                // 将新数据添加到当前行下一行
                oldData.splice(next, 0, {
                    "id": guid(),
                    "name": name,
                    "type": type,
                    "typeName": typeName,
                    "defaultValue": defaultValue
                })
                table.reload('labelTable', {
                    url: '',
                    data: oldData
                });
                layer.close(index);
            }
        }
    });
}

// 修改标签
function editLabel(obj) {
    initFlowLabelType();
    $("#nameH").val(obj.data.name);
    $("#typeH").find("option[value='" + obj.data.type + "']").attr("selected", true);
    $("#defaultValueH").val(obj.data.defaultValue);
    if (obj.data.type == 11) {
        $("#defaultValueH").attr("disabled", true);
    } else if (obj.data.type == 12) {
        $("#defaultValueH").attr("disabled", true);
    } else {
        $("#defaultValueH").attr("disabled", false);
    }
    layui.form.render('select');
    layer.open({
        title: ['修改标签', 'font-size:18px;'],
        type: 1,
        id: 'lay_add',
        content: $("#newLabel"), // 展示的容器
        btn: ['确定', '取消'],
        area: ['360px', '450px'],
        yes: function (index) {
            var name = $("#nameH").val();
            var type = $("#typeH option:selected").val();
            var typeName = $("#typeH option:selected").text();
            var defaultValue = $("#defaultValueH").val();
            var flag = true;
            if (!type) {
                layer.open({
                    content: "请选择类型！",
                    time: 2000
                });
                flag = false;
            }
            if (flag) {
                layer.close(index);
                var table = layui.table;
                var oldData = table.cache["labelTable"];
                for (var index = 1; index < oldData.length; index++) {
                    var element = oldData[index];
                    if (element.id == obj.data.id) {
                        // 删除原始内容，替换成新内容
                        oldData.splice(index, 1, {
                            id: obj.data.id,
                            name: name,
                            type: type,
                            typeName: typeName,
                            defaultValue: defaultValue
                        });
                        break;
                    }
                }
                // 重新加载标签table
                table.reload('labelTable', {
                    url: '',
                    data: oldData
                });
                // 更新节点table每个节点的标签
                var oldData = table.cache["nodeTable"];
                for (var index = 1; index < oldData.length; index++) {
                    var element = oldData[index];
                    updateNodeLabel(element, {
                        id: obj.data.id,
                        name: name
                    });
                }
                // 重新加载节点table
                table.reload('nodeTable', {
                    url: '',
                    data: oldData
                });
            }
        }
    });
}

// 提交添加流程
function initBtum(type) {
    var url = "";
    var msg = "";
    if (type === "add") {
        url = "/flow/addFlow.action";
        msg = "流程添加成功";
    } else if (type === "edit") {
        url = "/flow/editFlow.action";
        msg = "流程修改成功";
    }
    $("#cancel").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index);
    });
    $("#submit").click(function () {
        var flowId = $("#flowId").val();
        var flowName = $("#flowName").val();
        if (!flowName || flowName.trim().length == 0) {
            layer.msg("请输入流程名称！");
            return;
        }
        var flowType = $("#flowType").val();
        if (!flowType) {
            layer.msg("请选择流程类型！");
            return;
        }
        var flowClass = $("#flowClass").val();
        if (!flowClass) {
            layer.msg("请选择流程类别！");
            return;
        }
        var bindType = $('#bindType').val();
        if (bindType === null || bindType === undefined) {
            layer.msg("请选择绑定类型！");
            return;
        }
        var associateType = $("#associateType").val();
        if (isBlank(associateType)){
            layer.msg("请选择流程关联到的类型");
            return;
        }
        var table = layui.table;
        var flowLabelData = table.cache["labelTable"];
        var labelList = [];
        for (var i = 1; i < flowLabelData.length; i++) {
            labelList.push({
                position: i - 1,
                id: flowLabelData[i].id,
                name: flowLabelData[i].name,
                type: flowLabelData[i].type,
                defaultValue: flowLabelData[i].defaultValue
            });
        }
        var flowNodeData = table.cache["nodeTable"];
        if (flowNodeData.length <= 2) {
            layer.msg("至少要有2个节点！");
            return;
        }
        var nodeList = [];
        for (var i = 1; i < flowNodeData.length; i++) {
            nodeList.push({
                nodeIndex: i - 1,
                nodeId: flowNodeData[i].id,
                nodeName: flowNodeData[i].name,
                roleId: flowNodeData[i].roleId,
                dueTime: flowNodeData[i].dueTime,
                viewLabelIds: flowNodeData[i].viewLabelId,
                editLabelIds: flowNodeData[i].editLabelId,
                mustLabelIds: flowNodeData[i].mustLabelId,
                flowThresholds: flowNodeData[i].thresholds,
                thresholdFile: flowNodeData[i].thresholdFile
            });
        }
        //
        var viewerRoleId = formSelects.value('viewerRoles', 'valStr');
        var postData = {
            flowId: flowId,
            flowName: flowName,
            flowType: flowType,
            flowClass: flowClass,
            viewerRoleId: viewerRoleId,
            nodeList: nodeList,
            labelList: labelList,
            bindType: bindType,
            associateType: associateType
        };
        $.ajax({
            type: "POST",
            async: false,
            url: url,
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify(postData),
            success: function (data) {
                if (data.code == 200) {
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index);
                    parent.layer.msg(msg);
                    var table = layui.table;
                    parent.tableIns.reload();
                } else {
                    layer.msg(data.msg);
                }
            }
        });
    });
}

// 修改标签后更新节点的几个标签
function updateNodeLabel(element, data) { // element是节点table的一行，data是被修改过的标签
    // 展示标签原本的内容
    var viewLabelArr = element.viewLabel.split(',');
    var viewLabelIdArr = element.viewLabelId.split(',');
    // 更新被修改的标签
    for (var i = 0; i < viewLabelIdArr.length; i++) {
        if (data.id == viewLabelIdArr[i]) {
            viewLabelArr[i] = data.name;
        }
    }
    // 将新的内容赋给展示标签
    element.viewLabel = viewLabelArr.join(",");
    // 可编辑标签原本的内容
    var editLabelArr = element.editLabel.split(',');
    var editLabelIdArr = element.editLabelId.split(',');
    // 更新被修改的标签
    for (var i = 0; i < editLabelIdArr.length; i++) {
        if (data.id == editLabelIdArr[i]) {
            editLabelArr[i] = data.name;
        }
    }
    // 将新的内容赋给可编辑标签
    element.editLabel = editLabelArr.join(",");
    // 必要标签原本的内容
    var mustLabelArr = element.mustLabel.split(',');
    var mustLabelIdArr = element.mustLabelId.split(',');
    // 更新被修改的标签
    for (var i = 0; i < mustLabelIdArr.length; i++) {
        if (data.id == mustLabelIdArr[i]) {
            mustLabelArr[i] = data.name;
        }
    }
    // 将新的内容赋给必要标签
    element.mustLabel = mustLabelArr.join(",");
}

// 初始化流程类别下拉框
function initFlowClass() {
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            type: "POST",
            async: false,
            url: '/flow/getFlowClass.action?temp=' + Math.random(),
            dataType: 'json',
            data: {},
            success: function (data) {
                var select = $("#flowClass");
                var flowClass = $("#flowClassH").val();
                select.empty().append('<option value="">---请选择类别---</option>');
                for (var i = 0; i < data.length; i++) {
                    if (flowClass == data[i].value) {
                        select.append('<option value="' + data[i].value + '" selected="selected">' + data[i].name + '</option>');
                    } else {
                        select.append('<option value="' + data[i].value + '">' + data[i].name + '</option>');
                    }
                }
                layui.form.render('select');
            }
        });
    });
}


// 初始化流程类别下拉框
function init_flow_threshold_parameter() {
    $.ajax({
        type: "POST",
        async: false,
        url: '/parameter/readParameterByType.action?temp=' + Math.random(),
        dataType: 'json',
        data: {
            parameterType: 3
        },
        success: function (data) {
            if (data.code === 200) {
                var parameters = data.data;
                if (isNotBlank(parameters) && parameters.length > 0) {
                    var value_select_dom = $("select[name='flow_threshold_value']");
                    for (var p_index = 0; p_index < parameters.length; p_index++) {
                        var parameter = parameters[p_index];
                        value_select_dom.append('<option value=' + parameter.entityid + '>' +
                            parameter.paramkey + '(' + parameter.paramvalue + ')' + '</option>');
                    }
                }
            } else {
                layer.msg(data.msg);
            }
            form.render('select');
        }
    });
}

/** *****************************************新阈值代码************************************************************** **/
/** 初始化阈值上传按钮 */
function init_threshold_file_btn(upload) {
    upload.render({
        elem: '#upload-defined-item',
        url: '/operate/upLoadFile',
        accept: 'file',
        exts: 'js',
        field: 'files',
        done: function (res) {
            if (res.code === 200 || res.code === "200") {
                var file_arr = res.data;
                if (file_arr == null || file_arr.length === 0) {
                    layer.msg("脚本文件上传失败");
                } else {
                    show_threshold_file(file_arr[0])
                }
            } else {
                layer.msg(res.msg);
            }
        },
        error: function (index, upload) {
            layer.msg("脚本上传失败");
        }
    });
}

/** 充值文件上传 */
function reset_threshold_file() {
    $("#upload_file_dom").hide();
    $("#threshold_file_name").val("");
    $("#threshold_file_path").val("");
    $("#show_file_name").html("");
    $("#show_file_name").attr("");
}

/** 文件展示 */
function show_threshold_file(file) {
    $("#upload_file_dom").show();
    $("#threshold_file_name").val(file.fileName);
    $("#threshold_file_path").val(file.filePath);
    $("#show_file_name").html(file.fileName);
    $("#show_file_name").attr("title",file.fileName);
}

/** 获取阈值脚本信息 */
function get_threshold_file_info() {
    var fileName = $("#threshold_file_name").val();
    var filePath = $("#threshold_file_path").val();
    if (isNotBlank(fileName) && isNotBlank(filePath)) {
        return JSON.stringify({
            "fileName": fileName,
            "filePath": filePath
        });
    }
    return null;
}

/** 下载脚本Demo */
function down_load_demo(url) {
    //创建元素
    var ele = document.createElement('a');
    ele.download = "bill_money.js";
    ele.style.display = "none";
    ele.href = url;
    document.body.appendChild(ele);
    //模拟点击
    ele.click();
    //移除元素
    document.body.removeChild(ele);
}