var laydate;
var layer;
var element;

layui.use(['laydate', 'layer', 'form', 'element'], function () {
    laydate = layui.laydate;
    layer = layui.layer;
    element = layui.element;
    // 添加客户按钮点击事件
    init_add_customer_btn();
    // 绑定客户过滤按钮点击事件
    bind_customer_filter();
    // init_customer();
    load_customer_type();
});

// 添加客户按钮点击事件
function init_add_customer_btn() {
    // 添加客户
    $("#add_customer_info").click(function (e) {
        layer.open({
            type: 2,
            area: ['765px', '560px'],
            fixed: false, //不固定
            maxmin: true,
            content: '/customer/toAddCustomer'
        });
    });
}

// 加载客户类型
function load_customer_type() {
    var loadingIndex = layer.load(2);
    $.ajaxSettings.async = true;
    $.ajax({
        type: "POST",
        url: "/customer/getCustomerType?temp=" + Math.random(),
        dataType: "JSON",
        data:{
            deptIds: isNotBlank(deptIds) ? deptIds : '',
            userIds: isNotBlank(userIds) ? userIds : '',
            customerId: isNotBlank(searchCustomerId) ? searchCustomerId : '',
            customerKeyWord: isNotBlank(customerKeyWord) ? customerKeyWord : ''
        },
        success: function (result) {
            if (result.code === 200 ){
                var total_customer = 0;
                var customer_types = result.data;
                if (isNotBlank(customer_types)){
                    var customer_type_item = $("div[data-alias='customer_type_item']");
                    customer_type_item.empty();
                    for (var customer_type_index = 0; customer_type_index < customer_types.length; customer_type_index++) {
                        var customer_type = customer_types[customer_type_index];
                        //计数
                        total_customer += parseInt(customer_type.customerCount);
                        var customer_type_dom = "<div class='layui-colla-item'>"
                            + " <div class='layui-colla-title' data-my-size='title-size-0' data-my-opts-type='customer_type' "
                            + " flow_ent_count= " + customer_type.flowEntCount
                            + " supplier_type_id=" + customer_type.customerTypeId
                            + " supplier_type_name=" + customer_type.customerTypeName
                            + " data-my-id = " + customer_type.customerTypeId + ">"
                            + customer_type.customerTypeName
                            + "(" + customer_type.customerCount + "家)" + "</div>"
                            + " <div class='layui-colla-content' data-content-id = " + customer_type.customerTypeId+"_>"
                            + " </div>"
                            + "</div>";
                        customer_type_item.append(customer_type_dom);
                    }
                }
                $("b[data-alias='customer_module_title']").html('客户(' + total_customer + '家)');
                // 客户类型标题的功能
                init_customer_type();
            }else{
                layer.msg(result.msg);
            }
            layer.close(loadingIndex);
        }
    });
    $.ajaxSettings.async = false;
}

// 初始化 客户类型
function init_customer_type() {
    var customer_type = new myPannel({
        openItem: function (item, itemId, optsType) {
            var t = $(item).clone();
            t.find('.layui-badge').remove();
            var item_name = $(t.find('.my_text_title')[0]).text().trim();
            var index = item_name.indexOf("(");
            item_name = item_name.substring(0,index);
            if (!customer_type.isNull(optsType)) {
                if (optsType === "customer_type") {
                    // 记录展开的客户类型ID
                    sale_record_open_customer_type(itemId, item_name);
                    // 加载 下一级
                    load_dept_customer_info(itemId, "");
                    console.log("客户类型：" + item_name);
                    // 加载统计数据
                    // load_sale_statistics_time(0)
                }
            }
        }
    });
    customer_type.init("div[data-alias='customer_type_items']");
    element.render("collapse", "customer_type_title");
}


