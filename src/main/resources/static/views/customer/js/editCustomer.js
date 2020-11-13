var form;
var layer;
var laydate;
var table;
var _customerType = []; // 所有客户类型
var customerTypeValue; // 选中的客户类型的值，1合同客户
var _invoiceInfos; // 修改前的开票信息
var _bankInfos; // 修改前的银行信息
var deleteInvoiceInfoIds = []; // 被删掉的开票信息id集合
var deleteBankIds = []; // 被删掉的银行信息id集合
var oldContractData;

layui.use(['laydate', 'layer', 'upload', 'form', 'element'], function () {
    form = layui.form;
    layer = layui.layer;
    laydate = layui.laydate;
    table = layui.table;

    //判断是查看或编辑客户信息
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

    // 保存修改前的开票信息和银行信息
    _invoiceInfos = getInvoice();
    _bankInfos = getBankInfos(false);

    //取当天日期时间
    var date = new Date();
    var dataStart = dateFormatter(date);

    // 格式化时间
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
    });
    
    // 加载客户类别下拉框
    $.ajax({
        type: "POST",
        url: "/customer/getCustomerTypeSelect.action",
        data: {type: $("#customerTypeId").val()},
        dataType: "json",
        success: function (data) {
            if (data.code === '200' || data.code === 200) {
                var result = data.data;
                // 客户类型数组缓存起来待用
                _customerType = data.data;
                var dom = $("#customerType");
                dom.empty();
                var customerTypeId = $("#customerTypeId").val();
                for (var index = 0; index < result.length; index++) {
                    var temp = result[index];
                    if (temp.customerTypeId == customerTypeId) {
                        dom.append("<option value='" + temp.customerTypeId + "' selected='selected'>" + temp.customerTypeName + "</option>");
                        customerTypeValue = temp.customerTypeValue;
                    } else {
                        dom.append("<option value='" + temp.customerTypeId + "'>" + temp.customerTypeName + "</option>");
                    }
                }
                var form = layui.form;
                form.render();
                customerTypeChange(customerTypeId);
                // 添加监听事件
                form.on("select(customerType)", function (data) {
                    customerTypeChange(data.value);
                });
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
            var flowClass = $("#customerRegionId").val();
            select.empty();
            for (var i = 0; i < data.length; i++) {
                if (flowClass == data[i].value) {
                    select.append('<option value="' + data[i].value + '" selected="selected">' + data[i].name + '</option>');
                } else {
                    select.append('<option value="' + data[i].value + '">' + data[i].name + '</option>');
                }
            }
            layui.form.render('select');
        }
    });

    // 获取该客户以前上传的合同
    getOldContractData();
    initContractTable();
});

// 监听客户类型改变
function customerTypeChange(type_id) {
    customerTypeValue = findCustomerTypeValue(type_id);
    var company_introduction = $("#companyIntroduction-lable");
    var business_mode = $("#businessMode-lable");
    company_introduction.empty();
    business_mode.empty();
    // 合同客户必填
    if (customerTypeValue === 1 || customerTypeValue === "1") {
        company_introduction.append("<span style='color: red;'>*</span><span>公司情况介绍：</span>");
        business_mode.append("<span style='color: red;'>*</span><span>业务应用模式：</span>");
        $('.customer-invoice-not-must').attr('class', 'customer-invoice-must');
    } else {
        company_introduction.append("<span>公司情况介绍：</span>");
        business_mode.append("<span>业务应用模式：</span>");
        $('.customer-invoice-must').attr('class', 'customer-invoice-not-must');
    }
}

// 查找客户类型值
function findCustomerTypeValue(type_id) {
    var typeValue = -1;
    $.each(_customerType, function (i, item) {
        if (item.customerTypeId === type_id) {
            typeValue = item.customerTypeValue;
        }
    })
    return typeValue;
}

