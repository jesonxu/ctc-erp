document.write('<link rel="stylesheet" href="/layuiadmin/layui/css/layui.css" media="all">');
document.write('<link rel="stylesheet" href="/common/css/common.css" media="all">');
document.write('<link rel="stylesheet" href="/layuiadmin/style/admin.css" media="all">');
document.write('<link rel="stylesheet" href="/layuiadmin/layui/css/eleTree.css" media="all">');
document.write('<link rel="stylesheet" href="/common/font/font-one.css" media="all">');
document.write('<script src="/common/js/jquery-1.8.0.min.js"  type="text/javascript"></script>');
document.write('<script src="/common/js/jquery.i18n.properties-1.0.9.js"  type="text/javascript"></script>');
document.write('<script src="/layuiadmin/layui/layui.js"  type="text/javascript"></script>');

//扩展Date的format方法 
Date.prototype.format = function (format) {
	var o = {
		"M+": this.getMonth() + 1,
		"d+": this.getDate(),
		"h+": this.getHours(),
		"m+": this.getMinutes(),
		"s+": this.getSeconds(),
		"q+": Math.floor((this.getMonth() + 3) / 3),
		"S": this.getMilliseconds()
	}
	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for (var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
}

/**
 * 将选择的文本内容替换成模板分隔符，格式为${0,3}
 * @param contentId
 * @returns
 */
function changeSelectionText(contentId) {
	var selectStr = get_selection(contentId).text;
	var smsContent = $("#" + contentId).val();
	if (selectStr == "" || smsContent.indexOf(selectStr) < 0) {
		layer.msg('请用鼠标选中要生成变量的内容');
		return;
	}
	var testRegex = /[$]{1}{[0-9]+,[0-9]+}/g;
	var result = selectStr.match(testRegex);
	var endLength = 0;
	var contentLen = selectStr.length;
	if (result != null) {
		for (var i = 0; i < result.length; i++) {
			endLength = endLength + parseInt(result[i].substring(result[i].indexOf(",") + 1, result[i].indexOf("}")));
			contentLen = contentLen - result[i].length;
		}
	}
	contentLen = contentLen + endLength;
	var str = "${" + (contentLen > 5 ? parseInt(contentLen / 2) : 0) + "," + contentLen + "}";
	replace_selection(contentId, str);
}

function get_selection(the_id) {
	var e = (typeof (the_id) == 'string' || typeof (the_id) == 'String') ? document.getElementById(the_id) : the_id;

	//Mozilla and DOM 3.0
	if ('selectionStart' in e) {
		var l = e.selectionEnd - e.selectionStart;
		return {
			start: e.selectionStart,
			end: e.selectionEnd,
			length: l,
			text: e.value.substr(e.selectionStart, l)
		};
	}
	//IE
	else if (document.selection) {
		e.focus();
		var r = document.selection.createRange();
		var tr = e.createTextRange();
		var tr2 = tr.duplicate();
		tr2.moveToBookmark(r.getBookmark());
		tr.setEndPoint('EndToStart', tr2);
		if (r == null || tr == null) return {
			start: e.value.length,
			end: e.value.length,
			length: 0,
			text: ''
		};
		var text_part = r.text.replace(/[\r\n]/g, '.'); //for some reason IE doesn't always count the \n and \r in the length
		var text_whole = e.value.replace(/[\r\n]/g, '.');
		var the_start = text_whole.indexOf(text_part, tr.text.length);
		return {
			start: the_start,
			end: the_start + text_part.length,
			length: text_part.length,
			text: r.text
		};
	}
	//Browser not supported
	else return {
		start: e.value.length,
		end: e.value.length,
		length: 0,
		text: ''
	};
}

function replace_selection(the_id, replace_str) {
	var e = (typeof (the_id) == 'string' || typeof (the_id) == 'String') ? document.getElementById(the_id) : the_id;
	selection = get_selection(the_id);
	var start_pos = selection.start;
	var end_pos = start_pos + replace_str.length;
	e.value = e.value.substr(0, start_pos) + replace_str + e.value.substr(selection.end, e.value.length);
	return {
		start: start_pos,
		end: end_pos,
		length: replace_str.length,
		text: replace_str
	};
}

function checkIP(ip) {
	var ips = ip.replace(/ /g, "");
	ips = ips.split("|");
	var ipSegs = [];
	var ipses = [];
	var len = ips.length;
	for (var num = 0; num < ips.length; num++) {
		if (ips[num].indexOf("-") != -1) {
			var ipArr = ips[num].split("-");
			if (ip2number(ipArr[0]) > ip2number(ipArr[1])) {
				return "包含错误的ip:" + ips[num];
			}
			for (var i = 0; i < ipArr.length; i++) {
				if (!isIP(ipArr[i])) {
					return "包含错误的ip:" + ipArr[i];
				}
			}
			ipSegs.push(ips[num]);
			ips.splice(num, 1);
			num = num - 1;
		} else if (!isIP(ips[num])) {
			return "包含错误的ip:" + ips[num];
		}
	}
	//开始去重
	var arrData = deleteRepetion(ips)["arrData"];
	arrData = notEmpty(arrData);
	var repData = deleteRepetion(ips)["repData"];
	for (var num = 0; num < arrData.length; num++) {
		for (var i = 0; i < ipSegs.length; i++) {
			var ipSeg = ipSegs[i].split("-");
			var start = ipSeg[0];
			var end = ipSeg[1];
			var assertIp = assertIpNum(arrData[num], start, end);
			if (assertIp) {
				repData.push(arrData[num]);
				arrData.splice(num, 1);
				num = num - 1;
				break;
			}
		}
	}

	var repIpSegment = new Array(); //开始ip段相比较
	for (var j = 0; j < ipSegs.length; j++) {
		for (var k = j + 1; k < ipSegs.length; k++) {
			if (k != j) {
				var ipSeg = ipSegs[k].split("-");
				var start = ipSeg[0];
				var end = ipSeg[1];
				var ipSegCompare = ipSegs[j].split("-");
				var startCompare = ipSegCompare[0];
				var endCompare = ipSegCompare[1];
				if (assertIpNum(startCompare, start, end)) { //如果比较的ip段前端点被当前循环某ip段包含
					if (assertIpNum(endCompare, start, end)) { //如果比较的ip段后端点也被当前循环某ip段包含
						ipSegs.splice(j, 1);
						repIpSegment.push(ipSegs[j]);
						k = k - 1;
					} else if (ip2number(endCompare) >= ip2number(end) && ip2number(endCompare) >= ip2number(start)) {
						if (ip2number(end) >= ip2number(start)) {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, start + "-" + endCompare);
							repIpSegment.push(startCompare + "-" + end);
							k = k - 1;
						} else {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, end + "-" + endCompare);
							repIpSegment.push(startCompare + "-" + start);
							k = k - 1;
						}
					} else {
						if (ip2number(end) >= ip2number(start)) {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, endCompare + "-" + end);
							repIpSegment.push(start + "-" + startCompare);
							k = k - 1;
						} else {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, endCompare + "-" + start);
							repIpSegment.push(end + "-" + startCompare);
							k = k - 1;
						}
					}
				} else if (assertIpNum(endCompare, start, end)) { //如果比较的ip段后端点被当前循环某ip段包含且前端点不被包含
					if (ip2number(startCompare) >= ip2number(end) && ip2number(startCompare) >= ip2number(start)) {
						//如果当前ip段前端点大于内循环ip段的前后端点ip
						if (ip2number(end) >= ip2number(start)) {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, start + "-" + startCompare);
							repIpSegment.push(start + "-" + endCompare);
							k = k - 1;
						} else {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, end + "-" + startCompare);
							repIpSegment.push(endCompare + "-" + start);
							k = k - 1;
						}
					} else {
						if (ip2number(end) >= ip2number(start)) {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, startCompare + "-" + end);
							repIpSegment.push(start + "-" + endCompare);
							k = k - 1;
						} else {
							ipSegs.splice(k, 1);
							ipSegs.splice(j, 1, startCompare + "-" + start);
							repIpSegment.push(end + "-" + endCompare);
							k = k - 1;
						}
					}
				} else if (assertIpNum(start, startCompare, endCompare) || assertIpNum(end, startCompare, endCompare)) {
					repIpSegment.push(ipSegs[k]);
					ipSegs.splice(k, 1);
					k = k - 1;
				}
			}
		}
	}
	repData = repData.concat(repIpSegment);
	var arr = arrData.concat(ipSegs);
	var str = arr.toString();
	var strs = str.replace(/,/g, "|");
	$("#accessIp").val(strs);
	repData = notEmpty(repData);
	if (repData.length > 0) {
		return "重复IP：[" + repData.toString() + "]<br/>已经为您去除重复IP";
	}
	return true;
}

