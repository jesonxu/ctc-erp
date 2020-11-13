package com.dahantc.erp.dto.search;

import java.io.Serializable;

public class CustomerSearchRespDto implements Serializable {

    private static final long serialVersionUID = 7481245662006928606L;

    //公司名称
    private String companyName;

    //客户类型
    private String customerType;

    //通讯地址
    private String postalAdress;

    //业务联系人
    private String contactName;

    //联系手机
    private String contactPhone;

    //创建人
    private String createUser;

    //创建人部门
    private String createUserDept;

    //创建时间
    private String witme;

    //电子邮件
    private String email;

    //城市
    private String region;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getPostalAdress() {
        return postalAdress;
    }

    public void setPostalAdress(String postalAdress) {
        this.postalAdress = postalAdress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserDept() {
        return createUserDept;
    }

    public void setCreateUserDept(String createUserDept) {
        this.createUserDept = createUserDept;
    }

    public String getWitme() {
        return witme;
    }

    public void setWitme(String witme) {
        this.witme = witme;
    }
}
