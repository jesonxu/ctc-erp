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
			title : '账单信息',
			align : 'center',
			width : 160
		}, {
			field : 'receivables',
			title : '应收金额',
			align : 'right',
			width : 120
		}, {
			field : 'actualReceivables',
			title : '实收金额',
			align : 'right',
			minWidth : 120
		} ]];
	} else if (flowClass === '[InvoiceFlow]') {
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
			title : '账单信息',
			align : 'center',
			width : 160
		}, {
			field : 'receivables',
			title : '应开金额',
			align : 'right',
			width : 120
		}, {
			field : 'actualInvoiceAmount',
			title : '实开金额',
			align : 'right',
			minWidth : 120
		} ]];
	}
	layui.use([ 'table', 'form' ], function() {
		var table = layui.table;
		var form = layui.form;
		tableIns = table.render({
			elem : '#accoutlist',
			url : "/customerOperate/readProductBills.action?temp=" + Math.random(),
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
