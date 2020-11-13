var laydate;
var layer;
var element;
// 页面弹框索引，用来关闭
var openDialogIndex;

$(document).ready(function () {
    laydate = layui.laydate;
    layer = layui.layer;
    element = layui.element;
    // 添加供应商按钮点击事件
    init_add_supplier_btn();
    init_supplier();
});

// 添加供应商按钮点击事件
function init_add_supplier_btn() {
    // 添加供应商
    $("#add_supplier_info").click(function (e) {
        //iframe层-父子操作
        layer.open({
            type: 2,
            area: ['765px', '560px'],
            fixed: false, //不固定
            maxmin: true,
            content: '/dianShangSupplier/toAddDsSupperPage'
        });
    });
}

// 初始化 折叠筐
function init_supplier() {
    // 初始化 折叠筐
    var pannel = new myPannel({
        right: function (item, itemId, optsType) {
            if (!pannel.isNull(optsType)) {
                if (optsType == 1) {
                    // 编辑基本信息
                    to_edit_supperlier_baseinfo(itemId)
                } else if (optsType == 3) {
                    // 编辑部门
                    to_edit_supplier_dept(itemId)
                } else if (optsType == 4) {
                    // 添加联系日志
                    add_contact_log(itemId);
                }
            }
        },
        middle: function (item, itemId, optsType) {
            if (!pannel.isNull(optsType) && optsType == 1) {
                // 加载的时间较长，增加加载中图标
                var loadingIndex = layer.load(2);
                if (typeof record_supplier_info == "function") {
                    // 记录供应商id
                    record_supplier_info(itemId);
                }
                
                var t = $(item).clone();
                t.find('.layui-badge').remove();
                // 产品id和名称置空
                productId = '';
                productName = '';
                supplierName = $(t.find('.my_text_title')[0]).text().trim();
                supplierId = itemId;
                
                // 点击正文 查询对应产品
                /*loadSupplierProducts(itemId);*/

                // 加载所有运营
                if (typeof loadSupplierAllOperate == 'function') {
                    loadSupplierAllOperate(itemId);
                }
                // 加载所有结算
                if (typeof loadSupplierAllSettlement == 'function') {
                    loadSupplierAllSettlement(itemId);
                }
                // 加载统计
                if (typeof load_statistics_time == 'function') {
                    load_statistics_time(1);
                }
                layer.close(loadingIndex);
            }
        },
        openItem: function (item, itemId, optsType) {
            // 展开事件多用于 内容展示
            // 根据操作类型确定是哪一类进行操作的
            // itemId 是该按钮的唯一标识id
            if (!pannel.isNull(optsType)) {
                if (optsType == 0) {
                	// 产品和供应商置为空
                	productId = '';
                	productName = '';
                	supplierId = '';
                	supplierName = '';
                	
                	supplierTypeName = $(item).find('div[supplier_type_name]').attr('supplier_type_name');
                	supplierTypeId = $(item).find('div[supplier_type_id]').attr('supplier_type_id');
                	
                	// 加载统计数据
                	load_statistics_time(0);
                } else if (optsType == 2) {
                    // 基本信息展开
                    load_supplier_info(itemId);
                } else if (optsType == 3) {
                    // 部门信息
                    load_supplier_dept(itemId);
                } else if (optsType == 4) {
                    //操作日志
                    load_contact_log_time(itemId)
                }
            }
        },
        closeItem: function (item, itemId, optsType) {
            // 关闭触发事件
            // 关闭的时候，删除 大于等于 当前操作 的所有opts
        }
    });
    pannel.init("#muti_pannel");
}

// 点击添加日志
function add_contact_log(supplierId) {
    //iframe层-父子操作
    openDialogIndex = layer.open({
        type: 2,
        area: ['730px', '460px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toAddContactLog/' + supplierId + "?r=" + Math.random()
    });
}

function btn_add_contact_log(e) {
    var supplierId = $(e).attr("supplier_id");
    add_contact_log(supplierId);
}

// 根据id加载供应商信息
function load_supplier_info(supplier_id) {
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupplierInfoById/" + supplier_id + ".action?entityType=2&temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var supplier_ele = $("div[supplier-content-id=" + supplier_id + "]");
            supplier_ele.html("");
            supplier_ele.html(data);
        }
    });
}
// 电商扩展解析到页面
function extendDs() {
    var extend = $("#extendDs").val()
    extendDs = extend ? JSON.parse(extend) : {};
}

// 根据id加载供应商部门信息
function load_supplier_dept(supplier_id) {
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupplierDeptPageById/" + supplier_id + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var dept_ele = $("div[supplier-dept-id=" + supplier_id + "]");
            dept_ele.html("");
            dept_ele.html(data);
        }
    });
}

// 加载联系日期时间
function load_contact_log_time(supplier_id) {
    var leader = isNotBlank(isLeader) && isLeader;
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupContactLogTimeHtmlById/" + supplier_id + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var content = $('#supplier-contact-log-content' + supplier_id);
            content.html("");
            content.html(data);
            // 初始化 折叠筐
            var contact_pannel = new myPannel({
                openItem: function (item, itemId, optsType) {
                    // 展开事件多用于 内容展示
                    // 根据操作类型确定是哪一类进行操作的
                    // itemId 是该按钮的唯一标识id
                    if (!contact_pannel.isNull(optsType)) {
                        if (optsType == "month") {
                            var ids = itemId.split("||");
                            // 基本信息展开
                            init_contact_log(ids[0], ids[1], ids[2]);
                        }
                    }
                }
            });
            var element = layui.element;
            contact_pannel.init('#supplier-contact-log-content' + supplier_id);
            element.render('collapse', 'contact-log-' + supplier_id);
        }
    });
}

// 初始化联系日志
function init_contact_log(supplier_id, year, month) {
    var leader = isNotBlank(isLeader) && isLeader;
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupContactLogPageById/" + supplier_id + "/" + year + "/" + month + "/" + leader + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var content_ele = $("div[contact_log_detail ='" + supplier_id + year + month + "'");
            var last_year = content_ele.attr("the_last_year");
            var last_month = content_ele.attr("the_last_month");
            content_ele.html("");
            content_ele.html(data);
            if (last_year == year && last_month == month) {
                content_ele.append("<div class='layui-row' style='text-align: center'>" +
                    "<button type='button' supplier_id = '" + supplier_id + "' onclick=\"btn_add_contact_log(this)\"" +
                    " class='layui-btn layui-btn-primary layui-btn-sm contact_log_btn'>" +
                    " <i class='layui-icon layui-icon-add-circle'></i>添加日志</button></div>");
            }
        }
    });
}

// 编辑部门
function to_edit_supplier_dept(supplier_id) {
    //iframe层-父子操作
    openDialogIndex = layer.open({
        type: 2,
        title: ['部门信息'],
        area: ['700px', '90%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toAddOrEditSupplierDept/' + supplier_id + "?r=" + Math.random()
    });
}

// 编辑供应商基本信息
function to_edit_supperlier_baseinfo(supplier_id) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['765px', '560px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toEditSupperlierBaseinfo/' + supplier_id + "?entityType=2&r=" + Math.random()
    });
}