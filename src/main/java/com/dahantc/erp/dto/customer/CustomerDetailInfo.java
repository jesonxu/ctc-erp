package com.dahantc.erp.dto.customer;

import java.io.Serializable;

/**
 * 客户转移 客户信息
 */
public class CustomerDetailInfo implements Serializable {
    private String id;

    private String name;

    private String userName;

    private String deptName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return "CustomerDetailInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", deptName='" + deptName + '\'' +
                '}';
    }
}
