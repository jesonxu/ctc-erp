/**
 * ****************************************************************
 * 流程中的一些特殊数据,是不属于标签进行管辖的数据，是流程专有数据
 * ****************************************************************
 */
(function (window, factory) {
    window.RecordUnlabeledData = factory();
})(window, function () {
    /**
     * 流程基础数据
     */
    let RecordUnlabeledData = function (recordFlowMsg) {
        this.flowMsg = recordFlowMsg;
        // 转换处理记录数据
        this.parseRecordMsg(recordFlowMsg);
        // 转换平台基础数据
        this.parseBaseData();
    };

    /**
     * 渲染 流程的基础数据（BaseData）
     * @return {string}
     */
    RecordUnlabeledData.prototype.baseDateToText = function () {
        if (util.isNull(this.baseData)) {
            return "";
        }
        let recordDataId = util.uuid();
        this.dataId = recordDataId;
        // 流程详情
        let detailDom = [];

        // 账单编号
        let billInfoOfNumber= this.baseData['账单编号'];
        if (util.isNotNull(billInfoOfNumber)){
            detailDom.push("<div class='flow-detail-line'>账单编号：" + billInfoOfNumber + "</div>");
        }

        // 我司账单价格信息
        let billPriceInfo = this.baseData['BILL_PRICE_INFO_KEY'];
        if (util.isNotNull(billPriceInfo)) {
            detailDom.push("<div class='flow-detail-line'>");
            // 构建点击展示
            detailDom.push("我司数据：<span onclick='new RecordUnlabeledData(" + this.flowMsg
                + ").showBillDetail(\"" + recordDataId + "\")' class='bill-record-detail'>详情</span><br>");
            // 构建账单详情（是否要首先构建？）
            detailDom.push(this.buildBillDetail(this.baseData));
            detailDom.push("</div>")
        }

        // 基础数据中的特殊key（这几个 是销账流程中的特殊字段）
        let specialKey = ["BILL_PRICE_INFO_KEY", "账单编号", "lastCopyFilePath", "DAHAN_BILL_FILE_KEY"];
        // 去除账单的特殊标签
        for (let key in this.baseData) {
            if (specialKey.indexOf(key) < 0) {
                detailDom.push("<div class='flow-detail-line'>" + key + "：" + util.thousand(this.baseData[key]) + "</div>");
            }
        }

        // 获取账单的附件
        let billFiles = this.baseData['DAHAN_BILL_FILE_KEY'];
        if (util.isNotNull(billFiles)) {
            let files = [];
            $(billFiles.split(';')).each(function (index, billFile) {
                console.log("账单附件信息：" + billFile);
                let arr = billFile.split(/[\\/]/);
                files.push({
                    fileName: arr[arr.length - 1],
                    filePath: billFile
                });
            });
            // 处理文件
            if (util.arrayNotNull(files)) {
                // 查看Excel 下载Excel 下载PDF
                detailDom.push("<div class='flow-detail-line'>电子账单：" + this.initElectronicBill(files) + "</div>");
            }
        }
        return detailDom.join("");
    };


    /**
     * 平台的原来价格（这个主要用于调价流程 和 国际调价流程）
     * @constructor
     * @return {string}
     */
    RecordUnlabeledData.prototype.beforePriceToText = function(flowClass){
        if (util.isNull(flowClass)){
            return "";
        }
        // 原来价格
        let beforePrices = "";
        try {
            // 原来调价信息
            let beforePriceStr = this.recordMsgInfos["原来价格"];
            if (util.isNotNull(beforePriceStr)) {
                beforePrices = JSON.parse(beforePriceStr);
            }
        } catch (e) {
            console.log("捕获异常，不影响业务")
        }
        let beforePricesDom = [];
        if (util.isNotNull(beforePrices)) {
            let beforePriceDom = "<span>原来价格：</span></br>";
            for (let beforePriceIndex = 0; beforePriceIndex < beforePrices.length; beforePriceIndex++) {
                // 原来价格信息
                let beforePrice = beforePrices[beforePriceIndex];
                // 记录的时间
                let timeInfo = "(" + beforePrice.startTime + " 至 " + beforePrice.endTime + ") ";
                if (flowClass === "[AdjustPriceFlow]") {
                    // 调价流程
                    let maxSend = (util.isNull(beforePrice.maxSend) || parseFloat(beforePrice.maxSend) === 0) ? " <span>∞</span> " : beforePrice.maxSend;
                    // 调价流程 展示调价的内容信息
                    beforePriceDom += ("<span class='flow-record-2-content'>" + timeInfo + beforePrice.minSend + "条 <= 发送量 < " + maxSend + "条，价格：" + beforePrice.price + "元</span></br>");
                }
                if (flowClass === '[InterAdjustPriceFlow]') {
                    // 国际调价流程 展示文件 点击下载
                    let fileUrl = util.isNull(beforePrice.remark) ? " ∞ " : beforePrice.remark;
                    let fileName = timeInfo + "国际短信价格.xlsx";
                    let fileInfo = {
                        "fileName": fileName,
                        "filePath": fileUrl
                    };
                    // 调价流程 展示调价的内容信息(移动端暂时不做预览)
                    beforePriceDom += ("<span class='flow-record-2-content'>" + timeInfo
                        + "<span class='file-preview' onclick='fileTool.downLoadFile(" + JSON.stringify(fileInfo)
                        + ")'>(点击下载)</span></span></br>");
                }
            }
            beforePricesDom.push(beforePriceDom);
        }
        return beforePricesDom.join("");
    };


    /**
     * 构建展示账单详情
     * @param baseData
     * @returns {*}
     */
    RecordUnlabeledData.prototype.buildBillDetail = function (baseData) {
        // 构建表格
        let detail = $('<div class="table-flow-company-data" id="' + this.dataId + '"></div>');
        detail.append('<table cellpadding="0" cellspacing="0" ></table>');

        // 构建表头
        let tableThHtml = $('<tr></tr>');
        tableThHtml.append('<td width="40%">时间段</td>');
        tableThHtml.append('<td width="40%">价格信息</td>');
        tableThHtml.append('<td width="20%">发送量</td>');
        detail.find('table').append(tableThHtml);

        // 账单详情
        let billsDetail = baseData['BILL_PRICE_INFO_KEY'];
        if (util.isNotNull(billsDetail)) {
            billsDetail = JSON.parse(billsDetail);
            $(billsDetail).each(function (index, item) {
                let tableTrHtml = $('<tr class="flow-view-table-th" style="border-top:0.01em solid #666;"></tr>');
                tableTrHtml.append('<td class="flow-view-table-td">' + item.timeQuantum.split('、').join('<br/>') + '</td>');
                if (item.modifyPriceInfo.endWith('xls') || item.modifyPriceInfo.endWith('xlsx')) {
                    let arr = item.modifyPriceInfo.split(/[\\/]/);
                    let json = {
                        fileName: (arr[arr.length - 1]),
                        filePath: item.modifyPriceInfo
                    };
                    let aHtml = '<span onclick="fileTool.downLoadFile(' + JSON.stringify(json).replace(/"/g, '&quot;') + ')">调价文件</span>'
                    tableTrHtml.append('<td class="flow-view-table-td" style="white-space:pre-line;word-wrap: break-word;word-break: break-all;">' + aHtml + '</td>');
                } else {
                    tableTrHtml.append('<td class="flow-view-table-td" style="white-space:pre-line;word-wrap: break-word;word-break: break-all;">' + item.modifyPriceInfo + '</td>');
                }
                tableTrHtml.append('<td class="flow-view-table-td">' + util.thousand(item.successCount) + (item.provinceSuccessCount ? ('（省网：' + util.thousand(item.provinceSuccessCount) + '）') : '') + '</td>');
                detail.find('table').append(tableTrHtml);
            });
        }
        return detail.prop("outerHTML");
    };

    /**
     * 展示账单详情
     * @param id
     */
    RecordUnlabeledData.prototype.showBillDetail = function (id) {
        $("#" + id).toggleClass('table-flow-company-data-show');
    };

    /**
     * 解析处理记录的消息数据
     * @param recordMsg 记录信息
     * @returns {boolean}
     */
    RecordUnlabeledData.prototype.parseRecordMsg = function (recordMsg) {
        try {
            if (util.isNotNull(recordMsg)) {
                let recordMsgInfos = recordMsg;
                if (typeof recordMsg === "string") {
                    recordMsgInfos = JSON.parse(recordMsg);
                }
                if (util.isNotNull(recordMsgInfos)) {
                    this.recordMsgInfos = recordMsgInfos;
                }
            }
        } catch (e) {
            console.log("处理记录，流程数据转换错误", e)
        }
    };

    /**
     * 转换平台的基础数据
     * @returns {boolean}
     */
    RecordUnlabeledData.prototype.parseBaseData = function () {
        try {
            if (util.isNotNull(this.recordMsgInfos)) {
                // 账单流程的平台基础数据
                let platformBaseData = this.recordMsgInfos['baseData'];
                if (util.isNotNull(platformBaseData)) {
                    let baseData = JSON.parse(platformBaseData);
                    if (util.isNotNull(baseData)) {
                        this.baseData = baseData;
                    }
                }
            }
        } catch (e) {
            console.log("处理记录基础数据转换错误", e)
        }
    };


    return RecordUnlabeledData;
});