$(document).ready(function () {
    var table;
    var layer;
    var loadingIndex;
    layui.use(['table', 'layer'], function () {
        table = layui.table;
        layer = layui.layer;
        var fontSize = 'font-size: 12px;padding:0px;';
        var id = $('#id').val();
        loadingIndex = layer.load(2);
        //第一个实例
        table.render({
            elem: '#checkOutDetail',
            height: 'full-150',
            url: '/chargeRecord/getCheckOutDetail?id=' + id,
            page: false,
            limit: Number.MAX_VALUE,
            cols: [[{
                field: 'id',
                hide: true
            }, {
                field: 'operateTime',
                title: '到款时间',
                align: 'center',
                style: fontSize,
                unresize: false
            }, {
                field: 'serialNumber',
                title: '流水号',
                align: 'center',
                style: fontSize,
                unresize: false
            }, {
                field: 'bankName',
                title: '银行',
                align: 'center',
                style: fontSize,
                unresize: false
            }, {
                field: 'deptName',
                title: '部门',
                align: 'center',
                style: fontSize
            }, {
                field: 'realName',
                title: '销售',
                align: 'center',
                style: fontSize
            }, {
                field: 'depict',
                title: '摘要',
                align: 'center',
                style: fontSize
            }, {
                field: 'customerName',
                title: '关联客户',
                align: 'center',
                style: fontSize
            }, {
                field: 'cost',
                title: '到款',
                align: 'center',
                style: fontSize
            }, {
                field: 'checkOut',
                title: '核销状态',
                align: 'center',
                style: fontSize,
                templet: function (res) {
                    if (res.checkOut === '0' || res.checkOut === 0) {
                        return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未核销 </div>';
                    } else if (res.checkOut === '1' || res.checkOut === 1) {
                        return '<div style = "background-color:#FFB800;margin:1px;padding:1px;height:100%;width:100%;color: white"> 部分核销 </div>';
                    } else if (res.checkOut === '2' || res.checkOut === 2) {
                        return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 已核销 </div>';
                    } else {
                        return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                    }
                }
            }, {
                field: 'remainCheckOut',
                title: '可核销金额',
                align: 'center',
                style: fontSize
            }, {
                field: 'thisCheckOut',
                title: '本次核销金额',
                align: 'center',
                style: fontSize
            }, {
                field: 'remark',
                title: '备注',
                align: 'center',
                style: fontSize
            }]],
            parseData: function (res) {
                if (res.code == '200') {
                    return {
                        "code": 0, // 解析接口状态
                        "count": Number.MAX_VALUE, // 解析数据长度
                        "data": res.data
                    }
                } else {
                    return {}
                }

            },
            done: function () {
                // 取消加载中遮罩
                layer.close(loadingIndex);
            }
        });
    })
})