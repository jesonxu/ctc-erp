var initdate;
var isLeader = false;
var isSaleMan = false;
var isSaleLeader = false;
var isCustomerConsole = false;

// 运营、结算分页加载的参数
var operatePage = 1;
var operatePages; // 在后台返回时赋值
var settlementPage = 1;
var settlementPages; // 在后台返回时赋值
// 结算分栏，当前展示的栏目
var customerFlowType = 2;

// 公共参数
var productId = '';
var productName = '';
var supplierId = '';
var supplierName = '';
var customerId = '';
var customerName = '';
var supplierTypeId = '';
var supplierTypeName = '';
var customerTypeId = '';
var customerTypeName = '';

// 筛选条件 公司名（可模糊搜索）
var companyName = '';
// 过滤条件 部门id（以逗号分隔）
var deptIds = '';
//过滤条件 销售userId（以逗号分隔）
var userIds = '';
// 查询条件 客户id
var searchCustomerId = '';
// 查询条件 客户关键词
var customerKeyWord = '';
// 查询条件 供应商关键词
var supplierKeyWord = '';

$(document).ready(function() {
    //ajax全局判断 sesion超时跳转到登陆页面
    $(this).ajaxComplete(function(event, request, settings) {
        if (request.status == 600) {
            window.top.location.href = "/erp"
        }
    });
});

String.prototype.endWith = function (str) {
	var reg = new RegExp(str + "$");
	return reg.test(this);
}

Date.prototype.getLastMonth = function () {
    var year = this.getFullYear();
    var month = this.getMonth(); // 0~11
    if (month === 0){
        year = year - 1;
        month = 12;
    }
    return year + '-' + month;
}

Date.prototype.getNextMonth = function () {
    var year = this.getFullYear();
    var month = this.getMonth() + 2; // 0~11 -> 2~13
    if (month > 12){
        year = year + 1;
        month = 1;
    }
    return year + '-' + month;
}

//js计算精度问题 +
function accAdd(data1, data2) {
    var r1, r2, m, c;
    try {
        r1 = data1.toString().split(".")[1].length;
    } catch(e) {
        r1 = 0;
    }
    try {
        r2 = data2.toString().split(".")[1].length;
    } catch(e) {
        r2 = 0;
    }
    c = Math.abs(r1 - r2);
    m = Math.pow(10, Math.max(r1, r2));
    if (c > 0) {
        var cm = Math.pow(10, c);
        if (r1 > r2) {
            data1 = Number(data1.toString().replace(".", ""));
            data2 = Number(data2.toString().replace(".", "")) * cm;
        } else {
            data1 = Number(data1.toString().replace(".", "")) * cm;
            data2 = Number(data2.toString().replace(".", ""));
        }
    } else {
        data1 = Number(data1.toString().replace(".", ""));
        data2 = Number(data2.toString().replace(".", ""));
    }
    return (data1 + data2) / m;
}

// -
function accSub(data1, data2) {
	var r1, r2, m, n; 
	try {
		r1 = (data1 + '').split(".")[1].length;
	} catch (e) {
		r1 = 0;
	} 
	try {
		r2 = (data2 + '').split(".")[1].length;
	} catch (e) {
		r2 = 0;
	} 
	m = Math.pow(10, Math.max(r1, r2)); 
	n = (r1 >= r2) ? r1 : r2; 
	return parseFloat(((data1 * m - data2 * m) / m).toFixed(n)); 
}

// *
function accMulti(data1, data2) {
    var baseData = 0;
    try {
        baseData += data1.toString().split(".")[1].length;
    } catch(e) {}
    try {
        baseData += data2.toString().split(".")[1].length;
    } catch(e) {}
    return Number(data1.toString().replace(".", "")) * Number(data2.toString().replace(".", "")) / Math.pow(10, baseData);
}

// /
function accDiv(data1, data2) {
    var baseData1 = 0,
    baseData2 = 0,
    Num1, Num2;
    try {
        baseData1 = data1.toString().split(".")[1].length
    } catch(e) {}
    try {
        baseData2 = data2.toString().split(".")[1].length
    } catch(e) {}
    with(Math) {
        Num1 = Number(data1.toString().replace(".", ""));
        Num2 = Number(data2.toString().replace(".", ""));
        return (Num1 / Num2) * pow(10, baseData2 - baseData1);
    }
}

/*
 * 描述：动态取得国际化文件参数
 */
