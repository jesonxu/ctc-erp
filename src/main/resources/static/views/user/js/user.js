var import_file_md5_info = [];
var hasUpdated = false;
// 闭包防止参数污染
layui.use(['laydate', 'layer', 'form', 'element', 'table'], function () {
	// 获取layui js 对象
	var layer = layui.layer;
	var element = layui.element;
	var form = layui.form;
	var formSelects = layui.formSelects;

	// 点击所选参数
	var openParams = {
		type: -1,
		openDeptId: '',
		openUserId: '',
	}

	loadDeptAndUserInfo();
	initSelectData();
	initButton();

	// 加载部门结构
	function loadDeptAndUserInfo() {
		$.ajax({
			type: "POST",
			url: "/department/queryDeptAndUser?temp=" + Math.random(),
			dataType: "JSON",
			data: {
				deptId: openParams.openDeptId
			},
			async: true,
			success: function (result) {
				if (result.code === 200 ){
					var data = result.data;
					var deptItem = $('#deptAndUser');
					if (openParams.type != -1) {
						deptItem = $('div[data-content-id="' + openParams.openDeptId + '_' + openParams.type + '"]');
					}
					if (!isNull(data) && data.length > 0 ){
						deptItem.empty();
						createOrgStructureHtml(deptItem, data)
					} else {
						deptItem.html("<div style='padding-left: 20px;'>无子部门</div>");
					}
				} else {
					layer.msg(result.msg);
				}
			}
		});
	}
	
	// 创建组织架构html
	function createOrgStructureHtml(ele, data) {
		var thisEle = $(ele);
		thisEle.empty();
		var filter = new Date().getTime() + '';
		var html = '<div class="layui-collapse" lay-accordion lay-filter="' + filter + '" style="padding-left: 5px;">';
		if (data && data.length > 0) {
			$(data).each(function (i, item) {
				if (item.type == 0) {
					// 部门
					html += '<div class="layui-colla-item">'
						+ '<div class="layui-colla-title" data-my-size="title-size-0"'
						+ ' flow_ent_count="' + 0 + '"'
						+ ' data-my-id="' + item.id + '"'
						+ ' data-my-tag="' + item.name + '"'
						+ ' data-my-opts-type="' + item.type + '">'
						+ item.name
						+ '</div>'
						+ '<div class="layui-colla-content" data-content-id="' + item.id + '_' + item.type + '">'
						+ '</div>'
						+ '</div>';
				} else if (item.type == 1) {
					// 用户
					html += '<div class="layui-colla-item">'
						+ '<div class="layui-colla-title" data-my-size="title-size-0"'
						+ ' flow_ent_count="' + 0 + '"'
						+ ' data-my-id="' + item.id + '"'
						+ ' data-my-tag="' + item.name + '"'
						+ ' data-my-folder="false"'
						+ ' data-my-size="title-size-2"'
						+ ' data-my-opts-type="' + item.type + '">'
						+ item.name
						+ '</div>'
						+ '<div class="layui-colla-content" data-content-id="' + item.id + '_' + item.type + '">'
						+ '</div>'
						+ '</div>';
				}
			});
			html += '</div>';
			thisEle.append(html);
		}
		initOrgStructurePannel(thisEle, filter);
	}
	
	// 初始化组织架构面板
	function initOrgStructurePannel(ele, filter) {
		var pannel = new myPannel({
			openItem: function (item, itemId, optsType) {
				var t = $(item).clone();
				t.find('.layui-badge').remove();
				var itemName = $(t.find('.my_text_title')[0]).text().trim();
				if (optsType !== undefined && optsType !== null && optsType !== '') {
					if (optsType == 1) { // 点击用户
						recordDeptAndUser(1, '', itemId);
						loadUserInfo();
					} else { // 点击部门
						recordDeptAndUser(0, itemId, '');
						// 加载子部门和部门下的用户
						loadDeptAndUserInfo();
					}
				}
			}
		});
		pannel.init(ele);
		element.render("collapse", filter);
	}

	/**
	 * 获取员工信息
	 */
	function loadUserInfo() {
		$.post('/user/getUserInfo', {id: openParams.openUserId}, function (res) {
			if (res.code == 200) {
				showUserInfo(res.data);
			} else {
				layer.msg(res.msg);
			}
		})
	}

	/**
	 * 回显员工信息
	 * @param userInfo	员工信息
	 */
	function showUserInfo(userInfo) {
		$('#uRealName').text(userInfo.userName);
		$('#uSex').val(userInfo.sex);
		$('#uSexSelect').val(userInfo.sex);
		$('#uDeptName').text(userInfo.deptName);
		$('#uSuperior').text(userInfo.superior);
		$('#uOfficeAddress').text(userInfo.officeAddress);
		$('#uIdentity').text(userInfo.identityTypeName);
		$('#uMail').text(userInfo.mail);
		$('#uTelephone').text(userInfo.telephone);
		$('#uMobilePhone').text(userInfo.mobilePhone);
		$('#uEntryTime').text(userInfo.entryTime);
		$('#uGraduationDate').text(userInfo.graduationDate);
		$('#uRoleIds').val(userInfo.roleIds);
		$('#uJobTypes').val(userInfo.jobTypes);
		$('#uMaritalStatus').val(userInfo.maritalStatus);
		$('#uMaritalSelect').val(userInfo.maritalStatus);

		var roles = [];
		var roleIds = userInfo.roleIds;
		if (isNotBlank(roleIds)){
			roleIds.split(',').forEach(function (item) {
				roles.push(item);
			});
		}
		formSelects.btns('uRoleSelect', ['select', 'remove'], {show: 'name'});
		formSelects.value('uRoleSelect', roles); // 下拉框选中已有的角色

		var jobType = [];
		var jobTypes = userInfo.jobTypes;
		if (isNotBlank(jobTypes)){
			jobTypes.split(',').forEach(function (item) {
				jobType.push(item);
			});
		}
		formSelects.btns('uJobSelect', ['select', 'remove'], {show: 'name'});
		formSelects.value('uJobSelect', jobType); // 下拉框选中已有的角色

		form.render();
	}

	// 记录用户和部门
	function recordDeptAndUser(type, deptId, userId) {
		clearParams();
		openParams.type = type;
		openParams.openDeptId = deptId;
		openParams.openUserId = userId;
	}
	
	// 清空所有的记录数据
	function clearParams() {
		openParams.type = -1;
		openParams.openDeptId = '';
		openParams.openUserId = '';
		openParams.keyword = '';
		openParams.roleId = '';
		openParams.status = '';
		clearUserInfo();
	}

	// 清空右侧员工详情
	function clearUserInfo() {
		$('#uRealName').text('');
		$('#uSex').text('');
		$('#uDeptName').text('');
		$('#uSuperior').text('');
		$('#uOfficeAddress').text('');
		$('#uIdentity').text('');
		$('#uMail').text('');
		$('#uTelephone').text('');
		$('#uMobilePhone').text('');
		$('#uEntryTime').text('');
		$('#uGraduationDate').text('');
		$('#uRoleIds').val('');
		$('#uJobTypes').val('');
		$('#uMaritalStatus').val('');
		formSelects.value('uRoleSelect', []); // 清空下拉框选中
		formSelects.value('uJobSelect', []); // 清空下拉框选中
		$('#btn-update').hide();
		form.render();
	}

	// 初始化下拉框数据
	function initSelectData() {
		// 初始化角色
		formSelects.data('uRoleSelect', 'server', {
			url: '/user/getRoleSelect',
			linkageWidth: 80,
		});

		// 初始化岗位类型
		formSelects.data('uJobSelect', 'server', {
			url: '/user/getJobTypeSelect',
			linkageWidth: 80,
		});

		// 收起下拉框时检查是否做了改动
		formSelects.closed(function (id) {
			hasUpdated = false;
			// 选择框选项改变了，展示保存修改按钮
			if (isBlank(openParams.openUserId)) {
				return ;
			}
			var originIds = '';
			if (id === 'uRoleSelect') {
				originIds = $('#uRoleIds').val();
			} else {
				originIds = $('#uJobTypes').val();
			}
			var selectedArr = formSelects.value(id, 'val');
			var selectedIds = formSelects.value(id, 'valStr');
			if (originIds.length !== selectedIds.length) {
				hasUpdated = true;
			} else {
				$(selectedArr).each(function (index, item) {
					if (originIds.indexOf(item) === -1) {
						hasUpdated = true;
						return false;
					}
				});
			}
			if (hasUpdated) {
				$('#btn-update').show();
				form.render();
			}
		})

		// 搜索条件的角色下拉框
		$.get('/user/getRoleSelect', function (res) {
			var roles = JSON.parse(res);
			if (isNotBlank(roles)) {
				var roleSelect = $('#role');
				var options = '<option value="">--请选择--</option>';
				$.each(roles, function (index, item) {
					options += "<option value='" + item.value + "'>" + item.name + "</option>";
				})
				roleSelect.html(options);
				form.render('select');
			}
		});

		// 员工信息的婚姻状况下拉框
		$.get('/user/getMaritalSelect', function (res) {
			var data = JSON.parse(res);
			if (isNotBlank(data)) {
				var maritalSelect = $('#uMaritalSelect');
				var options = '';
				$.each(data, function (index, item) {
					options += "<option value='" + item.value + "'>" + item.name + "</option>";
				})
				maritalSelect.html(options);
				form.render('select');
			}
		});

		// 监听婚姻状况下拉框
		form.on('select(uMaritalSelect)', function (data) {
			hasUpdated = false;
			if (isBlank(openParams.openUserId)) {
				return ;
			}
			var originStatus = $('#uMaritalStatus').val();
			if (data.value != originStatus) {
				hasUpdated = true;
			}
			if (hasUpdated) {
				$('#btn-update').show();
				form.render();
			}
		});

		// 监听性别下拉框
		form.on('select(uSexSelect)', function (data) {
			hasUpdated = false;
			if (isBlank(openParams.openUserId)) {
				return ;
			}
			var originSex = $('#uSex').val();
			if (data.value != originSex) {
				hasUpdated = true;
			}
			if (hasUpdated) {
				$('#btn-update').show();
				form.render();
			}
		})
	}

	function initButton() {
		// 部门选择
		$("#deptName").unbind().bind('click', function () {
			layer.open({
				type: 2,
				title: '部门选择',
				area: ['380px', '450px'],
				btn: ['确定', '取消'],
				btnAlign: 'c',
				fixed: false, //不固定
				maxmin: true,
				content: '/department/toDeptTree.action?check=no',
				yes: function (index, layero) {
					var body = layer.getChildFrame('body', index);
					var result = $(body).find("input[id='clicked_dept_id']");
					$('#deptId').val(result.val());
					$('#deptName').val(result.attr('data-name'));
					layer.close(index);
				}
			});
		});

		// 清空部门
		$("#empty").unbind().bind('click', function () {
			$("#deptName").val('');
			$("#deptId").val('');
		});

		$('#btn-search').unbind().bind('click', function () {

			var statusCheck = $("input[name='userStatus']:checked");
			if (statusCheck.length === 0) {
				return layer.msg("至少勾选一个状态");
			}
			var status = [];
			$(statusCheck).each(function (index, item) {
				status.push(item.value)
			})
			var queryData = {
				keyword: $('#keyword').val(),
				deptId: $('#deptId').val(),
				roleId: $('#role').val(),
				status: status.join(',')
			}
			$.post('/user/queryUser', queryData, function (res) {
				if (res.code == 200) {
					var data = res.data;
					var deptItem = $('#deptAndUser');
					createOrgStructureHtml(deptItem, data);
				}
			})
		});

		$('#btn-reset').unbind().bind('click', function () {
			$('#keyword').val('');
			$('#deptName').val('');
			$('#deptId').val('');
			$('#role').val('');
			clearParams();
			loadDeptAndUserInfo();
		});

		$('#btn-import').unbind().bind('click', function () {
			layer.open({
				type: 2,
				area: ['700px', '450px'],
				fixed: false, //不固定
				maxmin: true,
				content: '/user/toUploadPage.action',
				cancel: function(index, layero){
					var md5arr = [];
					for (var fileIndex = 0; fileIndex < import_file_md5_info.length; fileIndex++) {
						md5arr.push(import_file_md5_info[fileIndex].md5);
					}
					if (md5arr.length >0){
						delUploadFile(md5arr);
					}
					return true;
				}
			});
		});

		$('#btn-update').unbind().bind('click', function () {
			var data = {
				ossUserId: openParams.openUserId,
				roleIds: formSelects.value('uRoleSelect', 'valStr'),
				jobTypes: formSelects.value('uJobSelect', 'valStr'),
				maritalStatus: $('#uMaritalSelect').val(),
				sex: $('#uSexSelect').val()
			}
			$.post('/user/editUser', data, function (res) {
				layer.msg(res.msg);
				if (res.code === 200 || res.code === '200') {
					$('#uRoleIds').val(data.roleIds);
					$('#uJobTypes').val(data.jobTypes);
					$('#uMaritalStatus').val(data.maritalStatus);
					$('#uSet').val(data.sex);
					$('#btn-update').hide();
				}
			})
		});

		$('#btn-leave').unbind().bind('click', function () {
			window.open('/userLeave/toUserLeave');
		})
	}
});
