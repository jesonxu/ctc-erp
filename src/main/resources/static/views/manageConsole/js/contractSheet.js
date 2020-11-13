var layer;
var element;
var form;
var table;
var excel;
// 加载中遮罩
var loadingIndex;
// 表实例
var _tableIns;
// 申请日期（开始）
var _applyDate;
// 申请日期（结束）
var _applyDateEnd;
var tipsIns;
var contractFilesMap = {};

$(document).ready(function () {
    initDate();
    initTable();
    initButton();
    autoCompleteUser();
});

// 初始化日期控件
function initDate() {
    var nowDate = new Date();
    var today = {
        date: nowDate.getDate(),
        month: nowDate.getMonth(),
        year: nowDate.getFullYear()
    };
    $('#applyDate').val(today.year + "-" + (today.month + 1) + "-01");
    $('#applyDateEnd').val(nowDate.format("yyyy-MM-dd"))
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        _applyDate = laydate.render({
            elem : '#applyDate',
            value : new Date(today.year, today.month, 1),
            format : 'yyyy-MM-dd',
            max : 0,
            type : 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新结束日期的最小日期
                _applyDateEnd.config.min = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });

        _applyDateEnd = laydate.render({
            elem: '#applyDateEnd',
            value: new Date(),
            format: 'yyyy-MM-dd',
            max: 0,
            type: 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新开始日期的最大日期
                _applyDate.config.max = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });
    });
}

// 重置日期控件的值
function resetDate() {
    var nowDate = new Date();
    var today = {
        date: nowDate.getDate(),
        month: nowDate.getMonth(),
        year: nowDate.getFullYear()
    };
    // 开始日期的最小值
    _applyDate.config.min = lay.extend({}, today, {
        date: 1,
        month: 0,
        year: 1900
    });
    // 开始日期的最大值
    _applyDate.config.max = lay.extend({}, today, {
        date: today.date,
        month: today.month
    });
    // 结束日期的最小值
    _applyDateEnd.config.min = lay.extend({}, today, {
        date: 1,
        month: 0,
        year: 1900
    });
    // 结束日期的最大值
    _applyDateEnd.config.max = lay.extend({}, today, {
        date: today.date,
        month: today.month
    });
}

function initTable() {
    layui.use(['table', 'form', 'excel'], function () {
        table = layui.table;
        form = layui.form;
        excel = layui.excel;
        _tableIns = table.render({
            elem: '#contractTable',
            url: '/contract/readContractByPage.action?temp=' + Math.random(),
            data: [],
            toolbar: '#toolbarDemo',
            defaultToolbar: false,
            limit: 15,
            limits: [15, 30, 60, 100],
            method: 'POST',
            height: 'full-160',
            page: true,
            where: getParameter(),
            cols: [
                [{
                    field: 'contractId',
                    title: '合同编号',
                    align: 'center',
                    width: '12%',
                    templet: function (row) {
                        if (isNotBlank(row.contractId)) {
                            contractFilesMap[row.contractId] = row.contractFilesScan;
                            return "<a id='" + row.contractId + "' style='text-decoration: underline' href='javascript:void(0);' onclick='showContractFiles(\"" + row.contractId + "\")'>" + row.contractId + "</a>"
                        }
                        return "-"
                    }
                }, {
                    field: 'contractName',
                    title: '合同名称',
                    align: 'center',
                    width: '12%',
                }, {
                    field: 'status',
                    title: '合同评审状态',
                    align: 'center',
                    width: '6%',
                    templet: function (row) {
                        if (row.status == '申请中') {
                            return '<div style = "background-color:#1E9FFF;margin:1px;padding:1px;height:100%;width:100%;color: white"> 申请中 </div>';
                        } else if (row.status == '已归档') {
                            return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已归档 </div>';
                        } else if (row.status == '已取消') {
                            return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已取消 </div>';
                        }
                    }
                }, {
                    field: 'applyDate',
                    title: '申请日期',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'realName',
                    title: '申请人',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'deptName',
                    title: '申请人部门',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'entityRegion',
                    title: '客户地域',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'entityName',
                    title: '客户',
                    align: 'center',
                    width: '15%',
                }, {
                    field: 'contactName',
                    title: '客户联系人',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'contractRegion',
                    title: '合同归属',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'contactPhone',
                    title: '客户联系方式',
                    align: 'center',
                    width: '8%',
                }, {
                    field: 'address',
                    title: '客户联系地址',
                    align: 'center',
                    width: '8%',
                }, {
                    field: 'contractType',
                    title: '合同类型',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'productType',
                    title: '产品类型',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'settleType',
                    title: '付费方式',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'monthCount',
                    title: '月发送量',
                    align: 'right',
                    width: '6%',
                }, {
                    field: 'contractAmount',
                    title: '合同金额',
                    align: 'right',
                    width: '8%',
                }, {
                    field: 'price',
                    title: '单价',
                    align: 'right',
                    width: '6%',
                }, {
                    field: 'projectLeader',
                    title: '项目负责人',
                    align: 'center',
                    width: '6%',
                }, {
                    field: 'validityDateStart',
                    title: '有效期开始',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'validityDateEnd',
                    title: '有效期结束',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'description',
                    title: '项目情况说明',
                    align: 'center',
                    width: '10%',
                }
                ]
            ],
            parseData: function (res) { // res 即为原始返回的数据
                return {
                    "code": 0, // 解析接口状态
                    "count": res.data.count, // 解析数据长度
                    "data": res.data.data
                    // 解析数据列表
                };
            }
        });
        form.render();
        table.on('toolbar(contractTable)', function (obj) {
            // 导出
            if (obj.event === 'EXPORT_EXCEL') {
                $.post("/contract/exportContract", getParameter(), function (data) {
                        if (data.code == 500) {
                            layer.msg(data.msg);
                        } else if (data.code == 200) {
                            down_load(data.data);
                        }
                    }
                );
            }
        })

    });
}

