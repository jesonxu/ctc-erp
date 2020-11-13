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
    },
    // 提单流程
    "[DsOrderFlow]" : {
        productname: "产品名称",
        format: "规格型号",
        price: "销售单价",
        amount: "数量",
        total: "销售总额",
        remark: "备注"
    },
    // 提单流程_配单信息
    "[DsOrderFlow]match" : {
        productname: "产品名称",
        format: "规格型号",
        price: "采购单价",
        amount: "数量",
        total: "采购总额",
        logisticsCost: "物流费",
        suppliername: "供应商",
        remark: "备注"
    },
    // 采购流程_配单信息
    "[DsPurchaseFlow]match" : {
        productname: "产品名称",
        format: "规格型号",
        price: "采购单价",
        amount: "数量",
        total: "采购总额",
        logisticsCost: "物流费",
        suppliername: "供应商",
        remark: "备注"
    },
    // 充值流程_账号
    "[DsPurchaseFlow]match" : {
        productname: "产品名称",
        format: "规格型号",
        price: "采购单价",
        amount: "数量",
        total: "采购总额",
        logisticsCost: "物流费",
        suppliername: "供应商",
        remark: "备注"
    },
    // 充值流程_充值账号
    "[PaymentFlow]" : {
    	recharge_account: "账号",
    	current_amount: "当前余额",
        price: "单价",
        recharge_amount: "充值金额",
        pieces: "条数"
    }
};

// 标签名称
var product_bill_input_name = {
    // 付款流程
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
    },
    // 充值流程_充值账号
    "[PaymentFlow]" : {
    	recharge_account: "rechargeAccount",
    	current_amount: "currentAmount",
        price: "price",
        recharge_amount: "rechargeAmount",
        pieces: "pieces"
    }
};
var laydate;
var layer;
var layedit;
var upload;
var element;
var form;
var dropdown;
var table;
/*layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    dropdown: 'dropdown'
});*/

var renderFormFuns = [];

// 查看电商供应商基本信息 operationType=1是查看信息
function viewDsSupplier(supplier_id) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['765px', '560px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toEditSupperlierBaseinfo/'+supplier_id+"?entityType=2&operationType=1&r=" + Math.random()
    });
}

// 查看通信供应商基本信息 operationType=1是查看信息
function viewSupplier(supplier_id) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['765px', '560px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toEditSupperlierBaseinfo/'+supplier_id+"?entityType=0&operationType=1&r=" + Math.random()
    });
}

// 查看客户基本信息 operationType=1是查看信息
function viewCustomer(customer_id) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['1000px', '640px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/customer/toEditCustomer/' + customer_id + "?entityType=1&operationType=1&r=" + Math.random()
    });
}

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
    // 添加客户名称或者供应商名称，可以点击查看基础信息
    var viewCustomer = "<div class='view-details'><span>客户名称：</span><a href='javascript:void(0);' onclick='viewCustomer(\"" + data.supplierId + "\")'>" + data.supplierName + "</a><div>"
    var viewSupplier = "<div class='view-details'><span>供应商名称：</span><a href='javascript:void(0);' onclick='viewSupplier(\"" + data.supplierId + "\")'>" + data.supplierName + "</a><div>"
    var viewDsSupplier = "<div class='view-details'><span>供应商名称：</span><a href='javascript:void(0);' onclick='viewDsSupplier(\"" + data.supplierId + "\")'>" + data.supplierName + "</a><div>"
    if (data.entityType === 1) {
        html += viewCustomer;
    } else if (data.entityType === 0) {
        html += viewSupplier;
    } else if (data.entityType === 2) {
        html += viewDsSupplier;
    }
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
            html += "<span>" + dateToWeek(dealTime) + "</span>&nbsp;&nbsp;<span>" + dealPerson + "[" + dealRole + "]</span>&nbsp;&nbsp;" + auditResultSpan;
            html += "</div>";
            // 内容：处理意见+修改内容
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
                    // 账单流程的平台基础数据
                    var baseData = changes['baseData'];
                    if (isNotBlank(baseData) && baseData !== '{}') {
                        baseData = JSON.parse(baseData);
                        var renderedKeys = [];
                        // 账单编号
                        if (renderedKeys.indexOf('账单编号') < 0 && baseData['账单编号']) {
                        	html += '账单编号' + "：" + baseData['账单编号'] + "<br/>";
                        	renderedKeys.push('账单编号');
                        }
                        // 账单的我司数据详情，时间段、单价、条数
                        if (renderedKeys.indexOf('BILL_PRICE_INFO_KEY') < 0 && baseData['BILL_PRICE_INFO_KEY']) {
                            html += "我司数据：" + (baseData['BILL_PRICE_INFO_KEY'] ? "<span onclick=\"showBillsRecordsDetail(this)\" style=\"color: #1E9FFF;cursor: pointer;\">详情</span>" : "") + "<br/>";
                            html = buildBillsRecordsDetail(html, baseData);
                            renderedKeys.push('BILL_PRICE_INFO_KEY');
                        }
                        // 电子账单（是老数据了，新对账流程不放这）
                        var files = [];
                        if (renderedKeys.indexOf('DAHAN_BILL_FILE_KEY') < 0 && baseData['DAHAN_BILL_FILE_KEY']) {
                            $(baseData['DAHAN_BILL_FILE_KEY'].split(';')).each(function(i, item) {
                                var arr = item.split(/[\\/]/);
                                files.push({
                                    fileName: arr[arr.length - 1],
                                    filePath: item
                                });
                            });
                            renderedKeys.push('DAHAN_BILL_FILE_KEY')
                        }
                        if (files && files.length > 0) {
                            // 查看Excel 下载Excel 下载PDF
                            html += '电子账单：';
                            html += addElectronicBilling(files);
                            html += "<br/>";
                        }
                        // 其他标签
                        for (var key in baseData) {
                            if (renderedKeys.indexOf(key) >= 0) {
                                continue;
                            }
                            if ($.isNumeric(baseData[key])) {
                                html += key + "：" + thousand(baseData[key]) + "<br/>";
                                renderedKeys.push(key)
                            } else {
                        		html += key + "：" + baseData[key] + "<br/>";
                        	}
                            renderedKeys.push(key);
                        }
                    }
                    
                    $.each(data.labelList, function (i, label) {
                    	// 绑定input联动事件
                    	if (/^\{\{(.+?)\}\}$/.test(label.defaultValue)) {
                    		renderFormFuns.push(bindInputLinkage(label.defaultValue, label.id, label.name, 'label-id'));
                    	}
                    	
                    	if (data.flowClass == '[BillWriteOffFlow]' && i == 0) {
				    		return;
				    	}
				 
                        if (changes[label.name] != null) {
                            html += label.name + "：";
                            // 处理记录中的标签值转为文字，不需要标签格式
                            html += labelValueToString(label.type, changes[label.name], label.defaultValue, data.flowEntId, data.flowClass, data.flowId, i, data.productId);
                            html += "<br/>"
                        }
                    });
                    
                    // 原来调价信息  // 增加 原来价格展示内容（调价流程或者国际调价流程）
                    var before_price_str = changes["原来价格"];
                    if (isNotBlank(before_price_str)){
                        var before_prices = null;
                        try{
                            before_prices = JSON.parse(before_price_str);
                        }catch (e) {
                            //console.log("捕获异常，不影响业务")
                        }

                        if (!isBlank(before_prices)){
                            var before_price_dom = "<span>原来价格:</span></br>";
                            for (var before_price_index = 0; before_price_index < before_prices.length; before_price_index++) {
                                var before_price = before_prices[before_price_index];
                                // 记录的时间
                                var time_info = "(" + before_price.startTime + "至" + before_price.endTime + ") ";
//                                console.log("时间" + time_info);
                                if (data.flowClass === "[AdjustPriceFlow]") {
                                    var max_send = isBlank(before_price.maxSend) || parseFloat(before_price.maxSend) === 0 ?
                                        " <span style='font-size: 18px'>∞</span> " : before_price.maxSend;
                                    // 调价流程 展示调价的内容信息
                                    var price_item = "<span>" + time_info + before_price.minSend + "条 <= 发送量 < " + max_send + "条，价格：" + before_price.price + "元</span></br>";
                                    before_price_dom += price_item;
//                                    console.log("价格:" + price_item);
                                }
                                if (data.flowClass === '[InterAdjustPriceFlow]') {
                                    // 国际调价流程 展示文件 点击下载
                                    var file_url = isBlank(before_price.remark) ?" ∞ " : before_price.remark;
                                    var file_name = time_info + "国际短信价格.xlsx";
                                    var file_info = {
                                        "fileName": file_name,
                                        "filePath": file_url
                                    };
                                    // 调价流程 展示调价的内容信息
                                    before_price_dom += time_info + "<a style='text-decoration: underline' href='javascript:void(0);'" +
                                        "onclick='view_File(" + JSON.stringify(file_info) + ")'>(点击预览)</a>&nbsp;&nbsp;<a style='text-decoration: underline' href='javascript:void(0);'" +
                                        "onclick='down_load(" + JSON.stringify(file_info) + ")'>(点击下载)</a></br>";
                                }
                            }
                            html += before_price_dom;
                        }
                    }
                } else { // 显示展示标签的修改内容
                    $.each(data.labelList, function (i, label) {
                        if (changes[label.name] != null) {
                        	if (data.flowClass == '[BillWriteOffFlow]' && i == 0) {
                        		return;
                        	}
                            // 处理记录中的标签值转为文字，不需要标签格式
                            if (data.flowClass == '[BillWriteOffFlow]' && i < 3){
                            	if (i == 1 && data.labelValueMap[label.name]) {
                        			html += label.name + " 修改为：";
                        			html += typeStringToBillsInfoString(data.labelValueMap[label.name]);
                        			html += "<br/>";
                        		} else if (i == 2 && data.labelValueMap[label.name]) {
                        			html += label.name + " 修改为：";
                        			html += typeStringToIncomeInfoString(data.labelValueMap[label.name]);
                        			html += "<br/>";
                        		}
                        	} else {
                        		html += label.name + " 修改为：";
                        		html += labelValueToString(label.type, changes[label.name], label.defaultValue, data.flowEntId, data.flowClass, data.flowId);
                        		html += "<br/>";
                        	}
                        }
                    });
                    // 显示修改后的电子账单
                    var labelKey = 'DAHAN_BILL_FILE_KEY';
                    var oldFile = changes['DAHAN_BILL_FILE_KEY_修改前'];
                    var newFile = changes['DAHAN_BILL_FILE_KEY'];
                    if ((oldFile && newFile && oldFile != newFile) || newFile) {
                    	html += '电子账单：';
                    	var arr = newFile.split(/[\\/]/);
                		var file = {
                			fileName: arr[arr.length - 1],
                			filePath: newFile
                		};
                		html += file.fileName + "&nbsp;<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='view_File(" + JSON.stringify(file) + ")'>预览</button>";
                        html += "<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='down_load(" + JSON.stringify(file) + ")'>下载</button>";
                        html += '<br/>';
                    }
                }
            }
            html += "</div>"; // recordContent
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

    // 流程待处理，没有人名/角色名说明流程已经走完
    if (dealPerson !== "" || data.canRevoke) {
    	html += "<div id='flowOperate' class='layui-input-block' style='text-align: left; margin: 0 10px 0 10px; min-height: 24px'>";
    	if (dealPerson !== "") {
	        // 标题：待处理人名/角色名+等待处理
	        html += "<div class='recordTitle layui-input-inline'>";
	        html += "<span>" + dealPerson + "</span>&nbsp;&nbsp;<span style='color: red'>等待处理</span>";
	        html += "</div>";
    	}
        // 当前用户可以审核时，显示内容：可编辑标签+审核区
        if (data.canOperat) {
            var baseData = data.baseDataMap;
            var editLabelIds = data.editLabelIds;

            if ((!isBlank(baseData) && JSON.stringify(baseData) !== '{}') || (isNotBlank(editLabelIds))) {
                html += "<div class='recordContent' style='background: whitesmoke; border: #e0e0e0; border-radius:7px;'>";

                // 账单流程平台数据
                var baseData = data.baseDataMap;
                if (!isBlank(baseData) && baseData !== '{}') {
                    html += "<div class='layui-form-item' style='margin: 5px'>";

                    baseData = typeof baseData == "object" ? baseData : JSON.parse(baseData);
                    for (var key in baseData) {
                    	if (key != 'BILL_PRICE_INFO_KEY'
                        		&& key != '账单编号'
                        		&& key != 'lastCopyFilePath'
                        		&& key != 'DAHAN_BILL_FILE_KEY') {
	                        html += "<div class='layui-input-block base-data-line'>";
	                        // 左侧标签名
	                        html += "<label class='layui-form-label base-data-title' data-label-name = '" + key + "'><span>" + key + "：</span></label>";
	                        // 右侧值
	                        html += "<div class='layui-input-inline base-data-value'>";
	                        html += "<span name='" + key + "'>" + baseData[key] + "</span>";
	                        html += "</div>";
	                        html += "</div>";
                    	}
                    }
                    html += "</div>"
                }

                // 流程的所有标签
                var labelList = data.labelList;
                if (labelList != null && labelList.length > 0) {
                    // 流程标签div
                    html += "<div class='layui-form' id='flowLabels' style='margin: 5px'>";

                    // 每个可编辑标签
                    $.each(labelList, function (i, label) {
                    	if (data.flowClass == '[BillWriteOffFlow]' && i < 3 && data.editLabelIds.indexOf(label.id) >= 0){
                    		if(i == 0) {
                    			html += '<div style="display:none;"><input type="radio" name="自动销账" value-type="3" data-type="3" input-required="false" value="0" title="否" checked></div>';
                    		} else if (i == 1) {
                    			html += "<div><span>" + label.name + "：</span></div>";
                    			if (data.flowStatus == 2 && data.editLabelIds && data.editLabelIds.indexOf(label.id) >= 0) {
                    				html += flowCreateBillsTable('', label, data.productId, data.labelValueMap);
                    			} else {
                    				html += typeStringToBillsInfoString(data.labelValueMap[label.name]);
                    			}
                    		} else {
                    			html += "<div><span>" + label.name + "：</span></div>";
                    			if (data.flowStatus == 2 && data.editLabelIds && data.editLabelIds.indexOf(label.id) >= 0) {
                    				html += flowCreateIncomesTable('', label, data.productId, data.labelValueMap);
                    			} else {
                    				html += typeStringToIncomeInfoString(data.labelValueMap[label.name]);
                    			}
                    		}
                    	} else {
                    		if (data.editLabelIds.indexOf(label.id) !== -1) {
                    			// 调价梯度类型标签的标签名
                    			html += (label.type === 10) ? ("<input type='hidden' name='gradient_label_name' value='" + label.name + "'/>") : "";

                    			html += "<div class='layui-form-item'>";
                    			// 左侧标签名
                    			html += "   <label class='layui-form-label show-lable' data-label-name = '" + label.name + "'><span>" + label.name + "：</span></label>";
                    			// 右侧值
                    			html += "   <div class='layui-input-block'" + (label.type != 37 ? "" : "style='margin-left: 0;'") + ">";
                    			html += "       <input type='text' class='layui-input' name='" + label.name + "' data-type='0' value-type='0' label-id=" + label.id + " label-type=" + label.type + " disabled='disabled' value='" + label.defaultValue + "'/>";
                    			html += "   </div>";
                    			html += "</div>";
                    		}
                    	}
                    });

                    html += "</div>"; // layui-form flowLabels
                }

                html += "</div>"; // recordContent
            }
            // 审核框：意见框、按钮
            html += "<div class='layui-form flow-audit' style='padding: 5px 5px 0 5px;'>";
            // 审核意见
            html += "<div class='layui-form-item'>";
            html += "<span style='color: red;'>*</span><span>处理意见：</span>";
            html += "<div class='layui-input-block'>";
            html += "<textarea id='audit-opinion' name='audit-opinion' placeholder='请输入处理意见' class='layui-textarea'></textarea>";
            html += "</div>";
            html += "</div>";

            // 以下是一排的审核操作按钮
            html += "<div class='layui-form-item' style='text-align: right;padding-top: 5px;'>";
            // 保存按钮（有可编辑标签）暂且不要
            /**
	            if (data.editLabelIds.length > 0 && data.nodeIndex !== 0) {
	                html += "<button type='button' class='layui-btn layui-btn-normal layui-btn-sm' onclick='audit(5, \"flowMsg_" + data.flowEntId + "\", this)'><i class='layui-icon layui-icon-ok-circle'></i>保存</button>";
	            }
            */
            // 通过按钮（所有节点)
            var buttonName = null;
            if (data.nodeIndex !== 0) {
            	if (data.nodeIndex == 3 && data.flowClass == '[BillWriteOffFlow]') {
            		buttonName = '确认销账';
            	} else {
            		buttonName = "通过";
            	}
            } else {
            	if (data.flowStatus == 2) {
            		buttonName = "重新申请";
            	} else {
            		buttonName = "通过";
            	}
            }
            html += "<button type='button' class='layui-btn layui-btn-sm' onclick='audit(2, \"flowMsg_" + data.flowEntId + "\", this)'><i class='layui-icon layui-icon-ok'></i>" + buttonName + "</button>";
            // 取消按钮（发起人节点）
            if (data.nodeIndex === 0 || (data.nodeIndex === 3 && data.flowClass == '[BillWriteOffFlow]')) {
                html += "<button type='button' class='layui-btn layui-btn-warm layui-btn-sm' onclick='audit(4, \"flowMsg_" + data.flowEntId + "\", this)'><i class='layui-icon layui-icon-close'></i>" + ((data.nodeIndex !== 0 && data.flowStatus == 2) ? "取消" : "放弃申请") + "</button>";
            }
            // 驳回按钮（非发起人节点）
            if (data.nodeIndex !== 0) {
                html += init_reject_select(data.flowId, "flowMsg_" + data.flowEntId, data.nodeIndex, data.flowClass);
            }
            html += "</div>"; // layui-form-item

            html += "</div>"; // layui-form flowAudit 审核框
        } else if (data.canRevoke) { // 可撤销
        	html += '<div class="layui-form flow-revoke flow-audit" style="padding: 5px 5px 0 5px;">';
        	html += '<div class="layui-form-item">';
        	html += '<span style="color: red;">*</span><span>撤销原因：</span>';
        	html += '<div class="layui-input-block"><textarea name="audit-opinion" placeholder="请输入撤销原因" class="layui-textarea revoke-reason"></textarea></div>';
        	html += '</div>';
        	html += '<div class="layui-form-item" style="text-align: right;padding-top: 5px;"><button type="button" class="layui-btn layui-btn-sm" onclick="revoke(&quot;' + data.flowEntId + '&quot;, this)"><i class="layui-icon layui-icon-ok"></i>撤销</button></div>'
        	html += '</div>';
        }
        html += "</div>"; // flowOperate 流程处理
    }

    html += "</div>"; // flowMsg 最外层框

    // 清除本流程以前的框
    $("#flowMsg_" + data.flowEntId).remove();
    $(ele).after(html);
    
    // 绑定事件
    if (renderFormFuns && renderFormFuns.length > 0) {
    	for (var i = 0; i < renderFormFuns.length; i++) {
    		if (typeof renderFormFuns[i] == 'function') {
    			renderFormFuns[i]($('#flowMsg_' + data.flowEntId), data.labelList);
    		}
    		renderFormFuns = [];
    	}
    }

    // 当前用户可以处理时，才渲染可编辑标签
    if (data.canOperat) {
        init_layui(data);
    }
}

