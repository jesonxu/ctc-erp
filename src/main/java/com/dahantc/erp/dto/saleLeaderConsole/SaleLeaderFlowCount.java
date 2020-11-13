package com.dahantc.erp.dto.saleLeaderConsole;

import com.dahantc.erp.dto.customer.CustomerRespDto;
import com.dahantc.erp.dto.customerType.CustomerTypeRespDto;
import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.dto.product.ProductRespDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 销售管理未处理流程
 * 
 * @author 8520
 */
public class SaleLeaderFlowCount implements Serializable {

	private static final long serialVersionUID = -5026921426403680003L;
	/**
	 * 客户类型统计
	 */
	private List<CustomerTypeRespDto> customerTypeCount;
	/**
	 * 客户统计
	 */
	private List<CustomerRespDto> customerCount;
	/**
	 * 产品统计
	 */
	private List<ProductRespDto> productCount;
	/**
	 * 运营月份统计
	 */
	private Map<String, List<ToQueryMonthRespDto>> opearteMonthCount;
	/**
	 * 结算月份统计
	 **/
	private Map<String, List<ToQueryMonthRespDto>> settlementMonthCount;
	/**
	 * 运营年份统计
	 **/
	private Map<String, Long> operateYearCount;
	/**
	 * 结算年份统计
	 **/
	private Map<String, Long> settlementYearCount;

	public List<CustomerTypeRespDto> getCustomerTypeCount() {
		return customerTypeCount;
	}

	public void setCustomerTypeCount(List<CustomerTypeRespDto> customerTypeCount) {
		this.customerTypeCount = customerTypeCount;
	}

	public List<CustomerRespDto> getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(List<CustomerRespDto> customerCount) {
		this.customerCount = customerCount;
	}

	public List<ProductRespDto> getProductCount() {
		return productCount;
	}

	public void setProductCount(List<ProductRespDto> productCount) {
		this.productCount = productCount;
	}

	public Map<String, List<ToQueryMonthRespDto>> getOpearteMonthCount() {
		return opearteMonthCount;
	}

	public void setOpearteMonthCount(Map<String, List<ToQueryMonthRespDto>> opearteMonthCount) {
		this.opearteMonthCount = opearteMonthCount;
	}

	public Map<String, List<ToQueryMonthRespDto>> getSettlementMonthCount() {
		return settlementMonthCount;
	}

	public void setSettlementMonthCount(Map<String, List<ToQueryMonthRespDto>> settlementMonthCount) {
		this.settlementMonthCount = settlementMonthCount;
	}

	public Map<String, Long> getOperateYearCount() {
		return operateYearCount;
	}

	public void setOperateYearCount(Map<String, Long> operateYearCount) {
		this.operateYearCount = operateYearCount;
	}

	public Map<String, Long> getSettlementYearCount() {
		return settlementYearCount;
	}

	public void setSettlementYearCount(Map<String, Long> settlementYearCount) {
		this.settlementYearCount = settlementYearCount;
	}
}