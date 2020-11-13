package com.dahantc.erp.vo.dailyExpenseTj.entity;

import com.dahantc.erp.enums.BusinessType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "erp_dailyexpenses_statistics")
@DynamicUpdate(true)
public class Dailyexpenses implements Serializable {

    private static final long serialVersionUID = -2542468141172844536L;

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    // 金额
    @Column(name = "cost")
    private BigDecimal cost;

    // 收支类型
	@Column(name = "incomeexpenditureype")
	private int incomeExpenditureType;

	//业务类型
	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

    // 统计时间，yyyy
    @Column(name = "statsyear")
    private Date statsYear;

    // 统计时间，yyyy-MM
    @Column(name = "statsyearmonth")
    private Date statsYearMonth;

    // 统计时间，yyyy-MM-dd
    @Column(name = "statsdate")
    private Date statsDate;

    // 部门id
    @Column(name = "deptid", length = 32)
	private String deptId;

    // 创建者id
    @Column(name = "creatorid", length = 32)
	private String creatorId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public int getIncomeExpenditureType() {
        return incomeExpenditureType;
    }

    public void setIncomeExpenditureType(int incomeExpenditureType) {
        this.incomeExpenditureType = incomeExpenditureType;
    }

    public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
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

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
}
