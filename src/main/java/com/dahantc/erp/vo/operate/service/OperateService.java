package com.dahantc.erp.vo.operate.service;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.dto.operate.AuditProcessReqDto;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.user.entity.User;

public interface OperateService {
	BaseResponse<String> applyProcess(ApplyProcessReqDto reqDto, OnlineUser onlineUser);

	BaseResponse<String> auditProcess(AuditProcessReqDto reqDto, String ossUserId);

	BaseResponse<String> queryApplying(String flowId, String billId, String type, String flowEntId);

	BaseResponse<String> verifyBill(String flowId, String flowMsg, String flowEntId);

	void automaticAuditByCondition(FlowEnt flowEnt, FlowNode flowNode);

	/**
	 * 设置调价信息 原来价格信息
	 *
	 * @param flowJson
	 *            流程申请的JSON数据
	 * @param productId
	 *            产品id
	 * @param flowClass
	 *            流程类
	 */
	void setAdjustFlowBeforePrice(JSONObject flowJson, String productId, String flowClass);

	String getBillBaseData(Product product, String flowMsg);

	BaseResponse<String> revokeProcess(String flowEntId, String revokeReson, String ossUserId, int platform);
}
