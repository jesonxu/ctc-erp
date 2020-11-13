var layer;
var element;

// 为表格重新设置宽度所记录的数据
var sale_statistic_year = "";

// 当前年份
var currentYear = new Date().getFullYear();

$(document).ready(function (e) {
    layui.use(['layer', 'element'], function () {
        layer = layui.layer;
        element = layui.element;
        // 加载时间（年）
        load_sale_statistics_time();
    });
});

// 加载时间（年）type：0客户类型，1客户，2产品，3过滤结果，4点击部门
function load_sale_statistics_time(type) {
    var search_dept_ids = deptIds;
    if (isNotBlank(sale_open_dept_id)) {
        // 点击的部门
        search_dept_ids = sale_open_dept_id;
    }
    if (isNotBlank(sale_open_sub_dept_id)) {
        // 点击的部门下的子部门
        search_dept_ids = search_dept_ids + "," + sale_open_sub_dept_id.join(',');
    }
    $.post("/saleStatistics/getSaleStatisticsTime", {
        deptIds: isNotBlank(search_dept_ids) ? search_dept_ids : '',
        customerTypeId: isNotBlank(sale_open_customer_type_id) ? sale_open_customer_type_id : '',
        customerId: isNotBlank(sale_customer_id) ? sale_customer_id : '',
        productId: isNotBlank(sale_product_id) ? sale_product_id : '',
        customerKeyWord: isNotBlank(customerKeyWord) ? customerKeyWord : ''
    }, function (data, status) {
        if (status === "success") {
            var statistics_ele = $("#content-statistics");
            statistics_ele.html(data);
            // 设置表格头
            show_table_title(type);
            // 渲染折叠筐
            bind_statistics_pannel_opts();
            // 初始查询当年
            $('#content-statistics div[data-my-id=' + currentYear + ']').trigger('click');
            if (!isNull(customer_module_open)) {
                customer_module_open.render();
            }
        }
    }, "html");
}

//初始化折叠框
function bind_statistics_pannel_opts() {
    // 初始化 折叠筐
    var statistics_pannel = new myPannel({
        openItem: function (item, itemId, optsType) {
            // 点击年标题
            if (optsType === "statistics_year") {
                var year = parseInt(itemId);
                // 要传递给新打开的frame的数据
                var data = {
                    'deptIds': deptIds,
                    'sale_open_customer_type_id': sale_open_customer_type_id,
                    'sale_customer_id': sale_customer_id,
                    'sale_customer_opts': isBlank(sale_customer_opts) ? '' : sale_customer_opts.join(','),
                    'sale_product_id': sale_product_id,
                    'sale_product_settle_type': sale_product_settle_type,
                    'sale_operate_year': sale_operate_year,
                    'sale_operate_month': sale_operate_month,
                    'sale_settlement_year': sale_settlement_year,
                    'sale_settlement_month': sale_settlement_month,
                    'sale_statistic_year': sale_statistic_year,
                    'sale_open_dept_id': sale_open_dept_id,
                    'sale_open_sub_dept_id': isBlank(sale_open_sub_dept_id) ? '' : sale_open_sub_dept_id.join(','),
                    'sale_open_customer_type_name': sale_open_customer_type_name,
                    'customerKeyWord': customerKeyWord
                };
                // 统计表
                $(".statistics-year-title").unbind().bind("click", function () {
                    var title = $(".statistics-year-title").html();
                    window.open("/saleStatistics/toStatisticsSheet.action?title=" + title + "&year=" + year + "&new=true&temp=" + Math.random() + "&"+$.param(data));
                    // openTabOnParent($(".statistics-year-title").html(), "/saleStatistics/toStatisticsSheet.action?year=" + year, "", "", data);
                });
                // 现金流表
                $(".cashflow-year-title").unbind().bind("click", function () {
                    var title = $(".cashflow-year-title").html();
                    // openTabOnParent($(".cashflow-year-title").html(), "/cashFlow/toCashFlowSheet.action?title=" + title + "&year=" + year + "&from=customer", "", "", data);
                    window.open("/cashFlow/toCashFlowSheet.action?title=" + title + "&year=" + year + "&from=customer&temp=" + Math.random() + "&"+$.param(data));
                });
                // 权益提成表
                $(".royalty-year-title").unbind().bind("click", function () {
                    var title = $(".royalty-year-title").html();
                    // openTabOnParent($(".royalty-year-title").html(), "/royalty/toRoyaltySheet.action?title=" + title + "&year=" + year, "", "", data);
                    window.open("/royalty/toRoyaltySheet.action?title=" + title + "&year=" + year + "&temp=" + Math.random() + "&"+$.param(data));
                });

                // 收款管理
                /*$(".income-manage-year-title").unbind().bind("click", function () {
                    openTabOnParent($(".income-manage-year-title").html(), "/fsExpenseIncome/toImportPage.action?year=" + year+ "&from=customer&type=import", "", "", data);
                });*/
                
                // 权益毛利率
                $(".gross-profit-title").unbind().bind("click", function () {
                	var title = $(".gross-profit-title").html();
                	console.log(data);
                	window.open("/saleGrossProfit/toSaleGrossProdift.action?title=" + title + "&year=" + year + "&temp=" + Math.random() + "&"+$.param(data));
                });
                
                // 利润提成表
                $(".profit-royalty-title").unbind().bind("click", function () {
                	var title = $(".profit-royalty-title").html();
                	console.log(data);
                	window.open("/realRoyalty/toRealRoyaltySheet.action?title=" + title + "&year=" + year + "&temp=" + Math.random() + "&"+$.param(data));
                });
                
                // 月度账单
                $(".month-bills-title").unbind().bind("click", function () {
                	var title = $(".month-bills-title").html();
                	console.log(data);
                	window.open("/monthBills/toMonthBillsSheet.action?title=" + title + "&year=" + year + "&temp=" + Math.random() + "&"+$.param(data));
                });
            }
        }
    });
    element.render('collapse');
    // 重新初始化pannel
    statistics_pannel.init("#statistics_body");
}

