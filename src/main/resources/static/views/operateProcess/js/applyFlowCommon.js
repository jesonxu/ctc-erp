var dates = [];
var dateItemIndex = 0;
var initGradientEvent = "";
var initFile = "";
// 存储上传的文件内容
var file_result = {};
// 已经上传文件的数据对应、记录临时数据
var upload_file = {};

var renderFormFuns = [];

var dateRender2TimeSlot = [];
var dateRender2Month = [];
var dateRender2Day = [];
var dateRender2DateTime = [];
// 用于记录时间账单信息 标签的数量
var timeAccountBillDate = [];
// 不同流程的标签名称
var billLabelName = {
    //付款流程
    "[BillPaymentFlow]": {
        payables_name: "应付金额：",
        actualpayables_name: "已付金额：",
        left_should_pay_name: "剩余应付：",
        thisPayment_name: "本次付款：",
        total_name: "合计付款："
    },
    // 收款流程
    "[RemunerationFlow]": {
        payables_name: "应收佣金：",
        actualpayables_name: "已收佣金：",
        left_should_pay_name: "剩余收款：",
        thisPayment_name: "本次收款：",
        total_name: "合计收款："
    },
    //销售收款流程
    "[BillReceivablesFlow]": {
        payables_name: "应收金额：",
        actualpayables_name: "已收金额：",
        left_should_pay_name: "剩余应收：",
        thisPayment_name: "本次收款：",
        total_name: "合计收款："
    },
    // 发票流程
    "[InvoiceFlow]": {
        payables_name: "应开金额：",
        actualpayables_name: "已开金额：",
        left_should_pay_name: "剩余应开：",
        thisPayment_name: "本次开票：",
        total_name: "合计开票："
    },
    // 充值流程
    "[PaymentFlow]": {
    	recharge_account: "账号",
    	current_amount: "当前余额",
        price: "单价",
        recharge_amount: "充值金额",
        pieces: "条数"
    }
};

// 标签名称
var billInputName = {
    // 资源账单付款流程
    "[BillPaymentFlow]": {
        payables_name: "payables",
        actualpayables_name: "actualpayables",
        left_should_pay_name: "left_should_pay",
        thisPayment_name: "thisPayment"
    },

    // 酬金收款流程
    "[RemunerationFlow]": {
        payables_name: "receivables",
        actualpayables_name: "actualReceivables",
        left_should_pay_name: "left_should_receive",
        thisPayment_name: "thisReceivables"
    },

    //销售账单收款流程
    "[BillReceivablesFlow]": {
        payables_name: "receivables",
        actualpayables_name: "actualReceivables",
        left_should_pay_name: "left_should_receive",
        thisPayment_name: "thisReceivables"
    },

    // 发票流程
    "[InvoiceFlow]": {
        payables_name: "receivables",
        actualpayables_name: "actualInvoiceAmount",
        left_should_pay_name: "left_should_receive：",
        thisPayment_name: "thisReceivables"
    },
    
    // 充值流程
    "[PaymentFlow]": {
    	recharge_account: "rechargeAccount",
    	current_amount: "currentAmount",
        price: "price",
        recharge_amount: "rechargeAmount",
        pieces: "pieces"
    }
};

// 给div内的各个发起按钮绑定点击事件
function bindApplyFlowClick(buttonBody) {
    var btns = buttonBody.find("button[class*='applyFlow']");
    if (btns.length === 0) {
        return;
    }
    $.each(btns, function (i, item) {
        var flowId = $(item).attr("data-my-id");
        $(item).unbind().bind("click", function () {
            // 重复点击收起所有标签
            if ($(this).hasClass("layui-btn-primary") === false) {
                $(this).parent().parent().find('.applyFlowLabel').remove();
                $(this).addClass("layui-btn-primary");
                return;
            }
            // 按钮父元素
            var button_block = $(this).parent();
            // 当前模块顶级元素
            var module_ele = $(this).parents("div[class*='move_outer']");
            var before = buttonBody.find("button[class!='layui-btn-primary']");
            $(before).addClass("layui-btn-primary");
            $(this).removeClass("layui-btn-primary");
            $.ajax({
                type: "POST",
                async: false,
                url: '/operate/getFlowTabLabel.action?temp=' + Math.random(),
                dataType: 'json',
                data: {flowId: flowId},
                success: function (data) {
                    if (typeof initFlowLabels == 'function') {
                		initFlowLabels(item, data.data);
            			setTimeout(function(){
            				$(module_ele).animate({
            					scrollTop: $(module_ele).scrollTop() + $(button_block).offset().top
        					}, 200);
        				},100);
                    }
                }
            });
        })
    });
}

// 初始化流程标签
function initFlowLabels(ele, flow) {
    var html = "<div id='flow_" + flow.flowId + "' class='applyFlowLabel' style='padding: 5px; margin: 5px 0 5px 0'>"
        + "<xmp hidden name='flowData' >" + JSON.stringify(flow) + "</xmp>"
        + "<form class='layui-form layui-show' id='" + flow.flowId + "' action=''>";
    if(flow.flowLabels.length > 0) {
        // 初始化标签
        html += initFormItem(flow, dateRender2DateTime, dateRender2Day, dateRender2Month, dateRender2TimeSlot);
    }
    html += "<div class='layui-form-item' style='text-align: center;padding-top: 10px'>" +
        "<button type='button' class='layui-btn layui-btn-normal'" +
        " onclick='toApplyFlow(\"flow_" + flow.flowId + "\")'>立即发起</button></div>"
        + "</form>"
        + "</div>";

    $(ele).parent().parent().find('div[class*=applyFlowLabel]').remove();
    $(ele).parent().after(html);
    // 渲染日期
    renderDate(dateRender2DateTime, dateRender2Day, dateRender2Month, dateRender2TimeSlot, dates);
    initGradient(flow.flowId);
    initFileUpload(initFile, flow.flowId);
    initFile = "";
    initValidate(flow.flowId);
    bindInvoiceClick();
    initDsApplyOrderTable(flow.flowId);
    initDsMatchOrderTable(flow.flowId);
    custInvoicePriceBindChange(flow.flowId,0);
    bindLeaveTypeCheck(flow.flowId);

    // 绑定事件
    if (renderFormFuns && renderFormFuns.length > 0) {
    	for (var i = 0; i < renderFormFuns.length; i++) {
    		if (typeof renderFormFuns[i] == 'function') {
    			renderFormFuns[i]($('#flow_' + flow.flowId), flow.flowLabels);
    		}
    	}
    	renderFormFuns = [];
    }
}

function initDsApplyOrderTable(flowId) {
    layui.use('table', function () {
        var table = layui.table;
        var orderTableName = 'order-table-' + flowId;
        table.init(orderTableName, {limit: 999});
        table.on('edit('+ orderTableName +')', function (obj) {
            if (isBlank(obj.data.id)) {
                obj.data.id = guid();
                var orderData = table.cache[orderTableName];
                var index = 0;
                // 计算当前行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (isBlank(orderData[i].id)) {
                        index = i;
                        break;
                    }
                }
                // 赋值
                orderData[index].id = obj.data.id;
                table.reload(orderTableName, {
                    url: '',
                    data: orderData
                });
            }
            // 输入内容校验
            if (obj.field === 'amount') {
                if(!$.isNumeric(obj.data.amount)) {
                    obj.data.amount = 0;
                    var orderData = table.cache[orderTableName];
                    var index = 0;
                    // 计算当前行的index
                    for (var i = 0; i < orderData.length; i++) {
                        if (orderData[i].uid === obj.data.uid) {
                            index = i;
                            break;
                        }
                    }
                    orderData[index].amount = 0;
                    layer.msg("数量只能是数字!");
                    table.reload(orderTableName, {
                        url: '',
                        data: orderData
                    });
                }
            } else if (obj.field === 'price') {
                if(!$.isNumeric(obj.data.price)) {
                    obj.data.price = 0.00;
                    var orderData = table.cache[orderTableName];
                    var index = 0;
                    // 计算当前行的index
                    for (var i = 0; i < orderData.length; i++) {
                        if (orderData[i].uid === obj.data.uid) {
                            index = i;
                            break;
                        }
                    }
                    orderData[index].price = '0.00';
                    layer.msg("单价只能是数字!");
                    table.reload(orderTableName, {
                        url: '',
                        data: orderData
                    });
                }
            }
            if (isNotBlank(obj.data.price) && isNotBlank(obj.data.amount)) {
                var orderData = table.cache[orderTableName];
                var index = 0;
                // 计算当前的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].id === obj.data.id) {
                        index = i;
                        break;
                    }
                }
                // 赋值总额
                var total = parseInt(obj.data.amount) * parseFloat(obj.data.price);
                orderData[index].total = format_num(total, 2);
                table.reload(orderTableName, {
                    url: '',
                    data: orderData
                });
            }
        });
        table.on('tool('+ orderTableName +')', function (obj) {
            var layEvent = obj.event; //获得 lay-event 对应的值
            if (layEvent === 'add') { //添加
                dsAddProduct(obj, orderTableName);
            } else if (layEvent === 'delete') { //删除
                layer.confirm('真的要删除行吗？', function (index) {
                    //删除对应行（tr）的DOM结构，并更新缓存
                    var orderData = table.cache[orderTableName];
                    var next = 1;
                    // 计算当前行下一行的index
                    for (var i = 0; i < orderData.length; i++) {
                        if (orderData[i].id == obj.data.id) {
                            next = i + 1;
                            break;
                        }
                    }
                    // 删除数据
                    orderData.splice(next - 1, 1);
                    table.reload(orderTableName, {
                        url: '',
                        data: orderData
                    });
                    layer.close(index);
                });
            }
        });
    });
}

function initDsMatchOrderTable(flowId) {
    layui.use('table', function () {
        var table = layui.table;
        var orderTableName = 'match-order-table-' + flowId;
        table.init(orderTableName, {limit: 999});
        table.on('edit('+ orderTableName +')', function (obj) {
            if (isBlank(obj.data.id)) {
                obj.data.id = guid();
                var orderData = table.cache[orderTableName];
                var index = 0;
                // 计算当前行下一行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (isBlank(orderData[i].id)) {
                        index = i;
                        break;
                    }
                }
                // 删除数据
                orderData[index].id = obj.data.id;
                table.reload(orderTableName, {
                    url: '',
                    data: orderData
                });
            }
        });
        table.on('tool('+ orderTableName +')', function (obj) {
            var layEvent = obj.event; //获得 lay-event 对应的值
            if (layEvent === 'delete') { //删除
                layer.confirm('真的要删除行吗？', function (index) {
                    //删除对应行（tr）的DOM结构，并更新缓存
                    var orderData = table.cache[orderTableName];
                    var next = 1;
                    // 计算当前行下一行的index
                    for (var i = 0; i < orderData.length; i++) {
                        if (orderData[i].id == obj.data.id) {
                            next = i + 1;
                            break;
                        }
                    }
                    // 删除数据
                    orderData.splice(next - 1, 1);
                    table.reload(orderTableName, {
                        url: '',
                        data: orderData
                    });
                    layer.close(index);
                });
            }
        });
    });
}

function dsAddProduct(obj, tableName) {
    var table = layui.table;
    var oldData = table.cache[tableName];
    var next = 1;
    // 计算当前行下一行的index
    for (var i = 0; i < oldData.length; i++) {
        if (oldData[i].id == obj.data.id) {
            next = i + 1;
            break;
        }
    }
    // 将新数据添加到当前行下一行
    oldData.splice(next, 0, {'id': guid()});
    // 重新加载节点table
    table.reload(tableName, {
        url: '',
        data: oldData
    });
}

function initValidate(id) {
    var flowEle = $("#flow_" + id);
    flowEle.find(".notnull").on('blur', function() {
        str = $(this).val();
        if (str === "" || str === null || str === undefined || str === "null" || str === "undefined") {
            layer.tips('不能为空', this);
        }
    });

    flowEle.find('.isnum').on('keyup', function() {
        $(this).val($(this).val().replace(/[^0-9.]/g,''));
    }).bind("paste",function(){ //CTR+V事件处理
        $(this).val($(this).val().replace(/[^0-9.]/g,''));
    }).bind("focus",function(){
        if (parseFloat($(this).val()) === 0) {
            $(this).val("");
        }
    }).css("ime-mode", "disabled"); //CSS设置输入法不可用

    flowEle.find('.isint').on('blur', function() {
        str = $(this).val();
        if(str.length > 0) {
            if(!/^[0-9]*[1-9][0-9]*$/.test(str)) {
                layer.tips('只能为正整数', this);
            }
        }
    });

    flowEle.find('.isdecimal').on('blur', function() {
        str = $(this).val();
        if(str.length > 0) {
            if(!/^([1-9][0-9]*|[0-9])(\.[0-9]*|)$/.test(str)) {
                layer.tips('请填写正确的数据格式', this);
            }
        }
    });

    flowEle.find('.isdecimal2').on('blur', function() {
        var str = $(this).val();
        if(str.length > 0) {
            if(!/^([1-9][0-9]*|[0-9])(\.[0-9]{1,2}|)$/.test(str)) {
                var result = parseFloat($(this).val()).toFixed(3);
                result = result.substring(0, result.length-1);
                $(this).val(result);
            }
        } else {
            $(this).val(parseFloat(0).toFixed(2));
        }
    });

    flowEle.find('.lastMax').on('blur', function() {
        var max =  $(this).val();
        if(max !='' && max.length > 0) {
            var min = $($(this).parent().parent().prev().find("input").get(1)).val();
            if(parseInt(min) > parseInt(max)) {
                layer.tips('不能小于这一梯度最小值', this);
            }
        }
    });
    // 处理酬金标签的单位
    takeRemunerationUnit(id);
    takeRemunerationInput(id);
    // 处理账单金额标签的单位
    // takeBillMoneyUnit(id);
    // 处理账单金额标签内容改变
    takeBillMoneyChange(id);
    // 处理账单信息标签的单位
    takeBillUnit(id);
    // 处理发票信息标签的单位
    takeInvoiceUnit(id);
}

// 开票信息下拉框事件
function bindInvoiceClick() {
    layui.use('form', function () {
        var form = layui.form;
        form.on('select(invoice-0)', function(data){
            $(data.othis).parents('.layui-form-item').find('div.invoice').remove();
            // $(data.othis).parents('.layui-form-item').append(showInvoiceInfoDetail(data.value, 'selfInvoice'));
            $(data.othis).parent().attr('title',$(this).attr('title'));
        });
        form.on('select(invoice-1)', function(data){
            $(data.othis).parents('.layui-form-item').find('div.invoice').remove();
            // $(data.othis).parents('.layui-form-item').append(showInvoiceInfoDetail(data.value, 'otherInvoice'));
            $(data.othis).parent().attr('title',$(this).attr('title'));
        });
        form.on('select(invoice-2)', function(data){
            $(data.othis).parents('.layui-form-item').find('div.bank-account').remove();
            $(data.othis).parents('.layui-form-item').append(showBankInfoDetail(data.value, 'selfBank'));
        });
        form.on('select(invoice-3)', function(data){
            $(data.othis).parents('.layui-form-item').find('div.bank-account').remove();
            $(data.othis).parents('.layui-form-item').append(showBankInfoDetail(data.value, 'otherBank'));
        });
    });


}

// 展开详细的开票信息
function showInvoiceInfoDetail(value, id) {
    if (isBlank(value)) {
        return "";
    }
    var json = {};
    var array = value.split('####');
    for (var i = 0; i < array.length; i++) {
        json[array[i].split(':')[0]] = array[i].split(':')[1];
    }
    return '<div class="layui-form-item invoice" id="' + id + '">' +
        '<div class="invoice-line" data-lable-name="">' +
        '<input name="basicsId" type="hidden" value="' + json.basicsId + '" />' +
        '<label class="layui-form-label"><span>公司名称：</span></label>' +
        '<div class="layui-input-block"><input name="companyName" class="layui-input" value="' + (json.companyName ? json.companyName : '') + '" readonly/></div>' +
        '<label class="layui-form-label"><span>税务号：</span></label>' +
        '<div class="layui-input-block"><input name="taxNumber" class="layui-input" value="' + (json.taxNumber ? json.taxNumber : '') +  '" readonly/></div>' +
        '<label class="layui-form-label"><span>公司地址：</span></label>' +
        '<div class="layui-input-block"><input name="companyAddress" class="layui-input" value="' + (json.companyAddress ? json.companyAddress : '') + '" readonly/></div>' +
        '<label class="layui-form-label"><span>联系电话：</span></label>' +
        '<div class="layui-input-block"><input name="phone" class="layui-input" value="' + (json.phone ? json.phone : '') + '" readonly/></div>' +
        '<label class="layui-form-label"><span>开户银行：</span></label>' +
        '<div class="layui-input-block"><input name="accountBank" class="layui-input" value="' + (json.accountBank ? json.accountBank : '') + '" readonly/></div>' +
        '<label class="layui-form-label"><span>银行账号：</span></label>' +
        '<div class="layui-input-block"><input name="bankAccount" class="layui-input" value="' + (json.bankAccount ? json.bankAccount : '') + '" readonly/></div>' +
        '</div>' +
        '</div>';
}

// 展开详细的银行信息
function showBankInfoDetail(value, id) {
    if (isBlank(value)) {
        return "";
    }
    var json = {};
    var array = value.split('####');
    for (var i = 0; i < array.length; i++) {
        json[array[i].split(':')[0]] = array[i].split(':')[1];
    }
    return '<div class="layui-form-item bank-account" id="' + id + '">' +
        '<div class="invoice-line" data-lable-name="">' +
        '<input name="basicsId" type="hidden" value="' + json.basicsId + '" />' +
        ('selfBank' != id ? '<label class="layui-form-label"><span>名称：</span></label><div class="layui-input-block"><input name="accountName" class="layui-input" value="' + (json.accountName ? json.accountName : '') + '" readonly/></div>' : '') +
        '<label class="layui-form-label"><span>开户银行：</span></label><div class="layui-input-block"><input name="accountBank" class="layui-input" value="' + (json.accountBank ? json.accountBank : '') + '" readonly/></div>' +
        '<label class="layui-form-label"><span>银行账号：</span></label><div class="layui-input-block"><input name="bankAccount" class="layui-input" value="' + (json.bankAccount  ? json.bankAccount : '') + '" readonly/></div>' +
        '</div>' +
        '</div>';
}

