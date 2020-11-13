package com.dahantc.erp.dto.realRoyalty;

import java.math.BigDecimal;

/**
 * 销售实际提成表，数据来源是月已销账账单（包含客户维度、账单维度全部字段，根据需要部分展示即可）
 *
 */
public class RealRoyaltyDto {
	/**
	 * 部门
	 */
	private String deptName;

	/**
	 * 销售
	 */
	private String saleName;

	/**
	 * 客户id
	 */
	private String customerId;

	/**
	 * 客户名称
	 */
	private String companyName;

	/**
	 * 产品id
	 */
	private String productId;

	/**
	 * 产品名称
	 */
	private String productName;

	/**
	 * 结算方式
	 */
	private String settleType;

	/**
	 * 账单Id
	 */
	private String billId;

	/**
	 * 账单编号
	 */
	private String billNumber;

	/**
	 * 发送量
	 */
	private long sendCount = 0L;

	/**
	 * 账单金额
	 */
	private BigDecimal billMoney = BigDecimal.ZERO;

	/**
	 * 账单毛利润
	 */
	private BigDecimal grossProfit = BigDecimal.ZERO;

	/**
	 * 运营成本
	 */
	private BigDecimal operateCost = BigDecimal.ZERO;

	/**
	 * 利润
	 */
	private BigDecimal profit = BigDecimal.ZERO;

	/**
	 * 利润提成
	 */
	private BigDecimal royalty = BigDecimal.ZERO;

	/**
	 * 计息金额（客户层面）
	 */
	private BigDecimal balanceInterest = BigDecimal.ZERO;

	/**
	 * 罚息金额（账单层面）
	 */
	private BigDecimal penaltyInterest = BigDecimal.ZERO;

	/**
	 * 实际提成
	 */
	private BigDecimal realRoyalty = BigDecimal.ZERO;

	/**
	 * 销账时间
	 */
	private String writeOffTime;

	private String incomeName;

	private String incomeDate;

	private BigDecimal thisCost = BigDecimal.ZERO;

	private BigDecimal incomeCost = BigDecimal.ZERO;

	public RealRoyaltyDto() {

	}

	public RealRoyaltyDto(String deptName, String saleName, String customerId, String companyName, String productId, String productName, String settleType,
			String billId, String billNumber, long sendCount, BigDecimal billMoney, BigDecimal grossProfit, BigDecimal operateCost, BigDecimal profit,
			BigDecimal royalty, BigDecimal balanceInterest, BigDecimal penaltyInterest, BigDecimal realRoyalty, String writeOffTime, String incomeName,
			String incomeDate, BigDecimal thisCost, BigDecimal incomeCost) {
		this.deptName = deptName;
		this.saleName = saleName;
		this.customerId = customerId;
		this.companyName = companyName;
		this.productId = productId;
		this.productName = productName;
		this.settleType = settleType;
		this.billId = billId;
		this.billNumber = billNumber;
		this.sendCount = sendCount;
		this.billMoney = billMoney;
		this.grossProfit = grossProfit;
		this.operateCost = operateCost;
		this.profit = profit;
		this.royalty = royalty;
		this.balanceInterest = balanceInterest;
		this.penaltyInterest = penaltyInterest;
		this.realRoyalty = realRoyalty;
		this.writeOffTime = writeOffTime;
		this.incomeName = incomeName;
		this.incomeDate = incomeDate;
		this.thisCost = thisCost;
		this.incomeCost = incomeCost;
	}

	public BigDecimal getIncomeCost() {
		return incomeCost;
	}

	public void setIncomeCost(BigDecimal incomeCost) {
		this.incomeCost = incomeCost;
	}

	public String getIncomeName() {
		return incomeName;
	}

	public void setIncomeName(String incomeName) {
		this.incomeName = incomeName;
	}

	public String getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(String incomeDate) {
		this.incomeDate = incomeDate;
	}

	public BigDecimal getThisCost() {
		return thisCost;
	}

	public void setThisCost(BigDecimal thisCost) {
		this.thisCost = thisCost;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public long getSendCount() {
		return sendCount;
	}

	public void setSendCount(long sendCount) {
		this.sendCount = sendCount;
	}

	public BigDecimal getBillMoney() {
		return billMoney;
	}

	public void setBillMoney(BigDecimal billMoney) {
		this.billMoney = billMoney;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getOperateCost() {
		return operateCost;
	}

	public void setOperateCost(BigDecimal operateCost) {
		this.operateCost = operateCost;
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

	public BigDecimal getBalanceInterest() {
		return balanceInterest;
	}

	public void setBalanceInterest(BigDecimal balanceInterest) {
		this.balanceInterest = balanceInterest;
	}

	public BigDecimal getPenaltyInterest() {
		return penaltyInterest;
	}

	public void setPenaltyInterest(BigDecimal penaltyInterest) {
		this.penaltyInterest = penaltyInterest;
	}

	public BigDecimal getRealRoyalty() {
		return realRoyalty;
	}

	public void setRealRoyalty(BigDecimal realRoyalty) {
		this.realRoyalty = realRoyalty;
	}

	public void addBillMoney(BigDecimal billMoney) {
		setBillMoney(this.getBillMoney().add(billMoney));
	}

	public void addSendCount(long sendCount) {
		setSendCount(this.getSendCount() + sendCount);
	}

	public void addGrossProfit(BigDecimal grossProfit) {
		setGrossProfit(this.getGrossProfit().add(grossProfit));
	}

	public void addOperateCost(BigDecimal operateCost) {
		setOperateCost(this.getOperateCost().add(operateCost));
	}

	public void addProfit(BigDecimal profit) {
		setProfit(this.getProfit().add(profit));
	}

	public void addRoyalty(BigDecimal royalty) {
		setRoyalty(this.getRoyalty().add(royalty));
	}

	public void addBalanceInterest(BigDecimal balanceInterest) {
		setBalanceInterest(this.getBalanceInterest().add(balanceInterest));
	}

	public void addPenaltyInterest(BigDecimal penaltyInterest) {
		setPenaltyInterest(this.getPenaltyInterest().add(penaltyInterest));
	}

	public void addRealRoyalty(BigDecimal realRoyalty) {
		setRealRoyalty(this.getRealRoyalty().add(realRoyalty));
	}

	public String getWriteOffTime() {
		return writeOffTime;
	}

	public void setWriteOffTime(String writeOffTime) {
		this.writeOffTime = writeOffTime;
	}

	public RealRoyaltyDto clone() {
		return new RealRoyaltyDto(deptName, saleName, customerId, companyName, productId, productName, settleType, billId, billNumber, sendCount, billMoney,
				grossProfit, operateCost, grossProfit, royalty, balanceInterest, penaltyInterest, realRoyalty, writeOffTime, incomeName, incomeDate, thisCost,
				incomeCost);

	}

}
