var type;
// 流程类型 0运营，1结算
var flowType = 999;

var layer;
var element;
var isLock = false;

$(document).ready(function () {
    layui.use(['layer', 'element'], function () {
        layer = layui.layer;
        element = layui.element;
        // 展开年份
        element.on('collapse(operate_year_title)', function (data) {
            // 点击年标题 yyyy
            var date = data.content.attr("operate-title-id");
            console_operate_year = date;
            return false;
        });
        // 展开月份时加载当月流程记录
        element.on('collapse(operate_month_title)', function (data) {
            // 点击月标题 yyyy-MM
            if (data.show) {
                var date = data.content.attr("operate-name-id");
                console_operate_month = date;
                scrollToLoadSupplierFlow('operate');
            }
        });
       loadSupplierAllOperate('');
    });
});

// 初始化流程记录折叠框
function initSupplierOperatePanel() {
    var panel = new myPannel({});
    panel.init("#operate_panel");
    element.render("collapse", "operate_year_title");
}

// 加载供应商的运营栏
function loadSupplierOperate(typeInt, productid) {
    type = 99;
    if (isNotBlank(typeInt)) {
        if (typeInt == 1 || typeInt == 0) {
            type = 1;
        }
    }
    productId = productid; //commonJs公共参数
    $.ajaxSettings.async = true;
    $.post("/operate/queryOperate.action?productId=" + productid + "&type=" + type, {}, function (data) {
        if (data !== undefined && data !== null && data.length != 0) {
            $('#priceCard').html(data);
            // 初始化折叠框
            initSupplierOperatePanel();
            // 展开最后一个月的流程
            openLastSupplierOperate();
            // 流程发起按钮区域
            var buttonBody = $('#priceCard').find('#buttonBody');
            if (buttonBody != null && buttonBody.length === 1) {
                initOperateFlowButton(0, 0, productid);
            }
            if (!isNull(resource_module_open)) {
                resource_module_open.render();
            }
        }
    }, 'html');
    $.ajaxSettings.async = false;
}

// 加载供应商所有产品的运营时间
function loadSupplierAllOperate() {
    var loadingIndex = layer.load(2);
    $.ajaxSettings.async = true;
    $.post("/dsMatchOrder/getAllOperate.action", {}, function (data) {
//        debugger
        if (isNotBlank(data)) {
            $('#priceCard').html(data);
            // 初始化折叠框
            initSupplierOperatePanel();
            // 展开最后一个月的流程
            openLastSupplierOperate();
        }
        layer.close(loadingIndex);
    }, 'html');
    $.ajaxSettings.async = false;
}

// 展开最近1个月的流程记录
function openLastSupplierOperate() {
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
function loadSupplierOperateByPage(date, productid, page, pageSize,callback) {
    if (isNotBlank(date)) {
        var data;
        var url;
        if (productId == '') { // 查询所有产品的运营记录
            url = "/dsMatchOrder/getAllOperateByPage.action";
            data = {
                "date": date,
                "supplierId": supplierId,
                "entityType": 1,
                "page": page,
                "pageSize":pageSize
            }
        } else { // 查询指定产品的运营记录
            url = "/dsMatchOrder/getOperateByPage.action";
            data = {
                "date": date,
                "productId": productId,
                "entityType": 1,
                "page":page,
                "pageSize":pageSize
            }
        }
        $.ajax({
            type: "POST",
            async: false,
            url: url,
            dataType: 'json',
            data: data,
            success: function (resp) {
                if (resp.code == 200) {
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
                                s += ',当前节点:' + item.nodeName
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
                                        renderFlowMsg(ele, data.data);
                                        element.render("collapse", "operate_month_title");
                                    }
                                }
                            });
                        });
                    }
                    loadOperateDetailByPage(flowEntIds,callback);
                    layer.close(loadingIndex);
                }
            }
        });
    }
}

function loadOperateDetailByPage(ids,callback) {
    if (!(Object.prototype.toString.call(ids) === '[object Array]') || ids.length === 0) {
        // 不是数组
        return;
    }
    // console.time("加载运营流程详情耗时" + ids);
    $.ajax({
        type: "POST",
        async: true,
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
function getAlldata(editLabelIds, flow, iframe, flowClass) {
    var data = {};
    $(flow).each(function (i, item) {
        var value = '';
        if (editLabelIds.indexOf(item.id) >= 0) {
            var type = parseInt(item.type);
            if (type === 3) { // 单选框
                value = iframe.find("input[name='" + item.id + "']:checked").val();
                data[item.name] = value;
            } else if (type === 8) { // 文件
                value = iframe.find(".layui-show").find("#" + item.id).val();
                if (isNotBlank(file_result)) {
                    // 取完文件后，清除文件上传中本标签的文件内容
                    delete file_result[item.id];
                }
                data[item.name] = value;
            } else if (type === 10) {
                value = getGradient(iframe);
                if (value == null) {
                    flag = false;
                    return;
                }
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
            } else {
                value = iframe.find(".layui-show").find("#" + item.id).val();
                data[item.name] = value;
            }
        }

    });
    return data
}

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

        if (isBlank(thisPayment) || parseFloat(thisPayment) == 0) {
            layer.msg('本次收付款金额不能为空或0');
            return null;
        }

        if (flowClass === '[BillPaymentFlow]') {            // 账单付款流程
            accountData.push(sortObjectKey({
                id: id,                                     // 账单id
                title: title,                               // 应付
                payables: payables,                         // 已付
                actualpayables: actualpayables,             // 已付
                thisPayment: thisPayment                    // 本次付款
            }));
        } else if (flowClass === '[RemunerationFlow]') {    // 酬金流程
            accountData.push(sortObjectKey({
                id: id,                                     // 账单id
                title: title,                               // 账单标题
                receivables: payables,                      // 应收
                actualReceivables: actualpayables,          // 已收
                thisReceivables: thisPayment                // 本次收款
            }));
        }

    });
    return accountData;
}

