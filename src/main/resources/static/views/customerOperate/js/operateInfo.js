var type;
// 流程类型 0运营，1结算
var flowType = 999;

var layer;
var table;
var element;
var isLock = false;

$(document).ready(function () {
    layui.use(['layer', 'element','table'], function () {
        layer = layui.layer;
        table = layui.table;
        element = layui.element;
        // 展开年份
        element.on('collapse(operate_year_title)', function (data) {
            // 点击年标题 yyyy
            sale_operate_year = data.content.attr("operate-title-id");
            return false;
        });
        // 展开月份时加载当月流程记录
        element.on('collapse(operate_month_title)', function (data) {
            // 点击月标题 yyyy-MM
            if (data.show) {
                sale_operate_month = data.content.attr("operate-name-id");
                scrollToLoadCustomerFlow('operate');
            }
        });
        loadCustomerAllOperate('');
    });
});

// 初始化流程年月标题折叠框
function initCustomerOperatePanel() {
    var operatePanel = new myPannel({});
    operatePanel.init("#operate_panel");
    element.render("collapse", "operate_year_title");
}

// 加载客户的运营栏
function loadCustomerOperate(typeInt, productid) {
    type = 99;
    if (isNotBlank(typeInt)) {
        if (typeInt == 1 || typeInt == 0) {
            type = 1;
        }
    }
    productId = productid; //commonJs公共参数
    $.ajaxSettings.async = true;
    $.post("/customerOperate/queryOperate.action?productId=" + productid + "&type=" + type, {}, function (data) {
        if (isNotBlank(data)) {
            $('#operateCard').html(data);
            // 初始化折叠框
            initCustomerOperatePanel();
            // 展开最后一个月的流程
            openLastCustomerOperate();
            // 流程发起按钮区域
            var buttonBody = $('#operateCard').find('#buttonBody');
            if (buttonBody != null && buttonBody.length === 1) {
                initCustOpFlowButton(1, 0, productid);
            }
            if (!isNull(customer_module_open)) {
                customer_module_open.render();
            }
        }
    }, 'html');
    // $.ajaxSettings.async = false;
}

// 加载客户下所有产品的运营时间
function loadCustomerAllOperate(customerid) {
    var loadingIndex = layer.load(2);
    // console.time('加载全部运营时间耗时');
    productId = '';
    customerId = customerid;
	$.ajaxSettings.async = true;
	$.post("/customerOperate/queryAllOperate.action", {"customerId": customerid}, function (data) {
		if (isNotBlank(data)) {
			$('#operateCard').html(data);
			// 初始化折叠框
			initCustomerOperatePanel();
			// 展开最后一个月的流程
			openLastCustomerOperate();
			// 流程发起按钮区域
			if (customerid) {
				var buttonBody = $('#operateCard').find('#buttonBody');
				if (buttonBody != null && buttonBody.length === 1) {
					initCustOpFlowButton(1, 0, '', customerid);
				}
			}
			if (!isNull(customer_module_open)) {
				customer_module_open.render();
			}
		}
		// console.timeEnd('加载全部运营时间耗时');
		layer.close(loadingIndex);
	}, 'html');
    // $.ajaxSettings.async = false;
}

// 展开最近1个月的流程记录
function openLastCustomerOperate() {
    var years = $("#operate_panel").find("div[data-my-opts-type='year']");
    if (!isBlank(years) && years.length > 0) {
        var last_year = years[years.length - 1];
        $(last_year).trigger("click");
    }

    var months = $("#operate_panel").find("div[data-my-opts-type='month']");
    if (!isBlank(months) && months.length > 0) {
        var last_month = months[months.length - 1];
        $(last_month).trigger("click");
    }
}