// 加载 部门-客户 信息
var isQuery = false;
function load_dept_customer_info(customerTypeId, deptId, ossUserId) {
    var loadingIndex = layer.load(2);
    $.ajaxSettings.async = true;
    $.ajax({
        type: "POST",
        url: "/customer/getCustomers?temp=" + Math.random(),
        dataType: "JSON",
        data:{
            customerTypeId: customerTypeId,
            searchDeptIds: isNotBlank(deptIds) ? deptIds : '',
            deptId: isNotBlank(deptId) ? deptId : '',
            ossUserId: isNotBlank(ossUserId) ? ossUserId : '',
            searchUserIds: isNotBlank(userIds) ? userIds : '',
            searchCustomerId: isNotBlank(searchCustomerId) ? searchCustomerId : '',
            customerKeyWord: isNotBlank(customerKeyWord) ? customerKeyWord : ''
        },
        success: function (result) {
        	deptId = isNotBlank(ossUserId) ? ossUserId : deptId;
            if (result.code === 200 ){
                var dept_customer_infos = result.data;
                if (!isNull(dept_customer_infos) && dept_customer_infos.length > 0 ){
                    // debugger
                    var dept_customer_item = $("div[data-content-id='" + customerTypeId + "_" + deptId + "']");
                    dept_customer_item.empty();
                    // 客户 - 部门
                    // console.time('标题拼接耗时');
                    var customerInfoEdit = window.perm['customerInfoEdit'];
                    var customer_type_dom = "<div class='layui-collapse' lay-accordion lay-filter='" + customerTypeId + "_" + deptId + "' style='padding-left: 5px'>";
                    for (var dept_customer_index = 0; dept_customer_index < dept_customer_infos.length; dept_customer_index++) {
                        // 部门 或者 客户
                        var dept_customer = dept_customer_infos[dept_customer_index];
                        var item =  "<div class='layui-colla-item'>";
                        if (dept_customer.isDept == 2) { // 销售
                            item = item +
                                "<div class='layui-colla-title'" +
                                "     data-my-title-tool= 'true' " + // 销售id
                                "     data-my-id = " + dept_customer.deptId +
                                "     flow_ent_count= " + dept_customer.flowCount +
                                "     data-my-right-tool='false'" +
                                "     data-my-opts-type='customer-user'" +
                                "     data-my-dept = 'true'" +        // 销售名 
                                "     data-my-size='title-size-2'>" + dept_customer.deptName + "(" + dept_customer.customerCount + "家)" + "</div>" +
                                " <div class='layui-colla-content' data-content-id='" + customerTypeId + "_" + dept_customer.deptId + "'></div>";
                        } else if (dept_customer.isDept == 1) { // 部门
                            item = item +
                                "<div class='layui-colla-title'" +
                                "     data-my-title-tool= 'true' " +
                                "     data-my-id = " + dept_customer.deptId +
                                "     flow_ent_count= " + dept_customer.flowCount +
                                "     data-my-right-tool='false'" +
                                "     data-my-opts-type='customer-dept'" +
                                "     data-my-dept = 'true'" +
                                "     data-my-size='title-size-2'>" + dept_customer.deptName + "(" + dept_customer.customerCount + "家)" + "</div>" +
                                " <div class='layui-colla-content' data-content-id='" + customerTypeId + "_" + dept_customer.deptId + "'></div>";
                        } else { // 客户
                            item = item+
                                " <div class='layui-colla-title'" +
                                "       data-my-title-tool= 'true' " +
                                "       data-my-id = " + dept_customer.customerId +
                                "       flow_ent_count= " + dept_customer.flowCount +
                                "       data-my-right-tool=" + (customerInfoEdit && !dept_customer.onlyShowBasic) +
                                "       data-my-opts-type='customer-info'" +
                                "       data-my-tool-right-icon='layui-icon-edit'" +
                                "       data-my-folder = 'false'" +
                                "       data-my-size='title-size-2'>" + dept_customer.companyName + "</div>" +
                                " <div class='layui-colla-content' data-content-id='" + customerTypeId + "_" + dept_customer.customerId + "'></div>";
                        }
                        item += "</div>";
                        customer_type_dom += item;
                    }
                    customer_type_dom += "</div>";
                    dept_customer_item.html(customer_type_dom);
                    // console.timeEnd('标题拼接耗时');
                    // 部门和客户标题的功能
                    // console.time('标题功能初始化耗时');
                    init_dept_customer(customerTypeId, deptId, dept_customer.onlyShowBasic);
                    // console.timeEnd('标题功能初始化耗时');
                    if (clickEle && ($(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-dept"]:first').length > 0
                    		|| $(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-user"]:first').length > 0
                    		|| $(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-info"]:first').length > 0)) {
                		if ($(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-dept"]:first').length > 0) {
                			clickEle = $(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-dept"]:first');
                		} else if ($(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-user"]:first').length > 0) {
                			clickEle = $(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-user"]:first');
                		} else {
                			clickEle = $(clickEle).nextAll('.layui-colla-content').find('[data-my-opts-type="customer-info"]:first .my_text_title');
                		}
                		$(clickEle).trigger('click');
                    } else if (clickEle) { // 到客户了加载流程信息
                    	clickEle = null;
                    	reload_customer_product();
                    	reload_customer_operate();
                    	reload_customer_settlement();
                    }
                    isQuery = false;
                }else{
                    $("div[data-content-id="+ customerTypeId +"_"+deptId +"]").html("<div style='padding-left: 20px;'>暂无数据</div>");
                }
            }else{
                layer.msg(result.msg);
            }
            layer.close(loadingIndex);
        }
    });
    $.ajaxSettings.async = false;
}

// 初始化 部门-客户 折叠框
function init_dept_customer(customerTypeId, deptId, onlyShowBasic) {
    var dept_company_pannel = new myPannel({
        right: function (item, itemId, optsType) {
            // 点击右侧画笔
            if ("customer-info".equals(optsType)){
                // 编辑客户基本信息
                to_edit_customer_baseinfo(itemId)
            }
        },
        middle: function (item, itemId, optsType) {
            var loadingIndex = layer.load(2);
            console.time('标题点击事件耗时');
            // 点击部门/客户标题
            var t = $(item).clone();
            t.find('.layui-badge').remove();
            var item_name = $(t.find('.my_text_title')[0]).text().trim();
            var index = item_name.indexOf("(");
            if (index >0){
                item_name = item_name.substring(0,index);
            }
            if ("customer-info".equals(optsType)) {
                // 点击的是客户标题
                // 记录点击的客户
                sale_record_open_customer(itemId, item_name);
                // 展开客户选项（基本信息、部门信息、联系日志）
                console.time('加载客户选项耗时');
                open_customer_opinion(item, itemId, onlyShowBasic);
                console.timeEnd('加载客户选项耗时');
                // 加载客户的产品
                query_customer_products(itemId);
                // 加载客户的流程和统计数据
                load_flow_and_statistic(itemId,1);
            }else if ("customer-dept".equals(optsType)){
                // 点击的是部门标题
                load_sub_depts(itemId, function (sub_dept_ids) {
                    // 记录打开的部门id及其子部门id
                    sale_record_open_dept(itemId, sub_dept_ids, item_name);
                    // 清空产品栏
                    query_customer_products("");
                    load_flow_and_statistic(itemId,4);
                    // console.timeEnd('加载子部门耗时');
                });
            }
            console.timeEnd('标题点击事件耗时');
            layer.close(loadingIndex);
        },
        openItem: function (item, itemId, optsType) {
            // 点击左侧展开
            if ("customer-dept".equals(optsType)){
                load_dept_customer_info(customerTypeId, itemId, '');
            } else if ("customer-user".equals(optsType)) {
            	load_dept_customer_info(customerTypeId, itemId, itemId);
            }
        }
    });
    dept_company_pannel.init("div[data-content-id='" + customerTypeId +"_" + deptId + "']");
    element.render("collapse", customerTypeId + "_" + deptId);
}

// 展开客户选项（基本信息、部门信息、联系日志）
function open_customer_opinion(item, itemId, onlyShowBasic) {
    var customerInfoEdit = window.perm['customerInfoEdit'];
    var opinion = "<div class='layui-collapse' lay-accordion lay-filter='customer_opinion_" + itemId + "'>" +
        "   <div class='layui-colla-item'>" +
        "       <div class='layui-colla-title'" +
        "            data-my-id = " + itemId +
        "            data-my-opts-type='customer-base-info'" +
        "            data-my-folder = 'false'"+
        "            data-my-size='title-size-2'>基本信息" +
        "       </div>" +
        "       <div class='layui-colla-content' style='padding-left: 15px'" +
        "            customer-content-id=" + itemId + ">" +
        "      </div>" +
        "   </div>";

    // 非 只允许查看基础信息
    if (!window.perm["customerInfoOnly"] && !onlyShowBasic){
        opinion +=
            "<div class='layui-colla-item'>" +
            "    <div class='layui-colla-title'" +
            "         data-my-id = " +  itemId +
            "         data-my-right-tool=" + customerInfoEdit+
            "         data-my-opts-type='customer-dept-info'" +
            "         data-my-size='title-size-2'" +
            "         data-my-folder = 'false' "+
            "         data-my-tool-right-icon='layui-icon-edit'>部门信息" +
            "    </div>" +
            "    <div class='layui-colla-content' customer-dept-id=" + itemId +"></div>" +
            "</div>";
    }

    // 非 只允许查看基础信息
    if (!window.perm["customerInfoOnly"] && !onlyShowBasic){
        opinion +=
            "<div class='layui-colla-item'>" +
            "    <div class='layui-colla-title'" +
            "         data-my-id = " +  itemId +
            "         data-my-right-tool= " + customerInfoEdit+
            "         data-my-opts-type='customer-contact-log'" +
            "         data-my-size='title-size-2'" +
            "         data-my-folder = 'false' "+
            "         data-my-tool-right-icon='layui-icon-add-circle'>联系日志" +
            "    </div>" +
            "    <div class='layui-colla-content' id ='customer-contact-log-content"+ itemId + "'>" +
            "    </div>" +
            "</div>";
    }
    opinion += "</div>";
    var opinion_div = $(item).find('.layui-colla-content')[0];
    $(opinion_div).html(opinion);
    // 客户选项的功能
    init_customer_opinion($(opinion_div).attr('data-content-id'));
}

// 初始化 客户选项 折叠框
function init_customer_opinion(id) {
    var customer_opinions = new myPannel({
        right: function (item, itemId, optsType) {
            if ("customer-info".equals(optsType)){
                // 编辑客户基本信息
                to_edit_customer_baseinfo(itemId)
            }else if ("customer-dept-info".equals(optsType)){
                // 编辑部门信息
                to_edit_customer_dept(itemId)
            }else if ("customer-contact-log".equals(optsType)){
                // 加载联系日志
                add_contact_log(itemId);
            }
        },
        middle: function (item, itemId, optsType) {
        },
        openItem: function (item, itemId, optsType) {
            if ("customer-base-info".equals(optsType)){
                // 基本信息展开
                load_customer_info(itemId);
            }else if ("customer-dept-info".equals(optsType)){
                // 部门信息
                load_customer_dept(itemId);
            }else if ("customer-contact-log".equals(optsType)){
                // 联系日志
                load_contact_log_time(itemId)
            }
            element.render("collapse", "customer_opinion_" + itemId);
        }
    });
    // console.time("初始化客户选项事件耗时");
    customer_opinions.init("div[data-content-id=" + id +"]");
    // console.timeEnd("初始化客户选项事件耗时");
    // console.time("客户选项渲染耗时");
    element.render("collapse", "customer_opinion_" + id);
    // console.timeEnd("客户选项渲染耗时");
}

// 加载流程和统计数据
function load_flow_and_statistic(itemId, type) {
    // 加载所有运营
    if (typeof loadCustomerAllOperate == 'function') {
        loadCustomerAllOperate(itemId);
    }
    // 加载所有结算
    if (typeof loadCustomerAllSettlement == 'function') {
        loadCustomerAllSettlement(itemId, customerFlowType);
    }
    // 加载统计
    /*if (typeof load_sale_statistics_time == 'function') {
        load_sale_statistics_time(type);
    }*/
}

// 点击添加日志
function add_contact_log(supplierId) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['730px', '460px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toAddContactLog/' + supplierId + "?r=" + Math.random()
    });
}

// 按钮添加联系日志
function btn_add_contact_log(e) {
    var supplierId = $(e).attr("supplier_id");
    add_contact_log(supplierId);
}

// 根据id加载供应商信息
function load_customer_info(customer_id) {
    $.ajax({
        type: "GET",
        async: false,
        url: "/customer/readCustomerInfoById/" + customer_id + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var supplier_ele = $("div[customer-content-id=" + customer_id + "]");
            supplier_ele.html("");
            supplier_ele.html(data);
        }
    });
}

// 根据id加载客户部门信息
function load_customer_dept(customer_id) {
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupplierDeptPageById/" + customer_id + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var dept_ele = $("div[customer-dept-id=" + customer_id + "]");
            dept_ele.html("");
            dept_ele.html(data);
        }
    });
}

// 加载联系日期时间
function load_contact_log_time(customer_id) {
    var leader = isNotBlank(isSaleLeader) && isSaleLeader;
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupContactLogTimeHtmlById/" + customer_id + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var content = $('#customer-contact-log-content' + customer_id);
            content.html("");
            content.html(data);
            // 初始化 折叠筐
            var contact_pannel = new myPannel({
                openItem: function (item, itemId, optsType) {
                    // itemId 是该按钮的唯一标识id
                    if (!contact_pannel.isNull(optsType)) {
                        if (optsType === "month") {
                            var ids = itemId.split("||");
                            // 基本信息展开
                            init_contact_log(ids[0], ids[1], ids[2]);
                        }
                    }
                }
            });
            var element = layui.element;
            contact_pannel.init('#customer-contact-log-content' + customer_id);
            element.render('collapse', 'contact-log-' + customer_id);
        }
    });
}

