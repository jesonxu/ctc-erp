// 不同流程的标签名称
var product_bill_label_name = {
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
    }
};

// 标签名称
var product_bill_input_name = {
    //付款流程
    "[BillPaymentFlow]": {
        payables_name: "payables",
        actualpayables_name: "actualpayables",
        left_should_pay_name: "left_should_pay",
        thisPayment_name: "thisPayment"
    },

    // 收款流程
    "[RemunerationFlow]": {
        payables_name: "receivables",
        actualpayables_name: "actualReceivables",
        left_should_pay_name: "left_should_receive",
        thisPayment_name: "thisReceivables"
    },

    //销售收款流程
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
    }
};
var laydate;
var layer;
var layedit;
var upload;
var element;
var form;
var dropdown;
/*layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    dropdown: 'dropdown'
});*/

/**
 * 渲染对话框式的流程处理记录
 * #flowMsg_flowEndId   本流程的最外层div
 * ├─#flowLogs          处理记录div
 * │ ├─#record0         一个节点的处理记录
 * │ └─#record1         一个节点的处理记录
 * └─#flowOperate       流程处理div
 *   ├─#flowLabels      可编辑标签div
 *   └─#flowAudit       审核div，意见+按钮
 *
 * @param ele   流程标题所在元素
 * @param data  流程详细信息
 */
function renderFlowMsg(ele, data) {
/*    console.log("-----------------------流程数据-开始---------------------------------------");
    console.log("加载的流程数据：" + JSON.stringify(data));
    console.log("-----------------------流程数据-结束---------------------------------------");*/
    // 重复点击关闭流程记录
    var nextEle = $(ele).next("#flowMsg_" + data.flowEntId);
    if (nextEle.length !== 0) {
        $(nextEle).remove();
        return;
    }

    // 最外层框
    var html = "<div id='flowMsg_" + data.flowEntId + "'>";
    // 流程详细信息
    html += "<xmp hidden name='flowData' >" + JSON.stringify(data) + "</xmp>";

    // 显示流程处理记录
    var records = data.record;
    if (records != null && records.length > 0) {
        html += "<div id='flowLogs'>";
        // 流程的状态
        /*var flowState = data.flowStatus;
        var flowStateEndDom = "";
        if (isNotBlank(flowState)) {
            if (flowState === 1) {
                // 流程归档
                flowStateEndDom = "<span class='flow-result-normal'>流程结束</span>";
            } else if (flowState === 3) {
                //流程取消
                flowStateEndDom = "<span class='flow-result-cancel'>流程结束</span>";
            }
        }*/
        $.each(records, function (recordIndex, item) {
/*
            console.log("-------第："+recordIndex+"项-内容-开始---------------------------------------------------");
            console.log(JSON.stringify(item));
            console.log("-------第："+recordIndex+"项-内容-结束---------------------------------------------------");
*/
            var dealPerson = item.dealPerson ? item.dealPerson : "未知";
            var dealRole = item.dealRole;
            var dealTime = item.dealTime;
            var auditResult = item.auditResult;
            var auditResultSpan;
            if (auditResult === '创建') {
                auditResultSpan = "<span class='audit-result' style='color: #1E9FFF;'>创建流程</span>";
            } else if (auditResult === '通过') {
                auditResultSpan = "<span class='audit-result' style='color: #5FB878;'>通过流程</span>";
            } else if (auditResult === '驳回') {
                auditResultSpan = "<span class='audit-result' style='color: #FF5722;'>驳回流程</span>";
            } else if (auditResult === '取消') {
                auditResultSpan = "<span class='audit-result' style='color: #FFB800;'>取消流程</span>";
            } else if (auditResult === '保存') {
                auditResultSpan = "<span class='audit-result' style='color: #01AAED;'>保存流程</span>";
            }
            /*var last = (records.length - 1) === recordIndex;
            if (isNotBlank(flowStateEndDom) && isNotBlank(auditResultSpan) && last) {
                auditResultSpan += flowStateEndDom;
            }*/
            var remark = item.remark;
            html += "<div id='record" + recordIndex + "' class='layui-input-block' style='text-align: left; padding-bottom: 5px; margin: 0 10px 0 10px;'>";
            // 标题：处理人+处理时间+处理结果
            html += "<div class='recordTitle layui-input-inline'>";
            html += "<span>" + dateToWeek(dealTime) + "</span>&nbsp;&nbsp;<span>" + dealPerson + "[" + dealRole + "]</span>&nbsp;&nbsp;" + auditResultSpan;
            html += "</div>";

            if (auditResult !== '创建') {
                // 内容：处理意见+修改内容
                html += "<div class='recordContent' style='width: fit-content; width: -moz-fit-content; width: -webkit-fit-content; padding: 10px; background: whitesmoke; border: #e0e0e0; border-radius:7px;'>";
                html += "<span>处理意见：" + remark + "</span><br/>";
                html += "</div>"; // recordContent
            }

            html += "</div>"; // record
        });
        html += "</div>" // flowLogs 流程处理记录
    }

    // 当前节点待处理的人名/角色名，发起人节点显示人名，非发起人节点显示角色名
    var dealPerson = "";
    if (!isBlank(data.dealRoleName)) {
        dealPerson = data.dealRoleName;
    }
    if (!isBlank(data.dealUserName)) {
        dealPerson = data.dealUserName;
    }
    // 流程
    html += "<div id='flowOperate' class='layui-input-block' style='text-align: left; margin: 0 10px 0 10px; min-height: 24px'>";
    // 流程待处理，没有人名/角色名说明流程已经走完
    if (dealPerson !== "") {   // 标题：待处理人名/角色名+等待处理
        html += "<div class='recordTitle layui-input-inline'>";
        html += "<span>" + dealPerson + "</span>&nbsp;&nbsp;<span style='color: red'>等待处理</span>";
        html += "</div>";
        // 当前用户可以审核时，显示内容：可编辑标签+审核区
    }
    if (data.canOperat) {
        var baseData = data.baseDataMap;
        var editLabelIds = data.editLabelIds;
        // 去配货
        html += "<div class='layui-form' id='flowAudit' style='padding: 5px 5px 0 5px;'>";
        // 以下是一排的审核操作按钮
        html += "<div class='layui-form-item' style='text-align: right;padding-top: 5px;'>";

        var buttonName = "去配货"
        html += "<button type='button' class='layui-btn layui-btn-danger layui-btn-sm' onclick='matchOrder(1, \"" + data.flowEntId + "\")'>" + buttonName + "</button>";

        html += "</div>"; // layui-form-item

        html += "</div>"; // layui-form flowAudit 审核框
    } else {
        html += "<div class='layui-form' id='flowAudit' style='padding: 5px 5px 0 5px;'>";
        // 以下是一排的审核操作按钮
        html += "<div class='layui-form-item' style='text-align: right;padding-top: 5px;'>";

       var buttonName = "查看详情"
                   html += "<button type='button' class='layui-btn layui-btn-sm' onclick='matchOrder(2, \"" + data.flowEntId + "\")'>" + buttonName + "</button>";

        html += "</div>"; // layui-form-item

        html += "</div>"; // layui-form flowAudit 审核框

    }
    html += "</div>"; // flowOperate 流程处理
    html += "</div>"; // flowMsg 最外层框

    // 清除本流程以前的框
    $("#flowMsg_" + data.flowEntId).remove();
    $(ele).after(html);

    // 当前用户可以处理时，才渲染可编辑标签
    if (data.canOperat) {
        init_layui(data);
    }
}

// 将标签值转换成文字
function labelValueToString(labelType, labelValue, defaultValue, flowEntId, flowClass, flowId, index) {
    if (isBlank(labelType) || isBlank(labelValue)) {
        return "";
    }
    var type = parseInt(labelType);
    if (type === 0) {
        // 字符串
    	if (flowClass == '[BillWriteOffFlow]' && (index == 1 || index == 2)) { // 账单 和 付款信息
    		if (index == 1) {
    			return typeStringToBillsInfoString(labelValue);
    		} else {
    			return typeStringToIncomeInfoString(labelValue);
    		}
    	}
        return typeStringToString(labelValue, defaultValue);
    } else if (type === 1) {
        // 整数
        return typeIntToString(labelValue, defaultValue);
    } else if (type === 2) {
        // 小数
        return typeFloatToString(labelValue, defaultValue);
    } else if (type === 3) {
        // 布尔类型(有默认值)
        return typeBoolToString(labelValue, defaultValue);
    } else if (type === 4) {
        // 日期类型 yyyy-MM-dd
        return typeDateToString(labelValue, defaultValue);
    } else if (type === 5) {
        // 时间 日期类型 yyyy-MM-dd HH:ss:mm
        return typeDateToString(labelValue, defaultValue);
    } else if (type === 6) {
        // 月份类型 yyyy-MM
        return typeDateToString(labelValue, defaultValue);
    } else if (type === 7) {
        // 下拉框类型
        return typeSelectToString(labelValue, defaultValue);
    } else if (type === 8) {
        // 文件类型
        return typeFileToString(labelValue, defaultValue);
    } else if (type === 9) {
        // 文本 类型
        return typeTextareaToString(labelValue, defaultValue);
    } else if (type === 10) {
        // 价格梯度类型
        return typeGradientToString(labelValue, defaultValue);
    } else if (type === 11) {
        //价格类型
        return typePriceTypeToString(labelValue, defaultValue);
    } else if (type === 12) {
        //充值类型(暂时视为下拉框类型处理)
        return typeChargeTypeToString(labelValue, defaultValue);
    } else if (type === 13) {
        // 酬金信息 金额*酬金比例+奖励-扣款
        return typtRemunerationToString(labelValue, defaultValue);
    } else if (type === 14) {
        // 账单展示
        return typeBillToString(labelValue, defaultValue, flowEntId, flowClass, flowId);
    } else if (type === 15) {
        // 账单金额标签
        return typeBillMoneyToString(labelValue, defaultValue);
    } else if (type === 17) {
        // 我司开票信息
        return typeInvoiceInfoToString(labelValue, defaultValue);
    } else if (type === 18) {
        // 对方开票信息
        return typeInvoiceInfoToString(labelValue, defaultValue);
    } else if (type === 19) {
        // 我司银行信息
        return typeBankInfoToString(labelValue, defaultValue);
    } else if (type === 20) {
        // 对方银行信息
        return typeBankInfoToString(labelValue, defaultValue);
    } else if (type === 21) {
        // 合同编号
        return typeStringToString(labelValue, defaultValue);
    } else if (type === 22) {
        // 历史单价
        return typeHistoryPriceToString(labelValue, defaultValue);
    } else if (type === 23) {
        // 发票信息
        return typeInvoiceToString(labelValue, defaultValue, flowEntId, flowClass, flowId)
    }
}

// 初始化layui和渲染标签
function init_layui(data) {
    layui.use(['form', 'layedit', 'laydate', 'element', 'upload', 'dropdown'], function () {
        form = layui.form;
        layedit = layui.layedit;
        laydate = layui.laydate;
        upload = layui.upload;
        element = layui.element;
        layer = layui.layer;
        dropdown = layui.dropdown;
        // 渲染标签
        init_form(data);
    });
}

// 渲染标签
function init_form(data) {
    // 本流程最外层框
    var flowMsgEle = $("#flowMsg_" + data.flowEntId);
    var entity_id = data.supplierId;
    if (!isBlank(data.labelList)) { // 本节点的所有标签
        // 获取初始化的占位标签
        var input_eles = $(flowMsgEle).find("input[data-type= '0']");
        input_eles.each(function (input_index, input_ele) {
            // 默认值
            var default_value = $(input_ele).val();
            // 标签名
            var label_name = $(input_ele).attr("name");
            // 标签的id
            var label_id = $(input_ele).attr("label-id");
            // 标签类型
            var label_type = $(input_ele).attr("label-type");
            // 是否为必须填写的内容
            if (!isBlank(data.mustLabelIds) && data.mustLabelIds.indexOf(label_id) !== -1) {
                $(input_ele).attr("input-required", "true");
                // 对应label前加红*
                var label_label = $(flowMsgEle).find("label[data-label-name= '" + label_name + "']");
                label_label.prepend("<span style='color: red;'>*</span>")
            } else {
                $(input_ele).attr("input-required", "false");
            }
            // 是否可以编辑
            if (!isBlank(data.editLabelIds) && data.editLabelIds.indexOf(label_id) !== -1) {
                $(input_ele).removeAttr("disabled");
            	// 根据标签类型，渲染显示的样式
            	set_show_label_type(data, "flowMsg_" + data.flowEntId, input_ele, default_value, label_name, label_id, label_type, entity_id);
            }
        });
    }
    form.render();
    // element.render();
    dropdown.render();
}

// 根据标签类型，渲染显示的样式
function set_show_label_type(data, ele_id, input_ele, default_value, label_name, label_id, label_type, entity_id) {
    // 设置提示内容
    $(input_ele).attr("placeholder", '请输入' + label_name);
    // 父div
    var parent = $(input_ele).parent();
    // 是否为必须要
    var required = $(input_ele).attr("input-required");
    required = !isBlank(required) && (required === true || required === "true");
    // 通过标签名获取实际的值
    var label_value = get_label_value(label_name, data);
    $(input_ele).val(label_value);
    // 是否可用
    var disabled = $(input_ele).is(":disabled") ? " disabled " : "";
    // 设置标识类型
    $(input_ele).attr("value-type", label_type);

    if (label_type === 0 || label_type === "0") {
        // 字符串
        take_string_input(input_ele, required);
    } else if (label_type === 1 || label_type === "1") {
        // 整数
        take_int_input(input_ele, required);
    } else if (label_type === 2 || label_type === "2") {
        // 小数
        take_float_input(input_ele, required);
    } else if (label_type === 3 || label_type === "3") {
        // 布尔类型(有默认值)
        take_boolean_input(parent, label_name, label_value, disabled, required);
    } else if (label_type === 4 || label_type === "4") {
        // 日期类型 yyyy-MM-dd
        take_time_input(label_type, 'date', ele_id)
    } else if (label_type === 5 || label_type === "5") {
        // 时间 日期类型 yyyy-MM-dd HH:ss:mm
        take_time_input(label_type, 'datetime', ele_id)
    } else if (label_type === 6 || label_type === "6") {
        // 月份类型 yyyy-MM
        take_time_input(label_type, 'month', ele_id)
    } else if (label_type === 7 || label_type === "7") {
        // 下拉框类型
        take_select_input(parent, label_name, label_value, default_value, disabled, required)
    } else if (label_type === 8 || label_type === "8") {
        // 文件类型
        take_file_input(parent, label_name, label_value, default_value, disabled, required, ele_id); // ele_id是流程最外层框的id（flowMsg_ + flowEntId）
    } else if (label_type === 9 || label_type === "9") {
        // 文本 类型
        take_textarea_input(parent, label_name, label_value, required, disabled);
    } else if (label_type === 10 || label_type === "10") {
        // 价格梯度类型
        take_gradient_input(parent, input_ele, label_id, label_name, label_type, default_value, label_value, required, disabled);
    } else if (label_type === 11 || label_type === "11") {
        //价格类型
        take_price_type_input(parent, input_ele, label_id, label_name, label_type, default_value, label_value, required, disabled, ele_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            // 改变价格类型
            gradient_type_change(parent, res, data);
        });
    } else if (label_type === 12 || label_type === "12") {
        //充值类型(暂时视为下拉框类型处理)
        take_charge_type_input(parent, label_name, label_value, default_value, required, disabled)
    } else if (label_type === 13 || label_type === "13") {
        // 酬金信息 金额*酬金比例+奖励-扣款
        take_remuneration_info(parent, label_name, label_value, default_value, required, disabled);
    } else if (label_type === 14 || label_type === "14") {
        // 账单展示
        take_product_bill(parent, label_name, label_value, default_value, required, disabled, data)
    } else if (label_type === 15 || label_type === "15") {
        // 账单金额标签
        take_bill_money_input(parent, label_name, label_value, default_value, required, disabled);
    } else if (label_type === 17 || label_type === "17") {
        // 我司开票信息
        take_invoice_select(parent, label_name, label_value, default_value, required, disabled, label_type, 0, ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.invoice').remove();
            $(res.othis).parents('.layui-form-item').append(write_html_invoice(res.value, 'selfInvoice'));
        });
    } else if (label_type === 18 || label_type === "18") {
        // 对方开票信息
        take_invoice_select(parent, label_name, label_value, default_value, required, disabled, label_type, 1, ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.invoice').remove();
            $(res.othis).parents('.layui-form-item').append(write_html_invoice(res.value, 'otherInvoice'));
        });
    } else if (label_type === 19 || label_type === "19") {
        // 我司银行信息
        take_invoice_select(parent, label_name, label_value, default_value, required, disabled, label_type, 2, ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.bank-account').remove();
            $(res.othis).parents('.layui-form-item').append(write_html_bank_account(res.value, 'selfBank'));
        });
    } else if (label_type === 20 || label_type === "20") {
        // 对方银行信息
        take_invoice_select(parent, label_name, label_value, default_value, required, disabled, label_type, 3, ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.bank-account').remove();
            $(res.othis).parents('.layui-form-item').append(write_html_bank_account(res.value, 'otherBank'));
        });
    } else if (label_type === 21 || label_type === "21") {
        // 合同编号
        take_contract_input(parent, label_name, label_value, required, disabled);
    } else if (label_type === 22 || label_type === "22") {
        // 历史单价
        take_string_input(input_ele, required);
    } else if (label_type === 23 || label_type === "23") {
        // 发票信息
        take_invoice_info(parent, label_name, label_value, default_value, required, disabled, data);
    }
}

