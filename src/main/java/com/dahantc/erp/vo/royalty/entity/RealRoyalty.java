package com.dahantc.erp.vo.royalty.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_real_royalty", indexes = { @Index(name = "real_royalty_wtime", columnList = "wtime") })
@DynamicUpdate(true)
public class RealRoyalty implements Serializable {

	private static final long serialVersionUID = 947972915637638358L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * 部门id
	 */
	@Column(name = "deptid", length = 32)
	private String deptId;

	/**
	 * 销售id
	 */
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	/**
	 * 供应商或客户id
	 */
	@Column(name = "entityid", length = 32)
	private String entityId;

	/**
	 * 产品id
	 */
	@Column(name = "productid", length = 32)
	private String productId;

	/**
	 * 产品类型
	 */
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	/**
	 * 产品id
	 */
	@Column(name = "billid", length = 32)
	private String billId;

	/**
	 * 产品id
	 */
	@Column(name = "billnumber", length = 32)
	private String billNumber;

	/**
	 * 发送量
	 */
	@Column(name = "sendcount", columnDefinition = "bigint default 0")
	private long sendCount = 0;

	/**
	 * 账单金额
	 */
	@Column(name = "billmoney", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '账单金额'")
	private BigDecimal billMoney = new BigDecimal(0);

	/**
	 * 毛利润
	 */
	@Column(name = "grossprofit", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '毛利润'")
	private BigDecimal grossProfit = new BigDecimal(0);

	/**
	 * 运营成本
	 */
	@Column(name = "operatecost", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '运营成本'")
	private BigDecimal operateCost = new BigDecimal(0);

	/**
	 * 利润
	 */
	@Column(name = "profit", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '利润'")
	private BigDecimal profit = new BigDecimal(0);

	/**
	 * 利润提成（不是最终的提成）
	 */
	@Column(name = "royalty", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '利润提成'")
	private BigDecimal royalty = new BigDecimal(0);

	/**
	 * 账单罚息
	 */
	@Column(name = "penaltyinterest", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '账单罚息'")
	private BigDecimal penaltyInterest = new BigDecimal(0);

	/**
	 * 创建时间
	 */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getId() {
		return id;
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

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
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

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
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

	public BigDecimal getPenaltyInterest() {
		return penaltyInterest;
	}

	public void setPenaltyInterest(BigDecimal penaltyInterest) {
		this.penaltyInterest = penaltyInterest;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}
}
