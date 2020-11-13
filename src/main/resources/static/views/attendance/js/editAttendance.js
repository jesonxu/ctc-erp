var form;
var layer;
var table;
var laydate;

$(document).ready(function () {
    layui.use(['form', 'layer', 'table', 'laydate'], function () {
        form = layui.form;
        layer = layui.layer;
        table = layui.table;
        laydate = layui.laydate;
        loadData();
    })
});

function loadData() {
    var id = $('#id').val();
    $.get('/attendance/queryAttendance?id=' + id, function (res) {
        if (res.code == 200) {
            var data = res.data[0];
            if (data) {
                showData(data);
            }
            showOperationLog(data.operationLog)
            bindOperations();
        } else {
            layer.msg(res.msg);
        }
    })
}

/**
 * 账单详情
 * @param data
 */
function showData(data) {

    $('#date').text(data.date);
    $('#deptName').text(data.deptName);
    $('#realName').text(data.realName);

    var workStatus = '';
    if (data.workStatus == 0) {
        workStatus = '<div class="status-unknown" title="复杂情况请手动确认"> 待确认 </div>';
    } else if (data.workStatus == 1) {
        workStatus = '<div class="status-normal" title="正常上下班打卡"> 正常出勤 </div>';
    } else if (data.workStatus == 2) {
        workStatus = '<div class="status-special" title="存在请假、加班、外勤、出差的情况"> 特殊出勤 </div>';
    } else if (data.workStatus == 3) {
        workStatus = '<div class="status-absence" title="存在迟到、未打卡、矿工的情况"> 异常出勤 </div>';
    }
    $('#workStatus').html(workStatus);

    $('#timeLine').unbind().bind('click', function () {
        parent.layer.open({
            type: 2,
            title: '出勤时间线',
            area: ['660px', '500px'],
            shadow: 0,
            btn:[],
            fixed: false, //不固定
            maxmin: true,
            content: '/attendance/toAttendanceTimeLine?id=' + data.id
        });
    });

    if (isNotBlank(data.workInfo)) {
        $('#work').text(data.work == 1 ? '是' : '');
        $('#workMins').text(data.workMins);
        $('#workInfo').text(getInfo(data.workInfo));
        $('#iconWork').addClass('icon-work');
    }

    if (isNotBlank(data.leaveInfo)) {
        $('#leave').text(data.leave == 1 ? '是' : '');
        $('#leaveMins').text(data.leaveMins);
        $('#leaveInfo').text(getInfo(data.leaveInfo));
        $('#iconLeave').addClass('icon-leave');
    }

    if (isNotBlank(data.overtimeInfo)) {
        $('#overtime').text(data.overtime == 1 ? '是' : '');
        $('#overtimeMins').text(data.overtimeMins);
        $('#overtimeInfo').text(getInfo(data.overtimeInfo));
        $('#iconOvertime').addClass('icon-overtime');
    }

    if (isNotBlank(data.outsideInfo)) {
        $('#outside').text(data.outside == 1 ? '是' : '');
        $('#outsideMins').text(data.outsideMins);
        $('#outsideInfo').text(getInfo(data.outsideInfo));
        $('#iconOutside').addClass('icon-outside');
    }

    if (isNotBlank(data.businessTravelInfo)) {
        $('#businessTravel').text(data.businessTravel == 1 ? '是' : '');
        $('#businessTravelMins').text(data.businessTravelMins);
        $('#businessTravelInfo').text(getInfo(data.businessTravelInfo));
        $('#iconBusinessTravel').addClass('icon-business-travel');
    }

    if (isNotBlank(data.lateInfo)) {
        $('#late').text(data.late == 1 ? '是' : '');
        $('#lateMins').text(data.lateMins);
        $('#lateInfo').text(getInfo(data.lateInfo));
        $('#iconLate').addClass('icon-late');
    }

    if (isNotBlank(data.absenteeismInfo)) {
        $('#absenteeism').text(data.absenteeism == 1 ? '是' : '');
        $('#absenteeismMins').text(data.absenteeismMins);
        $('#absenteeismInfo').text(getInfo(data.absenteeismInfo));
        $('#iconAbsenteeism').addClass('icon-absenteeism');
    }
}