// 初始化流程的标签信息
function initFormItem(flow, dateRender2DateTime, dateRender2Day, dateRender2Month, dateRender2TimeSlot) { // 初始化每个填写项目
    // 初始化时间账单标签数
    timeAccountBillDate = [];
    var htmlStr = "";
    $.each(flow.flowLabels,function(i,item) {
        // 可编辑的才展示
        if(flow.editLabelIds.indexOf(item.id) >= 0) {
            var type = parseInt(item.type);
            if(type === 0) { // 输入字符
            	if (flow.flowClass === '[BillWriteOffFlow]' && (i == 1 || i == 2) && flow.editLabelIds.indexOf(item.id) >= 0) {
            		htmlStr = initTable(htmlStr, item, i);
            	} else {
            		htmlStr = initStrInput(htmlStr,  flow.mustLabelIds, item);
            	}
            } else if(type === 1) { // 输入整型
                htmlStr = initIntInput(htmlStr,  flow.mustLabelIds, item);
            } else if(type === 2) {
                htmlStr = initFloadInput(htmlStr,  flow.mustLabelIds, item);
            } else if(type === 3) { // 布尔
            	if (flow.flowClass === '[BillWriteOffFlow]' && flow.editLabelIds.indexOf(item.id) >= 0) {
            		htmlStr = initHidden(htmlStr, item);
            	} else {
            		htmlStr = initBooleanInput(htmlStr,  flow.mustLabelIds, item);
            	}
            } else if(type === 4) { // 日期（YYYY-MM-dd）
                htmlStr = initDateToDayInput(htmlStr,  flow.mustLabelIds, item, dateRender2Day)
            } else if(type === 5) { // 日期（YYYY-MM-dd HH:mm:ss）
                htmlStr = initDateToTimeInput(htmlStr,  flow.mustLabelIds, item, dateRender2DateTime);
            } else if(type === 6) { // 日期（YYYY-MM）
                htmlStr = initDateToMonthInput(htmlStr,  flow.mustLabelIds, item, dateRender2Month);
            } else if(type === 7 || type === 11 || type === 12) {
                htmlStr = initSelect(htmlStr,  flow.mustLabelIds, item);
            } else if (type === 8) {
                htmlStr = initFileInput(htmlStr, flow.mustLabelIds, item, flow.flowClass);
            }else if(type === 9) {
                htmlStr = initTextArea(htmlStr, flow.mustLabelIds, item);
            } else if(type === 10) { // 价格梯度
                htmlStr = initFirstGradient(htmlStr, flow.flowId);
            } else if(type === 13) { // 酬金
                htmlStr = initRemunerationInfo(htmlStr, flow.mustLabelIds, item);
            } else if(type === 14) { // 账单信息
                htmlStr = initSelectBill(htmlStr, flow.mustLabelIds, item, flow.flowClass, flow.flowId);
            } else if(type === 15) { // 账单金额
                htmlStr = initBillMoney(htmlStr, flow.mustLabelIds, item, flow.flowId);
            } else if(type === 17) { // 我司开票信息
                htmlStr = initInvoiceInfo(htmlStr, flow.mustLabelIds, item, 0);
            } else if(type === 18) { // 对方开票信息
                htmlStr = initInvoiceInfo(htmlStr, flow.mustLabelIds, item, 1);
            } else if(type === 19) { // 我司银行信息
                htmlStr = initInvoiceInfo(htmlStr, flow.mustLabelIds, item, 2);
            } else if(type === 20) { // 对方银行信息
                htmlStr = initInvoiceInfo(htmlStr, flow.mustLabelIds, item, 3);
            } else if(type === 21) { // 合同编号
                htmlStr = initContractInput(htmlStr,  flow.mustLabelIds, item);
            } else if(type === 22) { // 历史单价
                htmlStr = initStrInput(htmlStr,  flow.mustLabelIds, item);
            } else if(type === 23) { // 发票信息
                htmlStr = initSelectInvoice(htmlStr, flow.mustLabelIds, item, flow.flowClass, flow.flowId);
            } else if(type === 24) { // 提单信息
                htmlStr = initDsApplyOrder(htmlStr, item, flow.flowId);
            } else if(type === 25) { // 配单信息
                htmlStr = initDsMatchOrder(htmlStr, item, flow.flowId);
            } else if(type === 26) { // 订单编号
                htmlStr = initOrderNoInput(htmlStr,  flow.mustLabelIds, item);
            } else if(type === 27) { // 配单员选择
                htmlStr = matchPerson(htmlStr, flow.mustLabelIds, item);
            } else if(type === 28) { // 采购单编号
                htmlStr = initPurchaseNoInput(htmlStr,  flow.mustLabelIds, item);
            } else if(type === 29) { //客户发票抬头
                htmlStr = initCustInvoiceInfo(htmlStr, flow.mustLabelIds, item,flow.flowId);
            } else if(type === 30) { //账单开票信息
                //新增账单信息 修改原有的标签
                htmlStr = initBillInfo(htmlStr, flow.mustLabelIds, item, flow.flowClass, flow.flowId);
            } else if(type === 31) { // 电商银行信息
                htmlStr = initDsBankInfo(htmlStr, flow.flowId);
            } else if (type === 32){// 时间账单金额信息标签
                htmlStr = initTimeAccountBillLabel(htmlStr, flow.mustLabelIds, dateRender2Day, item);
            } else if (type === 33){ // 平台账户信息
                htmlStr = initPlatformAccountInfo(htmlStr, flow.mustLabelIds, item);
            } else if (type === 34){ // 获取
                htmlStr = initUncheckedBill(htmlStr, flow.mustLabelIds, item, flow.flowClass, flow.flowId);
            } else if (type === 35){ // 时间段
                htmlStr = initTimeSlot(htmlStr, flow.mustLabelIds, item, dateRender2TimeSlot);
            } else if (type === 36){ // 单选框
                htmlStr = initRadioType(htmlStr, flow.mustLabelIds, item, flow.flowId);
            } else if (type === 37){ // 账号充值
                htmlStr = initAccountRecharge(htmlStr, item, flow.flowClass);
            } else if (type === 38){ // 请假类型单选框
                htmlStr = initLeaveType(htmlStr, flow.mustLabelIds, item, flow.flowId);
            }else if (type === 39){ // 时间段---2020/11/10 上午
                htmlStr = initNewTimeSlot(htmlStr, flow.mustLabelIds, item, dateRender2Day);
            }
        }
    });
    return htmlStr;
}

function initHidden(htmlStr, item) {
	htmlStr = htmlStr ? htmlStr : '';
	htmlStr += '<div style="display:none;"><input type="radio" id="' + item.id + '" name="' + item.id +'" value="0" title="否" checked /></div>';
	return htmlStr;
}

function initTable(htmlStr, item, i) { // 账单表格 和 收款信息表格
	if (i === 1) {
		htmlStr = htmlStr ? htmlStr : '';
		htmlStr += '<div>' + item.name + '：</div>'
		htmlStr = createBillsTable(htmlStr, item);
	} else if (i === 2) {
		htmlStr = htmlStr ? htmlStr : '';
		htmlStr += '<div>' + item.name + '：</div>'
		htmlStr = createIncomesTable(htmlStr, item);
	}
	return htmlStr;
}
var incomeInput, billsInput;
function contactsInfo(table) {
	var incomeSum = parseFloat($('.bill_write_off_sum_pay').html());
	var incomeSel = table.checkStatus('writeOffIncomes').data;
	var incomesInfo = [];
	if (incomeSel && incomeSel.length > 0){
		$(incomeSel).each(function (index, income) {
			incomeSum = accSub(incomeSum, income.remainRelatedCost);
			if (-incomeSum >= income.remainRelatedCost) {
				return;
			}
			incomesInfo.push({
				fsexpenseincomeid: income.id,
				cost: income.cost,
				operatetime: income.operateTime,
				banckcustomername: income.depict,
				thiscost: incomeSum >= 0 ? income.remainRelatedCost : accAdd(income.remainRelatedCost, incomeSum)
			});
		});
	}
	incomeInput.val(incomesInfo.length > 0 ? JSON.stringify(incomesInfo) : '');
	var billsSel = table.checkStatus('writeOffBills').data;
	var billsInfo = [];
	if (billsSel && billsSel.length > 0){
		billsInfo.push({isHandApplay: "T"});
		$(billsSel).each(function (index, bills) {
			billsInfo.push({
				productbillsid: bills.id,
				title: bills.title,
				receivables: bills.receivables,
				thiscost: accSub(bills.receivables, bills.actualReceivables)
			});
		});
	}
	billsInput.val(billsInfo.length > 0 ? JSON.stringify(billsInfo) : '');
}

// 流水号 金额 账户  剩余
function createIncomesTable(htmlStr, item) {
	htmlStr = htmlStr ? htmlStr : '';
	htmlStr += '<div class="layui-small-table">' +
					'<table id="writeOffIncomes" lay-filter="write-off-bills"></table>' +
					'<i class="bill_write_off_income_sum_tip" style="font-size: 18px;color: red;">收款合计：' +
						'<span class="bill_write_off_income_sum_pay" style="font-size: 18px;color: red;">0.00</span>&nbsp;元' +
					'</i>' +
					'<input type="hidden" id="' + item.id + '" name="' + item.id + '"/>' +
					'<input type="hidden" class="form-submit-tips" />' +
				'</div>';
	setTimeout(function () {
		layui.use([ 'table', 'form' ], function() {
			var table = layui.table;
			var form = layui.form;
			table.render({
				url:  "/fsExpenseIncome/readFsExpenseIncomesByProduct.action?temp=" + Math.random(),
				elem : '#writeOffIncomes',
				even : true,
				page : false,
				method : 'POST',
				data : [],
				cols : [[ {
					field : 'checked',
					title : '选择',
					type : 'checkbox',
					width : 50
				},{
					field : 'id',
					title : 'id',
					align : 'center',
					hide : true
				}, {
					field : 'operateTime',
					title : '到款时间',
					align : 'center',
					width : 90
				}, {
					field : 'depict',
					title : '银行客户名称',
					templet: function (d) {
                       if (d.relateStatus == 1) {
                           return '<span style="color:#008000;">' + d.depict + '</span>'
                      }else{
                          return d.depict
                      }
                    },
					align : 'center',
					width : 150
				}, {
					field : 'cost',
					title : '收款金额',
					align : 'right',
					templet: function (rowdata) {
						return thousand(rowdata.cost);
					}
				}, {
					field : 'remainRelatedCost',
					title : '剩余金额',
					align : 'right',
					templet: function (rowdata) {
						return thousand(rowdata.remainRelatedCost);
					}
				} ]],
				parseData : function(res) {
					return {
						"code" : 0, 
						"count" : res.count,
						"data" : res.data
					};
				},
				where : {
					productId : productId
				},
				done: function () {
					var bindClick = function () {
						incomeInput = $('#' + item.id);
						var tips = $('.form-submit-tips');
						$('#writeOffIncomes').next().find('[data-field="checked"]').bind('click', function () {
							setTimeout(function () {
								var checkStatus = table.checkStatus('writeOffIncomes');
								var selectData = checkStatus.data;
								var sum = 0.00;
								if (selectData && selectData.length > 0){
									$(selectData).each(function (index, income) {
										sum = accAdd(sum, income.remainRelatedCost);
									});
								}
								$('.bill_write_off_income_sum_pay').html(sum.toFixed(2));
								if (parseFloat($('.bill_write_off_sum_pay').html()) > parseFloat($('.bill_write_off_income_sum_pay').html())) {
									tips.val('收款总计必须大于等于账单总计');
								} else {
									tips.val('');
								}
								contactsInfo(table);
							}, 10);
						});
					}
					bindClick();
				}
			});
		});
	}, 5);
	return htmlStr;
}

// 名称 应收 实收
function createBillsTable(htmlStr, item) {
	htmlStr = htmlStr ? htmlStr : '';
	htmlStr += '<div class="layui-small-table">' +
					'<table id="writeOffBills" lay-filter="write-off-bills"></table>' +
					'<input type="hidden" id="' + item.id + '" name="' + item.id + '"/>' +
					'<i class="bill_write_off_sum_tip" style="font-size: 18px;color: red;">账单合计：' + 
						'<span class="bill_write_off_sum_pay" style="font-size: 18px;color: red;">0.00</span>&nbsp;元' +
					'</i>' +
				'</div>';
	setTimeout(function () {
		layui.use([ 'table', 'form' ], function() {
			var table = layui.table;
			var form = layui.form;
			table.render({
				url:  "/customerOperate/readProductBills.action?temp=" + Math.random(),
				elem : '#writeOffBills',
				even : true,
				page : false,
				method : 'POST',
				data : [],
				cols : [[ {
					field : 'checked',
					title : '选择',
					type : 'checkbox',
					width : 50
				},{
					field : 'id',
					title : 'id',
					align : 'center',
					hide : true
				},{
					field : 'title',
					title : '账单名称',
					align : 'center',
					width : 280
				}, {
					field : 'receivables',
					title : '账单金额',
					align : 'right'
				} ]],
				parseData : function(res) {
					return {
						"code" : 0, 
						"count" : res.count,
						"data" : res.data
					};
				},
				where : {
					productId : productId,
					needOrder : 'T',
					flowClass : '[BillReceivablesFlow]'
				},
				done: function () {
					billsInput = $('#' + item.id);
					var bindClick = function () {
						var tips = $('.form-submit-tips');
						$('#writeOffBills').next().find('[data-field="checked"]').bind('click', function () {
							setTimeout(function () {
								var checkStatus = table.checkStatus('writeOffBills');
								var selectData = checkStatus.data;
								var sum = 0.00;
								if (selectData && selectData.length > 0){
									$(selectData).each(function (index, bills) {
										sum = accAdd(sum, accSub(bills.receivables, bills.actualReceivables));
									});
								}
								$('.bill_write_off_sum_pay').html(sum.toFixed(2));
								if (parseFloat($('.bill_write_off_sum_pay').html()) > parseFloat($('.bill_write_off_income_sum_pay').html())) {
									tips.val('收款总计必须大于等于账单总计');
								} else {
									tips.val('');
								}
								contactsInfo(table);
							}, 10);
						});
					}
					bindClick();
				}
			});
		});
	}, 5);
	return htmlStr;
}

