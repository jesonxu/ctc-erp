var laydate;
var layer;
var element;
var form;
var product_type_info = [];
$(document).ready(function () {
    layui.use(['laydate', 'layer', 'form', 'element'], function () {
        laydate = layui.laydate;
        layer = layui.layer;
        form = layui.form;
        element = layui.element;
        load_customerStockOut();
        load_UserStockOut();
        init_search_btn_stockOut();
        init_reset_btn_stockOut();
        init_table_stockOut();
        laydate.render({
            elem: '#startTimeOut'
            ,format: 'yyyy-MM-dd'
        });
        laydate.render({
            elem: '#endTimeOut'
            ,format: 'yyyy-MM-dd'
        });
    });
});

var stockOuttableIns;

// 初始化表格
function init_table_stockOut() {
    layui.use(['table'], function () {
        var table = layui.table;
        //列表
        stockOuttableIns = table.render({
            elem: '#stockOutProductList',
            url: "/dsOutDepot/queryDsOutDepot.action?temp=" + Math.random(),
            request: {
                page: 'currentPage',
                limit: 'pageSize'
            },
            height: 'full-220',
            toolbar: '#table-opts-stockOut',
            defaultToolbar: [],
            cellMinWidth:80,
            /*even: true,*/
            page: true,
            limit: 15,
            limits: [15, 30, 50, 100],
            method: 'POST',
            cols: [[
                /*{
                    // fixed: 'left',
                    type:'checkbox',
                },*/
                {
                    type: 'numbers',
                    title: '序号',
                    // width: 80
                },
                {
                    field: 'id',
                    title: '主键id',
                    hide: true
                },
                {
                    field: 'outDepotDetialId',
                    title: '详情id',
                    hide: true
                },
                {
                    field: 'outDepotCode',
                    title: '出库批次号',
                    align: 'center',
                    /*width: 120*/
                },
                {
                    field: 'customerName',
                    title: '客户名称',
                    align: 'center',
                    /*width: 150,
                    minWidth: 60*/
                },
                {
                    field: 'productName',
                    title: '商品名',
                    align: 'center',
                    /*width: 200,
                    minWidth: 80*/
                },
                {
                    field: 'otherCost',
                    title: '其它费用',
                    align: 'center',
                    width: 80,
                    /*templet: function (d) {
                        format_num(d.otherCost, 2)
                    }*/
                },
                {
                    field: 'outDepotTotal',
                    title: '销售合计金额',
                    align: 'center',
                    width: 100,
                    /*templet: function (d) {
                        format_num(d.depotCost, 2)
                    }*/
                },
                {
                    field: 'outTime',
                    title: '出库日期',
                    align: 'center',
                    width: 90
                },
                {
                    field: 'createrName',
                    title: '创建人名称',
                    align: 'center',
                    width: 100
                },
                {
                    field: 'verifyStatus',
                    title: '审核状态',
                    align: 'center',
                    width: 100,
                    templet :function (d){
                        return getVerifyStatus(d.verifyStatus); // 是否可售
                    }
                },
                {
                    // fixed: 'right',
                    title: '操作',
                    width: 200,
                    minWidth: 200,
                    align: 'center',
                    toolbar: '#table-row-opts-stockOut'
                }
            ]]
            , parseData: function (res) {
                return {
                    "code": 0,
                    "count": res.data.count,
                    "data": res.data.data
                };
            }
        });

        // 点击行选中
        table.on('row(stockOutProductList)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });
        // 表格行 操作按钮
        table.on('tool(stockOutProductList)', function (obj) {
            var data = obj.data;
            var id = data.id;
            if (obj.event === 'updateStockOut') {
                editStockOut(id)
            } else if(obj.event === 'deleteRowStockOut'){
                layer.confirm('确定删除吗？', function(index){
                    deleteStockOut(id);
                    layer.close(index);
                });
            } else if(obj.event === 'auditStockOut'){
                layer.confirm('确定审核通过吗？', function(index){
                    auditStockOut(id);
                    layer.close(index);
                });
            } else if(obj.event === 'previewStockOut'){
                previewStockOut(id)
            } else {
                layer.alert("未知操作");
            }
        });
        // 表头操作按钮
        table.on('toolbar(stockOutProductList)', function (obj) {
            var checkStatus = table.checkStatus(obj.config.id);
            if ("addStockOut".equals(obj.event)) {
                initButtonStockOutClick();
            } else {
                layer.alert("未知操作");
            }
        });
    });
}
// 删除采购入库
function deleteStockOut(id) {
    $.ajax({
        type: "POST",
        url: "/dsOutDepot/delete.action",
        dataType: 'json',
        data: {
            id: id
        },
        success: function (data) {
            if (200 === data.code) {
                reload_table_stockOut();
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 审核采购入库
function auditStockOut(id) {
    $.ajax({
        type: "POST",
        url: "/dsOutDepot/audit.action",
        dataType: 'json',
        data: {
            id: id
        },
        success: function (data) {
            if (200 === data.code) {
                reload_table_stockOut();
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 是否审核
function getVerifyStatus(p){
    var str ="";
    if(p == "1"){
        str= "<span style='color: #5fb878;'>审核通过</span>";
    } else if (p == "0") {
        str= "<span style='color: #F581B1;'>待审核</span>";
    } else if (p == "2") {
        str= "<span style='color: #F52822;'>审核不通过</span>";
    }
    return str;
}
// 初始化参数类型--客户
function load_customerStockOut() {
    $.ajax({
        type: "POST",
        url: "/customer/queryAllCustomer.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<option value=''>请选择客户名称</option>";
                $.each(types, function (index, obj) {
                    /*console.log(index + "..." + obj.supplierId+"..."+obj.companyName);*/
                    content += "<option value=" + obj.customerId + ">" + obj.companyName + "</option>";
                });
                $("#stockOutCustomerId").html(content);
                form.render('select');
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 初始化参数类型--销售人员
function load_UserStockOut() {
    $.ajax({
        type: "POST",
        url: "/user/queryAllUser.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<option value=''>请选择销售人员</option>";
                $.each(types, function (index, obj) {
                    /*console.log(index + "..." + obj.supplierId+"..."+obj.companyName);*/
                    content += "<option value=" + obj.ossUserId + ">" + obj.realName + "</option>";
                });
                $("#stockOutUserId").html(content);
                form.render('select');
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 验证
function isPositiveInteger(s){//是否为正整数
     var re = /^[0-9]+$/ ;
     return re.test(s)
}
function isPriceInteger(s){//金额是否正确（最多两位小数）
     var re = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
     return re.test(s)
}
// 初始化查询按钮
function init_search_btn_stockOut() {
    $("#btn-search-stockOut").click(function () {
        reload_table_stockOut();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn_stockOut() {
    $("#btn-reset-stockOut").click(function () {
        productName: $("#stockOutProductName").val("");
        customerId: $("#stockOutCustomerId").val("");
        userId: $("#stockOutUserId").val("");
        verifyStatus: $("#stockOutVerifyStatus").val("");
        outDepotCode: $("#outDepotCode").val("");
        createName: $("#stockOutCreateName").val("");
        startTime: $("#startTimeOut").val("");
        endTime: $("#endTimeOut").val("");
        form.render('select');
    });
}

// 重新加载表格
function reload_table_stockOut() {
    stockOuttableIns.reload({
        url: "/dsOutDepot/queryDsOutDepot.action?temp=" + Math.random(),
        where: {
            productName: $("#stockOutProductName").val(),
            customerId: $("#stockOutCustomerId").val(),
            userId: $("#stockOutUserId").val(),
            verifyStatus: $("#stockOutVerifyStatus").val(),
            outDepotCode: $("#outDepotCode").val(),
            createName: $("#stockOutCreateName").val(),
            startTime: $("#startTimeOut").val(),
            endTime: $("#endTimeOut").val()
        }
    });
}
// 修改销售出库信息
function editStockOut(id) {
    layer.open({
        title: ['修改产品出库信息'],
        type: 2,
        area: ['80%', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsOutDepot/toDsOutDepotInfoPage.action?id=' + id + '&isEdit=0'
    });
}
// 查看出库信息
function previewStockOut(id) {
    layer.open({
        title: ['查看出库信息'],
        type: 2,
        area: ['80%', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsOutDepot/toPreviewStockOutPage.action?id=' + id + '&isEdit=1'
    });
}
// 添加加销售出库信息
function initButtonStockOutClick() {
    layer.open({
        title: ['添加产品出库信息'],
        type: 2,
        area: ['80%', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsOutDepot/toAddStockOutProductPage.action'
    });
}
function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