function getMsg(key) {
    var value;
    var lang = $("#lang").val();
    if (lang == null || lang == '') {
        lang = $("#lang", parent.document).val();
        if (lang == null || lang == '') {
            lang = $("#lang", parent.parent.document).val();
            if (lang == null || lang == '') {
                lang = $("#lang", parent.parent.parent.document).val();
                if (lang == null || lang == '') {
                    lang = $("#lang", parent.parent.parent.parent.document)
                        .val();
                    if (lang == null || lang == '') {
                        lang = 'zh_CN';
                    }
                }
            }
        }
    }
    if ($.i18n && $.i18n.map) {
        value = $.i18n.get(key);
        if (value !== undefined && value !== null && value !== '') {
            return  value;
        }
    }
    jQuery.i18n.properties({
        name : 'messages', // 资源文件名称
        path : '/i18n/', // 资源文件路径
        mode : 'map', // 用Map的方式使用资源文件中的值
        cache: true,
        encoding : 'UTF-8',
        language : lang,
        callback : function() { // 加载成功后设置显示内容
            value = $.i18n.prop(key);
        }
    });
    return value;
}

function isBlank(value) {
    var flag = false;
    if (typeof value == 'undefined' || value == null || value === '' || value.length === 0 || value === "null") {
        flag = true;
    }
    return flag;
}

function isNotBlank(value) {
    var flag = false;
    if (typeof value != 'undefined' && value != null && value !== '') {
        flag = true;
    }
    return flag;
}

function openTab(title, url, icon, skin) {
    var index = layui.layer.open({
        title: [title, 'font-size:18px;'],
        type: 2,
        content: url,
        icon: icon,
        skin: skin,
        area: ['100%', '100%'],
        success: function(layero, index) {
            layui.layer.tips('点击此处返回上一层', '.layui-layer-setwin .layui-layer-close', {
                tips: 3,
            });
        }
    });
    return index;
}

// 从父页面打开frame，data是要向新打开的frame传递的数据
function openTabOnParent(title, url, icon, skin, data) {
    var index = window.parent.layer.open({
        title: [title, 'font-size:18px; background-color: #cce4fc;'],
        type: 2,
        content: url,
        icon: icon,
        skin: skin,
        area: ['100%', '100%'],
        closeBtn: 2,
        success: function(layero, index) {
            // 要传递给新打开的frame的数据
            if (isBlank(data)) {
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
                    customerKeyWord: customerKeyWord,
                    supplierKeyWord: supplierKeyWord
                };
            }
            // 向弹窗传值
            var contentWindow = window.parent.$("#layui-layer-iframe" + index)[0].contentWindow;
            if (typeof contentWindow.setParams == 'function') {
                contentWindow.setParams(data);
            }
            if (typeof contentWindow.init == 'function') {
                // 解决document.ready 和 success回调 执行先后顺序不确定的问题
                contentWindow.init();
            }
            // 重新调整关闭按钮位置
            window.parent.$('.layui-layer-setwin .layui-layer-close').css({'right':'-1px', 'top':'-10px'});
            /* window.parent.layui.layer.tips('点击此处返回上一层', '.layui-layer-setwin .layui-layer-close', {
                 tips: 3,
             });*/
        }
    });
    return index;
}

/**
 * 数字千分位
 * @param num
 * @returns
 */
function formatNum(num) {
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    return (num + '').replace(reg, '$&,');
}

// 处理千分位
function thousand(num) {
    if (!num) {
        return 0;
    }
    var reg = /\d{1,3}(?=(\d{3})+$)/g;
    var tempArr = (num + '').split('.');
    return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
}


function getInitdate() {
    $.ajax({
        url: "/parameter/initSystemDate.action?temp=" + Math.random(),
        type: 'POST',
        async: false,
        success: function(data) {
            var obj = new Function("return" + data)();
            if (obj.result == "success") {
                initdate = obj.msg + " 00:00:00";
            } else {
                initdate = "2000-01-01 00:00:00";
            }
        }
    });
}

function getInitdateof() {
    $.ajax({
        url: "/parameter/initSystemDate.action?temp=" + Math.random(),
        type: 'POST',
        async: false,
        success: function(data) {
            var obj = new Function("return" + data)();
            if (obj.result == "success") {
                initdate = obj.msg;
            } else {
                initdate = "2000-01-01";
            }
        }
    });
}