//添加电子账单标签(虚拟的)
function addElectronicBilling(files) {
	var html = '';
	var pdfFile = {};
	var excelFile = {};
    $(files).each(function (i, file) {
    	if (file.fileName.indexOf('pdf') >= 0) {
    		pdfFile = file;
    	} else if (file.fileName.indexOf('xls') >= 0 || file.fileName.indexOf('xlsx') >= 0) {
    		excelFile = file;
    	}
    });
    html += "<div style='display: inline-block;'>" + pdfFile.fileName + "&nbsp;<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='view_File(" + JSON.stringify(pdfFile) + ")'>预览</button>";
    html += "<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='down_load(" + JSON.stringify(excelFile) + ")'>下载Excel</button>";
    html += "<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='down_load(" + JSON.stringify(pdfFile) + ")'>下载PDF</button></div>";
    return html;
}

// 将标签值转换成文字
function labelValueToString(labelType, labelValue, defaultValue, flowEntId, flowClass, flowId, index, productId) {
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
    			return typeStringToIncomeInfoString(labelValue, productId);
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
        return typeInvoiceInfoToString(labelValue, defaultValue, 0);
    } else if (type === 18) {
        // 对方开票信息
        return typeInvoiceInfoToString(labelValue, defaultValue, 1);
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
    } else if (type === 24) {
        return typeApplyOrderToString(labelValue, flowClass);
    } else if (type === 25) {
        return typeMatchOrderToString(labelValue, flowClass);
    } else if (type === 26) {
        // 订单编号
        return typeStringToString(labelValue, defaultValue);
    } else if (type === 27) {
        // 配单员信息
        return typeMatchPersonToString(labelValue, defaultValue);
    } else if (type === 28) {
        // 采购单编号
        return typeStringToString(labelValue, defaultValue);
    } else if (type === 29) {
        // 客户开票抬头
        return typeMutiInvoiceInfo(labelValue, defaultValue);
    } else if (type === 30) {
        // 账单开票信息
        return typeBillInvoiceToString(labelValue, defaultValue, flowEntId, flowClass, flowId);
    } else if (type === 31) {
        // 电商银行信息
		return typeDsBankInfoToString(labelValue, defaultValue);
    } else if (type === 32) {
        // 时间账单金额(供应商账单)
        return timeAccountBillToString(labelValue, defaultValue);
    } else if (type === 33){
        // 平台账号信息
        return platformAccountInfoToString(labelValue, defaultValue);
    } else if (type === 34){
        // 未对账账单
		return typeUncheckedBillToString(labelValue, defaultValue);
	} else if (type === 35){
        // 时间段
		return typeTimeSlotToString(labelValue, defaultValue);
	} else if (type === 36){
        // radiobox单选框
		return typeRadioToString(labelValue, defaultValue);
	}  else if (type === 37){
        // 充值账号
		return typeAccountRechargeToString(labelValue, defaultValue, flowClass);
	} else if (type === 38) {
        // 请假类型单选框
        return typeRadioToString(labelValue, defaultValue);
    } else if(type === 39){
        //TODO 时间段类型
        return typeNewTimeSlotToString(labelValue, defaultValue);
    }
}

// 初始化layui和渲染标签
function init_layui(data) {
    layui.use(['form', 'layedit', 'laydate', 'element', 'upload', 'dropdown', 'table'], function () {
        form = layui.form;
        layedit = layui.layedit;
        laydate = layui.laydate;
        upload = layui.upload;
        element = layui.element;
        layer = layui.layer;
        dropdown = layui.dropdown;
        table = layui.table;
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
        take_file_input(parent, label_name, label_value, default_value, disabled, required, ele_id, data.applyTime); // ele_id是流程最外层框的id（flowMsg_ + flowEntId）
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
            // $(res.othis).parents('.layui-form-item').append(write_html_invoice(res.value, 'selfInvoice'));
            $(res.othis).attr('title',$(this).attr('title'));
        });
    } else if (label_type === 18 || label_type === "18") {
        // 对方开票信息
        take_invoice_select(parent, label_name, label_value, default_value, required, disabled, label_type, 1, ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.invoice').remove();
            // $(res.othis).parents('.layui-form-item').append(write_html_invoice(res.value, 'otherInvoice'));
            $(res.othis).attr('title',$(this).attr('title'));
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
    } else if (label_type === 24 || label_type === '24') {
        // 提单信息
        take_apply_order(parent, label_name, label_value, ele_id);
        init_apply_order_table(ele_id, label_name);
    } else if (label_type === 25 || label_type === '25') {
        // 提单信息
        take_match_order(parent, label_name, label_value, ele_id);
        init_match_order_table(ele_id, label_name);
    } else if (label_type === 26 || label_type === '26') {
        // 订单编号
        take_order_no_input(parent, label_name, label_value, required, disabled);
    } else if (label_type === 27 || label_type === '27') {
        // 电商配单员选择
        take_match_input(parent, label_name, label_value, default_value, disabled, required);
    } else if (label_type === 28 || label_type === '28') {
        // 采购单编号
        take_purchase_no_input(parent, label_name, label_value, required, disabled);
    } else if (label_type === 29 || label_type === '29') {
        // 客户开票信息
        take_customer_invoice_info(parent, label_name, label_value, default_value, required, disabled, label_type,  ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.invoice').remove();
            // $(res.othis).parents('.layui-form-item').append(write_html_invoice(res.value, 'otherInvoice'));
            $(res.othis).attr('title',$(this).attr('title'));
        });
    } else if (label_type === 30 || label_type === '30') {
        // 账单信息
        take_bill_info(parent, label_name, label_value, required, disabled, ele_id, default_value, data);
    } else if (label_type === 31 || label_type === '31') {
        // 银行信息
        take_bank_info(parent, label_name, label_value, default_value, required, disabled, label_type, ele_id);
    } else if (label_type === 32 || label_type === '32') {
        renderTimeAccountBillLabel(parent, label_name, label_value, default_value, required, disabled, label_type, label_id);
    } else if (label_type === 33 || label_type === '33') {
        // 平台账号信息
        renderPlatformAccountInfo(parent, label_name, label_value, default_value, required, disabled, label_type, label_id);
    } else if (label_type === 34 || label_type === '34') {
        // 未对账账单
    	take_unchecked_bill(parent, label_name, label_value, default_value, required, disabled, label_type, ele_id, data);
	} else if (label_type === 35 || label_type === '35') {
        // 时间段
    	take_time_slot(label_name, label_value, default_value, required, disabled, label_type, ele_id, label_id);
	} else if (label_type === 36 || label_type === '36') {
        // radio单选框
        take_radio_type(parent, label_name, label_value, default_value, required, disabled, label_type, label_id, data.flowEntId);
	} else if (label_type === 37 || label_type === '37') {
        // 充值详情
		take_account_recharge(parent, label_name, label_value, disabled, ele_id, data.productId);
	} else if (label_type === 38 || label_type === '38') {
        // 请假类型
        take_leave_type(parent, label_name, label_value, default_value, required, disabled, label_type, data.flowEntId);
    }
    //TODO

}

function take_radio_type(parent, label_name, label_value, default_value, required, disabled, label_id, flowEntId) {
    var htmlStr = '';
    if (isNotBlank(default_value)) {
        $.each(default_value.split(","), function (j, value) {
            htmlStr += '<input type="radio" data-type="' + label_type + '" data-label-name="' + label_name + '"  name="radio-' + flowEntId + '-' + label_id +
                '" lay-filter="radio-' + flowEntId + '-' + label_id + '" value="' + value + '" title="' + value + '" ' + (value == label_value ? 'checked' : '') + '/>'
        });
    }
    parent.html(htmlStr);
}

// 请假类型
function take_leave_type(parent, label_name, label_value, default_value, required, disabled, label_type, flowEntId) {
    var htmlStr = '';
    htmlStr += "<div class='layui-input-block'>"; // block
    if (isNotBlank(default_value)) {
        $.each(default_value.split(","), function (j, value) {
            htmlStr += '<input type="radio" data-type="' + label_type + '" data-label-name="' + label_name + '" name="leave-type-' + flowEntId + '"' +
                ' lay-filter="leave-type-' + flowEntId + '" value="' + value + '" title="' + value + '" ' + (value == label_value ? 'checked' : '') + '/>'
        });
    }
    htmlStr += "</div>"; // block-div
    htmlStr += "<div class='layui-input-block'>"; // block
    htmlStr += "    <span class='leave-info'></span>"
    htmlStr += "    <input type='hidden' class='leave-days'/>"
    htmlStr += "</div>"; // block-div
    parent.after(htmlStr);
    parent.remove();
    bind_leave_type_check(flowEntId);
}

/**
 * 绑定请假类型单选的点击事件，查请假信息
 * @param flowId
 */
