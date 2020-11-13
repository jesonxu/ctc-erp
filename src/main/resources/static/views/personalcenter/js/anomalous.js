// 闭包防止参数污染
layui.use(['laydate', 'layer', 'form', 'element', 'table'], function () {
	// 获取layui js 对象
	var laydate = layui.laydate;
	var layer = layui.layer;
	var element = layui.element;
	var table = layui.table;

	// 点击所选参数
	var openParams = {
		type: -1,
		openDeptId: '',
		openUserId: '',
		queryDate: '',
		mark: ''
	}

	init();
	
	function init() {
		initDate();
		loadDeptAndUserInfo();
		initTable();
	}

	var insStart;
	var insEnd;

	function initDate() {
		insStart = laydate.render({
			elem : '#startDate',
			value : new Date(),
			format : 'yyyy-MM-dd',
			max : 0,
			type : 'date',
			trigger: 'click',
			done: function (value, date) {
				// 更新结束日期的最小日期
				insEnd.config.min = lay.extend({}, date, {
					date: date.date,
					month: date.month - 1
				});
				reloadTable();
			}
		});

		insEnd = laydate.render({
			elem: '#endDate',
			value: new Date(),
			format: 'yyyy-MM-dd',
			max: 0,
			type: 'date',
			trigger: 'click',
			done: function (value, date) {
				// 更新开始日期的最大日期
				insStart.config.max = lay.extend({}, date, {
					date: date.date,
					month: date.month - 1
				});
				reloadTable();
			}
		});
	}

	// 加载部门和销售信息
	function loadDeptAndUserInfo() {
		$.ajax({
			type: "POST",
			url: "/anomalous/queryDept?temp=" + Math.random(),
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
						deptItem.html("<div style='padding-left: 20px;'>暂无部门</div>");
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
					} else { // 点击部门
						recordDeptAndUser(0, itemId, '');
						loadDeptAndUserInfo();
					}
					reloadTable();
				}
			}
		});
		pannel.init(ele);
		element.render("collapse", filter);
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
	}

	function initTable() {
		table.render({
			elem: '#messageTable',
			height: 'full-120',
			// width: '80%',
			url: '',
			data: [],
			limit: Number.MAX_VALUE,
			cols: [
				[{
					field: 'messageId',
					hide: true
				}, {
					field: 'messageSourceId',
					hide: true
				}, {
					field: 'wtime',
					title: '时间',
					align: 'center',
					width: '10%'
				}, {
					field: 'infoTypeName',
					title: '异常类型',
					align: 'center',
					width: '10%',
				}, {
					field: 'deptName',
					title: '部门',
					align: 'left',
					width: '10%'
				}, {
					field: 'realName',
					title: '员工',
					align: 'left',
					width: '10%'
				}, {
					field: 'messageDetail',
					title: '异常描述',
					align: 'left',
					width: '60%'
				}]
			],
			parseData: function (res) {
				return {
					code: 0,
					msg: res.msg,
					data: res.data
				}
			}
		});
	}

	function reloadTable() {
		table.reload("messageTable", {
			url: '/anomalous/queryAnomalousMsg',
			where: {
				startDate: $('#startDate').val(),
				endDate: $('#endDate').val(),
				deptId: openParams.openDeptId,
				userId: openParams.openUserId
			}
		})
	}
});

//文件下载
function downloadFile(ele, file) {
	if (typeof file == 'object') {
		 var fileParams = "filePath=" + encodeURIComponent(file.filePath) + "&fileName=" + encodeURIComponent(file.fileName) + "&r=" + Math.random();
		 window.location.href = "/operate/downloadFile?" + fileParams;
	}
}