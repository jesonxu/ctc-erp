var tableIns;
var editIndex;
var laydate;
var form;
var layer;
var element;
var val_dateNum = parent.dateLineType;
$(document).ready(function () {
	layui.use(['laydate','form','element','layer'], function(){
		  laydate = layui.laydate;
		  form = layui.form;
		  layer = layui.layer;
		  element = layui.element;
		  //日期范围
		  laydate.render({
		    elem: '#rangeData'
		    ,range: true
		  });
		  /*form.render();*/
	});
	console.log(parent.infotype);
	console.log(parent.dateLineType);
	initTable(parent.infotype);
});
// 初始化数据
function tdTitle() {
	$('th').each(function(index,element){
		$(element).attr('title',$(element).text());
	});
	$('td').each(function(index,element){
		$(element).attr('title',$(element).text());
	});
};
// 初始化表格
function initTable(infotype) {
	layui.use(['table'], function () {
		var table = layui.table;
		//列表
		tableIns = table.render({
			elem: '#messageInfoList',
			url : "/messageCenter/getMsgList.action?temp=" + Math.random(),
			height: 'full-30',
			even: true,
			page: true,
			limit: 15,
			where: {
				infotype: infotype,
				dateLinetype: val_dateNum
			},
			limits: [15,30,60,100],
			method: 'POST',
			cols: [[{
                type: 'numbers',
                title: '序号',
            }, {
				field: 'ossUserId',
				title: 'ossUserId',
				align: 'center',
				hide: true
			}, {
				field: 'wtime',
				title: '时间',
				align: 'left',
				width: 145
			}, {
				field: 'messagedetail',
				title: '内容预览',
				align: 'left'
			}, {
				field: 'infotype',
				title: '消息类型',
				align: 'center',
				width: 100,
			}]]
			, parseData: function (res) { //res 即为原始返回的数据
				return {
					"code": 0, //解析接口状态
					"count": res.data.count, //解析数据长度
					"data": res.data.data //解析数据列表
				};
			}
			, done:function (res, curr, count) {
                $("[data-field='infotype']").children().each(function () {
                	if ($(this).text() == '1') {
                        $(this).text("新增客户");
                    } else if ($(this).text() == '2') {
                    	$(this).text("新增供应商");
                    } else if ($(this).text() == '3') {
                    	$(this).text("新增联系日志");
                    } else if ($(this).text() == '4') {
                    	$(this).text("公告");
                    }
                });
                tdTitle();
            }

		});
		table.on('row(messageInfoList)', function(obj){////注：test是table原始容器的属性 lay-filter="对应的值"
			obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
			/*alert(1)*/
		});
	});
}

function reloadTable() {
	tableIns.reload();
}

function closeEdit(){
	layui.layer.close(editIndex);
}