function deleteRepetion(arr) {
	var arrTable = {},
		arrData = [],
		repData = [];
	for (var i = 0; i < arr.length; i++) {
		if (!arrTable[arr[i]]) {
			arrTable[arr[i]] = true;
			arrData.push(arr[i]);
		} else {
			repData.push(arr[i]);
		}
	}
	var result = {
		"arrData": arrData,
		"repData": repData
	}
	return result;
}

function notEmpty(repData) {
	for (var num = 0; num < repData.length; num++) {
		if (!repData[num] || repData[num] == undefined || repData[num] == "" || repData[num] == null) {
			repData.splice(num, 1);
		}
	}
	return repData;
}

function ip2number(ip) {
	var tokens = ip.split(".");
	var numval = 0.0;
	for (var ipIndex = 0; ipIndex < tokens.length; ipIndex++) {
		numval = numval * 256 + parseFloat(tokens[ipIndex]);
	}
	return numval;
}
// 校验ip号码是否正确
function isIP(ip) {
	var reg = /^([0-9]{1,2}|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\.([0-9]{1,2}|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\.([0-9]{1,2}|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\.([0-9]{1,2}|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$/;
	if (reg.test(ip)) {
		return true;
	} else {
		return false;
	}
}

function assertIpNum(ip, start, end) {
	var ipNum = ip2number(ip);
	var startNum = ip2number(start);
	var endNum = ip2number(end);
	if (startNum < endNum) {
		return ipNum >= startNum && ipNum <= endNum;
	} else if (startNum > endNum) {
		return ipNum <= startNum && ipNum >= endNum;
	} else {
		return ipNum != startNum;
	}
}

function guid() {
    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}