// 上传的图片信息
var picture = '';
var form;
$(document).ready(function() {
    initClickCheckbox();
    init_file_upload();
    load_dianShangSupplier();
	layui.use('form', function() {
		form = layui.form;
        form.render();
    });
});

function initClickCheckbox() {

    $('#cancel').click(function() {
        var index = parent.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
        parent.layer.close(index);
    });

    $('#submit').click(function() {

        var formData = {
            supplierid: $("#supplierid").val(),
    		producttype: $("#producttype").val(),
            productname: $("#productname").val(),
            format: $("#format").val(),
            pcode: $("#pcode").val(),
            groupprice: $("#groupprice").val(),
            groupnumber: $('#groupnumber').val(),
            wholesaleprice: $('#wholesaleprice').val(),
            rant: $('#rant').val(),
            standardprice: $('#standardprice').val(),
            period: $('#period').val(),
            onsale: $('#onsale input[name="onsale"]:checked ').val(),//获取选中的值
            remark: $('#remark').val(),
            picture: picture
        };

        if (!formData.supplierid) {
            $("#supplierid").focus();
            return layer.tips("请选择供应商", $("#supplierid"));
        }
        if (!formData.producttype) {
            $("#producttype").focus();
            return layer.tips("请输入品类", $("#producttype"));
        }
        if (!formData.productname) {
            $("#productname").focus();
            return layer.tips("请输入品名", $("#productname"));
        }
        if (!formData.format) {
            $("#format").focus();
            return layer.tips("请输入产品规格", $("#format"));
        }
        if (!formData.groupprice) {
            $("#groupprice").focus();
            return layer.tips("请输入团购价格", $("#groupprice"));
        } else if (!isPriceInteger(formData.groupprice)) {
            $("#groupprice").focus();
            return layer.tips("金额为整数，最多两位小数", $("#groupprice"));
        }
        if (!formData.groupnumber) {
            $("#groupnumber").focus();
            return layer.tips("请输入团购起订量", $("#groupnumber"));
        } else if (!isPositiveInteger(formData.groupnumber)) {
            $("#groupnumber").focus();
            return layer.tips("请输入正整数类型", $("#groupnumber"));
        } else if (formData.groupnumber == 1 || formData.groupnumber == 0) {
            $("#groupnumber").focus();
            return layer.tips("团购起订量必须大于1", $("#groupnumber"));
        }
        if (!formData.rant) {
            $("#rant").focus();
            return layer.tips("请输入税率", $("#rant"));
        } else if (!isPositiveInteger(formData.rant)) {
            $("#rant").focus();
            return layer.tips("请输入正整数类型", $("#rant"));
        }
        if (!formData.standardprice) {
            $("#standardprice").focus();
            return layer.tips("请输入市场价", $("#standardprice"));
        } else if (!isPriceInteger(formData.standardprice)) {
            $("#standardprice").focus();
            return layer.tips("金额为整数，最多两位小数", $("#standardprice"));
        }
        if (isNull(formData.picture) || formData.picture == ' ') {
            $('#contractFiles').focus();
            layer.tips('请上传产品图示', '#contractFiles');
            return false;
        }
        if (formData.wholesaleprice) {
            if (!isPriceInteger(formData.wholesaleprice)) {
                $("#wholesaleprice").focus();
                return layer.tips("金额为整数，最多两位小数", $("#wholesaleprice"));
            }
        }
        if (formData.period) {
            if (!isPositiveInteger(formData.period)) {
                $("#period").focus();
                return layer.tips("请输入正整数类型", $("#period"));
            }
        }
        $.ajax({
            type: "POST",
            async: false,
            url: "/dsProduct/save.action",
            dataType: 'json',
            data: formData,
            success: function(data) {
                if (data.code != 200) {
                    return layer.msg(data.msg);
                }
                var index = parent.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
                parent.reload_table();
                parent.layer.close(index);
                parent.layer.msg(data.msg);
                if (typeof parent.loadSupplierProducts == 'function') {
                    parent.loadSupplierProducts(parent.supplierId)
                }
            }
        });
    });

}
function isPositiveInteger(s){//是否为正整数
     var re = /^[0-9]+$/ ;
     return re.test(s)
}
function isPriceInteger(s){//金额是否正确（最多两位小数）
     var re = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
     return re.test(s)
}
// 加载供应商信息
function load_dianShangSupplier() {
    $.ajax({
        type: "POST",
        url: "/dianShangSupplier/querySupplier.action",
        dataType: 'json',
        success: function (data) {
            if (200 === data.code) {
                var types = data.data;
                var content = "<option value=''>请选择供应商</option>";
                $.each(types, function (index, obj) {
                    content += "<option value=" + obj.supplierId + ">" + obj.companyName + "</option>";
                });
                $("#supplierid").html(content);
                layui.use('form', function() {
                    form = layui.form;
                    form.render();
                    form.render('select');
                });
            } else {
                return layer.msg(data.msg, {icon: 2});
            }
        }
    });
}


// 初始化图片上传
function init_file_upload() {
    /*var upload;*/
    layui.use('upload',function() {
      var upload = layui.upload;
      //普通图片上传
      var uploadInst = upload.render({
        elem: '#contractFiles'
        ,url: '/operate/upLoadFile' //改成您自己的上传接口
        ,field: 'files'
        ,before: function(obj){
          //预读本地文件示例，不支持ie8
          obj.preview(function(index, file, result){
            $('#demo1').attr('src', result); //图片链接（base64）
          });
        }
        ,done: function(res){
          //如果上传失败
          if(res.code == 200){
            picture = res.data[0].filePath;
            console.log(picture + 'picture');
            layer.msg('上传成功')
          } else {
            return layer.msg('上传失败');
          }
          //上传成功
        }
        ,error: function(){
          //演示失败状态，并实现重传
          var demoText = $('#demoText');
          demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
          demoText.find('.demo-reload').on('click', function(){
            uploadInst.upload();
          });
        }
      });
    });
}