// 初始化联系日志
function init_contact_log(customer_id, year, month) {
    var leader = isNotBlank(isSaleLeader) && isSaleLeader;
    $.ajax({
        type: "GET",
        async: false,
        url: "/supplier/readSupContactLogPageById/" + customer_id + "/" + year + "/" + month + "/" + leader + ".action?temp=" + Math.random(),
        dataType: "html",
        success: function (data) {
            var content_ele = $("div[contact_log_detail ='" + customer_id + year + month + "'");
            var last_year = content_ele.attr("the_last_year");
            var last_month = content_ele.attr("the_last_month");
            content_ele.html("");
            content_ele.html(data);
            if (last_year == year && last_month == month) {
                content_ele.append("<div class='layui-row' style='text-align: center'>" +
                    "<button type='button' supplier_id='" + customer_id + "' onclick=\"btn_add_contact_log(this)\"" +
                    " class='layui-btn layui-btn-primary layui-btn-sm contact_log_btn'>" +
                    " <i class='layui-icon layui-icon-add-circle'></i>添加日志</button></div>");
            }
        }
    });
}

// 编辑部门
function to_edit_customer_dept(customer_id) {
    openDialogIndex = layer.open({
        type: 2,
        title: ['部门信息'],
        area: ['700px', '90%'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toAddOrEditSupplierDept/' + customer_id + "?r=" + Math.random()
    });
}

// 编辑供应商基本信息
function to_edit_customer_baseinfo(customer_id) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['1000px', '640px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/customer/toEditCustomer/' + customer_id + "?r=" + Math.random()
    });
}

