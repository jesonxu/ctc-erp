package com.dahantc.erp.dto.search;

import java.io.Serializable;

public class SupplierSerchRespDto implements Serializable {

	private static final long serialVersionUID = -1293011107423091974L;

    //公司名称
    private String companyName;

    //供应商类型
    private String supplierType;

    //通讯地址
    private String postalAdress;

    //业务联系人
    private String contactName;

    //业务联系人手机
    private String contactPhone;

    //创建人
    private String createUser;

    //创建人部门
    private String createUserDept;

    //创建时间
    private String witme;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
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
