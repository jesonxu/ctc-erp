var table = "";
var editCssHtml = "<span style='position: absolute;left: 10px;' class='layui-icon layui-icon-edit' title='修改'></span>";
layui.use(['table', 'form'], function () {
    table = layui.table;
    form = layui.form;
    table.render({
        url: "/invoice/getVoidInvoiceBills.action?temp=" + Math.random(),
        elem: '#selectBillInfo',
        even: true,
        page: false,
        method: 'POST',
        cols: [[{
            field: 'checked',
            title: '选择',
            type: 'checkbox',
            width: 50
        }, {
            field: 'id',
            title: '账单id',
            align: 'center',
            hide: true
        }, {
            field: 'title',
            title: '账单信息',
            align: 'center',
            width: 200,
            templet: function (data) {
                return '<span title="' + data.title + '">' + data.title + '</span>'
            }
        }, {
            field: 'receivables',
            title: '应开金额(元)',
            align: 'right',
            width: 100,
            templet: function (data) {
                return thousand(data.receivables);
            }
        }, {
            field: 'actualInvoiceAmount',
            title: '已开金额(元)',
            align: 'right',
            width: 100,
            hide: true,
            templet: function (data) {
                return thousand(data.actualInvoiceAmount);
            }
        }, {
            title: '可开金额(元)',
            align: 'right',
            width: 100,
            hide: true,
            templet: function (data) {
                if (isNotBlank(data.receivables) && isNotBlank(data.actualInvoiceAmount)) {
                    return thousand(data.receivables - data.actualInvoiceAmount);
                }
                return '';
            }
        }, {
            field: "thisReceivables",
            title: '该账单本次开票(元)',
            align: 'right',
            width: 100,
            templet: function (data) {
                return thousand(data.thisReceivables);
            }
        }, {
            title: '扣减金额(元)',
            field: 'deductionAmount',
            width: 100,
            align: "right",
            templet: function (data) {
                var res = data.deductionAmount == undefined ? 0 : data.deductionAmount;
                if (isBlank(res) || !isNumber(res)) {
                    res = 0;
                }
                return editCssHtml + res;
            }
        }
        ]],
        parseData: function (res) {
            return {
                "code": 0,
                "count": res.count,
                "data": res.data.productBillsJSONObjectList,
            };
        },
        where: {
            invoiceId: $("#invoiceId").val().trim(),
        },
        done: function () {
            calcTotal();
        }
    });

    table.on('edit(select-bill-info)', function (obj) {
        // 限制输入格式
        if (!isNumber(obj.value)) {
            $(this).val(0);
            //同步更新缓存对应的值
            obj.update({
                deductionAmount: 0
            });
            calcTotal();
            return;
        }
        if (obj.data.thisReceivables < parseFloat(obj.value)) {
            $(this).val(0);
            //同步更新缓存对应的值
            obj.update({
                deductionAmount: 0
            });
            calcTotal();
            layer.msg("扣减金额不能大于本次开票金额");
            return;
        }
        obj.update({
            deductionAmount: parseFloat(obj.value)
        });
        calcTotal();
    });

    table.on('checkbox(select-bill-info)', function (obj) {
        calcTotal();
        //开启扣减列编辑
        if (obj.checked) {
            $(this).parent().parent().parent().find('td[data-field=deductionAmount]').data('edit', "text");
        } else {
            $(this).parent().parent().parent().find('td[data-field=deductionAmount]').data('edit', false);
        }
    });
});


/**
 * 获取表格选中数据
 * @returns {{data: [], isAll: boolean}|*}
 */
function obtainTableData() {
    return table.checkStatus('selectBillInfo');
}

/**
 * 获取到表格中所有选中的记录，计算出扣减金额总和
 */
var calcTotal = function () {
    setTimeout(function () {
        var checkStatus = table.checkStatus('selectBillInfo');
        var selectData = checkStatus.data;
        if (selectData && selectData.length > 0) {
            calculateDeductionAmount(selectData)
        } else {
            // 没有选中的记录
            $(".bill_info_sum_pay").text(0.00);
        }
    }, 10);
};

/**
 * 计算扣减金额总和
 * @param data
 */
function calculateDeductionAmount(data) {
    var sum = 0.00;
    for (var i = 0; i < data.length; i++) {
        if (data[i].deductionAmount != undefined) {
            sum = accAdd(sum, parseFloat(data[i].deductionAmount));
        }
    }
    $(".bill_info_sum_pay").text(sum.toFixed(2));
}

