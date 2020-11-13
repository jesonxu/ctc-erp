var layer;
var laydate;
var element;
var form;
// 加载中遮罩
var loadingIndex;
var table;

$(document).ready(function () {
    layui.use(['layer', 'laydate', 'form', 'element', "table"], function () {
        table = layui.table;
        layer = layui.layer;
        laydate = layui.laydate;
        loadingIndex = layer.load(2);
        element = layui.element;
        form = layui.form;
        initButton();
        // initSelect();
        initDate();
        initTable();
    });
})

/**
 * 加载已经导入的表格数据
 */
function initTable() {
    var fontSize = 'font-size: 12px;padding:0px;';
    var leftBorder = 'border-left: 1px solid;font-size: 12px;';
    var rightBorder = 'border-right: 1px solid;font-size: 12px;';

    //第一个实例
    table.render({
        elem: '#month-attendance',
        height: 'full-110',
        url: '/attendance/queryMonthAttendance.action',
        where: {
            keyword: $('#keyword').val(),
            month: $("#month").val(),
            deptId: $('#deptId').val(),
        },
        cols: [[{
            field: 'month',
            title: '月份',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            rowspan: 2,
        }, {
            field: 'ossUserId',
            hide: true
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            rowspan: 2,
        }, {
            field: 'realName',
            title: '姓名',
            align: 'center',
            style: fontSize,
            width: 100,
            rowspan: 2,
        }, {
            field: 'defaultWorkDays',
            title: '应出勤天数',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            rowspan: 2,
        },{
            field: 'normalAttendanceDays',
            title: '正常出勤天数',
            align: 'center',
            style: fontSize,
            width: 110,
            sort: true,
            rowspan: 2,
        }, {
            field: 'specialAttendanceDays',
            title: '特殊出勤天数',
            align: 'center',
            style: fontSize,
            width: 110,
            sort: true,
            rowspan: 2,
        }, {
            title: '特殊出勤',
            align: 'center',
            style: fontSize,
            colspan: 4
        }, {
            field: 'exceptionalAttendanceDays',
            title: '异常出勤天数',
            align: 'center',
            style: fontSize,
            width: 110,
            sort: true,
            rowspan: 2,
        }, {
            title: '异常出勤',
            align: 'center',
            style: fontSize,
            colspan: 4
        }, {
            field: 'unknownAttendanceDays',
            title: '未知出勤天数',
            align: 'center',
            style: fontSize,
            rowspan: 2,
        }], [{
            field: 'leaveDays',
            title: '请假天数',
            align: 'center',
            style: fontSize,
        }, {
            field: 'overtimeDays',
            title: '加班天数',
            align: 'center',
            style: fontSize,
        }, {
            field: 'outsideDays',
            title: '外勤天数',
            align: 'center',
            style: fontSize,
        }, {
            field: 'businessTravelDays',
            title: '出差天数',
            align: 'center',
            style: fontSize,
        }, {
            field: 'lateDays',
            title: '迟到次数',
            align: 'center',
            style: fontSize,
        }, {
            field: 'lateMins',
            title: '迟到时长',
            align: 'center',
            style: fontSize,
        }, {
            field: 'absenteeismDsys',
            title: '旷工次数',
            align: 'center',
            style: fontSize,
        }, {
            field: 'absenteeismMins',
            title: '旷工时长',
            align: 'center',
            style: fontSize,
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

    table.on('row(attendance)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    table.on('tool(attendance)', function (obj) {
        var data = obj.data;
        if (obj.event === 'time-line') {
            layer.open({
                type: 2,
                title: '出勤时间线',
                area: ['660px', '500px'],
                btn:[],
                fixed: false, //不固定
                maxmin: true,
                content: '/attendance/toAttendanceTimeLine?id=' + data.id
            });
        }
    })
}

function initDate() {
    var date = new Date();
    var thisMonth = date.getFullYear() + "-" + (date.getMonth() + 1);
    var lastMonth = date.getLastMonth();
    $("#month").val(lastMonth);

    laydate.render({
        elem: '#month',
        format: 'yyyy-MM',
        type: 'month',
        min: '2003-01-01',
        max: thisMonth,
        value: lastMonth,
        trigger: 'click',
        done: function (value, month) {
            $('#month').val(value);
            search();
        }
    });
}

// 按钮绑定
function initButton() {
    // 部门选择
    $("#deptName").unbind().bind('click', function () {
        layer.open({
            type: 2,
            title: '部门选择',
            area: ['310px', '450px'],
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
    $("#search").unbind().bind('click', function () {
        search();
    });

    // 重置
    $("#reset").unbind().bind('click' ,function () {
        initDate();
        $("#keyword").val("");
        $('#deptName').val('');
        $('#deptId').val('');
        form.render();
    });

    // 修改状态
    $("#edit").unbind().bind('click', function () {
        var checkStatus = table.checkStatus('attendance');
        if (!checkStatus || !checkStatus.data || checkStatus.data.length === 0) {
            return layer.msg("请选中一个员工");
        }
        layer.open({
            type: 2,
            title: '修改出勤记录',
            area: ['750px', '550px'],
            btn:[],
            fixed: false, //不固定
            maxmin: true,
            content: '/attendance/toEditAttendance?id=' + checkStatus.data[0].id
        });
    });
}

function search() {
    table.reload('month-attendance', {
        where: {
            keyword: $('#keyword').val(),
            month: $("#month").val(),
            deptId: $('#deptId').val(),
        }
    });
}