// 刷新资源工作台的气泡
function renderZYFlowEntCount() {
    $.ajax({
        url: "/resourceConsole/queryFlowEntCount.action?temp=" + Math.random(),
        data: {
            productId: productId,
            supplierId: supplierId,
            keyWord: supplierKeyWord
        },
        type: 'POST',
        async: true,
        success: function(data) {
            // 清理所有的气泡信息
            $('.my_span_title span.layui-badge').remove();
            $('.span_title span.layui-badge').remove();

            // 供应商类型
            // {'402812816d39753b016d39897c8c000a': 1, '402812816d39753b016d39897c8c000b': 1}
            for (var key in data.supplierTypeCount) {
                if (data.supplierTypeCount[key]) {
                    var titleSpan = $('div[supplier_type_id="' + key + '"]').prev().find('.my_text_title');
                    $(titleSpan).after('<span class="layui-badge" style="float: right">' + data.supplierTypeCount[key] + '</span>');
                }
            }

            // 供应商
            // {'402812816d39753b016d39897c8c000a': 1, '402812816d39753b016d39897c8c000b': 1}
            for (var key in data.supplierCount) {
                if (data.supplierCount[key]) {
                    var titleSpan = $($('div[data-my-id="' + key + '"]>.my_text_title')[0]);
                    $(titleSpan).after('<span class="layui-badge" style="float: right">' + data.supplierCount[key] + '</span>');
                }
            }

            // 产品
            // {'402812816d39753b016d39897c8c000a': 1, '402812816d39753b016d39897c8c000b': 1}
            for (var key in data.productCount) {
                if (data.productCount[key]) {
                    var titleSpan = $('#productCard div[data-my-id="' + key + '"]>.my_text_title');
                    $(titleSpan).after('<span class="layui-badge" style="float: right">' + data.productCount[key] + '</span>');
                }
            }

            // 运营 
            // operateYearCount {2019: 1} 
            // operateCount {2019: {9: 1, 10: 2}}
            // 年
            for (var yearKey in data.operateYearCount) {
            	if (data.operateYearCount[yearKey]) {
            		// 运营年份title
                    var operate_year_dom = $("#recordBody div[data-my-id='" + yearKey + "']>.my_text_title");
                    // 运营年份气泡数据更新
                    operate_year_dom.after('<span class="layui-badge" style="float: right">' + data.operateYearCount[yearKey] + '</span>');
            	}
            	
            	var operateCount = data.operateCount[yearKey];
            	// 月
                for (var monthKey in operateCount) {
                	if (operateCount[monthKey]) {
                		// 运营月份title
                		var operate_month_dom = $("#recordBody div[data-my-id='" + yearKey + "-" + monthKey + "']>.my_text_title");
                		// 更新运营月份气泡
                		operate_month_dom.after('<span class="layui-badge" style="float: right">' + operateCount[monthKey] + '</span>');
                	}
                }
            }

            // 结算
            // settleYearCount {2019: 1} 
            // settleCount {2019: {9: 1, 10: 2}}
            // 年
            for (var yearKey in data.settleYearCount) {
            	if (data.settleYearCount[yearKey]) {
            		var settlement_year_ele = $("#settlement_body div[data-my-opts-type='settlement_year'][data-my-id$='" + yearKey + "']>.my_text_title");
            		if (settlement_year_ele.length == 0) {
            			settlement_year_ele = $("#settlement_body div[data-my-opts-type='settlement_year'][data-my-tag$='" + yearKey + "']>.my_text_title");
            		}
                    settlement_year_ele.after('<span class="layui-badge" style="float: right">' + data.settleYearCount[yearKey] + '</span>');
            	}
            	
            	var settleCount = data.settleCount[yearKey];
            	// 月
                for (var monthKey in settleCount) {
                	if (settleCount[monthKey]) {
                		// 结算月份 title
                        var settlement_month_ele = $("#settlement_body div[data-my-opts-type='settlement_month'][data-my-id$='" + yearKey + "||" + monthKey + "']>.my_text_title");
                        // 更新结算月份气泡
                        settlement_month_ele.after('<span class="layui-badge" style="float: right">' + settleCount[monthKey] + '</span>');
                	}
                }
            }
        }
    });
}

/**
 * customerTypeId,
 * productId ,
 * customerId,
 * searchDeptIds,
 * openDeptId
 */
