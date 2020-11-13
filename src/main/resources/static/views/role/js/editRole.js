var roleid;
var objdata = "";
var pagePermissionList = "";
var pagePermissionTable;
var menuTypes = {};
var menuMap = {};

$(document).ready(function () {
    roleid = $('#roleid').val();
    deptIds = $('#deptIdsH').val();
    if (isNotBlank(deptIds)) {
        $('#deptId').val('已选择');
    }
    initTable();
    initDataPermission();
    initButtonClick();
});

function initTable() {
    layui.use(['table', 'form'], function () {
        var table = layui.table;
        var form = layui.form;
        table.render({
            id: 'menuTable',
			elem: '#menuTable',
			url: '/role/editRolesData.action?temp=' + Math.random(),
			where: {"roleid": roleid},
			cols: [[
                {
                	field: 'name',
					title: "菜单名称",
					width: 300
				}, {
					field: 'id',
					hide: true
				}, {
                    field: 'status',
					title: '状态',
					align: 'center',
					width: 200,
					templet: function (d) {
                        var html = '';
                        if (d.status) {
                            html += '<input data-menu-console-type="' + d.consoleType + '" id = ' + d.id + ' type="checkbox" checked = "checked" name="layTableCheckbox" lay-skin="switch" lay-filter="modifyStatus" lay-text="开启|关闭" data-menu-type=' + d.type + '>';
                        } else {
                            html += '<input data-menu-console-type="' + d.consoleType + '" id = ' + d.id + ' type="checkbox" name="layTableCheckbox" lay-skin="switch" lay-filter="modifyStatus" lay-text="开启|关闭" data-menu-type=' + d.type + '>';
                        }
                        return html;
                    }
                }, {
                    field: 'defaultMenu',
					title: '默认菜单',
					align: 'center',
					width: 200,
					templet: function (d) {
                        var html = '';
                        var id = 'm' + d.id;
                        if (d.defaultMenu) {
                            html += '<input id = ' + id + ' type="radio" value = ' + d.id + ' name = "defaultMenu" lay-filter="defaultMenu" checked = "">';
                        } else {
                            html += '<input id = ' + id + ' type="radio" value = ' + d.id + ' name = "defaultMenu" lay-filter="defaultMenu">';
                        }
                        return html;
                    }
                }
            ]]
            , parseData: function (res) { // 数据加载后的回调
                objdata = res;
                return res;
            }, done: function(res, curr, count){ // 数据渲染完的回调
                $.each(res.data, function (i, item) {
                	menuMap[item.consoleType] = item.name;
                    if (item.status) {
                        if (menuTypes[item.consoleType] == null) { // type：0默认，1供应商，2客户
                            menuTypes[item.consoleType] = 1;
                        } else {
                            menuTypes[item.consoleType] = menuTypes[item.type] + 1;
                        }
                    }
                });
                initPagePermission(); // 加载工作台对应页面权限
            }
        });

        // 监听工作台状态开关
        form.on('switch(modifyStatus)', function(data){
            var thisType = $(data.elem).attr('data-menu-console-type');
            if (data.elem.checked) { // 开启，相应类型数+1
                if (menuTypes[thisType] == null) { // type：0默认，1供应商，2客户
                    menuTypes[thisType] = 1;
                } else {
                    menuTypes[thisType] = menuTypes[thisType] + 1;
                }
            } else { // 关闭，相应类型数-1
                if (menuTypes[thisType] == null) { // type：0默认，1供应商，2客户
                    menuTypes[thisType] = 0;
                } else {
                    menuTypes[thisType] = menuTypes[thisType] - 1;
                }
            }
            // 重新加载页面权限表格
            var typeStr = '';
            for (var i in menuTypes) {
                if (menuTypes[i] > 0) {
                    typeStr = typeStr + i + ",";
                }
            }
            if (typeStr.length > 0) {
                typeStr = typeStr.substring(0, typeStr.length-1);
            }
            table.reload('pagePermission', {
                url: '/role/getPagePermission?type=' + typeStr + '&roleid=' + roleid
            })
        });

        // 监听数据权限选择框
        form.on('select(dataPermission)', function (data) {
            if (data.value == '4') { // 数据权限：0自己，1部门，2全部，3流程，4自定义
                $('.deptSelect').show();
            } else {
                $('.deptSelect').hide();
            }
        });
    });
}

