layui.use(['layer', 'element', "form"], function () {
    let layer = layui.layer;
    let form = layui.form;
    // 联系人（部门）
    const contactPropName = {
        deptName: "部门名称",
        contactsName: "联系人姓名",
        post: "职位",
        wx: "微信",
        qq: "QQ",
        firstPhone: "手机1",
        secondPhone: "手机2",
        telephone: "座机",
        email: "邮箱",
        remark: "备注",
    };

    // 联系日志
    const contactLogPropName = {
        recordTime: "联系时间",
        contacts: "联系人",
        contactsForm: "联系形式",
        content: "工作内容",
        result: "工作成果",
    };

    // 银行信息属性
    const bankAccountProp = {
        accountName: "名称",
        accountBank: "开户银行",
        bankAccount: "银行账号",
    };

    // 开票信息属性
    const invoiceProp = {
        companyName: "公司名称",
        taxNumber: "税务号",
        companyAddress: "公司地址",
        phone: "联系电话",
        accountBank: "开户银行",
        bankAccount: "银行账号",
    };

    // 客户ID
    let customerId = util.getUrlParam("customerId");
    loadCustomerInfo(customerId);

    /**
     * 加载客户信息
     * @param customerId 客户ID
     */
    function loadCustomerInfo(customerId) {
        $.ajax({
            type: "POST",
            url: "/customer/queryCustomerById.action",
            dataType: 'json',
            data: {
                customerId: customerId
            },
            success: function (result) {
                if (result.code === 200) {
                    let customerAll = result.data;
                    let baseInfo = customerAll.customerDetail;
                    for (let prop in baseInfo) {
                        $("#" + prop).text(baseInfo[prop]);
                    }
                    let mainContactUrl = baseInfo.businessCardPath;
                    showBusinessCard(mainContactUrl);

                    // 联系人部门
                    let contacts = customerAll.contacts;
                    showContact(contacts);
                    // 联系人日志
                    let contactLogs = customerAll.contactLogs;
                    showContactLogs(contactLogs);
                    // 银行信息
                    let bankAccounts = customerAll.bankAccounts;
                    showBankInfos(bankAccounts);
                    // 发票信息
                    let invoices = customerAll.invoices;
                    showInvoiceInfos(invoices);
                } else {
                    layer.msg(result.msg);
                }
            },
            error: function (result) {
                layer.msg("加载客户信息错误");
            }
        });
    }

    /**
     * 展示名片
     * @param mainContactUrl
     */
    function showBusinessCard(mainContactUrl) {
        let showBusinessCardBtn = $("#show-business-card");
        if (util.isNull(mainContactUrl)) {
            showBusinessCardBtn.text("暂未上传名片");

            return;
        }
        showBusinessCardBtn.addClass("show-business-card");
        showBusinessCardBtn.attr("data-img-path", Base64.encode(mainContactUrl));
        showBusinessCardBtn.unbind("click").bind("click", function (event) {
            let cardShow = $(this).attr("data-show");
            if (util.isTrue(cardShow)) {
                // 删除
                $(this).text("点击展示名片");
                $(this).attr("data-show", false);
            } else {
                $(this).text("点击隐藏名片");
                $(this).attr("data-show", true);
                $("#contactBusinessCard").attr("src", "/mobile/viewBusinessCard?filePath=" + (mainContactUrl.replace(/\\/g, "/")));
            }
            // 展示 或者显示
            $("div[data-refer='show-business-card']").toggleClass("business-hide");
        });
    }

    /**
     * 展示联系人信息
     * @param contacts
     */
    function showContact(contacts) {
        if (util.arrayNotNull(contacts)) {
            let contactItems = [];
            let itemIds = [];
            $(contacts).each(function (index, contact) {
                let contactDoms = [];
                for (let contactProp in contactPropName) {
                    let contactItemPropName = contactPropName[contactProp];
                    let contactPropValue = contact[contactProp];
                    if (util.isNotNull(contactPropValue)) {
                        contactDoms.push(
                            "<div class='content-line'>" +
                            "    <span class='content-line-title'>" + contactItemPropName + "：</span>" +
                            "    <span class='content-line-value' >" + contactPropValue + "</span>" +
                            "</div>");
                    }
                }
                // 增加展示名片内容
                if (util.isNotNull(contact.businessCardPath)) {
                    let contactId = contact.supplierContactsId;
                    itemIds.push(contactId);
                    let imgPathCode = Base64.encode(contact.businessCardPath);
                    contactDoms.push(
                        "<div class='content-line'>" +
                        "    <span class='content-line-title'>联系人名片：</span>" +
                        "    <span data-img-refer-id='" + contactId + "' class='content-line-value show-business-card'  data-img-path='" + imgPathCode + "'>点击展示名片" +
                        "    </span>" +
                        "    <div class='business-card-area business-hide' data-refer='" + contactId + "' >" +
                        "        <img class='business-card-img' data-img-id='" + contactId + "'/>" +
                        "    </div>" +
                        "</div>");
                }
                if (contactDoms.length > 0) {
                    contactItems.push("<div class='contact-item'>" + contactDoms.join("") + "</div>")
                }
            });
            if (contactItems.length > 0) {
                $("#contact-area").html(contactItems.join(""));
            }
            if (itemIds.length > 0) {
                bindBusinessCardEvent(itemIds);
            }
        }
    }

    /**
     * 绑定展示联系人名片事件
     */
    function bindBusinessCardEvent(itemIds) {
        for (let index = 0; index < itemIds.length; index++) {
            let itemId = itemIds[index];
            let showBusinessCardBtn = $("span[data-img-refer-id='" + itemId + "']");
            showBusinessCardBtn.unbind("click").bind("click", function (event) {
                let bindId = $(this).attr("data-img-refer-id");
                let cardShow = $(this).attr("data-show");
                let mainContactUrl = Base64.decode($(this).attr("data-img-path"));
                if (util.isTrue(cardShow)) {
                    // 删除
                    $(this).text("点击展示名片");
                    $(this).attr("data-show", false);
                } else {
                    $(this).text("点击隐藏名片");
                    $(this).attr("data-show", true);
                    $("img[data-img-id='" + bindId + "']").attr("src", "/mobile/viewBusinessCard?filePath=" + (mainContactUrl.replace(/\\/g, "/")));
                }
                // 展示 或者显示
                $("div[data-refer='" + bindId + "']").toggleClass("business-hide");
            });
        }
    }

    /**
     * 展示联系日志信息
     * @param contactLogs
     */
    function showContactLogs(contactLogs) {
        if (util.arrayNotNull(contactLogs)) {
            let contactLogItems = [];
            $(contactLogs).each(function (index, contactLog) {
                let contactLogDoms = [];
                for (let contactLogProp in contactLogPropName) {
                    let contactLogItemPropName = contactLogPropName[contactLogProp];
                    let contactLogPropValue = contactLog[contactLogProp];
                    if (util.isNotNull(contactLogPropValue)) {
                        contactLogDoms.push(
                            "<div class='content-line'>" +
                            "    <span class='content-line-title'>" + contactLogItemPropName + "：</span>" +
                            "    <span class='content-line-value' >" + contactLogPropValue + "</span>" +
                            "</div>");
                    }
                }
                if (contactLogDoms.length > 0) {
                    contactLogItems.push("<div class='contact-log-item'>" + contactLogDoms.join("") + "</div>")
                }
            });
            if (contactLogItems.length > 0) {
                $("#contact-log-area").html(contactLogItems.join(""));
            }
        }
    }

    /**
     * 展示银行信息
     * @param bankAccounts 银行信息
     */
    function showBankInfos(bankAccounts) {
        if (util.arrayNotNull(bankAccounts)) {
            let bankAccountItems = [];
            $(bankAccounts).each(function (index, bankAccount) {
                let bankAccountDoms = [];
                for (let bankProp in bankAccountProp) {
                    let bankPropName = bankAccountProp[bankProp];
                    let bankPropValue = bankAccount[bankProp];
                    if (util.isNotNull(bankPropValue)) {
                        bankAccountDoms.push(
                            "<div class='content-line'>" +
                            "    <span class='content-line-title'>" + bankPropName + "：</span>" +
                            "    <span class='content-line-value' >" + bankPropValue + "</span>" +
                            "</div>");
                    }
                }
                if (bankAccountDoms.length > 0) {
                    bankAccountItems.push("<div class='bank-account-item'>" + bankAccountDoms.join("") + "</div>")
                }
            });
            if (bankAccountItems.length > 0) {
                $("#bank-info-area").html(bankAccountItems.join(""));
            }
        }
    }

    /**
     * 展示发票信息
     * @param invoices 发票信息
     */
    function showInvoiceInfos(invoices) {
        if (util.arrayNotNull(invoices)) {
            let invoiceItems = [];
            $(invoices).each(function (index, invoice) {
                let invoiceDoms = [];
                for (let invoicePropKey in invoiceProp) {
                    let invoicePropName = invoiceProp[invoicePropKey];
                    let invoicePropValue = invoice[invoicePropKey];
                    if (util.isNotNull(invoicePropValue)) {
                        invoiceDoms.push(
                            "<div class='content-line'>" +
                            "    <span class='content-line-title'>" + invoicePropName + "：</span>" +
                            "    <span class='content-line-value' >" + invoicePropValue + "</span>" +
                            "</div>");
                    }
                }
                if (invoiceDoms.length > 0) {
                    invoiceItems.push("<div class='invoice-item'>" + invoiceDoms.join("") + "</div>")
                }
            });
            if (invoiceItems.length > 0) {
                $("#invoice-area").html(invoiceItems.join(""));
            }
        }
    }
});