// 展示详细的开票信息
function write_html_invoice(value, id) {
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

// 展示详细的银行信息
function write_html_bank_account(value, id) {
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

// 加载开票信息和银行信息下拉框
function take_invoice_select(parent, lable_name, label_value, default_value, required, disabled, label_type, type, ele_id, entity_id) {
    // type: 0我司开票，1对方开票，2我司银行，3对方银行
    // 下拉框类型
    var select_dom = "<select data-type='0' value-type='" + label_type + "' name='" + lable_name + "' input-required='" + required + "' " + disabled + "' lay-filter='" + (ele_id + lable_name) + "'>";
    select_dom = select_dom + "<option value=''>" + "请选择" + lable_name + "</option>";
    var data = {
        type: type,
        supplierId: entity_id
    };
    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/getInvoice.action",
        dataType: 'json',
        data: data,
        success: function (data) {
            if (data.code == 200) {
                $.each(data.data, function (index, item) {
                    select_dom = select_dom + "<option value='" + item.value + "' title='" + item.title + "' " + (label_value == item.value ? "selected" : "") + ">" + item.text + "</option>";
                });
            }
        }
    });
    select_dom = select_dom + "</select>";
    parent.html(select_dom);
    if (type === 0) {
        $(parent).parents('.layui-form-item').find('div.invoice').remove();
        $(parent).parents('.layui-form-item').append(write_html_invoice(label_value, 'selfInvoice'));
    } else if (type === 1) {
        $(parent).parents('.layui-form-item').find('div.invoice').remove();
        $(parent).parents('.layui-form-item').append(write_html_invoice(label_value, 'otherInvoice'));
    } else if (type === 2) {
        $(parent).parents('.layui-form-item').find('div.bank-account').remove();
        $(parent).parents('.layui-form-item').append(write_html_bank_account(label_value, 'selfBank'));
    } else if (type === 3) {
        $(parent).parents('.layui-form-item').find('div.bank-account').remove();
        $(parent).parents('.layui-form-item').append(write_html_bank_account(label_value, 'otherBank'));
    }
}

// 开票信息
function typeInvoiceInfoToString(labelValue, defaultValue) {
    if (isBlank(labelValue)) {
        return "无";
    }
    var json = {};
    var array = labelValue.split('####');
    for (var i = 0; i < array.length; i++) {
        json[array[i].split(':')[0]] = array[i].split(':')[1];
    }
    html = "<br/>公司名称：" + (json.companyName ? json.companyName : "");
    html += "<br/>税务号：" + (json.taxNumber ? json.taxNumber : "");
    html += "<br/>公司地址：" + (json.companyAddress ? json.companyAddress : "");
    html += "<br/>联系电话：" + (json.phone ? json.phone : "");
    html += "<br/>开户银行：" + (json.accountBank ? json.accountBank : "");
    html += "<br/>银行账号：" + (json.bankAccount ? json.bankAccount : "");
    return html;
}

// 银行信息
function typeBankInfoToString(labelValue, defaultValue) {
    if (isBlank(labelValue)) {
        return "无";
    }
    var json = {};
    var array = labelValue.split('####');
    for (var i = 0; i < array.length; i++) {
        json[array[i].split(':')[0]] = array[i].split(':')[1];
    }
    var html = "<br/>名称：" + (json.accountName ? json.accountName : "");
    html += "<br/>开户银行：" + (json.accountBank ? json.accountBank : "");
    html += "<br/>银行账号：" + (json.bankAccount ? json.bankAccount : "");
    return html;
}

// 处理字符串类型输入框
function take_string_input(input_ele, requred) {
    // 字符串
    $(input_ele).blur(function (e) {
        var value = $(this).val();
        if (requred && isBlank(value)) {
            layer.tips('不能为空', this);
        }
    });
}

// 字符串类型
function typeStringToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? defaultValue : labelValue;
}

// 处理整数输入框
function take_int_input(input_ele, requred) {
    // 整数
    $(input_ele).blur(function (e) {
        var value = $(this).val();
        if (requred && isBlank(value)) {
            layer.tips('不能为空', this);
        }
        if (!isBlank(value) && !isInt(value)) {
            layer.tips('请输入整数', this);
            $(this).val("");
            return false;
        }
    });
}

// 整数类型
function typeIntToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? defaultValue : labelValue;
}

// 处理浮点数输入框
function take_float_input(input_ele, required) {
    // 小数（对应数字）
    $(input_ele).blur(function (e) {
        var value = $(this).val();
        if (required && isBlank(value)) {
            layer.tips('不能为空', this);
        }
        if (isBlank(value) || !$.isNumeric(value)) {
            layer.tips('请输入数字', this);
            $(this).val("");
            return false;
        }
    });
}

// 浮点数类型
function typeFloatToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? defaultValue : labelValue;
}

// 历史单价
function typeHistoryPriceToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? defaultValue : labelValue;
}

// 处理boolean 类型输入框
function take_boolean_input(parent, lable_name, label_value, disabled, required) {
    // 布尔类型(有默认值)
    if (!isBlank(label_value) && label_value === '1') {
        $(parent).html("<input type='radio' name='" + lable_name +
            "' value-type='3' data-type='3'" + disabled +
            " input-required='" + required + "' checked value='1' title='是'/>" +
            "<input type='radio' name='" + lable_name +
            "' value-type='3' data-type='3' " + disabled +
            " input-required='" + required + "' value='0' title='否'>");
    } else {
        $(parent).html("<input type='radio' name='" + lable_name +
            "' value-type='3' data-type='3' " + disabled +
            "  input-required='" + required + "' value='1' title='是'/>" +
            "<input type='radio' name='" + lable_name + "' value-type='3' data-type='3' " +
            disabled + " input-required='" + required + "' checked value='0' title='否'>");
    }
}

// 布尔类型（是否）
function typeBoolToString(labelValue, defaultValue) {
    var value = isBlank(labelValue) ? defaultValue : labelValue;
    if (value === 1 || value === '1') {
        return "是";
    } else if (value === 0 || value === '0') {
        return "否";
    } else {
        return "未选择";
    }
}

// 处理时间输入框
function take_time_input(lable_type, type, ele_id) {
    $("#" + ele_id + " input[value-type='" + lable_type + "']").each(function () {
        laydate.render({
            elem: this,
            type: type,
            trigger: 'click' //采用click弹出
        });
    });
}

// 日期类型
function typeDateToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? defaultValue : labelValue;
}

// 设置处理选择输入框
function take_select_input(parent, lable_name, label_value, default_value, disabled, required) {
    var select_dom = "<select data-type='0' value-type='7' input-required='" + required + "' name='" + lable_name + "' " + disabled + " >";
    select_dom = select_dom + "<option value=''>" + "请选择" + lable_name + "</option>";
    // 下拉框类型，默认值即是选项
    if (!isBlank(default_value)) {
        var options = default_value.split(",");
        for (var option_index = 0; option_index < options.length; option_index++) {
            var value = options[option_index];
            if (!isBlank(label_value) && label_value === value) {
                select_dom = select_dom + "<option value='" + value + "' selected>" + value + "</option>";
            } else {
                select_dom = select_dom + "<option value='" + value + "'>" + value + "</option>";
            }
        }
    }
    select_dom = select_dom + "</select>";
    parent.html(select_dom);
}

// 下拉框类型
function typeSelectToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? "未选择" : labelValue;
}

// 充值类型展示
function take_charge_type_input(parent, lable_name, label_value, default_value, requred, disabled) {
    // 下拉框类型
    var select_dom = "<select data-type='0' value-type='7' input-required='" + requred + "' name='" + lable_name + "' " + disabled + " >";
    select_dom = select_dom + "<option value=''>" + "请选择" + lable_name + "</option>";
    if (!isBlank(default_value)) {
        var options = default_value.split(",");
        for (var option_index = 0; option_index < options.length; option_index++) {
            var value = options[option_index];
            var value_info = value.split(":");
            if (!isBlank(label_value) && label_value == value_info[0]) {
                select_dom = select_dom + "<option value='" + value_info[0] + "' selected>" + value_info[1] + "</option>";
            } else {
                select_dom = select_dom + "<option value='" + value_info[0] + "'>" + value_info[1] + "</option>";
            }
        }
    }
    select_dom = select_dom + "</select>";
    parent.html(select_dom);
}

// 充值类型展示
function typeChargeTypeToString(labelValue, defaultValue) {
    if (!isBlank(labelValue)) {
        var chargeype = defaultValue.split(",");
        for (var i = 0; i < chargeype.length; i++) {
            var type = chargeype[i].split(":");
            if (labelValue == type[0]) {
                return type[1];
            }
        }
        return "未知类型";
    } else {
        return "未选择";
    }
}

// 处理文件类型
function take_file_input(parent, lable_name, label_value, default_value, disabled, required, ele_id) {
    // 文件类型
    var btn = "<button data-type='0' value-type='8' type='button' " + disabled +
        "input-required='" + required + "' class='layui-btn layui-btn-sm' name='" + lable_name + "'>选择文件</button>" + "<span>(总大小不超过100M)</span>" +
        "<div class='layui-upload-list'>" +
        "    <table class='layui-table'>" +
        "      <thead>" +
        "        <tr><th style='width: 165px;'>文件名</th>" +
        "        <th>状态</th>" +
        "        <th>操作</th>" +
        "      </tr></thead>" +
        "      <tbody data-file-name = '" + lable_name + "'></tbody>" +
        "    </table>" +
        "</div> ";
    $(parent).html(btn);
    // 回显原来已经有的文件
    show_uploaded_file(lable_name, label_value, disabled, ele_id);
    // 文件标签的上传功能
    init_file_load(lable_name, default_value, ele_id);
}

// 文件类型
function typeFileToString(labelValue, defaultValue) {
    var fileArray = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
    if (fileArray.length === 0) {
        return "无";
    }
    var html = "(点击下载)<br/>";
    $.each(fileArray, function (i, file) {
        var fileJson = JSON.stringify(file);
        html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='down_load(" + fileJson + ")'>" + file.fileName + "</a>&nbsp;&nbsp;";
    });
    return html;
}

// 处理文本框类型
function take_textarea_input(parent, lable_name, label_value, required, disabled) {
    // 文本 类型
    $(parent).html("<textarea data-type='0' value-type='9' " + disabled +
        "input-required='" + required + "' name='" + lable_name +
        "' placeholder='请输入内容' class='layui-textarea' maxlength='1500'>" + label_value + "</textarea>");
}


// 文本框类型
function typeTextareaToString(labelValue, defaultValue) {
    var html = "<span style='word-break: break-all'>";
    html += isBlank(labelValue) ? defaultValue : labelValue + "</span>";
    return html;
}

// 处理价格梯度类型
function take_gradient_input(parent, input_ele, label_id, lable_name, label_type, default_value, label_value, requred, disabled) {
    if (label_value.indexOf("gradient") !== -1) {
        // 处理梯度价格
        take_gradient_price(parent, label_value, disabled);
    } else {
        // 处理统一价格
        take_uniform_price(parent, label_value, disabled);
    }
    // 处理语音单位
    take_voice_unit(parent, label_value, disabled);
    form.render();
    // element.render();
    $(parent).parent().remove();
}

// 处理价格梯度类型
function typeGradientToString(labelValue, defaultValue) {
    var html = "";
    if (labelValue.indexOf("gradient") !== -1) {
        // 处理梯度价格
        if (!isBlank(labelValue) && labelValue !== {}) {
            var gradient_price = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
            // 排序 防止数据循序不对
            gradient_price = gradient_price.sort(function (a, b) {
                if (a.gradient < b.gradient) {
                    return -1;
                } else if (a.gradient > b.gradient) {
                    return 1;
                } else {
                    return 0;
                }
            });
            // 循环处理梯度价格
            $.each(gradient_price, function (i, gradient_info) {
                if (isBlank(gradient_info)) {
                    gradient_info = {};
                }
                var is_default = gradient_info.isdefault;
                var min_send_count = isBlank(gradient_info.minsend) ? "空" : gradient_info.minsend;
                var max_send_count = isBlank(gradient_info.maxsend) ? "空" : gradient_info.maxsend;
                var price = isBlank(gradient_info.price) ? "未输入" : gradient_info.price;
                var millions_ratio = isBlank(gradient_info.complaintrate) ? "空" : gradient_info.complaintrate;
                var province_ratio = isBlank(gradient_info.provinceproportion) ? "空" : gradient_info.provinceproportion;
                html += "<br/>" + min_send_count + "条 <= 发送量 < " + max_send_count + "条，价格：" + price;
                html += "，百万投比：" + millions_ratio + "，省占比：" + province_ratio + "%";
                html += !isBlank(is_default) && (is_default === 1 || is_default === "1") ? "(默认)" : "";
            });
        } else {
            html += "无";
        }
    } else {
        // 处理统一价格
        var price = "";
        var provinceprice = "";
        if (!isBlank(labelValue) && labelValue !== {}) {
            // 只会存在一个
            var uniform_price = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
            if (!isBlank(uniform_price) && uniform_price.length > 0) {
                price = uniform_price[0].price;
                provinceprice = uniform_price[0].provinceprice;
            }
        }
        html += "价格：" + price + "，省网价格：" + provinceprice;
    }
    return html;
}

