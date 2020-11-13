/*#*/
/***进销存产品入库信息逻辑***/
/*#*/
var layer, element, form, laydate, table, dropdown;
var stockProductTableName = 'stock-product-table';
var dsDepotItems = $("#dsDepotItems").val();
var isEdit = $("#isEdit").val();
$(document).ready(function () {
    layui.use(['form', 'layer', 'laydate', 'element', 'table'], function () {
        layer = layui.layer;
        element = layui.element;
        form = layui.form;
        laydate = layui.laydate;
        table = layui.table;
        dropdown = layui.dropdown;
        initTable(true);
        laydate.render({
            elem: '#stock-date'
            ,format: 'yyyy-MM-dd'
        });
        /*laydate.render({
            elem: '.validTime'
            ,format: 'yyyy-MM-dd'
        });*/
        table.reload(stockProductTableName, {
            url: '',
            data: JSON.parse(dsDepotItems)
        });
        //其它费用输入框的值改变时触发
        $("#other-expenses").on("input",function(e){
            getTotalCost(stockProductTableName);
        });
        if (isEdit == 1) {
            $(".stock-table .layui-table-tool button").hide();
            $(".stock-table .layui-table-col-special").hide();
            $("#submit-stock").hide();
            $("#cancel-stock").hide();
        }
    })
});

// 将静态表格转换为layui表格
function initTable(canOperate) {
    stockProductTableName = 'stock-product-table';
    table.init(stockProductTableName,{limit: 999});
    // 监听提单信息表格里的“查”按钮
    table.on('toolbar(' + stockProductTableName + ')', function (obj) {
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (canOperate && layEvent === 'addStockInfo') {
            queryDsProduct();
        }
    });
    //库存位置信息改变
    form.on('select(stock-depotType)', function (obj) {
        var elem = $(obj.elem);
        var trElem = elem.parents('tr');
        console.log(obj.value);
        // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
        var orderData = table.cache[stockProductTableName];
        orderData[trElem.data('index')][elem.attr('name')] = obj.value;
        form.render('select');
        table.reload(stockProductTableName, {
            url: '',
            data: orderData
        });
    })
    //监听是否样品操作
    form.on('switch(stock-isSample)', function(obj){
        var orderData = table.cache[stockProductTableName];
        //根据业务判断是开启还是关闭
        var isSample = obj.elem.checked?0:1;
        //方法一取数据（根据相对位置取）
        /*var id = obj.othis.parents('tr').find("td :first").text();*/
        //方法二取数据 （根据索引table.cache里面的行数据）
        var index  = obj.othis.parents('tr').attr("data-index");
        orderData[index].isSample = isSample;
        table.reload(stockProductTableName, {
            url: '',
            data: orderData
        });
    });
    // 监听配单信息表格里的“删”按钮
    table.on('tool(' + stockProductTableName + ')', function (obj) {
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (canOperate && layEvent === 'deleteStockInfo') { //删除
            layer.confirm('真的要删除行吗？', function (index) {
                //删除对应行（tr）的DOM结构，并更新缓存
                var orderData = table.cache[stockProductTableName];
                var next = 1;
                // 计算当前行下一行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].id == obj.data.id) {
                        next = i + 1;
                        break;
                    }
                }
                // 删除数据
                orderData.splice(next - 1, 1);
                table.reload(stockProductTableName, {
                    url: '',
                    data: orderData
                });
                layer.close(index);
            });
        }
        var data = obj.data;
        if(obj.event === 'validTime'){
            var field = $(this).data('field');
            laydate.render({
                elem: this.firstChild
                , show: true //直接显示
                , closeStop: this
                , done: function (value, date) {
                    data[field] = value;
                    obj.update(data);
                    var orderData = table.cache[stockProductTableName];
                    table.reload(stockProductTableName, {
                        url: '',
                        data: orderData
                    });
                }
            });
        }
    });
    table.on('edit('+ stockProductTableName +')', function (obj) {
        // 输入内容校验
        if (obj.field === 'amount') {
            if(!$.isNumeric(obj.data.amount)) {
                obj.data.amount = 0;
                var orderData = table.cache[stockProductTableName];
                var index = 0;
                // 计算当前行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].id === obj.data.id) {
                        index = i;
                        break;
                    }
                }
                orderData[index].amount = 0;
                layer.msg("数量只能是数字!");
                table.reload(stockProductTableName, {
                    url: '',
                    data: orderData
                });
            }
        }
        if (isNotBlank(obj.data.amount)) {
            var orderData = table.cache[stockProductTableName];
            var index = 0;
            // 计算当前行的index
            for (var i = 0; i < orderData.length; i++) {
                if (orderData[i].id === obj.data.id) {
                    index = i;
                    break;
                }
            }
            // 赋值总额
            var total = parseInt(obj.data.amount) * parseFloat(obj.data.price);
            orderData[index].total = format_num(total, 2);
            table.reload(stockProductTableName, {
                url: '',
                data: orderData
            });
        }
        getTotalCost(stockProductTableName);
    });
}
/**
 * 根据调价查询电商产品，并在选中后填入配单信息表格
 *
 * @param obj “查”按钮所在行的数据
 */