function bind_leave_type_check(flowId) {
    layui.use(['form', 'element'], function () {
        var form = layui.form;
        form.on('radio(leave-type-' + flowId + ')', function (data) {
            var ele = data.elem;
            var msgEle = $(ele).parents('div.layui-form-item').find('span.leave-info');
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

// 充值账号
function take_account_recharge(parent, label_name, label_value, disabled, ele_id, productId) {
	var htmlStr = '';
	
	var jsonArr = JSON.parse(label_value);
	
	// 展示名称
	var show_name = product_bill_label_name['[PaymentFlow]'];
	// 输入框名称
	var input_name = product_bill_input_name['[PaymentFlow]'];
	
	var id = uuid();
	
	var accounts = queryAccounts(productId);
	var resourceHtmlStr = '<div class="layui-form-item account-recharge" id="' + id + '" style="margin: 6px 0;">'
		+ '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.recharge_account + '：</label>' // 账号
	    + '<div class="layui-input-block">'
	    + '<select name="' + input_name.recharge_account + '" lay-filter="recharge-account">';
	$(accounts).each(function (i, item) {
		resourceHtmlStr += '<option value="' + item + '">' + item + '</option>';
	});
	resourceHtmlStr += '</select>'
	    + '</div>'
	    + '<label class="layui-form-label">' + show_name.current_amount + '：</label>' // 当前余额
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.current_amount + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">元</span>'
	    + '</div>'
	    + '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.price + '：</label>' // 单价
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.price + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">元</span>'
	    + '</div>'
	    + '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.recharge_amount + '：</label>' // 充值金额
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.recharge_amount + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">元</span>'
	    + '</div>'
	    + '<label class="layui-form-label"><span style="color: red;">*</span>' + show_name.pieces + '：</label>' // 条数
	    + '<div class="layui-input-block">'
	    + '<input class="layui-input notnull isnum isdecimal gradient-detail" name="' + input_name.pieces + '" placeholder="请填写" />'
	    + '<span class="gradient-unit">条</span>';
	if (accounts.length > 1) {
		resourceHtmlStr +='<div class="layui-inline" style="display: inline-block;"></div>';
	}
	resourceHtmlStr += '</div></div>';
	
	// 回显账号信息
	var selectAccounts = [];
	$(jsonArr).each(function (i, item) {
		var htmlEle = $(resourceHtmlStr);
		$(selectAccounts).each(function (ind, account) {
			htmlEle.find('select[name="' + input_name.recharge_account + '"] option[value="' + account + '"]').remove();
		});
		htmlEle.find('select[name="' + input_name.recharge_account + '"] option[value="' + item[input_name.recharge_account] + '"]').attr('selected', 'selected');
		htmlEle.find('input[name="' + input_name.current_amount + '"]').val(item[input_name.current_amount]);
		htmlEle.find('input[name="' + input_name.current_amount + '"]').attr('value', item[input_name.current_amount]);
		htmlEle.find('input[name="' + input_name.recharge_amount + '"]').val(item[input_name.recharge_amount]);
		htmlEle.find('input[name="' + input_name.recharge_amount + '"]').attr('value', item[input_name.recharge_amount]);
		htmlEle.find('input[name="' + input_name.price + '"]').val(item[input_name.price]);
		htmlEle.find('input[name="' + input_name.price + '"]').attr('value', item[input_name.price]);
		htmlEle.find('input[name="' + input_name.pieces + '"]').val(item[input_name.pieces]);
		htmlEle.find('input[name="' + input_name.pieces + '"]').attr('value', item[input_name.pieces]);
		
		selectAccounts.push(item[input_name.recharge_account]);
		
		// 添加 和 删除
		if (!disabled && (i + 1) == jsonArr.length) {
			if (jsonArr.length < accounts.length) {
				if (jsonArr.length == 1) {
					htmlEle.find('.layui-input-block:last-child .layui-inline').append('<label class="layui-form-label" style="width: fit-content; text-align: left;  padding-left: 15px">'
							+ '<span class="gradient_btn_add" id="addAccountRecharge"><i class="layui-icon layui-icon-add-circle"></i></span>'
							+ '</label>');
				} else {
					htmlEle.find('.layui-input-block:last-child .layui-inline').append('<label class="layui-form-label" style="width: fit-content; text-align: left;  padding-left: 15px">'
							+ '<span class="gradient_btn_add" id="addAccountRecharge"><i class="layui-icon layui-icon-add-circle"></i></span>'
							+ '<span class="gradient_btn_reduce" id="reduce_gradient"><i class="layui-icon layui-icon-close-fill"></i></span>'
							+ '</label>');
				}
			} else {
				htmlEle.find('.layui-input-block:last-child .layui-inline').append('<label class="layui-form-label" style="width: fit-content; text-align: left;  padding-left: 15px">'
						+ '<span class="gradient_btn_reduce" id="reduce_gradient"><i class="layui-icon layui-icon-close-fill"></i></span>'
						+ '</label>');
			}
		}
		
		htmlStr += htmlEle.prop('outerHTML');
	});
	
	htmlStr = '<input type="hidden" id="productId_' + productId + '" value="' + (!accounts ? '' : accounts.join(',')) + '">' + htmlStr;
	
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
			
			var clone = $(resourceHtmlStr);
			$(clone).attr('id', uid);
			$('[name="rechargeAccount"]').each(function (i, item) {
				$(clone).find('option[value="' + $(item).val() + '"]').remove();
			});
			
			$(clone).find('.layui-input-block:last-child .layui-inline').append('<label class="layui-form-label" style="width: fit-content; text-align: left;  padding-left: 15px">'
				+ '<span class="gradient_btn_add" id="addAccountRecharge"><i class="layui-icon layui-icon-add-circle"></i></span>'
				+ '<span class="gradient_btn_reduce" id="reduceAccountRecharge"><i class="layui-icon layui-icon-close-fill"></i></span></label>');
			
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
	
	$(parent).html(htmlStr);
	bindEvent('#' + id);
}

// 查询产品账号
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

// 时间段
function take_time_slot(label_name, label_value, default_value, required, disabled, label_type, ele_id, label_id) {
    var labelEle = $("#" + ele_id + " input[label-id='" + label_id + "']");
    label_value = isNotBlank(label_value) ? label_value : default_value;
    var datetime = label_value;
    var days;
    if (label_value.indexOf('{') > -1) {
        label_value = JSON.parse(label_value);
        datetime = label_value['datetime'];
        days = label_value['days'];
    }
    var htmlStr = '';
    // htmlStr += "<div class='layui-form-item'>"; // 表单元素一行开始
    htmlStr += "    <label class='layui-form-label'>";
    if (required) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += label_name + "：" + "</label>";
    htmlStr += "    <div class='layui-input-block'>"; // block
    htmlStr += "        <input type='text' class='layui-input layui-date-pointer' data-type='" + label_type + "' data-label-id='" + label_id + "' name='" + label_name + "' placeholder='请选择日期时间范围' value='" + datetime + "' readonly '/>"
    htmlStr += "    </div>"; // block-div
    htmlStr += "    <label class='layui-form-label'>天数：</label>";
    htmlStr += "    <div class='layui-input-block'>"; // block
    htmlStr += "        <div class='time-duration-div'>"
    htmlStr += "            <span class='time-duration' data-work-time='" + default_value + "'>" + days + "</span>"
    htmlStr += "            <span>(以工作时间" + default_value + "为1个工作日，自动扣除非工作时间)</span>"
    htmlStr += "        </div>"; // time-duration-div
    htmlStr += "    </div>"; // block-div
    // htmlStr += "</div>"; // layui-form-item
    // 插入天数dom
    labelEle.parents('div.layui-form-item').html(htmlStr);

    layui.use('laydate', function() {
        var laydate = layui.laydate;
        laydate.render({
            elem: "#" + ele_id + " input[data-label-id='" + label_id + "']",
            type: 'datetime',
            range: true,
            trigger: 'click',
            value: datetime,
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
                take_time_slot_change($(this)[0].elem, date, endDate);
            }
        });
    })
}

/**
 * 根据选择的时间段计算工作日
 *
 * @param ele       日期控件
 * @param date      开始日期
 * @param endDate   结束日期
 */
function take_time_slot_change(ele, date, endDate) {
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

// 未对账账单标签
function typeUncheckedBillToString(labelValue, defaultValue) {
	labelValue = typeof labelValue == 'object' ? labelValue : JSON.parse(labelValue);
	var html = "<br/>";
	var billInfos = labelValue.billInfos;
	$.each(billInfos, function (index, billInfo) {
		html += billInfo.title + "<br/>";
		html += "&nbsp;&nbsp;我司数据：" + thousand(billInfo.platformSuccessCount) + " X " + billInfo.platformUnitPrice + " = " + thousand(billInfo.platformAmount) + "<br/>";
		html += "&nbsp;&nbsp;客户数据：" + thousand(billInfo.customerSuccessCount) + " X " + billInfo.customerUnitPrice + " = " + thousand(billInfo.customerAmount) + "<br/>";
		html += "&nbsp;&nbsp;对账数据：" + thousand(billInfo.checkedSuccessCount) + " X " + billInfo.checkedUnitPrice + " = " + thousand(billInfo.checkedAmount) + "<br/>";
	});
	var billTotal = labelValue.billTotal;
	html += "<br/>对账总计：<br/>";
	if (isNotBlank(billTotal.platformSuccessCount) && isNotBlank(billTotal.platformAmount)) {
		html += "我司数据：计费数：" + thousand(billTotal.platformSuccessCount) + "，金额：" + thousand(billTotal.platformAmount) + "<br/>";
	}
	if (isNotBlank(billTotal.checkedSuccessCount) && isNotBlank(billTotal.checkedAmount)) {
		html += "对账数据：计费数：" + thousand(billTotal.checkedSuccessCount) + "，金额：" + thousand(billTotal.checkedAmount) + "<br/>";
	}
	var billFile = labelValue.billFile;
	if (isNotBlank(billFile)) {
		html += "电子账单：";
		html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='view_File(" + JSON.stringify(billFile) + ")'>预览</a>&nbsp;&nbsp;";
		html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='down_load(" + JSON.stringify(billFile) + ")'>下载</a></br>";
	}
	var analysisFile = labelValue.analysisFile;
	if (isNotBlank(analysisFile)) {
		html += "数据报告：";
		html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='view_File(" + JSON.stringify(analysisFile) + ")'>预览</a>&nbsp;&nbsp;";
		html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='down_load(" + JSON.stringify(analysisFile) + ")'>下载</a></br>";
	}
	return html;
}

// 时间段
function typeTimeSlotToString(labelValue, defaultValue) {
	var value = '';
	var val = isBlank(labelValue) ? defaultValue : labelValue;
	if (labelValue) {
	    var datetime;
	    var days;
	    if (labelValue.startsWith('{')) {
	        var info = JSON.parse(labelValue);
	        datetime = info['datetime'];
	        days = info['days'];
        } else {
	        datetime = labelValue;
        }
		var arr = datetime.split(' - ');
		value += str2Date(arr[0]).Format('yyyy年MM月dd日 hh:mm') + ' - ' + str2Date(arr[1]).Format('yyyy年MM月dd日 hh:mm');
		if (isNotBlank(days)) {
		    value += '<br/>天数：' + days;
        }
	}
	return'<span style="word-break: break-all">' + value + '</span>';
}

// radio单选框
function typeRadioToString(labelValue, defaultValue) {
	return '<span style="word-break: break-all">' + (isBlank(labelValue) ? defaultValue : labelValue) + '</span>';
}

//时间段 区分上下午
function typeNewTimeSlotToString (labelValue, defaultValue) {
    var value = '';
    var val = isBlank(labelValue) ? defaultValue : labelValue;
    if (labelValue) {
        value += str2Date(labelValue).Format('yyyy年MM月dd日 hh:mm');
    }
    return'<span style="word-break: break-all">' + value + '</span>';
}

// 充值账号
function typeAccountRechargeToString(labelValue, defaultValue, flowClass) {
	
	// 展示名称
    var labelName = product_bill_label_name[flowClass];
    // 输入框名称
    var inputName = product_bill_input_name[flowClass];
    
	var str = '';
	if (isBlank(labelValue)) {
		str = defaultValue;
	} else {
		var arr = JSON.parse(labelValue);
		var strArr = [];
		$(arr).each(function (i, item) {
			strArr.push(labelName.recharge_account + '【' + item[inputName.recharge_account] + '】，' 
					+ labelName.current_amount + '【'
                    + (isBlank(item[inputName.current_amount]) ? '' : parseFloat(item[inputName.current_amount]).toFixed(2))
                    + '】，' + labelName.price + '【'
					+ parseFloat(item[inputName.price]).toFixed(6) + '】，' + labelName.recharge_amount + '【' + parseFloat(item[inputName.recharge_amount]).toFixed(2) + '】，'
					+ labelName.pieces + '【' + item[inputName.pieces] + '】');
		});
		str = strArr.join('；')
	}
	return '<span style="word-break: break-all">' + (isBlank(str) ? defaultValue : (str + '。')) + '</span>';
}

// 处理未对账账单标签
function take_unchecked_bill(parent, label_name, label_value, default_value, required, disabled, label_type, ele_id, data) {
	label_value = typeof label_value == 'object' ? label_value : JSON.parse(label_value);
	var htmlStr = '';
	htmlStr += "<div class='layui-form-item unchecked-bills' data-label-name='" + label_name + "'>"; // form-item
	htmlStr += "<label class='layui-form-label show-lable' style='float: none'>";
	if (required) {
		htmlStr += "<span style='color: red;'>*</span>";
	}
	htmlStr += label_name + "：</label>";
	// 所有未对账账单
	htmlStr += "<div class='unchecked-bill-list' id='unchecked-bill-list-" + data.flowEntId + "'></div>";

	htmlStr += "<div style='padding-top: 5px'><span class='layui-icon layui-icon-tips' title='提示'></span><span>未找到？<a href='javascript:void(0);' onclick='showBuildBill(\"" + data.supplierId + "\")' style='text-decoration: underline; color: #1E9FFF'>生成账单</a></span></div>";

	var billTotal = label_value.billTotal;
	// 本次对账总计
	htmlStr += "<div class='unchecked-bill-total'>";  // total
	htmlStr += "<label class='layui-form-label unchecked-bill-total-tip'>对账总计：</label>";
    // 我司数据、对账数据总计
    if (isBlank(billTotal)) {
        billTotal = {
            'platformSuccessCount': 0,
            'platformAmount': 0.0,
            'checkedSuccessCount': 0,
            'checkedAmount': 0.0
        };
    } else if (isBlank(billTotal.platformSuccessCount)) {
        billTotal.platformSuccessCount = 0;
        billTotal.platformAmount = 0.0;
    } else if (isBlank(billTotal.checkedSuccessCount)) {
        billTotal.checkedSuccessCount = 0;
        billTotal.checkedAmount = 0.0;
    }
	htmlStr += "<div class='bill-data-total' data-type='platform'><span>我司数据：</span>" +
        "<span>计费数：</span>" +
        "<input type='text' disabled class='layui-input bill-money isnum' name='successCount' style='text-align: left;' placeholder='成功数' data-unit='条' value='" + thousand(billTotal.platformSuccessCount) + "'/>" +
        "<span>金额：</span>" +
        "<input type='text' disabled class='layui-input bill-money' style='width: 120px; text-align: left' placeholder='金额' name='amount' data-unit='元' value='" + thousand(billTotal.platformAmount) + "'/>" +
    "</div>";

	// 实际数据总计
	htmlStr += "<div class='bill-data-total' data-type='checked'><span>对账数据：</span>" +
		"<span>计费数：</span>" +
		"<input type='text' disabled class='layui-input bill-money isnum' name='successCount' style='text-align: left;' placeholder='成功数' data-unit='条' value='" + thousand(billTotal.checkedSuccessCount) + "'/>" +
		"<span>金额：</span>" +
		"<input type='text' disabled class='layui-input bill-money' style='width: 120px; text-align: left' placeholder='金额' name='amount' data-unit='元' value='" + thousand(billTotal.checkedAmount) + "'/>" +
    "</div>";
	htmlStr += "</div>";    // total

	// 对账单文件
	var billFile = label_value.billFile;
	var onclickParam = "";
	if (isNotBlank(billFile)) {
		billFile = typeof billFile == 'object' ? billFile : JSON.parse(billFile);
		file_result['billFile_' + data.flowEntId] = billFile;
		onclickParam = "this, \"" + data.flowEntId + "\", " + JSON.stringify(billFile);
	} else {
		billFile = {};
		onclickParam = "this, \"" + data.flowEntId + "\"";
	}
	var options = isNotBlank(billFile.options) ? billFile.options : 'billFile';
	htmlStr += "<div class='unchecked-bill-file'>";
	// htmlStr += "<label class='layui-form-label'>电子账单：</label>";
	htmlStr += "<input type='checkbox' lay-filter='bill-file-" + data.flowEntId + "' name='billFile' title='电子账单' lay-skin='primary' value='billFile' " + (options.indexOf('billFile') > -1 ? 'checked' : '') + "/>";
	htmlStr += "<input type='checkbox' lay-filter='bill-file-" + data.flowEntId + "' name='billFile' title='数据详情' lay-skin='primary' value='dataDetail' " + (options.indexOf('dataDetail') > -1 ? 'checked' : '') + "/>";
	htmlStr += "<button type='button' name='downloadFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='download_check_bill_file(" + onclickParam + ")'>下载</button>";
	htmlStr += "<button type='button' name='previewFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='preview_check_bill_file(" + onclickParam + ")'>预览</button></br>";
	htmlStr += "</div>";
	htmlStr += "<hr>"
	// 数据分析报告
	var analysisFile = label_value.analysisFile;
	if (isNotBlank(analysisFile)) {
		analysisFile = typeof analysisFile == 'object' ? analysisFile : JSON.parse(analysisFile);
		file_result['analysisFile_' + data.flowEntId] = analysisFile;
		onclickParam = "this, \"" + data.flowEntId + "\", " + JSON.stringify(analysisFile);
	} else {
		onclickParam = "this, \"" + data.flowEntId + "\"";
	}
	htmlStr += "<div class='bill-analysis-file'>";
	htmlStr += "<label class='layui-form-label'>数据报告：</label>";
	htmlStr += "<button type='button' name='downloadFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='download_data_analysis_file(" + onclickParam + ")'>下载</button>";
	htmlStr += "<button type='button' name='previewFile' class='layui-btn layui-btn-xs unchecked-bill-file-tip' onclick='preview_data_analysis_file(" + onclickParam + ")'>预览</button></br>";
	htmlStr += "</div>";

	htmlStr += "</div>";     // form-item
	get_unchecked_bill_info(label_value.billInfos, data.flowEntId, data.supplierId);
	init_bill_file_option_check(data.flowEntId);
	$(parent).parents(".layui-form-item").html(htmlStr);
}

//电子账单、数据详情复选框事件
function init_bill_file_option_check(flowId) {
	layui.use('form', function () {
		var form = layui.form;
		form.on('checkbox(bill-file-' + flowId + ')', function (data) {
			// 每次重新计算总计之后，重新绑定下载、预览按钮的事件
			var billFileEle = $(this).parents('div.unchecked-bill-file');
			billFileEle.find('button[name=downloadFile]').attr('onclick', "download_check_bill_file(this, \"" + flowId + "\")");
			billFileEle.find('button[name=previewFile]').attr('onclick', "preview_check_bill_file(this, \"" + flowId + "\")");
			delete file_result['billFile_' + flowId];
		})
	})
}

// 电子账单、数据详情复选框事件
function init_bill_file_option_check(flowId) {
	layui.use('form', function () {
		var form = layui.form;
		form.on('checkbox(bill-file-' + flowId + ')', function (data) {
			// 每次重新计算总计之后，重新绑定下载、预览按钮的事件
			var billFileEle = $(this).parents('div.unchecked-bill-file');
			billFileEle.find('button[name=downloadFile]').attr('onclick', "download_check_bill_file(this, \"" + flowId + "\")");
			billFileEle.find('button[name=previewFile]').attr('onclick', "preview_check_bill_file(this, \"" + flowId + "\")");
			delete file_result['billFile_' + flowId];
		})
	})
}

// 下载已选中产品的对账单，不存在时先生成
function download_check_bill_file(ele, flowEntId, billFile) {
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
					file_result['billFile_' + flowEntId] = billFile;
					$(ele).attr('onclick', "download_check_bill_file(this, \"" + flowEntId + "\", " + JSON.stringify(billFile) +")");
					$(ele).next().attr('onclick', "preview_check_bill_file(this, \"" + flowEntId + "\", " + JSON.stringify(billFile) +")");
					down_load(billFile);
				} else {
					delete file_result['billFile_' + flowEntId];
					layui.layer.msg(res.msg);
				}
			})
		}
	}
}

// 预览已选中产品的对账单，不存在时先生成
function preview_check_bill_file(ele, flowEntId, billFile) {
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
					$(ele).attr('onclick', "preview_check_bill_file(this, \"" + flowEntId + "\", " + JSON.stringify(billFile) +")");
					$(ele).prev().attr('onclick', "download_check_bill_file(this, \"" + flowEntId + "\", " + JSON.stringify(billFile) +")");
					view_File(billFile);
				} else {
					layui.layer.msg(res.msg);
				}
			})
		}
	}
}

// 下载已选中产品的数据分析报告，不存在时先生成
function download_data_analysis_file(ele, flowEntId, analysisFile) {
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
					file_result['analysisFile_' + flowEntId] = analysisFile;
					$(ele).attr('onclick', "download_data_analysis_file(this, \"" + flowEntId + "\", " + JSON.stringify(analysisFile) +")");
					$(ele).next().attr('onclick', "preview_data_analysis_file(this, \"" + flowEntId + "\", " + JSON.stringify(analysisFile) +")");
					layui.layer.alert('正在生成中，请勿离开。稍后请再次点击下载按钮即可下载', {icon: 7})
					// down_load(billFile);
				} else {
					delete file_result['analysisFile_' + flowEntId];
					layui.layer.msg(res.msg);
				}
			})
		}
	}
}

// 下载已选中产品的数据分析报告，不存在时先生成
function preview_data_analysis_file(ele, flowEntId, analysisFile) {
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
					file_result['analysisFile_' + flowEntId] = analysisFile;
					$(ele).attr('onclick', "preview_data_analysis_file(this, \"" + flowEntId + "\", " + JSON.stringify(analysisFile) +")");
					$(ele).prev().attr('onclick', "download_data_analysis_file(this, \"" + flowEntId + "\", " + JSON.stringify(analysisFile) +")");
					layui.layer.alert('正在生成中，请勿离开。稍后请再次点击预览按钮即可预览', {icon: 7})
					// down_load(billFile);
				} else {
					delete file_result['analysisFile_' + flowEntId];
					layui.layer.msg(res.msg);
				}
			})
		}
	}
}

function get_unchecked_bill_info(billInfos, flowEntId, entityId) {
	$.ajax({
		type: "POST",
		async: true,
		url: '/bill/readUncheckedBills?temp=' + Math.random(),
		dataType: 'json',
		data: {
			customerId: entityId,
			flowEntId: flowEntId
		},
		success: function (data) {
			// 渲染标签
			if (data.code == 200) {
				var billsEle = $('#unchecked-bill-list-' + flowEntId);
				var bills = data.data;
				var html = "";
				$.each(bills, function (index, bill) {
					// 用已选中的账单数据替换查询到的账单（只替换客户、对账数据，我司数据以账单为准）
					for (var billIndex = 0; billIndex < billInfos.length; billIndex++) {
						if (billInfos[billIndex]['id'] === bill.id) {
							var checked = billInfos[billIndex];
							bill.customerSuccessCount = checked.customerSuccessCount;
							bill.customerUnitPrice = checked.customerUnitPrice;
							bill.customerAmount = checked.customerAmount;
							bill.checkedSuccessCount = checked.checkedSuccessCount;
							bill.checkedUnitPrice = checked.checkedUnitPrice;
							bill.checkedAmount = checked.checkedAmount;
							bill.checked = true;
							break;
						}
					}
					html += add_unchecked_bill_info_item(flowEntId, bill);
				})
				billsEle.html(html);
				// 绑定账单选中事件
				layui.use('form', function() {
					var form = layui.form;
					form.render();
					form.on('checkbox(unchecked-bill-item-' + flowEntId + ')', function(data) {
						take_unchecked_bill_total(flowEntId);
					});
				});
			}
		}
	});
}