// 处理价格类型
function take_price_type_input(parent, input_ele, label_id, lable_name, label_type, default_value, label_value, required, disabled, ele_id) {
    // 价格类型选择框的filter前加上流程div的id，保证filter唯一
    var gradient_dom = "<select data-type='0' value-type='10' " + disabled + "input-required='" + required + "' name='" + lable_name + "' lay-filter='" + (ele_id + lable_name);
    gradient_dom += "' data-default-gradient='" + label_value + "' data-before-gradient='" + label_value + "'>"; // 默认价格类型，以及选项变更前的价格类型
    if (!isBlank(default_value)) {
        // 梯度价格的选项
        var gradient_options = default_value.split(",");
        for (var gradient_index = 0; gradient_index < gradient_options.length; gradient_index++) {
            var value = gradient_options[gradient_index];
            var item_info = value.split(":");
            if (!isBlank(label_value) && label_value === item_info[0]) {
                gradient_dom = gradient_dom + "<option value='" + item_info[0] + "' selected>" + item_info[1] + "</option>";
            } else {
                gradient_dom = gradient_dom + "<option value='" + item_info[0] + "'>" + item_info[1] + "</option>";
            }
        }
    }
    gradient_dom = gradient_dom + "</select>";
    parent.html(gradient_dom);
}

// 处理价格类型
function typePriceTypeToString(labelValue, defaultValue) {
    if (!isBlank(labelValue)) {
        // 梯度价格的选项
        var priceType = defaultValue.split(",");
        for (var i = 0; i < priceType.length; i++) {
            var type = priceType[i].split(":");
            if (labelValue == type[0]) {
                return type[1];
            }
        }
        return "未知类型";
    } else {
        return "未选择";
    }
}

// 处理合同编号类型
function take_contract_input(parent, lable_name, label_value, required, disabled) {
    // 文本 类型
    $(parent).html("<input data-type='0' value-type='21' disabled name='" + lable_name +
        "' placeholder='流程自动生成，不需要填写' class='layui-input' value='" + label_value + "'/>");
}

var flowIncomeInput, flowBillsInput;
function flowContactsInfo(table) {
	var incomeSum = parseFloat($('.bill_write_off_sum_pay').html());
	var incomeSel = table.checkStatus('writeOffIncomes').data;
	var incomesInfo = [];
	if (incomeSel && incomeSel.length > 0){
		$(incomeSel).each(function (index, income) {
			incomeSum = incomeSum - income.remainRelatedCost;
			if (-incomeSum >= income.remainRelatedCost) {
				return;
			}
			incomesInfo.push({
				fsexpenseincomeid: income.id,
				cost: income.cost,
				operatetime: income.operateTime,
				banckcustomername: income.depict,
				thiscost: incomeSum >= 0 ? income.remainRelatedCost : (income.remainRelatedCost + incomeSum)
			});
		});
	}
	flowIncomeInput.val(incomesInfo.length > 0 ? JSON.stringify(incomesInfo) : '');
	var billsSel = table.checkStatus('writeOffBills').data;
	var billsInfo = [];
	if (billsSel && billsSel.length > 0){
		billsInfo.push({isHandApplay: "T"});
		$(billsSel).each(function (index, bills) {
			billsInfo.push({
				productbillsid: bills.id,
				title: bills.title,
				receivables: bills.receivables,
				thiscost: bills.receivables - bills.actualReceivables
			});
		});
	}
	flowBillsInput.val(billsInfo.length > 0 ? JSON.stringify(billsInfo) : '');
}

// 流水号 金额 账户  剩余
function flowCreateIncomesTable(htmlStr, label, productId, labelValueMap) {
	var selectIncomeIds = '';
	var tableData = [];
	htmlStr = htmlStr ? htmlStr : '';
	htmlStr += '<div class="layui-small-table">' +
					'<table id="writeOffIncomes" lay-filter="write-off-bills"></table>' +
					'<i class="bill_write_off_income_sum_tip" style="font-size: 18px;color: red;">收款合计：' +
						'<span class="bill_write_off_income_sum_pay" style="font-size: 18px;color: red;">0.00</span>&nbsp;元' +
					'</i>' +
					"<input type='hidden' class='layui-input' name='" + label.name + "' data-type='0' value-type='0' label-id=" + label.id + " label-type=" + label.type + " disabled='disabled' value='" + label.defaultValue + "'/>" +
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
					align : 'center',
					width : 150
				}, {
					field : 'cost',
					title : '收款金额',
					align : 'right'
				}, {
					field : 'remainRelatedCost',
					title : '剩余金额',
					align : 'right'
				} ]],
				parseData : function(res) {
					if (res.data && res.data.length > 0) {
						var incomeInfo = [];
						for (var item in labelValueMap){
							var val = labelValueMap[item];
							if (val != null && val.indexOf('[{') >=0 
									&& JSON.parse(val)[0] != null && JSON.parse(val)[0].fsexpenseincomeid) {
								$(JSON.parse(val)).each(function (index, income) {
									incomeInfo.push(income.fsexpenseincomeid);
								});
								break;
							}
						}
						selectIncomeIds = incomeInfo.join(',');
						tableData = res.data;
					}
					return {
						"code" : 0, 
						"count" : res.count,
						"data" : res.data
					};
				},
				where : {
					customer : customerId,
					productId : productId
				},
				done: function () {
					var bindClick = function () {
						flowIncomeInput = $('[label-id="' + label.id + '"]');
						var tips = $('.form-submit-tips');
						rowBindClick($('#writeOffIncomes').next());
						$('#writeOffIncomes').next().find('[data-field="checked"]').bind('click', function () {
							setTimeout(function () {
								var checkStatus = table.checkStatus('writeOffIncomes');
								var selectData = checkStatus.data;
								var sum = 0.00;
								if (selectData && selectData.length > 0){
									$(selectData).each(function (index, income) {
										sum += income.remainRelatedCost;
									});
								}
								$('.bill_write_off_income_sum_pay').html(sum.toFixed(2));
								if (parseFloat($('.bill_write_off_sum_pay').html()) > parseFloat($('.bill_write_off_income_sum_pay').html())) {
									tips.val('收款总计必须大于等于账单总计');
								} else {
									tips.val('');
								}
								flowContactsInfo(table);
							}, 10);
						});
					}
					bindClick();
					$('#writeOffIncomes').next().find('[data-field="id"]').each(function (index, item) {
						var id = $(this).find('div').text();
						if (id && selectIncomeIds.indexOf($(this).text()) >= 0) {
							if (tableData && tableData.length > 0) {
								var flag = true;
								$(tableData).each(function (index, item) {
									if (flag && id == item.id) {
										item['LAY_CHECKED'] = true;
										flag = false;
										return;
									}
								});
								triggerClick($(this).parent('tr'));
							}
						}
					});
				}
			});
		});
	}, 5);
	return htmlStr;
}

// 名称 应收 实收
function flowCreateBillsTable(htmlStr, label, productId, labelValueMap) {
	var selecBillsIds = '';
	var tableData = [];
	htmlStr = htmlStr ? htmlStr : '';
	htmlStr += '<div class="layui-small-table"  style="margin-bottom: 20px;">' +
					'<table id="writeOffBills" lay-filter="write-off-bills"></table>' +
					"<input type='hidden' class='layui-input' name='" + label.name + "' data-type='0' value-type='0' label-id=" + label.id + " label-type=" + label.type + " disabled='disabled' value='" + label.defaultValue + "'/>" +
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
					if (res.data && res.data.length > 0) {
						var billsInfo = [];
						for (var item in labelValueMap){
							var val = labelValueMap[item];
							if (val != null && val.indexOf('[{') >=0 
									&& JSON.parse(val)[0] != null && JSON.parse(val)[0].productbillsid) {
								$(JSON.parse(val)).each(function (index, bills) {
									billsInfo.push(bills.productbillsid);
								});
								break;
							}
						}
						selecBillsIds = billsInfo.join(',');
						tableData = res.data;
					}
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
					flowBillsInput = $('[label-id="' + label.id + '"]');
					var bindClick = function () {
						var tips = $('.form-submit-tips');
						$('#writeOffBills').next().find('[data-field="checked"]').bind('click', function () {
							setTimeout(function () {
								var checkStatus = table.checkStatus('writeOffBills');
								var selectData = checkStatus.data;
								var sum = 0.00;
								if (selectData && selectData.length > 0){
									$(selectData).each(function (index, bills) {
										sum += (bills.receivables - bills.actualReceivables);
									});
								}
								$('.bill_write_off_sum_pay').html(sum.toFixed(2));
								if (parseFloat($('.bill_write_off_sum_pay').html()) > parseFloat($('.bill_write_off_income_sum_pay').html())) {
									tips.val('收款总计必须大于等于账单总计');
								} else {
									tips.val('');
								}
								flowContactsInfo(table);
							}, 10);
						});
					}
					bindClick();
					$('#writeOffBills').next().find('[data-field="id"]').each(function (index, item) {
						var id = $(this).find('div').text();
						if (id && selecBillsIds.indexOf($(this).text()) >= 0) {
							if (tableData && tableData.length > 0) {
								var flag = true;
								$(tableData).each(function (index, item) {
									if (flag && id == item.id) {
										item['LAY_CHECKED'] = true;
										flag = false;
										return;
									}
								});
								triggerClick($(this).parent('tr'));
							}
						}
					});
				}
			});
		});
	}, 5);
	return htmlStr;
}

function rowBindClick(ele){
	$(ele).on("click", ".layui-table-body table.layui-table tbody tr", function (e) {
		if ($(e.target).hasClass("layui-table-col-special") || $(e.target).parent().hasClass("layui-table-col-special")) {
			return false;
		}
		var index = $(this).attr('data-index'), tableBox = $(this).closest('.layui-table-box'),
			tableFixed = tableBox.find(".layui-table-fixed.layui-table-fixed-l"),
			tableBody = tableBox.find(".layui-table-body.layui-table-main"),
			tableDiv = tableFixed.length ? tableFixed : tableBody,
			checkCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox i"),
			radioCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-radio div.layui-form-radio i");
		if (checkCell.length) {
			checkCell.click();
		}
		if (radioCell.length) {
				radioCell.click();
			}
	});
	$(ele).on("click", "td div.laytable-cell-checkbox div.layui-form-checkbox,td div.laytable-cell-radio div.layui-form-radio", function (e) {
		e.stopPropagation();
	});
}


/**
 * 拼接原来的价格信息，如果没有（不展示）
 */
function origin_price(flow_class,prices) {
    if (isNotBlank(prices) && prices.length > 0) {
        var origin_price_dom = "<div class='layui-form-item'><table style='width: 100%'>"+
            "<tr><td colspan='4'>原来单价:</td></tr>";
        for (var price_index = 0; price_index < prices.length ; price_index++){
            var price_item = prices[price_index];
            if ("[AdjustPriceFlow]" === flow_class ){
                if (price_item.priceType !== "1" && price_item.priceType !== 1) {
                    origin_price_dom +=
                        "<tr class='price_table_row'>" +
                        "   <td style='width: 20%;'>价格：</td>" +
                        "   <td style='width: 20%;'>"+price_item.price+" </td>" +
                        "   <td style='width: 20%;'>条数:</td>" +
                        "   <td style='width: 40%;'> "+ price_item.minSend+" ~ "+price_item.maxSend +" </td>" +
                        "</tr>";
                }else{
                    origin_price_dom +=
                        "<tr class='price_table_row'>" +
                        "   <td style='width: 20%;'>价格：</td>" +
                        "   <td style='width: 80%;'>"+price_item.price+" </td>" +
                        "</tr>";
                }
            }else if ("[InterAdjustPriceFlow]" === flow_class) {
                // 展示为一个文件 供下载
                origin_price_dom +=
                    "<tr class='price_table_row'>" +
                    "   <td style='width: 20%;'>价格文件：</td>" +
                    "   <td style='width: 80%;'>"+price_item.remark+" </td>" +
                    "</tr>";
            }
        }
        origin_price_dom += "</table></div>";
        return origin_price_dom;
    }
    return "";
}

// 是否可用（可以编辑）
function is_disabled(input_ele, label_id, data) {
    var disabled = "";
    // 是否可以编辑
    if (isBlank(data.editLabelIds) || data.editLabelIds.indexOf(label_id) < 0) {
        disabled = " disabled ";
    }
    return disabled;
}


// 根据标签名 获取对应的值
function get_label_value(label_name, data) {
    var value;
    if (isBlank(data.labelValueMap)) {
        data.labelValueMap = {};
    }
    value = data.labelValueMap[label_name];
    if (!isBlank(value)) {
        return value;
    } else {
        return "";
    }
}

// 已经上传文件的数据对应、记录临时数据
var upload_file = {};

// 文件标签的上传功能
function init_file_load(input_name, default_value, ele_id) {
    //
    var demoListView = $("#" + ele_id + " tbody[data-file-name='" + input_name + "']");
    var uploadListIns = upload.render({
        elem: "#" + ele_id + " button[name='" + input_name + "']",
        url: '/operate/upLoadFile',
        field: 'files',
        accept: 'file',
        multiple: true,
        auto: false,
        choose: function (obj) {
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                var tr = $(['<tr id="upload-' + index + '">', '<td>' + file.name + '</td>', '<td>等待上传</td>', '<td>', '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>', '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>', '</td>', '</tr>'].join(''));
                demoListView.append(tr);

                //上传，上传成功调用done方法
                obj.upload(index, file);

                //删除
                tr.find('.demo-delete').on('click', function () {
                    //删除对应的文件
                    delete files[index];
                    var file_info = upload_file[index];
                    // 删除已经记录上的数据
                    delete_upload_file(input_name, file_info, ele_id);
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });
            });
        },
        done: function (res, index, upload) { // 文件上传成功的回调方法
            if (res.code === 200) {
                //上传成功
                var tr = demoListView.find('tr#upload-' + index),
                    tds = tr.children();
                tds.eq(1).html('<span style="color: #5FB878;">上传成功</span>');
                //去掉重传按钮
                tds.eq(2).find("button[class*='demo-reload']").remove();
                upload_file[index] = res.data;
                // 处理上传的文件（记录已经上传的文件）
                take_upload_file_result(res.data, input_name, ele_id);
                //删除文件队列已经上传成功的文件
                return delete this.files[index];
            }
            this.error(index, upload);
        },
        error: function (index, upload) {
            var tr = demoListView.find('tr#upload-' + index),
                tds = tr.children();
            tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
    });
}

// 处理梯度价格信息
function take_gradient_price(input_parent, label_price_info, disabled) {
    var item_ele = $(input_parent).parent();
    if (!isBlank(label_price_info) && label_price_info != {}) {
        var gradient_price = (typeof label_price_info == "object") ? label_price_info : JSON.parse(label_price_info);
        // 排序 防止梯度顺序不对
        gradient_price = gradient_price.sort(function (a, b) {
            if (a.gradient < b.gradient) {
                return -1;
            } else if (a.gradient > b.gradient) {
                return 1;
            } else {
                return 0;
            }
        });
        // 循环处理梯度价格
        for (var gradient_price_index = 0; gradient_price_index < gradient_price.length; gradient_price_index++) {
            // 梯度价格信息
            var gradient_info = gradient_price[gradient_price_index];
            // 初始化的时候，清除已经有的增加梯度按钮
            $(item_ele).parent().find(".gradient").find(".operate").empty();
            $(item_ele).parent().find(".gradient").find("input[name='gradient_max']").attr("disabled", "disabled");
            // 增加梯度
            take_gradient_item(item_ele, gradient_info, disabled, gradient_price_index);
        }
    } else {
        take_gradient_item(item_ele, "", disabled, 0);
    }
}

