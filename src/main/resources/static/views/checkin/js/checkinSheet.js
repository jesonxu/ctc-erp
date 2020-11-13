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
        initDate();
        initTable();
    });
})

/**
 * 加载已经导入的表格数据
 */
function initTable() {
    // 异常类型
    var statusCheck = $("input[name='status']:checked");
    var status = [];
    $(statusCheck).each(function (index, item) {
        status.push($(item).val());
    });
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#checkin',
        height: 'full-150',
        url: '/checkin/queryCheckin.action',
        where: {
            keyword: $('#keyword').val(),
            date: $("#date").val(),
            deptId: $('#deptId').val(),
            status: status.join(','),
        },
        cols: [[{
            type: 'numbers'
        }, {
            field: 'ossUserId',
            hide: true
        }, {
            field: 'date',
            title: '日期',
            align: 'center',
            style: fontSize,
            unresize: false
        }, {
            field: 'realName',
            title: '姓名',
            align: 'center',
            style: fontSize,
            unresize: false,
            sort: true
        }, {
            field: 'deptName',
            title: '部门',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'checkinTime',
            title: '上班打卡时间',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'checkinInfo',
            title: '上班打卡情况',
            align: 'center',
            style: fontSize,
            sort: true,
            templet: function (row) {
                if (row.checkinInfo === '正常打卡') {
                    return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white">' + row.checkinInfo + ' </div>';
                } else {
                    return row.checkinInfo;
                }
            }
        }, {
            field: 'checkinLocationTitle',
            title: '上班打卡地点',
            align: 'left',
            style: fontSize,
            templet: function (row) {
                if (row.checkinLocationTitle) {
                    return "<span title='" + row.checkinLocationDetail + "'>" + row.checkinLocationTitle + "</span>"
                } else {
                    return "";
                }
            }
        }, {
            field: 'checkoutTime',
            title: '下班打卡时间',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'checkoutInfo',
            title: '下班打卡情况',
            align: 'center',
            style: fontSize,
            sort: true,
            templet: function (row) {
                if (row.checkoutInfo === '正常打卡') {
                    return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white">' + row.checkoutInfo + ' </div>';
                } else {
                    return row.checkoutInfo;
                }
            }
        }, {
            field: 'checkoutLocationTitle',
            title: '下班打卡地点',
            align: 'left',
            style: fontSize,
            templet: function (row) {
                if (row.checkoutLocationTitle) {
                    return "<span title='" + row.checkoutLocationDetail + "'>" + row.checkoutLocationTitle + "</span>"
                } else {
                    return "";
                }
            }
        }, {
            field: 'outsideCheckTimes',
            title: '外出打卡',
            align: 'center',
            style: fontSize,
            sort: true,
            templet: function (row) {
                if (row.outsideCheckTimes > 0) {
                    return row.outsideCheckTimes + '次';
                } else {
                    return '';
                }
            }
        }, {
            title: '操作',
            align: 'center',
            minWidth: 100,
            toolbar: '#table-row-opts'
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

    table.on('row(checkin)', function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    table.on('tool(checkin)', function (obj) {
        var data = obj.data;
        var tr = obj.tr;
        if (obj.event === 'detail') {
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
        form.render();
    });
}

function search() {
    var statusCheck = $("input[name='status']:checked");
    if (statusCheck.length === 0) {
        layer.msg('请勾选打卡状态');
        return;
    }
    var status = [];
    $(statusCheck).each(function (index, item) {
        status.push($(item).val());
    });
    table.reload('checkin', {
        where: {
            keyword: $('#keyword').val(),
            date: $("#date").val(),
            deptId: $('#deptId').val(),
            status: status.join(','),
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