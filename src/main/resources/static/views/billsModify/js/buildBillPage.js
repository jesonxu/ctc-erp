var layer;
var element;
var form;
var laydate;
var formSelects;
var doing; // 防止重复点击
var loading; // 加载中遮罩

$(document).ready(function () {
	layui.use(['layer', 'form', 'element', 'laydate'], function () {
		layer = layui.layer;
		element = layui.element;
		form = layui.form;
		laydate = layui.laydate;
		formSelects = layui.formSelects;

		var customerId = $('#customerId').val();
		initProductSelect(customerId);
		initDate();
		initButton();
	})
})

function initProductSelect(customerId) {
	// 设置产品下拉框
	formSelects.data('product', 'server', {
		url: '/customerProduct/getProductSelect?customerId=' + customerId,
		linkageWidth: 80
	});
}

function initDate() {
	var today = new Date();
	var year = today.getFullYear();
	var month = today.getMonth() + 1; // 自然月
	var maxValue = year + "-" + month
	// 默认上月
	month -= 1;
	if (month === 0) {
		year -= 1;
		month = 12;
	}
	if (month < 10) {
		month = '0' + month;
	}
	var yearMonth = year + '-' + month;
	laydate.render({
		elem: '#billMonth',
		type: 'month',
		trigger: 'click',
		value: yearMonth,
		max: maxValue
	});
}

function initButton() {
	$('#submit').unbind().bind('click', function () {
		if (doing) {
			return;
		}
		var productIds = formSelects.value('product', 'valStr');
		var billMonth = $('#billMonth').val();
		if (isBlank(productIds) || isBlank(billMonth)) {
			layer.msg('请选择产品和月份');
			return;
		}
		doing = true;
		loading = layer.load(2);
		$('#submit').attr({"disabled":"disabled"});
		$.post('/bill/buildBill', {productIds: productIds, billMonth: billMonth, redo: 'false'}, function (res) {
			doing = false;
			$('#submit').removeAttr("disabled");
			if (res.code == 200) {
				parent.layer.msg("账单生成成功<br/>" + res.msg, {time: 5000});
			} else {
				parent.layer.msg(res.msg, {time: 5000});
			}
			layer.close(loading);
		})
	});

	$('#cancel').unbind().bind('click', function () {
		var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		parent.layer.close(index);
	});
}