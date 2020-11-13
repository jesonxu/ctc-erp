package com.dahantc.erp.vo.financialoperatereport;

import java.util.Date;
import java.util.List;

import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.vo.financialoperatereport.entity.GrossProfitRateOverview;
import com.dahantc.erp.vo.financialoperatereport.entity.GrossProfitRateOverview.GrossProfitRateSection;
import com.dahantc.erp.vo.financialoperatereport.entity.MainCustResult;
import com.dahantc.erp.vo.financialoperatereport.entity.NegativeGrossProfitCust;
import com.dahantc.erp.vo.financialoperatereport.entity.SectionMainCust;

public interface IFinancialOperateReportService {

	List<GrossProfitRateOverview> getEverySectionRateOverview(Date startDate, Date endDate, String productType);

	MainCustResult<List<SectionMainCust>> getSectionMainCusts(Date startDate, Date endDate, GrossProfitRateSection section, String productType);

	MainCustResult<List<NegativeGrossProfitCust>> getNegativeGrossProfitCusts(Date startDate, Date endDate, String productType);

	BaseResponse<Integer> getSmallCustCount(Date startDate, Date endDate, String productType);

}