// 类型改变的时候触发事件
function gradient_type_change(parent, res, data) {
    // 选择框的dom
    var select_ele = res.elem;
    // 获取调价梯度的label_name，用来获取梯度值
    var gradient_name = $("#flowMsg_" + data.flowEntId).find("input[name='gradient_label_name']").val();
    // 流程原本的价格类型
    var default_gradient = $(select_ele).attr("data-default-gradient");
    // 选项变更前的价格类型
    var before_gradient = $(select_ele).attr("data-before-gradient");
    // 选项变更后的价格类型
    var after_gradient = res.value;
    // 价格类型选回原本的类型时，回显原本的数据
    var label_price_info = "";
    if (default_gradient === "1" && after_gradient === "1") {
        label_price_info = get_label_value(gradient_name, data);
    }
    if ((default_gradient === "2" || default_gradient === "3") && (after_gradient === "2" || after_gradient === "3")) {
        label_price_info = get_label_value(gradient_name, data);
    }

    if ((before_gradient === 2 || before_gradient === "2" || before_gradient === 3 || before_gradient === "3") && (after_gradient === "1" || after_gradient === 1)) {
        // 由 阶梯/阶段价 -> 统一价
        take_uniform_price(parent, label_price_info, "");
    } else if ((before_gradient === 1 || before_gradient === "1") && (after_gradient === "2" || after_gradient === 2 || after_gradient === 3 || after_gradient === "3")) {
        // 由 统一价 -> 阶梯/阶段价
        take_gradient_price(parent, label_price_info, "");
    } else {
        console.info("前后调整一致，不予做出界面调整，---" + after_gradient + "---")
    }
    // 更新变更前的价格类型
    $(select_ele).attr("data-before-gradient", after_gradient);
    form.render();
    // element.render();
}

// 增加价格梯度 对应item 最少发送量，最多发送量， 单价， 百万投比， 省占比
function take_gradient_item(item_ele, gradient_info, disabled, gradient_index) {
    if (isBlank(gradient_info)) {
        gradient_info = {};
    }
    var is_default = gradient_info.isdefault;
    // 获取新的梯度的最小发送量（梯度信息为空时，最小发送量是最后一个梯度的最大发送量）
    var min_send_count = get_min_send_count(item_ele, gradient_info);
    var max_send_count = isBlank(gradient_info.maxsend) ? "" : gradient_info.maxsend;
    var price = isBlank(gradient_info.price) ? "" : gradient_info.price;
    var millions_ratio = isBlank(gradient_info.complaintrate) ? "" : gradient_info.complaintrate;
    var province_ratio = isBlank(gradient_info.provinceproportion) ? "" : gradient_info.provinceproportion;
    var gradient = isBlank(gradient_info.gradient) ? gradient_index : parseInt(gradient_info.gradient);

    // 选中默认
    var default_checked = !isBlank(is_default) && (is_default === 1 || is_default === "1") ? " checked " : "";
    var before_dom = "<div class='layui-form-item gradient-line gradient' data-gradient-index = " + gradient + ">";
    var dom =
        "   <div class='layui-form-label'>" +
        "       <input type='radio' name='defaultGradient' value='0' title='默认' " + default_checked + disabled + ">" +
        "   </div>" +
        "   <div class='layui-input-block' style='height: 36px'>" +
        "       <input name='gradient_min' class='layui-input' style='width: 100px; display: inline; text-align: center;' placeholder='请填写' value='" + min_send_count + "' disabled />" +
        "       <span>&le;条&lt;</span>"+
        "       <input name='gradient_max' class='layui-input' style='width: 100px; display: inline; text-align: center;' placeholder='请填写' value='" + max_send_count + "'" + disabled + "/>" +
        "   </div>" +

        "   <label class='layui-form-label'><span style='color: red;'>*</span>价格：</label>" +
        "   <div class='layui-input-block'>" +
        "       <input name='price' class='layui-input gradient-detail' placeholder='请填写' value='" + price + "'" + disabled + "/><span class='gradient-unit'>元</span>" +
        "   </div>" +
        "   <label class='layui-form-label'><span>百万投比：</span></label>" +
        "   <div class='layui-input-block'>" +
        "       <input name='million_ratio' class='layui-input gradient-detail' placeholder='请填写' value='" + millions_ratio + "'" + disabled + "/>" +
        "   </div>" +
        "   <label class='layui-form-label'><span>省占比：</span></label>" +
        "   <div class='layui-input-block'>" +
        "       <input name='province_ratio' class='layui-input gradient-detail' placeholder='请填写' value='" + province_ratio + "'" + disabled + "/><span class='gradient-unit'>%</span>" +
        "       <div class='layui-inline operate'>" + add_opts_btn(gradient, disabled) + "</div>" +
        "   </div>";
    var end_dom = "</div>";

    if (gradient === 0) {
        // 移除统一价
        $(item_ele).parent().find(".nogradient").remove();
        // 追加梯度价
        $(item_ele).after(before_dom + dom + end_dom);
    } else {
        $(item_ele).parent().find("div[data-gradient-index=" + (gradient - 1) + "]").after(before_dom + dom + end_dom);
    }
    // 绑定校验函数
    gradient_max(item_ele, gradient);
    form.render();
    // element.render();
}

// 梯度最大值校验
function gradient_max(item_ele, index) {
    // 分段价格
    var item_gradient_ele = $(item_ele).parent().find("div[data-gradient-index = " + index + "]");
    item_gradient_ele.find("input[name='gradient_max']").blur(function (e) {
        var max_value = $(this).val();
        var min_ele = $(item_ele).parent().find("div[data-gradient-index = " + index + "]").find("input[name='gradient_min']");
        var min_value = $(min_ele).val();
        var gradient_count = $(item_ele).parent().find(".gradient").length;
        if (index !== (gradient_count - 1)) {
            if (isBlank(max_value) || parseInt(max_value) <= parseInt(min_value)) {
                layer.tips("梯度终止值必须大于起始值", $(this));
                // 清空数据
                $(this).val("");
            }
        }
        // 判断是否为整数
        if (!isBlank(max_value) && !isInt(max_value)) {
            layer.tips("数量必须为整数", $(this));
            // 清空数据
            $(this).val("");
        }
    });

    // 价格
    item_gradient_ele.find("input[name='price']").blur(function (e) {
        var price_value = $(this).val();
        if (isBlank(price_value)) {
            layer.tips("价格不能为空", $(this));
            return false;
        }
        if (!isBlank(price_value) && !$.isNumeric(price_value)) {
            layer.tips("价格只能是数字", $(this));
            // 清空数据
            $(this).val("");
            return false;
        }
    });

    //百万投比
    item_gradient_ele.find("input[name='million_ratio']").blur(function (e) {
        var million_ratio_value = $(this).val();
        if (!isBlank(million_ratio_value) && !$.isNumeric(million_ratio_value)) {
            layer.tips("百万投比只能是数字", $(this));
            // 清空数据
            $(this).val("");
            return false;
        }
    });

    // 省占比
    item_gradient_ele.find("input[name='province_ratio']").blur(function (e) {
        var province_ratio_value = $(this).val();
        if (!isBlank(province_ratio_value) && !$.isNumeric(province_ratio_value)) {
            layer.tips("省占比只能是数字", $(this));
            // 清空数据
            $(this).val("");
            return false;
        }
    });

}

// 获取最小发送量，梯度信息为空时，是最后一个梯度的最大发送量
function get_min_send_count(item_ele, gradient_info) {
    var min_send_count = 0;
    if (isNotBlank(gradient_info.minsend)) {
        min_send_count = gradient_info.minsend;
    } else {
        var gradient_eles = $(item_ele).parent().find(".gradient");
        if (gradient_eles.length > 0) {
            min_send_count = $(gradient_eles[gradient_eles.length - 1]).find("input[name='gradient_max']").val();
        } else {
            min_send_count = 0;
        }
    }
    return min_send_count;
}


// 生成梯度的添加删除按钮
function add_opts_btn(count, disabled) {
    if (disabled == " disabled ") {
        return "";
    }
    var remove_dom = "<span class='opts_btn' id='add_info' onclick='add_gradient_click(this)'>" +
        "    <i class='layui-icon layui-icon-add-circle'></i>" +
        "</span>";
    if (count > 0) {
        remove_dom = remove_dom + "<span class='opts_btn' id='reduce_info' style='margin-left: 15px;' onclick='reduce_gradient_click(this)'>" +
            "<i class='layui-icon layui-icon-close-fill'></i>" +
            "</span>";
    }
    return remove_dom;
}

// 新增按钮
function add_gradient_click(ele) {
    // 当前按钮所在梯度的元素
    var this_gradient_ele = $(ele).parents(".gradient-line");
    // 当前最大发送量不能为空
    var max_ele = this_gradient_ele.find("input[name='gradient_max']");
    var max_count = max_ele.val();
    var min_ele = this_gradient_ele.find("input[name='gradient_min']");
    var min_count = min_ele.val();
    if (isBlank(max_count)) {
        layer.tips('最大发送量不能为空', max_ele);
        return false;
    } else if (parseInt(max_count) <= parseInt(min_count)) {
        layer.tips('最大发送量应大于最小发送量', max_ele);
        return false;
    }
    max_ele.attr("disabled", "disabled");
    this_gradient_ele.find(".operate").empty();
    var gradient_index = this_gradient_ele.attr("data-gradient-index");
    gradient_index = parseInt(gradient_index) + 1;
    take_gradient_item(this_gradient_ele, {}, '', gradient_index);
}

// 删除价格梯度
function reduce_gradient_click(ele) {
    // 给上一个元素增加删除（有两个以上元素）
    // gradient_price_count--;
    var this_gradient_ele = $(ele).parents(".gradient-line");
    var gradient_index = this_gradient_ele.attr("data-gradient-index");
    gradient_index = parseInt(gradient_index);
    var before_gradient_ele = $(this_gradient_ele).prev();
    if (before_gradient_ele.length > 0 && $(before_gradient_ele).hasClass("gradient")) {
        // 确认是梯度调价的 增加操作按钮
        $(before_gradient_ele).find(".operate").html(add_opts_btn(gradient_index - 1, ""));
        // 可以编辑上一个最大值
        $(before_gradient_ele).find("input[name='gradient_max']").removeAttr("disabled");
    }
    // 删除当前元素
    $(this_gradient_ele).remove();
}

// 处理语音单位
function take_voice_unit(parent, label_price_info, disabled) {
    if (!isBlank(label_price_info) && label_price_info !== {}) {
        // 只会存在一个
        var label_price = (typeof label_price_info == "object") ? label_price_info : JSON.parse(label_price_info);
        if (!isBlank(label_price) && label_price.length > 0) {
            if (label_price[0].hasOwnProperty("voiceUnit")) {
                var voiceUnit = label_price[0].voiceUnit;
                var voiceUnit_dom = "<div class='layui-form-item'>" +
                    "    <label class='layui-form-label show-lable' data-lable-name='计费单位'>" +
                    "        <span style='color: red;'>*</span>" +
                    "        <span>计费单位：</span>" +
                    "    </label>" +
                    "    <div class='layui-input-block'>" +
                    "        <input type='text' class='layui-input' name='voiceUnit' value='" + voiceUnit + "' placeholder='请输入计费单位' " + disabled + "/>" +
                    "        <i class='gradient_unit'>秒</i>" +
                    "    </div>" +
                    "</div>";
                $(parent).parent().after(voiceUnit_dom);
            }
        }
    }
}


// 处理统一价格 // "[{voiceUnit:'','price' : '100000', 'provinceprice' : '1000'}]"
function take_uniform_price(input_parent, label_price_info, disabled) {
    var price = "";
    var provinceprice = "";
    var item_ele = $(input_parent).parent();
    if (!isBlank(label_price_info) && label_price_info !== {}) {
        // 只会存在一个
        var label_price = (typeof label_price_info == "object") ? label_price_info : JSON.parse(label_price_info);
        if (!isBlank(label_price) && label_price.length > 0) {
            price = label_price[0].price;
            provinceprice = label_price[0].provinceprice;
        }
    }
    // 清理掉阶段价格的html
    $(item_ele).parent().find(".gradient").remove();
    var uni_ele = "<div class='layui-form-item gradient-line nogradient'>" +
        "    <label class='layui-form-label'><span style='color: red;'>*</span><span>价格：</span></label>" +
        "    <div class='layui-input-block'>" +
        "        <input type='text' name='price'  class='layui-input gradient-detail' placeholder='请填写价格' value='" + price + "' " + disabled + " /><span class='gradient-unit'>元</span>" +
        "    </div>" +
        "    <label class='layui-form-label'><span>省网价格：</span></label>" +
        "    <div class='layui-input-block'>" +
        "        <input type='text' name='provinceprice' class='layui-input gradient-detail' placeholder='请填写省网价格' value='" + provinceprice + "'" + disabled + " /><span class='gradient-unit'>元</span>" +
        "    </div>" +
        "</div>";
    $(item_ele).after(uni_ele);
}

// 处理酬金标签
function take_remuneration_info(parent, lable_name, label_value, default_value, requred, disabled) {
    if (isBlank(label_value)) {
        label_value = "0.00,0.00,0.00,0.00,0.00";
    }
    var values = label_value.split(",");
    $(parent).addClass("remuneration");
    var remuneration_dom = "<span>金额&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input input-edit' name='money' value=" + values[0] + " data-label-name='" + lable_name + "' data-unit='元' " + disabled + ">" +
        "<span>&nbsp;&nbsp;X&nbsp;&nbsp;</span>" +
        "<span>酬金比例&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input input-edit' name='rate' value=" + values[1] + " data-unit='%'" + disabled + ">" +
        "<span>&nbsp;&nbsp;+&nbsp;&nbsp;奖励&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input input-edit' name='reward' value=" + values[2] + " data-unit='元'" + disabled + ">" +
        "<span>&nbsp;&nbsp;</span>" +
        "<br>" +
        "<span>－扣款&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input input-edit' name='deduction' value=" + values[3] + " data-unit='元'" + disabled + ">" +
        "<span>&nbsp;&nbsp;=&nbsp;&nbsp;</span>" +
        "<input type='text' class='layui-input' readonly name='remuneration' value=" + values[4] + " data-unit='元'>";
    $(parent).html(remuneration_dom);
    take_remuneration_unit(parent);
    take_remuneration_input(parent);
}

// 处理酬金标签
function typtRemunerationToString(labelValue, defaultValue) {
    if (isBlank(labelValue)) {
        labelValue = "0.00,0.00,0.00,0.00,0.00";
    }
    var values = labelValue.split(",");
    var html = "金额" + values[0] + "元" + "&nbsp;X&nbsp;酬金比例&nbsp;" + values[1] + "%" + "&nbsp;+&nbsp;奖励&nbsp;" + values[2] + "元";
    html += "&nbsp;－&nbsp;扣款&nbsp;" + values[3] + "元" + "&nbsp;=&nbsp;" + values[4] + "元";
    return html;
}

