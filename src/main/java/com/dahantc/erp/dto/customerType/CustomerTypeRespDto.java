package com.dahantc.erp.dto.customerType;

import com.dahantc.erp.vo.customerType.entity.CustomerType;

/**
 * 客户类型返回的实体
 * @author 8520
 */
public class CustomerTypeRespDto {

    /**
     * 类型id
     */
    private String customerTypeId;

    /**
     * 类型名称
     */
    private String customerTypeName;

    /**
     * 未处理事情
     */
    private Long flowEntCount;

    /**
     * 客户数量
     */
    private Integer customerCount;

    public CustomerTypeRespDto() {
    }

    public CustomerTypeRespDto(String customerTypeId, String customerTypeName, Long flowEntCount, Integer customerCount) {
        this.customerTypeId = customerTypeId;
        this.customerTypeName = customerTypeName;
        this.flowEntCount = flowEntCount;
        this.customerCount = customerCount;
    }


    public CustomerTypeRespDto(CustomerType customerType, Long flowEntCount, Integer customerCount) {
        this.customerTypeId = customerType.getCustomerTypeId();
        this.customerTypeName = customerType.getCustomerTypeName();
        this.flowEntCount = flowEntCount;
        this.customerCount = customerCount;
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

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    @Override
    public String toString() {
        return "CustomerTypeRespDto{" +
                "customerTypeId='" + customerTypeId + '\'' +
                ", customerTypeName='" + customerTypeName + '\'' +
                ", flowEntCount=" + flowEntCount +
                ", customerCount=" + customerCount +
                '}';
    }
}