// 刷新客户工作台的气泡
function renderKHFlowEntCount() {
    $.ajax({
        url: "/customerConsole/queryCustomerFlowEntCount.action?temp=" + Math.random(),
        data: {
            customerTypeId: sale_open_customer_type_id,
            openDeptId: sale_open_dept_id,
            customerId: sale_customer_id,
            productId: productId,
            searchDeptIds: deptIds,
            customerKeyWord: customerKeyWord
        },
        type: 'POST',
        async: true,
        success: function(data) {
            console.log("---------------------刷新数据---------------------");
            console.log(JSON.stringify(data));
            // 清理所有的气泡信息
            $('.my_span_title span.layui-badge').remove();
            $('.span_title span.layui-badge').remove();

            // 客户类型
            // {'402812816d39753b016d39897c8c000a': 1, '402812816d39753b016d39897c8c000b': 1}
            var customer_type_counts = data.customerTypeCount;
            if (isNotBlank(customer_type_counts)){
                for (var customer_type_key in customer_type_counts) {
                    var customer_type_count = customer_type_counts[customer_type_key];
                    console.log("1-客户类型未处理流程项：" + customer_type_key + "----------" + customer_type_count);
                    if (parseInt(customer_type_count) > 0 ) {
                        var customerTypeTitleSpan = $('div[supplier_type_id="' + customer_type_key + '"]').prev().find('.my_text_title');
                        $(customerTypeTitleSpan).after('<span class="layui-badge" style="float: right">' + customer_type_count + '</span>');
                    }
                }
            }

            // 客户
            // {'402812816d39753b016d39897c8c000a': 1, '402812816d39753b016d39897c8c000b': 1}
            var customer_counts =data.customerCount;
            if (isNotBlank(customer_counts)){
                for (var customer_key in customer_counts) {
                    var customer_flow_count =  customer_counts[customer_key];
                    console.log("2-客户未处理流程项：" + customer_key + "----------" + customer_flow_count);
                    if (parseInt(customer_flow_count) > 0 ) {
                        var customer_title_span = $($('div[data-my-opts-type="customer-info"][data-my-id="' + customer_key + '"]>.my_text_title')[0]);
                        $(customer_title_span).after('<span class="layui-badge" style="float: right">' + customer_flow_count+ '</span>');
                    }
                }
            }

            // 部门
            var dept_counts = data.deptCount;
            if (isNotBlank(dept_counts)) {
                for (var dept_key in dept_counts) {
                    var dept_flow_count = dept_counts[dept_key];
                    console.log("3-部门未处理流程项：" + dept_key + "----------" + dept_flow_count);
                    if (parseInt(dept_flow_count) > 0) {
                        var dept_title_span = $($('div[data-my-opts-type="customer-dept"][data-my-id="' + dept_key + '"]>.my_text_title')[0]);
                        $(dept_title_span).after('<span class="layui-badge" style="float: right">' + dept_flow_count + '</span>');
                    }
                }
            }

            // 产品
            // {'402812816d39753b016d39897c8c000a': 1, '402812816d39753b016d39897c8c000b': 1}
            var product_counts = data.productCount;
            if (isNotBlank(product_counts)){
                for (var product_key in product_counts) {
                    var product_flow_count = product_counts[product_key];
                    console.log("4-客户产品未处理流程项：" + dept_key + "----------" + dept_flow_count);
                    if (parseInt(product_flow_count) > 0 ) {
                        var titleSpan = $('#productCard div[data-my-id="' + product_key + '"]>.my_text_title');
                        $(titleSpan).after('<span class="layui-badge" style="float: right">' + product_flow_count+ '</span>');
                    }
                }
            }


            // 运营 
            // operateYearCount {2019: 1} 
            // operateCount {2019: {9: 1, 10: 2}}
            // 年
            for (var yearKey in data.operateYearCount) {
            	if (data.operateYearCount[yearKey]) {
            		// 运营年份title
                    var operate_year_dom = $("#operate_panel div[data-my-opts-type='year'][data-my-id='" + yearKey + "']>.my_text_title");
                    // 运营年份气泡数据更新
                    operate_year_dom.after('<span class="layui-badge" style="float: right">' + data.operateYearCount[yearKey] + '</span>');
            	}
            	
            	var operateCount = data.operateCount[yearKey];
            	// 月
                for (var monthKey in operateCount) {
                	if (operateCount[monthKey]) {
                		// 运营月份title
                		var operate_month_dom = $("#operate_panel div[data-my-opts-type='month'][data-my-id='" + yearKey + "-" + monthKey + "']>.my_text_title");
                		// 更新运营月份气泡
                		operate_month_dom.after('<span class="layui-badge" style="float: right">' + operateCount[monthKey] + '</span>');
                	}
                }
            }

            // 显示 对账、发票、 销账
            renderFlowCount4FlowType(data.billYearCount, data.billCount, '#settlement_body_2', $('[lay-filter="settlementCard"] [data-my-type="2"]'));
            renderFlowCount4FlowType(data.invoiceYearCount, data.invoiceCount, '#settlement_body_3', $('[lay-filter="settlementCard"] [data-my-type="3"]'));
            renderFlowCount4FlowType(data.writeOffYearCount, data.writeOffCount, '#settlement_body_4', $('[lay-filter="settlementCard"] [data-my-type="4"]'));
        }
    });
}

function renderFlowCount4FlowType(yearCountMap, countMap, id, li) {
	for (var yearKey in yearCountMap) {
    	if (yearCountMap[yearKey]) {
    		var year_ele = $(id + " div[data-my-opts-type='settlement_year'][data-my-id$='" + yearKey + "']>.my_text_title");
    		if (year_ele.length == 0) {
    			year_ele = $(id + " div[data-my-opts-type='settlement_year'][data-my-tag$='" + yearKey + "']>.my_text_title");
    		}
            year_ele.after('<span class="layui-badge" style="float: right">' + yearCountMap[yearKey] + '</span>');
    	}
    	
    	var count = countMap[yearKey];
    	// 月
        for (var monthKey in count) {
        	if (count[monthKey]) {
        		// 结算月份 title
                var month_ele = $("#settlement_body div[data-my-opts-type='settlement_month'][data-my-id$='" + yearKey + "||" + monthKey + "']>.my_text_title");
                // 更新结算月份气泡
                month_ele.after('<span class="layui-badge" style="float: right">' + count[monthKey] + '</span>');
        	}
        }
    }
}