// 校验输入数据，并结算结果
function take_remuneration_input(parent) {
    // 失去焦点事件，对数据进行校验
    $(parent).find("input[class*='input-edit']").blur(function (e) {
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
        $(this).parent().find("input[name='remuneration']").val(remuneration.toFixed(2));
    });

    // 获取焦点事件
    $(parent).find("input[class*='input-edit']").focus(function (e) {
        var ele_value = $(this).val();
        if (!isBlank(ele_value) && parseFloat(ele_value) == 0) {
            $(this).val("");
        }
    });
}

// 处理账单信息
function take_product_bill(parent, lable_name, label_value, default_value, requred, disabled, data) {
    var flowEntId = data.flowEntId;
    var productId = data.productId;
    var flowClass = data.flowClass;
    var flowId = data.flowId;
    // 当前节点可操作，并且按钮可用，才显示按钮
    var visable = disabled === '';
    var product_bill_dom = "<input type='button' onclick='chooseAccount(this, \"" + flowEntId + "\", \"" + productId + "\", \"" + flowClass + "\", \"" + flowId + "\")'";
    product_bill_dom += " data-label-name='" + lable_name + "' " + disabled + " class='layui-btn layui-btn-sm' value='选择账单' style='width: fit-content;";
    product_bill_dom += visable ? "'/>" : "display: none'/>";
    $(parent).empty();
    $(parent).append(product_bill_dom);
    if (!isBlank(label_value) && label_value !== "") {
        // 回显已经有的数据项
        var product_bill_infos = (typeof label_value == 'object') ? label_value : JSON.parse(label_value);
        take_product_bill_item(parent, lable_name, product_bill_infos, default_value, requred, disabled, flowEntId, flowClass, flowId);
    }
}

// 处理账单信息
function typeBillToString(labelValue, defaultValue, flowEntId, flowClass, flowId) {
    var html = "";
    if (!isBlank(labelValue)) {
        var productBills = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
        if (!isBlank(productBills) && productBills.length > 0) {
            // 本次收付款
            var this_time_pay = 0;
            // 展示名称
            var show_name = product_bill_label_name[flowClass];
            // 输入框名称
            var input_name = product_bill_input_name[flowClass];
            // 总计名称
            var total_name = show_name.total_name;
            $.each(productBills, function (i, productBill) {
                // 应该 支付|收款
                var payables_name = input_name.payables_name;
                var payables = isBlank(productBill[payables_name]) ? 0 : parseFloat(productBill[payables_name]);

                // 实际(已经) 支付|收款
                var actualpayables_name = input_name.actualpayables_name;
                var actualpayables = isBlank(productBill[actualpayables_name]) ? 0 : parseFloat(productBill[actualpayables_name]);

                // 本次 支付|收款
                var thisPayment_name = input_name.thisPayment_name;
                var thisPayment = isBlank(productBill[thisPayment_name]) ? 0 : parseFloat(productBill[thisPayment_name]);

                // 剩余应该 支付|收款（固定不变[要求]）
                var left_should_pay_name = input_name.left_should_pay_name;
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
                            flowEntId: flowEntId
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

                // 本次应付（总）
                this_time_pay += parseFloat(thisPayment);

                html += "<br/>" + productBill.title + "<br/>";
                html += show_name.payables_name + payables.toFixed(2) + "元，";
                html += show_name.actualpayables_name + actualpayables.toFixed(2) + "元，";
                html += show_name.left_should_pay_name + left_should_pay.toFixed(2) + "元，";
                html += show_name.thisPayment_name + thisPayment.toFixed(2) + "元";
            });
            html += "<br/>" + total_name + format_num(this_time_pay, 2) + "元";
            return html;
        }
    }
    return "无";
}

// 加载产品对应的账单信息
function chooseAccount(ele, flowEntId, productId, flowClass, flowId) {
    var url;
    if (flowClass === '[BillReceivablesFlow]' || flowClass === '[InvoiceFlow]') {
        url = '/customerOperate/toQueryAccount';
    } else {
        url = '/operate/toQueryAccount';
    }
    layui.use(['jquery', 'layer'], function () {
        var layer = layui.layer;
        layer.open({
            type: 2,
            area: ['535px', '355px'], //宽高
            content: url + '?productId=' + productId + "&flowClass=" + encodeURI(flowClass),
            btn: ['选择', '取消'],
            yes: function (index, layero) {
                var checkStatus = window["layui-layer-iframe" + index].layui.table.checkStatus('accoutlist');
                var parent = $(ele).parent();
                var label_name = $(ele).attr("data-label-name");
                take_product_bill_item(parent, label_name, checkStatus.data, "", "", "", flowEntId, flowClass, flowId);
                layer.close(index);
            }
        });
    });
}

// 回显原有的数据选项
function take_product_bill_item(parent, lable_name, product_bill_item_infos, default_value, required, disabled, flowEntId, flowClass, flowId) {
    if (!isBlank(product_bill_item_infos) && product_bill_item_infos.length > 0) {
        var product_bill_dom = "<div class='layui-form-item product_bill_item'>";
        var this_time_pay = 0;
        // 展示名称
        var show_name = product_bill_label_name[flowClass];
        // 输入框名称
        var input_name = product_bill_input_name[flowClass];
        // 总计名称
        var total_name = show_name.total_name;
        for (var bill_index = 0; bill_index < product_bill_item_infos.length; bill_index++) {
            // 计费信息
            var bill_info = product_bill_item_infos[bill_index];
            // 应该 支付|收款
            var payables_name = input_name.payables_name;
            var payables = isBlank(bill_info[payables_name]) ? 0 : parseFloat(bill_info[payables_name]);

            // 实际(已经) 支付|收款
            var actualpayables_name = input_name.actualpayables_name;
            var actualpayables = isBlank(bill_info[actualpayables_name]) ? 0 : parseFloat(bill_info[actualpayables_name]);

            // 本次 支付|收款
            var thisPayment_name = input_name.thisPayment_name;
            var thisPayment = isBlank(bill_info[thisPayment_name]) ? 0 : parseFloat(bill_info[thisPayment_name]);

            // 剩余应该 支付|收款（固定不变[要求]）
            var left_should_pay_name = input_name.left_should_pay_name;
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
                        billId: bill_info.id,
                        type: type,
                        flowEntId: flowEntId
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

            // 本次应付（总）
            this_time_pay += parseFloat(thisPayment);

            product_bill_dom = product_bill_dom +
                "<div class='product-bill-line' name=" + lable_name + ">" +
                "    <span class='product-bill-title' id='" + bill_info.id + "'>" + bill_info.title + "</span>" +
                "    <label class='layui-form-label'><span>" + show_name.payables_name + "</span></label>" +
                "    <div class='layui-input-block'>" +
                "        <input name='payables' data-param-name='" + payables_name + "' class='layui-input pay_detail' value='" + payables.toFixed(2) + "' disabled  data-unit='元'/>" +
                "    </div>" +
                "    <label class='layui-form-label'><span>" + show_name.actualpayables_name + "</span></label>" +
                "    <div class='layui-input-block'>" +
                "        <input name='actualpayables' data-param-name='" + actualpayables_name + "' class='layui-input pay_detail' value='" + actualpayables.toFixed(2) + "' disabled  data-unit='元'/>" +
                "    </div>" +
                "    <label class='layui-form-label'><span>" + show_name.left_should_pay_name + "</span></label>" +
                "    <div class='layui-input-block'>" +
                "        <input name ='left_should_pay' data-param-name='" + left_should_pay_name + "' class='layui-input pay_detail' value='" + left_should_pay.toFixed(2) + "'  disabled  data-unit='元'/>" +
                "    </div>" +
                "    <label class='layui-form-label'><span>" + show_name.thisPayment_name + "</span></label>" +
                "    <div class='layui-input-block'>" +
                "        <input name='thisPayment' data-param-name='" + thisPayment_name + "' onchange='take_this_payment_change(this)' class='layui-input pay_detail' value='" + thisPayment.toFixed(2) + "' " + disabled + " data-unit='元' />";
            if (isBlank(disabled)) {
                // 删除按钮
                product_bill_dom = product_bill_dom + "<i onclick='remove_product_bill_item(this)' class='layui-icon layui-icon-delete delete-paroduct-bill'></i>" +
                    "</div>";
            } else {
                product_bill_dom = product_bill_dom + "</div>";
            }
            product_bill_dom = product_bill_dom + "</div>";
        }
        product_bill_dom = product_bill_dom + "<i class='bill_count_tip'>" + total_name + "<span class='bill_this_time_pay'>" + format_num(this_time_pay, 2) + "</span>&nbsp;元</i>";
        product_bill_dom = product_bill_dom + "</div>"; // form item
        // 获取紧邻的标签
        var next = $(parent).parent().next();
        if ($(next).hasClass("product_bill_item")) {
            // 删除已有的标签
            $(next).remove();
        }
        $(parent).parent().after(product_bill_dom);
        take_product_bill_unit(parent);
    }
}

// 处理价格单位
function take_product_bill_unit(parent) {
    var next = $(parent).parent().next();
    if ($(next).hasClass("product_bill_item")) {
        var inputs = $(next).find("input[class*='pay_detail']");
        if (!isBlank(inputs)) {
            for (var input_index = 0; input_index < inputs.size(); input_index++) {
                var input_temp = inputs[input_index];
                var unit = $(input_temp).attr("data-unit");
                if (isNotBlank(unit)) {
                    $(input_temp).after("<i class='product-bill-unit'>" + unit + "</i>");
                }
            }
        }
    }
}

// 删除产品账单项
function remove_product_bill_item(ele) {
    var bill_form_item = $(ele).parents(".product_bill_item");
    $(ele).parents(".product-bill-line").remove();
    // 重新计算付款合计金额
    count_this_time_should_pay(bill_form_item);
}


// 监听本次支付改变值时处理时间
function take_this_payment_change(ele) {
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
    var payables = isBlank($(payables_ele).val()) ? 0.00 : parseFloat($(payables_ele).val()).toFixed(2);
    // 实际已付
    var actualpayables = isBlank($(actualpayables_ele).val()) ? 0.00 : parseFloat($(actualpayables_ele).val()).toFixed(2);
    //剩余应付金额
    var left_should_pay = payables - actualpayables - this_pay_money;
    if (left_should_pay < 0) {
        this_pay_money = payables - actualpayables;
        $(ele).val(this_pay_money);
        layer.tips("超过最大金额，设为最大金额", ele);
    }
    if (this_pay_money < 0) {
        layer.tips("数据错误，金额不能为负数", ele);
        $(ele).val(parseFloat(0).toFixed(2));
    }
    // 重新计算付款合计金额
    count_this_time_should_pay($(ele).parents(".product_bill_item"));
}

// 计算付款合计金额
function count_this_time_should_pay(ele) {
    var pay_money = 0;
    var bill_items = $(ele).find("div[class*='product-bill-line']");
    if (!isBlank(bill_items) && bill_items.size() > 0) {
        for (var bill_item_index = 0; bill_item_index < bill_items.size(); bill_item_index++) {
            var bill_item = bill_items[bill_item_index];
            var this_pay = $(bill_item).find("input[name='thisPayment']").val();
            this_pay = isBlank(this_pay) ? 0.00 : parseFloat(this_pay).toFixed(2);
            pay_money = parseFloat(pay_money) + parseFloat(this_pay);
        }
    }
    $(ele).find("span[class*='bill_this_time_pay']").text(format_num(pay_money, 2));
}


// 是否为整数
function isInt(obj) {
    if (!$.isNumeric(obj)) {
        return false;
    }
    return parseInt(obj) == obj;
}

// 存储上传的文件内容
var file_result = {};

// 记录本标签上传成功的文件
function take_upload_file_result(filesPaths, input_name, ele_id) {
    if (!isBlank(filesPaths)) {
        // 获取文件内容
        var files = file_result[input_name + ele_id];
        if (isBlank(files)) {
            files = new Array();
        }
        // 新加文件
        for (var file_index = 0; file_index < filesPaths.length; file_index++) {
            files.push(filesPaths[file_index]);
        }
        file_result[input_name + ele_id] = files;
    }
}

// 删除已经上传文件信息
function delete_upload_file(label_name, file_info, ele_id) {
    // 已经上传的数据
    var upload_file_array = file_result[label_name + ele_id];
    var posi = $.inArray(file_info, upload_file_array);
    // 删除文件
    upload_file_array.splice(posi, 1);
    // 初始化对应的数据
    file_result[label_name + ele_id] = upload_file_array;
}


// 回显已经上传的文件信息
function show_uploaded_file(label_name, loaded_file_json_array, disabled, ele_id) {
    if (!isBlank(loaded_file_json_array)) {
        var ele = $("#" + ele_id);
        // 解析数据
        var file_array = (typeof loaded_file_json_array == "object") ? loaded_file_json_array : JSON.parse(loaded_file_json_array);
        // 初始化对应的数据
        file_result[label_name + ele_id] = file_array;
        for (var file_index = 0; file_index < file_array.length; file_index++) {
            var file_temp = file_array[file_index];
            var json_file_temp = JSON.stringify(file_array[file_index]);
            var tr = $(["<tr id='upload-my-" + hex_md5(file_temp.filePath) + "'>",
                "<td>" + file_temp.fileName + "</td>",
                "<td><span style='color: #5FB878'>已经上传</span></td>",
                "<td>",
                "<span type='button' class='layui-btn layui-btn-xs layui-btn-danger my-down-load' onclick='down_load(" + json_file_temp + ")'>下载</span>",
                "<span type='button' class='layui-btn layui-btn-xs layui-btn-danger my-delete' " + disabled + " onclick='delete_file(\"" + label_name + "\"," + json_file_temp + ",\"" + ele_id + "\")'>删除</span>",
                "</td>",
                "</tr>"
            ].join(""));
            ele.find("tbody[data-file-name = '" + label_name + "']").append(tr);
        }
    }
}

