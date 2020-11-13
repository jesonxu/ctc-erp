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
    var statusCheck = $("input[name='userStatus']:checked");
    if (statusCheck.length === 0) {
        layer.msg('请勾选员工状态');
        return;
    }
    var status = [];
    $(statusCheck).each(function (index, item) {
        status.push($(item).val());
    });
    var fontSize = 'font-size: 12px;padding:0px;';
    //第一个实例
    table.render({
        elem: '#user-leave',
        height: 'full-150',
        url: '/userLeave/queryUserLeave.action',
        where: {
            keyword: $('#keyword').val(),
            year: $("#year").val(),
            deptId: $('#deptId').val(),
            status: status.join(',')
        },
        cols: [[{
            type: 'numbers'
        }, {
            field: 'ossUserId',
            hide: true
        }, {
            field: 'year',
            title: '年份',
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
            field: 'graduationDate',
            title: '毕业日期',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'workMonth',
            title: '累计工作',
            align: 'center',
            style: fontSize,
            sort: true,
            templet: function (row) {
                var text = "";
                if (row.workMonth) {
                    var workMonth = parseInt(row.workMonth);
                    if (workMonth < 0) {
                        text = "未毕业";
                    } else if (workMonth < 1) {
                        text = "<1个月";
                    } else {
                        var workYear = parseInt(workMonth / 12);
                        var moreMonth = workMonth % 12;
                        text = (workYear > 0 ? workYear + "年" : "") + (moreMonth > 0 ? moreMonth + "个月" : "");
                    }
                }
                return text;
            }
        }, {
            field: 'entryTime',
            title: '入职时间',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'entryMonth',
            title: '在职时长',
            align: 'center',
            style: fontSize,
            sort: true,
            templet: function (row) {
                var text = "";
                if (row.entryMonth) {
                    var entryMonth = parseInt(row.entryMonth);
                    if (entryMonth < 0) {
                        text = "未入职";
                    } else if (entryMonth < 1) {
                        text = "<1个月";
                    } else {
                        var entryYear = parseInt(entryMonth / 12);
                        var moreMonth = entryMonth % 12;
                        text = (entryYear > 0 ? entryYear + "年" : "") + (moreMonth > 0 ? moreMonth + "个月" : "");
                    }
                }
                return text;
            }
        }, {
            field: 'annualLeaveTotal',
            title: '年假天数',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'annualLeaveLeft',
            title: '剩余年假',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'overtimeTotal',
            title: '加班天数',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            field: 'overtimeLeft',
            title: '剩余调休',
            align: 'center',
            style: fontSize,
            sort: true
        }, {
            title: '操作',
            align: 'center',
            minWidth: 150,
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

    table.on('row(user-leave)',function(obj){
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    table.on('tool(user-leave)', function (obj) {
        var data = obj.data;
        if (obj.event === 'detail') {
            window.open('/specialAttendance/toSpecialAttendanceRecord?type=0&ossUserId=' + data.ossUserId);
            /*layer.open({
                type: 2,
                area: ['100%', '100%'],
                fixed: false,
                title: '员工特殊出勤报备记录',
                content: '/specialAttendance/toSpecialAttendanceRecord?type=0&ossUserId=' + data.ossUserId
            })*/
        }
    })
}

function initDate() {
    var date = new Date();
    var year = date.getFullYear();
    var max = year + '-12-31';
    $("#year").val(year);

    laydate.render({
        elem: '#year',
        format: 'yyyy',
        type: 'year',
        min: '2003-01-01',
        max: max,
        value: year,
        trigger: 'click',
        done: function (value, date) {
            $('#year').val(value);
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
            area: ['380px', '450px'],
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

    // 生成年假
    $("#build").unbind().click(function () {
        var year = $("#year").val();
        layer.confirm('确定生成' + year + '年假？员工在' + year + '年已使用的年假不会被重置。详情请看《年假规则》', function () {
            $.post('/userLeave/buildUserLeave', {year: year}, function (res) {
                layer.msg(res.msg, {time: 5000});
            })
        })
    });
}


function search() {
    var statusCheck = $("input[name='userStatus']:checked");
    if (statusCheck.length === 0) {
        layer.msg('请勾选员工状态');
        return;
    }
    var status = [];
    $(statusCheck).each(function (index, item) {
        status.push($(item).val());
    });
    table.reload('user-leave', {
        where: {
            keyword: $('#keyword').val(),
            year: $("#year").val(),
            deptId: $('#deptId').val(),
            status: status.join(',')
        }
    });
}


