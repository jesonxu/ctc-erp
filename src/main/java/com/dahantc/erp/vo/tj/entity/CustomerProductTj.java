package com.dahantc.erp.vo.tj.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "erp_customerproducttj")
public class CustomerProductTj implements Serializable {

	private static final long serialVersionUID = 738811642135814240L;

	/** 编号 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "loginname", length = 255)
	private String loginName;

	@Column(name = "bcustid", length = 32)
	private String bcustId;

	@Column(name = "chanid", columnDefinition = "int default 0")
	private int chanId;

	@Column(name = "channelid", length = 32)
	private String channelId;

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

	@Column(name = "countrycode", length = 10)
	private String countryCode;

	/** 统计时间 */
	private Timestamp statsDate;

	@Column(name = "businesstype", columnDefinition = "int default 0")
	private int businessType = BusinessType.YTX.ordinal();

	@Column(name = "costprice", precision = 19, scale = 6)
	private BigDecimal costPrice;

	@Column(name = "datasource", length = 3)
	private String dataSource;

	public String getId() {
		return id;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getBcustId() {
		return bcustId;
	}

	public int getChanId() {
		return chanId;
	}

	public String getChannelId() {
		return channelId;
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

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setBcustId(String bcustId) {
		this.bcustId = bcustId;
	}

	public void setChanId(int chanId) {
		this.chanId = chanId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
}
