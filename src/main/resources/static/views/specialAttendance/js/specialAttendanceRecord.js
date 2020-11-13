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
        initSelect();
        initDate();
        initTable();
    });
})

/**
 * 加载已经导入的表格数据
 */
function initTable() {
    var validCheck = $("input[name='valid']:checked");
    if (validCheck.length === 0) {
        layer.msg('请勾选是否有效状态');
        return;
    }
    var valid = [];
    $(validCheck).each(function (index, item) {
        valid.push($(item).val());
    });
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#record',
        height: 'full-130',
        url: '/specialAttendance/querySpecialAttendanceRecord.action',
        where: {
            startDate: $("#startDate").val(),
            endDate: $("#endDate").val(),
            ossUserId: $('#ossUserId').val(),
            keyword: $('#keyword').val(),
            deptId: $('#deptId').val(),
            type: $("#typeSelect").val(),
            valid: valid.join(',')
        },
        cols: [[{
            type: 'numbers'
        }, {
            field: 'ossUserId',
            hide: true
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize,
        }, {
            field: 'realName',
            title: '姓名',
            align: 'center',
            style: fontSize,
        }, {
            field: 'wtime',
            title: '申请日期',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'specialAttendanceType',
            title: '类型',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'leaveType',
            title: '请假类型',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'days',
            title: '时长(天)',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'startTime',
            title: '开始时间',
            align: 'center',
            style: fontSize
        }, {
            field: 'endTime',
            title: '结束时间',
            align: 'center',
            style: fontSize
        }, {
            field: 'valid',
            title: '是否有效',
            align: 'center',
            style: fontSize,
            templet: function (row) {
                if (row.valid == 0) {
                    return '<div style = "background-color:#FF5722;margin:1px;padding:1px;height:100%;width:100%;color: white"> 无效 </div>';
                } else if (row.valid == 1) {
                    return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 有效 </div>';
                } else {
                    return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                }
            }
        }, {
            field: 'spend',
            title: '时间状态',
            align: 'center',
            style: fontSize,
            templet: function (row) {
                if (row.spend == 0) {
                    return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未开始 </div>';
                } else if (row.spend == 1) {
                    return '<div style = "background-color:#1E9FFF;margin:1px;padding:1px;height:100%;width:100%;color: white"> 进行中 </div>';
                } else if (row.spend == 2) {
                    return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已度过 </div>';
                } else {
                    return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                }
            }
        }]],
        parseData: function (res) {
            return {
                "code": 0, // 解析接口状态
                "count": Number.MAX_VALUE,
                "data": res.data
            }
        },
        done: function () {
            // 取消加载中遮罩
            layer.close(loadingIndex);
        }
    });

    table.on('row(record)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });
}

function initDate() {
    var nowDate = new Date();
    var today = {
        date: nowDate.getDate(),
        month: nowDate.getMonth(),
        year: nowDate.getFullYear()
    }
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        insStart = laydate.render({
            elem : '#startDate',
            value : new Date(today.year, 0, 1),
            format : 'yyyy-MM-dd',
            max : 0,
            type : 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新结束日期的最小日期
                insEnd.config.min = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });

        insEnd = laydate.render({
            elem: '#endDate',
            value: new Date(today.year + 1, 0, 1),
            format: 'yyyy-MM-dd',
            // max: 0,
            type: 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新开始日期的最大日期
                insStart.config.max = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });
    });
}

// 按钮绑定
function initButton() {
    // 部门选择
    $("#deptName").unbind().bind('click', function () {
        layer.open({
            type: 2,
            title: '部门选择',
            area: ['400px', '450px'],
            btn: ['确定', '取消'],
            btnAlign: 'c',
            fixed: false, //不固定
            maxmin: true,
            content: '/department/toDeptTree.action?check=no',
            yes: function (index, layero) {
                var body = layer.getChildFrame('body', index);
                var result = $(body).find("input[id='clicked_dept_id']");
                $('#deptId').val(result.val());
                $('#deptName').val(result.attr('data-name'));
                layer.close(index);
            }
        });
    });

    // 清空部门
    $("#empty").unbind().bind('click', function () {
        $("#deptName").val('');
        $("#deptId").val('');
    });

    // 搜索
    $("#search").unbind().click(function () {
        search();
    });

    // 重置
    $("#reset").unbind().click(function () {
        initDate();
        $("#keyword").val("");
        $('#deptName').val('');
        $('#deptId').val('');
        $('#ossUserId').val('');
        $('#type').val('');
        form.render();
    });
}

function initSelect() {
    $.ajaxSettings.async = false;
    // 类型下拉框
    $.get('/specialAttendance/getSpecialAttendanceSelect', function (res) {
        var data = JSON.parse(res);
        if (isNotBlank(data)) {
            var typeSelect = $('#typeSelect');
            var selected = $('#type').val();
            var options = '<option value="">全部</option>';
            $.each(data, function (index, item) {
                options += "<option value='" + item.value + "'" + (item.value == selected ? " selected='selected'" : "") + ">" + item.name + "</option>";
            });
            typeSelect.html(options);
            form.render('select');
        }
        $.ajaxSettings.async = true;
    })

}

function search() {
    var validCheck = $("input[name='valid']:checked");
    if (validCheck.length === 0) {
        layer.msg('请勾选是否有效状态');
        return;
    }
    var valid = [];
    $(validCheck).each(function (index, item) {
        valid.push($(item).val());
    });
    table.reload('record', {
        where: {
            startDate: $("#startDate").val(),
            endDate: $("#endDate").val(),
            ossUserId: $('#ossUserId').val(),
            keyword: $('#keyword').val(),
            deptId: $('#deptId').val(),
            type: $("#typeSelect").val(),
            valid: valid.join(',')
        }
    });
}
