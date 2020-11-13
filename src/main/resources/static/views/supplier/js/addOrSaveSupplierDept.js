layui.use(['laydate', 'layer', 'form', 'element'], function () {
    var laydate = layui.laydate;
    //注意：parent 是 JS 自带的全局对象，可用于操作父页面
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    
    var origFormData = getFormData();
    
    var supplierId = $('#supplierId').val();
    
    var deptNames = [];
    
    $('input[name="deptName"]').each(function (index, item) {
    	deptNames.push($(item).val());
    });

	$("input[name='input-dept-name']").change(function (e) {
		var dept_item = $(this).parent().parent().parent();
		$(dept_item).find("input[name='deptName']").val($(this).val().trim());
	});

    // 添加部门
    $("#supplier_add_dept").click(function (e) {
    	var dept_name = $('#deptName').val().trim();
    	if (!dept_name) {
    		return layer.msg('请输入部门名称');
    	}
    	if (deptNames.indexOf(dept_name) >= 0) {
    		return layer.msg('部门已存在');
    	}
    	$('#supplier-dept-tips').css('display', 'none');
		$('#supplier-dept-line').css('display', 'none');
    	var item = $('#supplier-contacts-template').clone(true);
    	item.find('legend').html('<input type="text" class="input-dept-name" name="input-dept-name" value="'+dept_name+'"/>'
    			+ '<button type="button" class="supplier_add_contacts layui-inline layui-btn layui-btn-sm">添加联系人</button>');
    	item.removeAttr('style').find('input[name="deptName"]').val(dept_name);
    	item.removeAttr('id');
    	item.find('#supplier-dept-contacts-content').css('display', '').removeAttr('id');
    	item.find('.supplier_add_contacts').attr('deptName', dept_name);
    	item.find(".supplier_add_contacts").unbind().click(function (e) {
        	var item = $('#supplier-dept-contacts-content').clone(true);
        	item.css('display', '').removeAttr('id');
        	item.find('input[name="deptName"]').val($(this).attr('deptName').trim());
        	$(this).parents('fieldset').after(item);
        });
    	$('form').append(item);
    	$('#buttons').css('display', '');
    	deptNames.push(dept_name);
    	$('#deptName').val('');

    	item.find("input[name='input-dept-name']").unbind().change(function (e) {
			var dept_item = $(this).parent().parent().parent();
			$(dept_item).find("input[name='deptName']").val($(this).val().trim());
		});
    });
    
    // 添加联系人
	$(".supplier_add_contacts").click(function (e) {
    	var item = $('#supplier-dept-contacts-content').clone(true);
    	item.css('display', '').removeAttr('id');
    	item.find('input[name="deptName"]').val($(this).attr('deptName').trim());
    	$(this).parents('fieldset').after(item);
    });

    // 提交
    $("#supplier_submit").click(function (e) {
    	var formData = getFormData(true);
    	if (formData === null) {
    		return;
    	} else if (!formData || formData.length == 0) {
    		return layer.msg("没有修改");
    	}
        $.ajax({
            type: "POST",
            async: false,
            url: "/supplier/editSupplierDept.action?temp=" + Math.random(),
            data: {depts: JSON.stringify(formData)},
            success: function (data) {
                if (data.code == 200) {
                    parent.layer.msg("保存成功");
					if(typeof window.parent.reload_supplier_info === "function") { //是函数    其中 FunName 为函数名称
						// 供应商刷新
						window.parent.reload_supplier_info(2,3);
					}
					if(typeof window.parent.load_customer_dept === "function") {
						// 销售客户刷新
						window.parent.sale_reload_customer_info(2,"customer-dept-info");
					}
                    // 父页面需要重新加载（新增）
                    close_window();
                } else {
                	return layer.msg(data.msg);
                }
            }
        });
    });
    
    function getFormData(isVerify) {
    	var needVerify = isVerify;
    	var contacts = [];
    	var flag = true;
    	$('.site-demo-body .supplier-contacats').each(function (index, item) {
    		if (!flag) {
    			return;
    		}
        	var e = $(item);
        	var json = {};
        	json = {
        		supplierContactsId: e.find('input[name="supplierContactsId"]').val(),
        		supplierId: $('#supplierId').val(),
        		deptName: e.find('input[name="deptName"]').val(),
        		contactsName: e.find('input[name="contactsName"]').val().trim(),
        		post: e.find('input[name="post"]').val().trim(),
        		firstPhone: e.find('input[name="firstPhone"]').val().trim(),
        		secondPhone: e.find('input[name="secondPhone"]').val().trim(),
        		telephone: e.find('input[name="telephone"]').val().trim(),
        		email: e.find('input[name="email"]').val().trim(),
        		wx: e.find('input[name="wx"]').val().trim(),
                qq: e.find('input[name="qq"]').val().trim(),
        		remark: e.find('textarea[name="remark"]').val().trim()
        	}
        	if (isVerify) {
        		if (!json.contactsName) {
        			flag = false;
            		e.find('input[name="contactsName"]').focus();
                    return layer.tips("请填写姓名", e.find('input[name="contactsName"]'));
            	}
            	if (!json.post) {
            		flag = false;
            		e.find('input[name="post"]').focus();
                    return layer.tips("请填写职位", e.find('input[name="post"]'));
            	}
            	if (json.email && !/^[0-9a-z][_.0-9a-z-]{0,31}@([0-9a-z][0-9a-z-]{0,30}[0-9a-z]\.){1,4}[a-z]{2,4}$/.test(json.email)) {
            		flag = false;
            		e.find('input[name="email"]').focus();
                    return layer.tips("邮箱格式不正确", e.find('input[name="email"]'));
            	}
            	if (json.firstPhone && !/^1\d{10}$/.test(json.firstPhone)) {
            		flag = false;
            		e.find('input[name="firstPhone"]').focus();
                    return layer.tips("号码格式不正确", e.find('input[name="firstPhone"]'));
            	}
            	if (json.secondPhone && !/^1\d{10}$/.test(json.secondPhone)) {
            		flag = false;
            		e.find('input[name="secondPhone"]').focus();
                    return layer.tips("号码格式不正确", e.find('input[name="secondPhone"]'));
            	}
        	}
        	if (needVerify && origFormData.length > 0) {
        		for (var i = 0; i < origFormData.length; i++) {
        			if (JSON.stringify(origFormData[i]) == JSON.stringify(json)) {
        				return;
        			}
        		}
        	}
        	contacts.push(json);
        });
    	if (needVerify && !flag) {
    		return null;
    	} else {
    		return contacts; 
    	}
    }

    // 取消
    $("#supplier_cancel").click(function (e) {
        close_window();
    });

    //关闭窗口
    function close_window() {
        parent.layer.close(index);
    }

    function isNull(str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    }

});