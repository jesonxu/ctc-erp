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
        load_dianShangSupplier();
        init_search_btn();
        init_reset_btn();
        init_table();
    });
});

var tableIns;

// 初始化表格
function init_table() {
    layui.use(['table'], function () {
        var table = layui.table;
        //列表
        tableIns = table.render({
            elem: '#productList',
            url: "/dsProduct/queryProducts.action?temp=" + Math.random(),
            request: {
                page: 'currentPage',
                limit: 'pageSize'
            },
            height: 'full-190',
            toolbar: '#table-opts',
            defaultToolbar: [],
            /*even: true,*/
            page: true,
            limit: 10,
            limits: [10, 30, 50, 100],
            method: 'POST',
            cols: [[
                {
                    type: 'numbers',
                    title: '序号',
                    width: 40
                }, {
                    field: 'producttype',
                    title: '品类',
                    align: 'center',
                    width: 60
                }, {
                    field: 'productname',
                    title: '品名',
                    align: 'center',
                    width: 80
                }, {
                    field: 'format',
                    title: '规格/型号',
                    align: 'center',
                    width: 80
                }, {
                    field: 'pcode',
                    title: '产品编码',
                    align: 'center',
                    width: 80
                }, {
                    field: 'groupprice',
                    title: '团购价格',
                    align: 'center',
                    width: 70
                }, {
                    field: 'groupnumber',
                    title: '团购起订量',
                    align: 'center',
                    width: 80
                }, {
                    field: 'wholesaleprice',
                    title: '一件代发价',
                    align: 'center',
                    width: 80
                }, {
                    field: 'rant',
                    title: '税率(%)',
                    align: 'center',
                    width: 80
                }, {
                    field: 'standardprice',
                    title: '市场价',
                    align: 'center',
                    width: 60
                }, {
                    field: 'period',
                    title: '供货周期(天)',
                    align: 'center',
                    width: 90
                }, {
                    field: 'onsale',
                    title: '是否可售',
                    align: 'center',
                    width: 80,
                    templet :function (d){
                       return getOnsale(d.onsale); // 是否可售
                    }
                }, {
                    field: 'remark',
                    title: '备注',
                    align: 'center',
                    width: 80
                }, {
                    field: 'suppliername',
                    title: '供应商',
                    align: 'center',
                    width: 80
                }, {
                    field: 'picture',
                    title: '产品图示',
                    align: 'center',
                    width: 100,
                    templet :function (d){
                       return getPicture(d.picture); // 展示图片
                    }
                }, {
                    title: '操作',
                    width: 60,
                    align: 'center',
                    toolbar: '#table-row-opts'
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
        table.on('row(productList)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });
        // 表格行 操作按钮
        table.on('tool(productList)', function (obj) {
            var data = obj.data;
            var id = data.dsproductid;
            if (obj.event === 'update') {
                editProduct(id)
            } else {
                console.error("未知操作");
            }
        });
        // 表头操作按钮
        table.on('toolbar(productList)', function (obj) {
            var checkStatus = table.checkStatus(obj.config.id);
            if ("add".equals(obj.event)) {
                initButtonClick();
            } else {
                console.log("未知操作");
            }
        });
    });
}
// 是否可售
function getOnsale(p){
    var str ="";
    if(p == "1"){
        str= "<span style='color: #5fb878;'>是</span>";
    } else {
        str= "<span style='color: #F581B1;'>否</span>";
    }
    return str;
}
// 图片展示
function getPicture(p){
    var str ="";
    var imgUrl = p.split("upFile")[1];
    imgUrl = '/upFile'+imgUrl;
    if(p!=null&&p!=""){
        str= "<img style='padding:10px' src="+imgUrl+">";
    }
    return str;
}
// 初始化参数类型
function load_dianShangSupplier() {
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
                $("#supplierid").html(content);
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
function init_search_btn() {
    $("#btn-search").click(function () {
        var rant = $("#rant").val();
        var minprice = $("#minprice").val();
        var maxprice = $("#maxprice").val();
        if (rant) {
            if (!isPositiveInteger(rant)) {
                $("#rant").focus();
                return layer.tips("税率为正整数", $("#rant"));
            }
        }
        if (minprice) {
            if (!isPriceInteger(minprice)) {
                $("#minprice").focus();
                return layer.tips("金额为数字，最多两位小数", $("#minprice"));
            }
        }
        if (maxprice) {
            if (!isPriceInteger(maxprice)) {
                $("#maxprice").focus();
                return layer.tips("金额为数字，最多两位小数", $("#maxprice"));
            }
        }
        reload_table();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn() {
    $("#btn-reset").click(function () {
        $("#productname").val("");
        $("#supplierid").val("");
        $("#onsale").val("");
        $("#rant").val("");
        $("#minprice").val("");
        $("#maxprice").val("");
        form.render('select');

    });
}

// 重新加载表格
function reload_table() {
    tableIns.reload({
        url: "/dsProduct/queryProducts.action?temp=" + Math.random(),
        where: {
            productname: $("#productname").val(),
            supplierid: $("#supplierid").val(),
            onsale: $("#onsale").val(),
            rant: $("#rant").val(),
            minprice: $("#minprice").val(),
            maxprice: $("#maxprice").val()
        }
    });
}

function editProduct(dsproductid) {
    layer.open({
        title: ['修改产品'],
        type: 2,
        area: ['700px', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsProduct/toEditDsProduct.action?productid=' + dsproductid
    });
}
function initButtonClick() {
    layer.open({
        title: ['添加产品'],
        type: 2,
        area: ['700px', '80%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/dsProduct/toAddDsProduct.action?supplierId=' + supplierId
    });
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
