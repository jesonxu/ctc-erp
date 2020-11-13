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
        elem: '#attendance',
        height: 'full-110',
        url: '/attendance/queryAttendance.action',
        where: {
            keyword: $('#keyword').val(),
            date: $("#date").val(),
            deptId: $('#deptId').val(),
        },
        cols: [[{
            type: 'radio'
        }, {
            field: 'date',
            title: '出勤日期',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
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
        }, {
            field: 'realName',
            title: '姓名',
            align: 'center',
            style: fontSize,
            width: 100,
        }, {
            field: 'workStatus',
            title: '出勤状况',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.workStatus == 0) {
                    return '<div class="status-unknown" title="复杂情况请手动确认"> 待确认 </div>';
                } else if (row.workStatus == 1) {
                    return '<div class="status-normal" title="正常上下班打卡"> 正常出勤 </div>';
                } else if (row.workStatus == 2) {
                    return '<div class="status-special" title="存在请假、加班、外勤、出差的情况"> 特殊出勤 </div>';
                } else if (row.workStatus == 3) {
                    return '<div class="status-absence" title="存在迟到、未打卡、旷工的情况"> 异常出勤 </div>';
                }
            }
        }, {
            title: '打卡记录',
            align: 'center',
            width: 100,
            toolbar: '#table-row-opts1'
        }, {
            title: '时间线',
            align: 'center',
            width: 100,
            toolbar: '#table-row-opts2'
        }, {
            field: 'work',
            title: '是否工作',
            align: 'center',
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.work == '1') {
                    return '是'
                } else {
                    return '';
                }
            },
        }, {
            field: 'leave',
            title: '是否请假',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.leave == '1') {
                    return '是'
                } else {
                    return '';
                }
            },
        }, {
            field: 'overtime',
            title: '是否加班',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.overtime == '1') {
                    return '是'
                } else {
                    return '';
                }
            },
        }, {
            field: 'outside',
            title: '外出打卡次数',
            align: 'center',
            style: fontSize,
            width: 110,
            sort: true,
            templet: function (row) {
                if (row.outside == '0') {
                    return ''
                } else {
                    return row.outside;
                }
            },
        }, {
            field: 'businessTravel',
            title: '是否出差',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.businessTravel == '1') {
                    return '是'
                } else {
                    return '';
                }
            },
        }, {
            field: 'late',
            title: '是否迟到',
            align: 'center',
            style: fontSize,
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.late == '1') {
                    return '是'
                } else {
                    return '';
                }
            },
        }, {
            field: 'absenteeism',
            title: '是否旷工',
            align: 'center',
            style: rightBorder,
            width: 100,
            sort: true,
            templet: function (row) {
                if (row.absenteeism == '1') {
                    return '是'
                } else {
                    return '';
                }
            },
        }, {
            field: 'workMins',
            title: '工作时长',
            align: 'center',
            style: leftBorder,
            width: 100,
            sort: true,
        }, {
            field: 'workInfo',
            title: '工作详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.workInfo) {
                    var info = typeof row.workInfo == 'object' ? row.workInfo : JSON.parse(row.workInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
            }
        }, {
            field: 'leaveMins',
            title: '请假时长',
            align: 'center',
            style: leftBorder,
            width: 100,
        }, {
            field: 'leaveInfo',
            title: '请假详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.leaveInfo) {
                    var info = typeof row.leaveInfo == 'object' ? row.leaveInfo : JSON.parse(row.leaveInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
            }
        }, {
            field: 'overtimeMins',
            title: '加班时长',
            align: 'center',
            style: leftBorder,
            width: 100,
        }, {
            field: 'overtimeInfo',
            title: '加班详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.overtimeInfo) {
                    var info = typeof row.overtimeInfo == 'object' ? row.overtimeInfo : JSON.parse(row.overtimeInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
            }
        }, {
            field: 'outsideMins',
            title: '外勤时长',
            align: 'center',
            style: leftBorder,
            width: 100,
        }, {
            field: 'outsideInfo',
            title: '外勤详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.outsideInfo) {
                    var info = typeof row.outsideInfo == 'object' ? row.outsideInfo : JSON.parse(row.outsideInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
            }
        }, {
            field: 'businessTravelMins',
            title: '出差时长',
            align: 'center',
            style: leftBorder,
            width: 100,
        }, {
            field: 'businessTravelInfo',
            title: '出差详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.businessTravelInfo) {
                    var info = typeof row.businessTravelInfo == 'object' ? row.businessTravelInfo : JSON.parse(row.businessTravelInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
            }
        }, {
            field: 'lateMins',
            title: '迟到时长',
            align: 'center',
            style: leftBorder,
            width: 100,
        }, {
            field: 'lateInfo',
            title: '迟到详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.lateInfo) {
                    var info = typeof row.lateInfo == 'object' ? row.lateInfo : JSON.parse(row.lateInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
            }
        }, {
            field: 'absenteeismMins',
            title: '旷工时长',
            align: 'center',
            style: leftBorder,
            width: 100,
        }, {
            field: 'absenteeismInfo',
            title: '旷工详情',
            align: 'center',
            style: rightBorder,
            width: 100,
            templet: function (row) {
                var content = '';
                if (row.absenteeismInfo) {
                    var info = typeof row.absenteeismInfo == 'object' ? row.absenteeismInfo : JSON.parse(row.absenteeismInfo);
                    if (info.length > 0) {
                        $.each(info, function (index, item) {
                            var start = item['startTime'];
                            var end = item['endTime'];
                            content += start + ' ~ ' + end + '\n';
                        });
                    }
                }
                return content;
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

    table.on('row(attendance)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    table.on('tool(attendance)', function (obj) {
        var data = obj.data;

        if (obj.event === 'checkin') {
            var tr = obj.tr;
            var nextRow = $(tr).next();
            if (nextRow && nextRow.attr('data-type') === 'detail') {
                $(nextRow).remove();
                return;
            }
            var ossUserId = data.ossUserId;
            var date = $("#date").val();
            $.post("/checkin/queryCheckinDetail", {
                    ossUserId: ossUserId,
                    date: date
                }, function (res) {
                    if (res.code === 200 || res.code === "200") {
                        var detailList = res.data;
                        var html = "<tr class='detail' data-type='detail'><td colspan='12'>无数据</td></tr>";

                        if (isNotBlank(detailList) && detailList.length > 0) {
                            html = "<tr class='detail' data-type='detail'><td colspan='12'>" +
                                "<table class='detail-table'>" +
                                "   <thead>" +
                                "   <tr class='detail-table-row'>" +
                                "       <th style='width: 15%'>打卡时间</th>" +
                                "       <th style='width: 15%'>打卡类型</th>" +
                                "       <th style='width: 20%'>打卡地点</th>" +
                                "       <th style='width: 20%'>异常类型</th>" +
                                "       <th style='width: 15%'>附件</th>" +
                                "       <th style='width: 15%'>备注</th>" +
                                "   </tr>" +
                                "   </thead>";
                            var details = [];
                            html += "<tbody>";
                            for (var dataIndex = 0; dataIndex < detailList.length; dataIndex++) {
                                var detail = detailList[dataIndex];
                                details.push(
                                    "<tr class='detail-table-row'>" +
                                    "<td>" + detail.checkinTime + "</td>" +
                                    "<td>" + detail.checkinTypeName + "</td>" +
                                    "<td><span title='" + detail.locationDetail + "'>" + detail.locationTitle + "</span></td>" +
                                    "<td>" + detail.exceptionTypeName + "</td>" +
                                    "<td>" + (detail.mediaIds === '[]' ? "" : "<a href='javascript:void(0);' onclick='showMedia(this)' data-ids='" + detail.mediaIds + "'>点击查看附件</a></td>") +
                                    "<td>" + detail.notes + "</td>" +
                                    "</tr>"
                                );
                            }
                            html += details.join('');
                            html += "</tbody></table></td></tr>"
                        }
                        $(tr).after(html);
                    } else {
                        layer.msg(data.msg);
                    }
                }
            );
        } else if (obj.event === 'time-line') {
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
    var todayStr = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    date.setDate(date.getDate() - 1);
    var yesterdayStr = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    $("#date").val(yesterdayStr);

    laydate.render({
        elem: '#date',
        // position: 'static',
        // showBottom: false,
        format: 'yyyy-MM-dd',
        type: 'date',
        min: '2003-01-01',
        max: todayStr,
        value: yesterdayStr,
        trigger: 'click',
        done: function (value, date) {
            $('#date').val(value);
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

    // 月出勤记录
    $("#month").unbind().bind('click', function () {
        layer.open({
            type: 2,
            title: '月出勤记录',
            area: ['100%', '100%'],
            btn:[],
            fixed: false, //不固定
            maxmin: true,
            content: '/attendance/toMonthAttendance'
        });
    });
}

function search() {
    table.reload('attendance', {
        where: {
            keyword: $('#keyword').val(),
            date: $("#date").val(),
            deptId: $('#deptId').val(),
        }
    });
}

function showMedia(othis) {
    var ids = $(othis).attr('data-ids');
    if ('[]' === ids) {
        return ;
    }
    var mediaIds = JSON.parse(ids);
    var html = "";
    $(mediaIds).each(function (index, item) {
        html += '<img class="detail-img" src="/checkin/getMedia?mediaId=' + item + '" alt="无法显示"><br>';
    })
    layer.open({
        type: 1,
        title: '查看附件',
        area: ['600px', '600px'],
        btn:[],
        fixed: false, //不固定
        maxmin: true,
        content: html
    });
}