// 拼接一个未对账账单的样式
function add_unchecked_bill_info_item(flowEntId, bill) {
	var html = "";
	html += "<div class='unchecked-bill-item' id='" + bill.id + "'>";
	html += "   <input type='checkbox' lay-filter='unchecked-bill-item-" + flowEntId + "' title='" + bill.title + "' lay-skin='primary'" + (bill.checked === true ? " checked" : "") + " >";
	html += "   <span class='layui-icon layui-icon-refresh unchecked-bill-tools' title='重新统计' onclick='rebuild_bill(this, \"" + flowEntId + "\", \"" + bill.productId + "\", \"" + bill.billMonth + "\")'></span>";
	var remark = bill.remark;
	if (isNotBlank(remark)) {
		html += "   <span class='layui-icon layui-icon-rmb unchecked-bill-tools' title='价格详情' onclick='show_remark(this)'></span>";
		html += "   <div class='bill-remark layui-nav-child layui-anim layui-anim-upbit'>" + remark;
		html += "   <span class='layui-icon layui-icon-close-fill' title='关闭' onclick='show_remark(this)' style='position: absolute; top: -30px; font-size: 30px; text-align: center'></span>";
		html += "   </div>"
	}
	var billFile = bill.billFile;
	if (isNotBlank(billFile)) {
		// billFile = typeof billFile == 'object' ? billFile : JSON.parse(billFile);
		html += "   <span class='layui-icon layui-icon-file unchecked-bill-tools' title='电子账单' onclick='view_File(" + billFile + ")'></span>";
	}
	// 我司数据，后台统计的，不可修改
	html += "<div class='bill-data' data-type='platform'><span>我司数据</span>" +
		"<input type='text' disabled class='layui-input bill-money isnum' name='successCount' placeholder='成功数' value='" + bill.platformSuccessCount + "' data-unit='条' />" +
		"<span>X</span>" +
		"<input type='text' disabled class='layui-input bill-money isnum' name='unitPrice' style='width: 60px' placeholder='单价' value='" + bill.platformUnitPrice + "' data-unit='元' />" +
		"<span>=</span>" +
		"<input type='text' disabled class='layui-input bill-money' style='width: 120px' placeholder='金额' name='amount' value='" + bill.platformAmount + "' data-unit='元'/></div>";
	// 客户数据，默认填充的是后台统计数据，可修改
	html += "<div class='bill-data' data-type='customer'><span>客户数据</span>" +
		"<input type='text' class='layui-input bill-money isnum' name='successCount' placeholder='成功数' onchange='take_unchecked_bill_change(this, \"" + flowEntId + "\")' value='" + bill.customerSuccessCount + "' data-unit='条' />" +
		"<span>X</span>" +
		"<input type='text' class='layui-input bill-money isnum' name='unitPrice' style='width: 60px' placeholder='单价' onchange='take_unchecked_bill_change(this, \"" + flowEntId + "\")' value='" + bill.customerUnitPrice + "' data-unit='元' />" +
		"<span>=</span>" +
		"<input type='text' class='layui-input bill-money' style='width: 120px' placeholder='金额' onchange='take_unchecked_bill_total(\"" + flowEntId + "\")' name='amount' value='" + bill.customerAmount + "' data-unit='元'/></div>";
	// 对完账后实际得出的数据，默认填充的是后台统计数据，可修改
	html += "<div class='bill-data' data-type='checked'><span>对账数据</span>" +
		"<input type='text' class='layui-input bill-money isnum' name='successCount' placeholder='成功数' onchange='take_unchecked_bill_change(this, \"" + flowEntId + "\")' value='" + bill.checkedSuccessCount + "' data-unit='条' />" +
		"<span>X</span>" +
		"<input type='text' class='layui-input bill-money isnum' name='unitPrice' style='width: 60px' placeholder='单价' onchange='take_unchecked_bill_change(this, \"" + flowEntId + "\")' value='" + bill.checkedUnitPrice + "' data-unit='元' />" +
		"<span>=</span>" +
		"<input type='text' class='layui-input bill-money' style='width: 120px' placeholder='金额' onchange='take_unchecked_bill_total(\"" + flowEntId + "\")' name='amount' value='" + bill.checkedAmount + "' data-unit='元'/></div>";
	html += "</div>";
	return html;
}

function show_remark(ele) {
	$(ele).parents('div.unchecked-bill-item').find('div.bill-remark').toggleClass('layui-show');
}

// 账单后面的刷新按钮的点击事件
function rebuild_bill(ele, flowEntId, productId, yearMonth) {
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
				take_unchecked_bill_total(flowEntId);
				parent.layer.msg(res.msg, {time: 5000});
			} else {
				parent.layer.msg(res.msg, {time: 5000});
			}
		})
	})
}

function take_unchecked_bill_change(ele, flowEntId) {
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
	take_unchecked_bill_total(flowEntId);
}

// 自动计算对账总计
function take_unchecked_bill_total(flowEntId) {
	// 我司数据总计
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
	var billsEle = $('#flowMsg_' + flowEntId).find('div.unchecked-bills');
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
	billFileEle.find('button[name=downloadFile]').attr('onclick', "download_check_bill_file(this, \"" + flowEntId + "\")");
	billFileEle.find('button[name=previewFile]').attr('onclick', "preview_check_bill_file(this, \"" + flowEntId + "\")");
	delete file_result['billFile_' + flowEntId];

	var analysisFileEle = billsEle.find('div.bill-analysis-file');
	analysisFileEle.find('button[name=downloadFile]').attr('onclick', "download_data_analysis_file(this, \"" + flowEntId + "\")");
	analysisFileEle.find('button[name=previewFile]').attr('onclick', "preview_data_analysis_file(this, \"" + flowEntId + "\")");
	delete file_result['analysisFile_' + flowEntId];
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

function showBillsRecordsDetail(ele) {
	$(ele).parents('.recordContent').find('.bills-records').toggleClass('layui-show');
}

// 展示账单详情
function buildBillsRecordsDetail(html, baseData) {
	var detail = $('<div class="bills-records layui-nav-child layui-anim layui-anim-upbit"></div>');
	detail.append('<table class="flow-view-table" cellpadding="0" cellspacing="0"></table>');
	var tableThHtml = $('<tr class="flow-view-table-th"></tr>');
	tableThHtml.append('<td class="flow-view-table-th" width="30%">时间段</td>');
	tableThHtml.append('<td class="flow-view-table-th" width="40%">价格信息</td>');
	tableThHtml.append('<td class="flow-view-table-th" width="30%">发送量</td>');
	detail.find('table').append(tableThHtml);
	var billsDetail = baseData['BILL_PRICE_INFO_KEY'];
	if (typeof billsDetail == 'string') {
		billsDetail = JSON.parse(billsDetail);
	}
	$(billsDetail).each(function (index, item) {
		var tableTrHtml = $('<tr class="flow-view-table-th" style="border-top: 1px solid #e3e3e3;"></tr>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.timeQuantum.split('、').join('<br/>') + '</td>');
		if (item.modifyPriceInfo.endWith('xls') || item.modifyPriceInfo.endWith('xlsx')) {
			var arr = item.modifyPriceInfo.split(/[\\/]/);
			var json = {
				fileName: (arr[arr.length - 1]),
				filePath: item.modifyPriceInfo
			};
			var aHtml = '<a style="text-decoration: underline" href="javascript:void(0);" onclick="down_load(' + JSON.stringify(json).replace(/"/g, '&quot;') + ')">调价文件</a>'
			tableTrHtml.append('<td class="flow-view-table-td" style="white-space:pre-line;word-wrap: break-word;word-break: break-all;">' + aHtml + '</td>');
		} else {
			tableTrHtml.append('<td class="flow-view-table-td" style="white-space:pre-line;word-wrap: break-word;word-break: break-all;">' + item.modifyPriceInfo + '</td>');
		}
		tableTrHtml.append('<td class="flow-view-table-td">' + thousand(item.successCount) + (item.provinceSuccessCount ? ('（省网：' + thousand(item.provinceSuccessCount) + '）') : '' ) + '</td>');
		detail.find('table').append(tableTrHtml);
	});
	return html + detail.prop("outerHTML");;
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

// 展示详细的银行信息
function take_bank_info(parent, label_name, label_value, default_value,
                        required, disabled, label_type, ele_id) {
    if (isBlank(label_value)) {
        return "";
    }
    var html = '<div class="layui-form-item bank-info-ds">';
    var array = (typeof label_value == 'object') ? label_value : JSON
        .parse(label_value);
    html += '<input type="hidden" id="bankInfoCount" value="' + array.length + '">'; // 梯度个数
//    html += '<input type="hidden" id="dsBankInfos" value=' + label_value + '>'; // 银行信息
    for ( var index in array) {
        var json = array[index];
        html += '<div class="bank-info-line" data-lable-name="" id="bankInfo' + index + '">'
            + '<label class="layui-form-label"><span style="color: red;">*</span>名称：</label><div class="layui-input-block"><input name="accountName" class="layui-input" value="'
            + (json.accountName ? json.accountName : '')
            + '" /></div>'
            + '<label class="layui-form-label"><span style="color: red;">*</span>开户银行：</label><div class="layui-input-block"><input name="accountBank" class="layui-input" value="'
            + (json.accountBank ? json.accountBank : '')
            + '" /></div>'
            + '<label class="layui-form-label"><span style="color: red;">*</span>银行账号：</label><div class="layui-input-block"><input name="bankAccount" class="layui-input" value="'
            + (json.bankAccount ? json.bankAccount : '') + '" /></div>'
            + '</div>';
    }
    html += "<div class='bankInfoOperate'>"
    html += "<span class='gradient_btn_add' id='add_bank_info' onclick=\"addBankInfoClick('"
        + ele_id
        + "')\"> <i class='layui-icon layui-icon-add-circle'></i></span>"
    html += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_bank_info' onclick=\"reduceBankInfoClick('"
        + ele_id
        + "')\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
    html += '</div>'; // bankInfoOperate
    html += '</div>'; // bank-info-ds
    $(parent).html(html);

}

// 增加银行信息按钮的点击事件
function addBankInfoClick(ele_id) {
    var ele = $("#" + ele_id);
    // 当前银行信息个数
    var count = parseInt(ele.find("#bankInfoCount").val());
    var lastBankInfoDiv = $("#bankInfo" + (count - 1));
    var operateDiv = ele.find(".bankInfoOperate");
    operateDiv.remove();
    var htmlStr = "";

    htmlStr += '<div class="bank-info-line" data-lable-name="" id="bankInfo'
        + count
        + '">'
        + '<label class="layui-form-label"><span style="color: red;">*</span>名称：</label><div class="layui-input-block"><input name="accountName" class="layui-input" value="" /></div>'
        + '<label class="layui-form-label"><span style="color: red;">*</span>开户银行：</label><div class="layui-input-block"><input name="accountBank" class="layui-input" value="" /></div>'
        + '<label class="layui-form-label"><span style="color: red;">*</span>银行账号：</label><div class="layui-input-block"><input name="bankAccount" class="layui-input" value="" /></div>'
        + '</div>';
    htmlStr += "<div class='bankInfoOperate'>"
    htmlStr += "<span class='gradient_btn_add' id='add_bank_info' onclick=\"addBankInfoClick('"
        + ele_id
        + "')\"> <i class='layui-icon layui-icon-add-circle'></i></span>"
    htmlStr += "&nbsp;&nbsp;<span class='gradient_btn_reduce' id='reduce_bank_info' onclick=\"reduceBankInfoClick('"
        + ele_id
        + "')\"><i class='layui-icon layui-icon-close-fill' ></i></span>";
    htmlStr += '</div>'; // block

    lastBankInfoDiv.after(htmlStr);

    layui.use('form', function() {
        var form = layui.form;
        form.render();
    });

    // 银行信息个数加1
    count++;
    ele.find("#bankInfoCount").val(count);
    // initValidate(id);

}

// 减少银行信息按钮的点击事件
function reduceBankInfoClick(ele_id) {
    var ele = $("#" + ele_id);
    // 当前银行信息个数
    var count = parseInt(ele.find("#bankInfoCount").val());
    if( count == 1|| count < 1){
    	layer.msg("银行信息不能为空！");
    	return;
    }
    var lastBankInfoDiv = $("#bankInfo" + (count - 1));
    lastBankInfoDiv.remove();
    count--;
    ele.find("#bankInfoCount").val(count);
}

/**
 * 渲染时间账单信息
 * @param parent
 * @param labelName
 * @param labelValue
 * @param defaultValue
 * @param required
 * @param disabled
 * @param labelType
 * @param labelId
 * @returns {string}
 */
function renderTimeAccountBillLabel(parent, labelName, labelValue, defaultValue, required, disabled, labelType, labelId) {
    $(parent).addClass("time-account-bill");
    $(parent).attr("data-bill-index", 1);
    // 初始化时间账单金额标签
    var accountBillItem = $(parent).parent();
    $(accountBillItem).addClass("time-bill-money-item");
    $(accountBillItem).attr("data-id", labelId);
    $(accountBillItem).attr("data-label-name", labelName);
    var accountBillInfos = [];
    if (isNotBlank(labelValue)) {
        try {
            accountBillInfos = JSON.parse(labelValue);
        } catch (e) {
            var labelvalues = labelValue.split(",");
            var start = "";
            var end = "";
            // 根据账单月份 进行时间的渲染
            try {
                var flowInfo = $(parent).parents().find("xmp[name='flowData']").html();
                var flowInfoObj = JSON.parse(flowInfo);
                var labelInfos = flowInfoObj.labelValueMap;
                var billMonth = labelInfos['账单月份'];
                if (isNotBlank(billMonth)) {
                    start = billMonth + "-01";
                    end = new Date(billMonth.split("-")[0], parseInt(billMonth.split("-")[1]), 0);
                    var month = end.getMonth() + "";
                    end = end.getFullYear() + "-" + (month.length === 1 ? "0" + month : month) + "-" + end.getDate();
                }
            } catch (e2) {
            }
            accountBillInfos.push({
                "start": start,
                "end": end,
                "success": labelvalues[0],
                "price": labelvalues[1],
                "total": labelvalues[2]
            });
        }
    }
    console.log("开始渲染--时间账单标签");
    if (accountBillInfos.length > 0) {
        for (var billIndex = 0; billIndex < accountBillInfos.length; billIndex++) {
            var billItem = accountBillInfos[billIndex];
            var billItemDom = getRenderAccountBillDom(billIndex + 1, billItem);
            if (billIndex === 0) {
                $(parent).html(billItemDom);
                $(parent).after(renderAccountBillTotalDom());
                renderAddItemDate(parent);
            } else {
                $(parent).after(billItemDom);
            }
            renderUpdateTotal($(parent).find("input[name='time_success']")[0]);
        }
    } else {
        var billItemDom = getRenderAccountBillDom(1, null);
        $(parent).html(billItemDom);
        $(parent).after(renderAccountBillTotalDom());
        // 最开始就是第一行
        renderAddItemDate(parent);
    }
}

// 获取账户计费信息
function getRenderAccountBillDom(index,itemValue) {
    var start = "";
    var end = "";
    var success = "";
    var price = "";
    var total = "";
    if (!isBlank(itemValue)) {
        start = itemValue.start;
        end = itemValue.end;
        success = itemValue.success;
        price = itemValue.price;
        total = itemValue.total;
    }
    index = parseInt(index);
    var baseDom = "";
    if (index > 1) {
        baseDom += " <div class='layui-input-block time-account-bill' data-bill-index='" + index + "'>";
    }
    baseDom +=
        "<input type='text' class='layui-input time-start' name='time_start' title='开始日期' value='"+start+"' placeholder='开始日期' readonly>" +
        "-" +
        "<input type='text' class='layui-input time-end' name='time_end' title='结束日期'  value='"+end+"' placeholder='结束日期' readonly> <br>" +
        "<input type='text' class='layui-input success' name='time_success' onblur='renderTimeAccountBillChange(this,1," + index + ")'  value='"+success+"' title='成功数'  placeholder='成功数' data-unit='条' />" +
        "<span>X</span>" +
        "<input type='text' class='layui-input price' name='time_price' onblur='renderTimeAccountBillChange(this,2," + index + ")'  value='"+price+"' title='单价' placeholder='单价' data-unit='元' />" +
        "<span>=</span><br>" +
        "<input type='text' class='layui-input total-money' name='time_total_money'  value='"+total+"' onblur='renderTimeAccountBillChange(this,3," + index + ")' title='金额' placeholder='金额' data-unit='元'/>" +
        "<span class='btn-opts'> " +
        "   <i class='layui-icon layui-icon-add-1' onclick='renderTimeAccountBillBtn(this,1," + (index + 1) + ")'></i>";
    if (index > 1) {
        baseDom +=
            "<br>" +
            " <i class='layui-icon layui-icon-close' onclick='renderTimeAccountBillBtn(this,2," + index + ")'></i>";
    }
    baseDom += "</span>" +
        "<span class='time-account-bill-index'>" + index + "</span>" +
        "  </div>";
    if (index > 1) {
        baseDom += "</div>";
    }
    return baseDom;
}

/**
 * 时间账单金额总计
 * @returns {string}
 */
function renderAccountBillTotalDom() {
    return  "<div class='layui-input-block time-account-bill-total'>" +
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

// 按钮
function renderTimeAccountBillBtn(ele, type, index) {
    index = parseInt(index);
    if (type === 1) {
        if (index < 31) {
            var canAdd = false;
            if (index > 1) {
                var labelItem = $(ele).parent().parent().parent();
                canAdd = renderTimeAccountBillValidate(labelItem);
            }
            // 添加
            if (canAdd) {
                var newDom = getRenderAccountBillDom(index);
                $(ele).parent().parent().after(newDom);
                var newItem = $(ele).parent().parent().parent().find("div[data-bill-index='"+index+"']");
                renderAddItemDate(newItem);
                // 删除原来的按钮
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
                "<i class='layui-icon layui-icon-add-1' onclick='renderTimeAccountBillBtn(this,1," + index + ")'></i>";
            if (index > 2) {
                // 有删除按钮
                optsDom += "<br>" +
                    " <i class='layui-icon layui-icon-close' onclick='renderTimeAccountBillBtn(this,2," + (index-1) + ")'></i>";

            }
            optsDom += "</span>";
            var beforeItem = $("div[data-bill-index='" + (index-1) + "']");
            beforeItem.append(optsDom);
            renderUpdateTotal(beforeItem.find("input[name='time_success']")[0]);
        }
    }
}

// 校验 时间账单 标签内容
function renderTimeAccountBillValidate(labelItem) {
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
function renderTimeAccountBillChange(ele, type, index) {
    var thisValue = $(ele).val();
    if (isBlank(thisValue)) {
        renderUpdateTotal(ele);
        layer.tips("不能为空", ele);
        return;
    }
    if (!$.isNumeric(thisValue)) {
        renderUpdateTotal(ele);
        layer.tips("只能填写数字", ele);
        return ;
    }
    if (type === 1 && !(/^[0-9]*$/.test(thisValue)) ){
        $(ele).val("");
        renderUpdateTotal(ele);
        layer.tips("只能填写整数", ele);
        return ;
    }
    if (thisValue <= 0){
        $(ele).val("");
        renderUpdateTotal(ele);
        layer.tips("必须大于0", ele);
        return;
    }
    /*var price = $("input[name='time_success_" + index).val();
    var success = $("#time_price_" + index).val();
    if (price > 0 && success > 0) {
        $("#time_total_money_" + index).val((price * success).toFixed(4));
    }*/
    if (type !== 3) {
        var item = $(ele).parent();
        var success = $(item).find("input[name='time_success']").val();
        var price = $(item).find("input[name='time_price']").val();
        if (isBlank(price)) {
            price = 0;
        }
        if (isBlank(success)) {
            success = 0;
        }
        $(item).find("input[name='time_total_money']").val((price * success).toFixed(4));
    }
    renderUpdateTotal(ele);
}

function renderUpdateTotal(ele) {
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
            totalMoney += (itemSuccess * parseFloat(itemPrice))
            var itemTotal = $(item).find("input[name='time_total_money']").val();
            if (isBlank(itemTotal)) {
                itemTotal = 0.00;
            }
            inputTotalMoney += parseFloat(itemTotal);
        }
    }
    var totalItem = $(label).find("div[class*='time-account-bill-total']");
    $(totalItem).find("td[class*='total-success']").html(success);
    var averageMoney = 0.0000;
    if (success > 0) {
        averageMoney = totalMoney / success;
    }
    $(totalItem).find("td[class*='average-price']").html(averageMoney.toFixed(6));
    $(totalItem).find("td[class*='total-money']").html(inputTotalMoney.toFixed(2));
}



/**
 * 渲染平台账户信息
 * @param parent
 * @param labelName
 * @param labelValue
 * @param defaultValue
 * @param required
 * @param disabled
 * @param labelType
 * @param id
 * @returns {string|*}
 */
function renderPlatformAccountInfo(parent, labelName, labelValue, defaultValue, required, disabled, labelType, labelId) {
    $(parent).addClass("platform-account-item");
    $(parent).attr("data-platform-account-index", 1);
    $(parent).attr("data-id",labelId);
    var accountInfos = [];
    if (isNotBlank(labelValue)) {
        accountInfos = JSON.parse(labelValue);
    }
    if (accountInfos.length === 0) {
        var dom = getRenderPlatformAccountDom(1, null);
        $(parent).html(dom);
    } else {
        for (var index = 0; index < accountInfos.length; index++) {
            var accountInfo = accountInfos[index];
            var dom = getRenderPlatformAccountDom(index + 1, accountInfo);
            if (index === 0) {
                $(parent).html(dom);
            } else {
                $(parent).parent().next(dom);
            }
        }
    }
}

/**
 * 获取组装内容
 * @param index
 * @param value
 * @returns {string}
 */
function getRenderPlatformAccountDom(index, value) {
    index = parseInt(index);
    var account = "";
    var pwd = "";
    var note = "";
    if (isNotBlank(value)) {
        account = value.account;
        pwd = value.pwd;
        note = value.note;
    }
    var dom = "";
    if (index > 1) {
        dom = "<div class='layui-input-block platform-account-item' data-platform-account-index='" + index + "'>";
    }
    dom += " <input type='text' class='layui-input account' name='account' value='" + account + "' title='请填写账号' placeholder='请填写账号' >" +
        "    <input type='text' class='layui-input password ' name='password' value='" + pwd + "' title='请填写账号密码' placeholder='请填写账号密码'>" +
        "    <input type='text' class='layui-input note' name='note' value='" + note + "' title='请填写描述（100字以内）' placeholder='请填写描述（100字以内）'>" +
        getRenderPlatfromAccountOpts(index) +
        "    <span class='platform-index'>" + index + "</span>" +
        "</div>";
    return dom;
}

/**
 * 获取操作的dom
 * @param index
 * @returns {string}
 */
function getRenderPlatfromAccountOpts(index) {
    var optsDom =
        "<span class='platform-opts'>" +
        "   <i class='layui-icon layui-icon-add-1' onclick='addRenderPlatformAccount(this," + (index + 1) + ")'></i>";
    if (index > 1) {
        optsDom += "<br><i class='layui-icon layui-icon-close' onclick='delRenderPlatformAccount(this," + index + ")'></i>";
    }
    optsDom += "</span>";
    return optsDom;
}


/**
 * 添加项
 * @param ele
 * @param index
 */
function addRenderPlatformAccount(ele, index) {
    // 判断上一个已经填写完
    var items = $(ele).parent().parent().parent().find("div[data-platform-account-index]");
    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex];
        var account = $(item).find("input[name='account']").val();
        if (isBlank(account)) {
            layer.tips("账号不能为空", item);
            return;
        }
    }
    var newItem = getRenderPlatformAccountDom(index, null);
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
function delRenderPlatformAccount(ele, index) {
    index = parseInt(index);
    var thisItem = $(ele).parent().parent();
    if (index > 1) {
        var optsDom = getRenderPlatfromAccountOpts(index - 1);
        $(thisItem).prev().append(optsDom);
    }
    thisItem.remove();
}

//账单信息
function take_bill_info(parent, label_name, label_value, required, disabled, ele_id, default_value, data){
    if (typeof label_value == 'object') {
        label_value = JSON.stringify(label_value);
    }
    if (label_value.startWith('\\{')) {
        take_product_bill(parent, label_name, label_value, default_value, required, disabled, data)
    } else if (label_value.startWith('\\[')) {
		// 账单开票流程，账单信息标签
		initBillInfoTable(parent, ele_id, label_value, data.flowClass, required, label_name, data.flowId, data.flowEntId, data.supplierId);
    }
}

// 账单开票流程，账单信息标签
//[{"actualInvoiceAmount":0,"id":"402812816fac35a3016fac3db7ce0005","receivables":100000000,"thisReceivables":"100000000","title":"账单-2019-07-8523东区客户1-8523东区短信1"}]
function initBillInfoTable(parent, ele_id, label_value, flowClass, required, label_name, flowId, flowEntId, entityId) {
    label_value = typeof label_value == 'object'? label_value : JSON.parse(label_value);
    var htmlStr = '';
    htmlStr += '<div>';
    if (required) {
        htmlStr += "<span style='color: red;'>*</span>" // 必填标签
    }
    htmlStr += label_name+ '：</div>';
    var newEle = parent.parent();
    newEle.html(htmlStr);
    htmlStr = '';
    htmlStr += '<div class="layui-small-table">' +
        '<table id="selectBillInfo'+ele_id+'" class="layui-hide" lay-filter="select-bill-info'+ele_id+'" select-required = "'+required+'" data-label-name="'+label_name+'"></table>' +
        '<input type="hidden" id="' + ele_id + '" name="' + ele_id + '"/>' +
        '<i class="bill_info_sum_tip'+ele_id+'" style="font-size: 18px;color: red;">账单合计：' +
        '<span class="bill_info_sum_pay'+ele_id+'" style="font-size: 18px;color: red;"></span>&nbsp;元' +
        '</i>' +
        '</div>';
    setTimeout(function () {
        layui.use([ 'table', 'form' ], function() {
            var table = layui.table;
            var form = layui.form;
            table.render({
                url:  "/customerOperate/readInvoiceableBills.action?temp=" + Math.random(),
                elem : '#selectBillInfo'+ele_id,
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
						if(data.thisReceivables){
                            return  "<span class='layui-icon layui-icon-edit billInvoiceAmountTip' title='修改'></span><input id = 'thisBillPayment_"+data.id+ele_id+"' name ='thisBillPayment' data-param-name='" + data.id + "' class='layui-input billInvoiceAmount' value='"+data.thisReceivables+"' data-unit='元'/>";
                        }
                        return  "<span class='layui-icon layui-icon-edit billInvoiceAmountTip' title='修改'></span><input id = 'thisBillPayment_"+data.id+ele_id+"' name ='thisBillPayment' data-param-name='" + data.id + "' class='layui-input billInvoiceAmount' value='' disabled data-unit='元'/>";
                    }
                } ]],
                parseData : function(res) {
                    if(isBlank(res.data)){
                        res.data = label_value;
                    }
                    var tableData = [];
                    for (var i = 0; i < res.data.length; i++) {
                        var data = res.data[i];
                        $.each(label_value,function(j,item){
                            if (item.id == res.data[i].id) {
                                data.LAY_CHECKED = true;
                                data.thisReceivables = item.thisReceivables
                                return false;
                            }
                        })
                        tableData.push(data);
                    }
                    return {
                        "code" : 0,
                        "count" : res.count,
                        "data" : tableData
                    };

				},
				where: {
					customerId: entityId,
					needOrder: 'T',
					flowClass: flowClass,
					flowId: flowId,
					flowEntId: flowEntId
				},
				done: function (res, curr, count) {
					// 渲染完成的回调
					// 获取所有选中的账单，计算账单的合计（每个账单开多少票）
					var calcTotal = function () {
						setTimeout(function () {
							var checkStatus = table.checkStatus('selectBillInfo' + ele_id);
							var selectData = checkStatus.data;
							var sum = 0.00;
							if (selectData && selectData.length > 0) {
								$(selectData).each(function (index, bills) {
									var thisBillPayment = $('#thisBillPayment_' + bills.id + ele_id).val();
									sum = accAdd(sum, thisBillPayment);
								});
							}
							$('.bill_info_sum_pay' + ele_id).html(sum.toFixed(2));
						}, 10);
					}
					var bindClick = function () {
						$('#selectBillInfo' + ele_id).next().find('[data-field="checked"]').bind('click', function () {
							calcTotal()
						});
						$.each($('input[name=thisBillPayment]'), function (i, item) {
							$(item).on("change", function () {
								calcTotal();
							});
						})
					}
					bindClick();
					calcTotal();
				}
			});

			table.on('checkbox(select-bill-info' + ele_id + ')', function (obj) {
				var thisBillPayment = $('#thisBillPayment_' + obj.data.id + ele_id);
				if (obj.checked) {
					// 选中之后置为0，要求手动填金额
					$(thisBillPayment).removeAttr('disabled');
					$(thisBillPayment).attr('value', 0);
				} else {
					$(thisBillPayment).attr('disabled', true);
					$(thisBillPayment).attr('value', '');
				}
			});
		});
	}, 5);
    parent.remove();
    newEle.after(htmlStr);
}

