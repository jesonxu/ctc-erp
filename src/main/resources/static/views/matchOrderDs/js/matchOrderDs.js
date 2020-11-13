/*#*/
/***配货工作台逻辑***/
/*#*/
var layer, element, form, laydate, table, dropdown;
var applyOrderTableName;
var matchOrderTableName;
$(document).ready(function () {
    layui.use(['form', 'layer', 'laydate', 'element', 'table'], function () {
        layer = layui.layer;
        element = layui.element;
        form = layui.form;
        laydate = layui.laydate;
        table = layui.table;
        dropdown = layui.dropdown;
        // initTable();
        laydate.render({
            elem: '#date'
            ,format: 'yyyy-MM-dd'
        });
    })
});

/**配单基础信息展示**/
function setData(type, data) {   // type：1 是去配货， 2是查看详情
    if(type == "1") {
        clearSetData();
        $("#audit-opinion").parent().parent(".order_details").show();
    }
    var ele_id = 'matchOrder_' + data.flowEntId;
    $('div[name=flowMsg]').attr('id', ele_id);
    $('xmp[name=flowData]').text(JSON.stringify(data));
    var applyOrderLabelName = getApplyOrderLabelName(data.labelList);
    var matchOrderLabelName = getMatchOrderLabelName(data.labelList);
    applyOrderTableName = 'order-table-' + ele_id + applyOrderLabelName;
    matchOrderTableName = 'match-order-table-' + ele_id + matchOrderLabelName;
    $("table.apply-order-table").attr('lay-filter', applyOrderTableName);
    $("table.apply-order-table").attr('lay-id', applyOrderTableName);
    $("table.apply-order-table").attr('id', applyOrderTableName);
    $("table.match-order-table").attr('lay-filter', matchOrderTableName);
    $("table.match-order-table").attr('lay-id', matchOrderTableName);
    $("table.match-order-table").attr('id', matchOrderTableName);

    initTable(data.canOperat);
    // 初始化驳回给哪些节点的下拉框
    initMatchOrderAudit(data.flowId, ele_id, data.nodeIndex, data.canOperat);
    // 流程标签内容
    var labelValueMap = data.labelValueMap;
    if (labelValueMap) {
        $("#orderNumber").text(labelValueMap['订单编号']);
        $("#salesMoney").text(thousand(labelValueMap['采购金额']));
        $("#deliverDate").text(labelValueMap['交付日期']);
        $("#payType").text(labelValueMap['付款形式']);
        /*$("#payCycle").text(labelValueMap['付款周期']);*/
        if (labelValueMap['付款形式'] == '预付费') {
            $("#payCycle").text('0');
        } else {
            $("#payCycle").text(labelValueMap['付款周期']);
        }
        $("#packageDesignMoney").text(labelValueMap['包装设计费']);
        $("#invoiceType").text(labelValueMap['发票种类']);
        $("#invoicePoint").text(labelValueMap['发票税点']);
        $("#deliveryType").text(labelValueMap['发货形式']);
        $("#deliveryAddress").text(labelValueMap['配送地址']);
        $("#deliveryAddress").attr("title",labelValueMap['配送地址']);
        $("#orderNumber").text(labelValueMap['订单编号']);
        $("#purchaseCost").val(thousand(labelValueMap['采购成本总额']));
        $("#logisticsCosts").val(thousand(labelValueMap['采购物流费']));
        $("#date").val(labelValueMap['配货单有效截止日期']);
        $("#deliveryAddressFile").html("").append(deliveryAddressFileToString(labelValueMap['配送地址附件']))
        reloadApplyOrderTable(labelValueMap[applyOrderLabelName]);
        reloadMatchOrderTable(labelValueMap[matchOrderLabelName]);
        // type：1 是去配货， 2是查看详情
        if(type == "2") {
            // 禁用采购单有效期
            $("#date").attr("disabled","disabled");
            $("#audit-opinion").parent().parent(".order_details").hide();
            // 禁用配单备注列的编辑
            var remarkFields = $("div.match-order-table").find("td[data-field='remark']");
            $.each(remarkFields, function (i, field) {
                $(field).removeAttrs('data-edit');
            });
            // 禁用配单数量列的编辑
            var amountFields = $("div.match-order-table").find("td[data-field='amount']");
            $.each(amountFields, function (i, field) {
                $(field).removeAttrs('data-edit');
            });
            // 禁用配单物流费列的编辑
            var amountFields = $("div.match-order-table").find("td[data-field='logisticsCost']");
            $.each(amountFields, function (i, field) {
                $(field).removeAttrs('data-edit');
            });
        } else {
            // 启用采购单有效期
            $("#date").removeAttrs("disabled");
        }
    }
    if (data.ossUserName) {
        $("#ossUserName").text(data.ossUserName);
    }
    $("#customerName").text(data.supplierName); // 客户名称
    $("#projectName").text(data.productName);  // 项目名称
    /*// 物流费输入框失去焦点时，自动计算采购成本总额
    $('#logisticsCosts').unbind().bind('blur', function () {
        getTotalCost(matchOrderTableName);
    })*/
}

