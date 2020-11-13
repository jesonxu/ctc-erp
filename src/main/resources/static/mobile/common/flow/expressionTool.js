/**
 * 表达式工具
 */
(function (window, factory) {
    window.expressionTool = factory();
})(window, function () {
    let expressionTool = function () {
        this.executeables = [];
    };

    /**
     * push
     */
    expressionTool.prototype.push = function (executeable) {
        if (util.isNull(this.executeables)) {
            this.executeables = [];
        }
        this.executeables.push(executeable);
    };

    /**
     * execAll 执行所有
     */
    expressionTool.prototype.execAll = function (flowEle, labelList) {
        if (util.isNull(this.executeables)) {
            this.executeables = [];
        }
        let execs = this.executeables;
        if (execs.length > 0) {
            for (var i = 0; i < execs.length; i++) {
                if (typeof execs[i] == 'function') {
                    execs[i](flowEle, labelList);
                }
            }
            this.executeables = [];
        }
    };

    /**
     * 按正则分割表达式的变量
     * @param str       表达式
     * @param reg       正则
     * @returns {[]}    变量
     */
    expressionTool.prototype.getExecStrs = function (str, reg) {
        let list = [];
        let result = null;
        do {
            result = reg.exec(str);
            result && list.push(result[1]);
        } while (result)
        return list;
    }

    return new expressionTool();
});