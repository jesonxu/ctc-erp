var origFormData = getFormData();
$(document).ready(function() {
    initProductParamType();
    formatProductParam();
    initClickCheckbox();
    loadProductType();
    var directConnect = $("#directConnect").val();
    if (directConnect == 'true') {
        $(".directConnect").show();
    }
	layui.use('form', function() {
	var form = layui.form;
		form.on("select(productType)", function(data){
            // 国际短信、一键登录 这些产品类型不展示 落地省份 和 到达省份
            if (data.value == '4' || data.value == '5') {
				$(".reachProvinceProviceLabelUnit").hide();
				$(".baseProvinceUnit").hide();
			} else {
				$(".reachProvinceProviceLabelUnit").show();
				$(".baseProvinceUnit").show();
			}
            // 语音(按时计费)
            if (data.value == '6') {
				$(".voiceUnit").show();
			} else {
				$(".voiceUnit").hide();
			}
		});
		initGradientEvent= "";
		form.render();
	});
});

function initClickCheckbox() {
    layui.use('element', function() {
        var element = layui.element;
    });
    layui.use('form', function() {
        var element = layui.element;
        var form = layui.form;
        // 地区
        form.on('checkbox(reachProvinceCountry)', function(data) {
            if (data.elem.checked) { // 全选
                $('input.reachProvince').prop("checked", true);
                form.render('checkbox');
            } else { // 全不选
                $('input.reachProvince').prop("checked", false);
                form.render('checkbox');
            }
        });
        form.on('checkbox(reachProvinceProvice)', function(data) {
            var selectAll = true;
            $('input.reachProvince').each(function() {
                if (!$(this).prop('checked')) {
                    return selectAll = false;
                }
            });
            if (selectAll) {
                $('input#reachProvinceCountry').prop("checked", true);
            } else {
                $('input#reachProvinceCountry').prop("checked", false);
            }
            form.render('checkbox');
        });
        // 是否直连的选择框，如果是直连，产品标识必填通道名称
        form.on('select(directConnect)', function(data) {
            if (data.value == 'true') {
                $('.directConnect').show();
                $('#productMark').val('');
            } else if (data.value == 'false') {
                $('.directConnect').hide();
            }
        });
    });

    $('#cancel').click(function() {
        var index = parent.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
        parent.layer.close(index);
    });

    $('#submit').click(function() {
    	
        var formData = getFormData();

        if (JSON.stringify(origFormData) == JSON.stringify(formData)) {
            return layer.msg("没有修改");
        }
       
        if (!formData.productName) {
            $("#productName").focus();
            return layer.tips("请输入产品名称", $("#productName"));
        }
        if (!formData.productType) {
            $("#productType").focus();
            return layer.tips("请选择产品类型", $("#productType"));
        }
        if (formData.directConnect == 'true') {
            if (!formData.productMark) {
                $("#productMark").focus();
                return layer.tips("请输入产品标识", $("#productMark"));
            }
        }
        if (formData.productParam == "") {
            $("#productParam").focus();
            return layer.tips("请输入产品参数", $("#productParam"));
        } else {
            formData.productParam = formData.productParam + "\n协议类型:" + $("#productParamType").find("option:selected").text();
        }
        if (formData.productType == "4" || formData.productType == "5"){
	        formData.reachProvince = 0;
	        formData.baseProvince = 0;
	    } else {
	        if (!formData.baseProvince) {
	            $("#baseProvince").siblings(":first").find('input').focus();
	            return layer.tips("请选择落地省份", $("#baseProvince").siblings(":first").find('input'));
	        }
	    }
        if (!formData.currencyType) {
        	$("#currencyType").siblings(":first").find('input').focus();
        	return layer.tips("请选择结算币种", $("#currencyType").siblings(":first").find('input'));
        }
        if (formData.settleType !== 0 && !formData.settleType) {
            $("#settleType").siblings(":first").find('input').focus()
            return layer.tips("请选择结算方式", $("#settleType").siblings(":first").find('input'));
        }
        if(formData.lowdissipation){
        	if (!/^(([1-9][0-9]*)|(([0]\.\d{1,4}|[1-9][0-9]*\.\d{1,2})))$/.test(formData.lowdissipation)){
            	$("#lowdissipation").focus();
            	return layer.tips("最低套餐金额为数字且最多两位小数", "#lowdissipation");
        	} else if (formData.lowdissipation > 1000000000) {
        		$("#lowdissipation").focus();
            	return layer.tips("最低套餐金额数值过大", "#lowdissipation");
        	}
        }
        if(formData.unitvalue){
        	if (!/^[0-9]{1,}\d{0,}$/.test(formData.unitvalue)){
            	$("#unitvalue").focus();
            	return layer.tips("最低套餐条数只能为正整数", "#unitvalue");
        	} else if (formData.unitvalue + 1 > Math.pow(2, 31)) {
        		$("#unitvalue").focus();
        		return layer.tips("最低套餐条数数值过大", "#unitvalue");
        	}
        }
        $.ajax({
            type: "POST",
            async: false,
            url: "/product/save.action",
            dataType: 'json',
            data: formData,
            success: function(data) {
            	if (data.code != 200) {
            		return layer.msg(data.msg);
                }
                var index = parent.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
                parent.layer.close(index);
                parent.layer.msg(data.msg);
                if (typeof parent.loadSupplierProducts == 'function') {
                    parent.loadSupplierProducts(parent.supplierId)
                }
            }
        });
    });

}

