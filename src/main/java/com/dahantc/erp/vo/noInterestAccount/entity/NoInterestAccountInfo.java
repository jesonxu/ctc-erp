package com.dahantc.erp.vo.noInterestAccount.entity;

import com.dahantc.erp.util.AccountConfigUtil;
import com.dahantc.erp.util.StringUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * 不计息账号信息
 * @author 8520
 */
public class NoInterestAccountInfo {

    private String account;

    private String[] desc;

    public NoInterestAccountInfo(String[] info) {
        this.account = info[0];
        if (info.length > 1) {
            this.desc = new String[info.length - 1];
            System.arraycopy(info, 1, this.desc, 0, info.length - 1);
        }
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String[] getDesc() {
        return desc;
    }

    public void setDesc(String[] desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoInterestAccountInfo that = (NoInterestAccountInfo) o;
        // 只比较 account
        return Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        // 只管 account
        return Objects.hash(account);
    }

    public String toAccountInfo() {
        String params = "";
        if (this.desc != null && this.desc.length > 0) {
            params += AccountConfigUtil.separator + String.join(AccountConfigUtil.separator, this.desc);
        }
        return this.account + params;
    }

    @Override
    public String toString() {
        return "NoInterestAccountInfo{" +
                "account='" + account + '\'' +
                ", desc=" + Arrays.toString(desc) +
                '}';
    }
}
