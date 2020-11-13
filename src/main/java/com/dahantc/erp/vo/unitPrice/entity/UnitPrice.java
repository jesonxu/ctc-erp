package com.dahantc.erp.vo.unitPrice.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.EntityType;

/**
 *
 */
@Entity
@Table(name = "erp_unit_price")
public class UnitPrice implements Serializable {

	private static final long serialVersionUID = -6272729086761406871L;

	/**
	 * id
	 */
	@Id
	@Column(length = 32, name = "unitpriceid")
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String unitPriceId;

	/**
	 * 实体类型
	 */
	@Column(name = "entitytype", columnDefinition = "int default 0")
	private int entityType = EntityType.SUPPLIER.ordinal();

	/**
	 * 供应商产品Id 或 客户产品Id
	 */
	@Column(name = "basicsid", length = 32)
	private String basicsId;

	/**
	 * 月度平均单价
	 */
	@Column(name = "unitprice", precision = 19, scale = 6, columnDefinition = "decimal(19,6) default 0")
	private BigDecimal unitPrice = new BigDecimal(0);

	/**
	 * countrycode
	 */
	@Column(name = "countrycode")
	private String countryCode;

	/**
	 * 生效时间：月份
	 */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getUnitPriceId() {
		return unitPriceId;
	}

	public void setUnitPriceId(String unitPriceId) {
		this.unitPriceId = unitPriceId;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public String getBasicsId() {
		return basicsId;
	}

	public void setBasicsId(String basicsId) {
		this.basicsId = basicsId;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