// 加载某一个月的流程记录
function loadCustomerOperateByPage(date, productid, page, pageSize, callback) {
    // console.time("分页加载运营标题耗时");
    if (isNotBlank(date)) {
        var data;
        var url;
        if (productId == '') { // 查询所有产品的运营记录
            url = "/operate/getAllOperateByPage.action";
            data = {
                "date": date,
                "supplierId": customerId,
                "entityType": 1,
                "page": page,
                "pageSize": pageSize
            }
        } else { // 查询指定产品的运营记录
            url = "/operate/getOperateByPage.action";
            data = {
                "date": date,
                "productId": productId,
                "entityType": 1,
                "page": page,
                "pageSize": pageSize
            }
        }
        $.ajax({
            type: "POST",
            async: false,
            url: url,
            dataType: 'json',
            data: data,
            success: function (resp) {
                if (resp.code === 200) {
                    var loadingIndex = layer.load(2);
                    var recordDetail = resp.data;
                    operatePages = resp.msg;
                    var flowEntIds = [];
                    var html = '<div style="padding-left: 15px;">没有更多了</div>';
                    if (recordDetail !== undefined && recordDetail !== null && recordDetail.length > 0) {
                        html = '';
                        $.each(recordDetail, function (i, item) {
                            flowEntIds.push(item.id);
                            var s = '<div class="operate-title" style="cursor: pointer;" id="' + item.id + '" entId = "' + item.id + '" productId = "' + item.productId + '"><b><span>';
                            var applyTime = item.applyTime;
                            applyTime = applyTime.substring(8);
                            s += applyTime + "日 " + item.flowTitle;
                            /*if (isNotBlank(item.nodeName)) {
                                s += ',节点:' + item.nodeName
                            }*/
                            s += '</span>';
                            if (item.canOperat) {
                                s += '<span style = "color:red">(待处理)</span>'
                            }
                            if (item.flowStatus === "取消"){
                                s += "<span class='flow-state-cancel'>已取消</span>"
                            }else if (item.flowStatus === "归档"){
                                s += "<span class='flow-state-document'>已归档</span>"
                            }else{
                                s += "<span class='flow-state-process'>进行中</span>"
                            }
                            html += s + "</b></div><hr style='height: 3px; background-color: #1E9FFF'/>"
                        });
                    }
                    var operate_ele = $("div[operate-name-id=" + date + "]");
                    operate_ele.append(html);
                    // console.timeEnd("分页加载运营标题耗时");
                    // console.time("初始化运营标题事件耗时");
                    for (var i = 0; i < flowEntIds.length; i++) {
                        $("#" + flowEntIds[i]).unbind().bind('click', function () {
                            var ele = $(this);
                            var entId = $(this).attr('entId');
                            flowType = 0; // 运营的流程
                            $.ajax({
                                type: "POST",
                                async: true,
                                url: '/flow/flowDetail.action?id=' + entId + "&temp=" + Math.random(),
                                dataType: 'json',
                                data: {},
                                success: function (data) {
                                    // 渲染标签
                                    if (typeof renderFlowMsg == "function") {
                                        // console.log("================渲染标签================");
                                        renderFlowMsg(ele, data.data);
                                        element.render("collapse", "operate_month_title");
                                    }
                                }
                            });
                        });
                    }
                    // console.timeEnd("初始化运营标题事件耗时");
                    loadOperateDetailByPage(flowEntIds, callback);
                    layer.close(loadingIndex);
                }
            }
        });
    }
}

/**
 * 加载运营流程详情
 * @param ids
 * @param callback
 */
function loadOperateDetailByPage(ids,callback) {
    if (!(Object.prototype.toString.call(ids) === '[object Array]') || ids.length === 0) {
        // 不是数组
        return;
    }
    // console.time("加载运营流程详情耗时" + ids);
    $.ajax({
        type: "POST",
        async: false,
        url: '/flow/getFlowDetailByPage.action?temp=' + Math.random(),
        dataType: 'json',
        data: {
            'ids': ids.join(",")
        },
        success: function (data) {
            var details = isBlank(data.data) ? [] : data.data;
            // 渲染标签
            // console.time("渲染5个运营流程详情耗时");
            for (var i = 0; i < ids.length; i++) {
                // console.time("渲染1个运营流程详情耗时");
                renderFlowMsg($("#" + ids[i]), details[ids[i]]);
                // console.timeEnd("渲染1个运营流程详情耗时");
            }
            // console.timeEnd("渲染5个运营流程详情耗时");
            element.render("collapse", "operate_month_title"); // 放在后面统一render
            // console.timeEnd("加载运营流程详情耗时" + ids);
            if (typeof callback == "function") {
                callback();
            }
        }
    });
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}

// 获取所有标签的数据
function getAlldata(editLabelIds, flow, iframe, flowClass, flowId) {
    var data = {};
    $(flow).each(function (i, item) {
        var value = '';
        if (editLabelIds.indexOf(item.id) >= 0) {
            var type = parseInt(item.type);
            if (type === 3) { // 布尔类型单选框
                value = iframe.find("input[name='" + item.id + "']:checked").val();
                data[item.name] = value;
            } else if (type === 8) { // 文件
                value = iframe.find(".layui-show").find("#" + item.id).val();
                if (isNotBlank(file_result)) {
                    // 取完文件后，清除文件上传中本标签的文件内容
                    delete file_result[item.id];
                }
                data[item.name] = value;
            } else if (type === 10) { // 调价梯度
                value = getGradient(iframe);
                data[item.name] = JSON.stringify(value);
            } else if (type === 13) { // 酬金类型
                value = getRremuneration(iframe);
                data[item.name] = value;
            } else if (type === 14) { // 账单信息
                value = accountData(iframe, flowClass);
                data[item.name] = JSON.stringify(value);
            } else if (type === 15) { // 账单金额
                value = accountAmountData(iframe, item.id);
                data[item.name] = value;
            } else if (type === 23) { // 发票信息
                value = invoiceData(iframe, flowClass);
                data[item.name] = JSON.stringify(value);
            } else if (type === 24) { // 提单信息
                value = getApplyOrderData(iframe, flowId);
                data[item.name] = JSON.stringify(value);
            } else if(type === 29) { // 客户发票抬头
                value = getCustInvoiceData(iframe);
                data[item.name] = JSON.stringify(value);
            } else if(type === 30) { // 账单开票信息
                value = getBillInvoiceInfo(iframe);
                data[item.name] = JSON.stringify(value);
            } else if (type === 32){ // 时间段账单信息
                value = timeAccountBillData(iframe, item.id, item.name);
                if (value == null || value.length === 0) {
                    return;
                }
                data[item.name] = JSON.stringify(value);
            } else if (type === 33){ // 平台账号信息
                value = platformAccountInfoData(iframe, item.id, item.name, false);
                if (value == null || value.length === 0) {
                    return;
                }
                data[item.name] = JSON.stringify(value);
            } else if(type === 34) { // 未对账账单
                value = getUncheckedBill(iframe, flowId);
                data[item.name] = JSON.stringify(value);
            } else if (type === 35) { // 时间段
                var datetime = iframe.find(".layui-show").find('#' + item.id).val();
                var days = iframe.find(".layui-show").find('span.time-duration').text();
                value = {
                    datetime: datetime,
                    days: days
                }
                data[item.name] = JSON.stringify(value);
            } else if (type === 36) { // 请假类型单选框
                value = iframe.find(".layui-show").find('input[name="radio-' + flowId + '-' + item.id + '"]:checked').val();
                data[item.name] = value;
            } else if (type === 37) {
                // 充值详情
            	var arr = [];
            	iframe.find('.account-recharge').each(function (i, item) {
            		arr.push({
            			rechargeAccount: $(item).find('[name="rechargeAccount"]').val(),
            			currentAmount: $(item).find('[name="currentAmount"]').val(),
            			price: $(item).find('[name="price"]').val(),
            			rechargeAmount: $(item).find('[name="rechargeAmount"]').val(),
            			pieces: $(item).find('[name="pieces"]').val()
            		});
            	});
            	data[item.name] = JSON.stringify(arr);
            } else if (type === 38) { // 请假类型单选框
                value = iframe.find(".layui-show").find('input[name="' + item.id + '"]:checked').val();
                data[item.name] = value;
            } else {
                value = iframe.find(".layui-show").find("#" + item.id).val();
                data[item.name] = value;
            }
        }
    });
    return data;
}

