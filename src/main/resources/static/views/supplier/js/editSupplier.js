var deleteInvoiceInfoIds = [];
var deleteBankIds = [];
layui.use(['laydate','layer', 'form', 'element'], function() {
	var layer = layui.layer;
	var initInvoiceInfos = initinvoice(false);
	var initBankInfos = initbankinfos(false);
    var laydate = layui.laydate;

	//判断是查看详情还是编辑供应商信息
	var operationType = $("#operationType").val();
	if (operationType == 1) {
		$("input.layui-input").attr("disabled","disabled");
		$("select").attr("disabled","disabled");
		$("textarea.layui-textarea").attr("disabled","disabled");
		$("button").attr("disabled","disabled");
		$("#isShowSubmit").hide();
		$(".operate").hide();
		$(".operate0").hide();
	} else {
		$("input.layui-input").removeAttr("disabled");
		$("select").removeAttr("disabled");
		$("textarea.layui-textarea").removeAttr("disabled");
		$("button").removeAttr("disabled");
		$("#isShowSubmit").show();
		$(".operate").show();
		$(".operate0").show();
	}

    //注意：parent 是 JS 自带的全局对象，可用于操作父页面
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    //常规用法
    
    //取当天日期时间
    var date = new Date();
    var dataStart = dateFormatter(date);
    function dateFormatter(date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
    }
    
    laydate.render({
        elem: '#creationDate',
        trigger: 'click',
        max: dataStart,
    });

    $.ajax({
		type:"POST",
		url:"/supplier/getSupplierType.action",
		dataType:"json",
		success:function(data){
			if(data.code == '200') {
				var result = data.data;
				var dom = $("#supplierType");
				dom.empty();
				var supplierTypeId = $("#supplierTypeId").val();
				for (var i = 0; i < result.length; i++) {
					var temp = result[i];
					if(temp.supplierTypeId == supplierTypeId) {
						dom.append("<option value='" + temp.supplierTypeId + "' selected='selected'>" + temp.supplierTypeName + "</option>");
					} else {
						dom.append("<option value='" + temp.supplierTypeId + "'>" + temp.supplierTypeName + "</option>");
					}
				}

				var form = layui.form;
				form.render();
			}
		}
	});
    
    // 提交
    $("#supplier_submit").click(function (e) {
        var form_data = {
        	supplierId:$("#supplierId").val(),
            companyName:$("#companyName").val(),
            legalPerson:$("#legalPerson").val(),
            registrationNumber:$("#registrationNumber").val(),
            registrationAddress:$("#registrationAddress").val(),
            postalAddress:$("#postalAddress").val(),
            telephoneNumber:$("#telephoneNumber").val(),
            email:$("#email").val(),
            website:$("#website").val(),
            contactName:$("#contactName").val(),
            contactPhone:$("#contactPhone").val(),
            creationDate:$("#creationDate").val(),
            registeredCapital:$("#registeredCapital").val(),
            corporateNature:$("#corporateNature").val(),
            supplierTypeId:$("#supplierType").val(),
            taxation:$("#taxation").val(),
            invoiceInfos: JSON.stringify(invoice),
            bankInfos: JSON.stringify(bankinfos)
        };
        if (!validation(form_data)){
            return;
        }
		var invoice = initinvoice(true);
		if (invoice.length == 0) {
			return;
		}
		var bankinfos = initbankinfos(true);
		if (bankinfos.length == 0) {
			return;
		}
		// 去除多余的无操作信息
		for (var i = 0; i < invoice.length; i++) {
			for (var j = 0; j < initInvoiceInfos.length; j++) {
				if (JSON.stringify(invoice[i]) == JSON.stringify(initInvoiceInfos[j])) {
					if (arrayRemove(invoice, invoice[i]))
						i--;
					break;
				}
			}
		}
		for (var i = 0; i < bankinfos.length; i++) {
			for (var j = 0; j < initBankInfos.length; j++) {
				if (JSON.stringify(bankinfos[i]) == JSON.stringify(initBankInfos[j])) {
					if (arrayRemove(bankinfos, bankinfos[i]))
						i--;
					break;
				}
			}
		}
		form_data.invoiceInfos = JSON.stringify(invoice);
		form_data.delInvoiceIds = deleteInvoiceInfoIds.join(',');
		form_data.bankInfos = JSON.stringify(bankinfos);
		form_data.delBankIds = deleteBankIds.join(',');
		$("#supplier_submit").attr('disabled','true')
        $.ajax({
            type: "POST",
            async: false,
            url: "/supplier/editSupplier.action?temp=" + Math.random(),
            dataType: 'json',
            data: form_data,
            success: function (data) {
                if (data.code == 200) {
                    $("#supplier_submit").attr('disabled','false')
                    window.parent.layer.msg("修改成功");
					window.parent.reload_supplier_info(1);
                    // 父页面需要重新加载（新增）
                    close_window();
                } else {
                	layer.msg(data.msg);
                	$("#supplier_submit").attr('disabled','false')
                }
            }
        });
    });

    // 校验参数
    function validation(param){
        if (isNull(param.companyName)){
            layer.tips('公司名称不能为空', '#companyName');
            return false;
        }
        if (isNull(param.contactName)) {
            $('#contactName').focus();
            layer.tips('业务联系人不能为空', '#contactName');
            return false;
        }
        if (isNull(param.contactPhone)) {
            $('#contactPhone').focus();
            layer.tips('联系手机不能为空', '#contactPhone');
            return false;
        }
        if (!/^([\d\-]{1,})$/.test(param.contactPhone)) {
            $('#contactPhone').focus();
            layer.tips('联系手机格式错误', '#contactPhone');
            return false;
        }
        return true;
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

// 增加开票信息
function addInvoice(index) {
	var operateDiv = $(".operate" + index);
	var lastgradientDiv = operateDiv.parent();
	
	operateDiv.remove();
	var htmlStr = "";
	htmlStr += "<div class='layui-form-item gradient" + index + "'>"; // 表单元素一行开始
	htmlStr += "<div class='layui-form-item'>"
	htmlStr += "<label class='layui-form-label' style='width: 100%; text-align: left;'>开票信息：</label>"
	htmlStr += "</div>"
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>公司名称：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='companyname' name='companyname' class='layui-input' placeholder='请填写公司名称' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
		
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>税务号：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='taxnumber' name='taxnumber' class='layui-input' placeholder='请填写税务号' />"
	htmlStr += "</div>"
	htmlStr += "</div>"

	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>公司地址：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='companyaddress' name='companyaddress' class='layui-input' placeholder='请填写公司地址' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
		
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>联系电话：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='phone' name='phone' class='layui-input' placeholder='请填写联系电话' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
		
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>开户银行：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='accountbank' name='accountbank' class='layui-input' placeholder='请填写开户银行' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
		
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>银行账号：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='bankaccount' name='bankaccount' class='layui-input' placeholder='请填写银行账号' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
	
	htmlStr += "<div class='layui-inline operate" + index + "'>";
	htmlStr += "<label class='layui-form-label'>";
	htmlStr += "<span class='add_btn' id='add_info' onclick='addInvoice(" + index + ")'> <i class='layui-icon layui-icon-add-circle' title='添加开票信息'></i></span>";
	htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_info' onclick='reduceInvoice(" + index + ", this)'><i class='layui-icon layui-icon-close-fill' title='删除开票信息'></i></span>";
	htmlStr += "</label>";
	htmlStr += "</div>";
	
	htmlStr += "</div>";
	
	lastgradientDiv.after(htmlStr);
	layui.use('form', function() {
		var form = layui.form;
			form.render();
	});
}

// 增加银行信息
function addBank() {
	var operateDiv = $(".operate");
	var lastgradientDiv = operateDiv.parent();
	
	operateDiv.remove();
	var htmlStr = "";
	htmlStr += "<div class='layui-form-item gradient'>"; // 表单元素一行开始
	htmlStr += "<div class='layui-form-item'>"
	htmlStr += "<label class='layui-form-label' style='width: 100%; text-align: left;'>银行信息：</label>"
	htmlStr += "</div>"
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>名称：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='accountname' name='accountname' class='layui-input' placeholder='请填写名称' />"
	htmlStr += "</div>"
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>开户银行：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='accountbank' name='accountbank' class='layui-input' placeholder='请填写开户银行' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
	htmlStr += "<div class='layui-inline'>";
	htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>银行账号：</label>";
	htmlStr += "<div class='layui-input-inline'>"
	htmlStr += "<input type='text' id='bankaccount' name='bankaccount' class='layui-input' placeholder='请填写银行账号' />"
	htmlStr += "</div>"
	htmlStr += "</div>"
	htmlStr += '<div class="layui-inline"><label class="layui-form-label"></label><div class="layui-input-inline"></div></div>'
	
	htmlStr += "<div class='layui-inline operate'>";
	htmlStr += "<label class='layui-form-label'>";
	htmlStr += "<span class='add_btn' id='add_bank' onclick='addBank()'> <i class='layui-icon layui-icon-add-circle'></i></span>";
	htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_bank' onclick='reducebank(this)'><i class='layui-icon layui-icon-close-fill' ></i></span>";
	htmlStr += "</label>";
	htmlStr += "</div>";
	
	htmlStr += "</div>";
	
	lastgradientDiv.after(htmlStr);
	layui.use('form', function() {
		var form = layui.form;
			form.render();
	});
}

// 移除开票信息
function reduceInvoice(index, ele) {
	var invoceId = $(ele).parents('.gradient0').find('input[name="invoiceId"]') ? $(ele).parents('.gradient0').find('input[name="invoiceId"]').val() : '';
	if (invoceId) {
		deleteInvoiceInfoIds.push(invoceId);
	}
	var operateDiv = $(".operate" + index + "");
	var lastgradientDiv = operateDiv.parent();
	var newOperateGradientDiv = lastgradientDiv.prev(); 
	lastgradientDiv.remove();

	var htmlStr = "";
	htmlStr += "<div class='layui-inline operate" + index + "'>";
	htmlStr += "<label class='layui-form-label'>";
	htmlStr += "<span class='add_btn' id='add_invoice' onclick='addInvoice(" + index + ")'> <i class='layui-icon layui-icon-add-circle' title='添加开票信息'></i></span>";
	if ($('.gradient0').length > 1) {
		htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_invoice' onclick='reduceInvoice(" + index + ", this)'><i class='layui-icon layui-icon-close-fill' title='删除开票信息'></i></span>";
	}
	htmlStr += "</label>";
	htmlStr += "</div>";
	
	newOperateGradientDiv.append(htmlStr);
}

// 移除银行信息
function reducebank(ele) {
	var bankId = $(ele).parents('.gradient').find('input[name="bankAccountId"]') ? $(ele).parents('.gradient').find('input[name="bankAccountId"]').val() : '';
	if (bankId) {
		deleteBankIds.push(bankId);
	}
	var prev = $(ele).parents('.gradient').prev();
    $(ele).parents('.gradient').remove();

    var htmlStr = "";
    htmlStr += "<div class='layui-inline operate'>";
    htmlStr += "<label class='layui-form-label'>";
    htmlStr += "<span class='add_btn' id='add_bank' onclick='addBank()'> <i class='layui-icon layui-icon-add-circle' title='添加银行信息'></i></span>";
    if ($('.gradient').length > 1) {
        htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_bank' onclick='reducebank(this)'><i class='layui-icon layui-icon-close-fill' title='删除银行信息'></i></span>";
    }
    htmlStr += "</label>";
    htmlStr += "</div>";
    prev.append(htmlStr);
}

// 获取开票信息
function initinvoice(needVerify){
	var flag = true;
	var invoice = [];
	var companyNames = [];
	$('.gradient0').each(function (index, item) {
		if (!flag) {
			return;
		}
		var e = $(this);
		if (needVerify) {
			if (isNull(e.find('input[name="companyname"]').val())){
				e.find('input[name="companyname"]').focus();
				return layer.tips('公司名称不能为空' , e.find('input[name="companyname"]'));
			} else if (companyNames.indexOf(e.find('input[name="companyname"]').val()) >= 0) {
				e.find('input[name="companyname"]').focus();
				return layer.tips('公司名称重复' , e.find('input[name="companyname"]'));
			}
			if (isNull(e.find('input[name="taxnumber"]').val())){
				e.find('input[name="taxnumber"]').focus();
				return layer.tips('税务号不能为空' , e.find('input[name="taxnumber"]'));
			}
			if (isNull(e.find('input[name="companyaddress"]').val())){
				e.find('input[name="companyaddress"]').focus();
				return layer.tips('公司地址不能为空' , e.find('input[name="companyaddress"]'));
			}
			if (isNull(e.find('input[name="phone"]').val())){
				e.find('input[name="phone"]').focus();
				return layer.tips('联系电话不能为空' , e.find('input[name="phone"]'));
			}
			if (isNull(e.find('input[name="accountbank"]').val())){
				e.find('input[name="accountbank"]').focus();
				return layer.tips('开户银行不能为空' , e.find('input[name="accountbank"]'));
			}
			if (isNull(e.find('input[name="bankaccount"]').val())){
				e.find('input[name="bankaccount"]').focus();
				return layer.tips('银行账号不能为空' , e.find('input[name="bankaccount"]'));
			}
		}
        invoice.push({
        	invoiceId: e.find('input[name="invoiceId"]').val(),
        	companyName: e.find('input[name="companyname"]').val(),
        	taxNumber: e.find('input[name="taxnumber"]').val(),
        	companyAddress: e.find('input[name="companyaddress"]').val(),
        	phone: e.find('input[name="phone"]').val(),
        	accountBank: e.find('input[name="accountbank"]').val(),
        	bankAccount: e.find('input[name="bankaccount"]').val()
		});
        companyNames.push(e.find('input[name="companyname"]').val());
	});
	return !flag ? [] : invoice;
}

// 获取银行信息
function initbankinfos(needVerify){
	var flag = true;
	var bankinfos = [];
	var bankAccounts = [];
	$('.gradient').each(function (index, item) {
		if (!flag) {
			return;
		}
		var e = $(this);
		if (needVerify) {
			if (isNull(e.find('input[name="accountname"]').val())){
				e.find('input[name="accountname"]').focus();
				return layer.tips('名称不能为空' , e.find('input[name="accountname"]'));
			}
			if (isNull(e.find('input[name="accountbank"]').val())){
				e.find('input[name="accountbank"]').focus();
				return layer.tips('开户银行不能为空' , e.find('input[name="accountbank"]'));
			}
			if (isNull(e.find('input[name="bankaccount"]').val())){
				e.find('input[name="bankaccount"]').focus();
				return layer.tips('银行账号不能为空' , e.find('input[name="bankaccount"]'));
			} else if (bankAccounts.indexOf(e.find('input[name="bankaccount"]').val()) >= 0) {
				e.find('input[name="bankaccount"]').focus();
				return layer.tips('银行账号重复' , e.find('input[name="bankaccount"]'));
			}
		}
		bankinfos.push({
			bankAccountId: e.find('input[name="bankAccountId"]').val(),
			accountName: e.find('input[name="accountname"]').val(),
			accountBank: e.find('input[name="accountbank"]').val(),
			bankAccount: e.find('input[name="bankaccount"]').val(),
		});
		bankAccounts.push(e.find('input[name="bankaccount"]').val());
	});
	return !flag ? [] : bankinfos;
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}

//add fun array remove
function arrayIndexOf(array, val) {
	for (var i = 0; i < array.length; i++) {
		if (JSON.stringify(array[i]) == JSON.stringify(val)) {
			return i;
		}
	}
	return -1;
};

function arrayRemove(array, val) {
	var index = arrayIndexOf(array, val);
	if (index > -1) {
		return array.splice(index, 1);
	}
	return null;
};
