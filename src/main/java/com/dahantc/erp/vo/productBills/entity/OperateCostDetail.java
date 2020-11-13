package com.dahantc.erp.vo.productBills.entity;

import java.math.BigDecimal;

/**
 * 运营成本单价、比例信息
 */
public class OperateCostDetail {

	/**
	 * 均摊每条产品运营成本(统一)
	 */
	private BigDecimal unifiedOperateSingleCost;

	/**
	 * 当前产品的固定运营成本
	 */
	private BigDecimal productOperateFixedCost;

	/**
	 * 当前产品的均摊运营成本
	 */
	private BigDecimal productOperateSingleCost;

	/**
	 * 产品的账单金额运营成本的比例
	 */
	private BigDecimal billMoneyRatio;

	/**
	 * 产品的毛利润运营成本的比例
	 */
	private BigDecimal billGrossProfitRatio;

	public BigDecimal getUnifiedOperateSingleCost() {
		return unifiedOperateSingleCost;
	}

	public void setUnifiedOperateSingleCost(BigDecimal unifiedOperateSingleCost) {
		this.unifiedOperateSingleCost = unifiedOperateSingleCost;
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

	/**
	 * 用账单数据计算出该账单总的运营成本，是以下几项之和
	 * 
	 * @param successCount
	 *            成功数
	 * @param billMoney
	 *            账单金额
	 * @param billGrossProfit
	 *            账单毛利润
	 * @return 总运营成本
	 */
	public BigDecimal getTotalCost(long successCount, BigDecimal billMoney, BigDecimal billGrossProfit) {
		// 总运营成本是以下几项之和：
		// 1.统一单条运营成本总计 = 统一单条运营成本 x 计费数
		// 2.产品的单条运营成本总计 = 产品单条运营成本 x 计费数
		// 3.客户的固定运营成本
		// 4.产品的账单金额比例运营成本 = 账单金额 x 比例
		// 5.产品的毛利润比例运营成本 = 账单毛利润 x 比例
		BigDecimal cost = new BigDecimal(0);
		if (unifiedOperateSingleCost != null) {
			cost = cost.add(new BigDecimal(successCount).multiply(unifiedOperateSingleCost));
		}
		if (productOperateSingleCost != null) {
			cost = cost.add(new BigDecimal(successCount).multiply(productOperateSingleCost));
		}
		if (productOperateFixedCost != null) {
			cost = cost.add(productOperateFixedCost);
		}
		if (billMoneyRatio != null) {
			cost = cost.add(billMoney.multiply(billMoneyRatio));
		}
		if (billGrossProfitRatio != null) {
			cost = cost.add(billGrossProfit.multiply(billGrossProfitRatio));
		}
		return cost;
	}
}