// 获取酬金标签的内容
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
        return 'null';
    }
    return money.toFixed(2) + "," + rate.toFixed(2) + "," + reward.toFixed(2) + "," + deduction.toFixed(2) + "," + remuneration.toFixed(2)
}

// 获取价格梯度标签的内容
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
            json['isdefault'] = isdefault;                       // 是否默认
            json['minsend'] = min;                               // 最小值
            json['maxsend'] = max;                               // 最大值
            json['price'] = price;                               // 价格
            json['provinceproportion'] = pRatio;                 // 省占比
            json['complaintrate'] = mRatio;                      // 投诉比
            json['gradient'] = i;                                // 梯度下标
            json['priceType'] = 'gradient';                      // 梯度价
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
        json['price'] = price;                                  // 价格
        json['provinceprice'] = provinceprice;                  // 省网价格
        json['priceType'] = 'uniform';                          // 统一价
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

function verifyAlldata(mustLabelIds, flowdata, iframe, flowClass) {
    var mustLabelArr = mustLabelIds.split(",");
    var flag = true;
    $.each(flowdata, function (i, item) { // 遍历当前流程的每个标签
        if (mustLabelArr.indexOf(item.id) >= 0) { // 如果是必要标签，对value进行校验
            var key = item.id;
            var value = '';
            var type = parseInt(item.type)
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
            }
        }
        return flag; // return false 跳出循环
    });
    return flag;
}

// 显示各个流程的发起按钮
function initOperateFlowButton(entityType, flowType) {
    $.ajax({
        type: "POST",
        async: false,
        url: '/flow/getFlowByType.action',
        dataType: 'json',
        data: {
            entityType: entityType,
            flowType: flowType,
        },
        success: function (data) {
            // 展示流程发起按钮
            var buttonBody = $('#priceCard #buttonBody');
            buttonBody.find("button[class*='applyFlow']").remove();
            if (data !== "") {
                var html = "";
                $.each(data, function (i, item) {
                    html += "<button type='button' class='layui-btn layui-btn-sm layui-btn-primary applyFlow' style='margin: 0'" +
                        " data-my-id='" + item.flowId + "' title='" + item.flowName + "'>" + item.flowName + "</button>";
                });
                buttonBody.append(html);
            }
            if (typeof bindApplyFlowClick == 'function') {
                bindApplyFlowClick(buttonBody);
            }
        }
    });
}

var lastSubmitTime = 0;
var lastSubmitValue = null;
// 发起流程
function toApplyFlow(flowId) {
    var iframe = $("#" + flowId);
    var flowData = iframe.find("xmp[name='flowData']").text();
    var flow = JSON.parse(flowData);
    if (!verifyAlldata(flow.mustLabelIds, flow.flowLabels, iframe, flow.flowClass)) {
        return;
    }
    var flowMsg = getAlldata(flow.editLabelIds, flow.flowLabels, iframe, flow.flowClass);
    if (flowMsg == null) {
        return;
    }

    // 提交流程
    var data = {
        flowId: flow.flowId,
        supplierId: supplierId,
        productId: productId,
        flowMsg: JSON.stringify(flowMsg),
        entityType: 2
    };
    
    if (lastSubmitValue == hex_md5(JSON.stringify(data)) && (new Date().getTime() - lastSubmitTime) <= 30000) {
    	return layer.msg('不能连续提交相同数据');
    } else {
    	lastSubmitValue = hex_md5(JSON.stringify(data));
    	lastSubmitTime = new Date().getTime();
    }
    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/applyProcess.action",
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
                if (typeof renderZYFlowEntCount == 'function') {
                    renderZYFlowEntCount();
                }
                // 提交成功后刷新运营模块
                if (typeof reload_operate_info == "function") {
                    reload_operate_info();
                }
                // 刷新结算模块
                if (typeof reload_settlement_info == "function") {
                    reload_settlement_info();
                }
            } else {
                layer.msg(resp.msg);
            }
        }
    });
}