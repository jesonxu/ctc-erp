/*#*/
/***进销存产品入库信息逻辑***/
/*#*/
var layer, element, form, laydate, table, dropdown;
var stockOutProductTableName = 'stockOut-product-table';
var dsOutDepotDetials = $("#dsOutDepotDetials").val();
var isEdit = $("#isEdit").val();
$(document).ready(function () {
    layui.use(['form', 'layer', 'laydate', 'element', 'table'], function () {
        layer = layui.layer;
        element = layui.element;
        form = layui.form;
        laydate = layui.laydate;
        table = layui.table;
        dropdown = layui.dropdown;
        load_DsCustomerStockOut();
        load_DsUserStockOut();
        initTable(true);
        laydate.render({
            elem: '#stockOutTime'
            ,format: 'yyyy-MM-dd'
        });
        table.reload(stockOutProductTableName, {
            url: '',
            data: JSON.parse(dsOutDepotDetials)
        });
        //其它费用输入框的值改变时触发
        $("#stockOutOtherCost").on("input",function(e){
            getOutTotalCost(stockOutProductTableName);
        });
    })
});
// 初始化参数类型--客户
function load_DsCustomerStockOut() {
    $.ajax({
        type: "POST",
        url: "/customer/queryAllCustomer.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var customerTypeId = $("#customerTypeId").val();
                var content = "<option value=''>请选择客户</option>";
                $.each(types, function (index, obj) {
                    if (customerTypeId == obj.customerId) {
                        content += '<option value="' + obj.customerId + '" selected="selected">' + obj.companyName + '</option>';
                    } else {
                        /*content += '<option value="' + data[i].value + '">' + data[i].name + '</option>';*/
                        content += "<option value=" + obj.customerId + ">" + obj.companyName + "</option>";
                    }
                });
                $("#customerStockOut").html(content);
                form.render('select');
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 初始化参数类型--销售人员
function load_DsUserStockOut() {
    $.ajax({
        type: "POST",
        url: "/user/queryAllUser.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var userId = $("#userId").val();
                var content = "<option value=''>请选择销售人员</option>";
                $.each(types, function (index, obj) {
                    if (userId == obj.ossUserId) {
                        content += '<option value="' + obj.ossUserId + '" selected="selected">' + obj.realName + '</option>';
                    } else {
                        /*content += '<option value="' + data[i].value + '">' + data[i].name + '</option>';*/
                        content += "<option value=" + obj.ossUserId + ">" + obj.realName + "</option>";
                    }
                });
                $("#saleStockOut").html(content);
                form.render('select');
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}
// 将静态表格转换为layui表格
function initTable(canOperate) {
    stockOutProductTableName = 'stockOut-product-table';
    table.init(stockOutProductTableName,{limit: 999});
    // 监听提单信息表格里的“查”按钮
    table.on('toolbar(' + stockOutProductTableName + ')', function (obj) {
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (canOperate && layEvent === 'addStockInfo') {
            queryDsOutProduct();
        }
    });
    // 监听配单信息表格里的“删”按钮
    table.on('tool(' + stockOutProductTableName + ')', function (obj) {
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (canOperate && layEvent === 'deleteStockOutInfo') { //删除
            layer.confirm('真的要删除行吗？', function (index) {
                //删除对应行（tr）的DOM结构，并更新缓存
                var orderData = table.cache[stockOutProductTableName];
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
                table.reload(stockOutProductTableName, {
                    url: '',
                    data: orderData
                });
                layer.close(index);
            });
        }
    });
    table.on('edit('+ stockOutProductTableName +')', function (obj) {
        // 输入内容校验
        if (obj.field === 'price') {
            if(!isPriceInteger(obj.data.price)) {
                obj.data.price = 0;
                var orderData = table.cache[stockOutProductTableName];
                var index = 0;
                // 计算当前行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].uid === obj.data.uid) {
                        index = i;
                        break;
                    }
                }
                orderData[index].price = 0;
                layer.msg("价格只能是数字，最多两位小数!");
                table.reload(stockOutProductTableName, {
                    url: '',
                    data: orderData
                });
            }
        }
        if (obj.field === 'amount') {
            if(!$.isNumeric(obj.data.amount)) {
                obj.data.amount = 0;
                var orderData = table.cache[stockOutProductTableName];
                var index = 0;
                // 计算当前行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].uid === obj.data.uid) {
                        index = i;
                        break;
                    }
                }
                orderData[index].amount = 0;
                layer.msg("数量只能是数字!");
                table.reload(stockOutProductTableName, {
                    url: '',
                    data: orderData
                });
            }
        }
        if (isNotBlank(obj.data.amount) || isNotBlank(obj.data.price)) {
            var orderData = table.cache[stockOutProductTableName];
            var index = 0;
            // 计算当前行的index
            for (var i = 0; i < orderData.length; i++) {
                if (orderData[i].uid === obj.data.uid) {
                    index = i;
                    break;
                }
            }
            // 赋值总额
            var total = parseInt(obj.data.amount) * parseFloat(obj.data.price);
            orderData[index].total = format_num(total, 2);
            table.reload(stockOutProductTableName, {
                url: '',
                data: orderData
            });
        }
        getOutTotalCost(stockOutProductTableName);
    });
}
/**
 * 根据调价查询电商产品，并在选中后填入配单信息表格
 *
 * @param obj “查”按钮所在行的数据
 */
function queryDsOutProduct() {
    openTab("选择销售出库的产品" , "/dsOutDepot/toMatchStockOutProductPage.action?id=01", '', '');
}
/**
 * 将产品弹窗中选中的产品加到采购单信息表格
 *
 * @param products  弹窗中选中的产品行
 */
function setStockOutProduct(products, id) {
    var datas = [];
    $.each(products, function (i, product) {
        var data = {};
        // 本条配货的id
        data['uid'] = guid();
        // 所属产品需求的id
        /*data['id'] = id;*/
        // 配的电商产品的id
        data['depotItemId'] = product.id;
        data['depotHeadId'] = product.depotHeadId;
        data['productId'] = product.productId;
        data['productName'] = product.productName;
        data['supplierId'] = product.supplierId;
        data['supplierName'] = product.supplierName;
        data['depotNumber'] = product.amount;
        data['format'] = product.format;
        data['depotType'] = product.depotType;
        data['validTime'] = product.validTime;
        data['remark'] = '';
        datas.push(data);
    });
    var oldData = table.cache[stockOutProductTableName];
    var newData = oldData.concat(datas);
    reloadStockOutTable(newData);
}
// 重新加载配单信息表格的数据
function reloadStockOutTable(orderData) {
    orderData = isBlank(orderData) ? [] : (typeof orderData == 'object') ? orderData : JSON.parse(orderData);
    table.reload(stockOutProductTableName, {
        url: '',
        data: orderData
    });
}
// 计算采购成本总额
function getOutTotalCost(stockOutProductTableName) {
    // 包装设计费（其它费用）
    var packageDesignMoney = $('#stockOutOtherCost').val();
    // 采购的产品
    var stockProduct = table.cache[stockOutProductTableName];
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
    $('#stockOutTotal').val(format_num(totalCost, 2));
}
$("#submit-stockOut").click(function () {
    var otherCost = $('#stockOutOtherCost').val();
    var stockDate = $("#stockOutTime").val();
    var customerId = $("#customerStockOut").val();
    var userId = $("#saleStockOut").val();
    var orderData = table.cache[stockOutProductTableName];
    orderData = isBlank(orderData) ? [] : (typeof orderData == 'object') ? orderData : JSON.parse(orderData);
    /*console.log(orderData)*/
    var DsOutDepotDetials = new Array();
    if (orderData.length > 0) {
        var flag=true;
        $.each(orderData, function (i, item) {
            if (!item.price || item.price == 0) {
                flag=false;
                return layer.msg("请把所有商品的销售单价输入后再提交！");
            }
            if (!item.amount || item.amount == 0) {
                flag=false;
                return layer.msg("请把所有采购数量输入后再提交！");
            }
            if (item.amount > item.depotNumber) {
                flag=false;
                return layer.msg(item.productName+"商品的销售数量不应该大于当前仓库商品总量！");
            }
            item.total = clearComma(item.total)
            DsOutDepotDetials.push(item);
        })
        if (!flag) {
            return false;
        }
    } else {
        return layer.msg("请先添加出库的商品！");
    }
    if (otherCost && !isPriceInteger(otherCost)) {
        $("#stockOutOtherCost").focus();
        return layer.tips("其它费用为数字，最多两位小数", $("#stockOutOtherCost"));
    }
    if (!stockDate) {
        $("#stockOutTime").focus();
        return layer.tips("请选择出库日期", $("#stockOutTime"));
    }
    var ajaxData = {
        "DsOutDepotDetials": JSON.stringify(DsOutDepotDetials),
        "outTime": stockDate,
        "otherCost": otherCost?otherCost:0,
        "customerId": customerId,
        "userId": userId,
        "id": $("#homeOutId").val()
    }
    $.ajax({
        type: "POST",
        async: false,
        url: "/dsOutDepot/save.action",
        /*headers:{'Content-Type':'application/json;charset=utf8'},*/
        data: ajaxData,
        dataType: 'json',
        success: function (res) {
            if (res.code == 200) {
                window.parent.layer.msg("提交成功！");
                var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                window.parent.init_table_stockOut();
                parent.layer.close(index); //再执行关闭
            } else {
                layer.msg(res.msg);
            }
        }
    });
})
// 取消提交
$("#cancel-stockOut").click(function () {
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