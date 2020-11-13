//注意：parent 是 JS 自带的全局对象，可用于操作父页面
var index = window.parent.layer.getFrameIndex(window.name);
var layer;
var table;
var laydate;
// 加载中遮罩
var loadingIndex;
// 总量
layui.use(['layer', 'form', 'element', 'table', 'laydate'], function () {
    table = layui.table;
    layer = layui.layer;
    laydate = layui.laydate;
    loadingIndex = layer.load(2);
    initDate();
    initButton();
    initTable();
});

/**
 * 加载已经导入的表格数据
 */
function initTable() {
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#income',
        height: 'full-240',
        url: '/fsExpenseIncome/queryExpenseIncomePage.action',
        page: true,
        limit: 15,
        request: {
            pageName: 'page',
            limitName: 'pageSize'
        },
        method: 'POST',
        limits: [15, 30, 50, 100],
        where: getParam(),
        cols: [[{
            type: 'radio'
        }, {
            field: 'id',
            hide: true
        }, {
            field: 'operateTime',
            title: '到款时间',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'serialNumber',
            title: '流水号',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'bankName',
            title: '银行',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize
        }, {
            field: 'creatorName',
            title: '销售',
            align: 'center',
            style: fontSize
        }, {
            field: 'depict',
            title: '摘要',
            align: 'center',
            style: fontSize
        }, {
            field: 'customerName',
            title: '关联客户',
            align: 'center',
            style: fontSize
        }, {
            field: 'cost',
            title: '到款',
            align: 'center',
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
            title: '可核销金额',
            align: 'center',
            style: fontSize
        }
        ]],
        done: function () {
            // 取消加载中遮罩
            layer.close(loadingIndex);
            $("th[data-key='1-0-0']").find("i[class='layui-icon layui-icon-ok']").remove();
        }
    });
    
    table.on('row(income)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });
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

function initButton() {

    // 确定
    $("#ok_btn").click(function (e) {
        var checkStatus = table.checkStatus('income');
        if (checkStatus.data.length < 1){
            return layer.msg("请选择到款", {icon: 2});
        } else if (checkStatus.data.length > 1) {
            return layer.msg("请选择一条到款", {icon: 2});
        } else {
            var rowData = checkStatus.data[0];
            layer.confirm('确定用这笔到款的剩余金额【' + rowData.remainCheckOut + '】核销充值记录吗？', {
                btn: ['确认', '取消'],
                icon: 3,
                title: '确认'
            }, function () {
                $.ajax({
                    type: "POST",
                    url: "/chargeRecord/checkOut.action",
                    dataType: "json",
                    data: {
                        chargeRecordId: $('#chargeRecordId').val(),
                        incomeId: rowData.id
                    },
                    success: function (data) {
                        parent.layer.msg(data.msg, {time: 4000});
                        if (data.code == 200) {
                            parent.layer.close(index);
                            if (typeof parent.search == 'function') {
                                parent.search();
                            }
                        }
                    }
                });
            });
        }
    });


    // 取消
    $("#cancel_btn").click(function (e) {
        parent.layer.close(index);
    });

    $("#search").click(function () {
        search();
    });

    $("#reset").click(function () {
        $("#companyName").val('');
        initDate();
    });
}

function search() {
    table.reload('income', {
        url: '/fsExpenseIncome/queryExpenseIncomePage.action',
        where: getParam()
    });
}

function getParam() {
    return {
        companyName: $('#companyName').val(),
        startTime: $('#startDate').val() + ' 00:00:00',
        endTime: $('#endDate').val() + ' 23:59:59',
        checkOut: '0,1'
    }
}