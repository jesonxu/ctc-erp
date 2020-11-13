/**
 *我司银行信息标签 19
 */
(function (window, factory) {
    window.SelfBankLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let SelfBankLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【我司银行信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【我司银行信息标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    SelfBankLabel.prototype.toText = function (value) {
        console.log("我司银行信息标签：name:" + this.name + " - value:" + value);
        if (util.isNull(value)) {
            return this.name + "：无";
        }
        let selfBankInfo = {};
        let bankProps = value.split('####');
        for (let index = 0; index < bankProps.length; index++) {
            let propInfo = bankProps[index].split(":");
            selfBankInfo[propInfo[0]] = bankProps[propInfo[1]];
        }
        let html = "<br/>名称：" + util.formatBlank(selfBankInfo.accountName);
        html += "<br/>开户银行：" + util.formatBlank(selfBankInfo.accountBank);
        html += "<br/>银行账号：" + util.formatBlank(selfBankInfo.bankAccount);
        return this.name + "：<div class='flow-record-2-content'>" + html + "</div>";
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    SelfBankLabel.prototype.render = function (flowEle, value, required,entityId) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【我司银行信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        this.data = util.formatBlank(value);
        this.entityId = util.formatBlank(entityId);
        let labelDom =
            "<div class='layui-form-item label-type-self-bank' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "       <button class='layui-btn layui-btn-primary' data-btn-id='" + this.id + "'>请选择" + this.name + "</button>" +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
    };

    /**
     * 绑定事件
     */
    SelfBankLabel.prototype.bindEvent = function () {
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
            if (util.isNotNull(mine.data)){
                infoArr = mine.data.split("####")[0].split(":")[1];
            }
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择" + mine.name,
                content: "/mobileLabel/invoiceDetail?entityId=" + mine.entityId + "&type=2&checked=" + infoArr + "&time=" + new Date().getTime(),
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
    SelfBankLabel.prototype.getValue = function () {
        return this.data;
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    SelfBankLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    SelfBankLabel.prototype.verify = function () {
        // let btn = $($(this.flowEle).find("div[data-label-id='" + this.id + "']")).find("button[data-btn-id='" + this.id + "']");
        if (util.isTrue(this.required) && util.isNull(this.getValue())) {
            // layer.tips(this.name + "必须选择", btn[0], {tips: 1});
            layer.msg(this.name + "必须选择");
            return false;
        }
        return true;
    };
    return SelfBankLabel;
});