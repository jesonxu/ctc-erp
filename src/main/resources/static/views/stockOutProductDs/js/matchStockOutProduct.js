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
        init_search_btn();
        init_reset_btn();
        init_table();
        $("#getCheckData").click(function() {
            getCheckData();
        })
    });
});
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
                $("#supplierIdStockOut").html(content);
                form.render('select');
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
var tableIns;

// 初始化表格
function init_table() {
    layui.use(['table'], function () {
        var table = layui.table;
        //列表
        tableIns = table.render({
            elem: '#productList',
            url: "/dsOutDepot/queryStockOutProductInfo.action?temp=" + Math.random(),
            request: {
                page: 'currentPage',
                limit: 'pageSize'
            },
            where: {
                productName: $("#productName").val()
            },
            height: 'full-230',
            defaultToolbar: [],
            /*even: true,*/
            page: true,
            limit: 30,
            limits: [30, 100],
            method: 'POST',
            cols: [[
                {
                    type:'checkbox',
                    width: 40
                },
                {
                    field: 'id',
                    title: '采购详情id',
                    align: 'center',
                    hide: true
                },
                {
                    field: 'depotHeadId',
                    title: '采购id',
                    align: 'center',
                    hide: true
                },
                {
                    field: 'productName',
                    title: '商品名',
                    align: 'center',
                    minWidth: 80
                }, {
                    field: 'format',
                    title: '规格/型号',
                    align: 'center',
                    width: 100
                },
                {
                    field: 'depotType',
                    title: '库存类别',
                    align: 'center',
                    width: 100
                },
                {
                    field: 'amount',
                    title: '数量',
                    align: 'center',
                    width: 80
                },
                {
                    field: 'price',
                    title: '采购单价',
                    align: 'center',
                    width: 80
                },
                {
                    field: 'validTime',
                    title: '有效日期',
                    align: 'center',
                    width: 100
                },
                {
                    field: 'supplierName',
                    title: '供应商名称',
                    align: 'center',
                    minWidth: 120
                },
                {
                    field: 'remark',
                    title: '备注',
                    align: 'center',
                    width: 80
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


        table.on('checkbox(productList)', function(obj){
            console.log(obj)
        });
    });
}
// 获取选中数据
function getCheckData(){ //获取选中数据
    var id = $("#id").val();
    var checkStatus = layui.use(['table'], function () {
        var table = layui.table;
        var data = table.checkStatus('productList').data;
        if (id == "01") {
            if (typeof window.parent.setStockOutProduct == 'function') {
                window.parent.setStockOutProduct(data, id);
            }
        } else {
            if (typeof window.parent.setMatchedStockProduct == 'function') {
                window.parent.setMatchedStockProduct(data, id);
            }
        }
        /*if (typeof window.parent.setStockOutProduct == 'function') {
            window.parent.setStockOutProduct(data, id);
        }*/
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    })
}
// 是否样品
function getIsSample(p){
    var str ="";
    if(p == "0"){
        str= "<span style='color: #5fb878;'>是</span>";
    } else {
        str= "<span style='color: #F581B1;'>否</span>";
    }
    return str;
}

// 初始化查询按钮
function init_search_btn() {
    $("#btn-search").click(function () {
        reload_table();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn() {
    $("#btn-reset").click(function () {
        $("#productName").val("");
        $("#supplierIdStockOut").val("");
    });
}

// 重新加载表格
function reload_table() {
    tableIns.reload({
        url: "/dsOutDepot/queryStockOutProductInfo.action?temp=" + Math.random(),
        where: {
            productName: $("#productName").val(),
            supplierId: $("#supplierIdStockOut").val()
        }
    });
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
