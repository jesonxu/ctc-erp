var zNodes;
var zTree;
var isEditAble = true;
var setting = "";
$(document).ready(function() {

	// 展示界面,并定义事件
 	setting = {
		data : {
			simpleData : {
				enable : true
			},
			key : {
				title : "sequence"
			}
		},
		callback : {
			onClick : zTreeOnClick,
		},

	};
	initDept();
});
var layer;
// 查询基础数据
function initDept() {
	layui.use([ 'layer' ], function() {
		layer = layui.layer;
		$.ajax({
			url : '/account/readDepts.action?temp=' + Math.random(),
			async : false,
			type : "POST",
			dataType : "json",
			success : function(resp) {
				var zNodes = eval(resp.data);
				$.fn.zTree.init($("#treeDemo"), setting, zNodes);
				$("#selectAll").bind("click", selectAll);
				zTree = $.fn.zTree.getZTreeObj("treeDemo");
				var node = zTree.getNodeByTId("tree_1");
				zTree.expandNode(node, true, false, true);
			}
		});
	});
}

// 定义每次数据点击后赋值
function zTreeOnClick(event, treeId, treeNode) {
	if ($("#isEditAble").val() != 'true') {
		$("#deptId", parent.document).val(treeNode.id);
		$("#deptName", parent.document).val(treeNode.name);
		$("#deptNameId").val(treeNode.name);
		$("#deptId").val(treeNode.id);
		$("#autoDeptId").val(treeNode.id);
		$("#udeptNameId").val(treeNode.name);
		$("#udeptId").val(treeNode.id);
		$("#belongDeptName").val(treeNode.name);
		$("#belongDept").val(treeNode.id);
		$("#belongDeptName").focus();
	}
};
function selectAll() {
	zTree.setting.edit.editNameSelectAll = $("#selectAll").attr("checked");
}
