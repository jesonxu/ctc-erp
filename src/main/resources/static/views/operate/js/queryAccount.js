var tableIns;
$(document).ready(function() {
	initTable();
});


function initTable() {
	var productId = $("#productId").val().trim();
	var flowClass = $("#flowClass").val().trim();
	var clos = [];
	if(flowClass == '[BillPaymentFlow]') {
		clos = [[ {
			field : 'checked',
			title : '选择',
			type : 'checkbox',
			width : 50,
		},{
			field : 'id',
			title : 'id',
			align : 'center',
			hide : true
		},{
			field : 'title',
			title : '账单信息',
			align : 'center',
			width : 160,
		}, {
			field : 'payables',
			title : '应付金额',
			align : 'right',
			width : 120,
		}, {
			field : 'actualpayables',
			title : '实付金额',
			align : 'right',
			minWidth : 120,
		} ]];
	} else if(flowClass == '[RemunerationFlow]') {
		clos = [[ {
			field : 'checked',
			title : '选择',
			type : 'checkbox',
			width : 50,
		},{
			field : 'id',
			title : 'id',
			align : 'center',
			hide : true
		},{
			field : 'title',
			title : '账单信息',
			align : 'center',
			width : 160,
		}, {
			field : 'receivables',
			title : '应收佣金',
			align : 'right',
			width : 120,
		}, {
			field : 'actualReceivables',
			title : '实收佣金',
			align : 'right',
			minWidth : 120,
		} ]];
	}
	layui.use([ 'table', 'form' ], function() {
		var table = layui.table;
		var form = layui.form;
		tableIns = table.render({
			elem : '#accoutlist',
			url : "/operate/readProductBills.action?temp=" + Math.random(),
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
				flowClass : flowClass,
			}
		});
	});
}
