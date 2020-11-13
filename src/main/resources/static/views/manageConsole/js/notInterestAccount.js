layui.use(['form', 'layedit', 'laydate'], function () {
    var form = layui.form;
    var layer = layui.layer;
    var layedit = layui.layedit;
    var laydate = layui.laydate;
    loadAccountList();
    btnAdd();
    btnDelete();
    btnImportAdd();
    btnImportDelete();
    btnOption();

    /**
     * 加载数据
     */
    function loadAccountList() {
        var loadIndex = layer.load(2);
        $.ajax({
            type: "POST",
            async: false,
            url: "/notInterestAccount/readAll?temp=" + Math.random(),
            dataType: 'json',
            data: {},
            success: function (result) {
                if (result.code === 200 || result.code === '200') {
                    var accountList = result.data;
                    var accountListEle = $("#account-list");
                    accountListEle.html("");
                    var items  = [];
                    if (isNotBlank(accountList) && accountList.length > 0) {
                        for (var index = 0; index < accountList.length; index++) {
                            var accountInfo = accountList[index];
                            if (isNotBlank(accountInfo) && accountInfo.length > 0) {
                                items.push(
                                    "<div class='account-info-item'>" +
                                    "    <input type='checkbox' name='account' lay-filter='account' value='" + accountInfo[0] + "' title='" + accountInfo[0] + "' lay-skin='primary'/>" +
                                    "</div>");
                            }
                        }
                        accountListEle.html(items.join(""));
                    } else {
                        accountListEle.html("<div class='account-empty'>暂无数据</div>");
                    }
                    form.render();
                } else {
                    layer.msg(result.msg);
                }
                layer.close(loadIndex);
            },
            error: function (data) {
                layer.msg("数据加载异常")
                layer.close(loadIndex);
            }
        });
    }

    /**
     * 添加
     */
    function btnAdd() {
        $("#add").click(function () {
            layer.open({
                title: "添加账号",
                content: "账号：<input type='text' id='account-add' class='layui-input account-input'/>"
                , btn: ['确认', '取消']
                , yes: function (index, layero) {
                    var account = $("#account-add").val();
                    if (isNotBlank(account)) {
                        $.ajax({
                            type: "POST",
                            async: false,
                            url: "/notInterestAccount/add?temp=" + Math.random(),
                            dataType: 'json',
                            data: {accounts: [account].join(",")},
                            success: function (result) {
                                layer.msg(result.msg);
                                loadAccountList();
                            }
                        });
                    } else {
                        layer.tips("添加账号不能为空", "#account-add", {tips: 1});
                    }
                }
                , btn2: function (index, layero) {

                }
            });
        });
    }

    /**
     * 删除
     */
    function btnDelete() {
        $("#delete").click(function () {
            // 获取所有的选中的值
            var accounts = [];
            var accountCheckedEle = $("input[name='account']:checked");
            for (var index = 0; index < accountCheckedEle.length; index++) {
                accounts.push($(accountCheckedEle[index]).val());
            }
            if (isBlank(accounts)) {
                layer.msg("至少选一个需要删除的账号");
                return;
            }
            var confirmIndex = layer.confirm('您确认要删除选中账号（' + accounts.join("、") + '）？', {
                btn: ['确认', '取消']
            }, function () {
                layer.close(confirmIndex);
                $.ajax({
                    type: "POST",
                    async: false,
                    url: "/notInterestAccount/delete?temp=" + Math.random(),
                    dataType: 'json',
                    data: {accounts: accounts.join(",")},
                    success: function (result) {
                        layer.msg(result.msg);
                        loadAccountList();
                    },
                    error: function () {
                        layer.msg("删除账号异常");
                    }
                });
            });
        });
    }



    /**
     * 导入添加
     */
    function btnImportAdd() {
        $("#import-add").click(function () {
            var fileInfos = [];
            layer.open({
                title: "添加账号",
                content: "上传支持文件类型：txt,xls,xlsx<br>一行只能写一个账号<br>" +
                    "<button type='button' id='import-add-file' class='layui-btn layui-btn-primary my-file-btn'>选择上传文件</button>" +
                    "<div id='upload-file-list'></div>"
                , btn: ['确认', '取消']
                , success: function () {
                    layui.use('upload', function () {
                        var upload = layui.upload;
                        //执行实例
                        var uploadInst = upload.render({
                            elem: '#import-add-file' //绑定元素
                            , url: '/operate/upLoadFile' //上传接口
                            ,accept:"file"
                            ,field:"files"
                            ,exts:"xls|xlsx|txt"
                            , done: function (res) {
                                fileInfos = [];
                                console.log(res);
                                //上传完毕回调
                                var fileList = res.data;
                                if (isNotBlank(fileList) && fileList.length>0){
                                    for (var fIndex=0;fIndex<fileList.length;fIndex++){
                                        var fileInfo = fileList[fIndex];
                                        fileInfos.push(fileInfo);
                                        $("#upload-file-list").html("<div>" + fileInfo.fileName + "</div>");
                                    }
                                }
                            },error:function () {
                                layer.msg("上传错误");
                            }
                        });
                    });
                }
                , yes: function (index, layero) {
                    $.ajax({
                        type: "POST",
                        async: false,
                        url: "/notInterestAccount/importAdd?temp=" + Math.random(),
                        dataType: 'json',
                        data: {
                            files: JSON.stringify(fileInfos)
                        },
                        success: function (result) {
                            layer.msg(result.msg);
                            loadAccountList();
                        },
                        error: function () {
                            layer.msg("删除账号异常");
                        }
                    });
                }
            });
        });
    }


    /**
     * 导入删除 暂时先不实现
     */
    function btnImportDelete() {
        $("#import-delete").click(function () {
            var fileInfos = [];
            layer.open({
                title: "删除账号",
                content: "上传支持文件类型：txt,xls,xlsx<br>一行只能写一个账号<br>" +
                    "<button type='button' id='import-add-file' class='layui-btn layui-btn-primary my-file-btn'>选择上传文件</button>" +
                    "<div id='upload-file-list'></div>"
                , btn: ['确认', '取消']
                , success: function () {
                    layui.use('upload', function () {
                        var upload = layui.upload;
                        //执行实例
                        var uploadInst = upload.render({
                            elem: '#import-add-file' //绑定元素
                            , url: '/operate/upLoadFile' //上传接口
                            ,accept:"file"
                            ,field:"files"
                            ,exts:"xls|xlsx|txt"
                            , done: function (res) {
                                fileInfos = [];
                                console.log(res);
                                //上传完毕回调
                                var fileList = res.data;
                                if (isNotBlank(fileList) && fileList.length>0){
                                    for (var fIndex=0;fIndex<fileList.length;fIndex++){
                                        var fileInfo = fileList[fIndex];
                                        fileInfos.push(fileInfo);
                                        $("#upload-file-list").html("<div>" + fileInfo.fileName + "</div>");
                                    }
                                }
                            },error:function () {
                                layer.msg("上传错误");
                            }
                        });
                    });
                }
                , yes: function (index, layero) {
                    $.ajax({
                        type: "POST",
                        async: false,
                        url: "/notInterestAccount/importDelete?temp=" + Math.random(),
                        dataType: 'json',
                        data: {
                            files: JSON.stringify(fileInfos)
                        },
                        success: function (result) {
                            layer.msg(result.msg);
                            loadAccountList();
                        },
                        error: function () {
                            layer.msg("删除账号异常");
                        }
                    });
                }
            });
        });
    }

    /**
     * 功能按钮
     */
    function btnOption() {
        $("#all").click(function () {
            $("input[name='account']").each(function (index, item) {
                item.checked = true;
            })
            form.render('checkbox');
        })
        $("#reverse").click(function () {
            $("input[name='account']").each(function (index, item) {
                item.checked = !item.checked;
            })
            form.render('checkbox');
        })
    }
});