// 仅支持 ES5及其以上
String.prototype.equals= function (obj) {
    if (obj === null || obj === undefined){
        return false;
    }
    if (typeof obj !== typeof this.valueOf()){
        return false;
    }
    return this.valueOf() === obj;
};

// 防止兼容问题
String.prototype.startWith = function(str){
    var reg=new RegExp("^"+str);
    return reg.test(this);
};

// 防止兼容问题
String.prototype.endWith = function(str){
    var reg=new RegExp(str+"$");
    return reg.test(this);
};

// 对象内的key排序
function sortObjectKey(unordered) {
    var ordered = {};
    Object.keys(unordered).sort().forEach(function(key) {
        ordered[key] = unordered[key];
    });
    return ordered;
}


// 差集
function difference_set(from_arr, args, param_name) {
    if (isBlank(from_arr) || !(from_arr instanceof Array)) {
        return [];
    }
    if (isBlank(args) || !(args instanceof Array)) {
        return from_arr;
    }
    for (var arg_index = 0; arg_index < args.length; arg_index++) {
        var arg_ele = args[arg_index];
        for (var this_index = 0; this_index < from_arr.length; this_index++) {
            var from_ele = from_arr[this_index];
            if (!isBlank(param_name)) {
                if (from_ele[param_name] === arg_ele[param_name]) {
                    from_arr.splice(this_index, 1);
                    this_index--;
                }
            } else {
                if (from_ele === arg_ele) {
                    from_arr.splice(this_index, 1);
                    this_index--;
                }
            }
        }
    }
    return from_arr;
}

//格式化数字(默认保留两位小数)
function format_num(num, fix_size) {
    // 保留小数长度(设置保留位数长度)
    if (!isBlank(fix_size) && $.isNumeric(fix_size) && parseInt(fix_size) >= 0) {
        fix_size = parseInt(fix_size);
    } else {
        fix_size = 0
    }
    if (isBlank(num)) {
        num = 0;
        return num.toFixed(fix_size);
    }
    // 转换成字符串
    num = parseFloat(num).toFixed(fix_size) + "";
    var dot_num = "";
    var num_int = 0;
    if (num.indexOf(".") > 0) {
        dot_num = num.substring(num.indexOf("."), num.length);
        num_int = num.substring(0, num.indexOf("."));
    }
    if (parseInt(num_int) === 0) {
        if (fix_size > 0) {
            return num_int + dot_num;
        } else {
            return num_int;
        }
    }
    var front = "";
    if (parseInt(num_int) < 0) {
        front = "-";
    }
    // 取绝对值 开始添加分隔号
    num_int = Math.abs(num_int) + "";
    // 分割成数组
    var num_int_arr = num_int.split("");
    var num_int_resu = [];
    var rec_count = 1;
    for (var num_int_index = (num_int_arr.length - 1); num_int_index >= 0; num_int_index--) {
        num_int_resu.push(num_int_arr[num_int_index]);
        if (rec_count % 3 === 0 && num_int_index !== 0) {
            num_int_resu.push(",");
        }
        rec_count++;
    }
    num_int = num_int_resu.reverse().join("");
    if (fix_size > 0) {
        return front + num_int + dot_num;
    } else {
        return front + num_int;
    }
}

// 实现对象和数组的深拷贝
function deepCopy(obj) {
    if (typeof obj !== 'object') return obj;
    var newObj = (Object.prototype.toString.call(obj) === '[object Array]') ? [] : {};
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            newObj[key] = (typeof obj[key] !== 'object') ? obj[key]: deepCopy(obj[key])
        }
    }
    return newObj
}

function isNull(str) {
    return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
}


// 刷新工作台气泡
function loadConsoleFlowCount() {
    var customerConsoleBadge = $(window.parent.document).find("span#customerConsoleBadge");
    if (customerConsoleBadge.length > 0) {
        $.post("/customerConsole/queryConsoleFlowEntCount.action", {
            deptIds: deptIds,
            keyWord: customerKeyWord
        }, function (data) {
            if (data.customerFlowCount > 0) {
                $(customerConsoleBadge).html(data.customerFlowCount);
                $(customerConsoleBadge).css("display", "inline");
            } else {
                $(customerConsoleBadge).css("display", "none");
            }
        },'json');
    }
    var resourceConsoleBadge = $(window.parent.document).find("span#resourceConsoleBadge");
    if (resourceConsoleBadge.length > 0) {
        $.post("/resourceConsole/queryConsoleFlowEntCount.action", {
            deptIds: deptIds,
            keyWord: supplierKeyWord
        }, function (data) {
            if (data.resourceFlowCount > 0) {
                $(resourceConsoleBadge).html(data.resourceFlowCount);
                $(resourceConsoleBadge).css("display", "inline");
            } else {
                $(resourceConsoleBadge).css("display", "none");
            }
        },'json');
    }
    var supplierConsoleDsBadge = $(window.parent.document).find("span#supplierConsoleDsBadge");
    if (supplierConsoleDsBadge.length > 0) {
        $.post("/resourceConsole/queryConsoleFlowEntCount.action", {
            deptIds: deptIds,
            keyWord: supplierKeyWord
        }, function (data) {
            if (data.resourceFlowCount > 0) {
                $(supplierConsoleDsBadge).html(data.resourceFlowCount);
                $(supplierConsoleDsBadge).css("display", "inline");
            } else {
                $(supplierConsoleDsBadge).css("display", "none");
            }
        },'json');
    }
}