// 下载
function down_load(file_info) {
    console.log("下载文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.location.href = "/operate/downloadFile?" + file_params;
}

// 删除
function delete_file(label_name, file_info, ele_id) {
    var file_code = hex_md5(file_info.filePath);
    $("#" + ele_id + " #upload-my-" + file_code).remove();

    // 删除对应的原有值
    var file_array = file_result[label_name + ele_id];
    if (!isBlank(file_array) && file_array.length > 0) {
        var left_file_array = [];
        for (var file_index = 0; file_index < file_array.length; file_index++) {
            var temp_file = file_array[file_index];
            if (hex_md5(temp_file.filePath) != file_code) {
                left_file_array.push(temp_file);
            }
        }
        file_result[label_name + ele_id] = left_file_array;
    }
}

// 处理账单金额标签
function take_bill_money_input(parent, lable_name, label_value, default_value, requred, disabled) {
    var supplier_success = 0;
    var supplier_price = 0;
    var total_money = 0;
    if (isNotBlank(label_value)) {
        var bill_money = label_value.split(",");
        if (isNotBlank(bill_money) && bill_money.length >= 3) {
            supplier_success = isBlank(bill_money[0]) ? 0 : parseInt(bill_money[0]);
            supplier_price = isBlank(bill_money[1]) ? 0 : parseFloat(bill_money[1]).toFixed(4);
            total_money = isBlank(bill_money[2]) ? 0 : parseFloat(bill_money[2]).toFixed(2);
        }
    }
    var bill_money_dom = "<input type='text' class='layui-input bill-money' name='supplier_success' placeholder='成功数' data-label-name = '" + lable_name + "' value='" + supplier_success + "' data-unit='条' " + requred + disabled + "/>" +
        "<span>X</span>" +
        "<input type='text' class='layui-input bill-money' name='supplier_price' style='width: 60px' placeholder='单价' value='" + supplier_price + "' data-unit='元' " + requred + disabled + "/>" +
        "<span>=</span>" +
        "<input type='text' class='layui-input bill-money' placeholder='金额' name='total_money' value='" + total_money + "' data-unit='元'/>";
    $(parent).empty();
    $(parent).addClass("bill-money-item");
    $(parent).append(bill_money_dom);
    // 处理单位
    // take_bill_money_unit(parent);
    // 处理输入校验
    take_bill_money_change(parent);
}

//处理销账账单信息
function typeStringToBillsInfoString(labelValue) {
	var tableHtml = $('<table class="flow-view-table" border="1" cellpadding="0" cellspacing="0"></table>');
	var tableTrHtml = $('<tr class="flow-view-table-th"></th>');
	tableTrHtml.append('<th class="flow-view-table-th" width="65%">账单名称</th>');
	tableTrHtml.append('<th class="flow-view-table-th" width="35%">账单金额</th>');
	tableHtml.append(tableTrHtml);
	var billsSum = 0;
	$(typeof labelValue == 'string' ? JSON.parse(labelValue) : labelValue).each(function (i, item) {
		var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.title + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + item.thiscost + '</td>');
		tableHtml.append(tableTrHtml);
		billsSum += item.thiscost;
	});
	tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">账单合计</td>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + billsSum.toFixed(2) + '</td>');
	tableHtml.append(tableTrHtml);
    return tableHtml.prop("outerHTML");
}

//处理销账收款信息
function typeStringToIncomeInfoString(labelValue) {
	var tableHtml = $('<table class="flow-view-table" border="1" cellpadding="0" cellspacing="0"></table>');
	var tableTrHtml = $('<tr class="flow-view-table-th"></th>');
	tableTrHtml.append('<th class="flow-view-table-th" width="18%">收款时间</th>');
	tableTrHtml.append('<th class="flow-view-table-th" width="40%">银行客户名称</th>');
	tableTrHtml.append('<th class="flow-view-table-th" width="21%">收款金额</th>');
	tableTrHtml.append('<th class="flow-view-table-th" width="21%">销账金额</th>');
	tableHtml.append(tableTrHtml);
	var incomeSum = 0;
	$(typeof labelValue == 'string' ? JSON.parse(labelValue) : labelValue).each(function (i, item) {
		var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.operatetime + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.banckcustomername + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + item.cost + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + item.thiscost + '</td>');
		tableHtml.append(tableTrHtml);
		incomeSum += item.thiscost;
	});
	tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">收款合计</td>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money" colspan="3">' + incomeSum.toFixed(2) + '</td>');
	tableHtml.append(tableTrHtml);
    return tableHtml.prop("outerHTML");
}

// 处理账单金额标签
function typeBillMoneyToString(labelValue, defaultValue) {
    var supplier_success = 0;
    var supplier_price = 0;
    var total_money = 0;
    if (isNotBlank(labelValue)) {
        var bill_money = labelValue.split(",");
        if (isNotBlank(bill_money) && bill_money.length >= 3) {
            supplier_success = isBlank(bill_money[0]) ? 0 : parseInt(bill_money[0]);
            supplier_price = isBlank(bill_money[1]) ? 0 : parseFloat(bill_money[1]).toFixed(4);
            total_money = isBlank(bill_money[2]) ? 0 : parseFloat(bill_money[2]).toFixed(2);
        }
    }
    var html = supplier_success + "条" + "&nbsp;X&nbsp;" + supplier_price + "元" + "&nbsp;=&nbsp;" + total_money + "元";
    return html;
}

// 处理单位
function take_bill_money_unit(parent) {
    var inputs = $(parent).find("input[class*='bill-money']");
    if (isNotBlank(inputs) && inputs.size() > 0) {
        for (var bill_input_index = 0; bill_input_index < inputs.size(); bill_input_index++) {
            var input_temp = inputs[bill_input_index];
            var unit = $(input_temp).attr("data-unit");
            if (isNotBlank(unit)) {
                $(input_temp).after("<i class='bill-money-unit'>" + unit + "&nbsp;&nbsp;</i>")
            }
        }
    }
}

// 处理输入框数据改变的值
function take_bill_money_change(parent) {
    var supplier_success_ele = $(parent).find("input[name='supplier_success']");
    var supplier_price_ele = $(parent).find("input[name='supplier_price']");
    // 总数改变 || 单价改变
    $(supplier_success_ele, supplier_price_ele).change(function (e) {
        take_bill_input_value(parent); // 计算金额
    });

    // 成功数框获取焦点时的判断
    $(supplier_success_ele).focus(function (e) {
        var content = $(this).val();
        if (!isBlank(content) && parseFloat(content) === 0) {
            $(this).val("");
        }
    });

    // 单价框获取焦点时的判断
    $(supplier_price_ele).focus(function (e) {
        var content = $(this).val();
        if (!isBlank(content) && parseFloat(content) === 0) {
            $(this).val("");
        }
    });

    // 成功数输入框失去焦点时的判断
    $(supplier_success_ele).blur(function (e) {
        var content = $(this).val();
        if (isBlank(content) || !$.isNumeric(content) || parseInt(content) < 0) {
            $(this).val('');
            layer.tips("输入错误，输入内容必须为大于0的数字", this);
        }
        take_bill_input_value(parent); // 计算金额
    });

    // 单价输入框失去焦点时的判断
    $(supplier_price_ele).blur(function (e) {
        var content = $(this).val();
        if (isBlank(content) || !$.isNumeric(content) || parseFloat(content) < 0) {
            $(this).val('');
            layer.tips("输入错误，输入内容必须为数字", this);
        }
        take_bill_input_value(parent); // 计算金额
    });

}

