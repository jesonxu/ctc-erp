var layer;
var laydate;
var element;
var form;
// 加载中遮罩
var loadingIndex;
var table;
var insStart;
var insEnd;

$(document).ready(function () {
    layui.use(['layer', 'laydate', 'form', 'element', "table"], function () {
        table = layui.table;
        layer = layui.layer;
        laydate = layui.laydate;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        initButton();
        initDate();
        initTable();
    });
})

/**
 * 加载已经导入的表格数据
 */
function initTable() {
    var checkOut = [];
    $('input[name=checkOut]').each(function () {
        if ($(this).prop('checked')) {
            checkOut.push($(this).val());
        }
    })
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#charge-record',
        height: 'full-150',
        url: '/chargeRecord/readPages.action',
        page: true,
        limit: 15,
        limits: [15, 30, 50, 100],
        where: {
            companyName: $('#companyName').val(),
            realName: $('#realName').val(),
            startDate: $("#startDate").val(),
            endDate: $("#endDate").val(),
            checkOut: checkOut.join(','),
        },
        cols: [[{
            type: 'radio'
        }, {
            field: 'id',
            hide: true
        }, {
            field: 'wtime',
            title: '充值时间',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'entityId',
            hide: true
        }, {
            field: 'companyName',
            title: '公司名称',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'ossUserId',
            hide: true
        }, {
            field: 'realName',
            title: '发起人',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize
        }, {
            field: 'chargeType',
            title: '充值类型',
            align: 'center',
            style: fontSize
        }, {
            field: 'account',
            title: '账号',
            align: 'center',
            style: fontSize
        }, {
            field: 'price',
            title: '单价',
            align: 'right',
            style: fontSize
        }, {
            field: 'chargePrice',
            title: '充值金额',
            align: 'right',
            style: fontSize
        }, {
            field: 'checkOut',
            title: '核销状态',
            align: 'center',
            style: fontSize,
            templet: function (res) {
                if (res.checkOut === '0' || res.checkOut === 0) {
                    return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未核销 </div>';
                } else if (res.checkOut === '1' || res.checkOut === 1) {
                    return '<div style = "background-color:#FFB800;margin:1px;padding:1px;height:100%;width:100%;color: white"> 部分核销 </div>';
                } else if (res.checkOut === '2' || res.checkOut === 2) {
                    return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已核销 </div>';
                } else {
                    return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                }
            }
        }, {
            field: 'remainCheckOut',
            title: '未核销金额',
            align: 'right',
            style: fontSize
        }, {
            field: 'finalReceiveTime',
            title: '最后收款时间',
            align: 'right',
            style: fontSize
        }, {
            field: 'actualReceiveTime',
            title: '实际收款时间',
            align: 'right',
            style: fontSize
        }, {
            field: 'remark',
            title: '备注',
            align: 'center',
            style: fontSize
        }, {
            title: '其他操作',
            align: 'center',
            minWidth: 150,
            toolbar: '#table-row-opts'
        }]],
        parseData: function (res) {
            return {
                "code": 0, // 解析接口状态
                "count": res.data.count, // 解析数据长度
                "data": res.data.data
            }
        },
        done: function () {
            // 取消加载中遮罩
            layer.close(loadingIndex);
        }
    });

    table.on('row(charge-record)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    table.on('tool(charge-record)', function (obj) {
        var data = obj.data;
        if(obj.event === 'revert'){
            layer.confirm('确定还原充值记录到未核销状态？将会还原用于核销的到款', function(){
                $.post("/chargeRecord/revertCheckOut.action?chargeRecordId=" + data.id + "&temp=" + Math.random(), function (res) {
                    layer.msg(res.msg);
                    search();
                });
            });
        } else if(obj.event === 'forceCheckOut') {
            layer.confirm('确定要强制核销充值记录？将不会关联到款', function(){
                $.post("/chargeRecord/forceCheckOut.action?chargeRecordId=" + data.id + "&temp=" + Math.random(), function (res) {
                    layer.msg(res.msg);
                    search();
                });
            });
        } else if (obj.event === 'detail') {
            layer.open({
                type: 2,
                area: ['100%', '100%'],
                fixed: false,
                title: '充值记录核销详情',
                content: '/chargeRecord/toCheckOutDetail?id=' + data.id
            })
        }
    })
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] == 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}

function initDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = (date.getMonth() + 1) + "";
    if (month.length === 1) {
        month = "0" + month;
    }
    // 本月第一天日期
    var begin = year + "-" + month + "-01";
    $("#startDate").val(begin);
    var day = date.getDate() + "";
    if (day.length === 1) {
        day = "0" + day;
    }
    var end = year + "-" + month + "-" + day;
    $("#endDate").val(end);

    var min = year - 2 + '-01-01';

    insStart = laydate.render({
        elem: '#startDate',
        format: 'yyyy-MM-dd',
        type: 'date',
        min: min,
        max: end,
        value:  begin,
        trigger: 'click',
        done: function (value, date) {
            // 更新结束日期的最小日期
            insEnd.config.min = lay.extend({}, date, {
                month: date.month - 1
            });
            // 自动弹出结束日期的选择器
            insEnd.config.elem[0].focus();
        }
    });

    insEnd = laydate.render({
        elem: '#endDate',
        min: min,
        max: end,
        value: end,
        trigger: 'click',
        done: function (value, date) {
            // 更新开始日期的最大日期
            insStart.config.max = lay.extend({}, date, {
                month: date.month - 1
            });
        }
    });
}

// 按钮绑定
function initButton() {
    // 搜索
    $("#search").unbind().click(function () {
        search();
    });

    // 重置
    $("#reset").unbind().click(function () {
        insStart = "";
        insEnd = "";
        initDate();
        $("#companyName").val("");
        form.render();
        search();
    });

    // 核销
    $("#checkOut").unbind().click(function () {
        var checkStatus = table.checkStatus('charge-record');
        var checkedData = checkStatus.data;
        if (checkedData == null || checkedData.length === 0){
            layer.msg("请选择一条充值记录");
            return;
        } else if (checkedData.length > 1) {
            layer.msg("只能选择一条充值记录");
            return;
        }
        var checkOut = checkedData[0].checkOut;
        var remainCheckOut = format_num(checkedData[0].remainCheckOut, 2);
        if (checkOut === '2' || checkOut === 2 || '0.00'.equals(remainCheckOut)) {
            layer.msg("这条记录已经核销完，请先还原");
            return;
        }
        var id = checkedData[0].id;
        layer.open({
            type: 2,
            area: ['100%', '100%'],
            fixed: false,
            title: '充值记录核销',
            content: '/chargeRecord/toChargeRecordCheckOut?id=' + id
        });
    })
}

function search() {
	var checkOut = [];
	$('input[name=checkOut]').each(function () {
	    if ($(this).prop('checked')) {
	        checkOut.push($(this).val());
        }
    })
	if(checkOut.length < 1) {
		layer.msg("请选择核销状态！");
		return false;
	}
    table.reload('charge-record', {
        where: {
            companyName: $('#companyName').val(),
            realName: $('#realName').val(),
            startDate: $("#startDate").val(),
            endDate: $("#endDate").val(),
            checkOut: checkOut.join(','),
        }
    });
}


