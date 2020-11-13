package com.dahantc.erp.dto.operate;

import java.io.Serializable;

/**
 * 梯度价格
 * @author 8520
 */
public class GradientPriceDto implements Serializable {

    private static final long serialVersionUID = -5268630854311480538L;
    /**
     * 最大发送量（梯度、阶梯）
     */
    private Long maxsend;
    /**
     * 省占比（梯度、阶梯）
     */
    private Double provinceproportion;
    /**
     *投诉比例（梯度、阶梯）
     */
    private Double complaintrate;
    /**
     * 价格（都有）
     */
    private Double price;
    /**
     * 梯度（梯度、阶梯）
     */
    private String gradient;
    /**
     * 是否为默认（梯度、阶梯）
     */
    private Integer isdefault;
    /**
     * 最小发送量（梯度、阶梯）
     */
    private Long minsend;

    /**
     * 省网价格（统一价）
     */
    private Double provinceprice;

    public Long getMaxsend() {
        return maxsend;
    }

    public void setMaxsend(Long maxsend) {
        this.maxsend = maxsend;
    }

    public Double getProvinceproportion() {
        return provinceproportion;
    }

    public void setProvinceproportion(Double provinceproportion) {
        this.provinceproportion = provinceproportion;
    }

    public Double getComplaintrate() {
        return complaintrate;
    }

    public void setComplaintrate(Double complaintrate) {
        this.complaintrate = complaintrate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getGradient() {
        return gradient;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }

    public Integer getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(Integer isdefault) {
        this.isdefault = isdefault;
    }

    public Long getMinsend() {
        return minsend;
    }

    public void setMinsend(Long minsend) {
        this.minsend = minsend;
    }

    public Double getProvinceprice() {
        return provinceprice;
    }

    public void setProvinceprice(Double provinceprice) {
        this.provinceprice = provinceprice;
    }

    @Override
    public String toString() {
        return "GradientPriceDto{" +
                "maxsend=" + maxsend +
                ", provinceproportion=" + provinceproportion +
                ", complaintrate=" + complaintrate +
                ", price=" + price +
                ", gradient='" + gradient + '\'' +
                ", isdefault=" + isdefault +
                ", minsend=" + minsend +
                ", provinceprice=" + provinceprice +
                '}';
    }
}
