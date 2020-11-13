/**
 *我司开票信息标签 17
 */
(function (window, factory) {
    window.SelfInvoiceLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let SelfInvoiceLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【我司开票信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【我司开票信息标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    SelfInvoiceLabel.prototype.toText = function (value) {
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        let invoiceProps = {};
        let invoicePropArr = value.split('####');
        for (let index = 0; index < invoicePropArr.length; index++) {
            let prop = invoicePropArr[index].split(":");
            invoiceProps[prop[0]] = prop[1];
        }
        let spanTitle = "公司名称：" + util.formatBlank(invoiceProps.companyName);
        spanTitle += "<br>税务号：" + util.formatBlank(invoiceProps.taxNumber);
        spanTitle += "<br>公司地址：" + util.formatBlank(invoiceProps.companyAddress);
        spanTitle += "<br>联系电话：" + util.formatBlank(invoiceProps.phone);
        spanTitle += "<br>开户银行：" + util.formatBlank(invoiceProps.accountBank);
        spanTitle += "<br>银行账号：" + util.formatBlank(invoiceProps.bankAccount);
        return this.name + "：<div class='flow-record-2-content'>" + spanTitle + "</div>";
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     * @param entityId 主体ID
     */
    SelfInvoiceLabel.prototype.render = function (flowEle, value, required, entityId) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【我司开票信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        this.data = util.formatBlank(value);
        this.entityId = util.formatBlank(entityId);
        let labelDom =
            "<div class='layui-form-item label-type-self-invoice' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "       <button class='layui-btn layui-btn-primary' data-btn-id='" + this.id + "'>请选择" + this.name +
            "       </button>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        // 绑定事件
        this.bindEvent();
    };

    /**
     * 绑定事件
     */
    SelfInvoiceLabel.prototype.bindEvent = function () {
        let btn = $($(this.flowEle).find("div[data-label-id='" + this.id + "']")).find("button[data-btn-id='" + this.id + "']");
        let mine = this;
        $(btn).click(function () {
            if (util.isNull(mine.entityId) && typeof getEntityId === "function"){
                mine.entityId = getEntityId();
            }
            if (util.isNull(mine.entityId)) {
                layer.tips("请先选择主体（客户或供应商）", btn, {tips: 1});
                return false;
            }
            let infoArr = "";
            if (util.isNotNull(mine.data)) {
                infoArr = mine.data.split("####")[0].split(":")[1];
            }
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/invoiceDetail?entityId=" + mine.entityId + "&type=0&checked=" + infoArr + "&time=" + new Date().getTime(),
                cancel: function (index, layero) {
                    let checkedItem = $($(layero[0]).find("iframe").contents()).find("input[name='invoice']:checked");
                    let title = $(checkedItem).attr("data-invoice-text");
                    mine.data = util.formatBlank($(checkedItem).val());
                    $(btn).html(util.formatBlank(title, "请选择" + mine.name));
                }
            });
            layer.full(index);
        });
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    SelfInvoiceLabel.prototype.getValue = function () {
        return this.data;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    SelfInvoiceLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    SelfInvoiceLabel.prototype.verify = function () {
        // let btn = $($(this.flowEle).find("div[data-label-id='" + this.id + "']")).find("button[data-btn-id='" + this.id + "']");
        if (util.isTrue(this.required) && util.isNull(this.getValue())) {
            // layer.tips(this.name + "必须选择", btn[0], {tips: 1});
            layer.msg(this.name + "必须选择");
            return false;
        }
        return true;
    };
    return SelfInvoiceLabel;
});