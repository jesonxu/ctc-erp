var tableIns;
var table;
var searchStartDate;
var searchDate;
var searchType;
var searchContent
$(document).ready(function () {
    searchType = $('#searchType').val();
    searchContent = $('#searchContent').val();
    var date = new Date();
    // searchType：1流程，2客户，3供应商，4合同，5账单
    // 账单默认搜索上月
    if (searchType == 5) {
    	date = date.NextMonth(-1);
    }
    searchStartDate = date.Format('yyyy-MM');
    searchDate = date.Format('yyyy-MM');
    initTable(searchType, searchContent, searchStartDate, searchDate);
    $("#return-workbench").click(function(){
        var select_lay_href =$(window.parent.document).find("ul.layui-nav .layui-this .erp-menu-a").attr('lay-href');
        $(window.parent.document).find("#demoAdmin").attr("src", select_lay_href);
    })

    $(document).on("click","#dataExport",function(){
        tableExport();
    })
});

//切换年份按钮
function initChangeYear() {
    var yearMonth = new Date().Format('yyyy-MM');
    layui.use(['laydate'], function () {
        var laydate = layui.laydate;
        var insStart = laydate.render({
            elem: '#year2',
            type: 'month',
            trigger: 'click',
            max: yearMonth, // 最大值为今年今月
            format: "yyyy-MM",
            done: function (value, date) {
                $('#year2').html(value);
                searchStartDate = value;
                search();
            }
        });
        var insEnd = laydate.render({
            elem: '#year',
            type: 'month',
            trigger: 'click',
            max: yearMonth, // 最大值为今年今月
            format: "yyyy-MM",
            done: function (value, date) {
                $('#year').html(value);
                searchDate = value;
                search();
            }
        });
    });
}