// 处理客户开票抬头标签
function take_customer_invoice_info(parent, label_name, label_value, default_value, required, disabled, label_type, ele_id, entity_id) {
    //移除原有标签
    if (typeof label_value == 'object') {
        label_value = JSON.stringify(label_value);
    }
    if (label_value.startWith('\\{')) {
        take_invoice_select(parent, label_name, label_value, default_value, required, disabled, label_type, 1, ele_id, entity_id);
        form.on('select(' + (ele_id + label_name) + ')', function (res) {
            $(res.othis).parents('.layui-form-item').find('div.invoice').remove();
            $(res.othis).parents('.layui-form-item').append(write_html_invoice(res.value, 'otherInvoice'));
        });
    } else if (label_value.startWith('\\[')){
        var lastgradientDiv = $(parent).parent('div.layui-form-item')
        var newOperateGradientDiv = lastgradientDiv.prev();
        lastgradientDiv.remove();
        //重新喧染标签
        label_value = typeof label_value == 'object' ? label_value : eval(label_value);
        var invoiceSize = label_value.length;
        // 拼接开票抬头
        $.each(label_value,function (i,item) {
            createCustInvoiceInfo(newOperateGradientDiv, label_name, item, required, disabled, ele_id, label_type, invoiceSize,i+1, entity_id)
        })
		// 计算开票金额总计
		calcCustInfoiceInfoSum(ele_id);
    }

}

// 拼接一个客户开票抬头
// 数据：[{"custInvoiceInfo":"……", "receivables":"0", "thisReceivables":"10000"}]
// 客户开票抬头div.custInvoiceInfo
// ├─开票信息选择框 lay-filter = ele_id + label_name
// ├─已收金额 name = receivables
// ├─开票金额 name = receivables  增加减少按钮（最后一个抬头才显示）div.operate
// └─开票合计（最后一个抬头才有） name = InvoiceInfo
function createCustInvoiceInfo(parent, label_name, label_item, required, disabled, ele_id, label_type, invoiceSize, gradient, entityId){
    var htmlStr = '';
    // 是否最后一个开票抬头，是才显示加号和开票合计
    var isLastInvoice = invoiceSize === gradient;
    htmlStr += "<div class='layui-form-item custInvoiceInfo gradient-line' style='margin-bottom: 5px;' data-label-name='"+label_name+"' data-gradient-index = " + gradient + ">";
    htmlStr += "<div class='layui-form-item'>"; // 客户开票信息
    htmlStr += "<label class='layui-form-label'>";
    if(required){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "客户开票信息：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    var data = {
        type: 1,
        supplierId: entityId
    };
    htmlStr += "<select data-type='0' name='custInvoiceInfo' value-type='" + label_type + "' input-required = '"+required+"' "+disabled+" lay-filter='" + (ele_id + label_name) +"'>";
    htmlStr += "<option value=''>请选择</option>";
    var select_title = '';
    $.ajaxSettings.async = false;
    $.ajax({
        type: "POST",
        async: false,
        url: "/operate/getInvoice.action",
        dataType: 'json',
        data: data,
        success: function(data) {
            if (data.code == 200) {
                $.each(data.data, function (index, item) {
                    htmlStr += "<option value='" + item.value + "'"+(label_item.custInvoiceInfo == item.value ? "selected" : "")+" title='" + item.title + "'>" + item.text + "</option>";
                    if(label_item.custInvoiceInfo == item.value){
                        select_title = item.title;
                    }
                });
            }
        }
    });
    htmlStr += "</select>";
    $.ajaxSettings.async = true;
    htmlStr += "</div>"; // block-div
    htmlStr += "</div>"; // 客户开票信息

    //已收金额
    htmlStr += "<div class='layui-form-item'>"; // 已收金额
    htmlStr += "<label class='layui-form-label'>";
    if(required){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "已收金额：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    var receivables = (label_item.receivables ? label_item.receivables : '');
    htmlStr += "<input type='text' name='receivables' value='"+receivables+"' input-required = '"+required+"' "+disabled+" class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "</div>";
    htmlStr += "</div>"; // 已收金额

    //开票金额
    htmlStr += "<div class='layui-form-item'>"; // 开票金额
    htmlStr += "<label class='layui-form-label'>";
    if(required){
        htmlStr +=  "<span style='color: red;'>*</span>";
    }
    htmlStr += "开票金额：</label>";
    htmlStr += "<div class='layui-input-block'>"; // block
    var thisReceivables = (label_item.thisReceivables ? label_item.thisReceivables : '');
    htmlStr += "<input type='text' name='InvoiceInfo' value='"+thisReceivables+"' input-required = '"+required+"' "+disabled+" class='layui-input isnum isdecimal gradient-detail' placeholder='请填写'/><span class='gradient-unit'>元</span>";
    htmlStr += "<div class='layui-inline operate' style='margin-left: 10px;'>" + add_invoice_btn(isLastInvoice, disabled, invoiceSize, ele_id, label_name, entityId) + "</div>";
    htmlStr += "</div>"; // block
    htmlStr += "</div>"; // 开票金额

	// 最后一个开票抬头才显示合计h
    if(isLastInvoice){
    	// 移除上个开票抬头的合计
        $(parent).find('.cust_invoice_info_sum_tip' + ele_id).remove();
        // 把合计放到本次新加的开票抬头
        htmlStr += '<i class="cust_invoice_info_sum_tip' + ele_id + '" style="font-size: 18px;color: red;">开票合计：<span class="cust_invoice_info_sum_pay'+ele_id+'" style="font-size: 18px;color: red;"></span>&nbsp;元</i>'
    }
    htmlStr += "</div>";
    // 新的开票抬头 放在 最后一个开票抬头 的下一个位置
	if (gradient === 1) {
		parent.after(htmlStr);
	} else {
		parent.parent().find('div[data-gradient-index=' + (gradient - 1) + ']').after(htmlStr);
	}
    // 给 新开票抬头 的 开票金额输入框 绑定点击事件
    var thisEle = parent.parent().find('div[data-gradient-index=' + gradient + ']')
    invoicePriceBindChange(thisEle, ele_id)
    thisEle.find("div:first .layui-input-block").attr('title', select_title);
    form.render();
}

// 生成客户抬头的添加删除按钮
function add_invoice_btn(addBtn, disabled, invoiceSize, ele_id, label_name, entityId) {
    if (disabled == " disabled ") {
        return "";
    }
    var remove_dom = '';
    if(addBtn){
    	// 添加按钮
    	remove_dom += "<span class='opts_btn' id='add_info' onclick='add_invoice_click(this, \"" + ele_id + "\", \""+label_name + "\", \"" + entityId + "\")'>" +
                "    <i class='layui-icon layui-icon-add-circle'></i>" +
                "</span>";
    	// 一次有多个抬头时，显示删除按钮
        if (invoiceSize > 1) {
            remove_dom += "<span class='opts_btn' id='reduce_info' style='margin-left: 15px;' onclick='reduce_invoice_click(this, \"" + ele_id + "\", \""+label_name + "\", \"" + entityId + "\")'>" +
                "<i class='layui-icon layui-icon-close-fill'></i>" +
                "</span>";
        }
    }
    return remove_dom;
}

// 减开票抬头
function reduce_invoice_click(ele, ele_id, label_name, entityId){
    // 当前按钮所在梯度的元素
    var this_gradient_ele = $(ele).parents(".custInvoiceInfo");
    var prev_gradient_ele = this_gradient_ele.prev();
    // 当前开票抬头序号
    var gradient_index = this_gradient_ele.attr("data-gradient-index");
    gradient_index = parseInt(gradient_index) - 1;
    this_gradient_ele.find('.operate').empty();

    var disabled = (this_gradient_ele.find("input[name='receivables']").attr('disabled')?receivables.attr('disabled'):'');

    this_gradient_ele.remove();
    var htmlStr = add_invoice_btn(true, disabled, gradient_index, ele_id, label_name, entityId);
    prev_gradient_ele.find('div.operate').append(htmlStr);
    prev_gradient_ele.append('<i class="cust_invoice_info_sum_tip'+ele_id+'" style="font-size: 18px;color: red;">开票合计：<span class="cust_invoice_info_sum_pay'+ele_id+'" style="font-size: 18px;color: red;">'+0.00+'</span>&nbsp;元</i>');
    calcCustInfoiceInfoSum(ele_id);
}

// 加开票抬头
function add_invoice_click(ele, ele_id, label_name, entityId) {
    // 当前按钮所在梯度的元素
    var this_gradient_ele = $(ele).parents(".custInvoiceInfo");
    //客户开票信息
    var custInvoiceInfo = this_gradient_ele.find("select[name='custInvoiceInfo']");
    //已收金额
    var receivables = this_gradient_ele.find("input[name='receivables']");
    //开票金额
    var invoiceInfo = this_gradient_ele.find("input[name='InvoiceInfo']");

    if(isBlank(custInvoiceInfo.val())) {
        layer.tips('请选择客户开票信息', custInvoiceInfo.parent());
        return false;
    } else if(isBlank(receivables.val()) || parseInt(receivables.val()) < 0) {
        layer.tips('请输入已收金额', receivables,{tips:4});
        return;
    } else if(isBlank(invoiceInfo.val()) || parseInt(invoiceInfo.val()) < 0) {
        layer.tips('请输入开票金额', invoiceInfo,{tips:4});
        return;
    }
    // 移除原来的加号
    this_gradient_ele.find('.operate').empty();
    var gradient_index = this_gradient_ele.attr("data-gradient-index");
    gradient_index = parseInt(gradient_index) + 1;
    var required = (receivables.attr('input-required')?receivables.attr('input-required'):'');
    var disabled = (receivables.attr('disabled')?receivables.attr('disabled'):'');
    var label_type = (custInvoiceInfo.attr('value-type')?custInvoiceInfo.attr('value-type'):'');
    createCustInvoiceInfo(this_gradient_ele, label_name,{}, required,disabled, ele_id, label_type, gradient_index, gradient_index, entityId);
}

// 客户开票抬头 的 开票金额 输入框 change事件
function invoicePriceBindChange(ele, ele_id){
	// 重新计算开票合计
    calcCustInfoiceInfoSum(ele_id);
    setTimeout(function () {
        $(ele).find('input[name=InvoiceInfo]').unbind().on('change',function () {
            calcCustInfoiceInfoSum(ele_id);
        });
    },10);
}

// 重新计算开票合计
function calcCustInfoiceInfoSum(ele_id){
	var flowMsgEle = $('#' + ele_id);
    var sum = 0.00;
	var billInfoSum = 0.00;
	var needCheck = false;
	// 选择了账单，需要校验开票金额不能大于账单金额，无账单的开票，不需要校验
	if ($(flowMsgEle).find('span.bill_info_sum_pay' + ele_id).length > 0) {
		var billInfoSumStr = $(flowMsgEle).find('span.bill_info_sum_pay' + ele_id).text();
		// 初始化的时候 账单金额span 内容是空的，随后才请求后台并计算账单合计，因此这里做个空判断
		if (isNotBlank(billInfoSumStr)) {
			billInfoSum = parseFloat(billInfoSumStr);
			needCheck = true;
		}
	}
    // 遍历 每个客户开票抬头 中的 开票金额 输入框
    $.each($(flowMsgEle).find('input[name=InvoiceInfo]'), function (i,item) {
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
	$(flowMsgEle).find('span.cust_invoice_info_sum_pay'+ele_id).text(sum.toFixed(2));
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
    var select_title = '';
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
                    if(label_value == item.value){
                        select_title = item.title;
                    }
                });
            }
        }
    });
    select_dom = select_dom + "</select>";
    parent.html(select_dom);
    var selctDom = $(parent).find('select[name='+lable_name+']')
    layui.use('form', function() {
        var form = layui.form;
        selctDom.val(label_value);
        form.render('select');
    });
    if (type === 0) {
        $(parent).parents('.layui-form-item').find('div.invoice').remove();
        // $(parent).parents('.layui-form-item').append(write_html_invoice(label_value, 'selfInvoice'));
        parent.attr('title',select_title);
    } else if (type === 1) {
        $(parent).parents('.layui-form-item').find('div.invoice').remove();
        // $(parent).parents('.layui-form-item').append(write_html_invoice(label_value, 'otherInvoice'));
        parent.attr('title',select_title);
    } else if (type === 2) {
        $(parent).parents('.layui-form-item').find('div.bank-account').remove();
        $(parent).parents('.layui-form-item').append(write_html_bank_account(label_value, 'selfBank'));
    } else if (type === 3) {
        $(parent).parents('.layui-form-item').find('div.bank-account').remove();
        $(parent).parents('.layui-form-item').append(write_html_bank_account(label_value, 'otherBank'));
    }
}