function getUncheckedBill(iframe, flowId) {
    var value = {};
    var billInfos = [];
    var billIds = [];
    var billTotal = {
        'platformSuccessCount': 0,
        'platformAmount': 0.0,
        'checkedSuccessCount': 0,
        'checkedAmount': 0.0
    }
    // 获取选中的账单复选框
    var checkedBoxes = $(iframe).find('div.unchecked-bill-item > input[type=checkbox]:checked');
    $(checkedBoxes).each(function() {
        var billInfo = {};
        var itemEle = $(this).parent();
        billInfo['id'] = itemEle.attr('id');
        billIds.push(itemEle.attr('id'));
        billInfo['title'] = $(this).attr('title');

        var platformEle = $(itemEle).find('div[data-type=platform]');
        var platformSuccessCountEle = platformEle.find('input[name=successCount]');
        var platformUnitPriceEle = platformEle.find('input[name=unitPrice]');
        var platformAmountEle = platformEle.find('input[name=amount]');
        billInfo['platformSuccessCount'] = platformSuccessCountEle.val();
        billInfo['platformUnitPrice'] = platformUnitPriceEle.val();
        billInfo['platformAmount'] = platformAmountEle.val();
        billTotal.platformSuccessCount = billTotal.platformSuccessCount + parseInt(billInfo.platformSuccessCount);
        billTotal.platformAmount = accAdd(billTotal.platformAmount, billInfo.platformAmount);

        var customerEle = $(itemEle).find('div[data-type=customer]');
        var customerSuccessCountEle = customerEle.find('input[name=successCount]');
        var customerUnitPriceEle = customerEle.find('input[name=unitPrice]');
        var customerAmountEle = customerEle.find('input[name=amount]');
        billInfo['customerSuccessCount'] = customerSuccessCountEle.val();
        billInfo['customerUnitPrice'] = customerUnitPriceEle.val();
        billInfo['customerAmount'] = customerAmountEle.val();

        var checkedEle = $(itemEle).find('div[data-type=checked]');
        var checkedSuccessCountEle = checkedEle.find('input[name=successCount]');
        var checkedUnitPriceEle = checkedEle.find('input[name=unitPrice]');
        var checkedAmountEle = checkedEle.find('input[name=amount]');
        billInfo['checkedSuccessCount'] = checkedSuccessCountEle.val();
        billInfo['checkedUnitPrice'] = checkedUnitPriceEle.val();
        billInfo['checkedAmount'] = checkedAmountEle.val();
        billTotal.checkedSuccessCount = billTotal.checkedSuccessCount + parseInt(billInfo.checkedSuccessCount);
        billTotal.checkedAmount = accAdd(billTotal.checkedAmount, billInfo.checkedAmount);

        billInfos.push(sortObjectKey(billInfo));
    });
    value['billInfos'] = billInfos;
    value['billTotal'] = billTotal;
    // 电子账单
    var billFile = file_result['billFile_' + flowId];
    if (isNotBlank(billFile)) {
        value['billFile'] = billFile;
    } else {
        var optionBoxes = $(iframe).find('div.unchecked-bill-file > input[name=billFile]:checked');
        var options = []
        $(optionBoxes).each(function () {
            options.push($(this).val());
        })
        var loading = layui.layer.load(2);
        $.ajax({
            type: "POST",
            async: false,
            url: "/bill/buildCheckBillFile",
            dataType: 'json',
            data: {
                billIds: billIds.join(','),
                billTotal: JSON.stringify(billTotal),
                options: options.join(',')
            },
            success: function (res) {
                layui.layer.close(loading);
                if (res.code == 200) {
                    billFile = res.data;
                    value['billFile'] = billFile;
                    file_result['billFile_' + flowId] = billFile;
                } else {
                    layui.layer.msg(res.msg);
                }
            }
        })
    }
    // 数据报告
    var analysisFile = file_result['analysisFile_' + flowId];
    if (isNotBlank(analysisFile)) {
        value['analysisFile'] = analysisFile;
    } else {
        var loading = layui.layer.load(2);
        $.ajax({
            type: "POST",
            async: false,
            url: "/bill/buildDataAnalysisFile",
            dataType: 'json',
            data: {
                billIds: billIds.join(','),
            },
            success: function (res) {
                layui.layer.close(loading);
                if (res.code == 200) {
                    analysisFile = res.data;
                    value['analysisFile'] = analysisFile;
                    file_result['analysisFile_' + flowId] = analysisFile;
                } else {
                    layui.layer.msg(res.msg);
                }
            }
        })
    }
    return value;
}