function initTable(searchType, searchContent, searchStartDate, searchDate) {
    layui.use(['table'], function () {
        table = layui.table;
        var cols = getCols(searchType);
        firseOpen = false;
        tableIns = table.render({
            elem: '#searchData',
            url: "/search/searchContent.action?temp=" + Math.random(),
            height: 'full-120',
            id : 'searchData',
            toolbar: '#toolbarDemo',
            defaultToolbar: [],
            even: true,
            page: true,
            fixed: true,
            method: 'GET',
            data: [],
            cols: cols,
            parseData: function (res) { // res 即为原始返回的数据
                return {
                    "code": 0, // 解析接口状态
                    "count": res.data.count, // 解析数据长度
                    "data": res.data.data
                    // 解析数据列表
                };
            },
            where: {
                searchType: searchType,
                searchContent: searchContent,
                searchStartDate: searchStartDate,
                searchDate: searchDate
            },
            done: function (res, curr, count) {
                if(searchType != 2 && searchType != 3) {
                    $('#year2').html(searchStartDate);
                    $('#year').html(searchDate);
                    initChangeYear();
                }
            }
        });
        table.on('row(searchData)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });
    });
}

function tableExport() {
	if (searchType == 1) {
		layer.open({
			title: '导出 ',
			type: 1,
			content: $("#selectFlow"),
			btn: ['确定', '取消'],
			area: ['600px', '500px'],
			yes: function(index, layero){
				doExport();
				layer.close(index);
			}
		});
	} else {
		doExport();
	}
}

function doExport() {
	$.ajax({
		type: "GET",
		url: "/search/exportFsExpenseIncome.action",
		data: {
			searchType: searchType,
			searchContent: searchContent,
			searchStartDate: searchStartDate,
			searchDate: searchDate,
			flowId: $('#erpFlow').val()
		},
		success: function (data) {
			if(data.code == 500) {
				layer.msg(data.msg);
			}else if(data.code == 200){
				down_load(data.data);
			}
		}
	});
}

//下载
function down_load(file_info) {
    //console.log("下载文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.location.href = "/operate/downloadFile?" + file_params;
}

function search() {
    if (!isNull(tableIns)) {
        table.reload('searchData',{
            url: "/search/searchContent.action?temp=" + Math.random(),
            method: 'GET',
            where: {
                searchType: searchType,
                searchContent: searchContent,
                searchStartDate: searchStartDate,
                searchDate: searchDate
            },
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,done: function(res, curr, count){
                if(searchType != 2 && searchType != 3) {
                    $('#year2').html(searchStartDate);
                    $('#year').html(searchDate);
                    initChangeYear();
                }
            }
        });
    }
}

function getCols(searchType) {
    var cols = [];
    if (1 == searchType) {
        cols = flowEntCols();
    } else if (2 == searchType) {
        cols = custCols();
    } else if (3 == searchType) {
        cols = supplierCols();
    } else if (4 == searchType) {
        cols = contractCols();
    } else if (5 == searchType) {
        cols = billCols();
    }
    return cols;
}

function billCols() {
    var cols = [[{
        field: 'checked',
        title: '序号',
        type: 'numbers',
        width: 50
    }, {
        field: 'billNumber',
        title: '账单编号',
        align: 'center',
        width: 150
    }, {
        field: 'entityName',
        title: '名称',
        align: 'center',
        width: 120
    }, {
        field: 'entityType',
        title: '主体类型',
        align: 'center',
        width: 80
    }, {
        field: 'productName',
        title: '产品名称',
        align: 'center',
    }, {
        field: 'supplierCount',
        title: '供应商成功数',
        align: 'center',
        width: 80,
        format: function (data) {
            return thousand(data.supplierCount);
        }
    }, {
        field: 'platformCount',
        title: '平台成功数',
        align: 'center',
        width: 80,
        format: function (data) {
            return thousand(data.platformCount);
        }
    }, {
        field: 'receivables',
        title: '应收金额',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.receivables);
        }
    }, {
        field: 'actualReceivables',
        title: '实收金额',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.actualReceivables);
        }
    }, {
        field: 'payables',
        title: '应付金额',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.payables);
        }
    }, {
        field: 'actualPayables',
        title: '实付金额',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.actualPayables);
        }
    }, {
        field: 'actualInvoiceAmount',
        title: '已开票金额',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.actualInvoiceAmount);
        }
    }, {
        field: 'cost',
        title: '成本',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.cost);
        }
    }, {
        field: 'unitPrice',
        title: '平均成本单价',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.unitPrice);
        }
    }, {
        field: 'grossProfit',
        title: '毛利润',
        align: 'right',
        width: 80,
        format: function (data) {
            return thousand(data.grossProfit);
        }
    }, {
        field: 'finalPayTime',
        title: '支付截止日期',
        align: 'center',
        width: 150
    }, {
        field: 'finalReceiveTime',
        title: '收款截止日期',
        align: 'center',
        width: 150
    }]];
    return cols;
}