// 计算账单金额标签的金额
function take_bill_input_value(parent) {
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

// 初始化选择驳回到哪个节点
function init_reject_select(flowId, ele_id, nodeIndex, flowClass) {
    var data = {
        flowId: flowId,
        nodeIndex: nodeIndex
    };
    var dom = "<div class='layui-dropdown' style='padding: 0 5px'>" +
        "<button type='button' class='layui-btn layui-btn-danger layui-btn-sm'><i class='layui-icon layui-icon-triangle-r'></i>驳回给</button>";
    $.ajax({
        type: "POST",
        async: false,
        url: "/flow/getFlowNodeBefore.action",
        dataType: 'json',
        data: data,
        success: function (res) {
            if (res.code == 200) {
            	if (flowClass == '[BillWriteOffFlow]' && nodeIndex == 3) {
            		var ele = $(dom);
            		ele.find('button').attr('onclick', "cancel_confirm('" + res.data[0].nodeName + "', '" + res.data[0].roleName + "', '" + ele_id + "', 0);")
            			.html(ele.find('button').html().replace('驳回给', '修改'));
            		dom = ele.prop('outerHTML');
            	} else {
            		dom +=  "<ul>";
            		$.each(res.data, function (index, item) {
            			dom = dom + "<li><a href='javascript:void(0)' onclick='reject_confirm(\"" + item.nodeName + "\", \"" + item.roleName + "\", \"" + ele_id + "\", " + index + ");'>" + item.nodeName + "[" + item.roleName + "]</a></li>";
            		});
            		dom = dom + "</ul></div>";
            	}
            }
        }
    });
    return dom;
}

//驳回确认弹框
function cancel_confirm(nodeName, roleName, ele_id, nodeIndex) {
    layer.confirm("确认修改：<span style='color: red'>流程将返回给申请人！</span>", {
        title: "确认修改",
        icon: 3,
        btn: ["取消", "确认修改"],
        skin: "reject-confirm"
    }, function () {
    	
    }, function () {
        audit(3, ele_id, nodeIndex);
    })
}

// 驳回确认弹框
function reject_confirm(nodeName, roleName, ele_id, nodeIndex) {
    layer.confirm("确定驳回给节点：<span style='color: red'>" + nodeName + "</span>?<br>该节点的角色是：<span style='color: red'>" + roleName + "</span>", {
        title: "驳回确认",
        icon: 3,
        btn: ["取消", "确认驳回"],
        skin: "reject-confirm"
    }, function () {
        layer.msg("取消");
    }, function () {
        audit(3, ele_id, nodeIndex);
    })
}

// 审核
function audit(opts_type, ele_id, nodeIndex) {
    var flowMsgDiv = $("#" + ele_id);
    var flowData = flowMsgDiv.find("xmp[name='flowData']").text();
    var flow = JSON.parse(flowData);
    // 流程id
    var flow_id = flow.flowEntId;
    var rejectToNode = -1;
    var node_id = flow.nodeId;
    if (opts_type == 3 || opts_type == 4) { // 驳回、取消时不需要校验标签
        // 判断审核意见
        // 审核意见
        var audit_opinion_dom = flowMsgDiv.find("#audit-opinion");
        // 能够操作的时候才有意见框
        if (flow.canOperat && !isBlank(audit_opinion_dom) && audit_opinion_dom.size() > 0) {
            if (isBlank(audit_opinion_dom.val())) {
                layer.tips('审核意见不能为空', audit_opinion_dom);
                return false;
            }
        }
        if (nodeIndex !== undefined && nodeIndex !== null) {
            rejectToNode = nodeIndex;
        }
    } else if (!validation(ele_id)) { // 通过、保存时需要校验标签
        return false;
    }
    // 默认数据
    var base_data_eles = flowMsgDiv.find("input[base-data-map ='0']");
    var base_date = {};
    if (!isBlank(base_data_eles) && base_data_eles.size() > 0) {
        for (var base_data_index = 0; base_data_index < base_data_eles.size(); base_data_index++) {
            var input_ele = base_data_eles[base_data_index];
            var value = $(input_ele).val();
            var name = $(input_ele).attr("name");
            base_date[name] = value;
        }
    }

    // lable 值
    var label_date_map = flow.labelValueMap;
    // 普通输入框
    var data_input_eles = flowMsgDiv.find("input[data-type='0']");
    if (!isBlank(data_input_eles) && data_input_eles.size() > 0) {
        for (var data_input_ele_index = 0; data_input_ele_index < data_input_eles.size(); data_input_ele_index++) {
            var data_input_value = $(data_input_eles[data_input_ele_index]).val();
            var data_input_name = $(data_input_eles[data_input_ele_index]).attr("name");
            label_date_map[data_input_name] = data_input_value;
        }
    }

    // 单选框
    var data_radio_input_eles = flowMsgDiv.find("input[data-type='3']:checked");
    if (!isBlank(data_radio_input_eles) && data_radio_input_eles.size() > 0) {
        for (var data_radio_input_index = 0; data_radio_input_index < data_radio_input_eles.size(); data_radio_input_index++) {
            var data_radio_input_value = $(data_radio_input_eles[data_radio_input_index]).val();
            var data_radio_input_name = $(data_radio_input_eles[data_radio_input_index]).attr("name");
            label_date_map[data_radio_input_name] = data_radio_input_value;
        }
    }


    // 选择框
    var data_select_eles = flowMsgDiv.find("select[data-type='0']");
    if (!isBlank(data_select_eles) && data_select_eles.size() > 0) {
        for (var data_select_ele_index = 0; data_select_ele_index < data_select_eles.size(); data_select_ele_index++) {
            var data_select_value = $(data_select_eles[data_select_ele_index]).val();
            var data_select_name = $(data_select_eles[data_select_ele_index]).attr("name");
            label_date_map[data_select_name] = data_select_value;
        }
    }

    // 文本框
    var data_textarea_eles = flowMsgDiv.find("textarea[data-type='0']");
    if (!isBlank(data_textarea_eles) && data_textarea_eles.size() > 0) {
        for (var data_textarea_ele_index = 0; data_textarea_ele_index < data_textarea_eles.size(); data_textarea_ele_index++) {
            var data_textarea_value = $(data_textarea_eles[data_textarea_ele_index]).val();
            var data_textarea_name = $(data_textarea_eles[data_textarea_ele_index]).attr("name");
            label_date_map[data_textarea_name] = data_textarea_value;
        }
    }

    // 上传文件
    if (!isBlank(file_result)) {
        for (var file_label_name in file_result) {
            // 是本流程的文件类型的标签
            if (file_label_name.indexOf(ele_id) !== -1 && file_result.hasOwnProperty(file_label_name)) {
                label_date_map[file_label_name.replace(ele_id, '')] = file_result[file_label_name];
                // 取完文件后，清除文件上传中本标签的文件内容
                delete file_result[file_label_name];
            }
        }
    }

    // 梯度价格信息
    var gradient_values = [];

    // 处理梯度价格标签的校验
    var gradinet_items = flowMsgDiv.find(".gradient");
    if (!isBlank(gradinet_items) && gradinet_items.size() > 0) {
        for (var gradinet_items_index = 0; gradinet_items_index < gradinet_items.size(); gradinet_items_index++) {
            var gradient_value = {};
            // 梯度价
            var gradient_item = gradinet_items[gradinet_items_index];
            var defaultGradient = $(gradient_item).find("input[name='defaultGradient']").attr("checked");
            var gradient_min = $(gradient_item).find("input[name='gradient_min']").val();
            var gradient_max = $(gradient_item).find("input[name='gradient_max']").val();
            var price = $(gradient_item).find("input[name='price']").val();
            var million_ratio = $(gradient_item).find("input[name='million_ratio']").val();
            var province_ratio = $(gradient_item).find("input[name='province_ratio']").val();

            gradient_value.isdefault = 0;
            if (!isBlank(defaultGradient) && defaultGradient === "checked") {
                gradient_value.isdefault = 1;
            }
            gradient_value.minsend = isBlank(gradient_min) ? "" : gradient_min;
            gradient_value.maxsend = isBlank(gradient_max) ? "" : gradient_max;
            gradient_value.price = isBlank(price) ? "" : price;
            gradient_value.provinceproportion = isBlank(province_ratio) ? "" : province_ratio;
            gradient_value.complaintrate = isBlank(million_ratio) ? "" : million_ratio;
            gradient_value.gradient = gradinet_items_index;
            gradient_value.priceType = "gradient";
            if (gradinet_items_index === 0) {
                var voiceUnit = get_voice_unit_value(gradinet_items_index, gradient_item);
                if (!isBlank(voiceUnit)) {
                    gradient_value.voiceUnit = voiceUnit;
                }
            }
            gradient_values.push(sortObjectKey(gradient_value));
        }
    }

    // 统一价格
    var uniform_gradient_items = flowMsgDiv.find(".nogradient");
    if (!isBlank(uniform_gradient_items) && uniform_gradient_items.size() > 0) {
        var gradient_value = {};
        var price_value = $(uniform_gradient_items).find("input[name='price']").val();
        var provinceprice_value = $(uniform_gradient_items).find("input[name='provinceprice']").val();
        var price_info = {};
        price_info.price = isBlank(price_value) ? "" : price_value;
        price_info.provinceprice = isBlank(provinceprice_value) ? "" : provinceprice_value;
        price_info.priceType = "uniform";
        var voiceUnit = get_voice_unit_value(0, uniform_gradient_items);
        if (!isBlank(voiceUnit)) {
            gradient_value.voiceUnit = voiceUnit;
        }
        gradient_values.push(sortObjectKey(price_info));
    }

    // 价格梯度值
    var gradient_name = flowMsgDiv.find("input[name='gradient_label_name']").val();
    if (!isBlank(gradient_name) && !isBlank(gradient_values) && gradient_values.length > 0) {
        label_date_map[gradient_name] = gradient_values;
    }

    // 酬金数据
    var remuneration_items = flowMsgDiv.find("div[class*='remuneration']");
    if (!isBlank(remuneration_items) && remuneration_items.size() > 0) {
        for (var remuneration_index = 0; remuneration_index < remuneration_items.size(); remuneration_index++) {
            var remuneration_item = remuneration_items[remuneration_index];
            var money = $(remuneration_item).find("input[name='money']").val();
            var label_name = $(remuneration_item).find("input[name='money']").attr("data-label-name");
            if (isBlank(label_name)) {
                continue;
            }
            var rate = $(remuneration_item).find("input[name='rate']").val();
            var reward = $(remuneration_item).find("input[name='reward']").val();
            var deduction = $(remuneration_item).find("input[name='deduction']").val();
            var remuneration = $(remuneration_item).find("input[name='remuneration']").val();

            money = isBlank(money) ? 0 : parseFloat(money);
            rate = isBlank(rate) ? 0 : parseFloat(rate);
            reward = isBlank(reward) ? 0 : parseFloat(reward);
            deduction = isBlank(deduction) ? 0 : parseFloat(deduction);
            remuneration = isBlank(remuneration) ? 0 : parseFloat(remuneration);
            label_date_map[label_name] = money.toFixed(2) + "," + rate.toFixed(2) + "," + reward.toFixed(2) + "," + deduction.toFixed(2) + "," + remuneration.toFixed(2)
        }
    }

    // 账单金额 选项
    var bill_money_items = flowMsgDiv.find("div[class*='bill-money-item']");
    if (!isBlank(bill_money_items)) {
        for (var bill_money_index = 0; bill_money_index < bill_money_items.size(); bill_money_index++) {
            var bill_temp = bill_money_items[bill_money_index];
            var supplier_success = $(bill_temp).find("input[name='supplier_success']");
            // 标签名
            var label_name = $(supplier_success).attr("data-label-name");
            var success_count = $(supplier_success).val();
            var price = $(bill_temp).find("input[name='supplier_price']").val();
            var total_money = $(bill_temp).find("input[name='total_money']").val();

            // 成功数
            success_count = isBlank(success_count) ? 0 : parseInt(success_count);
            // 单价
            price = isBlank(price) ? 0 : parseFloat(price);
            // 总价
            total_money = isBlank(total_money) ? 0 : parseFloat(total_money);
            label_date_map[label_name] = success_count + "," + price.toFixed(4) + "," + total_money.toFixed(2);
        }
    }


    // 账单信息标签 product_bill_item
    if (flowMsgDiv.find(".product_bill_item").length > 0) {
        var product_bill_items = flowMsgDiv.find(".product_bill_item .product-bill-line");
        if (product_bill_items.length > 0) {
            var first_item = product_bill_items[0];
            var bill_item_name = $(first_item).attr("name");
            if (isBlank(bill_item_name)) {
                return;
            }
            var product_bills = [];
            for (var product_bill_index = 0; product_bill_index < product_bill_items.size(); product_bill_index++) {
                var product_bill_item = product_bill_items[product_bill_index];
                // 流程id
                var id = $(product_bill_item).find(".product-bill-title").attr("id");
                // 流程标题
                var title = $(product_bill_item).find(".product-bill-title").text();

                // 总的 付款|收款
                var payables_ele = $(product_bill_item).find("input[name='payables']");
                var payables = $(payables_ele).val();
                var payables_name = $(payables_ele).attr("data-param-name");

                //实际 付款|收款
                var actualpayables_ele = $(product_bill_item).find("input[name='actualpayables']");
                var actualpayables = $(actualpayables_ele).val();
                var actualpayables_name = $(actualpayables_ele).attr("data-param-name");

                // 本次 付款|收款
                var thisPayment_ele = $(product_bill_item).find("input[name='thisPayment']");
                var thisPayment = $(thisPayment_ele).val();
                var thisPayment_name = $(thisPayment_ele).attr("data-param-name");
                if (isBlank(thisPayment) || parseFloat(thisPayment)===0) {
                    $(thisPayment_ele).focus();
                    layer.tips("不能为空或0", $(thisPayment_ele));
                    return;
                }

                var product_bill = {};
                product_bill.id = isBlank(id) ? "" : id;
                product_bill.title = isBlank(title) ? "" : title;

                product_bill[actualpayables_name] = parseFloat(isBlank(actualpayables) ? 0 : actualpayables).toFixed(2);
                product_bill[payables_name] = parseFloat(isBlank(payables) ? 0 : payables).toFixed(2);
                product_bill[thisPayment_name] = parseFloat(isBlank(thisPayment) ? 0 : thisPayment).toFixed(2);
                product_bills.push(sortObjectKey(product_bill));
            }
            label_date_map[bill_item_name] = product_bills;
        } else {
            layer.msg('请选择相应账单信息');
            return;
        }
    }

    // 发票信息标签 invoice_info_item
    if (flowMsgDiv.find(".invoice_info_item").length > 0) {
        var invoice_items = flowMsgDiv.find(".invoice_info_item .invoice-info-line");
        if (invoice_items.length > 0) {
            var first_item = invoice_items[0];
            var invoice_item_name = $(first_item).attr("name");
            if (isBlank(invoice_item_name)) {
                return;
            }
            var invoices = [];
            for (var index = 0; index < invoice_items.size(); index++) {
                var invoice_item = invoice_items[index];
                // 流程id
                var id = $(invoice_item).find(".invoice-info-title").attr("id");
                // 流程标题
                var title = $(invoice_item).find(".invoice-info-title").text();
                // 总的 付款|收款
                var payables_ele = $(invoice_item).find("input[name='payables']");
                var payables = $(payables_ele).val();
                var payables_name = $(payables_ele).attr("data-param-name");

                //实际 付款|收款
                var actualpayables_ele = $(invoice_item).find("input[name='actualpayables']");
                var actualpayables = $(actualpayables_ele).val();
                var actualpayables_name = $(actualpayables_ele).attr("data-param-name");

                // 本次 付款|收款
                var thisPayment_ele = $(invoice_item).find("input[name='thisPayment']");
                var thisPayment = $(thisPayment_ele).val();
                var thisPayment_name = $(thisPayment_ele).attr("data-param-name");
                if (isBlank(thisPayment) || parseFloat(thisPayment)===0) {
                    $(thisPayment_ele).focus();
                    layer.tips("不能为空或0", $(thisPayment_ele));
                    return;
                }

                var invoice = {};
                invoice.id = isBlank(id) ? "" : id;
                invoice.title = isBlank(title) ? "" : title;

                invoice[actualpayables_name] = parseFloat(isBlank(actualpayables) ? 0 : actualpayables).toFixed(2);
                invoice[payables_name] = parseFloat(isBlank(payables) ? 0 : payables).toFixed(2);
                invoice[thisPayment_name] = parseFloat(isBlank(thisPayment) ? 0 : thisPayment).toFixed(2);
                invoices.push(sortObjectKey(invoice));
            }
            label_date_map[invoice_item_name] = invoices;
        } else {
            layer.msg('请选择相应发票信息');
            return;
        }
    }

    // 提单信息标签
    if (flowMsgDiv.find("div.order-table").length > 0) {
        var order_tables = flowMsgDiv.find("div.order-table");
        for (var index = 0; index < order_tables.length; index++) {
            var order_table = order_tables[index];
            var label_name = $(order_table).find("label.show-lable").attr('data-label-name');
            var table_name = 'order-table-' + ele_id + label_name;
            layui.use('table', function () {
                var table = layui.table;
                var order_data = table.cache[table_name];
                for (var i = 0; i < order_data.length; i++) {
                    order_data[i] = sortObjectKey(order_data[i]);
                }
                label_date_map[label_name] = JSON.stringify(order_data);
            });
        }
    }
    // 配单信息标签
    if (flowMsgDiv.find("div.match-order-table").length > 0) {
        var order_tables = flowMsgDiv.find("div.match-order-table");
        for (var index = 0; index < order_tables.length; index++) {
            var order_table = order_tables[index];
            var label_name = $(order_table).find("label.show-lable").attr('data-label-name');
            var table_name = 'match-order-table-' + ele_id + label_name;
            layui.use('table', function () {
                var table = layui.table;
                var order_data = table.cache[table_name];
                for (var i = 0; i < order_data.length; i++) {
                    order_data[i] = sortObjectKey(order_data[i]);
                }
                label_date_map[label_name] = JSON.stringify(order_data);
            });
        }
    }

    // 审核意见
    var audit_opinion = flowMsgDiv.find("#audit-opinion").val();
    // 审核数据
    var auit_data = {
        flowEntId: flow_id,
        nodeId: node_id,
        labelValueMap: JSON.stringify(label_date_map),
        baseDataMap: JSON.stringify(base_date),
        operateType: opts_type,
        remark: audit_opinion,
        rejectToNode: rejectToNode,
        platform: 0
    };
    console.log("审核请求信息---：" + JSON.stringify(auit_data));
    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/auditProcess.action",
        dataType: 'json',
        data: auit_data,
        success: function (resp) {
            if (resp.code == 200) {
                // 通过、驳回、取消
                if (opts_type === 2 || opts_type === 3 || opts_type === 4) {
                    window.scrollTo(0, 0);
                    // 刷新工作台气泡
                    if (typeof loadConsoleFlowCount == 'function') {
                        loadConsoleFlowCount();
                    }
                    // 刷新角色上的气泡
                    if (typeof loadRoleFlowCount == 'function') {
                        loadRoleFlowCount();
                    }
                    if (isCustomerConsole) {
                        // 刷新客户统计气泡数据
                        if (typeof renderKHFlowEntCount == "function") {
                            renderKHFlowEntCount();
                        }
                        // 重新加载销售的运营
                        if (typeof reload_sale_operate_info == "function") {
                            reload_sale_operate_info();
                        }
                        // 重新加载销售的结算
                        if (typeof reload_sale_settlement_info == "function") {
                            reload_sale_settlement_info();
                        }
                    } else {
                        // 刷新资源统计气泡数据
                        if (typeof renderZYFlowEntCount == "function") {
                            renderZYFlowEntCount();
                        }
                        // 重新加载运营
                        if (typeof reload_operate_info == "function") {
                            reload_operate_info();
                        }
                        // 重新加载结算
                        if (typeof reload_settlement_info == "function") {
                            reload_settlement_info();
                        }
                    }
                    // 配货工作台审核完后清空页面内容
                    if (typeof clearMatchOrder == "function") {
                        clearMatchOrder();
                    }
                }
                layer.msg(resp.msg);
                flowType = 999;
                $("#flowMsg_" + flow_id).remove();
            } else {
                layer.msg(resp.msg)
            }
        }
    });
}

function get_voice_unit_value(index, gradient_item) {
    if (index === 0) {
        var voiceUnit_ele = $(gradient_item).find("input[name='voiceUnit']");
        if (!isBlank(voiceUnit_ele) && voiceUnit_ele.size() > 0) {
            return $(voiceUnit_ele).val();
        }
    }
    return "";
}


