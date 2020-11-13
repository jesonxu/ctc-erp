layui.use(['layer', 'element', 'laydate', 'form', 'layedit'], function () {
	let layer = layui.layer,
		element = layui.element,
		laydate = layui.laydate,
		form = layui.form,
		layedit = layui.layedit,
		types = ['日报', '周报', '月报', '季报', '半年报', '年报'];
	
	var addReportTextIndex = layedit.build('reportContent', {
		tool: []
	});
	
	var reportType = $('#reportType').val();
	$('.page-title-name').text('编写' + types[reportType]);
	
	// 附件
    labelRender = new FileLabel('add-report-enclosure', '附件', 8, '/userReport/uploadFile');
    labelRender.render($('.report-enclosure'), null, false);
	
	var lastSubmit = 0;
	$('#submitReport').unbind().bind('click', function () {
		var content = layedit.getContent(addReportTextIndex);
		var reportId = $('#reportId').val();
		if (!content) {
			return layer.msg('请填写汇报');
		}
		
		if (new Date().getTime() - lastSubmit < 10 * 1000 && !reportId) {
			return layer.msg('操作过于频繁');
		}
		
		var files = [];
		var uploadFiles = labelRender.getValue();
		if (uploadFiles && uploadFiles.length > 0) {
			$(uploadFiles).each(function (i, item) {
				files.push(item.fileName + "," + item.filePath);
			});
		}
		
		$.ajax({
			url: '/userReport/saveUserReport?temp=' + Math.random(),
			type: 'POST',
			async: true,
			data: {
				reportId: reportId,
				content: content,
				reportType: $('#reportType').val(),
				files: files.join(';')
			},
			success: function (data) {
				if (data.code == 200) {
					if (data.data) {
						$('#reportId').val(data.data);
					}
					return layer.msg('提交成功');
				} else {
					return layer.msg('提交失败');
				}
			}
		})
		
	});
});