// 获取账单开票信息
function getBillInvoiceInfo(iframe) {
   var accountData = [];
    var tablcChecked = table.checkStatus('selectBillInfo');
    var data = tablcChecked.data;
    $.each(data,function (i,item) {
        accountData.push(sortObjectKey({
            id: item.id,
            title: item.title,
            receivables: item.receivables,                                                                  // 应开
            actualInvoiceAmount: item.actualInvoiceAmount,                                                  // 已开
            invoiceableAmount: accSub(accSub(item.receivables, item.actualInvoiceAmount), item.usedAmount), // 可开
            thisReceivables: $('#thisBillPayment_'+item.id) .val()                                          // 本次开票
        }))
    });
    return accountData;
}


/**
 * 获取时间账单数据
 * @param iframe
 * @param labelId
 * @param labelName
 */
function timeAccountBillData(iframe, labelId, labelName) {
    var dataDom = iframe.find(".layui-show").find("#" + labelId);
    var dataBillItems = $(dataDom).find("div[data-bill-index]");
    if (dataBillItems.length === 0) {
        return null;
    }
    console.log("开始获取 时间标签 数据 ------2")

    var resultDatas = [];
    for (var index = 0; index < dataBillItems.length; index++) {
        var item = dataBillItems[index];
        var itemIndex = $(item).attr("data-bill-index");
        var timeStart = $(item).find("input[name='time_start']").val();
        if (isBlank(timeStart)) {
            layer.msg(labelName + ':第' + itemIndex + "时间段，开始日期不能为空");
            return null;
        }
        var timeEnd = $(item).find("input[name='time_end']").val();
        if (isBlank(timeEnd) && (itemIndex !== dataBillItems.length)) {
            layer.msg(labelName + ':第' + itemIndex + "时间段，结束日期不能为空");
            return null;
        }
        var timeSuccess = $(item).find("input[name='time_success']").val();
        if (isBlank(timeSuccess) || timeSuccess <= 0 || !(/^[0-9]*$/.test(timeSuccess))) {
            layer.msg(labelName + ':第' + itemIndex + "时间段，成功数不能为空且为大于0的整数");
            return null;
        }
        var timePrice = $(item).find("input[name='time_price']").val();
        if (isBlank(timePrice) || timePrice <= 0) {
            layer.msg(labelName + ':第' + itemIndex + "时间段，单价不能为空且大于0");
            return null;
        }
        var timeTotalMoney = $(item).find("input[name='time_total_money']").val();
        resultDatas.push({
            index: itemIndex,
            start: timeStart,
            end: timeEnd,
            success: timeSuccess,
            price: timePrice,
            total: timeTotalMoney
        });
    }
    return resultDatas;
}


// 提单信息
function getApplyOrderData(iframe, flowId) {
    var orderTableName = "order-table-" + flowId;
    var orderTableData = '[]';
    layui.use('table', function () {
        var table = layui.table;
        var orderData = table.cache[orderTableName];
        for (var i = 0; i < orderData.length; i++) {
            orderData[i] = sortObjectKey(orderData[i]);
        }
        orderTableData = orderData;
    });
    return orderTableData;
}

// 账单金额
function accountAmountData(iframe, id) {
    var inputs = iframe.find("." + id);
    var supplier_success_value = inputs.find("input[name='supplier_success']").val();
    var supplier_price_value = inputs.find("input[name='supplier_price']").val();
    var total_money_value = inputs.find("input[name='total_money']").val();

    if (isBlank(supplier_success_value) || isBlank(supplier_price_value)) {
        return null;
    }
    return supplier_success_value + "," + supplier_price_value + "," + total_money_value;
}

