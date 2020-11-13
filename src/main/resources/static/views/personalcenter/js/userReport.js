// 闭包防止参数污染
layui.use(['laydate', 'layer', 'form', 'element', 'layedit', 'upload'], function () {
	
	$('.report-tab').height($(window).height() - 20);
	
	// 获取layui js 对象
	var laydate = layui.laydate;
	var layer = layui.layer;
	var element = layui.element;
	var layedit = layui.layedit;
	var form = layui.form;
	var upload = layui.upload;
	var queryHasReportDate = '';
	
	layedit.set({ // 设置富文本上传action
		uploadImage: {
			url: '/userReport/uploadTextImg.action' // 接口url
            ,type: 'post' // 默认post
        }
    });
	
	var selectYearOrMonthDate = '';
	
	var reportTypeArr = ['day', 'week', 'month', 'season', 'halfYear', 'year'];
	var chineseArr = ['', '一', '二', '三', '四', '五', '六'];
	var reportTypeChinese = ['日报', '周报', '月报', '季报', '半年报', '年报'];
	var weekChineseArr = ['日', '一', '二', '三', '四', '五', '六'];
	
	// isLeader -1：未登录，0：普通员工，1：是领导
	  
	// 创建富文本编辑框
	var addReportTextIndex;
	
	// 最新报告时间
	var newestReportTime;

	// 点击所选参数
	var openParams = {
		type: -1,
		openDeptId: '',
		openUserId: '',
		queryDate: '',
		reportType: -1,
		mark: ''
	}
	
	init();
	
	function init() {
		// initSelectPeriod();
		createTextComponent();
		loadDeptAndUserInfo();
		initBindEvent();
		queryNewestReportTime(parseInt($('#reportType input[name="reportType"]:checked').val()), false);
		queryReportLastTime();
	}
	
	form.on("radio(report-type)", function (data) {
		if (openParams.reportType == data.value) {
			$('#deptAndUser .my_active:first').trigger('click');
		}
		queryNewestReportTime(parseInt(data.value), false);
		queryReportLastTime();
		$('#saveReportButton').find('span').text('提交' +　reportTypeChinese[data.value]);
	});

	// ------------------------------------------------保存汇报内容部分--------------------------------------------------
	
	// 创建富文本组件
	function createTextComponent(textContent) {
		if (textContent) {
			$('#addReportText').html(textContent);
			$('#addReportText').val(textContent);
		} else {
			$('#addReportText').html('');
			$('#addReportText').val('');
		}
		addReportTextIndex = layedit.build('addReportText', { // 配置
			 tool: ['strong', 'italic', 'underline', 'del', '|', 'left', 'center', 'right', '|', 'link', 'unlink', 'face', 'image'],
			 height: 156
		});
	} 
	
	function initBindEvent() {
		$('#saveReportButton').click(function (i, item) {
			saveReport();
		});
		
		$('#reportDisplaySpan').bind('click', function () {
			$('#userReportMain').toggleClass('show-no');
			$('#saveReportDiv').toggleClass('show-no');
			$(this).find('i').toggleClass('layui-icon-extend layui-icon-extend-write-report');
			$(this).find('i').toggleClass('layui-icon-extend layui-icon-extend-hide-report-form');
			if ($(this).find('span').text() == '隐藏') {
				$(this).find('span').text('写汇报');
			} else {
				$(this).find('span').text('隐藏');
			}
		});
		
		$('#cancelReportButton').bind('click', function () {
			clearReportForm();
			$('#cancelReportButton').addClass('show-no');
			$('#saveReportButton').find('span').text('提交' + reportTypeChinese[openParams.reportType]);
			$('#reportDisplaySpan').trigger('click');
		});
	}
	
	var lastSubmitValue = '';
	var lastSubmitTime = null;
	// 保存汇报内容
	function saveReport() {
		var files = [];
		if (uploadFiles && uploadFiles.length > 0) {
			$(uploadFiles).each(function (i, item) {
				files.push(item.fileName + "," + item.filePath);
			});
		}
		var data = {
			reportType: $('#reportType input[name="reportType"]:checked').val(),
			content: layedit.getContent(addReportTextIndex),
			files: files.join(';'),
			reportId: $('#reportId').val()
		};
		if (!data.content) {
			return layer.msg('请输入内容');
		}
		if (lastSubmitValue == hex_md5(JSON.stringify(data)) && (new Date().getTime() - lastSubmitTime) <= 30000) {
	    	return layer.msg('不能连续提交相同数据');
	    } else {
	    	lastSubmitValue = hex_md5(JSON.stringify(data));
	    	lastSubmitTime = new Date().getTime();
	    }
		var ele = $('<div>'+ data.content + '</div>');
		ele.find('img').each(function (i, item) {
			var src = item.currentSrc + '';
			if (src && /:(.*?);(.*?)/.test(src)) { // base64文件
				var file = dataURLtoFile(src, uuid());
				if (file) {
					var formData = new FormData();
					formData.append('file', file);
					$.ajax({
						type: 'POST',
						url: '/userReport/uploadTextImg.action',
						dataType: 'JSON',
						async: false,
						processData: false,
						contentType: false,
						data: formData,
						success: function (result) {
							if (result && result.code == 0 && result.data) {
								item.src = result.data.src;
							}
						}
					});
				}
			}
		});
		data.content = ele.html();
		
		$.ajax({
			type: "POST",
			url: "/userReport/saveUserReport?temp=" + Math.random(),
			data: data,
			dataType: "JSON",
			async: true,
			success: function (result) {
				if (result && result.code == 200) {
					if (data.reportId) {
						$('[text-report-id="' + data.reportId + '"]').html(data.content);
						$('#cancelReportButton').addClass('show-no');
						$('#saveReportButton').find('span').text('提交' + reportTypeChinese[data.reportType]);
						dataComponentSrollTop();
						var newFileDom = createEnclosureHtml(uploadFiles);
						$('[text-report-id="' + data.reportId + '"]').next().html('<span>附件：</span>' + newFileDom);
					} else {
						var type = parseInt(openParams.reportType);
						newestReportTime = new Date();
						if (!$('[data-my-tag="我的汇报"]').hasClass('my_active')) {
							$('[data-my-tag="我的汇报"]').trigger('click');
						} else {
							$('[data-my-tag="我的汇报"]').trigger('click');
							$('[data-my-tag="我的汇报"]').trigger('click');
						}
						triggerClickDate($('#yearAndMonth'), 'year', newestReportTime);
					}
					$('#reportId').val('');
					layer.msg("保存成功!", {time: 2000, icon: 1});
					clearReportForm();
					$('#reportDisplaySpan').trigger('click');
				} else {
					layer.msg("保存失败!", {time: 2000, icon: 2});
					lastSubmitValue = '';
					lastSubmitTime = null;
				}
			}
		});
	}
	
	function dataURLtoFile(dataurl, filename) { // 将base64转换为文件，dataurl为base64字符串，filename为文件名（必须带后缀名，如.jpg,.png）
	    var arr = dataurl.split(','),
	        mime = arr[0].match(/:(.*?);/)[1],
	        bstr = atob(arr[1]),
	        n = bstr.length,
	        u8arr = new Uint8Array(n);
	    while (n--) {
	        u8arr[n] = bstr.charCodeAt(n);
	    }
	    return new File([u8arr], filename, { type: mime });
	}
	
	function getActiveIndex(ele, type) {
		var index = 0;
		$(ele).find('.my_span_title[data-my-id="' + type + '"]').each(function (i, item) {
			if (index != 0) {
				return;
			}
			if ($(this).hasClass('my_active')) {
				index = i + 1;
				ele = $(this).next().next();
			}
		});
		if (type == 'year') { 
			var subIndex = getActiveIndex(ele, 'halfYear');
			if (subIndex != 0) {
				index += subIndex;
			} else {
				subIndex = getActiveIndex(ele, 'season');
				if (subIndex != 0) {
					index += subIndex;
				} else {
					subIndex = getActiveIndex(ele, 'month');
					if (subIndex != 0) {
						index += subIndex;
					}
				}
			}
		} else if (type == 'month') {
			var subIndex = getActiveIndex(ele, 'week');
			if (subIndex != 0) {
				index += subIndex;
			} else {
				subIndex = getActiveIndex(ele, 'day');
				if (subIndex != 0) {
					index += subIndex;
				}
			}
		}
		return index <= 0 ? 0 : index;
	}

	// 清除报告表单数据
	function clearReportForm() {
		fileNames = [];
		uploadFiles = [];
		$('#uploadReportFileList').html('');
		createTextComponent();
	}
	
	function initSelectPeriod() {
		if (isLeader === undefined || isLeader === null || isLeader === '') {
			$.ajax({
				type: "POST",
				url: "/user/isLeader?temp=" + Math.random(),
				dataType: "JSON",
				async: false,
				success: function (result) {
					if (result && result.code == 200) {
						isLeader = result.data;
					}
				}
			});
		}
		if (isLeader == 0) { // 普通员工
			$('#reportType').find('input:eq(0)').attr('checked', true);
		} else { // 领导
			$('#reportType').find('input:eq(1)').attr('checked', true);
		}
		$('#textContent').addClass('show-no');
		$('#saveReportDiv').addClass('show-no');
		$('#uploadReportFile').addClass('show-no');
		$('#reportDisplaySpan').find('span').text('写汇报');
		$('#saveReportButton').find('span').text('提交' +　reportTypeChinese[1]);
	}
	
	function initCurrentUserDepts() {
		$.ajax({
			type: "POST",
			url: "/user/isLeader?temp=" + Math.random(),
			dataType: "JSON",
			async: false,
			success: function (result) {
				if (result && result.code == 200) {
					isLeader = result.data;
				}
			}
		});
	}
	
    // 多文件上传
	var fileNames = [];
	var uploadFiles = [];
    var reportFileListView = $('#uploadReportFileList')
        , uploadListIns = upload.render({
        elem: '#uploadReportFileButton' // 按钮ID
        , url: '/userReport/uploadFile.action?temp=' + Math.random() // action
        , accept: 'file'
        , multiple: true
        , auto: false
        , choose: function (obj) {
            files = this.files = obj.pushFile(); // 将每次选择的文件追加到文件队列
            // 读取本地文件
            obj.preview(function (index, file, result) {
                if (fileNames.indexOf(file.name) >= 0){
                    layer.msg("选择文件重复", {time: 2000, icon:2});
                    return "";
                }
                obj.upload(index, file);
            });
        }
        , done: function (res, index, upload) {
            if (res.code === 200 || res.code === "200") { // 上传成功
            	for (var fileIndex = 0; fileIndex < res.data.length; fileIndex++) {
                    var span = $('<span style="margin-left: 15px;">' + res.data[fileIndex].fileName + '</span>'
                    		+ '<button style="margin: 0 5px;" type="button" name="deleteFile" class="layui-btn layui-btn-xs unchecked-bill-file-tip">删除</button>');
                    reportFileListView.append(span);
                    fileNames.push(res.data[fileIndex].fileName);
                    uploadFiles.push(res.data[fileIndex]);
                    // 删除
                    $(reportFileListView).find('button[name="deleteFile"]').unbind().bind('click', function () {
                    	var fileName = $(this).prev().text();
                    	var index = fileNames.indexOf(fileName);
                        fileNames.splice(index, 1);
                        uploadFiles.splice(index, 1);
                        uploadListIns.config.elem.next()[0].value = ''; // 清空 input file 值，以免删除后出现同名文件不可选
                        $(this).prev().remove();
                        $(this).remove();
                    });
                }
            } else {
            	return layer.msg('上传失败!')
            }
        }
        , error: function (index, upload) {
            return layer.msg('上传失败!')
        }
    });
    
    // 查询最近的报告时间
    function queryNewestReportTime(type, needClick) {
		$.ajax({
			type: "POST",
			url: "/userReport/queryReportNewestTime?temp=" + Math.random(),
			dataType: "JSON",
			data: {
				type: type
			},
			async: false,
			success: function (result) {
				if (result && result.code == 200) {
					newestReportTime = str2Date(result.msg);
					if (needClick) {
						triggerClickDate($('#yearAndMonth'), 'year', newestReportTime);
					}
				}
			}
		});
	}
    
    // --------------------------------------汇报部门查询--------------------------------------------------
	
	// 加载部门和销售信息
	function loadDeptAndUserInfo() {
		$.ajax({
			type: "POST",
			url: "/department/queryDept4Report?temp=" + Math.random(),
			dataType: "JSON",
			data: {
				deptId: openParams.openDeptId
			},
			async: true,
			success: function (result) {
				if (result.code === 200 ){
					var data = result.data;
					var deptItem = $('#deptAndUser');
					if (openParams.type != -1) {
						deptItem = $('div[data-content-id="' + openParams.openDeptId + '_' + openParams.type + '"]');
					}
					if (!isNull(data) && data.length > 0 ){
						deptItem.empty();
						createOrgStructureHtml(deptItem, data)
					} else {
						deptItem.html("<div style='padding-left: 20px;'>暂无部门</div>");
					}
				} else {
					layer.msg(result.msg);
				}
			}
		});
	}
	
	// 创建组织架构html
	function createOrgStructureHtml(ele, data) {
		var thisEle = $(ele);
		thisEle.empty();
		var filter = new Date().getTime() + '';
		var html = '<div class="layui-collapse" lay-accordion lay-filter="' + filter + '" style="padding-left: 5px;">';
		if (data && data.length > 0) {
			$(data).each(function (i, item) {
				html += '<div class="layui-colla-item">'
					+ '<div class="layui-colla-title" data-my-size="title-size-0"'
					+ ' flow_ent_count="' + 0 + '"'
					+ ' data-my-id="' + item.id + '"'
					+ ' data-my-tag="' + item.name + '"'
					+ ' data-my-opts-type="' + item.type + '">'
					+ item.name
					+ '</div>'
					+ '<div class="layui-colla-content" data-content-id="' + item.id + '_' + item.type + '">'
					+ '</div>'
					+ '</div>';
			});
			html += '</div>';
			thisEle.append(html);
		}
		initOrgStructurePannel(thisEle, filter)
	}
	
	// 初始化组织架构面板
	function initOrgStructurePannel(ele, filter) {
		var pannel = new myPannel({
			openItem: function (item, itemId, optsType) {
				var t = $(item).clone();
				t.find('.layui-badge').remove();
				var itemName = $(t.find('.my_text_title')[0]).text().trim();
				if (optsType !== undefined && optsType !== null && optsType !== '') {
					if (optsType == 1) { // 点击用户
						recordDeptAndUser(1, '', itemId);
					} else { // 点击部门
						recordDeptAndUser(0, itemId, '');
						loadDeptAndUserInfo();
					}
					if (!newestReportTime) {
						setTimeout(function () {
							queryUserReports(openParams.reportType, openParams.queryDate, $('div[data-content-id="' + reportTypeArr[openParams.reportType] + '_' + openParams.mark + '"]'), false, dataComponentSrollTop, '');
							reflushHasReport();
						}, 50);
					}
				}
			}
		});
		pannel.init(ele);
		element.render("collapse", filter);
	}
	
	// 记录用户和部门
	function recordDeptAndUser(type, deptId, userId) {
		clearParams();
		openParams.type = type;
		openParams.openDeptId = deptId;
		openParams.openUserId = userId;
	}
	
	// 清空所有的记录数据
	function clearParams() {
		openParams.type = -1;
		openParams.penDeptId = '';
		openParams.openUserId = '';
	}
	
	// -----------------------------------------汇报日期部分--------------------------------------------------
	
	var needOpen = false;
	function queryReportLastTime() {
		var reportType = $('#reportType input[name="reportType"]:checked').val();
		$.ajax({
			type: "POST",
			url: "/userReport/queryReportLastTime?temp=" + Math.random(),
			dataType: "JSON",
			async: false,
			success: function (result) {
				if (result && result.code == 200) {
					needOpen = true;
					var daterArr = [];
					var dateStr = result.msg;
					var date = str2Date(dateStr);
					var now = new Date();
					var threeYearBefore = str2Date(now.NextYear(-3).Format('yyyy'));
					if (threeYearBefore.getTime() > date.getTime()) {
						date = threeYearBefore;
					}
					str2Date(now.NextYear(-3).Format('yyyy'))
					for (; date.getTime() <= now.getTime(); date.setFullYear(date.getFullYear() + 1)) {
						daterArr.push({
							txt: date.Format('yyyy') + '年',
							mark: date.Format('yyyy'),
							value: str2Date(date.Format('yyyy')).Format('yyyy-MM-dd hh:mm:ss') 
								+ '~' + str2Date(date.Format('yyyy')).NextYear().Format('yyyy-MM-dd hh:mm:ss'),
							startDate: dateStr
						});
					}
					createDateHtml(daterArr, reportType, reportTypeArr[5], $('#yearAndMonth'));
					dataComponentSrollTop();
					needOpen = false;
				}
			}
		});
	}
	
	function scrollFixedDistance(scrollDistance) {
		return function () {
			dataComponentSrollTop(scrollDistance);
		}
	}
	
	function dataComponentSrollTop(scrollDistance) {
		if (scrollDistance === undefined || scrollDistance === null || scrollDistance === 0) {
			var eleIndex = getActiveIndex($('#yearAndMonth'), 'year');
			eleIndex = eleIndex < 0 ? 0 : (eleIndex - 1);
			scrollDistance = parseInt(35 * eleIndex);
		}
		setTimeout(function () {
			$("#yearAndMonth").scrollTop(scrollDistance);
		}, 50);
	} 
	
	// 创建日期html
	function createDateHtml(data, reportType, type, ele) {
		var thisEle = $(ele);
		thisEle.empty();
		if (data && data.length > 0) {
			var filter = new Date().getTime() + '';
			var html = '<div class="layui-collapse" lay-accordion lay-filter="' + filter + '" style="padding-left: 5px;">';
			$(data).each(function (i, item) { // 年
				html += '<div class="layui-colla-item">'
					+ '<div class="layui-colla-title" data-my-size="title-size-0"'
					+ ' data-my-id="' + type + '"'
					+ ' data-my-mark="' + item.mark + '"'
					+ ' data-my-start-date="' + item.startDate + '"'
					+ ' data-my-tag="' + item.value + '"'
					+ ' data-my-opts-type="' + reportType + '">'
					+ item.txt
					+ '</div>'
					+ '<div class="layui-colla-content" data-content-id="' + type + '_' + item.mark + '">'
					+ '</div>'
					+ '</div>';
			});
			html += '</div>';
			thisEle.append(html);
			initDatePannel(thisEle, filter, reportType, type);
		}
	}
	
	// 初始化日期面板
	function initDatePannel(ele, filter, reportType, type) {
		var pannel = new myPannel({
			openItem: function (item, itemId, optsType) {
				var t = $(item).clone();
				t.find('.layui-badge').remove();
				var itemName = $(t.find('.my_text_title')[0]).text().trim();
				
				var queryDate = $(item).find('.my_span_title').attr('data-my-tag'); // 2020-01-01 00:00:00~2021-01-01 00:00:00
				var startDate = $(item).find('.layui-colla-title').attr('data-my-start-date'); // 2020-06-01
				var mark = $(item).find('.layui-colla-title').attr('data-my-mark'); // 2020、2020-06。。。
				
				if (optsType !== undefined && optsType !== null && optsType !== '') {
					var reportType = parseInt(optsType + '');
					var needQuery = false;
					if (reportTypeArr[reportType] == itemId) {
						needQuery = true;
					}
					if (needQuery) {
						if (reportType == 5) {
							queryHasReportDate = '';
							queryDateHasReport();
						}
						openParams.mark = mark;
						queryUserReports(reportType, queryDate, $('div[data-content-id="' + reportTypeArr[reportType] + '_' + mark + '"]'), false, newestReportTime ? dataComponentSrollTop : null, '');
						newestReportTime = '';
					} else {
						openParams.openDate = '';
						if (reportType == 0) { // 日
							if (itemId == reportTypeArr[5]) { // 年(展示月)
								showMonth(mark, startDate, reportType);
							} else if (itemId == reportTypeArr[2]) { // 月(展示日)
								showDay(mark, startDate, reportType);
								queryHasReportDate = queryDate;
								queryDateHasReport(queryDate);
							}
						} else if (reportType == 1) { // 周
							if (itemId == reportTypeArr[5]) {
								showMonth(mark, startDate, reportType);
							} else if (itemId == reportTypeArr[2]) {
								showWeek(mark, startDate, reportType);
								queryHasReportDate = queryDate;
								queryDateHasReport(queryDate);
							}
						} else if (reportType == 2) { // 月
							if (itemId == reportTypeArr[5]) {
								showMonth(mark, startDate, reportType);
								queryHasReportDate = queryDate;
								queryDateHasReport(queryDate);
							}
						} else if (reportType == 3) { // 季
							if (itemId == reportTypeArr[5]) {
								showSeason(mark, startDate, reportType);
								queryHasReportDate = queryDate;
								queryDateHasReport(queryDate);
							}
						} else if (reportType == 4) { // 半年
							if (itemId == reportTypeArr[5]) {
								showHalfYear(mark, startDate, reportType);
								queryHasReportDate = queryDate;
								queryDateHasReport(queryDate);
							}
						}
					}
				}
			}
		});
		pannel.init(ele);
		element.render("collapse", filter);
		if (newestReportTime) {
			triggerClickDate($(ele), type, newestReportTime);
		} else if (needOpen) {
			$(ele).find('.my_span_title:last').trigger('click');
		} 
	}
	
	function triggerClickDate(ele, type, date) {
		$(ele).find('[data-my-id="' + type + '"]').each(function () {
			var dataMyTag = $(this).attr('data-my-tag');
			var sTime = str2Date(dataMyTag.split('~')[0]);
			var eTime = str2Date(dataMyTag.split('~')[1]);
			if (sTime.getTime() <= date.getTime() && eTime.getTime() > date.getTime()) {
				if (!$(this).hasClass('my_active')) {
					$(this).trigger('click');
				} else {
					$(this).trigger('click');
					$(this).trigger('click');
				}
			}
		})
	}
	
	// 月
	function showMonth(mark, startDate, reportType) {
		var date = str2Date(str2Date(startDate).Format('yyyy-MM'));
		if (date.getTime() < str2Date(mark)) {
			date = str2Date(mark);
		}
		
		var now = new Date();
		if (str2Date(mark).NextYear().getTime() < now.getTime()) {
			now = str2Date(mark).NextYear();
		}
		var dateArr = [];
		for (; date.getTime() < now.getTime(); date.setMonth(date.getMonth() + 1)) {
			dateArr.push({
				txt: (date.getMonth() + 1) + '月',
				mark: date.Format('yyyy-MM'),
				value:str2Date(date.Format('yyyy-MM')).Format('yyyy-MM-dd hh:mm:ss') 
					+ '~' + str2Date(date.Format('yyyy-MM')).NextMonth().Format('yyyy-MM-dd hh:mm:ss'),
				startDate: startDate
			});
		}
		createDateHtml(dateArr, reportType, reportTypeArr[2], $('div[data-content-id="year_' + mark + '"]'));
	}
	
	// 日
	function showDay(mark, startDate, reportType) {
		var date = str2Date(str2Date(startDate).Format('yyyy-MM-dd'));
		if (date.getTime() < str2Date(mark)) {
			date = str2Date(mark);
		}
		
		var now = new Date();
		if (str2Date(mark).NextMonth().getTime() < now.getTime()) {
			now = str2Date(mark).NextMonth();
		}
		var dateArr = [];
		for (; date.getTime() < now.getTime(); date.setDate(date.getDate() + 1)) {
			dateArr.push({
				txt: date.getDate() + '号（周' + weekChineseArr[date.getDay()] + '）',
				mark: date.Format('yyyy-MM-dd'),
				value: str2Date(date.Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss') 
					+ '~' + str2Date(date.Format('yyyy-MM-dd')).NextDay().Format('yyyy-MM-dd hh:mm:ss'),
				startDate: startDate
			});
		}
		createDateHtml(dateArr, reportType, reportTypeArr[0], $('div[data-content-id="month_' + mark + '"]'));
	}
	
	// 周
	function showWeek(mark, startDate, reportType) {
		var dateArr = [];
		
		var date = str2Date(str2Date(startDate).Format('yyyy-MM-dd'));
		if (date.getTime() < str2Date(mark)) {
			date = str2Date(mark);
		}
		
		var now = new Date();
		if (str2Date(mark).NextMonth().getTime() < now.getTime()) {
			now = str2Date(mark).NextMonth();
		}
		
		var weekNum = date.getDay(); // 星期几
		
		// 第一周
		var index = getMonthWeek(date) - 1;
		dateArr.push({
			txt: '第' + chineseArr[index + 1] + '周',
			mark: (index + 1),
			value: str2Date(date.Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss') 
				+ '~' + str2Date(date.NextDay(8 - weekNum).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss'),
			startDate: startDate
		});
		date = date.NextDay(8 - weekNum);
		index++;
		
		for (; date.getTime() < now.getTime(); index++) {
			dateArr.push({
				txt: '第' + chineseArr[index + 1] + '周',
				mark: (index + 1),
				value: str2Date(date.Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss') 
					+ '~' + str2Date((date.NextDay(7).getTime() > now.getTime() ? now.NextDay(1) : date.NextDay(7)).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss'),
				startDate: startDate
			});
			date = date.NextDay(7);
		}
		createDateHtml(dateArr, reportType, reportTypeArr[1], $('div[data-content-id="month_' + mark + '"]'));
	}
	
	// 季
	function showSeason(mark, startDate, reportType) {
		var dateArr = [];
		var date = str2Date(mark);
		var sDate = str2Date(str2Date(startDate).Format('yyyy-MM-dd'));
		for (var i = 0; i < 4; i++) {
			date.setMonth(date.getMonth() + 3);
			if (date.getTime() > sDate.getTime() && date.getTime() < new Date().NextMonth(3).getTime()) {
				dateArr.push({
					txt: '第' + chineseArr[i + 1] + '季',
					mark: (i + 1),
					value: str2Date(getMonthDiffFirst(date, 3).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss') 
						+ '~' + str2Date(getMonthDiffFirst(date, -3).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss'),
					startDate: startDate
				});
			}
		}
		createDateHtml(dateArr, reportType, reportTypeArr[3], $('div[data-content-id="year_' + mark + '"]'));
	}
	
	// 半年
	function showHalfYear(mark, startDate, reportType) {
		var dateArr = [];
		var date = str2Date(mark);
		var sDate = str2Date(str2Date(startDate).Format('yyyy-MM-dd'));
		date.setMonth(date.getMonth() + 6);
		if (date.getTime() > sDate.getTime()) {
			dateArr.push({
				txt: '上半年',
				mark: 1,
				value: str2Date(getMonthDiffFirst(date, 6).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss') 
					+ '~' + str2Date(getMonthDiffFirst(date, -6).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss'),
				startDate: startDate
			});
		}
		if (date.getTime() < new Date().getTime()) {
			dateArr.push({
				txt: '下半年',
				mark: 2,
				value: str2Date(date.Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss') 
					+ '~' + str2Date(getMonthDiffFirst(date, -6).Format('yyyy-MM-dd')).Format('yyyy-MM-dd hh:mm:ss'),
				startDate: startDate
			});
		}
		createDateHtml(dateArr, reportType, reportTypeArr[4], $('div[data-content-id="year_' + mark + '"]'));
	}
	
	// ------------------------------------------查询显示汇报内容并批注-----------------------------------------------
	
	function queryUserReports(reportType, queryDate, thisEle, ordinary, callBack, selectDeptId) {
		openParams.queryDate = queryDate;
		openParams.reportType = reportType;
		var deptId = '';
		var userId = '';
		if (selectDeptId) {
			deptId = selectDeptId;
		} else {
			if ($('#deptAndUser > div > div > .my_active:first').length > 0) {
				var ele = $('#deptAndUser > div > div > .my_active:first');
				while($(ele).next().next().children().children().children('.my_active:first').length != 0) {
					ele = $(ele).next().next().children().children().children('.my_active:first');
				}
				if ($(ele).attr('data-my-opts-type') == 0) {
					deptId = $(ele).attr('data-my-id');
				} else if ($(ele).attr('data-my-opts-type') == 1) {
					userId = $(ele).attr('data-my-id');
				}
			}
		}
		$.ajax({
			type: "POST",
			url: "/userReport/queryUserReports?temp=" + Math.random(),
			data: {
				deptId: deptId,
				userId: userId,
				reportType: reportType,
				queryDate: queryDate,
				ordinary: ordinary ? true : false
			},
			dataType: "JSON",
			async: true,
			success: function (result) {
				if (result && result.code == 200 && result.data && result.data.length > 0) {
					var data = result.data;
					var filter = uuid();
					var contentItem = $('<div class="show-report-items" filter="' + filter + '"></div>');
					var itemClass = $(thisEle).attr('item-class');
					itemClass = itemClass === undefined || itemClass === null || itemClass === '' ? 0 : parseInt(itemClass);
					$(data).each(function (i, item) {
						var html = '';
						if (!item.blankReport) {
							html = '<div item-class="' + itemClass + '" class="show-report-item' + (item.leader ? '' :　' show-report-items-user') + '" report-id="' + item.id + '">'
								+ '<div class="show-report-item-content">'
								+ '<div class="show-report-item-user">'
								+ '<span class="show-report-item-user-name">' + item.userName + '：</span><span>' + new Date(item.wtime).Format('yyyy-MM-dd hh:mm:ss') + '</span>'
								+ (item.leader && !$('[data-my-tag="我的汇报"]').hasClass('my_active') ? '<span class="show-report-item-menu show-report-item-more" report-type="' + reportType + '" query-date="' + queryDate + '" dept-id="' + item.deptId + '" user-name="' + item.userName + '">查看部门人员</span>' : '')
								+ (item.modify ? '<span class="show-report-item-menu show-report-item-modify" report-id="' + item.id + '">修改</span>' : '')
								+ '</div>'
								+ '<div class="show-report-item-content-main">'
								+ '<div class="show-report-item-content-text" text-report-id="' + item.id + '">' + item.content + '</div>'
								+ '<div class="show-report-item-enclosure">';
							html += '<span>附件：</span>';
							html += createEnclosureHtml(item.enclosure)
							// show-report-item-enclosure
							html +='</div>';
							
							// show-report-item-content
							html += '</div>';
							
							html += createReportCommentHtml(item.comments, item.id);
							
							html += '<div class="show-report-item-comment-input"><span style="width: 70px;"></span><input name="reportComment" class="layui-input" placeholder="请输入评论内容，回车键保存"/><button style="margin: 0 5px;" type="button" name="commentSave" class="layui-btn layui-btn-xs unchecked-bill-file-tip">保存</button></div>';
							
							// show-report-item-content-main
							html += '</div>';
								
							// show-report-item
							html += '</div>';
							
							if (item.leader) {
								html += '<div item-class="' + (itemClass + 1) + '" class="show-report-items show-report-items-user show-no show-report-items-dept" user-id="' + item.userId + '" dept-id="' + item.deptId + '" user-name="' + item.userName + '"></div>';
							}
						} else { // 空报告
							html = '<div item-class="' + itemClass + '" class="show-report-item' + (item.leader ? '' :　' show-report-items-user') + '" report-id="' + '' + '">'
							+ '<div class="show-report-item-content">'
							+ '<div class="show-report-item-user">'
							+ '<span class="show-report-item-user-name">' + item.userName + '：</span><span>无工作汇报</span>'
							+ (item.leader && !$('[data-my-tag="我的汇报"]').hasClass('my_active') ? '<span class="show-report-item-menu show-report-item-more" report-type="' + reportType + '" query-date="' + queryDate + '" dept-id="' + item.deptId + '" user-name="' + item.userName + '">查看部门人员</span>' : '')
							+ '</div>'
							+ '</div>';
							html += '</div>';
							
							if (item.leader) {
								html += '<div item-class="' + (itemClass + 1) + '" class="show-report-items show-report-items-user show-no show-report-items-dept" dept-id="' + item.deptId + '" user-name="' + item.userName + '"></div>';
							}
						}
						contentItem.append(html);
					});
					contentItem.append('<div class="clear-both"></div>');
					thisEle.empty();
					thisEle.append(contentItem.prop('outerHTML'));
					// 绑定事件
					bindReportEvent(filter);
				} else {
					if (ordinary) {
						layer.msg('暂无汇报');
					} else {
						thisEle.empty();
						thisEle.append('<div class="report-no-data">暂无汇报</div>')
					}
				}
				if (callBack &&　typeof callBack == 'function') {
					callBack();
				}
			}
		});
	}
	
	// 附件html
	function createEnclosureHtml(enclosure) {
		var html = '';
		if (enclosure && enclosure.length > 0) {
			var arr = [];
			$(enclosure).each(function (ind, file) {
				arr.push((ind > 0 ? '<span style="margin-left: 42px;"></span>' : '')
						+ '<span class="enclosure-file-span" file="{&quot;fileName&quot;:&quot;' + file.fileName + '&quot;,&quot;filePath&quot;:&quot;' 
						+ file.filePath.replace(/\\/g, '\\\\') + '&quot;}"></span>'
						+ '<button style="margin: 0 5px;" title="点击下载" type="button" name="downloadFile" class="layui-btn layui-btn-xs unchecked-bill-file-tip" '
						+ 'onclick="downloadFile(this, {&quot;fileName&quot;: &quot;' + file.fileName + '&quot;, &quot;filePath&quot;: &quot;' 
						+ file.filePath.replace(/\\/g, '\\\\') + '&quot;})">' + file.fileName + '</button>'
						+ (typeMatchOfficeFile(file.fileName) ? ('<button style="margin: 0 5px;" title="点击预览" class="layui-btn layui-btn-xs unchecked-bill-file-tip" onclick=\"view_File(' 
								+ JSON.stringify({fileName: file.fileName, filePath: file.filePath}).replace(/"/g, '&quot;') + ')\">预览</button>') : '')
						);
			});
			html += arr.join('<br/>');
		}
		return html;
	}
	
	// 评论的html
	function createReportCommentHtml(comments, reportId, filter) {
		var html = '';
		if (comments && comments.length > 0) {
			$(comments).each(function (i, comment) {
				if (i == 0) {
					html += '<div><div class="show-report-item-show-comment"' + (filter ? (' filter="' + filter + '"') : '') + ' report-id="' 
						+ reportId + '"><div class="show-comment-item"><span style="">【评论】：</span>';
				} else {
					html += '<div class="show-comment-item">';
				}
				html += '<span class="report-comment-title"' + (i == 0 ? '' : ' style="margin-left: 70px;"') +'>'
					+ new Date(comment.wtime).Format('MM月dd号hh时mm分') + '&nbsp;' + comment.userName + '：</span>'
					+ '<span>&nbsp;&nbsp;' + comment.comment + '</span>'
					+ '</div>';
			});
			html += '</div>'; 
			html += '</div>';
		} else {
			html = '<div><div class="show-report-item-show-comment" report-id="' + reportId + '"><span style="">【评论】：暂无评论</span></div></div>';
		}
		return html;
	}
	
	// 汇报相关的点击事件
	function bindReportEvent(filter) {
		
		$('[filter="' + filter + '"] .show-report-item-more').on('click', function () { // 查看全部
			var deptId = $(this).attr('dept-id');
			var queryDate = $(this).attr('query-date');
			var reportType = $(this).attr('report-type');
			var userName = $(this).attr('user-name');
			if (!$(this).hasClass('show-report-item-more-active')) {
				var topDistance = $(this).offset().top;
				var windowHeight = $(window).height();
				var needScroll = windowHeight - topDistance < windowHeight / 2; // 当前元素不在中间偏上位置
				
				var yearTopScroll = $('#yearAndMonth [data-my-id="year"]:first').offset().top;
				var scrollDistance = topDistance - yearTopScroll - 20;
				queryUserReports(parseInt(reportType + ''), queryDate, $('.show-report-items-user[dept-id="' + deptId + '"]'), true, needScroll ? scrollFixedDistance(scrollDistance) : null, deptId);
			}
			$('.show-report-items-user[dept-id="' + deptId + '"][user-name="' + userName + '"]').toggleClass('show-no');
			$(this).toggleClass('show-report-item-more-active');
		});
		
		$('[filter="' + filter + '"] button[name="commentSave"]').on('click', function () { // 评论
			saveComment(this);
		});
		
		$('[filter="' + filter + '"] input[name="reportComment"]').keyup(function (event) {
			if(event.keyCode == 13){
				saveComment(this);
			}
		});
		
		$('[filter="' + filter + '"] .show-report-item-modify').on('click', function () { // 修改
			var reportId = $(this).attr('report-id');
			var reportContent = $(this).parents('.show-report-item').find('.show-report-item-content-text').html();
			
			var fileHtml = '';
			fileNames = [];
			uploadFiles = [];

			$(this).parents('.show-report-item').find('.enclosure-file-span').each(function (i, item) {
				var json = JSON.parse($(this).attr('file'));
				fileHtml += '<span style="margin-left: 15px;">' + json.fileName + '</span>'
        			+ '<button style="margin: 0 5px;" type="button" name="deleteFile" class="layui-btn layui-btn-xs unchecked-bill-file-tip">删除</button>';
				fileNames.push(json.fileName);
				uploadFiles.push(json);
			});
			
			$('#uploadReportFileList').html(fileHtml);
			
			$('#uploadReportFileList').find('button[name="deleteFile"]').unbind().bind('click', function () {
            	var fileName = $(this).prev().text();
            	var index = fileNames.indexOf(fileName);
                fileNames.splice(index, 1);
                uploadFiles.splice(index, 1);
                uploadListIns.config.elem.next()[0].value = ''; // 清空 input file 值，以免删除后出现同名文件不可选
                $(this).prev().remove();
                $(this).remove();
            });
			
			createTextComponent(reportContent);
			$('input#reportId').val(reportId);
			$('#saveReportButton').find('span').text('修改');
			$('#cancelReportButton').removeClass('show-no');
			if ($('#reportDisplaySpan').find('span').text() != '隐藏') {
				$('#reportDisplaySpan').trigger('click');
			}
		});
	}
	
	function saveComment(ele) {
		var reportId = $(ele).parents('.show-report-item').attr('report-id');
		var reportComment = $(ele).parents('.show-report-item').find('input[name="reportComment"]').val();
		if (!reportComment || !reportComment.trim()) {
			$(ele).parents('.show-report-item').find('input[name="reportComment"]').focus();
			layer.msg('先输入评论内容!');
		} else {
			$(ele).parents('.show-report-item').find('input[name="reportComment"]').val('');
			$.ajax({
				type: "POST",
				url: "/userReport/commentReport?temp=" + Math.random(),
				data: {
					reportId: reportId,
					isComment: false,
					reportComment: reportComment
				},
				dataType: "JSON",
				async: false,
				success: function (result) {
					if (!result) {
						layer.msg('保存评论失败!');
					} else {
						if (result.code == 200) {
							layer.msg('保存评论成功!');
							reflushComments(reportId);
						} else {
							layer.msg(result.msg);
						}
					}
				}
			});
		}
	}
	
	function reflushComments(reportId) { // 刷新评论
		$.ajax({
			type: "POST",
			url: "/userReport/queryReportCommentsByReportId?temp=" + Math.random(),
			data: {
				reportId: reportId
			},
			dataType: "JSON",
			async: false,
			success: function (result) {
				if (result && result.code == 200) {
					var html = '';
					var filter = uuid();
					if (result.data && result.data.length > 0) {
						html = createReportCommentHtml(result.data, reportId, filter);
					} else {
						html = createReportCommentHtml(null, reportId, filter);
					}
					var ele = $('.show-report-item-show-comment[report-id="' + reportId + '"]');
					$(ele).parent().html($(html).html());
					
					bindReportEvent(filter);
				}
			}
		});
	}
	
	function jumpSavePosition() {
		$('[data-my-tag="我的汇报"] span').trigger('click');
	}
	
	function queryDateHasReport(queryDate) {
		var type = parseInt($('#reportType input[name="reportType"]:checked').val());
		var deptId = '';
		var userId = '';
		if ($('#deptAndUser > div > div > .my_active:first').length > 0) {
			var ele = $('#deptAndUser > div > div > .my_active:first');
			while($(ele).next().next().children().children().children('.my_active:first').length != 0) {
				ele = $(ele).next().next().children().children().children('.my_active:first');
			}
			if ($(ele).attr('data-my-opts-type') == 0) {
				deptId = $(ele).attr('data-my-id');
			} else if ($(ele).attr('data-my-opts-type') == 1) {
				userId = $(ele).attr('data-my-id');
			}
		}
		$.ajax({
			type: "POST",
			url: "/userReport/queryHasReport?temp=" + Math.random(),
			data: {
				type: type,
				deptId: deptId,
				userId: userId,
				queryDate: queryDate
			},
			dataType: "JSON",
			async: true,
			success: function (result) {
				var ele = null;
				if (queryDate) {
					ele = $('[data-my-tag="' + queryDate + '"]').next().next();
				} else {
					ele = $('#yearAndMonth');
				}
				if (result && result.code == 200 && result.data && result.data.length > 0) {
					var data = result.data;
					var arr = $(ele).find('.my_span_title');
					if (arr.length > 0) {
						var j = 0
						for (var i = 0; i < arr.length; i++) {
							var flag = false;
							for (; j < data.length; j++) {
								var dataMyTag = $(arr[i]).attr('data-my-tag');
								var timeArr = dataMyTag.split('~');
								var sTime = str2Date(timeArr[0]);
								var eTime = str2Date(timeArr[1]);
								if (sTime.getTime() <= str2Date(data[j]) 
										&& str2Date(data[j]) < eTime.getTime()) {
									flag = true;
									if ($(arr[i]).find('.layui-extent-report-tips').length == 0 
											&& $(arr[i]).find('.layui-extent-report-tips-blank').length == 0) {
										$(arr[i]).find('span').before('<i class="layui-icon-extend layui-extent-report-tips"></i>');
									}
								} else if (str2Date(data[j]) >= eTime.getTime()) {
									break;
								}
							}
							if (!flag) {
								if ($(arr[i]).find('.layui-extent-report-tips').length == 0 
										&& $(arr[i]).find('.layui-extent-report-tips-blank').length == 0) {
									$(arr[i]).find('span').before('<i class="layui-icon-extend layui-extent-report-tips-blank"></i>');
								}
							}
						}
					}
				} else {
					$(ele).find('.my_span_title').each(function (i, item) {
						if ($(this).find('.layui-extent-report-tips').length == 0 
								&& $(this).find('.layui-extent-report-tips-blank').length == 0) {
							$(this).find('span').before('<i class="layui-icon-extend layui-extent-report-tips-blank"></i>');
						}
					});
				}
			}
		});
		selectYearOrMonth = '';
	}
	
	function reflushHasReport() {
		queryDateHasReport(queryHasReportDate);
	}
});

//文件下载
function downloadFile(ele, file) {
	if (typeof file == 'object') {
		 var fileParams = "filePath=" + encodeURIComponent(file.filePath) + "&fileName=" + encodeURIComponent(file.fileName) + "&r=" + Math.random();
		 window.location.href = "/operate/downloadFile?" + fileParams;
	}
}