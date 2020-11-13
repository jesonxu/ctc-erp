var labelArr = [];
var layer;
var form;
var element;
var formSelects;

$(document).ready(function () {
    layui.use(['layer', 'form', 'element'], function () {
        layer = layui.layer;
        form = layui.form;
        element = layui.element;
        var methodtype = $("#type").val();
        initButton(methodtype);
        loadCostPriceTypeSelect();
    });
});

// 提交添加流程
function initButton(type) {
    var url = "";
    if (type === "add") {
        url = "/productType/addProductType.action";
    } else if (type === "edit") {
        url = "/productType/editProductType.action";
    }
    $("#cancel").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index);
    });
    $("#submit").click(function () {
        var id = $("#id").val();
        var productTypeNameEle = $("#productTypeName");
        var productTypeName = productTypeNameEle.val();
        if (isBlank(productTypeName)) {
            layer.tips('请输入产品类型名称！', productTypeNameEle);
            return;
        }
        var productTypeKeyEle = $("#productTypeKey");
        var productTypeKey = productTypeKeyEle.val();
        if (isBlank(productTypeKey)) {
            layer.tips('请输入产品类型标识！', productTypeKeyEle);
            return;
        }
        var productTypeValueEle = $("#productTypeValue")
        var productTypeValue = productTypeValueEle.val();
        if (isBlank(productTypeValue)) {
            layer.tips('请输入产品类型值！', productTypeValueEle);
            return;
        } else if (!/^[0-9]*$/.test(productTypeValue)) {
            layer.tips('产品类型值只能是数字', productTypeValueEle);
            return;
        }
        var costPriceType = $("#costPriceType").val();
        if (isBlank(costPriceType)) {
            layer.msg("请选择成本类型！");
            return;
        }
        // 0平台同步，1手动配置
        var costPriceEle = $("#costPrice");
        var costPrice = costPriceEle.val();
        if (costPriceType == '1') {
            if (isBlank(costPrice) || isBlank(costPrice.trim())) {
                layer.tips('请输入成本单价！', costPriceEle);
                return;
            }
        }
        var postData = {
            id: id,
            productTypeName: productTypeName,
            productTypeKey: productTypeKey,
            productTypeValue: productTypeValue,
            costPriceType: costPriceType,
            costPrice: costPrice,
            remark: $('#remark').val()
        };
        $.post(url, postData, function (data) {
            if (data.code == 200) {
                var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                parent.layer.close(index);
                parent.layer.msg(data.msg);
                parent.tableIns.reload();
            } else {
                layer.msg(data.msg);
            }
        });
    });
}


// 加载成本类型下拉框
function loadCostPriceTypeSelect() {
    layui.use('form', function () {
        var form = layui.form;
        $.get('/productType/getCostPriceTypeSelect.action?temp=' + Math.random(), function (res) {
            var select = $("#costPriceType");
            var costPriceType = $("#costPriceTypeH").val();
            select.empty();
            if (isNotBlank(res) && res.length > 0) {
                for (var i = 0; i < res.length; i++) {
                    if (costPriceType == res[i].value) {
                        select.append('<option value="' + res[i].value + '" selected="selected">' + res[i].name + '</option>');
                    } else {
                        select.append('<option value="' + res[i].value + '">' + res[i].name + '</option>');
                    }
                }
            }

            form.on('select(costPriceType)', function (data) {
                var costPriceDiv = $('#costPriceDiv');
                if (data.value == '1') {
                    costPriceDiv.show();
                } else {
                    costPriceDiv.hide();
                }
            })
            var costPriceDiv = $('#costPriceDiv');
            if (costPriceType == '1') {
                costPriceDiv.show();
            } else {
                costPriceDiv.hide();
            }
            form.render('select');
        });
    });
}
