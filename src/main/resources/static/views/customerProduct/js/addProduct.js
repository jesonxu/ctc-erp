// 获取系统参数的配置，判断是电商还是通信进行验证
var product_items = $("#product_items").val();
console.log(product_items);
var isDianShang;
if(product_items.indexOf("voiceUnit") >= 0 ) { 
	isDianShang = false;
} else {
	isDianShang = true;
}
$(document).ready(function () {
    // 隐藏必填的红*
    var accountRequired = $("#accountRequired").val();
    if (accountRequired === "false") {
        $("#accountSpan").hide();
    }
    initSelect();
    initButton();
    initBillTaskDay();
    loadDirectChannel();
    loadProductType();
    // 默认选中全部运营商
    $('input[name=yysType]').each(function (){
       $(this).prop("checked", true);
    });
});

function initSelect() {
    layui.use('form', function () {
        var form = layui.form;
        form.on("select(productType)", function(data){
            if (data.value === 4 || data.value === '4') { // 国际短信
                $(".yysTypeDiv").hide();
                $(".voiceUnit").hide();
            } else if(data.value === 6 || data.value === '6') { // 语音（按时计费）
                $(".yysTypeDiv").show();
                $(".voiceUnit").show();
            } else {
                $(".yysTypeDiv").show();
                $(".voiceUnit").hide();
            }
        });
        // 是否直连的选择框，如果是直连，要选择对应的直连通道
        form.on('select(directConnect)', function(data) {
            var accountEle = $('#account');
            if (data.value == 'true') {
                $('.directConnect').css('display', '');
                var directChannel = $('#directChannel');
                accountEle.val(directChannel.val());
                accountEle.attr('readonly', 'true');
            } else if (data.value == 'false') {
                $('.directConnect').css('display', 'none');
                accountEle.removeAttrs('readonly');
                accountEle.val('');
            }
        });
        // 选中直连通道后，给将通道名称作为账号
        form.on('select(directChannel)', function(data) {
            $('#account').val(data.value);
        });
        // 运营商类型全选框
        form.on('checkbox(yysTypeAll)', function(data) {
            if (data.elem.checked) { // 全选
                $('input.yysType').prop("checked", true);
                form.render('checkbox');
            } else { // 全不选
                $('input.yysType').prop("checked", false);
                form.render('checkbox');
            }
        });
        // 运营商类型每个框选中后，自动全选
        form.on('checkbox(yysType)', function(data) {
            var selectAll = true;
            $('input.yysType').each(function() {
                if (!$(this).prop('checked')) {
                    return selectAll = false;
                }
            });
            if (selectAll) {
                $('input#yysTypeAll').prop("checked", true);
            } else {
                $('input#yysTypeAll').prop("checked", false);
            }
            form.render('checkbox');
        });
        form.render();
    });
}

function initButton() {
    $('#cancel').unbind().bind('click', function () {
        var index = parent.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
        parent.layer.close(index);
    });

    $('#submit').unbind().bind('click', function () {
        var formData = getFormData();
        if (!formData.productName) {
            $("#productName").focus();
            var tips = getMsg("tips.productName");
            return layer.tips(tips, $("#productName"));
        }
        if(!isDianShang) {
        	// 不是电商这些也要判断
	        if (formData.productType !== 0 && !formData.productType) {
	            $("#productType").focus();
	            var tips = getMsg("tips.productType");
	            return layer.tips(tips, $("#productType"));
	        } else if (formData.productType == 6 && (!formData.voiceUnit || formData.voiceUnit ==0)) {
	            $("#voiceUnit").focus();
	            var tips = getMsg("tips.voiceUnit");
	            return layer.tips(tips, $("#voiceUnit"));
	        }
	        if (!formData.account) {
	            var accountRequired = $("#accountRequired").val();
	            if (accountRequired !== "false") {
	                $("#account").focus();
	                var tips = getMsg("tips.account");
	                return layer.tips(tips, "#account");
	            }
	        }
	        if (formData.billType !== 0 && !formData.billType) {
	            $("#billType").focus();
	            var tips = getMsg("tips.billType");
	            return layer.tips(tips, $("#billType"));
	        }
	        if (formData.billTaskDay !== 0 && !formData.billTaskDay) {
	            $("#billTaskDay").focus();
	            var tips = getMsg("tips.billTaskDay");
	            return layer.tips(tips, $("#billTaskDay"));
	        }
	        if (formData.settleType !== 0 && !formData.settleType) {
	            $("#settleType").focus();
	            var tips = getMsg("tips.settleType");
	            return layer.tips(tips, $("#settleType"));
	        }
            if (isBlank(formData.yysType)) {
                $('#yysTypeAll').focus();
                return layer.tips("请选择运营商发送范围", $('#yysTypeAll'));
            }
        }
        if (!formData.sendDemo) {
            $("#sendDemo").focus();
            var tips = getMsg("tips.sendDemo");
            return layer.tips(tips, $("#sendDemo"));
        }

        $.ajax({
            type: "POST",
            async: false,
            url: "/customerProduct/save.action",
            dataType: 'json',
            data: formData,
            success: function (data) {
                if (data.code != 200) {
                    return layer.alert(data.msg);
                }
                var index = parent.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
                parent.layer.close(index);
                parent.layer.msg(data.msg);
                if (typeof parent.query_customer_products == 'function') {
                    parent.query_customer_products(parent.customerId)
                }
            }
        });
    });
}

function getFormData() {
    var yysType = [];
    if ($('#yysTypeAll').prop('checked') || $('.yysTypeDiv').is(':hidden')) {
        yysType.push('1000');
    } else {
        $('input[name=yysType]:checked').each(function () {
            yysType.push($(this).val());
        })
    }

    var formData = {
        customerId: $("#customerId").val(),
        productId: $("#productId").val(),
        productName: $("#productName").val().trim()||"",
        productType: $("#productType").val()||"",
        directConnect: $("#directConnect").val(),
        account: $("#account").val()||"",
        billType: $('#billType').val()||"",
        billCycle: $('#billCycle').val()||"",
        settleType: $('#settleType').val()||"",
        billTaskDay: $("#billTaskDay").val()||"",
        voiceUnit: $("#voiceUnit").val()||"",
        sendDemo: $("#sendDemo").val()||"",
        yysType: yysType.join(',')
    };
    return formData;
}

// 生成账单日下拉框选项 1~31日
function initBillTaskDay() {
    var options = "";
    for(var i=1 ; i<=31; i++){
        options +="<option value='" + i + "'>" + i + "</option>";
    }
    $("#billTaskDay").append(options);
}

// 获取供应商的直连通道
function loadDirectChannel() {
    var options = "";
    $.ajax({
        type: "POST",
        async: false,
        url: "/product/getDirectChannel.action",
        dataType: 'json',
        data: {},
        success: function (data) {
            if (isNotBlank(data) && data.length > 0) {
                $.each(data, function (index, item) {
                    options = options + "<option value='" + item.value + "'>" + item.name + "</option>";
                });
                $("#directChannel").html(options);
            }
        }
    });
}

// 获取产品类型
function loadProductType() {
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
                    options = options + "<option value='" + item.value + "'>" + item.name + "</option>";
                });
                $("#productType").html(options);
            }
        }
    });
}