layui.use(['layer'], function(){
	
	var layer = layui.layer;
	var totalHeight = parent.$('#content-main').height() - 4;
	
	$('.personal-center-container').height(totalHeight);
	$('#contentMain').height(totalHeight);

	initCount();
	initBindEvent();
	
	function initBindEvent() {
		$('.pc-menu-group-detail > .pc-menu-group-detail-item').bind('click', function () {
			$('.pc-menu-group-detail > .pc-menu-group-detail-item').removeClass('pc-menu-group-detail-item-actived');
			$(this).addClass('pc-menu-group-detail-item-actived');
		});
	}
	
	$('#contentMain iframe').width($('#contentMain').width() - 8);
});

function logout() {
	location.href = '/erp';
}

function jumpMenuContentPage(ele) {
	var pageUrl = $(ele).attr('page-url');
	$('#contentMain iframe').attr('src', pageUrl);
}

function jumpUseDocPage() {
	window.open("/common/help/ERP_HELP.pdf");
}

function jumpFAQPage() {
	window.open("/login/faq");
}

function jumpUserReportPage() {
	window.open("/personalCenter/toUserReportPage");
}