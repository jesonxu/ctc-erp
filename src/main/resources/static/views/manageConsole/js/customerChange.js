layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;
    // 初始化 部门查询
    bindDeptFilter();
    // 初始化日期查询
    bindYearMonthInput();
    // 加载表格
    loadCustomerChangeTable();

    /**
     * 加载数据
     */
    function loadCustomerChangeTable() {
        table.render({
            elem: "#customer-change-table",
            // totalRow: true,
            defaultToolbar: ["exports"],
            height: 'full-60',
            url: "/customerChangeRecord/getCustomerChangeTable",
            method: "POST",
            where: {
                "userId": $("#userIds").val(),
                "yearMonth": $("#year-month-data").val(),
                "deptId": $("#deptIds").val(),
            },
            request: {
                pageName: 'currentPage',
                limitName: 'pageSize'
            },
            cols: [
                [{
                    field: 'deptName',
                    title: '部门',
                    align: 'left',
                    width: 100,
                    unresize: false,
                    totalRow: false,
                    fixed: "left",
                    totalRowText: '合计'
                }, {
                    field: 'userName',
                    title: '销售',
                    align: 'left',
                    width: 80,
                    fixed: "left",
                    totalRow: false,
                    totalRowText: '-',
                    templet: function (d) {
                        if (isBlank(d.userName)){
                            return "-";
                        }
                        return d.userName;
                    }
                }, {
                    field: 'contractTotal',
                    title: '合同客户数',
                    width: 100,
                    align: 'right',
                    totalRow: true,
                    sort: true,
                    templet: function (d) {
                        if (d.contractTotal === 0 || d.contractTotal ==='0'){
                            return "-";
                        }
                        return thousand(d.contractTotal);
                    }
                }, {
                    field: 'testTotal',
                    title: '测试客户数',
                    width: 100,
                    align: 'right',
                    totalRow: true,
                    sort: true,
                    templet: function (d) {
                        if (d.testTotal === 0 || d.testTotal ==='0'){
                            return "-";
                        }
                        return thousand(d.testTotal);
                    }
                }, {
                    field: 'intentionTotal',
                    title: '意向客户数',
                    width: 100,
                    align: 'right',
                    totalRow: true,
                    sort: true,
                    templet: function (d) {
                        if (d.intentionTotal === 0 || d.intentionTotal ==='0'){
                            return "-";
                        }
                        return thousand(d.intentionTotal);
                    }
                }, {
                    field: 'silenceTotal',
                    title: '沉默客户数',
                    width: 100,
                    align: 'right',
                    totalRow: true,
                    sort: true,
                    templet: function (d) {
                        if (d.silenceTotal === 0 || d.silenceTotal ==='0'){
                            return "-";
                        }
                        return thousand(d.silenceTotal);
                    }
                }, {
                    field: 'monthChangeCount',
                    title: '当月新增数',
                    width: 100,
                    align: 'right',
                    totalRow: true,
                    sort: true,
                    templet: function (d) {
                        if (d.monthChangeCount === 0 || d.monthChangeCount ==='0'){
                            return "-";
                        }
                        return thousand(d.monthChangeCount);
                    }
                }, {
                    field: 'toContractCount',
                    title: '合同客户变更数',
                    align: 'center',
                    totalRowText: '-',
                    sort:true,
                    templet: function (res) {
                        // 从合同降
                        var downFromContractCount = res.downFromContractCount;
                        // 升到合同
                        var upToContractCount = res.upToContractCount;
                        var contractDom = [];
                        if (upToContractCount > 0) {
                            contractDom.push("<span class='change-count'><i class='icon2 icon-up-right' title='升级到合同客户'></i>" + thousand(upToContractCount) + "</span>");
                        } else {
                            contractDom.push("<span class='change-count'></span>");
                        }
                        if (downFromContractCount > 0) {
                            contractDom.push("<span class='change-count'>" + thousand(downFromContractCount) + "<i class='icon2 icon-right-down' title='从合同客户降级'></i></span>");
                        } else {
                            contractDom.push("<span class='change-count'></span>");
                        }
                        return contractDom.join("");
                    },
                }, {
                    field: 'testUpCount',
                    title: '测试客户变更数',
                    align: 'center',
                    totalRowText: '-',
                    sort: true,
                    templet: function (res) {
                        // 升到测试
                        var upToTestCount = res.upToTestCount;
                        // 降到测试
                        var downToTestCount = res.downToTestCount;
                        // 从测试降
                        var downFromTestCount = res.downFromTestCount;
                        // 从测试升
                        var upFromTestCount = res.upFromTestCount;
                        var testDom = [];
                        if (downToTestCount >0){
                            testDom.push("<span class='change-count'><i class='icon2 icon-down-right' title='降级到测试客户'></i>" + thousand(downToTestCount) + "</span>");
                        }else {
                            testDom.push("<span class='change-count'></span>");
                        }
                        if (upToTestCount >0){
                            testDom.push("<span class='change-count'><i class='icon2 icon-up-right' title='升级到测试客户'></i>" + thousand(upToTestCount) + "</span>");
                        }else {
                            testDom.push("<span class='change-count'></span>");
                        }
                        if (downFromTestCount > 0) {
                            testDom.push("<span class='change-count'>" + thousand(downFromTestCount) + "<i class='icon2 icon-right-down' title='从测试客户降级'></i></span>");
                        }else {
                            testDom.push("<span class='change-count'></span>");
                        }
                        if (upFromTestCount > 0) {
                            testDom.push("<span class='change-count'>" + thousand(upFromTestCount) + "<i class='icon2 icon-right-up' title='从测试客户升级'></i></span>");
                        }else {
                            testDom.push("<span class='change-count'></span>");
                        }
                        return testDom.join("");
                    },
                }, {
                    field: 'intentionUpCount',
                    title: '意向客户变更数',
                    align: 'center',
                    totalRowText: '-',
                    sort: true,
                    templet: function (res) {
                        var upToIntentionCount= res.upToIntentionCount;
                        var downToIntentionCount= res.downToIntentionCount;
                        var downFromIntentionCount = res.downFromIntentionCount;
                        var upFromIntentionCount = res.upFromIntentionCount;
                        var intentionDom = [];
                        if (downToIntentionCount > 0) {
                            intentionDom.push("<span class='change-count'><i class='icon2 icon-down-right' title='降级到意向客户'></i>" + thousand(downToIntentionCount) + "</span>");
                        }else {
                            intentionDom.push("<span class='change-count'></span>");
                        }
                        if (upToIntentionCount >0){
                            intentionDom.push("<span class='change-count'><i class='icon2 icon-up-right' title='升级到意向客户'></i>" + thousand(upToIntentionCount) + "</span>");
                        }else {
                            intentionDom.push("<span class='change-count'></span>");
                        }
                        if (downFromIntentionCount > 0) {
                            intentionDom.push("<span class='change-count'>" + thousand(downFromIntentionCount) + "<i class='icon2 icon-right-down' title='从意向客户降级'></i></span>");
                        }else {
                            intentionDom.push("<span class='change-count'></span>");
                        }
                        if (upFromIntentionCount > 0) {
                            intentionDom.push("<span class='change-count'>" + thousand(upFromIntentionCount) + "<i class='icon2 icon-right-up' title='从意向客户升级'></i></span>");
                        }else {
                            intentionDom.push("<span class='change-count'></span>");
                        }
                        return intentionDom.join("");
                    }
                }, {
                    field: 'silenceUpCount',
                    title: '沉默客户变更数',
                    align: 'center',
                    totalRowText: '-',
                    sort: true,
                    templet: function (res) {
                        var upToSilenceCount = res.upToSilenceCount;
                        var downToSilenceCount = res.downToSilenceCount;
                        var downFromSilenceCount = res.downFromSilenceCount;
                        var upFromSilenceCount = res.upFromSilenceCount;
                        var silenceDom = [];
                        if (downToSilenceCount > 0) {
                            silenceDom.push("<span class='change-count'><i class='icon2 icon-down-right' title='降级到沉默客户'></i>" + thousand(downToSilenceCount) + "</span>");
                        } else {
                            silenceDom.push("<span class='change-count'></span>");
                        }
                        if (upToSilenceCount > 0) {
                            silenceDom.push("<span class='change-count'><i class='icon2 icon-up-right' title='升级到沉默客户'></i>" + thousand(upToSilenceCount) + "</span>");
                        } else {
                            silenceDom.push("<span class='change-count'></span>");
                        }
                        if (downFromSilenceCount > 0) {
                            silenceDom.push("<span class='change-count'>" + thousand(downFromSilenceCount) + "<i class='icon2 icon-right-down' title='从沉默客户降级'></i></span>");
                        } else {
                            silenceDom.push("<span class='change-count'></span>");
                        }
                        if (upFromSilenceCount > 0) {
                            silenceDom.push("<span class='change-count'>" + thousand(upFromSilenceCount) + "<i class='icon2 icon-right-up' title='从沉默客户升级'></i></span>");
                        } else {
                            silenceDom.push("<span class='change-count'></span>");
                        }
                        return silenceDom.join("");
                    },
                }, {
                    field: 'upFromPublic',
                    title: '公共池变更数',
                    align: 'center',
                    totalRowText: '-',
                    sort: true,
                    templet: function (res) {
                        var downToPublic = res.downToPublic;
                        var upFromPublic = res.upFromPublic;
                        var publicDom = [];
                        if (downToPublic > 0) {
                            publicDom.push("<span class='change-count'><i class='icon2 icon-down-right' title='降级到公共客户'></i>" + thousand(downToPublic) + "</span>");
                        }else {
                            publicDom.push("<span class='change-count'></span>");
                        }
                        if (upFromPublic > 0) {
                            publicDom.push("<span class='change-count'>" + thousand(upFromPublic) + "<i class='icon2 icon-right-up' title='从公共客户升级'></i></span>");
                        }else {
                            publicDom.push("<span class='change-count'></span>");
                        }
                        return publicDom.join("");
                    },
                }]
            ],
            done: function (res, curr, count) {
                // mergeTable(res);
            }
        });
    }

    /**
     * 刷新表格
     */
    function reloadCustomerChangeTable() {
        table.reload('customer-change-table', {
            url: "/customerChangeRecord/getCustomerChangeTable",
            where: {
                "userId": $("#userIds").val(),
                "yearMonth": $("#year-month-data").val(),
                "deptId": $("#deptIds").val(),
            },
            request: {
                pageName: 'currentPage',
                limitName: 'pageSize'
            },
        });
    }

    /**
     * 绑定查看详情按钮
     */
    function bindShowDetailBtn() {
        var buttons = $("button[data-detail-id]");
        for (var btnIndex = 0; btnIndex < buttons.length; btnIndex++) {
            var button = buttons[btnIndex];
            $(button).click(function () {
                // 客户ID
                var customerId = $(this).attr("data-detail-id");
                // 月份
                var queryDate = $("#year-month-data").val();
                // 按钮的ID
                var btnId = $(this).attr("data-btn-id");
                // 获取当前行
                var thisRow = $(this).parent().parent().parent();
                var table = $(thisRow).parent();
                var detailEle = $(table).find("tr[data-detail-id='" + btnId + "']");
                if (detailEle.length > 0) {
                    $(detailEle).remove();
                    return false;
                }
                $.ajax({
                    url: "/balanceInterest/getDetail.action?temp=" + Math.random(),
                    type: 'POST',
                    data: {
                        month: queryDate,
                        companyId: customerId
                    },
                    success: function (data) {
                        if (data.code === 200 || data.code === "200") {
                            var detailList = data.data;
                            var details = [];
                            if (isNotBlank(detailList) && detailList.length > 0) {
                                for (var dataIndex = 0; dataIndex < detailList.length; dataIndex++) {
                                    var detail = detailList[dataIndex];
                                    details.push("<div class='detail-row'>" +
                                        "<span>" + detail.time + ":</span>" +
                                        "<span>" + detail.leftMoney + "（元）</span>" +
                                        "<span> X </span> " +
                                        "<span>" + (parseFloat(detail.rate) * 100).toFixed(4) + "%</span>" +
                                        "<span> = </span>" +
                                        "<span>" + detail.interest + "（元）</span></div>");
                                }
                            }
                            var newRow = $("<tr data-detail-id='" + btnId + "'></tr>");
                            newRow.append("<td colspan='7' ><div class='layui-table-cell detail-info'>未查到数据</div></td>");
                            if (details.length > 0) {
                                newRow.find("div[class*='detail-info']").html(details.join(""));
                            }
                            $(thisRow).after(newRow.prop("outerHTML"))
                        } else {
                            layer.msg(data.msg);
                        }
                    },
                    error: function () {
                        layer.msg("数据查询错误");
                    }
                });
            });
        }
    }


    /**
     * 合并单元格（暂时不用合并）
     * @param res
     */
    function mergeTable(res) {
        var data = res.data;
        var mergeIndex = 0; //定位需要添加合并属性的行数
        var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
        var _number = 1; //保持序号列数字递增
        var columsName = ['deptName', 'saleName']; //需要合并的列名称
        var columsIndex = [0, 1]; //需要合并的列索引值
        var mergeCondition = 'id'; //需要合并的 首要条件  在这个前提下进行内容相同的合并
        var tdArrL = layui.$('.layui-table-fixed-l > .layui-table-body').find("tr"); //序号列左定位产生的table tr
        var tdArrR = layui.$('.layui-table-fixed-r > .layui-table-body').find("tr"); //操作列定右位产生的table tr

        for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
            var trArr = layui.$(".layui-table-main>.layui-table").find("tr"); //所有行
            for (var i = 1; i < res.data.length; i++) { //这里循环表格当前的数据

                if (data[i][mergeCondition] === data[i - 1][mergeCondition]) {
                    var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]); //获取当前行的当前列
                    var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]); //获取相同列的第一列

                    if (data[i][columsName[k]] && data[i][columsName[k]] === data[i - 1][columsName[k]]
                        && data[i][columsName[0]] === data[i - 1][columsName[0]]) { //后一行的值与前一行的值做比较，相同就需要合并
                        mark += 1;
                        tdPreArr.each(function () { //相同列的第一列增加rowspan属性
                            layui.$(this).attr("rowspan", mark);
                        });
                        tdCurArr.each(function () { //当前行隐藏
                            layui.$(this).css("display", "none");
                        });
                    } else {
                        mergeIndex = i;
                        mark = 1; //一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                    }
                } else {
                    mergeIndex = i;
                    mark = 1; //一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                }
            }
            mergeIndex = 0;
            mark = 1;
        }

        //操作左右定位列的表格
        layui.$.each(layui.$("#qua_standard_table").siblings('.layui-table-view').find('.layui-table-main>.layui-table').find("tr"), function (i, v) {
            if (layui.$(v).find('td').eq(2).css('display') === 'none') {
                tdArrL.eq(i).find('td').css('display', 'none');
                tdArrR.eq(i).find('td').css('display', 'none');
            } else {
                tdArrL.eq(i).find('td').find('.laytable-cell-numbers').html(_number++);
                tdArrL.eq(i).find('td').css('height', layui.$(v).find('td').eq(2)[0].clientHeight);
                tdArrR.eq(i).find('td').css('height', layui.$(v).find('td').eq(2)[0].clientHeight);

            }
        });
    }


    /**
     * 绑定 年月输入框
     */
    function bindYearMonthInput() {
        // 查询年份 // 默认查去年
        var now = new Date();
        var month = (now.getMonth() + 1) + "";
        if (month.length === 1) {
            month = "0" + month;
        }
        var yearMonth = now.getFullYear() + "年" + month + "月";
        $("#year-month-data").val(now.getFullYear() + "-" + month);
        laydate.render({
            elem: '#yearSelect',
            type: 'month',
            trigger: 'click',
            max: yearMonth, // 最大值为今年
            value: yearMonth,
            format: "yyyy年MM月",
            btns: ['now', 'confirm'],
            done: function (value, date) {
                var year = value.substring(0, value.indexOf("年"));
                var month = value.substring(value.indexOf("年") + 1, value.length - 1);
                var time = year + "-" + month;
                $("#year-month-data").val(time);
                reloadCustomerChangeTable();
            }
        })
    }

    /**
     * 绑定部门过滤
     */
    function bindDeptFilter() {
        $("#dept-filter").click(function () {
            var userId = $("#userIds").val();
            var deptId = $("#deptIds").val();
            var area = ['400px', '600px'];
            layer.open({
                type: 2,
                title: '客户过滤',
                area: area,
                btn: ['确定', '取消'],
                btnAlign: 'c',
                fixed: false, //不固定
                maxmin: true,
                content: '/customer/toCustomerFilter.action?deptIds=' + deptId + '&userIds=' + userId,
                yes: function (index, layero) {
                    var body = layer.getChildFrame('body', index);
                    var deptId = $(body).find("input[id='checkedDeptIds']").val();
                    var userId = $(body).find("input[id='checkedUserIds']").val();
                    $("#userIds").val(userId);
                    $("#deptIds").val(deptId);
                    layer.close(index);
                    reloadCustomerChangeTable();
                },success: function(layero, index){
                    $(layero).find("iframe").contents().find(".keyword-filter").css('display', 'none');
                }
            });
        });
    }
});

