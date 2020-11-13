var layer;
var element;

// 当前年份
var currentYear = new Date().getFullYear();

$(document).ready(function (e) {
    layui.use(['layer', 'form', 'element'], function () {
        layer = layui.layer;
        element = layui.element;
        // 加载时间（年）
        load_statistics_time();
    });
});

// 加载时间（年）
function load_statistics_time(type) {
    $.post("/statistics/getStatisticsTime", {
        supplierTypeId: supplierTypeId ? supplierTypeId : '',
        supplierId: supplierId ? supplierId : '',
        productId: productId ? productId : '',
        keyWord: supplierKeyWord ? supplierKeyWord : '',
        entityType: 2
    }, function (data, status) {
        if (status === "success") {
            var statistics_ele = $("#statistics_depart");
            statistics_ele.html(data);
            // 设置表格头
            show_table_title(type);
            // 渲染折叠筐
            bind_statistics_pannel_opts();
            // 初始查询当年
            $('#statistics_depart div[data-my-id=' + currentYear + ']').trigger('click');
            /*if (!isNull(resource_module_open)) {
                resource_module_open.render();
            }*/
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
                data = {
                    productId: productId,
                    productName: productName,
                    supplierId: supplierId,
                    supplierName: supplierName,
                    customerId: customerId,
                    customerName: customerName,
                    supplierTypeId: supplierTypeId,
                    supplierTypeName: supplierTypeName,
                    customerTypeId: customerTypeId,
                    customerTypeName: customerTypeName,
                    companyName: companyName,
                    deptIds: deptIds,
                    searchCustomerId: searchCustomerId,
                    keyWord: supplierKeyWord
                };
                // 统计表
                $(".statistics-year-title").unbind().bind("click", function () {
                    // openTabOnParent($(".statistics-year-title").html(), "/statistics/toStatisticsSheet.action?year=" + year, "", "", data);
                    var title = $(".statistics-year-title").html();
                    window.open("/statistics/toStatisticsSheet.action?title=" + title + "&year=" + year + "&temp=" + Math.random() + "&"+$.param(data));
                });
                // 新统计表
                $(".statistics-year-title-new").unbind().bind("click", function () {
                    // openTabOnParent($(".statistics-year-title-new").html(), "/statistics/toStatisticsSheet.action?year=" + year + "&new=true", "", "", data);
                    var title = $(".statistics-year-title-new").html();
                    window.open("/statistics/toStatisticsSheet.action?title=" + title + "&year=" + year + "&new=true&temp=" + Math.random() + "&"+$.param(data));
                });
                // 现金流表
                $(".cashflow-year-title").unbind().bind("click", function () {
                    // openTabOnParent($(".cashflow-year-title").html(), "/cashFlow/toCashFlowSheet.action?year=" + year + "&from=supplier", "", "", data);
                    var title = $(".cashflow-year-title").html();
                    window.open("/cashFlow/toCashFlowSheet.action?title=" + title + "&year=" + year + "&from=supplier&temp=" + Math.random() + "&"+$.param(data));
                });
                // 收入管理
                $(".income-manage-year-title").unbind().bind("click", function () {
                    openTabOnParent($(".income-manage-year-title").html(), "/fsExpenseIncome/toImportPage.action?year=" + year + "&from=supplier", "", "", data);
                });
            }
        }
    });
    element.render('collapse');
    statistics_pannel.init("#statistics_body");
}

// 设置各个统计表的标题
function show_table_title(type) {
    var title = "统计表";
    var title2 = "现金流表";
    var title3 = '新统计表';
    if (type === 0 || type === '0') { // 供应商类型（移动、联通、电信、第三方）
        title = supplierTypeName + title;
        title2 = supplierTypeName + title2;
        title3 = supplierTypeName + title3;
    } else if (type === 1 || type === '1') { // 供应商
        title = supplierName + title;
        title2 = supplierName + title2;
        title3 = supplierName + title3;
    } else if (type === 2 || type === '2') { // 产品
        title = supplierName + '：' + productName + title;
        title2 = supplierName + '：' + productName + title2;
        title3 = supplierName + '：' + productName + title3;
    } else if (type === 3 || type === '3') { // 供应商名称搜索
        productId = '';
        title = '搜索结果' + title;
        title2 = '搜索结果' + title2;
        title3 = '搜索结果' + title3;
    }

    $('.statistics-year-title').each(function (index, item) {
        if (title) {
            $(this).css('display', '').html(title);
        } else {
            $(this).css('display', 'none');
        }
    });
    $('.statistics-year-title-new').each(function (index, item) {
        if (title) {
            $(this).css('display', '').html(title3);
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
}