//注意：parent 是 JS 自带的全局对象，可用于操作父页面
var index = window.parent.layer.getFrameIndex(window.name);
var uploadFiles = [];
var files = [];
layui.use('upload', function () {
    var fileNames = [];
    var upload = layui.upload;
    //多文件列表示例
    var demoListView = $('#demoList')
        , uploadListIns = upload.render({
        elem: '#testList'
        , url: '/fsExpenseIncome/upLoadFile.action'
        , accept: 'file'
        , exts: 'xlsx|xls'
        , multiple: true
        , auto: false
        , choose: function (obj) {
            files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                if (fileNames.indexOf(file.name) >=0){
                    layer.msg("选择文件重复", {time: 2000, icon:2});
                    return "";
                }
                fileNames.push(file.name);
                var tr = $(['<tr id="upload-' + index + '">'
                    , '<td>' + file.name + '</td>'
                    , '<td>' + (file.size / 1014).toFixed(1) + 'kb</td>'
                    , '<td>等待上传</td>'
                    , '<td>'
                    , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                    , '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                    , '</td>'
                    , '</tr>'].join(''));
                obj.upload(index, file);
                //单个重传
                tr.find('.demo-reload').on('click', function () {
                    obj.upload(index, file);
                });
                //删除
                tr.find('.demo-delete').on('click', function () {
                    console.log("删除文件：" + JSON.stringify(files[index]));
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    fileNames.splice(fileNames.indexOf(file.name),1);
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });
                demoListView.append(tr);
            });
        }
        , done: function (res, index, upload) {
            if (res.code === 200 || res.code === "200") { //上传成功
                var tr = demoListView.find('tr#upload-' + index)
                    , tds = tr.children();
                tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                tds.eq(3).html(''); //清空操作
                for (var fileIndex = 0;fileIndex<res.data.length;fileIndex++){
                    uploadFiles.push(res.data[fileIndex]);
                }
                window.parent.window.import_file_md5_info = uploadFiles;
                return delete this.files[index]; //删除文件队列已经上传成功的文件
            }
            layer.msg(res.msg, {time: 2000, icon:2});
            this.error(index, upload);
        }
        , error: function (index, upload) {
            var tr = demoListView.find('tr#upload-' + index)
                , tds = tr.children();
            tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
    });
});

/**
 * 删除上传文件信息
 */
function deleteUploadFileInfo(md5s,delAfter){
    $.ajax({
        type: "POST",
        url: "/fsExpenseIncome/delUploadFileInfo.action",
        dataType: "json",
        data: {
            md5s: md5s.join(",")
        },
        success: function (data) {
            if (data.code === '200' || data.code === 200) {
               if (delAfter != null){
                   delAfter(md5s,data);
               }
            }
        }
    });
}


// 确定
$("#ok_btn").click(function (e) {
    $.ajax({
        type: "POST",
        url: "/fsExpenseIncome/uploadExpenseIncome.action",
        dataType: "json",
        data: {
            fileInfos: JSON.stringify(uploadFiles)
        },
        success: function (data) {
            if (data.code === '200' || data.code === 200) {
                var year = $("#year").val();
                parent.layer.msg("账单关联成功");
                parent.layer.close(index);
                parent.refresh(year);
            } else {
                layer.msg(data.msg, {time: 2000, icon: 2});
            }
        }
    });
});

// 取消
$("#cancel_btn").click(function (e) {
    if (uploadFiles != null && uploadFiles.length > 0) {
        var md5arr = [];
        for (var fileIndex = 0; fileIndex < uploadFiles.length; fileIndex++) {
            md5arr.push(uploadFiles[fileIndex].md5);
        }
        deleteUploadFileInfo(md5arr);
    }
    parent.layer.close(index);
});

function downloadTemplate() {
    window.location.href = "/views/fsExpenseIncome/js/income201912.xlsx";
}