document.write('<script type="text/javascript" src="/common/js/dropdown.js" ></script>');
layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    dropdown: 'dropdown'
});

var weekChineseArr = ['日', '一', '二', '三', '四', '五', '六'];
var currentPage = 0;
var totalPage = -1;
layui.use(['form', 'layedit', 'laydate', 'element', 'upload', 'dropdown', 'table'], function(){
	form = layui.form;
    layedit = layui.layedit;
    laydate = layui.laydate;
    upload = layui.upload;
    element = layui.element;
    layer = layui.layer;
    dropdown = layui.dropdown;
    table = layui.table;
	
	init();
	
	function init() {
		queryFlowEnts();
	}
});

var flag = false;

function queryFlowEnts() {
	$.ajax({
        type: "POST",
        async: true,
        url: '/personalCenter/getPersonalFlowEntByPage.action',
        dataType: 'json',
        data: {
        	page: currentPage + 1,
        	pageSize: 20
        },
        success: function (result) {
            if (result && result.code == 200 && result.data) {
            	if (totalPage == -1) {
            		totalPage = parseInt(result.msg);
            	}
            	if (result.data.length == 0) {
            		$('.flow-ent-history-content').html('<div class="apply-flow-menu-no-data">无流程处理</div>');
            	} else {
            		currentPage = currentPage + 1;
            		if (!flag) {
            			$('.flow-ent-history-content').html('');
            		}
            		
            		var flowEntIds = [];
            		var html = '';
            		
            		$(result.data).each(function (i, item) {
            			flowEntIds.push(item.id);
            			var s = '<div id="flowMsg_' + item.id + '"><div class="settlement-title" style="cursor: pointer;" id="' + item.id + '" entId="' + item.id + '" productId="' + item.productId + '">'
            				+ '<b><span class="flow-ent-title-time">' + str2Date(item.applyTime).Format('yyyy年MM月dd日') + '(周' + weekChineseArr[str2Date(item.applyTime).getDay()] + ')</span>'
							+ '<span class="flow-ent-title-user">' + item.deptName + ' / ' + item.realName + '</span>'
	                    	+ '<span class="flow-ent-title-name">' + item.flowTitle + '</span>'
	                    	+ '<span class="flow-ent-status">' + (item.flowStatus == '归档' ? '归档' : (item.nodeName ? item.nodeName : '无')) + '</span>';
	                    
	                    if (item.canOperat) {
	                        s += '<span style="color:red;">(待处理)</span>'
	                    }
	                    
	                    if (item.flowStatus === "取消"){
	                        s += "<span class='flow-state-cancel'>已取消</span>";
	                    } else if (item.flowStatus === "归档"){
	                        s += "<span class='flow-state-document'>已归档</span>";
	                    } else {
	                        s += "<span class='flow-state-process'>进行中</span>";
	                    }
	                    
	                    html += s + "</b></div><hr style='height: 3px; background-color: #1E9FFF'/></div>";
            		});
            		
            		if ($('.flow-ent-history-content .settlement-title').length == 0) {
            			$('.flow-ent-history-content').html(html);
            		} else {
            			$('.flow-ent-history-content').append(html);
            		}
            		
            		for (var i = 0; i < flowEntIds.length; i++) {
                        $('[entid="' + flowEntIds[i] + '"]').unbind().bind('click', function () {
                            var entId = $(this).attr('entId');
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
            		
                    if (!flag) {
                    	flag = true;
                    	// 渲染第一个
                		var entId = $('[entid="' + flowEntIds[0] + '"]').attr('entId');
                        var ele =  $('[entid="' + flowEntIds[0] + '"]');
                        $.ajax({
                            type: "POST",
                            async: true,
                            url: '/flow/flowDetail.action?id=' + entId +"&temp=" + Math.random(),
                            dataType: 'json',
                            data: {},
                            success: function (data) {
                            	renderFlowEntContent(ele, data.data);
                            }
                        });
                    }
                    
                    $(window).unbind('scroll').bind('scroll', function () {
                		// scrollTop就是触发滚轮事件时滚轮的高度
                        var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
                        // 变量windowHeight是可视区的高度
                        var windowHeight = document.documentElement.clientHeight || document.body.clientHeight;
                        // 变量scrollHeight是滚动条的总高度
                        var scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
                        // 判断滚动条是否到底部
                        if (scrollTop + windowHeight == scrollHeight) {
                        	if (totalPage == -1 || totalPage > currentPage) {
                        		$(window).unbind('scroll');
                        		queryFlowEnts();
                        	}
                        }
                    });
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
    var nextEle = $(ele).next('.flowMsg');
    if (nextEle.length !== 0) {
        $(nextEle).remove();
        return;
    }

    // 最外层框
    var html = "<div class='flowMsg'>";
    
	// 流程详细信息
	html += "<xmp hidden name='flowData' >" + JSON.stringify(data) + "</xmp>";

	var records = data.record;
	if (records != null && records.length > 0) {
		
		html += "<div id='flowLogs'>"; // flowLogs
		
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
            
	        html += "<div id='record" + recordIndex + "' class='record layui-input-block'>"; // recordX
	        
	        // 标题：处理人+处理时间+处理结果
	        html += "<div class='recordTitle layui-input-inline'>"; // recordTitle
            html += "<span>" + str2Date(dealTime).Format('yyyy年MM月dd日 hh:mm') + "&nbsp;&nbsp;&nbsp;&nbsp;" + dealPerson + "[" + dealRole + "]</span>&nbsp;&nbsp;&nbsp;&nbsp;" + auditResultSpan;
            html += "</div>"; // recordTitle
            
	        // 内容：处理意见+修改内容
	        html += "<div class='recordContent'>"; // recordContent
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
                           // 处理记录中的标签值转为文字，不需要标签格式
                   		html += label.name + "：";
                   		html += labelValueToString(label.type, changes[label.name], label.defaultValue, data.flowEntId, data.flowClass, data.flowId);
                   		html += "<br/>";
                       }
                   });
	            } else {
	            	$.each(data.labelList, function (i, label) {
                        if (changes[label.name] != null) {
                            // 处理记录中的标签值转为文字，不需要标签格式
                    		html += label.name + " 修改为：";
                    		html += labelValueToString(label.type, changes[label.name], label.defaultValue, data.flowEntId, data.flowClass, data.flowId);
                    		html += "<br/>";
                        }
                    });
	            }
	        }
			html += "</div>"; // recordContent
			html += "</div>"; // recordX
	    });

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
	    	
	        // 当前用户可以审核时，显示内容：可编辑标签 + 审核区
	        if (data.canOperat) {
	            var editLabelIds = data.editLabelIds;

				if (isNotBlank(editLabelIds)) {
					html += "<div class='recordContent' style='background: whitesmoke; border: #e0e0e0; border-radius:7px;'>";
					// 流程的所有标签
					var labelList = data.labelList;
					if (labelList != null && labelList.length > 0) {
						// 流程标签div
						html += "<div class='layui-form' id='flowLabels' style='margin: 5px'>";

						// 每个可编辑标签
						$.each(labelList, function (i, label) {
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
	            
	            // 通过按钮（所有节点)
	            var buttonName = null;
	            if (data.nodeIndex !== 0) {
	            	buttonName = "通过";
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
	        	html += '<div class="layui-form-item" style="text-align: right;padding-top: 5px;"><button type="button" class="layui-btn layui-btn-sm" onclick="revokeFlow(&quot;' + data.flowEntId + '&quot;, this)"><i class="layui-icon layui-icon-ok"></i>撤销</button></div>'
	        	html += '</div>';
	        }
	        html += "</div>"; // flowOperate 流程处理
	    }

	    html += "</div>"; // flowMsg 最外层框
	    
	    html += "</div>" // flowLogs 流程处理记录

	    // 清除本流程以前的框
	    $(ele).next('.flowMsg').remove();
	    $(ele).after(html);
	    
	    // 当前用户可以处理时，才渲染可编辑标签
	    if (data.canOperat) {
	        init_layui(data);
	    }
	}
}

//撤销
function revokeFlow(flowEntId, ele) {
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
                    layer.msg('撤销成功');
                    $("#flowMsg_" + flowEntId).remove();
                } else {
                    layer.msg(data.msg);
                    $(ele).bind('click', function () {
                    	revokeFlow(flowEntId, ele);
                    });
                }
            }
        });
    }, function () {
    	layer.msg("取消");
    });
}