function cleanData() {
    $('#date').text('');
    $('#deptName').text('');
    $('#realName').text('');
    $('#workStatus').html('');
    $('#timeLine').unbind();
    $('#time').val('');

    $('#work').text('');
    $('#workMins').text('');
    $('#workInfo').text('');
    $('#iconWork').removeClass('icon-work');

    $('#leave').text('');
    $('#leaveMins').text('');
    $('#leaveInfo').text('');
    $('#iconLeave').removeClass('icon-leave');

    $('#overtime').text('');
    $('#overtimeMins').text('');
    $('#overtimeInfo').text('');
    $('#iconOvertime').removeClass('icon-overtime');

    $('#outside').text('');
    $('#outsideMins').text('');
    $('#outsideInfo').text('');
    $('#iconOutside').removeClass('icon-outside');

    $('#businessTravel').text('');
    $('#businessTravelMins').text('');
    $('#businessTravelInfo').text('');
    $('#iconBusinessTravel').removeClass('icon-business-travel');

    $('#late').text('');
    $('#lateMins').text('');
    $('#lateInfo').text('');
    $('#iconLate').removeClass('icon-late');

    $('#absenteeism').text('');
    $('#absenteeismMins').text('');
    $('#absenteeismInfo').text('');
    $('#iconAbsenteeism').removeClass('icon-absenteeism');
}

function getInfo(info) {
    let text = '';
    if (isNotBlank(info)) {
        info = typeof info == 'object' ? info : JSON.parse(info);
        if (info.length > 0) {
            $.each(info, function (index, item) {
                text += '第' + (index + 1) + '段：' + item.startTime + ' ~ ' + item.endTime + '，时长：' + item.mins + '分\n';
            });
        }
    }
    return text;
}

/**
 * 操作按钮
 */
function bindOperations() {
    $('.status-icon').each(function (index, ele) {
        var type = $(ele).attr('data-type');
        $(ele).unbind().bind('click', function () {
            document.getElementById(type).scrollIntoView(true);
        });
    });

    $('button[name="clean"]').each(function (index, ele) {
        var type = $(ele).attr('data-type');
        $(ele).unbind().bind('click', function () {
            layer.confirm('确认清空此项记录？', function (index) {
                cleanLog(type);
                layer.close(index);
            });
        });
    });

    $('button[name="add"]').each(function (index, ele) {
        var type = $(ele).attr('data-type');
        $(ele).unbind().bind('click', function () {
            addLog(type);

        });
    });
}

function cleanLog(type) {
    var id = $('#id').val();
    $.post('/attendance/cleanLog', {id: id, type: type}, function (res) {
        layer.msg(res);
        cleanData();
        loadData();
    });
}

function addLog(type) {
    layer.open({
        type: 1,
        title: '添加记录',
        area: ['450px', '180px'],
        fixed: false, //不固定
        maxmin: true,
        content: $("#addPanel"),
        btn: ['确定', '取消'],
        success: function(layero, index){
            var date = $('#data').text();
            laydate.render({
                elem: '#time',
                type: 'datetime',
                range: '~',
                min: date + ' 00:00:00',
                max: date + ' 23:59:59',
                trigger: 'click',
                done: function (value, date) {
                    $('#time').val(value);
                }
            });
        },
        yes: function (index, layero) {
            layer.confirm('确认增加一条记录？', function(index) {
                var time = $("#time").val();
                var id = $('#id').val();
                $.post('/attendance/addLog', {id: id, type: type, time: time}, function (res) {
                    parent.layer.msg(res);
                    cleanData();
                    loadData();
                })
                layer.close(index);
            });
            layer.close(index);
        }
    });
}

function operatedata(ele, url) {
    var title = $(ele).text();
    var data = {
        dataId: $('#dataId').text(),
        platformSuccessCount: $('#platformSuccessCount').val(),
        cost: $('#cost').val(),
        checkedSuccessCount: $('#checkedSuccessCount').val(),
        receivables: $('#receivables').val(),
        actualReceivables: $('#actualReceivables').val(),
        payables: $('#payables').val(),
        actualPayables: $('#actualPayables').val(),
        actualInvoiceAmount: $('#actualInvoiceAmount').val(),
        unitPrice: $('#unitPrice').val(),
        grossProfit: $('#grossProfit').val()
    }
    layer.confirm("确认执行【" + title + "】操作?", {
        title: "确认操作",
        icon: 3,
        btn: ["确认", "取消"],
        skin: "reject-confirm"
    }, function (index) {
        $.post(url, data, function (res) {
            layer.tips(res, $(ele));
        });
        layer.close(index);
    }, function () {

    });
}

function showOperationLog(operationLog) {
    if (isNull(operationLog)) {
        return
    }
    operationLog = typeof operationLog == 'object' ? operationLog : JSON.parse(operationLog);
    table.init('operationLog', {limit: 999});
    table.reload('operationLog', {
        url: '',
        style: '',
        data: operationLog
    });
    table.render();
}