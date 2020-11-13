var zTree;
var setting = "";
var checkedDeptIds; // 选中部门id
var checkedUserIds; // 选中用户id
$(document).ready(function () {
    // 没有部门过滤权限，加载部门
    if (!perm["customerFilter"]) {
        return;
    }
    // 展示界面,并定义事件
    setting = {
        check: {
            enable: true,
            chkboxType: {"Y": "s", "N": ""}
        },
        data: {
            simpleData: {
                enable: true
            }
            ,
            key: {
                title: "sequence"
            }
        }
        ,
        callback: {
            onCheck: function (event, treeId, treeNode) {
                var zTree = $.fn.zTree.getZTreeObj("treeDemo");
                var checkedNodes = zTree.getCheckedNodes(true);
                getCheckedNodeIds(checkedNodes);
                console.log("触发了勾选");
            }
            ,
            onClick: function (event, treeId, treeNode) {
                console.log("--点击选项--")
            }
        }
    }
    ;
    $.ajax({
        url: '/department/searchDepartment.action?temp=' + Math.random(),
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

    /**
     * 组装数据，回显选中的节点
     * @param deptInfos     部门结构信息，包含部门节点和用户节点
     * @returns
     */
    function formDate(deptInfos) {
        if (isBlank(deptInfos)) {
            return {};
        }
        // 被选中的部门id分割成数组
        var deptIdArr = [];
        if (isNotBlank(window.deptIds)) {
            deptIdArr = window.deptIds.split(",");
        }
        // 被选中的用户id分割成数组
        var userIdArr = [];
        if (isNotBlank(window.userIds)) {
            userIdArr = window.userIds.split(",");
        }
        // 遍历每个节点，可能是部门，也可能是用户
        deptInfos.forEach(function (deptInfo, index, deptInfos) {
            if (deptInfo.nodeType === 'dept') {
                // 部门节点，检查是否被选中的部门id
                deptInfo.checked = (deptIdArr.indexOf(deptInfo.id) >= 0);
            } else if (deptInfo.nodeType === 'user') {
                // 用户节点，检查是否被选中的用户id
                deptInfo.checked = (userIdArr.indexOf(deptInfo.id) >= 0);
            }
            deptInfo.open = true;
            deptInfos[index] = deptInfo;
        });
        return deptInfos;
    }

    // 获取选中节点的id（部门id | 用户id）
    function getCheckedNodeIds(checkedNodes) {
        checkedDeptIds = [];
        checkedUserIds = [];
        checkedNodes.forEach(function (node, index, checkedNodes) {
            if (node.nodeType === 'dept') {
                checkedDeptIds.push(node.id);
            } else if (node.nodeType === 'user') {
                checkedUserIds.push(node.id);
            }
        });
        $("#checkedDeptIds").val(checkedDeptIds.join(","));
        $("#checkedUserIds").val(checkedUserIds.join(","));
    }


    // 全选
    $("#check-all").click(function (e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        zTree.checkAllNodes(true);
        var nodes = zTree.getCheckedNodes(true);
        getCheckedNodeIds(nodes);
    });

    // 反选
    $("#check-opposite").click(function (e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        // 已经选择的
        var checkedNodes = zTree.getCheckedNodes(true);
        // 没选择的
        var uncheckedNodes = zTree.getCheckedNodes(false);
        for (var checked_index = 0; checked_index < checkedNodes.length; checked_index++) {
            // 已选中的取消选中
            zTree.checkNode(checkedNodes[checked_index], false, false);
        }
        for (var unchecked_index = 0; unchecked_index < uncheckedNodes.length; unchecked_index++) {
            // 未选中的选中
            zTree.checkNode(uncheckedNodes[unchecked_index], true, false);
        }
        var nodes = zTree.getCheckedNodes(true);
        getCheckedNodeIds(nodes);
    });

    // 清空
    $("#clear-all").click(function (e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        zTree.checkAllNodes(false);
        var nodes = zTree.getCheckedNodes(true);
        getCheckedNodeIds(nodes);
    });
});