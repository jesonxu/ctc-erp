layui.use(['layer', 'element', "form", "upload", "laydate"], function () {
    let layer = layui.layer;
    let form = layui.form;
    let upload = layui.upload;

    // 初始化名片扫描按钮
    initScanCompanyCard();
    // 初始化客户选择
    initSupplierChoose();
    // 提交按钮
    bindSubmitBtn();

    /**
     * 初始化名片扫描按钮
     */
    function initScanCompanyCard() {
        let loadingIndex = null;
        upload.render({
            elem: '#scanBusinessCard',
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
                    $("#business-card-path").val(cardInfo.filePath);
                    let name = util.jsonArrayToStr(cardInfo.name);
                    $("input[name='contactsName']").val(name);
                    // 职位
                    let title = util.jsonArrayToStr(cardInfo.title);
                    $("input[name='post']").val(title);
                    // 号码
                    let mobile = util.jsonArrayToStr(cardInfo.mobile);
                    $("input[name='firstPhone']").val(mobile);
                    // 座机
                    let tel = util.jsonArrayToStr(cardInfo.tel);
                    $("input[name='telephone']").val(tel);
                    // 邮箱
                    let email = util.jsonArrayToStr(cardInfo.email);
                    $("input[name='email']").val(email);
                    // 其余的全部写到备注里面
                    let remarkInfo = [];
                    // 公司
                    let company = util.jsonArrayToStr(cardInfo.company);
                    if (util.isNotNull(company)) {
                        remarkInfo.push("公司名：" + company);
                    }
                    // 公司地址
                    let address = util.jsonArrayToStr(cardInfo.address);
                    if (util.isNotNull(address)) {
                        remarkInfo.push("公司地址：" + address);
                    }
                    // 网址
                    let url = util.jsonArrayToStr(cardInfo.url);
                    if (util.isNotNull(url)) {
                        remarkInfo.push("公司网址：" + url);
                    }
                    // 传真
                    let fax = util.jsonArrayToStr(cardInfo.fax);
                    if (util.isNotNull(fax)) {
                        remarkInfo.push("传真：" + fax);
                    }
                    // 邮编
                    let pc = util.jsonArrayToStr(cardInfo.pc);
                    if (util.isNotNull(pc)) {
                        remarkInfo.push("邮编：" + pc);
                    }
                    if (remarkInfo.length > 0) {
                        $("textarea[name='remark']").val(remarkInfo.join(","));
                    }
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
     * 初始化 客户/供应商 关联信息（选择框）
     */
    function initSupplierChoose() {
        // 设置对应名称
        $("#supplier-choose").click(function () {
            let entityId = $("#entity-id").val();
            let index = layer.open({
                type: 2,
                area: ['100%', '100%'],
                title: "选择客户",
                content: "/mobile/toEntityChoose?associateType=1&entityId=" + entityId + "&noPublic=1"
            });
            layer.full(index);
        });
    }

    /**
     * 绑定提交按钮
     */
    function bindSubmitBtn() {
        $("#submit").click(function (e) {
            var formData = getFormData(true);
            if (formData === null) {
                return;
            }
            $.ajax({
                type: "POST",
                async: false,
                url: "/contact/addSupplierContact.action?temp=" + Math.random(),
                data: formData,
                success: function (data) {
                    if (data.code === 200) {
                        layer.msg("保存成功");
                        setTimeout(function () {
                            window.history.back();
                        }, 1000)
                    } else {
                        return layer.msg(data.msg);
                    }
                }
            });
        });

    }

    /**
     * 获取添加数据
     * @returns {null|[]}
     */
    function getFormData() {
        let json = {
            contactId: $('input[name="supplierContactsId"]').val(),
            entityId: $('#entity-id').val(),
            deptName: $('input[name="deptName"]').val(),
            contactsName: $('input[name="contactsName"]').val(),
            post: $('input[name="post"]').val(),
            firstPhone: $('input[name="firstPhone"]').val(),
            secondPhone: $('input[name="secondPhone"]').val(),
            telephone: $('input[name="telephone"]').val(),
            email: $('input[name="email"]').val(),
            wx: $('input[name="wx"]').val(),
            qq: $('input[name="qq"]').val(),
            remark: $('textarea[name="remark"]').val(),
            businessCardPath: $("#business-card-path").val()
        };
        if (util.isNull(json.entityId)) {
            layer.msg("客户必须选择");
            return null;
        }
        if (util.isNull(json.deptName)) {
            layer.msg("请填写部门名称");
            return null;
        }
        if (util.isNull(json.contactsName)) {
            layer.msg("请填写姓名");
            return null;
        }
        if (util.isNull(json.post)) {
            layer.msg("请填写职位");
            return null;
        }
        if (util.isNotNull(json.email) && !/^[0-9a-z][_.0-9a-z-]{0,31}@([0-9a-z][0-9a-z-]{0,30}[0-9a-z]\.){1,4}[a-z]{2,4}$/.test(json.email)) {
            layer.msg("邮箱格式不正确");
            return null;
        }
        if (util.isNotNull(json.firstPhone) && !/^1\d{10}$/.test(json.firstPhone)) {
            layer.msg("号码格式不正确");
            return null;
        }
        if (util.isNotNull(json.secondPhone) && !/^1\d{10}$/.test(json.secondPhone)) {
            layer.msg("号码格式不正确");
            return null;
        }
        return json;
    }


    form.render();
});

// 不用 但是要有
function clearSelectedProductInfo() {

}