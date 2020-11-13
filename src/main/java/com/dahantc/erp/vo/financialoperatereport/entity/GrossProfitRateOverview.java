package com.dahantc.erp.vo.financialoperatereport.entity;

import java.math.BigDecimal;

public class GrossProfitRateOverview {

	/** 毛利区间 */
	private String rateSection;

	/** 区间收入占比 */
	private BigDecimal sectionReceiveRate = BigDecimal.ZERO;

	/** 收入 */
	private BigDecimal receive = BigDecimal.ZERO;

	/** 毛利 */
	private BigDecimal grossProfit = BigDecimal.ZERO;

	/** 区间毛利占比 */
	private BigDecimal sectionGrossProfitRate = BigDecimal.ZERO;

	/** 客户量 */
	private int custCount = 0;

	public String getRateSection() {
		return rateSection;
	}

	public void setRateSection(String rateSection) {
		this.rateSection = rateSection;
	}

	public BigDecimal getSectionReceiveRate() {
		return sectionReceiveRate;
	}

	public void setSectionReceiveRate(BigDecimal sectionReceiveRate) {
		this.sectionReceiveRate = sectionReceiveRate;
	}

	public BigDecimal getReceive() {
		return receive;
	}

	public void setReceive(BigDecimal receive) {
		this.receive = receive;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getSectionGrossProfitRate() {
		return sectionGrossProfitRate;
	}

	public void setSectionGrossProfitRate(BigDecimal sectionGrossProfitRate) {
		this.sectionGrossProfitRate = sectionGrossProfitRate;
	}

	public int getCustCount() {
		return custCount;
	}

	public void setCustCount(int custCount) {
		this.custCount = custCount;
	}

	public void setGrossProfitSection(GrossProfitRateSection section) {
		this.rateSection = section.getDesc();
	}

	public static enum GrossProfitRateSection {

		ONE("小于5%", 0.00, 0.05),

		TWO("介于5%~10%之间", 0.05, 0.10),

		THREE("介于10%~15%之间", 0.10, 0.15),

		FOUR("介于15%~20%之间", 0.15, 0.20),

		FIVE("介于20%~25%之间", 0.20, 0.25),

		SIX("介于25%~30%之间", 0.25, 0.30),

		SEVEN("介于30%~35%之间", 0.30, 0.35),

		EIGHT("介于35%~40%之间", 0.35, 0.40),

		NINE("介于40%~45%之间", 0.40, 0.45),

		TEN("介于45%~50%之间", 0.45, 0.50),

		ELEVEN("大于或等于50%", 0.50, null);

		private String desc;
		
		private Double start;
		
		private Double end;
		
		public Double getStart() {
			return start;
		}

		public void setStart(Double start) {
			this.start = start;
		}

		public Double getEnd() {
			return end;
		}

		public void setEnd(Double end) {
			this.end = end;
		}

		public static GrossProfitRateSection getGrossProfitRateSection(BigDecimal grossProfitRate) {
			// 直接计算索引不用判断首尾
			int index = grossProfitRate.divide(new BigDecimal(0.05), BigDecimal.ROUND_UP).intValue();
			if (index < 0) {
				index = 0;
			}
			if (index > 10) {
				index = 10;
			}
			return values()[index];
		}

		private GrossProfitRateSection(String desc, Double start, Double end) {
			this.desc = desc;
			this.start = start;
			this.end = end;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	}

}
