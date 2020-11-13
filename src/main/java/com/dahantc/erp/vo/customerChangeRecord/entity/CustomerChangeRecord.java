package com.dahantc.erp.vo.customerChangeRecord.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customerType.entity.CustomerType;

/**
 * 客户变更记录表
 * @author 8520
 */
@Entity
@Table(name = "erp_customer_change_record")
@DynamicUpdate(true)
public class CustomerChangeRecord implements Serializable {

    private static final long serialVersionUID = -5297514354953897227L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id" ,length = 32)
    private String id;

	/**
	 * 用户id
	 * @see Customer#getOssuserId() ;
	 */
    @Column(name = "ossuserid" ,length = 32)
    private String ossUserId;

    /**
     * 部门ID
     * @see Customer#getDeptId()
     */
    @Column(name = "deptid" ,length = 32)
    private String deptId;

    @Column(name = "depict" ,length = 1000)
    private String depict;

    @Column(name = "changetime")
    private Timestamp changeTime;

    /**
     * 客户id
     * @see Customer#getCustomerId()
     */
    @Column(name = "customerid" ,length = 32)
    private String customerId;

    /**
	 * 客户名
	 * @see Customer#getCompanyName()
	 */
    @Column(name = "companyname" ,length = 255)
    private String companyName;

    /**
     * 原来的客户类型
     * @see CustomerType#getCustomerTypeValue()
     */
    @Column(name = "origincustomertype")
    private Integer originCustomerType;

    /**
     * 变更后的客户类型
     * @see CustomerType#getCustomerTypeId()
     */
    @Column(name = "nowcustomertype")
    private Integer nowCustomerType;

    /***
     * @see com.dahantc.erp.enums.CustomerChangeType
     */
    @Column(name = "changetype")
    private Integer changeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOssUserId() {
        return ossUserId;
    }

    public void setOssUserId(String ossUserId) {
        this.ossUserId = ossUserId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public Timestamp getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Timestamp changeTime) {
        this.changeTime = changeTime;
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

    public Integer getOriginCustomerType() {
        return originCustomerType;
    }

    public void setOriginCustomerType(Integer originCustomerType) {
        this.originCustomerType = originCustomerType;
    }

    public Integer getNowCustomerType() {
        return nowCustomerType;
    }

    public void setNowCustomerType(Integer nowCustomerType) {
        this.nowCustomerType = nowCustomerType;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    @Override
    public String toString() {
        return "CustomerChangeRecord{" +
                "id='" + id + '\'' +
                ", ossUserId='" + ossUserId + '\'' +
                ", deptId='" + deptId + '\'' +
                ", depict='" + depict + '\'' +
                ", changeTime=" + changeTime +
                ", customerId='" + customerId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", originCustomerType=" + originCustomerType +
                ", nowCustomerType=" + nowCustomerType +
                ", changeType=" + changeType +
                '}';
    }
}