function initContractInput(htmlStr, mustLabelIds, item) { // 输入字符
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' id='" + item.id + "' name='" + item.id;
    htmlStr += "' class='layui-input' placeholder='流程自动生成，不需要填写' disabled/>";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initOrderNoInput(htmlStr, mustLabelIds, item) { // 输入字符
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' id='" + item.id + "' name='" + item.id;
    htmlStr += "' class='layui-input' placeholder='流程自动生成，不需要填写' disabled/>";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initCustInvoiceInfo(htmlStr, mustLabelIds, item,id){
    htmlStr = htmlStr ? htmlStr : '';
    var must = false;
    if(mustLabelIds.indexOf(item.id) >= 0) {

        must = true;
    }
    htmlStr += "<div class='layui-form-item gradient-line nogradient" + id + "' style='margin-bottom: 5px;'>"; // 价格
    htmlStr += "<input type='hidden' id='custInvoiceCount' value='1'>"; // 梯度个数
    htmlStr += "<div class='layui-form-item'>"; // 价格
    htmlStr += "<label class='layui-form-label'>";
    if(must){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "客户开票信息：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    var data = {
        type: 1,
        supplierId: supplierId ? supplierId : customerId ? customerId : ''
    };
    var custInvoiceSize = 0;
    $.ajaxSettings.async = false;
    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/getInvoice.action",
        dataType: 'json',
        data: data,
        success: function(data) {
            if (data.code == 200) {
                htmlStr += "<select id='" + id +"CustInvoice0' lay-filter='invoice-" + type +"'>";
                htmlStr += "<option value=''>请选择</option>";
                $.each(data.data, function (index, item) {
                    htmlStr += "<option value='" + item.value + "' title='" + item.title + "'>" + item.text + "</option>";
                });
                custInvoiceSize = data.data.length
                htmlStr += "</select>";
            }
        }
    });
    $.ajaxSettings.async = true;
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>"; // 价格
    //已收金额
    htmlStr += "<div class='layui-form-item'>"; // 价格
    htmlStr += "<label class='layui-form-label'>";
    if(must){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "已收金额：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<input type='text' id='" + id + "Receivables0' name='" + id + "Receivables' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "</div>";
    htmlStr += "</div>";
    //开票金额
    htmlStr += "<div class='layui-form-item'>"; // 价格
    htmlStr += "<label class='layui-form-label'>";
    if(must){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "开票金额：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<input type='text' id='" + id + "InvoiceInfo0' name='" + id + "InvoiceInfo' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";

    htmlStr += "<div class='layui-inline operateCustVoice" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_gradient' onclick='addCustInvoiceClick(\"" + id + "\",\""+must+"\")'> <i class='layui-icon layui-icon-add-circle'></i></span>";
    htmlStr += "</label>";
    htmlStr += "</div>"; // operate

    htmlStr += "</div>";
    htmlStr += "</div>";
    htmlStr += '<i class="cust_invoice_info_sum_tip" style="font-size: 18px;color: red;">开票合计：<span class="cust_invoice_info_sum_pay" style="font-size: 18px;color: red;">0.00</span>&nbsp;元</i>'
    htmlStr += "</div>";
    return htmlStr;
}

//开票金额 输入框 change事件
function custInvoicePriceBindChange(id, count){

    setTimeout(function () {
        $('#'+id +'InvoiceInfo'+count).unbind().on('change',function () {
            var billInfoSum = 0.00;
            var needCheck = false;
            // 选择了账单，需要校验开票金额不能大于账单金额，无账单的开票，不需要校验
            if ($('.bill_info_sum_pay').length > 0) {
                var billInfoSumStr = $('.bill_info_sum_pay').text();
                billInfoSum = parseFloat(billInfoSumStr);
                needCheck = true;
            }
            var sum = 0.00;
            $.each($('input[name='+id+'InvoiceInfo]'),function (i,item) {
                var invoicePrice = $(item).val();
                if(isBlank(invoicePrice)) {
                    invoicePrice = 0;
                }
                if (needCheck && accAdd(sum, invoicePrice) > billInfoSum) {
                    layer.tips("<span>开票合计</span>不能大于<span>账单合计</span>", $(item), {tips:4});
                    $(item).val(0);
                } else {
                    sum = accAdd(sum, invoicePrice);
                }
            })
            $('.cust_invoice_info_sum_pay').text(sum.toFixed(2));
        });
    },10);

}

function invoicePriceUnBindChange(id){
    var sum = 0.00;
    $.each($('input[name='+id+'InvoiceInfo]'),function (i,item) {
        var invoicePrice = $(item).val();
        if(isBlank(invoicePrice)) {
            invoicePrice = 0;
        }
        sum = accAdd(sum, invoicePrice);
    })
    $('.cust_invoice_info_sum_pay').text(sum.toFixed(2));
}

//客户开票信息 添加一行 点击事件
function addCustInvoiceClick(id,must){
    // 当前梯度个数
    var count = parseInt($("#flow_" + id).find("#custInvoiceCount").val());

    //客户开票信息
    var prevCustInvoice = $("#" + id + "CustInvoice" + (count - 1)).val();
    if(isBlank(prevCustInvoice)) {
        layer.tips('请选择客户开票信息', $("#" + id + "CustInvoice" + (count - 1)).parent());
        return;
    }
    //已收金额
    var receivablesPrice = $("#" + id + "Receivables" + (count - 1)).val();
    if(isBlank(receivablesPrice) || parseInt(receivablesPrice) < 0) {
        layer.tips('请输入已收金额', $("#" + id + "Receivables" + (count - 1)),{tips:4});
        return;
    }
    //开票金额
    var prevInvoicePrice = $("#" + id + "InvoiceInfo" + (count - 1)).val();
    if(isBlank(prevInvoicePrice) || parseInt(prevInvoicePrice) < 0) {
        layer.tips('请输入开票金额', $("#" + id + "InvoiceInfo" + (count - 1)),{tips:4});
        return;
    }
    var operateDiv = $(".operateCustVoice" + id);

    var lastgradientDiv = operateDiv.parents(".gradient-line");
    operateDiv.remove();
    var custInvoiceTip = $(".cust_invoice_info_sum_tip");
    var custInvoiceValue =  custInvoiceTip.children().text();
    custInvoiceTip.remove();
    var htmlStr = "";
    htmlStr += "<div class='layui-form-item gradient-line nogradient" + id + "' style='margin-bottom: 5px;'>"; // 价格
    htmlStr += "<div class='layui-form-item'>"; // 价格
    htmlStr += "<label class='layui-form-label'>";
    if('true' == must){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "客户开票信息：</label>"
    htmlStr += "<div class='layui-input-block'>"; // block

    var data = {
        type: 1,
        supplierId: supplierId ? supplierId : customerId ? customerId : ''
    };

    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/getInvoice.action",
        dataType: 'json',
        data: data,
        success: function(data) {
            if (data.code == 200) {
                htmlStr += "<select id='" + id +"CustInvoice"+count+"' lay-filter='invoice-" + type +"'>";
                htmlStr += "<option value=''>请选择</option>";
                $.each(data.data, function (index, item) {
                    htmlStr += "<option value='" + item.value + "' title='" + item.title + "'>" + item.text + "</option>";
                });
                custInvoiceSize = data.data.length;
                htmlStr += "</select>";
            }
        }
    });
    $.ajaxSettings.async = true;
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>"; // 价格
    //已收金额
    htmlStr += "<div class='layui-form-item'>"; // 价格
    htmlStr += "<label class='layui-form-label'>";
    if('true' == must){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "已收金额：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<input type='text' id='" + id + "Receivables"+count+"' name='" + id + "Receivables' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "</div>";
    htmlStr += "</div>";

    //开票金额
    htmlStr += "<div class='layui-form-item'>"; // 价格
    htmlStr += "<label class='layui-form-label'>";
    if('true' == must){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "开票金额：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<input type='text' id='" + id + "InvoiceInfo"+count+"' name='" + id + "InvoiceInfo' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";

    htmlStr += "<div class='layui-inline operateCustVoice" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_gradient' onclick='addCustInvoiceClick(\"" + id + "\",\""+must+"\")'> <i class='layui-icon layui-icon-add-circle'></i></span>";
    htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_gradient' onclick=\"reduceCustInvoiceClick('" + id + "',"+must+")\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
    htmlStr += "</label>";
    htmlStr += "</div>"; // operate

    htmlStr += "</div>";
    htmlStr += "</div>";
    htmlStr += '<i class="cust_invoice_info_sum_tip" style="font-size: 18px;color: red;">开票合计：<span class="cust_invoice_info_sum_pay" style="font-size: 18px;color: red;">'+custInvoiceValue+'</span>&nbsp;元</i>'
    htmlStr += "</div>";

    lastgradientDiv.after(htmlStr);

    layui.use('form', function() {
        var form = layui.form;
        form.render();
    });
    custInvoicePriceBindChange(id,count);
    // 梯度个数加1
    count++;
    $("#flow_" + id).find("#custInvoiceCount").val(count);
    initValidate(id);
}

//减少客户开票信息按钮的点击事件
function reduceCustInvoiceClick(id,must) {
// 当前梯度个数
    var count = parseInt($("#flow_" + id).find("#custInvoiceCount").val());

    var operateDiv = $(".operateCustVoice" + id + "");
    var lastgradientDiv = operateDiv.parents(".gradient-line");
    var newOperateGradientDiv = lastgradientDiv.prev();
    lastgradientDiv.remove();
    var custInvoiceTip = $(".cust_invoice_info_sum_tip");
    var custInvoiceValue =  custInvoiceTip.text();

    custInvoiceTip.remove();

    // 梯度个数减1
    count--;

    var invoicePrice = $('#'+id +'InvoiceInfo'+count).val();
    if(isBlank(invoicePrice)){
        invoicePrice = 0;
    }

    if(isBlank(custInvoiceValue)){
        custInvoiceValue = 0;
    } else {
        custInvoiceValue = accSub(custInvoiceValue,invoicePrice);
    }

    var htmlStr = '';
    htmlStr += "<div class='layui-inline operateCustVoice" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_gradient' onclick='addCustInvoiceClick(\"" + id + "\",\""+must+"\")'> <i class='layui-icon layui-icon-add-circle'></i></span>";
    if(count > 1) {
        htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_gradient' onclick=\"reduceCustInvoiceClick('" + id + "',"+must+")\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
    }
    htmlStr += "</label>";
    htmlStr += "</div>";
    // 将操作按钮放在最后
    newOperateGradientDiv.find("div:last").append(htmlStr);
    newOperateGradientDiv.append('<i class="cust_invoice_info_sum_tip" style="font-size: 18px;color: red;">开票合计：<span class="cust_invoice_info_sum_pay" style="font-size: 18px;color: red;">'+custInvoiceValue+'</span>&nbsp;元</i>');
    invoicePriceUnBindChange(id)
    $("#flow_" + id).find("#custInvoiceCount").val(count);
}

function initPurchaseNoInput(htmlStr, mustLabelIds, item) { // 输入字符
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' id='" + item.id + "' name='" + item.id;
    htmlStr += "' class='layui-input' placeholder='流程自动生成，不需要填写' disabled/>";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

// 加载开票信息和银行信息下拉框
function initInvoiceInfo(htmlStr, mustLabelIds, item, type) {
    var currentHtml = htmlStr;
    var data = {
        type: type,
        supplierId: supplierId ? supplierId : customerId ? customerId : ''
    };
    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/getInvoice.action",
        dataType: 'json',
        data: data,
        success: function(data) {
            if (data.code == 200) {
                htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);

                htmlStr += "<select id='" + item.id +"' lay-filter='invoice-" + type +"'>";
                htmlStr += "<option value=''>请选择</option>";
                $.each(data.data, function (index, item) {
                    htmlStr += "<option value='" + item.value + "' title='" + item.title + "'>" + item.text + "</option>";
                });
                htmlStr += "</select>";
                htmlStr += "</div>"; // block-div
                htmlStr += "</div>"; // 表单元素一行结束 item-div

                currentHtml = htmlStr;
            }
        }
    });
    return currentHtml;
}

// 加载配单员信息下拉框
function matchPerson(htmlStr, mustLabelIds, item) {
    var currentHtml = htmlStr;
    var data = {};
    $.ajax({
        type: "POST",
        async: false,
        url: "/dsMatchOrder/getSelectRole.action",
        dataType: 'json',
        data: data,
        success: function(data) {
            if (data.code == 200) {
                htmlStr = initNomalLabel(htmlStr, mustLabelIds, item);

                htmlStr += "<select id='" + item.id +"' lay-filter='matchPerson'>";
                htmlStr += "<option value=''>请选择</option>";
                $.each(data.data, function (index, item) {
                    htmlStr += "<option value='" + item.ossUserId + "' title='" + item.realName + "'>" + item.realName + "</option>";
                });
                htmlStr += "</select>";
                htmlStr += "</div>"; // block-div
                htmlStr += "</div>"; // 表单元素一行结束 item-div

                currentHtml = htmlStr;
            }
        }
    });
    return currentHtml;
}

function initNomalLabel(htmlStr, mustLabelIds, item) {
    htmlStr += "<div class='layui-form-item'>"; // 表单元素一行开始
    htmlStr += "<label class='layui-form-label' for='" + item.id +"'>";

    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name + "：" + "</label>";
    htmlStr += "<div class='layui-input-block'>"; // block

    return htmlStr;
}
function initStrInput(htmlStr, mustLabelIds, item) { // 输入字符

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' id='" + item.id + "' name='" + item.id;
    htmlStr += "' class='layui-input";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += " notnull"
    }
    htmlStr += "' placeholder='请输入内容'";
    if(item.defaultValue && item.defaultValue != '') {
    	htmlStr += " value='" + item.defaultValue
    }
    htmlStr += "'/>";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initIntInput(htmlStr, mustLabelIds, item) { // 输入整型

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' id='" + item.id + "' name='" + item.name;
    htmlStr += "' class='layui-input isnum isint";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += " notnull"
    }
    htmlStr += "' placeholder='请填写整数'";
    if(item.defaultValue && item.defaultValue != '') {
    	if (/^\{\{(.+?)\}\}$/.test(item.defaultValue)) {
    		renderFormFuns.push(bindInputLinkage(item.defaultValue, item.id, item.name, 'id'));
    	} else {
    		htmlStr += " value='" + item.defaultValue
    	}
    }
    htmlStr += "' />";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div

    return htmlStr;
}

function initFloadInput(htmlStr, mustLabelIds, item) { // 输入小数

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' id='" + item.id + "' name='" + item.name;
    htmlStr += "' class='layui-input isnum isdecimal";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += " notnull"
    }
    htmlStr += "' placeholder='请填写数额'";
    if(item.defaultValue && item.defaultValue != '') {
    	if (/^\{\{(.+?)\}\}$/.test(item.defaultValue)) {
    		renderFormFuns.push(bindInputLinkage(item.defaultValue, item.id, item.name));
    	} else {
    		htmlStr += " value='" + item.defaultValue
    	}
    }
    htmlStr += "' />";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initBooleanInput(htmlStr, mustLabelIds, item) { // 布尔

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    if(item.defaultValue == '0'){
        htmlStr += "<input type='radio' name='" + item.id +"' value='0' title='否' checked='checked'>";
        htmlStr += "<input type='radio' name='" + item.id +"' value='1' title='是' >";
    } else {
        htmlStr += "<input type='radio' name='" + item.id +"' value='0' title='否'>";
        htmlStr += "<input type='radio' name='" + item.id +"' value='1' title='是'  checked='checked'>";
    }
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initDateToDayInput(htmlStr, mustLabelIds, item, dateRender2Day) { // 日期（YYYY-MM-dd）

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' class='layui-input layui-date-pointer";
    htmlStr += "'" ;
    if(item.defaultValue && item.defaultValue.length > 0) {
        htmlStr += " value='" + defaultValue + "'";
    }
    htmlStr += " name='formDate" + dateItemIndex + "' id='" +  item.id + "' placeholder='请选择日期' readonly />"
    dateRender2Day.push("formDate" + dateItemIndex++);
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initDateToTimeInput(htmlStr, mustLabelIds, item, dateRender2DateTime) {// 日期（YYYY-MM-dd HH:mm:ss）

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' class='layui-input layui-date-pointer";
    htmlStr += "'";
    if(item.defaultValue && item.defaultValue.length > 0) {
        htmlStr += "value='" + defaultValue + "'";
    }
    htmlStr += " name='formDate" + dateItemIndex + "' id='" +  item.id + "' placeholder='请选择日期及时间 ' readonly />"
    dateRender2DateTime.push("formDate" + dateItemIndex++);
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initDateToTimeInput(htmlStr, mustLabelIds, item, dateRender2DateTime) {// 日期（YYYY-MM-dd HH:mm:ss）

    htmlStr = initNomalLabel(htmlStr, mustLabelIds, item);

    htmlStr += "<input type='text' class='layui-input layui-date-pointer";
    htmlStr += "'";
    if(item.defaultValue && item.defaultValue.length > 0) {
        htmlStr += "value='" + defaultValue + "'";
    }
    htmlStr += " name='formDate" + dateItemIndex + "' id='" +  item.id + "' placeholder='请选择日期及时间 ' readonly />"
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    dateRender2DateTime.push("formDate" + dateItemIndex++);
    return htmlStr;
}
function initDateToMonthInput(htmlStr, mustLabelIds, item, dateRender2Month) {// 日期（YYYY-MM）

    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<input type='text' class='layui-input layui-date-pointer";
    htmlStr += "'";
    if(item.defaultValue && item.defaultValue.length > 0) {
        htmlStr += " value='" + defaultValue + "'";
    }
    htmlStr += " name='formDate" + dateItemIndex + "' id='" +  item.id + "' placeholder='请选择月份' readonly />"
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    dateRender2Month.push("formDate" + dateItemIndex++);
    return htmlStr;
}

function initSelect(htmlStr, mustLabelIds, item) { // 下拉框
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    htmlStr += "<select id='" + item.id +"' lay-filter='" + item.id +"'>";
    htmlStr+=  "<option value=''>请选择</option>";
    $.each(item.defaultValue.split(","), function (j,option) {
        if(item.type == 7) {
            htmlStr +=  "<option value='" + option + "'>" + option + "</option>"
        } else {
            var keys = option.split(":");
            htmlStr +=  "<option value='" + keys[0] + "'>" + keys[1] + "</option>"
        }
    });
    htmlStr += "</select>";
    if(item.type == 11) { // 价格类型
        initGradientEvent = item.id;
    }
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initFileInput(htmlStr, mustLabelIds, item, flowClass) {
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    // 文件类型
    htmlStr += "<button data-type='0' value-type='8' type='button' "  +
        "input-required='" + true + "' style='margin-right: 5px;width: 170px;' " +
        "class='layui-btn layui-btn-sm' name='" + item.id  + "'>选择文件</button>" + "<span>(总大小不超过100M)</span>" +
        (flowClass == '[InterAdjustPriceFlow]' && item.name == '报价单' ? "<a href='/common/example/country_price.xlsx'style='text-decoration: underline;' download='country_price.xlsx'>(模板下载)</a><span style='color: red'>请用此模板，不要直接用短信云的报价单</span>" : '') +
        "<div class='layui-upload-list'>" +
        "	<table class='layui-table'>" +
        "		<thead>" +
        "		<tr><th>批次</th><th>文件名</th>" +
        "		<th>状态</th>" +
        "		<th>操作</th>" +
        "		</tr></thead>" +
        "	 	<tbody data-file-name = '" + item.id + "'></tbody>" +
        "	</table>" +
        "</div> ";
    // 初始化加载文件
    initFile = item.id;
    htmlStr += "<input type='hidden'  id='" + item.id + "' />";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

function initTextArea(htmlStr, mustLabelIds, item) {
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    //文本域
    htmlStr += "<textarea placeholder='请输入内容' class='layui-textarea";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += " notnull"
    }
    htmlStr += "' id='" + item.id + "' maxlength = '1500' ></textarea>";
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

// 初始化调价梯度
function initFirstGradient(htmlStr, id) {
    // 统一价，默认显示
    htmlStr += "<div class='layui-form-item gradient-line nogradient" + id + "'>"; // 价格
    htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>价格：" + "</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<input type='text' id='" + id + "price' name='" + id + "price' class='layui-input notnull isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "</div>";
    htmlStr += "<label class='layui-form-label'><span>省网价格：</span></label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<input type='text' id='" + id + "pPrice' name='" + id + "pPrice' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "</div>";
    htmlStr += "</div>";

    // 第一个价格梯度，默认隐藏，切换到阶梯、阶段价时，隐藏统一价，显示价格梯度
    htmlStr += "<div class='layui-form-item gradient-line gradient" + id + "' style='display:none'>";

    htmlStr += "<input type='hidden' id='count' value='1'>"; // 梯度个数

    htmlStr += "<div class='layui-form-label'>";
    htmlStr += "<input type='radio' name='" + id + "defaultGradient' value='0' title='默认' checked='checked'/>";
    htmlStr += "</div>";
    htmlStr += "<div class='layui-input-block' style='height: 36px'>";
    htmlStr += "<input id='" + id + "gradientmin0' style='width: 100px; display: inline; text-align: center' class='layui-input' value='0' readonly='readonly'/>";
    htmlStr += "<span>&le;条&lt;</span>";
    htmlStr += "<input id='" + id + "gradientmax0' style='width: 100px; display: inline; text-align: center' class='layui-input isnum isint lastMax' placeholder='请填写'/>";
    htmlStr += "</div>";

    htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>价格：</label>";
    htmlStr += "<div class='layui-input-block'><input id='" + id + "price0' class='layui-input notnull isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "</div>";
    htmlStr += "<label class='layui-form-label'><span>百万投比：</span></label>";
    htmlStr += "<div class='layui-input-block'><input id='" + id + "millionRatio0' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/>";
    htmlStr += "</div>";

    htmlStr += "<label class='layui-form-label'><span>省占比：</span></label>";
    htmlStr += "<div class='layui-input-block'><input id='" + id + "provinceRatio0' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>%</span>";
    htmlStr += "<div class='layui-inline operate" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_gradient' onclick='addGradientClick(\"" + id +"\")'> <i class='layui-icon layui-icon-add-circle'></i></span>";
    htmlStr += "</label>";
    htmlStr += "</div>"; // operate
    htmlStr += "</div>"; // block

    htmlStr += "</div>"; // gradient line
    return htmlStr;
}

// 初始化电商银行信息
function initDsBankInfo(htmlStr, id) {
    
    // 第一个银行信息
    htmlStr += "<div class='layui-form-item gradient-line gradient" + id + "' style='display:none'>";

    htmlStr += "<input type='hidden' id='count' value='1'>"; // 梯度个数

    htmlStr += "<div class='layui-form-label'>";

    htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>名称：</label>";
    htmlStr += "<div class='layui-input-block'><input id='" + id + "price0' class='layui-input notnull isnum isdecimal gradient-detail' placeholder='请填写'/>";
    htmlStr += "</div>";
    htmlStr += "<label class='layui-form-label'><span>开户银行：</span></label>";
    htmlStr += "<div class='layui-input-block'><input id='" + id + "millionRatio0' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/>";
    htmlStr += "</div>";

    htmlStr += "<label class='layui-form-label'><span>银行账号：</span></label>";
    htmlStr += "<div class='layui-input-block'><input id='" + id + "provinceRatio0' class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/>";
    htmlStr += "<div class='layui-inline operate" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_bank_info' onclick='addBankInfoClick(\"" + id +"\")'> <i class='layui-icon layui-icon-add-circle'></i></span>";
    htmlStr += "</label>";
    htmlStr += "</div>"; // operate
    htmlStr += "</div>"; // block

    htmlStr += "</div>"; // gradient line
    return htmlStr;
}


// 初始化时间账单金额标签
function initTimeAccountBillLabel(htmlStr, mustLabelIds, dateRender2Day, item) {
    htmlStr += "<div class='layui-form-item time-bill-money-item' id='" + item.id + "' data-label-name='" + item.name + "'>";
    htmlStr += "<label class='layui-form-label' for='" + item.id + "'>";
    if (mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name + "：" + "</label>";
    htmlStr += getAccountBillDom(1);  // 表单元素一行结束 item-div
    timeAccountBillDate.push(item.id);
    return htmlStr;
}

// 获取账户计费信息
function getAccountBillDom(index) {
    index = parseInt(index);
    var baseDom = " <div class='layui-input-block time-account-bill' data-bill-index='" + index + "'>" +
        "     <input type='text' class='layui-input time-start' name='time_start' title='开始日期' placeholder='开始日期' readonly>" +
        "     -" +
        "     <input type='text' class='layui-input time-end' name='time_end' title='结束日期' placeholder='结束日期' readonly> <br>" +
        "     <input type='text' class='layui-input success' name='time_success' onblur='timeAccountBillChange(this,1," + index + ")' title='成功数'  placeholder='成功数' data-unit='条' />" +
        "     <span>X</span>" +
        "     <input type='text' class='layui-input price' name='time_price' onblur='timeAccountBillChange(this,2," + index + ")' title='单价' placeholder='单价' data-unit='元' />" +
        "     <span>=</span><br>" +
        "     <input type='text' class='layui-input total-money' name='time_total_money' onblur='timeAccountBillChange(this,3," + index + ")'  title='金额' placeholder='金额' data-unit='元'/>" +
        "     <span class='btn-opts'> " +
        "        <i class='layui-icon layui-icon-add-1' onclick='timeAccountBillBtn(this,1," + (index + 1) + ")'></i>";
    if (index > 1) {
        baseDom +=
            "<br>" +
            " <i class='layui-icon layui-icon-close' onclick='timeAccountBillBtn(this,2," + index + ")'></i>";
    }
    baseDom += "</span>" +
        "<span class='time-account-bill-index'>" + index + "</span>" +
        "  </div>";
    if (index === 1) {
        baseDom += "<div class='layui-input-block time-account-bill-total'>" +
            "     <table>" +
            "        <tr>" +
            "            <td rowspan='2' style=' width: 35px;'>合计</td>" +
            "            <td>成功数</td>" +
            "            <td>单价</td>" +
            "            <td>总金额</td>" +
            "        </tr>" +
            "        <tr>" +
            "            <td class='total-success'>0</td>" +
            "            <td class='average-price'>0.000000</td>" +
            "            <td class='total-money'>0.00</td>" +
            "        </tr>" +
            "     </table>" +
            "</div>";
    }
    baseDom += "</div>";
    return baseDom;
}

// 按钮
function timeAccountBillBtn(ele, type, index) {
    index = parseInt(index);
    if (type === 1) {
        if (index < 31) {
            var canAdd = false;
            if (index > 1) {
                var labelItem = $(ele).parent().parent().parent();
                canAdd = timeAccountBillValidate(labelItem);
            }
            // 添加
            if (canAdd) {
                var newDom = getAccountBillDom(index);
                $(ele).parent().parent().after(newDom);
                var newItem = $(ele).parent().parent().parent().find("div[data-bill-index='"+index+"']");
                renderAddItemDate(newItem);
                $(ele).parent().remove();
            }
        } else {
            layer.msg("计算阶段最多只能31个");
        }
    } else {
        // 删除
        if (index > 1) {
            // 删除当前的dom
            $("div[data-bill-index='" + index + "']").remove();
            // 增加前一个的添加和删除按钮
            var optsDom = "<span class='btn-opts'> " +
                "<i class='layui-icon layui-icon-add-1' onclick='timeAccountBillBtn(this,1," + index + ")'></i>";
            if (index > 2) {
                // 有删除按钮
                optsDom += "<br>" +
                    " <i class='layui-icon layui-icon-close' onclick='timeAccountBillBtn(this,2," + (index-1) + ")'></i>";

            }
            optsDom += "</span>";
            var beforeItem = $("div[data-bill-index='" + (index-1) + "']");
            beforeItem.append(optsDom);
            renderUpdateTotal(beforeItem.find("input[name='time_success']")[0]);
        }
    }
}

// 校验 时间账单 标签内容
function timeAccountBillValidate(labelItem) {
    var beforeItems = $(labelItem).find("div[data-bill-index]");
    if (beforeItems != null && beforeItems.length > 0) {
        for (var itemIndex = 0; itemIndex < beforeItems.length; itemIndex++) {
            var item = beforeItems[itemIndex];
            var timeStart = $(item).find("input[class*='time-start']");
            if (isBlank($(timeStart).val())) {
                layer.tips("开始时间不能为空", timeStart);
                return false;
            }
            var timeEnd = $(item).find("input[class*='time-end']");
            if (isBlank($(timeEnd).val())) {
                layer.tips("结束时间不能为空", timeEnd);
                return false;
            }
            var success = $(item).find("input[class*='success']");
            if (isBlank($(success).val()) || $(success).val() <= 0) {
                layer.tips("成功数不能为空,且必须大于0", success);
                return false;
            }
            var price = $(item).find("input[class*='price']");
            if (isBlank($(price).val()) || $(price).val() <= 0) {
                layer.tips("单价不能为空,且必须大于0", price);
                return false;
            }
            var totalMoney = $(item).find("input[class*='total-money']");
            if (isBlank($(totalMoney).val()) || $(totalMoney).val() <= 0) {
                layer.tips("总价不能为空,且必须大于0", totalMoney);
                return false;
            }
        }
    }
    return true;
}

// 渲染新加的时间
function renderAddItemDate(item) {
    layui.use(['laydate', 'layer', 'form'], function () {
        var form = layui.form;
        var laydate = layui.laydate;
        var itemStart = laydate.render({
            elem: $(item).find("input[name='time_start']")[0],
            type: 'date',
            format: 'yyyy-MM-dd', //日期格式
            zIndex: 99999999,
            done: function (value, date) {
                itemEnd.config.min = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                };
            }
        });
        var itemEnd = laydate.render({
            elem: $(item).find("input[name='time_end']")[0],
            type: 'date',
            format: 'yyyy-MM-dd', //日期格式
            zIndex: 99999999,
            done: function (value, date) {
                itemStart.config.max = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                };
            }
        });
    });
}

/**
 * 输入改变事件
 * @param ele 当前对象
 * @param type 类型 1：成功数，2：单价，3：总金额
 * @param index 行号
 */
function timeAccountBillChange(ele, type, index) {
    var thisValue = $(ele).val();
    if (isBlank(thisValue)) {
        updateTotal(ele);
        layer.tips("不能为空", ele);
        return;
    }
    if (!$.isNumeric(thisValue)) {
        updateTotal(ele);
        layer.tips("只能填写数字", ele);
        return ;
    }
    if (type === 1 && !(/^[0-9]*$/.test(thisValue)) ){
        $(ele).val("");
        updateTotal(ele);
        layer.tips("只能填写整数", ele);
        return ;
    }
    if (thisValue <= 0){
        $(ele).val("");
        updateTotal(ele);
        layer.tips("必须大于0", ele);
        return;
    }
    var item = $(ele).parent();
    var success = $(item).find("input[name='time_success']").val();
    var price = $(item).find("input[name='time_price']").val();
    if (isBlank(price)){
        price = 0;
    }
    if (isBlank(success)){
        success = 0;
    }
    if (type !== 3){
        $(item).find("input[name='time_total_money']").val((price * success).toFixed(6));
    }
    updateTotal(ele);
}

function updateTotal(ele) {
    console.log("=====")
    var success = 0;
    var totalMoney = 0.00;
    var inputTotalMoney = 0.00;
    // 更新到总计里面
    var label = $(ele).parent().parent();
    var items = $(label).find("div[data-bill-index]");
    if (items.length > 0) {
        for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
            var item = items[itemIndex];
            var itemSuccess = $(item).find("input[name='time_success']").val();
            if (isBlank(itemSuccess)) {
                itemSuccess = 0;
            }
            success += parseInt(itemSuccess);
            var itemPrice = $(item).find("input[name='time_price']").val();
            if (isBlank(itemPrice)) {
                itemPrice = 0.00;
            }
            totalMoney += (itemSuccess * parseFloat(itemPrice));
            var itemTotal = $(item).find("input[name='time_total_money']").val();
            if (isBlank(itemTotal)) {
                itemTotal = 0.00;
            }
            inputTotalMoney += parseFloat(itemTotal);
        }
    }
    var totalItem = $(label).find("div[class*='time-account-bill-total']");
    $(totalItem).find("td[class*='total-success']").html(success);
    var averageMoney = 0.000000;
    if (success > 0) {
        averageMoney = totalMoney / success;
    }
    $(totalItem).find("td[class*='average-price']").html(averageMoney.toFixed(6));
    $(totalItem).find("td[class*='total-money']").html(inputTotalMoney.toFixed(2));
}


/**
 * 初始化平台的账户信息
 * @param htmlStr
 * @param mustLabelIds
 * @param item
 */
function initPlatformAccountInfo(htmlStr, mustLabelIds, item) {
    htmlStr = htmlStr ? htmlStr : '';
    htmlStr += "<div class='layui-form-item' id='"+ item.id+"'><label class='layui-form-label' for='" + item.id + "'>";
    if (mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>";
    }
    htmlStr += item.name + "：</label>";
    htmlStr += getPlatformAccountInfoDom(1);
    htmlStr +="</div>";
    return htmlStr;
}

/**
 * 获取组装内容
 * @param index
 * @returns {string}
 */
function getPlatformAccountInfoDom(index) {
    index = parseInt(index);
    var dom = "<div class='layui-input-block platform-account-item' data-platform-account-index='" + index + "'>" +
        "    <input type='text' class='layui-input account' name='account' title='请填写账号' placeholder='请填写账号' >" +
        "    <input type='text' class='layui-input password ' name='password' title='请填写账号密码' placeholder='请填写账号密码'>" +
        "    <input type='text' class='layui-input note' name='note' title='请填写描述（100字以内）' placeholder='请填写描述（100字以内）'>" +
        getPlatfromAccountInfoOpts(index) +
        "    <span class='platform-index'>" + index + "</span>" +
        "</div>";
    return dom;
}

/**
 * 获取操作的dom
 * @param index
 * @returns {string}
 */
function getPlatfromAccountInfoOpts(index) {
    var optsDom =
        "<span class='platform-opts'>" +
        "   <i class='layui-icon layui-icon-add-1' onclick='addPlatformAccountInfo(this," + (index + 1) + ")'></i>";
    if (index > 1) {
        optsDom += "<br><i class='layui-icon layui-icon-close' onclick='delPlatformAccountInfo(this," + index + ")'></i>";
    }
    optsDom += "</span>";
    return optsDom;
}


/**
 * 添加项
 * @param ele
 * @param index
 */
function addPlatformAccountInfo(ele,index) {
    // 判断上一个已经填写完
    var items = $("div[data-platform-account-index]");
    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex];
        var account = $(item).find("input[name='account']").val();
        if (isBlank(account)) {
            layer.tips("账号不能为空", item);
            return;
        }
    }
    var newItem = getPlatformAccountInfoDom(index);
    var thisItem = $(ele).parent().parent();
    $(thisItem).after(newItem);
    // 删除本节点的操作按钮
    $(thisItem).find("span[class*='platform-opts']").remove();
}

/**
 * 删除项
 * @param ele
 * @param index
 */
function delPlatformAccountInfo(ele,index) {
    index = parseInt(index);
    var thisItem = $(ele).parent().parent();
    if (index >1){
        var optsDom = getPlatfromAccountInfoOpts(index-1);
        $(thisItem).prev().append(optsDom);
    }
    thisItem.remove();
}

/**
 * 账单信息展示标签  table类型
 * @param htmlStr
 * @param mustLabelIds
 * @param item
 * @param flowClass
 * @param flowId
 */
function initBillInfo(htmlStr, mustLabelIds, item, flowClass, flowId){
    htmlStr = htmlStr ? htmlStr : '';

    htmlStr += '<div>';
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name+ '：</div>';
    htmlStr = createBillInfoTable(htmlStr, item, flowClass, flowId);
    return htmlStr;
}

/**
 * 账单开票流程 账单信息标签，可以选择未开完票的账单
 * @param htmlStr
 * @param item
 * @param flowClass
 * @param flowId
 */
function createBillInfoTable(htmlStr, item, flowClass, flowId) {
    htmlStr = htmlStr ? htmlStr : '';
    htmlStr += '<div class="layui-small-table">' +
        '<table id="selectBillInfo" class="layui-hide" lay-filter="select-bill-info"></table>' +
        '<input type="hidden" id="' + item.id + '" name="' + item.id + '"/>' +
        '<i class="bill_info_sum_tip" style="font-size: 18px;color: red;">账单合计：' +
        '<span class="bill_info_sum_pay" style="font-size: 18px;color: red;">0.00</span>&nbsp;元' +
        '</i>' +
        '</div>';
    setTimeout(function () {
        layui.use([ 'table', 'form' ], function() {
            var table = layui.table;
            var form = layui.form;
            table.render({
                url:  "/customerOperate/readInvoiceableBills.action?temp=" + Math.random(),
                elem : '#selectBillInfo',
                even : true,
                page : false,
                method : 'POST',
                data : [],
                cols : [[ {
                    field : 'checked',
                    title : '选择',
                    type : 'checkbox',
                    width : 50
                },{
                    field : 'id',
                    title : 'id',
                    align : 'center',
                    hide : true
                },{
                    field : 'title',
                    title : '账单信息',
                    align : 'center',
                    width : 200,
                    templet : function (data) {
                        return '<span title="'+data.title+'">'+data.title+'</span>'
                    }
                }, {
                    field : 'receivables',
                    title : '应开金额(元)',
                    align : 'right',
                    width : 100,
                    templet: function (data) {
                        return thousand(data.receivables);
                    }
                }, {
                    field : 'actualInvoiceAmount',
                    title : '已开金额(元)',
                    align : 'right',
                    width : 100,
                    hide : true,
                    templet: function (data) {
                        return thousand(data.actualInvoiceAmount);
                    }
                },{
                    field : 'unopenedAmount',
                    title : '可开金额(元)',
                    align : 'right',
                    width: 100,
                    templet: function (data) {
                        if(isNotBlank(data.receivables ) && isNotBlank(data.actualInvoiceAmount)){
                            return thousand(data.receivables - data.actualInvoiceAmount - data.usedAmount);
                        }
                        return '';
                    }
                },{
                    title : '该账单本次开票(元)',
                    align : 'right',
                    minWidth : 120,
                    templet : function (data) {
                        return  "<span class='layui-icon layui-icon-edit billInvoiceAmountTip' title='修改'></span><input id = 'thisBillPayment_"+data.id+"' name ='thisBillPayment' data-param-name='" + data.id + "' class='layui-input billInvoiceAmount' value='' disabled data-unit='元'/>";
                    }
                } ]],
                parseData : function(res) {
                    return {
                        "code" : 0,
                        "count" : res.count,
                        "data" : res.data
                    };
                },
                where : {
                    customerId : customerId,
                    needOrder : 'T',
                    flowClass : flowClass,
                    flowId: flowId
                },
                done: function () {
                    var calcTotal = function () {
                        setTimeout(function () {
                            var checkStatus = table.checkStatus('selectBillInfo');
                            var selectData = checkStatus.data;
                            var sum = 0.00;
                            if (selectData && selectData.length > 0){
                                $(selectData).each(function (index, bills) {
                                    var thisBillPayment = $('#thisBillPayment_' + bills.id).val();
                                    sum = accAdd(sum, thisBillPayment);
                                });
                            }
                            $('.bill_info_sum_pay').html(sum.toFixed(2));

                        }, 10);
                    }
                    var bindClick = function () {
                        $('#selectBillInfo').next().find('[data-field="checked"]').bind('click', function () {
                            calcTotal()
                        });
                        $.each($('input[name=thisBillPayment]'),function (i,item) {
                            $(item).on("change",function(){
                                calcTotal();
                            });
                        })
                    }
                    bindClick();
                }
            });

            table.on('checkbox(select-bill-info)', function(obj){
                var thisBillPayment = $('#thisBillPayment_' + obj.data.id);
                var type = obj.type;
                if(obj.checked) {
                    if('one' == type) {
                        $(thisBillPayment).removeAttr('disabled');
                        $(thisBillPayment).attr('value', 0);
                    } else if('all' == type) {
                        var checkStatus = table.checkStatus('selectBillInfo');
                        var selectData = checkStatus.data;
                        $.each(selectData,function (i,item) {
                            $('#thisBillPayment_' + item.id).removeAttr('disabled');
                            $('#thisBillPayment_' + item.id).attr('value', 0);
                        })
                    }
                } else {
                    if('one' == type) {
                        $(thisBillPayment).attr('value', '');
                        $(thisBillPayment).attr('disabled', true);
                    } else if('all' == type) {
                        var thisBillPayments =  $('input[name=thisBillPayment]');
                        $.each(thisBillPayments,function(i,item){
                            $(item).attr('value', '');
                            $(item).attr('disabled', true);
                        })
                    }
                }
            });
        });
    }, 5);
    return htmlStr;
}

// 处理账单信息标签
function initSelectBill(htmlStr, mustLabelIds, item, flowClass, flowId) {
    htmlStr += "<div class='layui-form-item'>";
    htmlStr += "<label class='layui-form-label'>";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name +  "：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<button data-type='0' value-type='13' type='button' " +
        "input-required='" + true + "' style='margin-right: 5px;width: fit-content;' " +
        "class='layui-btn layui-btn-sm' name='" + item.id  + "' onClick=\"chooseBill('" + item.id + "', '" + flowClass + "', '" + flowId + "')\">选择账单</button>";
    htmlStr += "</div>";
    htmlStr += "</div>";

    htmlStr += "<div class='layui-form-item product_bill_item' id='" + item.id + "' style='display:none'>";
    htmlStr += "<div class='layui-input-block' style='line-height: 2'>"; // block

    htmlStr += "</div>";
    htmlStr += "</div>";

    return htmlStr;
}

// 处理酬金标签
function initRemunerationInfo(htmlStr, mustLabelIds, item) {
    var values = [0,0,0,0,0];
    htmlStr += "<div class='layui-form-item'>"; // 表单元素一行开始
    htmlStr += "<label class='layui-form-label' for='" + item.id +"'>";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name + "：" + "</label>";

    htmlStr += "<div class='layui-input-block remuneration'>";
    htmlStr += "<span>金额&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input isnum isdecimal' name='money' value=" + values[0] + " data-label-name='" + item.name + "' data-unit='元' >" +
        "<span>&nbsp;&nbsp;X&nbsp;&nbsp;</span>" +
        "<span>酬金比例&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input isnum isdecimal' name='rate' value=" + values[1] + " data-unit='%'>" +
        "<span>&nbsp;&nbsp;+&nbsp;&nbsp;奖励&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input isnum isdecimal' name='reward' value=" + values[2] + " data-unit='元'>" +
        "<span>&nbsp;&nbsp;－</span>" +
        "<br>" +
        "<span>扣款&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input isnum isdecimal' name='deduction' value=" + values[3] + " data-unit='元'>" +
        "<span>&nbsp;&nbsp;=&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input' readonly name='remuneration' value=" + values[4] + " data-unit='元'>";
    htmlStr += "</div>";

    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

// 处理账单金额标签
function initBillMoney(htmlStr, mustLabelIds, item) {
    var supplier_success = '';
    var supplier_price = '';
    var total_money = '';
    htmlStr += "<div class='layui-form-item bill-money-item " + item.id +"'>"; // 表单元素一行开始
    htmlStr += "<label class='layui-form-label' for='" + item.id +"'>";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name + "：" + "</label>";

    htmlStr += "<input type='text' class='layui-input bill-money isnum' name='supplier_success' placeholder='成功数' data-label-name = '" + item.name + "' value='" + supplier_success + "' data-unit='条' />" +
        "<span>X</span>" +
        "<input type='text' class='layui-input bill-money isnum' name='supplier_price' style='width: 60px' placeholder='单价' value='" + supplier_price + "' data-unit='元' />" +
        "<span>=</span>" +
        "<input type='text' class='layui-input bill-money' style='width: 120px' placeholder='金额' name='total_money' value='" + total_money + "' data-unit='元'/>";

    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

// 处理电商提单信息
function initDsApplyOrder(htmlStr, item, id) {
    // 商品需求，字段有：产品名称，规格型号，销售单价，数量，销售总额，备注
    htmlStr += "<div class='layui-form-item order-line'>";
    htmlStr += "<label class='layui-form-label' style='float: none'>" + item.name + "：" + "</label>";
    htmlStr += "<div>";
    htmlStr += "<table class='layui-table' lay-filter='order-table-" + id + "' id='order-table-" + id + "'>" +
        "  <thead>" +
        "    <tr>" +
        "      <th lay-data=\"{field:'id', hide: true}\"></th>" +
        "      <th lay-data=\"{field:'productname', edit: 'text'}\">产品名称</th>" +
        "      <th lay-data=\"{field:'format', edit: 'text'}\">规格型号</th>" +
        "      <th lay-data=\"{field:'price', edit: 'text', width: 100}\">销售单价</th>" +
        "      <th lay-data=\"{field:'amount', edit: 'text', width: 100}\">数量</th>" +
        "      <th lay-data=\"{field:'total', edit: 'text', width: 100}\">销售总额</th>" +
        "      <th lay-data=\"{field:'remark', edit: 'text'}\">备注</th>" +
        "      <th lay-data=\"{templet: '#applyOrderTools', width: 150}\">操作</th>" +
        "    </tr>" +
        "  </thead>" +
        "  <tbody>" +
        "       <tr></tr>" +
        "  </tbody>" +
        "</table>";

    // 工具栏
    htmlStr += "<script type=\"text/html\" id=\"applyOrderTools\">" +
        "           <a class=\"layui-btn  layui-btn-xs\" lay-event=\"add\">添加</a>" +
        "           {{# if(d.id != null && d.LAY_TABLE_INDEX != null && d.LAY_TABLE_INDEX > 0){ }}" +
        "           <a class=\"layui-btn layui-btn-danger layui-btn-xs\" lay-event=\"delete\">删除</a>\n" +
        "           {{# } }}" +
        "       </script>";
    htmlStr += "</div> ";

    htmlStr += "<input type='hidden' id='orderCount' value='1'>"; // 需求个数

    htmlStr += "</div>"; // order-linee
    return htmlStr;
}

//处理电商配单信息
function initDsMatchOrder(htmlStr, item, id) {
    // 商品需求，字段有：产品名称，规格型号，销售单价，数量，销售总额，备注
    htmlStr += "<div class='layui-form-item match-order-line'>";
    htmlStr += "<label class='layui-form-label' style='float: none'>" + item.name + "：" + "</label>";
    htmlStr += "<div>";
    htmlStr += "<table class='layui-table' lay-filter='match-order-table-" + id + "' id='match-order-table-" + id + "'>" +
        "  <thead>" +
        "    <tr>" +
        "      <th lay-data=\"{field:'id', 'hide': true}\"></th>" +
        "      <th lay-data=\"{field:'dsproductid', hide: true}\"></th>" +
        "      <th lay-data=\"{field:'productname', edit: 'text'}\">产品名称</th>" +
        "      <th lay-data=\"{field:'format', edit: 'text'}\">规格型号</th>" +
        "      <th lay-data=\"{field:'price', edit: 'text', width: 100}\">销售单价</th>" +
        "      <th lay-data=\"{field:'amount', edit: 'text', width: 100}\">数量</th>" +
        "      <th lay-data=\"{field:'total', edit: 'text', width: 100}\">销售总额</th>" +
        "      <th lay-data=\"{field:'suppliername', edit: 'text'}\">供应商</th>" +
        "      <th lay-data=\"{field:'supplierid', hide: true}\"></th>" +
        "      <th lay-data=\"{field:'remart', edit: 'text'}\">备注</th>" +
        "      <th lay-data=\"{templet: '#matchOrderTools', width: 100}\">操作</th>" +
        "    </tr>" +
        "  </thead>" +
        "  <tbody>" +
        "  </tbody>" +
        "</table>";

    // 工具栏
    htmlStr += "<script type=\"text/html\" id=\"matchOrderTools\">" +
        "           <a class=\"layui-btn layui-btn-danger layui-btn-xs\" lay-event=\"delete\">删除</a>\n" +
        "       </script>";
    htmlStr += "</div> ";
    htmlStr += "</div>"; // match-order-line
    return htmlStr;
}

// 处理账单金额标签的单位
function takeBillMoneyUnit(id) {
    var flowEle = $("#flow_" + id);
    var inputs = flowEle.find(".bill-money-item").find("input[class*='bill-money']");
    if (isNotBlank(inputs) && inputs.size() > 0) {
        for (var bill_input_index = 0; bill_input_index < inputs.size(); bill_input_index++) {
            var input_temp = inputs[bill_input_index];
            var unit = $(input_temp).attr("data-unit");
            if (isNotBlank(unit)) {
                if($(input_temp).next().get(0) == undefined  || $(input_temp).next().get(0).className != 'bill-money-unit') {
                    $(input_temp).after("<i class='bill-money-unit'>" + unit + "&nbsp;&nbsp;</i>")
                }
            }
        }
    }
}

// 处理账单金额标签内容改变
function takeBillMoneyChange(id) {
    var flowEle = $("#flow_" + id);
    var items = flowEle.find(".bill-money-item");

    $.each(items, function(i,item) {
        var supplier_success_ele = $(item).find("input[name='supplier_success']");
        var supplier_price_ele = $(item).find("input[name='supplier_price']");
        // 总数改变 || 单价改变
        $(supplier_success_ele, supplier_price_ele).change(function (e) {
            takeBillMoneyValue(parent); // 计算金额
        });

        // 成功数框获取焦点时的判断
        $(supplier_success_ele).focus(function (e) {
            var content = $(this).val();
            if (isBlank(content) || parseFloat(content) == 0) {
                $(this).val("");
            }
        });

        // 单价框获取焦点时的判断
        $(supplier_price_ele).focus(function (e) {
            var content = $(this).val();
            if (isBlank(content) || parseFloat(content) == 0) {
                $(this).val("");
            }
        });

        // 失去焦点时的判断
        $(supplier_success_ele).blur(function (e) {
            var content = $(this).val();
            if (isBlank(content) || !$.isNumeric(content) || parseInt(content) < 0) {
                $(this).val('');
                layer.tips("输入错误，输入内容必须为大于0的数字", this);
            }
            takeBillMoneyValue(item); // 计算金额
        });

        // 失去焦点时的判断
        $(supplier_price_ele).blur(function (e) {
            var content = $(this).val();
            if (isBlank(content) || !$.isNumeric(content) || parseInt(content) < 0) {
                $(this).val('');
                layer.tips("输入错误，输入内容必须为大于0的数字", this);
            }
            takeBillMoneyValue(item); // 计算金额
        });
    });
}

function initGradient(id) { // 初始化梯度
    layui.use('form', function() {
        var form = layui.form;
        if(initGradientEvent && initGradientEvent.length > 0) {
            form.on("select(" +initGradientEvent +")", function(data){
                if(data.value != '' && data.value != '1') { // 不是统一价
                    $(".gradient" + id).show();
                    $(".nogradient" + id).hide();
                } else {
                    $(".gradient" + id).hide();
                    $(".nogradient" + id).show();
                }
            });
            initGradientEvent= "";
            form.render();
        }
    });
}

// 计算账单金额标签的金额
function takeBillMoneyValue(parent) {
    var supplier_success_ele = $(parent).find("input[name='supplier_success']");
    var supplier_price_ele = $(parent).find("input[name='supplier_price']");
    var total_money_ele = $(parent).find("input[name='total_money']");
    // 总数
    var count = $(supplier_success_ele).val();
    count = isBlank(count) ? 0 : parseInt(count);
    // 单价
    var price = $(supplier_price_ele).val();
    price = isBlank(price) ? 0 : parseFloat(price);
    // 计算金额
    $(total_money_ele).val((count * price).toFixed(2));
}

// 初始化文件上传
function initFileUpload(input_id, flow_id) {
    layui.use('upload', function () {
        var upload = layui.upload;
        //多文件列表示例
        var demoListView = $("#flow_" + flow_id + " tbody[data-file-name='" + input_id + "']")
            , uploadListIns = upload.render({
            elem: "button[name='" + input_id + "']"
            , url: '/operate/upLoadFile'
            , field: 'files'
            , accept: 'file'
            , multiple: true
            , auto: false
            , choose: function (obj) {
                var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                //读取本地文件
                obj.preview(function (index, file, result) {
                    var tr = $(['<tr id="upload-' + index + '">'
                        , '<td style="text-align: center">1</td>'
                        , '<td>' + file.name + '</td>'
                        , '<td>等待上传</td>'
                        , '<td>'
                        , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                        , '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                        , '</td>'
                        , '</tr>'].join(''));
                    demoListView.append(tr);

                    //上传
                    obj.upload(index, file);

                    //删除
                    tr.find('.demo-delete').on('click', function () {
                        //删除对应的文件
                        delete files[index];
                        var file_info = upload_file[index];
                        // 删除已经记录上的数据
                        deleteUploadFile(input_id, file_info);
                        tr.remove();
                        uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                    });
                });
            }
            , done: function (res, index, upload) {
                if (res.code === 200 || res.code === '200') { //上传成功
                    var tr = demoListView.find('tr#upload-' + index)
                        , tds = tr.children();
                    tds.eq(2).html('<span style="color: #5FB878;">' + res.data[0].time + '</span>');
                    tds.eq(3).find("button[class*='demo-reload']").remove(); //清空操作
                    upload_file[index] = res.data;
                    takeUploadFileResult(res.data, input_id);
                    $("#flow_" + flow_id).find("#" + input_id).val(JSON.stringify(file_result[input_id]));
                    return delete this.files[index]; //删除文件队列已经上传成功的文件
                }
                this.error(index, upload);
            }
            , error: function (index, upload) {
                var tr = demoListView.find('tr#upload-' + index)
                    , tds = tr.children();
                tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
            }
        });
    });
}

// 初始化日期控件
function renderDate(dateRender2DateTime, dateRender2Day, dateRender2Month, dateRender2TimeSlot, dates) {
    var dataIndex = dates.length;
    layui.use(['laydate','layer', 'form'], function() {
        var form = layui.form;
        var laydate = layui.laydate;

        $.each(dateRender2DateTime, function(i,item) {
            if(item.split(",").length > 1) {

            } else {
                dates[dataIndex++] = laydate.render({
                    elem: "input[name='"+ item + "']",
                    type: 'datetime',
                    format: 'yyyy-MM-dd HH:mm:ss', //日期格式
                    zIndex: 99999999,
                    trigger: 'click',
                    done: function (value, date) {
                    }
                });
            }

        });

        $.each(dateRender2Day, function(i,item) {
            if(item.split(",").length > 1) {

            } else {
                dates[dataIndex++] = laydate.render({
                    elem: "input[name='"+ item + "']",
                    type: 'date',
                    format: 'yyyy-MM-dd', //日期格式
                    zIndex: 99999999,
                    trigger: 'click',
                    done: function (value, date) {
                    }
                });
            }

        });

        $.each(dateRender2Month, function(i,item) {
            if(item.split(",").length > 1) {

            } else {
                dates[dataIndex++] = laydate.render({
                    elem: "input[name='"+ item + "']",
                    type: 'month',
                    format: 'yyyy-MM', //日期格式
                    zIndex: 99999999,
                    trigger: 'click',
                    done: function (value, date) {
                    }
                });
            }

        });
        
        $.each(dateRender2TimeSlot, function(i,item) {
            if(item.split(",").length > 1) {

            } else {
                //判断是否需要range

                dates[dataIndex++] = laydate.render({
                    elem: "input[name='"+ item + "']",
                    type: 'datetime',
                    range: true,
                    zIndex: 99999999,
                    trigger: 'click',
                    done: function (value, date, endDate) {
                        if (endDate.hours === 0 && endDate.minutes === 0 && endDate.seconds === 0) {
                            // 点击时间选择
                            $(".layui-laydate-footer [lay-type='datetime'].laydate-btns-time").click();
                            // 结束时间选择23:59:59
                            $(".laydate-main-list-1 .layui-laydate-content li ol li:last-child").click();
                            $(".layui-laydate-footer [lay-type='date'].laydate-btns-time").click();
                            endDate.hours = 23;
                            endDate.minutes = 59;
                            endDate.seconds = 59;
                        }
                        timeSlotChange($(this)[0].elem, date, endDate);
                    }
                });
            }

        });

        if (timeAccountBillDate.length > 0) {
            for (var itemId in timeAccountBillDate) {
                var itemEle = $("#" + timeAccountBillDate[itemId]);
                var startInput = itemEle.find("input[name='time_start']")[0];
                var endInput = itemEle.find("input[name='time_end']")[0];
                var start = laydate.render({
                    elem: startInput,
                    type: 'date',
                    format: 'yyyy-MM-dd', //日期格式
                    zIndex: 99999999,
                    trigger: 'click',
                    done: function (value, date) {
                        end.config.min = {
                            year: date.year,
                            month: date.month - 1,
                            date: date.date
                        };
                    }
                });
                var end = laydate.render({
                    elem: endInput,
                    type: 'date',
                    format: 'yyyy-MM-dd', //日期格式
                    zIndex: 99999999,
                    trigger: 'click',
                    done: function (value, date) {
                        start.config.max = {
                            year: date.year,
                            month: date.month - 1,
                            date: date.date
                        };
                    }
                });
            }
        }
        form.render();
    });
}

// 增加梯度按钮的点击事件
function addGradientClick(id) {
    // 当前梯度个数
    var count = parseInt($("#flow_" + id).find("#count").val());

    var nextMin = $("#" + id + "gradientmax" + (count - 1)).val();
    var thisPrice = $("#" + id + "price" + (count - 1)).val();
    if(nextMin > 0 && thisPrice > 0) {
        if(count > 1) {
            var thisMax = nextMin;
            var thisMin = $("#" + id + "gradientmin" + (count - 1)).val();
            var prePice = $("#" + id + "price" + (count - 2)).val();

            if(parseInt(thisMax) <= parseInt(thisMin)) {
                layer.tips('不能小于这一梯度最小值', $("#" + id + "gradientmax" + (count - 1)));
                return;
            }
            if(parseFloat(thisPrice) >= parseFloat(prePice)) {
                layer.tips('该价格不能大于上一梯度的价格', $("#" + id + "price" + (count - 1)));
                return;
            }
        }
        var operateDiv = $(".operate" + id);
        var lastgradientDiv = operateDiv.parents(".gradient-line");

        operateDiv.remove();
        var htmlStr = "";
        htmlStr += "<div class='layui-form-item gradient-line gradient" + id + "'>"; // 表单元素一行开始

        htmlStr += "<div class='layui-form-label'>";
        htmlStr += "<input type='radio' name='" + id + "defaultGradient' value='"+ count +"' title='默认' />";
        htmlStr += "</div>";
        // 上一个梯度部分元素设为不可修改
        $("#" + id + "gradientmax" + (count - 1)).attr("readonly","readonly");
        $("#" + id + "gradientmax" + (count - 1)).removeClass("lastMax");
        $("#" + id + "price" + (count - 1)).attr("readonly","readonly");

        htmlStr += "<div class='layui-input-block' style='height: 36px'>";
        htmlStr += "<input id='" + id + "gradientmin"+ count +"' style='width: 100px; display: inline; text-align: center' class='layui-input isnum isdecimal' value='" + nextMin +"' readonly='readonly'/>";
        htmlStr += "<span>&le;条&lt;</span>";
        htmlStr += "<input id='" + id + "gradientmax"+ count +"' style='width: 100px; display: inline; text-align: center' class='layui-input isnum isdecimal lastMax' placeholder='请填写'/>";
        htmlStr += "</div>";

        htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>价格：</label>";
        htmlStr += "<div class='layui-input-block'><input id='" + id + "price"+ count +"' class='layui-input gradient-detail notnull isnum isdecimal' placeholder='请填写'/><span class='gradient-unit'>元</span>";
        htmlStr += "</div>";
        htmlStr += "<label class='layui-form-label'><span>百万投比：</span></label>";
        htmlStr += "<div class='layui-input-block'><input id='" + id + "millionRatio"+ count +"' class='layui-input gradient-detail isnum isdecimal' placeholder='请填写'/>";
        htmlStr += "</div>";

        htmlStr += "<label class='layui-form-label'><span>省占比：</span></label>";
        htmlStr += "<div class='layui-input-block'><input id='" + id + "provinceRatio"+ count +"' class='layui-input gradient-detail isnum isdecimal' placeholder='请填写'/><span class='gradient-unit'>%</span>";
        htmlStr += "<div class='layui-inline operate" + id + "'>";
        htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left;  padding-left: 15px'>";
        htmlStr += "<span class='gradient_btn_add' id='add_gradient' onclick=\"addGradientClick('" + id + "')\"> <i class='layui-icon layui-icon-add-circle'></i></span>";
        htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_gradient' onclick=\"reduceGradientClick('" + id + "')\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
        htmlStr += "</label>";
        htmlStr += "</div>"; // operate
        htmlStr += "</div>"; // block

        htmlStr += "</div>"; // gradient line

        lastgradientDiv.after(htmlStr);

        layui.use('form', function() {
            var form = layui.form;
            form.render();
        });

        // 梯度个数加1
        count++;
        $("#flow_" + id).find("#count").val(count);
        initValidate(id);
    } else {
        if(!nextMin || nextMin <= 0) {
            layer.tips('不能为空或等于0', $("#" + id + "gradientmax" + (count - 1)));
            return;
        }
        if(!thisPrice || thisPrice <= 0) {
            layer.tips('不能为空或等于0', $("#" + id + "price" + (count - 1)));
            return;
        }
    }
}

// 减少梯度按钮的点击事件
function reduceGradientClick(id) {
    // 当前梯度个数
    var count = parseInt($("#flow_" + id).find("#count").val());

    var operateDiv = $(".operate" + id + "");
    var lastgradientDiv = operateDiv.parents(".gradient-line");
    var newOperateGradientDiv = lastgradientDiv.prev();
    lastgradientDiv.remove();
    // 梯度个数减1
    count--;
    $("#" + id + "gradientmax" + (count - 1)).removeAttr("readonly");
    $("#" + id + "gradientmax" + (count - 1)).addClass("lastMax");
    $("#" + id + "price" + (count - 1)).removeAttr("readonly");

    var htmlStr = "";
    htmlStr += "<div class='layui-inline operate" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_gradient' onclick=\"addGradientClick('" + id + "')\"> <i class='layui-icon layui-icon-add-circle'></i></span>";

    if(count > 1) {
        htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_gradient' onclick=\"reduceGradientClick('" + id + "')\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
    }
    htmlStr += "</label>";
    htmlStr += "</div>";

    // 将操作按钮放在最后
    newOperateGradientDiv.find("div:last").append(htmlStr);
    $("#flow_" + id).find("#count").val(count);

}

// 增加电商按钮的点击事件
function addBankInfoClick(id) {
    // 当前梯度个数
    var count = parseInt($("#flow_" + id).find("#count").val());

    var nextMin = $("#" + id + "gradientmax" + (count - 1)).val();
    var thisPrice = $("#" + id + "price" + (count - 1)).val();
        var operateDiv = $(".operate" + id);
        var lastgradientDiv = operateDiv.parents(".gradient-line");

        operateDiv.remove();
        var htmlStr = "";
        htmlStr += "<div class='layui-form-item gradient-line gradient" + id + "'>"; // 表单元素一行开始

        // 上一个梯度部分元素设为不可修改
        $("#" + id + "gradientmax" + (count - 1)).attr("readonly","readonly");
        $("#" + id + "gradientmax" + (count - 1)).removeClass("lastMax");
        $("#" + id + "price" + (count - 1)).attr("readonly","readonly");

        htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>名称：</label>";
        htmlStr += "<div class='layui-input-block'><input id='" + id + "price"+ count +"' class='layui-input gradient-detail notnull isnum isdecimal' placeholder='请填写'/>";
        htmlStr += "</div>";
        htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>开户银行：</label>";
        htmlStr += "<div class='layui-input-block'><input id='" + id + "millionRatio"+ count +"' class='layui-input gradient-detail isnum isdecimal' placeholder='请填写'/>";
        htmlStr += "</div>";

        htmlStr += "<label class='layui-form-label'><span style='color: red;'>*</span>银行账号：</label>";
        htmlStr += "<div class='layui-input-block'><input id='" + id + "provinceRatio"+ count +"' class='layui-input gradient-detail isnum isdecimal' placeholder='请填写'/>";
        htmlStr += "<div class='layui-inline operate" + id + "'>";
        htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left;  padding-left: 15px'>";
        htmlStr += "<span class='gradient_btn_add' id='add_bank_info' onclick=\"addBankInfoClick('" + id + "')\"> <i class='layui-icon layui-icon-add-circle'></i></span>";
        htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_bank_info' onclick=\"reduceBankInfoClick('" + id + "')\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
        htmlStr += "</label>";
        htmlStr += "</div>"; // operate
        htmlStr += "</div>"; // block

        htmlStr += "</div>"; // gradient line

        lastgradientDiv.after(htmlStr);

        layui.use('form', function() {
            var form = layui.form;
            form.render();
        });

        // 梯度个数加1
        count++;
        $("#flow_" + id).find("#count").val(count);
        initValidate(id);
   
}

// 减少电商按钮的点击事件
function reduceBankInfoClick(id) {
    // 当前梯度个数
    var count = parseInt($("#flow_" + id).find("#count").val());

    var operateDiv = $(".operate" + id + "");
    var lastgradientDiv = operateDiv.parents(".gradient-line");
    var newOperateGradientDiv = lastgradientDiv.prev();
    lastgradientDiv.remove();
    // 梯度个数减1
    count--;
    $("#" + id + "gradientmax" + (count - 1)).removeAttr("readonly");
    $("#" + id + "gradientmax" + (count - 1)).addClass("lastMax");
    $("#" + id + "price" + (count - 1)).removeAttr("readonly");

    var htmlStr = "";
    htmlStr += "<div class='layui-inline operate" + id + "'>";
    htmlStr += "<label class='layui-form-label' style='width: fit-content; text-align: left; padding-left: 15px'>";
    htmlStr += "<span class='gradient_btn_add' id='add_bank_info' onclick=\"addBankInfoClick('" + id + "')\"> <i class='layui-icon layui-icon-add-circle'></i></span>";

    if(count > 1) {
        htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_bank_info' onclick=\"reduceBankInfoClick('" + id + "')\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
    }
    htmlStr += "</label>";
    htmlStr += "</div>";

    // 将操作按钮放在最后
    newOperateGradientDiv.find("div:last").append(htmlStr);
    $("#flow_" + id).find("#count").val(count);

}

// 上传文件成功后的处理
function takeUploadFileResult(filesPaths, input_id) {
    if (filesPaths && filesPaths.length > 0) {
        // 获取文件内容
        var files = file_result[input_id];
        if (!files || files.length == 0) {
            files = new Array();
        }
        // 新加文件
        for (var file_index = 0; file_index < filesPaths.length; file_index++) {
            var file = filesPaths[file_index];
            file.batchNum = 1;
            files.push(file);
        }
        file_result[input_id] = files;
    }
}

// 删除已经上传文件信息
function deleteUploadFile(label_name, file_info) {
    // 已经上传的数据
    var upload_file_array = file_result[label_name];
    var posi = $.inArray(file_info,upload_file_array);
    // 删除文件
    upload_file_array.splice(posi,1);
    // 初始化对应的数据
    file_result[label_name] = upload_file_array;
    $("#" + label_name).val(JSON.stringify(file_result[label_name]));
}

// 选择账单按钮的点击事件
function chooseBill(divId, flowClass, flowId) {
    var url = '';
    if (isNotBlank(supplierId)) {
        url = '/operate/toQueryAccount?productId='+productId + "&flowClass=" + encodeURI(flowClass);
    } else if (isNotBlank(customerId)) {
        url = '/customerOperate/toQueryAccount?productId=' + productId + "&flowClass=" + encodeURI(flowClass);
    }
    layui.use(['jquery', 'layer'], function(){
        var layer = layui.layer;
        layer.open({
            type: 2,
            area: ['535px', '355px'], //宽高
            content: url,
            btn: [ '选择', '取消' ],
            yes: function(index, layero) {
                var checkStatus = window["layui-layer-iframe" + index].layui.table.checkStatus('accoutlist');
                if(checkStatus.data.length > 0) {
                    var operateDiv = $("#" + divId);
                    operateDiv.show();
                    operateDiv.html(addBill(checkStatus.data, operateDiv, flowClass, flowId));
                    initValidate(flowId);
                    layer.close(index);
                } else {
                    layer.msg('请至少选择一个账单！');
                }
            }
        });
    });
}

// 显示选择的账单的信息
function addBill(data, operateDiv, flowClass, flowId) {
    var htmlStr = "";

    // 本次收付款
    var this_time_pay = 0;
    // 展示名称
    var labelName = billLabelName[flowClass];
    // 输入框名称
    var inputName = billInputName[flowClass];

    for (var bill_index = 0; bill_index < data.length; bill_index++) {
        var productBill = data[bill_index];

        // 应该 支付|收款|开票
        var payables_name = inputName.payables_name;
        var payables = isBlank(productBill[payables_name]) ? 0 : parseFloat(productBill[payables_name]);

        // 实际(已经) 支付|收款|开票
        var actualpayables_name = inputName.actualpayables_name;
        var actualpayables = isBlank(productBill[actualpayables_name]) ? 0 : parseFloat(productBill[actualpayables_name]);

        // 本次 支付|收款|开票
        var thisPayment_name = inputName.thisPayment_name;
        var thisPayment = isBlank(productBill[thisPayment_name]) ? 0 : parseFloat(productBill[thisPayment_name]);

        // 剩余应该 支付|收款（固定不变[要求]）
        var left_should_pay_name = inputName.left_should_pay_name;
        var left_should_pay = payables - actualpayables;

        // 去除未审核和驳回的账单金额
        var type = '';
        if (flowClass === '[BillPaymentFlow]') {
            type = 'thisPayment';
        } else if (flowClass === 'RemunerationFlow' || flowClass === '[BillReceivablesFlow]' || flowClass === '[InvoiceFlow]'){
            type = 'thisReceivables';
        }
        if (type !== '') {
            $.ajax({
                type: "POST",
                url: '/operate/queryApplying.action?v=' + new Date().getTime(),
                data: {
                    flowId: flowId,
                    billId: productBill.id,
                    type: type,
                    flowEntId: ''
                },
                async: false,
                dataType: 'json',
                success: function (data) {
                    if (data.code == 200) {
                        left_should_pay -= parseFloat(data.msg);
                    }
                }
            });
        }

        left_should_pay = left_should_pay.toFixed(3);
        left_should_pay = left_should_pay.substring(0, left_should_pay.length-1);
        // 本次应付（总）
        this_time_pay = parseFloat(this_time_pay) + parseFloat(left_should_pay);

        htmlStr +=
            "<div class='product-bill-line' name=''>" +
            "    <span class='product-bill-title' id='" + productBill.id + "'>" + productBill.title + "</span>" +
            "    <label class='layui-form-label'><span>" + labelName.payables_name + "</span></label>" +
            "    <div class='layui-input-block'><input name='payables' data-param-name='" + payables_name + "' class='layui-input pay_detail' value='" + payables.toFixed(2) + "' disabled  data-unit='元'/>" +
            "	 </div>" +
            "    <label class='layui-form-label'><span>" + labelName.actualpayables_name + "</span></label>" +
            "    <div class='layui-input-block'><input name='actualpayables' data-param-name='" + actualpayables_name + "' class='layui-input pay_detail' value='" + actualpayables.toFixed(2) + "' disabled  data-unit='元'/>" +
            "	 </div>" +
            "    <label class='layui-form-label'><span>" + labelName.left_should_pay_name + "</span></label>" +
            "    <div class='layui-input-block'><input name ='left_should_pay' data-param-name='" + left_should_pay_name + "' class='layui-input pay_detail' value='" + left_should_pay + "'  disabled  data-unit='元'/>" +
            "	 </div>" +
            "    <label class='layui-form-label'><span>" + labelName.thisPayment_name + "</span></label>" +
            "    <div class='layui-input-block'><input name='thisPayment' data-param-name='" + thisPayment_name + "' onchange='takeThisPaymentChange(this)' class='layui-input pay_detail isnum isdecimal2' value='" + left_should_pay + "' data-unit='元' />" +
            "    <i onclick='removeBillItem(this)' class='layui-icon layui-icon-delete delete-paroduct-bill'></i>" +
            "	 </div>" +
            "</div>";
    }
    // 合计
    htmlStr += "<i class='bill_count_tip'>" + labelName.total_name +
        "<span class='bill_this_time_pay'>" + format_num(this_time_pay, 2) + "</span>&nbsp;元</i>";

    // 获取紧邻的标签
    var next = $(parent).parent().next();
    if ($(next).hasClass("product_bill_item")) {
        // 删除已有的标签
        $(next).remove();
    }
    $(parent).parent().after(operateDiv);
    return htmlStr;
}

// 监听账单信息中本次收付款内容的改变
function takeThisPaymentChange(ele) {
    // 本次付款金额
    var this_pay_money = isBlank($(ele).val()) ? 0 : parseFloat($(ele).val());
    var bill_line_ele = $(ele).parents(".product-bill-line");
    // 总的应付
    var payables_ele = $(bill_line_ele).find("input[name='payables']");
    // 实际已经支付
    var actualpayables_ele = $(bill_line_ele).find("input[name='actualpayables']");
    // 剩余应付（dom）
    var left_should_pay_ele = $(bill_line_ele).find("input[name='left_should_pay']");
    // 总的应付
    var payables = isBlank($(payables_ele).val()) ? 0 : parseFloat($(payables_ele).val());
    // 实际已付
    var actualpayables = isBlank($(actualpayables_ele).val()) ? 0 : parseFloat($(actualpayables_ele).val());
    //剩余应付金额
    var left_should_pay = payables - actualpayables - this_pay_money;
    if (left_should_pay < 0) {
        this_pay_money = payables - actualpayables;
        $(ele).val(this_pay_money.toFixed(2));
        layer.tips("超过最大金额，设为最大金额", ele);
    }
    if (this_pay_money < 0) {
        layer.tips("数据错误，金额不能为负数", ele);
        $(ele).val(parseFloat(0).toFixed(2));
    }
    // 重新计算付款合计金额
    countThisTimeShouldPay($(ele).parents(".product_bill_item"));
}

// 删除账单项
function removeBillItem(ele) {
    var billFormItem = $(ele).parents(".product_bill_item");
    $(ele).parents(".product-bill-line").remove();
    // 重新计算付款合计金额
    countThisTimeShouldPay(billFormItem);
}

// 计算付款合计金额
function countThisTimeShouldPay(ele) {
    var pay_money = 0;
    var bill_items = $(ele).find("div[class*='product-bill-line']");
    if (!isBlank(bill_items) && bill_items.size() > 0) {
        for (var bill_item_index = 0; bill_item_index < bill_items.size(); bill_item_index++) {
            var bill_item = bill_items[bill_item_index];
            var this_pay = $(bill_item).find("input[name='thisPayment']").val();
            this_pay = isBlank(this_pay) ? 0 : parseFloat(this_pay).toFixed(2);
            pay_money = parseFloat(pay_money) + parseFloat(this_pay);
        }
    }
    $(ele).find("span[class*='bill_this_time_pay']").text(format_num(pay_money, 2));
}

// 处理账单金额标签的单位
function takeRemunerationUnit(id) {
    var flowEle = $("#flow_" + id);
    var all_unit_input = flowEle.find(".remuneration").find("input[data-unit]");
    if (!isBlank(all_unit_input) && all_unit_input.size() > 0) {
        for (var all_unit_input_index = 0; all_unit_input_index < all_unit_input.size(); all_unit_input_index++) {
            var temp = all_unit_input[all_unit_input_index];
            var data_uint = $(temp).attr("data-unit");
            if($(temp).next().get(0) == undefined || $(temp).next().get(0).className != 'remuneration_unit') {
                $(temp).after("<i class='remuneration_unit'>" + data_uint + "</i>")
            }
        }
    }
}

// 校验输入数据，并结算结果
function takeRemunerationInput(id) {
    var flowEle = $("#flow_" + id);
    // 校验监听事件
    flowEle.find(".remuneration").find("input").blur(function (e) {
        var ele_value = $(this).val();
        if (!$.isNumeric(ele_value)) {
            ele_value = 0;
            layer.tips('只能输入数字', this);
        }
        if ($(this).val() < 0) {
            ele_value = 0;
            layer.tips('不能为负数', this);
        }
        $(this).val(parseFloat(ele_value).toFixed(2));

        var money = $(this).parent().find("input[name='money']").val();
        var rate = $(this).parent().find("input[name='rate']").val();
        var reward = $(this).parent().find("input[name='reward']").val();
        var deduction = $(this).parent().find("input[name='deduction']").val();
        money = isBlank(money) ? 0 : parseFloat(money);
        rate = isBlank(rate) ? 0 : parseFloat(rate);
        reward = isBlank(reward) ? 0 : parseFloat(reward);
        deduction = isBlank(deduction) ? 0 : parseFloat(deduction);
        var remuneration = (money * rate) / 100 + reward - deduction;
        if (remuneration < 0) {
            layer.msg('酬金不能是负数，请重新填写！');
            $(this).parent().find("input[name='reward']").val(parseFloat(0).toFixed(2));
            $(this).parent().find("input[name='deduction']").val(parseFloat(0).toFixed(2));

            money = $(this).parent().find("input[name='money']").val();
            rate = $(this).parent().find("input[name='rate']").val();
            reward = $(this).parent().find("input[name='reward']").val();
            deduction = $(this).parent().find("input[name='deduction']").val();
            money = isBlank(money) ? 0 : parseFloat(money);
            rate = isBlank(rate) ? 0 : (parseFloat(rate) / 100);
            reward = isBlank(reward) ? 0 : parseFloat(reward);
            deduction = isBlank(deduction) ? 0 : parseFloat(deduction);
            remuneration = (money * rate) + reward - deduction;
        }
        $(this).parent().find("input[name='remuneration']").val(remuneration.toFixed(2));
    });
}

// 处理账单标签的单位
function takeBillUnit(id) {
    var flowEle = $("#flow_" + id);
    var inputs = flowEle.find("div[class*='product_bill_item']").find("input[class*='pay_detail']");
    if (isNotBlank(inputs) && inputs.size() > 0) {
        for (var bill_input_index = 0; bill_input_index < inputs.size(); bill_input_index++) {
            var input_temp = inputs[bill_input_index];
            var unit = $(input_temp).attr("data-unit");
            if (isNotBlank(unit)) {
                if($(input_temp).next().get(0) == undefined  || $(input_temp).next().get(0).className != 'bill-money-unit') {
                    $(input_temp).after("<i class='bill-money-unit'>" + unit + "&nbsp;&nbsp;</i>")
                }
            }
        }
    }
}

// 处理发票信息标签
function initSelectInvoice(htmlStr, mustLabelIds, item, flowClass, flowId) {
    htmlStr += "<div class='layui-form-item'>";
    htmlStr += "<label class='layui-form-label'>";
    if(mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += item.name +  "：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "<button data-type='0' value-type='23' type='button' " +
        "input-required='" + true + "' style='margin-right: 5px;width: fit-content;' " +
        "class='layui-btn layui-btn-sm' name='" + item.id  + "' onClick=\"chooseInvoice('" + item.id + "', '" + flowClass + "', '" + flowId + "')\">选择发票</button>";
    htmlStr += "</div>";
    htmlStr += "</div>";

    htmlStr += "<div class='layui-form-item invoice_info_item' id='" + item.id + "' style='display:none'>";
    htmlStr += "<div class='layui-input-block' style='line-height: 2'>"; // block

    htmlStr += "</div>";
    htmlStr += "</div>";

    return htmlStr;
}

// 选择发票按钮的点击事件
function chooseInvoice(divId, flowClass, flowId) {
    var url = '';
    if (isNotBlank(customerId)) {
        url = '/customerOperate/toQueryInvoice?productId=' + productId + "&flowClass=" + encodeURI(flowClass);
    }
    layui.use(['jquery', 'layer'], function(){
        var layer = layui.layer;
        layer.open({
            type: 2,
            area: ['535px', '355px'], //宽高
            content: url,
            btn: [ '选择', '取消' ],
            yes: function(index, layero) {
                var checkStatus = window["layui-layer-iframe" + index].layui.table.checkStatus('invoicelist');
                if(checkStatus.data.length > 0) {
                    var operateDiv = $("#" + divId);
                    operateDiv.show();
                    operateDiv.html(addInvoice(checkStatus.data, operateDiv, flowClass, flowId));
                    initValidate(flowId);
                    layer.close(index);
                } else {
                    layer.msg('请至少选择一个发票！');
                }
            }
        });
    });
}

// 显示选择的发票的信息
function addInvoice(data, operateDiv, flowClass, flowId) {
    var htmlStr = "";

    // 本次收付款
    var this_time_pay = 0;
    // 展示名称
    var labelName = billLabelName[flowClass];
    // 输入框名称
    var inputName = billInputName[flowClass];

    for (var index = 0; index < data.length; index++) {
        var invoice = data[index];

        // 应该 支付|收款
        var payables_name = inputName.payables_name;
        var payables = isBlank(invoice[payables_name]) ? 0 : parseFloat(invoice[payables_name]);

        // 实际(已经) 支付|收款
        var actualpayables_name = inputName.actualpayables_name;
        var actualpayables = isBlank(invoice[actualpayables_name]) ? 0 : parseFloat(invoice[actualpayables_name]);

        // 本次 支付|收款|开票
        var thisPayment_name = inputName.thisPayment_name;
        var thisPayment = isBlank(invoice[thisPayment_name]) ? 0 : parseFloat(invoice[thisPayment_name]);

        // 剩余应该 支付|收款（固定不变[要求]）
        var left_should_pay_name = inputName.left_should_pay_name;
        var left_should_pay = payables - actualpayables;

        // 去除未审核和驳回的账单金额
        var type = '';
        if (flowClass === '[BillReceivablesFlow]') {
            type = 'thisReceivables';
        }
        if (type !== '') {
            // 查询所有的未走完的此类流程中，flowMsg包含此发票id的流程，减去它们的本次收款
            $.ajax({
                type: "POST",
                url: '/operate/queryApplying.action?v=' + new Date().getTime(),
                data: {
                    flowId: flowId,
                    billId: invoice.id,
                    type: type,
                    flowEntId: ''
                },
                async: false,
                dataType: 'json',
                success: function (data) {
                    if (data.code == 200) {
                        left_should_pay -= parseFloat(data.msg);
                    }
                }
            });
        }

        left_should_pay = left_should_pay.toFixed(3);
        left_should_pay = left_should_pay.substring(0, left_should_pay.length-1);
        // 本次应付（总）
        this_time_pay = parseFloat(this_time_pay) + parseFloat(left_should_pay);

        htmlStr +=
            "<div class='invoice-info-line' name=''>" +
            "    <div class='invoice-info-title' id='" + invoice.id + "'>" + invoice.title + "</div>" +
            "    <label class='layui-form-label'><span>" + labelName.payables_name + "</span></label>" +
            "    <div class='layui-input-block'><input name='payables' data-param-name='" + payables_name + "' class='layui-input pay_detail' value='" + payables.toFixed(2) + "' disabled  data-unit='元'/>" +
            "    </div>" +
            "    <label class='layui-form-label'><span>" + labelName.actualpayables_name + "</span></label>" +
            "    <div class='layui-input-block'><input name='actualpayables' data-param-name='" + actualpayables_name + "' class='layui-input pay_detail' value='" + actualpayables.toFixed(2) + "' disabled  data-unit='元'/>" +
            "    </div>" +
            "    <label class='layui-form-label'><span>" + labelName.left_should_pay_name + "</span></label>" +
            "    <div class='layui-input-block'><input name ='left_should_pay' data-param-name='" + left_should_pay_name + "' class='layui-input pay_detail' value='" + left_should_pay + "'  disabled  data-unit='元'/>" +
            "    </div>" +
            "    <label class='layui-form-label'><span>" + labelName.thisPayment_name + "</span></label>" +
            "    <div class='layui-input-block'><input name='thisPayment' data-param-name='" + thisPayment_name + "' onchange='takeInvoiceInputChange(this)' class='layui-input pay_detail isnum isdecimal2' value='" + left_should_pay + "' data-unit='元'/>" +
            "    <i onclick='removeInvoiceItem(this)' class='layui-icon layui-icon-delete delete-paroduct-bill'></i>" +
            "    </div>" +
            "</div>";
    }
    htmlStr += "<i class='bill_count_tip'>" + labelName.total_name +
        "<span class='invoice_this_time_pay'>" + format_num(this_time_pay, 2) + "</span>&nbsp;元</i>";

    // 获取紧邻的标签
    var next = $(parent).parent().next();
    if ($(next).hasClass("invoice_info_item")) {
        // 删除已有的标签
        $(next).remove();
    }
    $(parent).parent().after(operateDiv);
    return htmlStr;
}

// 监听发票信息中本次收付款内容的改变
function takeInvoiceInputChange(ele) {
    // 本次付款金额
    var this_pay_money = isBlank($(ele).val()) ? 0 : parseFloat($(ele).val());
    var invoice_line_ele = $(ele).parents(".invoice-info-line");
    // 总的应付
    var payables_ele = $(invoice_line_ele).find("input[name='payables']");
    // 实际已经支付
    var actualpayables_ele = $(invoice_line_ele).find("input[name='actualpayables']");
    // 剩余应付（dom）
    var left_should_pay_ele = $(invoice_line_ele).find("input[name='left_should_pay']");
    // 总的应付
    var payables = isBlank($(payables_ele).val()) ? 0 : parseFloat($(payables_ele).val());
    // 实际已付
    var actualpayables = isBlank($(actualpayables_ele).val()) ? 0 : parseFloat($(actualpayables_ele).val());
    //剩余应付金额
    var left_should_pay = payables - actualpayables - this_pay_money;
    if (left_should_pay < 0) {
        this_pay_money = payables - actualpayables;
        $(ele).val(this_pay_money.toFixed(2));
        layer.tips("超过最大金额，设为最大金额", ele);
    }
    if (this_pay_money < 0) {
        layer.tips("数据错误，金额不能为负数", ele);
        $(ele).val(parseFloat(0).toFixed(2));
    }
    // 重新计算合计金额
    countInvoiceInput($(ele).parents(".invoice_info_item"));
}

// 删除发票项
function removeInvoiceItem(ele) {
    var invoiceFormItem = $(ele).parents(".invoice_info_item");
    $(ele).parents(".invoice-info-line").remove();
    // 重新计算合计金额
    countInvoiceInput(invoiceFormItem);
}

// 计算合计金额
function countInvoiceInput(ele) {
    var pay_money = 0;
    var invoice_items = $(ele).find("div[class*='invoice-info-line']");
    if (!isBlank(invoice_items) && invoice_items.size() > 0) {
        for (var index = 0; index < invoice_items.size(); index++) {
            var invoice_item = invoice_items[index];
            var this_pay = $(invoice_item).find("input[name='thisPayment']").val();
            this_pay = isBlank(this_pay) ? 0 : parseFloat(this_pay);
            pay_money = pay_money + this_pay;
        }
    }
    $(ele).find("span[class*='invoice_this_time_pay']").text(format_num(pay_money, 2));
}

// 处理账单标签的单位
function takeInvoiceUnit(id) {
    var flowEle = $("#flow_" + id);
    var inputs = flowEle.find("div[class*='invoice_info_item']").find("input[class*='pay_detail']");
    if (isNotBlank(inputs) && inputs.size() > 0) {
        for (var invoice_input_index = 0; invoice_input_index < inputs.size(); invoice_input_index++) {
            var input_temp = inputs[invoice_input_index];
            var unit = $(input_temp).attr("data-unit");
            if (isNotBlank(unit)) {
                if($(input_temp).next().get(0) == undefined  || $(input_temp).next().get(0).className != 'invoice-money-unit') {
                    $(input_temp).after("<i class='invoice-money-unit'>" + unit + "&nbsp;&nbsp;</i>")
                }
            }
        }
    }
}

// 处理未对账账单标签
function initUncheckedBill(htmlStr, mustLabelIds, item, flowClass, flowId) {
    htmlStr = htmlStr ? htmlStr : '';
    htmlStr += "<div class='layui-form-item unchecked-bills' data-label-name='" + item.name + "'>"; // form-item
    htmlStr += "<label class='layui-form-label' style='float: none'>";
    if (mustLabelIds.indexOf(item.id) >= 0) {
        htmlStr += "<span style='color: red;'>*</span>"
    }
    htmlStr += item.name + "：</label>";
    // 所有未对账账单
    htmlStr += "<div class='unchecked-bill-list' id='unchecked-bill-list-" + flowId + "'></div>"

    htmlStr += "<div style='padding-top: 5px'><span class='layui-icon layui-icon-tips' title='提示'></span><span>未找到？<a href='javascript:void(0);' onclick='showBuildBill(\"" + sale_customer_id + "\")' style='text-decoration: underline; color: #1E9FFF;'>生成账单</a></span></div>"
    // 本次对账总计
    htmlStr += "<div class='unchecked-bill-total'>";  // total
    htmlStr += "<label class='layui-form-label unchecked-bill-total-tip'>对账总计：</label>";
    // 我司数据总计
    htmlStr += "<div class='bill-data-total' data-type='platform'><span>我司数据：</span>" +
        "<span>计费数：</span>" +
        "<input type='text' disabled class='layui-input bill-money isnum' style='text-align: left' name='successCount' placeholder='成功数' data-unit='条' />" +
        "<span>金额：</span>" +
        "<input type='text' disabled class='layui-input bill-money' style='width: 120px; text-align: left' placeholder='金额' name='amount' data-unit='元'/>" +
        "</div>";

    // 实际数据总计
    htmlStr += "<div class='bill-data-total' data-type='checked'><span>对账数据：</span>" +
        "<span>计费数：</span>" +
        "<input type='text' disabled class='layui-input bill-money isnum' style='text-align: left' name='successCount' placeholder='成功数' value='' data-unit='条' />" +
        "<span>金额：</span>" +
        "<input type='text' disabled class='layui-input bill-money' style='width: 120px; text-align: left' placeholder='金额' name='amount' value='' data-unit='元'/>" +
        "</div>";
    htmlStr += "</div>";    // total

    // 电子账单 数据详情复选框
    htmlStr += "<div class='unchecked-bill-file'>";
    // htmlStr += "<label class='layui-form-label'>电子账单：</label>";
    htmlStr += "<input type='checkbox' lay-filter='bill-file-" + flowId + "' name='billFile' title='电子账单' lay-skin='primary' value='billFile' checked/>";
    htmlStr += "<input type='checkbox' lay-filter='bill-file-" + flowId + "' name='billFile' title='数据详情' lay-skin='primary' value='dataDetail'/>";
    htmlStr += "<button type='button' name='downloadFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='downloadCheckBillFile(this, \"" + flowId + "\")'>下载</button>";
    htmlStr += "<button type='button' name='previewFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='previewCheckBillFile(this, \"" + flowId + "\")'>预览</button></br>";
    htmlStr += "</div>";
    htmlStr += "<hr>";
    htmlStr += "<div class='bill-analysis-file'>";
    htmlStr += "<label class='layui-form-label'>数据报告：</label>";
    htmlStr += "<button type='button' name='downloadFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='downloadDataAnalysisFile(this, \"" + flowId + "\")'>下载</button>";
    htmlStr += "<button type='button' name='previewFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='previewDataAnalysisFile(this, \"" + flowId + "\")'>预览</button></br>";
    htmlStr += "</div>";

    htmlStr += "</div>";     // form-item
    // 查询未对账账单
    getUncheckedBillInfo(flowId, item);
    initBillFileOptionCheck(flowId);
    return htmlStr;
}

// 时间段
function initTimeSlot(htmlStr, mustLabelIds, item, dateRender2TimeSlot) {
	htmlStr = initNomalLabel(htmlStr, mustLabelIds, item);
    htmlStr += "<input type='text' class='layui-input layui-date-pointer'";
    htmlStr += " name='formDate" + dateItemIndex + "' id='" +  item.id + "' placeholder='请选择日期时间范围' readonly '/>"
    dateRender2TimeSlot.push("formDate" + dateItemIndex++);
    htmlStr += "</div>"; // block-div

    var defaultWorkTime = '8:30-11:45,13:15-18:00';
    if(isNotBlank(item.defaultValue)) {
        defaultWorkTime = item.defaultValue;
    }
    htmlStr += "<label class='layui-form-label'>天数：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "    <div class='time-duration-div'>"
    htmlStr += "        <span class='time-duration' data-work-time='" + defaultWorkTime + "'>0</span>"
    htmlStr += "        <span>(以工作时间" + defaultWorkTime + "为1个工作日，自动扣除非工作时间)</span>"
    htmlStr += "    </div>"; // block-div
    htmlStr += "</div>"; // block-div

    htmlStr += "</div>";  // 表单元素一行结束 item-div

    return htmlStr;
}

/**
 * 根据选择的时间段计算工作日
 *
 * @param ele       日期控件
 * @param date      开始日期
 * @param endDate   结束日期
 */
function timeSlotChange(ele, date, endDate) {
    var durationEle = $(ele).parents('div.layui-form-item').find('span.time-duration');
    if (durationEle.length === 0) return;
    // 8:30-11:45,13:15-18:00
    var workTime = $(durationEle).attr('data-work-time');
    var workTimes = workTime.split(','); // [8:30-11:45, 13:15-18:00]
    var amWorkTime = workTimes[0].split('-'); // [8:30, 11:45]
    var pmWorkTime = workTimes[1].split('-'); // [13:15, 18:00]
    var amWorkStartTime = amWorkTime[0].split(':'); // [8, 30]
    var amWorkEndTime = amWorkTime[1].split(':'); // [11, 45]
    var pmWorkStartTime = pmWorkTime[0].split(':'); // [13, 15]
    var pmWorkEndTime = pmWorkTime[1].split(':'); // [18, 0]

    // 上下班的时分折算成一天的分钟
    var amWorkStart = parseInt(amWorkStartTime[0]) * 60 + parseInt(amWorkStartTime[1]);
    var amWorkEnd = parseInt(amWorkEndTime[0]) * 60 + parseInt(amWorkEndTime[1]);
    var pmWorkStart = parseInt(pmWorkStartTime[0]) * 60 + parseInt(pmWorkStartTime[1]);
    var pmWorkEnd = parseInt(pmWorkEndTime[0]) * 60 + parseInt(pmWorkEndTime[1]);
    // 一个工作日的工作分钟数
    var totalWorkTime = amWorkEnd - amWorkStart + pmWorkEnd - pmWorkStart;
    // 请假时间的时分折算成一天的分钟
    var startTime = date.hours * 60 + date.minutes;
    var endTime = endDate.hours * 60 + endDate.minutes;
    if (startTime <= amWorkStart) {
        startTime = amWorkStart;
    }
    if (endTime <= amWorkStart) {
        endTime = amWorkStart;
    }
    if (startTime >= pmWorkEnd) {
        startTime = pmWorkEnd;
    }
    if (endTime >= pmWorkEnd) {
        endTime = pmWorkEnd;
    }

    var startDay = new Date(date.year, date.month - 1, date.date);
    var endDay = new Date(endDate.year, endDate.month - 1, endDate.date);
    // 请假天数
    var days = (endDay - startDay) / (24 * 60 * 60 * 1000);
    var minutes = endTime - startTime;

    if (days === 0 || minutes >= 0) {
        // 同一工作日，或 跨工作日且结束时间的时分在开始时间的时分之后
        // 如：2020-08-18 08:30:00 - 2020-08-18 18:00:00
        // 2020-08-18 08:30:00 - 2020-08-21 18:00:00
        if (startTime <= amWorkEnd && pmWorkStart <= endTime) {
            // __8_|_11__13_|_18__
            minutes -= pmWorkStart - amWorkEnd;
        } else if (startTime <= amWorkEnd && amWorkEnd <= endTime && endTime <= pmWorkStart) {
            // __8_|_11_|_13__18__
            minutes -= endTime - amWorkEnd;
        } else if (amWorkEnd <= startTime && startTime <= pmWorkStart && endTime <= pmWorkStart) {
            // __8__11_|_|_13__18__
            minutes -= endTime - startTime;
        } else if (amWorkEnd <= startTime && startTime <= pmWorkStart && pmWorkStart <= endTime ) {
            // __8__11_|_13_|_18__
            minutes -= pmWorkStart - startTime;
        }
        days += accDiv(minutes, totalWorkTime);
    } else if (days > 0 && minutes < 0) {
        // 跨工作日，且结束时间的时分在开始时间的时分之前
        // 如：2020-08-18 13:15:00 - 2020-08-21 11:45:00
        days--;
        minutes = 0;
        // 结束时间
        if (amWorkStart <= endTime && endTime <= amWorkEnd) {
            // __8_|_11__13__18__
            minutes += endTime - amWorkStart;
        } else if (amWorkEnd <= endTime && endTime <= pmWorkStart) {
            // __8__11_|_13__18__
            minutes += amWorkEnd - amWorkStart;
        } else if (pmWorkStart <= endTime && endTime <= pmWorkEnd) {
            // __8__11__13_|_18__
            minutes += (amWorkEnd - amWorkStart) + (endTime - pmWorkStart);
        }
        days += accDiv(minutes, totalWorkTime);

        minutes = 0;
        // 开始时间
        if (amWorkStart <= startTime && startTime <= amWorkEnd) {
            // __8_|_11__13__18__
            minutes += (amWorkEnd - startTime) + (pmWorkEnd - pmWorkStart);
        } else if (amWorkEnd <= startTime && startTime <= pmWorkStart) {
            // __8__11_|_13__18__
            minutes += pmWorkEnd - pmWorkStart;
        } else if (pmWorkStart <= startTime && startTime <= pmWorkEnd) {
            // __8__11__13_|_18__
            minutes += pmWorkEnd - startTime;
        }
        days += accDiv(minutes, totalWorkTime);
    }

    $(durationEle).text(format_num(days, 2));
}

// 单选框
function initRadioType(htmlStr, mustLabelIds, item, flowId) {
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    $.each(item.defaultValue.split(","), function (j, value) {
        htmlStr += '<input type="radio" name="radio-' + flowId + '-' + item.id + '" value="' + value + '" title="' + value + '"' + (j == 0 ? ' checked' : '') + '>'
    });
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

// 请假类型单选框
function initLeaveType(htmlStr, mustLabelIds, item, flowId) {
    htmlStr = initNomalLabel(htmlStr,  mustLabelIds, item);
    $.each(item.defaultValue.split(","), function (j, value) {
        htmlStr += '<input type="radio" name="' + item.id + '" lay-filter="leave-type-' + flowId + '" value="' + value + '" title="' + value + '">'
    });
    htmlStr += "</div>"; // block-div
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "    <span class='leave-info'></span>"
    htmlStr += "    <input type='hidden' class='leave-days'/>"
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

//新时间段
function initNewTimeSlot(htmlStr, mustLabelIds, item, dateRender2Day) {
    //TODO
    htmlStr = initNomalLabel(htmlStr, mustLabelIds, item);
    htmlStr += "<input type='text' class='layui-input layui-date-pointer'";
    htmlStr += " name='formDate" + dateItemIndex
        + "' id='" +  item.id + "' placeholder='请选择日期' readonly '/>"
    dateRender2Day.push("formDate" + dateItemIndex++);
    htmlStr += "<select class='layui-select' id='"+item.id+"' lay-filter='selectTime'><option value='上午'>上午</option><option value='下午'>下午</option></select></div>";
    var defaultWorkTime = '8:30-11:45,13:15-18:00';
    if(isNotBlank(item.defaultValue)) {
        defaultWorkTime = item.defaultValue;
    }
    htmlStr += "</div>";  // 表单元素一行结束 item-div
    return htmlStr;
}

/**
 * 绑定请假类型单选的点击事件，查请假信息
 * @param flowId
 */
function bindLeaveTypeCheck(flowId) {
    layui.use(['form', 'element'], function () {
        var form = layui.form;
        form.on('radio(leave-type-' + flowId + ')', function (data) {
            var ele = data.elem;
            var msgEle = $(ele).parents('div.LAYUI-FORM-ITEM').find('span.leave-info');
            msgEle.html('');
            $.post('/userLeave/getLeaveInfo', {leaveType: data.value}, function (res) {
                res = typeof res == "object" ? res : JSON.parse(res);
                var msg = res.msg;
                if (isNotBlank(res.leftDays)) {
                    $(msgEle).next().val(res.leftDays);
                }
                msgEle.text(msg);
            })
        });
    })
}

function initAccountRecharge(htmlStr, item, flowClass) {
	
	// 展示名称
    var show_name = product_bill_label_name[flowClass];
    // 输入框名称
    var input_name = product_bill_input_name[flowClass];
    
    var id = uuid();
	
	var accounts = queryAccounts(productId);
	accounts = !accounts ? '' : accounts;
	htmlStr = '<div class="layui-form-item account-recharge" id="' + id + '" style="">'
		+ '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.recharge_account + '：</label>'
	    + '<div class="layui-input-block">'
	    + '<select name="' + input_name.recharge_account + '" lay-filter="recharge-account">';
	$(accounts).each(function (i, item) {
		htmlStr += '<option value="' + item + '">' + item + '</option>';
	});
	htmlStr += '</select>'
	    + '</div>'
	    + '<label class="layui-form-label">' + show_name.current_amount + '：</label>'
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input isnum isdecimal gradient-detail" name="' + input_name.current_amount + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">元</span>'
	    + '</div>'
	    + '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.price + '：</label>'
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.price + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">元</span>'
	    + '</div>'
	    + '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.recharge_amount + '：</label>'
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.recharge_amount + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">元</span>'
	    + '</div>'
	    + '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.pieces + '：</label>'
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.pieces + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">条</span>';
	if (accounts.length > 1) {
		htmlStr +='<div class="layui-inline" style="display: inline-block;">'
			+ '<label class="layui-form-label" style="width: fit-content; text-align: left;  padding-left: 15px">'
			+ '<span class="gradient_btn_add" id="addAccountRecharge">'
			+ '<i class="layui-icon layui-icon-add-circle"></i>'
			+ '</span></label>'
			+ '</div>';
	}
	htmlStr += '</div>'
	    + '</div>';
	
	var bindEvent = function (filer) {
		var ele = $(filer);
		ele.find('#addAccountRecharge').unbind().bind('click', function () {
			if ($('.account-recharge').length == $('#productId_' + productId).val().split(',').length) {
				return;
			}
			if (!ele.find('[name="rechargeAccount"]').val()) {
				return layer.tips('请选择', ele.find('[name="' + input_name.recharge_account + '"]'));
			}
			if (!ele.find('[name="currentAmount"]').val()) {
				return layer.tips('不能为空或等于0', ele.find('[name="' + input_name.current_amount + '"]'));
			}
			if (!ele.find('[name="price"]').val()) {
				return layer.tips('不能为空或等于0', ele.find('[name="' + input_name.price + '"]'));
			}
			if (!ele.find('[name="rechargeAmount"]').val()) {
				return layer.tips('不能为空或等于0', ele.find('[name="' + input_name.recharge_amount + '"]'));
			}
			if (!ele.find('[name="pieces"]').val()) {
				return layer.tips('不能为空或等于0', ele.find('[name="' + input_name.pieces + '"]'));
			}
			
			$(ele).find('input').attr('readonly', 'readonly');
			$(ele).find('select').attr('disabled', 'disabled');
			
			var uid = uuid();
			
			var clone = $(htmlStr);
			$(clone).attr('id', uid);
			$('[name="rechargeAccount"]').each(function (i, item) {
				$(clone).find('option[value="' + $(item).val() + '"]').remove();
			});
			
			$(clone).find('#addAccountRecharge')
				.parents('label')
				.append('<span class="gradient_btn_reduce" id="reduceAccountRecharge"><i class="layui-icon layui-icon-close-fill"></i></span>');
			
			if ($('.account-recharge').length + 1 == $('#productId_' + productId).val().split(',').length) {
				$(clone).find('#addAccountRecharge').remove();
				$(clone).find('#layui-icon-add-circle').remove();
			}
			
			$(ele).find('#addAccountRecharge').parents('label').remove();
			$(ele).after(clone);
			layui.form.render();
			bindEvent('#' + uid);
		});
		
		ele.find('#reduceAccountRecharge').unbind().bind('click', function () {
			var label = $('<label class="layui-form-label" style="width: fit-content; text-align: left;  padding-left: 15px">'
				+ '<span class="gradient_btn_add" id="addAccountRecharge"><i class="layui-icon layui-icon-add-circle"></i></span>'
				+ '<span class="gradient_btn_reduce" id="reduceAccountRecharge"><i class="layui-icon layui-icon-close-fill"></i></span></label>');
			
			if ($('.account-recharge').length == 2) {
				label.find('#reduceAccountRecharge').remove();
				label.find('.layui-icon-close-fill').remove();
			}
			
			var prev = $(this).parents('.account-recharge').prev();
			$(prev).html();
			$(prev).find('input').removeAttr('readonly');
			$(prev).find('select').removeAttr('disabled');
			prev.find('.layui-input-block:last-child .layui-inline').append(label);
			
			$(this).parents('.account-recharge').remove();
			layui.form.render();
			bindEvent('#' + prev.attr('id'));
		});

		
		ele.find('[name="rechargeAmount"],[name="price"]').unbind().bind('keyup', function () {
			var rechargeAmount = ele.find('[name="rechargeAmount"]').val();
			var price = ele.find('[name="price"]').val();
			rechargeAmount = !rechargeAmount ? 0 : rechargeAmount;
			price = !price ? 0 : price;
			var pieces = price == 0 ? 0 : (rechargeAmount / price);
			pieces = isNaN(pieces) ? 0 : pieces;
			ele.find('[name="pieces"]').val(parseInt(pieces.toFixed(0)));
		});
	}
	
	renderFormFuns.push(function () {bindEvent('#' + id);});
	
    return '<input type="hidden" id="productId_' + productId + '" value="' + (!accounts ? '' : accounts.join(',')) + '">' + htmlStr;
}

function queryAccounts(productId) {
	var result = null;
	$.ajax({
        type: "POST",
        async: false,
        url: '/customerProduct/queryAccounts?temp=' + Math.random(),
        dataType: 'json',
        data: {
            productId: productId
        },
        success: function (data) {
        	result = data;
        }
	});
	return result;
}

// 电子账单、数据详情复选框事件
function initBillFileOptionCheck(flowId) {
    layui.use('form', function () {
        var form = layui.form;
        form.on('checkbox(bill-file-' + flowId + ')', function (data) {
            // 每次重新计算总计之后，重新绑定下载、预览按钮的事件
            var billFileEle = $(this).parents('div.unchecked-bill-file');
            billFileEle.find('button[name=downloadFile]').attr('onclick', "downloadCheckBillFile(this, \"" + flowId + "\")");
            billFileEle.find('button[name=previewFile]').attr('onclick', "previewCheckBillFile(this, \"" + flowId + "\")");
            delete file_result['billFile_' + flowId];
        })
    })
}

// 下载已选中产品的对账单，不存在时先生成
function downloadCheckBillFile(ele, flowId, billFile) {
    if (typeof billFile == 'object') {
        down_load(billFile);
    } else {
        var billIds = [];
        var labelEle = $(ele).parents('div.unchecked-bills');
        var checkedBoxes = labelEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
        if (checkedBoxes.length === 0) {
            layui.layer.msg('请选择账单');
            return;
        }
        var optionBoxes = $(ele).parent().find('input[name=billFile]:checked');
        if (optionBoxes.length === 0) {
            layui.layer.msg('请勾选账单或数据详情复选框');
            return;
        }
        var options = []
        $(optionBoxes).each(function () {
            options.push($(this).val());
        })
        $(checkedBoxes).each(function() {
            var itemEle = $(this).parent();
            billIds.push(itemEle.attr('id'));
        });
        // 对账单的实际付款用对账总计
        var checkEle = labelEle.find('div.unchecked-bill-total').find('div[data-type=checked]');
        var billTotal = {
            checkedSuccessCount: checkEle.find('input[name=successCount]').val().replace(/,/g, ''),
            checkedAmount: checkEle.find('input[name=amount]').val().replace(/,/g, '')
        };

        if (billIds.length > 0) {
            $.post("/bill/buildCheckBillFile", {
                billIds: billIds.join(','),
                billTotal: JSON.stringify(billTotal),
                options: options.join(',')
            }, function (res) {
                if (res.code == 200) {
                    billFile = res.data;
                    file_result['billFile_' + flowId] = billFile;
                    $(ele).attr('onclick', "downloadCheckBillFile(this, \"" + flowId + "\", " + JSON.stringify(billFile) +")");
                    $(ele).next().attr('onclick', "previewCheckBillFile(this, \"" + flowId + "\", " + JSON.stringify(billFile) +")");
                    down_load(billFile);
                } else {
                    delete file_result['billFile_' + flowId];
                    layui.layer.msg(res.msg);
                }
            })
        }
    }
}

// 预览已选中产品的对账单，不存在时先生成
function previewCheckBillFile(ele, flowId, billFile) {
    if (typeof billFile == 'object') {
        view_File(billFile);
    } else {
        var billIds = [];
        var labelEle = $(ele).parents('div.unchecked-bills');
        var checkedBoxes = labelEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
        if (checkedBoxes.length === 0) {
            layui.layer.msg('请选择账单');
            return;
        }
        var optionBoxes = $(ele).parent().find('input[name=billFile]:checked');
        if (optionBoxes.length === 0) {
            layui.layer.msg('请勾选账单或数据详情复选框');
            return;
        }
        var options = []
        $(optionBoxes).each(function () {
            options.push($(this).val());
        })
        $(checkedBoxes).each(function() {
            var itemEle = $(this).parent();
            billIds.push(itemEle.attr('id'));
        });
        // 对账单的实际付款用对账总计
        var checkEle = labelEle.find('div.unchecked-bill-total').find('div[data-type=checked]');
        var billTotal = {
            checkedSuccessCount: checkEle.find('input[name=successCount]').val().replace(/,/g, ''),
            checkedAmount: checkEle.find('input[name=amount]').val().replace(/,/g, '')
        };
        if (billIds.length > 0) {
            $.post("/bill/buildCheckBillFile", {
                billIds: billIds.join(','),
                billTotal: JSON.stringify(billTotal),
                options: options.join(',')
            }, function (res) {
                if (res.code == 200) {
                    billFile = res.data;
                    file_result['billFile_' + flowId] = billFile;
                    $(ele).attr('onclick', "previewCheckBillFile(this, \"" + flowId + "\", " + JSON.stringify(billFile) +")");
                    $(ele).prev().attr('onclick', "downloadCheckBillFile(this, \"" + flowId + "\", " + JSON.stringify(billFile) +")");
                    view_File(billFile);
                } else {
                    delete file_result['billFile_' + flowId];
                    layui.layer.msg(res.msg);
                }
            })
        }
    }
}

// 下载已选中产品的数据分析报告，不存在时先生成
function downloadDataAnalysisFile(ele, flowId, analysisFile) {
    if (typeof analysisFile == 'object') {
        down_load(analysisFile);
    } else {
        var billIds = [];
        var labelEle = $(ele).parents('div.unchecked-bills');
        var checkedBoxes = labelEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
        if (checkedBoxes.length === 0) {
            layui.layer.msg('请选择账单');
            return;
        }
        $(checkedBoxes).each(function() {
            var itemEle = $(this).parent();
            billIds.push(itemEle.attr('id'));
        });

        if (billIds.length > 0) {
            $.post("/bill/buildDataAnalysisFile", {
                billIds: billIds.join(','),
            }, function (res) {
                if (res.code == 200) {
                    analysisFile = res.data;
                    file_result['analysisFile_' + flowId] = analysisFile;
                    $(ele).attr('onclick', "downloadDataAnalysisFile(this, \"" + flowId + "\", " + JSON.stringify(analysisFile) +")");
                    $(ele).next().attr('onclick', "previewDataAnalysisFile(this, \"" + flowId + "\", " + JSON.stringify(analysisFile) +")");
                    layui.layer.alert('正在生成中，请勿离开。稍后请再次点击下载按钮即可下载', {icon: 7})
                    // down_load(billFile);
                } else {
                    delete file_result['analysisFile_' + flowId];
                    layui.layer.msg(res.msg);
                }
            })
        }
    }
}

// 预览已选中产品的数据分析报告，不存在时先生成
function previewDataAnalysisFile(ele, flowId, analysisFile) {
    if (typeof analysisFile == 'object') {
        view_File(analysisFile);
    } else {
        var billIds = [];
        var labelEle = $(ele).parents('div.unchecked-bills');
        var checkedBoxes = labelEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
        if (checkedBoxes.length === 0) {
            layui.layer.msg('请选择账单');
            return;
        }
        $(checkedBoxes).each(function() {
            var itemEle = $(this).parent();
            billIds.push(itemEle.attr('id'));
        });

        if (billIds.length > 0) {
            $.post("/bill/buildDataAnalysisFile", {
                billIds: billIds.join(','),
            }, function (res) {
                if (res.code == 200) {
                    analysisFile = res.data;
                    file_result['analysisFile_' + flowId] = analysisFile;
                    $(ele).attr('onclick', "previewDataAnalysisFile(this, \"" + flowId + "\", " + JSON.stringify(analysisFile) +")");
                    $(ele).prev().attr('onclick', "downloadDataAnalysisFile(this, \"" + flowId + "\", " + JSON.stringify(analysisFile) +")");
                    layui.layer.alert('正在生成中，请勿离开。稍后请再次点击预览按钮即可预览', {icon: 7})
                    // down_load(billFile);
                } else {
                    delete file_result['analysisFile_' + flowId];
                    layui.layer.msg(res.msg);
                }
            })
        }
    }
}

// 请求后台，获取未对账账单
function getUncheckedBillInfo(flowId, item) {
    $.ajax({
        type: "POST",
        async: true,
        url: '/bill/readUncheckedBills?temp=' + Math.random(),
        dataType: 'json',
        data: {
            customerId: customerId
        },
        success: function (data) {
            // 渲染标签
            if (data.code == 200) {
                var billsEle = $('#unchecked-bill-list-' + flowId);
                var bills = data.data;
                var html = "";
                $.each(bills, function (index, bill) {
                    // 拼接该账单的HTML
                    html += addUncheckedBillInfoItem(flowId, bill);
                })
                billsEle.html(html);
                // 绑定账单选中事件
                layui.use('form', function() {
                    var form = layui.form;
                    form.render();
                    form.on('checkbox(unchecked-bill-item-' + flowId + ')', function(data) {
                        // 计算总计
                        takeUncheckedBillTotal(flowId);
                    });
                });
            }
        }
    });
}

// 拼接一个未对账账单的样式
function addUncheckedBillInfoItem(flowId, bill) {
    var html = "";
    html += "<div class='unchecked-bill-item' id='" + bill.id + "'>";
    html += "   <input type='checkbox' lay-filter='unchecked-bill-item-" + flowId + "' title='" + bill.title + "' lay-skin='primary' >";
    html += "   <span class='layui-icon layui-icon-refresh unchecked-bill-tools' title='重新统计' onclick='rebuildBill(this, \"" + flowId + "\", \"" + bill.productId + "\", \"" + bill.billMonth + "\")'></span>";
    var remark = bill.remark;
    if (isNotBlank(remark)) {
        html += "   <span class='layui-icon layui-icon-rmb unchecked-bill-tools' title='价格详情' onclick='showRemark(this)'></span>";
        html += "   <div class='bill-remark layui-nav-child layui-anim layui-anim-upbit'><p>" + remark + "</p>";
        html += "   <span class='layui-icon layui-icon-close-fill' title='关闭' onclick='showRemark(this)' style='position: absolute; top: -30px; font-size: 30px; text-align: center'></span>";
        html += "   </div>"
    }
    var billFile = bill.billFile;
    if (isNotBlank(billFile)) {
        // billFile = typeof billFile == 'object' ? billFile : JSON.parse(billFile);
        html += "   <span class='layui-icon layui-icon-file unchecked-bill-tools' title='电子账单' onclick='view_File(" + billFile + ")'></span>";
    }
    // 我司数据，后台统计的，不可修改
    html += "<div class='bill-data' data-type='platform'><span>我司数据</span>" +
        "<input type='text' disabled class='layui-input bill-money isnum isint' name='successCount' placeholder='成功数' value='" + bill.platformSuccessCount + "' data-unit='条' />" +
        "<span>X</span>" +
        "<input type='text' disabled class='layui-input bill-money isnum isdecimal' name='unitPrice' style='width: 60px' placeholder='单价' value='" + bill.platformUnitPrice + "' data-unit='元' />" +
        "<span>=</span>" +
        "<input type='text' disabled class='layui-input bill-money isdecimal' style='width: 120px' placeholder='金额' name='amount' value='" + bill.platformAmount + "' data-unit='元'/></div>";
    // 客户数据，默认填充的是后台统计数据，可修改
    html += "<div class='bill-data' data-type='customer'><span>客户数据</span>" +
        "<input type='text' class='layui-input bill-money isnum isint' name='successCount' placeholder='成功数' onblur='takeUncheckedBillChange(this, \"" + flowId + "\")' value='" + bill.customerSuccessCount + "' data-unit='条' />" +
        "<span>X</span>" +
        "<input type='text' class='layui-input bill-money isnum isdecimal' name='unitPrice' style='width: 60px' placeholder='单价' onblur='takeUncheckedBillChange(this, \"" + flowId + "\")' value='" + bill.customerUnitPrice + "' data-unit='元' />" +
        "<span>=</span>" +
        "<input type='text' class='layui-input bill-money isdecimal' style='width: 120px' placeholder='金额' onblur='takeUncheckedBillTotal(\"" + flowId + "\")' name='amount' value='" + bill.customerAmount + "' data-unit='元'/></div>";
    // 对完账后实际得出的数据，默认填充的是后台统计数据，可修改
    html += "<div class='bill-data' data-type='checked'><span>对账数据</span>" +
        "<input type='text' class='layui-input bill-money isnum isint' name='successCount' placeholder='成功数' onblur='takeUncheckedBillChange(this, \"" + flowId + "\")' value='" + bill.checkedSuccessCount + "' data-unit='条' />" +
        "<span>X</span>" +
        "<input type='text' class='layui-input bill-money isnum isdecimal' name='unitPrice' style='width: 60px' placeholder='单价' onblur='takeUncheckedBillChange(this, \"" + flowId + "\")' value='" + bill.checkedUnitPrice + "' data-unit='元' />" +
        "<span>=</span>" +
        "<input type='text' class='layui-input bill-money isdecimal' style='width: 120px' placeholder='金额' onblur='takeUncheckedBillTotal(\"" + flowId + "\")' name='amount' value='" + bill.checkedAmount + "' data-unit='元'/></div>";
    html += "</div>";
    return html;
}

function showRemark(ele) {
    $(ele).parents('div.unchecked-bill-item').find('div.bill-remark').toggleClass('layui-show');
}

// 账单后面的刷新按钮的点击事件
function rebuildBill(ele, flowId, productId, yearMonth) {
    layer.confirm("确定要重新统计账单数据？", function (index) {
        $(".layui-layer-btn0").css("pointer-events","none");
        var loading = layer.load(2);
        $.post('/bill/rebuildBill', {productId: productId, billMonth: yearMonth}, function (res) {
            layer.close(index);
            layer.close(loading);
            if (res.code == 200) {
                var bill = res.data;
                var platformEle = $(ele).parent().find('div[data-type=platform]');
                $(platformEle).find('input[name=successCount]').val(bill.platformSuccessCount);
                $(platformEle).find('input[name=unitPrice]').val(bill.platformUnitPrice);
                $(platformEle).find('input[name=amount]').val(bill.platformAmount);
                var customerEle = $(ele).parent().find('div[data-type=customer]');
                $(customerEle).find('input[name=successCount]').val(bill.customerSuccessCount);
                $(customerEle).find('input[name=unitPrice]').val(bill.customerUnitPrice);
                $(customerEle).find('input[name=amount]').val(bill.customerAmount);
                var checkedEle = $(ele).parent().find('div[data-type=checked]');
                $(checkedEle).find('input[name=successCount]').val(bill.checkedSuccessCount);
                $(checkedEle).find('input[name=unitPrice]').val(bill.checkedUnitPrice);
                $(checkedEle).find('input[name=amount]').val(bill.checkedAmount);
                takeUncheckedBillTotal(flowId);
                parent.layer.msg(res.msg + "，请重新点开流程查看", {time: 5000});
            } else {
                parent.layer.msg(res.msg, {time: 5000});
            }
        })
    })
}

// 生成账单a标签的点击事件
function showBuildBill(customerId) {
    layui.use(['layer', 'form'], function () {
        var layer = layui.layer;
        layer.open({
            type: 2,
            area: ['400px', '450px'],
            fixed: false, //不固定
            maxmin: false,
            title: '生成产品月账单',
            content: '/bill/toBuildBill?customerId=' + customerId
        });
    })
}

// 客户数据和对账数据的修改
function takeUncheckedBillChange(ele, flowId) {
    var parentEle = $(ele).parent();
    var successCountEle = parentEle.find('input[name=successCount]');
    if (!$.isNumeric(successCountEle.val())) {
        layer.tips('数量只能是数字', successCountEle);
        $(successCountEle).val(0);
    }
    var unitPriceEle = parentEle.find('input[name=unitPrice]');
    if (!$.isNumeric(unitPriceEle.val())) {
        layer.tips('单价只能是数字', unitPriceEle);
        $(unitPriceEle).val('0.000000');
    } else {
        $(unitPriceEle).val(parseFloat(unitPriceEle.val()).toFixed(6));
    }
    var amount = accMulti(parseInt(successCountEle.val()), parseFloat(unitPriceEle.val()));
    parentEle.find('input[name=amount]').val(amount.toFixed(2));
    takeUncheckedBillTotal(flowId);
}

// 自动计算对账总计
function takeUncheckedBillTotal(flowId) {
    // 平台总计
    var platformTotal = {
        'successCount': 0,
        'amount': 0.0
    };
    // 对账总计
    var checkedTotal = {
        'successCount': 0,
        'amount': 0.0
    };
    var flag = true; // 为false说明输入数据不正确
    var billsEle = $('#flow_' + flowId).find('div.unchecked-bills');
    var checkedBoxes = billsEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
    $(checkedBoxes).each(function() {
        var itemEle = $(this).parent();
        var platformEle = $(itemEle).find('div[data-type=platform]');
        var platformSuccessCountEle = platformEle.find('input[name=successCount]');
        if (!$.isNumeric(platformSuccessCountEle.val())) {
            layer.tips('数量只能是数字', platformSuccessCountEle);
            flag = false;
            return false;
        }
        platformTotal.successCount = platformTotal.successCount + parseInt(platformSuccessCountEle.val());
        var platformUnitPriceEle = platformEle.find('input[name=unitPrice]');
        if (!$.isNumeric(platformUnitPriceEle.val())) {
            layer.tips('单价只能是数字', platformUnitPriceEle);
            flag = false;
            return false;
        }
        var platformAmountEle = platformEle.find('input[name=amount]');
        if (!$.isNumeric(platformAmountEle.val())) {
            layer.tips('金额只能是数字', platformAmountEle);
            flag = false;
            return false;
        }
        platformTotal.amount = accAdd(platformTotal.amount, parseFloat(platformAmountEle.val()));

        var customerEle = $(itemEle).find('div[data-type=customer]');
        var customerSuccessCountEle = customerEle.find('input[name=successCount]');
        if (!$.isNumeric(customerSuccessCountEle.val())) {
            layer.tips('数量只能是数字', customerSuccessCountEle);
            flag = false;
            return false;
        }
        var customerUnitPriceEle = customerEle.find('input[name=unitPrice]');
        if (!$.isNumeric(customerUnitPriceEle.val())) {
            layer.tips('单价只能是数字', customerUnitPriceEle);
            flag = false;
            return false;
        }
        var customerAmountEle = customerEle.find('input[name=amount]');
        if (!$.isNumeric(customerAmountEle.val())) {
            layer.tips('金额只能是数字', customerAmountEle);
            flag = false;
            return false;
        }

        var checkedEle = $(itemEle).find('div[data-type=checked]');
        var checkedSuccessCountEle = checkedEle.find('input[name=successCount]');
        if (!$.isNumeric(checkedSuccessCountEle.val())) {
            layer.tips('数量只能是数字', checkedSuccessCountEle);
            flag = false;
            return false;
        }
        checkedTotal.successCount = checkedTotal.successCount + parseInt(checkedSuccessCountEle.val());
        var checkedUnitPriceEle = checkedEle.find('input[name=unitPrice]');
        if (!$.isNumeric(checkedUnitPriceEle.val())) {
            layer.tips('单价只能是数字', checkedUnitPriceEle);
            flag = false;
            return false;
        }
        var checkedAmountEle = checkedEle.find('input[name=amount]');
        if (!$.isNumeric(checkedAmountEle.val())) {
            layer.tips('金额只能是数字', checkedAmountEle);
            flag = false;
            return false;
        }
        checkedTotal.amount = accAdd(checkedTotal.amount, parseFloat(checkedAmountEle.val()));
    });
    // 输入数据不正确，置为0
    if (!flag) {
        platformTotal = {
            'successCount': 0,
            'amount': 0.0
        };
        checkedTotal = {
            'successCount': 0,
            'amount': 0.0
        };
    }
    var billTotalEle = billsEle.find('div.unchecked-bill-total');

    var platformTotalEle = $(billTotalEle).find('div[data-type=platform]');
    platformTotalEle.find('input[name=successCount]').val(thousand(platformTotal.successCount));
    platformTotalEle.find('input[name=amount]').val(thousand(platformTotal.amount.toFixed(2)));

    var checkedTotalEle = $(billTotalEle).find('div[data-type=checked]');
    checkedTotalEle.find('input[name=successCount]').val(thousand(checkedTotal.successCount));
    checkedTotalEle.find('input[name=amount]').val(thousand(checkedTotal.amount.toFixed(2)));

    // 每次重新计算总计之后，重新绑定下载、预览按钮的事件
    var billFileEle = billsEle.find('div.unchecked-bill-file');
    billFileEle.find('button[name=downloadFile]').attr('onclick', "downloadCheckBillFile(this, \"" + flowId + "\")");
    billFileEle.find('button[name=previewFile]').attr('onclick', "previewCheckBillFile(this, \"" + flowId + "\")");
    delete file_result['billFile_' + flowId];

    var analysisFileEle = billsEle.find('div.bill-analysis-file');
    analysisFileEle.find('button[name=downloadFile]').attr('onclick', "downloadDataAnalysisFile(this, \"" + flowId + "\")");
    analysisFileEle.find('button[name=previewFile]').attr('onclick', "previewDataAnalysisFile(this, \"" + flowId + "\")");
    delete file_result['analysisFile_' + flowId];
}

// 格式化数字(默认保留两位小数)
function format_num(num, fix_size) {
    // 保留小数长度(设置保留位数长度)
    if (!isBlank(fix_size) && $.isNumeric(fix_size) && parseInt(fix_size) >= 0) {
        fix_size = parseInt(fix_size);
    } else {
        fix_size = 0
    }
    if (isBlank(num)) {
        num = 0;
        return num.toFixed(fix_size);
    }
    // 转换成字符串
    num = parseFloat(num).toFixed(fix_size) + "";
    var dot_num = "";
    var num_int = 0;
    if (num.indexOf(".") > 0) {
        dot_num = num.substring(num.indexOf("."), num.length);
        num_int = num.substring(0, num.indexOf("."));
    }
    if (parseInt(num_int) === 0) {
        if (fix_size > 0) {
            return num_int + dot_num;
        } else {
            return num_int;
        }
    }
    var front = "";
    if (parseInt(num_int) < 0) {
        front = "-";
    }
    // 取绝对值 开始添加分隔号
    num_int = Math.abs(num_int) + "";
    // 分割成数组
    var num_int_arr = num_int.split("");
    var num_int_resu = [];
    var rec_count = 1;
    for (var num_int_index = (num_int_arr.length - 1); num_int_index >= 0; num_int_index--) {
        num_int_resu.push(num_int_arr[num_int_index]);
        if (rec_count % 3 === 0 && num_int_index !== 0) {
            num_int_resu.push(",");
        }
        rec_count++;
    }
    num_int = num_int_resu.reverse().join("");
    if (fix_size > 0) {
        return front + num_int + dot_num;
    } else {
        return front + num_int;
    }
}

function guid() {
    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}