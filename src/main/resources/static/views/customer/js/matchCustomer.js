layui.use(['table'], function () {
    var table = layui.table;
    var fontSize = 'font-size: 12px;';
    var id = table.render({
        elem: '#test',
        url: '/customer/queryCustomerByName',
        request: {
            pageName: 'currentPage',
            limitName: 'pageSize'
        },
        where: {companyName: window.companyName},
        page: true,
        method: "POST",
        height: 200,
        cols: [[
            {
                field: 'companyId', width: 50, title: '序号',align:"center",style: fontSize,
                templet: function (d) {
                    return d.LAY_TABLE_INDEX + 1;
                }
            },
            {field: 'customerType', width: 100, align:"center", style: fontSize, title: '客户类型'},
            {field: 'companyName', width: 100, style: fontSize, title: '公司名'},
            {field: 'saleName', width: 100, style: fontSize, title: '销售'},
            {field: 'createTime', width: 140, align:"center", style: fontSize, title: '创建时间'}
        ]],
        parseData: function (data) {
            if (data.code === 200) {
                if (!window.parent.isNull(data.data)) {
                    return data.data;
                }
            }
            return {
                "code": 200,
                "msg": data.msg,
                "count": 0,
                "data": {}
            };
        },
        limit:5,
        limits: [5, 10,15,50,100]
    });
});