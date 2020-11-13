var tableIns;
var editIndex;
var laydate;
var form;
var table ;
var layer;
var element;
var val_dateNum = '1';
layui.use(['laydate', 'form', 'element', 'layer','table'], function () {
	laydate = layui.laydate;
	form = layui.form;
	layer = layui.layer;
	element = layui.element;
	table = layui.table;
	//日期范围
	laydate.render({
		elem: '#rangeData',
		range: true
	});
	// 周月季年近三年数据
	form.on("radio(layui-date)", function (data) {
		val_dateNum = data.value;
		initCount(val_dateNum);
	});
	initToolBar();
	initTableUnread();
	$("#return-workbench").click(function () {
		window.history.back(-1);
	});
});



// 初始化数据
function tdTitle() {
	$('th').each(function(index,element){
		$(element).attr('title',$(element).text());
	});
	$('td').each(function(index,element){
		$(element).attr('title',$(element).text());
	});
}

// 初始化统计计数
function initCount(num) {
	var dateLinetype;
	if (num) {
		dateLinetype = num;
	} else {
		dateLinetype = "1";
	}
	var ajaxData = {
		"dateLinetype": dateLinetype
	};
	$.ajax({
		type: "POST",
		async: false,
		url: "/messageCenter/getMsgCount?temp=" + Math.random(),
		dataType: 'json',
		data: ajaxData,
		success: function (data) {
			if (data.code == 200) {
				var dataCount = data.data;
				$.each(dataCount,function(index,item){
	                if (item.infoType == "0") {
	                	$("#unread").text(item.count);
	                } else if (item.infoType == "1") {
	                	$("#customer cite").text(item.count);
	                } else if (item.infoType == "2") {
	                	$("#supplier cite").text(item.count);
	                } else if (item.infoType == "3") {
	                	$("#customerLog cite").text(item.count);
	                } else if (item.infoType == "4") {
	                	$("#supplierLog cite").text(item.count);
	                }
	            });
			} else {
				layer.msg(data.msg);
			}
		}
	});
}

// 初始化未读消息表格
function initTableUnread() {
	layui.use(['table'], function () {
		var table = layui.table;
		//列表
		tableIns = table.render({
			elem: '#unreadMessage',
			url : "/messageCenter/getMessageByPage.action?temp=" + Math.random(),
			//toolbar: '#toolbarDemo', //开启头部工具栏，并为其绑定左侧模板
			height: 'full-150',
			defaultToolbar: [],
			page: true,
			limit: 15,
			where: {
				// infotype:"0",
				time: $("#startTime").val(),
				userId: $("#userId").val(),
				msgType: $("#messageType").val()
			},
			limits: [15,30,60,100],
			method: 'POST',
			cols: [[{
				type: 'checkbox',
				fixed: 'left'
			},{
				field: 'messageid',
				hide: true,
			}, {
                type: 'numbers',
                title: '序号'
            }, {
				field: 'wtime',
				title: '时间',
				align: 'left',
				width: 145
			}, {
				field: 'messagedetail',
				title: '内容预览',
				align: 'left',
				width: '70%'
			}, {
				field: 'infotype',
				title: '消息类型',
				align: 'center',
				width: 100,
				templet: function (res) {
					var infoType = res.infotype;
					if (infoType === '1' || infoType === 1) {
						return "新增客户";
					} else if (infoType === '2' || infoType === 2) {
						return "新增供应商";
					} else if (infoType === '3' || infoType === 3) {
						return "新增联系日志";
					} else if (infoType === '4' || infoType === 4) {
						return "新增联系供应商日志";
					} else if (infoType === '5' || infoType === 5) {
						return "公告信息";
					} else if (infoType === '6' || infoType === 6) {
						return "客户警告通知";
					} else if (infoType === '7' || infoType === 7) {
						return "账单警告通知";
					} else if (infoType === '8' || infoType === 8) {
						return "流程警告通知";
					} else {
						return "未知";
					}
				}
			}, {
				field: 'state',
				align: 'center',
				hide: true
			},{
                title: '操作',
                width: 80,
                align: 'center',
                toolbar: '#table-read'
			}]]
			, parseData: function (res) { //res 即为原始返回的数据
				return {
					"code": 0, //解析接口状态
					"count": res.data.count, //解析数据长度
					"data": res.data.data //解析数据列表
				};
			}
			, done:function (res, curr, count) {
                var $table = $('.layui-table').eq(1);
        		if($table.length > 0){
        		//遍历所有行
        			$table.find('tr').each(function(){
        				var state = $(this).find('td[data-field="state"]').text();
        				if(state == "0"){   //给状态为0的数据行设置背景色
        					$(this).attr('style',"background:#f8f8f8;;color:#999");
        				}
        			})
        		}
				// 自动搜索 用户
				// initToolBar();
                tdTitle();
            }
		});
		//监听工具条
		table.on('tool(unreadMessage)', function(obj){
		    if(obj.event === 'read'){
		      var data = obj.data;
		      var messageid = {
		    	"messageid": data.messageid
		      };
		      $.ajax({
		  		type: "POST",
		  		async: false,
		  		url: "/messageCenter/updateMsgDetail?temp=" + Math.random(),
		  		dataType: 'json',
		  		data: messageid,
		  		success: function (data) {
		  			if (data.code == 200) {
		  				/*layer.msg('ID：'+ data.messageid + ' 的阅读操作');*/
		  				reloadTable();
		  			} else {
		  				layer.msg(data.msg);
		  			}
		  		}
		      });
		    }
		});
	});
	initCount();
}

