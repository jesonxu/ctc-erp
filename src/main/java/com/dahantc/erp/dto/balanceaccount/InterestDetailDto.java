package com.dahantc.erp.dto.balanceaccount;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 计息详情
 * @author 8520
 */
public class InterestDetailDto {
    /**
     * 时间（到天）
     */
    @JsonFormat(pattern = "yyyy年MM月dd日")
    private Date time;

    /**
     * 剩余金额
     */
    private BigDecimal leftMoney;

    /**
     * 计息
     */
    private BigDecimal interest;
    /**
     * 计息率
     */
    private BigDecimal rate;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public BigDecimal getLeftMoney() {
        return leftMoney;
    }

    public void setLeftMoney(BigDecimal leftMoney) {
        this.leftMoney = leftMoney;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