// 加载部门的子部门信息
function load_sub_depts(parent_id, call_back) {
    $.ajaxSettings.async = false;
    $.ajax({
        type: "POST",
        async: true,
        url: "/department/querySubDeptId?parentId=" + parent_id,
        dataType: "JSON",
        success: function (data) {
            if (typeof call_back == "function") {
                var ids = [];
                var deptIds = data.data;
                if (isNotBlank(deptIds)) {
                    for (var dept_index in deptIds){
                        ids.push(deptIds[dept_index]);
                    }
                }
                call_back(ids);
            }
        }
    });
    $.ajaxSettings.async = true;
}

// 绑定客户过滤按钮点击事件
function bind_customer_filter() {
    $(".customer-filter").unbind().bind('click', function (e) {
        open_filter_tab();
    });
}

// 打开客户过滤tab页
function open_filter_tab() {
    var area;
    if (window.perm['customerFilter']) {
        // 有按部门过滤的权限，要显示部门架构，框要大些
        area = ['400px', '600px'];
    } else {
        // 小框
        area = ['400px', '170px'];
    }
    layer.open({
        type: 2,
        title: '客户过滤',
        area: area,
        btn: ['确定', '取消'],
        btnAlign: 'c',
        fixed: false, //不固定
        maxmin: true,
        content: '/customer/toCustomerFilter.action?deptIds=' + deptIds +'&userIds='+userIds,
        yes: function (index, layero) {
            var body = layer.getChildFrame('body', index);
            customerKeyWord = $(body).find("input[id='keyWord']").val();
            deptIds = $(body).find("input[id='checkedDeptIds']").val();
            userIds = $(body).find("input[id='checkedUserIds']").val();
            layer.close(index);
        	isQuery = true;
            reload_customer();
        }
    });
}