/**
 * 重新加载表格
 */
function reloadTable() {
	tableIns.reload({
		where: {
			time: $("#startTime").val(),
			userId: $("#userId").val(),
			msgType: $("#messageType").val()
		}
	});
}

function closeEdit(){
	layui.layer.close(editIndex);
}


// 客户统计列表公共方法
function pumpLsit(infotype) {
	window.infotype = infotype; //infoype 是需要传递的数据
	window.dateLineType = val_dateNum;
	var editPwdIndex = layer.open({
        title: ['客户统计', 'font-size:18px;'],
        type: 2,
        area: ['100%', '100%'],
        fixed: true, //不固定
        fix: false,
        maxmin: true,
        content: '/messageCenter/toMsgCenterDetail.action?temp=temp=' + Math.random()
   });
}


$("#customer").click(function() {
	pumpLsit(1)
});
$("#supplier").click(function() {
	pumpLsit(2)
});
$("#customerLog").click(function() {
	pumpLsit(3)
});
$("#supplierLog cite").click(function() {
	pumpLsit(4)
});


/**
 * 初始化按钮
 */
function initToolBar() {
	layui.use(['autocomplete','laydate'], function () {
		var autocomplete = layui.autocomplete;
		// 自动补全目标销售
		autocomplete.render({
			elem: $('#userName')[0],
			hidelem: $('#userId'),
			url: '/user/queryUserByAuto',
			template_val: '{{d.realName}}',
			template_txt: '<div>{{d.realName}} <span class="layui-badge layui-bg-gray">{{d.deptName}}</span></div>',
			template_name: '{{d.realName}}',
			onselect: function (resp) {
				$("#userId").val(resp.ossUserId);
				$('#userId').attr("data-user-name",resp.realName);
			}
		});
		$("#userName").change(function (){
			var userIdEle = $("#userId");
			var realName = userIdEle.attr("data-user-name");
			var displayName = $(this).val();
			if (displayName !== realName) {
				userIdEle.val("");
				userIdEle.attr("data-user-name", "");
			}
		});
	});
	var laydate = layui.laydate;
	// 当前时间(默认当前月)
	var now = new Date();
	var year = now.getFullYear();
	var month = (now.getMonth() + 1) + "";
	if (month.length === 1) {
		month = "0" + month;
	}
	var day = now.getDate() + "";
	if (day.length === 1) {
		day = "0" + day;
	}
	// 开始时间
	laydate.render({
		elem: '#startTime',
		range: '~',
		value: year + "-" + month + "-01 ~ " + year + "-" + month + "-" + day,
		max:0
	});

	//加载初始化 消息类型
	$.ajax({
		type: "POST",
		dataType: 'json',
		async:false,
		url:"/messageCenter/messageType",
		success:function (resp) {
			if (!isBlank(resp)){
				var typeArr = [];
				for (var key in resp){
					typeArr.push({
						index:parseInt(key),
						value:("<option value='"+key+"'>"+resp[key]+"</option>")
					});
				}
				typeArr.reverse();
				var typeEle = $("#messageType");
				typeEle.html("<option></option>");
				for (var aIndex=0;aIndex<typeArr.length;aIndex++){
					typeEle.append(typeArr[aIndex].value);
				}
			}
			form.render();
		}
	});

	// 查询按钮
	$("button[lay-event='search']").click(function () {
		initTableUnread();
	});

	// 阅读
	$("button[lay-event='getCheckData']").click(function () {
		readMsg();
	});
}

/**
 * 阅读
 */
function readMsg() {
	var checkStatus = table.checkStatus("unreadMessage");
	var data = checkStatus.data;
	var messageids = "";
	$.each(data, function (index, value) {
		messageids += data[index].messageid + ",";
	});
	//去掉最后一个逗号(如果不需要去掉，就不用写)
	if (messageids.length > 0) {
		messageids = messageids.substr(0, messageids.length - 1);
	}
	if (isBlank(messageids)) {
		layer.msg("请先选择阅读的记录");
		return;
	}
	var messageid = {
		"messageid": messageids
	};
	$.ajax({
		type: "POST",
		async: false,
		url: "/messageCenter/updateMsgDetail?temp=" + Math.random(),
		dataType: 'json',
		data: messageid,
		success: function (data) {
			if (data.code === 200 || data.code === '200') {
				reloadTable();
			} else {
				layer.msg(data.msg);
			}
		}
	});
}