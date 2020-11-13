/**

 @Name：layuiAdmin 公共业务
 @Author：贤心
 @Site：http://www.layui.com/admin/
 @License：LPPL
    
 */
 
layui.define(function(exports){
  var $ = layui.$
  ,layer = layui.layer
  ,laytpl = layui.laytpl
  ,setter = layui.setter
  ,view = layui.view
  ,admin = layui.admin
  
  //公共业务的逻辑处理可以写在此处，切换任何页面都会执行
  //……
  
  
  
  //退出
  admin.events.logout = function(){
    //清空本地记录的 token，并跳转到登入页
    admin.exit(function(){
      location.href = '/views/user/login.html';
    });
  };
  
  // 修改密码
  admin.events.editpwd = function(){
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
						type : "POST",
						async : false,
						url : "/user/editPwd.action",
						dataType : 'json',
						data : {
							"oldPwd" : hex_md5(oldPwd),
							"newPwd" : hex_md5(newPwd),
							"checkPwd" : hex_md5(checkPwd),
						},
						success : function(data) {
							if (data.code == 500) {
								layer.msg(data.msg);
							} else if (data.code == 200) {
								layer.msg(data.msg);
								layer.close(index);
							}
						}
					});
				}
	        }
	   });
  };

	admin.events.helpdoc = function(){
		window.open("/login/faq");
	}
  
  //对外暴露的接口
  exports('common', {});
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