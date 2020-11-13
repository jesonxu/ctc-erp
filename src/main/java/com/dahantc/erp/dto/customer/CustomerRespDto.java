package com.dahantc.erp.dto.customer;

import java.io.Serializable;

/**
 * 供应商返回信息
 * @author 8520
 */
public class CustomerRespDto implements Serializable {
	
	private static final long serialVersionUID = -7328420899050374892L;

	/**
     * 客户id
     */
    private String customerId;
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 客户类型id
     */
    private String customerTypeId;

    /**
     * 客户类型
     */
    private String customerTypeName;

    /**
     * 未处理事情数量
     */
    private Long flowEntCount;

    public CustomerRespDto() {
    }

    public CustomerRespDto(String customerId, String companyName, String customerTypeId, Long flowEntCount) {
        this.customerId = customerId;
        this.companyName = companyName;
        this.customerTypeId = customerTypeId;
        this.flowEntCount = flowEntCount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(String customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public String getCustomerTypeName() {
        return customerTypeName;
    }

    public void setCustomerTypeName(String customerTypeName) {
        this.customerTypeName = customerTypeName;
    }

    public Long getFlowEntCount() {
        return flowEntCount;
    }

    public void setFlowEntCount(Long flowEntCount) {
        this.flowEntCount = flowEntCount;
    }

    @Override
    public String toString() {
        return "CustomerRespDto{" +
                "customerId='" + customerId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", customerTypeId='" + customerTypeId + '\'' +
                ", flowEntCount=" + flowEntCount +
                '}';
    }
}
