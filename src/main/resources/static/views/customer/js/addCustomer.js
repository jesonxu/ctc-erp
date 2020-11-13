layui.use(['laydate', 'layer', 'upload', 'form', 'element'], function () {
    var form = layui.form,
        layer = layui.layer,
        layedit = layui.layedit,
        laydate = layui.laydate,
        upload = layui.upload;
    //注意：parent 是 JS 自带的全局对象，可用于操作父页面
    var index = parent.layer.getFrameIndex(window.name);

    //取当天日期时间
    var date = new Date();
    var dataStart = dateFormatter(date);

    function dateFormatter(date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
    }

    // 公司创立日期
    laydate.render({
        elem: '#creationDate',
        trigger: 'click',
        max: dataStart
    });

    // 使用日期
    laydate.render({
        elem: '#useDate',
        trigger: 'click',
        max: dataStart
    });

    // 加载客户类别下拉框
    $.ajax({
        type: "POST",
        url: "/customer/getCustomerTypeSelect.action",
        dataType: "JSON",
        success: function (data) {
            if (data.code === 200 || data.code ==='200') {
                var customer_type_infos = data.data;
                var dom = $("#customerType");
                dom.empty();
                for (var type_index = 0; type_index < customer_type_infos.length; type_index++) {
                    var customer_type = customer_type_infos[type_index];
                    console.log("--------客户类型-------->>"+JSON.stringify(customer_type));
                    dom.append("<option value='" + customer_type.customerTypeId +
                        "' data-type-value='" + customer_type.customerTypeValue +
                        "'>" + customer_type.customerTypeName + "</option>");
                }
                form.render('select');
            }
        }
    });

    // 加载客户区域下拉框
    $.ajax({
        type: "POST",
        async: false,
        url: '/customer/getRegion.action?temp=' + Math.random(),
        dataType: 'json',
        data: {},
        success: function (data) {
            var select = $("#customerRegion");
            select.empty();
            for (var i = 0; i < data.length; i++) {
                select.append('<option value="' + data[i].value + '">' + data[i].name + '</option>');
            }
            form.render('select');
        }
    });

    // 提交按钮
    $("#supplier_submit").click(function (e) {
        var form_data = {
            companyName: $("#companyName").val(),
            legalPerson: $("#legalPerson").val(),
            registrationNumber: $("#registrationNumber").val(),
            registrationAddress: $("#registrationAddress").val(),
            postalAddress: $("#postalAddress").val(),
            telePhoneNumber: $("#telePhoneNumber").val(),
            email: $("#email").val(),
            website: $("#website").val(),
            contactName: $("#contactName").val(),
            contactDept: $('#contactDept').val(),
            contactPosition: $('#contactPosition').val(),
            contactTelephone: $('#contactTelephone').val(),
            contactPhone: $("#contactPhone").val(),
            creationDate: $("#creationDate").val(),
            useDate: $("#useDate").val(),
            registeredCapital: $("#registeredCapital").val(),
            corporateNature: $("#corporateNature").val(),
            taxation: $("#taxation").val(),
            customerTypeId: $("#customerType").val(),
            customerRegion: $("#customerRegion").val(),
            companyIntroduction: $("#companyIntroduction").val(),
            businessMode: $("#businessMode").val(),
            bankAccountId: $("#bankAccountId").val()
        };
        if (!validation(form_data)) {
            return;
        }
        // 开票信息
        var invoice = initinvoice(false);
        form_data.invoiceInfos = JSON.stringify(invoice);

        // 银行信息
        var bankinfos = initbankinfos(false);
        form_data.bankInfos = JSON.stringify(bankinfos);

        $.ajax({
            type: "POST",
            async: false,
            url: "/customer/addCustomerInfo.action?temp=" + Math.random(),
            dataType: 'json',
            data: form_data,
            success: function (data) {
                if (data.code == 200) {
                    window.parent.layer.msg("添加成功");
                    if (typeof window.parent.sale_reload_customer_info == "function") {
                        window.parent.sale_reload_customer_info(0);
                    }
                    // 父页面需要重新加载（新增）
                    close_window();
                } else {
                    window.parent.layer.msg(data.msg);
                }
            }
        });
    });

    // 校验参数
    function validation(param) {
        if (isNull(param.companyName)) {
            $('#companyName').focus();
            layer.tips('公司名称不能为空', '#companyName');
            return false;
        }
        if (isNull(param.contactName)) {
            $('#contactName').focus();
            layer.tips('客户联系人不能为空', '#contactName');
            return false;
        }
        if (isNull(param.contactPhone)) {
            $('#contactPhone').focus();
            layer.tips('联系人手机不能为空', '#contactPhone');
            return false;
        }
        return true;
    }

    // 校验按钮点击事件绑定
    $("#check_company_name").click(function (e) {
        var company_name = $(this).siblings().filter("input[type='text']").val();
        if (isNull(company_name)) {
            layer.tips("请填写公司名称", $(this).siblings().filter("input[type='text']"), {
                tips: 1
            });
            return "";
        }
        if (company_name.length < 2) {
            layer.tips("检测公司名长度不得小于2", $(this).siblings().filter("input[type='text']"), {
                tips: 1
            });
            return "";
        }
        $.ajax({
            type: "POST",
            async: false,
            url: "/customer/queryCustomerByName.action?temp=" + Math.random(),
            dataType: 'json',
            data: {
                companyName: company_name,
                pageSize: "10",
                currentPage: "1"
            },
            success: function (data) {
                if (data.code === 200) {
                    if (!isNull(data.data) && !isNull(data.data.data) && data.data.count > 0) {
                        layer.open({
                            shade: 0.1,
                            shadeClose: true,
                            type: 2,
                            title: false,
                            area: ['520px', '200px'],
                            closeBtn: 0,
                            fixed: false,
                            content: '/customer/toMatchCustomer/' + company_name
                        });
                    } else {
                        layer.msg("没有匹配客户");
                    }
                } else {
                    // 失败的情况
                    layer.msg(data.msg);
                }
            }
        });
    });

    // 取消
    $("#supplier_cancel").click(function (e) {
        close_window();
    });

    //关闭窗口
    function close_window() {
        parent.layer.close(index);
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
    htmlStr += "<label class='layui-form-label' style='width: 100%; text-align: left;'>开票信息<span style='color: red'>(提流程时会用到)</span>：</label>"
    htmlStr += "</div>"
    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>公司名称：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='companyname' name='companyname' class='layui-input' placeholder='请填写公司名称' />"
    htmlStr += "</div>"
    htmlStr += "</div>"

    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>税务号：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='taxnumber' name='taxnumber' class='layui-input' placeholder='请填写税务号' />"
    htmlStr += "</div>"
    htmlStr += "</div>"

    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>公司地址：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='companyaddress' name='companyaddress' class='layui-input' placeholder='请填写公司地址' />"
    htmlStr += "</div>"
    htmlStr += "</div>"

    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>联系电话：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='phone' name='phone' class='layui-input' placeholder='请填写联系电话' />"
    htmlStr += "</div>"
    htmlStr += "</div>"

    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>开户银行：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='accountbank' name='accountbank' class='layui-input' placeholder='请填写开户银行' />"
    htmlStr += "</div>"
    htmlStr += "</div>"

    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>银行账号：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='bankaccount' name='bankaccount' class='layui-input' placeholder='请填写银行账号' />"
    htmlStr += "</div>"
    htmlStr += "</div>"

    htmlStr += "<div class='layui-inline operate" + index + "'>";
    htmlStr += "<label class='layui-form-label'>";
    htmlStr += "<span class='add_btn' id='add_info' onclick='addInvoice(" + index + ")'> <i class='layui-icon layui-icon-add-circle' title='添加开票信息'></i></span>";
    htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_info' onclick='reduceInvoice(" + index + ")'><i class='layui-icon layui-icon-close-fill' title='删除银行信息'></i></span>";
    htmlStr += "</label>";
    htmlStr += "</div>";

    htmlStr += "</div>";

    lastgradientDiv.after(htmlStr);
    layui.use('form', function () {
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
    htmlStr = "<div class='layui-form-item gradient'>" +
        "   <div class='layui-form-item'>" +
        "      <label class='layui-form-label' style='width: 100%; text-align: left;'>银行信息<span style='color: red'>(提流程时会用到)</span>：</label>" +
        "   </div>" +
        "   <div class='layui-inline'>" +
        "   <label class='layui-form-label'>名称：</label>" +
        "   <div class='layui-input-inline'>" +
        "       <input type='text' id='accountname' name='accountname' class='layui-input' placeholder='请填写名称' />" +
        "   </div>" +
        "   <div class='layui-inline'>" +
        "       <label class='layui-form-label'>开户银行：</label>" +
        "       <div class='layui-input-inline'>" +
        "            <input type='text' id='accountbank' name='accountbank' class='layui-input' placeholder='请填写开户银行' />" +
        "       </div>" +
        "   </div>" +
        "   <div class='layui-inline'>" +
        "       <label class='layui-form-label'>银行账号：</label>" +
        "       <div class='layui-input-inline'>" +
        "           <input type='text' id='bankaccount' name='bankaccount' class='layui-input' placeholder='请填写银行账号' />" +
        "       </div>" +
        "   </div>" +
        "   <div class='layui-inline'>" +
        "        <label class='layui-form-label'></label>" +
        "        <div class='layui-input-inline'></div>" +
        "   </div>" +
        "   <div class='layui-inline operate'>" +
        "       <label class='layui-form-label'>" +
        "          <span class='add_btn' id='add_bank' onclick='addBank()'>" +
        "              <i class='layui-icon layui-icon-add-circle'></i>" +
        "          </span>&nbsp;&nbsp;" +
        "          <span class='add_btn' id='reduce_bank' onclick='reducebank(this)'>" +
        "              <i class='layui-icon layui-icon-close-fill' ></i>" +
        "          </span>" +
        "       </label>" +
        "   </div>" +
        "</div>";

    lastgradientDiv.after(htmlStr);
    layui.use('form', function () {
        var form = layui.form;
        form.render();
    });
}

// 移除开票信息
function reduceInvoice(index) {
    var operateDiv = $(".operate" + index + "");
    var lastgradientDiv = operateDiv.parent();
    var newOperateGradientDiv = lastgradientDiv.prev();
    lastgradientDiv.remove();

    var htmlStr = "<div class='layui-inline operate" + index + "'>" +
        "<label class='layui-form-label'>" +
        "<span class='add_btn' id='add_invoice' onclick='addInvoice(" + index + ")'> " +
        "  <i class='layui-icon layui-icon-add-circle'></i>" +
        "</span>";
    if ($('.gradient0').length > 1) {
        htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_invoice' onclick='reduceInvoice(" + index + ")'>" +
            "<i class='layui-icon layui-icon-close-fill' title='删除开票信息'></i>" +
            "</span>";
    }
    htmlStr += "</label>";
    htmlStr += "</div>";
    newOperateGradientDiv.append(htmlStr)
}

// 移除银行信息
function reducebank(ele) {
    var prev = $(ele).parents('.gradient').prev();
    $(ele).parents('.gradient').remove();

    var htmlStr = "<div class='layui-inline operate'>" +
        "<label class='layui-form-label'>" +
        "   <span class='add_btn' id='add_bank' onclick='addBank()'> " +
        "      <i class='layui-icon layui-icon-add-circle' title='添加银行信息'></i>" +
        "   </span>";
    if ($('.gradient').length > 1) {
        htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_bank' onclick='reducebank(this)'>" +
            "<i class='layui-icon layui-icon-close-fill' title='删除银行信息'></i>" +
            "</span>";
    }
    htmlStr += "</label></div>";
    prev.append(htmlStr);
}

// 获取填写的开票信息
function initinvoice(needVerify) {
    var flag = true;
    var invoice = [];
    var companyNames = [];

    $('.gradient0').each(function (index, item) {
        if (!flag) {
            return;
        }
        var e = $(this);
        var companyName = getInputVal(e, 'companyname'),
	        taxNumber = getInputVal(e, 'taxnumber'),
	        companyAddress = getInputVal(e, 'companyaddress'),
	        phone = getInputVal(e, 'phone'),
	        accountBank = getInputVal(e, 'accountbank'),
	        bankAccount = getInputVal(e, 'bankaccount'); 
        if (needVerify) {
            if (isNull(companyName)) {
                flag = false;
                e.find('input[name="companyname"]').focus();
                return layer.tips('公司名称不能为空', e.find('input[name="companyname"]'));
            } else if (companyNames.indexOf(companyName) >= 0) {
                flag = false;
                e.find('input[name="companyname"]').focus();
                return layer.tips('公司名称重复', e.find('input[name="companyname"]'));
            }
            if (isNull(taxNumber)) {
                flag = false;
                e.find('input[name="taxnumber"]').focus();
                return layer.tips('税务号不能为空', e.find('input[name="taxnumber"]'));
            }
            if (isNull(companyAddress)) {
                flag = false;
                e.find('input[name="companyaddress"]').focus();
                return layer.tips('公司地址不能为空', e.find('input[name="companyaddress"]'));
            }
            if (isNull(phone)) {
                flag = false;
                e.find('input[name="phone"]').focus();
                return layer.tips('联系电话不能为空', e.find('input[name="phone"]'));
            }
            if (isNull(accountBank)) {
                flag = false;
                e.find('input[name="accountbank"]').focus();
                return layer.tips('开户银行不能为空', e.find('input[name="accountbank"]'));
            }
            if (isNull(bankAccount)) {
                flag = false;
                e.find('input[name="bankaccount"]').focus();
                return layer.tips('银行账号不能为空', e.find('input[name="bankaccount"]'));
            }
        }
        if (companyName || taxNumber || companyAddress 
        		|| phone || accountBank || bankAccount) {
        	invoice.push({
        		companyName: companyName,
        		taxNumber: taxNumber,
        		companyAddress: companyAddress,
        		phone: phone,
        		accountBank: accountBank,
        		bankAccount: bankAccount
        	});
        }
        if (e.find('input[name="companyname"]').val()) {
            companyNames.push(e.find('input[name="companyname"]').val());
        }
    });
    return !flag ? [] : invoice;
}

function getInputVal(ele, name) {
	var val = ele.find('input[name="' + name + '"]').val();
	if (val) {
		val = val.trim();
	}
	return val;
}

// 获取填写的银行信息
function initbankinfos() {
    var flag = true;
    var bankinfos = [];
    var bankAccounts = [];
    var customerTypeName = $('dd[lay-value="' + $('#customerType').val() + '"]').text();
    $('.gradient').each(function (index, item) {
        if (!flag) {
            return;
        }
        var e = $(this);
        var	accountName = getInputVal(e, 'accountname'),
        	accountBank = getInputVal(e, 'accountbank'),
        	bankAccount = getInputVal(e, 'bankaccount');
        if (customerTypeName != '意向客户' && customerTypeName != '测试客户') {
            if (isNull(accountName)) {
                flag = false;
                e.find('input[name="accountname"]').focus();
                return layer.tips('名称不能为空', e.find('input[name="accountname"]'));
            }
            if (isNull(accountBank)) {
                flag = false;
                e.find('input[name="accountbank"]').focus();
                return layer.tips('开户银行不能为空', e.find('input[name="accountbank"]'));
            }
            if (isNull(bankAccount)) {
                flag = false;
                e.find('input[name="bankaccount"]').focus();
                return layer.tips('银行账号不能为空', e.find('input[name="bankaccount"]'));
            } else if (bankAccounts.indexOf(bankAccount) >= 0) {
                flag = false;
                e.find('input[name="bankaccount"]').focus();
                return layer.tips('银行账号重复', e.find('input[name="bankaccount"]'));
            }
        }
        if (accountName || accountBank || bankAccount) {
        	bankinfos.push({
        		accountName: accountName,
        		accountBank: accountBank,
        		bankAccount: bankAccount
        	});
        }
        if (e.find('input[name="bankaccount"]').val()) {
            bankAccounts.push(e.find('input[name="bankaccount"]').val());
        }
    });
    return !flag ? [] : bankinfos;
}