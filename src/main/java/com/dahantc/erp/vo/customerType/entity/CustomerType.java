package com.dahantc.erp.vo.customerType.entity;

import com.dahantc.erp.enums.CustomerTypeValue;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicUpdate(true)
@Table(name = "erp_customer_type")
public class CustomerType implements Serializable {

	private static final long serialVersionUID = -4535018673927333909L;

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "customertypeid", length = 32)
    private String customerTypeId;

    /**
     * 用户类型名称
     */
    @Column(name = "customertypename")
    private String customerTypeName;

    /**
     * 客户类型值
     */
    @Column(name = "customertypevalue", columnDefinition = "int(11) default 3")
    private Integer customerTypeValue = CustomerTypeValue.INTENTION.getCode();

    /**
     * 排序号
     */
    @Column(name = "sequence")
    private Integer sequence;

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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getCustomerTypeValue() {
        return customerTypeValue;
    }

    public void setCustomerTypeValue(Integer customerTypeValue) {
        this.customerTypeValue = customerTypeValue;
    }

    @Override
    public String toString() {
        return "CustomerType{" +
                "customerTypeId='" + customerTypeId + '\'' +
                ", customerTypeName='" + customerTypeName + '\'' +
                ", customerTypeValue=" + customerTypeValue +
                ", sequence=" + sequence +
                '}';
    }
}
