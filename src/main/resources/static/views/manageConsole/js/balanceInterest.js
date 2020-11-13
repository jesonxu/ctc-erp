layui.use(['layer', 'table', 'form', 'element', 'laydate'], function () {
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;
    // 初始化 部门查询
    bindDeptFilter();
    // 初始化日期查询
    bindYearMonthInput();
    // 加载表格
    loadBalanceInterestTable();

    /**
     * 加载数据
     */
    function loadBalanceInterestTable() {
        table.render({
            elem: "#balance-interest-table",
            totalRow: true,
            defaultToolbar: ["exports"],
            height: 'full-55',
            limit: 20,
            url: "/balanceInterest/getBalanceInterest",
            method: "POST",
            where: {
                "userId": $("#userIds").val(),
                "queryDate": $("#year-month-data").val(),
                "deptId": $("#deptIds").val(),
            },
            request: {
                pageName: 'currentPage',
                limitName: 'pageSize'
            },
            page: true,
            limits: [10, 20, 30, 50, 100, 200, 500],
            cols: [
                [{
                    field: 'deptName',
                    title: '部门',
                    align: 'left',
                    width: 250,
                    unresize: false,
                    totalRow: false,
                    totalRowText: '合计'
                }, {
                    field: 'saleName',
                    title: '销售',
                    align: 'left',
                    width: 200,
                    totalRow: false,
                    totalRowText: '-'
                }, {
                    field: 'customerName',
                    title: '客户名称',
                    width: 250,
                    align: 'left',
                    totalRowText: '-'
                }, {
                    field: 'accountBalance',
                    title: '计息金额（元）',
                    align: 'right',
                    totalRow: false,
                    templet: function (res) {
                        return res.accountBalance ? thousand(parseFloat(res.accountBalance).toFixed(2)) : '0.00';
                    },
                    totalRowText: '-'
                }, {
                    field: 'interestRatio',
                    title: '计息率(%)',
                    align: 'right',
                    width: 150,
                    totalRow: false,
                    totalRowText: '-',
                    templet: function (res) {
                        var interestRatio = res.interestRatio;
                        if (isBlank(interestRatio)){
                            return "0.00";
                        }
                        interestRatio = parseFloat(interestRatio);
                        return thousand((interestRatio * 100).toFixed(4));
                    },
                }, {
                    field: 'interest',
                    title: '计息（元）',
                    align: 'right',
                    templet: function (res) {
                        return res.interest ? thousand(parseFloat(res.interest).toFixed(2)) : '0.00';
                    },
                    totalRow: true,
                    totalConfig: {decimal: 2, thousand: true}
                }, {
                    title: '操作',
                    align: 'center',
                    width: 200,
                    templet: function (res) {
                        console.log(res);
                        var btnId = uuid();
                        return "<button class='layui-btn layui-btn-xs' data-btn-id='" + btnId + "' data-detail-id='" + res.customerId + "'>" +
                            "<i class='layui-icon layui-icon-list'></i> 查看详情</button>";
                    },
                }]
            ],
            done: function (res, curr, count) {
                // mergeTable(res);
                bindShowDetailBtn();
            }
        });
    }

    /**
     * 刷新表格
     */
    function reloadBalanceInterestTable() {
        table.reload('balance-interest-table', {
            url: "/balanceInterest/getBalanceInterest",
            where: {
                "userId": $("#userIds").val(),
                "queryDate": $("#year-month-data").val(),
                "deptId": $("#deptIds").val()
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
                                    details.push("<li class='detail-row'>" +
                                        "<span>" + detail.time + ":</span>" +
                                        "<span>" + thousand(detail.leftMoney) + "（元）</span>" +
                                        "<span> X </span> " +
                                        "<span>" + (parseFloat(detail.rate) * 100).toFixed(4) + "%</span>" +
                                        "<span> = </span>" +
                                        "<span>" + thousand(detail.interest) + "（元）</span></li>");
                                }
                            }
                            var newRow = $("<tr class='table-detail-row' data-detail-id='" + btnId + "'></tr>");
                            newRow.append("<td class='table-detail-cell' colspan='7' ><ul class='layui-table-cell detail-info'>未查到数据</ul></td>");
                            if (details.length > 0) {
                                newRow.find("ul[class*='detail-info']").html(details.join(""));
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
                // $('#yearSelect').html(value);
                var year = value.substring(0, value.indexOf("年"));
                var month = value.substring(value.indexOf("年") + 1, value.length - 1);
                var time = year + "-" + month;
                $("#year-month-data").val(time);
                reloadBalanceInterestTable();
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
                    reloadBalanceInterestTable();
                },success: function(layero, index){
                    $(layero).find("iframe").contents().find(".keyword-filter").css('display', 'none');
                }
            });
        });
    }
});

