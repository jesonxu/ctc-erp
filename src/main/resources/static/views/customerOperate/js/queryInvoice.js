var tableIns;
$(document).ready(function() {
	initTable();
});


function initTable() {
	var productId = $("#productId").val().trim();
	var flowClass = $("#flowClass").val().trim();
	var clos = [];
	if(flowClass === '[BillReceivablesFlow]') { // 账单收款流程
		clos = [[ {
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
			title : '发票信息',
			align : 'center',
			width : 160
		}, {
			field : 'receivables',
			title : '发票金额',
			align : 'right',
			width : 120
		}, {
			field : 'actualReceivables',
			title : '已收金额',
			align : 'right',
			minWidth : 120
		} ]];
	}
	layui.use([ 'table', 'form' ], function() {
		var table = layui.table;
		var form = layui.form;
		tableIns = table.render({
			elem : '#invoicelist',
			url : "/customerOperate/readInvoices.action?temp=" + Math.random(),
			height : 'full-50',
			even : true,
			page : false,
			method : 'POST',
			data : [],
			cols : clos,
			parseData : function(res) { // res 即为原始返回的数据
				return {
					"code" : 0, // 解析接口状态
					"count" : res.count, // 解析数据长度
					"data" : res.data
				// 解析数据列表
				};
			},
			where : {
				productId : productId,
				flowClass : flowClass
			}
		});
	});
}
