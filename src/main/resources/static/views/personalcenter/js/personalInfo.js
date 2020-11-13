var tableIns;
var laydate;
var form;
var table;
var layer;
var element;
var val_dateNum = '1';
layui.use(['layer', 'form'], function () {
	laydate = layui.laydate;
	form = layui.form;
	layer = layui.layer;
	element = layui.element;
	table = layui.table;
	// 周月季年近三年数据
	form.on("radio(layui-date)", function (data) {
		val_dateNum = data.value;
		initCount(val_dateNum);
	});
	initCount();
});

function editPwd() {
	var editPwdIndex = layer.open({
		title: ['修改密码', 'font-size:18px;'],
	    type: 2,
	    area: ['500px', '315px'],
	    fixed: true, //不固定
	    fix: false,
	    maxmin: true,
	    content: '/user/toEditPwd.action?temp=temp=' + Math.random(),
	    btn: ['确认', '取消'],
	    yes: function (index, layero) {
	        var iframe = $("#layui-layer-iframe" + index).contents();
	        var verifyNeedPwd = iframe.find('#verifyNeedPwd').val();
	        if (validate(iframe, verifyNeedPwd)) {
				var oldPwd = iframe.find("#oldPwd").val();
				var newPwd = iframe.find("#newPwd").val();
				var checkPwd = iframe.find("#checkPwd").val();
				$.ajax({
					type: "POST",
					async: false,
					url: "/user/editPwd.action",
					dataType: 'json',
					data: {
						oldPwd: hex_md5(oldPwd),
						newPwd: hex_md5(newPwd),
						checkPwd: hex_md5(checkPwd),
					},
					success: function(data) {
						if (data.code == 500) {
							layer.msg(data.msg);
						} else if (data.code == 200) {
							layer.msg(data.msg);
							layer.close(editPwdIndex);
						}
					}
				});
	        }
	    }
	});
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

$("#customer").click(function () {
	pumpLsit(1)
});
$("#supplier").click(function () {
	pumpLsit(2)
});
$("#customerLog").click(function () {
	pumpLsit(3)
});
$("#supplierLog cite").click(function () {
	pumpLsit(4)
});

function validate(iframe, verifyNeedPwd) { // 验证
	var flag = true;
	var msg = "";

	var oldPwd = iframe.find("#oldPwd").val();
	var newPwd = iframe.find("#newPwd").val();
	var checkPwd = iframe.find("#checkPwd").val();

	if (!oldPwd && oldPwd.length < 1) {
		if (verifyNeedPwd == 'true') {
			msg = "请输入旧密码";
			flag = false;
		}
	} else if (!newPwd && newPwd.length < 1) {
		msg = "请输入新密码";
		flag = false;
	} else if (newPwd.length < 6 || newPwd.length > 16
		|| /[^\w\.\/]/ig.test(newPwd)) {
		msg = "密码长度最少6位,最多16位";
		flag = false;
	} else if (oldPwd == newPwd) {
		msg = "新密码和旧密码不能相同";
		flag = false;
	} else if (!checkPwd && checkPwd.length < 1) {
		msg = "确认新密码能为空";
		flag = false;
	} else if (checkPwd != newPwd) {
		msg ="确认新密码和新密码不相同";
		flag = false;
	}
	if (!flag) {
		layer.msg(msg);
	}
	return flag;
}