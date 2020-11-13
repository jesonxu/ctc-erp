package com.dahantc.erp.vo.customerStatistics.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.BusinessType;

@Entity
@Table(name = "erp_customer_statistics", indexes = { @Index(name = "cust_ss_saleuserid", columnList = "saleUserId"),
		@Index(name = "cust_ss_statsdate", columnList = "statsDate"), @Index(name = "cust_ss_custid", columnList = "customerId"),
		@Index(name = "cust_ss_statsyearmonth", columnList = "statsYearMonth"), @Index(name = "cust_ss_producttype", columnList = "productType"),
		@Index(name = "cust_ss_custid_statsyearmonth", columnList = "customerId,statsYearMonth") })
@DynamicUpdate(true)
public class CustomerStatistics implements Serializable {
	private static final long serialVersionUID = 6955397092270562759L;

	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	// 所属销售id
	@Column(name = "saleuserid", length = 32)
	private String saleUserId;

	// 客户id
	@Column(name = "customerid", length = 32)
	private String customerId;

	// 客户类型id
	@Column(name = "customertypeid", length = 32)
	private String customerTypeId;

	// 产品id
	@Column(name = "productid", length = 32)
	private String productId;

	// 产品类型
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	// 客户所属销售的部门
	@Column(name = "deptid", length = 32)
	private String deptId;

	// 统计时间，yyyy
	@Column(name = "statsyear")
	private Date statsYear;

	// 统计时间，yyyy-MM
	@Column(name = "statsyearmonth")
	private Date statsYearMonth;

	// 统计时间，yyyy-MM-dd
	@Column(name = "statsdate")
	private Date statsDate;

	// 产品下所有账号的发送量
	@Column(name = "totalcount", columnDefinition = "bigint default 0")
	private long totalCount = 0;

	// 产品下所有账号的计算销售额的成功数
	@Column(name = "successcount", columnDefinition = "bigint default 0")
	private long successCount = 0;

	// 产品下所有账号的权益成功数
	@Column(name = "totalsuccessCount", columnDefinition = "bigint default 0")
	private long totalSuccessCount = 0;

	// 产品下所有账号的没有客户产品单价的成功数，不用来算销售单价
	@Column(name = "nocustpricecount", columnDefinition = "bigint default 0")
	private long noCustPriceCount = 0;

	// 产品下所有账号的不算成本的成功数，由于该条统计记录没有成本单价
	@Column(name = "nocostpricecount", columnDefinition = "bigint default 0")
	private long noCostPriceCount = 0;

	// 产品下所有账号的失败数
	@Column(name = "failcount", columnDefinition = "bigint default 0")
	private long failCount = 0;

	// 当天平均销售单价
	@Column(name = "custprice", columnDefinition = "decimal(19,6) default 0")
	private BigDecimal custPrice = new BigDecimal(0);

	// 产品下所有账号的应收
	@Column(name = "receivables")
	private BigDecimal receivables = new BigDecimal(0);

	// 当天平均成本单价
	@Column(name = "costprice", columnDefinition = "decimal(19,6) default 0")
	private BigDecimal costPrice = new BigDecimal(0);

	// 产品下所有账号的成本
	@Column(name = "cost")
	private BigDecimal cost = new BigDecimal(0);

	// 产品下所有账号的毛利润
	@Column(name = "grossprofit")
	private BigDecimal grossProfit = new BigDecimal(0);

	// 客户类型id
	@Column(name = "loginname", length = 2000)
	private String loginName;

	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSaleUserId() {
		return saleUserId;
	}

	public void setSaleUserId(String saleUserId) {
		this.saleUserId = saleUserId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(String customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Date getStatsYear() {
		return statsYear;
	}

	public void setStatsYear(Date statsYear) {
		this.statsYear = statsYear;
	}

	public Date getStatsYearMonth() {
		return statsYearMonth;
	}

	public void setStatsYearMonth(Date statsYearMonth) {
		this.statsYearMonth = statsYearMonth;
	}

	public Date getStatsDate() {
		return statsDate;
	}

	public void setStatsDate(Date statsDate) {
		this.statsDate = statsDate;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getTotalSuccessCount() {
		return totalSuccessCount;
	}

	public void setTotalSuccessCount(long totalSuccessCount) {
		this.totalSuccessCount = totalSuccessCount;
	}

	public long getNoCustPriceCount() {
		return noCustPriceCount;
	}

	public void setNoCustPriceCount(long noCustPriceCount) {
		this.noCustPriceCount = noCustPriceCount;
	}

	public long getNoCostPriceCount() {
		return noCostPriceCount;
	}

	public void setNoCostPriceCount(long noCostPriceCount) {
		this.noCostPriceCount = noCostPriceCount;
	}

	public long getFailCount() {
		return failCount;
	}

	public void setFailCount(long failCount) {
		this.failCount = failCount;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public BigDecimal getCustPrice() {
		return custPrice;
	}

	public void setCustPrice(BigDecimal custPrice) {
		this.custPrice = custPrice;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}

	public void addCost(BigDecimal cost) {
		setCost(this.cost.add(cost));
	}

	public void addReceivables(BigDecimal receivables) {
		setReceivables(this.receivables.add(receivables));
	}

	public void addGrossProfit(BigDecimal grossProfit) {
		setGrossProfit(this.grossProfit.add(grossProfit));
	}

	public void addSuccessCount(long successCount) {
		setSuccessCount(this.successCount + successCount);
	}

	public void addTotalSuccessCount(long totalSuccessCount) {
		setTotalSuccessCount(this.totalSuccessCount + totalSuccessCount);
	}

	public void addNoCustPriceCount(long noCustPriceCount) {
		setNoCustPriceCount(this.noCustPriceCount + noCustPriceCount);
	}

	public void addNoCostPriceCount(long noCostPriceCount) {
		setNoCostPriceCount(this.noCostPriceCount + noCostPriceCount);
	}

	public void addFailCount(long failCount) {
		setFailCount(this.failCount + failCount);
	}

	public void addTotalCount(long totalCount) {
		setTotalCount(this.totalCount + totalCount);
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	public void setScale2() {
		setCost(cost.setScale(2, BigDecimal.ROUND_HALF_UP));
		setReceivables(receivables.setScale(2, BigDecimal.ROUND_HALF_UP));
		setGrossProfit(grossProfit.setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}