// 设置各个统计表的标题
function show_table_title(type) {
    var title = '统计表';
    var title2 = '现金流表';
    var title3 = '提成表';
    var title5 = '';
    var title6 = '';
    var title7 = '';
    if (type === 0 || type === '0') { // 点击客户类型（合同、测试等）
        title = sale_open_customer_type_name + title;
        title2 = sale_open_customer_type_name + title2;
        title3 = sale_open_customer_type_name + title3;
    } else if (type === 1 || type === '1') { // 点击客户
        title = sale_customer_name + title;
        title2 = sale_customer_name + title2;
        title3 = sale_customer_name + title3;
        title5 = sale_customer_name + '权益毛利表';
        title6 = sale_customer_name + '利润提成表';
        title7 = sale_customer_name + '月度账单表';
    } else if (type === 2 || type === '2') { // 点击产品
        title = sale_customer_name + '：' + sale_product_name + title;
        title2 = sale_customer_name + '：' + sale_product_name + title2;
        title3 = sale_customer_name + '：' + sale_product_name + title3;
        title5 = sale_customer_name + '：' + sale_product_name + '权益毛利表';
        title6 = sale_customer_name + '：' + sale_product_name + '利润提成表';
        title7 = sale_customer_name + '：' + sale_product_name + '月度账单表';
    } else if (type === 3 || type === '3') { // 部门过滤
        title = '过滤结果' + title;
        title2 = '过滤结果' + title2;
        title3 = '过滤结果' + title3;
    } else if (type === 4 || type === '4') { // 点击部门
        title = sale_open_dept_name + title;
        title2 = sale_open_dept_name + title2;
        title3 = sale_open_dept_name + title3;
    }

    $('.statistics-year-title').each(function (index, item) {
        if (title) {
            $(this).css('display', '').html(title);
        } else {
            $(this).css('display', 'none');
        }
    });
    $('.cashflow-year-title').each(function (index, item) {
        if (title2) {
            $(this).css('display', '').html(title2);
        } else {
            $(this).css('display', 'none');
        }
    });
    $('.royalty-year-title').each(function (index, item) {
        if (title3) {
            $(this).css('display', '').html(title3);
        } else {
            $(this).css('display', 'none');
        }
    });
    $('.gross-profit-title').each(function (index, item) {
    	if (title5) {
    		$(this).css('display', '').html(title5);
    	} else {
    		$(this).css('display', 'none');
    	}
    });
    $('.profit-royalty-title').each(function (index, item) {
        if (title6) {
            $(this).css('display', '').html(title6);
        } else {
            $(this).css('display', 'none');
        }
    });
    $('.month-bills-title').each(function (index, item) {
        if (title7) {
            $(this).css('display', '').html(title7);
        } else {
            $(this).css('display', 'none');
        }
    });
}