function initButtonClick() {
    $("#startInstall").on('click', function () {
        var roleName = $("#roleName").val();
        var dataPermission = $("#dataPermission").val();
        if (!roleName && roleName.length < 1) {
            layer.msg("请输入角色名称");
        } else if (dataPermission == '') {
            layer.msg("请选择数据权限");
        } else {
            var datas = objdata.data;
            var val = $('input:radio[name="defaultMenu"]:checked').val();
            var data = {};
            var otherMenuIds = [];
            var defaultMenuId = '';
            $.each(datas, function (i, item) {
                if ($('#' + item.id).is(":checked")) {
                    if (item.id == val) {
                        defaultMenuId = val;
                    } else {
                        otherMenuIds.push(item.id);
                    }
                }
            });
            if (otherMenuIds != null && otherMenuIds.length > 0) {
                if (val == null || val == undefined) {
                    layer.msg("请选择一个默认菜单");
                    return;
                }
            }
            var pagePermission = {};
            $.each(pagePermissionList, function (i, item) {
                if ($('#' + item.desc).is(":checked")) {
                    pagePermission[item.desc] = true;
                } else {
                    pagePermission[item.desc] = false;
                }
            });
            data.roleName = roleName;
            data.defaultMenuId = defaultMenuId;
            data.otherMenuIds = otherMenuIds;
            data.roleId = roleid;
            data.pagePermission = JSON.stringify(pagePermission);
            data.dataPermission = dataPermission;
            data.deptIds = deptIds;
            $.ajax({
                type: "POST",
                async: false,
                url: "/role/edit.action",
                dataType: 'json',
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(data),
                success: function (data) {
                    var msg = "";
                    if (data.code == 200) {
                        msg = "修改角色成功";
                        setTimeout(function () {
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }, 1000);
                    } else {
                        msg = data.msg;
                    }
                    layer.msg(msg);
                }
            });
        }
    });

    // 部门选择
    $(".dept-filter").click(function (e) {
        layer.open({
            type: 2,
            title: '部门选择',
            area: ['380px', '450px'],
            btn: ['确定', '取消'],
            btnAlign: 'c',
            fixed: false, //不固定
            maxmin: true,
            content: '/department/toDeptTree.action?deptIds=' + deptIds,
            yes: function (index, layero) {
                var body = layer.getChildFrame('body', index);
                deptIds = $(body).find("input[id='choosed_dept_ids']").val();
                layer.close(index);
                $('#deptId').val('已选择');
            }
        });
    });
}

// 初始化数据权限下拉框
function initDataPermission() {
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            type: "POST",
            async: false,
            url: '/role/getDataPermission.action?temp=' + Math.random(),
            dataType: 'json',
            data: {},
            success: function (data) {
                var select = $("#dataPermission");
                select.empty().append('<option value="">---请选择类型---</option>');
                var dataPermission = $('#dataPermissionH').val();
                for (var i = 0; i < data.length; i++) {
                    if (dataPermission == data[i].value) {
                        select.append('<option value=' + data[i].value + ' selected="selected">' + data[i].name + '</option>');
                    } else {
                        select.append('<option value=' + data[i].value + '>' + data[i].name + '</option>');
                    }
                }
                layui.form.render('select');
                if (dataPermission == '4') { // 数据权限：0自己，1部门，2全部，3流程，4自定义
                    $('.deptSelect').show();
                } else {
                    $('.deptSelect').hide();
                }
            }
        });
    });
}

// 初始化页面权限
function initPagePermission() {
    var typeStr = '';
    for (var i in menuTypes) {
        if (menuTypes[i] > 0) {
            typeStr = typeStr + i + ",";
        }
    }
    if (typeStr.length > 0) {
        typeStr = typeStr.substring(0, typeStr.length-1);
    }
    layui.use('table', function () {
        var table = layui.table;
        pagePermissionTable = table.render({
            elem: '#pagePermission',
            url: '/role/getPagePermission?type=' + typeStr + '&roleid=' + roleid,
			cols: [
                [{
					field: 'subTableMenuName',
					title: '菜单名称',
					align: 'left',
					width: 250,
					templet: function (rowData) {
						return menuMap[rowData.consoleType];
					}
				},{
                    field: 'name',
                    title: '页面权限',
                    align: 'left',
                    width: 250
                }, {
                    field: 'desc',
                    hide: true
                }, {
                    field: 'status',
                    title: '状态',
                    align: 'center',
					width: 150,
					templet: function (d) {
                        var html = '';
                        if (d.status) {
                            html += '<input id = ' + d.desc + ' type="checkbox" checked = "checked" name="pageCheckbox" lay-skin="switch" lay-filter="pageStatus" lay-text="开启|关闭">';
                        } else {
                            html += '<input id = ' + d.desc + ' type="checkbox" name="pageCheckbox" lay-skin="switch" lay-filter="pageStatus" lay-text="开启|关闭">';
                        }
                        return html;
                    }
                }]
            ], parseData: function (res) {// 数据加载后回调
                pagePermissionList = res.data;
                return res;
            }, done: function(res, curr, count){
				var lastMenuName = null;
				var sumRow = 0;
				var lastEle = null;
				$('td[data-field="subTableMenuName"]').each(function () {
					if ($(this).find('div').text() != lastMenuName) {
						if (sumRow > 1) {
							lastEle.css('display', '');
							lastEle.attr('rowspan', sumRow);
						}
						lastMenuName = $(this).find('div').text();
						sumRow = 1;
						lastEle = $(this);
					} else {
						$(this).css('display', 'none');
						sumRow++;
					}
				});
				if (sumRow > 1) {
					lastEle.css('display', '');
					lastEle.attr('rowspan', sumRow);
				}
			}
        });
    });
}