// 封装账单信息标签内容
function accountData(iframe, flowClass) {
    var accountData = [];
    var accounts = iframe.find(".layui-show").find('.product-bill-line');
    if (accounts.length < 1) {
        return null;
    }
    $(accounts).each(function (i, item) {
        item = $(item);
        var id = item.find(".product-bill-title").attr("id");
        var title = item.find(".product-bill-title").text();

        var payables = item.find("input[name='payables']").val();
        var actualpayables = item.find("input[name='actualpayables']").val();
        var thisPayment = item.find("input[name='thisPayment']").val();

        if (isBlank(thisPayment) || parseFloat(thisPayment) === 0) {
            layer.msg('本次收付款金额不能为空或0');
            return null;
        }

        if (flowClass === '[BillPaymentFlow]') { // 账单付款流程
            accountData.push(sortObjectKey({
                id: id,                                                                 // 账单id
                title: title,                                                           // 账单标题
                payables: payables,                                                     // 应付
                actualpayables: actualpayables,                                         // 已付
                thisPayment: thisPayment                                                // 本次付款
            }));
        } else if (flowClass === '[RemunerationFlow]') { // 酬金流程
            accountData.push(sortObjectKey({
                id: id,                                                                 // 账单id
                title: title,                                                           // 账单标题
                receivables: payables,                                                  // 应收
                actualReceivables: actualpayables,                                      // 已收
                thisReceivables: thisPayment                                            // 本次收款
            }));
        } else if (flowClass === '[BillReceivablesFlow]') { // 销售收款流程
            accountData.push(sortObjectKey({
                id: id,                                                                 // 账单id
                title: title,                                                           // 账单标题
                receivables: payables,                                                  // 应收
                actualReceivables: actualpayables,                                      // 已收
                thisReceivables: thisPayment                                            // 本次收款
            }));
        } else if (flowClass === '[InvoiceFlow]') {
            accountData.push(sortObjectKey({
                id: id,
                title: title,
                receivables: payables,                                                  // 应开
                actualInvoiceAmount: actualpayables,                                    // 已开
                thisReceivables: thisPayment                                            // 本次开票
            }))
        }
    });
    return accountData;
}

function getRremuneration(iframe) {
    var remunerations = iframe.find(".layui-show").find(".remuneration");

    var money = remunerations.find("input[name='money']").val();
    var rate = remunerations.find("input[name='rate']").val();
    var reward = remunerations.find("input[name='reward']").val();
    var deduction = remunerations.find("input[name='deduction']").val();

    money = isBlank(money) ? 0 : parseFloat(money);
    rate = isBlank(rate) ? 0 : parseFloat(rate);
    reward = isBlank(reward) ? 0 : parseFloat(reward);
    deduction = isBlank(deduction) ? 0 : parseFloat(deduction);
    var remuneration = (money * rate) / 100 + reward - deduction;
    if (remuneration < 0) {
        layer.msg('酬金不能是负数');
        return null;
    }
    return money.toFixed(2) + "," + rate.toFixed(2) + "," + reward.toFixed(2) + "," + deduction.toFixed(2) + "," + remuneration.toFixed(2)
}

// 获取每个 客户开票抬头（抬头、已收金额、已开金额）
function getCustInvoiceData(iframe) {
    var custInvoiceData = [];
    var index = iframe.find(".layui-show").attr("id");
    var count = parseInt(iframe.find("#custInvoiceCount").val());
    // 遍历每张票
    for (var i = 0; i < count; i++) {
        // 客户开票抬头信息
        var custInvoiceInfo = iframe.find("#" + index + 'CustInvoice' + i).val();
        // 开票金额
        var invoicePrice = iframe.find("#" + index + 'InvoiceInfo' + i).val();
        // 已收金额
        var receivablesPrice = iframe.find("#" + index + 'Receivables' + i).val();
        var json = {};
        json['custInvoiceInfo'] = custInvoiceInfo;
        json['thisReceivables'] = invoicePrice;
        json['receivables'] = receivablesPrice;
        custInvoiceData.push(sortObjectKey(json));
    }
    return custInvoiceData;
}

