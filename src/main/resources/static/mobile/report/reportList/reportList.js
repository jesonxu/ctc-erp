layui.use(['layer', 'element', 'laydate', 'flow'], function () {
	
	// 日期默认查询日 当天或者前一天的
	var currentTime = new Date();
	if (currentTime.getHours() > 16) {
		$('#selectDateSlot').val(currentTime.Format('yyyy-MM-dd') + ' - ' + currentTime.Format('yyyy-MM-dd'));
	} else {
		$('#selectDateSlot').val(currentTime.NextDay(-1).Format('yyyy-MM-dd') + ' - ' + currentTime.NextDay(-1).Format('yyyy-MM-dd'));
	}
	
    let layer = layui.layer,
    	element = layui.element,
    	laydate = layui.laydate,
    	form = layui.form,
    	flow = layui.flow;
	
	let typeJson = {
		0: {
			type: '日报',
			icon: '日'
		},
		1: {
			type: '周报',
			icon: '周'
		},
		2: {
			type: '月报',
			icon: '月'
		},
		3: {
			type: '季报',
			icon: '季'
		},
		4: {
			type: '半年报',
			icon: '半'
		},
		5: {
			type: '年报',
			icon: '年'
		}
	};
	
	var selectType = 0,
		currentPage = 0,
		pageSize = 20,
		deptFilter;
	
    $('#reportType').unbind().bind('click', function () {
    	$('.select-report-type-item').toggleClass('select-report-type-item-unselect');
    });
    
    $('.select-report-type-item dd').unbind().bind('click', function () {
    	var ele = $(this);
    	ele.css('background-color', 'rgb(1, 150, 136)');
    	setTimeout(function() {
    		var type = ele.attr('value');
    		selectType = type;
    		
    		currentPage = 0;
    		$('#reportListUl').html('');
    		queryReportByPage();
    		
    		$('#reportType').text(typeJson[type]['icon']);
    		$('.select-report-type-item').toggleClass('select-report-type-item-unselect');
    		ele.css('background-color', 'white');
		}, 100);
    })
    
    function jumpAddReportPage(type) {
    	$('.float-add-flow-btn i').trigger('click');
    	
    	// 跳转添加汇报页面
    	window.location.href = '/mobile/toAddreport.action?reportType=' + type + '&temp=' + Math.random();
    }
    
    laydate.render({
	  elem: '#selectDateSlot',
	  range: true,
	  trigger: 'click',
	  done: function (value, date) {
		  // 延迟一下查询否则时间没渲染查询日期不对
		  setTimeout(function () {
			  currentPage = 0;
			  $('#reportListUl').html('');
			  queryReportByPage();
		  }, 50);
	  }
	});

	/**
	 * 遍历已经存放的节点，将json放在合适的位置
	 * @param depts    		部门结构根节点
	 * @param json    		新节点
	 * @param findChildren	已经确定是子节点
	 * @returns {boolean}
	 */
	function ergodicDept(depts, json, findChildren) {
		// 是否在已有节点中找到json的父节点
		var findParent = false;
		// 是json节点的子节点的序号
		$(depts).each(function (i, item) {
			// json是当前节点的子部门
			if (item.value == json.pId) {
				if (!item.children) {
					item.children = [json];
				} else {
					item.children.push(json);
				}
				findParent = true;
			} else if (item.pId == json.value) {
				// 当前节点是json的子节点
				if (!json.children) {
					json.children = [item];
				} else {
					json.children.push(item);
				}
				findChildren.push(i);
			} else if (item.children) {
				// 在当前节点的子部门中找json的位置
				if (ergodicDept(item.children, json, findChildren)) {
					findParent = true;
				}
			}
		});
		return findParent;
	}
    
    // 查询部门组织架构
    $.ajax({
        url: "/department/searchDepartment.action?temp=" + Math.random(),
        type: "POST",
        async: false,
        success: function (result) {
        	var data = [];
        	if (result && result.data && result.data.length > 0) {
        		data = result.data;
        	}

    		// 部门结构根节点
    		var arr = [];
    		arr.push({
    			value: -999,
				name: '我的汇报',
				pId: -999,
				nodeType: 'user',
				selected: true
    		});
    		// 权限下能看到的部门、用户
    		var idStrArr = [];
    		$(data).each(function (i, item) {
    			idStrArr.push(item.id);
    		});

			$(data).each(function (i, item) {
    			var json = {
    				value: item.id,
    				name: item.name,
    				pId: item.pId,
    				nodeType: item.nodeType
    			};

				// 遍历已有的节点，将json放在合适的位置
				var findChildren = [];
				var findParent = ergodicDept(arr, json, findChildren);
				// 从已有部门结构中移除 是子部门 的节点
				for (var index = findChildren.length - 1; index >= 0; index--) {
					arr.splice(findChildren[index], 1);
				}
				// json找到父节点会放在父节点的children中，但暂时没在找到父节点就作为根节点
				if (!findParent) {
					arr.push(json);
				}
    		});
    		
        	deptFilter = xmSelect.render({
            	el: '#deptFilter', 
            	autoRow: true,
            	filterable: true,
            	radio: true,
            	tips: '请选择部门',
            	tree: {
            		strict: false,
            		show: true,
            		showFolderIcon: true,
            		showLine: true,
            		indent: 20,
            		expandedKeys: true,
            	},
            	toolbar: {
            		show: true,
            		list: ['CLEAR']
            	},
            	height: 'auto',
            	data(){
            		return arr;
            	}
            });
        	
        	$('.label-content').append('<span>&nbsp;</span>');
        	
        	var mutationObserver = new MutationObserver(function callback(mutationsList, observer) { // 回调事件
        		if ($('.label-content .xm-label-block').length == 0) {
        			$('.label-content').append('<span>&nbsp;</span>');
        		} else {
        			$('.label-content > span').remove();
        		}
        		
        		currentPage = 0;
        		$('#reportListUl').html('');
        		queryReportByPage();
        	});
        	mutationObserver.observe($('.label-content')[0],  { // options：监听的属性
        		attributes: true, 
        		childList: false,
        		subtree: false,
        		attributeOldValue: false
        	});
        }
    });
    
    queryReportByPage();
    
    // 查询汇报
    function queryReportByPage() {
    	var deptId = '';
    	var userId = '';
    	if (deptFilter && deptFilter.getValue() && deptFilter.getValue().length > 0) {
    		var selected = deptFilter.getValue()[0];
    		if (selected.value == -999) {
    			deptId = '';
    			userId = $('#userId').val();
    		} else {
    			deptId = selected.nodeType == 'dept' ? selected.value : '';
    			userId = selected.nodeType == 'user' ? selected.value : '';
    		}
    	}
    	
    	flow.load({
            elem: '#reportListUl', //流加载容器
            done: function (page, next) { //执行下一页的回调
            	let loadIndex = layer.load(1, {
            		shade: [0.1, '#fff']
            	});
    	
		    	$.ajax({
		    		url: '/userReport/queryUserReportByPage4Mobile.action?temp=' + Math.random(),
		    		type: 'POST',
		    		async: true,
		    		data: {
		    			reportType: selectType,
		    			queryDate: $('#selectDateSlot').val(),
		    			deptId: deptId,
		    			userId: userId,
		    			currentPage: currentPage + 1,
		    			pageSize: pageSize
		    		},
		    		success: function (result) {
		    			if (result && result.data && result.data.data && result.data.data.length > 0) {
		    				
		    				var totalHtml = '';
		    				currentPage++;
		    				
		    				// 遍历汇报内容
		    				$(result.data.data).each(function (i, item) {
		    					$('#reportListUl > span').remove();
		    					var html = '<li data-report-id="' + item.id + '">' +
		    							'	<div class="report-title">' +
		    							'		<span class="report-title-dept">' + item.userName.split(' - ')[0] + '</span>' +
		    							'		<span class="report-title-user">' + item.userName.split(' - ')[1] + '</span>' +
		    							'		<span class="report-title-time">' + new Date(item.wtime).Format('yyyy-MM-dd hh:mm:ss') + '</span>' +
		    							'	</div>'+
		    							'<div class="report-content">' +
		               	 				'</div>' +
		               	 				'<div class="report-comments">' +
		               	 				'<div class="report-comment-input"><input placeholder="请输入评论"/><button>保存</button></div>' +
		               	 				'</div>' +
		               	 			'</li>';
		    					var ele = $(html);
		    					$(ele).find('.report-content').append( '<div class="report-content-main">' + item.content + '</div>');
		    					
		    					$(ele).find('.report-content .report-content-main').after('<div class="report-content-enclosure"><span>附件：</span></div>');
		    					
		    					// 附件
		    					var enclosureHtml = '';
		    					if (item.enclosure && item.enclosure.length > 0) {
		    						var arr = [];
		    						$(item.enclosure).each(function (ind, file) {
		    							arr.push('<span class="enclosure-file-span" file="{&quot;fileName&quot;:&quot;' + file.fileName + '&quot;,&quot;filePath&quot;:&quot;' 
		    									+ file.filePath.replace(/\\/g, '\\\\') + '&quot;}"></span>'
		    									+ '<button style="margin: 0 5px;" title="点击下载" type="button" name="downloadFile" class="layui-btn layui-btn-xs unchecked-bill-file-tip" '
		    									+ 'onclick="downloadFile(this, {&quot;fileName&quot;: &quot;' + file.fileName + '&quot;, &quot;filePath&quot;: &quot;' 
		    									+ file.filePath.replace(/\\/g, '\\\\') + '&quot;})">' + file.fileName + '</button>'
		    							);
		    						});
		    						enclosureHtml += arr.join('<br/>');
		    					}
		    					$(ele).find('.report-content .report-content-enclosure span').after(enclosureHtml);
		    					
		    					// 评论
		    					var commentsHtml = '';
		    					if (item.comments && item.comments.length > 0) {
		    						var arr = [];
		    						$(item.comments).each(function (i, comment) {
		    							arr.push('<div class="report-comments-item"><span class="report-comment-title">'+ new Date(comment.wtime).Format('MM月dd号hh时mm分') + '&nbsp;' + comment.userName + '：</span>'
		    								+ '<span>&nbsp;&nbsp;' + comment.comment + '</span></div>');
		    						});
		    						commentsHtml += arr.join('');
		    					}
		    					commentsHtml = commentsHtml ? commentsHtml : '<div class="blank-comments"><span>暂无评论</span></div>'
		    					$(ele).find('.report-comments').prepend('<span>评论：</span><br/>' + commentsHtml);
		    					
		    					totalHtml += ele.prop('outerHTML');
		    				});
		    				
		    			} else {
		    				totalHtml = '<span style="display: inline-block; width: 100%; text-align: center; height: 2em; line-height: 2em;">暂无汇报</span>'
		    			}
		    			next(totalHtml,result && result.data && currentPage < result.data.totalPages);
		    			layer.close(loadIndex);
		    			
		    			$('#reportListUl li .report-comments button').unbind().bind('click', function () {
	    					if (!$(this).parents('li').find('.report-comments input').val()) {
	    						$(this).parents('li').find('.report-comments input').trigger('focus');
	    						return layer.msg('请输入评论内容');
	    					}
	    					
	    					var thisEle = $(this);
	    					
							$.ajax({
								type: "POST",
								url: "/userReport/commentReport?temp=" + Math.random(),
								data: {
									reportId: $(this).parents('li').attr('data-report-id'),
									isComment: false,
									reportComment: $(this).parents('li').find('.report-comments input').val()
								},
								dataType: "JSON",
								async: true,
								success: function (result) {
									if (!result) {
										layer.msg('保存评论失败!');
									} else {
										if (result.code == 200) {
											layer.msg('保存评论成功!');
											$(thisEle).parents('li').find('.report-comments .blank-comments').remove();
											$(thisEle).parents('li').find('.report-comments br').after('<div class="report-comments-item"><span class="report-comment-title">'+ new Date().Format('MM月dd号hh时mm分') + '&nbsp;' + $('#userName').val() + '：</span>'
													+ '<span>&nbsp;&nbsp;' + $(thisEle).parents('li').find('.report-comments input').val() + '</span></div>');
											$(thisEle).parents('li').find('.report-comments input').val('');
										} else {
											layer.msg(result.msg);
										}
									}
								}
							});
	    				});
		    		}
		    	});
		    	
            }
    	});
    }
    
    
    let floatBtn = new Float("div[data-float-btn='true']", "#add-menu-list");
    floatBtn.do([{
        icon: "icon-one icon-report-day",
        name: "日报",
        handle: function () {
        	jumpAddReportPage(0);
        }
    }, {
        icon: "icon-one icon-report-week",
        name: "周报",
        handle: function () {
        	jumpAddReportPage(1);
        }
    }, {
        icon: "icon-one icon-report-month",
        name: "月报",
        handle: function () {
        	jumpAddReportPage(2);
        }
    }, {
        icon: "icon-one icon-report-season",
        name: "季报",
        handle: function () {
        	jumpAddReportPage(3);
        }
    }, {
        icon: "icon-one icon-report-half-year",
        name: "半年报",
        handle: function () {
        	jumpAddReportPage(4);
        }
    }, {
        icon: "icon-one icon-report-year",
        name: "年报",
        handle: function () {
        	jumpAddReportPage(5);
        }
    }]);
   
});

//文件下载
function downloadFile(ele, file) {
	if (typeof file == 'object') {
		 var fileParams = "filePath=" + encodeURIComponent(file.filePath) + "&fileName=" + encodeURIComponent(file.fileName) + "&r=" + Math.random();
		 window.location.href = "/operate/downloadFile?" + fileParams;
	}
}