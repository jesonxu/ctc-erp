package com.dahantc.erp.vo.deductionPrice.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_deduction_price")
@DynamicUpdate(true)
public class DeductionPrice implements Serializable {

	private static final long serialVersionUID = -2542468141172844536L;

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "deductionid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String deductionId;

	/**
	 * 调价表id
	 */
	@Column(name = "modifypriceid", length = 32)
	private String modifyPriceId;

	/**
	 * 单价
	 */
	@Column(name = "price", precision = 19, scale = 6, columnDefinition = "decimal(19,6) default 0")
	private BigDecimal price = BigDecimal.ZERO;
	/**
	 * 省网单价
	 */
	@Column(name = "provinceprice", precision = 19, scale = 6, columnDefinition = "decimal(19,6) default 0")
	private BigDecimal provincePrice = BigDecimal.ZERO;

	/**
	 * 最小发送量
	 */
	@Column(name = "minsend")
	private double minSend;

	/**
	 * 最大发送量
	 */
	@Column(name = "maxsend")
	private double maxSend;

	/**
	 * 省份占比
	 */
	@Column(name = "provinceproportion")
	private BigDecimal provinceProportion;

	/**
	 * 投诉率
	 */
	@Column(name = "complaintrate")
	private BigDecimal complaintRrate;

	/**
	 * 是否默认
	 */
	@Column(name = "isdefault", columnDefinition = "int default 0")
	public int isDefault;

	/**
	 * 梯度（记录梯度数）
	 */
	@Column(name = "gradient", columnDefinition = "int default 0")
	private int gradient;

	public String getDeductionId() {
		return deductionId;
	}

	public void setDeductionId(String deductionId) {
		this.deductionId = deductionId;
	}

	public String getModifyPriceId() {
		return modifyPriceId;
	}

	public void setModifyPriceId(String modifyPriceId) {
		this.modifyPriceId = modifyPriceId;
	}

	public double getMinSend() {
		return minSend;
	}

	public void setMinSend(double minSend) {
		this.minSend = minSend;
	}

	public double getMaxSend() {
		return maxSend;
	}

	public void setMaxSend(double maxSend) {
		this.maxSend = maxSend;
	}

	public BigDecimal getProvinceProportion() {
		return provinceProportion;
	}

	public void setProvinceProportion(BigDecimal provinceProportion) {
		this.provinceProportion = provinceProportion;
	}

	public BigDecimal getComplaintRrate() {
		return complaintRrate;
	}

	public void setComplaintRrate(BigDecimal complaintRrate) {
		this.complaintRrate = complaintRrate;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public int getGradient() {
		return gradient;
	}

	public void setGradient(int gradient) {
		this.gradient = gradient;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getProvincePrice() {
		return provincePrice;
	}

	public void setProvincePrice(BigDecimal provincePrice) {
		this.provincePrice = provincePrice;
	}

}
