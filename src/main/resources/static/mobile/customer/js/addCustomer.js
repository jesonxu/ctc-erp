layui.use(['layer', 'element', "form", "upload", "laydate"], function () {
    let layer = layui.layer;
    let form = layui.form;
    let upload = layui.upload;
    let laydate = layui.laydate;
    //取当天日期时间
    let dataStart = util.dateFormatter(new Date());
    // 卡片选择按钮
    initScanCompanyCard();
    // 时间选择
    bindTimeSelect();
    // 下拉框
    loadSelect();
    // 绑定检查按钮
    bindCheckBtn();
    // 绑定增加发票信息
    bindAddInvoiceBtn();
    // 绑定增加银行信息
    bindAddBankBtn();
    // 绑定提交按钮
    bindSubmitBtn();

    /**
     *扫描按钮
     */
    function initScanCompanyCard() {
        let loadingIndex = null;
        upload.render({
            elem: '#scanCompanyCard',
            url: '/mobile/scanBusinessCard',
            field: 'file',
            accept: "images",
            acceptMime: "image/jpg,image/png,image/jpeg",
            exts: "jpg|jpeg|png",
            before: function (obj) {
                // 显示加载请求中
                loadingIndex = layer.load(2);
            },
            done: function (res) {
                layer.close(loadingIndex);
                if (res.code === 200 || res.code === "200") {
                    let cardInfo = res.data;
                    let address = util.jsonArrayToStr(cardInfo.address);
                    $("#registrationAddress").val(address);
                    let mobile = util.jsonArrayToStr(cardInfo.mobile);
                    $("#contactPhone").val(mobile);
                    let name = util.jsonArrayToStr(cardInfo.name);
                    $("#contactName").val(name);
                    // 网址
                    let url = util.jsonArrayToStr(cardInfo.url);
                    $("#website").val(url);
                    // 座机
                    let tel = util.jsonArrayToStr(cardInfo.tel);
                    $("#contactTelephone").val(tel);
                    let company = util.jsonArrayToStr(cardInfo.company);
                    $("#companyName").val(company);
                    // 职位
                    let title = util.jsonArrayToStr(cardInfo.title);
                    $("#contactPosition").val(title);
                    let email = util.jsonArrayToStr(cardInfo.email);
                    $("#email").val(email);
                    $("#businessCardPath").val(cardInfo.filePath);
                } else {
                    layer.msg(res.msg);
                }
            }
            , error: function () {
                layer.close(loadingIndex);
                layer.msg("名片识别错误");
            }
        });
    }

    /**
     * 绑定时间选择
     */
    function bindTimeSelect() {
        // 公司创立日期
        laydate.render({
            elem: '#creationDate',
            trigger: 'click',
            max: dataStart
        });

        // 使用日期
        laydate.render({
            elem: '#useDate',
            trigger: 'click',
            max: dataStart
        });
    }

    /**
     * 加载选择按钮的值
     */
    function loadSelect() {
        // 加载客户类别下拉框
        $.ajax({
            type: "POST",
            url: "/customer/getCustomerTypeSelect.action",
            dataType: "JSON",
            success: function (data) {
                if (data.code === 200 || data.code === '200') {
                    let customerTypes = data.data;
                    let typeSelects = [];
                    for (let index = 0; index < customerTypes.length; index++) {
                        let customerType = customerTypes[index];
                        typeSelects.push("<option value='" + customerType.customerTypeId +
                            "' data-type-value='" + customerType.customerTypeValue +
                            "'>" + customerType.customerTypeName + "</option>");
                    }
                    $("#customerType").html(typeSelects.join(""));
                    form.render('select');
                }
            }
        });

        // 加载客户区域下拉框
        $.ajax({
            type: "POST",
            url: '/customer/getRegion.action?temp=' + Math.random(),
            dataType: 'json',
            success: function (data) {
                let areaSelects = [];
                for (let i = 0; i < data.length; i++) {
                    areaSelects.push('<option value="' + data[i].value + '">' + data[i].name + '</option>');
                }
                $("#customerRegion").html(areaSelects.join(""));
                form.render('select');
            }
        });

        // 加载我放银行信息
        $.ajax({
            type: "POST",
            url: '/bankInfo/queryBankInfoByType?temp=' + Math.random(),
            dataType: 'json',
            data: {type: 2},
            success: function (data) {
                if (data.code === 200 || data.code === "200") {
                    let bankSelects = [];
                    let bankInfos = data.data;
                    for (let index = 0; index < bankInfos.length; index++) {
                        let bankInfo = bankInfos[index];
                        let text = bankInfo.accountName + "：" + bankInfo.accountBank;
                        bankSelects.push('<option value="' + bankInfo.id + '">' + text + '</option>');
                    }
                    $("#bankAccountId").html(bankSelects.join(""));
                    form.render('select');
                } else {
                    layer.msg(data.msg);
                }
            }
        });
    }

    /**
     * 点击校验按钮事件
     */
    function bindCheckBtn() {
        $("#check-customer").click(function (e) {
            let companyName = $("#companyName").val();
            if (util.isNull(companyName)) {
                layer.msg("请填写公司名称");
                return "";
            }
            if (companyName.length < 2) {
                layer.msg("检测公司名长度不得小于2");
                return "";
            }
            $.ajax({
                type: "POST",
                async: false,
                url: "/customer/queryCustomerByName.action?temp=" + Math.random(),
                dataType: 'json',
                data: {
                    companyName: companyName,
                    pageSize: "10",
                    currentPage: "1"
                },
                success: function (data) {
                    if (data.code === 200) {
                        if (util.isNotNull(data.data)) {
                            let customers = data.data.data;
                            if (util.isNotNull(customers) && customers.length > 0) {
                                window.location.href = '/mobile/matchCustomer?companyName=' + Base64.encode(companyName) + "";
                                return;
                            }
                        }
                        layer.msg("没有匹配客户");
                    } else {
                        // 失败的情况
                        layer.msg(data.msg);
                    }
                }
            });
        });
    }

    /**
     * 绑定增加开票信息按钮事件
     */
    function bindAddInvoiceBtn() {
        $("#addInvoiceItem").unbind("click").bind("click", function () {
            if (!verifyInvoiceItems()) {
                return;
            }
            let invoiceItemDom = $("#invoice-item-template").html();
            let temEle = $("<div></div>");
            temEle.html(invoiceItemDom);
            let invoiceCount = $("div[data-invoice-item-id]").length;
            temEle.find("span[data-invoice-title='text']").text("开票信息" + (invoiceCount + 1));
            let invoiceItemId = util.uuid();
            temEle.children().attr("data-invoice-item-id", invoiceItemId);
            $(this).parent().before(temEle.html());
            // 绑定删除事件
            $("div[data-invoice-item-id='" + invoiceItemId + "']").find("span[data-operation='delete-item']").unbind("click").bind("click", function (event) {
                $(this).parent().parent().remove();
            });
        });
    }

    /**
     * 添加的时候校验数据
     * @returns {boolean}
     */
    function verifyInvoiceItems() {
        let invoiceItems = $("div[data-invoice-item-id]");
        if (invoiceItems.length > 0) {
            for (let itemIndex = 0; itemIndex < invoiceItems.length; itemIndex++) {
                let invoiceItemEle = invoiceItems[itemIndex];
                let companyName = $(invoiceItemEle).find("input[name='companyname']").val();
                let taxNumber = $(invoiceItemEle).find("input[name='taxnumber']").val();
                let companyAddress = $(invoiceItemEle).find("input[name='companyaddress']").val();
                let phone = $(invoiceItemEle).find("input[name='phone']").val();
                let accountBank = $(invoiceItemEle).find("input[name='accountbank']").val();
                let bankAccount = $(invoiceItemEle).find("input[name='bankaccount']").val();
                let itemNotEmpty = util.isAllNotNull([companyName, companyAddress, taxNumber, phone, accountBank, bankAccount]);
                if (!itemNotEmpty) {
                    layer.msg("请先填写完整前面已有项");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 绑定添加银行信息
     */
    function bindAddBankBtn() {
        $("#addBankItem").unbind("click").bind("click", function () {
            if (!verifyBankItems()) {
                return;
            }
            let bankItemDom = $("#bank-item-template").html();
            let bankTemEle = $("<div></div>");
            bankTemEle.html(bankItemDom);
            let bankItemCount = $("div[data-bank-item-id]").length;
            bankTemEle.find("span[data-bank-title='text']").text("银行信息" + (bankItemCount + 1));
            let bankItemId = util.uuid();
            bankTemEle.children().attr("data-bank-item-id", bankItemId);
            $(this).parent().before(bankTemEle.html());
            // 绑定删除事件
            $("div[data-bank-item-id='" + bankItemId + "']").find("span[data-bank-operation='delete-item']").unbind("click").bind("click", function (event) {
                $(this).parent().parent().remove();
            });
        });
    }

    /**
     * 添加银行信息的时候校验数据
     * @returns {boolean}
     */
    function verifyBankItems() {
        let bankItems = $("div[data-bank-item-id]");
        if (bankItems.length > 0) {
            for (let index = 0; index < bankItems.length; index++) {
                let bankItemEle = bankItems[index];
                let accountName = $(bankItemEle).find("input[name='accountname']").val();
                let accountBank = $(bankItemEle).find("input[name='accountbank']").val();
                let bankAccount = $(bankItemEle).find("input[name='bankaccount']").val();
                let itemNotEmpty = util.isAllNotNull([accountName, accountBank, bankAccount]);
                if (!itemNotEmpty) {
                    layer.msg("请先填写完整前面已有项");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 提交按钮
     */
    function bindSubmitBtn() {
        $("#submit").unbind("click").bind("click", function (event) {
            let formData = {
                companyName: $("#companyName").val(),
                legalPerson: $("#legalPerson").val(),
                registrationNumber: $("#registrationNumber").val(),
                registrationAddress: $("#registrationAddress").val(),
                postalAddress: $("#postalAddress").val(),
                telePhoneNumber: $("#telePhoneNumber").val(),
                email: $("#email").val(),
                website: $("#website").val(),
                contactName: $("#contactName").val(),
                contactDept: $('#contactDept').val(),
                contactPosition: $('#contactPosition').val(),
                contactTelephone: $('#contactTelephone').val(),
                contactPhone: $("#contactPhone").val(),
                creationDate: $("#creationDate").val(),
                useDate: $("#useDate").val(),
                registeredCapital: $("#registeredCapital").val(),
                corporateNature: $("#corporateNature").val(),
                taxation: $("#taxation").val(),
                customerTypeId: $("#customerType").val(),
                customerRegion: $("#customerRegion").val(),
                companyIntroduction: $("#companyIntroduction").val(),
                businessMode: $("#businessMode").val(),
                bankAccountId: $("#bankAccountId").val(),
                businessCardPath:$("#businessCardPath").val()
            };
            if (!validation(formData)) {
                return;
            }
            // 开票信息
            let invoice = getInvoiceInfos(false);
            formData.invoiceInfos = JSON.stringify(invoice);

            // 银行信息
            let bankInfos = getBankInfos(false);
            formData.bankInfos = JSON.stringify(bankInfos);

            $.ajax({
                type: "POST",
                async: false,
                url: "/customer/addCustomerInfo.action?temp=" + Math.random(),
                dataType: 'json',
                data: formData,
                success: function (data) {
                    if (data.code === 200) {
                        layer.msg("添加成功");
                        window.history.back();
                    } else {
                       layer.msg(data.msg);
                    }
                }
            });
        });
    }

    /**
     * 获取发票信息
     * @returns {*}
     */
    function getInvoiceInfos() {
        let invoiceInfos = [];
        let invoiceItems = $("div[data-invoice-item-id]");
        if (invoiceItems.length > 0) {
            for (let itemIndex = 0; itemIndex < invoiceItems.length; itemIndex++) {
                let invoiceItemEle = invoiceItems[itemIndex];
                let companyName = $(invoiceItemEle).find("input[name='companyname']").val();
                let taxNumber = $(invoiceItemEle).find("input[name='taxnumber']").val();
                let companyAddress = $(invoiceItemEle).find("input[name='companyaddress']").val();
                let phone = $(invoiceItemEle).find("input[name='phone']").val();
                let accountBank = $(invoiceItemEle).find("input[name='accountbank']").val();
                let bankAccount = $(invoiceItemEle).find("input[name='bankaccount']").val();
                let itemNotEmpty = util.isAllNotNull([companyName, companyAddress, taxNumber, phone, accountBank, bankAccount]);
                if (!itemNotEmpty) {
                    invoiceInfos.push({
                        companyName: companyName,
                        taxNumber: taxNumber,
                        companyAddress: companyAddress,
                        phone: phone,
                        accountBank: accountBank,
                        bankAccount: bankAccount
                    });
                }
            }
        }
        return invoiceInfos;
    }

    /**
     * 获取银行信息
     * @returns {boolean|*}
     */
    function getBankInfos() {
        let bankItems = $("div[data-bank-item-id]");
        let bankInfos = [];
        if (bankItems.length > 0) {
            for (let index = 0; index < bankItems.length; index++) {
                let bankItemEle = bankItems[index];
                let accountName = $(bankItemEle).find("input[name='accountname']").val();
                let accountBank = $(bankItemEle).find("input[name='accountbank']").val();
                let bankAccount = $(bankItemEle).find("input[name='bankaccount']").val();
                // 必须全部都填写了才会记录
                let itemNotEmpty = util.isAllNotNull([accountName, accountBank, bankAccount]);
                if (!itemNotEmpty) {
                    bankInfos.push({
                        accountName: accountName,
                        accountBank: accountBank,
                        bankAccount: bankAccount
                    });
                }
            }
        }
        return bankInfos;
    }

    // 校验参数
    function validation(param) {
        if (util.isNull(param.companyName)) {
            layer.msg('公司名称不能为空');
            return false;
        }
        if (util.isNull(param.contactName)) {
            layer.msg('客户联系人不能为空');
            return false;
        }
        if (util.isNull(param.contactPhone)) {
            layer.msg('联系人手机不能为空');
            return false;
        }
        return true;
    }

    form.render();
});