function initButton() {
    $("#btn-search").click(function () {
        search();
    });
    $("#btn-reset").click(function () {
        clearAll();
    });
    $("#btn-build").click(function () {
        buildFromFlow();
    });
}

// 显示合同文件下载和预览层
function showContractFiles(contractId) {
    var dom = '<span style="color: black">无文件</span>';
    var files = contractFilesMap[contractId];
    if (isNotBlank(files)) {
        files = (typeof files == 'object') ? files : JSON.parse(files);
        dom = '';
        $.each(files, function (i, item) {
            var fileJson = JSON.stringify(item);
            dom += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='down_load(" + fileJson + ")'>" + item.fileName + "</a>";
            dom += "<button type='button' class='layui-btn layui-btn-xs my-down-load' onclick='view_File(" + fileJson + ")'>预览</button></br>";
        })
    }
    tipsIns = layer.tips(dom, '#' + contractId, {
        tips: [1, 'whitesmoke'],
        time: 6000
    });
}

// 获取查询条件
function getParameter() {
    var contractName = $("#contractName").val().trim();
    var contractId = $("#contractId").val().trim();
    var ossUserId = $("#ossUserId").val().trim();
    var entityName = $("#entityName").val().trim();
    var applyDate = $("#applyDate").val().trim() + " 00:00:00";
    var applyDateEnd = $("#applyDateEnd").val().trim() + " 23:59:59";
    var validStatus = new Array();
    var valid = $("#valid").attr('checked');
    if (valid) {
        validStatus.push($("#valid").val())
    }
    var invalid = $("#invalid").attr('checked');
    if (invalid) {
        validStatus.push($("#invalid").val())
    }
    if (validStatus.length < 1) {
        layer.msg('请选择是否在合同有效期');
        return;
    }
    return {
        contractName: contractName,
        contractId: contractId,
        applyDate: applyDate,
        applyDateEnd: applyDateEnd,
        ossUserId: ossUserId,
        entityName: entityName,
        validStatus: validStatus.join(',')
    }
}

// 搜索
function search() {
    var parameter = getParameter();
    if (isBlank(parameter)) {
        return;
    }
    _tableIns.reload({
        url: "/contract/readContractByPage.action?temp=" + Math.random(),
        where: parameter
    });
}

// 自动补全用户
function autoCompleteUser() {
    layui.config({
        base: '/common/js/'
    }).extend({ // 设定模块别名
        autocomplete: 'autocomplete'
    });
    layui.use('autocomplete', function () {
        var autocomplete = layui.autocomplete;
        autocomplete.render({
            elem: $('#realName'),
            hidelem: $('#ossUserId'),
            url: '/account/queryByAuto.action',
            template_val: '{{d.ossUserId}}',
            template_txt: '{{d.loginName}} <span class=\'layui-badge layui-bg-gray\'>{{d.realName}}</span>',
            onselect: function (resp) {
                $("#realName").val(resp.realName);
                $("#ossUserId").val(resp.ossUserId);
            }
        });
    })
}

function clearAll() {
    $("#contractName").val('');
    $("#contractId").val('');
    $("#entityName").val('');
    $("#realName").val('');
    $("#ossUserId").val('');
    $('#valid').attr('checked', true);
    $('#invalid').attr('checked', false);
    resetDate();
    layui.use('form', function() {
        var form = layui.form, layer = layui.layer;
        form.render();
    });
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