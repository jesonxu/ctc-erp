/**
 *平台账号信息标签
 */
(function (window, factory) {
    window.PlatformAccountInfoLabel = factory();
})(window, function () {

    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let PlatformAccountInfoLabel = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【平台账号信息标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【平台账号信息标签】ID为空");
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    PlatformAccountInfoLabel.prototype.toText = function (value) {
        let accountInfos = "";
        try {
            if (util.isNotNull(value)) {
                accountInfos = JSON.parse(value);
            }
        } catch (e) {
            console.log("捕获数据解析异常", e);
        }
        if (util.arrayNull(accountInfos)) {
            return this.name + ":无";
        }
        let accountItems = [];
        for (let accountIndex = 0; accountIndex < accountInfos.length; accountIndex++) {
            let accountInfo = accountInfos[accountIndex];
            let account = accountInfo.account;
            let pwd = accountInfo.pwd;
            let note = accountInfo.note;
            let itemIndex = accountIndex + 1;
            accountItems.push("<div class='flow-record-2-content'>账号"+itemIndex+"：" + util.formatBlank(account, "")
                + "<br/>密码"+itemIndex+"：" + util.formatBlank(pwd)
                + "<br/>备注"+itemIndex+"：" + util.formatBlank(note) + "</div>");
        }
        return this.name + "：" + accountItems.join("");
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    PlatformAccountInfoLabel.prototype.render = function (flowEle, value, required) {
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【平台账号信息标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        let labelDom =
            "<div class='layui-form-item label-type-platform-account' data-label-id='" + this.id + "'>" +
            "    <label " + util.getRequired(this.required) + ">" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" + this.accountInfoDom(value) +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
    };

    /**
     * 绑定按钮事件
     */
    PlatformAccountInfoLabel.prototype.bindEvent = function () {
        let labelEle = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        let addBtnEle = $(labelEle).find("button[data-item-index]");
        let mine = this;
        if (util.arrayNotNull(addBtnEle)) {
            $(addBtnEle[0]).click(function () {
                let itemIndex = $(this).attr("data-item-index");
                mine.add(this, itemIndex);
            });
        }
        let deleteBtnEle = $(labelEle).find("button[data-delete-id]");
        if (util.arrayNotNull(deleteBtnEle)) {
            $(deleteBtnEle[0]).click(function () {
                let deleteIndex = $(this).attr("data-delete-id");
                mine.delete(this, deleteIndex);
            });
        }
    };

    /**
     * 获取账号信息的HTML
     * @param labelValue
     * @returns {string}
     */
    PlatformAccountInfoLabel.prototype.accountInfoDom = function (labelValue) {
        let accountInfos = [];
        if (util.isNotNull(labelValue)) {
            accountInfos = JSON.parse(labelValue);
        }
        if (accountInfos.length === 0) {
            // 没有默认值
            return this.getAccountDom(1, null);
        } else {
            let accountDomArr = [];
            for (let index = 0; index < accountInfos.length; index++) {
                let accountInfo = accountInfos[index];
                let dom = this.getAccountDom(index + 1, accountInfo);
                accountDomArr.push(dom);
            }
            return accountDomArr.join("");
        }
    };

    /**
     * 获取组装内容
     * @param index
     * @param value
     * @returns {string}
     */
    PlatformAccountInfoLabel.prototype.getAccountDom = function (index, value) {
        index = parseInt(index);
        let account = "";
        let pwd = "";
        let note = "";
        if (util.isNotNull(value)) {
            account = util.formatBlank(value.account);
            pwd = util.formatBlank(value.pwd);
            note = util.formatBlank(value.note);
        }
        return "<div class=' platform-account-item ' data-platform-account-index='" + index + "'>" +
            "    <div class='platform-account-line'>" +
            "       <label required='true'>账号：</label>" +
            "       <div class='platform-account-value'>" +
            "            <input type='text' class='layui-input account' name='account' value='" + account + "' placeholder='请填写账号' >" +
            "       </div>" +
            "    </div>" +
            "    <div class='platform-account-line'>" +
            "       <label>密码：</label>" +
            "       <div class='platform-account-value'>" +
            "           <input type='text' class='layui-input password ' name='password' value='" + pwd + "' placeholder='请填写账号密码'>" +
            "       </div>" +
            "    </div>" +
            "    <div class='platform-account-line'>" +
            "       <label>描述：</label>" +
            "       <div class='platform-account-value'>" +
            "           <input type='text' class='layui-input note' name='note' value='" + note + "' placeholder='请填写描述（100字以内）'>" +
            "       </div>" +
            "    </div>" + this.getOpts(index) +
            "</div>";
    };

    /**
     * 获取操作的dom
     * @param index
     * @returns {string}
     */
    PlatformAccountInfoLabel.prototype.getOpts = function (index) {
        let optsDom =
            "<div class='platform-account-line platform-opts'>" +
            "   <button class='layui-btn layui-btn-sm layui-btn-primary operation-add' data-item-index='" + (index + 1) + "'>" +
            "       <i class='layui-icon layui-icon-add-1'></i>添加" +
            "   </button>";
        if (index > 1) {
            optsDom += "<button class='layui-btn layui-btn-sm layui-btn-primary operation-delete' data-delete-id='" + index + "'>" +
                "<i class='layui-icon layui-icon-delete'></i> 删除</button>";
        }
        optsDom += "</div>";
        return optsDom;
    };


    /**
     * 添加项
     * @param ele
     * @param index
     */
    PlatformAccountInfoLabel.prototype.add = function (ele, index) {
        // 流程的展示的区域
        this.flowEle = $(ele).parent().parent().parent().parent().parent().parent();
        let verifyResult = this.verify();
        if (!verifyResult) {
            return;
        }
        // 找到当前的项
        let thisItem = $(ele).parent().parent();
        let newItem = this.getAccountDom(index, null);
        $(thisItem).after(newItem);
        // 删除本节点的操作按钮
        $(thisItem).find("div[class*='platform-opts']").remove();
        this.bindEvent();
    };

    /**
     * 删除项
     * @param ele
     * @param index
     */
    PlatformAccountInfoLabel.prototype.delete = function (ele, index) {
        index = parseInt(index);
        let thisItem = $(ele).parent().parent();
        if (index > 1) {
            let optsDom = this.getOpts(index - 1);
            $(thisItem).prev().append(optsDom);
        }
        thisItem.remove();
        this.bindEvent();
    };


    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    PlatformAccountInfoLabel.prototype.getValue = function () {
        // 账户信息
        let accountInfos = [];
        // 当前流程
        let flowLabelRow = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        if (flowLabelRow.length > 0) {
            let labelItems = $(flowLabelRow).find("div[data-platform-account-index]");
            if (labelItems.length > 0) {
                for (let itemIndex = 0; itemIndex < labelItems.length; itemIndex++) {
                    let labelItem = labelItems[itemIndex];
                    let account = $(labelItem).find("input[name='account']").val();
                    let pwd = $(labelItem).find("input[name='password']").val();
                    let note = $(labelItem).find("input[name='note']").val();
                    accountInfos.push({
                        "account": account,
                        "pwd": pwd,
                        "note": note
                    });
                }
            }
        }
        if (accountInfos.length === 0) {
            return "";
        }
        return JSON.stringify(accountInfos);
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    PlatformAccountInfoLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    PlatformAccountInfoLabel.prototype.verify = function () {
        // 当前流程
        let flowLabelRow = $(this.flowEle).find("div[data-label-id='" + this.id + "']");
        if (flowLabelRow.length === 0) {
            return false;
        }
        let labelItems = $(flowLabelRow).find("div[data-platform-account-index]");
        if (labelItems.length === 0) {
            return false;
        }
        for (let itemIndex = 0; itemIndex < labelItems.length; itemIndex++) {
            let accountEle = $(labelItems[itemIndex]).find("input[name='account']");
            if (util.isNull($(accountEle).val())) {
                layer.msg(this.name + "第" + (itemIndex + 1) + "项，账号不能为空");
                return false;
            }
        }
        // 初始化的时候已经有选择 不需要再校验
        return true;
    };


    return PlatformAccountInfoLabel;
});