function typeMutiInvoiceInfo(labelValue, defaultValue) {
    if (isBlank(labelValue)) {
        return "无";
    }
    if (typeof labelValue == 'object') {
        labelValue = JSON.stringify(labelValue);
    }
    if (labelValue.startsWith('{')) {
        labelValue = JSON.parse(labelValue);
        return typeInvoiceInfoToString(labelValue.custInvoiceInfo, defaultValue, 1);
    } else if (labelValue.startsWith('[')) {
        labelValue = JSON.parse(labelValue);
        var result = '';
        $.each(labelValue, function(i, item) {
            result += typeInvoiceInfoToString(item.custInvoiceInfo, defaultValue, 2);
            if ('无' == result) {
                return false;
            }
            result += "<br/>已收金额：" + (item.receivables ? thousand(item.receivables) : "");
            result += "<br/>开票金额：" + (item.thisReceivables ? thousand(item.thisReceivables) : "");
            result += "<br/>";
        });
        return result;
    }
}

// 设置电商配单员选择框
function take_match_input(parent, lable_name, label_value, default_value, disabled, required) {
    var select_dom = "<select data-type='0' value-type='7' input-required='" + required + "' name='" + lable_name + "' " + disabled + " >";
    select_dom = select_dom + "<option value=''>" + "请选择" + lable_name + "</option>";
    $.ajax({
        type: "POST",
        async: false,
        url: "/dsMatchOrder/getSelectRole.action",
        dataType: 'json',
        data: {},
        success: function (data) {
            if (data.code == 200) {
                $.each(data.data, function (index, item) {
                    select_dom = select_dom + "<option value='" + item.ossUserId + "' title='" + item.realName + "' " + (label_value == item.ossUserId ? "selected" : "") + ">" + item.realName + "</option>";
                });
            }
        }
    });
    select_dom = select_dom + "</select>";
    parent.html(select_dom);
}

/**
 * 开票信息以字符串展示
 *
 * @param labelValue    开票信息标签内容
 * @param defaultValue
 * @param type          类型：0我方，1对方，2对方带金额
 * @returns {string}
 */
