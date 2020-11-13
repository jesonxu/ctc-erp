// 电商预览更多信息
$(document).ready(function () {
    var supplierId = $("#supplierId").val();
    $("#privewMore").click(function(){
        to_preivew_supperlier_baseinfo(supplierId);
    })
});
// 查看供应商基本信息 operationType=1是查看信息
function to_preivew_supperlier_baseinfo(supplier_id) {
    openDialogIndex = layer.open({
        type: 2,
        area: ['765px', '560px'],
        fixed: false, //不固定
        maxmin: true,
        content: '/supplier/toEditSupperlierBaseinfo/' + supplier_id + "?entityType=2&operationType=1&r=" + Math.random()
    });
}