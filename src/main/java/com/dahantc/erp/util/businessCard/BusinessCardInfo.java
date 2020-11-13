package com.dahantc.erp.util.businessCard;

import java.io.Serializable;

/**
 * 名片识别出来的信息
 *
 * @author 8520
 */
public class BusinessCardInfo implements Serializable {
    private static final long serialVersionUID = -173719002100576289L;
    /**
     * 地址
     */
    private String address;
    /**
     * 传真
     */
    private String fax;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 姓名
     */
    private String name;
    /**
     * 邮编
     */
    private String pc;
    /**
     * 网址
     */
    private String url;
    /**
     * 电话
     */
    private String tel;
    /**
     * 公司
     */
    private String company;
    /**
     * 称呼
     */
    private String title;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 名片原件路径
     */
    private String filePath;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
