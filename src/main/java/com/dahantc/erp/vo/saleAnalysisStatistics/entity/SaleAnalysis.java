package com.dahantc.erp.vo.saleAnalysisStatistics.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.SettleType;

@Entity
@Table(name = "erp_saleanalysis_statistics")
@DynamicUpdate(true)
public class SaleAnalysis implements Serializable {
	private static final long serialVersionUID = 6955397092270562759L;

	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	// 客户id
	@Column(name = "customerid", length = 32)
	private String customerId;
	
	// 客户所属销售的部门
	@Column(name = "deptid", length = 32)
	private String deptId;

	
	// 产品类型
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;
	
	// 所属销售id
	@Column(name = "saleuserid", length = 32)
	private String saleUserId;
	
	// 到款金额
	@Column(name = "receivables")
	private BigDecimal receivables = BigDecimal.ZERO;

	// 成功数
	@Column(name = "successcount", columnDefinition = "bigint default 0")
	private long successCount = 0;
	
	// 销售单价
	@Column(name = "saleunitprice", precision = 19, scale = 6, columnDefinition = "decimal(19,6) default 0")
	private BigDecimal saleUnitPrice = BigDecimal.ZERO;
	
	// 消费金额
	@Column(name = "expenses")
	private BigDecimal expenses = BigDecimal.ZERO;
	
	// 成本单价
	@Column(name = "costunitprice", precision = 19, scale = 6, columnDefinition = "decimal(19,6) default 0")
	private BigDecimal costUnitPrice = BigDecimal.ZERO;
	
	// 成本金额
	@Column(name = "costsum")
	private BigDecimal costSum = BigDecimal.ZERO;
	
	// 毛利润
	@Column(name = "grossprofit")
	private BigDecimal grossProfit = BigDecimal.ZERO;

	// 权益提成
	@Column(name = "royalty")
	private BigDecimal royalty = BigDecimal.ZERO;
	
	// 结算方式
	@Column(name = "settletype", columnDefinition = "int default 0")
	private int settleType = SettleType.Prepurchase.ordinal();
	
	// 账期
	@Column(name = "accountperiod")
	private String accountPeriod;
	
	// 累计欠款
	@Column(name = "arrears")
	private BigDecimal arrears = BigDecimal.ZERO;
	
	// 客户余额
	@Column(name = "currentbalance")
	private BigDecimal currentbalance = BigDecimal.ZERO;
	
	// 销售费用
	@Column(name = "sellingexpenses")
	private BigDecimal sellingExpenses = BigDecimal.ZERO;
	
	// 修正毛利润
	@Column(name = "correctgrossprofit")
	private BigDecimal correctGrossprofit = BigDecimal.ZERO;

	// 统计时间，yyyy
	@Column(name = "statsyear")
	private Date statsYear;

	// 统计时间，yyyy-MM
	@Column(name = "statsyearmonth")
	private Date statsYearMonth;

	// 统计时间，yyyy-MM-dd
	@Column(name = "statsdate")
	private Date statsDate;

	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getSaleUserId() {
		return saleUserId;
	}

	public void setSaleUserId(String saleUserId) {
		this.saleUserId = saleUserId;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public BigDecimal getSaleUnitPrice() {
		return saleUnitPrice;
	}

	public void setSaleUnitPrice(BigDecimal saleUnitPrice) {
		this.saleUnitPrice = saleUnitPrice;
	}

	public BigDecimal getExpenses() {
		return expenses;
	}

	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}

	public BigDecimal getCostUnitPrice() {
		return costUnitPrice;
	}

	public void setCostUnitPrice(BigDecimal costUnitPrice) {
		this.costUnitPrice = costUnitPrice;
	}

	public BigDecimal getCostSum() {
		return costSum;
	}

	public void setCostSum(BigDecimal costSum) {
		this.costSum = costSum;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getRoyalty() {
		return royalty;
	}

	public void setRoyalty(BigDecimal royalty) {
		this.royalty = royalty;
	}

	public int getSettleType() {
		return settleType;
	}

	public void setSettleType(int settleType) {
		this.settleType = settleType;
	}

	public String getAccountPeriod() {
		return accountPeriod;
	}

	public void setAccountPeriod(String accounTperiod) {
		this.accountPeriod = accounTperiod;
	}

	public BigDecimal getArrears() {
		return arrears;
	}

	public void setArrears(BigDecimal arrears) {
		this.arrears = arrears;
	}

	public BigDecimal getCurrentbalance() {
		return currentbalance;
	}

	public void setCurrentbalance(BigDecimal currentbalance) {
		this.currentbalance = currentbalance;
	}

	public BigDecimal getSellingExpenses() {
		return sellingExpenses;
	}

	public void setSellingExpenses(BigDecimal sellingExpenses) {
		this.sellingExpenses = sellingExpenses;
	}

	public BigDecimal getCorrectGrossprofit() {
		return correctGrossprofit;
	}

	public void setCorrectGrossprofit(BigDecimal correctGrossprofit) {
		this.correctGrossprofit = correctGrossprofit;
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

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}
}