// 在角色上显示气泡
function loadRoleFlowCount() {
    $.post("/flow/queryRoleFlowEntCount.action", {}, function (data) {
        var total = 0;
        if (data) {
            var index = $(window.parent.document);
            // 遍历每个角色
            for (var roleId in data) {
                // 如果该角色后面原来就有气泡，如果最新查询的数量大于0，将气泡的文本替换成最新的，否则移除原来的气泡
                var flowCountEle = $(index).find('#flowCount-' + roleId);
                if (flowCountEle.length > 0) {
                    if (data[roleId] > 0) {
                        $(flowCountEle).text(data[roleId])
                    } else {
                        $(flowCountEle).remove();
                    }
                } else {
                    // 如果原来角色后面没有气泡，如果最新查询的数量大于0，在角色后面加上气泡
                    var beforeEle = $(index).find('#before-' + roleId);
                    if (data[roleId] > 0) {
                        $(beforeEle).append('<span id="flowCount-' + roleId + '" class="layui-badge" style="position: unset; font-size: 10px">' + data[roleId] + '</span>');
                    }
                }
                total += data[roleId];
            }
            // 当前角色后面是总数的气泡
            var totalCountEle = $(index).find('#flowTotal');
            // 如果最新查询的总数大于0，如果原来有总数气泡，将气泡的文本替换成新的总数，如果原来没有气泡就加上
            if (total > 0) {
                if (totalCountEle.length > 0) {
                    $(totalCountEle).text(total);
                } else {
                    $(index).find('#beforeTotal').after('<span id="flowTotal" class="layui-badge" style="float: right; font-size: 12px; margin: 5px 0; top: 0; right: 0;">' + total + '</span>');
                }
            } else {
                // 如果总数为0，原来有总数气泡就移除，没有就不处理
                if (totalCountEle.length > 0) {
                    $(totalCountEle).remove();
                }
            }
        }
    },'json');
}

// 下载
function down_load(file_info) {
    console.log("下载文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.location.href = "/operate/downloadFile?" + file_params;
}

//预览
function view_File(file_info) {
    console.log("预览文件：" + JSON.stringify(file_info));
    var file_params = "filePath=" + encodeURIComponent(file_info.filePath) + "&fileName=" + encodeURIComponent(file_info.fileName) + "&r=" + Math.random();
    window.open("/operate/viewFile?" + file_params);
}


Number.prototype.toFixed = function (d) {
    var num = this + "";
    if (!d) d = 0;
    if (num.indexOf(".") === -1) num += ".";
    num += new Array(d + 1).join("0");
    if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (d + 1) + "})?)\\d*$").test(num)) {
        let s = "0" + RegExp.$2, pm = RegExp.$1, a = RegExp.$3.length, b = true;
        if (a === d + 2) {
            a = s.match(/\d/g);
            if (parseInt(a[a.length - 1]) > 4) {
                for (var i = a.length - 2; i >= 0; i--) {
                    a[i] = parseInt(a[i]) + 1;
                    if (a[i] == 10) {
                        a[i] = 0;
                        b = i != 1;
                    } else break;
                }
            }
            s = a.join("").replace(new RegExp("(\\d+)(\\d{" + d + "})\\d$"), "$1.$2");

        }
        if (b) s = s.substr(1);
        return (pm + s).replace(/\.$/, "");
    }
    return this + "";
}

// ------------------------------日期-----------------------------

Date.prototype.clone = function() {
	return new Date(this.valueOf());
}

Date.prototype.NextYear = function(i) {
	i = i === undefined || i === null || i === '' ? 1 : i;
	var date = this.clone();
	date.setFullYear(date.getFullYear() + i);
	return date;
}

Date.prototype.NextMonth = function(i) {
	i = i === undefined || i === null || i === '' ? 1 : i;
	var date = this.clone();
	date.setMonth(date.getMonth() + i);
	return date;
}