// 下载
function down_load(file_info) {
    console.log("下载文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.location.href = "/operate/downloadFile?" + file_params;
}

//预览
function view_File(file_info) {
    console.log("预览文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.open("/operate/viewFile?" + file_params);
}

// 提交
$("#customer_submit").click(function (e) {
    var form_data = {
        customerId: $("#customerId").val(),
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
        contractDate: $("#contractDate").val(),
        useDate: $("#useDate").val(),
        registeredCapital: $("#registeredCapital").val(),
        corporateNature: $("#corporateNature").val(),
        customerTypeId: $("#customerType").val(),
        customerRegion: $("#customerRegion").val(),
        taxation: $("#taxation").val(),
        contractFiles: $("#contractFilesInfo").val(),
        companyIntroduction: $("#companyIntroduction").val(),
        businessMode: $("#businessMode").val(),
        bankAccountId: $("#bankAccountId").val()
    };
    if (!validation(form_data, customerTypeValue)) {
        return;
    }

    var invoice = getInvoice(); // 修改后的开票信息

    var bankinfos = getBankInfos(true); // 修改后的银行信息

    // 去除多余的无操作信息
    for (var i = 0; i < invoice.length; i++) {
        for (var j = 0; j < _invoiceInfos.length; j++) {
            if (JSON.stringify(invoice[i]) == JSON.stringify(_invoiceInfos[j])) {
                if (arrayRemove(invoice, invoice[i]))
                    i--;
                break;
            }
        }
    }

    for (var i = 0; i < bankinfos.length; i++) {
        for (var j = 0; j < _bankInfos.length; j++) {
            if (JSON.stringify(bankinfos[i]) == JSON.stringify(_bankInfos[j])) {
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
    $.ajax({
        type: "POST",
        async: false,
        url: "/customer/editCustomer.action?temp=" + Math.random(),
        dataType: 'json',
        data: form_data,
        success: function (data) {
            if (data.code == 200) {
                window.parent.layer.msg("修改成功");
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
function validation(param, customerTypeValue) {
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
    // 合同客户必填项
    if (customerTypeValue === 1 || customerTypeValue === '1') {
        if (isNull(param.companyIntroduction)) {
            $('#companyIntroduction').focus();
            layer.tips('公司情况介绍不能为空', '#companyIntroduction');
            return false;
        }
        if (isNull(param.businessMode)) {
            $('#businessMode').focus();
            layer.tips('业务应用模式不能为空', '#businessMode');
            return false;
        }
    }
    return true;
}

// 取消
$("#customer_cancel").click(function (e) {
    close_window();
});

//关闭窗口
function close_window() {
    var index = parent.layer.getFrameIndex(window.name);
    parent.layer.close(index);
}

//增加开票信息
function addInvoice(index) {
    var customerTypeName = $('dd[lay-value="' + $('#customerType').val() + '"]').text();
    var operateDiv = $(".operate" + index);
    var lastgradientDiv = operateDiv.parent();

    operateDiv.remove();
    var htmlStr = "";
    htmlStr += "<div class='layui-form-item gradient" + index + "'>"; // 表单元素一行开始
    htmlStr += "<div class='layui-form-item'>"
    htmlStr += "<label class='layui-form-label' style='width: 100%; text-align: left;'>开票信息：</label>"
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
    var customerTypeName = $('dd[lay-value="' + $('#customerType').val() + '"]').text();
    var operateDiv = $(".operate");
    var lastgradientDiv = operateDiv.parent();

    operateDiv.remove();
    var htmlStr = "";
    htmlStr += "<div class='layui-form-item gradient'>"; // 表单元素一行开始
    htmlStr += "<div class='layui-form-item'>"
    htmlStr += "<label class='layui-form-label' style='width: 100%; text-align: left;'>银行信息：</label>"
    htmlStr += "</div>"
    htmlStr += "<div class='layui-inline'>";
    htmlStr += "<label class='layui-form-label'>名称：</label>";
    htmlStr += "<div class='layui-input-inline'>"
    htmlStr += "<input type='text' id='accountname' name='accountname' class='layui-input' placeholder='请填写名称' />"
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
    htmlStr += '<div class="layui-inline"><label class="layui-form-label"></label><div class="layui-input-inline"></div></div>'

    htmlStr += "<div class='layui-inline operate'>";
    htmlStr += "<label class='layui-form-label'>";
    htmlStr += "<span class='add_btn' id='add_bank' onclick='addBank()'> <i class='layui-icon layui-icon-add-circle'></i></span>";
    htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_bank' onclick='reducebank(this)'><i class='layui-icon layui-icon-close-fill' ></i></span>";
    htmlStr += "</label>";
    htmlStr += "</div>";

    htmlStr += "</div>";

    lastgradientDiv.after(htmlStr);
    layui.use('form', function () {
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

    newOperateGradientDiv.append(htmlStr)
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
function getInvoice() {
	var deleteIds = [];
    var invoice = [];
    $('.gradient0').each(function (index, item) {
        var e = $(this);
        var invoiceId = getInputVal(e, 'invoiceId'),
        	companyName = getInputVal(e, 'companyname'),
	        taxNumber = getInputVal(e, 'taxnumber'),
	        companyAddress = getInputVal(e, 'companyaddress'),
	        phone = getInputVal(e, 'phone'),
	        accountBank = getInputVal(e, 'accountbank'),
	        bankAccount = getInputVal(e, 'bankaccount');
        if (companyName || taxNumber || companyAddress 
        		|| phone || accountBank || bankAccount) {
        	invoice.push({
        		invoiceId: invoiceId,
        		companyName: companyName,
        		taxNumber: taxNumber,
        		companyAddress: companyAddress,
        		phone: phone,
        		accountBank: accountBank,
        		bankAccount: bankAccount
        	});
        } else if (invoiceId) {
        	deleteIds.push(invoiceId);
        }
    });
    $(deleteIds).each(function (i, item) {
    	deleteInvoiceInfoIds.push(item);
    });
    return invoice;
}

// 获取银行信息
function getBankInfos(needVerify) {
	var flag = true;
    var bankinfos = [];
    var customerTypeName = $('dd[lay-value="' + $('#customerType').val() + '"]').text();
    var deleteIds = [];
    $('.gradient').each(function (index, item) {
    	if (!flag) {
            return;
        }
        var e = $(this);
        var	bankAccountId = getInputVal(e, 'bankAccountId'),
        	accountName = getInputVal(e, 'accountname'),
    		accountBank = getInputVal(e, 'accountbank'),
    		bankAccount = getInputVal(e, 'bankaccount');
        if (accountName || accountBank || bankAccount) {
        	bankinfos.push({
        		bankAccountId: bankAccountId,
        		accountName: accountName,
        		accountBank: accountBank,
        		bankAccount: bankAccount
        	});
        } else if (bankAccountId) {
        	deleteIds.push(bankAccountId);
        }
    });
    $(deleteIds).each(function (i, item) {
    	deleteBankIds.push(item);
    })
    return bankinfos;
}

function getInputVal(ele, name) {
	var val = ele.find('input[name="' + name + '"]').val();
	if (val) {
		val = val.trim();
	}
	return val;
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
}

function arrayRemove(array, val) {
    var index = arrayIndexOf(array, val);
    if (index > -1) {
        return array.splice(index, 1);
    }
    return null;
}

// 查询当前客户以前上传的合同
function getOldContractData() {
    // 放在合同客户中的老合同文件
    var oldFiles = $("#contractFilesInfo").val();
    if (isNotBlank(oldFiles)) {
        oldFiles = JSON.parse(oldFiles);
        if (oldFiles.length > 0) {
            oldContractData = {
                contractId: '-',
                contractName: '过去上传到客户信息的合同',
                applyDate: '-',
                validityDateStart: '-',
                validityDateEnd: '-',
                contractFilesScan: oldFiles,
                contractType: '-'
            }
        }
    }
}

// 合同表格
function initContractTable() {
    layui.use('table', function () {
        table = layui.table;
        table.render({
            elem: '#contractFiles',
            url: '/contract/readContractByEntityId.action?temp=' + Math.random(),
            // toolbar: '#toolbarDemo',
            defaultToolbar: false,
            method: 'POST',
            // width: 800,
            where: {
                entityId: $('#customerId').val()
            },
            cols: [
                [{
                    field: 'contractId',
                    title: '合同编号',
                    align: 'center',
                    width: '15%',
                }, {
                    field: 'contractName',
                    title: '合同名称',
                    align: 'center',
                    width: '15%',
                }, {
                    field: 'status',
                    title: '合同评审状态',
                    align: 'center',
                    width: '10%',
                    templet: function (row) {
                        if (row.status == '申请中') {
                            return '<div style = "background-color:#1E9FFF;margin:1px;padding:1px;height:100%;width:100%;color: white"> 申请中 </div>';
                        } else if (row.status == '已归档') {
                            return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已归档 </div>';
                        } else if (row.status == '已取消') {
                            return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已取消 </div>';
                        } else {
                            return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                        }
                    }
                }, {
                    field: 'applyDate',
                    title: '申请日期',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'validityDateStart',
                    title: '有效期开始',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'validityDateEnd',
                    title: '有效期结束',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'contractFilesScan',
                    title: '文件',
                    align: 'center',
                    width: '20%',
                    templet: function (row) {
                        var files = row.contractFilesScan;
                        var dom = '';
                        if (isNotBlank(files)) {
                            files = (typeof files == 'object') ? files : JSON.parse(files);
                            $.each(files, function (i, item) {
                                var fileJson = JSON.stringify(item);
                                dom += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='down_load(" + fileJson + ")'>" + item.fileName + "</a>";
                                dom += "<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='view_File(" + fileJson + ")'>预览</button></br>";
                            })
                        }
                        return dom;
                    }
                }, {
                    field: 'contractType',
                    title: '合同类型',
                    align: 'center',
                    width: '10%',
                }]
            ],
            parseData: function (res) {
                var code = 0;
                if (res.code !== 200) {
                    code = res.code;
                }
                var data = res.data;
                data = isBlank(oldContractData) ? data : data.concat(oldContractData);
                return {
                    "code": code,
                    "msg": res.msg, //解析提示文本
                    "data": data //解析数据列表
                };
            }

        });
    })
}