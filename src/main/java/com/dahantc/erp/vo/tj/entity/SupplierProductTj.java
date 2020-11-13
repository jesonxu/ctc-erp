package com.dahantc.erp.vo.tj.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.YysType;

@Entity
@Table(name = "erp_supplierproducttj")
public class SupplierProductTj implements Serializable {

	private static final long serialVersionUID = -1213820353172212361L;

	/** 编号 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "chanid", columnDefinition = "int default 0")
	private int chanId;

	@Column(name = "channelid", length = 32)
	private String channelId;

	@Column(name = "countrycode", length = 10)
	private String countryCode;

	@Column(name = "regionid", columnDefinition = "int default 0")
	private int regionId;

	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	@Column(name = "totalcount", columnDefinition = "bigint default 0")
	private long totalCount = 0;

	@Column(name = "successcount", columnDefinition = "bigint default 0")
	private long successCount = 0;

	@Column(name = "failcount", columnDefinition = "bigint default 0")
	private long failCount = 0;
	
	@Column(name = "yystype", columnDefinition = "int default 0")
	private int yysType = YysType.CMCC.ordinal();

	/** 统计时间 */
	private Timestamp statsDate;

	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

	public String getId() {
		return id;
	}

	public int getChanId() {
		return chanId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public int getRegionId() {
		return regionId;
	}

	public int getProductType() {
		return productType;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public long getFailCount() {
		return failCount;
	}

	public Timestamp getStatsDate() {
		return statsDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setChanId(int chanId) {
		this.chanId = chanId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public void setFailCount(long failCount) {
		this.failCount = failCount;
	}

	public void setStatsDate(Timestamp statsDate) {
		this.statsDate = statsDate;
	}

	public int getYysType() {
		return yysType;
	}

	public void setYysType(int yysType) {
		this.yysType = yysType;
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}
}
