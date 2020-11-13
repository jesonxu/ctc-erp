$(document).ready(function () {
	initTable();
	initButton();
	initSelectData();
});

var tableIns;
var editIndex;

// 初始化表格
function initTable() {
	layui.use(['table'], function () {
		var table = layui.table;
		//列表
		tableIns = table.render({
			elem: '#userList',
			url : "/account/readPages.action?temp=" + Math.random(),
			height: 'full-150',
			page: true,
			limit: 15,
			limits: [15,30,60,100],
			method: 'POST',
			cols: [[{
				type: 'radio'
			},{
				field: 'ossUserId',
				title: 'ossUserId',
				align: 'center',
				hide: true
			}, {
				field: 'realName',
				title: '姓名',
				align: 'center',
				width: '18%',
			}, {
				field: 'loginName',
				title: '登录名',
				align: 'center',
				width: '10%',
			}, {
				field: 'deptName',
				title: '部门',
				align: 'center',
				width: '20%',
			},{
				field: 'roleName',
				title: '角色',
				align: 'center',
				width: '25%',
			},{
				field: 'uState',
				title: '状态',
				align: 'center',
				width: '10%',
			},{
                title: '操作',
                width: '10%',
                align: 'center',
                fixed: 'right',
                toolbar: '#table-customer-admin'
			}]]
			, parseData: function (res) { //res 即为原始返回的数据
				// debugger
				return {
					"code": 0, //解析接口状态
					"count": res.data.count, //解析数据长度
					"data": res.data.data //解析数据列表
				};
			}
		});
		table.on('row(userList)', function(obj){////注：test是table原始容器的属性 lay-filter="对应的值"
			obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
		});
		table.on('tool(userList)', function(obj) {
			if (obj.event === 'update') {
				var data = obj.data;
				var ossUserId = data.ossUserId;
				$('#editAccount #loginNameH').text('').text(data.loginName);
				$('#editAccount #realNameH').text('').text(data.realName);
				$('#editAccount #deptNameH').text('').text(data.deptName);
				var roles = [];
				var roleId = data.roleId;
				var roleIds;
				if (roleId != null && roleId.length > 0){
					roleIds = data.roleId.split(','); // 角色Id字符串数组
					roleIds.forEach(function (item, index, roleIds) {
						roles.push(item);
					});
				}
				var formSelects = layui.formSelects;
				formSelects.btns('selectRoles', ['select', 'remove'], {show: 'name'});
				formSelects.value('selectRoles', roles); // 下拉框选中已有的角色

				var jobType = [];
				var jobTypeId = data.jobType;
				var jobTypeIds;
				if (jobTypeId != null && jobTypeId.length > 0){
					jobTypeIds = jobTypeId.split(','); // 角色Id字符串数组
					jobTypeIds.forEach(function (item, index, jobTypeIds) {
						jobType.push(item);
					});
				}
				formSelects.btns('selectJobType', ['select', 'remove'], {show: 'name'});
				formSelects.value('selectJobType', jobType); // 下拉框选中已有的角色
				editIndex = layer.open({
					title: ['修改员工信息', 'font-size:18px;' ],
					type: 1,
					id: 'lay_edit',
					content: $("#editAccount"), // 展示的容器
					btn: ['确定', '取消'],
					area: ['400px', '500px'],
					yes: function(){
						var roles = '';
						var roleselect = layui.formSelects.value('selectRoles'); // 获取下拉框的值
						if (roleselect) {
							roleselect.forEach(function (item, index, roleselect) {
								roles += item.value + ',';
							});
						}
						var flag = true;
						if (roles.length > 0) {
							roles = roles.substring(0, roles.length - 1);
						} else {
							layer.open({ content: "请选择角色！", time: 2000 });
							flag = false;
						}

						var jobType = '';
						var jobTypeSelect = layui.formSelects.value('selectJobType'); // 获取下拉框的值
						if (jobTypeSelect) {
							jobTypeSelect.forEach(function (item, index, jobTypeSelect) {
								jobType += item.value + ',';
							});
						}
						if (flag) {
							var ajaxData = {
								"ossUserId": ossUserId.trim(),
								"roleId": roles.trim(),
								"jobType": jobType.trim()
							};
							$.ajax({
								type: "POST",
								async: false,
								url: "/account/editAccount.action?temp=" + Math.random(),
								dataType: 'json',
								data: ajaxData,
								success: function (data) {
									if (data.code == 200) {
										closeEdit();
										reloadTable();
										layer.msg(data.msg);
									} else {
										layer.msg('修改失败');
									}
								}
							});
						}
					}
				});
			}
		});
	});
}
// 初始化下拉框数据
function initSelectData() {
	var formSelects = layui.formSelects;
	// 初始化角色
	formSelects.data('selectRoles', 'server', {
		url: '/account/getSelectRole.action',
		linkageWidth: 80,
	});

	// 初始化岗位类型
	formSelects.data('selectJobType', 'server', {
		url: '/account/getJobType.action',
		linkageWidth: 80,
	});

	$.get('/account/getSelectRole', function (res) {
		var roles = JSON.parse(res);
		if (isNotBlank(roles)) {
			var roleSelect = $('#role');
			var options = '<option value="">--请选择--</option>';
			$.each(roles, function (index, item) {
				options += "<option value='" + item.value + "'>" + item.name + "</option>";
			})
			roleSelect.html(options);
		}
	})
}

// 初始化按钮
function initButton() {
	// 部门选择
	$("#deptName").click(function() {
		layer.open({
			type : 2,
			title : '部门选择',
			area: ['360px', '270px'],
			id : 'lay_dept',
			btn : [ '确定', '取消' ],
			content : '/account/toDepts.action?isEditAble=false',
		});
	});
	
	// 清空部门
	$("#empty").click(function() {
		$("#deptName").val('');
		$("#deptId").val('');
	});
	
	$("#btn-search").click(function (){
		search();
	});
	
	$("#btn-reset").click(function (){
		clearAll();
		layui.use('form', function() {
			var form = layui.form, layer = layui.layer;
			form.render();
		});
	});
}

function reloadTable() {
	tableIns.reload();
}

function closeEdit(){
	layui.layer.close(editIndex);
}

function search(){
	var realName = $("#realName").val();
	var deptId = $("#deptId").val();
	var accountNumber = $("#accountNumber").val();
	
	var ustate = new Array();
	var stateActive = $("#userStatus_active").attr('checked');
	if(stateActive) {
		ustate.push($("#userStatus_active").val())
	}
	var stateForbidden = $("#userStatus_disabled").attr('checked');
	if(stateForbidden) {
		ustate.push($("#userStatus_disabled").val())
	}
	if(ustate.length < 1) {
		layer.msg("请至少选择一个状态！");
		return false;
	}
	tableIns.reload({
		page: { curr: 1},
		where: {
			realName : realName,
			deptId : deptId,
			accountNumber : accountNumber,
			roleId: $('#role').val(),
			ustate : ustate.join(',')
		}
	});
}

function clearAll() {
	$("#realName").val('');
	$("#deptName").val('');
	$("#deptId").val('');
	$("#accountNumber").val('');
	$('#userStatus_active').attr('checked', true);
	$('#userStatus_disabled').attr('checked', false);
}