// 将静态表格转换为layui表格
function initTable(canOperate) {
    table.init(applyOrderTableName, {limit: 999});
    table.init(matchOrderTableName, {limit: 999});
    // 监听提单信息表格里的“查”按钮
    table.on('tool(' + applyOrderTableName + ')', function (obj) {
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (canOperate && layEvent === 'query') {
            queryDsProduct(obj);
        } else if (canOperate && layEvent === 'queryStock') {
            queryDsStockProduct(obj)
        }
    });
    // 监听配单信息表格里的“删”按钮
    table.on('tool(' + matchOrderTableName + ')', function (obj) {
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (canOperate && layEvent === 'delete') { //删除
            layer.confirm('真的要删除行吗？', function (index) {
                //删除对应行（tr）的DOM结构，并更新缓存
                var orderData = table.cache[matchOrderTableName];
                var next = 1;
                // 计算当前行下一行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].uid == obj.data.uid) {
                        next = i + 1;
                        break;
                    }
                }
                // 删除数据
                orderData.splice(next - 1, 1);
                table.reload(matchOrderTableName, {
                    url: '',
                    data: orderData
                });
                layer.close(index);
            });
        }
    });
    table.on('edit('+ matchOrderTableName +')', function (obj) {
        // 输入内容校验
        if (obj.field === 'amount') {
            if(!$.isNumeric(obj.data.amount)) {
                obj.data.amount = 0;
                var orderData = table.cache[matchOrderTableName];
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
                table.reload(matchOrderTableName, {
                    url: '',
                    data: orderData
                });
            }
        }
        if (isNotBlank(obj.data.amount)) {
            var orderData = table.cache[matchOrderTableName];
            var index = 0;
            // 计算当前行的index
            for (var i = 0; i < orderData.length; i++) {
                if (orderData[i].uid === obj.data.uid) {
                    index = i;
                    break;
                }
            }
            if (orderData[index].depotItemId) {
                if (obj.data.amount > orderData[index].depotNumber) {
                    layer.msg(obj.data.productname +"的库存数量不够出库的数量，请重新填写!");
                }
            }
            // 赋值总额
            var total = parseInt(obj.data.amount) * parseFloat(obj.data.price);
            orderData[index].total = format_num(total, 2);
            table.reload(matchOrderTableName, {
                url: '',
                data: orderData
            });
        }
        // 物流费
        if (obj.field === 'logisticsCost') {
            if(!$.isNumeric(obj.data.logisticsCost)) {
                obj.data.logisticsCost = 0;
                var orderData = table.cache[matchOrderTableName];
                var index = 0;
                // 计算当前行的index
                for (var i = 0; i < orderData.length; i++) {
                    if (orderData[i].uid === obj.data.uid) {
                        index = i;
                        break;
                    }
                }
                orderData[index].logisticsCost = 0;
                layer.msg("物流费只能是数字!");
                table.reload(matchOrderTableName, {
                    url: '',
                    data: orderData
                });
            }
        }
        getTotalCost(matchOrderTableName);
    });
}

// 重新加载提单信息表格的数据
function reloadApplyOrderTable(orderData) {
    orderData = isBlank(orderData) ? [] : (typeof orderData == 'object') ? orderData : JSON.parse(orderData);
    table.reload(applyOrderTableName, {
        url: '',
        data: orderData
    });
}

// 重新加载配单信息表格的数据
function reloadMatchOrderTable(orderData) {
    orderData = isBlank(orderData) ? [] : (typeof orderData == 'object') ? orderData : JSON.parse(orderData);
    table.reload(matchOrderTableName, {
        url: '',
        data: orderData
    });
}


/**
 * 根据调价查询电商产品，并在选中后填入配单信息表格
 *
 * @param obj “查”按钮所在行的数据
 */
