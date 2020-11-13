var zTree;
var setting = "";
$(document).ready(function () {
    // 展示界面,并定义事件
    setting = {
        data: {
            simpleData: {
                enable: true
            },
            key: {
                title: "sequence"
            }
        },
        callback: {
            onCheck: function (event, treeId, treeNode) {
                var zTree = $.fn.zTree.getZTreeObj("treeDemo");
                var checkedNodes = zTree.getCheckedNodes(true);
                var ids = getNodeDeptIds(checkedNodes);
                if (isNotBlank(ids)) {
                    $("#choosed_dept_ids").val(ids.join(","));
                }
                console.log("触发了勾选");
            },
            onClick: function (event, treeId, treeNode) {
                $('#clicked_dept_id').val(treeNode.id);
                $('#clicked_dept_id').attr('data-name', treeNode.name);
                console.log("--点击选项--")
            }
        }
    };
    // 是否多选
    if (check !== 'no') {
        setting['check'] = {
            enable: true,
            chkboxType: {"Y": "s", "N": ""}
        }
    }
    $.ajax({
        url: '/department/obtainUserDept.action?temp=' + Math.random(),
        async: false,
        type: "POST",
        dataType: "json",
        success: function (resp) {
            var zNodes = eval(resp.data);
            zNodes = formDate(zNodes);
            $.fn.zTree.init($("#treeDemo"), setting, zNodes);
            zTree = $.fn.zTree.getZTreeObj("treeDemo");
            var node = zTree.getNodeByTId("tree_1");
            zTree.expandNode(node, true, false, true);
        }
    });

    // 组装数据（回显选中数据）
    function formDate(depts) {
        if (isBlank(depts)) {
            return {};
        }
        // 分割成数组
        var deptIdArr = [];
        if (isNotBlank(window.deptIds)) {
            deptIdArr = window.deptIds.split(",");
        }
        for (var dept_index = 0; dept_index < depts.length; dept_index++) {
            // 部门信息
            var dept = depts[dept_index];
            for (var dept_id_index = 0; dept_id_index < deptIdArr.length; dept_id_index++) {
                var selected_dept_id = deptIdArr[dept_id_index];
                if (selected_dept_id === dept.id) {
                    dept.checked = true;
                }
            }
            dept.open = true;
            depts[dept_index] = dept;
        }
        return depts;
    }

    // 获取选中的部门id
    function getNodeDeptIds(checkedNodes) {
        var ids = [];
        if (isBlank(checkedNodes)) {
            return ids;
        }
        for (var node_index = 0; node_index < checkedNodes.length; node_index++) {
            var node = checkedNodes[node_index];
            ids.push(node.id);
        }
        return ids;
    }

    // 全选
    $("#check-all").click(function (e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        zTree.checkAllNodes(true);
        var nodes = zTree.getCheckedNodes(true);
        var checkedIds = getNodeDeptIds(nodes);
        $("#choosed_dept_ids").val(checkedIds.join(","));
    });

    // 反选
    $("#check-opposite").click(function (e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        // 已经选择的
        var checkedNodes = zTree.getCheckedNodes(true);
        // 没选择的
        var uncheckedNodes = zTree.getCheckedNodes(false);
        for (var checked_index = 0; checked_index < checkedNodes.length; checked_index++) {
            // 选择的不选
            zTree.checkNode(checkedNodes[checked_index], false, false);
        }
        for (var unchecked_index = 0; unchecked_index < uncheckedNodes.length; unchecked_index++) {
            // 选择的不选
            zTree.checkNode(uncheckedNodes[unchecked_index], true, false);
        }
        var nodes = zTree.getCheckedNodes(true);
        var checkedIds = getNodeDeptIds(nodes);
        $("#choosed_dept_ids").val(checkedIds.join(","));
    });

    // 清空
    $("#clear-all").click(function (e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        zTree.checkAllNodes(false);
        var nodes = zTree.getCheckedNodes(true);
        var checkedIds = getNodeDeptIds(nodes);
        $("#choosed_dept_ids").val(checkedIds.join(","));
    });
});