function getGradient(iframe) {
    var gradientData = [];
    var index = iframe.find(".layui-show").attr("id");
    var e = iframe.find(".layui-show").find(".nogradient" + index);
    var voiceUnit = iframe.find("#" + index + "voiceUnit").val();
    if (voiceUnit !== undefined) {
        if (voiceUnit.length === 0 || voiceUnit === '0') {
            layer.msg('语音产品必须填计费单位');
            return null;
        }
    }
    if (e.css('display') === 'none') {// 统一价被隐藏了，说明价格类型不是统一价
        var count = parseInt(iframe.find("#count").val());
        var minId = "gradientmin";
        var maxId = "gradientmax";
        var priceId = "price";
        var mRatioId = "millionRatio";
        var pRatioId = "provinceRatio";
        var defaultGradient = iframe.find("input[name='" + index + "defaultGradient']:checked").val();
        if (count === 1) {
            layer.msg('至少有两个梯度');
            return null;
        }
        for (var i = 0; i < count; i++) {
            var min = iframe.find("#" + index + minId + i).val();
            var max = iframe.find("#" + index + maxId + i).val();
            if (count > 1 && i === count - 1) {
                if (max !== "") {
                    layer.msg('最后一个梯度的最大值应该为空，表示正无穷');
                    return null;
                }
                var price = iframe.find("#" + index + priceId + i).val();
                var prePrice = iframe.find("#" + index + priceId + (i - 1)).val();

                if (parseFloat(prePrice) < parseFloat(price)) {
                    layer.msg('最后一个梯度的价格不能大于上一梯度');
                    return null;
                }

            }
            var price = iframe.find("#" + index + priceId + i).val();
            if (!price || price == 0) {
                layer.msg('梯度里价格不能为空或0');
                return null;
            }
            var mRatio = iframe.find("#" + index + mRatioId + i).val();
            var pRatio = iframe.find("#" + index + pRatioId + i).val();
            var isdefault = 0;
            if (defaultGradient == i) {
                isdefault = 1;
            }
            var json = {};
            json['isdefault'] = isdefault;                                  // 是否默认
            json['minsend'] = min;                                          // 最小值
            json['maxsend'] = max;                                          // 最大值
            json['price'] = price;                                          // 价格
            json['provinceproportion'] = pRatio;                            // 省占比
            json['complaintrate'] = mRatio;                                 // 投诉比
            json['gradient'] = i;                                           // 梯度下标
            json['priceType'] = 'gradient';                                 // 梯度价
            if (i == 0 && voiceUnit) {
                json['voiceUnit'] = voiceUnit;
            }
            gradientData.push(sortObjectKey(json));
        }
    } else {
        var price = iframe.find("#" + index + "price").val();
        if (!price || price == 0) {
            layer.msg('统一价价格不能为空或0');
            return null;
        }
        var provinceprice = iframe.find("#" + index + "pPrice").val();
        var json = {};
        json['price'] = price;                                           // 价格
        json['provinceprice'] = provinceprice;                           // 省网价格
        json['priceType'] = 'uniform';                                   // 统一价
        if (voiceUnit) {
            json['voiceUnit'] = voiceUnit;
        }
        gradientData.push(sortObjectKey(json));
    }
    return gradientData;
}

// 封装发票信息标签内容
function invoiceData(iframe, flowClass) {
    var invoiceData = [];
    var invoiceItems = iframe.find(".layui-show").find('.invoice-info-line');
    if (invoiceItems.length < 1) {
        return null;
    }
    $(invoiceItems).each(function (i, item) {
        item = $(item);
        var id = item.find(".invoice-info-title").attr("id");
        var title = item.find(".invoice-info-title").text();

        var payables = item.find("input[name='payables']").val();
        var actualpayables = item.find("input[name='actualpayables']").val();
        var thisPayment = item.find("input[name='thisPayment']").val();

        if (isBlank(thisPayment) || parseFloat(thisPayment) === 0) {
            layer.msg('本次收付款金额不能为空或0');
            return null;
        }

        if (flowClass === '[BillReceivablesFlow]') { // 销售收款流程
            invoiceData.push(sortObjectKey({
                id: id,                                                                 // 账单id
                title: title,                                                           // 账单标题
                receivables: payables,                                                  // 应收
                actualReceivables: actualpayables,                                      // 已收
                thisReceivables: thisPayment                                            // 本次收款
            }));
        }
    });
    return invoiceData;
}

