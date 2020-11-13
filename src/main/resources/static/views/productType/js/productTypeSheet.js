var tableIns;
var insStart;
var insEnd;

$(document).ready(function () {
    initButton();
    initDate();
    initTable();
})

function initTable() {
    layui.use('table', function () {
        var table = layui.table;
        tableIns = table.render({
            elem: '#productType',
            url: '/productType/readPages?temp=' + Math.random(),
            toolbar: '#headerToolbar',
            defaultToolbar: false,
            limit: 15,
            limits: [15, 30, 60, 100],
            method: 'POST',
            height: 'full-140',
            page: true,
            cols: [[
                {
                    field: 'id',
                    title: 'id',
                    align: 'center',
                    hide: true
                }, {
                    field: 'productTypeName',
                    title: '类型名称',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'productTypeKey',
                    title: '类型标识',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'productTypeValue',
                    title: '类型值',
                    align: 'center',
                    width: '10%',
                    sort: true
                }, {
                    field: 'visible',
                    title: '可见状态',
                    align: 'center',
                    width: '10%',
                    templet: function (data) {
                        if (data.visible == '0') {
                            return '<div style = "background-color:#FF5722;margin:1px;padding:1px;height:100%;width:100%;color: white"> 隐藏 </div>';
                        } else if (data.visible == '1') {
                            return '<div style = "background-color:#5FB878;margin:1px;padding:1px;height:100%;width:100%;color: white"> 展示 </div>';
                        } else {
                            return '<div style = "background-color:#C6C6C6;margin:1px;padding:1px;height:100%;width:100%;color: white"> 未知 </div>';
                        }
                    }
                }, {
                    field: 'costPriceType',
                    title: '成本类型',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'costPrice',
                    title: '成本单价',
                    align: 'center',
                    width: '10%'
                }, {
                    field: 'userName',
                    title: '创建人',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'wtime',
                    title: '创建时间',
                    align: 'center',
                    width: '10%',
                }, {
                    field: 'remark',
                    title: '备注',
                    align: 'center',
                    width: '10%',
                }, {
                    title: '操作',
                    toolbar: '#rowToolbar',
                    align: 'center',
                    width: '10%'
                }
            ]],
            parseData: function (res) { // res 即为原始返回的数据
                return {
                    "code": 0, // 解析接口状态
                    "count": res.data.count, // 解析数据长度
                    "data": res.data.data
                    // 解析数据列表
                };
            }
        });
        table.on('row(productType)', function (obj) {
            obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
        });

        //头工具栏事件
        table.on('toolbar(productType)', function(obj){
            if (obj.event === 'add') {
                layui.layer.open({
                    title: '添加产品类型',
                    type: 2,
                    content: '/productType/toUpdateProductType.action?type=add&temp=' + Math.random(),
                    area: ['480px', '360px']
                });
            }
        });

        //监听行工具事件
        table.on('tool(productType)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                layer.confirm('确定删除产品类型：' + data.productTypeName + '?', function(index){
                    $.post("/productType/deleteProductType.action?id=" + data.id + "&temp=" + Math.random(), function (res) {
                        layer.msg(res.msg, {time: 5000});
                        parent.tableIns.reload();
                    });
                });
            } else if(obj.event === 'edit') {
                layui.layer.open({
                    title: '编辑产品类型',
                    type: 2,
                    content: "/productType/toUpdateProductType.action?type=edit&id=" + data.id + "&temp=" + Math.random(),
                    area: ['480px', '360px']
                });
            } else if (obj.event === 'show') {
                layer.confirm('确定展示：' + data.productTypeName + '?', function(index){
                    $.post("/productType/toggleVisible.action?id=" + data.id + "&visible=1", function (res) {
                        layer.msg(res.msg);
                        parent.tableIns.reload();
                    });
                });
            } else if (obj.event === 'hide') {
                layer.confirm('确定隐藏：' + data.productTypeName + '?', function(index){
                    $.post("/productType/toggleVisible.action?id=" + data.id + "&visible=0", function (res) {
                        layer.msg(res.msg);
                        parent.tableIns.reload();
                    });
                });
            }
        });
    })
}

function initButton() {
    $("#btn-search").click(function () {
        search();
    });
}

function search() {
    var productTypeName = $('#productTypeName').val();
    var date = $('#date').val();
    var endDate = $('#endDate').val();
    var visible = [];
    var checkedEle = $("input[name=visible]:checked");
    if (checkedEle.length > 0) {
        $(checkedEle).each(function () {
            visible.push($(this).val())
        })
    } else {
        layer.msg('请选择一个状态');
        return false;
    }
    tableIns.reload({
        url: "/productType/readPages.action?temp=" + Math.random(),
        where: {
            productTypeName: productTypeName,
            date: date + ' 00:00:00',
            endDate: endDate + ' 23:59:59',
            visible: visible.join(',')
        }
    });
}

function initDate() {
    var nowDate = new Date();
    var today = {
        date: nowDate.getDate(),
        month: nowDate.getMonth(),
        year: nowDate.getFullYear()
    }
    $("#date").val(today.year, today.month, 1);
    $("#endDate").val(today.year, today.month, today.date);
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        insStart = laydate.render({
            elem : '#date',
            value : '2020-07-01',
            format : 'yyyy-MM-dd',
            max : 0,
            type : 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新结束日期的最小日期
                insEnd.config.min = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });

        insEnd = laydate.render({
            elem: '#endDate',
            value: new Date(),
            format: 'yyyy-MM-dd',
            max: 0,
            type: 'date',
            trigger: 'click',
            done: function (value, date) {
                // 更新开始日期的最大日期
                insStart.config.max = lay.extend({}, date, {
                    date: date.date,
                    month: date.month - 1
                });
            }
        });
    });
}