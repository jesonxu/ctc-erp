Toolbar = function (config) {
	// 承载容器
	this.renderTo = config.renderTo;

	// 子组件
	this.items = config.items || [];

	// 承载容器
	this.renderContent = typeof this.renderTo == "string" ? $("#" +
		this.renderTo) : this.renderTo;

};


Toolbar.prototype = {
	init: function () {
		this.btnBody = $(document.createElement("DIV"));
		this.btnBody.addClass("layui-btn-container");
		this.btnBody.appendTo(this.renderContent);
		for (var i = 0; i < this.items.length; i++) {
			this.add(this.items[i]);
		}
	},
	render: function () {
		this.init();
	},
	add: function (t) {
		var toolbarEntity = this;

		if (t.type == "button") {
			var button = $(document.createElement("BUTTON"));
			button.addClass("layui-btn layui-btn-sm");
			button.text(t.text);
			if(t.id){
				button.attr("id", t.id);
			}
			if(t.icon){
				button.prepend('<i class="layui-icon '+ t.icon+'" style = "margin-left:-3px;margin-right:5px;"></i>')
			}
			if (t.bodyStyle) {
				button.addClass(t.bodyStyle);
			}

			if (t.title) {
				button.attr("title", t.title);
			}

			// 是否有权限
			if (t.useable != "F") {
				if (t.handler) {
					button.bind("click", t.handler);
				}
				button.appendTo(this.btnBody);
			}
		}
	}
};