function verifyAlldata(mustLabelIds, flowdata, iframe, flowClass, flowId) {
    var mustLabelArr = mustLabelIds.split(",");
    var flag = true;
    if (flowClass == '[BillWriteOffFlow]') {
    	$('.form-submit-tips').each(function () {
    		if ($(this).val() && flag) {
    			flag = false;
    			return layer.msg($(this).val());
    		}
    	})
    }
    $.each(flowdata, function (i, item) { // 遍历当前流程的每个标签
        if (mustLabelArr.indexOf(item.id) >= 0) { // 如果是必要标签，对value进行校验
            var key = item.id;
            var value = '';
            var type = parseInt(item.type);
            if (type === 0 || type === 4 || type === 5 || type === 6 || type === 9) { // 字符串、日期、日期时间、月份、文本框
                value = iframe.find(".layui-show").find("#" + key).val();
                if (isNull(value) || value.trim().length == 0) {
                    layer.msg(item.name + '不能为空！');
                    flag = false;
                }
            } else if (type === 1) { // 整型
                value = iframe.find(".layui-show").find("#" + key).val();
                if (isNull(value)) {
                    layer.msg(item.name + '不能为空！');
                    flag = false;
                } else if (!/^[0-9]*[1-9][0-9]*$/.test(value)) {
                    layer.msg(item.name + '格式不正确！');
                    flag = false;
                }
            } else if (type === 2) { // 小数
                value = iframe.find(".layui-show").find("#" + key).val();
                if (isNull(value)) {
                    layer.msg(item.name + '不能为空！');
                    flag = false;
                } else if (!/^([1-9][0-9]*|[0-9])(\.[0-9]*|)$/.test(value)) {
                    layer.msg(item.name + '格式不正确！');
                    flag = false;
                }
            } else if (type === 3) { // 单选
                var chkRadio = iframe.find(".layui-show").find("input[name='" + key + "']");
                for (var i = 0; i < chkRadio.length; i++) {
                    if (chkRadio[i].checked) {
                        value = chkRadio[i].value;
                    }
                }
                if (isNull(value)) {
                    layer.msg("请选择" + item.name + "！");
                    flag = false;
                }
            } else if (type === 7 || type === 11 || type === 12) { // 下拉框、价格类型、充值类型
                value = iframe.find(".layui-show").find("#" + key).val();
                if (isNull(value)) {
                    layer.msg("请选择" + item.name + "！");
                    flag = false;
                }
            } else if (type === 8) { // 文件
                value = iframe.find(".layui-show").find("#" + key).val();
                if (isNull(value)) {
                    layer.msg(item.name + '不能为空！');
                    flag = false;
                }
            } else if (type === 10) { // 调价梯度
                value = getGradient(iframe);
                if (isNull(value)) {
                    flag = false;
                }
            } else if (type === 13) { // 酬金类型
                value = getRremuneration(iframe);
                if (isNull(value)) {
                    flag = false;
                }
            } else if (type === 14) { // 账单信息
                value = accountData(iframe, flowClass);
                if (isNull(value)) {
                    layer.msg('请选择' + item.name);
                    flag = false;
                }
            } else if (type === 15) { // 账单金额
                value = accountAmountData(iframe, item.id);
                if (isNull(value)) {
                    layer.msg(item.name + '不能为空！');
                    flag = false;
                }
            } else if (type === 23) { // 发票信息
                value = invoiceData(iframe, flowClass);
                if (isNull(value)) {
                    layer.msg('请选择' + item.name);
                    flag = false;
                }
            } else if (type === 17 || type === 18 || type === 19 || type === 20){
                value = iframe.find(".layui-show").find("#" + key).val();
                if (isNull(value)) {
                    layer.msg(item.name + '不能为空！');
                    flag = false;
                }
            } else if (type === 24) {
                value = getApplyOrderData(iframe, flowId);
                $.each(value, function (i, order) {
                    if (!$.isNumeric(order.price) || !$.isNumeric(order.amount)) {
                        layer.msg('价格和数量只能为数字！');
                        flag = false;
                    }
                });
            } else if(type === 29) {
                // 客户开票抬头
                value = getCustInvoiceData(iframe);
                if(isNull(value) || value.length === 0){
                    layer.msg('客户开票信息不能为空！');
                    flag = false;
                }
                $.each(value, function (i, item) {
                    if (isNull(item.custInvoiceInfo)) {
                        layer.msg('客户开票信息不能为空');
                        flag = false;
                    } else if (!$.isNumeric(item.receivables)) {
                        layer.msg('已开金额只能为数字！');
                        flag = false;
                    } else if (!$.isNumeric(item.thisReceivables)  || parseFloat(item.thisReceivables).toFixed(2) === '0.00') {
                        layer.msg('开票金额必须为大于0的数字！');
                        flag = false;
                    } else if (parseFloat(item.receivables) > parseFloat(item.thisReceivables)) {
                        layer.msg('已收金额不能大于开票金额');
                        flag = false;
                    }
                    return flag;
                });
                if(flag) {
                    var billSumEle = $(iframe).find('.bill_info_sum_pay');
                    // 账单开票流程，校验 账单合计 是否等于 开票合计，无账单开票不用校验
                    if (billSumEle.length > 0) {
                        var billSum = billSumEle.text();
                        var  custInvoiceSum = $(iframe).find('.cust_invoice_info_sum_pay').text();
                        if(parseFloat(billSum).toFixed(2) !== parseFloat(custInvoiceSum).toFixed(2)){
                            layer.msg('开票金额合计 必须等于 账单金额合计');
                            flag = false;
                        }
                    }
                }
            } else if(type === 30) {
                // 账单开票信息
                value = getBillInvoiceInfo(iframe);
                if (isNull(value) || value.length === 0) {
                    layer.msg('请选择' + item.name);
                    flag = false;
                } else {
                    $.each(value, function (i, item) {
                        if (!$.isNumeric(item.thisReceivables)) {
                            layer.msg('请输入账单的本次开票金额');
                            flag = false;
                        } else if (parseFloat(item.thisReceivables).toFixed(2) === '0.00') {
                            layer.msg("账单的本次开票金额必须大于0");
                            flag = false;
                        } else if (parseFloat(item.thisReceivables) > parseFloat(item.invoiceableAmount)) {
                            layer.msg("账单的本次开票金额不能大于可开金额");
                            flag = false;
                        }
                        return flag; // 跳出循环
                    });

                }
            } else if (type === 32){ // 时间段账单信息
                value = timeAccountBillData(iframe, key, item.name);
                if (isNull(value) || value.length === 0) {
                    flag = false;
                }
            } else if (type === 34) {
                // 账单对账标签
                var uncheckedBillInfoEle = iframe.find("div.unchecked-bills");
                if (uncheckedBillInfoEle.length > 0) {
                    var checkedBills = uncheckedBillInfoEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
                    if (checkedBills.length === 0) {
                        layer.msg('请选择账单');
                        flag = false;
                    }
                    $(checkedBills).each(function() {
                        var itemEle = $(this).parent();
                        var successCountEle = itemEle.find('input[name=successCount]');
                        for (var index = 0; index < successCountEle.length; index++) {
                            if (!/^[0-9]*$/.test($(successCountEle[index]).val())) {
                                layer.tips('数量只能是数字', $(successCountEle[index]));
                                flag = false;
                                break;
                            }
                        }
                        return flag;
                    });
                    var optionBoxes = uncheckedBillInfoEle.find('div.unchecked-bill-file > input[name=billFile]:checked');
                    if (optionBoxes.length === 0) {
                        layui.layer.msg('请勾选账单或数据详情复选框');
                        flag = false;
                    }
                }
            }  else if (type === 37) {
            	iframe.find('.account-recharge').each(function (i, item) {
            		if (!flag) {
            			return;
            		}
            		var ele = $(item);
            		if (!ele.find('[name="rechargeAccount"]').val()) {
            			flag = false;
        				return layer.tips('请选择账号', ele.find('[name="rechargeAccount"]'));
        			}
        			if (!ele.find('[name="price"]').val()) {
        				flag = false;
        				return layer.tips('不能为空或等于0', ele.find('[name="price"]'));
        			}
        			if (!ele.find('[name="rechargeAmount"]').val()) {
        				flag = false;
        				return layer.tips('不能为空或等于0', ele.find('[name="rechargeAmount"]'));
        			}
        			if (!ele.find('[name="pieces"]').val()) {
        				flag = false;
        				return layer.tips('不能为空或等于0', ele.find('[name="pieces"]'));
        			}
            	});
            } 
        }
        return flag; // return false 跳出循环
    });
    return flag;
}

