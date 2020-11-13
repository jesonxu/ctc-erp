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
        init_search_btn();
        init_reset_btn();
        init_table();
        $("#getCheckData").click(function() {
            getCheckData();
        })
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
            where: {
                productname: $("#productname").val()
            },
            height: 'full-230',
            defaultToolbar: [],
            /*even: true,*/
            page: true,
            limit: 300,
            limits: [300],
            method: 'POST',
            cols: [[
                {
                    type:'checkbox',
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
        if (typeof window.parent.setMatchedProduct == 'function') {
            window.parent.setMatchedProduct(data, id);
        }
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    })
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

// 初始化查询按钮
function init_search_btn() {
    $("#btn-search").click(function () {
        reload_table();
    });
}

// 初始化 重置查询 按钮
function init_reset_btn() {
    $("#btn-reset").click(function () {
        $("#productname").val("");
    });
}

// 重新加载表格
function reload_table() {
    tableIns.reload({
        url: "/dsProduct/queryProducts.action?temp=" + Math.random(),
        where: {
            productname: $("#productname").val()
        }
    });
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}