Date.prototype.NextDay = function(i) {
	i = i === undefined || i === null || i === '' ? 1 : i;
	var date = this.clone();
	date.setDate(date.getDate() + i);
	return date;
}

Date.prototype.Format = function(fmt) {  
	var o = {
		"M+": this.getMonth()+1,                 //月份   
		"d+": this.getDate(),                    //日   
		"h+": this.getHours(),                   //小时   
		"m+": this.getMinutes(),                 //分   
		"s+": this.getSeconds(),                 //秒   
		"q+": Math.floor((this.getMonth() + 3) / 3), //季度   
		"S": this.getMilliseconds()             //毫秒   
	};   
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));   
	for(var k in o)   
		if(new RegExp("(" + k + ")").test(fmt))   
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));   
	return fmt;
}

function getMonthWeek(date) {
	if (!date)
		date = new Date();
	var w = date.getDay(),
		d = date.getDate();
	return Math.ceil((d + 6 - w) / 7 ); 
}

function str2Date(str) {
	if (!str) {
		return null;
	}
	str = str.replace(/-/g, '/'); // "2010/08/01";
	if (str.length == 4) {
		str += '/01/01 00:00:00';
	} else if (str.length == 7) {
		str += '/01 00:00:00';
	} else if (str.length == 10) {
		str += ' 00:00:00';
	}

	return new Date(str);
}

function getCurrentMonthFirst() {
	var date = new Date()
	date.setDate(1)
	return date.Format('yyyy-MM-dd');
}

function getMonthDiffFirst(date, diff) { // 下几个月第一天
	if (!date)
		date = new Date();
	date.setDate(1)
	if (diff === undefined || diff === null || diff === '')
		diff = 1;
	date.setMonth(date.getMonth() - diff); // 设置日期
	return date;
}

function getCurrentDiffStr(dayCount){
	if(null == dayCount){
		dayCount = 0;
	}
	var dd = new Date();
	dd.setDate(dd.getDate() + dayCount); // 设置日期
	return dd.Format('yyyy-MM-dd');
}