/**
 * 初始化流程发起按钮
 * @param entityType    0供应商，1客户
 * @param flowType      0运营，1结算
 * @param productid     点击产品id
 */
function initCustOpFlowButton(entityType, flowType, productid, customerid) {
	customerid = customerid ? customerid : '';
    $.ajax({
        type: "POST",
        // async: false,
        url: '/flow/getFlowByType.action',
        dataType: 'json',
        data: {
            entityType: entityType,
            flowType: flowType,
            productId: productid,
            customerId: customerid
        },
        success: function (data) {
            // 展示流程发起按钮
            var buttonBody = $('#operateCard #buttonBody');
            buttonBody.find("button[class*='applyFlow']").remove();
            if (data !== "" && data != null) {
                var html = "";
                $.each(data, function (i, item) {
                    html += "<button type='button' class='layui-btn layui-btn-sm layui-btn-primary applyFlow' style='margin: 0'" +
                        " data-my-id='" + item.flowId + "' title='"+ item.flowName + "'>" + item.flowName + "</button>";
                });
                buttonBody.append(html);
            }
            if (typeof bindApplyFlowClick == 'function') {
                bindApplyFlowClick(buttonBody);
            }
        }
    });
}

var lastSubmitTime = new Date().getTime();
var lastSubmitValue = null;
// 发起流程
function toApplyFlow(flowId) {
    var iframe = $("#" + flowId);
    var flowData = iframe.find("xmp[name='flowData']").text();
    var flow = JSON.parse(flowData);
    if (!verifyAlldata(flow.mustLabelIds, flow.flowLabels, iframe, flow.flowClass, flow.flowId)) {
        return;
    }
    var flowMsg = getAlldata(flow.editLabelIds, flow.flowLabels, iframe, flow.flowClass, flow.flowId);
    if (flowMsg == null) {
        return;
    }

    // 提交流程
    var data = {
        flowId: flow.flowId,
        supplierId: customerId,
        productId: productId,
        flowMsg: JSON.stringify(flowMsg),
        platform: 0
    };
    layer.confirm("确认发起流程吗？", {
        title: "确认操作",
        icon: 3,
        btn: ["确认", "取消"],
        skin: "reject-confirm"
    }, function (index, ele) {
    	if (lastSubmitValue == hex_md5(JSON.stringify(data)) && (new Date().getTime() - lastSubmitTime) <= 30000) {
        	return layer.msg('不能连续提交相同数据');
        } else {
        	lastSubmitValue = hex_md5(JSON.stringify(data));
            lastSubmitTime = new Date().getTime();
        }
	    $.ajax({
	        type: "POST",
	        async: true,
	        url: "/customerOperate/applyFlow.action",
	        dataType: 'json',
	        contentType: "application/json;charset=utf-8",
	        data: JSON.stringify(data),
	        beforeSend: function () {
	        	return layer.msg("申请中。。。");
	        },
	        success: function (resp) {
	            if (resp.code == 200) {
	                layer.msg(resp.msg);
	                // 刷新工作台气泡
	                if (typeof loadConsoleFlowCount == 'function') {
	                    loadConsoleFlowCount();
	                }
	                // 刷新气泡
	                if (typeof renderKHFlowEntCount == 'function') {
	                    renderKHFlowEntCount();
	                }
	                // 提交成功后刷新运营模块
	                if (typeof reload_sale_operate_info == "function") {
	                    reload_sale_operate_info();
	                }
	                // 刷新结算模块
	                if (typeof reload_sale_settlement_info == "function") {
	                    reload_sale_settlement_info();
	                }
	            } else {
	                layer.msg(resp.msg);
	            }
	        }
	    });
	    layer.close(index);
    }, function () {
        layer.msg("取消");
    });
}