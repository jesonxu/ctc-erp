var weekChineseArr = ['日', '一', '二', '三', '四', '五', '六'];
layui.use(['layer', 'form', 'element'], function(){
	var layer = layui.layer,
		form = layui.form,
		element = layui.element;
	
	init();
	
	function init() {
		initFlowMenu();
		queryHistoryFlowEnts();
	}
	
	function initFlowMenu() {
	    $.ajax({
	        type: "POST",
	        async: true,
	        url: '/personalCenter/getPersonalFlow',
	        dataType: 'json',
	        success: function (result) {
	            if (result && result.code == 200 && result.data) {
	            	if (result.data.length == 0) {
	            		$('#applyFlowMenu').html('<div class="apply-flow-menu-no-data">无流程申请，请联系管理员</div>');
	            	} else {
	            		$('#applyFlowMenu').html('');
	            		$(result.data).each(function (i, item) {
	            			$('#applyFlowMenu').append('<div class="apply-flow-menu-item" flow-id="' + item.flowId + '" onclick="createFlowContent(\'' + item.flowId + '\', this)">' + item.flowName + '</div>')
	            		});
	            	}
	            } else {
	            	layer.msg('查询可申请的流程失败');
	            	$('#applyFlowMenu').html('<div class="apply-flow-menu-no-data">重新刷新</div>');
	            }
	        }
	    });
	}
});

function queryHistoryFlowEnts() {
	$.ajax({
        type: "POST",
        async: true,
        url: '/personalCenter/queryHistoryFlowEnts',
        dataType: 'json',
        success: function (result) {
            if (result && result.code == 200 && result.data) {
            	if (result.data.length == 0) {
            		$('.flow-ent-history-content').html('<div class="apply-flow-menu-no-data">无历史流程</div>');
            	} else {
            		$('.flow-ent-history-content').html('');
            		
            		var flowEntIds = [];
            		var html = '';
            		$(result.data).each(function (i, item) {
            			flowEntIds.push(item.id);
            			var s = '<div class="settlement-title" style="cursor: pointer;" id="' + item.id + '" entId = "' + item.id + '" productId = "' + item.productId + '">'
            				+ '<b><span class="flow-ent-title-time">' + str2Date(item.wtime).Format('yyyy年MM月dd日') + '(周' + weekChineseArr[str2Date(item.wtime).getDay()] + ')' + str2Date(item.wtime).Format('hh:mm') + '</span>'
	                    	+ '<span class="flow-ent-title-name">' + item.flowTitle + '</span>'
	                    	+ '<span class="flow-ent-status">' + (item.flowStatus == '归档' ? '归档' : (item.nodeName ? item.nodeName : '无')) + '</span>';
	                    if (item.canOperat) {
	                        s += '<span style="color:red;">(待处理)</span>'
	                    }
	                    if (item.flowStatus === "取消"){
	                        s += "<span class='flow-state-cancel'>已取消</span>"
	                    } else if (item.flowStatus === "归档"){
	                        s += "<span class='flow-state-document'>已归档</span>"
	                    } else{
	                        s += "<span class='flow-state-process'>进行中</span>"
	                    }
	                    html += s + "</b></div><hr style='height: 3px; background-color: #1E9FFF'/>"
            		});
            		
            		$('.flow-ent-history-content').html(html);
            		
            		for (var i = 0; i < flowEntIds.length; i++) {
                        $("#" + flowEntIds[i]).unbind().bind('click', function () {
                            var entId = $(this).attr('entId');
                            flowType = 1; // 结算的流程
                            var ele = $(this);
                            $.ajax({
                                type: "POST",
                                async: true,
                                url: '/flow/flowDetail.action?id=' + entId +"&temp=" + Math.random(),
                                dataType: 'json',
                                data: {},
                                success: function (data) {
                                	renderFlowEntContent(ele, data.data)
                                }
                            });
                        });
                    }
            	}
            } else {
            	layer.msg('查询历史流程失败');
            	$('.flow-ent-history-content').html('<div class="apply-flow-menu-no-data">重新刷新</div>');
            }
        }
    });
}

