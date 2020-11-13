/**
 * 文件下载工具
 */
(function (window, factory) {
    window.fileTool = factory();
})(window, function () {
    let fileTool = function () {
    };

    /**
     * 文件下载
     * @param fileInfo
     * @returns {string}
     */
    fileTool.prototype.downLoadFile = function (fileInfo) {
        if (util.isNull(fileInfo)) {
            return "";
        }
        if (typeof fileInfo === "string") {
            fileInfo = JSON.parse(fileInfo);
        }
        if (util.isNotNull(fileInfo)) {
            let fileParams = "filePath=" + encodeURIComponent(fileInfo.filePath) + "&fileName=" + encodeURIComponent(fileInfo.fileName) + "&r=" + Math.random();
            window.location.href = "/operate/downloadFile?" + fileParams;
        }
    };

    return new fileTool();
});