// 验证是否所有的都已经填写
function validation(ele_id) {
    // 当前审核的流程的框
    var flowMsgDiv = $("#" + ele_id);

    // 输入框(必须要求的输入框)
    var required_input = $(flowMsgDiv).find("input[input-required='true']");
    if (!isBlank(required_input)) {
        for (var required_input_index = 0; required_input_index < required_input.size(); required_input_index++) {
            var required_input_temp = required_input[required_input_index];
            var input_value = $(required_input_temp).val();
            if (isBlank(input_value)) {
                $(required_input_temp).focus();
                layer.tips($(required_input_temp).attr("name") + "不能为空", $(required_input_temp));
                return false;
            }
        }
    }

    // 选择下拉框
    var required_select = $(flowMsgDiv).find("select[input-required='true']");
    if (!isBlank(required_select)) {
        for (var required_select_index = 0; required_select_index < required_select.size(); required_select_index++) {
            var required_select_temp = required_select[required_select_index];
            var select_value = $(required_select_temp).val();
            if (isBlank(select_value)) {
                $(required_select_temp).focus();
                layer.tips("请选择" + $(required_select_temp).attr("name"), $(required_select_temp));
                return false;
            }
        }
    }

    // 文本域
    var required_textarea = $(flowMsgDiv).find("textarea[input-required='true']");
    if (!isBlank(required_textarea)) {
        for (var required_textarea_index = 0; required_textarea_index < required_textarea.size(); required_textarea_index++) {
            var required_textarea_temp = required_textarea[required_textarea_index];
            var textarea_value = $(required_textarea_temp).val();
            if (isBlank(textarea_value)) {
                $(required_textarea_temp).focus();
                layer.tips("请填写" + $(required_textarea_temp).attr("name"), $(required_textarea_temp));
                return false;
            }
        }
    }

    // 文件上传按钮框
    var required_file = $(flowMsgDiv).find("button[input-required='true']");
    if (!isBlank(required_file)) {
        for (var required_file_index = 0; required_file_index < required_file.size(); required_file_index++) {
            var required_file_temp = required_file[required_file_index];
            var file_label_name = $(required_file_temp).attr("name");
            var label_upload_file = file_result[file_label_name + ele_id];
            if (isBlank(label_upload_file)) {
                $(required_file_temp).focus();
                layer.tips("请上传" + file_label_name, $(required_file_temp));
                return false;
            }
        }
    }

    var voiceUnit_ele = $(flowMsgDiv).find("input[name = 'voiceUnit']");
    if (!isBlank(voiceUnit_ele) && voiceUnit_ele.size() > 0) {
        if (isBlank($(voiceUnit_ele).val())) {
            $(voiceUnit_ele).focus();
            layer.tips("语音计价单位不能为空", $(voiceUnit_ele));
            return false;
        } else if (!$.isNumeric($(voiceUnit_ele).val())) {
            $(voiceUnit_ele).focus();
            layer.tips("语音计价单位只能为数字", $(voiceUnit_ele));
            return false;
        }
    }

    // 处理梯度价格标签的校验
    var gradinet_items = $(flowMsgDiv).find(".gradient");
    if (!isBlank(gradinet_items) && gradinet_items.size() > 0) {
        for (var gradinet_items_index = 0; gradinet_items_index < gradinet_items.size(); gradinet_items_index++) {
            // 梯度价
            var gradient_item = gradinet_items[gradinet_items_index];
            var price_ele = $(gradient_item).find("input[name='price']");
            if (isBlank($(price_ele).val())) {
                $(price_ele).focus();
                layer.tips("价格不能为空", $(price_ele));
                return false;
            } else if (!$.isNumeric($(price_ele).val())) {
                $(price_ele).focus();
                layer.tips("价格只能为数字", $(price_ele));
                return false;
            }
            var million_ratio_ele = $(gradient_item).find("input[name='million_ratio']");
            if (!isBlank($(million_ratio_ele).val()) && !$.isNumeric($(million_ratio_ele).val())) {
                $(million_ratio_ele).focus();
                layer.tips("百万投比只能是数字", $(million_ratio_ele));
                return false;
            }
            var province_ratio_ele = $(gradient_item).find("input[name='province_ratio']");
            if (!isBlank($(province_ratio_ele).val()) && !$.isNumeric($(province_ratio_ele).val())) {
                $(province_ratio_ele).focus();
                layer.tips("省占比只能为数字", $(province_ratio_ele));
                return false;
            }
        }
    }

    var uniform_gradient_item = $(flowMsgDiv).find(".nogradient");
    if (!isBlank(uniform_gradient_item) && uniform_gradient_item.size() > 0) {
        // 统一价格
        var price_ele = uniform_gradient_item.find("input[name='price']");
        if (!isBlank(price_ele) && price_ele.size() > 0) {
            if (isBlank($(price_ele).val())) {
                $(price_ele).focus();
                layer.tips("价格不能为空", $(price_ele));
                return false;
            } else if (!$.isNumeric($(price_ele).val())) {
                $(price_ele).focus();
                layer.tips("价格只能为数字", $(price_ele));
                return false;
            }
        }

        var provinceprice_ele = uniform_gradient_item.find("input[name='provinceprice']");
        if (!isBlank($(provinceprice_ele).val()) && !$.isNumeric($(provinceprice_ele).val())) {
            $(provinceprice_ele).focus();
            layer.tips("省网价格只能为数字", $(provinceprice_ele));
            return false;
        }
    }

    // 提单信息标签
    if (flowMsgDiv.find("div.order-table").length > 0) {
        var order_tables = flowMsgDiv.find("div.order-table");
        var flag = true;
        for (var index = 0; index < order_tables.length; index++) {
            var order_table = order_tables[index];
            var label_name = $(order_table).find("label.show-lable").attr('data-label-name');
            var table_name = 'order-table-' + ele_id + label_name;
            layui.use('table', function () {
                var table = layui.table;
                var order_data = table.cache[table_name];
                for (var i = 0; i < order_data.length; i++) {
                    if (!$.isNumeric(order_data[i].price) || !$.isNumeric(order_data[i].amount)) {
                        layer.msg('价格和数量只能为数字！');
                        flag = false;
                        break;
                    }
                }
            });
        }
        if (!flag) {
            return flag;
        }
    }
    // 配单信息标签
    if (flowMsgDiv.find("div.match-order-table").length > 0) {
        var order_tables = flowMsgDiv.find("div.match-order-table");
        var flag = true;
        for (var index = 0; index < order_tables.length; index++) {
            var order_table = order_tables[index];
            var label_name = $(order_table).find("label.show-lable").attr('data-label-name');
            var table_name = 'match-order-table-' + ele_id + label_name;
            layui.use('table', function () {
                var table = layui.table;
                var order_data = table.cache[table_name];
                if(order_data.length != 0) {
                    for (var i = 0; i < order_data.length; i++) {
                        if (!$.isNumeric(order_data[i].price) || !$.isNumeric(order_data[i].amount) || !$.isNumeric(order_data[i].logisticsCost)) {
                            layer.msg('价格和数量只能为数字！');
                            flag = false;
                            break;
                        }
                        if (order_data[i].depotItemId) {
                            if (order_data[i].amount > order_data[i].depotNumber) {
                                layer.msg(order_data[i].productname +"的库存数量不够出库的数量，请重新填写!");
                                flag = false;
                                break;
                            }
                        }
                    }
                } else {
                    layer.msg('配单信息不能为空！');
                    flag = false;
                }
            });
        }
        if (!flag) {
            return flag;
        }
    }

    // 审核意见
    var audit_opinion_dom = $(flowMsgDiv).find("#audit-opinion");
    if (audit_opinion_dom.size() > 0) {
        if (isBlank(audit_opinion_dom.val())) {
            $(audit_opinion_dom).focus();
            layer.tips('审核意见不能为空', $(audit_opinion_dom));
            return false;
        }
    }
    return true;
}

// 处理酬金单位
function take_remuneration_unit(parent) {
    var all_unit_input = $(parent).find("input[data-unit]");
    if (!isBlank(all_unit_input) && all_unit_input.size() > 0) {
        for (var all_unit_input_index = 0; all_unit_input_index < all_unit_input.size(); all_unit_input_index++) {
            var temp = all_unit_input[all_unit_input_index];
            var data_uint = $(temp).attr("data-unit");
            $(temp).after("<i class='remuneration_unit'>" + data_uint + "</i>")
        }
    }
}

// 处理发票信息
function take_invoice_info(parent, lable_name, label_value, default_value, requred, disabled, data) {
    var flowEntId = data.flowEntId;
    var productId = data.productId;
    var flowClass = data.flowClass;
    var flowId = data.flowId;
    // 当前节点可操作，并且按钮可用，才显示按钮
    var visable = disabled === '';
    var invoice_info_dom = "<input type='button' onclick='choose_invoice(this, \"" + flowEntId + "\", \"" + productId + "\", \"" + flowClass + "\", \"" + flowId + "\")'";
    invoice_info_dom += " data-label-name='" + lable_name + "' " + disabled + " class='layui-btn layui-btn-sm' value='选择发票' style='width: fit-content;";
    invoice_info_dom += visable ? "'/>" : "display: none'/>";
    $(parent).empty();
    $(parent).append(invoice_info_dom);
    if (!isBlank(label_value) && label_value !== "") {
        // 回显已经有的数据项
        var invoice_info = (typeof label_value == 'object') ? label_value : JSON.parse(label_value);
        take_invoice_item(parent, lable_name, invoice_info, default_value, requred, disabled, flowEntId, flowClass, flowId);
    }
}

// 选择发票
function choose_invoice(ele, flowEntId, productId, flowClass, flowId) {
    var url = '/customerOperate/toQueryInvoice?productId=' + productId + "&flowClass=" + encodeURI(flowClass);
    layui.use(['jquery', 'layer'], function () {
        var layer = layui.layer;
        layer.open({
            type: 2,
            area: ['535px', '355px'], //宽高
            content: url,
            btn: ['选择', '取消'],
            yes: function (index, layero) {
                var checkStatus = window["layui-layer-iframe" + index].layui.table.checkStatus('invoicelist');
                var parent = $(ele).parent();
                var label_name = $(ele).attr("data-label-name");
                take_invoice_item(parent, label_name, checkStatus.data, "", "", "", flowEntId, flowClass, flowId);
                layer.close(index);
            }
        });
    });
}

// 回显原有的数据选项
function take_invoice_item(parent, lable_name, invoice_info, default_value, required, disabled, flowEntId, flowClass, flowId) {
    if (!isBlank(invoice_info) && invoice_info.length > 0) {
        var invoice_info_dom = "<div class='layui-form-item invoice_info_item'>";
        var this_time_pay = 0;
        // 展示名称
        var show_name = product_bill_label_name[flowClass];
        // 输入框名称
        var input_name = product_bill_input_name[flowClass];
        // 总计名称
        var total_name = show_name.total_name;
        for (var bill_index = 0; bill_index < invoice_info.length; bill_index++) {
            // 计费信息
            var bill_info = invoice_info[bill_index];
            // 应该 支付|收款
            var payables_name = input_name.payables_name;
            var payables = isBlank(bill_info[payables_name]) ? 0 : parseFloat(bill_info[payables_name]);

            // 实际(已经) 支付|收款
            var actualpayables_name = input_name.actualpayables_name;
            var actualpayables = isBlank(bill_info[actualpayables_name]) ? 0 : parseFloat(bill_info[actualpayables_name]);

            // 本次 支付|收款
            var thisPayment_name = input_name.thisPayment_name;
            var thisPayment = isBlank(bill_info[thisPayment_name]) ? 0 : parseFloat(bill_info[thisPayment_name]);

            // 剩余应该 支付|收款（固定不变[要求]）
            var left_should_pay_name = input_name.left_should_pay_name;
            var left_should_pay = payables - actualpayables;
            // 去除未审核和驳回的账单金额
            var type = '';
            if (flowClass === '[BillReceivablesFlow]') {
                type = 'thisReceivables';
            }
            if (type !== '') {
                $.ajax({
                    type: "POST",
                    url: '/operate/queryApplying.action?v=' + new Date().getTime(),
                    data: {
                        flowId: flowId,
                        billId: bill_info.id,
                        type: type,
                        flowEntId: flowEntId
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

            // 本次应付（总）
            this_time_pay += parseFloat(thisPayment);

            invoice_info_dom = invoice_info_dom +
                "<div class='invoice-info-line' name=" + lable_name + ">" +
                "    <span class='invoice-info-title' id='" + bill_info.id + "'>" + bill_info.title + "</span>" +
                "    <label class='layui-form-label'><span>" + show_name.payables_name + "</span></label>" +
                "    <div class='layui-input-block'><input name='payables' data-param-name='" + payables_name + "' class='layui-input pay_detail' value='" + payables.toFixed(2) + "' disabled  data-unit='元'/>" +
                "    </div>" +
                "    <label class='layui-form-label'><span>" + show_name.actualpayables_name + "</span></label>" +
                "    <div class='layui-input-block'><input name='actualpayables' data-param-name='" + actualpayables_name + "' class='layui-input pay_detail' value='" + actualpayables.toFixed(2) + "' disabled  data-unit='元'/>" +
                "    </div>" +
                "    <label class='layui-form-label'><span>" + show_name.left_should_pay_name + "</span></label>" +
                "    <div class='layui-input-block'><input name ='left_should_pay' data-param-name='" + left_should_pay_name + "' class='layui-input pay_detail' value='" + left_should_pay.toFixed(2) + "'  disabled  data-unit='元'/>" +
                "    </div>" +
                "    <label class='layui-form-label'><span>" + show_name.thisPayment_name + "</span></label>" +
                "    <div class='layui-input-block'><input name='thisPayment' data-param-name='" + thisPayment_name + "' onchange='take_invoice_input_change(this)' class='layui-input pay_detail' value='" + thisPayment.toFixed(2) + "' " + disabled + " data-unit='元' />";
            if (isBlank(disabled)) {
                invoice_info_dom = invoice_info_dom + "<i onclick='remove_invoice_item(this)' class='layui-icon layui-icon-delete delete-paroduct-bill'></i>" +
                    "</div>";
            } else {
                invoice_info_dom = invoice_info_dom + "</div>";
            }
            invoice_info_dom = invoice_info_dom + "</div>";
        }
        invoice_info_dom = invoice_info_dom + "<i class='bill_count_tip'>" + total_name + "<span class='invoice_this_time_pay'>" + format_num(this_time_pay, 2) + "</span>&nbsp;元</i>";
        invoice_info_dom = invoice_info_dom + "</div>"; // from item
        // 获取紧邻的标签
        var next = $(parent).parent().next();
        if ($(next).hasClass("invoice_info_item")) {
            // 删除已有的标签
            $(next).remove();
        }
        $(parent).parent().after(invoice_info_dom);
        take_invoice_unit(parent);
    }
}

// 处理价格单位
function take_invoice_unit(parent) {
    var next = $(parent).parent().next();
    if ($(next).hasClass("invoice_info_item")) {
        var inputs = $(next).find("input[class*='pay_detail']");
        if (!isBlank(inputs)) {
            for (var input_index = 0; input_index < inputs.size(); input_index++) {
                var input_temp = inputs[input_index];
                var unit = $(input_temp).attr("data-unit");
                if (isNotBlank(unit)) {
                    $(input_temp).after("<i class='invoice-money-unit'>" + unit + "</i>");
                }
            }
        }
    }
}

// 删除产品账单项
function remove_invoice_item(ele) {
    var invoice_form_item = $(ele).parents(".invoice_info_item");
    $(ele).parents(".invoice-info-line").remove();
    count_invoice_input(invoice_form_item);
}

// 监听本次支付改变值时处理时间
function take_invoice_input_change(ele) {
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
    count_invoice_input($(ele).parents(".invoice_info_item"));
}

// 计算本次应付金额
function count_invoice_input(ele) {
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

// 发票
function typeInvoiceToString(labelValue, defaultValue, flowEntId, flowClass, flowId) {
    var html = "";
    if (!isBlank(labelValue)) {
        var invoiceItems = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
        if (!isBlank(invoiceItems) && invoiceItems.length > 0) {
            // 本次收付款
            var this_time_pay = 0;
            // 展示名称
            var show_name = product_bill_label_name[flowClass];
            // 输入框名称
            var input_name = product_bill_input_name[flowClass];
            // 总计名称
            var total_name = show_name.total_name;
            $.each(invoiceItems, function (i, productBill) {
                // 应该 支付|收款
                var payables_name = input_name.payables_name;
                var payables = isBlank(productBill[payables_name]) ? 0 : parseFloat(productBill[payables_name]);

                // 实际(已经) 支付|收款
                var actualpayables_name = input_name.actualpayables_name;
                var actualpayables = isBlank(productBill[actualpayables_name]) ? 0 : parseFloat(productBill[actualpayables_name]);

                // 本次 支付|收款
                var thisPayment_name = input_name.thisPayment_name;
                var thisPayment = isBlank(productBill[thisPayment_name]) ? 0 : parseFloat(productBill[thisPayment_name]);

                // 剩余应该 支付|收款（固定不变[要求]）
                var left_should_pay_name = input_name.left_should_pay_name;
                var left_should_pay = payables - actualpayables;

                // 去除未审核和驳回的账单金额
                var type = '';
                if (flowClass === '[BillReceivablesFlow]') {
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
                            flowEntId: flowEntId
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

                // 本次应付（总）
                this_time_pay += parseFloat(thisPayment);

                html += "<br/>" + productBill.title + "<br/>";
                html += show_name.payables_name + payables.toFixed(2) + "元，";
                html += show_name.actualpayables_name + actualpayables.toFixed(2) + "元，";
                html += show_name.left_should_pay_name + left_should_pay.toFixed(2) + "元，";
                html += show_name.thisPayment_name + thisPayment.toFixed(2) + "元";
            });
            html += "<br/>" + total_name + format_num(this_time_pay, 2) + "元";
            return html;
        }
    }
    return "无";
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

// 获取格式化的数字
function get_format_num(format_str) {
    return format_str.replace(/,/g, "");
}

// 接收“yyyy-MM-dd HH:mm:ss”格式的日期时间字符串，转为“dd日 HH:mm:ss 周几”
function dateToWeek(str) {
    var tempStrs = str.split(" ");
    var dateStrs = tempStrs[0].split("-");
    var year = parseInt(dateStrs[0], 10);
    var month = parseInt(dateStrs[1], 10) - 1;
    var day = parseInt(dateStrs[2], 10);
    var date = new Date(year, month, day);
    var weekday = "日一二三四五六".charAt(date.getDay());
    return day + "日 " + tempStrs[1] + " 周" + weekday;
}

// 去配货（查看详情）1去配货，2查看详情
function matchOrder(type, entId) {
    var type = type;
    $.ajax({
        type: "POST",
        async: true,
        url: '/flow/flowDetail.action?id=' + entId + "&temp=" + Math.random(),
        dataType: 'json',
        data: {},
        success: function (data) {
            // 渲染标签
            if (typeof setData == "function") {
                 setData(type,data.data);
            }
        }
    });
}