function renderFlowEntContent(ele, data) {
    // 重复点击关闭流程记录
    var nextEle = $(ele).next("#flowMsg_" + data.flowEntId);
    if (nextEle.length !== 0) {
        $(nextEle).remove();
        return;
    }
    
    // 最外层框
    var html = "<div class='flowMsg' id='flowMsg_" + data.flowEntId + "'>";
    
    // 流程详细信息
    html += "<xmp hidden name='flowData' >" + JSON.stringify(data) + "</xmp>";
    
    // 显示流程处理记录
    var records = data.record;
    if (records != null && records.length > 0) {
    	
        html += "<div id='flowLogs'>";
        
        $.each(records, function (recordIndex, item) {
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
            var remark = item.remark;
            html += "<div id='record" + recordIndex + "' class='record layui-input-block'>";
            // 标题：处理人+处理时间+处理结果
            html += "<div class='recordTitle layui-input-inline'>";
            html += "<span>" + str2Date(dealTime).Format('yyyy年MM月dd日 hh:mm') + "&nbsp;&nbsp;&nbsp;&nbsp;" + dealPerson + "[" + dealRole + "]</span>&nbsp;&nbsp;&nbsp;&nbsp;" + auditResultSpan;
            html += "</div>";
            // 内容：处理意见 + 修改内容
            html += "<div class='recordContent'>";
            if (auditResult !== '创建') {
                html += "<span>处理意见：" + remark + "</span><br/>";
            }
            // 本条处理记录对流程标签修改的部分
            var changes = item.flowMsg;
            if (changes !== undefined && changes !== '' && changes !== '{}') {
                // 转换成对象
                changes = JSON.parse(changes);
            } else {
                changes = null;
            }
           
            if (changes != null) {
            	if (auditResult === '创建') { // 显示展示标签的所有内容
		            $.each(data.labelList, function (i, label) {
		                if (changes[label.name] != null) {
		                	html += '<label class="flow-label">' + label.name + "：</label>";
		                    // 处理记录中的标签值转为文字，不需要标签格式
                            html += labelValueToString(label.type, changes[label.name], label.defaultValue, data.flowEntId, data.flowClass, data.flowId, i, data.productId);
                            html += "<br/>";
		                }
		            });
            	} else { // 修改
                	$.each(data.labelList, function (i, label) {
                        if (changes[label.name] != null) {
                    		html += '<label class="flow-label">' + label.name + " 修改为：</label>";
                            html += labelValueToString(label.type, changes[label.name], label.defaultValue, data.flowEntId, data.flowClass, data.flowId);
                            html += "<br/>";
                        }
                    });
                }
            }
            html += "</div>"; // recordContent
            html += "</div>"; // record
        });
        html += "</div>"; // flowLogs 流程处理记录
    }
    
    // 清除本流程以前的框
    $("#flowMsg_" + data.flowEntId).remove();
    $(ele).after(html);
}

function createFlowContent(flowId, ele) {
	var flag = false;
	var lastSelected = $('.apply-flow-menu-item-actived');
	if (lastSelected.length > 0) {
		if (lastSelected.attr('flow-id') == flowId) { // 同一个
			$(ele).removeClass('apply-flow-menu-item-actived');
			flag = true;
		} else {
			$('.apply-flow-menu-item-actived').removeClass('apply-flow-menu-item-actived');
			$(ele).addClass('apply-flow-menu-item-actived');
		}
	} else {
		$(ele).addClass('apply-flow-menu-item-actived');
	}
	if (flag) {
		$('#applyFlowOuter').css('display', 'none')
		$('#applyFlowMain').html('');
	} else {
		$('#applyFlowOuter').css('display', '')
		$.ajax({
			type: "POST",
			async: false,
			url: '/operate/getFlowTabLabel.action?temp=' + Math.random(),
			dataType: 'json',
			data: {
				flowId: flowId
			},
			success: function (data) {
				initFlow(data.data);
			}
		});
	}
}

// 复写applyFlowCommon.js的相同方法
function initFlow(flow) {
    var html = "<div id='flow_" + flow.flowId + "' class='applyFlowLabel' style='padding: 5px; margin: 5px 0 5px 0'>"
        + "<xmp hidden name='flowData' >" + JSON.stringify(flow) + "</xmp>"
        + "<form class='layui-form layui-show' id='" + flow.flowId + "' action=''>";
    if (flow.flowLabels.length > 0) {
        // 初始化标签
        html += initFormItem(flow, dateRender2DateTime, dateRender2Day, dateRender2Month, dateRender2TimeSlot);
    }
    html += "<div class='layui-form-item' style='text-align: center;padding-top: 10px'>" +
        "<button type='button' class='layui-btn layui-btn-normal'" +
        " onclick='toApplyFlow(\"flow_" + flow.flowId + "\")'>立即发起</button></div>"
        + "</form>"
        + "</div>";

    $('#applyFlowMain').html(html);
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
        flowMsg: JSON.stringify(flowMsg)
    };
    if (lastSubmitValue == hex_md5(JSON.stringify(data)) && (new Date().getTime() - lastSubmitTime) <= 30000) {
        return layer.msg('不能连续提交相同数据');
    } else {
        lastSubmitValue = hex_md5(JSON.stringify(data));
        lastSubmitTime = new Date().getTime();
    }
    layer.confirm("确认发起流程吗？", {
        title: "确认操作",
        icon: 3,
        btn: ['确认', '取消'],
        skin: "reject-confirm"
    }, function () {
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
	                $('#applyFlowMain').html('');
	                $('.apply-flow-menu-item-actived').removeClass('apply-flow-menu-item-actived');
	                queryHistoryFlowEnts();
	            } else {
	                layer.msg(resp.msg);
	            }
	        }
	    });
    }, function () {
    	layer.msg("取消");
    });
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
            }else if (type === 32){ // 时间段账单金额
                value = timeAccountBillData(iframe, key, item.name);
                if (isNull(value) || value.length === 0) {
                    flag = false;
                }
            }else if (type === 33) { // 平台账号信息
                value = platformAccountInfoData(iframe, item.id, item.name, true);
                if (value == null || value.length === 0) {
                    return;
                }
            }
        }
        return flag; // return false 跳出循环
    });
    return flag;
}

// 获取所有标签的数据
function getAlldata(editLabelIds, flow, iframe, flowClass) {
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
            }else if(type === 39){//时间+上午/下午 控件类型
                inputValue = iframe.find(".layui-show").find('input[name="' + item.id + '"]').val();
                selectValue = iframe.find(".layui-show").find('select[name="' + item.id + '"]:checked').val();
                data[item.name] = inputValue + "-" +selectValue;
            }else {
                value = iframe.find(".layui-show").find("#" + item.id).val();
                data[item.name] = value;
            }
        }
    });
    return data
}