function uuid() {
    return 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0;
        var v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function cloneObj(obj){
	var str, newobj = obj.constructor === Array ? [] : {};
    if(typeof obj !== 'object'){
        return;
    } else if(window.JSON){
        str = JSON.stringify(obj), // 序列化对象
        newobj = JSON.parse(str); // 还原
    } else {
        for(var i in obj){
            newobj[i] = typeof obj[i] === 'object' ? cloneObj(obj[i]) : obj[i];
        }
    }
    return newobj;
};

// -----------------------------------------------------------正则---------------------------------------------------------------

function getExecStrs (str, reg) {
	var list = []
    var result = null
    do {
        result = reg.exec(str)
        result && list.push(result[1])
    } while (result)
    return list
}

// ----------------------------------------------------------input框联动-------------------------------------------------------------

function bindInputLinkage(expression, targetInputId, targetLabelName, attrName) {
	return function(form, flowLabels) { // 表单, 表达式, 流程标签
		if (!expression || !form || !flowLabels || flowLabels.length == 0) {
			return;
		}
		if (/^\{\{(.+?)\}\}$/.test(expression)) {
			expression = expression.substr(2, expression.length - 4);
		}
		var variableMap = {};
		var labelArr = [];
		var labelTypeMap = {};
		$(flowLabels).each(function (i, item) {
			labelTypeMap[item['name']] = {
				type: item['type'],
				id: item['id']
			}
		});
		var list = getExecStrs(expression, /#\{(.+?)\}/g);
		if (list && list.length > 0) {
			$(list).each(function (i, item) {
				variableMap[item] = null;
				if (labelArr.indexOf(item) < 0) {
					labelArr.push(item);
				}
			});
		}
		$(labelArr).each(function (i, item) {
			$(form).find('input[' + attrName + '="' + labelTypeMap[item]['id'] + '"]').bind('keyup', function () {
				var exp = expression;
				var vMap = cloneObj(variableMap);
				for (var j = 0; j < labelArr.length; j++) { // 获取各个input的值
					var value = $(form).find('input[' + attrName + '="' + labelTypeMap[labelArr[j]]['id'] + '"]').val();
					if (value === undefined || value === null || value === '') {
						if (labelTypeMap[labelArr[j]]['type'] == 1) {
							value = 0;
						} else if (labelTypeMap[labelArr[j]]['type'] == 2) {
							value = 0.00;
						} else {
							value = '';
						}
					}
					vMap[labelArr[j]] = value;
				}
				$(labelArr).each(function (index, v) {
					exp = exp.replace(new RegExp('#{' + v + '}', 'gm'), (labelTypeMap[v]['type'] == 1 
							|| labelTypeMap[v]['type'] == 2) ? vMap[v] : ('"' + vMap[v] + '"'));
				});
				var val = eval(exp);
				if (labelTypeMap[targetLabelName]['type'] == 1) {
					val = val == Infinity ? 0 : val;
					val = isNaN(val) ? 0 : val;
					val = val.toFixed(2);
					val = parseInt(val);
				} else if (labelTypeMap[targetLabelName]['type'] == 2) {
					val = val == Infinity ? 0.000000 : val;
					val = isNaN(val) ? 0.000000 : val;
					val = parseFloat(val).toFixed(6);
				} else {
					val = val === undefined || val === null ? '' : val;
				}
				$(form).find('input[' + attrName + '="' + targetInputId + '"]').val(val);
			});
		});
	}
}


// -------------------------------------------------------------转百分比-----------------------------------------------------
function toPercent(percent, scale) {
	if (scale === undefined || scale === null 
			|| scale === '' || scale < 0) {
		scale = 0;
	}
	if (percent === undefined || percent === null || percent === '') {
		return (0).toFixed(scale) + '%';
	}
	var val = parseFloat(percent);
    return (val * 100).toFixed(scale) + "%";
}

// ----------------------------------------------------------文件异步下载-------------------------------------------------------
function asyncExportExcel(url, fileName){
	
	layui.layer.msg('正在下载文件请勿离开页面!');
	
	// 不是IE，模拟a的点击事件
	var link = document.createElement("a");
	link.href = url;
	link.style = "visibility:hidden";
	link.download = fileName; 
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
}

// ---------------------------------------------------------判断文件类型----------------------------------------------------------
var imgExt = new Array('.png', '.jpg', '.jpeg', '.bmp', '.gif'); // 图片文件的后缀名
var docExt = new Array('.doc', '.docx', '.DOC', '.DOCX'); // word文件的后缀名
var xlsExt = new Array('.xls', '.xlsx', '.XLS', 'XLSX'); // excel文件的后缀名

String.prototype.extension = function(){
    var ext = null;
    var name = this.toLowerCase();
    var i = name.lastIndexOf(".");
    if(i > -1){
    var ext = name.substring(i);
    }
    return ext;
}

// 判断Array中是否包含某个值
Array.prototype.contain = function(obj){
    for(var i=0; i<this.length; i++){
        if(this[i] === obj)
        	return true;
    }
    return false;
}

function typeMatch(type, fileName){
    var ext = fileName.extension();
    if (type.contain(ext)) {        
        return true;
    }
    return false;
}

function typeMatchOfficeFile(fileName){
	var flag = false;
	flag = typeMatch(docExt, fileName);
	if (!flag) {
		flag = typeMatch(xlsExt, fileName);
	}
	return flag;
}

// 消息中心未读消息数
function initCount(num) {
    var loadingStr = '<span style="font-size: 20px">加载中，请稍候...</span>';
    $("#customer cite").html(loadingStr);
    $("#supplier cite").html(loadingStr);
    $("#customerLog cite").html(loadingStr);
    var dateLinetype;
    if (num) {
        dateLinetype = num;
    } else {
        dateLinetype = "1";
    }
    var ajaxData = {
        "dateLinetype": dateLinetype
    };
    $.ajax({
        type: "POST",
        async: true,
        url: "/messageCenter/getMsgCount",
        dataType: 'json',
        data: ajaxData,
        success: function (data) {
            if (data.code == 200) {
                var dataCount = data.data;
                if (data.data) {
                    var unreadCount;
                    $.each(dataCount, function (index, item) {
                        if (item.infoType == "0") {
                            unreadCount = item.count;
                            //寻找该iframe的父窗口id为unread的元素，解决抽口阅读数据之后，无法同步数据的问题
                            $('#unread', window.parent.document).html(unreadCount)
                            $("#unread").html(unreadCount);
                            console.log(unreadCount)
                        } else if (item.infoType == "1") {
                            $("#customer cite").text(item.count);
                        } else if (item.infoType == "2") {
                            $("#supplier cite").text(item.count);
                        } else if (item.infoType == "3") {
                            $("#customerLog cite").text(item.count);
                        }
                    });
                    if (unreadCount > 99) {
                        $("#messageConsoleBadge").text("99+");
                        $("#messageConsoleBadge").css({"display": "block"});
                    } else if ((unreadCount > 0 && unreadCount < 99) || unreadCount == 99) {
                        $("#messageConsoleBadge").text(unreadCount);
                        $("#messageConsoleBadge").css({"display": "block"});
                    } else if (unreadCount == 0) {
                        $("#messageConsoleBadge").css({"display": "none"});
                    }
                } else {
                    $("#messageConsoleBadge").css({"display": "none"});
                }
            } else {
                $("#messageConsoleBadge").css({"display": "none"});
            }
        }
    });
}

/**
 * 是否为数字（不含负数）
 * @param num
 * @returns {*}
 */
function isNumber(num) {
    return /^(\d+)(\.\d+)?$/.test(num);
}