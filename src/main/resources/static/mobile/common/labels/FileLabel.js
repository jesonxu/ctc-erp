/**
 * 文本标签 也担任 所有 不能识别的标签 基础标签展示 8
 * @author 8520
 */
(function (window, factory) {
    window.FileLabel = factory();
})(window, function () {
    /**
     * (渲染流程详情) 初始化对象
     * @param labelId 标签的ID
     * @param labelName 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let FileLabel = function (labelId, labelName, labelType, uploadUrl) {
        // 渲染的位置（对应元素下面 直接添加）
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error("【文件标签】名称为空");
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error("【文件标签】ID为空");
        }
        this.labelType = labelType;
        // 默认设置值为 空数组
        this.files = [];
        this.uploadUrl = uploadUrl;
    };

    /**
     * 转换为文本
     */
    FileLabel.prototype.toText = function (value) {
        value = util.formatBlank(value);
        // 会有可能是列表
        this.parseValue(value);
        let filesTextDom = [];
        if (this.files.length > 0) {
            for (let fileIndex = 0; fileIndex < this.files.length; fileIndex++) {
                let file = this.files[fileIndex];
                let fileStr = JSON.stringify(file);
                let fileTitle = [];
                let batchNum = file.batchNum;
                if (util.isNotNull(batchNum)) {
                    fileTitle.push(batchNum);
                }
                let time = file.time;
                if (util.isNotNull(time)) {
                    fileTitle.push("[" + time + "]")
                }
                fileTitle.push(file.fileName);
                // 预览暂时不实现
                let fileNameDom = "<br><span class='file-preview' onclick='fileTool.downLoadFile(" + fileStr + ")'>" + fileTitle.join("") + "</span>";
                filesTextDom.push(fileNameDom);
            }
        }
        return this.name + "：" + filesTextDom.join("");
    };

    /**
     * 渲染(有值 和没有值 区别回显)
     */
    FileLabel.prototype.render = function (flowEle, value, required) {
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error("【文件标签】对应的位置元素不存在");
        }
        this.required = util.isTrue(required);
        this.parseValue(value);
        let labelDom =
            "<div class='layui-form-item label-type-file' data-label-id='" + this.id + "'>" +
            "    <label class='flow-label-name' " + util.getRequired(this.required) + " >" + this.name + ":</label>" +
            "    <div class='flow-label-content'>" +
            "        <div class='layui-upload'>" +
            "          <button type='button' class='layui-btn layui-btn-xs layui-btn-primary' data-file-btn-id='" + this.id + "'>" +
            "              <i class='layui-icon layui-icon-templeate-1'></i>请选择上传文件" +
            "          </button> " +
            "          <div class='layui-upload-list'>" +
            "            <table class='layui-table file-label-head'>" +
            "              <thead>" +
            "                <tr>" +
            "                    <th width='10%'>批次</th>" +
            "                    <th width='60%'>文件名</th>" +
            "                    <th width='20%'>状态</th>" +
            "                    <th width='10%'>操作</th>" +
            "                </tr>" +
            "              </thead>" +
            "              <tbody data-file-body-id='" + this.id + "'> " + this.reshowFile() + "</tbody>" +
            "            </table>" +
            "          </div>" +
            "        </div>  " +
            "    </div>" +
            "</div>";
        $(this.flowEle).append(labelDom);
        this.bindEvent();
    };

    /**
     * 绑定事件
     **/
    FileLabel.prototype.bindEvent = function () {
        let label = this;
        // 首先 绑定 已经有的文件事件
        //多文件列表示例
        let demoListView = $("tbody[data-file-body-id='" + label.id + "']");
        let originFileBtns = demoListView.find("button[class*='demo-delete']");
        if (util.isNotNull(originFileBtns) && originFileBtns.length > 0) {
            for (let btnIndex = 0; btnIndex < originFileBtns.length; btnIndex++) {
                let btn = originFileBtns[btnIndex];
                $(btn).click(function (e) {
                    let deleteFile = $(this).attr("data-file");
                    label.deleteFile(JSON.parse(deleteFile));
                    // 删除
                    $(this).parent().parent().remove();
                });
            }
        }

        // 渲染 新的
        layui.use('upload', function () {
        	let url = this.uploadUrl;
        	if (!url) {
        		url = '/operate/upLoadFile';
        	}
            let upload = layui.upload;
            let uploadListIns = upload.render({
                elem: "button[data-file-btn-id='" + label.id + "']"
                , url: url
                , field: 'files'
                , accept: 'file'
                , multiple: false
                , auto: true
                , choose: function (obj) {
                    let files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                    //读取本地文件
                    obj.preview(function (index, file, result) {
                        let tr = $(['<tr id="upload-' + index + '">'
                            , '<td>' + label.getBatchNum() + '</td>'
                            , '<td>' + file.name + '</td>'
                            , '<td>等待上传</td>'
                            , '<td>'
                            , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                            , '<button class="layui-btn file-label-opts-btn demo-delete">删除</button>'
                            , '</td>'
                            , '</tr>'].join(''));
                        //单个重传
                        tr.find('.demo-reload').on('click', function () {
                            obj.upload(index, file);
                        });
                        //删除
                        tr.find('.demo-delete').on('click', function () {
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });
                        demoListView.append(tr);
                    });
                }
                , done: function (res, index, upload) {
                    console.log("上传完成" + JSON.stringify(res));
                    if (res.code === 200 || res.code === "200") { //上传成功
                        label.recordFile(res.data);
                        let tr = demoListView.find('tr#upload-' + index)
                            , tds = tr.children();
                        tds.eq(2).html('<span style="color: #5FB878;">' + util.formatBlank(res.data[0].time, "上传成功") + '</span>');
                        tds.eq(3).find('.demo-reload').remove();
                        tds.eq(3).find('.demo-delete').attr("data-file", JSON.stringify(res.data));
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                }
                , error: function (index, upload) {
                    let tr = demoListView.find('tr#upload-' + index)
                        , tds = tr.children();
                    tds.eq(2).html('<span style="color: #FF5722;">失败</span>');
                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        });
    };

    FileLabel.prototype.reshowFile = function () {
        let files = this.files;
        let fileListDom = [];
        if (util.arrayNotNull(files)) {
            for (let fileIndex = 0; fileIndex < files.length; fileIndex++) {
                let file = files[fileIndex];
                fileListDom.push(
                    "<tr>" +
                    "  <td>" + util.formatBlank(file.batchNum, "") + "</td>" +
                    "  <td>" + file.fileName + "</td>" +
                    "  <td><span style='color: #5FB878;'>" + util.formatBlank(file.time, "上传成功") + "</span></td>" +
                    "  <td><button class='layui-btn file-label-opts-btn demo-delete' data-file='" + JSON.stringify(file) + "'>删除</button></td>" +
                    "</tr>");
            }
        }
        return fileListDom.join("");
    };

    /**
     * 记录上传了的文件(内部使用)
     **/
    FileLabel.prototype.recordFile = function (fileInfos) {
        if (util.arrayNotNull(fileInfos)) {
            for (let fileIndex = 0; fileIndex < fileInfos.length; fileIndex++) {
                let file = fileInfos[fileIndex];
                file.batchNum = this.getBatchNum();
                this.files.push(file);
            }
        }
    };

    /**
     * 删除按钮 删除文件 (内部使用)
     **/
    FileLabel.prototype.deleteFile = function (fileInfo) {
        if (util.isNotNull(fileInfo) && util.isNotNull(fileInfo.filePath)) {
            // 通过文件的路径 进行删除（因为保存的文件 路径是唯一的，其他的不是唯一的）
            let fileArr = this.files;
            let newFileArr = [];
            for (let fileIndex = 0; fileIndex < fileArr.length; fileIndex++) {
                let file = fileArr[fileIndex];
                if (file.filePath !== fileInfo.filePath) {
                    newFileArr.push(file);
                }
            }
            // 重新赋值
            this.files = newFileArr;
        }
    };

    /**
     * 解析值（内部使用）
     * @param value
     */
    FileLabel.prototype.parseValue = function (value) {
        let batchNum = 0;
        if (util.isNotNull(value)) {
            let fileArr = value;
            if (typeof value ==="string" ){
                fileArr = JSON.parse(value);
            }
            for (let fileIndex = 0; fileIndex < fileArr.length; fileIndex++) {
                let file = fileArr[fileIndex];
                // 要记录 渲染的文件 必须文件的 路径不能为空
                if (util.isNotNull(file.filePath)) {
                    // 将 原来的值 解析当对象上面
                    this.files.push(file);
                }
                if (util.isNotNull(file.batchNum) && util.isInteger(file.batchNum) && (parseInt(file.batchNum) > batchNum)) {
                    batchNum = parseInt(file.batchNum);
                }
            }
        }
        this.batchNum = batchNum;
    };

    /**
     * 获取文件的批次号（内部使用）
     */
    FileLabel.prototype.getBatchNum = function () {
        return (this.batchNum > 0) ? this.batchNum : 1;
    };

    /**
     * 获取标签值（对外 必须实现）
     */
    FileLabel.prototype.getValue = function () {
        return this.files;
    };

    /**
     * 获取标签名称（对外 必须实现）
     */
    FileLabel.prototype.getName = function () {
        return this.name;
    };

    /**
     * 校验 (对外 必须要实现)
     */
    FileLabel.prototype.verify = function () {
        // 是必须标签
        if (this.required && util.arrayNull(this.getValue())) {
            let tipEle = $(this.flowEle).find("button[ data-file-btn-id='" + this.id + "']")[0];
            layer.msg(this.name + "不能为空");
            return false
        }
        return true;
    };
    return FileLabel;
});