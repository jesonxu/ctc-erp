layui.use(['layer', 'element', "form"], function () {
    let layer = layui.layer;
    let form = layui.form;
    /**
     * 加载客户类型
     */
    loadCustomerType();

    /**
     * 加载客户类型
     */
    function loadCustomerType() {
        $.ajax({
            type: "POST",
            url: '/customer/getCustomerType.action?v=' + new Date().getTime(),
            data: {},
            success: function (result) {
                let customerTypeInfos = result.data;
                let customerTypes = [];
                if (util.isNotNull(customerTypeInfos)) {
                    $(customerTypeInfos).each(function (index, cusType) {
                        let customerCount = cusType.customerCount;
                        let customerName = cusType.customerTypeName;
                        let customerTypeId = cusType.customerTypeId;
                        customerTypes.push("<div class='customer-type-item' data-type-id='" + customerTypeId + "'>" + customerName + "(" + customerCount + "家)" + "</div>");
                    })
                }
                let customerTypeEle = $("#customerTypeInfos");
                customerTypeEle.html(customerTypes.join(""));
                // 绑定事件
                let customerTypeItems = customerTypeEle.find("div[data-type-id]");
                $(customerTypeItems).each(function (index, customerItem) {
                    $(customerItem).unbind("click").bind("click", function (event) {
                        let customerTypeId = $(this).attr("data-type-id");
                        loadCustomerList(this, customerTypeId, null, null, null, null, null, null);
                    });
                });
            },
            error: function (data) {
                layer.msg("加载客户类型信息错误");
            }
        });
    }

    /**
     * 加载客户信息
     * @param customerTypeId 客户类型ID
     * @param deptId 部门ID
     * @param thisEle 点击的元素
     * @param searchDeptIds
     * @param userId
     * @param searchUserIds
     * @param searchCustomerId
     * @param customerKeyWord
     */
    function loadCustomerList(thisEle, customerTypeId, deptId, userId, searchDeptIds, searchUserIds, searchCustomerId, customerKeyWord) {
        let referId = util.isNotNull(userId) ? userId : (util.isNotNull(deptId) ? deptId : (util.isNotNull(customerTypeId) ? customerTypeId : ""));
        let referEle = $(thisEle).parent().find("div[data-ref-id='" + referId + "']");
        if (referEle.length > 0) {
            $(referEle).remove();
            return;
        }
        $.ajax({
            type: "POST",
            url: '/customer/getCustomers?v=' + new Date().getTime(),
            data: {
                customerTypeId: customerTypeId,
                searchDeptIds: searchDeptIds,
                deptId: deptId,
                ossUserId: userId,
                searchUserIds: searchUserIds,
                searchCustomerId: searchCustomerId,
                customerKeyWord: customerKeyWord,
            },
            success: function (result) {
                let customers = result.data;
                let childEle = $("<div class='customer-detail' data-ref-id='" + referId + "'></div>");
                if (util.isNull(customers) || customers.length === 0) {
                    layer.msg("暂无数据");
                    return;
                }
                $(customers).each(function (index, customer) {
                    let customerTypeInfo = " data-customer-type-id ='" + customerTypeId + "'";
                    // 部门
                    let deptIdInfo = "";
                    // 用户
                    let userIdInfo = "";
                    // 客户
                    let customerIdInfo = "";
                    // 数据项名称
                    let itemName = "";

                    let customerId = customer.customerId;
                    let deptId = customer.deptId;
                    let customerName = customer.companyName;
                    let deptName = customer.deptName;
                    let customerCount = util.formatBlank(customer.customerCount, "0");

                    //  0 客户 1 部门 2 销售
                    let deptType = parseInt(util.formatBlank(customer.isDept, "0"));
                    if (deptType === 0) {
                        customerIdInfo = " data-customer-id='" + customerId + "'"
                        itemName = customerName;
                    } else if (deptType === 1) {
                        deptIdInfo = " data-dept-id='" + deptId + "'";
                        itemName = deptName + "（" + customerCount + "）";
                    } else if (deptType === 2) {
                        userIdInfo = " data-user-id='" + deptId + "'";
                        itemName = deptName + "（" + customerCount + "）";
                    }
                    childEle.append("<div class='customer-item' " + customerTypeInfo + deptIdInfo + userIdInfo + customerIdInfo + ">" + itemName + "</div>");
                });
                // 放到紧邻当前元素的后面
                $(thisEle).after(childEle.prop("outerHTML"));

                // 关联的详细内容元素
                let detailEle = $(thisEle).parent().find("div[data-ref-id='" + referId + "']");
                // 部门
                let deptEles = $(detailEle).find("div[data-dept-id]");
                if (deptEles.length > 0) {
                    $(deptEles).each(function (index, deptEle) {
                        $(deptEle).unbind("click").bind("click", function (event) {
                            let customerTypeId = $(this).attr("data-customer-type-id");
                            let deptId = $(this).attr("data-dept-id");
                            loadCustomerList(this, customerTypeId, deptId, null, null, null, null, null);
                            event.stopPropagation();
                        });
                    });
                }

                // 销售
                let userEles = $(detailEle).find("div[data-user-id]");
                if (userEles.length > 0) {
                    $(userEles).each(function (index, userEle) {
                        $(userEle).unbind("click").bind("click", function (event) {
                            let userId = $(this).attr("data-user-id");
                            let customerTypeId = $(this).attr("data-customer-type-id");
                            // openDeptId 填了 userId，用于查询员工下的客户
                            loadCustomerList(this, customerTypeId, userId, userId, null, null, null, null);
                            event.stopPropagation();
                        });
                    });
                }

                // 客户
                let customerEles = $(detailEle).find("div[data-customer-id]");
                if (customerEles.length > 0) {
                    $(customerEles).each(function (index, customerEle) {
                        $(customerEle).unbind("click").bind("click", function (event) {
                            let customerId = $(this).attr("data-customer-id");
                            loadCustomerInfo(customerId);
                            event.stopPropagation();
                        });
                    });
                }
            },
            error: function (data) {
            }
        })
    }


    /**
     * 加载客户详情
     * @param customerId
     */
    function loadCustomerInfo(customerId) {
        // 打开新的页面
        window.location.href = "/mobile/toCustomerDetail?customerId=" + customerId;
    }

    form.render();
});