function contractCols() {
    var cols = [[{
        field: 'checked',
        title: '序号',
        type: 'numbers',
        width: 50
    }, {
        field: 'contractId',
        title: '合同编号',
        align: 'center',
        width: 120
    }, {
        field: 'contractName',
        title: '合同名称',
        align: 'center',
        width: 120
    }, {
        field: 'status',
        title: '合同状态',
        align: 'center',
        width: 80
    }, {
        field: 'ossUserName',
        title: '申请人',
        align: 'center',
        width: 120
    }, {
        field: 'deptName',
        title: '申请人部门',
        align: 'center',
        width: 80
    }, {
        field: 'entityName',
        title: '客户/供应商名称',
        align: 'center',
        width: 120
    }, {
        field: 'entityRegion',
        title: '客户/供应商区域',
        align: 'center',
        width: 120
    }, {
        field: 'contractRegion',
        title: '合同归属区域',
        align: 'center',
        width: 120
    }, {
        field: 'contactName',
        title: '联系人',
        align: 'center',
        width: 120
    }, {
        field: 'contactPhone',
        title: '联系方式',
        align: 'center',
        width: 120
    }, {
        field: 'address',
        title: '联系地址',
        align: 'center',
    }, {
        field: 'contractType',
        title: '合同类型',
        align: 'center',
        width: 60
    }, {
        field: 'productType',
        title: '产品类型',
        align: 'center',
        width: 60
    }, {
        field: 'settleType',
        title: '付费方式',
        align: 'center',
        width: 60
    }, {
        field: 'monthCount',
        title: '月发送量',
        align: 'center',
        width: 60
    }, {
        field: 'contractAmount',
        title: '合同金额',
        align: 'center',
        width: 60
    }, {
        field: 'price',
        title: '单价',
        align: 'center',
        width: 60
    }, {
        field: 'projectLeader',
        title: '项目负责人',
        align: 'center',
        width: 60,
    }, {
        field: 'validityDateStart',
        title: '开始日期',
        align: 'center',
        width: 150
    }, {
        field: 'validityDateEnd',
        title: '结束日期',
        align: 'center',
        width: 150
    }, {
        field: 'description',
        title: '项目情况说明',
    }, {
        field: 'wtime',
        title: '申请时间',
        align: 'center',
        width: 150
    }]];
    return cols;
}
//获取供应商展示信息
function supplierCols() {
    var cols = [[{
        field: 'checked',
        title: '序号',
        type: 'numbers',
        width: 50
    }, {
        field: 'companyName',
        title: '供应商名称',
        align: 'center',
    }, {
        field: 'supplierType',
        title: '供应商类型',
        align: 'center',
        width: 120
    }, {
        field: 'contactName',
        title: '业务联系人',
        align: 'center',
        width: 120
    }, {
        field: 'contactPhone',
        title: '移动电话',
        align: 'center',
        width: 120
    }, {
        field: 'postalAdress',
        title: '地址',
        align: 'center',
    }, {
        field: 'createUser',
        title: '创建人',
        align: 'center',
        width: 120
    }, {
        field: 'witme',
        title: '创建时间',
        align: 'center',
        width: 150
    }]];
    return cols;
}

//获取客户展示信息
function custCols() {
    var cols = [[{
        field: 'checked',
        title: '序号',
        type: 'numbers',
        width: 50
    }, {
        field: 'companyName',
        title: '客户名称',
        align: 'center',
    }, {
        field: 'region',
        title: '城市',
        align: 'center',
        width: 120
    }, {
        field: 'email',
        title: '电子邮件',
        align: 'center',
        width: 240
    }, {
        field: 'customerType',
        title: '客户类型',
        align: 'center',
        width: 80
    }, {
        field: 'createUser',
        title: '负责销售',
        align: 'center',
        width: 160
    }, {
        field: 'contactName',
        title: '姓名',
        align: 'center',
        width: 160
    }, {
        field: 'contactPhone',
        title: '移动电话',
        align: 'center',
        width: 120
    }, {
        field: 'witme',
        title: '创建时间',
        align: 'center',
        width: 150
    }]];
    return cols;
}
//获取流程展示的列
function flowEntCols() {
    var cols = [[{
        field: 'checked',
        title: '序号',
        type: 'numbers',
        width: 50
    }, {
        field: 'wtime',
        title: '创建时间',
        align: 'center',
        width: 100
    }, {
        field: 'createUser',
        title: '创建人',
        align: 'center',
        width: 70
    }, {
        field: 'flowTitle',
        title: '流程标题',
        align: 'center',
        width: 250
    },  {
        field: 'flowMsg',
        title: '流程信息',
        align: 'left',
        templet: function (rowData) {
        	if (!rowData.labelValue) {
        		return '';
        	} else {
        		var labels = [];
        		$(rowData.labelValue).each(function (i, item) {
        			labels.push('<div style="width: 101%;">' + item.key + '：' + item.value + '</div>');
        		});
        		return labels.join('');
        	}
        }
    }, {
        field: 'nodeName',
        title: '当前节点',
        align: 'center',
        width: 80
    }, {
        field: 'receiveTime',
        title: '接收日期',
        align: 'center',
        width: 100
    }, {
        field: 'nowStatus',
        title: '当前状态',
        align: 'center',
        width: 70
    }]];
    return cols;
}
