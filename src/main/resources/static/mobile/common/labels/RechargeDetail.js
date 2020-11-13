/**
 * 充值详情 37
 */
(function (window, factory) {
    window.RechargeDetail = factory();
})(window, function () {
	
	const LABEL_TYPE_NAME = '充值详情';
	
	// 充值详情的各个子标签的名称
    const RECHARGE_DETAIL_NAME = {
		recharge_account: '账号',
    	current_amount: '当前余额',
        price: '单价',
        recharge_amount: '充值金额',
        pieces: '条数'
    };
    
    // 充值详情的各个子标签对应流程信息flowMsg的key的映射
    const RECHARGE_DETAIL_KEY = {
		recharge_account: 'rechargeAccount',
    	current_amount: 'currentAmount',
        price: 'price',
        recharge_amount: 'rechargeAmount',
        pieces: 'pieces'
    };
	
	// 原始的充值账号的 div item 
	let itemId = util.uuid();
	let accounts = [];
	let resourceItemEle = $(
		'<div class="gradient-item account-recharge" lay-filter="' + itemId + '" id="' + itemId + '">' +
		'	<div class="layui-form-item">' + // 账号
		'		<label class="flow-label-name"><span style="color: red;">*</span>' + RECHARGE_DETAIL_NAME.recharge_account + '：</label>' +
		'		<div class="flow-label-content">'+
		'			<select name="' + RECHARGE_DETAIL_KEY.recharge_account + '" lay-filter="recharge-account-' + itemId + '">' + '</select>' +
		'		</div>' +
		'	</div>' +
		'	<div class="layui-form-item">' + // 当前余额
		'		<label class="flow-label-name"><span style="color: red;">*</span>' + RECHARGE_DETAIL_NAME.current_amount + '：</label>' +
		'		<div class="flow-label-content">'+
		'			<input class="layui-input" name="' + RECHARGE_DETAIL_KEY.current_amount + '" placeholder="请填写" />' +
		'		</div>' +
		'	</div>' +
		'	<div class="layui-form-item">' + // 单价
		'		<label class="flow-label-name"><span style="color: red;">*</span>' + RECHARGE_DETAIL_NAME.price + '：</label>' +
		'		<div class="flow-label-content">'+
		'			<input class="layui-input" name="' + RECHARGE_DETAIL_KEY.price + '" placeholder="请填写" />' +
		'		</div>' +
		'	</div>' +
		'	<div class="layui-form-item">' + // 充值金额
		'		<label class="flow-label-name"><span style="color: red;">*</span>' + RECHARGE_DETAIL_NAME.recharge_amount + '：</label>' +
		'		<div class="flow-label-content">'+
		'			<input class="layui-input" name="' + RECHARGE_DETAIL_KEY.recharge_amount + '" placeholder="请填写" />' +
		'		</div>' +
		'	</div>' +
		'	<div class="layui-form-item">' + // 条数
		'		<label class="flow-label-name"><span style="color: red;">*</span>' + RECHARGE_DETAIL_NAME.pieces + '：</label>' +
		'		<div class="flow-label-content">'+
		'			<input class="layui-input" name="' + RECHARGE_DETAIL_KEY.pieces + '" placeholder="请填写" />' +
		'		</div>' +
		'	</div>' +
		'</div>'
	);
	
	let optsEle = $(
		'<div class="layui-form-item gradient-opts">' +
		'	<button class="layui-btn layui-btn-primary layui-btn-xs" data-operate="add"><i class="layui-icon layui-icon-add-circle"></i>添加</button>' +
		'	<button class="layui-btn layui-btn-danger layui-btn-xs" data-operate="delete"><i class="layui-icon layui-icon-close"></i>删除</button>' +
		'</div>'
	);
	
    /**
     * 初始化对象（构造函数）
     * @param labelId 标签的ID
     * @param RECHARGE_DETAIL_NAME 标签名称
     * @param labelType 标签类型
     * @constructor
     */
    let RechargeDetail = function (labelId, labelName, labelType) {
        this.name = labelName;
        if (util.isNull(this.name)) {
            throw new Error('【' + LABEL_TYPE_NAME + '】名称为空');
        }
        this.id = labelId;
        if (util.isNull(this.id)) {
            throw new Error('【' + LABEL_TYPE_NAME + '】ID为空');
        }
        this.labelType = labelType;
    };

    /**
     * 转换为文本 (对外接口 需要渲染标签展示文本必须实现)
     */
    RechargeDetail.prototype.toText = function (value) {
    	let detailInfos = '';
    	if (!util.isNotNull(value)){
    		try {
                if (util.isNotNull(value)) {
                	detailInfos = JSON.parse(value);
                }
            } catch (e) {
                console.log("捕获数据解析异常", e);
            }
    	}
    	if (util.arrayNull(detailInfos)) {
            return this.name + ":无";
        }
    	let strArr = [];
    	for(let i = 0; i < accountInfos.length; i++) {
    		let item = accountInfos[i];
    		strArr.push(RECHARGE_DETAIL_NAME.recharge_account + '【' + item[RECHARGE_DETAIL_KEY.recharge_account] + '】，' 
				+ RECHARGE_DETAIL_NAME.current_amount + '【'
				+ (util.isNotNull(item[RECHARGE_DETAIL_KEY.current_amount]) ? parseFloat(item[RECHARGE_DETAIL_KEY.current_amount]).toFixed(2) : '')
				+ '】，' + RECHARGE_DETAIL_NAME.price + '【'
				+ parseFloat(item[RECHARGE_DETAIL_KEY.price]).toFixed(6) + '】，' + RECHARGE_DETAIL_NAME.recharge_amount + '【' + parseFloat(item[RECHARGE_DETAIL_KEY.recharge_amount]).toFixed(2) + '】，'
				+ RECHARGE_DETAIL_NAME.pieces + '【' + item[RECHARGE_DETAIL_KEY.pieces] + '】');
		}
        return this.name + "：" + strArr.join('；') + '。';
    };

    /**
     * 渲染可以编辑的标签 (对外接口 需要渲染标签必须实现)
     * @param flowEle 渲染的地方
     * @param value 值
     * @param required 是否必须
     */
    RechargeDetail.prototype.render = function (flowEle, value, productId) {
    	if (flowEle.parents('body').find('#entity-id').length > 0) {
	    	let thisEntity = this;
	    	// 绑定选择产品 --> 影响账号获取
	    	let mutationObserver = new MutationObserver(function callback(mutationsList, observer) { // 回调事件
	    		if ($(mutationsList[0].target).attr('value')) {
	    			//查询账号
	    	    	$.ajax({
	    	            type: "POST",
	    	            async: false,
	    	            url: '/customerProduct/queryAccounts?temp=' + Math.random(),
	    	            dataType: 'json',
	    	            data: {
	    	                customerId: $(mutationsList[0].target).attr('value')
	    	            },
	    	            success: function (data) {
	    	            	accounts = data;
	    	            }
	    	    	});
	    	    	$(thisEntity.flowEle).find('div[data-label-id="' + thisEntity.id + '"]').find('select').html('');
	    	    	resourceItemEle.find('select').html('');
	    	    	for (let i = 0; i < accounts.length; i++) {
	    				let account = accounts[i];
	    				resourceItemEle.find('select').append('<option value="' + account + '">' + account + '</option>');
	    			}
	    	    	
	    	    	let cloneItemEle = resourceItemEle.clone();
	    	    	
	    	    	// 添加操作按钮
	        		let cloneOptsEle = optsEle.clone();
	        		cloneOptsEle = thisEntity.getOptsEle(cloneOptsEle, 1);
	        		cloneItemEle.append(cloneOptsEle.prop('outerHTML'));
	    	    	
	    	    	$(thisEntity.flowEle).find('div[data-label-id="' + thisEntity.id + '"]').html('').append(cloneItemEle.prop('outerHTML'));
	    	    	thisEntity.bindEvent($(thisEntity.flowEle).find('div[data-label-id="' + thisEntity.id + '"]').find('.account-recharge:last-child'));
	    	    	layui.form.render();
	    		}
	    	});
	    	mutationObserver.observe(flowEle.parents('body').find('#entity-id')[0],  { // options：监听的属性
	    		attributes: true, 
	    		childList: false,
	    		subtree: false,
	    		attributeOldValue: false
	    	});
    	}
    	
        // 渲染的位置（对应元素下面 直接添加）
        this.flowEle = flowEle;
        if (util.isNull(this.flowEle)) {
            throw new Error('【' + LABEL_TYPE_NAME + '】对应的位置元素不存在');
        }
        
        value = util.formatBlank(value);
        // 数据回显
        
        if (util.isNotNull(productId)) {
	        //查询账号
	    	$.ajax({
	            type: "POST",
	            async: false,
	            url: '/customerProduct/queryAccounts?temp=' + Math.random(),
	            dataType: 'json',
	            data: {
	                productId: productId
	            },
	            success: function (data) {
	            	accounts = data;
	            }
	    	});
        }
    	
    	value = util.formatBlank(value);
    	if (util.isNotNull(value)) {
    		try {
    			value = JSON.parse(value);
    		} catch (e) {
    			console.error(value + '转json异常', e);
    			value = null;
    		}
    	}
		
		let rootEle = $('<div class="layui-form-item label-type-gradient" data-label-id="' + this.id + '"></div>');
		
		for (let i = 0; i < accounts.length; i++) {
			let account = accounts[i];
			resourceItemEle.find('select').append('<option value="' + account + '">' + account + '</option>');
		}
	
		let selectedAccounts = [];
		if (!util.arrayNull(value)) {
			for (let i = 0; i < value.length; i++) {
				let detail = value[i];
				let cloneItemEle = resourceItemEle.clone(false);
				let uid = util.uuid();
				cloneItemEle.attr('id', uid).attr('lay-filter', uid);
				
				for (let i = 0; i < selectedAccounts.length; i++) { // 去除已经选择的账号
					let account = selectedAccounts[i];
					cloneItemEle.find('select option[value="' + account + '"]').remove();
				}
				cloneItemEle.find('select option[value="' + detail[RECHARGE_DETAIL_KEY.recharge_account] + '"]').css('selected', 'selected');
				selectedAccounts.push(detail[RECHARGE_DETAIL_KEY.recharge_account]);
				// 当前余额
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.current_amount + '"]').val(detail[RECHARGE_DETAIL_KEY.current_amount]);
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.current_amount + '"]').attr('value', detail[RECHARGE_DETAIL_KEY.current_amount]);
				// 单价
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.price + '"]').val(detail[RECHARGE_DETAIL_KEY.price]);
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.price + '"]').attr('value', detail[RECHARGE_DETAIL_KEY.price]);
				// 充值金额
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.recharge_amount + '"]').val(detail[RECHARGE_DETAIL_KEY.recharge_amount]);
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.recharge_amount + '"]').attr('value', detail[RECHARGE_DETAIL_KEY.recharge_amount]);
				// 条数
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.pieces + '"]').val(detail[RECHARGE_DETAIL_KEY.pieces]);
				cloneItemEle.find('input[name="' + RECHARGE_DETAIL_KEY.pieces + '"]').attr('value', detail[RECHARGE_DETAIL_KEY.pieces]);
				
				rootEle.append(cloneItemEle.prop('outerHTML'));
			}
			
			rootEle.find('input').attr('readonly', 'readonly');
			rootEle.find('select').attr('disabled', 'disabled');
			
			rootEle.find('.account-recharge:last-child input').removeAttr('readonly');
			rootEle.find('.account-recharge:last-child select').removeAttr('disabled');
		} else {
			rootEle.append(resourceItemEle.prop('outerHTML'));
		}
		
		// 添加操作按钮
		let cloneOptsEle = optsEle.clone();
		cloneOptsEle = this.getOptsEle(cloneOptsEle, rootEle.find('.account-recharge').length);
		rootEle.find('.account-recharge:last-child').append(cloneOptsEle.prop('outerHTML'));
		
        $(this.flowEle).append(rootEle.prop('outerHTML'));
        
        this.bindEvent($(this.flowEle).find('.account-recharge:last-child'));
    };
    
    RechargeDetail.prototype.getOptsEle = function (optsEle, itemCount) {
    	if (accounts.length == 1) {
    		optsEle.find('button').remove();
		} else if (accounts.length == itemCount) {
			optsEle.find('button[data-operate="add"]').remove();
		} else if (itemCount == 1) {
			optsEle.find('button[data-operate="delete"]').remove();
		}
    	return optsEle;
    }

    // 绑定事件
    RechargeDetail.prototype.bindEvent = function (ele) {
    	let thisEntity = this;
        // 条数自动计算
    	ele.find('[name="rechargeAmount"],[name="price"]').unbind().bind('input propertychange', function () {
			let rechargeAmount = ele.find('[name="rechargeAmount"]').val();
			let price = ele.find('[name="price"]').val();
			rechargeAmount = !rechargeAmount ? 0 : rechargeAmount;
			price = !price ? 0 : price;
			let pieces = price == 0 ? 0 : (rechargeAmount / price);
			pieces = isNaN(pieces) ? 0 : pieces;
			ele.find('[name="pieces"]').val(parseInt(pieces.toFixed(0)));
		});
    	
    	// 绑定添加事件
    	ele.find('button[data-operate="add"]').unbind().bind('click', function () {
    		let thisEle = $(this).parents('.account-recharge');
    		let flag = thisEntity.verifySingleItem(thisEle);
    		if (!flag) { // 当前的一组数据不合格不给添加
    			return;
    		}
    		
    		thisEle.find('input').attr('readonly', 'readonly');
    		thisEle.find('select').attr('disabled', 'disabled');
    		thisEle.find('.gradient-opts button').remove();
    		
    		let cloneItemEle = resourceItemEle.clone(false);
    		let uid = util.uuid();
    		cloneItemEle.attr('id', uid).attr('lay-filter', uid);
    		
    		thisEle.parent().find('.account-recharge select[name="' + RECHARGE_DETAIL_KEY.recharge_account + '"]').each(function (i, item) {
    			cloneItemEle.find('select[name="' + RECHARGE_DETAIL_KEY.recharge_account + '"] option[value="' + $(item).val() + '"]').remove();
    		});
    		
    		// 添加操作按钮
    		let cloneOptsEle = optsEle.clone();
    		cloneOptsEle = thisEntity.getOptsEle(cloneOptsEle, thisEle.parent().find('.account-recharge').length + 1);
    		cloneItemEle.append(cloneOptsEle.prop('outerHTML'));
    		
    		thisEle.after(cloneItemEle.prop('outerHTML'));
    		layui.form.render();
    		
    		thisEntity.bindEvent(thisEle.parent().find('.account-recharge:last-child'));
    	});
    	
    	// 绑定删除事件
    	ele.find('button[data-operate="delete"]').unbind().bind('click', function () {
    		let thisEle = $(this).parents('.account-recharge');
    		let prev = thisEle.prev();
    		thisEle.remove();
    		
    		prev.find('input').removeAttr('readonly');
    		prev.find('select').removeAttr('disabled');
    		
    		// 添加操作按钮
    		let cloneOptsEle = optsEle.clone();
    		cloneOptsEle = thisEntity.getOptsEle(cloneOptsEle, prev.parent().find('.account-recharge').length);
    		prev.append(cloneOptsEle.prop('outerHTML'));
    		layui.form.render();
    		
    		thisEntity.bindEvent(prev.parent().find('.account-recharge:last-child'));
    	});
    };

    /**
     * 获取标签值 (对外接口 需要取值必须实现)
     * @returns {*}
     */
    RechargeDetail.prototype.getValue = function () {
        let thisLabelEle = $(this.flowEle).find('div[data-label-id="' + this.id + '"]');
        let itemJsonArr = [];
        thisLabelEle.find('.account-recharge').each(function (i, item) {
        	let json = {};
        	json[RECHARGE_DETAIL_KEY.recharge_account] = $(item).find('select[name="' + RECHARGE_DETAIL_KEY.recharge_account + '"]').val();
        	json[RECHARGE_DETAIL_KEY.current_amount] = $(item).find('input[name="' + RECHARGE_DETAIL_KEY.current_amount + '"]').val();
        	json[RECHARGE_DETAIL_KEY.price] = $(item).find('input[name="' + RECHARGE_DETAIL_KEY.price + '"]').val();
        	json[RECHARGE_DETAIL_KEY.recharge_amount] = $(item).find('input[name="' + RECHARGE_DETAIL_KEY.recharge_amount + '"]').val();
        	json[RECHARGE_DETAIL_KEY.pieces] = $(item).find('input[name="' + RECHARGE_DETAIL_KEY.pieces + '"]').val();
        	itemJsonArr.push(json);
        });
        return JSON.stringify(itemJsonArr);
    };

    /**
     * 获取标签名称(对外接口)
     * @returns {*}
     */
    RechargeDetail.prototype.getName = function () {
        return this.name;
    };
    
    RechargeDetail.prototype.verifySingleItem = function (ele) {
    	let account = ele.find('select[name="' + RECHARGE_DETAIL_KEY.recharge_account + '"]').val(); // 账号
    	let currentAmount = ele.find('input[name="' + RECHARGE_DETAIL_KEY.current_amount + '"]').val(); // 当前余额
    	let price = ele.find('input[name="' + RECHARGE_DETAIL_KEY.price + '"]').val(); // 单价
    	let rechargeAmount = ele.find('input[name="' + RECHARGE_DETAIL_KEY.recharge_amount + '"]').val(); // 充值金额
    	let pieces = ele.find('input[name="' + RECHARGE_DETAIL_KEY.pieces + '"]').val(); // 条数

    	// 账号
		if (util.isNull(account)) {
			layer.msg('请选择' + RECHARGE_DETAIL_NAME.recharge_account);
			return false;
		}
		// 当前余额
		console.log(currentAmount);
		if (util.isNull(currentAmount)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.current_amount + '不能为空');
			return false;
		} else if (!util.isNumber(currentAmount)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.current_amount + '不合法');
			return false;
		}
		// 单价
		if (util.isNull(price)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.price + '不能为空');
			return false;
		} else if (!util.isMoney(price)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.price + '不合法');
			return false;
		}
		// 充值金额
		if (util.isNull(rechargeAmount)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.recharge_amount + '不能为空');
			return false;
		} else if (!util.isMoney(rechargeAmount)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.recharge_amount + '不合法');
			return false;
		}
		// 条数
		if (util.isNull(pieces)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.pieces + '不能为空');
			return false;
		} else if (!util.isMoney(pieces)) {
			layer.msg(account + '的' + RECHARGE_DETAIL_NAME.pieces + '不合法');
			return false;
		}
		return true;
    }

    /**
     * 校验(对外接口 需要渲染标签必须实现)
     * @returns {boolean}
     */
    RechargeDetail.prototype.verify = function () {
        let thisLabelEle = $(this.flowEle).find('div[data-label-id="' + this.id + '"]');
        return this.verifySingleItem(thisLabelEle.find('.account-recharge:last-child'));
    };

    return RechargeDetail;
});