function queryDsProduct() {
    openTab("选择采购产品" , "/dsDepot/matchStockProduct.action?id=01", '', '');
}
/**
 * 将产品弹窗中选中的产品加到采购单信息表格
 *
 * @param products  弹窗中选中的产品行
 */
function setStockProduct(products, id) {
    var datas = [];
    $.each(products, function (i, product) {
        var data = {};
        // 本条配货的id
        data['id'] = guid();
        // 所属产品需求的id
        /*data['id'] = id;*/
        // 配的电商产品的id
        data['productId'] = product.dsproductid;
        data['productName'] = product.productname;
        data['supplierId'] = product.supplierid;
        data['supplierName'] = product.suppliername;
        data['format'] = product.format;
        data['price'] = product.groupprice;
        data['depotType'] = '我方外租仓';
        data['isSample'] = 1;
        data['isSample'] = 1;
        data['validTime'] = '';
        datas.push(data);
    });
    var oldData = table.cache[stockProductTableName];
    var newData = oldData.concat(datas);
    reloadStockTable(newData);
}
// 重新加载配单信息表格的数据
function reloadStockTable(orderData) {
    orderData = isBlank(orderData) ? [] : (typeof orderData == 'object') ? orderData : JSON.parse(orderData);
    table.reload(stockProductTableName, {
        url: '',
        data: orderData
    });
}
// 计算采购成本总额
function getTotalCost(stockProductTableName) {
    // 包装设计费（其它费用）
    var packageDesignMoney = $('#other-expenses').val();
    // 采购的产品
    var stockProduct = table.cache[stockProductTableName];
    packageDesignMoney = isNotBlank(packageDesignMoney) ? parseFloat(packageDesignMoney) : 0.0;
    // 采购产品总额
    var productTotalCost = 0.0;
    $.each(stockProduct, function (i, product) {
        if (isNotBlank(product.price) && isNotBlank(product.amount)) {
            productTotalCost += parseFloat(product.price) * parseInt(product.amount);
        }
    });
    // 采购成本总额 = 其它费用 + 产品总额
    var totalCost = packageDesignMoney + productTotalCost;
    $('#total-payment').val(format_num(totalCost, 2));
}
// 修改提交
$("#submit-stock").click(function () {
    var otherCost = $('#other-expenses').val();
    var stockDate = $("#stock-date").val();
    var orderData = table.cache[stockProductTableName];
    orderData = isBlank(orderData) ? [] : (typeof orderData == 'object') ? orderData : JSON.parse(orderData);
    /*console.log(orderData)*/
    var DsDepotItems = new Array();
    if (orderData.length > 0) {
        var supplierId = orderData[0].supplierId;
        var supplierName = orderData[0].supplierName;
        var flag=true;
        $.each(orderData, function (i, item) {
            if (item.supplierId != supplierId) {
                flag=false;
                return layer.msg("一个批次的采购只能是一个供应商！");
            }
            if (!item.amount || item.amount == 0) {
                flag=false;
                return layer.msg("请把所有采购数量输入后再提交！");
            }
            item.total = clearComma(item.total)
            DsDepotItems.push(item);
        })
        if (!flag) {
            return false;
        }
    } else {
        return layer.msg("请先添加采购的商品！");
    }
    if (otherCost && !isPriceInteger(otherCost)) {
        $("#other-expenses").focus();
        return layer.tips("其它费用为数字，最多两位小数", $("#other-expenses"));
    }
    if (!stockDate) {
        $("#stock-date").focus();
        return layer.tips("请选择采购日期", $("#stock-date"));
    }
    var ajaxData = {
        "DsDepotItems": JSON.stringify(DsDepotItems),
        "buyTime":stockDate,
        "otherCost": otherCost?otherCost:0,
        "supplierId": supplierId,
        "supplierName": supplierName,
        "id": $("#homeId").val()
    }
    $.ajax({
        type: "POST",
        async: false,
        url: "/dsDepot/save.action",
        /*headers:{'Content-Type':'application/json;charset=utf8'},*/
        data: ajaxData,
        dataType: 'json',
        success: function (res) {
            if (res.code == 200) {
                window.parent.layer.msg("提交成功！");
                var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                window.parent.init_table_stock();
                parent.layer.close(index); //再执行关闭
            } else {
                layer.msg(res.msg);
            }
        }
    });
})
$("#cancel-stock").click(function () {
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
})
function guid() {
    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}
/**
 * 清除数字千分位
 *
 * @param s
 * @returns
 */
function clearComma(s) {
    if ($.trim(s) == "") {
        return s;
    } else {
        return (s + "").replace(/[,]/g, "");
    }
}
function isPriceInteger(s){//金额是否正确（最多两位小数）
    var re = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
    return re.test(s)
}