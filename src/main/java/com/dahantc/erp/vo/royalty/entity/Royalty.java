package com.dahantc.erp.vo.royalty.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_royalty")
@DynamicUpdate(true)
public class Royalty implements Serializable {

	private static final long serialVersionUID = 947972915637638358L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * 销售ID
	 */
	@Column(name = "ossuserid", length = 32)
	private String ossuserid;

	/**
	 * 供应商或客户id
	 */
	@Column(name = "entityid", length = 32)
	private String entityid;

	/**
	 * 产品id
	 */
	@Column(name = "productid", length = 32)
	private String productid;

	/**
	 * 产品类型
	 */
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	/**
	 * 毛利润额
	 */
	@Column(name = "profit")
	private BigDecimal profit = new BigDecimal(0);

	/**
	 * 提成金额
	 */
	@Column(name = "royalty")
	private BigDecimal royalty = new BigDecimal(0);

	@Column(name = "deptid", length = 32)
	private String deptId;

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/**
	 * 成功数
	 */
	@Column(name = "successcount", columnDefinition = "bigint default 0")
	private long successCount = 0;

	/**
	 * 总数
	 */
	@Column(name = "totalcount", columnDefinition = "bigint default 0")
	private long totalCount = 0;

	public String getId() {
		return id;
	}

	public String getOssuserid() {
		return ossuserid;
	}

	public void setOssuserid(String ossuserid) {
		this.ossuserid = ossuserid;
	}

	public String getEntityid() {
		return entityid;
	}

	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getRoyalty() {
		return royalty;
	}

	public void setRoyalty(BigDecimal royalty) {
		this.royalty = royalty;
	}
	
	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}
