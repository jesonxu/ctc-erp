layui.use(["layer", 'table', 'flow'], function () {
    let layer = layui.layer;
    let flow = layui.flow;
    let companyName = Base64.decode(util.getUrlParam("companyName"));
    loadCustomer(companyName);

    function loadCustomer(companyName) {
        flow.load({
            elem: '#customer-infos',
            done: function (page, next) {
                let loadIndex = layer.load(1, {
                    shade: [0.1, '#fff']
                });
                $.ajax({
                    type: "POST",
                    url: "/customer/queryCustomerByName",
                    dataType: "json",
                    data: {
                        "companyName": companyName,
                        "currentPage": page,
                        "pageSize": 10
                    },
                    success: function (data) {
                        let pageInfo = data.data;
                        let customers = pageInfo.data;
                        let customerListDom = renderCustomerList(customers);
                        next(customerListDom.join(''), page < pageInfo.totalPages);
                        layer.close(loadIndex);
                    },
                    error: function (data) {
                        $("#cutomer-infos").html("暂无数据")
                    }
                });
            }
        });
    }

    /**
     * 渲染客户信息
     * @param customers
     */
    function renderCustomerList(customers) {
        let customerDomList = [];
        if (util.isNull(customers) || customers.length === 0) {
            return customerDomList;
        }
        $(customers).each(function (index, customer) {
            if (util.isNull(customer)) {
                return false;
            }
            let customerDom = [];
            customerDom.push("<ul class='customer-item'>");
            customerDom.push("<li><span class='line-title'>客户类型：</span><span class='line-value'>" + customer.customerType + "</span></li>");
            customerDom.push("<li><span class='line-title'>客户名：</span><span class='line-value'>" + customer.companyName + "</span></li>");
            customerDom.push("<li><span class='line-title'>销售：</span><span class='line-value'>" + customer.saleName + "</span></li>");
            customerDom.push("<li><span class='line-title'>创建时间：</span><span class='line-value'>" + customer.createTimeStr + "</span></li>");
            customerDom.push("</ul>");
            customerDomList.push(customerDom.join(""));
        });
        return customerDomList;
    }
});