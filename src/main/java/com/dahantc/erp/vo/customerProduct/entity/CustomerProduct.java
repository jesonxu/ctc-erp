package com.dahantc.erp.vo.customerProduct.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_customer_product")
public class CustomerProduct implements Serializable {

	private static final long serialVersionUID = 7326073984381548985L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "productid", length = 32)
	private String productId;

	@Column(name = "productname", length = 255)
	private String productName;

	@Column(name = "account", length = 2000)
	private String account;

	@Column(name = "producttype")
	private int productType;

	@Column(name = "billtype")
	private int billType;

	@Column(name = "billcycle")
	private int billCycle;

	@Column(name = "settletype")
	private int settleType;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	@Column(name = "customerid", length = 32)
	private String customerId;

	@Column(name = "billtaskday")
	private int billTaskDay;

	@Column(name = "voiceunit")
	private int voiceUnit;

	@Column(name = "senddemo", length = 1000)
	private String sendDemo;

	/** 账期(月) */
	@Column(name = "billperiod", columnDefinition = "int default 1")
	private int billPeriod = 1;

	/**
	 * 当前产品的固定成本
	 */
	@Column(name = "productoperatefixedcost", columnDefinition = "decimal(19,2) default 0 comment '客户的固定运营成本'", precision = 19, scale = 2)
	private BigDecimal productOperateFixedCost = BigDecimal.ZERO;

	/**
	 * 当前产品的均摊成本
	 */
	@Column(name = "productoperatesinglecost", columnDefinition = "decimal(19,6) default 0 comment '产品的单条运营成本单价'", precision = 19, scale = 6)
	private BigDecimal productOperateSingleCost = BigDecimal.ZERO;

	/**
	 * 当前产品的账单比例运营成本的比例
	 */
	@Column(name = "billmoneyratio", columnDefinition = "decimal(19,6) default 0 comment '账单金额比例运营成本的比例'", precision = 19, scale = 6)
	private BigDecimal billMoneyRatio = BigDecimal.ZERO;

	// 当前产品的毛利润比例运营成本的比例
	@Column(name = "billgrossprofitratio", columnDefinition = "decimal(19,6) default 0 comment '毛利润比例运营成本的比例'", precision = 19, scale = 6)
	private BigDecimal billGrossProfitRatio = BigDecimal.ZERO;

	// 第一次产生账单的时间
	@Column(name = "firstgeneratebilltime")
	private Timestamp firstGenerateBillTime;

	@Column(name = "costremark", length = 255)
	private String costRemark;

	/** 是否直连客户 */
	@Column(name = "directconnect", columnDefinition = "tinyint(1) default 0")
	private Boolean directConnect = false;

	/** 运营商发送范围 */
	@Column(name = "yystype", columnDefinition = "varchar(255) default '1000'")
	private String yysType = "1000";

	public Timestamp getFirstGenerateBillTime() {
		return firstGenerateBillTime;
	}

	public void setFirstGenerateBillTime(Timestamp firstGenerateBillTime) {
		this.firstGenerateBillTime = firstGenerateBillTime;
	}

	public String getCostRemark() {
		return costRemark;
	}

	public void setCostRemark(String costRemark) {
		this.costRemark = costRemark;
	}

	public BigDecimal getProductOperateFixedCost() {
		return productOperateFixedCost;
	}

	public void setProductOperateFixedCost(BigDecimal productOperateFixedCost) {
		this.productOperateFixedCost = productOperateFixedCost;
	}

	public BigDecimal getProductOperateSingleCost() {
		return productOperateSingleCost;
	}

	public void setProductOperateSingleCost(BigDecimal productOperateSingleCost) {
		this.productOperateSingleCost = productOperateSingleCost;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getBillType() {
		return billType;
	}

	public void setBillType(int billType) {
		this.billType = billType;
	}

	public int getBillCycle() {
		return billCycle;
	}

	public void setBillCycle(int billCycle) {
		this.billCycle = billCycle;
	}

	public int getSettleType() {
		return settleType;
	}

	public void setSettleType(int settleType) {
		this.settleType = settleType;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getBillTaskDay() {
		return billTaskDay;
	}

	public void setBillTaskDay(int billTaskDay) {
		this.billTaskDay = billTaskDay;
	}

	public int getVoiceUnit() {
		return voiceUnit;
	}

	public void setVoiceUnit(int voiceUnit) {
		this.voiceUnit = voiceUnit;
	}

	public String getSendDemo() {
		return sendDemo;
	}

	public void setSendDemo(String sendDemo) {
		this.sendDemo = sendDemo;
	}

	public int getBillPeriod() {
		return billPeriod;
	}

	public void setBillPeriod(int billPeriod) {
		this.billPeriod = billPeriod;
	}

	public Boolean getDirectConnect() {
		return directConnect;
	}

	public void setDirectConnect(Boolean directConnect) {
		this.directConnect = directConnect;
	}

	public BigDecimal getBillMoneyRatio() {
		return billMoneyRatio;
	}

	public void setBillMoneyRatio(BigDecimal billMoneyRatio) {
		this.billMoneyRatio = billMoneyRatio;
	}

	public BigDecimal getBillGrossProfitRatio() {
		return billGrossProfitRatio;
	}

	public void setBillGrossProfitRatio(BigDecimal billGrossProfitRatio) {
		this.billGrossProfitRatio = billGrossProfitRatio;
	}

	public String getYysType() {
		return yysType;
	}

	public void setYysType(String yysType) {
		this.yysType = yysType;
	}

	@Override
	public String toString() {
		return "CustomerProduct{" + "productId='" + productId + '\'' + ", productName='" + productName + '\'' + ", account='" + account + '\''
				+ ", productType=" + productType + ", billType=" + billType + ", billCycle=" + billCycle + ", settleType=" + settleType + ", ossUserId='"
				+ ossUserId + '\'' + ", customerId='" + customerId + '\'' + ", billTaskDay=" + billTaskDay + ", voiceUnit=" + voiceUnit + ", sendDemo='"
				+ sendDemo + '\'' + ", billPeriod=" + billPeriod + ", directConnect=" + directConnect + '}';
	}
}