function queryDsProduct(obj) {
    var data = obj.data;
    var productName = data.productname;
    var id = data.id;
    openTab("查询页面 - " + productName, "/dsMatchOrder/toQueryDsProduct.action?id=" + id + "&productName=" + productName, '', '');
}

/**
 * 根据调价查询电商产品，并在选中后填入配单信息表格
 *
 * @param obj “查库存”按钮所在行的数据
 */
function queryDsStockProduct(obj) {
    var data = obj.data;
    var productName = data.productname;
    var id = data.id;
    openTab("查询库存页面 - " + productName, "/dsOutDepot/toMatchStockOutProductPage.action?id=" + id + "&productName=" + productName, '', '');
}

/**
 * 将产品弹窗中选中的产品加到配单信息表格
 *
 * @param products  弹窗中选中的产品行
 */
function setMatchedProduct(products, id) {
    var deliveryType = $('#deliveryType').text();
    var datas = [];
    $.each(products, function (i, product) {
        var data = {};
        // 本条配货的id
        data['uid'] = guid();
        // 所属产品需求的id
        data['id'] = id;
        // 配的电商产品的id
        data['dsproductid'] = product.dsproductid;
        data['productname'] = product.productname;
        data['supplierid'] = product.supplierid;
        data['suppliername'] = product.suppliername;
        data['logisticsCost'] = 0;
        data['format'] = product.format;
        data['price'] = deliveryType === '集采' ? product.groupprice : product.wholesaleprice;
        datas.push(data);
    });
    var oldData = table.cache[matchOrderTableName];
    var newData = oldData.concat(datas);
    reloadMatchOrderTable(newData);
}
/**
 * 将产品弹窗中选中的产品加到配单信息表格
 *
 * @param products  弹窗中选中的产品行
 */
function setMatchedStockProduct(products, id) {
    var datas = [];
    $.each(products, function (i, product) {
        var data = {};
        // 本条配货的id
        data['uid'] = guid();
        // 所属产品库存需求的id
        data['id'] = id;
        // 配的电商产品的id
        data['depotItemId'] = product.id;
        data['depotHeadId'] = product.depotHeadId;
        data['dsproductid'] = product.productId;
        data['productname'] = product.productName;
        data['supplierid'] = product.supplierId;
        data['suppliername'] = product.supplierName;
        data['depotNumber'] = product.amount;
        data['format'] = product.format;
        data['depotType'] = product.depotType;
        data['price'] =  product.price;
        data['logisticsCost'] = 0;
        datas.push(data);
    });
    var oldData = table.cache[matchOrderTableName];
    var newData = oldData.concat(datas);
    reloadMatchOrderTable(newData);
}
/**
 * 从标签设计中获取“提单信息”类型的标签名
 *
 * @param labelList
 * @returns {string}
 */
function getApplyOrderLabelName(labelList) {
    var labelName = '提单信息';
    labelList = (typeof labelList == 'object') ? labelList : JSON.parse(labelList);
    for (var label in labelList) {
        if (label['type'] === '24' || label['type'] === 24) {
            labelName = label['name'];
            break;
        }
    }
    return labelName;
}

/**
 * 从标签设计中获取“配单信息”类型的标签名
 *
 * @param labelList
 * @returns {string}
 */
function getMatchOrderLabelName(labelList) {
    var labelName = '配单信息';
    labelList = (typeof labelList == 'object') ? labelList : JSON.parse(labelList);
    for (var label in labelList) {
        if (label['type'] === '25' || label['type'] === 25) {
            labelName = label['name'];
            break;
        }
    }
    return labelName;
}

