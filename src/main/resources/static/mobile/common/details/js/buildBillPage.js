layui.use(['layer', 'form', 'laydate'], function () {
    let layer = layui.layer;
    let form = layui.form;
    let laydate = layui.laydate;
    let windowIndex = parent.layer.getFrameIndex(window.name);
    let customerId = util.getUrlParam("customerId");
    loadCustomerInfo();
    initProductSelect();
    initDate();
    initButton();

    function loadCustomerInfo() {
        $.ajax({
            type: "POST",
            url: "/mobile/getEntityInfo?temp=" + Math.random(),
            dataType: 'json',
            data: {
                entityId: customerId
            },
            success: function (data) {
                if (data.code === 200 || data.code === '200') {
                    $("#companyName").html(data.data.companyName);
                } else {
                    layer.msg(data.msg);
                }
            }
        });
    }

    /**
     * 初始化产品选择
     */
    function initProductSelect() {
        $.ajax({
            type: "POST",
            url: "/customerProduct/getProductSelect?temp=" + Math.random(),
            dataType: 'json',
            data: {
                customerId: customerId
            },
            success: function (data) {
                // debugger;
                let productItems = [];
                if (util.arrayNotNull(data)) {
                    $(data).each(function (index, item) {
                        productItems.push("<li><input type='checkbox' name='product' lay-skin='primary' value='" + item.value + "' title='" + item.name + "'/></li>")
                    });
                } else {
                    productItems.push("<li>客户没有产品</li>")
                }
                let productListDom = "<ul class='product-choose-list'>" + productItems.join("") + "</ul>";
                $("#productChoose").html(productListDom);
                form.render();
            }
        });
    }

    /**
     * 初始化时间
     */
    function initDate() {
        let today = new Date();
        let year = today.getFullYear();
        let month = today.getMonth() + 1; // 自然月
        let maxValue = year + "-" + month;
        // 默认上月
        month -= 1;
        if (month === 0) {
            year -= 1;
            month = 12;
        }
        if (month < 10) {
            month = '0' + month;
        }
        let yearMonth = year + '-' + month;
        laydate.render({
            elem: '#billMonth',
            type: 'month',
            trigger: 'click',
            value: yearMonth,
            max: maxValue
        });
    }

    /**
     * 初始化按钮
     */
    function initButton() {
        let loading = null;
        let doing = false;
        $('#submit').unbind().bind('click', function () {
            if (doing) {
                return;
            }
            let checkedProduct = $("input[name='product']:checked");
            let productIds = [];
            if (checkedProduct.length > 0) {
                $(checkedProduct).each(function (index, item) {
                    productIds.push($(item).val());
                });
            }
            if (util.arrayNull(productIds)) {
                layer.msg("请先选择产品");
                return;
            }
            let billMonth = $('#billMonth').val();
            if (util.isNull(billMonth)) {
                layer.msg('请选择账单月份');
                return;
            }
            doing = true;
            loading = layer.load(2);
            $(this).attr({"disabled": "disabled"});
            $.post('/bill/buildBill', {
                productIds: productIds.join(","),
                billMonth: billMonth,
                redo: 'false'
            }, function (res) {
                doing = false;
                $('#submit').removeAttr("disabled");
                layer.close(loading);
                if (res.code === 200 || res.code === '200') {
                    layer.msg("账单生成成功<br/>" + res.msg, {time: 2000});
                    setTimeout(function () {
                        // 延迟关闭
                        close();
                    }, 1000);
                } else {
                    layer.msg(res.msg, {time: 2000});
                }
            })
        });

        /**
         * 取消按钮
         */
        $('#cancel').unbind().bind('click', function () {
            close();
        });

        /**
         * 关闭方法
         */
        function close() {
            let closeBtn = $("#layui-layer" + windowIndex, window.parent.document).find("a[class*='layui-layer-close1']");
            $(closeBtn)[0].click();
        }
    }
});
