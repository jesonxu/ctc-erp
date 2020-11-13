package com.dahantc.erp.vo.bankAccount.vo;

import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;

import java.io.Serializable;

/**
 * 银行信息 传到前端
 *
 * @author 8520
 */
public class BankInfoVo implements Serializable {
    private static final long serialVersionUID = -9046177717263929035L;

    private String id;

    private String accountName;

    private String accountBank;

    private String bankAccount;

    private String companyAddress;

    public BankInfoVo() {
    }

    public BankInfoVo(BankAccount bankAccount) {
        this.id = bankAccount.getBankAccountId();
        this.accountName = bankAccount.getAccountName();
        this.accountBank = bankAccount.getAccountBank();
        this.bankAccount = bankAccount.getBankAccount();
        this.companyAddress = bankAccount.getCompanyAddress();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
