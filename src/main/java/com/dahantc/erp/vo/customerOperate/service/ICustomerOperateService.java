package com.dahantc.erp.vo.customerOperate.service;

import java.util.Date;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customerOperate.BuildBillFlowReqDto;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl.TimeQuantum;
import com.dahantc.erp.vo.user.entity.User;

public interface ICustomerOperateService {
	public BaseResponse<String> applyProcess(ApplyProcessReqDto reqDto, OnlineUser onlineUser);

	public BaseResponse<String> buildBillFlow(BuildBillFlowReqDto reqDto, String flowId, int flowType, String viewerRoleId);

	public long getPlatformSuccessCount(int productType, String accounts, Date startDate, Date endDate);
	
	public Map<Date, Long> getPlatformSuccessDateCount(int productType, String accounts, Date startDate, Date endDate, Map<String, Long> accountSuccessMap);

	public Map<String, Long> getPlatformSuccessCount4Inter(int productType, String accounts, Date startDate, Date endDate);
	
	public Map<Date, Map<String, Long>> getPlatformSuccessDateCount4Inter(int productType, String accounts, Date startDate, Date endDate, Map<String, Long> accountSuccessMap);
	

	public String getFlowBaseData(CustomerProduct product, Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate,
			Map<TimeQuantum, ModifyPrice> mpRecord);

	public String getFlowBaseData4Inter(CustomerProduct product, Map<Date, Map<String, Long>> countryCountMap, Date startDate, Date endDate,
			Map<TimeQuantum, ModifyPrice> mpRecord);

}
