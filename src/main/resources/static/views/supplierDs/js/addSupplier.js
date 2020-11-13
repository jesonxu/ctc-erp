layui.use(['laydate','layer', 'upload', 'form', 'element'], function() {
	var layer = layui.layer;
    var laydate = layui.laydate;
    var upload = layui.upload;
    var form = layui.form;
    var layedit = layui.layedit;
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
				for (var i = 0; i < result.length; i++) {
					var temp = result[i];
					dom.append("<option value='" + temp.supplierTypeId + "'>" + temp.supplierTypeName + "</option>");
				}
				
				var form = layui.form;
				form.render();
			}
		}
	});

    // 上传的认证文件信息
    var file_infos = [];
    var sto_index = {};

    // 初始化文件上传--认证文件
    init_file_upload();
    function init_file_upload() {
        //多文件列表示例
        var demoListView = $('#demoList'),
            uploadListIns = upload.render({
                elem: '#contractFiles',
                url: '/operate/upLoadFile',
                field: 'files',
                accept: 'file',
                multiple: true,
                auto: false,
                choose: function (obj) {
                    //将每次选择的文件追加到文件队列
                    var files = this.files = obj.pushFile();
                    demoListView.parent().show();
                    //读取本地文件
                    obj.preview(function (index, file, result) {
                        var tr = $(['<tr id="upload-' + index + '">', '<td>' + file.name + '</td>', '<td>等待上传</td>', '<td>', '<button type="button" class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>', '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>', '</td>', '</tr>'].join(''));
                        obj.upload(index, file);
                        //删除
                        tr.find('.demo-delete').on('click', function () {
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            var file_index = sto_index[index];
                            // 找出对应的位置
                            var file_posi = $.inArray(file_index, file_infos);
                            // 删除文件
                            file_infos.splice(file_posi, 1);
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        // 重新上传
                        tr.find(".demo-reload").on('click', function (e) {
                            obj.upload(index, file);
                        });
                        demoListView.append(tr);
                    });
                },
                done: function (res, index, upload) {
                    if (res.code == 200) { //上传成功
                        var tr = demoListView.find('tr#upload-' + index),
                            tds = tr.children();
                        tds.eq(1).html('<span style="color: #5FB878;">上传成功</span>');
                        //清空操作（去掉重传按钮）
                        tds.eq(2).find("button[class*='demo-reload']").remove();
                        if (res.data.length > 0) {
                            file_infos.push(res.data[0]);
                            sto_index[index] = res.data[0];
                        }
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {
                    var tr = demoListView.find('tr#upload-' + index),
                        tds = tr.children();
                    tds.eq(1).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(2).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        demoListView.parent().hide();
    }
    // 上传的纳税证明或完整的审计报告或上市公司财报等文件信息
    var file_infos1 = [];
    var sto_index1 = {};
    // 初始化文件上传--纳税证明或完整的审计报告或上市公司财报等
    init_file_upload1();
    function init_file_upload1() {
        //多文件列表示例
        var demoListView = $('#demoList1'),
            uploadListIns = upload.render({
                elem: '#contractFiles1',
                url: '/operate/upLoadFile',
                field: 'files',
                accept: 'file',
                multiple: true,
                auto: false,
                choose: function (obj) {
                    //将每次选择的文件追加到文件队列
                    var files = this.files = obj.pushFile();
                    demoListView.parent().show();
                    //读取本地文件
                    obj.preview(function (index, file, result) {
                        var tr = $(['<tr id="upload-' + index + '">', '<td>' + file.name + '</td>', '<td>等待上传</td>', '<td>', '<button type="button" class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>', '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>', '</td>', '</tr>'].join(''));
                        obj.upload(index, file);
                        //删除
                        tr.find('.demo-delete').on('click', function () {
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            var file_index = sto_index1[index];
                            // 找出对应的位置
                            var file_posi = $.inArray(file_index, file_infos1);
                            // 删除文件
                            file_infos1.splice(file_posi, 1);
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        // 重新上传
                        tr.find(".demo-reload").on('click', function (e) {
                            obj.upload(index, file);
                        });
                        demoListView.append(tr);
                    });
                },
                done: function (res, index, upload) {
                    if (res.code == 200) { //上传成功
                        var tr = demoListView.find('tr#upload-' + index),
                            tds = tr.children();
                        tds.eq(1).html('<span style="color: #5FB878;">上传成功</span>');
                        //清空操作（去掉重传按钮）
                        tds.eq(2).find("button[class*='demo-reload']").remove();
                        if (res.data.length > 0) {
                            file_infos1.push(res.data[0]);
                            sto_index1[index] = res.data[0];
                        }
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {
                    var tr = demoListView.find('tr#upload-' + index),
                        tds = tr.children();
                    tds.eq(1).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(2).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        demoListView.parent().hide();
    }
    // 上传的公司管理相关认证文件信息
    var file_infos2 = [];
    var sto_index2 = {};
    // 初始化文件上传--公司管理相关认证
    init_file_upload2();
    function init_file_upload2() {
        //多文件列表示例
        var demoListView = $('#demoList2'),
            uploadListIns = upload.render({
                elem: '#contractFiles2',
                url: '/operate/upLoadFile',
                field: 'files',
                accept: 'file',
                multiple: true,
                auto: false,
                choose: function (obj) {
                    //将每次选择的文件追加到文件队列
                    var files = this.files = obj.pushFile();
                    demoListView.parent().show();
                    //读取本地文件
                    obj.preview(function (index, file, result) {
                        var tr = $(['<tr id="upload-' + index + '">', '<td>' + file.name + '</td>', '<td>等待上传</td>', '<td>', '<button type="button" class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>', '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>', '</td>', '</tr>'].join(''));
                        obj.upload(index, file);
                        //删除
                        tr.find('.demo-delete').on('click', function () {
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            var file_index = sto_index2[index];
                            // 找出对应的位置
                            var file_posi = $.inArray(file_index, file_infos2);
                            // 删除文件
                            file_infos2.splice(file_posi, 1);
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        // 重新上传
                        tr.find(".demo-reload").on('click', function (e) {
                            obj.upload(index, file);
                        });
                        demoListView.append(tr);
                    });
                },
                done: function (res, index, upload) {
                    if (res.code == 200) { //上传成功
                        var tr = demoListView.find('tr#upload-' + index),
                            tds = tr.children();
                        tds.eq(1).html('<span style="color: #5FB878;">上传成功</span>');
                        //清空操作（去掉重传按钮）
                        tds.eq(2).find("button[class*='demo-reload']").remove();
                        if (res.data.length > 0) {
                            file_infos2.push(res.data[0]);
                            sto_index2[index] = res.data[0];
                        }
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                },
                error: function (index, upload) {
                    var tr = demoListView.find('tr#upload-' + index),
                        tds = tr.children();
                    tds.eq(1).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(2).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        demoListView.parent().hide();
    }
    // 提交
    $("#supplier_submit").click(function (e) {
        var form_data = {
            companyName:$("#companyName").val().trim(), //公司名称
            registrationNumber:$("#registrationNumber").val().trim(),  //统一社会信用代码
            registeredCapital:$("#registeredCapital").val().trim(),  //注册资本(万元)
            creationDate:$("#creationDate").val(), //公司创立日期
            companyQualification:$("#companyQualification").val(), //公司资质
            legalRisk:$("#legalRisk").val(), //法律风险
            deliveryCycle:$("#deliveryCycle").val().trim(),  //正常交货周期
            cooperationType:$("#cooperationType").val(), //合作形式
            settlementType:$("#settlementType").val(), //结算方式
            saleType:$("#saleType").val(), //销售方式
            postalAddress:$("#postalAddress").val().trim(),  //公司地址
            telephoneNumber:$("#telephoneNumber").val().trim(),  //公司电话
            certification:$("#certification").val(), //相关技术、资质认证

            contractFiles: JSON.stringify(file_infos), //认证文件

            legalPerson:$("#legalPerson").val().trim(),  //法人
            corporateCredit:$("#corporateCredit").val().trim(),  //法人征信
            productRange:$("#productRange").val().trim(),  //产品范围
            advantageProduct:$("#advantageProduct").val().trim(),  //优势产品
            email:$("#email").val().trim(),  //电子邮件
            website:$("#website").val().trim(),  //公司网页
            contactName:$("#contactName").val().trim(),  //业务联系人
            contactPhone:$("#contactPhone").val().trim(),  //联系手机
            corporateNature:$("#corporateNature").val(), //公司性质
            logistics:$("#logistics").val().trim(), //配送物流
            supplierTypeId:$("#supplierType").val(), //供应商类别ID
            caseContract:$("#caseContract").text().trim(),  //合作客户案例合同
            companyIntroduction:$("#companyIntroduction").text().trim(),  //行业水平及外部评价
            annualIncome:$("#annualIncome").val(), //近两年任一年度主营收入
            isIncomeProve:$("#isIncomeProve").val(), //是否提供有效营收证明
            financialFile: JSON.stringify(file_infos1), //纳税证明或完整的审计报告或上市公司财报等
            manageCertificationFile: JSON.stringify(file_infos2), //公司管理相关认证
        };
        if (!validation(form_data)){
            return;
        }
		/*var invoice = initinvoice();
		if (invoice.length == 0) {
			return;
		}*/
		var bankinfos = initbankinfos();
		if (bankinfos.length == 0) {
			return;
		}
		/*form_data.invoiceInfos = JSON.stringify(invoice);*/
		form_data.bankInfos = JSON.stringify(bankinfos);
        $.ajax({
            type: "POST",
            async: false,
            url: "/supplier/addSupplier.action?entityType=2&temp=" + Math.random(),
            dataType: 'json',
            data: form_data,
            success: function (data) {
                if (data.code == 200) {
                    window.parent.layer.msg("添加成功");
					window.parent.reload_supplier_info(0);
                    // 父页面需要重新加载（新增）
                    close_window();
                }else{
                    layer.msg(data.msg);
                }
            }
        });
    });

    // 校验参数
    function validation(param){
		if (isNull(param.companyName)) {
			$('#companyName').focus();
			layer.tips('公司名称不能为空', '#companyName');
			return false;
		}
		if (isNull(param.registrationNumber)) {
			$('#registrationNumber').focus();
			layer.tips('统一社会信用代码不能为空', '#registrationNumber');
			return false;
		}
		if (isNull(param.registeredCapital)) {
            $('#registeredCapital').focus();
            layer.tips('注册资本不能为空', '#registeredCapital');
            return false;
        }
        if (isNull(param.creationDate)) {
            $('#creationDate').focus();
            layer.tips('公司创立日期不能为空', '#creationDate');
            return false;
        }
        if (isNull(param.companyQualification)) {
            $('#companyQualification').focus();
            layer.tips('公司资质不能为空', '#companyQualification');
            return false;
        }
        if (isNull(param.legalRisk)) {
            $('#legalRisk').focus();
            layer.tips('法律风险不能为空', '#legalRisk');
            return false;
        }
		if (isNull(param.deliveryCycle)) {
            $('#deliveryCycle').focus();
            layer.tips('正常交货周期不能为空', '#deliveryCycle');
            return false;
        }
        if (!isPositiveInteger(param.deliveryCycle)) {
            $("#deliveryCycle").focus();
            layer.tips("正常交货周期输入必须是正整数", $("#deliveryCycle"));
            return false;
        }
		if (isNull(param.cooperationType)) {
			$('#cooperationType').focus();
			layer.tips('合作形式不能为空', '#cooperationType');
			return false;
		}
		if (isNull(param.settlementType)) {
            $('#settlementType').focus();
            layer.tips('结算方式不能为空', '#settlementType');
            return false;
        }
        if (isNull(param.saleType)) {
            $('#saleType').focus();
            layer.tips('销售方式不能为空', '#saleType');
            return false;
        }
        if (isNull(param.postalAddress)) {
            $('#postalAddress').focus();
            layer.tips('公司地址不能为空', '#postalAddress');
            return false;
        }
        if (isNull(param.telephoneNumber)) {
            $('#telephoneNumber').focus();
            layer.tips('公司电话不能为空', '#telephoneNumber');
            return false;
        }
        if (!/^([\d\-]{1,})$/.test(param.telephoneNumber)) {
            $('#telephoneNumber').focus();
            layer.tips('公司电话格式错误', '#telephoneNumber');
            return false;
        }
        if (isNull(param.certification)) {
            $('#certification').focus();
            layer.tips('相关技术、资质认证不能为空', '#certification');
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
});
function isPositiveInteger(s){//是否为正整数
     var re = /^[0-9]+$/ ;
     return re.test(s)
}
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
	htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_info' onclick='reduceInvoice(" + index + ")'><i class='layui-icon layui-icon-close-fill' title='删除银行信息'></i></span>";
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
function reduceInvoice(index) {
	var operateDiv = $(".operate" + index + "");
	var lastgradientDiv = operateDiv.parent();
	var newOperateGradientDiv = lastgradientDiv.prev(); 
	lastgradientDiv.remove();

	var htmlStr = "";
	htmlStr += "<div class='layui-inline operate" + index + "'>";
	htmlStr += "<label class='layui-form-label'>";
	htmlStr += "<span class='add_btn' id='add_invoice' onclick='addInvoice(" + index + ")'> <i class='layui-icon layui-icon-add-circle' title='添加开票信息'></i></span>";
	if ($('.gradient0').length > 1) {
		htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_invoice' onclick='reduceInvoice(" + index + ")'><i class='layui-icon layui-icon-close-fill' title='删除开票信息'></i></span>";
	}
	htmlStr += "</label>";
	htmlStr += "</div>";
	
	newOperateGradientDiv.append(htmlStr)
}

// 移除银行信息
function reducebank(ele) {
	var prev = $(ele).parents('.gradient').prev();
    $(ele).parents('.gradient').remove();

    var htmlStr = "";
    htmlStr += "<div class='layui-inline operate'>";
    htmlStr += "<label class='layui-form-label'>";
    htmlStr += "<span class='add_btn' id='add_bank' onclick='addBank()'> <i class='layui-icon layui-icon-add-circle' title='添加银行信息'></i></span>";
    if ($('.gradient').length > 1 && !$($('.gradient')[$('.gradient').length - 1]).find('input[name="bankAccountId"]').val()) {
        htmlStr += "&nbsp;&nbsp;<span class='add_btn' id='reduce_bank' onclick='reducebank(this)'><i class='layui-icon layui-icon-close-fill' title='删除银行信息'></i></span>";
    }
    htmlStr += "</label>";
    htmlStr += "</div>";
    prev.append(htmlStr);
}

// 获取填写的开票信息
function initinvoice(){
	var flag = true;
	var invoice = [];
	var companyNames = [];
	$('.gradient0').each(function (index, item) {
		if (!flag) {
			return;
		}
		var e = $(this);
        if (isNull(e.find('input[name="companyname"]').val())){
        	flag = false;
        	e.find('input[name="companyname"]').focus();
        	return layer.tips('公司名称不能为空' , e.find('input[name="companyname"]'));
        }else if (companyNames.indexOf(e.find('input[name="companyname"]').val()) >= 0) {
    		flag = false;
    		e.find('input[name="companyname"]').focus();
    		return layer.tips('公司名称重复' , e.find('input[name="companyname"]'));
        }
        if (isNull(e.find('input[name="taxnumber"]').val())){
        	flag = false;
        	e.find('input[name="taxnumber"]').focus();
        	return layer.tips('税务号不能为空' , e.find('input[name="taxnumber"]'));
        }
        if (isNull(e.find('input[name="companyaddress"]').val())){
        	flag = false;
        	e.find('input[name="companyaddress"]').focus();
        	return layer.tips('公司地址不能为空' , e.find('input[name="companyaddress"]'));
        }
        if (isNull(e.find('input[name="phone"]').val())){
        	flag = false;
        	e.find('input[name="phone"]').focus();
        	return layer.tips('联系电话不能为空' , e.find('input[name="phone"]'));
        }
        if (isNull(e.find('input[name="accountbank"]').val())){
        	flag = false;
        	e.find('input[name="accountbank"]').focus();
        	return layer.tips('开户银行不能为空' , e.find('input[name="accountbank"]'));
        }
        if (isNull(e.find('input[name="bankaccount"]').val())){
        	flag = false;
        	e.find('input[name="bankaccount"]').focus();
        	return layer.tips('银行账号不能为空' , e.find('input[name="bankaccount"]'));
        }
        invoice.push({
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

// 获取填写的银行信息
function initbankinfos(){
	var flag = true;
	var bankinfos = [];
	var bankAccounts = [];
	$('.gradient').each(function (index, item) {
		if (!flag) {
			return;
		}
		var e = $(this);
		if (isNull(e.find('input[name="accountname"]').val())){
        	flag = false;
        	e.find('input[name="accountname"]').focus();
            return layer.tips('名称不能为空' , e.find('input[name="accountname"]'));
        }
        if (isNull(e.find('input[name="accountbank"]').val())){
        	flag = false;
        	e.find('input[name="accountbank"]').focus();
            return layer.tips('开户银行不能为空' , e.find('input[name="accountbank"]'));
        }
        if (isNull(e.find('input[name="bankaccount"]').val())){
        	flag = false;
        	e.find('input[name="bankaccount"]').focus();
            return layer.tips('银行账号不能为空' , e.find('input[name="bankaccount"]'));
        } else if (bankAccounts.indexOf(e.find('input[name="bankaccount"]').val()) >= 0) {
    		flag = false;
    		e.find('input[name="bankaccount"]').focus();
    		return layer.tips('银行账号重复' , e.find('input[name="bankaccount"]'));
        }
		bankinfos.push({
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
