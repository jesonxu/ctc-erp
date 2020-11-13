/**
 *未对账账单标签 34
 */
(function (window, factory) {
    window.UncheckedBillInfoLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let UncheckedBillInfoLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【未对账账单标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【未对账账单标签】ID为空");
        }
        this.required = false;
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    UncheckedBillInfoLabel.prototype.toText = function (value) {
        // console.log("未对账账单标签：name:" + this.name + " - value:" + value);
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        try {
            value = JSON.parse(value);
        } catch (e) {
        }
        let html = "<br/>";
        // 已选中的未对账账单部分
        let billInfos = value.billInfos;
        $.each(billInfos, function (index, billInfo) {
            html += billInfo.title + "<br/>";
            html += "&nbsp;&nbsp;我司数据：" + util.thousand(billInfo.platformSuccessCount) + " X " + billInfo.platformUnitPrice + " = " + util.thousand(billInfo.platformAmount) + "<br/>";
            html += "&nbsp;&nbsp;客户数据：" + util.thousand(billInfo.customerSuccessCount) + " X " + billInfo.customerUnitPrice + " = " + util.thousand(billInfo.customerAmount) + "<br/>";
            html += "&nbsp;&nbsp;对账数据：" + util.thousand(billInfo.checkedSuccessCount) + " X " + billInfo.checkedUnitPrice + " = " + util.thousand(billInfo.checkedAmount) + "<br/>";
        });
        // 总计部分
        let billTotal = value.billTotal;
        html += "<br/>对账总计：<br/>";
        if (util.isNotNull(billTotal.platformSuccessCount) && util.isNotNull(billTotal.platformAmount)) {
            html += "我司数据：计费数：" + util.thousand(billTotal.platformSuccessCount) + "，金额：" + util.thousand(billTotal.platformAmount) + "<br/>";
        }
        if (util.isNotNull(billTotal.checkedSuccessCount) && util.isNotNull(billTotal.checkedAmount)) {
            html += "对账数据：计费数：" + util.thousand(billTotal.checkedSuccessCount) + "，金额：" + util.thousand(billTotal.checkedAmount) + "<br/>";
        }
        // 对账单部分
        let billFile = value.billFile;
        if (util.isNotNull(billFile)) {
            html += "<br/>电子账单：";
            html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='fileTool.downLoadFile(" + JSON.stringify(billFile) + ")'>下载</a>&nbsp;&nbsp;";
        }
        // 数据分析报告部分
        let analysisFile = value.analysisFile;
        if (util.isNotNull(analysisFile)) {
            html += "<br/>数据报告：";
            html += "<a style='text-decoration: underline' href='javascript:void(0);' onclick='fileTool.downLoadFile(" + JSON.stringify(analysisFile) + ")'>下载</a></br>";
        }
        return this.name + html;
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param flowEntId 流程实体id（审核时）/流程id（发起时）
     * @param entityId 主体id（客户/供应商）
     */
    UncheckedBillInfoLabel.prototype.render = function (flowEle, value, required, flowEntId, entityId) {
        if (util.isNull(flowEle)) {
            throw new Error("【未对账账单标签】对应的位置元素不存在");
        }
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        this.flowEntId = flowEntId;
        this.entityId = entityId;
        this.required = util.isTrue(required);
        try {
            value = JSON.parse(value)
        } catch (e) {
        }
        this.data = util.formatBlank(value);
        let htmlStr = "";
        htmlStr += "<div class='layui-form-item label-type-unchecked-bill' data-label-id='" + this.id + "'>"; // form-item
        htmlStr += "<label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + "：</label>";
        htmlStr += "<div class='flow-label-content'>"; // flow-label-content

        // 选择账单按钮
        htmlStr +=
            "<button name='chooseBill' type='button' class='layui-btn layui-btn-primary choose-bill-btn'>" +
            "    <i class='layui-icon layui-icon-list'></i>点击选择对账账单" +
            "</button>";

        // 所有未对账账单
        htmlStr += "<div class='unchecked-bill-list'></div>";

        // 手动生成账单
        htmlStr +=
            "<div class='bill-tip-line'>" +
            "   <i class='layui-icon layui-icon-tips' title='提示'></i>" +
            "   <span class='build-bill'>未找到？" +
            "   <a href='javascript:void(0);' style='text-decoration: underline; color: #1E9FFF'>生成账单</a>" +
            "   </span>" +
            "</div>";

        let billTotal = value.billTotal;
        // 本次对账总计
        htmlStr += "<div class='bill-total'>";
        htmlStr += "<span class='unchecked-bill-total-tip'>对账总计：</span>";

        // 我司数据、对账数据总计
        if (util.isNull(billTotal)) {
            billTotal = {
                'platformSuccessCount': 0,
                'platformAmount': 0.0,
                'checkedSuccessCount': 0,
                'checkedAmount': 0.0
            };
        } else if (util.isNull(billTotal.platformSuccessCount)) {
            billTotal.platformSuccessCount = 0;
            billTotal.platformAmount = 0.0;
        } else if (util.isNull(billTotal.checkedSuccessCount)) {
            billTotal.checkedSuccessCount = 0;
            billTotal.checkedAmount = 0.0;
        }
        htmlStr += "<div class='unchecked-bill-total'>";  // total
        htmlStr +=
            "<div class='bill-data-total' data-type='platform'>" +
            "   <div class='bill-total-line-title'>我司数据</div>" +
            "   <div class='bill-data-line'>" +
            "   <span>计费数：</span>" +
            "   <input type='text' disabled class='layui-input' name='successCount' value='" + util.thousand(billTotal.platformSuccessCount) + "'/>" +
            "   </div>" +
            "   <div class='bill-data-line'>" +
            "   <span>金额：</span>" +
            "   <input type='text' disabled class='layui-input' name='amount' value='" + util.thousand(billTotal.platformAmount) + "'/>" +
            "   </div>" +
            "</div>";
        // 实际数据总计
        htmlStr +=
            "<div class='bill-data-total' data-type='checked'>" +
            "   <div class='bill-total-line-title'>对账数据</div>" +
            "   <div class='bill-data-line'>" +
            "       <span>计费数：</span>" +
            "       <input type='text' disabled class='layui-input' name='successCount' value='" + util.thousand(billTotal.checkedSuccessCount) + "'/>" +
            "   </div>" +
            "   <div class='bill-data-line'>" +
            "       <span>金额：</span>" +
            "       <input type='text' disabled class='layui-input' name='amount' value='" + util.thousand(billTotal.checkedAmount) + "'/>" +
            "   </div>" +
            "</div>";

        htmlStr += "</div>";    // total
        htmlStr += "</div>";

        // 对账单文件
        let billFile = value.billFile;
        let options = 'billFile';
        if (util.isNotNull(billFile)) {
            billFile = typeof billFile == 'object' ? billFile : JSON.parse(billFile);
            this.billFile = billFile;
            options = util.isNotNull(billFile.options) ? billFile.options : options;
        }
        htmlStr += "<div class='unchecked-bill-file'>"; // unchecked-bill-file
        htmlStr += "    <input type='checkbox' lay-filter='bill-file-" + flowEntId + "' name='billFile' title='电子账单' lay-skin='primary' value='billFile' " + (options.indexOf('billFile') > -1 ? 'checked' : '') + "/>";
        htmlStr += "    <input type='checkbox' lay-filter='bill-file-" + flowEntId + "' name='billFile' title='数据详情' lay-skin='primary' value='dataDetail' " + (options.indexOf('dataDetail') > -1 ? 'checked' : '') + "/>";
        htmlStr += "    <button type='button' name='downloadFile' class='layui-btn layui-btn-sm unchecked-bill-file-tip'>下载</button>";
        htmlStr += "</div>";     // unchecked-bill-file

        // 数据分析报告
        let analysisFile = value.analysisFile;
        if (util.isNotNull(analysisFile)) {
            analysisFile = typeof analysisFile == 'object' ? analysisFile : JSON.parse(analysisFile);
            this.analysisFile = analysisFile;
        }
        htmlStr += "<div class='bill-analysis-file'>"; // bill-analysis-file
        htmlStr += "    <label class='layui-form-label'>数据分析报告：</label>";
        htmlStr += "    <button type='button' name='downloadFile' class='layui-btn layui-btn-sm unchecked-bill-file-tip'>下载</button>";
        htmlStr += "</div>";     // bill-analysis-file

        htmlStr += "</div>";     // flow-label-content
        htmlStr += "</div>";     // form-item
        $(this.flowEle).append(htmlStr);
        this.billInfos = value.billInfos;
        this.getUncheckedBillInfo(value.billInfos);
    };

    UncheckedBillInfoLabel.prototype.bindEvent = function () {
        let othis = this;
        let flowEntId = this.flowEntId;
        let entityId = this.entityId;

        // 选择账单弹窗
        let chooseBillBtn = this.flowEle.find('button[name=chooseBill]');
        if (chooseBillBtn.length > 0) {
            $(chooseBillBtn).unbind().bind('click', function () {
                // 客户id为空，说明是在申请的时候
                if (util.isNull(entityId) && typeof getEntityId === "function") {
                    entityId = getEntityId();
                    othis.entityId = entityId;
                }
                if (util.isNull(entityId)) {
                    layer.msg("请先选择客户");
                    return
                }
                let checkedItems = [];
                if (util.isNotNull(othis.billInfos)) {
                    othis.billInfos.forEach(function (item) {
                        checkedItems.push(item.id)
                    })
                }
                let index = layer.open({
                    skin: 'account-class',
                    type: 2,
                    area: ['100%', '100%'],
                    title: "选择对账账单",
                    content: "/mobileLabel/uncheckedBillDetail?entityId=" + entityId + "&flowEntId=" + flowEntId + "&checkedItem=" + checkedItems.join(","),
                    cancel: function (index, layero) {
                        let checkedItems = $($(layero[0]).find("iframe").contents()).find("input[name='billItem']:checked");
                        let checkedBillItems = [];
                        $(checkedItems).each(function () {
                            checkedBillItems.push(JSON.parse($(this).val()))
                        });
                        layer.close(index);
                        othis.billInfos = checkedBillItems;
                        // 渲染
                        othis.getUncheckedBillInfo(checkedBillItems);
                    }
                })
            })
        }

        // 生成账单弹窗
        let buildBill = this.flowEle.find('span.build-bill > a');
        if (buildBill.length > 0) {
            $(buildBill).unbind().bind('click', function () {
                if (util.isNull(entityId)) {
                    // 调用 外部实现的 获取产品 ID
                    entityId = getEntityId();
                    othis.entityId = entityId;
                }
                if (util.isNull(entityId)) {
                    layer.msg("请先选择客户");
                    return
                }
                layui.layer.open({
                    type: 2,
                    area: ['100%', '100%'],
                    maxmin: false,
                    title: '生成产品月账单',
                    content: '/mobileLabel/toBuildBillDetail?customerId=' + entityId
                });
            })
        }

        // 刷新账单按钮
        let rebuildBill = this.flowEle.find('span[data-tool=rebuild]');
        if (rebuildBill.length > 0) {
            $(rebuildBill).each(function () {
                let rebuildEle = $(this);
                let productId = rebuildEle.attr('data-product-id');
                let billMonth = rebuildEle.attr('data-bill-month');
                $(rebuildEle).unbind().bind('click', function () {
                    layui.use('layer', function () {
                        let layer = layui.layer;
                        layer.confirm("确定要重新统计账单数据？", function (index) {
                            $(".layui-layer-btn0").css("pointer-events", "none");
                            let loading = layer.load(2);
                            $.post('/bill/rebuildBill', {productId: productId, billMonth: billMonth}, function (res) {
                                layer.close(index);
                                layer.close(loading);
                                if (res.code == 200) {
                                    let bill = res.data;
                                    let billItem = $(rebuildEle).parents('div.unchecked-bill-item');
                                    let platformEle = billItem.find('div[data-type=platform]');
                                    $(platformEle).find('input[name=successCount]').val(bill.platformSuccessCount);
                                    $(platformEle).find('input[name=unitPrice]').val(bill.platformUnitPrice);
                                    $(platformEle).find('input[name=amount]').val(bill.platformAmount);
                                    let customerEle = billItem.find('div[data-type=customer]');
                                    $(customerEle).find('input[name=successCount]').val(bill.customerSuccessCount);
                                    $(customerEle).find('input[name=unitPrice]').val(bill.customerUnitPrice);
                                    $(customerEle).find('input[name=amount]').val(bill.customerAmount);
                                    let checkedEle = billItem.find('div[data-type=checked]');
                                    $(checkedEle).find('input[name=successCount]').val(bill.checkedSuccessCount);
                                    $(checkedEle).find('input[name=unitPrice]').val(bill.checkedUnitPrice);
                                    $(checkedEle).find('input[name=amount]').val(bill.checkedAmount);
                                    othis.takeUncheckedBillTotal();
                                    parent.layer.msg(res.msg, {time: 5000});
                                } else {
                                    parent.layer.msg(res.msg, {time: 5000});
                                }
                            })
                        })
                    })
                })
            })
        }

        // 编辑账单按钮
        let editBill = this.flowEle.find('span[data-tool=edit]');
        if (editBill.length > 0) {
            $(editBill).each(function () {
                let editEle = $(this);
                let billItemEle = $(this).parents('div.unchecked-bill-item').find('input');
                let bill = billItemEle.val();
                $(editEle).unbind().bind('click', function () {
                    othis.showEditBill(bill, billItemEle);
                })
            })
        }

        // 账单复选框
        layui.use('form', function () {
            let form = layui.form;
            form.render();
            form.on('checkbox(unchecked-bill-item-' + flowEntId + ')', function () {
                othis.takeUncheckedBillTotal();
            });
            form.on('checkbox(bill-file-' + flowEntId + ')', function () {
                // 每次重新计算总计之后，重新绑定下载、预览按钮的事件
                othis.billFile = null;
            })
        });

        // 下载按钮事件
        let billFileEle = this.flowEle.find('div.unchecked-bill-file');
        billFileEle.find('button[name=downloadFile]').unbind().bind('click', function () {
            othis.downloadCheckBillFile();
        });

        let analysisFileEle = this.flowEle.find('div.bill-analysis-file');
        analysisFileEle.find('button[name=downloadFile]').unbind().bind('click', function () {
            othis.downloadDataAnalysisFile();
        });
    };

    UncheckedBillInfoLabel.prototype.downloadCheckBillFile = function () {
        let othis = this;
        if (util.isNotNull(this.billFile)) {
            fileTool.downLoadFile(this.billFile);
        } else {
            let billIds = [];
            let labelEle = $(this.flowEle).find('div.label-type-unchecked-bill');
            let checkedBoxes = labelEle.find('div.unchecked-bill-item > div.layui-form-checked');
            if (checkedBoxes.length === 0) {
                layui.layer.msg('请选择账单');
                return;
            }
            let optionBoxes = $(labelEle).find('input[name=billFile]:checked');
            if (optionBoxes.length === 0) {
                layui.layer.msg('请勾选账单或数据详情复选框');
                return;
            }
            let options = [];
            $(optionBoxes).each(function () {
                options.push($(this).val());
            });
            $(checkedBoxes).each(function () {
                let itemEle = $(this).parent();
                billIds.push(itemEle.attr('data-bill-id'));
            });
            // 对账单的实际付款用对账总计
            let checkEle = labelEle.find('div.unchecked-bill-total').find('div[data-type=checked]');
            let billTotal = {
                checkedSuccessCount: checkEle.find('input[name=successCount]').val().replace(/,/g, ''),
                checkedAmount: checkEle.find('input[name=amount]').val().replace(/,/g, ''),
            };
            if (billIds.length > 0) {
                $.post("/bill/buildCheckBillFile", {
                    billIds: billIds.join(','),
                    billTotal: JSON.stringify(billTotal),
                    options: options.join(',')
                }, function (res) {
                    if (res.code == 200) {
                        billFile = res.data;
                        othis.billFile = billFile;
                        fileTool.downLoadFile(billFile);
                    } else {
                        layui.layer.msg(res.msg);
                    }
                })
            }
        }
    };

    UncheckedBillInfoLabel.prototype.downloadDataAnalysisFile = function () {
        let othis = this;
        if (util.isNotNull(this.analysisFile)) {
            fileTool.downLoadFile(this.analysisFile);
        } else {
            let billIds = [];
            let labelEle = $(this.flowEle).find('div.label-type-unchecked-bill');
            let checkedBoxes = labelEle.find('div.unchecked-bill-item > div.layui-form-checked');
            if (checkedBoxes.length === 0) {
                layui.layer.msg('请选择账单');
                return;
            }
            $(checkedBoxes).each(function () {
                let itemEle = $(this).parent();
                billIds.push(itemEle.attr('data-bill-id'));
            });

            if (billIds.length > 0) {
                $.post("/bill/buildDataAnalysisFile", {
                    billIds: billIds.join(','),
                }, function (res) {
                    if (res.code == 200) {
                        analysisFile = res.data;
                        othis.analysisFile = analysisFile;
                        layui.layer.alert('正在生成中，请勿离开。稍后请再次点击下载按钮即可下载', {icon: 7})
                    } else {
                        layui.layer.msg(res.msg);
                    }
                })
            }
        }
    };

    UncheckedBillInfoLabel.prototype.getUncheckedBillInfo = function (billInfos) {
        let othis = this;
        $.ajax({
            type: "POST",
            async: true,
            url: '/bill/readUncheckedBills?temp=' + Math.random(),
            dataType: 'json',
            data: {
                customerId: this.entityId,
                flowEntId: this.flowEntId
            },
            success: function (data) {
                // 渲染标签
                if (data.code == 200) {
                    let billsEle = othis.flowEle.find('div.unchecked-bill-list');
                    let billDetails = data.data;
                    let html = "";
                    if (util.isNotNull(billInfos)) {
                        // 遍历选中的账单，替换从后台查出的数据
                        $.each(billInfos, function (index, checkedBill) {
                            for (let billIndex = 0; billIndex < billDetails.length; billIndex++) {
                                if (billDetails[billIndex]['id'] === checkedBill.id) {
                                    let bill = billDetails[billIndex];
                                    bill.customerSuccessCount = checkedBill.customerSuccessCount;
                                    bill.customerUnitPrice = checkedBill.customerUnitPrice;
                                    bill.customerAmount = checkedBill.customerAmount;
                                    bill.checkedSuccessCount = checkedBill.checkedSuccessCount;
                                    bill.checkedUnitPrice = checkedBill.checkedUnitPrice;
                                    bill.checkedAmount = checkedBill.checkedAmount;
                                    bill.checked = true;
                                    html += othis.addUncheckedBillItem(bill);
                                    break;
                                }
                            }
                        });
                    }
                    billsEle.html(html);
                    layui.use('form', function () {
                        let form = layui.form;
                        form.render();
                    })
                    // 绑定各种事件
                    othis.bindEvent();
                    othis.takeUncheckedBillTotal();
                }
            }
        });
    };

    // 拼接一个未对账账单的样式
    UncheckedBillInfoLabel.prototype.addUncheckedBillItem = function (bill) {
        let html = "";
        html += "<div class='unchecked-bill-item' data-bill-id='" + bill.id + "'>";
        html += this.addBillInfo(bill);
        html += "   <span class='layui-icon layui-icon-edit unchecked-bill-tools' data-tool='edit' title='编辑'></span>";
        html += "   <span class='layui-icon layui-icon-refresh unchecked-bill-tools' data-tool='rebuild' title='重新统计' data-product-id='" + bill.productId + "' data-bill-month='" + bill.billMonth + "'></span>";
        let billFile = bill.billFile;
        if (util.isNotNull(billFile)) {
            billFile = typeof billFile == 'object' ? JSON.stringify(billFile) : billFile;
            html += "   <span class='layui-icon layui-icon-file unchecked-bill-tools' title='电子账单' onclick='fileTool.downLoadFile(" + billFile + ")'></span>";
        }
        html += "</div>"; // unchecked-bill-item
        return html;
    };

    // 拼接checkbox
    UncheckedBillInfoLabel.prototype.addBillInfo = function (bill) {
        let flowEntId = this.flowEntId;
        let itemTitle =
            "<div class=\"bill-item-detail\">" +
            "    <div class=\"bill-title\">" + bill.title + "</div>" +
            "    <div class=\"bill-detail\">" +
            "       <div class=\"bill-detail-line\" data-type=\"platform\">" +
            "           <label>我司数据：</label>" +
            "           <span data-name=\"successCount\">" + util.formatBlank(bill.platformSuccessCount, "0") + "</span>" +
            "           <span>X</span>" +
            "           <span data-name=\"unitPrice\">" + util.formatBlank(bill.platformUnitPrice, "0.000000") + "</span>" +
            "           <span>=</span>" +
            "           <span data-name=\"amount\">" + util.formatBlank(bill.platformAmount, "0.00") + "</span>" +
            "       </div>" +
            "       <div class=\"bill-detail-line\" data-type=\"customer\">" +
            "           <label>客户数据：</label>" +
            "           <span data-name=\"successCount\">" + util.formatBlank(bill.customerSuccessCount, "0") + "</span>" +
            "           <span>X</span>" +
            "           <span data-name=\"unitPrice\">" + util.formatBlank(bill.customerUnitPrice, "0.000000") + "</span>" +
            "           <span>=</span>" +
            "           <span data-name=\"amount\">" + util.formatBlank(bill.customerAmount, "0.00") + "</span>" +
            "       </div>" +
            "       <div class=\"bill-detail-line\" data-type=\"checked\">" +
            "           <label>对账数据：</label>" +
            "           <span data-name=\"successCount\">" + util.formatBlank(bill.checkedSuccessCount, "0") + "</span>" +
            "           <span>X</span>" +
            "           <span data-name=\"unitPrice\">" + util.formatBlank(bill.checkedUnitPrice, "0.000000") + "</span>" +
            "           <span>=</span>" +
            "           <span data-name=\"amount\">" + util.formatBlank(bill.checkedAmount, "0.00") + "</span>" +
            "       </div>" +
            "    </div>" +
            "</div>" ;

        return "<input type='checkbox' lay-filter='unchecked-bill-item-" + flowEntId + "' title='" + itemTitle + "' lay-skin='primary'" + (bill.checked === true ? " checked" : "") + " value='" + JSON.stringify(bill) + "' >";

    }

    // 点击弹出修改对账数据的弹窗
    UncheckedBillInfoLabel.prototype.showEditBill = function (bill, billItemEle) {
        let othis = this;
        bill = typeof bill == "object" ? bill : JSON.parse(bill);
        let billData = {
            'customerSuccessCount': bill.customerSuccessCount,
            'customerUnitPrice': bill.customerUnitPrice,
            'customerAmount': bill.customerAmount,
            'checkedSuccessCount': bill.checkedSuccessCount,
            'checkedUnitPrice': bill.checkedUnitPrice,
            'checkedAmount': bill.checkedAmount,
            'platformSuccessCount': bill.platformSuccessCount,
            'platformUnitPrice': bill.platformUnitPrice,
            'platformAmount': bill.platformAmount
        }
        layui.use(['layer', 'form'], function () {
            let layer = layui.layer;
            let form = layui.form;
            layer.open({
                type: 2,
                area: ['100%', '100%'],
                fixed: true, //不固定
                // scrollbar: false,
                maxmin: false,
                title: '编辑对账数据',
                content: '/mobileLabel/toEditBillPage?' + $.param(billData),
                btn: ['确定'],
                btnAlign: 'c',
                yes: function (index, layero) {
                    let page = $($(layero[0]).find("iframe").contents())

                    let customerEle = page.find("div[data-type=customer]");
                    let customerSuccessCount = customerEle.find('input[name=successCount]').val();
                    let customerUnitPrice = customerEle.find('input[name=unitPrice]').val();
                    let customerAmount = customerEle.find('input[name=amount]').val();

                    let checkedEle = page.find("div[data-type=checked]");
                    let checkedSuccessCount = checkedEle.find('input[name=successCount]').val();
                    let checkedUnitPrice = checkedEle.find('input[name=unitPrice]').val();
                    let checkedAmount = checkedEle.find('input[name=amount]').val();

                    bill['customerSuccessCount'] = customerSuccessCount;
                    bill['customerUnitPrice'] = customerUnitPrice;
                    bill['customerAmount'] = customerAmount;
                    bill['checkedSuccessCount'] = checkedSuccessCount;
                    bill['checkedUnitPrice'] = checkedUnitPrice;
                    bill['checkedAmount'] = checkedAmount;

                    layer.close(index);

                    let checkbox = othis.addBillInfo(bill);
                    let billItem = $(billItemEle).parents('div.unchecked-bill-item');
                    let oldCheckbox = billItem.find('input');
                    $(oldCheckbox).after($(checkbox));
                    $(oldCheckbox).remove();
                    form.render();
                    othis.bindEvent();
                    othis.takeUncheckedBillTotal();
                }
            })
        })
    };

    // 计算对账总计
    UncheckedBillInfoLabel.prototype.takeUncheckedBillTotal = function () {
        let othis = this;
        let flowEle = this.flowEle;
        // 我司数据总计
        let platformTotal = {
            'successCount': 0,
            'amount': 0.0
        };
        // 对账总计
        let checkedTotal = {
            'successCount': 0,
            'amount': 0.0
        };
        let flag = true; // 为false说明输入数据不正确
        let billsEle = $(flowEle).find('div.label-type-unchecked-bill');
        let checkedBoxes = billsEle.find('div.unchecked-bill-item > div.layui-form-checked');
        $(checkedBoxes).each(function () {
            let itemEle = $(this).find('div.bill-detail');
            let platformEle = $(itemEle).find('div[data-type=platform]');
            let platformSuccessCountEle = platformEle.find('span[data-name=successCount]');
            if (!$.isNumeric(platformSuccessCountEle.text())) {
                layer.tips('数量只能是数字', platformSuccessCountEle);
                flag = false;
                return false;
            }
            platformTotal.successCount = platformTotal.successCount + parseInt(platformSuccessCountEle.text());
            let platformUnitPriceEle = platformEle.find('span[data-name=unitPrice]');
            if (!$.isNumeric(platformUnitPriceEle.text())) {
                layer.tips('单价只能是数字', platformUnitPriceEle);
                flag = false;
                return false;
            }
            let platformAmountEle = platformEle.find('span[data-name=amount]');
            if (!$.isNumeric(platformAmountEle.text())) {
                layer.tips('金额只能是数字', platformAmountEle);
                flag = false;
                return false;
            }
            platformTotal.amount = util.accAdd(platformTotal.amount, parseFloat(platformAmountEle.text()));

            let customerEle = $(itemEle).find('div[data-type=customer]');
            let customerSuccessCountEle = customerEle.find('span[data-name=successCount]');
            if (!$.isNumeric(customerSuccessCountEle.text())) {
                layer.tips('数量只能是数字', customerSuccessCountEle);
                flag = false;
                return false;
            }
            let customerUnitPriceEle = customerEle.find('span[data-name=unitPrice]');
            if (!$.isNumeric(customerUnitPriceEle.text())) {
                layer.tips('单价只能是数字', customerUnitPriceEle);
                flag = false;
                return false;
            }
            let customerAmountEle = customerEle.find('span[data-name=amount]');
            if (!$.isNumeric(customerAmountEle.text())) {
                layer.tips('金额只能是数字', customerAmountEle);
                flag = false;
                return false;
            }

            let checkedEle = $(itemEle).find('div[data-type=checked]');
            let checkedSuccessCountEle = checkedEle.find('span[data-name=successCount]');
            if (!$.isNumeric(checkedSuccessCountEle.text())) {
                layer.tips('数量只能是数字', checkedSuccessCountEle);
                flag = false;
                return false;
            }
            checkedTotal.successCount = checkedTotal.successCount + parseInt(checkedSuccessCountEle.text());
            let checkedUnitPriceEle = checkedEle.find('span[data-name=unitPrice]');
            if (!$.isNumeric(checkedUnitPriceEle.text())) {
                layer.tips('单价只能是数字', checkedUnitPriceEle);
                flag = false;
                return false;
            }
            let checkedAmountEle = checkedEle.find('span[data-name=amount]');
            if (!$.isNumeric(checkedAmountEle.text())) {
                layer.tips('金额只能是数字', checkedAmountEle);
                flag = false;
                return false;
            }
            checkedTotal.amount = util.accAdd(checkedTotal.amount, parseFloat(checkedAmountEle.text()));
        });
        // 输入数据不正确，置为0
        if (!flag) {
            platformTotal = {
                'successCount': 0,
                'amount': 0.0
            };
            checkedTotal = {
                'successCount': 0,
                'amount': 0.0
            };
        }
        let billTotalEle = billsEle.find('div.unchecked-bill-total');
        let platformTotalEle = $(billTotalEle).find('div[data-type=platform]');
        platformTotalEle.find('input[name=successCount]').val(util.thousand(platformTotal.successCount));
        platformTotalEle.find('input[name=amount]').val(util.formatNum(platformTotal.amount, 2));

        let checkedTotalEle = $(billTotalEle).find('div[data-type=checked]');
        checkedTotalEle.find('input[name=successCount]').val(util.thousand(checkedTotal.successCount));
        checkedTotalEle.find('input[name=amount]').val(util.formatNum(checkedTotal.amount, 2));

        // 每次重新计算总计之后，重新绑定下载、预览按钮的事件
        othis.billFile = null;
        othis.analysisFile = null;
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    UncheckedBillInfoLabel.prototype.getValue = function () {
        let othis = this;
        let flowEle = this.flowEle;
        let value = {};
        let billInfos = [];
        let billIds = [];
        let billTotal = {
            'platformSuccessCount': 0,
            'platformAmount': 0.0,
            'checkedSuccessCount': 0,
            'checkedAmount': 0.0
        };
        // 获取选中的账单复选框
        let checkedBoxes = $(flowEle).find('div.unchecked-bill-item > div.layui-form-checked');
        $(checkedBoxes).each(function () {
            let billInfo = {};
            let itemEle = $(this).parent();
            billInfo['id'] = itemEle.attr('data-bill-id');
            billIds.push(itemEle.attr('data-bill-id'));
            billInfo['title'] = $(itemEle).find('div.bill-title').text();

            let platformEle = $(itemEle).find('div[data-type=platform]');
            let platformSuccessCountEle = platformEle.find('span[data-name=successCount]');
            let platformUnitPriceEle = platformEle.find('span[data-name=unitPrice]');
            let platformAmountEle = platformEle.find('span[data-name=amount]');
            billInfo['platformSuccessCount'] = platformSuccessCountEle.text();
            billInfo['platformUnitPrice'] = platformUnitPriceEle.text();
            billInfo['platformAmount'] = platformAmountEle.text();
            billTotal.platformSuccessCount = billTotal.platformSuccessCount + parseInt(billInfo.platformSuccessCount);
            billTotal.platformAmount = util.accAdd(billTotal.platformAmount, billInfo.platformAmount);

            let customerEle = $(itemEle).find('div[data-type=customer]');
            let customerSuccessCountEle = customerEle.find('span[data-name=successCount]');
            let customerUnitPriceEle = customerEle.find('span[data-name=unitPrice]');
            let customerAmountEle = customerEle.find('span[data-name=amount]');
            billInfo['customerSuccessCount'] = customerSuccessCountEle.text();
            billInfo['customerUnitPrice'] = customerUnitPriceEle.text();
            billInfo['customerAmount'] = customerAmountEle.text();

            let checkedEle = $(itemEle).find('div[data-type=checked]');
            let checkedSuccessCountEle = checkedEle.find('span[data-name=successCount]');
            let checkedUnitPriceEle = checkedEle.find('span[data-name=unitPrice]');
            let checkedAmountEle = checkedEle.find('span[data-name=amount]');
            billInfo['checkedSuccessCount'] = checkedSuccessCountEle.text();
            billInfo['checkedUnitPrice'] = checkedUnitPriceEle.text();
            billInfo['checkedAmount'] = checkedAmountEle.text();
            billTotal.checkedSuccessCount = billTotal.checkedSuccessCount + parseInt(billInfo.checkedSuccessCount);
            billTotal.checkedAmount = util.accAdd(billTotal.checkedAmount, billInfo.checkedAmount);

            billInfos.push(util.sortObjectKey(billInfo));
        });
        value['billInfos'] = billInfos;
        value['billTotal'] = billTotal;
        // 电子账单
        let billFile = this.billFile;
        if (util.isNotNull(billFile)) {
            value['billFile'] = billFile;
        } else {
            let optionBoxes = $(flowEle).find('div.unchecked-bill-file > input[name=billFile]:checked');
            let options = [];
            $(optionBoxes).each(function () {
                options.push($(this).val());
            });
            let loading = layui.layer.load(2);
            $.ajax({
                type: "POST",
                async: false,
                url: "/bill/buildCheckBillFile",
                dataType: 'json',
                data: {
                    billIds: billIds.join(','),
                    billTotal: JSON.stringify(billTotal),
                    options: options.join(',')
                },
                success: function (res) {
                    layui.layer.close(loading);
                    if (res.code == 200) {
                        billFile = res.data;
                        value['billFile'] = billFile;
                        othis.billFile = billFile;
                    } else {
                        layui.layer.msg(res.msg);
                    }
                }
            })
        }
        // 数据报告
        let analysisFile = this.analysisFile;
        if (util.isNotNull(analysisFile)) {
            value['analysisFile'] = analysisFile;
        } else {
            let loading = layui.layer.load(2);
            $.ajax({
                type: "POST",
                async: false,
                url: "/bill/buildDataAnalysisFile",
                dataType: 'json',
                data: {
                    billIds: billIds.join(','),
                },
                success: function (res) {
                    layui.layer.close(loading);
                    if (res.code == 200) {
                        analysisFile = res.data;
                        value['analysisFile'] = analysisFile;
                        othis.analysisFile = analysisFile;
                    } else {
                        layui.layer.msg(res.msg);
                    }
                }
            })
        }
        return value;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    UncheckedBillInfoLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    UncheckedBillInfoLabel.prototype.verify = function () {
        let flowEle = this.flowEle;
        let checkedBoxes = $(flowEle).find('div.unchecked-bill-item > div.layui-form-checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请选择对账账单');
            return false;
        }

        var optionBoxes = $(flowEle).find('div.unchecked-bill-file > input[name=billFile]:checked');
        if (optionBoxes.length === 0) {
            layer.msg('请勾选账单或数据详情复选框');
            return false;
        }
        return true;
    };

    return UncheckedBillInfoLabel;
});