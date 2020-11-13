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
        load_dianShangSupplierStock();
        init_search_btn_stock();
        init_reset_btn_stock();
        init_table_stock();
        laydate.render({
            elem: '#startTime'
            ,format: 'yyyy-MM-dd'
        });
        laydate.render({
            elem: '#endTime'
            ,format: 'yyyy-MM-dd'
        });
    });
});

var stocktableIns;

// 初始化表格
function init_table_stock() {
    layui.use(['table'], function () {
        var table = layui.table;
        //列表
        stocktableIns = table.render({
            elem: '#stockProductList',
            url: "/dsDepot/queryDsDepot.action?temp=" + Math.random(),
            request: {
                page: 'currentPage',
                limit: 'pageSize'
            },
            height: 'full-220',
            toolbar: '#table-opts-stock',
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
                    field: 'depotItemId',
                    title: '详情id',
                    hide: true
                },
                {
                    field: 'depotCode',
                    title: '采购批次号',
                    align: 'center',
                    width: 120
                },
                {
                    field: 'supplierName',
                    title: '供应商名称',
                    align: 'center',
                    minWidth: 100
                },
                {
                    field: 'productName',
                    title: '商品名',
                    align: 'center',
                    /*width: 200,*/
                    minWidth: 100
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
                    field: 'depotCost',
                    title: '采购合计金额',
                    align: 'center',
                    width: 100,
                    /*templet: function (d) {
                        format_num(d.depotCost, 2)
                    }*/
                },
                {
                    field: 'buyTime',
                    title: '采购日期',
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
                    width: 80,
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
                    toolbar: '#table-row-opts-stock'
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
        table.on('row(stockProductList)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });
        // 表格行 操作按钮
        table.on('tool(stockProductList)', function (obj) {
            var data = obj.data;
            var id = data.id;
            if (obj.event === 'updateStock') {
                editStock(id)
            } else if(obj.event === 'deleteRowStock'){
                layer.confirm('确定删除吗？', function(index){
                    deleteStock(id);
                    layer.close(index);
                });
            } else if(obj.event === 'auditStock'){
                /*layer.alert('编辑行：<br>'+ JSON.stringify(data))*/
                layer.confirm('确定审核通过吗？', function(index){
                    auditStock(id);
                    layer.close(index);
                });
            } else if(obj.event === 'previewStock'){
                previewStock(id)
            } else {
                layer.alert("未知操作");
            }
        });
        // 表头操作按钮
        table.on('toolbar(stockProductList)', function (obj) {
            var checkStatus = table.checkStatus(obj.config.id);
            if ("addStock".equals(obj.event)) {
                initButtonStockProductClick();
            } else {
                layer.alert("未知操作");
            }
        });
    });
}
// 删除采购入库
function deleteStock(id) {
    $.ajax({
        type: "POST",
        url: "/dsDepot/delete.action",
        dataType: 'json',
        data: {
            id: id
        },
        success: function (data) {
            if (200 === data.code) {
                reload_table_stock();
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 审核采购入库
function auditStock(id) {
    $.ajax({
        type: "POST",
        url: "/dsDepot/audit.action",
        dataType: 'json',
        data: {
            id: id
        },
        success: function (data) {
            if (200 === data.code) {
                reload_table_stock();
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
// 初始化参数类型
function load_dianShangSupplierStock() {
    $.ajax({
        type: "POST",
        url: "/dianShangSupplier/querySupplier.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<option value=''>请选择供应商</option>";
                $.each(types, function (index, obj) {
                    /*console.log(index + "..." + obj.supplierId+"..."+obj.companyName);*/
                    content += "<option value=" + obj.supplierId + ">" + obj.companyName + "</option>";
                });
                $("#stocksupplierid").html(content);
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
function init_search_btn_stock() {
    $("#btn-search-stock").click(function () {
        reload_table_stock();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn_stock() {
    $("#btn-reset-stock").click(function () {
        productName: $("#stockproductname").val("");
        supplierId: $("#stocksupplierid").val("");
        verifyStatus: $("#stockVerifyStatus").val("");
        depotCode: $("#depotCode").val("");
        createName: $("#stockCreateName").val("");
        startTime: $("#startTime").val("");
        endTime: $("#endTime").val("");
        form.render('select');
    });
}

// 重新加载表格
function reload_table_stock() {
    stocktableIns.reload({
        url: "/dsDepot/queryDsDepot.action?temp=" + Math.random(),
        where: {
            productName: $("#stockproductname").val(),
            supplierId: $("#stocksupplierid").val(),
            verifyStatus: $("#stockVerifyStatus").val(),
            depotCode: $("#depotCode").val(),
            createName: $("#stockCreateName").val(),
            startTime: $("#startTime").val(),
            endTime: $("#endTime").val()
        }
    });
}
// 修改采购入库信息
function editStock(id) {
    layer.open({
        title: ['修改采购入库信息'],
        type: 2,
        area: ['80%', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsDepot/toDsDepotInfoPage.action?id=' + id + '&isEdit=0'
    });
}
// 查看入库信息
function previewStock(id) {
    layer.open({
        title: ['查看入库信息'],
        type: 2,
        area: ['80%', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsDepot/toPreviewStockPage.action?id=' + id + '&isEdit=1'
    });
}
// 增加采购入库信息
function initButtonStockProductClick() {
    layer.open({
        title: ['增加采购入库信息'],
        type: 2,
        area: ['80%', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsDepot/toAddDsDepotPage.action'
    });
}
function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
