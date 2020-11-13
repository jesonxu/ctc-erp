package com.dahantc.erp.vo.operateCost.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 运营成本表
 */
@Entity
@Table(name = "erp_operate_cost")
public class OperateCost implements Serializable {

	private static final long serialVersionUID = 471197609535424808L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", length = 32)
	private String id;

	// 客户id
	@Column(name = "entityid", length = 32)
	private String entityId;

	// 产品id
	@Column(name = "productid", length = 32)
	private String productId;

	// 账单id
	@Column(name = "billid", length = 32)
	private String billId;

	// 账单月份
	@Column(name = "billmonth", columnDefinition = "datetime comment '账单月份'")
	private Timestamp billMonth = new Timestamp(System.currentTimeMillis());

	// 创建时间
	@Column(name = "wtime", columnDefinition = "datetime comment '记录创建时间'")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	// 客户的固定运营成本，一个客户每月只算在一个产品上，并非每个产品都有
	@Column(name = "customerfixedcost", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '客户的固定运营成本'")
	private BigDecimal customerFixedCost = BigDecimal.ZERO;

	// 产品的单条运营成本总计 = 产品单条运营成本 x 计费数
	@Column(name = "productsinglecosttotal", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '产品的单条运营成本总计'")
	private BigDecimal productSingleCostTotal = BigDecimal.ZERO;

	// 产品的账单金额比例运营成本 = 账单金额 x 比例
	@Column(name = "billmoneycost", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '账单金额比例运营成本'")
	private BigDecimal billMoneyCost = BigDecimal.ZERO;

	// 产品的毛利润比例运营成本 = 账单毛利润 x 比例
	@Column(name = "billgrossprofitcost", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '毛利润比例运营成本'")
	private BigDecimal billGrossProfitCost = BigDecimal.ZERO;

	// 统一单条运营成本总计 = 统一单条运营成本 x 计费数
	@Column(name = "unifiedsinglecosttotal", precision = 19, scale = 2, columnDefinition = "decimal(19,2) default 0 comment '统一单条运营成本总计'")
	private BigDecimal unifiedSingleCostTotal = BigDecimal.ZERO;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public Timestamp getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(Timestamp billMonth) {
		this.billMonth = billMonth;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public BigDecimal getCustomerFixedCost() {
		return customerFixedCost;
	}

	public void setCustomerFixedCost(BigDecimal fixedCost) {
		this.customerFixedCost = fixedCost;
	}

	public BigDecimal getProductSingleCostTotal() {
		return productSingleCostTotal;
	}

	public void setProductSingleCostTotal(BigDecimal singleOperateCostTotal) {
		this.productSingleCostTotal = singleOperateCostTotal;
	}

	public BigDecimal getBillGrossProfitCost() {
		return billGrossProfitCost;
	}

	public void setBillGrossProfitCost(BigDecimal grossProfitRateCost) {
		this.billGrossProfitCost = grossProfitRateCost;
	}

	public BigDecimal getUnifiedSingleCostTotal() {
		return unifiedSingleCostTotal;
	}

	public void setUnifiedSingleCostTotal(BigDecimal unifiedSingleCostTotal) {
		this.unifiedSingleCostTotal = unifiedSingleCostTotal;
	}

	public BigDecimal getBillMoneyCost() {
		return billMoneyCost;
	}

	public void setBillMoneyCost(BigDecimal billMoneyCost) {
		this.billMoneyCost = billMoneyCost;
	}
}
