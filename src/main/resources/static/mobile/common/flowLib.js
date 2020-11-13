/**
 * 加载所有的流程工具 和 标签文件
 * @attention 文件的文件里面的内容 可能有 顺序的加载要求 如果引用进来有问题 请注意文件的顺序
 */

document.write('<script type="text/javascript" src="/common/js/dropdown.js" ></script>');
layui.config({
    base: '/common/js/'
}).extend({ // 设定模块别名
    dropdown: 'dropdown'
});

/**
 * 流程标签 文件
 **/
var now = new Date().getTime();
document.write('<script type="text/javascript" src="/mobile/common/labels/AccountBillLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/AccountInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/BankInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/BillInvoiceInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/BooleanLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/ChargeTypeLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/ContractNumberLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/CustInvoiceInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DateLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DateTimeLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DsApplyOrderLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DsMatchOrderLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DsMatchPeopleLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DsOrderNumberLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/DsPurchaseNumberLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/FileLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/GradientLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/HistoryPriceLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/IntegerLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/InvoiceInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/MonthLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/NumberLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/OtherBankLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/OtherInvoiceLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/PlatformAccountInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/PriceTypeLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/RemunerationLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/SelectLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/SelfBankLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/SelfInvoiceLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/SwitchLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/TextareaLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/TextLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/TimeAccountBillLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/UncheckedBillInfoLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/UnWriteOffBillLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/UnWriteOffReceiptLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/RadioLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/TimeSlotLabel.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/labels/RechargeDetail.js?v=' + now + '" ></script>');

/**
 * 流程工具文件
 */
document.write('<script type="text/javascript" src="/mobile/common/flow/rejectSelect.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/flowOperate.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/fileTool.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/expressionTool.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/FlowDetail.js?v=' + now + '" ></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/WriteOffBillFlowAuditLabels.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/FlowAuditLabels.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/FlowRecord.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/RecordUnlabeledData.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/WriteOffBillFlowDetail.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/WriteOffBillFlowApplyLabels.js?v=' + now + '"></script>');
document.write('<script type="text/javascript" src="/mobile/common/flow/FlowApplyLabels.js?v=' + now + '"></script>');

/**
 * 样式
 */
document.write('<link rel="stylesheet" href="/mobile/common/css/label.css?v=' + now + '"/>');