function getFormData() {
    var formData =  {
        supplierId: $("#supplierId").val(),
        voiceUnit: $("#voiceUnit").val(),
    	productId: $("#productId").val(),
        productName: $("#productName").val().trim(),
        productType: $("#productType").val(),
        productParam: $("#productParam").val().trim(),
        directConnect: $("#directConnect").val(),
        productMark: $("#productMark").val().trim(),
        reachProvince: '',
        baseProvince: $('#baseProvince').val(),
        currencyType: $('#currencyType').val(),
        settleType: $('#settleType').val(),
        lowdissipation: $('#lowdissipation').val().trim(),
        unitvalue: $('#unitvalue').val().trim()
    };
    
    $('input[name="reachProvince"]:checked').each(function() {
		formData.reachProvince += ($(this).val() + ',');
	});
    return formData;
}


function initProductParamType(){
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            type: "POST",
            async: false,
            url: '/product/loadProductParamType.action?temp=' +  Math.random(),
            dataType: 'json',
            data: {
                paramType: 1
            },
            success: function (data) {
                var productParamType = $("#productParamTypeH").val();
                for (var i = 0; i < data.length; i++) {
                	var optstr;
                	if(data[i].depict==productParamType){
                		optstr = '<option value="' + data[i].extended + '" selected="selected">' + data[i].depict + '</option>'
                	} else {
                		optstr = '<option value="' + data[i].extended + '">' + data[i].depict + '</option>'
                	}
                    $('form#addProduct select[name="productParamType"]').append(optstr);
                }
                layui.form.render('select');
                initProductParamChange();
            }
        });
    });
}

function initProductParamChange(){
    layui.use('form', function () {
        var form = layui.form;
        form.on('select(productParamType)', function(data){
            if(data.value) {
                $("#productParam").val('');
                $(data.value).find("e").each(function() {
                    var element = $(this);
                    var key = element.attr("k");//读取节点属性
                    var val = element.text();//读取节点的值
                    $("#productParam").val($("#productParam").val()+key+":"+val+"\n");
                });
            } else {
                $("#productParam").val('');
            }
        });
    });
}

function formatProductParam() {
    var xml = $("#productParamVal").val();
    if(xml) {
        $("#productParam").val('');
        $(xml).find("e").each(function() {
            var element = $(this);
            var key = element.attr("k");//读取节点属性
            var val = element.text();//读取节点的值
            if ('协议类型'==key) {
            	$("#productParamTypeH").val(val);
            } else {
            	$("#productParam").val($("#productParam").val()+key+":"+val+"\n");
        	}
        });
    } else {
        $("#productParam").val('');
    }
}

// 获取产品类型
function loadProductType() {
    var productType = $('#productTypeH').val();
    var options = "";
    $.ajax({
        type: "POST",
        async: false,
        url: "/productType/getProductTypeSelect.action",
        dataType: 'json',
        data: {},
        success: function (data) {
            if (isNotBlank(data) && data.length > 0) {
                $.each(data, function (index, item) {
                    options = options + "<option value='" + item.value + "' " + (productType == item.value ? 'selected' : '') +">" + item.name + "</option>";
                });
                $("#productType").html(options);
            }
            if (productType == 6) {
                $(".voiceUnit").show();
            }
            if (productType == 4 || productType == 5) {
                $(".reachProvinceProviceLabelUnit").hide();
                $(".baseProvinceUnit").hide();
            }
        }
    });
}