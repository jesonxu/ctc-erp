package com.dahantc.erp.dto.contact;

import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 添加联系人参数
 *
 * @author 8520
 */
public class AddContactDto implements Serializable {

    private String contactId;

    /**
     * 供应商/客户id
     */
    @NotNull(message = "客户/供应商必须选择")
    private String entityId;

    /**
     * 部门名称
     */
    @NotNull(message = "部门名称不能为空")
    private String deptName;

    /**
     * 联系人姓名
     */
    @NotNull(message = "联系人不能为空")
    private String contactsName;

    /**
     * 职位
     */
    @NotNull(message = "职务不能为空")
    private String post;

    /**
     * 手机1
     */
    private String firstPhone;

    /**
     * 手机2
     */
    private String secondPhone;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;

    /**
     * 微信
     */
    private String wx;

    /**
     * QQ
     */
    private String qq;
    /**
     * 名片路径
     */
    private String businessCardPath;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getFirstPhone() {
        return firstPhone;
    }

    public void setFirstPhone(String firstPhone) {
        this.firstPhone = firstPhone;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getBusinessCardPath() {
        return businessCardPath;
    }

    public void setBusinessCardPath(String businessCardPath) {
        this.businessCardPath = businessCardPath;
    }

    public SupplierContacts getContactInfo() {
        SupplierContacts supplierContacts = new SupplierContacts();
        supplierContacts.setSupplierContactsId(this.contactId);
        supplierContacts.setDeptName(this.deptName);
        supplierContacts.setPost(this.post);
        supplierContacts.setContactsName(this.contactsName);
        supplierContacts.setWx(this.wx);
        supplierContacts.setSecondPhone(this.secondPhone);
        supplierContacts.setQq(this.qq);
        supplierContacts.setFirstPhone(this.firstPhone);
        supplierContacts.setTelephone(this.telephone);
        supplierContacts.setEmail(this.email);
        supplierContacts.setSupplierId(this.entityId);
        supplierContacts.setRemark(this.remark);
        supplierContacts.setBusinessCardPath(this.businessCardPath);
        return supplierContacts;
    }
}
