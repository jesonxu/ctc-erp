package com.dahantc.erp.vo.modifyPrice.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_modify_price")
@DynamicUpdate(true)
public class ModifyPrice implements Serializable {

	private static final long serialVersionUID = -8755041475348907375L;
	/**
	 * 主键id
	 */
	@Id
	@Column(name = "modifypriceid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String modifyPriceId;

	@Column(name = "flowentid", length = 32)
	private String flowEntId;

	/**
	 * 供应商id
	 */
	@Column(name = "entityid", length = 32)
	private String entityId;

	/**
	 * 产品id
	 */
	@Column(name = "productid", length = 32)
	private String productId;

	/**
	 * 流程发起人
	 */
	@Column(name = "createrid", length = 32)
	private String createrId;

	/**
	 * 价格类型（1-统一价，2-阶段价，3-阶梯价）
	 */
	@Column(name = "pricetype", columnDefinition = "int default 1")
	private int priceType;

	/**
	 * 单位
	 */
	@Column(name = "unit")
	private int unit;

	/**
	 * 价格有效期起始
	 */
	@Column(name = "validitydatestart")
	private Timestamp validityDateStart = new Timestamp(System.currentTimeMillis());

	/**
	 * 价格有效期结束
	 */
	@Column(name = "validitydateend")
	private Timestamp validityDateEnd = new Timestamp(System.currentTimeMillis());

	/**
	 * 调价详情
	 */
	@Column(name = "modifypricedetail")
	private String modifyPriceDetail;

	/**
	 * 创建时间
	 */
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/**
	 * 备注
	 */
	@Column(name = "remark")
	private String remark;

	@Column(name = "entitytype")
	private int entityType;

	public String getCreaterId() {
		return createrId;
	}

	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getModifyPriceId() {
		return modifyPriceId;
	}

	public void setModifyPriceId(String modifyPriceId) {
		this.modifyPriceId = modifyPriceId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getPriceType() {
		return priceType;
	}

	public void setPriceType(int priceType) {
		this.priceType = priceType;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public Timestamp getValidityDateStart() {
		return validityDateStart;
	}

	public void setValidityDateStart(Timestamp validityDateStart) {
		this.validityDateStart = validityDateStart;
	}

	public Timestamp getValidityDateEnd() {
		return validityDateEnd;
	}

	public void setValidityDateEnd(Timestamp validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}

	public String getModifyPriceDetail() {
		return modifyPriceDetail;
	}

	public void setModifyPriceDetail(String modifyPriceDetail) {
		this.modifyPriceDetail = modifyPriceDetail;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}
}