// 初始化审核按钮，通过和选择驳回到哪个节点
function initMatchOrderAudit(flowId, ele_id, nodeIndex, canOperate) {
    if (!canOperate) {
        $('#matchOrderAudit').html('');
        return
    }
    var pass_dom = "<button type=\"button\" class=\"layui-btn layui-btn-sm\" onclick='audit(2, \"" + ele_id + "\")'><i class=\"layui-icon layui-icon-ok\"></i>通过</button>"
    var data = {
        flowId: flowId,
        nodeIndex: nodeIndex
    };
    var reject_dom = "<div class=\"layui-dropdown\" style=\"padding: 0 5px\">" +
        "<button type='button' class='layui-btn layui-btn-danger layui-btn-sm'><i class='layui-icon layui-icon-triangle-r' style='right: -2px;margin-top: -8px;'></i>驳回给</button>";
    $.ajax({
        type: "POST",
        async: false,
        url: "/flow/getFlowNodeBefore.action",
        dataType: 'json',
        data: data,
        success: function (res) {
            if (res.code == 200) {
                reject_dom +=  "<ul>";
                $.each(res.data, function (index, item) {
                    reject_dom = reject_dom + "<li><a href='javascript:void(0)' onclick='reject_confirm(\"" + item.nodeName + "\", \"" + item.roleName + "\", \"" + ele_id + "\", " + index + ");'>" + item.nodeName + "[" + item.roleName + "]</a></li>";
                });
                reject_dom = reject_dom + "</ul></div>";
            }
        }
    });
    $('#matchOrderAudit').html(pass_dom + reject_dom);
    dropdown.render();
}

// 去配货清空原来的赋值data
function clearSetData() {
    // 销售提单信息
    $("#orderNumber").text("");
    $("#ossUserName").text("");
    $("#projectName").text("");
    $("#customerName").text("");
    $("#salesMoney").text("");
    $("#deliverDate").text("");
    $("#payType").text("");
    $("#payCycle").text("");
    $("#packageDesignMoney").text("");
    $("#invoiceType").text("");
    $("#invoicePoint").text("");
    $("#deliveryType").text("");
    $("#deliveryAddress").text("");
    $("#deliveryAddressFile").html("");
    // 配单员输入
    $("#logisticsCosts").val("");
    $("#purchaseCost").val("");
    $("#date").val("");
    $("#audit-opinion").val("");
}

// 审核通过清空配货
function clearMatchOrder() {
    // 销售提单信息
    $("#orderNumber").text("");
    $("#ossUserName").text("");
    $("#projectName").text("");
    $("#customerName").text("");
    $("#salesMoney").text("");
    $("#deliverDate").text("");
    $("#payType").text("");
    $("#payCycle").text("");
    $("#packageDesignMoney").text("");
    $("#invoiceType").text("");
    $("#invoicePoint").text("");
    $("#deliveryType").text("");
    $("#deliveryAddress").text("");
    $("#deliveryAddressFile").html("");
    // 配单员输入
    $("#logisticsCosts").val("");
    $("#purchaseCost").val("");
    $("#date").val("");
    $("#audit-opinion").val("");
    // 清除提单配单
    reloadApplyOrderTable();
    reloadMatchOrderTable();
    initMatchOrderAudit(null, null, null, false);
}

// 计算采购成本总额
function getTotalCost(matchOrderTableName) {
    // 包装设计费
    var packageDesignMoney = $('#packageDesignMoney').text();
    // 配单产品
    var matchOrderData = table.cache[matchOrderTableName];
    logisticsCosts = isNotBlank(logisticsCosts) ? parseFloat(logisticsCosts) : 0.0;
    packageDesignMoney = isNotBlank(packageDesignMoney) ? parseFloat(packageDesignMoney) : 0.0;
    // 配单产品总额
    var productTotalCost = 0.0;
    var costs = 0.0; //配单物流费总额
    $.each(matchOrderData, function (i, product) {
        if (isNotBlank(product.price) && isNotBlank(product.amount)) {
            productTotalCost += parseFloat(product.price) * parseInt(product.amount);
        }
        if (isNotBlank(product.logisticsCost)) {
            costs += parseFloat(product.logisticsCost);
        }
    });
    // 采购物流费总额页面显示
    $('#logisticsCosts').val(format_num(costs, 2));
    // 采购成本总额 = 采购物流费 + 包装设计费 + 产品总额
    var totalCost = costs + packageDesignMoney + productTotalCost;
    $('#purchaseCost').val(format_num(totalCost, 2));
}

// 配送地址文件类型下载
function deliveryAddressFileToString(labelValue) {
    var arr = [];
    var html = "";
    var fileArray =(!labelValue) ? arr : (typeof labelValue == 'object') ? labelValue : JSON.parse(labelValue);
    if (fileArray.length === 0) {
        return html +=  "无";
    }
    $.each(fileArray, function (i, file) {
        var fileJson = JSON.stringify(file);
        html += "<a style='text-decoration: underline;color:#f00;' href='javascript:void(0);' onclick='down_load(" + fileJson + ")'>" + file.fileName + "</a>&nbsp;&nbsp;";
    });
    html +=  "(点击下载)";
    return html;
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}