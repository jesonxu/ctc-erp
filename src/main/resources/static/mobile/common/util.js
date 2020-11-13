(function (window, factory) {
    window.util = factory();
})(window, function () {

    String.prototype.endWith = function (str) {
        let reg = new RegExp(str + "$");
        return reg.test(this);
    };

    /**
     *  初始化对象
     */
    let util = function () {

    };

    /**
     *判断是否为空
     */
    util.prototype.isNull = function (str) {
        return (str === "" || str === null || str === undefined || str === "null" || str === "undefined");
    };

    /**
     *判断是否全部为空
     */
    util.prototype.isAllNull = function (strs) {
        if (this.isNull(strs)) {
            return true;
        }
        for (let index = 0; index < strs.length; index++) {
            if (this.isNotNull(strs[index])) {
                return false;
            }
        }
        return true;
    };
    /**
     *判断是否全部不为空
     */
    util.prototype.isAllNotNull = function (strs) {
        if (this.isNull(strs)) {
            return false;
        }
        for (let index = 0; index < strs.length; index++) {
            if (this.isNull(strs[index])) {
                return false;
            }
        }
        return true;
    };
    /**
     * 判断不为空
     * @param str
     * @returns {boolean}
     */
    util.prototype.isNotNull = function (str) {
        return !this.isNull(str);
    };

    /**
     * 判断不为空
     * @param arr
     * @returns {boolean}
     */
    util.prototype.arrayNotNull = function (arr) {
        return !this.arrayNull(arr);
    };

    /**
     * 判断为空
     * @param arr
     * @returns {boolean}
     */
    util.prototype.arrayNull = function (arr) {
        return this.isNull(arr) || arr.length === 0;
    };

    /**
     * 是否为 真（仅限 boolean 和0,1以及 相关字符串）
     * @param required
     * @returns {boolean}
     */
    util.prototype.isTrue = function (required) {
        if (this.isNull(required)) {
            return false;
        }
        if (required === 1 || required === "1") {
            return true;
        }
        if (required === 0 || required === "0") {
            return false;
        }
        if (required === "true") {
            return true;
        }
        if (required === "false") {
            return false;
        }
        return required;
    };

    /**
     * 是否为整数（含负数）
     * @param num
     * @returns {*}
     */
    util.prototype.isInteger = function (num) {
        let regExp = new RegExp("^(-)?\\d{1,}$");
        return regExp.test(num);
    };

    /**
     * 是否为数字（含负数）
     * @param num
     * @returns {*}
     */
    util.prototype.isNumber = function (num) {
        return /^(-?\d+)(\.\d+)?$/.test(num);
    };

    /**
     * 是否为正数
     * @param num
     * @returns {*}
     */
    util.prototype.isMoney = function (num) {
        return /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/.test(num);
    };

    /**
     * 格式化空字符串
     * @returns {*}
     * @param str
     * @param replace
     */
    util.prototype.formatBlank = function (str, replace) {
        if (this.isNull(str)) {
            if (this.isNotNull(replace)) {
                return replace;
            } else {
                return "";
            }
        }
        return str;
    };

    /**
     * 产生UUID
     * @returns {string}
     */
    util.prototype.uuid = function () {
        return 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            let r = Math.random() * 16 | 0;
            let v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    /**
     * 处理千分位
     * @param num
     * @returns {string|number}
     */
    util.prototype.thousand = function (num) {
        if (this.isNull(num)) {
            return "";
        }
        let reg = /\d{1,3}(?=(\d{3})+$)/g;
        let tempArr = (num + '').split('.');
        return tempArr[0].replace(reg, '$&,') + (tempArr[1] === 0 || tempArr[1] ? ('.' + tempArr[1]) : '');
    };

    /**
     * 格式化数字(默认保留两位小数)
     * @param num
     * @param fixSize
     * @returns {string|number}
     */
    util.prototype.formatNum = function (num, fixSize) {
        // 保留小数长度(设置保留位数长度)
        if (this.isNotNull(fixSize) && $.isNumeric(fixSize) && parseInt(fixSize) >= 0) {
            fixSize = parseInt(fixSize);
        } else {
            fixSize = 0
        }
        if (this.isNull(num)) {
            num = 0;
            return num.toFixed(fixSize);
        }
        // 转换成字符串
        num = parseFloat(num).toFixed(fixSize) + "";
        let dot_num = "";
        let num_int = 0;
        if (num.indexOf(".") > 0) {
            dot_num = num.substring(num.indexOf("."), num.length);
            num_int = num.substring(0, num.indexOf("."));
        }
        if (parseInt(num_int) === 0) {
            if (fixSize > 0) {
                return num_int + dot_num;
            } else {
                return num_int;
            }
        }
        let front = "";
        if (parseInt(num_int) < 0) {
            front = "-";
        }
        // 取绝对值 开始添加分隔号
        num_int = Math.abs(num_int) + "";
        // 分割成数组
        let num_int_arr = num_int.split("");
        let num_int_resu = [];
        let rec_count = 1;
        for (let num_int_index = (num_int_arr.length - 1); num_int_index >= 0; num_int_index--) {
            num_int_resu.push(num_int_arr[num_int_index]);
            if (rec_count % 3 === 0 && num_int_index !== 0) {
                num_int_resu.push(",");
            }
            rec_count++;
        }
        num_int = num_int_resu.reverse().join("");
        if (fixSize > 0) {
            return front + num_int + dot_num;
        } else {
            return front + num_int;
        }
    };

    /**
     * 将数字四舍五入保留fixSize位小数
     * @param num
     * @param fixSize
     */
    util.prototype.toFixed = function (num, fixSize) {
        num = num + "";
        if (!fixSize) fixSize = 0;
        if (num.indexOf(".") === -1) num += ".";
        num += new Array(fixSize + 1).join("0");
        if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (fixSize + 1) + "})?)\\d*$").test(num)) {
            let s = "0" + RegExp.$2, pm = RegExp.$1, a = RegExp.$3.length, b = true;
            if (a === fixSize + 2) {
                a = s.match(/\d/g);
                if (parseInt(a[a.length - 1]) > 4) {
                    for (let i = a.length - 2; i >= 0; i--) {
                        a[i] = parseInt(a[i]) + 1;
                        if (a[i] == 10) {
                            a[i] = 0;
                            b = i != 1;
                        } else break;
                    }
                }
                s = a.join("").replace(new RegExp("(\\d+)(\\d{" + fixSize + "})\\d$"), "$1.$2");

            }
            if (b) s = s.substr(1);
            return (pm + s).replace(/\.$/, "");
        }
        return num + "";
    };

    //js计算精度问题 +
    util.prototype.accAdd = function (data1, data2) {
        let r1, r2, m, c;
        try {
            r1 = data1.toString().split(".")[1].length;
        } catch (e) {
            r1 = 0;
        }
        try {
            r2 = data2.toString().split(".")[1].length;
        } catch (e) {
            r2 = 0;
        }
        c = Math.abs(r1 - r2);
        m = Math.pow(10, Math.max(r1, r2));
        if (c > 0) {
            let cm = Math.pow(10, c);
            if (r1 > r2) {
                data1 = Number(data1.toString().replace(".", ""));
                data2 = Number(data2.toString().replace(".", "")) * cm;
            } else {
                data1 = Number(data1.toString().replace(".", "")) * cm;
                data2 = Number(data2.toString().replace(".", ""));
            }
        } else {
            data1 = Number(data1.toString().replace(".", ""));
            data2 = Number(data2.toString().replace(".", ""));
        }
        return (data1 + data2) / m;
    };

    // -
    util.prototype.accSub = function (data1, data2) {
        let r1, r2, m, n;
        try {
            r1 = (data1 + '').split(".")[1].length;
        } catch (e) {
            r1 = 0;
        }
        try {
            r2 = (data2 + '').split(".")[1].length;
        } catch (e) {
            r2 = 0;
        }
        m = Math.pow(10, Math.max(r1, r2));
        n = (r1 >= r2) ? r1 : r2;
        return parseFloat(((data1 * m - data2 * m) / m).toFixed(n));
    };

    // *
    util.prototype.accMulti = function (data1, data2) {
        let baseData = 0;
        try {
            baseData += data1.toString().split(".")[1].length;
        } catch (e) {
        }
        try {
            baseData += data2.toString().split(".")[1].length;
        } catch (e) {
        }
        return Number(data1.toString().replace(".", "")) * Number(data2.toString().replace(".", "")) / Math.pow(10, baseData);
    };

    // /
    util.prototype.accDiv = function (data1, data2) {
        let baseData1 = 0,
            baseData2 = 0,
            Num1, Num2;
        try {
            baseData1 = data1.toString().split(".")[1].length
        } catch (e) {
        }
        try {
            baseData2 = data2.toString().split(".")[1].length
        } catch (e) {
        }
        with (Math) {
            Num1 = Number(data1.toString().replace(".", ""));
            Num2 = Number(data2.toString().replace(".", ""));
            return (Num1 / Num2) * pow(10, baseData2 - baseData1);
        }
    };

    /**
     * 获取url中的参数
     **/
    util.prototype.getUrlParam = function (name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        let r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) {
            return unescape(r[2]);
        } else {
            return null;
        }
    };

    /**
     * 获取必须的字符串
     * @param required
     * @returns {string}
     */
    util.prototype.getRequired = function (required) {
        if (this.isTrue(required)) {
            return "required='true'";
        } else {
            return "";
        }
    };

    /**
     * 对象内的key排序
     */
    util.prototype.sortObjectKey = function (unordered) {
        let ordered = {};
        Object.keys(unordered).sort().forEach(function (key) {
            ordered[key] = unordered[key];
        });
        return ordered;
    };

    /**
     * 克隆对象
     */
    util.prototype.cloneObj = function (obj) {
        let str;
        let newobj = obj.constructor === Array ? [] : {};
        if (typeof obj !== 'object') {
            return;
        } else if (window.JSON) {
            str = JSON.stringify(obj); // 序列化对象
            newobj = JSON.parse(str); // 还原
        } else {
            for (let i in obj) {
                newobj[i] = typeof obj[i] === 'object' ? this.cloneObj(obj[i]) : obj[i];
            }
        }
        return newobj;
    };
    /**
     * 格式化时间
     * @param date
     * @returns {string}
     */
    util.prototype.dateFormatter = function (date) {
        if (this.isNull(date)) {
            return "";
        }
        let y = date.getFullYear();
        let m = date.getMonth() + 1;
        let d = date.getDate();
        return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
    };
    /**
     * 将JSON数组 转换成字符串
     * @param arrJSON JSON数组 字符串
     * @param spliter 分隔字符串
     */
    util.prototype.jsonArrayToStr = function (arrJSON, spliter) {
        if (this.isNull(arrJSON)){
            return "";
        }
        let arr = JSON.parse(arrJSON);
        spliter = this.formatBlank(spliter,",");
        return arr.join(spliter);
    };
    return new util();
});