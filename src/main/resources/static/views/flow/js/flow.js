var tableIns;
var insStart;
var insEnd;
$(document).ready(function () {
	initDate();
	initTable();
	initButton();
	autoCompleteUser();
});

function resetDate() {
	var nowDate = new Date();
	var today = {
		date: nowDate.getDate(),
		month: nowDate.getMonth(),
		year: nowDate.getFullYear()
	}
	$("#date").val(today.year, today.month, 1);
	$("#endDate").val(today.year, today.month, today.date);
	// 开始日期的最小值
	insStart.config.min = lay.extend({}, today, {
		date: 1,
		month: 0,
		year: 1900
	});
	// 开始日期的最大值
	insStart.config.max = lay.extend({}, today, {
		date: today.date,
		month: today.month
	});
	// 结束日期的最小值
	insEnd.config.min = lay.extend({}, today, {
		date: 1,
		month: 0,
		year: 1900
	});
	// 结束日期的最大值
	insEnd.config.max = lay.extend({}, today, {
		date: today.date,
		month: today.month
	});
}

function initDate() {
	var nowDate = new Date();
	var today = {
		date: nowDate.getDate(),
		month: nowDate.getMonth(),
		year: nowDate.getFullYear()
	}
	$("#date").val(today.year, today.month, 1);
	$("#endDate").val(today.year, today.month, today.date);
	layui.use('laydate', function () {
		var laydate = layui.laydate;
		insStart = laydate.render({
			elem : '#date',
			value : '2019-08-01',
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
			}
		});
	});
}

function search() {
	var flowName = $("#flowName").val().trim();
	var creatorid = $("#creatorid").val().trim();
	var date = $("#date").val().trim() + " 00:00:00";
	var endDate = $("#endDate").val().trim() + " 23:59:59";
	var status = new Array();
	var stateActive = $("#stateActive").attr('checked');
	if (stateActive) {
		status.push($("#stateActive").val())
	}
	var stateForbidden = $("#stateForbidden").attr('checked');
	if (stateForbidden) {
		status.push($("#stateForbidden").val())
	}
	if (status.length < 1) {
		layer.msg('请选择一个状态');
		return false;
	}
	tableIns.reload({
		url: "/flow/readPages.action?temp=" + Math.random(),
		where: {
			flowName: flowName,
			creatorid: creatorid,
			date: date,
			endDate: endDate,
			status: status.join(',')
		}
	});
}

function initTable() {
	layui.use(['table', 'form'], function () {
		var table = layui.table;
		var form = layui.form;
		tableIns = table.render({
			elem: '#flows',
			url: '',
			data: [],
			limit: 15,
			limits: [15, 30, 60, 100],
			method: 'POST',
			height: 'full-190',
			page: true,
			cols: [
				[{
						type: 'radio'
					},
					{
						field: 'flowId',
						title: 'flowId',
						align: 'center',
						hide: true
					},
					{
						field: 'flowName',
						title: '流程名称',
						align: 'center',
						width: '27%',
					},
					{
						field: 'flowTypeDesc',
						title: '流程类型',
						align: 'center',
						width: '10%',
					}, {
						field: 'status',
						title: '状态',
						align: 'center',
						width: '10%',
						templet: function (row) {
							if (row.status == '1') {
								return '<div style = "background-color:#D6E9CB;margin:0px;padding:0px;height:150%;width:100%"> 激活 </div>';
							} else if (row.status == '0') {
								return '<div style = "background-color:#E9C2C7;margin:0px;padding:0px;height:150%;width:100%"> 禁用 </div>';
							}
						}
					},
					{
						field: 'startNodeName',
						title: '开始节点',
						align: 'center',
						width: '20%',
					},
					{
						field: 'startNodeId',
						title: 'startNodeId',
						align: 'center',
						hide: true
					},
					{
						field: 'creatorRealName',
						title: '创建人',
						align: 'center',
						width: '10%',
					},
					{
						field: 'wTime',
						title: '创建时间',
						align: 'center',
						width: '20%',
					}
				]
			],
			parseData: function (res) { // res 即为原始返回的数据
				return {
					"code": 0, // 解析接口状态
					"count": res.data.count, // 解析数据长度
					"data": res.data.data
					// 解析数据列表
				};
			}
		});
		table.on('row(flows)', function (obj) {
			obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
		});
	});
}

function initButton() {
	var toolbar = new Toolbar({
		renderTo: 'button_toolbar',
		items: [{
			type: 'button',
			text: '添加',
			icon: 'layui-icon-add-circle',
			bodyStyle: 'layui-btn-normal',
			handler: function () {
				openTab('添加流程', "/flow/toAddFlow.action?temp=" + Math.random(), "", "");
			}
		}, {
			type: 'button',
			text: '修改',
			icon: 'layui-icon-edit',
			bodyStyle: 'layui-btn-normal',
			handler: function () {
				var checkStatus = layui.table.checkStatus('flows'),
					data = checkStatus.data;
				if (data != undefined && data.length == 1) {
					var flowId = data[0].flowId;
					openTab('修改流程', "/flow/toEditFlow.action?temp=" + Math.random() + "&flowId=" + flowId, "", "");
				} else {
					layer.open({
						content: "请选择一个流程!",
						time: 2000
					});
				}
			}
		}, {
			type: 'button',
			text: '激活/禁用',
			icon: 'layui-icon-edit',
			bodyStyle: 'layui-btn-normal',
			handler: function () {
				var checkStatus = layui.table.checkStatus('flows'),
					data = checkStatus.data;
				if (data != undefined && data.length == 1) {
					var flowId = data[0].flowId
					$.ajax({
						type: "POST",
						async: false,
						url: "/flow/enableFlow.action",
						dataType: 'json',
						data: {
							flowId: flowId
						},
						success: function (data) {
							var msg = data.msg;
							layer.msg(msg);
							tableIns.reload();
						}
					});
				} else {
					layer.open({
						content: "请选择一个流程!",
						time: 2000
					});
				}
			}
		}],
	});

	toolbar.render();
	$("#btn-search").click(function () {
		search();
	});
	$("#btn-reset").click(function () {
		clearAll();
	});
}

function clearAll() {
	$("#flowName").val('');
	$("#creatorid").val('');
	$("#creator").val('');
	$('#stateActive').attr('checked', true);
	$('#stateForbidden').attr('checked', false);
	resetDate();
	layui.use('form', function() {
		var form = layui.form, layer = layui.layer;
		form.render();
	});
}

function autoCompleteUser() {
	layui.config({
		base: '/common/js/'
	}).extend({ // 设定模块别名
		autocomplete: 'autocomplete'
	});
	layui.use('autocomplete', function () {
		var autocomplete = layui.autocomplete;
		autocomplete.render({
			elem: $('#creator'),
			hidelem: $('#creatorid'),
			url: '/account/queryByAuto.action',
			template_val: '{{d.ossUserId}}',
			template_txt: '{{d.loginName}} <span class=\'layui-badge layui-bg-gray\'>{{d.realName}}</span>',
			onselect: function (resp) {
				$("#creator").val(resp.realName);
				$("#creatorid").val(resp.ossUserId);
			}
		});
	})
}