var ppValue;
$(document).ready(function () {
	initTable();
	initButton();
});
var tableIns;
function initTable() {
	layui.use(['table','form'], function () {
		var table = layui.table;
		var form = layui.form;
		tableIns = table.render({
			elem: '#roles',
			url: "/role/readPages.action?temp=" + Math.random(),
			height: 'full-120',
			contentType: 'application/json',
			even: true,
			page: true,
			method: 'POST',
			cols: [
				[{
					type: 'radio'
				}, {
					field: 'roleid',
					title: '角色id',
					align: 'center',
					hide: true
				}, {
					field: 'rolename',
					title: '角色名称',
					align: 'center'
				}, {
					field: 'wtime',
					title: '创建时间',
					align: 'center',
				}]
			],
			parseData: function (res) { //res 即为原始返回的数据
				return {
					"code": 0, //解析接口状态
					"count": res.data.count, //解析数据长度
					"data": res.data.data //解析数据列表
				};
			}
		});
		
		table.on('row(roles)', function(obj){////注：test是table原始容器的属性 lay-filter="对应的值"
			obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
       });
	});

}

function initButton() {
	var toolbar = new Toolbar({
		renderTo: 'role_toolbar',
		items: [{
			type: 'button',
			text: '添加',
            icon : 'layui-icon-add-circle',
            bodyStyle : 'layui-btn-normal',
			handler: function () {
				openTab('添加', "/role/toAddRolePage.action?temp=" + Math.random(), "", "");
			}
		}, {
			type: 'button',
			text: '修改',
            icon : 'layui-icon-edit',
            bodyStyle : 'layui-btn-normal',
			handler: function () {
				var checkStatus = layui.table.checkStatus('roles')
					, data = checkStatus.data;
				if (data != undefined && data.length == 1) {
					var roleid = data[0].roleid
					openTab('修改', "/role/toEditRolePage.action?temp=" + Math.random() + "&roleid=" + roleid, "", "");
				} else {
					layer.open({ content: "请选择要修改的角色!", time: 2000 });
				}
			}
		}, {
			type: 'button',
			text: '刷新',
            icon : 'layui-icon-refresh-3',
            bodyStyle : 'layui-btn-primary',
			useable: 'T',
			handler: function () {
				tableIns.reload({
					page: { curr: 1 },
				});
			}
		}],
	});
	toolbar.render();
}