function typeInvoiceInfoToString(labelValue, defaultValue, type) {
    if (isBlank(labelValue)) {
        return "无";
    }
    var json = {};
    var array = labelValue.split('####');
    for (var i = 0; i < array.length; i++) {
        json[array[i].split(':')[0]] = array[i].split(':')[1];
    }

    // 我司开票信息缩起来，鼠标放上去时在title里展示全部
    if (type === 0) {
        var spanTitle = "公司名称：" + (json.companyName ? json.companyName : "");
        spanTitle += "\n税务号：" + (json.taxNumber ? json.taxNumber : "");
        spanTitle += "\n公司地址：" + (json.companyAddress ? json.companyAddress : "");
        spanTitle += "\n联系电话：" + (json.phone ? json.phone : "");
        spanTitle += "\n开户银行：" + (json.accountBank ? json.accountBank : "");
        spanTitle += "\n银行账号：" + (json.bankAccount ? json.bankAccount : "");

        html = "<br/><span title='" + spanTitle + "'>" + (json.companyName ? json.companyName : "");
        html += "【开户银行：" + (json.accountBank ? json.accountBank : "");
        html += "：" + (json.bankAccount ? json.bankAccount : "") + "】</span><br/>";
    } else {
        html = "<br/>公司名称：" + (json.companyName ? json.companyName : "");
        html += "<br/>税务号：" + (json.taxNumber ? json.taxNumber : "");
        html += "<br/>公司地址：" + (json.companyAddress ? json.companyAddress : "");
        html += "<br/>联系电话：" + (json.phone ? json.phone : "");
        html += "<br/>开户银行：" + (json.accountBank ? json.accountBank : "");
        html += "<br/>银行账号：" + (json.bankAccount ? json.bankAccount : "");
    }
    // type==1是对方开票，到此标签结束，可加换行；type==2是对方开票带金额，到此标签未结束，不加换行
    if (type === 1) {
        html += "<br/>";
    }
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

//电商银行信息
function typeDsBankInfoToString(labelValue, defaultValue) {
	if (isBlank(labelValue)) {
		return "无";
	}
	var json = {};
	var html = "";
	var array = JSON.parse(labelValue);
	for (var i = 0; i < array.length; i++) {
		json = array[i]
		html += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;名称：" + (json.accountName ? json.accountName : "");
		html += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;开户银行：" + (json.accountBank ? json.accountBank : "");
		html += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;银行账号：" + (json.bankAccount ? json.bankAccount : "");
	}
	return html;
}

/**
 * 时间账单金额标签的回显
 * @param labelValue 标签值
 */
function timeAccountBillToString(labelValue) {
    var accountBillObj = null;
    try{
        accountBillObj = JSON.parse(labelValue);
    }catch (e) {
        //console.log("JSON转换错误，所有都在掌握之中")
    }
    if (isBlank(accountBillObj)){
        // 兼容原来数据
        var accountBillInfo = labelValue.split(",");
        if (accountBillInfo.length >= 3){
            return "(全月)成功数："
                + thousand(accountBillInfo[0]) + "条 X "
                + thousand(accountBillInfo[1]) + "元 = "
                + thousand(accountBillInfo[2]) + "元";
        }
        return "";
    }else{
        var accountMoneyDom = "<br>";
        for (var index = 0; index < accountBillObj.length; index++) {
            var item = accountBillObj[index];
            accountMoneyDom +=("[" + item.start + " ~ " + item.end + "]："
                + item.success + "条 X " + item.price + "元 = " + item.total + "元");
            if (index !== (accountBillObj.length - 1)) {
                accountMoneyDom += "<br>";
            }
        }
        return accountMoneyDom;
    }
}

function platformAccountInfoToString(labelValue) {
    console.log(labelValue);
    var accountInfo = null;
    try{
        accountInfo = JSON.parse(labelValue);
    }catch (e) {}
    if (isNotBlank(accountInfo)){
        var accountInfoDom = "<br>";
        for (var index = 0; index < accountInfo.length; index++) {
            var item = accountInfo[index];
            accountInfoDom +=("账号：" + item.account + " 密码： " + item.pwd + "描述："+ item.note);
            if (index !== (accountInfo.length - 1)) {
                accountInfoDom += "<br>";
            }
        }
        return accountInfoDom;
    }
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
function typeMatchPersonToString(labelValue, defaultValue) {
    var realName = '';
    if (isBlank(labelValue)) {
        realName = "未选择"
    } else {
       var ossUserId = labelValue;
       $.ajax({
           type: "POST",
           async: false,
           url: "/dsMatchOrder/getSelectRole.action",
           dataType: 'json',
           data: {},
           success: function(data) {
               if (data.code == 200) {
                   $.each(data.data, function (index, item) {
                       if(item.ossUserId == ossUserId) {
                            realName = item.realName
                       }
                   });
               }
           }
       });
    }
    return realName;
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
	return isBlank(labelValue) ? thousand(defaultValue) : labelValue;
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
    return isBlank(labelValue) ? thousand(defaultValue) : thousand(labelValue);
}

// 历史单价
function typeHistoryPriceToString(labelValue, defaultValue) {
    return isBlank(labelValue) ? thousand(defaultValue) : thousand(labelValue);
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
    var select_dom = "<select data-type='0' value-type='7' layer-filter='" + lable_name + "' input-required='" + required + "' name='" + lable_name + "' " + disabled + " >";
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
    
    layui.form.render();
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
function take_file_input(parent, lable_name, label_value, default_value, disabled, required, ele_id, apply_time) {
    // 文件类型
    var btn = "<button data-type='0' value-type='8' type='button' " + disabled +
        "input-required='" + required + "' class='layui-btn layui-btn-xs' name='" + lable_name + "'>选择文件</button>" + "<span>(总大小不超过100M)</span>" +
        "<div class='layui-upload-list'>" +
        "    <table class='layui-table'>" +
        "      <thead>" +
        "        <tr><th style='text-align: center'>批次</th>" +
        "        <th style='width: 165px;'>文件名</th>" +
        "        <th>状态/日期</th>" +
        "        <th>操作</th>" +
        "      </tr></thead>" +
        "      <tbody data-file-name = '" + lable_name + "'></tbody>" +
        "    </table>" +
        "</div> ";
    $(parent).html(btn);
    // 回显原来已经有的文件
    show_uploaded_file(lable_name, label_value, disabled, ele_id, apply_time);
    // 文件标签的上传功能
    init_file_load(lable_name, label_value, ele_id);
}

// 文件类型
function typeFileToString(labelValue, defaultValue) {
    var fileArray = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
    if (fileArray.length === 0) {
        return "无";
    }
    var html = "</br>";
    $.each(fileArray, function (i, file) {
        var fileJson = JSON.stringify(file);
        var fileTitle = "";
        if (isNotBlank(file.batchNum)) {
            fileTitle += "[" + file.batchNum + "] ";
        }
        if (isNotBlank(file.time)) {
            fileTitle += "[" + file.time + "] ";
        }
        html += fileTitle + "<a style='text-decoration: underline' href='javascript:void(0);' onclick='down_load(" + fileJson + ")'>" + file.fileName + "</a>";
        html += "<a style='text-decoration: underline; float: right' href='javascript:void(0);' onclick='view_File(" + fileJson + ")'>预览</a></br>";
    });
    return html;
}

// 处理文本框类型
function take_textarea_input(parent, lable_name, label_value, required, disabled) {
    // 文本 类型
    label_value = label_value.replace(/\n/g, "<br/>");
    $(parent).html("<textarea data-type='0' value-type='9' " + disabled +
        "input-required='" + required + "' name='" + lable_name +
        "' placeholder='请输入内容' class='layui-textarea' maxlength='1500'>" + label_value + "</textarea>");
}


// 文本框类型
function typeTextareaToString(labelValue, defaultValue) {
    var html = "<pre>";
    html += isBlank(labelValue) ? defaultValue : labelValue + "</pre>";
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
    if (isBlank(labelValue)) {
        return html;
    }
    labelValue = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
    if (labelValue === {} || labelValue === []) {
        return "无";
    }
    if (labelValue[0]['priceType'] === 'gradient') {
        // 处理梯度价格
        var gradient_price = labelValue;
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
            var min_send_count = isBlank(gradient_info.minsend) ? "空" : thousand(gradient_info.minsend);
            var max_send_count = isBlank(gradient_info.maxsend) ? "空" : thousand(gradient_info.maxsend);
            var price = isBlank(gradient_info.price) ? "未输入" : thousand(gradient_info.price);
            var millions_ratio = isBlank(gradient_info.complaintrate) ? "空" : thousand(gradient_info.complaintrate);
            var province_ratio = isBlank(gradient_info.provinceproportion) ? "空" : thousand(gradient_info.provinceproportion);
            html += "<br/>" + min_send_count + "条 <= 发送量 < " + max_send_count + "条，价格：" + price;
            html += "，百万投比：" + millions_ratio + "，省占比：" + province_ratio + "%";
            html += isNotBlank(is_default) && (is_default === 1 || is_default === "1") ? "(默认)" : "";
        });

    } else if (labelValue[0]['priceType'] === 'uniform') {
        // 处理统一价格
        var price = "";
        var provinceprice = "";

        var uniform_price = labelValue[0];
        if (isNotBlank(uniform_price)) {
            price = uniform_price.price;
            provinceprice = uniform_price.provinceprice;
        }
        html += "价格：" + thousand(price) + "，省网价格：" + thousand(provinceprice);
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

// 处理订单编号类型
function take_order_no_input(parent, lable_name, label_value, required, disabled) {
    // 文本 类型
    $(parent).html("<input data-type='0' value-type='26' disabled name='" + lable_name +
        "' placeholder='流程自动生成，不需要填写' class='layui-input' value='" + label_value + "'/>");
}

// 处理订单编号类型
function take_purchase_no_input(parent, lable_name, label_value, required, disabled) {
    // 文本 类型
    $(parent).html("<input data-type='0' value-type='28' disabled name='" + lable_name +
        "' placeholder='流程自动生成，不需要填写' class='layui-input' value='" + label_value + "'/>");
}

var flowIncomeInput, flowBillsInput;
function flowContactsInfo(table) {
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
				thiscost: accSub(bills.receivables, bills.actualReceivables)
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
					customerId : customerId,
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
										sum = accAdd(sum, income.remainRelatedCost);
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
					align : 'right',
					templet: function (rowdata) {
						return thousand(rowdata.receivables);
					}
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
										sum = accAdd(sum, accSub(bills.receivables, bills.actualReceivables));
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

function triggerClick(tr) {
	var index = $(tr).attr('data-index');
	var tableBox = $(tr).closest('.layui-table-box');
	var tableFixed = tableBox.find(".layui-table-fixed.layui-table-fixed-l")
	var tableBody = tableBox.find(".layui-table-body.layui-table-main");
	var tableDiv = tableFixed.length ? tableFixed : tableBody;
	var checkCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox i");
	var radioCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-radio div.layui-form-radio i");
	if (checkCell.length) {
		checkCell.click();
	}
	if (radioCell.length) {
		radioCell.click();
	}
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
function init_file_load(input_name, label_value, ele_id) {
    // 解析数据
    var file_array = isBlank(label_value) ? [] : (typeof label_value == "object") ? label_value : JSON.parse(label_value);
    var file_batch = 0;
    for (var file_index = 0; file_index < file_array.length; file_index++) {
        var file_temp = file_array[file_index];
        var batch_num = file_temp.batchNum;
        if (isNotBlank(batch_num) && $.isNumeric(batch_num) && parseInt(batch_num) > file_batch) {
            file_batch = parseInt(batch_num);
        }
    }
    // 这次的批次
    var now_batch_num = file_batch > 0 ? (file_batch + 1) : 1;
    var demoListView = $("#" + ele_id + " tbody[data-file-name='" + input_name + "']");
    var uploadListIns = upload.render({
        elem: "#" + ele_id + " button[name='" + input_name + "']",
        url: '/operate/upLoadFile',
        field: 'files',
        accept: 'file',
        multiple: false,
        auto: false,
        choose: function (obj) {
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                var tr = $(['<tr id="upload-' + index + '">',
                    '<td style="text-align: center">' + now_batch_num + '</td>',
                    '<td>' + file.name + '</td>',
                    '<td>等待上传</td>', '<td>',
                    '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>',
                    '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>',
                    '</td>',
                    '</tr>'].join(''));
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
                tds.eq(2).html('<span style="color: #5FB878;">'+ res.data[0].time+'</span>');
                //去掉重传按钮
                tds.eq(3).find("button[class*='demo-reload']").remove();
                upload_file[index] = res.data;
                // 处理上传的文件（记录已经上传的文件）
                take_upload_file_result(res.data, input_name, ele_id, now_batch_num);
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
    var default_checked = !isBlank(is_default) && (is_default === 1 || is_default === "1") ? " checked ='checked' " : "";
    var before_dom = "<div class='layui-form-item gradient-line gradient' data-gradient-index = " + gradient + ">";
    var dom =
        "   <div class='layui-form-label'>" +
        "       <input type='radio' class='defaultGradient' value='0' title='默认' " + default_checked + disabled + " />" +
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
    var html = "金额" + thousand(values[0]) + "元" + "&nbsp;X&nbsp;酬金比例&nbsp;" + thousand(values[1]) + "%" + "&nbsp;+&nbsp;奖励&nbsp;" + thousand(values[2]) + "元";
    html += "&nbsp;－&nbsp;扣款&nbsp;" + thousand(values[3]) + "元" + "&nbsp;=&nbsp;" + thousand(values[4]) + "元";
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

function typeBillInvoiceToString(labelValue, defaultValue, flowEntId, flowClass, flowId){
    return typeBillToString(labelValue, defaultValue, flowEntId, flowClass, flowId);
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
                html += show_name.payables_name + thousand(payables.toFixed(2)) + "元，";
                html += show_name.actualpayables_name + thousand(actualpayables.toFixed(2)) + "元，";
                html += show_name.left_should_pay_name + thousand(left_should_pay.toFixed(2)) + "元，";
                html += show_name.thisPayment_name + thousand(thisPayment.toFixed(2)) + "元";
            });
            html += "<br/>" + total_name + format_num(this_time_pay, 2) + "元<br/>";
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
function take_upload_file_result(filesPaths, input_name, ele_id, now_batch_num) {
    if (!isBlank(filesPaths)) {
        // 获取文件内容
        var files = file_result[input_name + ele_id];
        if (isBlank(files)) {
            files = new Array();
        }
        // 新加文件
        for (var file_index = 0; file_index < filesPaths.length; file_index++) {
            var file = filesPaths[file_index];
            file.batchNum = now_batch_num;
            files.push(file);
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
function show_uploaded_file(label_name, loaded_file_json_array, disabled, ele_id, apply_time) {
    if (!isBlank(loaded_file_json_array)) {
        var ele = $("#" + ele_id);
        // 解析数据
        var file_array = (typeof loaded_file_json_array == "object") ? loaded_file_json_array : JSON.parse(loaded_file_json_array);
        // 初始化对应的数据
        file_result[label_name + ele_id] = file_array;
        for (var file_index = 0; file_index < file_array.length; file_index++) {
            var file_temp = file_array[file_index];
            if (isNotBlank(file_temp.time)) {
                apply_time = file_temp.time;
            }
            var file_batch = file_temp.batchNum;
            if (isBlank(file_batch)){
                file_batch = "-";
            }
            var json_file_temp = JSON.stringify(file_array[file_index]);
            var tr = $(["<tr id='upload-my-" + hex_md5(file_temp.filePath) + "'>",
                "<td style='text-align: center'>" + file_batch + "</td>",
                "<td>" + file_temp.fileName + "</td>",
                "<td><span style='color: #5FB878'>" + apply_time + "</span></td>",
                "<td>",
                "<span type='button' class='layui-btn layui-btn-xs my-down-load' onclick='view_File(" + json_file_temp + ")'>预览</span>",
                "<span type='button' class='layui-btn layui-btn-xs layui-btn-danger my-down-load' onclick='down_load(" + json_file_temp + ")'>下载</span>",
                "<span type='button' class='layui-btn layui-btn-xs layui-btn-danger my-delete' " + disabled + " onclick='delete_file(\"" + label_name + "\"," + json_file_temp + ",\"" + ele_id + "\")'>删除</span>",
                "</td>",
                "</tr>"
            ].join(""));
            ele.find("tbody[data-file-name = '" + label_name + "']").append(tr);
        }
    }
}

//预览
function view_File(file_info) {
    console.log("预览文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
     window.open("/operate/viewFile?" + file_params);
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
            supplier_price = isBlank(bill_money[1]) ? 0 : parseFloat(bill_money[1]).toFixed(6);
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
	var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
	tableTrHtml.append('<td class="flow-view-table-th" width="65%">账单名称</td>');
	tableTrHtml.append('<td class="flow-view-table-th" width="35%">账单金额</td>');
	tableHtml.append(tableTrHtml);
	var billsSum = 0;
	$(typeof labelValue == 'string' ? JSON.parse(labelValue) : labelValue).each(function (i, item) {
		var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.title + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.thiscost) + '</td>');
		tableHtml.append(tableTrHtml);
		billsSum += item.thiscost;
	});
	tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">账单合计</td>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(billsSum.toFixed(2)) + '</td>');
	tableHtml.append(tableTrHtml);
    return tableHtml.prop("outerHTML");
}

//处理销账收款信息
function typeStringToIncomeInfoString(labelValue, productId) {
	var tableHtml = $('<table class="flow-view-table" border="1" cellpadding="0" cellspacing="0"></table>');
	var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
	tableTrHtml.append('<td class="flow-view-table-th" width="16%">到款时间</td>');
	tableTrHtml.append('<td class="flow-view-table-th" width="39%">到款信息</td>');
	tableTrHtml.append('<td class="flow-view-table-th" width="15%">到款</td>');
	tableTrHtml.append('<td class="flow-view-table-th" width="15%">余额</td>');
	tableTrHtml.append('<td class="flow-view-table-th" width="15%">销账</td>');
	tableHtml.append(tableTrHtml);
	var incomeSum = 0;
	var arr = typeof labelValue == 'string' ? JSON.parse(labelValue) : labelValue;
	
	var ids = [];
	$(arr).each(function (i, item) {
		if (item.remain === undefined) {
			ids.push(item.fsexpenseincomeid);
		}
	});
	
	var remianJson = {};
	var fsExpenseIncomeInfos = null;
	if (productId || ids.length > 0) {
		$.ajax({
			type: "POST",
			async: false,
			url: "/fsExpenseIncome/readFsExpenseIncomesByProduct.action?temp=" + Math.random(),
			dataType: 'json',
			data: {
				self: 'T',
				productId: productId,
				ids: ids.join(',')
			},
			success: function (res) {
				if (res && res.data && res.data.length > 0) {
					fsExpenseIncomeInfos = res.data;
				}
			}
		});
	}
	if (fsExpenseIncomeInfos) {
		$(fsExpenseIncomeInfos).each(function (i, item) {
			remianJson[item.id] = item.remainRelatedCost;
		});
	}
	
	var existIds = [];
	$(arr).each(function (i, item) {
		existIds.push(item.fsexpenseincomeid);
		var remain = 0;
		if (!item.remain) {
			if (remianJson[item.fsexpenseincomeid] !== undefined) {
				remain = remianJson[item.fsexpenseincomeid] + item.thiscost;
			} else {
				remain = item.cost;
			}
		} else {
			remain = item.remain;
		}
		var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.operatetime + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td">' + item.banckcustomername + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.cost) + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(remain.toFixed(2)) + '</td>');
		tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.thiscost) + '</td>');
		tableHtml.append(tableTrHtml);
		incomeSum += item.thiscost;
	});
	
	if (fsExpenseIncomeInfos) {
		existIds = existIds.join(',');
		$(fsExpenseIncomeInfos).each(function (i, item) {
			if (existIds.indexOf(item.id) < 0) {
				tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
				tableTrHtml.append('<td class="flow-view-table-td">' + item.operateTime + '</td>');
				tableTrHtml.append('<td class="flow-view-table-td">' + item.depict + '</td>');
				tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.cost) + '</td>');
				tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.remainRelatedCost) + '</td>');
				tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(0) + '</td>');
				tableHtml.append(tableTrHtml);
			}
		});
	}
	
	tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">销账合计</td>');
	tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money" colspan="4">' + thousand(incomeSum.toFixed(2)) + '</td>');
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
    var html = thousand(supplier_success) + "条" + "&nbsp;X&nbsp;" + thousand(supplier_price) + "元" + "&nbsp;=&nbsp;" + thousand(total_money) + "元";
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

function getSelectBillInvoiceInfo(ele_id) {
    var accountData = [];
    var tablcChecked = table.checkStatus('selectBillInfo'+ele_id);
    var data = tablcChecked.data;
    $.each(data,function (i,item) {
        accountData.push(sortObjectKey({
            id: item.id,
            title: item.title,
            receivables: item.receivables,                                                  				// 应开
            actualInvoiceAmount: item.actualInvoiceAmount,                                  				// 已开
			invoiceableAmount: accSub(accSub(item.receivables, item.actualInvoiceAmount), item.usedAmount), // 可开
			thisReceivables: $('#thisBillPayment_'+item.id+ele_id) .val()                                   // 本次开票
        }))
    });

    return accountData;
}

function second_confirm(opts_type, ele_id, nodeIndex, buttonContent) {
	layer.confirm("确认“" + buttonContent + "”吗？", {
        title: "确认操作",
        icon: 3,
        btn: ["确认", "取消"]
    }, function () {
    	audit(opts_type, ele_id, nodeIndex);
    }, function () {
    	layer.msg("取消");
    });
}

// 撤销
function revoke(flowEntId, ele) {
	var revokeReson = $(ele).parents('.flow-revoke').find('.revoke-reason').val();
	if (!revokeReson) {
		$(ele).parents('.flow-revoke').find('.revoke-reason').focus();
		return layer.msg('请输入撤销原因');
	}
	layer.confirm("确认“撤销操作”吗？", {
        title: "确认操作",
        icon: 3,
        btn: ["确认", "取消"]
    }, function () {
    	$(ele).unbind();
    	$.ajax({
            type: "POST",
            async: true,
            url: "/operate/revokeProcess.action",
            dataType: 'json',
            data: {
            	revokeReson: $(ele).parents('.flow-revoke').find('.revoke-reason').val(),
            	flowEntId: flowEntId,
                platform: 0
            },
            success: function (resp) {
                if (resp.code == 200) {
                	// 通过、驳回、取消
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
                    layer.msg('撤销成功');
                    flowType = 999;
                    $("#flowMsg_" + flowEntId).remove();
                } else {
                    layer.msg(data.msg);
                    $(ele).bind('click', function () {
                    	revoke(flowEntId, ele);
                    });
                }
            }
        });
    }, function () {
    	layer.msg("取消");
    });
}

// 审核
function audit(opts_type, ele_id, nodeIndex) {
	var buttonContent = '';
	var args = arguments;
	$(args).each(function (i, item) {
		if (item && item.innerText) {
			buttonContent = item.innerText;
			args[i] = '';
		}
	});
	if (buttonContent
			&& (buttonContent == '通过'
				|| buttonContent == '确认销账'
				|| buttonContent == '重新申请')) {
		return second_confirm(args[0], args[1], args[2], buttonContent);
	}
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
/*    var base_data_eles = flowMsgDiv.find("input[base-data-map ='0']");
    var base_date = {};
    if (!isBlank(base_data_eles) && base_data_eles.size() > 0) {
        for (var base_data_index = 0; base_data_index < base_data_eles.size(); base_data_index++) {
            var input_ele = base_data_eles[base_data_index];
            var value = $(input_ele).val();
            var name = $(input_ele).attr("name");
            base_date[name] = value;
        }
    }*/

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

    //发票流程 账单信息
    var selectBill = getSelectBillInvoiceInfo(ele_id);
    var billLabelName = $('#selectBillInfo'+ele_id).attr('data-label-name');
    if (billLabelName) {
    	label_date_map[billLabelName] = selectBill;
    }

    // 布尔类型单选框
    var data_radio_input_eles = flowMsgDiv.find("input[data-type='3']:checked");
    if (!isBlank(data_radio_input_eles) && data_radio_input_eles.size() > 0) {
        for (var data_radio_input_index = 0; data_radio_input_index < data_radio_input_eles.size(); data_radio_input_index++) {
            var data_radio_input_value = $(data_radio_input_eles[data_radio_input_index]).val();
            var data_radio_input_name = $(data_radio_input_eles[data_radio_input_index]).attr("name");
            label_date_map[data_radio_input_name] = data_radio_input_value;
        }
    }

    // radio单选框
    var radio_input_eles = flowMsgDiv.find("input[data-type='36']:checked");
    if (radio_input_eles.length > 0) {
        for (var index = 0; index < radio_input_eles.length; index++) {
            var labelValue = $(radio_input_eles[index]).val();
            var labelName = $(radio_input_eles[index]).attr("data-label-name");
            label_date_map[labelName] = labelValue;
        }
    }

    // 请假类型radio单选框
    var leave_type_eles = flowMsgDiv.find("input[data-type='38']:checked");
    if (leave_type_eles.length > 0) {
        for (var index = 0; index < leave_type_eles.length; index++) {
            var labelValue = $(leave_type_eles[index]).val();
            var labelName = $(leave_type_eles[index]).attr("data-label-name");
            label_date_map[labelName] = labelValue;
        }
    }

    // 时间段类型
    var timeSlotEle = flowMsgDiv.find("input[data-type=35]");
    if (timeSlotEle.length > 0) {
        for (var index = 0; index < timeSlotEle.length; index++) {
            var labelEle = $(timeSlotEle[index]);
            var datetime = labelEle.val();
            var labelName = labelEle.attr('name');
            var daysEle = labelEle.parents('div.layui-form-item').find('span.time-duration');
            var days = '';
            if (daysEle.length > 0) {
                days = daysEle.text();
            }
            label_date_map[labelName] = {'datetime': datetime, 'days': days};
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
            var defaultGradient = $(gradient_item).find("input.defaultGradient").prop("checked");
            var gradient_min = $(gradient_item).find("input[name='gradient_min']").val();
            var gradient_max = $(gradient_item).find("input[name='gradient_max']").val();
            var price = $(gradient_item).find("input[name='price']").val();
            var million_ratio = $(gradient_item).find("input[name='million_ratio']").val();
            var province_ratio = $(gradient_item).find("input[name='province_ratio']").val();
            gradient_value.isdefault = 0;
            if (defaultGradient) {
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
    var bill_money_items = flowMsgDiv.find("div.bill-money-item");
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
    
    // 获取银行信息
//    var dsBankInfos = flowMsgDiv.find("#dsBankInfos").val();
    var BankInfoCount = flowMsgDiv.find("#bankInfoCount").val();
    var dsBankInfoData = [];
//    var arrayBankInfos = JSON.parse(dsBankInfos);
	for (var index = 0; index < BankInfoCount; index++) {
		var dsBankInfos = flowMsgDiv.find("#bankInfo" + index);
		var accountName = $(dsBankInfos).find("input[name='accountName']").val();
        var accountBank = $(dsBankInfos).find("input[name='accountBank']").val();
        var bankAccount = $(dsBankInfos).find("input[name='bankAccount']").val();
//		var accountName = dsBankInfo.accountName;
//        var accountBank = dsBankInfo.accountBank;
//        var bankAccount = dsBankInfo.bankAccount;
        var json = {};
        json['accountName'] = accountName;
        json['accountBank'] = accountBank;
        json['bankAccount'] = bankAccount;
        dsBankInfoData.push(sortObjectKey(json));
    };
    if(dsBankInfoData.length > 0){
    	label_date_map['银行信息'] = dsBankInfoData;
    }

    // 客户开票抬头
    var custInvoiceInfos = flowMsgDiv.find('.custInvoiceInfo');
    var custInvoiceData = [];
    var custInvoiceName = '';
    if(isNotBlank(custInvoiceInfos)) {
        $.each(custInvoiceInfos,function (i,item) {
            if(0 == i) {
                custInvoiceName = $(item).attr('data-label-name')
            }
            var custInvoiceInfo = $(item).find('select[name=custInvoiceInfo]').val();
            //开票金额
            var invoicePrice = $(item).find('input[name=InvoiceInfo]').val();
            //已收金额
            var receivablesPrice = $(item).find('input[name=receivables]').val();
            var json = {};
            json['custInvoiceInfo'] = custInvoiceInfo;
            json['thisReceivables'] = invoicePrice;
            json['receivables'] = receivablesPrice;
            custInvoiceData.push(json);
        });
    }
    if(isNotBlank(custInvoiceName)){
        label_date_map['客户开票信息'] = custInvoiceData;
    }

    // 时间账单标签
    var timeBillMoneyItems = $(flowMsgDiv).find("div[class*='time-bill-money-item']");
    if (timeBillMoneyItems.length > 0) {
        for (var billLabelIndex = 0; billLabelIndex < timeBillMoneyItems.length; billLabelIndex++) {
            var timeBillMoneyItem = timeBillMoneyItems[billLabelIndex];
            var labelName = $(timeBillMoneyItem).attr("data-label-name");
            // 进行校验
            var dataBillItems = $(timeBillMoneyItem).find("div[data-bill-index]");
            var resultDatas = [];
            for (var dataBillIndex = 0; dataBillIndex < dataBillItems.length; dataBillIndex++) {
                var item = dataBillItems[dataBillIndex];
                var itemIndex = $(item).attr("data-bill-index");
                var timeStart = $(item).find("input[name='time_start']").val();
                var timeEnd = $(item).find("input[name='time_end']").val();
                var timeSuccess = $(item).find("input[name='time_success']").val();
                var timePrice = $(item).find("input[name='time_price']").val();
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
            if (resultDatas.length > 0) {
                label_date_map[labelName] = JSON.stringify(resultDatas);
            }
        }
    }

    // 平台账户信息
    var platfromAccountInfos = flowMsgDiv.find("div[data-platform-account-index]");
    if (platfromAccountInfos.length > 0) {
        var accountInfos = [];
        for (var accountIndex = 0; accountIndex < platfromAccountInfos.length; accountIndex++) {
            var accountInfoItem = platfromAccountInfos[accountIndex];
            var account = $(accountInfoItem).find("input[name='account']").val();
            var password = $(accountInfoItem).find("input[name='password']").val();
            var note = $(accountInfoItem).find("input[name='note']").val();
            if (isNotBlank(account)) {
                accountInfos.push({
                    "account": account,
                    "pwd": password,
                    "note": note,
                });
            }
        }
        if (accountInfos.length > 0) {
            var accountInfoLabel = $(platfromAccountInfos[0]).prev();
            var labelName = $(accountInfoLabel).attr("data-label-name");
            label_date_map[labelName] = JSON.stringify(accountInfos);
        }
    }

    // 账单对账标签
    var uncheckedBillInfoEle = flowMsgDiv.find("div.unchecked-bills");
    if (uncheckedBillInfoEle.length > 0) {
    	var labelName = uncheckedBillInfoEle.attr('data-label-name');
		var value = getUncheckedBill(flowMsgDiv, flow_id);
		label_date_map[labelName] = value;
	}

    // 充值详情
    var accountRechargeEle = flowMsgDiv.find("div.account-recharge");
    if (accountRechargeEle.length > 0) {
        var labelName;
        var labelValue = [];
        $(accountRechargeEle).each(function (i, item) {
            if (i === 0) {
                var labelEle = $(item).parents('div.layui-form-item').find('label.show-lable');
                labelName = labelEle.attr('data-label-name');
            }
            labelValue.push({
                rechargeAccount: $(item).find('[name="rechargeAccount"]').val(),
                currentAmount: $(item).find('[name="currentAmount"]').val(),
                price: $(item).find('[name="price"]').val(),
                rechargeAmount: $(item).find('[name="rechargeAmount"]').val(),
                pieces: $(item).find('[name="pieces"]').val()
            });
        });
        label_date_map[labelName] = labelValue;
    }

    // 审核意见
    var audit_opinion = flowMsgDiv.find("#audit-opinion").val();
    // 审核数据
    var auit_data = {
        flowEntId: flow_id,
        nodeId: node_id,
        labelValueMap: JSON.stringify(label_date_map),
        baseDataMap: JSON.stringify(flow.baseDataMap),
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
    var flowData = flowMsgDiv.find("xmp[name='flowData']").text();
    var flow = JSON.parse(flowData);
    var mustIds = flow.mustLabelIds;
    // 输入框(必须要求的输入框)
    var required_input = $(flowMsgDiv).find("input[input-required='true']");

    if (!isBlank(required_input)) {
        for (var required_input_index = 0; required_input_index < required_input.size(); required_input_index++) {
            var required_input_temp = required_input[required_input_index];
            var input_value = $(required_input_temp).val();
            var input_name = $(required_input_temp).attr('name');
            if (isBlank(input_value)) {
                $(required_input_temp).focus();
                layer.tips(input_name + "不能为空", $(required_input_temp));
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

    // 校验 开票流程 账单信息
	var selectBillInfos = $(flowMsgDiv).find('#selectBillInfo' + ele_id);
    if (selectBillInfos.length > 0) {
		var selectBills = getSelectBillInvoiceInfo(ele_id);
		if (isNull(selectBills) || selectBills.length === 0) {
			layer.msg('请选择' + selectBillInfos.attr('data-label-name'));
			return false;
		} else {
			var flag = true;
			$.each(selectBills, function (i, item) {
				if (!$.isNumeric(item.thisReceivables)) {
					layer.msg('请输入账单的 本次开票金额');
					flag = false;
				} else if (parseFloat(item.thisReceivables).toFixed(2) === '0.00') {
					layer.msg("账单的 本次开票金额 必须大于0");
					flag = false;
				} else if (parseFloat(item.thisReceivables) > parseFloat(item.invoiceableAmount)) {
					layer.msg("账单的 本次开票金额 不能大于 可开金额");
					flag = false;
				}
				return flag; // 跳出循环
			});
			if (!flag) {
				return flag;
			}
		}
	}

	// 校验 开票流程 客户开票抬头
	var custInvoiceInfos = $(flowMsgDiv).find('.custInvoiceInfo');
	if(custInvoiceInfos.length > 0) {
		for (var index = 0; index < custInvoiceInfos.length; index++) {
			var custInvoiceInfo = custInvoiceInfos[index];
			//开票金额
			var invoiceEle = $(custInvoiceInfo).find('input[name=InvoiceInfo]');
			if (isBlank(invoiceEle.val()) || !$.isNumeric(invoiceEle.val())) {
				layer.tips("开票金额只能为数字", $(invoiceEle));
				return false;
			} else if (parseFloat(invoiceEle.val()).toFixed(2) === '0.00' || parseFloat(invoiceEle.val()) < 0.0) {
				layer.tips("开票金额必须大于0", $(invoiceEle));
				return false;
			}
			//已收金额
			var receivablesEle = $(custInvoiceInfo).find('input[name=receivables]');
			if (isNotBlank(receivablesEle.val())) {
				if (!$.isNumeric(receivablesEle.val())) {
					layer.tips("已收金额只能为数字", $(receivablesEle));
					return false;
				} else if (parseFloat(receivablesEle.val()) > parseFloat(invoiceEle.val())) {
					layer.tips("已收金额不能大于开票金额", $(receivablesEle));
					return false;
				}
			}
		}
		//校验 账单合计 是否等于 开票合计
		var billSumEle = flowMsgDiv.find('span.bill_info_sum_pay'+ele_id);
		if (billSumEle.length > 0) {
			var billSum = flowMsgDiv.find('span.bill_info_sum_pay'+ele_id).text();
			var custInvoiceSum = flowMsgDiv.find('span.cust_invoice_info_sum_pay'+ele_id).text();
			if(parseFloat(billSum).toFixed(2) !== parseFloat(custInvoiceSum).toFixed(2)){
				layer.msg('开票金额合计 必须等于 账单金额合计');
				return false;
			}
		}
	}

    // 校验 账号信息是否为必须
    var platform_account_info_dom = flowMsgDiv.find("div[data-platform-account-index]");
    if (platform_account_info_dom.length > 0) {
        var firstAccountInfo = platform_account_info_dom[0];
        if (mustIds.indexOf($(firstAccountInfo).attr("data-id")) >= 0) {
            for (var accountInfoIndex = 0; accountInfoIndex < platform_account_info_dom.length; accountInfoIndex++) {
                var accountInfoItem = platform_account_info_dom[accountInfoIndex];
                if (isBlank($(accountInfoItem).find("input[name='account']").val())) {
                    layer.tips('账号不能为空', $(accountInfoItem).find("input[name='account']"));
                    return false;
                }
            }
        }
    }

    // 时间账单标签
    var timeBillMoneyItems = $(flowMsgDiv).find("div[class*='time-bill-money-item']");
    if (timeBillMoneyItems.length >0){
        for(var billLabelIndex=0;billLabelIndex<timeBillMoneyItems.length;billLabelIndex++){
            var timeBillMoneyItem = timeBillMoneyItems[billLabelIndex];
            var labelId = $(timeBillMoneyItem).attr("data-id");
            var labelName = $(timeBillMoneyItem).attr("data-label-name");
            if (mustIds.indexOf(labelId) >= 0) {
                // 进行校验
                var dataBillItems = $(timeBillMoneyItem).find("div[data-bill-index]");
                var resultDatas = [];
                for (var index = 0; index < dataBillItems.length; index++) {
                    var item = dataBillItems[index];
                    var itemIndex = $(item).attr("data-bill-index");
                    var timeStart = $(item).find("input[name='time_start']").val();
                    if (isBlank(timeStart)) {
                        layer.msg(labelName + ':第' + itemIndex + "时间段，开始日期不能为空");
                        return false;
                    }
                    var timeEnd = $(item).find("input[name='time_end']").val();
                    if (isBlank(timeEnd) && (itemIndex !== dataBillItems.length)) {
                        layer.msg(labelName + ':第' + itemIndex + "时间段，结束日期不能为空");
                        return false;
                    }
                    var timeSuccess = $(item).find("input[name='time_success']").val();
                    if (isBlank(timeSuccess) || timeSuccess <= 0 || !(/^[0-9]*$/.test(timeSuccess))) {
                        layer.msg(labelName + ':第' + itemIndex + "时间段，成功数不能为空且为大于0的整数");
                        return false;
                    }
                    var timePrice = $(item).find("input[name='time_price']").val();
                    if (isBlank(timePrice) || timePrice <= 0) {
                        layer.msg(labelName + ':第' + itemIndex + "时间段，单价不能为空且大于0");
                        return false;
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
            }
        }
    }

	// 账单对账标签
	var uncheckedBillInfoEle = flowMsgDiv.find("div.unchecked-bills");
	if (uncheckedBillInfoEle.length > 0) {
		var checkedBills = uncheckedBillInfoEle.find('div.unchecked-bill-item > input[type=checkbox]:checked');
		if (checkedBills.length === 0) {
			layer.msg('请选择账单');
			return false;
		}
		var flag = true;
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

// 处理提单信息
function take_apply_order(parent, label_name, label_value, ele_id) {
    var label_dom = "<label class='layui-form-label show-lable' style='float: none' data-label-name='" + label_name + "'><span>" + label_name + "：</span></label>";
    var table_dom = "<table class='layui-table' lay-filter='order-table-" + (ele_id + label_name) + "' id='order-table-" + (ele_id + label_name) + "'>" +
        "  <thead>" +
        "    <tr>" +
        "      <th lay-data=\"{field:'id', 'hide': true}\"></th>" +
        "      <th lay-data=\"{field:'productname'}\">产品名称</th>" +
        "      <th lay-data=\"{field:'format'}\">规格型号</th>" +
        "      <th lay-data=\"{field:'price'}\">销售单价</th>" +
        "      <th lay-data=\"{field:'amount', width: 100}\">数量</th>" +
        "      <th lay-data=\"{field:'total'}\">销售总额</th>" +
        "      <th lay-data=\"{field:'remark'}\">备注</th>" +
        "    </tr>" +
        "  </thead>" +
        "  <tbody>" +
        "  </tbody>" +
        "</table>";
    parent.parents('.layui-form-item').html("<div class='order-table'>" + label_dom + table_dom + "</div>");
}

function init_apply_order_table(ele_id, label_name) {
    layui.use('table', function () {
        var table = layui.table;
        var orderTableName = 'order-table-' + (ele_id + label_name);
        table.init(orderTableName, {limit: 999});
        var flowMsgDiv = $("#" + ele_id);
        var flowData = flowMsgDiv.find("xmp[name='flowData']").text();
        var flow = JSON.parse(flowData);
        var orders = flow.labelValueMap[label_name];
        if (isNotBlank(orders)) {
            var orderData = JSON.parse(orders);
            table.reload(orderTableName, {
                url: '',
                data: orderData
            });
        }
    });
}

//处理配单信息
function take_match_order(parent, label_name, label_value, ele_id) {
    var label_dom = "<label class='layui-form-label show-lable' style='float: none' data-label-name='" + label_name + "'><span>" + label_name + "：</span></label>";
    var table_dom = "<table class='layui-table' lay-filter='match-order-table-" + (ele_id + label_name) + "' id='match-order-table-" + (ele_id + label_name) + "'>" +
        "  <thead>" +
        "    <tr>" +
        "      <th lay-data=\"{field:'id', 'hide': true}\"></th>" +
        "      <th lay-data=\"{field:'dsproductid', 'hide': true}\"></th>" +
        "      <th lay-data=\"{field:'productname'}\">产品名称</th>" +
        "      <th lay-data=\"{field:'format'}\">规格型号</th>" +
        "      <th lay-data=\"{field:'price'}\">销售单价</th>" +
        "      <th lay-data=\"{field:'amount', width: 100}\">数量</th>" +
        "      <th lay-data=\"{field:'total'}\">销售总额</th>" +
        "      <th lay-data=\"{field:'suppliername'}\">供应商</th>" +
        "      <th lay-data=\"{field:'supplierid', 'hide': true}\"></th>" +
        "      <th lay-data=\"{field:'remark'}\">备注</th>" +
        "    </tr>" +
        "  </thead>" +
        "  <tbody>" +
        "  </tbody>" +
        "</table>";
    parent.parents('.layui-form-item').html("<div class='match-order-table'>" + label_dom + table_dom + "</div>");
}

function init_match_order_table(ele_id, label_name) {
    layui.use('table', function () {
        var table = layui.table;
        var orderTableName = 'match-order-table-' + (ele_id + label_name);
        table.init(orderTableName, {limit: 999});
        var flowMsgDiv = $("#" + ele_id);
        var flowData = flowMsgDiv.find("xmp[name='flowData']").text();
        var flow = JSON.parse(flowData);
        var orders = flow.labelValueMap[label_name];
        if (isNotBlank(orders)) {
            var orderData = JSON.parse(orders);
            table.reload(orderTableName, {
                url: '',
                data: orderData
            });
        }
    });
}

// 提单信息转换成字符串
function typeApplyOrderToString(labelValue, flowClass) {
    var html = "";
    var items = product_bill_label_name[flowClass];
    if (!isBlank(labelValue)) {
        var orders = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
        if (!isBlank(orders) && orders.length > 0) {
            /*html += "<span onclick=\"showOrders(this)\" style=\"color: #1E9FFF;cursor: pointer;\">详情</span>"*/
            html = showOrdersDetails(html, labelValue);
            /*$.each(orders, function (i, order) {
                html += "<br>";
                for (var name in items) {
                    if (isNotBlank(order[name])) {
                        html += items[name] + "：" + order[name] + "，";
                    }
                }
                if (html.endWith("，")) {
                    html = html.substring(0, html.length - 1);
                }
            });*/
        }
    }
    return html;
}

// 提单信息转换成字符串
function typeMatchOrderToString(labelValue, flowClass) {
    var html = "";
    var items = product_bill_label_name[flowClass + "match"];
    if (!isBlank(labelValue)) {
        var orders = (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
        if (!isBlank(orders) && orders.length > 0) {
            html = showMatchOrdersDetails(html, labelValue);
            /*$.each(orders, function (i, order) {
                html += "<br>";
                for (var name in items) {
                    if (isNotBlank(order[name])) {
                        html += items[name] + "：" + order[name] + "，";
                    }
                }
                if (html.endWith("，")) {
                    html = html.substring(0, html.length - 1);
                }
            });*/
        }
    }
    return html;
}

// 点击查看详情
function showOrders(ele) {
    $(ele).parents('.recordContent').find('.orders-records').toggleClass('layui-show');
}
// 提单信息修改成表格展示
function showOrdersDetails(html,labelValue) {
    var detail = $('<div class=""></div>');
    /*detail.append('<table class="flow-view-table" cellpadding="0" cellspacing="0"></table>');*/
    var tableHtml = $('<table class="flow-view-table" border="1" cellpadding="0" cellspacing="0"></table>');
    var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
    tableTrHtml.append('<td class="flow-view-table-th" width="30%">产品名称</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="20%">规格型号</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="15%">销售单价</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="15%">数量</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="20%">销售总额</td>');
    tableHtml.append(tableTrHtml);
    detail.append(tableHtml);
    var totalSum = 0;
    $(typeof labelValue == 'string' ? JSON.parse(labelValue) : labelValue).each(function (i, item) {
        var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
        tableTrHtml.append('<td class="flow-view-table-td">' + item.productname + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td">' + item.format + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.price) + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.amount) + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.total) + '</td>');
        tableHtml.append(tableTrHtml);
        detail.append(tableHtml);
        totalSum += parseFloat(clearComma(item.total));
    });
    tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
    tableTrHtml.append('<td class="flow-view-table-td">销售总额合计</td>');
    tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money" colspan="4">' + thousand(totalSum.toFixed(2)) + '</td>');
    tableHtml.append(tableTrHtml);
    detail.append(tableHtml);
    return html + detail.prop("outerHTML");;
}

// 配单信息修改成表格展示
function showMatchOrdersDetails(html,labelValue) {
    var detail = $('<div class=""></div>');
    /*detail.append('<table class="flow-view-table" cellpadding="0" cellspacing="0"></table>');*/
    var tableHtml = $('<table class="flow-view-table" border="1" cellpadding="0" cellspacing="0"></table>');
    var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
    tableTrHtml.append('<td class="flow-view-table-th" width="15%">供应商</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="20%">产品名称</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="15%">规格型号</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="15%">采购单价</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="10%">数量</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="15%">采购总额</td>');
    tableTrHtml.append('<td class="flow-view-table-th" width="10%">运费</td>');
    tableHtml.append(tableTrHtml);
    detail.append(tableHtml);
    var totalSum = 0;
    $(typeof labelValue == 'string' ? JSON.parse(labelValue) : labelValue).each(function (i, item) {
        var tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
        tableTrHtml.append('<td class="flow-view-table-td">' + item.suppliername + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td">' + item.productname + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td">' + item.format + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.price) + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.amount) + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.total) + '</td>');
        tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money">' + thousand(item.logisticsCost) + '</td>');
        tableHtml.append(tableTrHtml);
        detail.append(tableHtml);
        totalSum += (parseFloat(clearComma(item.total))+parseFloat(clearComma(item.logisticsCost)));
    });
    tableTrHtml = $('<tr class="flow-view-table-th"></tr>');
    tableTrHtml.append('<td class="flow-view-table-td">采购总额合计（加运费）</td>');
    tableTrHtml.append('<td class="flow-view-table-td flow-view-table-td-money" colspan="6">' + thousand(totalSum.toFixed(2)) + '</td>');
    tableHtml.append(tableTrHtml);
    detail.append(tableHtml);
    return html + detail.prop("outerHTML");
}

/**
 * 清除数字千分位
 *
 * @param s
 * @returns
 */
function clearComma(s) {
    if ($.trim(s) == "") {
        return s;
    } else {
        return (s + "").replace(/[,]/g, "");
    }
}