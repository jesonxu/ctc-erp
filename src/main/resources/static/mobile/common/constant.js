/**
 * 移动端默认配置参数
 * @type {{flow_load_count: number}}
 */
var DEFAULT_PARAM = {
    flowLoadCount: 10, // 流程加載数量
    supplierLoadCount: 20// 供应商加载数
};

/**
 * 流程状态
 * @type {{"0": string, "1": string, "2": string, "3": string}}
 */
var FLOW_STATE = {
    0: '未审核',
    1: '归档',
    2: '待审核',
    3: '取消'
};
/**
 * 流程状态 带样式
 * @type {{"0": string, "1": string, "2": string, "3": string}}
 */
var FLOW_STATE_STYLE = {
    0: "<span class='flow-state-process'>（进行中）</span>",
    1: "<span class='flow-state-document'>（已归档）</span>",
    2: "<span class='flow-state-process'>（进行中）</span>",
    3: "<span class='flow-state-cancel'>（已取消）</span>"
};
/**
 * 列表的开闭状态
 * @type {{"0": boolean, "1": boolean}}
 */
var OPEN_STATE = {
    close: false,
    open: true
};
/**
 * Boolean 字符串转换
 * @type {{true: boolean, false: boolean}}
 */
var BOOLEAN_CONVERT = {
    "true": true,
    "false": false
};

/**
 * 流程类型
 * @type {{"0": string, "1": string, "2": string, "3": string, "4": string}}
 */
const FLOW_TYPE = {
    0: "运营",
    1: "结算",
    2: "对账",
    3: "发票",
    4: "销账",
    5: "个人"
};